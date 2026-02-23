package org.rsmod.content.other.consumables.food

import jakarta.inject.Inject
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.statBase
import org.rsmod.api.player.stat.statHeal
import org.rsmod.api.script.onOpHeld2
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Food eating script that handles consuming all F2P food items.
 *
 * Features:
 * - Heals hitpoints (capped at base level)
 * - 3-tick delay between eating (1 tick for combo foods)
 * - Partial foods (pizzas) replaced correctly
 * - Animation on eat
 */
class FoodScript @Inject constructor(private val objTypes: ObjTypeList) : PluginScript() {

    override fun ScriptContext.startup() {
        // Register eat handler for all F2P food items
        for (foodId in FoodRegistry.ALL_FOOD_IDS) {
            val objType = objTypes[foodId] ?: continue
            onOpHeld2(objType) { eatFood(it.slot) }
        }
    }

    /**
     * Handle eating food from inventory.
     *
     * @param slot Inventory slot containing the food
     */
    private suspend fun ProtectedAccess.eatFood(slot: Int) {
        val foodItem = inv[slot] ?: return
        val foodId = foodItem.id

        // Validate this is food
        if (!FoodRegistry.isFood(foodId)) {
            return
        }

        // Check if player can eat (action delay allows it)
        if (actionDelay > mapClock) {
            return
        }

        // Calculate heal amount
        val healAmount = calculateHealAmount(foodId)

        // Play eat animation
        anim(food_seqs.human_eat)

        // Consume the food
        val replacementId = FoodRegistry.getReplacement(foodId)
        if (replacementId != null) {
            // Partial food (pizza) - replace with half
            val replacementType = objTypes[replacementId] ?: return
            invReplace(inv, foodId, 1, replacementType)
        } else {
            // Normal food - delete from inventory
            val foodType = objTypes[foodId] ?: return
            invDel(inv, foodType, 1)
        }

        // Heal hitpoints (capped at base level)
        statHeal(stats.hitpoints, healAmount, 0)

        // Set eating delay (3 ticks normal, 1 tick for combo food)
        val delayTicks = if (FoodRegistry.isComboFood(foodId)) 1 else 3
        actionDelay = mapClock + delayTicks

        // Delay for the eating action
        delay(delayTicks)
    }

    /**
     * Calculate the heal amount for a food item. Most foods have fixed heal amounts, but some (like
     * anglerfish) are dynamic.
     */
    private fun ProtectedAccess.calculateHealAmount(foodId: Int): Int {
        // Special case: Anglerfish overheals based on hitpoints level
        if (foodId == 13441) { // Anglerfish
            return calculateAnglerfishHeal()
        }

        // Standard foods have fixed heal amounts
        return FoodRegistry.getHealAmount(foodId) ?: 0
    }

    /**
     * Calculate anglerfish heal amount. Heals floor(baseHp/10) + tier bonus:
     * - HP 1-24: +2 (total heal: floor(hp/10) + 2)
     * - HP 25-49: +4
     * - HP 50-74: +6
     * - HP 75-92: +8
     * - HP 93-99: +13
     */
    private fun ProtectedAccess.calculateAnglerfishHeal(): Int {
        val baseHp = player.statBase(stats.hitpoints)
        val tierBonus =
            when (baseHp) {
                in 1..24 -> 2
                in 25..49 -> 4
                in 50..74 -> 6
                in 75..92 -> 8
                in 93..99 -> 13
                else -> 2
            }
        return (baseHp / 10) + tierBonus
    }
}

/** Local sequence references for food-related animations. */
private typealias food_seqs = FoodSeqs

private object FoodSeqs : SeqReferences() {
    /** Eating animation (seq 829). */
    val human_eat = find("human_eat")
}
