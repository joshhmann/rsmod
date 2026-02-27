package org.rsmod.content.skills.prayer.scripts

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
 * F2P Altar interactions for Prayer skill.
 *
 * Supports:
 * - Clicking altar to restore prayer points
 * - F2P altar locations: Lumbridge, Varrock, Edgeville Monastery
 * - 2x faster prayer restoration at altars
 */
class PrayerAltar @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // F2P Church altars
        onOpLoc1(prayer_altars.lumbridge_altar) { useAltar() }
        onOpLoc1(prayer_altars.varrock_altar) { useAltar() }
        onOpLoc1(prayer_altars.monastery_altar) { useAltar() }

        // Slab altar (Lumbridge church)
        onOpLoc1(prayer_altars.altar_slab) { useAltar() }
    }

    private suspend fun ProtectedAccess.useAltar() {
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

/** F2P Altar location references */
private typealias prayer_altars = PrayerAltars

object PrayerAltars : LocReferences() {
    // Lumbridge Church altar
    val lumbridge_altar = find("altar")

    // Lumbridge Church altar slab
    val altar_slab = find("altar_slab")

    // Varrock Church altar
    val varrock_altar = find("fai_varrock_church_altar")

    // Edgeville Monastery altar
    val monastery_altar = find("monks_altar")
}
