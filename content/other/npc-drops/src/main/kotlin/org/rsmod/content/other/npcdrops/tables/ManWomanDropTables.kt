package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Man and Woman NPCs.
 *
 * Drop table source: https://oldschool.runescape.wiki/w/Man
 * - Found throughout Gielinor (Lumbridge, Varrock, Falador, etc.)
 * - Level 2/3 combat.
 *
 * Drop structure:
 * - Always: Bones (100%)
 * - Main drops: Coins
 * - Rare: Cabbage, Herbs, Earth talisman
 * - Tertiary: Clue scroll (beginner)
 */
internal object ManWomanDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerMan(registry)
        registerWoman(registry)
    }

    // -----------------------------------------------------------------------
    // Man
    // Drop table source: https://oldschool.runescape.wiki/w/Man
    // Always: Bones
    // Main drops: Coins
    // Rare: Cabbage, Earth talisman, Grimy herbs
    // Tertiary: Clue scroll (beginner)
    // -----------------------------------------------------------------------
    private fun registerMan(registry: NpcDropTableRegistry) {
        val manTable = dropTable {
            always(objs.bones)

            // Coins - most common drop
            table("Coins", weight = 70) {
                item(objs.coins, quantity = 1..5, weight = 40)
                item(objs.coins, quantity = 6..10, weight = 20)
                item(objs.coins, quantity = 11..20, weight = 8)
                item(objs.coins, quantity = 21..50, weight = 2)
            }

            // Rare drops
            table("Rare", weight = 20) {
                nothing(weight = 15)
                item(ManWomanObjs.cabbage, weight = 3)
                item(ManWomanObjs.earth_talisman, weight = 1)
                item(ManWomanObjs.grimy_guam, weight = 1)
            }

            // Tertiary drops - clue scrolls (1/128 each = 2/128 total)
            table("Tertiary", weight = 1) {
                nothing(weight = 126) // 126/128 chance of nothing
                item(DropTableObjs.trail_clue_beginner, weight = 1) // 1/128 beginner
                item(DropTableObjs.trail_clue_easy_simple001, weight = 1) // 1/128 easy
            }
        }

        // Register for all man variants
        registry.register(ManWomanNpcs.man_1, manTable)
        registry.register(ManWomanNpcs.man_2, manTable)
        registry.register(ManWomanNpcs.man_3, manTable)
    }

    // -----------------------------------------------------------------------
    // Woman
    // Same drops as Man
    // -----------------------------------------------------------------------
    private fun registerWoman(registry: NpcDropTableRegistry) {
        val womanTable = dropTable {
            always(objs.bones)

            // Coins - most common drop
            table("Coins", weight = 70) {
                item(objs.coins, quantity = 1..5, weight = 40)
                item(objs.coins, quantity = 6..10, weight = 20)
                item(objs.coins, quantity = 11..20, weight = 8)
                item(objs.coins, quantity = 21..50, weight = 2)
            }

            // Rare drops
            table("Rare", weight = 20) {
                nothing(weight = 15)
                item(ManWomanObjs.cabbage, weight = 3)
                item(ManWomanObjs.earth_talisman, weight = 1)
                item(ManWomanObjs.grimy_guam, weight = 1)
            }

            // Tertiary drops - clue scrolls (1/128 each = 2/128 total)
            table("Tertiary", weight = 1) {
                nothing(weight = 126) // 126/128 chance of nothing
                item(DropTableObjs.trail_clue_beginner, weight = 1) // 1/128 beginner
                item(DropTableObjs.trail_clue_easy_simple001, weight = 1) // 1/128 easy
            }
        }

        // Register for all woman variants
        registry.register(ManWomanNpcs.woman_1, womanTable)
        registry.register(ManWomanNpcs.woman_2, womanTable)
        registry.register(ManWomanNpcs.woman_3, womanTable)
    }
}

/** NPC type references for Man and Woman variants. */
internal object ManWomanNpcs : NpcReferences() {
    // Man variants - found throughout Gielinor
    val man_1 = find("misc_etc_man_1")
    val man_2 = find("misc_etc_man_2")
    val man_3 = find("misc_etc_man_3")

    // Woman variants - found throughout Gielinor
    val woman_1 = find("misc_etc_woman_1")
    val woman_2 = find("misc_etc_woman_2")
    val woman_3 = find("misc_etc_woman_3")
}

/** Object type references for Man/Woman drops not in BaseObjs or DropTableObjs. */
internal object ManWomanObjs : ObjReferences() {
    // Food
    val cabbage = find("cabbage")

    // Herbs (grimy - for low-level NPCs)
    val grimy_guam = find("unidentified_guam")

    // Talismans
    val earth_talisman = find("earth_talisman")
}
