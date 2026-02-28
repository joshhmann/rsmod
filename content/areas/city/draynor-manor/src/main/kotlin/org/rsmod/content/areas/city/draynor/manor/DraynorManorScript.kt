package org.rsmod.content.areas.city.draynor.manor

import jakarta.inject.Inject
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Draynor Manor script handling:
 * - Main entrance doors
 * - Witch's house entrance
 * - Basement trapdoor
 * - Staircases between floors
 */
class DraynorManorScript @Inject constructor(private val locRepo: LocRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        // Disabled placeholder.
        //
        // The previous implementation referenced extremely generic loc symbol names
        // (e.g., "door", "door_left_closed") which do not exist in the rev233 `loc.sym`
        // tables and breaks strict `packCache` verification.
        //
        // Re-implement by targeting concrete, canonical loc internal names from
        // `rsmod/.data/symbols/loc.sym`.
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
        // TODO: implement once correct loc refs are wired.
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
