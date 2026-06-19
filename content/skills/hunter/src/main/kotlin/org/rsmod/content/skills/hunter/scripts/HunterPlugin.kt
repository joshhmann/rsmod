package org.rsmod.content.skills.hunter.scripts

// =============================================================================
// Hunter Skill — Rev 233
//
// Phase 1: Butterfly Netting (levels 15-45)
// Phase 2: Salamanders (levels 29-67)
// Phase 3: Box Trapping (levels 27-73)
// Phase 4: Implings (levels 16-85)
// Phase 5: Bird Snare — LOC-based (levels 1-19)
// Phase 6: Deadfall Trapping — kebbits (levels 23-53)
// Phase 7: Bird Net Trapping (levels 11-39)
// Phase 8: Big Cat Box Trapping (levels 61-71)
// =============================================================================

import jakarta.inject.Inject
import kotlin.random.Random
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.hunterLvl
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.entity.Npc
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// ---------------------------------------------------------------------------
// Local NPC references
// ---------------------------------------------------------------------------
internal object HunterNpcs : NpcReferences() {
    // Butterflies
    val ruby_harvest = find("butterfly_ruby")
    val sapphire_glacialis = find("butterfly_glacialis")
    val snowy_knight = find("butterfly_snowy")
    val black_warlock = find("butterfly_warlock")

    // Salamanders
    val swamp_lizard = find("salamander_green")
    val orange_salamander = find("salamander_orange")
    val red_salamander = find("salamander_red")
    val black_salamander = find("salamander_black")

    // Box trapping
    val chinchompa = find("hunting_chinchompa")
    val red_chinchompa = find("hunting_chinchompa_big")
    val black_chinchompa = find("hunting_chinchompa_black")
    val ferret = find("hunting_ferret")

    // Implings
    val impling_1 = find("ii_impling_type_1")
    val impling_2 = find("ii_impling_type_2")
    val impling_3 = find("ii_impling_type_3")
    val impling_4 = find("ii_impling_type_4")
    val impling_5 = find("ii_impling_type_5")
    val impling_6 = find("ii_impling_type_6")
    val impling_7 = find("ii_impling_type_7")
    val impling_8 = find("ii_impling_type_8")
    val impling_9 = find("ii_impling_type_9")
    val impling_10 = find("ii_impling_type_10")
    val impling_11 = find("ii_impling_type_11")

    // Phase 6 — Deadfall creatures (kebbits)
    val spiky_kebbit = find("huntingbeast_spiky")
    val sabretooth_kebbit = find("huntingbeast_sabreteeth")
    val barbtail_kebbit = find("huntingbeast_barbedtail")
    val claw_kebbit = find("huntingbeast_claws")

    // Phase 7 — Birds (net trapping)
    val bird_woodland = find("hunting_bird_woodland")
    val bird_desert = find("hunting_bird_desert")
    val bird_polar = find("hunting_bird_polar")
    val bird_jungle = find("hunting_bird_jungle")

    // Phase 8 — Big cats (box trapping)
    val hunting_leopard = find("hunting_leopard")
    val hunting_jaguar = find("hunting_jaguar")
    val hunting_snow_tiger = find("hunting_snow_tiger")

    // Phase 9 — Falconry
    val speedy_kebbit = find("huntingbeast_speedy")
    val silent_kebbit = find("huntingbeast_silent")
    val speedy2_kebbit = find("huntingbeast_speedy2")
    val falconer = find("hunting_npc_falconer")
}

// ---------------------------------------------------------------------------
// Local LOC references (map objects)
// ---------------------------------------------------------------------------
internal object HunterLocs : LocReferences() {
    val treestump = find("treestump")
    val treestump2 = find("treestump2")
    val treestump2_green = find("treestump2_green")
    val treestump2_small = find("treestump2_small")
    val hunting_treestump = find("sotn_hunting_treestump")
}

// ---------------------------------------------------------------------------
// Local item references
// ---------------------------------------------------------------------------
internal object HunterObjs : ObjReferences() {
    // Tools
    val butterfly_net = find("hunting_butterfly_net")
    val butterfly_jar = find("butterfly_jar")
    val box_trap = find("hunting_box_trap")
    val bird_snare = find("hunting_ojibway_bird_snare")
    val small_net = find("net")
    val rope = find("rope")
    val teasing_stick = find("hunting_teasing_stick")
    val impling_jar = find("ii_impling_jar")

