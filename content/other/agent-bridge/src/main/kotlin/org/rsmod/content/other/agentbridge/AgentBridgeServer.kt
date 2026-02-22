package org.rsmod.content.other.agentbridge

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import org.rsmod.annotations.InternalApi
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.stats
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Player

private const val PORT = 43595

/**
 * Singleton WebSocket server that:
 * - Broadcasts per-player state snapshots to LLM testing agents every tick.
 * - Receives action commands from agents and queues them for execution on the game thread.
 *
 * Action protocol (inbound JSON):
 * ```
 * { "player": "TestBot", "type": "walk", "x": 3222, "z": 3219 }
 * { "player": "TestBot", "type": "teleport", "x": 3222, "z": 3219, "plane": 0 }
 * { "player": "TestBot", "type": "interact_loc", "id": 10820, "x": 3223, "z": 3219, "option": 1 }
 * { "player": "TestBot", "type": "interact_npc", "index": 7, "option": 1 }
 * { "player": "TestBot", "type": "spawn_item", "item_id": 1511, "count": 1 }
 * ```
 *
 * The `player` field targets a specific player by name. Omitting it broadcasts to all players.
 * Actions are queued thread-safely and dequeued by [AgentBridgeScript] on the game thread.
 */
@Singleton
class AgentBridgeServer @Inject constructor(private val clock: MapClock) {

    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()
    private val clients = CopyOnWriteArrayList<WebSocket>()
    private val started = AtomicBoolean(false)

    /** Pending actions per player (keyed by lowercase player name). Game thread reads these. */
    private val pendingActions = ConcurrentHashMap<String, ConcurrentLinkedQueue<BotAction>>()
    private val telemetry = ConcurrentHashMap<String, PlayerTelemetry>()

    fun start() {
        if (!started.compareAndSet(false, true)) return
        val server = buildServer()
        server.isReuseAddr = true
        server.isDaemon = true
        server.start()
    }

    /**
     * Broadcast game state to all connected agent clients. Called from the game thread.
     * [nearbyNpcs] and [nearbyLocs] are computed by [AgentBridgeScript] using Hunt.
     */
    fun broadcast(
        player: Player,
        nearbyNpcs: List<NearbyNpcSnapshot>,
        nearbyLocs: List<NearbyLocSnapshot>,
    ) {
        ensureClientTap(player)
        if (clients.isEmpty()) return
        val telemetryState = telemetry[player.avatar.name.lowercase()]
        val snapshot = player.toSnapshot(clock.cycle, nearbyNpcs, nearbyLocs, telemetryState)
        val json = mapper.writeValueAsString(snapshot)
        val openClients = clients.filter { it.isOpen }
        for (client in openClients) {
            client.send(json)
        }
    }

    /**
     * Dequeue the next pending action for [playerName]. Called from the game thread. Returns null
     * if no actions are queued.
     */
    fun pollAction(playerName: String): BotAction? = pendingActions[playerName.lowercase()]?.poll()

    fun ensureClientTap(player: Player) {
        if (player.client is AgentBridgeTapClient) {
            return
        }
        val playerKey = player.avatar.name.lowercase()
        val current = player.client
        player.client = AgentBridgeTapClient(current, telemetrySink(playerKey))
    }

    private fun enqueue(playerName: String, action: BotAction) {
        pendingActions.getOrPut(playerName.lowercase()) { ConcurrentLinkedQueue() }.offer(action)
    }

    private fun parseAndEnqueue(message: String) {
        val node: JsonNode =
            try {
                mapper.readTree(message)
            } catch (_: Exception) {
                println("[AgentBridge] Invalid JSON: $message")
                return
            }

        val playerName = node.get("player")?.asText() ?: "_all"
        val type = node.get("type")?.asText() ?: return

        val action: BotAction =
            try {
                when (type) {
                    "walk" -> BotAction.Walk(x = node.req("x").asInt(), z = node.req("z").asInt())
                    "teleport" ->
                        BotAction.Teleport(
                            x = node.req("x").asInt(),
                            z = node.req("z").asInt(),
                            plane = node.get("plane")?.asInt() ?: 0,
                        )
                    "interact_loc" ->
                        BotAction.InteractLoc(
                            id = node.req("id").asInt(),
                            x = node.req("x").asInt(),
                            z = node.req("z").asInt(),
                            option = node.get("option")?.asInt() ?: 1,
                        )
                    "interact_npc" ->
                        BotAction.InteractNpc(
                            index = node.req("index").asInt(),
                            option = node.get("option")?.asInt() ?: 1,
                        )
                    "spawn_item" ->
                        BotAction.SpawnItem(
                            itemId = node.req("item_id").asInt(),
                            count = node.get("count")?.asInt() ?: 1,
                        )
                    else -> {
                        println("[AgentBridge] Unknown action type: $type")
                        return
                    }
                }
            } catch (e: IllegalArgumentException) {
                println("[AgentBridge] Missing field in $type action: ${e.message}")
                return
            }

        enqueue(playerName, action)
    }

    private fun JsonNode.req(field: String): JsonNode =
        get(field) ?: throw IllegalArgumentException("Missing required field: $field")

    private fun buildServer(): WebSocketServer =
        object : WebSocketServer(InetSocketAddress(PORT)) {
            override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
                clients.add(conn)
                println("[AgentBridge] Agent connected: ${conn.remoteSocketAddress}")
            }

