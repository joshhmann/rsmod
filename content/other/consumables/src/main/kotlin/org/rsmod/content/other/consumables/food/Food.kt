package org.rsmod.content.other.consumables.food

import org.rsmod.game.type.obj.ObjType

/**
 * Represents a consumable food item with healing properties.
 *
 * @property item The food item type
 * @property healAmount Amount of hitpoints healed
 * @property isComboFood Whether this is a combo food (1-tick eat)
 * @property replacementItem Optional item to replace with after eating (e.g., pizza -> half pizza)
 */
data class Food(
    val item: ObjType,
    val healAmount: Int,
    val isComboFood: Boolean = false,
    val replacementItem: ObjType? = null,
) {
    /** Eating delay in ticks (3 for normal food, 1 for combo food). */
    val eatDelay: Int = if (isComboFood) 1 else 3
}

/** Food registry containing all F2P food definitions. */
object FoodRegistry {

    /**
     * Get the heal amount for a food item.
     *
     * @param itemId The object type ID
     * @return Heal amount, or null if not food
     */
    fun getHealAmount(itemId: Int): Int? {
        return FOOD_HEAL_AMOUNTS[itemId]
    }

    /**
     * Check if an item is a combo food.
     *
     * @param itemId The object type ID
     * @return True if combo food
     */
    fun isComboFood(itemId: Int): Boolean {
        return COMBO_FOODS.contains(itemId)
    }

    /**
     * Get replacement item for partial foods (e.g., pizza -> half pizza).
     *
     * @param itemId The object type ID
     * @return Replacement item ID, or null if no replacement
     */
    fun getReplacement(itemId: Int): Int? {
        return FOOD_REPLACEMENTS[itemId]
    }

    // F2P Food heal amounts (item ID -> heal amount)
    // These are the base IDs from rev 233 cache
    private val FOOD_HEAL_AMOUNTS =
        mapOf(
            // Fish
            315 to 3, // Shrimps
            325 to 4, // Sardine
            2140 to 4, // Cooked chicken
            2142 to 4, // Cooked meat
            347 to 5, // Herring
            355 to 6, // Mackerel
            333 to 7, // Trout
            339 to 7, // Cod
            351 to 8, // Pike
            329 to 9, // Salmon
            361 to 10, // Tuna
            379 to 12, // Lobster
            365 to 13, // Bass
            373 to 14, // Swordfish
            7946 to 16, // Monkfish
            385 to 20, // Shark

            // Bread
            2309 to 5, // Bread

            // Pizzas (full)
            2289 to 7, // Plain pizza
            2293 to 8, // Meat pizza
            2297 to 9, // Anchovy pizza

            // Pizza halves (already eaten once)
            2291 to 7, // Half plain pizza
            2295 to 8, // Half meat pizza
            2299 to 9, // Half anchovy pizza

            // Combo food
            3144 to 18, // Cooked karambwan (combo food - 1 tick)

            // Overheal (special case)
            13441 to 0, // Anglerfish (heals floor(baseHp/10) + tier, calculated separately)
        )

    // Combo foods (1-tick eat)
    private val COMBO_FOODS =
        setOf(
            3144 // Cooked karambwan
        )

    // Food replacements (eat full -> get half, eat half -> gone)
    private val FOOD_REPLACEMENTS =
        mapOf(
            2289 to 2291, // Plain pizza -> Half plain pizza
            2293 to 2295, // Meat pizza -> Half meat pizza
            2297 to 2299, // Anchovy pizza -> Half anchovy pizza
        )

    /** Food IDs that are considered F2P food. */
    val ALL_FOOD_IDS = FOOD_HEAL_AMOUNTS.keys

    /**
     * Check if an item ID is food.
     *
     * @param itemId The object type ID
     * @return True if food
     */
    fun isFood(itemId: Int): Boolean {
        return FOOD_HEAL_AMOUNTS.containsKey(itemId)
    }
}
