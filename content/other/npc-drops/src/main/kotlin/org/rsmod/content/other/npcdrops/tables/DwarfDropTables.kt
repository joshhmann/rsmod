package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.npc.NpcType

/**
 * Drop table registrations for Dwarf.
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 * - Existing Kronos-based handler preserved and enriched
 *
 * Enriched from corpus June 2026: added hammer, copper ore, expanded coin entries,
 * chaos talisman, nature talisman, gem/RDT table.
 * Expanded NPC registration to include dwarf_chaos and dwarf_mountain variants.
 * Skipped: bronze bolts (not in rev 233 cache), clue scrolls (post-2013),
 * key halves, shield left half (post-2013).
 */
internal object DwarfDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerDwarf(registry)
    }

    private fun registerDwarf(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Ores and Bars
            table("Ores and Bars", weight = 1) {
                item(objs.bronze_pickaxe, weight = 13)
                item(objs.tin_ore, weight = 3)
                item(DwarfObjs.bronze_bar, weight = 7)
                item(objs.copper_ore, weight = 3)
                item(objs.iron_ore, weight = 4)
                item(objs.coal, weight = 2)
                item(DwarfObjs.iron_bar, weight = 3)
                item(objs.hammer, weight = 10)
            }

            // Runes
            table("Runes", weight = 1) {
                item(objs.chaosrune, quantity = 2, weight = 4)
                item(objs.naturerune, quantity = 2, weight = 4)
            }

            // Weapons/Armour
            table("Weapons", weight = 1) {
                item(objs.bronze_med_helm, weight = 4)
                item(DwarfObjs.bronze_battleaxe, weight = 2)
                item(objs.iron_battleaxe, weight = 1)
            }

            // Coins and Other
            table("Other", weight = 1) {
                item(objs.coins, quantity = 4, weight = 20)
                item(objs.coins, quantity = 10, weight = 15)
                item(objs.coins, quantity = 30, weight = 2)
            }

            // Gem / Rare Drop Table
            table("Gem/RDT", weight = 1) {
                item(objs.uncut_sapphire, weight = 4)
                item(objs.uncut_emerald, weight = 2)
                item(objs.uncut_ruby, weight = 1)
                item(DwarfObjs.chaos_talisman, weight = 1)
                item(DwarfObjs.nature_talisman, weight = 1)
                item(objs.uncut_diamond, weight = 1)
                item(objs.rune_javelin, quantity = 5, weight = 1)
            }
        }

        val dwarfNpcs: List<NpcType> =
            listOf(
                DwarfNpcs.dwarf_normal,
                DwarfNpcs.dwarf_chaos,
                DwarfNpcs.dwarf_mountain,
            )
        registry.register(dwarfNpcs.distinct(), table)
    }
}

internal object DwarfNpcs : NpcReferences() {
    val dwarf_normal = find("dwarf_normal")
    val dwarf_chaos = find("dwarf_chaos")
    val dwarf_mountain = find("dwarf_mountain")
}

internal object DwarfObjs : ObjReferences() {
    val bronze_bar = find("bronze_bar")
    val iron_bar = find("iron_bar")
    val bronze_battleaxe = find("bronze_battleaxe")
    val chaos_talisman = find("chaos_talisman")
    val nature_talisman = find("nature_talisman")
}
