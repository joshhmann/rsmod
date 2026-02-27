package org.rsmod.content.other.consumables.potions

import org.rsmod.game.type.stat.StatType

/**
 * Represents a potion effect that can be applied when drinking.
 *
 * @property stat The stat to boost/restore
 * @property constant The flat boost amount
 * @property percent The percentage of base level to add (0-100)
 * @property isRestore Whether this restores toward base (true) or boosts above base (false)
 * @property curesPoison Whether this potion cures/downgrades poison or venom-to-poison
 * @property curesVenom Whether this potion fully cures venom (antivenom variants)
 * @property poisonImmunityTicks Ticks of poison immunity granted after drinking (0 = none)
 * @property venomImmunityTicks Ticks of venom immunity granted after drinking (0 = none)
 * @property isEnergyRestore Whether this potion restores run energy
 * @property energyRestorePercent Percentage of run energy to restore (0-100)
 */
data class PotionEffect(
    val stat: StatType,
    val constant: Int,
    val percent: Int,
    val isRestore: Boolean = false,
    val curesPoison: Boolean = false,
    val curesVenom: Boolean = false,
    val poisonImmunityTicks: Int = 0,
    val venomImmunityTicks: Int = 0,
    val isEnergyRestore: Boolean = false,
    val energyRestorePercent: Int = 0,
)

/**
 * Potion type definition with all dose variants.
 *
 * @property name Potion name (e.g., "Attack potion")
 * @property effect The potion effect when consumed
 * @property doseIds Map of dose count (1-4) to item ID
 */
data class PotionType(
    val name: String,
    val effect: PotionEffect,
    val doseIds: Map<Int, Int>, // dose (1-4) -> item ID
) {
    /** Get the next dose item ID (e.g., 4-dose -> 3-dose, 1-dose -> vial). */
    fun getNextDose(currentDose: Int): Int? {
        return when (currentDose) {
            4 -> doseIds[3]
            3 -> doseIds[2]
            2 -> doseIds[1]
            1 -> PotionRegistry.EMPTY_VIAL_ID // Last dose becomes empty vial
            else -> null
        }
    }

    /** Get dose number from item ID. */
    fun getDoseFromId(itemId: Int): Int? {
        return doseIds.entries.find { it.value == itemId }?.key
    }

    /** Check if item ID is a dose of this potion. */
    fun isDose(itemId: Int): Boolean {
        return doseIds.containsValue(itemId)
    }
}

/** Registry of all F2P potions. */
object PotionRegistry {

    /** Empty vial item ID. */
    const val EMPTY_VIAL_ID = 229 // Verify with obj.sym

    // Stat references - will be resolved via BaseObjs or refs
    // These are placeholder references - actual implementation will use
    // org.rsmod.api.config.refs.stats

