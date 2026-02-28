package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Warrior Woman and Al-Kharid Warrior NPCs.
 *
 * Drop table source: https://oldschool.runescape.wiki/w/Warrior_woman
 * - Warrior Woman: Level 24, found in Al-Kharid palace
 * - Al-Kharid Warrior: Level 26, found in Al-Kharid palace
 *
 * Drop structure:
 * - Always: Bones (100%)
 * - Main drops: Coins, Steel equipment
 * - Rare: Grimy herbs, Mind talisman
 * - Tertiary: Clue scroll (beginner)
 */
internal object WarriorDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerWarriorWoman(registry)
        // TODO: Al-Kharid Warrior - need to identify correct internal name
        // registerAlkharidWarrior(registry)
    }

    // -----------------------------------------------------------------------
    // Warrior Woman
    // Drop table source: https://oldschool.runescape.wiki/w/Warrior_woman
    // Level 24, found in Al-Kharid palace
    // Always: Bones
    // Main drops: Coins, Steel equipment
    // Rare: Grimy herbs, Mind talisman
    // Tertiary: Clue scroll (beginner)
    // -----------------------------------------------------------------------
    private fun registerWarriorWoman(registry: NpcDropTableRegistry) {
        val warriorWomanTable = dropTable {
            always(objs.bones)

            // Coins
            table("Coins", weight = 50) {
                item(objs.coins, quantity = 5..20, weight = 30)
                item(objs.coins, quantity = 21..50, weight = 15)
                item(objs.coins, quantity = 51..100, weight = 5)
            }

            // Steel equipment
            table("Steel Equipment", weight = 25) {
                item(WarriorObjs.steel_axe, weight = 8)
                item(WarriorObjs.steel_dagger, weight = 7)
                item(WarriorObjs.steel_mace, weight = 6)
                item(WarriorObjs.steel_scimitar, weight = 4)
            }

            // Runes and talismans
            table("Runes/Talismans", weight = 15) {
                item(objs.mindrune, quantity = 5..10, weight = 8)
                item(WarriorObjs.mind_talisman, weight = 3)
                item(WarriorObjs.waterrune, quantity = 5..10, weight = 3)
                item(WarriorObjs.earthrune, quantity = 5..10, weight = 1)
            }

            // Herbs (grimy)
            table("Herbs", weight = 10) {
                nothing(weight = 7)
                item(WarriorObjs.grimy_guam, weight = 2)
                item(WarriorObjs.grimy_marrentill, weight = 1)
            }

            // Tertiary drops - clue scrolls (1/128 each = 2/128 total)
            table("Tertiary", weight = 1) {
                nothing(weight = 126) // 126/128 chance of nothing
                item(DropTableObjs.trail_clue_beginner, weight = 1) // 1/128 beginner
                item(DropTableObjs.trail_clue_easy_simple001, weight = 1) // 1/128 easy
            }
        }

        // Register for warrior woman variants
        registry.register(WarriorNpcs.warrior_woman, warriorWomanTable)
        // Additional variants if they exist
        // registry.register(WarriorNpcs.warrior_woman_variant01, warriorWomanTable)
        // registry.register(WarriorNpcs.warrior_woman_variant02, warriorWomanTable)
    }

    // -----------------------------------------------------------------------
    // Al-Kharid Warrior
    // TODO: Need to identify correct internal name for Al-Kharid Warrior
    // Level 26, found in Al-Kharid palace
    // Similar drops to Warrior Woman but slightly better
    // -----------------------------------------------------------------------
    /*
    private fun registerAlkharidWarrior(registry: NpcDropTableRegistry) {
        val alkharidWarriorTable = dropTable {
            always(objs.bones)

            // Similar to warrior woman but with slightly better drops
            table("Coins", weight = 50) {
                item(objs.coins, quantity = 10..30, weight = 30)
                item(objs.coins, quantity = 31..60, weight = 15)
                item(objs.coins, quantity = 61..120, weight = 5)
            }

            table("Steel Equipment", weight = 30) {
                item(WarriorObjs.steel_axe, weight = 8)
                item(WarriorObjs.steel_dagger, weight = 7)
                item(WarriorObjs.steel_mace, weight = 7)
                item(WarriorObjs.steel_scimitar, weight = 5)
                item(WarriorObjs.steel_med_helm, weight = 3)
            }

            table("Runes/Talismans", weight = 12) {
                item(objs.mindrune, quantity = 5..10, weight = 6)
                item(WarriorObjs.mind_talisman, weight = 3)
                item(WarriorObjs.waterrune, quantity = 5..10, weight = 2)
                item(WarriorObjs.earthrune, quantity = 5..10, weight = 1)
            }

            table("Herbs", weight = 7) {
                nothing(weight = 4)
                item(WarriorObjs.grimy_guam, weight = 2)
                item(WarriorObjs.grimy_marrentill, weight = 1)
            }

            table("Tertiary", weight = 1) {
                item(DropTableObjs.trail_clue_easy_simple001, weight = 1)
            }
        }

        registry.register(WarriorNpcs.alkharid_warrior, alkharidWarriorTable)
    }
    */
}

/** NPC type references for Warrior Woman and Al-Kharid Warrior. */
internal object WarriorNpcs : NpcReferences() {
    // Warrior Woman - Level 24, Al-Kharid palace
    val warrior_woman = find("warrior_woman")
    // val warrior_woman_variant01 = find("warrior_woman_variant01")
    // val warrior_woman_variant02 = find("warrior_woman_variant02")

    // TODO: Al-Kharid Warrior - Level 26, need correct internal name
    // val alkharid_warrior = find("al_kharid_warrior")
}

/** Object type references for Warrior drops not in BaseObjs or DropTableObjs. */
internal object WarriorObjs : ObjReferences() {
    // Steel equipment
    val steel_axe = find("steel_axe")
    val steel_dagger = find("steel_dagger")
    val steel_mace = find("steel_mace")
    val steel_scimitar = find("steel_scimitar")
    val steel_med_helm = find("steel_med_helm")

    // Runes
    val waterrune = find("waterrune")
    val earthrune = find("earthrune")

    // Talismans
    val mind_talisman = find("mind_talisman")

    // Herbs (grimy)
    val grimy_guam = find("unidentified_guam")
    val grimy_marrentill = find("unidentified_marentill")
}
