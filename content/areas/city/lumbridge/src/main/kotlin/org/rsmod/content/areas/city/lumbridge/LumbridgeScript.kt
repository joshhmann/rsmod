package org.rsmod.content.areas.city.lumbridge

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.basePrayerLvl
import org.rsmod.api.player.stat.prayerLvl
import org.rsmod.api.player.stat.statHeal
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_locs
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// Altar loc reference for Lumbridge church
private typealias lumbridge_locs_extras = LumbridgeLocsExtras

internal object LumbridgeLocsExtras : LocReferences() {
    val altar = find("altar") // Lumbridge church altar (409)
}

class LumbridgeScript
@Inject
constructor(private val locRepo: LocRepository, private val objRepo: ObjRepository) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(lumbridge_locs.winch) { operateWinch() }
        onOpLoc1(lumbridge_locs.farmerfred_axe_logs) { takeAxeFromLogs(it.loc) }
        // Church altar - restores prayer to full
        onOpLoc1(lumbridge_locs_extras.altar) { prayAtAltar() }
    }

    private fun ProtectedAccess.operateWinch() {
        mes("It seems the winch is jammed - I can't move it.")
        soundSynth(synths.lever)
    }

    private suspend fun ProtectedAccess.takeAxeFromLogs(loc: BoundLocInfo) {
        if (content.woodcutting_axe in inv) {
            mesbox("You already have an axe.")
            return
        }

        if (inv.isFull()) {
            mesbox("You don't have enough room for the axe.")
            return
        }

        locRepo.change(loc, lumbridge_locs.farmerfred_logs, 50)
        invAddOrDrop(objRepo, objs.bronze_axe)
        soundSynth(synths.take_axe)
        objbox(objs.bronze_axe, 400, "You take a bronze axe from the logs.")
    }

    private fun ProtectedAccess.prayAtAltar() {
        val currentPrayer = player.prayerLvl
        val maxPrayer = player.basePrayerLvl

        if (currentPrayer >= maxPrayer) {
            mes("You already have full prayer.")
            return
        }

        // Restore prayer to full
        val restoreAmount = maxPrayer - currentPrayer
        player.statHeal(stats.prayer, restoreAmount, 0)
        mes("You recharge your prayer at the altar.")
        // Note: No sound for prayer restore in current synths
    }
}