    /** All registered potions. */
    val ALL_POTIONS: List<PotionType> =
        listOf(
            // Energy potion: restores 10% run energy per dose (F2P obtainable from GE)
            PotionType(
                name = "Energy potion",
                effect =
                    PotionEffect(
                        stat =
                            org.rsmod.api.config.refs.stats
                                .hitpoints, // Dummy stat, energy handled separately
                        constant = 0,
                        percent = 0,
                        isEnergyRestore = true,
                        energyRestorePercent = 10,
                    ),
                doseIds =
                    mapOf(
                        4 to 3008, // Energy potion(4)
                        3 to 3010, // Energy potion(3)
                        2 to 3012, // Energy potion(2)
                        1 to 3014, // Energy potion(1)
                    ),
            ),

            // Attack potion: +3 + 10% of base
            PotionType(
                name = "Attack potion",
                effect =
                    PotionEffect(
                        stat = org.rsmod.api.config.refs.stats.attack,
                        constant = 3,
                        percent = 10,
                    ),
                doseIds =
                    mapOf(
                        4 to 2428, // Attack potion(4)
                        3 to 121, // Attack potion(3)
                        2 to 123, // Attack potion(2)
                        1 to 125, // Attack potion(1)
                    ),
            ),

            // Strength potion: +3 + 10% of base
            PotionType(
                name = "Strength potion",
                effect =
                    PotionEffect(
                        stat = org.rsmod.api.config.refs.stats.strength,
                        constant = 3,
                        percent = 10,
                    ),
                doseIds =
                    mapOf(
                        4 to 113, // Strength potion(4)
                        3 to 115, // Strength potion(3)
                        2 to 117, // Strength potion(2)
                        1 to 119, // Strength potion(1)
                    ),
            ),

            // Defence potion: +3 + 10% of base
            PotionType(
                name = "Defence potion",
                effect =
                    PotionEffect(
                        stat = org.rsmod.api.config.refs.stats.defence,
                        constant = 3,
                        percent = 10,
                    ),
                doseIds =
                    mapOf(
                        4 to 2432, // Defence potion(4)
                        3 to 133, // Defence potion(3)
                        2 to 135, // Defence potion(2)
                        1 to 137, // Defence potion(1)
                    ),
            ),

            // Prayer potion: restore 7 + 25% of base (toward base, not boost)
            PotionType(
                name = "Prayer potion",
                effect =
                    PotionEffect(
                        stat = org.rsmod.api.config.refs.stats.prayer,
                        constant = 7,
                        percent = 25,
                        isRestore = true,
                    ),
                doseIds =
                    mapOf(
                        4 to 2434, // Prayer potion(4)
                        3 to 139, // Prayer potion(3)
                        2 to 141, // Prayer potion(2)
                        1 to 143, // Prayer potion(1)
                    ),
            ),

            // Super attack: +5 + 15% of base
            PotionType(
                name = "Super attack",
                effect =
                    PotionEffect(
                        stat = org.rsmod.api.config.refs.stats.attack,
                        constant = 5,
                        percent = 15,
                    ),
                doseIds =
                    mapOf(
                        4 to 2436, // Super attack(4)
                        3 to 145, // Super attack(3)
                        2 to 147, // Super attack(2)
                        1 to 149, // Super attack(1)
                    ),
            ),

            // Super strength: +5 + 15% of base
            PotionType(
                name = "Super strength",
                effect =
                    PotionEffect(
                        stat = org.rsmod.api.config.refs.stats.strength,
                        constant = 5,
                        percent = 15,
                    ),
                doseIds =
                    mapOf(
                        4 to 2440, // Super strength(4)
                        3 to 157, // Super strength(3)
                        2 to 159, // Super strength(2)
                        1 to 161, // Super strength(1)
                    ),
            ),

            // Super defence: +5 + 15% of base
            PotionType(
                name = "Super defence",
                effect =
                    PotionEffect(
                        stat = org.rsmod.api.config.refs.stats.defence,
                        constant = 5,
                        percent = 15,
                    ),
                doseIds =
                    mapOf(
                        4 to 2442, // Super defence(4)
                        3 to 163, // Super defence(3)
                        2 to 165, // Super defence(2)
                        1 to 167, // Super defence(1)
                    ),
            ),

            // Antipoison: cures poison + 150-tick immunity (~90 seconds)
            PotionType(
                name = "Antipoison",
                effect =
                    PotionEffect(
                        stat =
                            org.rsmod.api.config.refs.stats.hitpoints, // Dummy stat, just for cure
                        constant = 0,
                        percent = 0,
                        curesPoison = true,
                        poisonImmunityTicks = 150, // IMMUNITY_ANTIPOISON
                    ),
                doseIds =
                    mapOf(
                        4 to 2446, // Antipoison(4)
                        3 to 175, // Antipoison(3)
                        2 to 177, // Antipoison(2)
                        1 to 179, // Antipoison(1)
                    ),
            ),

            // Superantipoison: cures poison + 300-tick immunity (~3 min)
            PotionType(
                name = "Superantipoison",
                effect =
                    PotionEffect(
                        stat = org.rsmod.api.config.refs.stats.hitpoints,
                        constant = 0,
                        percent = 0,
                        curesPoison = true,
                        poisonImmunityTicks = 300, // IMMUNITY_SUPERANTIPOISON
                    ),
                doseIds =
                    mapOf(
                        4 to 2448, // Superantipoison(4)
                        3 to 181, // Superantipoison(3)
                        2 to 183, // Superantipoison(2)
                        1 to 185, // Superantipoison(1)
                    ),
            ),

            // Antidote+: cures poison + 600-tick immunity (~6 min)
            PotionType(
                name = "Antidote+",
                effect =
                    PotionEffect(
                        stat = org.rsmod.api.config.refs.stats.hitpoints,
                        constant = 0,
                        percent = 0,
                        curesPoison = true,
                        poisonImmunityTicks = 600, // IMMUNITY_ANTIDOTE_PLUS
                    ),
                doseIds =
                    mapOf(
                        4 to 5943, // Antidote+(4)
                        3 to 5944, // Antidote+(3)
                        2 to 5945, // Antidote+(2)
                        1 to 5946, // Antidote+(1)
                    ),
            ),

            // Antidote++: cures poison + 1200-tick immunity (~12 min)
            PotionType(
                name = "Antidote++",
                effect =
                    PotionEffect(
                        stat = org.rsmod.api.config.refs.stats.hitpoints,
                        constant = 0,
                        percent = 0,
                        curesPoison = true,
                        poisonImmunityTicks = 1200, // IMMUNITY_ANTIDOTE_PLUS_PLUS
                    ),
                doseIds =
                    mapOf(
                        4 to 5952, // Antidote++(4)
                        3 to 5953, // Antidote++(3)
                        2 to 5954, // Antidote++(2)
                        1 to 5955, // Antidote++(1)
                    ),
            ),

            // Antivenom: fully cures venom + 300-tick venom immunity (no poison immunity)
            PotionType(
                name = "Antivenom",
                effect =
                    PotionEffect(
                        stat = org.rsmod.api.config.refs.stats.hitpoints,
                        constant = 0,
                        percent = 0,
                        curesVenom = true,
                        venomImmunityTicks = 300, // IMMUNITY_ANTIVENOM
                        poisonImmunityTicks = 0,
                    ),
                doseIds =
                    mapOf(
                        4 to 12905, // Antivenom(4)
                        3 to 12906, // Antivenom(3)
                        2 to 12907, // Antivenom(2)
                        1 to 12908, // Antivenom(1)
                    ),
            ),

            // Antivenom+: fully cures venom + 1600-tick venom AND poison immunity (~16 min)
            PotionType(
                name = "Antivenom+",
                effect =
                    PotionEffect(
                        stat = org.rsmod.api.config.refs.stats.hitpoints,
                        constant = 0,
                        percent = 0,
                        curesVenom = true,
                        venomImmunityTicks = 1600, // IMMUNITY_ANTIVENOM_PLUS
                        poisonImmunityTicks = 1600, // IMMUNITY_ANTIVENOM_PLUS
                    ),
                doseIds =
                    mapOf(
                        4 to 12913, // Antivenom+(4)
                        3 to 12914, // Antivenom+(3)
                        2 to 12915, // Antivenom+(2)
                        1 to 12916, // Antivenom+(1)
                    ),
            ),

            // Energy potion: restores 10% run energy per dose (F2P available)
            PotionType(
                name = "Energy potion",
                effect =
                    PotionEffect(
                        stat = org.rsmod.api.config.refs.stats.hitpoints, // Dummy stat
                        constant = 0,
                        percent = 0,
                        isEnergyRestore = true,
                        energyRestorePercent = 10, // 10% run energy
                    ),
                doseIds =
                    mapOf(
                        4 to 3008, // Energy potion(4)
                        3 to 3010, // Energy potion(3)
                        2 to 3012, // Energy potion(2)
                        1 to 3014, // Energy potion(1)
                    ),
            ),
        )

