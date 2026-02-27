package org.rsmod.content.skills.crafting.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.craftingLvl
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpLocU
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.content.skills.crafting.craft_objs
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Spinning wheel implementation for Crafting skill.
 *
 * Mechanics: Use wool on spinning wheel → Ball of wool
 *
 * Level requirement: 1 Crafting XP: 2.5 per ball of wool
 *
 * Animation: 894 (human_spinningwheel)
 */
class Spinning
@Inject
constructor(private val xpMods: XpModifiers, private val objRepo: ObjRepository) : PluginScript() {

    override fun ScriptContext.startup() {
        // Wool on spinning wheel
        onOpLocU(spinning_locs.wheel, craft_objs.wool) { spinWool() }
    }

    private suspend fun ProtectedAccess.spinWool() {
        // Level check (Spinning requires level 1, but check anyway for consistency)
        if (player.craftingLvl < 1) {
            mes("You need a Crafting level of 1 to spin wool.")
            return
        }

        if (!inv.contains(craft_objs.wool)) {
            mes("You need wool to use the spinning wheel.")
            return
        }

        val count = countDialog("How many would you like to spin?")
        if (count == 0) {
            return
        }

        val startCoords = player.coords
        repeat(count) {
            if (player.coords != startCoords) {
                return
            }

            val removed = invDel(inv, craft_objs.wool, count = 1, strict = true)
            if (removed.failure) {
                return
            }

            // Spinning wheel animation (ID 894)
            anim(spinning_seqs.spin)
            delay(3)

            // Grant XP and give ball of wool
            val xp = 2.5 * xpMods.get(player, stats.crafting)
            statAdvance(stats.crafting, xp)
            invAddOrDrop(objRepo, craft_objs.ball_of_wool)
            mes("You spin the wool into a ball.")
        }
    }

    companion object {
        // XP per ball of wool
        private const val SPIN_XP = 2.5
    }
}

// LocReferences for spinning wheel locations
internal typealias spinning_locs = SpinningLocs

internal object SpinningLocs : LocReferences() {
    // Spinning wheel (ID 14889)
    val wheel = find("spinningwheel")
}

// SeqReferences for spinning animations
internal typealias spinning_seqs = SpinningSeqs

internal object SpinningSeqs : SeqReferences() {
    // Spinning wheel animation (ID 894)
    val spin = find("human_spinningwheel")
}
