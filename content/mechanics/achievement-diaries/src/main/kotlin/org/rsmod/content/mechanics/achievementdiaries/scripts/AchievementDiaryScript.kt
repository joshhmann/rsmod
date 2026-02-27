package org.rsmod.content.mechanics.achievementdiaries.scripts

import jakarta.inject.Inject
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.onPlayerLogin
import org.rsmod.content.mechanics.achievementdiaries.configs.AchievementDiaryVarps
import org.rsmod.content.mechanics.achievementdiaries.configs.DiaryRegion
import org.rsmod.content.mechanics.achievementdiaries.configs.DiaryTier
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Achievement Diary system implementation.
 *
 * Tracks player progress across all achievement diary regions and tiers. Handles task completion,
 * diary completion checks, and reward eligibility.
 */
class AchievementDiaryScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        onPlayerLogin { player.initializeDiaryTracking() }
    }

    /**
     * Initializes diary tracking for a player on login. Ensures all diary varps are properly set
     * up.
     */
    private fun Player.initializeDiaryTracking() {
        // Check each diary region for completion status
        DiaryRegion.entries.forEach { region ->
            DiaryTier.entries.forEach { tier ->
                if (isDiaryTierComplete(region, tier)) {
                    // Grant any pending rewards or update status
                    updateDiaryCompletionStatus(region, tier)
                }
            }
        }
    }

    /** Checks if a specific diary tier is complete. */
    private fun Player.isDiaryTierComplete(region: DiaryRegion, tier: DiaryTier): Boolean {
        val taskCount = getTaskCountForTier(region, tier)
        val completedTasks = countCompletedTasks(region, tier)
        return completedTasks >= taskCount
    }

    /** Gets the number of tasks required for a tier. */
    private fun getTaskCountForTier(region: DiaryRegion, tier: DiaryTier): Int {
        return when (tier) {
            DiaryTier.EASY -> region.easyTasks
            DiaryTier.MEDIUM -> region.mediumTasks
            DiaryTier.HARD -> region.hardTasks
            DiaryTier.ELITE -> region.eliteTasks
        }
    }

    /** Counts completed tasks for a specific region and tier. */
    fun Player.countCompletedTasks(region: DiaryRegion, tier: DiaryTier): Int {
        val varpValue = getDiaryVarp(region, tier)
        val taskCount = getTaskCountForTier(region, tier)

        // Count set bits in the varp (each bit represents a completed task)
        var count = 0
        for (i in 0 until taskCount) {
            if ((varpValue and (1 shl i)) != 0) {
                count++
            }
        }
        return count
    }

    /** Gets the diary varp value for a region and tier. */
    private fun Player.getDiaryVarp(region: DiaryRegion, tier: DiaryTier): Int {
        return when (region) {
            DiaryRegion.VARROCK ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> varrockDiary
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> varrockDiary2
                }
            DiaryRegion.ARDOUGNE ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> ardougneDiary
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> ardougneDiary2
                }
            DiaryRegion.DESERT ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> desertDiary
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> desertDiary2
                }
            DiaryRegion.FALADOR ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> faladorDiary
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> faladorDiary2
                }
            DiaryRegion.FREMENNIK ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> fremennikDiary
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> fremennikDiary2
                }
            DiaryRegion.KANDARIN ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> kandarinDiary
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> kandarinDiary2
                }
            DiaryRegion.KARAMJA ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> karamjaDiary
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> karamjaDiary2
                }
            DiaryRegion.KOUREND ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> kourendDiary
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> kourendDiary2
                }
            DiaryRegion.LUMBRIDGE ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> lumbridgeDiary
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> lumbridgeDiary2
                }
            DiaryRegion.MORYTANIA ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> morytaniaDiary
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> morytaniaDiary2
                }
            DiaryRegion.WESTERN ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> westernDiary
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> westernDiary2
                }
            DiaryRegion.WILDERNESS ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> wildernessDiary
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> wildernessDiary2
                }
        }
    }

    /** Sets the diary varp value for a region and tier. */
    private fun Player.setDiaryVarp(region: DiaryRegion, tier: DiaryTier, value: Int) {
        when (region) {
            DiaryRegion.VARROCK ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> varrockDiary = value
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> varrockDiary2 = value
                }
            DiaryRegion.ARDOUGNE ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> ardougneDiary = value
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> ardougneDiary2 = value
                }
            DiaryRegion.DESERT ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> desertDiary = value
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> desertDiary2 = value
                }
            DiaryRegion.FALADOR ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> faladorDiary = value
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> faladorDiary2 = value
                }
            DiaryRegion.FREMENNIK ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> fremennikDiary = value
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> fremennikDiary2 = value
                }
            DiaryRegion.KANDARIN ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> kandarinDiary = value
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> kandarinDiary2 = value
                }
            DiaryRegion.KARAMJA ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> karamjaDiary = value
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> karamjaDiary2 = value
                }
            DiaryRegion.KOUREND ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> kourendDiary = value
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> kourendDiary2 = value
                }
            DiaryRegion.LUMBRIDGE ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> lumbridgeDiary = value
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> lumbridgeDiary2 = value
                }
            DiaryRegion.MORYTANIA ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> morytaniaDiary = value
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> morytaniaDiary2 = value
                }
            DiaryRegion.WESTERN ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> westernDiary = value
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> westernDiary2 = value
                }
            DiaryRegion.WILDERNESS ->
                when (tier) {
                    DiaryTier.EASY,
                    DiaryTier.MEDIUM -> wildernessDiary = value
                    DiaryTier.HARD,
                    DiaryTier.ELITE -> wildernessDiary2 = value
                }
        }
    }

    /** Updates the diary completion status and grants rewards if applicable. */
    private fun Player.updateDiaryCompletionStatus(region: DiaryRegion, tier: DiaryTier) {
        // TODO: Implement reward granting and status updates
        // - Check if reward already claimed
        // - Grant diary item reward
        // - Apply passive benefits
    }

    /**
     * Marks a specific task as complete.
     *
     * @param player The player completing the task
     * @param region The diary region
     * @param tier The diary tier
     * @param taskNumber The task number (0-indexed within the tier)
     */
    fun completeTask(player: Player, region: DiaryRegion, tier: DiaryTier, taskNumber: Int) {
        if (isTaskComplete(player, region, tier, taskNumber)) {
            return // Already complete
        }

        val currentVarp = player.getDiaryVarp(region, tier)
        val newVarp = currentVarp or (1 shl taskNumber)
        player.setDiaryVarp(region, tier, newVarp)

        // Notify player of task completion
        player.message(
            "Congratulations! You have completed a task: ${region.name} ${tier.name} Task #${taskNumber + 1}"
        )

        // Check for tier completion
        if (player.isDiaryTierComplete(region, tier)) {
            player.message(
                "Congratulations! You have completed the ${region.name} ${tier.name} Diary!"
            )
            player.updateDiaryCompletionStatus(region, tier)
        }
    }

    /** Checks if a player has completed a specific task. */
    fun isTaskComplete(
        player: Player,
        region: DiaryRegion,
        tier: DiaryTier,
        taskNumber: Int,
    ): Boolean {
        val varpValue = player.getDiaryVarp(region, tier)
        return (varpValue and (1 shl taskNumber)) != 0
    }

    /** Gets the completion percentage for a diary tier. */
    fun getCompletionPercentage(player: Player, region: DiaryRegion, tier: DiaryTier): Int {
        val total = getTaskCountForTier(region, tier)
        val completed = player.countCompletedTasks(region, tier)
        return if (total > 0) (completed * 100) / total else 0
    }

    /** Sends a game message to the player. */
    private fun Player.message(text: String) {
        mes(text)
    }
}

