package org.rsmod.content.mechanics.clues.configs

import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Clue Scroll reward tables implementation with weighted drop rates.
 *
 * Based on OSRS clue scroll reward data from the wiki. Each tier has multiple reward slots with
 * different item categories.
 *
 * Source: https://oldschool.runescape.wiki/w/Clue_scroll_(beginner)/Rewards Source:
 * https://oldschool.runescape.wiki/w/Clue_scroll_(easy)/Rewards
 */
object ClueRewardTablesImpl {

    /** Roll rewards for a beginner clue casket. */
    fun rollBeginnerRewards(): List<Pair<String, Int>> {
        val rewards = mutableListOf<Pair<String, Int>>()

        // Beginner clues have 1-3 reward rolls
        val rollCount = (1..3).random()

        repeat(rollCount) {
            val reward = rollBeginnerRewardSlot()
            if (reward != null) {
                rewards.add(reward)
            }
        }

        return rewards
    }

    /** Roll rewards for an easy clue casket. */
    fun rollEasyRewards(): List<Pair<String, Int>> {
        val rewards = mutableListOf<Pair<String, Int>>()

        // Easy clues have 2-4 reward rolls
        val rollCount = (2..4).random()

        repeat(rollCount) {
            val reward = rollEasyRewardSlot()
            if (reward != null) {
                rewards.add(reward)
            }
        }

        return rewards
    }

    /** Roll a single beginner reward slot. */
    private fun rollBeginnerRewardSlot(): Pair<String, Int>? {
        val totalWeight = BeginnerRewards.sumOf { it.weight }
        var roll = (0 until totalWeight).random()

        for (reward in BeginnerRewards) {
            roll -= reward.weight
            if (roll < 0) {
                val quantity =
                    when (reward.quantity) {
                        is IntRange -> (reward.quantity as IntRange).random()
                        else -> reward.quantity as Int
                    }
                return reward.item to quantity
            }
        }

        return null
    }

    /** Roll a single easy reward slot. */
    private fun rollEasyRewardSlot(): Pair<String, Int>? {
        val totalWeight = EasyRewards.sumOf { it.weight }
        var roll = (0 until totalWeight).random()

        for (reward in EasyRewards) {
            roll -= reward.weight
            if (roll < 0) {
                val quantity =
                    when (reward.quantity) {
                        is IntRange -> (reward.quantity as IntRange).random()
                        else -> reward.quantity as Int
                    }
                return reward.item to quantity
            }
        }

        return null
    }

    /** Data class for reward entries with weights. */
    private data class RewardEntry(val item: String, val quantity: Any, val weight: Int)

    /** Beginner clue reward table (F2P accessible). */
    private val BeginnerRewards =
        listOf(
            // Common rewards (high weight)
            RewardEntry("monk_robe_top", 1, 200),
            RewardEntry("monk_robe_bottom", 1, 200),
            RewardEntry("amulet_of_defence", 1, 150),
            RewardEntry("amulet_of_magic", 1, 150),
            RewardEntry("amulet_of_strength", 1, 150),
            RewardEntry("amulet_of_power", 1, 100),

            // Medium value rewards
            RewardEntry("team_cape", 1, 80),
            RewardEntry("leather_boots", 1, 60),
            RewardEntry("leather_gloves", 1, 60),
            RewardEntry("cape", 1, 50),

            // Low value but useful
            RewardEntry("air_rune", 100..300, 40),
            RewardEntry("water_rune", 100..300, 40),
            RewardEntry("earth_rune", 100..300, 40),
            RewardEntry("fire_rune", 100..300, 40),
            RewardEntry("mind_rune", 100..300, 40),
            RewardEntry("body_rune", 100..300, 40),

            // Coins
            RewardEntry("coins", 500..2000, 100),
            RewardEntry("coins", 2000..5000, 50),

            // Food and supplies
            RewardEntry("trout", 5..15, 30),
            RewardEntry("salmon", 5..10, 20),
            RewardEntry("tuna", 3..8, 15),

            // Rare rewards (low weight)
            RewardEntry("rainbow_cape", 1, 5), // Very rare F2P fashion item
            RewardEntry("highwayman_mask", 1, 10),
            RewardEntry("blue_wizard_hat", 1, 20),
            RewardEntry("black_wizard_hat", 1, 15),
        )

