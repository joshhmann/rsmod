package org.rsmod.content.mechanics.achievementdiaries.hooks

import jakarta.inject.Inject
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.onEvent
import org.rsmod.content.mechanics.achievementdiaries.configs.AchievementDiaryVarps
import org.rsmod.content.mechanics.achievementdiaries.events.*
import org.rsmod.game.entity.Player
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Hooks for achievement diary task completion.
 *
 * This script subscribes to diary-related events and updates task completion status when players
 * perform required actions.
 */
class DiaryTaskHooks @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // Mining events
        onEvent<OreMinedEvent> { onIronMined(player, oreType, locX, locZ) }

        // Woodcutting events
        onEvent<TreeChoppedEvent> { onTreeChopped(player, treeType, locX, locZ) }

        // Crafting events
        onEvent<PotteryFiredEvent> { onPotteryFired(player, product, locX, locZ) }

        // Runecrafting events
        onEvent<RunesCraftedEvent> { onRunesCrafted(player, runeType) }

        // Fishing events
        onEvent<FishCaughtEvent> { onFishCaught(player, fishType, locX, locZ) }

        // Thieving events
        onEvent<StallThievedEvent> { onStallThieved(player, stallType) }

        // Combat events
        onEvent<NpcKilledEvent> { onNpcKilled(player, npcName, locX, locZ) }

        // Agility events
        onEvent<AgilityShortcutCompletedEvent> {
            onAgilityShortcut(player, shortcutName, locX, locZ)
        }

        // Dungeon events
        onEvent<DungeonLevelEnteredEvent> { onDungeonLevelEntered(player, dungeonName, level) }

        // Shop events
        onEvent<ShopBrowsedEvent> { onShopBrowsed(player, shopName, npcName) }
        onEvent<ItemPurchasedEvent> { onItemPurchased(player, shopName, item) }

        // NPC interaction events
        onEvent<NpcTalkedToEvent> { onNpcTalkedTo(player, npcName) }
        onEvent<NpcTeleportEvent> { onNpcTeleport(player, npcName, destination) }

        // Item use events
        onEvent<ItemUsedOnNpcEvent> { onItemUsedOnNpc(player, item, npcName) }
    }

    // ==================== VARROCK EASY TASKS ====================

    /** Task 3: Mine iron in south-east Varrock mining patch. */
    private fun onIronMined(player: Player, oreType: ObjType, locX: Int, locZ: Int) {
        // Check if iron ore and in south-east Varrock mine area
        if (oreType.internalName == "iron_ore" && locX in 3280..3295 && locZ in 3360..3375) {
            player.completeVarrockEasyTask(2)
        }
    }

    /** Task 7: Chop dying tree in Lumber Yard. */
    private fun onTreeChopped(player: Player, treeType: LocType, locX: Int, locZ: Int) {
        // Check if in Lumber Yard and is a dying tree
        if (locX in 3295..3310 && locZ in 3490..3510) {
            if (treeType.internalName == "dying_tree") {
                player.completeVarrockEasyTask(6)
            }
        }
    }

    /** Task 10: Spin and fire a bowl in Barbarian Village. */
    private fun onPotteryFired(player: Player, product: ObjType, locX: Int, locZ: Int) {
        // Check if in Barbarian Village pottery house
        if (locX in 3070..3085 && locZ in 3400..3415) {
            if (product.internalName == "bowl") {
                player.completeVarrockEasyTask(9)
            }
        }
    }

    /** Task 12: Craft Earth runes from Essence. */
    private fun onRunesCrafted(player: Player, runeType: ObjType) {
        if (runeType.internalName == "earth_rune") {
            player.completeVarrockEasyTask(11)
        }
    }

    /** Task 13: Catch trout at Barbarian Village. */
    private fun onFishCaught(player: Player, fishType: ObjType, locX: Int, locZ: Int) {
        // Check if at River Lum in Barbarian Village
        if (locX in 3100..3115 && locZ in 3420..3435) {
            if (fishType.internalName == "trout") {
                player.completeVarrockEasyTask(12)
            }
        }
    }

    /** Task 14: Steal from Tea stall in Varrock. */
    private fun onStallThieved(player: Player, stallType: LocType) {
        if (stallType.internalName == "tea_stall") {
            player.completeVarrockEasyTask(13)
        }
    }

    /** Task 5: Enter Stronghold of Security level 2. */
    private fun onDungeonLevelEntered(player: Player, dungeonName: String, level: Int) {
        if (dungeonName == "stronghold_of_security" && level == 2) {
            player.completeVarrockEasyTask(4)
        }
    }

    /** Task 6: Jump fence south of Varrock. */
    private fun onAgilityShortcut(player: Player, shortcutName: String, locX: Int, locZ: Int) {
        // Check if it's the Varrock south fence shortcut
        if (shortcutName == "varrock_south_fence" || (locX in 3235..3245 && locZ in 3330..3340)) {
            player.completeVarrockEasyTask(5)
        }
    }

    /** Task 1: Browse Thessalia's store. */
    private fun onShopBrowsed(player: Player, shopName: String, npcName: String) {
        if (npcName == "thessalia" || shopName == "thessalias_fine_clothes") {
            player.completeVarrockEasyTask(0)
        }
    }

    /** Task 8: Buy a newspaper. */
    private fun onItemPurchased(player: Player, shopName: String, item: ObjType) {
        if (item.internalName == "newspaper") {
            player.completeVarrockEasyTask(7)
        }
    }

    /** Task 2: Have Aubury teleport you to the Essence mine. */
    private fun onNpcTeleport(player: Player, npcName: String, destination: String) {
        if (npcName == "aubury" && destination == "rune_essence_mine") {
            player.completeVarrockEasyTask(1)
        }
    }

    /** Task 9: Give a dog a bone. */
    private fun onItemUsedOnNpc(player: Player, item: ObjType, npcName: String) {
        if (item.internalName == "bones" && npcName == "stray_dog") {
            player.completeVarrockEasyTask(8)
        }
    }

    /** Task 11: Speak to Haig Halen with 50+ Kudos. */
    private fun onNpcTalkedTo(player: Player, npcName: String) {
        if (npcName == "haig_halen") {
            // TODO: Check if player has 50+ Kudos
            // player.completeVarrockEasyTask(10)
        }
    }

    // ==================== FALADOR EASY TASKS (F2P) ====================

    /** Task 4: Kill a duck in Falador Park. */
    private fun onNpcKilled(player: Player, npcName: String, locX: Int, locZ: Int) {
        // Check if in Falador Park
        if (locX in 2985..3025 && locZ in 3375..3410) {
            if (npcName.lowercase().contains("duck")) {
                player.completeFaladorEasyTask(3)
            }
        }
    }
}

