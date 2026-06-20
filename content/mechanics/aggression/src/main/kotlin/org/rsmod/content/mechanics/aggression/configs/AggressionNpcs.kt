@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.mechanics.aggression.configs

import org.rsmod.api.config.refs.huntmodes
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.game.type.npc.NpcType

typealias aggression_npcs = AggressionNpcs

object AggressionNpcs : NpcReferences() {
    // Lumbridge + Edgeville goblin sets.
    val goblin_armed = find("goblin_armed")
    val goblin_unarmed_melee_1 = find("goblin_unarmed_melee_1")
    val goblin_unarmed_melee_2 = find("goblin_unarmed_melee_2")
    val goblin_unarmed_melee_3 = find("goblin_unarmed_melee_3")
    val goblin_unarmed_melee_4 = find("goblin_unarmed_melee_4")
    val goblin_unarmed_melee_5 = find("goblin_unarmed_melee_5")
    val goblin_unarmed_melee_6 = find("goblin_unarmed_melee_6")
    val goblin_unarmed_melee_7 = find("goblin_unarmed_melee_7")
    val goblin_unarmed_melee_8 = find("goblin_unarmed_melee_8")

    // Common F2P aggressive NPCs.
    val guard1 = find("guard1")
    val bearded_dark_wizard = find("bearded_dark_wizard")
    val young_dark_wizard = find("young_dark_wizard")
    val black_knight = find("black_knight")
    val aggressive_black_knight = find("aggressive_black_knight")

    // Skeletons — aggressive in dungeons
    val skeleton_unarmed = find("skeleton_unarmed")
    val skeleton_unarmed2 = find("skeleton_unarmed2")
    val skeleton_unarmed3 = find("skeleton_unarmed3")
    val skeleton_unarmed4 = find("skeleton_unarmed4")
    val skeleton_armed = find("skeleton_armed")
    val skeleton_armed2 = find("skeleton_armed2")
    val skeleton_armed3 = find("skeleton_armed3")
    val skeleton_armed4 = find("skeleton_armed4")
    val skeleton_armed5 = find("skeleton_armed5")
    val giantskeleton = find("giantskeleton")
    val giantskeleton2 = find("giantskeleton2")
    val skeletonmage = find("skeletonmage")

    // Zombies — aggressive in sewers/dungeons
    val zombie_unarmed = find("zombie_unarmed")
    val zombie_unarmed2 = find("zombie_unarmed2")
    val zombie_unarmed3 = find("zombie_unarmed3")
    val zombie_unarmed4 = find("zombie_unarmed4")
    val zombie_unarmed5 = find("zombie_unarmed5")
    val zombie_unarmed6 = find("zombie_unarmed6")
    val zombie_armed = find("zombie_armed")
    val zombie_armed2 = find("zombie_armed2")
    val zombie_armed3 = find("zombie_armed3")
    val zombie2 = find("zombie2")
    val zombie2_b = find("zombie2_b")
    val zombie2_c = find("zombie2_c")

    // Wolves — aggressive wilderness NPCs
    val wolf = find("wolf")
    val whitewolf = find("whitewolf")
    val whitewolf_sentry = find("whitewolf_sentry")
    val wolfpack_leader = find("wolfpack_leader")
    val pack_wolf = find("pack_wolf")
    val pack_wolf2 = find("pack_wolf2")
    val pack_wolf3 = find("pack_wolf3")

    // Demons — all variants are aggressive
    val lesser_demon = find("lesser_demon")
    val lesser_demon2 = find("lesser_demon2")
    val lesser_demon3 = find("lesser_demon3")
    val lesser_demon4 = find("lesser_demon4")
    val lesser_demon5 = find("lesser_demon5")
    val greater_demon = find("greater_demon")
    val greater_demon2 = find("greater_demon2")
    val greater_demon3 = find("greater_demon3")
    val greater_demon4 = find("greater_demon4")
    val greater_demon5 = find("greater_demon5")
    val black_demon = find("black_demon")
    val black_demon2 = find("black_demon2")
    val black_demon3 = find("black_demon3")
    val black_demon4 = find("black_demon4")
    val black_demon5 = find("black_demon5")
    val hellhound = find("hellhound")

    // Giants — all variants are aggressive
    val giant = find("giant")
    val giant2 = find("giant2")
    val giant3 = find("giant3")
    val giant4 = find("giant4")
    val giant5 = find("giant5")
    val giant6 = find("giant6")
    val wilderness_hill_giant = find("wilderness_hill_giant")
    val wilderness_hill_giant2 = find("wilderness_hill_giant2")
    val wilderness_hill_giant3 = find("wilderness_hill_giant3")
    val mossgiant = find("mossgiant")
    val mossgiant2 = find("mossgiant2")
    val mossgiant3 = find("mossgiant3")
    val mossgiant4 = find("mossgiant4")
    val roving_mossgiant = find("roving_mossgiant")
    val firegiant = find("firegiant")
    val firegiant2 = find("firegiant2")
    val firegiant3 = find("firegiant3")
    val firegiant_big = find("firegiant_big")
    val firegiant_big2 = find("firegiant_big2")
    val firegiant_big3 = find("firegiant_big3")
    val icegiant = find("icegiant")
    val icegiant2 = find("icegiant2")
    val icegiant3 = find("icegiant3")
    val icegiant_low_wanderrange = find("icegiant_low_wanderrange")
    val icegiant_low_wanderrange2 = find("icegiant_low_wanderrange2")

    // Bears — aggressive
    val brownbear = find("brownbear")
    val darkbear = find("darkbear")

