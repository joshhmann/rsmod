package org.rsmod.content.skills.prayer.scripts

// IMPLEMENTATION NOTES:
// Bone burying and altar offering for Prayer XP.
//
// Burying:
//   - onOpHeld2(bone) — option 2 is "Bury" for all bone items.
//   - Plays human_pickupfloor (seq 827) — the canonical OSRS bury animation.
//   - 1-tick delay then XP, matching wiki timing.
//   - Message sequence matches RS: dig-hole message then bury message.
//
// Altar offering:
//   - onOpLocU(altar, bone) — item-on-altar triggers this.
//   - x2 XP multiplier for standard altars (chapels/monasteries).
//   - x3.5 XP multiplier for POH altars (tiers 4-7 have incense burners active in OSRS;
//     we apply 3.5x unconditionally as a simplification since POH incense-burner state
//     is not yet implemented). Wiki-accurate base is 3.5x with both burners lit.
//
// Bones included: all variants confirmed present in obj.sym (rev 228 cache).
//   Missing from cache (not yet in sym): shaikahan_bones, fayrg_bones, raurg_bones as
//   plain non-prefixed names — the cache has them prefixed as zogre_ancestral_bones_fayg /
//   zogre_ancestral_bones_raurg. Those are mapped to their wiki XP values.
//
// Altar locs included: standard world altars (x2) + POH altars (x3.5).
//   Confirmed in loc.sym. chaos_altar (loc 411) is a world altar handled as x2.
//   POH altars use the poh_altar_* naming convention.
//
// Engine gaps (local refs):
//   - human_bone_sacrifice (seq 3705): altar offering anim — not yet in BaseSeqs.kt.
//   - All bone ObjTypes except objs.bones and objs.big_bones live in local PrayerObjs.
//   - objs.bones and objs.big_bones are already in BaseObjs.kt — used directly.
//   - All altar LocTypes: local PrayerLocs (no BaseLocs.kt exists yet).

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpHeld2
import org.rsmod.api.script.onOpLocU
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// ---------------------------------------------------------------------------
// Local type reference objects
// ---------------------------------------------------------------------------

private typealias prayer_seqs = PrayerSeqs

internal object PrayerSeqs : SeqReferences() {
    // Altar bone-offering animation — seq 3705 in sym: "human_bone_sacrifice".
    // Not yet promoted to BaseSeqs.kt.
    val human_bone_sacrifice = find("human_bone_sacrifice")
}

private typealias prayer_objs = PrayerObjs

internal object PrayerObjs : ObjReferences() {
    // Standard / common bones
    // NOTE: objs.bones is in BaseObjs; objs.big_bones is also in BaseObjs.
    //       Only the remaining types not yet promoted to BaseObjs are declared here.
    val burnt_bones = find("bones_burnt")
    val bat_bones = find("bat_bones")
    val wolf_bones = find("wolf_bones")
    val babydragon_bones = find("babydragon_bones")
    val dragon_bones = find("dragon_bones")
    val wyvern_bones = find("wyvern_bones")
    val dagannoth_bones = find("dagannoth_king_bones")
    val superior_dragon_bones = find("dragon_bones_superior")

    // Jogre / Zogre / Tribal family
    val jogre_bones = find("tbwt_jogre_bones")
    val zogre_bones = find("zogre_bones")

    // Ourg / ancestral family (named in cache with zogre_ancestral_ prefix)
    val fayrg_bones = find("zogre_ancestral_bones_fayg")
    val raurg_bones = find("zogre_ancestral_bones_raurg")
    val ourg_bones = find("zogre_ancestral_bones_ourg")

    // Monkey bones — five variants share the same 5.0 XP
    val monkey_bones_1 = find("mm_small_ninja_monkey_bones")
    val monkey_bones_2 = find("mm_medium_ninja_monkey_bones")
    val monkey_bones_3 = find("mm_normal_gorilla_monkey_bones")
    val monkey_bones_4 = find("mm_bearded_gorilla_monkey_bones")
    val monkey_bones_5 = find("mm_normal_monkey_bones")
    val monkey_bones_6 = find("mm_small_zombie_monkey_bones")
    val monkey_bones_7 = find("mm_large_zombie_monkey_bones")

    // Wyrm / Drake / Hydra (Dragon Slayer II area bones)
    val wyrm_bones = find("wyrm_bones")
    val drake_bones = find("drake_bones")
    val hydra_bones = find("hydra_bones")

    // Lava dragon bones (lava maze)
    val lava_dragon_bones = find("lava_dragon_bones")
}

private typealias prayer_locs = PrayerLocs

internal object PrayerLocs : LocReferences() {
    // -----------------------------------------------------------------------
    // Standard world altars — x2 XP multiplier
    // -----------------------------------------------------------------------
    val altar = find("altar") // Lumbridge chapel, generic altar (409)
    val monks_altar = find("monks_altar") // Edgeville / monastery (2640)
    val chaos_altar = find("chaosaltar") // Chaos temple wilderness (411)
    val hosidius_altar = find("hosidius_altar") // Hosidius church (27501)
    val fai_varrock_church_altar = find("fai_varrock_church_altar") // Varrock church (14860)
    val slp_church_altar = find("slp_church_altar") // Port Sarim church (32630)
    val cata_altar = find("cata_altar") // Catacombs of Kourend (28900)
    val guthix_altar = find("guthix_altar") // Guthix altar (410)

