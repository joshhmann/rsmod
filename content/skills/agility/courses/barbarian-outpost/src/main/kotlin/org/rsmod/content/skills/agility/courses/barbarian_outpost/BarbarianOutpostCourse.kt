package org.rsmod.content.skills.agility.courses.barbarian_outpost

import jakarta.inject.Inject
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.agilityLvl
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.content.skills.agility.configs.AgilityLocs
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Barbarian Outpost Agility Course implementation.
 *
 * Requires 35 Agility to enter and complete the course. Obstacles must be completed in order for
 * the lap bonus.
 *
 * Course layout:
 * 1. Log Balance (13.7 XP)
 * 2. Obstacle Net (8.2 XP)
 * 3. Balancing Ledge (22 XP)
 * 4. Ladder (13.7 XP) - Climb down
 * 5. Crumbling Wall (13.7 XP) - Three wall variants
 * 6. Lap Bonus (46.2 XP)
 *
 * Total per lap: 117.5 XP (without bonus: 71.3 XP)
 */
class BarbarianOutpostCourse @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // Log Balance
        onOpLoc1(AgilityLocs.barb_log_balance) { attemptObstacle(it.loc, ObstacleType.LOG_BALANCE) }

        // Obstacle Net
        onOpLoc1(AgilityLocs.barb_obstacle_net) {
            attemptObstacle(it.loc, ObstacleType.OBSTACLE_NET)
        }

        // Balancing Ledge
        onOpLoc1(AgilityLocs.barb_balancing_ledge) {
            attemptObstacle(it.loc, ObstacleType.BALANCING_LEDGE)
        }

        // Crumbling Walls (3 variants share the same ID)
        onOpLoc1(AgilityLocs.barb_crumbling_wall_1) {
            attemptObstacle(it.loc, ObstacleType.CRUMBLING_WALL)
        }

        // Ladder (climb down after ledge)
        onOpLoc1(AgilityLocs.barb_ladder) { attemptObstacle(it.loc, ObstacleType.LADDER) }
    }

    private suspend fun ProtectedAccess.attemptObstacle(loc: Any, type: ObstacleType) {
        if (player.agilityLvl < LEVEL_REQUIREMENT) {
            mes("You need an Agility level of $LEVEL_REQUIREMENT to attempt this obstacle.")
            return
        }

        // Grant XP for the obstacle
        statAdvance(stats.agility, type.xp)

        // Play appropriate animation based on obstacle type
        when (type) {
            ObstacleType.LOG_BALANCE -> {
                anim(BarbarianOutpostSeqs.human_walk_logbalance)
                mes("You carefully walk across the log.")
            }
            ObstacleType.OBSTACLE_NET -> {
                anim(BarbarianOutpostSeqs.human_climbing)
                mes("You climb up the net.")
            }
            ObstacleType.BALANCING_LEDGE -> {
                anim(BarbarianOutpostSeqs.human_ledge_walk_left)
                mes("You carefully cross the ledge.")
            }
            ObstacleType.CRUMBLING_WALL -> {
                anim(BarbarianOutpostSeqs.human_jump_hurdle)
                mes("You climb over the crumbling wall.")
            }
            ObstacleType.LADDER -> {
                anim(BarbarianOutpostSeqs.human_climbing_down)
                mes("You climb down the ladder.")
            }
        }

        // TODO: Track obstacle sequence for lap bonus XP
        // TODO: Add proper movement/teleport to destination coords
        // TODO: Add failure chance based on agility level
    }

    private enum class ObstacleType(val xp: Double) {
        LOG_BALANCE(13.7),
        OBSTACLE_NET(8.2),
        BALANCING_LEDGE(22.0),
        CRUMBLING_WALL(13.7),
        LADDER(13.7),
    }

    companion object {
        const val LEVEL_REQUIREMENT = 35
        const val LAP_BONUS_XP = 46.2
    }
}

/** Local Seq references for Barbarian Outpost animations. */
internal object BarbarianOutpostSeqs : SeqReferences() {
    val human_walk_logbalance = find("human_walk_logbalance")
    val human_climbing = find("human_climbing")
    val human_climbing_down = find("human_climbing_down")
    val human_ledge_walk_left = find("human_ledge_walk_left")
    val human_jump_hurdle = find("human_jump_hurdle")
}