    // Hobgoblins — aggressive
    val hobgoblin_unarmed = find("hobgoblin_unarmed")
    val hobgoblin_armed = find("hobgoblin_armed")

    // Barbarians — aggressive to low levels
    val barbarian = find("barbarian")
}

object AggressionNpcEditor : NpcEditor() {
    init {
        val goblins =
            setOf(
                aggression_npcs.goblin_armed,
                aggression_npcs.goblin_unarmed_melee_1,
                aggression_npcs.goblin_unarmed_melee_2,
                aggression_npcs.goblin_unarmed_melee_3,
                aggression_npcs.goblin_unarmed_melee_4,
                aggression_npcs.goblin_unarmed_melee_5,
                aggression_npcs.goblin_unarmed_melee_6,
                aggression_npcs.goblin_unarmed_melee_7,
                aggression_npcs.goblin_unarmed_melee_8,
            )
        goblins.forEach(::aggressiveMelee)

        val f2pAggressiveHumanoids =
            setOf(
                aggression_npcs.guard1,
                aggression_npcs.bearded_dark_wizard,
                aggression_npcs.young_dark_wizard,
                aggression_npcs.black_knight,
                aggression_npcs.aggressive_black_knight,
            )
        f2pAggressiveHumanoids.forEach(::aggressiveMelee)

        val skeletons =
            setOf(
                aggression_npcs.skeleton_unarmed,
                aggression_npcs.skeleton_unarmed2,
                aggression_npcs.skeleton_unarmed3,
                aggression_npcs.skeleton_unarmed4,
                aggression_npcs.skeleton_armed,
                aggression_npcs.skeleton_armed2,
                aggression_npcs.skeleton_armed3,
                aggression_npcs.skeleton_armed4,
                aggression_npcs.skeleton_armed5,
                aggression_npcs.giantskeleton,
                aggression_npcs.giantskeleton2,
                aggression_npcs.skeletonmage,
            )
        skeletons.forEach(::aggressiveMelee)

        val zombies =
            setOf(
                aggression_npcs.zombie_unarmed,
                aggression_npcs.zombie_unarmed2,
                aggression_npcs.zombie_unarmed3,
                aggression_npcs.zombie_unarmed4,
                aggression_npcs.zombie_unarmed5,
                aggression_npcs.zombie_unarmed6,
                aggression_npcs.zombie_armed,
                aggression_npcs.zombie_armed2,
                aggression_npcs.zombie_armed3,
                aggression_npcs.zombie2,
                aggression_npcs.zombie2_b,
                aggression_npcs.zombie2_c,
            )
        zombies.forEach(::aggressiveMelee)

        val wolves =
            setOf(
                aggression_npcs.wolf,
                aggression_npcs.whitewolf,
                aggression_npcs.whitewolf_sentry,
                aggression_npcs.wolfpack_leader,
                aggression_npcs.pack_wolf,
                aggression_npcs.pack_wolf2,
                aggression_npcs.pack_wolf3,
            )
        wolves.forEach(::aggressiveMelee)

        val demons =
            setOf(
                aggression_npcs.lesser_demon,
                aggression_npcs.lesser_demon2,
                aggression_npcs.lesser_demon3,
                aggression_npcs.lesser_demon4,
                aggression_npcs.lesser_demon5,
                aggression_npcs.greater_demon,
                aggression_npcs.greater_demon2,
                aggression_npcs.greater_demon3,
                aggression_npcs.greater_demon4,
                aggression_npcs.greater_demon5,
                aggression_npcs.black_demon,
                aggression_npcs.black_demon2,
                aggression_npcs.black_demon3,
                aggression_npcs.black_demon4,
                aggression_npcs.black_demon5,
                aggression_npcs.hellhound,
            )
        demons.forEach(::aggressiveMelee)

        val giants =
            setOf(
                aggression_npcs.giant,
                aggression_npcs.giant2,
                aggression_npcs.giant3,
                aggression_npcs.giant4,
                aggression_npcs.giant5,
                aggression_npcs.giant6,
                aggression_npcs.wilderness_hill_giant,
                aggression_npcs.wilderness_hill_giant2,
                aggression_npcs.wilderness_hill_giant3,
                aggression_npcs.mossgiant,
                aggression_npcs.mossgiant2,
                aggression_npcs.mossgiant3,
                aggression_npcs.mossgiant4,
                aggression_npcs.roving_mossgiant,
                aggression_npcs.firegiant,
                aggression_npcs.firegiant2,
                aggression_npcs.firegiant3,
                aggression_npcs.firegiant_big,
                aggression_npcs.firegiant_big2,
                aggression_npcs.firegiant_big3,
                aggression_npcs.icegiant,
                aggression_npcs.icegiant2,
                aggression_npcs.icegiant3,
                aggression_npcs.icegiant_low_wanderrange,
                aggression_npcs.icegiant_low_wanderrange2,
            )
        giants.forEach(::aggressiveMelee)

        val bears =
            setOf(
                aggression_npcs.brownbear,
                aggression_npcs.darkbear,
            )
        bears.forEach(::aggressiveMelee)

        val hobgoblins =
            setOf(
                aggression_npcs.hobgoblin_unarmed,
                aggression_npcs.hobgoblin_armed,
            )
        hobgoblins.forEach(::aggressiveMelee)

        val barbarians =
            setOf(
                aggression_npcs.barbarian,
            )
        barbarians.forEach(::aggressiveMelee)
    }

    private fun aggressiveMelee(type: NpcType) {
        edit(type) {
            huntMode = huntmodes.aggressive_melee
            huntRange = 5
        }
    }
}
