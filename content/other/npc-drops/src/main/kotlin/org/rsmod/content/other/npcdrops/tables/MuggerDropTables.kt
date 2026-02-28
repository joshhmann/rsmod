package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/** Drop table registrations for Mugger Source: Kronos-184 drops/eco/Mugger.json Cache ID: 513 */
internal object MuggerDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerMugger(registry)
    }

    private fun registerMugger(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Runes
            table("Runes", weight = 1) {
                item(MuggerObjs.mind_rune, quantity = 9, weight = 2)
                item(MuggerObjs.earth_rune, quantity = 5, weight = 1)
                item(MuggerObjs.water_rune, quantity = 6, weight = 1)
            }

            // Weapons/Armour
            table("Weapons", weight = 1) {
                item(MuggerObjs.bronze_bolts, quantity = 3..11, weight = 3)
                item(objs.bronze_med_helm, weight = 1)
            }

            // Herbs (P2P)
            table("Herbs", weight = 1) {
                // Members-only drops
            }
        }

        registry.register(MuggerNpcs.mugger, table)
    }
}

internal object MuggerNpcs : NpcReferences() {
    val mugger = find("mugger")
}

internal object MuggerObjs : ObjReferences() {
    val bronze_bolts = find("bolts")
    val mind_rune = find("mindrune")
    val earth_rune = find("earthrune")
    val water_rune = find("waterrune")
}
