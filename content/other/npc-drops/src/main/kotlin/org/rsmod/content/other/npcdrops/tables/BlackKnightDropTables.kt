package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Black Knights.
 *
 * Drop table source: https://oldschool.runescape.wiki/w/Black_Knight
 * - Found in Black Knights' Fortress (F2P), Taverley Dungeon, and various locations.
 * - Level 33 combat.
 *
 * Drop structure:
 * - Always: Bones (100%)
 * - Main drops: Iron equipment, Law runes, Nature runes, Coins
 * - Rare: Black equipment
 * - Tertiary: Clue scroll (beginner)
 */
internal object BlackKnightDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerBlackKnight(registry)
        registerAggressiveBlackKnight(registry)
    }

    // -----------------------------------------------------------------------
    // Black Knight
    // Drop table source: https://oldschool.runescape.wiki/w/Black_Knight
    // Always: Bones
    // Main drops: Iron equipment, Runes, Coins
    // Rare: Black equipment
    // Tertiary: Clue scroll (beginner)
    // -----------------------------------------------------------------------
    private fun registerBlackKnight(registry: NpcDropTableRegistry) {
        val blackKnightTable = dropTable {
            always(objs.bones)

            // Iron equipment - common drops
            table("Iron Equipment", weight = 40) {
                item(BlackKnightObjs.iron_full_helm, weight = 8)
                item(BlackKnightObjs.iron_sword, weight = 8)
                item(BlackKnightObjs.iron_dagger, weight = 7)
                item(BlackKnightObjs.iron_mace, weight = 7)
                item(BlackKnightObjs.iron_med_helm, weight = 6)
                item(BlackKnightObjs.iron_scimitar, weight = 4)
            }

            // Runes
            table("Runes", weight = 25) {
                item(BlackKnightObjs.lawrune, quantity = 1..3, weight = 10)
                item(BlackKnightObjs.naturerune, quantity = 1..3, weight = 8)
                item(BlackKnightObjs.waterrune, quantity = 5..10, weight = 5)
                item(objs.mindrune, quantity = 5..10, weight = 2)
            }

            // Coins
            table("Coins", weight = 30) {
                item(objs.coins, quantity = 1..10, weight = 15)
                item(objs.coins, quantity = 11..30, weight = 10)
                item(objs.coins, quantity = 31..50, weight = 5)
            }

            // Black equipment - rare drops
            table("Black Equipment", weight = 5) {
                item(BlackKnightObjs.black_sword, weight = 2)
                item(BlackKnightObjs.black_knife, weight = 1)
                item(BlackKnightObjs.black_platelegs, weight = 1)
                item(BlackKnightObjs.black_plateskirt, weight = 1)
            }

            // Other drops
            table("Other", weight = 20) {
                nothing(weight = 15)
                item(BlackKnightObjs.bread, weight = 3)
                item(BlackKnightObjs.wine_of_zamorak, weight = 2)
            }

            // Tertiary drops - clue scrolls (1/128 each = 2/128 total)
            table("Tertiary", weight = 1) {
                nothing(weight = 126) // 126/128 chance of nothing
                item(DropTableObjs.trail_clue_beginner, weight = 1) // 1/128 beginner
                item(DropTableObjs.trail_clue_easy_simple001, weight = 1) // 1/128 easy
            }
        }

        registry.register(BlackKnightNpcs.black_knight, blackKnightTable)
    }

    // -----------------------------------------------------------------------
    // Aggressive Black Knight
    // Same drops as regular Black Knight
    // -----------------------------------------------------------------------
    private fun registerAggressiveBlackKnight(registry: NpcDropTableRegistry) {
        // Aggressive variant uses same table as regular Black Knight
        // The table is already registered above, no need to duplicate
        // If different drops are needed in future, create separate table here
    }
}

/** NPC type references for Black Knight variants. */
internal object BlackKnightNpcs : NpcReferences() {
    val black_knight = find("black_knight")
    val aggressive_black_knight = find("aggressive_black_knight")
}

/** Object type references for Black Knight drops not in BaseObjs or DropTableObjs. */
internal object BlackKnightObjs : ObjReferences() {
    // Iron equipment
    val iron_full_helm = find("iron_full_helm")
    val iron_sword = find("iron_sword")
    val iron_dagger = find("iron_dagger")
    val iron_mace = find("iron_mace")
    val iron_med_helm = find("iron_med_helm")
    val iron_scimitar = find("iron_scimitar")

    // Runes
    val lawrune = find("lawrune")
    val naturerune = find("naturerune")
    val waterrune = find("waterrune")

    // Black equipment
    val black_sword = find("black_sword")
    val black_knife = find("black_knife")
    val black_platelegs = find("black_platelegs")
    val black_plateskirt = find("black_plateskirt")

    // Other
    val bread = find("bread")
    val wine_of_zamorak = find("wine_of_zamorak")
}
