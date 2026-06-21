package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.npc.NpcType

/**
 * Drop table registrations for Chaos Druid.
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 *
 * Varrock regional batch (Level 5 operational mode).
 * Chaos druid (chaos_druid) — level 13 NPC in Edgeville Dungeon.
 * Had combat via ChaosDruidCombatScript but NO drop table registered.
 *
 * Chaos druids are known for their herb drops (best F2P herb source).
 * Corpus provides rich herb table with 10 grimy herb types.
 *
 * Skipped: wilderness_chaos_druid variant (wilderness-only),
 * mithril bolts, grimy marrentill, vial of water, unholy mould
 * (not in rev 233 cache), key halves, shield left half (post-2013),
 * looting bag, larran's key, slayer's enchantment (wilderness/post-2013),
 * ensouled chaos druid head (post-2015).
 */
internal object ChaosDruidDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerChaosDruid(registry)
    }

    private fun registerChaosDruid(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Runes
            table("Runes", weight = 1) {
                item(objs.lawrune, quantity = 2, weight = 7)
                item(objs.airrune, quantity = 36, weight = 3)
                item(objs.bodyrune, quantity = 9, weight = 2)
                item(objs.earthrune, quantity = 9, weight = 2)
                item(objs.mindrune, quantity = 12, weight = 2)
                item(objs.naturerune, quantity = 3, weight = 1)
            }

            // Herbs (primary draw — chaos druids are the best F2P herb source)
            table("Herbs", weight = 1) {
                item(ChaosDruidObjs.guam_leaf, weight = 20)
                item(ChaosDruidObjs.tarromin, weight = 15)
                item(ChaosDruidObjs.harralander, weight = 12)
                item(ChaosDruidObjs.ranarr_weed, weight = 9)
                item(ChaosDruidObjs.irit_leaf, weight = 7)
                item(ChaosDruidObjs.avantoe, weight = 5)
                item(ChaosDruidObjs.kwuarm, weight = 4)
                item(ChaosDruidObjs.cadantine, weight = 3)
                item(ChaosDruidObjs.lantadyme, weight = 2)
                item(ChaosDruidObjs.dwarf_weed, weight = 2)
            }

            // Equipment
            table("Equipment", weight = 1) {
                item(ChaosDruidObjs.bronze_longsword, weight = 1)
                item(objs.snape_grass, weight = 1)
            }

            // Coins
            table("Coins", weight = 1) {
                item(objs.coins, quantity = 3, weight = 5)
                item(objs.coins, quantity = 8, weight = 5)
                item(objs.coins, quantity = 29, weight = 3)
                item(objs.coins, quantity = 35, weight = 1)
            }

            // Gem / Rare Drop Table
            table("Gem/RDT", weight = 1) {
                item(objs.uncut_sapphire, weight = 4)
                item(objs.uncut_emerald, weight = 2)
                item(objs.uncut_ruby, weight = 1)
                item(ChaosDruidObjs.chaos_talisman, weight = 1)
                item(ChaosDruidObjs.nature_talisman, weight = 1)
                item(objs.uncut_diamond, weight = 1)
                item(objs.rune_javelin, quantity = 5, weight = 1)
                item(objs.rune_spear, weight = 1)
                item(objs.dragon_spear, weight = 1)
            }
        }

        val chaosDruidNpcs: List<NpcType> =
            listOf(ChaosDruidNpcs.chaos_druid)
        registry.register(chaosDruidNpcs.distinct(), table)
    }
}

internal object ChaosDruidNpcs : NpcReferences() {
    val chaos_druid = find("chaos_druid")
}

internal object ChaosDruidObjs : ObjReferences() {
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
    val bronze_longsword = find("bronze_longsword")
    val chaos_talisman = find("chaos_talisman")
    val nature_talisman = find("nature_talisman")
}
