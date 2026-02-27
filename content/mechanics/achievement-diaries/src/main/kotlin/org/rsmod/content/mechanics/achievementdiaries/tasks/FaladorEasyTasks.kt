package org.rsmod.content.mechanics.achievementdiaries.tasks

import jakarta.inject.Inject
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.onNpcDeath
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.content.mechanics.achievementdiaries.configs.AchievementDiaryVarps
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Falador Easy Achievement Diary tasks implementation (F2P-accessible tasks).
 *
 * F2P Tasks:
 * 1. Climb over the western Falador wall (requires 5 Agility)
 * 2. Get a Haircut or Shave from the Falador Hairdresser
 * 3. Fill a bucket from the pump north of Falador west bank
 * 4. Kill a duck in Falador Park
 * 5. Make a mind tiara
 * 6. Claim a security book from the Security Guard at Port Sarim jail
 *
 * Note: Falador Diary has 11 Easy tasks total, but only 6 are F2P-accessible. The remaining tasks
 * require P2P skills/content (Construction, Farming, members' areas).
 */
class FaladorEasyTasks @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // Register duck kill listener for Task 4
        onNpcDeath {
            val player = killer ?: return@onNpcDeath
            if (isDuckOrDrake(npc) && isInFaladorPark(player)) {
                player.completeFaladorEasyTask(3) // Task 4 (0-indexed)
            }
        }
    }

    /** Task 1: Climb over the western Falador wall (5 Agility) */
    fun completeTask1(player: Player) {
        player.completeFaladorEasyTask(0)
    }

    /** Task 2: Get a Haircut or Shave from the Falador Hairdresser */
    fun completeTask2(player: Player) {
        player.completeFaladorEasyTask(1)
    }

    /** Task 3: Fill a bucket from the pump north of Falador west bank */
    fun completeTask3(player: Player) {
        player.completeFaladorEasyTask(2)
    }

    /** Task 4: Kill a duck in Falador Park - handled by onNpcDeath */
    fun completeTask4(player: Player) {
        player.completeFaladorEasyTask(3)
    }

    /** Task 5: Make a mind tiara */
    fun completeTask5(player: Player) {
        player.completeFaladorEasyTask(4)
    }

    /** Task 6: Claim a security book from the Security Guard at Port Sarim jail */
    fun completeTask6(player: Player) {
        player.completeFaladorEasyTask(5)
    }

    /** Check if NPC is a duck or drake */
    private fun isDuckOrDrake(npc: Npc): Boolean {
        return npc.type == FaladorNpcs.duck || npc.type == FaladorNpcs.drake
    }

    /** Check if player is in Falador Park area (approximate coordinates) */
    private fun isInFaladorPark(player: Player): Boolean {
        // Falador Park is roughly around coords: x=2985-3020, z=3370-3395
        val x = player.coords.x
        val z = player.coords.z
        return x in 2985..3020 && z in 3370..3395
    }

    /** Check if all F2P Easy tasks are complete */
    fun isEasyTierComplete(player: Player): Boolean {
        return (0..5).all { player.isFaladorEasyTaskComplete(it) }
    }

    /** Get number of completed Easy tasks */
    fun getCompletedTaskCount(player: Player): Int {
        return (0..5).count { player.isFaladorEasyTaskComplete(it) }
    }
}

/** NPC references for Falador diary tasks */
internal object FaladorNpcs : NpcReferences() {
    val duck = find("duck")
    val drake = find("drake")
}

/** Player extension properties for Falador Easy diary */
private var Player.faladorDiary by intVarp(AchievementDiaryVarps.falador_achievement_diary)
private var Player.faladorDiary2 by intVarp(AchievementDiaryVarps.falador_achievement_diary2)

/** Complete a specific Falador Easy task (0-5 for F2P tasks) */
private fun Player.completeFaladorEasyTask(taskNumber: Int) {
    if (isFaladorEasyTaskComplete(taskNumber)) return

    val bitPosition = taskNumber
    faladorDiary = faladorDiary or (1 shl bitPosition)

    // Notify player of task completion
    // TODO: Send message "Congratulations! You have completed a Falador Easy task."
}

/** Check if a specific Falador Easy task is complete */
private fun Player.isFaladorEasyTaskComplete(taskNumber: Int): Boolean {
    val bitPosition = taskNumber
    return (faladorDiary and (1 shl bitPosition)) != 0
}
