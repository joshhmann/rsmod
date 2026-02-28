package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Bear NPCs.
 *
 * Drop table source: https://oldschool.runescape.wiki/w/Bear
 * - Combat level 21, 27 HP
 * - Found in Varrock south, east of Dark Warriors' Fortress
 *
 * Drop structure:
 * - Always: Bones, Bear fur, Raw bear meat
 * - Main drops: Coins (common)
 */
internal object BearDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerBear(registry)
    }

    private fun registerBear(registry: NpcDropTableRegistry) {
        val bearTable = dropTable {
            always(objs.bones)
            always(BearObjs.bear_fur)
            always(BearObjs.raw_bear_meat)

            // Coins (common drop)
            table("Loot", weight = 1) {
                item(objs.coins, quantity = 1..10, weight = 20)
                nothing(weight = 80)
            }
        }

        registry.register(BearNpcs.bear, bearTable)
    }
}

/** NPC type references for Bear. */
internal object BearNpcs : NpcReferences() {
    val bear = find("brownbear")
}

/** Object type references for Bear drops. */
internal object BearObjs : ObjReferences() {
    val bear_fur = find("fur")
    val raw_bear_meat = find("raw_bear_meat")
}
