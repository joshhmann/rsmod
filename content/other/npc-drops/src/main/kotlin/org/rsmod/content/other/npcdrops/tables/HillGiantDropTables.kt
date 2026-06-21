package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Hill Giant NPCs.
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source (rev 233)
 * - Existing manually-written table preserved
 *
 * Edgeville batch (Level 5 operational mode).
 * Hill Giant — level 28, found in Edgeville Dungeon, Wilderness, Giants' Plateau.
 *
 * Enriched June 2026: added herb table (10 types), removed post-2013
 * items from gem table (key halves) and tertiary table (clue scrolls).
 */
internal object HillGiantDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerHillGiant(registry)
    }

    private val table = dropTable {
        always(objs.big_bones)

        // Pre-roll: Giant key (1/128 for Obor boss)
        table("Pre-roll", weight = 1) {
            nothing(weight = 127)
            item(HillGiantObjs.giant_key, weight = 1)
        }

        // Weapons and Armour
        table("Weapons and Armour", weight = 16) {
            nothing(weight = 1)
            item(objs.iron_dagger, weight = 4)
            item(objs.iron_med_helm, weight = 5)
            item(HillGiantObjs.iron_full_helm, weight = 5)
            item(objs.iron_kiteshield, weight = 3)
            item(HillGiantObjs.steel_longsword, weight = 2)
        }

        // Runes and Ammunition
        table("Runes and Ammunition", weight = 24) {
            nothing(weight = 1)
            item(objs.iron_arrow, quantity = 3, weight = 6)
            item(objs.firerune, quantity = 15, weight = 3)
            item(objs.waterrune, quantity = 7, weight = 3)
            item(objs.lawrune, quantity = 2, weight = 3)
            item(objs.steel_arrow, quantity = 10, weight = 2)
            item(objs.mindrune, quantity = 3, weight = 2)
            item(objs.cosmicrune, quantity = 2, weight = 2)
            item(objs.naturerune, quantity = 6, weight = 2)
            item(objs.chaosrune, quantity = 2, weight = 1)
            item(objs.deathrune, quantity = 2, weight = 1)
        }

        // Coins
        table("Coins", weight = 65) {
            item(objs.coins, quantity = 5, weight = 18)
            item(objs.coins, quantity = 38, weight = 14)
            item(objs.coins, quantity = 52, weight = 10)
            item(objs.coins, quantity = 15, weight = 8)
            item(objs.coins, quantity = 10, weight = 7)
            item(objs.coins, quantity = 8, weight = 6)
            item(objs.coins, quantity = 88, weight = 2)
        }

        // Other drops
        table("Other", weight = 20) {
            nothing(weight = 1)
            item(HillGiantObjs.limpwurt_root, weight = 11)
            item(HillGiantObjs.beer, weight = 6)
            item(HillGiantObjs.body_talisman, weight = 2)
        }

        // Herbs
        table("Herbs", weight = 10) {
            item(HillGiantObjs.guam_leaf, weight = 10)
            item(HillGiantObjs.tarromin, weight = 8)
            item(HillGiantObjs.harralander, weight = 6)
            item(HillGiantObjs.ranarr_weed, weight = 4)
            item(HillGiantObjs.irit_leaf, weight = 3)
            item(HillGiantObjs.avantoe, weight = 2)
            item(HillGiantObjs.kwuarm, weight = 2)
            item(HillGiantObjs.cadantine, weight = 1)
            item(HillGiantObjs.lantadyme, weight = 1)
            item(HillGiantObjs.dwarf_weed, weight = 1)
        }

        // Gem Drop Table
        table("Gem Drop Table", weight = 3) {
            nothing(weight = 1)
            item(objs.uncut_sapphire, weight = 32)
            item(objs.uncut_emerald, weight = 16)
            item(objs.uncut_ruby, weight = 8)
            item(HillGiantObjs.chaos_talisman, weight = 3)
            item(HillGiantObjs.nature_talisman, weight = 3)
            item(objs.uncut_diamond, weight = 2)
            item(HillGiantObjs.rune_javelin, quantity = 5, weight = 1)
        }
    }

    private fun registerHillGiant(registry: NpcDropTableRegistry) {
        registry.register(HillGiantNpcs.giant, table)
        registry.register(HillGiantNpcs.giant2, table)
        registry.register(HillGiantNpcs.giant3, table)
        registry.register(HillGiantNpcs.giant4, table)
        registry.register(HillGiantNpcs.giant5, table)
        registry.register(HillGiantNpcs.giant6, table)
        registry.register(HillGiantNpcs.wilderness_hill_giant, table)
        registry.register(HillGiantNpcs.wilderness_hill_giant2, table)
        registry.register(HillGiantNpcs.wilderness_hill_giant3, table)
    }
}

internal object HillGiantNpcs : NpcReferences() {
    val giant = find("giant")
    val giant2 = find("giant2")
    val giant3 = find("giant3")
    val giant4 = find("giant4")
    val giant5 = find("giant5")
    val giant6 = find("giant6")
    val wilderness_hill_giant = find("wilderness_hill_giant")
    val wilderness_hill_giant2 = find("wilderness_hill_giant2")
    val wilderness_hill_giant3 = find("wilderness_hill_giant3")
}

internal object HillGiantObjs : ObjReferences() {
    val iron_full_helm = find("iron_full_helm")
    val steel_longsword = find("steel_longsword")
    val limpwurt_root = find("limpwurt_root")
    val beer = find("beer")
    val body_talisman = find("body_talisman")
    val chaos_talisman = find("chaos_talisman")
    val nature_talisman = find("nature_talisman")
    val rune_javelin = find("rune_javelin")
    val giant_key = find("hillgiant_boss_key")
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
