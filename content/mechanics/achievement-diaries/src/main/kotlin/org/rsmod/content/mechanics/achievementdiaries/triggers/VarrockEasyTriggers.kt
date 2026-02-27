package org.rsmod.content.mechanics.achievementdiaries.triggers

import jakarta.inject.Inject
import org.rsmod.api.player.vars.intVarp
import org.rsmod.content.mechanics.achievementdiaries.configs.AchievementDiaryVarps
import org.rsmod.game.entity.Player
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Event triggers for Varrock Easy Achievement Diary tasks.
 *
 * This script hooks into game events to automatically detect and complete Varrock Easy diary tasks
 * when players perform the required actions.
 *
 * Note: Many triggers require integration with other systems (mining, fishing, woodcutting, etc.)
 * and are documented here for reference.
 */
class VarrockEasyTriggers
@Inject
constructor(private val locTypes: LocTypeList, private val npcTypes: NpcTypeList) : PluginScript() {

    override fun ScriptContext.startup() {
        // Task triggers are implemented in their respective content scripts.
        // This class provides helper methods for those scripts to use.

        // Task 1: Browse Thessalia's store
        // Trigger: onOpNpc1(thessalia) - implemented in Thessalia's shop script

        // Task 2: Aubury teleport to Essence mine
        // Trigger: onNpcTeleport(aubury) - implemented in Aubury's dialogue

        // Task 3: Mine iron in south-east Varrock
        // Trigger: onMineComplete(iron_rocks) with location check

        // Task 4: Make plank at Sawmill
        // Trigger: onPlankCreated(normal_plank)

        // Task 5: Enter Stronghold of Security level 2
        // Trigger: onEnterDungeonLevel(stronghold_of_security, 2)

        // Task 6: Jump fence south of Varrock
        // Trigger: onAgilityShortcutComplete(fence_south_varrock)

        // Task 7: Chop dying tree in Lumber Yard
        // Trigger: onTreeChopped(dying_tree) with location check

        // Task 8: Buy newspaper from Benny
        // Trigger: onOpNpc1(benny) - implemented in Benny's shop script

        // Task 9: Give dog a bone
        // Trigger: onUseItemOnNpc(bones, stray_dog)

        // Task 10: Spin and fire bowl in Barbarian Village
        // Trigger: onPotteryFired(bowl) with location check

        // Task 11: Speak to Haig Halen with 50+ Kudos
        // Trigger: onOpNpc1(haig_halen) with kudos check

        // Task 12: Craft Earth runes
        // Trigger: onRunesCrafted(earth_rune)

        // Task 13: Catch trout at Barbarian Village
        // Trigger: onFishCaught(trout) with location check

        // Task 14: Steal from Tea stall
        // Trigger: onStallThieved(tea_stall)
    }

    /** Check if player is in the south-east Varrock mining area */
    fun isInSoutheastVarrockMine(player: Player): Boolean {
        // Approximate coordinates for south-east Varrock mine
        return player.coords.x in 3280..3295 && player.coords.z in 3360..3375
    }

    /** Check if player is in the Lumber Yard */
    fun isInLumberYard(player: Player): Boolean {
        // Approximate coordinates for Lumber Yard
        return player.coords.x in 3295..3310 && player.coords.z in 3490..3510
    }

    /** Check if player is at Barbarian Village pottery */
    fun isInBarbarianVillagePottery(player: Player): Boolean {
        // Approximate coordinates for Barbarian Village pottery house
        return player.coords.x in 3070..3085 && player.coords.z in 3400..3415
    }

    /** Check if player is fishing at River Lum in Barbarian Village */
    fun isAtBarbarianVillageFishing(player: Player): Boolean {
        // Approximate coordinates for River Lum at Barbarian Village
        return player.coords.x in 3100..3115 && player.coords.z in 3420..3435
    }

    /** Check if player has at least 50 Kudos */
    fun has50Kudos(player: Player): Boolean {
        // TODO: Implement kudos tracking
        return false
    }
}

/** Player extension properties */
private var Player.varrockDiary by intVarp(AchievementDiaryVarps.varrock_achievement_diary)

/** Complete a Varrock Easy task by setting the appropriate bit */
fun Player.completeVarrockEasyTask(taskNumber: Int) {
    if (isVarrockEasyTaskComplete(taskNumber)) return

    val bitPosition = taskNumber
    varrockDiary = varrockDiary or (1 shl bitPosition)

    // Send completion message
    // TODO: Use proper message API when available
}

/** Check if a Varrock Easy task is complete */
fun Player.isVarrockEasyTaskComplete(taskNumber: Int): Boolean {
    val bitPosition = taskNumber
    return (varrockDiary and (1 shl bitPosition)) != 0
}