    // Butterfly jars (filled)
    val ruby_harvest_jar = find("butterfly_jar_ruby")
    val sapphire_glacialis_jar = find("butterfly_jar_glacialis")
    val snowy_knight_jar = find("butterfly_jar_snowy")
    val black_warlock_jar = find("butterfly_jar_warlock")

    // Salamander items (caught)
    val green_salamander = find("green_salamander")
    val orange_salamander_item = find("orange_salamander")
    val red_salamander_item = find("red_salamander")
    val black_salamander_item = find("black_salamander")

    // Box trap items (caught)
    val chinchompa_captured = find("chinchompa_captured")
    val chinchompa_big_captured = find("chinchompa_big_captured")
    val chinchompa_black = find("chinchompa_black")
    val ferret_item = find("hunting_ferret")

    // Impling jars (filled)
    val captured_impling_1 = find("ii_captured_impling_1")
    val captured_impling_2 = find("ii_captured_impling_2")
    val captured_impling_3 = find("ii_captured_impling_3")
    val captured_impling_4 = find("ii_captured_impling_4")
    val captured_impling_5 = find("ii_captured_impling_5")
    val captured_impling_6 = find("ii_captured_impling_6")
    val captured_impling_7 = find("ii_captured_impling_7")
    val captured_impling_8 = find("ii_captured_impling_8")
    val captured_impling_9 = find("ii_captured_impling_9")
    val captured_impling_10 = find("ii_captured_impling_10")
    val captured_impling_11 = find("ii_captured_impling_11")

    // Bird feathers
    val stripy_feather = find("hunting_stripy_bird_feather")
    val woodland_feather = find("hunting_woodland_feather")
    val jungle_feather = find("hunting_jungle_feather")
    val desert_feather = find("hunting_desert_feather")
    val polar_feather = find("hunting_polar_feather")

    // Phase 6 — Deadfall loot
    val kebbit_spike = find("huntingbeast_spike")
    val kebbit_bigspike = find("huntingbeast_bigspike")
    val kebbit_sabreteeth = find("huntingbeast_sabreteeth")
    val kebbit_sabreteeth_dust = find("huntingbeast_sabreteeth_dust")
    val kebbit_claws = find("huntingbeast_claws")
    val kebbit_barbed_meat = find("huntingbeast_barbed_meat")
    val kebbit_wild_meat = find("huntingbeast_wild_meat")
    val kebbit_spiky_fur = find("huntingbeast_polar_fur")
    val kebbit_sabre_fur = find("huntingbeast_jungle_fur")
    val kebbit_barbed_fur = find("huntingbeast_desert_fur")
    val kebbit_claw_fur = find("huntingbeast_woodland_fur")

    // Phase 7 — Bird loot
    val raw_bird_meat = find("spit_raw_bird_meat")

    // Phase 8 — Big cat loot
    val jaguar_fur_shabby = find("hunting_fur_jaguar_shabby")
    val jaguar_fur_perfect = find("hunting_fur_jaguar_perfect")
    val leopard_fur_shabby = find("hunting_fur_leopard_shabby")
    val leopard_fur_perfect = find("hunting_fur_leopard_perfect")
    val tiger_fur_shabby = find("hunting_fur_tiger_shabby")
    val tiger_fur_perfect = find("hunting_fur_tiger_perfect")

    // Phase 9 — Falconry items
    val falcon_gloves = find("falcon_gloves")
    val falcon_on_gloves = find("falcon_on_gloves")
    val speedy_fur = find("huntingbeast_speedy_fur")
    val speedy2_fur = find("huntingbeast_speedy2_fur")
    val silent_fur = find("huntingbeast_silent_fur")
    val speedy2_meat = find("huntingbeast_speedy2_meat")
    val wild_meat = find("huntingbeast_wild_meat")
}

// ---------------------------------------------------------------------------
// Local animation references
// ---------------------------------------------------------------------------
internal object HunterSeqs : SeqReferences() {
    val net_swing = find("human_smallnet")
    val butterfly_hover = find("butterfly_hover")
}

// =============================================================================
// Plugin
// =============================================================================

