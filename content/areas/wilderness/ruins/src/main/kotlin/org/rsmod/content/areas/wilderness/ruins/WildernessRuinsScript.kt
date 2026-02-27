package org.rsmod.content.areas.wilderness.ruins

import jakarta.inject.Inject
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.stat
import org.rsmod.api.player.stat.statBase
import org.rsmod.api.player.stat.statRestore
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Wilderness Ruins area script.
 *
 * Handles:
 * - Eastern Ruins (level 25-28): Furnace, item spawns
 * - Western Ruins (level 21-24): Anvil, scattered planks
 * - Chaos Temple (level 11): Prayer altar for recharging prayer points
 * - NPC spawns: Zombies, Deadly red spiders, Grizzly bears, Giant rats
 */
class WildernessRuinsScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // Chaos Temple prayer altar - restores prayer points
        onOpLoc1(ruins_locs.chaos_temple_altar) { useChaosAltar() }
    }

    private suspend fun ProtectedAccess.useChaosAltar() {
        if (player.stat(stats.prayer) >= player.statBase(stats.prayer)) {
            mes("You already have full prayer points.")
            return
        }

        mes("You pray at the altar...")
        anim(seqs.human_pickupfloor)
        delay(2)

        // Restore prayer to full
        player.statRestore(stats.prayer)

        mes("You recharge your prayer points.")
    }
}

/** Wilderness Ruins location references */
private typealias ruins_locs = WildernessRuinsLocs

object WildernessRuinsLocs : LocReferences() {
    // Chaos Temple (level 11 Wilderness) - prayer altar
    val chaos_temple_altar = find("chaosaltar")
}
