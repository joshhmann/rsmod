package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.npc.NpcType

/**
 * Drop table registrations for Jail Guard.
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 * - Jail guards share the same drop table as regular city guards per OSRS wiki.
 *
 * Created for Draynor second-zone validation.
 * Jail guards existed in F2PMonsterCombatScript but had no drop table registered.
 *
 * Skipped: Iron bolts (not in rev 233 cache), clue scrolls (post-2013),
 * Key (medium, post-2013).
 */
internal object JailGuardDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerJailGuard(registry)
    }

    private fun registerJailGuard(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Armour / Weapons (weight 1 of 4)
            table("Armour/Weapons", weight = 1) {
                item(JailGuardObjs.iron_dagger, weight = 8)
                item(objs.bronze_arrow, quantity = 1, weight = 3)
                item(objs.bronze_arrow, quantity = 2, weight = 2)
                item(objs.steel_arrow, weight = 4)
                item(objs.steel_arrow, quantity = 5, weight = 1)
            }

            // Runes / Talismans (weight 1 of 4)
            table("Runes/Talismans", weight = 1) {
                item(objs.airrune, quantity = 6, weight = 10)
                item(objs.earthrune, quantity = 3, weight = 10)
                item(objs.firerune, quantity = 2, weight = 10)
                item(objs.chaosrune, quantity = 1, weight = 6)
                item(objs.naturerune, weight = 1)
                item(objs.bloodrune, weight = 1)
                item(JailGuardObjs.body_talisman, weight = 3)
            }

            // Coins and Other (weight 1 of 4)
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

        val jailGuardNpcs: List<NpcType> =
            listOf(
                JailGuardNpcs.jail_guard_1,
                JailGuardNpcs.jail_guard_2,
                JailGuardNpcs.jail_guard_3,
                JailGuardNpcs.jail_guard_4,
                JailGuardNpcs.jail_guard_5,
            )
        registry.register(jailGuardNpcs.distinct(), table)
    }
}

internal object JailGuardNpcs : NpcReferences() {
    val jail_guard_1 = find("jail_guard_1")
    val jail_guard_2 = find("jail_guard_2")
    val jail_guard_3 = find("jail_guard_3")
    val jail_guard_4 = find("jail_guard_4")
    val jail_guard_5 = find("jail_guard_5")
}

internal object JailGuardObjs : ObjReferences() {
    val iron_dagger = find("iron_dagger")
    val body_talisman = find("body_talisman")
}
