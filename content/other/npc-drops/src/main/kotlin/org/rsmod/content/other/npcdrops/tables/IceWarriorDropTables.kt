package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.npc.NpcType

/**
 * Drop table registrations for Ice Warrior NPCs.
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 *
 * Asgarnian Ice Caves regional batch (Level 5 operational mode).
 * Ice Warriors (icewarrior, icewarrior_low_wanderrange) — level 34 NPCs
 * found in Asgarnian Ice Caves. Had NO combat registration and NO drop
 * table. Combat registration added to AggressiveNpcCombat alongside
 * the drop table.
 *
 * Skipped: grimy marrentill, wildblood seed (not in rev 233 cache),
 * clue scroll (medium), key halves, shield left half (post-2013),
 * looting bag (wilderness), frozen tear (post-2013), Larran's key,
 * slayer's enchantment.
 */
internal object IceWarriorDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerIceWarrior(registry)
    }

    private fun registerIceWarrior(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Weapons / Armour
            table("Weapons/Armour", weight = 1) {
                item(objs.iron_battleaxe, weight = 3)
                item(IceWarriorObjs.mithril_mace, weight = 1)
            }

            // Runes / Ammo
            table("Runes", weight = 1) {
                item(objs.naturerune, quantity = 4, weight = 10)
                item(objs.chaosrune, quantity = 3, weight = 8)
                item(objs.lawrune, quantity = 2, weight = 7)
                item(objs.cosmicrune, quantity = 2, weight = 5)
                item(IceWarriorObjs.mithril_arrow, quantity = 3, weight = 5)
                item(IceWarriorObjs.adamant_arrow, quantity = 2, weight = 3)
                item(objs.deathrune, quantity = 2, weight = 3)
                item(objs.bloodrune, quantity = 2, weight = 1)
            }

            // Herbs (grimy)
            table("Herbs", weight = 1) {
                item(IceWarriorObjs.guam_leaf, weight = 20)
                item(IceWarriorObjs.tarromin, weight = 15)
                item(IceWarriorObjs.harralander, weight = 12)
                item(IceWarriorObjs.ranarr_weed, weight = 9)
                item(IceWarriorObjs.irit_leaf, weight = 7)
                item(IceWarriorObjs.avantoe, weight = 5)
                item(IceWarriorObjs.kwuarm, weight = 4)
                item(IceWarriorObjs.cadantine, weight = 3)
                item(IceWarriorObjs.lantadyme, weight = 2)
                item(IceWarriorObjs.dwarf_weed, weight = 2)
            }

            // Seeds
            table("Seeds", weight = 1) {
                item(objs.limpwurt_seed, weight = 15)
                item(objs.strawberry_seed, weight = 12)
                item(IceWarriorObjs.marrentill_seed, weight = 10)
                item(IceWarriorObjs.jangerberry_seed, weight = 8)
                item(objs.tarromin_seed, weight = 6)
                item(objs.watermelon_seed, weight = 5)
                item(IceWarriorObjs.harralander_seed, weight = 4)
                item(objs.snape_grass_seed, weight = 3)
                item(IceWarriorObjs.ranarr_seed, weight = 3)
                item(IceWarriorObjs.whiteberry_seed, weight = 2)
                item(IceWarriorObjs.mushroom_spore, weight = 2)
                item(IceWarriorObjs.toadflax_seed, weight = 2)
                item(IceWarriorObjs.belladonna_seed, weight = 1)
                item(IceWarriorObjs.irit_seed, weight = 1)
                item(IceWarriorObjs.avantoe_seed, weight = 1)
                item(IceWarriorObjs.kwuarm_seed, weight = 1)
                item(IceWarriorObjs.cadantine_seed, weight = 1)
                item(IceWarriorObjs.lantadyme_seed, weight = 1)
                item(IceWarriorObjs.dwarf_weed_seed, weight = 1)
                item(IceWarriorObjs.torstol_seed, weight = 1)
            }

            // Coins and Other
            table("Other", weight = 1) {
                item(objs.coins, quantity = 15, weight = 39)
                item(objs.coins, quantity = 5, weight = 18)
                item(objs.coins, quantity = 10, weight = 10)
            }

            // Gem / Rare Drop Table
            table("Gem/RDT", weight = 1) {
                item(objs.uncut_sapphire, weight = 4)
                item(objs.uncut_emerald, weight = 2)
                item(objs.uncut_ruby, weight = 1)
                item(objs.uncut_diamond, weight = 1)
                item(IceWarriorObjs.chaos_talisman, weight = 1)
                item(IceWarriorObjs.nature_talisman, weight = 1)
                item(objs.rune_javelin, quantity = 5, weight = 1)
                item(objs.rune_spear, weight = 1)
                item(IceWarriorObjs.dragon_spear, weight = 1)
            }
        }

        val iceWarriorNpcs: List<NpcType> =
            listOf(
                IceWarriorNpcs.icewarrior,
                IceWarriorNpcs.icewarrior_low_wanderrange,
            )
        registry.register(iceWarriorNpcs.distinct(), table)
    }
}

internal object IceWarriorNpcs : NpcReferences() {
    val icewarrior = find("icewarrior")
    val icewarrior_low_wanderrange = find("icewarrior_low_wanderrange")
}

internal object IceWarriorObjs : ObjReferences() {
    val mithril_mace = find("mithril_mace")
    val mithril_arrow = find("mithril_arrow")
    val adamant_arrow = find("adamant_arrow")
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
    val chaos_talisman = find("chaos_talisman")
    val nature_talisman = find("nature_talisman")
    val dragon_spear = find("dragon_spear")
    val marrentill_seed = find("marrentill_seed")
    val jangerberry_seed = find("jangerberry_seed_2")
    val harralander_seed = find("harralander_seed")
    val ranarr_seed = find("ranarr_seed")
    val whiteberry_seed = find("whiteberry_seed_2")
    val mushroom_spore = find("mushroom_spore_2")
    val toadflax_seed = find("toadflax_seed")
    val belladonna_seed = find("belladonna_seed")
    val irit_seed = find("irit_seed")
    val avantoe_seed = find("avantoe_seed")
    val kwuarm_seed = find("kwuarm_seed")
    val cadantine_seed = find("cadantine_seed")
    val lantadyme_seed = find("lantadyme_seed")
    val dwarf_weed_seed = find("dwarf_weed_seed")
    val torstol_seed = find("torstol_seed")
}
