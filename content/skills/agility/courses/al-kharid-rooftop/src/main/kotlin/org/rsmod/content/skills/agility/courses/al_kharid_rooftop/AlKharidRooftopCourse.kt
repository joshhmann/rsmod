package org.rsmod.content.skills.agility.courses.al_kharid_rooftop

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
 * Al Kharid Rooftop Agility Course (F2P).
 *
 * Requires 20 Agility to enter and complete the course. Obstacles must be completed in order for
 * the lap bonus.
 *
 * Course layout:
 * 1. Rough Wall (Climb) - 10 XP
 * 2. Tightrope (Walk) - 10 XP
 * 3. Cable (Swing) - 10 XP
 * 4. Zip Line (Slide) - 10 XP
 * 5. Tightrope (Walk) - 10 XP
 * 6. Tree (Climb down) - 5 XP
 * 7. Rough Wall (Climb down) - 10 XP
 * 8. Lap Bonus - 180 XP
 *
 * Total per lap: 245 XP (without bonus: 65 XP)
 */
class AlKharidRooftopCourse @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // Obstacle 1: Rough Wall (start)
        onOpLoc1(AgilityLocs.kharid_wallclimb) {
            attemptObstacle(ObstacleType.ROUGH_WALL_1)
        }

        // Obstacle 2: Tightrope
        onOpLoc1(AgilityLocs.kharid_tightrope_1) {
            attemptObstacle(ObstacleType.TIGHTROPE)
        }

        // Obstacle 3: Cable Swing
        onOpLoc1(AgilityLocs.kharid_rope_swing) {
            attemptObstacle(ObstacleType.CABLE_SWING)
        }

        // Obstacle 4: Zip Line Slide
        onOpLoc1(AgilityLocs.kharid_slide_side) {
            attemptObstacle(ObstacleType.ZIP_LINE)
        }

        // Obstacle 5: Second Tightrope
        onOpLoc1(AgilityLocs.kharid_tightrope_4) {
            attemptObstacle(ObstacleType.TIGHTROPE_2)
        }

        // Obstacle 6: Tree (climb down)
        onOpLoc1(AgilityLocs.kharid_leapdown) {
            attemptObstacle(ObstacleType.TREE_DOWN)
        }

        // Obstacle 7: Second Rough Wall (end)
        onOpLoc1(AgilityLocs.kharid_wallclimb_2) {
            attemptObstacle(ObstacleType.ROUGH_WALL_2)
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
            ObstacleType.ROUGH_WALL_1,
            ObstacleType.ROUGH_WALL_2 -> {
                anim(AlKharidRooftopSeqs.human_wall_climb)
                mes("You climb the rough wall.")
            }
            ObstacleType.TIGHTROPE,
            ObstacleType.TIGHTROPE_2 -> {
                anim(AlKharidRooftopSeqs.human_tightrope_walk)
                mes("You carefully walk across the tightrope.")
            }
            ObstacleType.CABLE_SWING -> {
                anim(AlKharidRooftopSeqs.human_rope_swing)
                mes("You swing across on the cable.")
            }
            ObstacleType.ZIP_LINE -> {
                anim(AlKharidRooftopSeqs.human_zip_line)
                mes("You slide down the zip line.")
            }
            ObstacleType.TREE_DOWN -> {
                anim(AlKharidRooftopSeqs.human_climb_down)
                mes("You climb down the tree.")
            }
        }

        // TODO: Track obstacle sequence for lap bonus XP
        // TODO: Add proper movement/teleport to destination coords
        // TODO: Add failure chance based on agility level
    }

    private enum class ObstacleType(val xp: Double) {
        ROUGH_WALL_1(10.0),
        TIGHTROPE(10.0),
        CABLE_SWING(10.0),
        ZIP_LINE(10.0),
        TIGHTROPE_2(10.0),
        TREE_DOWN(5.0),
        ROUGH_WALL_2(10.0),
    }

    companion object {
        const val LEVEL_REQUIREMENT = 20
        const val LAP_BONUS_XP = 180.0
    }
}

/** Local Seq references for Al Kharid Rooftop animations. */
internal object AlKharidRooftopSeqs : SeqReferences() {
    // These were previously named after obstacle semantics, but rev233 internal names differ.
    val human_wall_climb = find("human_climbing")
    val human_tightrope_walk = find("human_walk_logbalance")
    val human_rope_swing = find("human_ropeswing")
    val human_zip_line = find("zipline_slide")
    val human_climb_down = find("human_climbing_down")
}
