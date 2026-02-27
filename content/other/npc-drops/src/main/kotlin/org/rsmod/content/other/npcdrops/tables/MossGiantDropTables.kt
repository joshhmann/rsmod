package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Moss Giant NPCs.
 *
 * Drop table source: https://oldschool.runescape.wiki/w/Moss_giant
 * - Found in Crandor (F2P), Varrock Sewers (F2P), Wilderness (F2P)
 * - Found in Brimhaven Dungeon (P2P), Catacombs of Kourend (P2P), Giants' Den (P2P)
 * - Combat level 42, 60 HP
 * - Slayer category: Moss Giants
 *
 * Drop structure:
 * - Always: Big bones (100%)
 * - Weapons/Armour: Black sq shield, Magic staff, Steel/Mithril equipment
 * - Runes: Law, Air, Earth, Chaos, Nature, Cosmic, Death, Blood
 * - Ammunition: Iron/Steel arrows
 * - Herbs: Full herb drop table (5/128 chance)
 * - Seeds: Uncommon seed drop table (35/128 chance)
 * - Coins: Various amounts (5-300)
 * - Other: Steel bar, Coal, Spinach roll
 * - Gem Drop Table: 4/128 chance
 * - Tertiary: Mossy key, Clue scroll, Ensouled giant head, Champion scroll, etc.
 */
