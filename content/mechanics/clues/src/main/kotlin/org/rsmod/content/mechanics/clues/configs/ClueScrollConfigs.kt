package org.rsmod.content.mechanics.clues.configs

import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.varp.VarpReferences

/** Clue Scroll varp references for tracking clue progress. */
object ClueScrollVarps : VarpReferences() {
    val cluequest_main = find("cluequest_main")
    val cluequest_prev_coord = find("cluequest_prev_coord")
    val completed_clues = find("completed_clues")
    val completed_clues_total = find("completed_clues_total")
}

/** Clue Scroll item references. */
object ClueScrollObjs : ObjReferences() {
    // Beginner clue scrolls (F2P)
    val beginner_clue_scroll = find("trail_clue_beginner")

    // Easy clue scrolls
    val easy_clue_scroll = find("trail_clue_easy_simple001")

    // Caskets
    val beginner_casket = find("trail_reward_casket_beginner")
    val easy_casket = find("trail_reward_casket_easy")
    val casket = find("casket")
}

/** Clue scroll difficulty tiers. */
enum class ClueTier(
    val displayName: String,
    val minSteps: Int,
    val maxSteps: Int,
    val isF2P: Boolean,
) {
    BEGINNER("Beginner", 1, 4, true),
    EASY("Easy", 2, 6, false),
    MEDIUM("Medium", 3, 5, false),
    HARD("Hard", 4, 6, false),
    ELITE("Elite", 5, 7, false),
    MASTER("Master", 6, 8, false),
}

/** Types of clue scroll steps. */
enum class ClueStepType {
    DIG,
    EMOTE,
    GEAR_CHECK,
    MAP,
    CRYPTIC,
    ANAGRAM,
    COORDINATE,
    SIMPLE,
    PUZZLE,
}

/** Represents a single clue scroll step. */
data class ClueStep(
    val id: Int,
    val tier: ClueTier,
    val type: ClueStepType,
    val description: String,
    val answer: String? = null,
    val coordX: Int? = null,
    val coordZ: Int? = null,
    val plane: Int = 0,
    val emote: String? = null,
    val requiredItems: List<String>? = null,
)

/** Clue scroll drop table entries. */
data class ClueDrop(
    val npcId: Int,
    val npcName: String,
    val beginnerRate: Int, // 1 in X
    val easyRate: Int,
    val mediumRate: Int = 0,
    val hardRate: Int = 0,
    val eliteRate: Int = 0,
)

/** Beginner clue scroll steps (F2P accessible). Based on OSRS beginner clue scroll data. */
object BeginnerClueSteps {
    val steps =
        listOf(
            // Emote clues
            ClueStep(
                1,
                ClueTier.BEGINNER,
                ClueStepType.EMOTE,
                "Bow outside the entrance to the Legends' Guild.",
                coordX = 2728,
                coordZ = 3348,
                emote = "BOW",
            ),
            ClueStep(
                2,
                ClueTier.BEGINNER,
                ClueStepType.EMOTE,
                "Dance a jig by the entrance to the Fishing Guild.",
                coordX = 2610,
                coordZ = 3391,
                emote = "JIG",
            ),
            ClueStep(
                3,
                ClueTier.BEGINNER,
                ClueStepType.EMOTE,
                "Wave on the northern wall of Castle Drakan.",
                coordX = 3562,
                coordZ = 3350,
                emote = "WAVE",
            ),

            // Dig clues
            ClueStep(
                4,
                ClueTier.BEGINNER,
                ClueStepType.DIG,
                "Dig near the entrance to the Lumber Yard.",
                coordX = 3301,
                coordZ = 3490,
            ),
            ClueStep(
                5,
                ClueTier.BEGINNER,
                ClueStepType.DIG,
                "Dig in the center of the cabbage patch north of Port Sarim.",
                coordX = 3049,
                coordZ = 3287,
            ),

            // Simple clues (talk to NPC)
            ClueStep(
                6,
                ClueTier.BEGINNER,
                ClueStepType.SIMPLE,
                "Talk to the Chemist in Rimmington.",
                answer = "Chemist",
            ),
            ClueStep(
                7,
                ClueTier.BEGINNER,
                ClueStepType.SIMPLE,
                "Talk to Hans in Lumbridge Castle.",
                answer = "Hans",
            ),
            ClueStep(
                8,
                ClueTier.BEGINNER,
                ClueStepType.SIMPLE,
                "Talk to the Estate Agent in Varrock.",
                answer = "Estate Agent",
            ),
        )
}

/** Clue scroll drop sources for F2P. */
object F2PClueDrops {
    val drops =
        listOf(
            // Common F2P NPCs that drop beginner clues
            ClueDrop(1, "Man", 128, 128),
            ClueDrop(2, "Woman", 128, 128),
            ClueDrop(3, "Goblin", 128, 128),
            ClueDrop(4, "Chicken", 256, 0),
            ClueDrop(5, "Cow", 256, 0),
            ClueDrop(6, "Guard", 128, 128),
            ClueDrop(7, "Hill Giant", 64, 64),
            ClueDrop(8, "Moss Giant", 64, 64),
            ClueDrop(9, "Barbarian", 128, 128),
        )
}

/** Reward casket loot tables. */
object ClueRewardTables {
    /** Beginner clue rewards (F2P accessible). */
    val beginnerRewards =
        listOf(
            // Common rewards
            "monk_robe_top" to 20,
            "monk_robe_bottom" to 20,
            "amulet_of_defence" to 15,
            "amulet_of_magic" to 15,
            "amulet_of_strength" to 15,

            // Uncommon rewards
            "team_cape" to 10,
            "leather_boots" to 10,
            "leather_gloves" to 10,

            // Rare rewards
            "rainbow_cape" to 1, // Very rare F2P fashion item
        )
}
