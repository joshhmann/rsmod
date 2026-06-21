package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.npc.NpcType

/**
 * Drop table registrations for Karamja Pirate NPCs (F2P).
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 * - Archive rs2 script at _archive/rs-sdk/server/content/scripts/drop tables/scripts/pirate.rs2
 *
 * NPC: Karamja Pirate (corpus NPC ID 15452, internal symbol `pirate1` in KaramjaNpcs.kt)
 * Combat level: 26, Found on Karamja (F2P area)
 * Always: Bones
 *
 * Drop structure:
 * - Weapons: Iron dagger (6/128), Bronze scimitar (4/128), Iron platebody (1/128) = 11/128
 * - Ranged: Iron bolts 2-12 (10/128)
 * - Runes/Ammo: Chaos 2 (6/128), Nature 2 (5/128), Bronze arrow 9 (3/128), Bronze arrow 12 (2/128),
 *               Air 10 (2/128), Earth 9 (2/128), Fire 5 (2/128), Law 2 (1/128) = 23/128
 * - Coins: 4 (29/128), 25 (13/128), 7 (8/128), 12 (6/128), 35 (4/128), 55 (1/128) = 61/128
 * - Other: Right eye patch (12/128), Chef's hat (1/128), Iron bar (1/128) = 14/128
 * - Gem Drop Table: 4/128 chance when rolled
 *
 * Skipped: clue scrolls (post-2013 content, not in rev 233 scope for this handoff),
 * key halves and shield left half (also post-2013 gem table expansion).
 *
 * Note: The Right eye patch is a members item in vanilla OSRS but included here for
 * completeness. The `map_members` gate from the original archive script is not
 * implemented in this drop table definition.
 */
internal object PirateDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerPirate(registry)
    }

    private fun registerPirate(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Weapons (total weight 11/128)
            table("Weapons", weight = 11) {
                item(objs.iron_dagger, weight = 6)
                item(objs.bronze_scimitar, weight = 4)
                item(objs.iron_platebody, weight = 1)
            }

            // Ranged (weight 10/128)
            table("Ranged", weight = 10) {
                item(objs.iron_bolts, quantity = 2..12, weight = 10)
            }

            // Runes and Ammunition (total weight 23/128)
            table("Runes and Ammo", weight = 23) {
                nothing(weight = 1)
                item(objs.chaosrune, quantity = 2, weight = 6)
                item(objs.naturerune, quantity = 2, weight = 5)
                item(objs.bronze_arrow, quantity = 9, weight = 3)
                item(objs.bronze_arrow, quantity = 12, weight = 2)
                item(objs.airrune, quantity = 10, weight = 2)
                item(objs.earthrune, quantity = 9, weight = 2)
                item(objs.firerune, quantity = 5, weight = 2)
                item(objs.lawrune, quantity = 2, weight = 1)
            }

            // Coins (total weight 61/128)
            table("Coins", weight = 61) {
                item(objs.coins, quantity = 4, weight = 29)
                item(objs.coins, quantity = 25, weight = 13)
                item(objs.coins, quantity = 7, weight = 8)
                item(objs.coins, quantity = 12, weight = 6)
                item(objs.coins, quantity = 35, weight = 4)
                item(objs.coins, quantity = 55, weight = 1)
            }

            // Other (total weight 14/128)
            table("Other", weight = 14) {
                nothing(weight = 1)
                item(PirateObjs.right_eye_patch, weight = 12)
                item(objs.chefs_hat, weight = 1)
                item(objs.iron_bar, weight = 1)
            }

            // Gem Drop Table (4/128 chance)
            table("Gem Drop Table", weight = 4) {
                nothing(weight = 1)
                item(objs.uncut_sapphire, weight = 32)
                item(objs.uncut_emerald, weight = 16)
                item(objs.uncut_ruby, weight = 8)
                item(objs.chaos_talisman, weight = 3)
                item(objs.nature_talisman, weight = 3)
                item(objs.uncut_diamond, weight = 2)
                item(objs.rune_javelin, quantity = 5, weight = 1)
                item(PirateObjs.loop_half_key, weight = 1)
                item(PirateObjs.tooth_half_key, weight = 1)
            }
        }

        val pirateNpcs: List<NpcType> = listOf(
            PirateNpcs.pirate
        )
        registry.register(pirateNpcs.distinct(), table)
    }
}

/** NPC type references for Karamja Pirate. */
internal object PirateNpcs : NpcReferences() {
    val pirate = find("pirate1")
}

/** Object type references for Karamja Pirate drops not in BaseObjs. */
internal object PirateObjs : ObjReferences() {
    val right_eye_patch = find("right_eye_patch")
    val loop_half_key = find("keyhalf1")
    val tooth_half_key = find("keyhalf2")
}
