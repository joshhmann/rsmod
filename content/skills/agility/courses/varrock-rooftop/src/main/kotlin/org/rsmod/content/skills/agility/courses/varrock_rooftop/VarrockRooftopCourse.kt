package org.rsmod.content.skills.agility.courses.varrock_rooftop

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
 * Varrock Rooftop Agility Course (F2P).
 *
 * Requires 30 Agility to enter and complete the course. Obstacles must be completed in order for
 * the lap bonus.
 *
 * Course layout:
 * 1. Rough Wall (Climb) - 12 XP
 * 2. Clothesline (Cross) - 21 XP
 * 3. Gap (Leap) - 17 XP
 * 4. Wall (Swing across) - 25 XP
 * 5. Gap (Leap) - 9 XP
 * 6. Gap (Leap) - 15 XP
 * 7. Ledge (Balance) - 15 XP
 * 8. Edge (Jump down) - 15 XP
 * 9. Lap Bonus - 125 XP
 *
 * Total per lap: 154 XP (without bonus: 129 XP)
 */
class VarrockRooftopCourse @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // Obstacle 1: Rough Wall (start)
        onOpLoc1(AgilityLocs.varrock_wallclimb) {
            attemptObstacle(ObstacleType.ROUGH_WALL)
        }

        // Obstacle 2: Clothesline
        onOpLoc1(AgilityLocs.varrock_clothesline) {
            attemptObstacle(ObstacleType.CLOTHESLINE)
        }

        // Obstacle 3: Gap leap to ruins
        onOpLoc1(AgilityLocs.varrock_leaptoruins) {
            attemptObstacle(ObstacleType.GAP_LEAP_1)
        }

        // Obstacle 4: Wall swing
        onOpLoc1(AgilityLocs.varrock_wallswing) {
            attemptObstacle(ObstacleType.WALL_SWING)
        }

        // Obstacle 5: Gap leap
        onOpLoc1(AgilityLocs.varrock_leaptobalcony) {
            attemptObstacle(ObstacleType.GAP_LEAP_2)
        }

        // Obstacle 6: Wall scramble
        onOpLoc1(AgilityLocs.varrock_wallscramble) {
            attemptObstacle(ObstacleType.WALL_SCRAMBLE)
        }

        // Obstacle 7: Step up roof
        onOpLoc1(AgilityLocs.varrock_stepuproof) {
            attemptObstacle(ObstacleType.STEP_UP)
        }

        // Obstacle 8: Gap leap down (finish)
        onOpLoc1(AgilityLocs.varrock_leapdown) {
            attemptObstacle(ObstacleType.GAP_LEAP_DOWN)
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
                anim(VarrockRooftopSeqs.human_wall_climb)
                mes("You climb up the rough wall.")
            }
            ObstacleType.CLOTHESLINE -> {
                anim(VarrockRooftopSeqs.human_tightrope_walk)
                mes("You carefully walk across the clothesline.")
            }
            ObstacleType.GAP_LEAP_1,
            ObstacleType.GAP_LEAP_2 -> {
                anim(VarrockRooftopSeqs.human_leap_gap)
                mes("You leap across the gap.")
            }
            ObstacleType.WALL_SWING -> {
                anim(VarrockRooftopSeqs.human_wall_swing)
                mes("You swing across the wall.")
            }
            ObstacleType.WALL_SCRAMBLE -> {
                anim(VarrockRooftopSeqs.human_wall_scramble)
                mes("You scramble up the wall.")
            }
            ObstacleType.STEP_UP -> {
                anim(VarrockRooftopSeqs.human_step_up)
                mes("You step up onto the roof.")
            }
            ObstacleType.GAP_LEAP_DOWN -> {
                anim(VarrockRooftopSeqs.human_leap_down)
                mes("You leap down from the roof.")
            }
        }

        // TODO: Track obstacle sequence for lap bonus XP
        // TODO: Add proper movement/teleport to destination coords
        // TODO: Add failure chance based on agility level
    }

    private enum class ObstacleType(val xp: Double) {
        ROUGH_WALL(12.0),
        CLOTHESLINE(21.0),
        GAP_LEAP_1(17.0),
        WALL_SWING(25.0),
        GAP_LEAP_2(9.0),
        WALL_SCRAMBLE(15.0),
        STEP_UP(15.0),
        GAP_LEAP_DOWN(15.0),
    }

    companion object {
        const val LEVEL_REQUIREMENT = 30
        const val LAP_BONUS_XP = 125.0
    }
}

/** Local Seq references for Varrock Rooftop animations. */
internal object VarrockRooftopSeqs : SeqReferences() {
    val human_wall_climb = find("human_wall_climb")
    val human_tightrope_walk = find("human_tightrope_walk")
    val human_leap_gap = find("human_leap_gap")
    val human_wall_swing = find("human_wall_swing")
    val human_wall_scramble = find("human_wall_scramble")
    val human_step_up = find("human_step_up")
    val human_leap_down = find("human_leap_down")
}
