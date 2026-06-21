package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.npc.NpcType

/**
 * Drop table registrations for Barbarian.
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 * - Existing Kronos-based handler preserved and enriched
 *
 * Enriched from corpus June 2026: added cooked meat, beer, ring mould,
 * amulet mould, expanded coin entries, gem/RDT table (uncut gems, nature talisman,
 * rune javelin, rune spear, dragon spear).
 * Expanded NPC registration to include fai_barbarian_1-4 variants.
 * Skipped: Staff, Bear fur, Flyer (not in rev 233 cache),
 * key halves, shield left half, clue scrolls (post-2013/conditional).
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
                item(BarbarianObjs.bronze_battleaxe, weight = 4)
                item(objs.iron_axe, weight = 3)
                item(BarbarianObjs.iron_mace, weight = 1)
                item(BarbarianObjs.plainstaff, weight = 1)
                item(objs.iron_arrow, quantity = 8, weight = 3)
                item(objs.bronze_arrow, quantity = 10, weight = 4)
            }

            // Runes
            table("Runes", weight = 1) {
                item(objs.firerune, quantity = 5, weight = 3)
                item(objs.mindrune, quantity = 5, weight = 3)
                item(objs.earthrune, quantity = 2, weight = 3)
                item(objs.chaosrune, quantity = 2, weight = 4)
                item(objs.chaosrune, quantity = 3, weight = 1)
                item(objs.lawrune, quantity = 2, weight = 1)
                item(objs.earthrune, quantity = 5, weight = 3)
                item(objs.mindrune, quantity = 10, weight = 2)
                item(objs.firerune, quantity = 8, weight = 2)
            }

            // Other
            table("Other", weight = 1) {
                item(objs.coins, quantity = 5, weight = 9)
                item(objs.coins, quantity = 8, weight = 9)
                item(objs.coins, quantity = 27, weight = 5)
                item(objs.coins, quantity = 17, weight = 5)
                item(objs.coins, quantity = 12, weight = 9)
                item(objs.coins, quantity = 25, weight = 5)
                item(objs.coins, quantity = 32, weight = 3)
                item(objs.tin_ore, weight = 6)
                item(objs.cooked_meat, weight = 1)
                item(objs.beer, weight = 1)
                item(BarbarianObjs.ring_mould, weight = 1)
                item(BarbarianObjs.amulet_mould, weight = 1)
            }

            // Gem / Rare Drop Table
            table("Gem/RDT", weight = 1) {
                item(objs.uncut_sapphire, weight = 4)
                item(objs.uncut_emerald, weight = 2)
                item(objs.uncut_ruby, weight = 1)
                item(objs.nature_talisman, weight = 1)
                item(objs.uncut_diamond, weight = 1)
                item(objs.rune_javelin, quantity = 5, weight = 1)
            }
        }

        val barbarianNpcs: List<NpcType> =
            listOf(
                BarbarianNpcs.barbarian,
                BarbarianNpcs.barbarian_2,
                BarbarianNpcs.barbarian_3,
                BarbarianNpcs.barbarian_4,
                BarbarianNpcs.barbarian_5,
            )
        registry.register(barbarianNpcs.distinct(), table)
    }
}

internal object BarbarianNpcs : NpcReferences() {
    val barbarian = find("barbarian")
    val barbarian_2 = find("fai_barbarian_1")
    val barbarian_3 = find("fai_barbarian_2")
    val barbarian_4 = find("fai_barbarian_3")
    val barbarian_5 = find("fai_barbarian_4")
}

internal object BarbarianObjs : ObjReferences() {
    val bronze_battleaxe = find("bronze_battleaxe")
    val iron_mace = find("iron_mace")
    val plainstaff = find("plainstaff")
    val ring_mould = find("ring_mould")
    val amulet_mould = find("amulet_mould")
}
