package org.rsmod.content.other.npccombat

import jakarta.inject.Inject
import org.rsmod.api.script.onNpcHit
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Aggressive NPC combat handlers. Ported from Kronos-184 JSON data.
 *
 * This script registers onNpcHit handlers for aggressive combat NPCs
 * across Gielinor. The actual combat behavior (retaliation, aggression,
 * accuracy, max hit) is handled by the combat engine using the params
 * set in npcs.toml within this module.
 *
 * Data source: Kronos-184-Fixed/.../data/npcs/combat/
 * All NPC refs use rev 233 cache symbols (NOT Kronos numeric IDs).
 */
class AggressiveNpcCombatScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        registerGuards()
        registerSkeletons()
        registerZombies()
        registerGhosts()
        registerWolves()
        registerDemons()
        registerGiants()
        registerBears()
        registerDragons()
        registerScorpions()
        registerSpiders()
        registerBats()
        registerRats()
        registerHobgoblins()
    }

    // =========================================================================
    // GUARDS — Level 22, aggressive in cities
    // =========================================================================
    private fun ScriptContext.registerGuards() {
        val npcs = listOf(
            AggressiveCombatNpcs.guard1,
            AggressiveCombatNpcs.guard1_variant01,
            AggressiveCombatNpcs.guard1_f,
            AggressiveCombatNpcs.guard1_f_variant01,
        )
        npcs.forEach { onNpcHit(it) { /* Combat handled by engine via npcs.toml */ } }
    }

    // =========================================================================
    // SKELETONS — Level 13-25, aggressive in dungeons
    // =========================================================================
    private fun ScriptContext.registerSkeletons() {
        val unarmed = listOf(
            AggressiveCombatNpcs.skeleton_unarmed,
            AggressiveCombatNpcs.skeleton_unarmed2,
            AggressiveCombatNpcs.skeleton_unarmed3,
            AggressiveCombatNpcs.skeleton_unarmed4,
        )
        val armed = listOf(
            AggressiveCombatNpcs.skeleton_armed,
            AggressiveCombatNpcs.skeleton_armed2,
            AggressiveCombatNpcs.skeleton_armed3,
            AggressiveCombatNpcs.skeleton_armed4,
            AggressiveCombatNpcs.skeleton_armed5,
        )
        val special = listOf(
            AggressiveCombatNpcs.giantskeleton,
            AggressiveCombatNpcs.giantskeleton2,
            AggressiveCombatNpcs.skeletonmage,
            AggressiveCombatNpcs.draynor_skeleton,
        )
        (unarmed + armed + special).forEach { onNpcHit(it) { /* Combat handled by engine */ } }
    }

    // =========================================================================
    // ZOMBIES — Level 13-25, aggressive in sewers/dungeons
    // =========================================================================
    private fun ScriptContext.registerZombies() {
        val npcs = listOf(
            AggressiveCombatNpcs.zombie_unarmed,
            AggressiveCombatNpcs.zombie_unarmed2,
            AggressiveCombatNpcs.zombie_unarmed3,
            AggressiveCombatNpcs.zombie_unarmed4,
            AggressiveCombatNpcs.zombie_unarmed5,
            AggressiveCombatNpcs.zombie_unarmed6,
            AggressiveCombatNpcs.zombie_armed,
            AggressiveCombatNpcs.zombie_armed2,
            AggressiveCombatNpcs.zombie_armed3,
            AggressiveCombatNpcs.zombie2,
            AggressiveCombatNpcs.zombie2_b,
            AggressiveCombatNpcs.zombie2_c,
        )
        npcs.forEach { onNpcHit(it) { /* Combat handled by engine */ } }
    }

    // =========================================================================
    // GHOSTS — Level 13-19, found in various dungeons
    // =========================================================================
    private fun ScriptContext.registerGhosts() {
        val npcs = listOf(
            AggressiveCombatNpcs.ghost,
            AggressiveCombatNpcs.ghost2,
            AggressiveCombatNpcs.ghost3,
            AggressiveCombatNpcs.ghost4,
            AggressiveCombatNpcs.ghost5,
            AggressiveCombatNpcs.ghost6,
            AggressiveCombatNpcs.ghost7,
            AggressiveCombatNpcs.ghost8,
        )
        npcs.forEach { onNpcHit(it) { /* Combat handled by engine */ } }
    }

    // =========================================================================
    // WOLVES — Level 11-38, aggressive
    // =========================================================================
    private fun ScriptContext.registerWolves() {
        val npcs = listOf(
            AggressiveCombatNpcs.wolf,
            AggressiveCombatNpcs.whitewolf,
            AggressiveCombatNpcs.whitewolf_sentry,
            AggressiveCombatNpcs.wolfpack_leader,
            AggressiveCombatNpcs.pack_wolf,
            AggressiveCombatNpcs.pack_wolf2,
            AggressiveCombatNpcs.pack_wolf3,
            AggressiveCombatNpcs.guarddog,
        )
        npcs.forEach { onNpcHit(it) { /* Combat handled by engine */ } }
    }

    // =========================================================================
    // DEMONS — Lesser (82), Greater (92), Black (172)
    // =========================================================================
    private fun ScriptContext.registerDemons() {
        val lesser = listOf(
            AggressiveCombatNpcs.lesser_demon,
            AggressiveCombatNpcs.lesser_demon2,
            AggressiveCombatNpcs.lesser_demon3,
            AggressiveCombatNpcs.lesser_demon4,
            AggressiveCombatNpcs.lesser_demon5,
        )
        val greater = listOf(
            AggressiveCombatNpcs.greater_demon,
            AggressiveCombatNpcs.greater_demon2,
            AggressiveCombatNpcs.greater_demon3,
            AggressiveCombatNpcs.greater_demon4,
            AggressiveCombatNpcs.greater_demon5,
        )
        val black = listOf(
            AggressiveCombatNpcs.black_demon,
            AggressiveCombatNpcs.black_demon2,
            AggressiveCombatNpcs.black_demon3,
            AggressiveCombatNpcs.black_demon4,
            AggressiveCombatNpcs.black_demon5,
        )
        (lesser + greater + black + listOf(AggressiveCombatNpcs.hellhound))
            .forEach { onNpcHit(it) { /* Combat handled by engine */ } }
    }

    // =========================================================================
    // GIANTS — Hill (28), Moss (42), Fire (86), Ice (53)
    // =========================================================================
    private fun ScriptContext.registerGiants() {
        val hill = listOf(
            AggressiveCombatNpcs.giant,
            AggressiveCombatNpcs.giant2,
            AggressiveCombatNpcs.giant3,
            AggressiveCombatNpcs.giant4,
            AggressiveCombatNpcs.giant5,
            AggressiveCombatNpcs.giant6,
            AggressiveCombatNpcs.wilderness_hill_giant,
            AggressiveCombatNpcs.wilderness_hill_giant2,
            AggressiveCombatNpcs.wilderness_hill_giant3,
        )
        val moss = listOf(
            AggressiveCombatNpcs.mossgiant,
            AggressiveCombatNpcs.mossgiant2,
            AggressiveCombatNpcs.mossgiant3,
            AggressiveCombatNpcs.mossgiant4,
            AggressiveCombatNpcs.roving_mossgiant,
        )
        val fire = listOf(
            AggressiveCombatNpcs.firegiant,
            AggressiveCombatNpcs.firegiant2,
            AggressiveCombatNpcs.firegiant3,
            AggressiveCombatNpcs.firegiant_big,
            AggressiveCombatNpcs.firegiant_big2,
            AggressiveCombatNpcs.firegiant_big3,
        )
        val ice = listOf(
            AggressiveCombatNpcs.icegiant,
            AggressiveCombatNpcs.icegiant2,
            AggressiveCombatNpcs.icegiant3,
            AggressiveCombatNpcs.icegiant_low_wanderrange,
            AggressiveCombatNpcs.icegiant_low_wanderrange2,
        )
        (hill + moss + fire + ice).forEach { onNpcHit(it) { /* Combat handled by engine */ } }
    }

    // =========================================================================
    // BEARS — Level 19-21, aggressive
    // =========================================================================
    private fun ScriptContext.registerBears() {
        val npcs = listOf(
            AggressiveCombatNpcs.brownbear,
            AggressiveCombatNpcs.darkbear,
            AggressiveCombatNpcs.brownbear_cub_1,
            AggressiveCombatNpcs.brownbear_cub_2,
        )
        npcs.forEach { onNpcHit(it) { /* Combat handled by engine */ } }
    }

    // =========================================================================
    // DRAGONS — Green, Blue, Red, Black + babies
    // =========================================================================
    private fun ScriptContext.registerDragons() {
        val adults = listOf(
            AggressiveCombatNpcs.green_dragon,
            AggressiveCombatNpcs.blue_dragon,
            AggressiveCombatNpcs.red_dragon,
            AggressiveCombatNpcs.black_dragon,
        )
        val babies = listOf(
            AggressiveCombatNpcs.babybluedragon,
            AggressiveCombatNpcs.babyreddragon,
        )
        (adults + babies).forEach { onNpcHit(it) { /* Combat handled by engine */ } }
    }

    // =========================================================================
    // SCORPIONS — Level 7-14
    // =========================================================================
    private fun ScriptContext.registerScorpions() {
        val npcs = listOf(
            AggressiveCombatNpcs.scorpion,
            AggressiveCombatNpcs.poison_scorpion,
            AggressiveCombatNpcs.kingscorpion,
        )
        npcs.forEach { onNpcHit(it) { /* Combat handled by engine */ } }
    }

    // =========================================================================
    // SPIDERS — Level 1-8
    // =========================================================================
    private fun ScriptContext.registerSpiders() {
        val npcs = listOf(
            AggressiveCombatNpcs.spider,
            AggressiveCombatNpcs.giantspider1,
            AggressiveCombatNpcs.giantspider2,
            AggressiveCombatNpcs.deadly_red_spider,
            AggressiveCombatNpcs.jungle_spider,
            AggressiveCombatNpcs.ice_spider,
            AggressiveCombatNpcs.shadow_spider,
            AggressiveCombatNpcs.poisonspider,
        )
        npcs.forEach { onNpcHit(it) { /* Combat handled by engine */ } }
    }

    // =========================================================================
    // BATS — Level 1-6
    // =========================================================================
    private fun ScriptContext.registerBats() {
        onNpcHit(AggressiveCombatNpcs.giant_bat) { /* Combat handled by engine */ }
    }

    // =========================================================================
    // RATS — Level 1-3
    // =========================================================================
    private fun ScriptContext.registerRats() {
        val npcs = listOf(
            AggressiveCombatNpcs.rat,
            AggressiveCombatNpcs.giantrat,
            AggressiveCombatNpcs.giantrat2,
            AggressiveCombatNpcs.giantrat3,
        )
        npcs.forEach { onNpcHit(it) { /* Combat handled by engine */ } }
    }

    // =========================================================================
    // HOBGOBLINS — Level 21-27
    // =========================================================================
    private fun ScriptContext.registerHobgoblins() {
        val npcs = listOf(
            AggressiveCombatNpcs.hobgoblin_unarmed,
            AggressiveCombatNpcs.hobgoblin_armed,
            AggressiveCombatNpcs.rimmington_hobgoblin_unarmed_1,
            AggressiveCombatNpcs.rimmington_hobgoblin_unarmed_2,
            AggressiveCombatNpcs.rimmington_hobgoblin_unarmed_3,
            AggressiveCombatNpcs.rimmington_hobgoblin_armed_1,
        )
        npcs.forEach { onNpcHit(it) { /* Combat handled by engine */ } }
    }
}

