package org.rsmod.content.mechanics.achievementdiaries

import org.rsmod.content.mechanics.achievementdiaries.hooks.DiaryTaskHooks
import org.rsmod.content.mechanics.achievementdiaries.scripts.AchievementDiaryScript
import org.rsmod.content.mechanics.achievementdiaries.tasks.VarrockEasyTasks
import org.rsmod.content.mechanics.achievementdiaries.triggers.VarrockEasyTriggers
import org.rsmod.plugin.module.PluginModule

/**
 * Module for Achievement Diary system.
 *
 * Binds all achievement diary scripts including:
 * - Core achievement diary tracking and management
 * - Varrock Easy Tasks
 * - Varrock Easy Triggers (event hooks)
 * - Diary Task Hooks (event subscribers)
 */
class AchievementDiariesModule : PluginModule() {
    override fun bind() {
        bindInstance<AchievementDiaryScript>()
        bindInstance<VarrockEasyTasks>()
        bindInstance<VarrockEasyTriggers>()
        bindInstance<DiaryTaskHooks>()
    }
}
