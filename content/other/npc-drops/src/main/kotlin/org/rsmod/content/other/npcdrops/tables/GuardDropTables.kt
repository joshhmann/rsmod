package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.npc.NpcType

/**
 * Drop table registrations for Guard.
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 * - Existing inline handler in NpcDropTablesScript.kt preserved and enriched
 *
 * Enriched from corpus June 2026: added steel arrows, blood rune, body talisman,
 * expanded seed table with additional farming seeds, expanded coin entries.
 */
internal object GuardDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerGuard(registry)
    }

    private fun registerGuard(registry: NpcDropTableRegistry) {
        val guardTable = dropTable {
            always(objs.bones)

            // Armour / Weapons (weight 1 of 5)
            table("Armour/Weapons", weight = 1) {
                item(GuardObjs.iron_dagger, weight = 8)
                item(objs.bronze_arrow, quantity = 1, weight = 3)
                item(objs.bronze_arrow, quantity = 2, weight = 2)
                item(objs.steel_arrow, weight = 4)
                item(objs.steel_arrow, quantity = 5, weight = 1)
                // TODO: wiki-validate drop rates
                item(GuardObjs.steel_sword, weight = 1)
                item(GuardObjs.steel_med_helm, weight = 1)
            }

            // Ores / Bars (weight 1 of 5)
            table("Ores/Bars", weight = 1) {
                item(objs.iron_ore, weight = 1)
            }

            // Runes / Talismans (weight 1 of 5)
            table("Runes/Talismans", weight = 1) {
                item(objs.airrune, quantity = 6, weight = 10)
                item(objs.earthrune, quantity = 3, weight = 10)
                item(objs.firerune, quantity = 2, weight = 10)
                item(objs.chaosrune, quantity = 1, weight = 6)
                item(objs.naturerune, weight = 1)
                item(objs.bloodrune, weight = 1)
                item(objs.body_talisman, weight = 3)
            }

            // Seeds (weight 1 of 5) — low-level farming seeds
            table("Seeds", weight = 1) {
                item(objs.cabbage_seed, quantity = 4, weight = 6)
                item(objs.potato_seed, quantity = 4, weight = 12)
                item(objs.onion_seed, quantity = 4, weight = 9)
                item(objs.tomato_seed, quantity = 3, weight = 3)
                item(objs.sweetcorn_seed, quantity = 3, weight = 2)
                item(objs.strawberry_seed, quantity = 2, weight = 1)
                item(objs.watermelon_seed, quantity = 2, weight = 1)
                item(objs.snape_grass_seed, quantity = 2, weight = 1)
            }

            // Other (weight 1 of 5)
            table("Other", weight = 1) {
                item(objs.coins, quantity = 1, weight = 19)
                item(objs.coins, quantity = 7, weight = 16)
                item(objs.coins, quantity = 5, weight = 18)
                item(objs.coins, quantity = 12, weight = 9)
                item(objs.coins, quantity = 4, weight = 8)
                item(objs.coins, quantity = 25, weight = 4)
                item(objs.coins, quantity = 17, weight = 4)
                item(objs.coins, quantity = 30, weight = 2)
                item(objs.grain, weight = 10)
            }
        }

        val guardNpcs: List<NpcType> =
            listOf(
                GuardNpcs.city_guard,
                GuardNpcs.guard1,
                GuardNpcs.deadman_guard_lumbridge,
                GuardNpcs.falador_guard,
            )
        registry.register(guardNpcs.distinct(), guardTable)
    }
}

internal object GuardNpcs : NpcReferences() {
    val city_guard = find("city_guard")
    val guard1 = find("guard1")
    val deadman_guard_lumbridge = find("deadman_guard_lumbridge")
    val falador_guard = find("fai_falador_guard1")
}

internal object GuardObjs : ObjReferences() {
    val iron_dagger = find("iron_dagger")
    val steel_sword = find("steel_sword")
    val steel_med_helm = find("steel_med_helm")
}
