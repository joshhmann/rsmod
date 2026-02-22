package org.rsmod.content.other.agentbridge

import jakarta.inject.Inject
import org.rsmod.api.hunt.Hunt
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.player.interact.LocInteractions
import org.rsmod.api.player.interact.NpcInteractions
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.script.onPlayerLogin
import org.rsmod.api.script.onPlayerSoftTimer
import org.rsmod.events.EventBus
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionLocOp
import org.rsmod.game.interact.InteractionNpcOp
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.movement.RouteRequestLoc
import org.rsmod.game.movement.RouteRequestPathingEntity
import org.rsmod.game.type.hunt.HuntVis
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Drives the AgentBridge per-tick loop:
 * 1. Dequeues any pending bot actions and executes them on the game thread.
 * 2. Broadcasts the player's state snapshot to all connected agent clients.
 *
 * Pattern mirrors MapClockScript — a period-1 soft timer per logged-in player.
 */
class AgentBridgeScript
@Inject
constructor(
    private val server: AgentBridgeServer,
    private val hunt: Hunt,
    private val eventBus: EventBus,
    private val locRegistry: LocRegistry,
    private val locTypes: LocTypeList,
    private val locInteractions: LocInteractions,
    private val npcInteractions: NpcInteractions,
    private val npcList: NpcList,
    private val objTypes: ObjTypeList,
) : PluginScript() {

    override fun ScriptContext.startup() {
        server.start()
        onPlayerLogin {
            server.ensureClientTap(player)
            player.softTimer(agent_timers.agent_bridge, 1)
        }
        onPlayerSoftTimer(agent_timers.agent_bridge) {
            server.ensureClientTap(player)
            val action = server.pollAction(player.avatar.name)
            if (action != null) {
                executeAction(player, action)
            }
            val nearbyNpcs =
                hunt
                    .findNpcs(player.coords, SCAN_RADIUS, HuntVis.Off)
                    .map { npc ->
                        NearbyNpcSnapshot(
                            id = npc.type.id,
                            index = npc.slotId,
                            name = npc.name,
                            x = npc.coords.x,
                            z = npc.coords.z,
                            animation = npc.pendingSequence.id,
                        )
                    }
                    .toList()
            val nearbyLocs =
                hunt
                    .findLocs(player.coords, SCAN_RADIUS, HuntVis.Off)
                    .mapNotNull { loc ->
                        val type = locTypes.types[loc.id] ?: return@mapNotNull null
                        NearbyLocSnapshot(
                            id = loc.id,
                            name = type.name,
                            x = loc.coords.x,
                            z = loc.coords.z,
                        )
                    }
                    .toList()
            server.broadcast(player, nearbyNpcs, nearbyLocs)
        }
    }

    companion object {
        /** Chebyshev tile radius for nearby entity scanning. */
        private const val SCAN_RADIUS = 16
    }

    private fun executeAction(player: Player, action: BotAction) {
        when (action) {
            is BotAction.Walk -> {
                player.clearPendingAction(eventBus)
                player.walk(CoordGrid(action.x, action.z, player.level))
            }

            is BotAction.Teleport -> {
                player.coords = CoordGrid(action.x, action.z, action.plane)
            }

            is BotAction.InteractLoc -> {
                val coords = CoordGrid(action.x, action.z, player.level)
                val locInfo = locRegistry.findType(coords, action.id)
                if (locInfo == null) {
                    println("[AgentBridge] interact_loc: no loc id=${action.id} at $coords")
                    return
                }
                val type =
                    locTypes.types[locInfo.id]
                        ?: run {
                            println("[AgentBridge] interact_loc: unknown loc type id=${action.id}")
                            return
                        }
                val boundLoc = BoundLocInfo(locInfo, type)
                val op = interactionOp(action.option)
                val opTrigger = locInteractions.hasOpTrigger(player, boundLoc, op, type)
                val apTrigger = locInteractions.hasApTrigger(player, boundLoc, op, type)
                if (!locInteractions.hasOp(boundLoc, type, player.vars, op)) {
                    println("[AgentBridge] interact_loc: no handler for op=$op on loc=$boundLoc")
                    return
                }
                val interaction =
                    InteractionLocOp(
                        target = boundLoc,
                        op = op,
                        hasOpTrigger = opTrigger,
                        hasApTrigger = apTrigger,
                    )
                val routeRequest = RouteRequestLoc(loc = locInfo, type = type)
                player.clearPendingAction(eventBus)
                player.resetFaceEntity()
                player.faceLoc(locInfo, type.width, type.length)
                player.interaction = interaction
                player.routeRequest = routeRequest
            }

            is BotAction.InteractNpc -> {
                val npc = npcList[action.index]
                if (npc == null) {
                    println("[AgentBridge] interact_npc: no npc at index=${action.index}")
                    return
                }
                if (npc.isDelayed) return
                val op = interactionOp(action.option)
                if (!npcInteractions.hasOp(npc, player.vars, op)) {
                    println("[AgentBridge] interact_npc: no handler for op=$op on npc=$npc")
                    return
                }
                val opTrigger = npcInteractions.hasOpTrigger(player, npc, op)
                val apTrigger = npcInteractions.hasApTrigger(player, npc, op)
                val interaction =
                    InteractionNpcOp(
                        target = npc,
                        op = op,
                        hasOpTrigger = opTrigger,
                        hasApTrigger = apTrigger,
                    )
                val routeRequest = RouteRequestPathingEntity(npc.avatar, clientRequest = false)
                player.clearPendingAction(eventBus)
                player.faceNpc(npc)
                player.interaction = interaction
                player.routeRequest = routeRequest
            }

            is BotAction.SpawnItem -> {
                val objType = objTypes[action.itemId]
                if (objType == null) {
                    println("[AgentBridge] spawn_item: unknown item id=${action.itemId}")
                    return
                }
                val result = player.invAdd(player.inv, objType, action.count)
                val msg =
                    if (result.success) {
                        "[AgentBridge] spawn_item: added ${action.count}x ${objType.name} " +
                            "(id=${action.itemId})"
                    } else {
                        "[AgentBridge] spawn_item: failed to add ${action.count}x " +
                            "${objType.name} (inventory full?)"
                    }
                println(msg)
            }
        }
    }

    private fun interactionOp(option: Int): InteractionOp =
        when (option) {
            1 -> InteractionOp.Op1
            2 -> InteractionOp.Op2
            3 -> InteractionOp.Op3
            4 -> InteractionOp.Op4
            5 -> InteractionOp.Op5
            else -> InteractionOp.Op1
        }
}