/**
 * NPC type references for aggressive NPC combat types.
 * All names are rev 233 cache symbols (NOT Kronos numeric IDs).
 */
internal object AggressiveCombatNpcs : NpcReferences() {

    // GUARDS
    val guard1 = find("guard1")
    val guard1_variant01 = find("guard1_variant01")
    val guard1_f = find("guard1_f")
    val guard1_f_variant01 = find("guard1_f_variant01")

    // SKELETONS
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
    val draynor_skeleton = find("draynor_skeleton")

    // ZOMBIES
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

    // GHOSTS
    val ghost = find("ghost")
    val ghost2 = find("ghost2")
    val ghost3 = find("ghost3")
    val ghost4 = find("ghost4")
    val ghost5 = find("ghost5")
    val ghost6 = find("ghost6")
    val ghost7 = find("ghost7")
    val ghost8 = find("ghost8")

    // WOLVES
    val wolf = find("wolf")
    val whitewolf = find("whitewolf")
    val whitewolf_sentry = find("whitewolf_sentry")
    val wolfpack_leader = find("wolfpack_leader")
    val pack_wolf = find("pack_wolf")
    val pack_wolf2 = find("pack_wolf2")
    val pack_wolf3 = find("pack_wolf3")
    val guarddog = find("guarddog")

