package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Roving Moss Giant Source: Kronos-184 drops/eco/Moss_giant.json Cache
 * ID: 891
 */
internal object RovingMossgiantDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerRovingMossgiant(registry)
    }

    private fun registerRovingMossgiant(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.big_bones)

            // Equipment
            table("Equipment", weight = 1) {
                item(RovingMossgiantObjs.black_sq_shield, weight = 9)
                item(objs.steel_med_helm, weight = 6)
                // item(objs.magic_staff, weight = 6)  // TODO: Verify symbol name
                // item(objs.mithril_sword, weight = 6)  // TODO: Verify symbol name
                item(objs.iron_arrow, quantity = 15, weight = 3)
                item(objs.steel_arrow, quantity = 30, weight = 3)
                item(objs.steel_kiteshield, weight = 1)
                // item(objs.mithril_spear, weight = 1)  // TODO: Verify symbol name
            }

            // Runes/Talismans
            table("Runes", weight = 1) {
                item(objs.airrune, quantity = 18, weight = 6)
                item(objs.earthrune, quantity = 27, weight = 6)
                item(objs.naturerune, quantity = 6, weight = 3)
            }

            // Coins
            table("Other", weight = 2) { item(objs.coins, quantity = 5..50, weight = 10) }
        }

        registry.register(RovingMossgiantNpcs.roving_mossgiant, table)
    }
}

internal object RovingMossgiantNpcs : NpcReferences() {
    val roving_mossgiant = find("roving_mossgiant")
}

internal object RovingMossgiantObjs : ObjReferences() {
    val black_sq_shield = find("black_sq_shield")
}
