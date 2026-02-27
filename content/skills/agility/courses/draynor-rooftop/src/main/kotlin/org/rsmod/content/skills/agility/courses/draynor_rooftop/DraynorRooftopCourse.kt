package org.rsmod.content.skills.agility.courses.draynor_rooftop

import jakarta.inject.Inject
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.agilityLvl
import org.rsmod.api.player.stat.statAdvance
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.content.skills.agility.configs.AgilityLocs
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Draynor Village Rooftop Agility Course (F2P).
 *
 * Requires 10 Agility to enter and complete the course. Obstacles must be completed in order for
 * the lap bonus.
 *
 * Course layout:
 * 1. Rough Wall (Climb) - 10 XP
 * 2. Tightrope 1 (Walk) - 8 XP
 * 3. Tightrope 2 (Walk) - 7 XP
 * 4. Narrow Wall (Cross) - 10 XP
 * 5. Wall (Scramble) - 10 XP
 * 6. Gap (Leap down) - 5 XP
 * 7. Crate (Jump) - 5 XP
 * 8. Lap Bonus - 79 XP
 *
 * Total per lap: 134 XP (without bonus: 55 XP)
 */
class DraynorRooftopCourse @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // Obstacle 1: Rough Wall (start)
        onOpLoc1(AgilityLocs.draynor_wallclimb) {
            attemptObstacle(ObstacleType.ROUGH_WALL)
        }

        // Obstacle 2: First Tightrope
        onOpLoc1(AgilityLocs.draynor_tightrope_1) {
            attemptObstacle(ObstacleType.TIGHTROPE_1)
        }

        // Obstacle 3: Second Tightrope
        onOpLoc1(AgilityLocs.draynor_tightrope_2) {
            attemptObstacle(ObstacleType.TIGHTROPE_2)
        }

        // Obstacle 4: Narrow Wall Crossing
        onOpLoc1(AgilityLocs.draynor_wallcrossing) {
            attemptObstacle(ObstacleType.NARROW_WALL)
        }

        // Obstacle 5: Wall Scramble
        onOpLoc1(AgilityLocs.draynor_wallscramble) {
            attemptObstacle(ObstacleType.WALL_SCRAMBLE)
        }

        // Obstacle 6: Gap (leap down)
        onOpLoc1(AgilityLocs.draynor_leapdown) {
            attemptObstacle(ObstacleType.GAP_LEAP)
        }

        // Obstacle 7: Crate (finish)
        onOpLoc1(AgilityLocs.draynor_crate) {
            attemptObstacle(ObstacleType.CRATE)
        }
    }

    private suspend fun ProtectedAccess.attemptObstacle(type: ObstacleType) {
        if (player.agilityLvl < LEVEL_REQUIREMENT) {
            mes("You need an Agility level of $LEVEL_REQUIREMENT to attempt this obstacle.")
            return
        }

        // Grant XP for the obstacle
        statAdvance(stats.agility, type.xp)

        // Play appropriate animation based on obstacle type
        when (type) {
            ObstacleType.ROUGH_WALL -> {
                anim(DraynorRooftopSeqs.human_wall_climb)
                mes("You climb up the rough wall.")
            }
            ObstacleType.TIGHTROPE_1,
            ObstacleType.TIGHTROPE_2 -> {
                anim(DraynorRooftopSeqs.human_tightrope_walk)
                mes("You carefully walk across the tightrope.")
            }
            ObstacleType.NARROW_WALL -> {
                anim(DraynorRooftopSeqs.human_wall_balance)
                mes("You carefully cross the narrow wall.")
            }
            ObstacleType.WALL_SCRAMBLE -> {
                anim(DraynorRooftopSeqs.human_wall_scramble)
                mes("You scramble up the wall.")
            }
            ObstacleType.GAP_LEAP -> {
                anim(DraynorRooftopSeqs.human_leap_gap)
                mes("You leap across the gap.")
            }
            ObstacleType.CRATE -> {
                anim(DraynorRooftopSeqs.human_jump)
                mes("You jump down to the crate.")
            }
        }

        // TODO: Track obstacle sequence for lap bonus XP
        // TODO: Add proper movement/teleport to destination coords
        // TODO: Add failure chance based on agility level
    }

    private enum class ObstacleType(val xp: Double) {
        ROUGH_WALL(10.0),
        TIGHTROPE_1(8.0),
        TIGHTROPE_2(7.0),
        NARROW_WALL(10.0),
        WALL_SCRAMBLE(10.0),
        GAP_LEAP(5.0),
        CRATE(5.0),
    }

    companion object {
        const val LEVEL_REQUIREMENT = 10
        const val LAP_BONUS_XP = 79.0
    }
}

/** Local Seq references for Draynor Rooftop animations. */
internal object DraynorRooftopSeqs : SeqReferences() {
    val human_wall_climb = find("human_wall_climb")
    val human_tightrope_walk = find("human_tightrope_walk")
    val human_wall_balance = find("human_wall_balance")
    val human_wall_scramble = find("human_wall_scramble")
    val human_leap_gap = find("human_leap_gap")
    val human_jump = find("human_jump")
}