    // DEMONS
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

    // GIANTS
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

    // BEARS
    val brownbear = find("brownbear")
    val darkbear = find("darkbear")
    val brownbear_cub_1 = find("brownbear_cub_1")
    val brownbear_cub_2 = find("brownbear_cub_2")

    // DRAGONS
    val green_dragon = find("green_dragon")
    val blue_dragon = find("blue_dragon")
    val red_dragon = find("red_dragon")
    val black_dragon = find("black_dragon")
    val babybluedragon = find("babybluedragon")
    val babyreddragon = find("babyreddragon")

    // SCORPIONS
    val scorpion = find("scorpion")
    val poison_scorpion = find("poison_scorpion")
    val kingscorpion = find("kingscorpion")

    // SPIDERS
    val spider = find("spider")
    val giantspider1 = find("giantspider1")
    val giantspider2 = find("giantspider2")
    val deadly_red_spider = find("deadly_red_spider")
    val jungle_spider = find("jungle_spider")
    val ice_spider = find("ice_spider")
    val shadow_spider = find("shadow_spider")
    val poisonspider = find("poisonspider")

    // BATS
    val giant_bat = find("giant_bat")

    // RATS
    val rat = find("rat")
    val giantrat = find("giantrat")
    val giantrat2 = find("giantrat2")
    val giantrat3 = find("giantrat3")

    // HOBGOBLINS
    val hobgoblin_unarmed = find("hobgoblin_unarmed")
    val hobgoblin_armed = find("hobgoblin_armed")
    val rimmington_hobgoblin_unarmed_1 = find("rimmington_hobgoblin_unarmed_1")
    val rimmington_hobgoblin_unarmed_2 = find("rimmington_hobgoblin_unarmed_2")
    val rimmington_hobgoblin_unarmed_3 = find("rimmington_hobgoblin_unarmed_3")
    val rimmington_hobgoblin_armed_1 = find("rimmington_hobgoblin_armed_1")
}
