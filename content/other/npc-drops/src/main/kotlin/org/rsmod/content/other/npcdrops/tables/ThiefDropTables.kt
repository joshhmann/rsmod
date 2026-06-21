package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.npc.NpcType

/**
 * Drop table registrations for Thief.
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 *
 * Varrock regional batch (Level 5 operational mode).
 * Thief (thief1, thief2) — level 16 NPCs in Varrock marketplace
 * and Port Sarim. Had combat via F2PMonsterCombatScript but NO drop
 * table registered. Now populated from corpus data: weapons, runes,
 * herbs, coins, and miscellaneous items.
 *
 * Skipped: bronze bolts, grimy marrentill (not in rev 233 cache),
 * clue scrolls (post-2013).
 */
internal object ThiefDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerThief(registry)
    }

    private fun registerThief(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Weapons / Armour
            table("Weapons/Armour", weight = 1) {
                item(objs.bronze_med_helm, weight = 2)
                item(ThiefObjs.iron_dagger, weight = 1)
                item(objs.bronze_arrow, quantity = 7, weight = 3)
            }

            // Runes
            table("Runes", weight = 1) {
                item(objs.earthrune, quantity = 4, weight = 2)
                item(objs.firerune, quantity = 6, weight = 2)
                item(objs.mindrune, quantity = 9, weight = 2)
                item(objs.chaosrune, quantity = 2, weight = 1)
            }

            // Herbs (grimy herb table)
            table("Herbs", weight = 1) {
                item(ThiefObjs.guam_leaf, weight = 20)
                item(ThiefObjs.tarromin, weight = 15)
                item(ThiefObjs.harralander, weight = 12)
                item(ThiefObjs.ranarr_weed, weight = 9)
                item(ThiefObjs.irit_leaf, weight = 7)
                item(ThiefObjs.avantoe, weight = 5)
                item(ThiefObjs.kwuarm, weight = 4)
                item(ThiefObjs.cadantine, weight = 3)
                item(ThiefObjs.lantadyme, weight = 2)
                item(ThiefObjs.dwarf_weed, weight = 2)
            }

            // Coins and Other
            table("Other", weight = 1) {
                item(objs.coins, quantity = 3, weight = 38)
                item(objs.coins, quantity = 10, weight = 23)
                item(objs.coins, quantity = 5, weight = 9)
                item(objs.coins, quantity = 15, weight = 4)
                item(objs.coins, quantity = 25, weight = 1)
                item(objs.fishing_bait, weight = 5)
                item(objs.copper_ore, weight = 2)
                item(ThiefObjs.earth_talisman, weight = 2)
                item(objs.cabbage, weight = 1)
            }
        }

        val thiefNpcs: List<NpcType> =
            listOf(
                ThiefNpcs.thief1,
                ThiefNpcs.thief2,
            )
        registry.register(thiefNpcs.distinct(), table)
    }
}

internal object ThiefNpcs : NpcReferences() {
    val thief1 = find("thief1")
    val thief2 = find("thief2")
}

internal object ThiefObjs : ObjReferences() {
    val iron_dagger = find("iron_dagger")
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
    val earth_talisman = find("earth_talisman")
}
