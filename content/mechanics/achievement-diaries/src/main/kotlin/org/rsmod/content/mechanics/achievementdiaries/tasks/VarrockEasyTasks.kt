package org.rsmod.content.mechanics.achievementdiaries.tasks

import jakarta.inject.Inject
import org.rsmod.api.player.vars.intVarp
import org.rsmod.content.mechanics.achievementdiaries.configs.AchievementDiaryVarps
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Varrock Easy Achievement Diary tasks implementation.
 *
 * Tasks:
 * 1. Browse Thessalia's store
 * 2. Have Aubury teleport you to the Essence mine
 * 3. Mine some iron in the south-east Varrock mining patch
 * 4. Make a normal plank at the Sawmill
 * 5. Enter the second level of the Stronghold of Security
 * 6. Jump over the fence south of Varrock
 * 7. Chop down a dying tree in the Lumber Yard
 * 8. Buy a newspaper
 * 9. Give a dog a bone
 * 10. Spin a bowl on the pottery wheel and fire it in Barbarian Village
 * 11. Speak to Haig Halen after obtaining at least 50 Kudos
 * 12. Craft some Earth runes from Essence
 * 13. Catch some trout in the River Lum at Barbarian Village
 * 14. Steal from the Tea stall in Varrock
 */
class VarrockEasyTasks @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // Task completion listeners will be registered here
        // Each task needs specific event handlers
    }

    /** Task 1: Browse Thessalia's store */
    fun completeTask1(player: Player) {
        player.completeVarrockEasyTask(0)
    }

    /** Task 2: Have Aubury teleport you to the Essence mine */
    fun completeTask2(player: Player) {
        player.completeVarrockEasyTask(1)
    }

    /** Task 3: Mine iron in south-east Varrock mining patch */
    fun completeTask3(player: Player) {
        player.completeVarrockEasyTask(2)
    }

    /** Task 4: Make a normal plank at the Sawmill */
    fun completeTask4(player: Player) {
        player.completeVarrockEasyTask(3)
    }

    /** Task 5: Enter second level of Stronghold of Security */
    fun completeTask5(player: Player) {
        player.completeVarrockEasyTask(4)
    }

    /** Task 6: Jump over fence south of Varrock */
    fun completeTask6(player: Player) {
        player.completeVarrockEasyTask(5)
    }

    /** Task 7: Chop dying tree in Lumber Yard */
    fun completeTask7(player: Player) {
        player.completeVarrockEasyTask(6)
    }

    /** Task 8: Buy a newspaper */
    fun completeTask8(player: Player) {
        player.completeVarrockEasyTask(7)
    }

    /** Task 9: Give a dog a bone */
    fun completeTask9(player: Player) {
        player.completeVarrockEasyTask(8)
    }

    /** Task 10: Spin and fire a bowl in Barbarian Village */
    fun completeTask10(player: Player) {
        player.completeVarrockEasyTask(9)
    }

    /** Task 11: Speak to Haig Halen with 50+ Kudos */
    fun completeTask11(player: Player) {
        player.completeVarrockEasyTask(10)
    }

    /** Task 12: Craft Earth runes from Essence */
    fun completeTask12(player: Player) {
        player.completeVarrockEasyTask(11)
    }

    /** Task 13: Catch trout in River Lum at Barbarian Village */
    fun completeTask13(player: Player) {
        player.completeVarrockEasyTask(12)
    }

    /** Task 14: Steal from Tea stall in Varrock */
    fun completeTask14(player: Player) {
        player.completeVarrockEasyTask(13)
    }

    /** Check if all Easy tasks are complete */
    fun isEasyTierComplete(player: Player): Boolean {
        return (0..13).all { player.isVarrockEasyTaskComplete(it) }
    }

    /** Get number of completed Easy tasks */
    fun getCompletedTaskCount(player: Player): Int {
        return (0..13).count { player.isVarrockEasyTaskComplete(it) }
    }
}

/** Player extension properties for Varrock Easy diary */
private var Player.varrockDiary by intVarp(AchievementDiaryVarps.varrock_achievement_diary)
private var Player.varrockDiary2 by intVarp(AchievementDiaryVarps.varrock_achievement_diary2)

/** Complete a specific Varrock Easy task (0-13) */
private fun Player.completeVarrockEasyTask(taskNumber: Int) {
    if (isVarrockEasyTaskComplete(taskNumber)) return

    val bitPosition = taskNumber
    varrockDiary = varrockDiary or (1 shl bitPosition)

    // Notify player of task completion
    // TODO: Send message "Congratulations! You have completed a Varrock Easy task."
}

/** Check if a specific Varrock Easy task is complete */
private fun Player.isVarrockEasyTaskComplete(taskNumber: Int): Boolean {
    val bitPosition = taskNumber
    return (varrockDiary and (1 shl bitPosition)) != 0
}
