package org.rsmod.content.other.consumables.potions

import jakarta.inject.Inject
import kotlin.math.min
import org.rsmod.api.config.constants
import org.rsmod.api.player.output.UpdateRun
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.statBoost
import org.rsmod.api.player.stat.statHeal
import org.rsmod.api.script.onOpHeld2
import org.rsmod.content.mechanics.poison.scripts.PoisonScript
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Potion drinking script that handles consuming all potions.
 *
 * Features:
 * - Stat boosts (attack, strength, defence)
 * - Prayer restoration (restore toward base, not boost)
 * - Antipoison / antivenom (wired to PoisonScript)
 * - Dose tracking (4→3→2→1→vial)
 * - 3-tick delay between potions
 */
class PotionScript
@Inject
constructor(private val objTypes: ObjTypeList, private val poison: PoisonScript) : PluginScript() {

    override fun ScriptContext.startup() {
        // Register drink handler for all F2P potion doses
        for (potionId in PotionRegistry.ALL_POTION_IDS) {
            val objType = objTypes[potionId] ?: continue
            onOpHeld2(objType) { drinkPotion(it.slot) }
        }
    }

    /**
     * Handle drinking a potion from inventory.
     *
     * @param slot Inventory slot containing the potion
     */
    private suspend fun ProtectedAccess.drinkPotion(slot: Int) {
        val potionItem = inv[slot] ?: return
        val potionId = potionItem.id

        // Validate this is a potion
        if (!PotionRegistry.isPotion(potionId)) {
            return
        }

        // Check if player can drink (action delay allows it)
        if (actionDelay > mapClock) {
            return
        }

        val potionType = PotionRegistry.getPotionType(potionId) ?: return
        val effect = potionType.effect

        // Play drink animation
        anim(food_seqs.human_eat)

        // Replace potion with next dose (or empty vial)
        val replacementId = PotionRegistry.getReplacement(potionId)
        if (replacementId != null) {
            val replacementType = objTypes[replacementId] ?: return
            invReplace(inv, slot, 1, replacementType)
        }

        // Apply potion effect
        if (effect.isEnergyRestore) {
            // Energy potion - restore run energy
            restoreRunEnergy(effect.energyRestorePercent)
        } else if (effect.curesVenom) {
            with(poison) { cureVenom(effect.venomImmunityTicks, effect.poisonImmunityTicks) }
        } else if (effect.curesPoison) {
            with(poison) { curePoison(effect.poisonImmunityTicks) }
        } else if (effect.isRestore) {
            // Prayer potion - restore toward base (statHeal)
            statHeal(effect.stat, effect.constant, effect.percent)
        } else {
            // Stat boost potion - boost above base (statBoost)
            statBoost(effect.stat, effect.constant, effect.percent)
        }

        // Set drinking delay (3 ticks)
        actionDelay = mapClock + 3

        // Delay for the drinking action
        delay(3)
    }

    /**
     * Restore run energy by a percentage of maximum. Energy potions restore 10% per dose in F2P.
     *
     * @param percent Percentage of max energy to restore (0-100)
     */
    private fun ProtectedAccess.restoreRunEnergy(percent: Int) {
        val restoreAmount = (constants.run_max_energy * percent) / 100
        val newEnergy = min(constants.run_max_energy, player.runEnergy + restoreAmount)
        player.runEnergy = newEnergy
        UpdateRun.energy(player, newEnergy)
    }
}

/** Local sequence references for potion-related animations. */
private typealias food_seqs = FoodSeqs

object FoodSeqs : org.rsmod.api.type.refs.seq.SeqReferences() {
    /** Drinking animation (seq 829) - same as eating. */
    val human_eat = find("human_eat")
}