    /** All potion item IDs (all doses). */
    val ALL_POTION_IDS: Set<Int> = ALL_POTIONS.flatMap { it.doseIds.values }.toSet()

    /**
     * Get potion type from item ID.
     *
     * @param itemId The item ID
     * @return PotionType, or null if not a potion
     */
    fun getPotionType(itemId: Int): PotionType? {
        return ALL_POTIONS.find { it.isDose(itemId) }
    }

    /**
     * Get dose number from item ID.
     *
     * @param itemId The item ID
     * @return Dose number (1-4), or null if not a potion dose
     */
    fun getDose(itemId: Int): Int? {
        return getPotionType(itemId)?.getDoseFromId(itemId)
    }

    /**
     * Check if item ID is a potion.
     *
     * @param itemId The item ID
     * @return True if potion
     */
    fun isPotion(itemId: Int): Boolean {
        return ALL_POTION_IDS.contains(itemId)
    }

    /**
     * Get the replacement item ID after drinking.
     *
     * @param itemId The current potion item ID
     * @return Replacement item ID, or null
     */
    fun getReplacement(itemId: Int): Int? {
        val potion = getPotionType(itemId) ?: return null
        val dose = potion.getDoseFromId(itemId) ?: return null
        return potion.getNextDose(dose)
    }
}