internal object MossGiantDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerMossGiant(registry)
    }

    // -----------------------------------------------------------------------
    // Moss Giant (Level 42)
    // Drop table source: https://oldschool.runescape.wiki/w/Moss_giant
    // Always: Big bones
    // Main drops: Weapons, Armour, Runes, Coins, Herbs, Seeds
    // Tertiary: Mossy key, Clue scrolls, Ensouled head, Champion scroll
    // -----------------------------------------------------------------------
    private fun registerMossGiant(registry: NpcDropTableRegistry) {
        val mossGiantTable = dropTable {
            always(objs.big_bones)

            // Weapons and Armour (total weight: 13/128)
            table("Weapons and Armour", weight = 13) {
                nothing(weight = 1) // Adjusted for exact rates
                item(MossGiantObjs.black_sq_shield, weight = 5)
                item(MossGiantObjs.magic_staff, weight = 2)
                item(objs.steel_med_helm, weight = 2)
                item(MossGiantObjs.mithril_sword, weight = 2)
                item(MossGiantObjs.mithril_spear, weight = 1)
                item(objs.steel_kiteshield, weight = 1)
            }

            // Runes and Ammunition (total weight: 20/128)
            table("Runes and Ammunition", weight = 20) {
                nothing(weight = 1) // Adjusted for exact rates
                item(objs.lawrune, quantity = 3, weight = 4)
                item(objs.airrune, quantity = 18, weight = 3)
                item(objs.earthrune, quantity = 27, weight = 3)
                item(objs.chaosrune, quantity = 7, weight = 3)
                item(objs.naturerune, quantity = 6, weight = 3)
                item(objs.cosmicrune, quantity = 3, weight = 2)
                item(objs.iron_arrow, quantity = 15, weight = 2)
                item(objs.steel_arrow, quantity = 30, weight = 1)
                item(objs.deathrune, quantity = 3, weight = 1)
                item(objs.bloodrune, quantity = 1, weight = 1)
            }

            // Coins (total weight: 82/128 across all coin entries)
            table("Coins", weight = 82) {
                item(objs.coins, quantity = 5, weight = 35)
                item(objs.coins, quantity = 37, weight = 19)
                item(objs.coins, quantity = 2, weight = 11)
                item(objs.coins, quantity = 119, weight = 10)
                item(objs.coins, quantity = 10, weight = 5)
                item(objs.coins, quantity = 300, weight = 2)
            }

            // Herbs (5/128 base chance - implemented as sub-table)
            table("Herbs", weight = 5) {
                nothing(weight = 0) // Always rolls a herb when this table is selected
                item(MossGiantObjs.grimy_guam, weight = 32)
                item(MossGiantObjs.grimy_marrentill, weight = 24)
                item(MossGiantObjs.grimy_tarromin, weight = 18)
                item(MossGiantObjs.grimy_harralander, weight = 14)
                item(MossGiantObjs.grimy_ranarr, weight = 11)
                item(MossGiantObjs.grimy_irit, weight = 8)
                item(MossGiantObjs.grimy_avantoe, weight = 6)
                item(MossGiantObjs.grimy_kwuarm, weight = 5)
                item(MossGiantObjs.grimy_cadantine, weight = 4)
                item(MossGiantObjs.grimy_lantadyme, weight = 3)
                item(MossGiantObjs.grimy_dwarf_weed, weight = 3)
            }

            // Seeds (35/128 base chance - implemented as sub-table)
            table("Seeds", weight = 35) {
                nothing(weight = 0) // Always rolls a seed when this table is selected
                item(MossGiantObjs.limpwurt_seed, weight = 137)
                item(MossGiantObjs.strawberry_seed, weight = 131)
                item(MossGiantObjs.marrentill_seed, weight = 125)
                item(MossGiantObjs.jangerberry_seed, weight = 92)
                item(MossGiantObjs.tarromin_seed, weight = 85)
                item(MossGiantObjs.wildblood_seed, weight = 83)
                item(MossGiantObjs.watermelon_seed, weight = 63)
                item(MossGiantObjs.harralander_seed, weight = 56)
                item(MossGiantObjs.snape_grass_seed, weight = 40)
                item(MossGiantObjs.ranarr_seed, weight = 39)
                item(MossGiantObjs.whiteberry_seed, weight = 34)
                item(MossGiantObjs.mushroom_spore, weight = 29)
                item(MossGiantObjs.toadflax_seed, weight = 27)
                item(MossGiantObjs.belladonna_seed, weight = 18)
                item(MossGiantObjs.irit_seed, weight = 18)
                item(MossGiantObjs.poison_ivy_seed, weight = 13)
                item(MossGiantObjs.avantoe_seed, weight = 12)
                item(MossGiantObjs.cactus_seed, weight = 12)
                item(MossGiantObjs.kwuarm_seed, weight = 9)
                item(MossGiantObjs.potato_cactus_seed, weight = 8)
                item(MossGiantObjs.snapdragon_seed, weight = 5)
                item(MossGiantObjs.cadantine_seed, weight = 4)
                item(MossGiantObjs.lantadyme_seed, weight = 3)
                item(MossGiantObjs.dwarf_weed_seed, weight = 2)
                item(MossGiantObjs.torstol_seed, weight = 1)
            }

            // Other drops (total weight: 8/128)
            table("Other", weight = 8) {
                nothing(weight = 1) // Adjusted for exact rates
                item(objs.steel_bar, weight = 6)
                item(objs.coal, weight = 1)
                item(MossGiantObjs.spinach_roll, weight = 1)
            }

            // Gem Drop Table (4/128 chance)
            table("Gem Drop Table", weight = 4) {
                nothing(weight = 1) // 1/64 chance for nothing in gem table
                item(objs.uncut_sapphire, weight = 32)
                item(objs.uncut_emerald, weight = 16)
                item(objs.uncut_ruby, weight = 8)
                item(MossGiantObjs.chaos_talisman, weight = 3)
                item(MossGiantObjs.nature_talisman, weight = 3)
                item(objs.uncut_diamond, weight = 2)
                item(MossGiantObjs.rune_javelin, quantity = 5, weight = 1)
                item(MossGiantObjs.loop_half_key, weight = 1)
                item(MossGiantObjs.tooth_half_key, weight = 1)
                // Rune spear, Shield left half, Dragon spear require Legends' Quest
            }

            // Tertiary drops (roll independently)
            // Clue scrolls: 1/64 beginner, 1/64 easy (2/64 total = 1/32 for any clue)
            table("Tertiary", weight = 1) {
                nothing(weight = 61) // 61/64 chance of nothing
                item(DropTableObjs.trail_clue_beginner, weight = 1) // 1/64 beginner
                item(DropTableObjs.trail_clue_easy_simple001, weight = 1) // 1/64 easy
                item(MossGiantObjs.mossy_key, weight = 1) // 1/64 mossy key
            }
        }

        // Register for all moss giant variants
        registry.register(MossGiantNpcs.mossgiant, mossGiantTable)
        registry.register(MossGiantNpcs.mossgiant2, mossGiantTable)
        registry.register(MossGiantNpcs.mossgiant3, mossGiantTable)
        registry.register(MossGiantNpcs.mossgiant4, mossGiantTable)
        registry.register(MossGiantNpcs.kourend_mossgiant, mossGiantTable)
    }
}

