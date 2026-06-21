package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Mugger.
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 * - Existing Kronos-based handler preserved and enriched
 *
 * Enriched from corpus June 2026: added coins, rope, knife, fishing bait,
 * copper ore, cabbage, bronze med helm, herb sub-table (guam through dwarf weed).
 * Skipped: bronze bolts (not in rev 233 cache), grimy marrentill (not in cache),
 * clue scrolls (post-2013).
 */
internal object MuggerDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerMugger(registry)
    }

    private fun registerMugger(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Runes
            table("Runes", weight = 1) {
                item(objs.mindrune, quantity = 9, weight = 3)
                item(objs.earthrune, quantity = 5, weight = 2)
                item(objs.waterrune, quantity = 6, weight = 2)
            }

            // Weapons/Armour
            table("Weapons", weight = 1) {
                item(objs.bronze_med_helm, weight = 2)
                item(objs.knife, weight = 1)
            }

            // Herbs (P2P — herb drop table)
            table("Herbs", weight = 1) {
                item(MuggerObjs.guam_leaf, weight = 15)
                item(MuggerObjs.tarromin, weight = 11)
                item(MuggerObjs.harralander, weight = 9)
                item(MuggerObjs.ranarr_weed, weight = 7)
                item(MuggerObjs.irit_leaf, weight = 5)
                item(MuggerObjs.avantoe, weight = 4)
                item(MuggerObjs.kwuarm, weight = 3)
                item(MuggerObjs.cadantine, weight = 2)
                item(MuggerObjs.lantadyme, weight = 2)
                item(MuggerObjs.dwarf_weed, weight = 2)
            }

            // Other
            table("Other", weight = 1) {
                item(objs.coins, quantity = 10, weight = 13)
                item(objs.coins, quantity = 5, weight = 12)
                item(objs.coins, quantity = 15, weight = 3)
                item(objs.coins, quantity = 25, weight = 1)
                item(objs.rope, weight = 40)
                item(MuggerObjs.fishing_bait, weight = 6)
                item(objs.copper_ore, weight = 2)
                item(objs.cabbage, weight = 1)
            }
        }

        registry.register(MuggerNpcs.mugger, table)
    }
}

internal object MuggerNpcs : NpcReferences() {
    val mugger = find("mugger")
}

internal object MuggerObjs : ObjReferences() {
    val fishing_bait = find("fishing_bait")
    val guam_leaf = find("guam_leaf")
    val tarromin = find("tarromin")
    val harralander = find("harralander")
    val ranarr_weed = find("ranarr_weed")
    val irit_leaf = find("irit_leaf")
    val avantoe = find("avantoe")
    val kwuarm = find("kwuarm")
    val cadantine = find("cadantine")
    val lantadyme = find("lantadyme")
    val dwarf_weed = find("dwarf_weed")
}