    /** Easy clue reward table. */
    private val EasyRewards =
        listOf(
            // Common rewards
            RewardEntry("steel_full_helm", 1, 100),
            RewardEntry("steel_platebody", 1, 100),
            RewardEntry("steel_platelegs", 1, 100),
            RewardEntry("steel_plateskirt", 1, 100),
            RewardEntry("steel_kiteshield", 1, 100),

            // Black equipment (uncommon)
            RewardEntry("black_full_helm", 1, 60),
            RewardEntry("black_platebody", 1, 60),
            RewardEntry("black_platelegs", 1, 60),
            RewardEntry("black_plateskirt", 1, 60),
            RewardEntry("black_kiteshield", 1, 60),
            RewardEntry("black_dagger", 1, 50),
            RewardEntry("black_sword", 1, 50),
            RewardEntry("black_scimitar", 1, 40),
            RewardEntry("black_longsword", 1, 40),
            RewardEntry("black_mace", 1, 50),
            RewardEntry("black_axe", 1, 50),

            // Mithril equipment (uncommon)
            RewardEntry("mithril_full_helm", 1, 40),
            RewardEntry("mithril_platebody", 1, 40),
            RewardEntry("mithril_platelegs", 1, 40),
            RewardEntry("mithril_plateskirt", 1, 40),
            RewardEntry("mithril_kiteshield", 1, 40),

            // Trimmed armor (rare)
            RewardEntry("black_helm_h1", 1, 15),
            RewardEntry("black_helm_h2", 1, 15),
            RewardEntry("black_helm_h3", 1, 15),
            RewardEntry("black_helm_h4", 1, 15),
            RewardEntry("black_helm_h5", 1, 15),
            RewardEntry("black_shield_h1", 1, 15),
            RewardEntry("black_shield_h2", 1, 15),
            RewardEntry("black_shield_h3", 1, 15),
            RewardEntry("black_shield_h4", 1, 15),
            RewardEntry("black_shield_h5", 1, 15),

            // God pages (very rare)
            RewardEntry("saras_page_1", 1, 5),
            RewardEntry("saras_page_2", 1, 5),
            RewardEntry("saras_page_3", 1, 5),
            RewardEntry("saras_page_4", 1, 5),
            RewardEntry("zammy_page_1", 1, 5),
            RewardEntry("zammy_page_2", 1, 5),
            RewardEntry("zammy_page_3", 1, 5),
            RewardEntry("zammy_page_4", 1, 5),
            RewardEntry("guthix_page_1", 1, 5),
            RewardEntry("guthix_page_2", 1, 5),
            RewardEntry("guthix_page_3", 1, 5),
            RewardEntry("guthix_page_4", 1, 5),

            // Emote items (rare)
            RewardEntry("blue_beret", 1, 20),
            RewardEntry("black_beret", 1, 20),
            RewardEntry("white_beret", 1, 20),
            RewardEntry("red_beret", 1, 20),
            RewardEntry("highwayman_mask", 1, 15),
            RewardEntry("bob_shirt", 1, 25),

            // Runes (common)
            RewardEntry("chaos_rune", 50..150, 60),
            RewardEntry("nature_rune", 30..100, 50),
            RewardEntry("law_rune", 30..100, 50),
            RewardEntry("cosmic_rune", 30..100, 40),
            RewardEntry("death_rune", 20..50, 30),

            // Coins
            RewardEntry("coins", 1000..5000, 100),
            RewardEntry("coins", 5000..15000, 50),
            RewardEntry("coins", 15000..30000, 20),

            // Food and supplies
            RewardEntry("lobster", 5..15, 40),
            RewardEntry("swordfish", 3..10, 30),
            RewardEntry("tuna", 5..15, 35),

            // Jewelry
            RewardEntry("gold_ring", 1, 30),
            RewardEntry("sapphire_ring", 1, 20),
            RewardEntry("emerald_ring", 1, 15),
            RewardEntry("ruby_ring", 1, 10),
            RewardEntry("diamond_ring", 1, 5),
        )
}

/** Object references for clue reward items not in BaseObjs. */
internal object ClueRewardItemObjs : ObjReferences() {
    // Beginner clue items
    val rainbow_cape = find("rainbow_cape")
    val highwayman_mask = find("highwayman_mask")

    // Easy clue trimmed armor
    val black_helm_h1 = find("black_helm_h1")
    val black_helm_h2 = find("black_helm_h2")
    val black_helm_h3 = find("black_helm_h3")
    val black_helm_h4 = find("black_helm_h4")
    val black_helm_h5 = find("black_helm_h5")
    val black_shield_h1 = find("black_shield_h1")
    val black_shield_h2 = find("black_shield_h2")
    val black_shield_h3 = find("black_shield_h3")
    val black_shield_h4 = find("black_shield_h4")
    val black_shield_h5 = find("black_shield_h5")

    // God pages
    val saras_page_1 = find("saras_page_1")
    val saras_page_2 = find("saras_page_2")
    val saras_page_3 = find("saras_page_3")
    val saras_page_4 = find("saras_page_4")
    val zammy_page_1 = find("zammy_page_1")
    val zammy_page_2 = find("zammy_page_2")
    val zammy_page_3 = find("zammy_page_3")
    val zammy_page_4 = find("zammy_page_4")
    val guthix_page_1 = find("guthix_page_1")
    val guthix_page_2 = find("guthix_page_2")
    val guthix_page_3 = find("guthix_page_3")
    val guthix_page_4 = find("guthix_page_4")

    // Emote items
    val blue_beret = find("blue_beret")
    val black_beret = find("black_beret")
    val white_beret = find("white_beret")
    val red_beret = find("red_beret")
    val bob_shirt = find("bob_shirt")
}