/** NPC type references for Moss Giant variants. */
internal object MossGiantNpcs : NpcReferences() {
    // Standard moss giants (found in Crandor, Varrock Sewers, Wilderness)
    val mossgiant = find("mossgiant")
    val mossgiant2 = find("mossgiant2")
    val mossgiant3 = find("mossgiant3")
    val mossgiant4 = find("mossgiant4")

    // Kourend moss giant (found in Catacombs of Kourend, Giants' Den)
    val kourend_mossgiant = find("kourend_mossgiant")
}

/** Object type references for Moss Giant drops not in BaseObjs. */
internal object MossGiantObjs : ObjReferences() {
    // Weapons and Armour
    val black_sq_shield = find("black_sq_shield")
    val magic_staff = find("magic_staff")
    val mithril_sword = find("mithril_sword")
    val mithril_spear = find("mithril_spear")

    // Grimy herbs
    val grimy_guam = find("unidentified_guam")
    val grimy_marrentill = find("unidentified_marentill")
    val grimy_tarromin = find("unidentified_tarromin")
    val grimy_harralander = find("unidentified_harralander")
    val grimy_ranarr = find("unidentified_ranarr")
    val grimy_irit = find("unidentified_irit")
    val grimy_avantoe = find("unidentified_avantoe")
    val grimy_kwuarm = find("unidentified_kwuarm")
    val grimy_cadantine = find("unidentified_cadantine")
    val grimy_lantadyme = find("unidentified_lantadyme")
    val grimy_dwarf_weed = find("unidentified_dwarf_weed")

    // Seeds
    val limpwurt_seed = find("limpwurt_seed")
    val strawberry_seed = find("strawberry_seed")
    val marrentill_seed = find("marrentill_seed")
    val jangerberry_seed = find("jangerberry_seed")
    val tarromin_seed = find("tarromin_seed")
    val wildblood_seed = find("wildblood_seed")
    val watermelon_seed = find("watermelon_seed")
    val harralander_seed = find("harralander_seed")
    val snape_grass_seed = find("snape_grass_seed")
    val ranarr_seed = find("ranarr_seed")
    val whiteberry_seed = find("whiteberry_seed")
    val mushroom_spore = find("mushroom_spore")
    val toadflax_seed = find("toadflax_seed")
    val belladonna_seed = find("belladonna_seed")
    val irit_seed = find("irit_seed")
    val poison_ivy_seed = find("poison_ivy_seed")
    val avantoe_seed = find("avantoe_seed")
    val cactus_seed = find("cactus_seed")
    val kwuarm_seed = find("kwuarm_seed")
    val potato_cactus_seed = find("potato_cactus_seed")
    val snapdragon_seed = find("snapdragon_seed")
    val cadantine_seed = find("cadantine_seed")
    val lantadyme_seed = find("lantadyme_seed")
    val dwarf_weed_seed = find("dwarf_weed_seed")
    val torstol_seed = find("torstol_seed")

    // Other
    val spinach_roll = find("spinach_roll")
    val chaos_talisman = find("chaos_talisman")
    val nature_talisman = find("nature_talisman")
    val loop_half_key = find("loop_half_key")
    val tooth_half_key = find("tooth_half_key")

    // Tertiary
    val mossy_key = find("mossy_key")

    // Gem table items not in BaseObjs
    val rune_javelin = find("rune_javelin")
}
