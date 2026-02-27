package org.rsmod.content.areas.city.draynor.manor

import jakarta.inject.Inject
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// Draynor Manor loc references
private typealias draynor_manor_locs = DraynorManorLocs

internal object DraynorManorLocs : LocReferences() {
    // Main entrance doors
    val manor_front_door_left = find("door_left_closed")
    val manor_front_door_right = find("door_right_closed")
    // Witch's house entrance
    val witch_door = find("door")
    // Basement trapdoor
    val basement_trapdoor_closed = find("trapdoor_draynor_manor_closed")
    val basement_trapdoor_open = find("trapdoor_draynor_manor_open")
    // Stairs
    val stairs_up = find("staircase_draynor_manor_up")
    val stairs_down = find("staircase_draynor_manor_down")
}

/**
 * Draynor Manor script handling:
 * - Main entrance doors
 * - Witch's house entrance
 * - Basement trapdoor
 * - Staircases between floors
 */
class DraynorManorScript @Inject constructor(private val locRepo: LocRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        // Front entrance doors
        onOpLoc1(draynor_manor_locs.manor_front_door_left) { openManorDoor(it.loc) }
        onOpLoc1(draynor_manor_locs.manor_front_door_right) { openManorDoor(it.loc) }

        // Witch's house door
        onOpLoc1(draynor_manor_locs.witch_door) { mes("The door is locked.") }

        // Basement trapdoor
        onOpLoc1(draynor_manor_locs.basement_trapdoor_closed) { openTrapdoor(it.loc) }
        onOpLoc1(draynor_manor_locs.basement_trapdoor_open) { climbDownTrapdoor() }

        // Stairs
        onOpLoc1(draynor_manor_locs.stairs_up) { climbUpStairs() }
        onOpLoc1(draynor_manor_locs.stairs_down) { climbDownStairs() }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun ProtectedAccess.openManorDoor(loc: BoundLocInfo) {
        mes("You open the door.")
        soundSynth(synths.door_open)
        // TODO: add open-door loc refs and call locRepo.change(loc,
        // draynor_manor_locs.manor_door_open, 50)
    }

    private fun ProtectedAccess.openTrapdoor(loc: BoundLocInfo) {
        mes("You open the trapdoor.")
        soundSynth(synths.door_open)
        locRepo.change(loc, draynor_manor_locs.basement_trapdoor_open, 500)
    }

    private fun ProtectedAccess.climbDownTrapdoor() {
        teleport(CoordGrid(3116, 9754, 0))
        mes("You climb down the trapdoor into the basement.")
    }

    private fun ProtectedAccess.climbUpStairs() {
        val current = player.coords
        teleport(CoordGrid(current.x, current.z, current.level + 1))
        mes("You walk up the stairs.")
    }

    private fun ProtectedAccess.climbDownStairs() {
        val current = player.coords
        teleport(CoordGrid(current.x, current.z, current.level - 1))
        mes("You walk down the stairs.")
    }
}
