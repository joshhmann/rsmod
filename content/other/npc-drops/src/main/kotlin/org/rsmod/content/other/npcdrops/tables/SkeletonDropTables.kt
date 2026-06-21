package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Skeleton NPCs.
 *
 * Data sources:
 * - Primary: drops_by_source.json (OSRS wiki corpus, 76 filtered items)
 * - Secondary: https://oldschool.runescape.wiki/w/Skeleton
 *
 * Conditional drops filtered:
 * - Looting bag (Wilderness-only)
 * - Clue scrolls (post-2013)
 * - Key halves, shield left half (RDT — separate handling)
 * - Larran's key, Slayer's enchantment (post-2013)
 * - Skeleton champion scroll (minigame)
 */
internal object SkeletonDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        val table = dropTable {
            // Guaranteed
            always(objs.bones)

            // Weapons & Armour (weight 40)
            table("Weapons/Armour", weight = 40) {
                item(objs.bronze_arrow, quantity = 2, weight = 7)
                item(objs.bronze_arrow, quantity = 5, weight = 4)
                item(objs.bronze_arrow, quantity = 8, weight = 2)
                item(objs.iron_arrow, quantity = 2, weight = 5)
                item(objs.iron_arrow, quantity = 5, weight = 3)
                item(objs.steel_arrow, quantity = 1..2, weight = 3)
                item(objs.bronze_axe, weight = 1)
                item(objs.bronze_longsword, weight = 1)
                item(DropTableObjs.iron_dagger, weight = 1)
                item(objs.bronze_med_helm, weight = 1)
                item(DropTableObjs.bronze_bolts, quantity = 2..7, weight = 4)
            }

            // Runes (weight 25)
            table("Runes", weight = 25) {
                item(objs.chaosrune, quantity = 2, weight = 3)
                item(objs.chaosrune, quantity = 5, weight = 1)
                item(objs.naturerune, quantity = 2, weight = 2)
                item(objs.naturerune, quantity = 4, weight = 1)
                item(objs.mindrune, quantity = 9, weight = 1)
                item(objs.bodyrune, quantity = 3, weight = 3)
                item(objs.bodyrune, quantity = 6, weight = 2)
                item(objs.airrune, quantity = 9, weight = 2)
                item(objs.waterrune, quantity = 4, weight = 1)
                item(objs.firerune, quantity = 3..6, weight = 2)
                item(objs.earthrune, quantity = 4, weight = 1)
                item(objs.lawrune, weight = 1)
            }

            // Coins (weight 15)
            table("Coins", weight = 15) {
                item(objs.coins, quantity = 6, weight = 6)
                item(objs.coins, quantity = 12, weight = 6)
                item(objs.coins, quantity = 26, weight = 3)
                item(objs.coins, quantity = 56, weight = 1)
            }

            // Herbs (weight 5)
            table("Herbs", weight = 5) {
                item(SkeletonObjs.grimy_guam, weight = 20)
                item(SkeletonObjs.grimy_tarromin, weight = 15)
                item(SkeletonObjs.grimy_harralander, weight = 12)
                item(SkeletonObjs.grimy_ranarr, weight = 9)
                item(SkeletonObjs.grimy_irit, weight = 7)
                item(SkeletonObjs.grimy_avantoe, weight = 5)
                item(SkeletonObjs.grimy_kwuarm, weight = 4)
                item(SkeletonObjs.grimy_cadantine, weight = 3)
                item(SkeletonObjs.grimy_lantadyme, weight = 2)
                item(SkeletonObjs.grimy_dwarf_weed, weight = 2)
            }

            // Other (weight 10)
            table("Other", weight = 10) {
                item(objs.hammer, weight = 8)
                item(objs.fishing_bait, quantity = 1..3, weight = 5)
                item(objs.copper_ore, weight = 2)
                item(objs.tin_ore, weight = 1)
            }
        }

        // Register all skeleton variants
        val skeletonNpcs = listOf(
            SkeletonNpcs.skeleton_unarmed, SkeletonNpcs.skeleton_unarmed2,
            SkeletonNpcs.skeleton_unarmed3, SkeletonNpcs.skeleton_unarmed4,
            SkeletonNpcs.skeleton_unagressive, SkeletonNpcs.skeleton_unagressive2,
            SkeletonNpcs.skeleton_unagressive3,
            SkeletonNpcs.skeleton_armed, SkeletonNpcs.skeleton_armed2,
            SkeletonNpcs.skeleton_armed3, SkeletonNpcs.skeleton_armed4,
            SkeletonNpcs.skeleton_armed5,
        )
        registry.register(skeletonNpcs.distinct(), table)
    }
}

internal object SkeletonNpcs : NpcReferences() {
    val skeleton_unarmed = find("skeleton_unarmed")
    val skeleton_unarmed2 = find("skeleton_unarmed2")
    val skeleton_unarmed3 = find("skeleton_unarmed3")
    val skeleton_unarmed4 = find("skeleton_unarmed4")
    val skeleton_unagressive = find("skeleton_unagressive")
    val skeleton_unagressive2 = find("skeleton_unagressive2")
    val skeleton_unagressive3 = find("skeleton_unagressive3")
    val skeleton_armed = find("skeleton_armed")
    val skeleton_armed2 = find("skeleton_armed2")
    val skeleton_armed3 = find("skeleton_armed3")
    val skeleton_armed4 = find("skeleton_armed4")
    val skeleton_armed5 = find("skeleton_armed5")
}

internal object SkeletonObjs : ObjReferences() {
    val grimy_guam = find("unidentified_guam")
    val grimy_tarromin = find("unidentified_tarromin")
    val grimy_harralander = find("unidentified_harralander")
    val grimy_ranarr = find("unidentified_ranarr")
    val grimy_irit = find("unidentified_irit")
    val grimy_avantoe = find("unidentified_avantoe")
    val grimy_kwuarm = find("unidentified_kwuarm")
    val grimy_cadantine = find("unidentified_cadantine")
    val grimy_lantadyme = find("unidentified_lantadyme")
    val grimy_dwarf_weed = find("unidentified_dwarf_weed")
}
