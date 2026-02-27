package org.rsmod.content.other.windmill

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLocU
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Windmill flour mill mechanics.
 *
 * Process:
 * 1. Pick grain from wheat fields (implemented in generic pickables)
 * 2. Use grain on hopper (upstairs) - puts grain in hopper
 * 3. Operate hopper controls - grinds grain into flour
 * 4. Collect flour from bin (downstairs) with empty pot
 *
 * Locations: Lumbridge, Cooking Guild
 */
class WindmillScript @Inject constructor(private val locRepo: LocRepository) : PluginScript() {
    // Track which windmills have flour ready (per windmill coords)
    private val flourReadyBins = mutableSetOf<CoordGrid>()

    override fun ScriptContext.startup() {
        // Hopper interactions
        onOpLocU(windmill_locs.hopper, objs.grain) { putGrainInHopper(it.loc) }
        onOpLoc1(windmill_locs.hopper) { checkHopper() }

        // Hopper controls (operate)
        onOpLoc1(windmill_locs.hopper_controls) { operateHopperControls(it.loc) }

        // Flour bin interactions
        onOpLoc1(windmill_locs.flour_bin_empty) { checkFlourBin(empty = true) }
        onOpLoc1(windmill_locs.flour_bin_full) { collectFlour(it.loc) }
        onOpLocU(windmill_locs.flour_bin_empty, objs.pot_empty) { collectFlour(it.loc) }
        onOpLocU(windmill_locs.flour_bin_full, objs.pot_empty) { collectFlour(it.loc) }
    }

    /** Player uses grain on the hopper. */
    private suspend fun ProtectedAccess.putGrainInHopper(loc: BoundLocInfo) {
        arriveDelay()
        faceSquare(loc.coords)

        // Check if grain is already in the hopper
        if (vars[windmill_varbits.mill_flour] != 0) {
            mes("There is already grain in the hopper.")
            return
        }

        // Remove grain from inventory
        val remove = invDel(player.inv, objs.grain, 1)
        if (remove.failure) {
            return
        }

        anim(seqs.human_pickuptable)
        mes("You put the grain in the hopper.")

        // Set the varbit to indicate grain is in the hopper
        vars[windmill_varbits.mill_flour] = 1
    }

    /** Check the hopper to see if there's grain in it. */
    private fun ProtectedAccess.checkHopper() {
        if (vars[windmill_varbits.mill_flour] != 0) {
            mes("There is grain in the hopper.")
        } else {
            mes("The hopper is empty.")
        }
    }

    /** Operate the hopper controls to grind the grain into flour. */
    private suspend fun ProtectedAccess.operateHopperControls(loc: BoundLocInfo) {
        arriveDelay()
        faceSquare(loc.coords)

        // Check if there's grain to grind
        if (vars[windmill_varbits.mill_flour] == 0) {
            mes("The hopper is empty. You need to put some grain in it first.")
            return
        }

        // Check if flour is already waiting in the bin
        val binCoords = getFlourBinCoords(loc.coords)
        if (binCoords in flourReadyBins) {
            mes("You should collect the flour from the bin first.")
            return
        }

        // Operate the controls
        anim(seqs.human_reachforladder)
        soundSynth(synths.lever)
        mes("You operate the controls.")
        delay(2)

        // Clear the hopper and mark flour as ready
        vars[windmill_varbits.mill_flour] = 0
        flourReadyBins.add(binCoords)

        // Transform the flour bin to show flour
        val binLoc = locRepo.findExact(binCoords, windmill_locs.flour_bin_empty)
        if (binLoc != null) {
            locRepo.change(
                binLoc,
                windmill_locs.flour_bin_full,
                200,
            ) // 200 tick duration (~2 minutes)
        }

        mes("The grain slides down the chute.")
    }

    /** Check the flour bin to see if there's flour available. */
    private fun ProtectedAccess.checkFlourBin(empty: Boolean) {
        if (empty) {
            mes("The flour bin is empty.")
        } else {
            mes("The flour bin is full.")
        }
    }

    /** Collect flour from the flour bin using an empty pot. */
    private suspend fun ProtectedAccess.collectFlour(loc: BoundLocInfo) {
        arriveDelay()
        faceSquare(loc.coords)

        // Check if player has an empty pot
        if (objs.pot_empty !in inv) {
            mes("You need an empty pot to collect the flour.")
            return
        }

        // Check if there's flour available at this bin
        if (loc.coords !in flourReadyBins) {
            mes("The flour bin is empty.")
            return
        }

        // Replace empty pot with pot of flour
        val replace = invReplace(inv, objs.pot_empty, 1, windmill_objs.pot_of_flour)
        if (replace.failure) {
            return
        }

        anim(seqs.human_pickuptable)
        mes("You collect some flour from the bin.")

        // Remove this bin from the ready set
        flourReadyBins.remove(loc.coords)

        // Transform the bin back to empty
        val binLoc = locRepo.findExact(loc.coords, windmill_locs.flour_bin_full)
        if (binLoc != null) {
            locRepo.change(binLoc, windmill_locs.flour_bin_empty, 1)
        }
    }

    /**
     * Get the coordinates of the flour bin for a given hopper/control location. Flour bins are
     * typically directly below the hopper (2 levels down).
     */
    private fun getFlourBinCoords(hopperCoords: CoordGrid): CoordGrid {
        // The flour bin is on the ground floor (level 0), hopper is on level 2
        // We need to translate down 2 levels while keeping the same x/z
        return hopperCoords.translateLevel(-2)
    }
}
