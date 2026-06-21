package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.npc.NpcType

/**
 * Drop table registrations for Ice Giant NPCs.
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 *
 * Asgarnian Ice Caves regional batch (Level 5 operational mode).
 * Ice Giants (icegiant, icegiant2, icegiant3, icegiant_low_wanderrange,
 * icegiant_low_wanderrange2) — level 53 NPCs found in Asgarnian Ice Caves
 * and White Wolf Mountain. Combat registration existed in AggressiveNpcCombat
 * but NO drop table was registered.
 *
 * Skipped: wildblood seed, jug of wine (not in rev 233 cache),
 * clue scrolls, ice giant ribs, ensouled giant head (post-2013),
 * key halves, shield left half, long/curved bone (post-2013),
 * giant champion scroll (not in cache), looting bag (wilderness),
 * blighted items (wilderness-only), teleport tablets, Larran's key,
 * slayer's enchantment, frozen tear (post-2013).
 */
internal object IceGiantDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerIceGiant(registry)
    }

    private fun registerIceGiant(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(IceGiantObjs.big_bones)

            // Weapons / Armour
            table("Weapons/Armour", weight = 1) {
                item(IceGiantObjs.iron_2h_sword, weight = 5)
                item(IceGiantObjs.black_kiteshield, weight = 5)
                item(objs.steel_axe, weight = 4)
                item(objs.steel_sword, weight = 4)
                item(IceGiantObjs.iron_platelegs, weight = 1)
                item(IceGiantObjs.mithril_mace, weight = 1)
                item(IceGiantObjs.mithril_sq_shield, weight = 1)
                item(IceGiantObjs.mithril_axe, weight = 4)
                item(IceGiantObjs.adamant_sword, weight = 4)
                item(IceGiantObjs.mithril_platelegs, weight = 1)
                item(IceGiantObjs.adamant_dagger, weight = 1)
                item(IceGiantObjs.adamant_mace, weight = 1)
                item(IceGiantObjs.adamant_sq_shield, weight = 1)
                item(IceGiantObjs.adamant_kiteshield, weight = 1)
                item(IceGiantObjs.rune_dagger, weight = 1)
            }

            // Runes
            table("Runes", weight = 1) {
                item(objs.naturerune, quantity = 6, weight = 4)
                item(objs.mindrune, quantity = 24, weight = 3)
                item(objs.bodyrune, quantity = 37, weight = 3)
                item(objs.lawrune, quantity = 3, weight = 2)
                item(objs.waterrune, quantity = 12, weight = 1)
                item(objs.cosmicrune, quantity = 4, weight = 1)
                item(objs.deathrune, quantity = 3, weight = 1)
                item(objs.deathrune, quantity = 5, weight = 3)
                item(objs.bloodrune, quantity = 2, weight = 1)
                item(objs.bloodrune, quantity = 5, weight = 2)
                item(objs.chaosrune, quantity = 15, weight = 1)
            }

            // Coins
            table("Coins", weight = 1) {
                item(objs.coins, quantity = 117, weight = 32)
                item(objs.coins, quantity = 53, weight = 12)
                item(objs.coins, quantity = 196, weight = 10)
                item(objs.coins, quantity = 5, weight = 8)
                item(objs.coins, quantity = 8, weight = 7)
                item(objs.coins, quantity = 22, weight = 6)
                item(objs.coins, quantity = 400, weight = 2)
                item(objs.coins, quantity = 100, weight = 6)
            }

            // Seeds (rich seed table)
            table("Seeds", weight = 1) {
                item(objs.limpwurt_seed, weight = 15)
                item(objs.strawberry_seed, weight = 12)
                item(IceGiantObjs.marrentill_seed, weight = 10)
                item(IceGiantObjs.jangerberry_seed, weight = 8)
                item(objs.tarromin_seed, weight = 6)
                item(objs.watermelon_seed, weight = 5)
                item(IceGiantObjs.harralander_seed, weight = 4)
                item(objs.snape_grass_seed, weight = 3)
                item(IceGiantObjs.ranarr_seed, weight = 3)
                item(IceGiantObjs.whiteberry_seed, weight = 2)
                item(IceGiantObjs.mushroom_spore, weight = 2)
                item(IceGiantObjs.toadflax_seed, weight = 2)
                item(IceGiantObjs.belladonna_seed, weight = 1)
                item(IceGiantObjs.irit_seed, weight = 1)
                item(IceGiantObjs.avantoe_seed, weight = 1)
                item(IceGiantObjs.kwuarm_seed, weight = 1)
                item(IceGiantObjs.cadantine_seed, weight = 1)
                item(IceGiantObjs.lantadyme_seed, weight = 1)
                item(IceGiantObjs.dwarf_weed_seed, weight = 1)
                item(IceGiantObjs.torstol_seed, weight = 1)
            }

            // Other / Materials
            table("Other", weight = 1) {
                item(IceGiantObjs.mithril_ore, weight = 1)
                item(objs.banana, weight = 1)
            }

            // Gem / Rare Drop Table
            table("Gem/RDT", weight = 1) {
                item(objs.uncut_sapphire, weight = 4)
                item(objs.uncut_emerald, weight = 2)
                item(objs.uncut_ruby, weight = 1)
                item(objs.uncut_diamond, weight = 1)
                item(IceGiantObjs.chaos_talisman, weight = 1)
                item(IceGiantObjs.nature_talisman, weight = 1)
                item(objs.rune_javelin, quantity = 5, weight = 1)
                item(objs.rune_spear, weight = 1)
                item(IceGiantObjs.dragon_spear, weight = 1)
            }
        }

        val iceGiantNpcs: List<NpcType> =
            listOf(
                IceGiantNpcs.icegiant,
                IceGiantNpcs.icegiant2,
                IceGiantNpcs.icegiant3,
                IceGiantNpcs.icegiant_low_wanderrange,
                IceGiantNpcs.icegiant_low_wanderrange2,
            )
        registry.register(iceGiantNpcs.distinct(), table)
    }
}

internal object IceGiantNpcs : NpcReferences() {
    val icegiant = find("icegiant")
    val icegiant2 = find("icegiant2")
    val icegiant3 = find("icegiant3")
    val icegiant_low_wanderrange = find("icegiant_low_wanderrange")
    val icegiant_low_wanderrange2 = find("icegiant_low_wanderrange2")
}

internal object IceGiantObjs : ObjReferences() {
    val big_bones = find("big_bones")
    val iron_2h_sword = find("iron_2h_sword")
    val black_kiteshield = find("black_kiteshield")
    val iron_platelegs = find("iron_platelegs")
    val mithril_mace = find("mithril_mace")
    val mithril_sq_shield = find("mithril_sq_shield")
    val mithril_axe = find("mithril_axe")
    val adamant_sword = find("adamant_sword")
    val mithril_platelegs = find("mithril_platelegs")
    val adamant_dagger = find("adamant_dagger")
    val adamant_mace = find("adamant_mace")
    val adamant_sq_shield = find("adamant_sq_shield")
    val adamant_kiteshield = find("adamant_kiteshield")
    val rune_dagger = find("rune_dagger")
    val mithril_ore = find("mithril_ore")
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
