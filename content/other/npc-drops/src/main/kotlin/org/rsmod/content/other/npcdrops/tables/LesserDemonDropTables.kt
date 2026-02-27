package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Lesser Demon NPCs.
 *
 * Drop table source: https://oldschool.runescape.wiki/w/Lesser_demon
 * - Found in Crandor (F2P), Crandor and Karamja Dungeon (F2P), Wilderness (F2P), Wizards' Tower
 *   (F2P)
 * - Found in Taverley Dungeon (P2P), Chasm of Fire (P2P), Catacombs of Kourend (P2P)
 * - Combat level 82, 79 HP
 * - Slayer category: Lesser Demons
 *
 * Drop structure:
 * - Always: Vile ashes (100%)
 * - Weapons/Armour: Steel full helm, Steel axe, Steel scimitar, Mithril sq shield, Mithril
 *   chainbody, Rune med helm
 * - Runes: Fire runes (60), Chaos runes (12), Death runes (3), Fire runes (30)
 * - Coins: Various amounts (10-450)
 * - Other: Jug of wine, Gold ore
 * - Gem Drop Table: 4/128 chance
 * - Tertiary: Ensouled demon head, Champion scroll, Ancient shards (Catacombs), Dark totem pieces
 *   (Catacombs)
 */
internal object LesserDemonDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerLesserDemon(registry)
    }

    // -----------------------------------------------------------------------
    // Lesser Demon (Level 82)
    // Drop table source: https://oldschool.runescape.wiki/w/Lesser_demon
    // Always: Vile ashes
    // Main drops: Weapons, Armour, Runes, Coins
    // Tertiary: Ensouled demon head, Champion scroll
    // -----------------------------------------------------------------------
    private fun registerLesserDemon(registry: NpcDropTableRegistry) {
        val lesserDemonTable = dropTable {
            always(LesserDemonObjs.vile_ashes)

            // Weapons and Armour (total weight: 14/128)
            table("Weapons and Armour", weight = 14) {
                nothing(weight = 1) // Adjusted for exact rates
                item(objs.steel_full_helm, weight = 4)
                item(objs.steel_axe, weight = 4)
                item(objs.steel_scimitar, weight = 3)
                item(objs.mithril_sq_shield, weight = 1)
                item(objs.mithril_chainbody, weight = 1)
                item(objs.rune_med_helm, weight = 1)
            }

            // Runes (total weight: 17/128)
            table("Runes", weight = 17) {
                nothing(weight = 1) // Adjusted for exact rates
                item(objs.firerune, quantity = 60, weight = 8)
                item(objs.chaosrune, quantity = 12, weight = 5)
                item(objs.deathrune, quantity = 3, weight = 3)
                item(objs.firerune, quantity = 30, weight = 1)
            }

            // Coins (total weight: 87/128)
            table("Coins", weight = 87) {
                item(objs.coins, quantity = 120, weight = 40)
                item(objs.coins, quantity = 40, weight = 29)
                item(objs.coins, quantity = 200, weight = 10)
                item(objs.coins, quantity = 10, weight = 7)
                item(objs.coins, quantity = 450, weight = 1)
            }

            // Herbs (1/128 base chance - implemented as sub-table)
            table("Herbs", weight = 1) {
                nothing(weight = 0) // Always rolls a herb when this table is selected
                item(LesserDemonObjs.grimy_guam, weight = 32)
                item(LesserDemonObjs.grimy_marrentill, weight = 24)
                item(LesserDemonObjs.grimy_tarromin, weight = 18)
                item(LesserDemonObjs.grimy_harralander, weight = 14)
                item(LesserDemonObjs.grimy_ranarr, weight = 11)
                item(LesserDemonObjs.grimy_irit, weight = 8)
                item(LesserDemonObjs.grimy_avantoe, weight = 6)
                item(LesserDemonObjs.grimy_kwuarm, weight = 5)
                item(LesserDemonObjs.grimy_cadantine, weight = 4)
                item(LesserDemonObjs.grimy_lantadyme, weight = 3)
                item(LesserDemonObjs.grimy_dwarf_weed, weight = 3)
            }

            // Other drops (total weight: 5/128)
            table("Other", weight = 5) {
                nothing(weight = 1) // Adjusted for exact rates
                item(LesserDemonObjs.jug_of_wine, weight = 3)
                item(objs.gold_ore, weight = 2)
            }

            // Gem Drop Table (4/128 chance)
            table("Gem Drop Table", weight = 4) {
                nothing(weight = 1) // 1/64 chance for nothing in gem table
                item(objs.uncut_sapphire, weight = 32)
                item(objs.uncut_emerald, weight = 16)
                item(objs.uncut_ruby, weight = 8)
                item(LesserDemonObjs.chaos_talisman, weight = 3)
                item(LesserDemonObjs.nature_talisman, weight = 3)
                item(objs.uncut_diamond, weight = 2)
                item(LesserDemonObjs.rune_javelin, quantity = 5, weight = 1)
                item(LesserDemonObjs.loop_half_key, weight = 1)
                item(LesserDemonObjs.tooth_half_key, weight = 1)
                // Rune spear, Shield left half, Dragon spear require Legends' Quest
            }

            // Tertiary drops (roll independently)
            table("Tertiary", weight = 1) {
                nothing(weight = 49) // 1/50 for ensouled demon head
                item(LesserDemonObjs.ensouled_demon_head, weight = 1)
            }
        }

        // Register for all lesser demon variants
        registry.register(LesserDemonNpcs.lesser_demon, lesserDemonTable)
        registry.register(LesserDemonNpcs.lesser_demon2, lesserDemonTable)
        registry.register(LesserDemonNpcs.lesser_demon3, lesserDemonTable)
        registry.register(LesserDemonNpcs.lesser_demon4, lesserDemonTable)
        registry.register(LesserDemonNpcs.lesser_demon5, lesserDemonTable)
        registry.register(LesserDemonNpcs.kourend_lesser_demon, lesserDemonTable)
        registry.register(LesserDemonNpcs.kourend_lesser_demon2, lesserDemonTable)
    }
}

/** NPC type references for Lesser Demon variants. */
internal object LesserDemonNpcs : NpcReferences() {
    // Standard lesser demons (found in Crandor, Crandor and Karamja Dungeon, Wilderness, Wizards'
    // Tower)
    val lesser_demon = find("lesser_demon")
    val lesser_demon2 = find("lesser_demon2")
    val lesser_demon3 = find("lesser_demon3")
    val lesser_demon4 = find("lesser_demon4")
    val lesser_demon5 = find("lesser_demon5")

    // Kourend lesser demons (found in Catacombs of Kourend)
    val kourend_lesser_demon = find("kourend_lesser_demon")
    val kourend_lesser_demon2 = find("kourend_lesser_demon2")
}

/** Object type references for Lesser Demon drops not in BaseObjs. */
internal object LesserDemonObjs : ObjReferences() {
    // Always drop
    val vile_ashes = find("vile_ashes")

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

    // Other
    val jug_of_wine = find("jug_of_wine")
    val chaos_talisman = find("chaos_talisman")
    val nature_talisman = find("nature_talisman")
    val loop_half_key = find("loop_half_key")
    val tooth_half_key = find("tooth_half_key")

    // Tertiary
    val ensouled_demon_head = find("ensouled_demon_head")

    // Gem table items not in BaseObjs
    val rune_javelin = find("rune_javelin")
}