    // -----------------------------------------------------------------------
    // POH (Player-Owned House) altars — x3.5 XP multiplier
    // All seven tiers of Saradomin/Zamorak/Guthix altars.
    // -----------------------------------------------------------------------
    val poh_altar_saradomin_1 = find("poh_altar_saradomin_1")
    val poh_altar_zamorak_1 = find("poh_altar_zamorak_1")
    val poh_altar_guthix_1 = find("poh_altar_guthix_1")
    val poh_altar_saradomin_2 = find("poh_altar_saradomin_2")
    val poh_altar_zamorak_2 = find("poh_altar_zamorak_2")
    val poh_altar_guthix_2 = find("poh_altar_guthix_2")
    val poh_altar_saradomin_3 = find("poh_altar_saradomin_3")
    val poh_altar_zamorak_3 = find("poh_altar_zamorak_3")
    val poh_altar_guthix_3 = find("poh_altar_guthix_3")
    val poh_altar_saradomin_4 = find("poh_altar_saradomin_4")
    val poh_altar_zamorak_4 = find("poh_altar_zamorak_4")
    val poh_altar_guthix_4 = find("poh_altar_guthix_4")
    val poh_altar_saradomin_5 = find("poh_altar_saradomin_5")
    val poh_altar_zamorak_5 = find("poh_altar_zamorak_5")
    val poh_altar_guthix_5 = find("poh_altar_guthix_5")
    val poh_altar_saradomin_6 = find("poh_altar_saradomin_6")
    val poh_altar_zamorak_6 = find("poh_altar_zamorak_6")
    val poh_altar_guthix_6 = find("poh_altar_guthix_6")
    val poh_altar_saradomin_7 = find("poh_altar_saradomin_7")
    val poh_altar_zamorak_7 = find("poh_altar_zamorak_7")
    val poh_altar_guthix_7 = find("poh_altar_guthix_7")

    // Occult altars (POH, post-Ancient Altar update)
    val poh_altar_ancient = find("poh_altar_ancient")
    val poh_altar_lunar = find("poh_altar_lunar")
    val poh_altar_dark = find("poh_altar_dark")
    val poh_altar_occult = find("poh_altar_occult")
    val poh_altar_occult_standard = find("poh_altar_occult_standard")
    val poh_altar_occult_ancient = find("poh_altar_occult_ancient")
    val poh_altar_occult_lunar = find("poh_altar_occult_lunar")
    val poh_altar_occult_arceuus = find("poh_altar_occult_arceuus")
}

// ---------------------------------------------------------------------------
// Data model
// ---------------------------------------------------------------------------

/** Associates a bone [ObjType] with its wiki-accurate XP value. */
private data class BoneDef(val obj: ObjType, val xp: Double)

/**
 * All bones with their per-bury XP values, ordered as per the OSRS wiki.
 *
 * Sources:
 * - Kronos Bone.java (validated against OSRS wiki)
 * - Wiki Prayer training page (accessed 2026-02)
 */
private val BONE_DEFS: List<BoneDef> by lazy {
    listOf(
        // Basic bones — 4.5 XP each
        BoneDef(objs.bones, 4.5),
        BoneDef(prayer_objs.burnt_bones, 4.5),
        BoneDef(prayer_objs.bat_bones, 4.5),
        BoneDef(prayer_objs.wolf_bones, 4.5),

        // Standard bones — 15 XP
        // objs.big_bones is in BaseObjs — use it directly rather than local ref
        BoneDef(objs.big_bones, 15.0),
        BoneDef(prayer_objs.jogre_bones, 15.0),

        // Mid-tier bones
        BoneDef(prayer_objs.zogre_bones, 22.5),
        BoneDef(prayer_objs.wyrm_bones, 30.0), // wiki: 30 XP
        BoneDef(prayer_objs.babydragon_bones, 30.0),

        // Monkey bones — 5 XP each
        BoneDef(prayer_objs.monkey_bones_1, 5.0),
        BoneDef(prayer_objs.monkey_bones_2, 5.0),
        BoneDef(prayer_objs.monkey_bones_3, 5.0),
        BoneDef(prayer_objs.monkey_bones_4, 5.0),
        BoneDef(prayer_objs.monkey_bones_5, 5.0),
        BoneDef(prayer_objs.monkey_bones_6, 5.0),
        BoneDef(prayer_objs.monkey_bones_7, 5.0),

        // Dragon-tier bones
        BoneDef(prayer_objs.drake_bones, 60.0), // wiki: 60 XP
        BoneDef(prayer_objs.dragon_bones, 72.0),
        BoneDef(prayer_objs.wyvern_bones, 72.0),
        BoneDef(prayer_objs.lava_dragon_bones, 85.0),

        // Ancestral (Fayrg / Raurg / Ourg) — wiki accurate
        BoneDef(prayer_objs.fayrg_bones, 84.0),
        BoneDef(prayer_objs.raurg_bones, 96.0),
        BoneDef(prayer_objs.hydra_bones, 110.0), // wiki: 110 XP
        BoneDef(prayer_objs.dagannoth_bones, 125.0),
        BoneDef(prayer_objs.ourg_bones, 140.0),
        BoneDef(prayer_objs.superior_dragon_bones, 150.0),
    )
}

