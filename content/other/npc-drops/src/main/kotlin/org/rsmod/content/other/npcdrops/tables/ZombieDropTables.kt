package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Zombie NPCs.
 *
 * Data sources:
 * - Primary: drops_by_source.json (OSRS wiki corpus, 64 filtered items)
 * - Secondary: Kronos-184 drops/eco/Zombie.json
 *
 * Conditional drops filtered:
 * - Zombie champion scroll (minigame)
 * - Zombie bone (post-2013 — tracks old bones)
 * - Clue scrolls (post-2013)
 * - Key halves, shield left half (RDT)
 */
internal object ZombieDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        val table = dropTable {
            // Guaranteed
            always(objs.bones)

            // Weapons & Armour
            table("Weapons/Armour", weight = 30) {
                item(objs.iron_arrow, quantity = 5, weight = 7)
                item(objs.iron_arrow, quantity = 12, weight = 3)
                item(objs.bronze_arrow, quantity = 5..8, weight = 5)
                item(objs.bronze_arrow, quantity = 15, weight = 2)
                item(objs.steel_arrow, quantity = 3..5, weight = 3)
                item(objs.bronze_axe, weight = 2)
                item(objs.bronze_longsword, weight = 1)
                item(objs.bronze_med_helm, weight = 4)
                item(objs.bronze_sword, weight = 2)
                item(DropTableObjs.bronze_sq_shield, weight = 2)
                item(DropTableObjs.iron_dagger, weight = 1)
                item(DropTableObjs.bronze_bolts, quantity = 2..10, weight = 3)
            }

            // Runes
            table("Runes", weight = 30) {
                item(objs.bodyrune, quantity = 6, weight = 5)
                item(objs.mindrune, quantity = 5..9, weight = 5)
                item(objs.airrune, quantity = 13, weight = 4)
                item(objs.airrune, quantity = 7, weight = 3)
                item(objs.firerune, quantity = 7, weight = 3)
                item(objs.waterrune, quantity = 4, weight = 3)
                item(objs.earthrune, quantity = 4..6, weight = 3)
                item(objs.chaosrune, quantity = 2..4, weight = 3)
                item(objs.naturerune, quantity = 6..12, weight = 3)
                item(objs.lawrune, quantity = 2, weight = 2)
                item(objs.cosmicrune, quantity = 2, weight = 2)
            }

            // Coins
            table("Coins", weight = 20) {
                item(objs.coins, quantity = 5..10, weight = 8)
                item(objs.coins, quantity = 15..25, weight = 6)
                item(objs.coins, quantity = 30..50, weight = 3)
            }

            // Herbs
            table("Herbs", weight = 5) {
                item(ZombieObjs.grimy_guam, weight = 15)
                item(ZombieObjs.grimy_tarromin, weight = 12)
                item(ZombieObjs.grimy_harralander, weight = 9)
                item(ZombieObjs.grimy_ranarr, weight = 7)
                item(ZombieObjs.grimy_irit, weight = 5)
                item(ZombieObjs.grimy_kwuarm, weight = 3)
            }

            // Other
            table("Other", weight = 10) {
                item(objs.hammer, weight = 5)
                item(objs.fishing_bait, quantity = 1..5, weight = 3)
                item(objs.copper_ore, weight = 2)
                item(objs.tin_ore, weight = 1)
                item(objs.iron_ore, weight = 1)
                item(objs.knife, weight = 1)
                item(objs.needle, weight = 1)
            }
        }

        // Register all zombie variants found in F2P areas
        val zombieNpcs = listOf(
            ZombieNpcs.zombie_unarmed, ZombieNpcs.zombie_unarmed2,
            ZombieNpcs.zombie_unarmed3, ZombieNpcs.zombie_unarmed4,
            ZombieNpcs.zombie_unarmed5, ZombieNpcs.zombie_unarmed6,
            ZombieNpcs.zombie_unarmed_city1, ZombieNpcs.zombie_unarmed_city2,
            ZombieNpcs.zombie_unarmed_city3, ZombieNpcs.zombie_unarmed_city4,
            ZombieNpcs.zombie_unarmed_city5, ZombieNpcs.zombie_unarmed_city6,
            ZombieNpcs.zombie_unarmed_sewer1, ZombieNpcs.zombie_unarmed_sewer2,
            ZombieNpcs.zombie_unarmed_sewer3,
            ZombieNpcs.zombie2, ZombieNpcs.zombie2_b,
            ZombieNpcs.zombie2_c, ZombieNpcs.zombie2_rural1,
        )
        registry.register(zombieNpcs.distinct(), table)
    }
}

internal object ZombieNpcs : NpcReferences() {
    val zombie_unarmed = find("zombie_unarmed")
    val zombie_unarmed2 = find("zombie_unarmed2")
    val zombie_unarmed3 = find("zombie_unarmed3")
    val zombie_unarmed4 = find("zombie_unarmed4")
    val zombie_unarmed5 = find("zombie_unarmed5")
    val zombie_unarmed6 = find("zombie_unarmed6")
    val zombie_unarmed_city1 = find("zombie_unarmed_city1")
    val zombie_unarmed_city2 = find("zombie_unarmed_city2")
    val zombie_unarmed_city3 = find("zombie_unarmed_city3")
    val zombie_unarmed_city4 = find("zombie_unarmed_city4")
    val zombie_unarmed_city5 = find("zombie_unarmed_city5")
    val zombie_unarmed_city6 = find("zombie_unarmed_city6")
    val zombie_unarmed_sewer1 = find("zombie_unarmed_sewer1")
    val zombie_unarmed_sewer2 = find("zombie_unarmed_sewer2")
    val zombie_unarmed_sewer3 = find("zombie_unarmed_sewer3")
    val zombie2 = find("zombie2")
    val zombie2_b = find("zombie2_b")
    val zombie2_c = find("zombie2_c")
    val zombie2_rural1 = find("zombie2_rural1")
}

internal object ZombieObjs : ObjReferences() {
    val grimy_guam = find("unidentified_guam")
    val grimy_tarromin = find("unidentified_tarromin")
    val grimy_harralander = find("unidentified_harralander")
    val grimy_ranarr = find("unidentified_ranarr")
    val grimy_irit = find("unidentified_irit")
    val grimy_kwuarm = find("unidentified_kwuarm")
}
