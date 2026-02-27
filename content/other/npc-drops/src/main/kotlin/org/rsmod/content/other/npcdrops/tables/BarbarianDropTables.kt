package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Barbarian Source: Kronos-184 drops/eco/Barbarian.json Cache ID: 3256
 */
internal object BarbarianDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerBarbarian(registry)
    }

    private fun registerBarbarian(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Weapons
            table("Weapons", weight = 1) {
                item(objs.bronze_arrow, quantity = 15, weight = 6)
                item(objs.bronze_axe, weight = 3)
                item(BarbarianObjs.bronze_battleaxe, weight = 3)
                item(objs.iron_axe, weight = 3)
                item(BarbarianObjs.iron_mace, weight = 3)
                item(BarbarianObjs.plainstaff, weight = 1)
                item(objs.iron_arrow, quantity = 8, weight = 1)
            }

            // Runes
            table("Runes", weight = 1) {
                item(objs.firerune, quantity = 5, weight = 3)
                item(objs.mindrune, quantity = 4, weight = 3)
                item(objs.earthrune, quantity = 2, weight = 3)
                item(objs.chaosrune, quantity = 2, weight = 3)
                item(objs.chaosrune, quantity = 3, weight = 1)
                item(objs.lawrune, quantity = 2, weight = 1)
            }

            // Other
            table("Other", weight = 1) {
                item(objs.coins, quantity = 5, weight = 9)
                item(objs.coins, quantity = 8, weight = 9)
                item(objs.coins, quantity = 27, weight = 9)
                item(objs.tin_ore, weight = 6)
            }
        }

        registry.register(BarbarianNpcs.barbarian, table)
    }
}

internal object BarbarianNpcs : NpcReferences() {
    val barbarian = find("barbarian")
}

internal object BarbarianObjs : ObjReferences() {
    val bronze_battleaxe = find("bronze_battleaxe")
    val iron_mace = find("iron_mace")
    val plainstaff = find("plainstaff")
}