/** Standard world altars grant x2 base XP. */
private const val STANDARD_ALTAR_MULTIPLIER = 2.0

/** POH altars grant x3.5 base XP (assumes incense burners lit — wiki accurate). */
private const val POH_ALTAR_MULTIPLIER = 3.5

// ---------------------------------------------------------------------------
// Plugin
// ---------------------------------------------------------------------------

class PrayerTraining @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        for (bone in BONE_DEFS) {
            // Bury handler — op2 = "Bury" on a bone in inventory
            onOpHeld2(bone.obj) { buryBone(bone) }

            // Standard world altar offerings
            for (altarLoc in STANDARD_WORLD_ALTARS) {
                onOpLocU(altarLoc, bone.obj) { offerBoneAtAltar(bone, STANDARD_ALTAR_MULTIPLIER) }
            }

            // POH altar offerings
            for (altarLoc in POH_ALTARS) {
                onOpLocU(altarLoc, bone.obj) { offerBoneAtAltar(bone, POH_ALTAR_MULTIPLIER) }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Burying
    // -----------------------------------------------------------------------

    private suspend fun ProtectedAccess.buryBone(bone: BoneDef) {
        // Remove bone first (consume from inventory before animation)
        val deleted = invDel(inv, bone.obj)
        if (deleted.failure) {
            return
        }

        // "You dig a hole in the ground..." message plays before the animation completes
        mes("You dig a hole in the ground...")

        // Bury animation: human_pickupfloor (seq 827) — already in BaseSeqs
        anim(seqs.human_pickupfloor)

        // Wait 1 tick (matches OSRS tick timing for burying)
        delay(1)

        // Grant XP and send final message
        statAdvance(stats.prayer, bone.xp)
        mes("You bury the bones.")
    }

    // -----------------------------------------------------------------------
    // Altar offering
    // -----------------------------------------------------------------------

    private suspend fun ProtectedAccess.offerBoneAtAltar(bone: BoneDef, multiplier: Double) {
        val deleted = invDel(inv, bone.obj)
        if (deleted.failure) {
            return
        }

        // Altar offering animation: human_bone_sacrifice (seq 3705) — local ref
        anim(prayer_seqs.human_bone_sacrifice)

        // Wait 3 ticks (altar offering is slower than burying)
        delay(3)

        statAdvance(stats.prayer, bone.xp * multiplier)
        mes("The gods are pleased with your offering.")
    }
}

// ---------------------------------------------------------------------------
// Altar loc collections (used in startup registration loop)
// ---------------------------------------------------------------------------

private val STANDARD_WORLD_ALTARS: List<LocType> by lazy {
    listOf(
        prayer_locs.altar,
        prayer_locs.monks_altar,
        prayer_locs.chaos_altar,
        prayer_locs.hosidius_altar,
        prayer_locs.fai_varrock_church_altar,
        prayer_locs.slp_church_altar,
        prayer_locs.cata_altar,
        prayer_locs.guthix_altar,
    )
}

private val POH_ALTARS: List<LocType> by lazy {
    listOf(
        prayer_locs.poh_altar_saradomin_1,
        prayer_locs.poh_altar_zamorak_1,
        prayer_locs.poh_altar_guthix_1,
        prayer_locs.poh_altar_saradomin_2,
        prayer_locs.poh_altar_zamorak_2,
        prayer_locs.poh_altar_guthix_2,
        prayer_locs.poh_altar_saradomin_3,
        prayer_locs.poh_altar_zamorak_3,
        prayer_locs.poh_altar_guthix_3,
        prayer_locs.poh_altar_saradomin_4,
        prayer_locs.poh_altar_zamorak_4,
        prayer_locs.poh_altar_guthix_4,
        prayer_locs.poh_altar_saradomin_5,
        prayer_locs.poh_altar_zamorak_5,
        prayer_locs.poh_altar_guthix_5,
        prayer_locs.poh_altar_saradomin_6,
        prayer_locs.poh_altar_zamorak_6,
        prayer_locs.poh_altar_guthix_6,
        prayer_locs.poh_altar_saradomin_7,
        prayer_locs.poh_altar_zamorak_7,
        prayer_locs.poh_altar_guthix_7,
        prayer_locs.poh_altar_ancient,
        prayer_locs.poh_altar_lunar,
        prayer_locs.poh_altar_dark,
        prayer_locs.poh_altar_occult,
        prayer_locs.poh_altar_occult_standard,
        prayer_locs.poh_altar_occult_ancient,
        prayer_locs.poh_altar_occult_lunar,
        prayer_locs.poh_altar_occult_arceuus,
    )
}
