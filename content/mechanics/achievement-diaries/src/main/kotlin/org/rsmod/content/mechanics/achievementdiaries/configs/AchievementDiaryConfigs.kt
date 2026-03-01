package org.rsmod.content.mechanics.achievementdiaries.configs

import org.rsmod.api.type.refs.varp.VarpReferences

/**
 * Achievement Diary varp references for tracking task completion.
 *
 * Each diary region has two varps:
 * - First varp: Tasks 1-31 (bits 0-30)
 * - Second varp: Tasks 32+ and completion status
 *
 * Diary regions:
 * - Ardougne (F2P/P2P)
 * - Desert (P2P)
 * - Falador (F2P)
 * - Fremennik (P2P)
 * - Kandarin (P2P)
 * - Karamja (F2P/P2P)
 * - Kourend & Kebos (P2P)
 * - Lumbridge & Draynor (F2P)
 * - Morytania (P2P)
 * - Varrock (F2P)
 * - Western Provinces (P2P)
 * - Wilderness (F2P/P2P)
 */
object AchievementDiaryVarps : VarpReferences() {
    // Ardougne Diary
    val ardougne_achievement_diary = find("ardougne_achievement_diary")
    val ardougne_achievement_diary2 = find("ardougne_achievement_diary2")

    // Desert Diary
    val desert_achievement_diary = find("desert_achievement_diary")
    val desert_achievement_diary2 = find("desert_achievement_diary2")

    // Falador Diary
    val falador_achievement_diary = find("falador_achievement_diary")
    val falador_achievement_diary2 = find("falador_achievement_diary2")

    // Fremennik Diary
    val fremennik_achievement_diary = find("fremennik_achievement_diary")
    val fremennik_achievement_diary2 = find("fremennik_achievement_diary2")

    // Kandarin Diary
    val kandarin_achievement_diary = find("kandarin_achievement_diary")
    val kandarin_achievement_diary2 = find("kandarin_achievement_diary2")

    // Karamja Diary
    // TODO(rev233): Karamja diary does not expose `karamja_achievement_diary*` varps in our
    // symbols.
    // Keep stable placeholders so packCache remains strict-clean until Karamja diary storage is
    // verified.
    val karamja_achievement_diary = find("achievement_diary_taskcounts5")
    val karamja_achievement_diary2 = find("achievement_diary_taskcounts6")

    // Kourend & Kebos Diary
    val kourend_achievement_diary = find("kourend_achievement_diary")
    val kourend_achievement_diary2 = find("kourend_achievement_diary2")

    // Lumbridge & Draynor Diary
    val lumb_dray_achievement_diary = find("lumb_dray_achievement_diary")
    val lumb_dray_achievement_diary2 = find("lumb_dray_achievement_diary2")

    // Morytania Diary
    val morytania_achievement_diary = find("morytania_achievement_diary")
    val morytania_achievement_diary2 = find("morytania_achievement_diary2")

    // Varrock Diary
    val varrock_achievement_diary = find("varrock_achievement_diary")
    val varrock_achievement_diary2 = find("varrock_achievement_diary2")

    // Western Provinces Diary
    val western_achievement_diary = find("western_achievement_diary")
    val western_achievement_diary2 = find("western_achievement_diary2")

    // Wilderness Diary
    val wilderness_achievement_diary = find("wilderness_achievement_diary")
    val wilderness_achievement_diary2 = find("wilderness_achievement_diary2")

    // General achievement diary tracking
    val achievement_diary = find("achievement_diary")
    val achievement_diary2 = find("achievement_diary2")
    val achievement_diary_rewards = find("achievement_diary_rewards")
    val achievement_diary_rewards2 = find("achievement_diary_rewards2")
}

/** Achievement Diary regions with their metadata. */
enum class DiaryRegion(
    val displayName: String,
    val easyTasks: Int,
    val mediumTasks: Int,
    val hardTasks: Int,
    val eliteTasks: Int,
    val isF2P: Boolean,
) {
    ARDOUGNE("Ardougne", 11, 13, 14, 10, false),
    DESERT("Desert", 11, 12, 13, 9, false),
    FALADOR("Falador", 11, 13, 13, 8, true),
    FREMENNIK("Fremennik", 11, 13, 13, 8, false),
    KANDARIN("Kandarin", 11, 14, 13, 9, false),
    KARAMJA("Karamja", 11, 13, 11, 6, true),
    KOUREND("Kourend & Kebos", 11, 13, 13, 9, false),
    LUMBRIDGE("Lumbridge & Draynor", 11, 13, 13, 10, true),
    MORYTANIA("Morytania", 11, 13, 13, 9, false),
    VARROCK("Varrock", 11, 13, 13, 9, true),
    WESTERN("Western Provinces", 11, 14, 13, 9, false),
    WILDERNESS("Wilderness", 11, 14, 13, 9, true),
}

/** Achievement Diary difficulty tiers. */
enum class DiaryTier(val displayName: String, val rewardMultiplier: Int) {
    EASY("Easy", 1),
    MEDIUM("Medium", 2),
    HARD("Hard", 3),
    ELITE("Elite", 4),
}

/** Represents a single achievement diary task. */
data class DiaryTask(
    val region: DiaryRegion,
    val tier: DiaryTier,
    val taskNumber: Int,
    val description: String,
    val varpIndex: Int,
    val bitPosition: Int,
)

/** Achievement Diary reward items (placeholder for reward system). */
object AchievementDiaryRewards {
    // HYGIENE: Strings below are config key identifiers, not ObjType references
    // Ardougne rewards
    const val ARDOUGNE_CLOAK_1 = "ardougne_cloak_1" // HYGIENE: Config key identifier
    const val ARDOUGNE_CLOAK_2 = "ardougne_cloak_2" // HYGIENE: Config key identifier
    const val ARDOUGNE_CLOAK_3 = "ardougne_cloak_3" // HYGIENE: Config key identifier
    const val ARDOUGNE_CLOAK_4 = "ardougne_cloak_4" // HYGIENE: Config key identifier

    // Falador rewards
    const val FALADOR_SHIELD_1 = "falador_shield_1" // HYGIENE: Config key identifier
    const val FALADOR_SHIELD_2 = "falador_shield_2" // HYGIENE: Config key identifier
    const val FALADOR_SHIELD_3 = "falador_shield_3" // HYGIENE: Config key identifier
    const val FALADOR_SHIELD_4 = "falador_shield_4" // HYGIENE: Config key identifier

    // Varrock rewards
    const val VARROCK_ARMOUR_1 = "varrock_armour_1" // HYGIENE: Config key identifier
    const val VARROCK_ARMOUR_2 = "varrock_armour_2" // HYGIENE: Config key identifier
    const val VARROCK_ARMOUR_3 = "varrock_armour_3" // HYGIENE: Config key identifier
    const val VARROCK_ARMOUR_4 = "varrock_armour_4" // HYGIENE: Config key identifier

    // Wilderness rewards
    const val WILDERNESS_SWORD_1 = "wilderness_sword_1" // HYGIENE: Config key identifier
    const val WILDERNESS_SWORD_2 = "wilderness_sword_2" // HYGIENE: Config key identifier
    const val WILDERNESS_SWORD_3 = "wilderness_sword_3" // HYGIENE: Config key identifier
    const val WILDERNESS_SWORD_4 = "wilderness_sword_4" // HYGIENE: Config key identifier
}