            override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
                clients.remove(conn)
                println("[AgentBridge] Agent disconnected: ${conn.remoteSocketAddress}")
            }

            override fun onMessage(conn: WebSocket, message: String) {
                parseAndEnqueue(message)
            }

            override fun onError(conn: WebSocket?, ex: Exception) {
                // Log errors silently — don't crash the game server.
                println("[AgentBridge] WebSocket error: ${ex.message}")
            }

            override fun onStart() {
                println("[AgentBridge] WebSocket server listening on port $PORT")
            }
        }

    private fun telemetrySink(playerKey: String): PacketTapSink =
        object : PacketTapSink {
            override fun onGameMessage(type: Int, text: String) {
                val state = telemetry.getOrPut(playerKey) { PlayerTelemetry() }
                if (text.isBlank()) return
                if (state.gameMessages.size >= 20) {
                    state.gameMessages.removeFirst()
                }
                state.gameMessages.addLast(GameMessageSnapshot(type = type, text = text))
            }

            override fun onInterfaceText(interfaceId: Int, componentId: Int, text: String) {
                val state = telemetry.getOrPut(playerKey) { PlayerTelemetry() }
                if (text.isBlank()) return
                if (state.interfaceTexts.size >= 80) {
                    state.interfaceTexts.removeFirst()
                }
                state.interfaceTexts.addLast(
                    InterfaceTextSnapshot(
                        interfaceId = interfaceId,
                        componentId = componentId,
                        text = text,
                    )
                )
            }
        }
}

private data class PlayerTelemetry(
    val gameMessages: ArrayDeque<GameMessageSnapshot> = ArrayDeque(),
    val interfaceTexts: ArrayDeque<InterfaceTextSnapshot> = ArrayDeque(),
)

private data class InterfaceTextSnapshot(
    val interfaceId: Int,
    val componentId: Int,
    val text: String,
)

// ---------------------------------------------------------------------------
// State serialisation helpers
// ---------------------------------------------------------------------------

private fun Player.toSnapshot(
    tick: Int,
    nearbyNpcs: List<NearbyNpcSnapshot>,
    nearbyLocs: List<NearbyLocSnapshot>,
    telemetry: PlayerTelemetry?,
): StateSnapshot {
    val skills = buildMap {
        put("attack", statSnapshot(stats.attack))
        put("defence", statSnapshot(stats.defence))
        put("strength", statSnapshot(stats.strength))
        put("hitpoints", statSnapshot(stats.hitpoints))
        put("ranged", statSnapshot(stats.ranged))
        put("prayer", statSnapshot(stats.prayer))
        put("magic", statSnapshot(stats.magic))
        put("cooking", statSnapshot(stats.cooking))
        put("woodcutting", statSnapshot(stats.woodcutting))
        put("fletching", statSnapshot(stats.fletching))
        put("fishing", statSnapshot(stats.fishing))
        put("firemaking", statSnapshot(stats.firemaking))
        put("crafting", statSnapshot(stats.crafting))
        put("smithing", statSnapshot(stats.smithing))
        put("mining", statSnapshot(stats.mining))
        put("herblore", statSnapshot(stats.herblore))
        put("agility", statSnapshot(stats.agility))
        put("thieving", statSnapshot(stats.thieving))
        put("slayer", statSnapshot(stats.slayer))
        put("farming", statSnapshot(stats.farming))
        put("runecrafting", statSnapshot(stats.runecrafting))
        put("hunter", statSnapshot(stats.hunter))
        put("construction", statSnapshot(stats.construction))
    }

    val inventory =
        inv.objs.mapIndexedNotNull { slot, obj ->
            if (obj != null) InvItemSnapshot(slot, obj.id, obj.count) else null
        }

    val equipment =
        worn.objs.mapIndexedNotNull { slot, obj ->
            if (obj != null) InvItemSnapshot(slot, obj.id, obj.count) else null
        }

    val modalInterfaceIds = ui.modals.values.toSet()
    val messageBuffer = telemetry?.gameMessages?.toList().orEmpty()
    val dialogLines = mutableListOf<String>()
    if (telemetry != null && modalInterfaceIds.isNotEmpty()) {
        for (entry in telemetry.interfaceTexts) {
            if (entry.interfaceId in modalInterfaceIds) {
                val line = entry.text.trim()
                if (line.isNotEmpty()) {
                    dialogLines.add(line)
                }
            }
        }
    }

    val dialogSnapshot =
        DialogSnapshot(
            isOpen = ui.getModalOrNull(components.chatbox_chatmodal) != null,
            modalInterfaceIds = modalInterfaceIds.toList(),
            lines = dialogLines.takeLast(12),
        )

    return StateSnapshot(
        tick = tick,
        player =
            PlayerSnapshot(
                name = avatar.name,
                position = PositionSnapshot(coords.x, coords.z, coords.level),
                skills = skills,
                inventory = inventory,
                equipment = equipment,
                animation = pendingSequence.id,
                nearbyNpcs = nearbyNpcs,
                nearbyLocs = nearbyLocs,
                dialog = dialogSnapshot,
                gameMessages = messageBuffer,
            ),
        dialog = dialogSnapshot,
        gameMessages = messageBuffer,
    )
}

@OptIn(InternalApi::class)
private fun Player.statSnapshot(stat: org.rsmod.game.type.stat.StatType): SkillSnapshot =
    SkillSnapshot(level = statMap.getBaseLevel(stat).toInt() and 0xFF, xp = statMap.getXP(stat))
