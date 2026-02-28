package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Hill Giant NPCs.
 *
 * Drop table source: https://oldschool.runescape.wiki/w/Hill_giant
 * - Found in Edgeville Dungeon (F2P), Wilderness (F2P), Giants' Plateau (F2P)
 * - Found in Taverley Dungeon (P2P), Catacombs of Kourend (P2P), Giants' Den (P2P)
 * - Combat level 28, 35 HP
 * - Slayer category: Hill Giants
 *
 * Drop structure:
 * - Always: Big bones (100%)
 * - Pre-roll: Giant key (1/128, 2/128 in Wilderness)
 * - Weapons/Armour: Iron dagger, Iron med helm, Iron full helm, Iron kiteshield, Steel longsword
 * - Runes: Fire, Water, Law, Mind, Cosmic, Nature, Chaos, Death
 * - Ammunition: Iron arrows, Steel arrows
 * - Coins: Various amounts (5-88)
 * - Other: Limpwurt root, Beer, Body talisman
 * - Gem Drop Table: 3/128 chance
 * - Tertiary: Clue scroll (beginner), Ensouled giant head, Long bone, Champion scroll
 */
internal object HillGiantDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerHillGiant(registry)
    }

    // -----------------------------------------------------------------------
    // Hill Giant (Level 28)
    // Drop table source: https://oldschool.runescape.wiki/w/Hill_giant
    // Always: Big bones
    // Pre-roll: Giant key (1/128)
    // Main drops: Weapons, Armour, Runes, Coins, Other
    // Tertiary: Clue scrolls, Ensouled head, Long bone, Champion scroll
    // -----------------------------------------------------------------------
    private fun registerHillGiant(registry: NpcDropTableRegistry) {
        val hillGiantTable = dropTable {
            always(objs.big_bones)

            // Pre-roll: Giant key (1/128 chance before main table)
            table("Pre-roll", weight = 1) {
                nothing(weight = 127)
                item(HillGiantObjs.giant_key, weight = 1)
            }

            // Weapons and Armour (total weight: 16/128)
            table("Weapons and Armour", weight = 16) {
                nothing(weight = 1) // Adjusted for exact rates
                item(objs.iron_dagger, weight = 4)
                item(objs.iron_med_helm, weight = 5)
                item(HillGiantObjs.iron_full_helm, weight = 5)
                item(objs.iron_kiteshield, weight = 3)
                item(HillGiantObjs.steel_longsword, weight = 2)
            }

            // Runes and Ammunition (total weight: 24/128)
            table("Runes and Ammunition", weight = 24) {
                nothing(weight = 1) // Adjusted for exact rates
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

            // Coins (total weight: 65/128 for F2P)
            table("Coins", weight = 65) {
                item(objs.coins, quantity = 5, weight = 18)
                item(objs.coins, quantity = 38, weight = 14)
                item(objs.coins, quantity = 52, weight = 10)
                item(objs.coins, quantity = 15, weight = 8)
                item(objs.coins, quantity = 10, weight = 7)
                item(objs.coins, quantity = 8, weight = 6)
                item(objs.coins, quantity = 88, weight = 2)
            }

            // Other drops (total weight: 20/128)
            table("Other", weight = 20) {
                nothing(weight = 1) // Adjusted for exact rates
                item(HillGiantObjs.limpwurt_root, weight = 11)
                item(HillGiantObjs.beer, weight = 6)
                item(HillGiantObjs.body_talisman, weight = 2)
            }

            // Gem Drop Table (3/128 chance)
            table("Gem Drop Table", weight = 3) {
                nothing(weight = 1) // 1/85.49 chance for nothing in gem table
                item(objs.uncut_sapphire, weight = 32)
                item(objs.uncut_emerald, weight = 16)
                item(objs.uncut_ruby, weight = 8)
                item(HillGiantObjs.chaos_talisman, weight = 3)
                item(HillGiantObjs.nature_talisman, weight = 3)
                item(objs.uncut_diamond, weight = 2)
                item(HillGiantObjs.rune_javelin, quantity = 5, weight = 1)
                item(HillGiantObjs.loop_half_key, weight = 1)
                item(HillGiantObjs.tooth_half_key, weight = 1)
            }

            // Tertiary drops (roll independently)
            // Clue scrolls: 1/64 beginner, 1/64 easy (2/64 total = 1/32 for any clue)
            table("Tertiary", weight = 1) {
                nothing(weight = 62) // 62/64 chance of nothing
                item(HillGiantObjs.clue_scroll_beginner, weight = 1) // 1/64 beginner
                item(DropTableObjs.trail_clue_easy_simple001, weight = 1) // 1/64 easy
            }
        }

        // Register for all hill giant variants
        registry.register(HillGiantNpcs.giant, hillGiantTable)
        registry.register(HillGiantNpcs.giant2, hillGiantTable)
        registry.register(HillGiantNpcs.giant3, hillGiantTable)
        registry.register(HillGiantNpcs.giant4, hillGiantTable)
        registry.register(HillGiantNpcs.giant5, hillGiantTable)
        registry.register(HillGiantNpcs.giant6, hillGiantTable)
        registry.register(HillGiantNpcs.wilderness_hill_giant, hillGiantTable)
        registry.register(HillGiantNpcs.wilderness_hill_giant2, hillGiantTable)
        registry.register(HillGiantNpcs.wilderness_hill_giant3, hillGiantTable)
    }
}

/** NPC type references for Hill Giant variants. */
internal object HillGiantNpcs : NpcReferences() {
    // Standard hill giants (found in Edgeville Dungeon, Taverley Dungeon, etc.)
    val giant = find("giant")
    val giant2 = find("giant2")
    val giant3 = find("giant3")
    val giant4 = find("giant4")
    val giant5 = find("giant5")
    val giant6 = find("giant6")

    // Wilderness hill giants
    val wilderness_hill_giant = find("wilderness_hill_giant")
    val wilderness_hill_giant2 = find("wilderness_hill_giant2")
    val wilderness_hill_giant3 = find("wilderness_hill_giant3")
}

/** Object type references for Hill Giant drops not in BaseObjs. */
internal object HillGiantObjs : ObjReferences() {
    // Weapons and Armour
    val iron_full_helm = find("iron_full_helm")
    val steel_longsword = find("steel_longsword")

    // Other
    val limpwurt_root = find("limpwurt_root")
    val beer = find("beer")
    val body_talisman = find("body_talisman")

    // Gem table items
    val chaos_talisman = find("chaos_talisman")
    val nature_talisman = find("nature_talisman")
    val rune_javelin = find("rune_javelin")
    val loop_half_key = find("keyhalf1")
    val tooth_half_key = find("keyhalf2")

    // Tertiary
    val giant_key = find("hillgiant_boss_key")
    val clue_scroll_beginner = find("trail_clue_beginner")
}
