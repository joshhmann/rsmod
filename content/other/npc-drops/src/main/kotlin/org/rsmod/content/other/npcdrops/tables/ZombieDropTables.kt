package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/** Drop table registrations for Zombie Source: Kronos-184 drops/eco/Zombie.json Cache ID: 26 */
internal object ZombieDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerZombie(registry)
    }

    private fun registerZombie(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Runes/Ammunition (weight scale: 128)
            table("Runes", weight = 10) {
                item(objs.bodyrune, quantity = 3, weight = 3)
                item(objs.chaosrune, quantity = 4, weight = 1)
                item(objs.airrune, quantity = 3, weight = 1)
                item(objs.firerune, quantity = 7, weight = 1)
                item(objs.mindrune, quantity = 5..7, weight = 1)
                item(objs.naturerune, quantity = 6, weight = 1)
                item(objs.naturerune, quantity = 12, weight = 1)
                item(objs.lawrune, quantity = 2, weight = 1)
                item(objs.cosmicrune, quantity = 2, weight = 1)
                item(objs.iron_arrow, quantity = 5, weight = 1)
                item(objs.iron_arrow, quantity = 8, weight = 1)
                item(objs.steel_arrow, quantity = 3..28, weight = 3)
                item(objs.mithril_arrow, quantity = 1, weight = 1)
                item(objs.mithril_arrow, quantity = 2, weight = 1)
            }

            // Weapons/Armour
            table("Weapons", weight = 5) {
                item(objs.bronze_axe, weight = 1)
                // item(objs.bronze_longsword, weight = 1)  // TODO: Verify symbol name
                // TODO: Add more weapons from Kronos data
            }

            // Herbs (empty for low-level zombies)
            table("Herbs", weight = 2) {
                // Low drop rate for F2P zombies
            }

            // Other
            table("Other", weight = 3) { item(objs.coins, quantity = 1..10, weight = 5) }
        }

        registry.register(ZombieNpcs.zombie, table)
    }
}

internal object ZombieNpcs : NpcReferences() {
    val zombie = find("zombie")
}

internal object ZombieObjs : ObjReferences() {
    // Item references use BaseObjs where available
}
