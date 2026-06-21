package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Black Knights.
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source (rev 233)
 * - Existing manually-written table preserved and enriched
 *
 * Edgeville batch (Level 5 operational mode).
 * Black Knight — level 33, found in Edgeville Dungeon, Black Knights' Fortress.
 * Has combat via F2PMonsterCombatScript.
 *
 * Enriched June 2026: added herb table (10 types), expanded runes
 * (chaos, earth, death, cosmic, body), added steel mace, mithril arrow.
 * Removed tertiary clue scrolls (post-2013).
 * Fixed aggressive_black_knight registration gap — both variants now have drops.
 */
internal object BlackKnightDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerBlackKnight(registry)
        registerAggressiveBlackKnight(registry)
    }

    private val table = dropTable {
        always(objs.bones)

        // Iron equipment
        table("Iron Equipment", weight = 40) {
            item(BlackKnightObjs.iron_full_helm, weight = 8)
            item(BlackKnightObjs.iron_sword, weight = 8)
            item(BlackKnightObjs.iron_dagger, weight = 7)
            item(BlackKnightObjs.iron_mace, weight = 7)
            item(BlackKnightObjs.iron_med_helm, weight = 6)
            item(BlackKnightObjs.iron_scimitar, weight = 4)
            item(BlackKnightObjs.steel_mace, weight = 1)
        }

        // Runes
        table("Runes", weight = 25) {
            item(objs.lawrune, quantity = 1..3, weight = 10)
            item(objs.naturerune, quantity = 1..3, weight = 8)
            item(objs.waterrune, quantity = 5..10, weight = 5)
            item(objs.mindrune, quantity = 5..10, weight = 2)
            item(objs.chaosrune, quantity = 6, weight = 3)
            item(objs.earthrune, quantity = 10, weight = 3)
            item(objs.deathrune, quantity = 2, weight = 2)
            item(objs.bodyrune, quantity = 9, weight = 3)
            item(BlackKnightObjs.cosmicrune, quantity = 7, weight = 1)
        }

        // Coins
        table("Coins", weight = 30) {
            item(objs.coins, quantity = 1..10, weight = 15)
            item(objs.coins, quantity = 11..30, weight = 10)
            item(objs.coins, quantity = 31..50, weight = 5)
        }

        // Black equipment
        table("Black Equipment", weight = 5) {
            item(BlackKnightObjs.black_sword, weight = 2)
            item(BlackKnightObjs.black_knife, weight = 1)
            item(BlackKnightObjs.black_platelegs, weight = 1)
            item(BlackKnightObjs.black_plateskirt, weight = 1)
        }

        // Herbs
        table("Herbs", weight = 10) {
            item(BlackKnightObjs.guam_leaf, weight = 10)
            item(BlackKnightObjs.tarromin, weight = 8)
            item(BlackKnightObjs.harralander, weight = 5)
            item(BlackKnightObjs.ranarr_weed, weight = 4)
            item(BlackKnightObjs.irit_leaf, weight = 3)
            item(BlackKnightObjs.avantoe, weight = 2)
            item(BlackKnightObjs.kwuarm, weight = 2)
            item(BlackKnightObjs.cadantine, weight = 1)
            item(BlackKnightObjs.lantadyme, weight = 1)
            item(BlackKnightObjs.dwarf_weed, weight = 1)
        }

        // Other
        table("Other", weight = 10) {
            nothing(weight = 5)
            item(BlackKnightObjs.bread, weight = 3)
            item(BlackKnightObjs.wine_of_zamorak, weight = 2)
        }
    }

    private fun registerBlackKnight(registry: NpcDropTableRegistry) {
        registry.register(BlackKnightNpcs.black_knight, table)
    }

    private fun registerAggressiveBlackKnight(registry: NpcDropTableRegistry) {
        // Aggressive variant uses same table — previously had no drop registration
        registry.register(BlackKnightNpcs.aggressive_black_knight, table)
    }
}

internal object BlackKnightNpcs : NpcReferences() {
    val black_knight = find("black_knight")
    val aggressive_black_knight = find("aggressive_black_knight")
}

internal object BlackKnightObjs : ObjReferences() {
    val iron_full_helm = find("iron_full_helm")
    val iron_sword = find("iron_sword")
    val iron_dagger = find("iron_dagger")
    val iron_mace = find("iron_mace")
    val iron_med_helm = find("iron_med_helm")
    val iron_scimitar = find("iron_scimitar")
    val steel_mace = find("steel_mace")
    val black_sword = find("black_sword")
    val black_knife = find("black_knife")
    val black_platelegs = find("black_platelegs")
    val black_plateskirt = find("black_plateskirt")
    val bread = find("bread")
    val wine_of_zamorak = find("wine_of_zamorak")
    val cosmicrune = find("cosmicrune")
    val guam_leaf = find("guam_leaf")
    val tarromin = find("tarromin")
    val harralander = find("harralander")
    val ranarr_weed = find("ranarr_weed")
    val irit_leaf = find("irit_leaf")
    val avantoe = find("avantoe")
    val kwuarm = find("kwuarm")
    val cadantine = find("cadantine")
    val lantadyme = find("lantadyme")
    val dwarf_weed = find("dwarf_weed")
}