class Hunter
@Inject
constructor(
    private val xpMods: XpModifiers,
    private val objRepo: ObjRepository,
) : PluginScript() {

    override fun ScriptContext.startup() {
        // Phase 1 — Butterfly netting
        onOpNpc1(HunterNpcs.ruby_harvest) { catchButterfly(it.npc, BUTTERFLIES[0]) }
        onOpNpc1(HunterNpcs.sapphire_glacialis) { catchButterfly(it.npc, BUTTERFLIES[1]) }
        onOpNpc1(HunterNpcs.snowy_knight) { catchButterfly(it.npc, BUTTERFLIES[2]) }
        onOpNpc1(HunterNpcs.black_warlock) { catchButterfly(it.npc, BUTTERFLIES[3]) }

        // Phase 2 — Salamander net trapping
        onOpNpc1(HunterNpcs.swamp_lizard) { catchSalamander(it.npc, SALAMANDERS[0]) }
        onOpNpc1(HunterNpcs.orange_salamander) { catchSalamander(it.npc, SALAMANDERS[1]) }
        onOpNpc1(HunterNpcs.red_salamander) { catchSalamander(it.npc, SALAMANDERS[2]) }
        onOpNpc1(HunterNpcs.black_salamander) { catchSalamander(it.npc, SALAMANDERS[3]) }

        // Phase 3 — Box trapping (base)
        onOpNpc1(HunterNpcs.ferret) { catchBoxTrap(it.npc, BOX_TRAPS[0]) }
        onOpNpc1(HunterNpcs.chinchompa) { catchBoxTrap(it.npc, BOX_TRAPS[1]) }
        onOpNpc1(HunterNpcs.red_chinchompa) { catchBoxTrap(it.npc, BOX_TRAPS[2]) }
        onOpNpc1(HunterNpcs.black_chinchompa) { catchBoxTrap(it.npc, BOX_TRAPS[3]) }

        // Phase 4 — Implings
        onOpNpc1(HunterNpcs.impling_1) { catchImpling(it.npc, IMPLINGS[0]) }
        onOpNpc1(HunterNpcs.impling_2) { catchImpling(it.npc, IMPLINGS[1]) }
        onOpNpc1(HunterNpcs.impling_3) { catchImpling(it.npc, IMPLINGS[2]) }
        onOpNpc1(HunterNpcs.impling_4) { catchImpling(it.npc, IMPLINGS[3]) }
        onOpNpc1(HunterNpcs.impling_5) { catchImpling(it.npc, IMPLINGS[4]) }
        onOpNpc1(HunterNpcs.impling_6) { catchImpling(it.npc, IMPLINGS[5]) }
        onOpNpc1(HunterNpcs.impling_7) { catchImpling(it.npc, IMPLINGS[6]) }
        onOpNpc1(HunterNpcs.impling_8) { catchImpling(it.npc, IMPLINGS[7]) }
        onOpNpc1(HunterNpcs.impling_9) { catchImpling(it.npc, IMPLINGS[8]) }
        onOpNpc1(HunterNpcs.impling_10) { catchImpling(it.npc, IMPLINGS[9]) }
        onOpNpc1(HunterNpcs.impling_11) { catchImpling(it.npc, IMPLINGS[10]) }

        // Phase 5 — Bird snare (LOC-based on tree stumps)
        onOpLoc1(HunterLocs.treestump) { setBirdSnare(it.loc, BIRD_SNARES[0]) }
        onOpLoc1(HunterLocs.treestump2) { setBirdSnare(it.loc, BIRD_SNARES[1]) }
        onOpLoc1(HunterLocs.treestump2_green) { setBirdSnare(it.loc, BIRD_SNARES[2]) }
        onOpLoc1(HunterLocs.treestump2_small) { setBirdSnare(it.loc, BIRD_SNARES[3]) }
        onOpLoc1(HunterLocs.hunting_treestump) { setBirdSnare(it.loc, BIRD_SNARES[4]) }

        // Phase 6 — Deadfall trapping (kebbits)
        onOpNpc1(HunterNpcs.spiky_kebbit) { catchDeadfall(it.npc, DEADFALLS[0]) }
        onOpNpc1(HunterNpcs.sabretooth_kebbit) { catchDeadfall(it.npc, DEADFALLS[1]) }
        onOpNpc1(HunterNpcs.barbtail_kebbit) { catchDeadfall(it.npc, DEADFALLS[2]) }
        onOpNpc1(HunterNpcs.claw_kebbit) { catchDeadfall(it.npc, DEADFALLS[3]) }

        // Phase 7 — Bird net trapping
        onOpNpc1(HunterNpcs.bird_woodland) { catchBird(it.npc, BIRDS[0]) }
        onOpNpc1(HunterNpcs.bird_desert) { catchBird(it.npc, BIRDS[1]) }
        onOpNpc1(HunterNpcs.bird_polar) { catchBird(it.npc, BIRDS[2]) }
        onOpNpc1(HunterNpcs.bird_jungle) { catchBird(it.npc, BIRDS[3]) }

        // Phase 8 — Big cat box trapping
        onOpNpc1(HunterNpcs.hunting_leopard) { catchBigCat(it.npc, BIG_CATS[0]) }
        onOpNpc1(HunterNpcs.hunting_jaguar) { catchBigCat(it.npc, BIG_CATS[1]) }
        onOpNpc1(HunterNpcs.hunting_snow_tiger) { catchBigCat(it.npc, BIG_CATS[2]) }

        // Phase 9 — Falconry
        onOpNpc1(HunterNpcs.speedy_kebbit) { catchFalconry(it.npc, FALCONRIES[0]) }
        onOpNpc1(HunterNpcs.silent_kebbit) { catchFalconry(it.npc, FALCONRIES[1]) }
        onOpNpc1(HunterNpcs.speedy2_kebbit) { catchFalconry(it.npc, FALCONRIES[2]) }
    }

    // -----------------------------------------------------------------------
    // Butterfly netting
    // -----------------------------------------------------------------------
    private suspend fun ProtectedAccess.catchButterfly(npc: Npc, def: ButterflyDef) {
        if (player.hunterLvl < def.levelReq) {
            mes("You need a Hunter level of " + def.levelReq + " to catch this butterfly.")
            return
        }
        if (!inv.contains(HunterObjs.butterfly_net)) {
            mes("You need a butterfly net to catch butterflies.")
            return
        }
        if (!inv.contains(HunterObjs.butterfly_jar)) {
            mes("You need an empty butterfly jar to catch butterflies.")
            return
        }
        if (inv.isFull()) {
            mes("Your inventory is too full to hold any more butterflies.")
            return
        }

        anim(HunterSeqs.net_swing)

        val deleted = invDel(inv, HunterObjs.butterfly_jar, count = 1, strict = false)
        if (deleted.failure) {
            mes("You need an empty butterfly jar to catch butterflies.")
            return
        }

        val xp = def.xp * xpMods.get(player, stats.hunter)
        spam("You catch " + articleFor(def.name) + " " + def.name + "!")
        statAdvance(stats.hunter, xp)
        invAddOrDrop(objRepo, def.jarObj, count = 1)
    }

    private fun articleFor(name: String): String {
        val vowels = setOf('a', 'e', 'i', 'o', 'u')
        return if (name.first().lowercaseChar() in vowels) "an" else "a"
    }

    // -----------------------------------------------------------------------
    // Salamander net trapping
    // -----------------------------------------------------------------------
    private suspend fun ProtectedAccess.catchSalamander(npc: Npc, def: SalamanderDef) {
        if (player.hunterLvl < def.levelReq) {
            mes("You need a Hunter level of " + def.levelReq + " to catch this.")
            return
        }
        if (!inv.contains(HunterObjs.small_net) || !inv.contains(HunterObjs.rope)) {
            mes("You need a small fishing net and some rope to set a trap.")
            return
        }
        if (!inv.contains(HunterObjs.teasing_stick)) {
            mes("You need a teasing stick to lure the salamander.")
            return
        }
        if (inv.isFull()) {
            mes("Your inventory is too full to hold another salamander.")
            return
        }

        anim(HunterSeqs.net_swing)

        val levelDiff = player.hunterLvl - def.levelReq
        val successChance = (30 + levelDiff * 3).coerceIn(5, 95)
        val roll = Random.nextInt(100)

        if (roll >= successChance) {
            spam("You fail to catch the " + def.name + ". It slips away!")
            return
        }

        val xp = def.xp * xpMods.get(player, stats.hunter)
        spam("You catch " + articleFor(def.name) + " " + def.name + "!")
        statAdvance(stats.hunter, xp)
        invAddOrDrop(objRepo, def.caughtItem, count = 1)
    }

    // -----------------------------------------------------------------------
    // Box trapping (Chinchompas + Ferret)
    // -----------------------------------------------------------------------
    private suspend fun ProtectedAccess.catchBoxTrap(npc: Npc, def: BoxTrapDef) {
        if (player.hunterLvl < def.levelReq) {
            mes("You need a Hunter level of " + def.levelReq + " to catch this.")
            return
        }
        if (!inv.contains(HunterObjs.box_trap)) {
            mes("You need a box trap to catch this.")
            return
        }
        if (inv.isFull()) {
            mes("Your inventory is too full to hold another creature.")
            return
        }

        anim(HunterSeqs.net_swing)

        val levelDiff = player.hunterLvl - def.levelReq
        val successChance = (25 + levelDiff * 3).coerceIn(5, 95)
        val roll = Random.nextInt(100)

        if (roll >= successChance) {
            spam("You fail to catch the " + def.name + ". The trap snaps shut too late!")
            return
        }

        val xp = def.xp * xpMods.get(player, stats.hunter)
        spam("You catch " + articleFor(def.name) + " " + def.name + "!")
        statAdvance(stats.hunter, xp)
        invAddOrDrop(objRepo, def.caughtItem, count = 1)
    }

    // -----------------------------------------------------------------------
    // Impling catching
    // -----------------------------------------------------------------------
    private suspend fun ProtectedAccess.catchImpling(npc: Npc, def: ImplingDef) {
        if (player.hunterLvl < def.levelReq) {
            mes("You need a Hunter level of " + def.levelReq + " to catch this impling.")
            return
        }
        if (!inv.contains(HunterObjs.butterfly_net)) {
            mes("You need a butterfly net to catch implings.")
            return
        }
        if (!inv.contains(HunterObjs.impling_jar)) {
            mes("You need an impling jar to capture implings.")
            return
        }
        if (inv.isFull()) {
            mes("Your inventory is too full to hold another impling.")
            return
        }

        anim(HunterSeqs.net_swing)

        val deleted = invDel(inv, HunterObjs.impling_jar, count = 1, strict = false)
        if (deleted.failure) {
            mes("You need an impling jar to capture implings.")
            return
        }

        val xp = def.xp * xpMods.get(player, stats.hunter)
        spam("You catch " + articleFor(def.name) + " " + def.name + "!")
        statAdvance(stats.hunter, xp)
        invAddOrDrop(objRepo, def.jarObj, count = 1)
    }

    // -----------------------------------------------------------------------
    // Bird snare (LOC-based)
    // -----------------------------------------------------------------------
    private suspend fun ProtectedAccess.setBirdSnare(loc: BoundLocInfo, def: BirdSnareDef) {
        if (player.hunterLvl < def.levelReq) {
            mes("You need a Hunter level of " + def.levelReq + " to set a bird snare.")
            return
        }
        if (!inv.contains(HunterObjs.bird_snare)) {
            mes("You need a bird snare to set a trap.")
            return
        }
        if (inv.isFull()) {
            mes("Your inventory is too full.")
            return
        }

        anim(HunterSeqs.net_swing)

        // Success chance: base 20% + 5% per level above requirement
        val levelDiff = player.hunterLvl - def.levelReq
        val successChance = (20 + levelDiff * 5).coerceIn(5, 95)
        val roll = Random.nextInt(100)

        if (roll >= successChance) {
            spam("You set a bird snare on the tree stump, but nothing is caught.")
            return
        }

        // Small chance to consume the bird snare (like OSRS)
        val snareBreak = Random.nextInt(100)
        if (snareBreak < 10) {
            val deleted = invDel(inv, HunterObjs.bird_snare, count = 1, strict = false)
            if (!deleted.failure) {
                spam("The bird snare is damaged and breaks.")
            }
        }

        val xp = def.xp * xpMods.get(player, stats.hunter)
        spam("You catch " + articleFor(def.name) + " and get " + def.featherCount + " " + def.featherName + ".")
        statAdvance(stats.hunter, xp)
        invAddOrDrop(objRepo, def.featherObj, count = def.featherCount)
    }

    // -----------------------------------------------------------------------
    // Deadfall trapping (kebbits)
    // -----------------------------------------------------------------------
    private suspend fun ProtectedAccess.catchDeadfall(npc: Npc, def: DeadfallDef) {
        if (player.hunterLvl < def.levelReq) {
            mes("You need a Hunter level of " + def.levelReq + " to catch this.")
            return
        }
        if (inv.isFull()) {
            mes("Your inventory is too full.")
            return
        }

        anim(HunterSeqs.net_swing)

        val levelDiff = player.hunterLvl - def.levelReq
        val successChance = (30 + levelDiff * 3).coerceIn(5, 95)
        val roll = Random.nextInt(100)

        if (roll >= successChance) {
            spam("You fail to catch the " + def.name + ". The deadfall trap snaps shut too late!")
            return
        }

        val xp = def.xp * xpMods.get(player, stats.hunter)
        spam("You catch " + articleFor(def.name) + " " + def.name + "!")
        statAdvance(stats.hunter, xp)

        // Award primary loot
        invAddOrDrop(objRepo, def.primaryLoot, count = 1)

        // Optionally award secondary loot (meat for some, fur for others)
        if (def.secondaryLoot != null && Random.nextInt(100) < 60) {
            invAddOrDrop(objRepo, def.secondaryLoot, count = 1)
        }
    }

    // -----------------------------------------------------------------------
    // Bird net trapping
    // -----------------------------------------------------------------------
    private suspend fun ProtectedAccess.catchBird(npc: Npc, def: BirdDef) {
        if (player.hunterLvl < def.levelReq) {
            mes("You need a Hunter level of " + def.levelReq + " to catch this.")
            return
        }
        if (!inv.contains(HunterObjs.small_net) || !inv.contains(HunterObjs.rope)) {
            mes("You need a small fishing net and some rope to set a trap.")
            return
        }
        if (!inv.contains(HunterObjs.teasing_stick)) {
            mes("You need a teasing stick to lure the bird.")
            return
        }
        if (inv.isFull()) {
            mes("Your inventory is too full.")
            return
        }

        anim(HunterSeqs.net_swing)

        val levelDiff = player.hunterLvl - def.levelReq
        val successChance = (30 + levelDiff * 3).coerceIn(5, 95)
        val roll = Random.nextInt(100)

        if (roll >= successChance) {
            spam("The bird escapes! You reset your net.")
            return
        }

        val xp = def.xp * xpMods.get(player, stats.hunter)
        spam("You catch " + articleFor(def.name) + " " + def.name + "!")
        statAdvance(stats.hunter, xp)

        // Feathers + chance of raw bird meat
        invAddOrDrop(objRepo, def.featherObj, count = 1)
        if (Random.nextInt(100) < 40) {
            invAddOrDrop(objRepo, HunterObjs.raw_bird_meat, count = 1)
        }
    }

    // -----------------------------------------------------------------------
    // Big cat box trapping
    // -----------------------------------------------------------------------
    private suspend fun ProtectedAccess.catchBigCat(npc: Npc, def: BigCatDef) {
        if (player.hunterLvl < def.levelReq) {
            mes("You need a Hunter level of " + def.levelReq + " to catch this.")
            return
        }
        if (!inv.contains(HunterObjs.box_trap)) {
            mes("You need a box trap to catch this.")
            return
        }
        if (inv.isFull()) {
            mes("Your inventory is too full.")
            return
        }

        anim(HunterSeqs.net_swing)

        val levelDiff = player.hunterLvl - def.levelReq
        val successChance = (20 + levelDiff * 3).coerceIn(5, 95)
        val roll = Random.nextInt(100)

        if (roll >= successChance) {
            spam("The " + def.name + " escapes from the box trap!")
            return
        }

        val xp = def.xp * xpMods.get(player, stats.hunter)
        spam("You catch " + articleFor(def.name) + " " + def.name + "!")
        statAdvance(stats.hunter, xp)

        // Award shabby fur (always) + chance of perfect fur
        invAddOrDrop(objRepo, def.shabbyFur, count = 1)
        if (Random.nextInt(100) < 25) {
            invAddOrDrop(objRepo, def.perfectFur, count = 1)
            spam("You manage to obtain a perfect " + def.name + " fur!")
        }
    }

    // -----------------------------------------------------------------------
    // Falconry
    // -----------------------------------------------------------------------
    private suspend fun ProtectedAccess.catchFalconry(npc: Npc, def: FalconryDef) {
        if (player.hunterLvl < def.levelReq) {
            mes("You need a Hunter level of " + def.levelReq + " to hunt this.")
            return
        }
        if (!inv.contains(HunterObjs.falcon_gloves)) {
            mes("You need a falcon to hunt this kebbit.")
            return
        }
        if (inv.isFull()) {
            mes("Your inventory is too full.")
            return
        }

        anim(HunterSeqs.net_swing)

        val levelDiff = player.hunterLvl - def.levelReq
        val successChance = (25 + levelDiff * 3).coerceIn(5, 95)
        val roll = Random.nextInt(100)

        if (roll >= successChance) {
            spam("The " + def.name + " evades your falcon!")
            return
        }

        val xp = def.xp * xpMods.get(player, stats.hunter)
        spam("Your falcon catches " + articleFor(def.name) + " " + def.name + "!")
        statAdvance(stats.hunter, xp)

        // Fur (always) + chance of meat
        invAddOrDrop(objRepo, def.furObj, count = 1)
        if (def.meatObj != null && Random.nextInt(100) < 40) {
            invAddOrDrop(objRepo, def.meatObj, count = 1)
        }
    }

    // -----------------------------------------------------------------------
    // Data
    // -----------------------------------------------------------------------
    private data class ButterflyDef(
        val levelReq: Int,
        val xp: Double,
        val jarObj: ObjType,
        val name: String,
    )

    private data class SalamanderDef(
        val levelReq: Int,
        val xp: Double,
        val caughtItem: ObjType,
        val name: String,
    )

    private data class BoxTrapDef(
        val levelReq: Int,
        val xp: Double,
        val caughtItem: ObjType,
        val name: String,
    )

    private data class ImplingDef(
        val levelReq: Int,
        val xp: Double,
        val jarObj: ObjType,
        val name: String,
    )

    private data class BirdSnareDef(
        val levelReq: Int,
        val xp: Double,
        val featherObj: ObjType,
        val featherCount: Int,
        val featherName: String,
        val name: String,
    )

    private data class DeadfallDef(
        val levelReq: Int,
        val xp: Double,
        val primaryLoot: ObjType,
        val secondaryLoot: ObjType?,
        val name: String,
    )

    private data class BirdDef(
        val levelReq: Int,
        val xp: Double,
        val featherObj: ObjType,
        val name: String,
    )

    private data class BigCatDef(
        val levelReq: Int,
        val xp: Double,
        val shabbyFur: ObjType,
        val perfectFur: ObjType,
        val name: String,
    )

    private data class FalconryDef(
        val levelReq: Int,
        val xp: Double,
        val furObj: ObjType,
        val meatObj: ObjType?,
        val name: String,
    )

    companion object {
        private val BUTTERFLIES = listOf(
            ButterflyDef(15, 24.0, HunterObjs.ruby_harvest_jar, "ruby harvest"),
            ButterflyDef(25, 34.0, HunterObjs.sapphire_glacialis_jar, "sapphire glacialis"),
            ButterflyDef(35, 44.0, HunterObjs.snowy_knight_jar, "snowy knight"),
            ButterflyDef(45, 54.0, HunterObjs.black_warlock_jar, "black warlock"),
        )

        private val SALAMANDERS = listOf(
            SalamanderDef(29, 152.0, HunterObjs.green_salamander, "swamp lizard"),
            SalamanderDef(47, 224.0, HunterObjs.orange_salamander_item, "orange salamander"),
            SalamanderDef(59, 272.0, HunterObjs.red_salamander_item, "red salamander"),
            SalamanderDef(67, 316.0, HunterObjs.black_salamander_item, "black salamander"),
        )

        private val BOX_TRAPS = listOf(
            BoxTrapDef(27, 115.0, HunterObjs.ferret_item, "ferret"),
            BoxTrapDef(53, 198.0, HunterObjs.chinchompa_captured, "chinchompa"),
            BoxTrapDef(63, 264.0, HunterObjs.chinchompa_big_captured, "red chinchompa"),
            BoxTrapDef(73, 315.0, HunterObjs.chinchompa_black, "black chinchompa"),
        )

        private val IMPLINGS = listOf(
            ImplingDef(16, 40.0, HunterObjs.captured_impling_1, "baby impling"),
            ImplingDef(17, 60.0, HunterObjs.captured_impling_2, "young impling"),
            ImplingDef(22, 80.0, HunterObjs.captured_impling_3, "gourmet impling"),
            ImplingDef(28, 100.0, HunterObjs.captured_impling_4, "earth impling"),
            ImplingDef(36, 120.0, HunterObjs.captured_impling_5, "essence impling"),
            ImplingDef(42, 140.0, HunterObjs.captured_impling_6, "eclectic impling"),
            ImplingDef(50, 160.0, HunterObjs.captured_impling_7, "nature impling"),
            ImplingDef(58, 180.0, HunterObjs.captured_impling_8, "magpie impling"),
            ImplingDef(65, 200.0, HunterObjs.captured_impling_9, "ninja impling"),
            ImplingDef(74, 220.0, HunterObjs.captured_impling_10, "dragon impling"),
            ImplingDef(85, 240.0, HunterObjs.captured_impling_11, "crystal impling"),
        )

        private val BIRD_SNARES = listOf(
            BirdSnareDef(1, 34.0, HunterObjs.stripy_feather, 1, "stripy feathers", "a crimson swift"),
            BirdSnareDef(1, 34.0, HunterObjs.stripy_feather, 1, "stripy feathers", "a crimson swift"),
            BirdSnareDef(5, 48.0, HunterObjs.woodland_feather, 1, "woodland feathers", "a golden warbler"),
            BirdSnareDef(9, 61.0, HunterObjs.jungle_feather, 1, "jungle feathers", "a copper longtail"),
            BirdSnareDef(19, 95.0, HunterObjs.desert_feather, 1, "desert feathers", "a tropical wagtail"),
        )

        // Phase 6 — Deadfall kebbits (levels 23-57)
        private val DEADFALLS = listOf(
            DeadfallDef(23, 132.0, HunterObjs.kebbit_spike, null, "spiky kebbit"),
            DeadfallDef(33, 166.0, HunterObjs.kebbit_sabreteeth, HunterObjs.kebbit_sabreteeth_dust, "sabre-toothed kebbit"),
            DeadfallDef(43, 195.0, HunterObjs.kebbit_barbed_meat, HunterObjs.kebbit_barbed_fur, "barb-tailed kebbit"),
            DeadfallDef(53, 230.0, HunterObjs.kebbit_claws, HunterObjs.kebbit_claw_fur, "claw kebbit"),
        )

        // Phase 7 — Birds (levels 11-39)
        private val BIRDS = listOf(
            BirdDef(11, 78.0, HunterObjs.woodland_feather, "woodland bird"),
            BirdDef(19, 95.0, HunterObjs.desert_feather, "desert bird"),
            BirdDef(29, 130.0, HunterObjs.polar_feather, "polar bird"),
            BirdDef(39, 170.0, HunterObjs.jungle_feather, "jungle bird"),
        )

        // Phase 8 — Big cats (levels 61-71)
        private val BIG_CATS = listOf(
            BigCatDef(61, 280.0, HunterObjs.leopard_fur_shabby, HunterObjs.leopard_fur_perfect, "leopard"),
            BigCatDef(63, 290.0, HunterObjs.jaguar_fur_shabby, HunterObjs.jaguar_fur_perfect, "jaguar"),
            BigCatDef(71, 335.0, HunterObjs.tiger_fur_shabby, HunterObjs.tiger_fur_perfect, "snow tiger"),
        )

        // Phase 9 — Falconry (levels 55-79)
        private val FALCONRIES = listOf(
            FalconryDef(55, 244.0, HunterObjs.speedy_fur, null, "dashing kebbit"),
            FalconryDef(65, 300.0, HunterObjs.silent_fur, HunterObjs.wild_meat, "dark kebbit"),
            FalconryDef(79, 400.0, HunterObjs.speedy2_fur, HunterObjs.speedy2_meat, "dashing kebbit (elite)"),
        )
    }
}