/** Player extension properties for diary varps. */
private var Player.ardougneDiary by intVarp(AchievementDiaryVarps.ardougne_achievement_diary)
private var Player.ardougneDiary2 by intVarp(AchievementDiaryVarps.ardougne_achievement_diary2)
private var Player.desertDiary by intVarp(AchievementDiaryVarps.desert_achievement_diary)
private var Player.desertDiary2 by intVarp(AchievementDiaryVarps.desert_achievement_diary2)
private var Player.faladorDiary by intVarp(AchievementDiaryVarps.falador_achievement_diary)
private var Player.faladorDiary2 by intVarp(AchievementDiaryVarps.falador_achievement_diary2)
private var Player.fremennikDiary by intVarp(AchievementDiaryVarps.fremennik_achievement_diary)
private var Player.fremennikDiary2 by intVarp(AchievementDiaryVarps.fremennik_achievement_diary2)
private var Player.kandarinDiary by intVarp(AchievementDiaryVarps.kandarin_achievement_diary)
private var Player.kandarinDiary2 by intVarp(AchievementDiaryVarps.kandarin_achievement_diary2)
private var Player.karamjaDiary by intVarp(AchievementDiaryVarps.karamja_achievement_diary)
private var Player.karamjaDiary2 by intVarp(AchievementDiaryVarps.karamja_achievement_diary2)
private var Player.kourendDiary by intVarp(AchievementDiaryVarps.kourend_achievement_diary)
private var Player.kourendDiary2 by intVarp(AchievementDiaryVarps.kourend_achievement_diary2)
private var Player.lumbridgeDiary by intVarp(AchievementDiaryVarps.lumb_dray_achievement_diary)
private var Player.lumbridgeDiary2 by intVarp(AchievementDiaryVarps.lumb_dray_achievement_diary2)
private var Player.morytaniaDiary by intVarp(AchievementDiaryVarps.morytania_achievement_diary)
private var Player.morytaniaDiary2 by intVarp(AchievementDiaryVarps.morytania_achievement_diary2)
private var Player.varrockDiary by intVarp(AchievementDiaryVarps.varrock_achievement_diary)
private var Player.varrockDiary2 by intVarp(AchievementDiaryVarps.varrock_achievement_diary2)
private var Player.westernDiary by intVarp(AchievementDiaryVarps.western_achievement_diary)
private var Player.westernDiary2 by intVarp(AchievementDiaryVarps.western_achievement_diary2)
private var Player.wildernessDiary by intVarp(AchievementDiaryVarps.wilderness_achievement_diary)
private var Player.wildernessDiary2 by intVarp(AchievementDiaryVarps.wilderness_achievement_diary2)
