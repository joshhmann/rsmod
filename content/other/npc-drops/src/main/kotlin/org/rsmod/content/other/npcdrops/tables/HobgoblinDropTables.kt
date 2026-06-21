package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.npc.NpcType

/**
 * Drop table registrations for Hobgoblins.
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 *
 * Port Sarim / Rimmington regional batch (Level 5 operational mode).
 * Hobgoblins (hobgoblin_unarmed, hobgoblin_armed) — level 15-28 NPCs
 * found in Rimmington, Asgarnian Ice Caves, and the Wilderness.
 * Had combat registration in AggressiveNpcCombat but NO drop table.
 * Rimmington variants (rimmington_hobgoblin_unarmed_1-3,
 * rimmington_hobgoblin_armed_1) added as region-specific variants.
 *
 * Corpus source: 135 items total.
 * Skipped: grimy marrentill, trading sticks (not in rev 233 cache),
 * goblin mail (not in cache), key halves, clue scrolls (post-2013),
 * hobgoblin champion scroll (not in cache), looting bag (wilderness).
 */
internal object HobgoblinDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerHobgoblin(registry)
    }

    private fun registerHobgoblin(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Weapons / Armour
            table("Weapons/Armour", weight = 1) {
                item(objs.bronze_spear, weight = 3)
                item(objs.iron_sword, weight = 3)
                item(HobgoblinObjs.steel_dagger, weight = 3)
                item(objs.iron_spear, weight = 2)
                item(objs.steel_spear, weight = 2)
                item(HobgoblinObjs.steel_longsword, weight = 1)
                item(HobgoblinObjs.iron_mace, weight = 1)
                item(HobgoblinObjs.steel_mace, weight = 1)
            }

            // Runes
            table("Runes", weight = 1) {
                item(objs.lawrune, quantity = 2, weight = 3)
                item(objs.waterrune, quantity = 2, weight = 2)
                item(objs.firerune, quantity = 7, weight = 2)
                item(objs.bodyrune, quantity = 6, weight = 2)
                item(objs.chaosrune, quantity = 3, weight = 2)
                item(objs.naturerune, quantity = 2, weight = 2)
                item(objs.mindrune, quantity = 9, weight = 1)
                item(objs.cosmicrune, quantity = 2, weight = 1)
            }

            // Coins
            table("Coins", weight = 1) {
                item(objs.coins, quantity = 5, weight = 34)
                item(objs.coins, quantity = 15, weight = 16)
                item(objs.coins, quantity = 28, weight = 16)
                item(objs.coins, quantity = 10, weight = 7)
                item(objs.coins, quantity = 4, weight = 5)
                item(objs.coins, quantity = 42, weight = 2)
            }

            // Herbs (grimy herb table)
            table("Herbs", weight = 1) {
                item(HobgoblinObjs.guam_leaf, weight = 20)
                item(HobgoblinObjs.tarromin, weight = 15)
                item(HobgoblinObjs.harralander, weight = 12)
                item(HobgoblinObjs.ranarr_weed, weight = 9)
                item(HobgoblinObjs.irit_leaf, weight = 7)
                item(HobgoblinObjs.avantoe, weight = 5)
                item(HobgoblinObjs.kwuarm, weight = 4)
                item(HobgoblinObjs.cadantine, weight = 3)
                item(HobgoblinObjs.lantadyme, weight = 2)
                item(HobgoblinObjs.dwarf_weed, weight = 2)
            }

            // Seeds
            table("Seeds", weight = 1) {
                item(objs.potato_seed, quantity = 4, weight = 12)
                item(objs.onion_seed, quantity = 4, weight = 9)
                item(objs.cabbage_seed, quantity = 4, weight = 6)
                item(objs.tomato_seed, quantity = 3, weight = 3)
                item(objs.sweetcorn_seed, quantity = 3, weight = 2)
                item(objs.strawberry_seed, quantity = 2, weight = 1)
                item(objs.watermelon_seed, quantity = 2, weight = 1)
                item(objs.snape_grass_seed, quantity = 2, weight = 1)
            }

            // Gem / Rare Drop Table
            table("Gem/RDT", weight = 1) {
                item(objs.uncut_sapphire, weight = 4)
                item(objs.uncut_emerald, weight = 2)
                item(objs.uncut_ruby, weight = 1)
                item(objs.uncut_diamond, weight = 1)
                item(HobgoblinObjs.iron_javelin, quantity = 5, weight = 1)
            }

            // Other
            table("Other", weight = 1) {
                item(HobgoblinObjs.limpwurt_root, weight = 43)
                item(objs.copper_ore, weight = 2)
                item(HobgoblinObjs.mushroom_spore, weight = 1)
            }
        }

        val hobgoblinNpcs: List<NpcType> =
            listOf(
                HobgoblinNpcs.hobgoblin_unarmed,
                HobgoblinNpcs.hobgoblin_armed,
                HobgoblinNpcs.rimmington_hobgoblin_unarmed_1,
                HobgoblinNpcs.rimmington_hobgoblin_unarmed_2,
                HobgoblinNpcs.rimmington_hobgoblin_unarmed_3,
                HobgoblinNpcs.rimmington_hobgoblin_armed_1,
            )
        registry.register(hobgoblinNpcs.distinct(), table)
    }
}

internal object HobgoblinNpcs : NpcReferences() {
    val hobgoblin_unarmed = find("hobgoblin_unarmed")
    val hobgoblin_armed = find("hobgoblin_armed")
    val rimmington_hobgoblin_unarmed_1 = find("rimmington_hobgoblin_unarmed_1")
    val rimmington_hobgoblin_unarmed_2 = find("rimmington_hobgoblin_unarmed_2")
    val rimmington_hobgoblin_unarmed_3 = find("rimmington_hobgoblin_unarmed_3")
    val rimmington_hobgoblin_armed_1 = find("rimmington_hobgoblin_armed_1")
}

internal object HobgoblinObjs : ObjReferences() {
    val steel_dagger = find("steel_dagger")
    val steel_longsword = find("steel_longsword")
    val iron_mace = find("iron_mace")
    val steel_mace = find("steel_mace")
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
    val limpwurt_root = find("limpwurt_root")
    val mushroom_spore = find("mushroom_spore")
    val iron_javelin = find("iron_javelin")
}
