package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/** Drop table registrations for Dwarf Source: Kronos-184 drops/eco/Dwarf.json Cache ID: 290 */
internal object DwarfDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerDwarf(registry)
    }

    private fun registerDwarf(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Ores and Bars
            table("Ores and Bars", weight = 1) {
                item(objs.bronze_pickaxe, weight = 6)
                item(objs.tin_ore, weight = 6)
                item(DwarfObjs.bronze_bar, weight = 3)
                item(objs.copper_ore, weight = 3)
                item(objs.iron_ore, weight = 3)
                item(objs.coal, weight = 1)
                item(DwarfObjs.iron_bar, weight = 1)
            }

            // Runes
            table("Runes", weight = 1) {
                item(DwarfObjs.chaos_rune, quantity = 2, weight = 1)
                item(DwarfObjs.nature_rune, quantity = 2, weight = 1)
            }

            // Weapons/Armour
            table("Weapons", weight = 1) {
                item(objs.bronze_med_helm, weight = 4)
                item(DwarfObjs.bronze_battleaxe, weight = 4)
                item(objs.iron_battleaxe, weight = 4)
                item(DwarfObjs.bronze_bolts, quantity = 2, weight = 1)
                item(DwarfObjs.bronze_bolts, quantity = 11, weight = 1)
            }

            // Other
            table("Other", weight = 1) { item(objs.coins, quantity = 4, weight = 3) }
        }

        registry.register(DwarfNpcs.dwarf, table)
    }
}

internal object DwarfNpcs : NpcReferences() {
    val dwarf = find("dwarf")
}

internal object DwarfObjs : ObjReferences() {
    val bronze_bar = find("bronze_bar")
    val iron_bar = find("iron_bar")
    val bronze_battleaxe = find("bronze_battleaxe")
    val bronze_bolts = find("bronze_bolts")
    val chaos_rune = find("chaosrune")
    val nature_rune = find("naturerune")
}