// ==================== VARROCK EASY EXTENSIONS ====================

private var Player.varrockDiary by intVarp(AchievementDiaryVarps.varrock_achievement_diary)

/** Complete a Varrock Easy task by setting the appropriate bit */
fun Player.completeVarrockEasyTask(taskNumber: Int) {
    if (isVarrockEasyTaskComplete(taskNumber)) return

    val bitPosition = taskNumber
    varrockDiary = varrockDiary or (1 shl bitPosition)

    // Send completion message
    // mes("Congratulations! You have completed a Varrock Easy task.")
}

/** Check if a Varrock Easy task is complete */
fun Player.isVarrockEasyTaskComplete(taskNumber: Int): Boolean {
    val bitPosition = taskNumber
    return (varrockDiary and (1 shl bitPosition)) != 0
}

// ==================== FALADOR EASY EXTENSIONS ====================

private var Player.faladorDiary by intVarp(AchievementDiaryVarps.falador_achievement_diary)

/** Complete a Falador Easy task by setting the appropriate bit */
fun Player.completeFaladorEasyTask(taskNumber: Int) {
    if (isFaladorEasyTaskComplete(taskNumber)) return

    val bitPosition = taskNumber
    faladorDiary = faladorDiary or (1 shl bitPosition)

    // Send completion message
    // mes("Congratulations! You have completed a Falador Easy task.")
}

/** Check if a Falador Easy task is complete */
fun Player.isFaladorEasyTaskComplete(taskNumber: Int): Boolean {
    val bitPosition = taskNumber
    return (faladorDiary and (1 shl bitPosition)) != 0
}
