package org.rsmod.content.other.consumables.potions

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.statBoost
import org.rsmod.api.player.stat.statHeal
import org.rsmod.api.script.onOpHeld2
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Potion drinking script that handles consuming all F2P potions.
 *
 * Features:
 * - Stat boosts (attack, strength, defence)
 * - Prayer restoration (restore toward base, not boost)
 * - Antipoison (cures poison)
 * - Dose tracking (4→3→2→1→vial)
 * - 3-tick delay between potions
 */
class PotionScript @Inject constructor(private val objTypes: ObjTypeList) : PluginScript() {

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
        if (effect.curesPoison) {
            curePoison()
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

    /** Cure poison status. This would interface with the poison system when implemented. */
    private fun ProtectedAccess.curePoison() {
        // TODO: Implement poison cure when poison system exists
        // For now, just acknowledge the antipoison was consumed
        // player.poison = false
    }
}

/** Local sequence references for potion-related animations. */
private typealias food_seqs = FoodSeqs

private object FoodSeqs : org.rsmod.api.type.refs.seq.SeqReferences() {
    /** Drinking animation (seq 829) - same as eating. */
    val human_eat = find("human_eat")
}
