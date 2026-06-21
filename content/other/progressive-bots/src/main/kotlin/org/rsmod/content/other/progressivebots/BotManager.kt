package org.rsmod.content.other.progressivebots

import jakarta.inject.Inject
import org.rsmod.api.game.process.GameLifecycle
import org.rsmod.api.player.isInCombat
import org.rsmod.content.other.agentbridge.PlayerBotService
import org.rsmod.game.entity.Player
import org.rsmod.game.seq.EntitySeq
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Tracks a single progressive bot's state between ticks.
 */
data class BotState(
    val def: BotDef,
    var ticksAtCurrentPos: Int = 0,
    var lastX: Int = def.spawnX,
    var lastZ: Int = def.spawnZ,
    var personality: BotPersonality = BotPersonality.forPlanner(def.planner),
    var lastAction: BotTaskAction = BotTaskAction.Idle,
)

/**
 * Simple bot action tasks — what a bot can decide to do each tick.
 */
enum class BotTaskAction {
    Wander, Idle, Socialize, Gather, Fight, Shop,
}

/**
 * Progressive bot script — manages all server-side autonomous bot players.
 *
 * Lifecycle:
 *   1. On startup: spawns all bots from [BotConfig], subscribes to tick events
 *   2. Each LateCycle: evaluates each bot's state and picks actions
 *
 * Bots are real Player objects with NoopClient, visible to all online players.
 */
class BotManager
@Inject
constructor(
    private val playerBotService: PlayerBotService,
) : PluginScript() {

    private val bots = mutableMapOf<String, BotState>()
    private var initialized = false

    override fun ScriptContext.startup() {
        if (initialized) return
        initialized = true

        eventBus.subscribeUnbound(GameLifecycle.LateCycle::class.java) { onTick() }

        val total = BotConfig.bots.size
        var spawned = 0

        for (def in BotConfig.bots) {
            try {
                playerBotService.spawnBot(def.username, def.spawnX, def.spawnZ)
                bots[def.username] = BotState(def = def)
                spawned++
            } catch (e: Exception) {
                logger.warn { "[ProgressiveBots] Failed to spawn '${def.username}': ${e.message}" }
            }
        }

        logger.info { "[ProgressiveBots] Spawned $spawned/$total progressive bots" }

        val actual = playerBotService.botCount()
        logger.info { "[ProgressiveBots] PlayerList reports $actual NoopClient bots" }
    }

    private fun onTick() {
        if (!initialized) return

        for ((name, state) in bots.toList()) {
            val player = playerBotService.findBot(name) ?: continue
            try {
                tickBot(player, state)
            } catch (_: Exception) { }
        }
    }

    private fun tickBot(player: Player, state: BotState) {
        val coords = player.coords

        if (coords.x != state.lastX || coords.z != state.lastZ) {
            state.lastX = coords.x
            state.lastZ = coords.z
            state.ticksAtCurrentPos = 0
        } else {
            state.ticksAtCurrentPos++
        }

        if (state.ticksAtCurrentPos % 25 != 0) return

        val view = BotPlayerView(
            x = coords.x,
            z = coords.z,
            level = coords.level,
            inCombat = player.isInCombat(),
            animating = player.pendingSequence != EntitySeq.NULL,
        )

        if (view.inCombat || view.animating) return

        state.lastAction = state.personality.pickAction(view, state)
        executeBotAction(player, state.lastAction)
    }

    private fun executeBotAction(player: Player, action: BotTaskAction) {
        val coords = player.coords

        when (action) {
            BotTaskAction.Wander -> {
                val dx = (-4..4).random()
                val dz = (-4..4).random()
                player.walk(CoordGrid(
                    (coords.x + dx).coerceIn(3200, 3270),
                    (coords.z + dz).coerceIn(3200, 3270), 0
                ))
            }
            BotTaskAction.Idle -> { }
            BotTaskAction.Socialize -> {
                player.walk(CoordGrid((3218..3225).random(), (3218..3225).random(), 0))
            }
            BotTaskAction.Gather -> {
                val spots = listOf(
                    CoordGrid(3235, 3148, 0), CoordGrid(3227, 3256, 0),
                    CoordGrid(3244, 3155, 0), CoordGrid(3239, 3252, 0),
                )
                player.walk(spots.random())
            }
            BotTaskAction.Fight -> {
                val spots = listOf(
                    CoordGrid(3237, 3226, 0), CoordGrid(3256, 3265, 0),
                    CoordGrid(3248, 3216, 0),
                )
                player.walk(spots.random())
            }
            BotTaskAction.Shop -> {
                player.walk(CoordGrid(3215, 3245, 0))
            }
        }
    }

    companion object {
        private val logger = com.github.michaelbull.logging.InlineLogger()
    }
}
