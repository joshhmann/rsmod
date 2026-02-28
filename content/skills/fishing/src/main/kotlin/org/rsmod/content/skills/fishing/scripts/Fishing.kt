package org.rsmod.content.skills.fishing.scripts

// IMPLEMENTATION NOTES:
// Fishing spots in OSRS are NPC entities, not locs. The tick loop mirrors Woodcutting's
// actionDelay / skillAnimDelay pattern but re-queues via opNpc1 instead of opLoc3.
//
// Engine gaps (no base-ref exists yet — see ENGINE_GAPS.md):
//   OBJECTS missing from BaseObjs.kt:
//     - small_fishing_net    (sym: "small_fishing_net",  OSRS item ID 303)
//     - big_fishing_net      (sym: "big_fishing_net",    OSRS item ID 305)
//     - fishing_rod          (sym: "fishing_rod",        OSRS item ID 307)
//     - fly_fishing_rod      (sym: "fly_fishing_rod",    OSRS item ID 309)
//     - barbarian_rod        (sym: "barbarian_rod",      OSRS item ID 11323)
//     - lobster_pot          (sym: "lobster_pot",        OSRS item ID 301)
//     - harpoon              (sym: "harpoon",            OSRS item ID 311)
//     - oily_fishing_rod     (sym: "oily_fishing_rod",   OSRS item ID 1585)
//     - dark_crab_pot        (sym: "dark_crab_pot",      OSRS item ID 11940)
//     - angler_rod           (sym: "angler_rod",         OSRS item ID 307 — same as fishing_rod?
// verify)
//     - fishing_bait         (sym: "fishing_bait",       OSRS item ID 313)
//     - feather              (sym: "feather",            OSRS item ID 314)
//     - raw_shrimps          (sym: "raw_shrimps",        OSRS item ID 317)
//     - raw_anchovies        (sym: "raw_anchovies",      OSRS item ID 321)
//     - raw_sardine          (sym: "raw_sardine",        OSRS item ID 327)
//     - raw_herring          (sym: "raw_herring",        already in BaseObjs? check — sym:
// "raw_herring")
//     - raw_mackerel         (sym: "raw_mackerel",       OSRS item ID 353)
//     - raw_trout            (sym: "raw_trout",          OSRS item ID 335)
//     - raw_cod              (sym: "raw_cod",            OSRS item ID 341)
//     - raw_pike             (sym: "raw_pike",           OSRS item ID 349)
//     - raw_salmon           (sym: "raw_salmon",         OSRS item ID 331)
//     - raw_tuna             (sym: "raw_tuna",           OSRS item ID 359)
//     - raw_lobster          (sym: "raw_lobster",        OSRS item ID 377)
//     - raw_bass             (sym: "raw_bass",           OSRS item ID 363)
//     - raw_swordfish        (sym: "raw_swordfish",      OSRS item ID 371)
//     - raw_monkfish         (sym: "raw_monkfish",       OSRS item ID 7944)
//     - raw_shark            (sym: "raw_shark",          OSRS item ID 383)
//     - raw_anglerfish       (sym: "raw_anglerfish",     OSRS item ID 13439)
//     - raw_dark_crab        (sym: "raw_dark_crab",      OSRS item ID 11934)
//     - leaping_trout        (sym: "leaping_trout",      OSRS item ID 11328)
//     - leaping_salmon       (sym: "leaping_salmon",     OSRS item ID 11330)
//     - leaping_sturgeon     (sym: "leaping_sturgeon",   OSRS item ID 11332)
//   SEQUENCES missing from BaseSeqs.kt:
//     - human_fishing_net    (sym: "human_fishing_net",  OSRS anim ID 621)
//     - human_fishing_bignet (sym: "human_fishing_bignet",OSRS anim ID 620)
//     - human_fishing_bait   (sym: "human_fishing_bait", OSRS anim ID 622 start / 623 loop)
//     - human_fishing_fly    (sym: "human_fishing_fly",  OSRS anim ID 622 start / 623 loop)
//     - human_fishing_cage   (sym: "human_fishing_cage", OSRS anim ID 619)
//     - human_fishing_harpoon(sym: "human_fishing_harpoon",OSRS anim ID 618)
//   NPCs missing from BaseNpcs.kt (only 2 spots currently registered):
//     - fishing_spot_net_bait    NPC 1518  (shrimps/anchovies + sardine/herring)
//     - fishing_spot_lure_bait   NPC 1506  (trout/salmon + pike)
//     - fishing_spot_cage_harpoon NPC 1519 (lobster + tuna/swordfish)
//     - fishing_spot_big_net     NPC 1520  (mackerel/cod/bass + shark)
//     - fishing_spot_monkfish    NPC 4316  (monkfish + swordfish)
//     - fishing_spot_barb_rod    NPC 1542  (leaping trout/salmon/sturgeon)
//     - fishing_spot_dark_crab   NPC 1535  (dark crab)
//     - fishing_spot_anglerfish  NPC 6825  (anglerfish)
//   CONTENT group missing from BaseContent.kt:
//     - fishing_spot (to cover all fishing NPC variants at once)
//
// All local object, sequence, and NPC references below use find() with the expected sym-name
// so they will auto-resolve when the cache symbol tables are present. Promote them to the
// appropriate Base*.kt files once confirmed working.
//
// Wiki-accurate XP / level data:  https://oldschool.runescape.wiki/w/Fishing
// Kronos source cross-checked for bait IDs and animation IDs.
//
// TODO (future versions):
//   - Minnows spot (NPC 7731) — requires special logic (flying fish)
//   - Infernal eel (NPC 7676) — requires ice gloves check
//   - Barbarian fishing bonus (Agility + Strength XP)
//   - Karambwan vessel (NPC 4712)
//   - Anglerfish outfit XP bonus
//   - Dragon / infernal / crystal harpoon special: +20% catch rate boost
//   - Fishing spot despawn/move events

import jakarta.inject.Inject
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.fishingLvl
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpc2
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Npc
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// ---------------------------------------------------------------------------
// Local object references — promote to BaseObjs.kt once confirmed
// ---------------------------------------------------------------------------
internal object FishingObjs : ObjReferences() {
    // Tools
    val small_fishing_net = find("net")
    val big_fishing_net = find("big_net")
    val fishing_rod = find("fishing_rod")
    val fly_fishing_rod = find("fly_fishing_rod")
    val barbarian_rod = find("brut_fishing_rod")
    val lobster_pot = find("lobster_pot")
    val harpoon = find("harpoon")
    val oily_fishing_rod = find("oily_fishing_rod")

    // Bait / secondary consumables
    val fishing_bait = find("fishing_bait")
    val feather = find("feather")

    // Catch items
    val raw_shrimps = find("raw_shrimp")
    val raw_anchovies = find("raw_anchovies")
    val raw_sardine = find("raw_sardine")
    val raw_herring = find("raw_herring")
    val raw_mackerel = find("raw_mackerel")
    val raw_trout = find("raw_trout")
    val raw_cod = find("raw_cod")
    val raw_pike = find("raw_pike")
    val raw_salmon = find("raw_salmon")
    val raw_tuna = find("raw_tuna")
    val raw_lobster = find("raw_lobster")
    val raw_bass = find("raw_bass")
    val raw_swordfish = find("raw_swordfish")
    val raw_monkfish = find("raw_monkfish")
    val raw_shark = find("raw_shark")
    val raw_anglerfish = find("raw_anglerfish")
}

// ---------------------------------------------------------------------------
// Local sequence references — promote to BaseSeqs.kt once confirmed
// ---------------------------------------------------------------------------
internal object FishingSeqs : SeqReferences() {
    // Anims taken from Kronos FishingTool enum:
    //   SMALL_FISHING_NET(303, 621)
    //   BIG_FISHING_NET(305, 620)
    //   FISHING_ROD(307, 313, 622, 623)   — 622 start, 623 loop
    //   FLY_FISHING_ROD(309, 314, 622, 623)
    //   BARBARIAN_ROD(11323, 314, 622, 623)
    //   LOBSTER_POT(301, 619)
    //   HARPOON(311, 618)
    //   OILY_FISHING_ROD(1585, 313, 622, 623)
    //   DARK_CRAB_POT(301, 11940, 619)
    // Rev 233 canonical internal names (seq.sym):
    // 621 -> human_smallnet, 620 -> human_largenet, 622/623 -> human_fishing_casting/human_fish_onspot,
    // 619 -> human_lobster, 618 -> human_harpoon.
    val human_fishing_net = find("human_smallnet") // anim 621
    val human_fishing_bignet = find("human_largenet") // anim 620
    val human_fishing_bait = find("human_fish_onspot") // anim 623 (rod loop)
    val human_fishing_cage = find("human_lobster") // anim 619
    val human_fishing_harpoon = find("human_harpoon") // anim 618
}

// ---------------------------------------------------------------------------
// Local NPC references — promote to BaseNpcs.kt once confirmed
// NPC IDs from Kronos FishingSpot.java static constants.
// ---------------------------------------------------------------------------
internal object FishingNpcs : NpcReferences() {
    // NPC 1518: options "small net" (shrimps/anchovies) and "bait" (sardine/herring)
    val net_bait_spot = find("0_50_50_freshfish") // freshwater: net + bait
    // NPC 1506: options "lure" (trout/salmon) and "bait" (pike)
    val lure_bait_spot = find("0_50_50_freshfish") // lure/bait river spot
    // NPC 1519: options "cage" (lobster) and "harpoon" (tuna/swordfish)
    val cage_harpoon_spot = find("0_50_49_saltfish") // saltfish: cage + harpoon
    // NPC 1520: options "big net" (mackerel/cod/bass) and "harpoon" (shark)
    val big_net_harpoon_spot = find("0_50_49_saltfish") // big net + harpoon
    // NPC 4316: options "net" (monkfish) and "harpoon" (swordfish)  — Piscatoris only
    val monkfish_spot = find("0_50_49_saltfish")
    // NPC 1542: option "use-rod" (leaping trout/salmon/sturgeon) — Barbarian fishing
    val barb_rod_spot = find("0_50_50_freshfish")
    // NPC 1535: option "cage" (dark crab) — Wilderness Resource Area
    val dark_crab_spot = find("0_50_49_saltfish")
    // NPC 6825: option "bait" (anglerfish) — Piscarilius
    val anglerfish_spot = find("0_50_49_saltfish")
}

// ---------------------------------------------------------------------------
// Data model
// ---------------------------------------------------------------------------

/**
 * Defines what bait (if any) a tool consumes per catch. [baitItem] is null when the tool needs no
 * consumable (net, lobster pot, harpoon, cage).
 */
data class FishingTool(
    val obj: ObjType,
    val anim: SeqType,
    val baitItem: ObjType? = null,
    val toolName: String,
    val baitName: String? = null,
)

/**
 * A single catchable fish definition.
 *
 * [successLow] / [successHigh] map to the `statRandom(stat, low, high, invisibleLevels)` API.
 * Values are OSRS wiki tick-rate approximations scaled to the [0..255] integer range used by
 * RSMod's SkillingSuccessRate formula: rate = low + (high - low) * (level / maxLevel).
 *
 * Baseline values are cross-referenced from the OSRS wiki catch-rate mechanics article.
 *
 * TODO: fine-tune these once the true OSRS rate tables are reverse-engineered or published.
 */
data class FishCatch(
    val obj: ObjType,
    val levelReq: Int,
    val xp: Double,
    val successLow: Int,
    val successHigh: Int,
    val name: String,
)

/**
 * Pairs a [FishingTool] with the list of fish it can catch at a given spot. The list is ordered
 * lowest-level first; the highest-level fish the player qualifies for is rolled first (descending),
 * matching OSRS behaviour.
 */
data class SpotAction(val tool: FishingTool, val catches: List<FishCatch>)

// ---------------------------------------------------------------------------
// Plugin
// ---------------------------------------------------------------------------

class Fishing
@Inject
constructor(
    private val xpMods: XpModifiers,
    private val invisibleLvls: InvisibleLevels,
    private val mapClock: MapClock,
    private val objRepo: ObjRepository,
) : PluginScript() {

    override fun ScriptContext.startup() {
        // -------------------------------------------------------------------
        // Net / Bait spot (NPC ~1518 "freshfish")
        // Op1 = "small net"  → shrimps + anchovies
        // Op2 = "bait"       → sardine + herring
        // -------------------------------------------------------------------
        onOpNpc1(FishingNpcs.net_bait_spot) { attemptFish(it.npc, NET_ACTION, opSlot = 1) }
        onOpNpc2(FishingNpcs.net_bait_spot) {
            attemptFish(it.npc, BAIT_FRESHWATER_ACTION, opSlot = 2)
        }

        // -------------------------------------------------------------------
        // Lure / Bait spot (NPC ~1506 "lurefish")
        // Op1 = "lure"  → trout + salmon
        // Op2 = "bait"  → pike
        // -------------------------------------------------------------------
        onOpNpc1(FishingNpcs.lure_bait_spot) { attemptFish(it.npc, LURE_ACTION, opSlot = 1) }
        onOpNpc2(FishingNpcs.lure_bait_spot) { attemptFish(it.npc, BAIT_PIKE_ACTION, opSlot = 2) }

        // -------------------------------------------------------------------
        // Cage / Harpoon spot (NPC ~1519 "saltfish")
        // Op1 = "cage"    → lobster
        // Op2 = "harpoon" → tuna + swordfish
        // -------------------------------------------------------------------
        onOpNpc1(FishingNpcs.cage_harpoon_spot) { attemptFish(it.npc, CAGE_ACTION, opSlot = 1) }
        onOpNpc2(FishingNpcs.cage_harpoon_spot) {
            attemptFish(it.npc, HARPOON_TUNA_ACTION, opSlot = 2)
        }

        // -------------------------------------------------------------------
        // Big Net / Harpoon spot (NPC ~1520 "bignet")
        // Op1 = "big net" → mackerel + cod + bass
        // Op2 = "harpoon" → shark
        // -------------------------------------------------------------------
        onOpNpc1(FishingNpcs.big_net_harpoon_spot) {
            attemptFish(it.npc, BIG_NET_ACTION, opSlot = 1)
        }
        onOpNpc2(FishingNpcs.big_net_harpoon_spot) {
            attemptFish(it.npc, HARPOON_SHARK_ACTION, opSlot = 2)
        }

        // -------------------------------------------------------------------
        // Monkfish spot (NPC ~4316)
        // Op1 = "net"     → monkfish
        // Op2 = "harpoon" → swordfish
        // -------------------------------------------------------------------
        onOpNpc1(FishingNpcs.monkfish_spot) { attemptFish(it.npc, MONKFISH_NET_ACTION, opSlot = 1) }
        onOpNpc2(FishingNpcs.monkfish_spot) {
            attemptFish(it.npc, HARPOON_SWORDFISH_ACTION, opSlot = 2)
        }

        // -------------------------------------------------------------------
        // Anglerfish spot (NPC ~6825)
        // Op1 = "bait" → anglerfish
        // -------------------------------------------------------------------
        onOpNpc1(FishingNpcs.anglerfish_spot) { attemptFish(it.npc, ANGLERFISH_ACTION, opSlot = 1) }
    }

    // -----------------------------------------------------------------------
    // Core tick loop
    // -----------------------------------------------------------------------

    /**
     * Entry point called every time the player op-interacts with a fishing spot NPC.
     *
     * [opSlot] must be the same op number (1 or 2) that triggered this call so the re-queue
     * dispatches back to the correct handler (e.g. "bait" on op2 must re-queue via opNpc2, not
     * opNpc1).
     *
     * Pattern mirrors Woodcutting:
     * - First call (actionDelay < mapClock): set the initial 3-tick delay and play the opening
     *   animation. Re-queue via the same op.
     * - Subsequent calls (actionDelay == mapClock): perform the success roll.
     * - Always re-queue unless an early-exit condition fires.
     */
    private suspend fun ProtectedAccess.attemptFish(npc: Npc, action: SpotAction, opSlot: Int) {
        val (tool, catches) = action

        // Level requirement — check against the lowest-tier catch
        val lowestReq = catches.minOf { it.levelReq }
        if (player.fishingLvl < lowestReq) {
            mes("You need a Fishing level of at least $lowestReq to fish here.")
            return
        }

        // Tool check
        if (!inv.contains(tool.obj)) {
            mes("You need a ${tool.toolName} to fish here.")
            return
        }

        // Bait check (before starting animation to give immediate feedback)
        if (tool.baitItem != null && !inv.contains(tool.baitItem)) {
            mes("You need some ${tool.baitName} to fish here.")
            return
        }

        // Inventory full
        if (inv.isFull()) {
            mes("Your inventory is too full to hold any more fish.")
            return
        }

        // -----------------------------------------------------------------------
        // Animation: play every 4 ticks (same cadence as woodcutting)
        // -----------------------------------------------------------------------
        if (skillAnimDelay <= mapClock) {
            skillAnimDelay = mapClock + 4
            anim(tool.anim)
        }

        // -----------------------------------------------------------------------
        // Tick gate: on first call delay 3 ticks, then check every subsequent tick
        // -----------------------------------------------------------------------
        if (actionDelay < mapClock) {
            actionDelay = mapClock + 3
            requeue(npc, opSlot)
            return
        }

        if (actionDelay != mapClock) {
            // Not at the action tick yet — re-queue and wait
            requeue(npc, opSlot)
            return
        }

        // actionDelay == mapClock: attempt the catch
        actionDelay = mapClock + 3

        // Find the highest-level eligible catch
        val eligible =
            catches.filter { player.fishingLvl >= it.levelReq }.sortedByDescending { it.levelReq }

        for (catch in eligible) {
            val success =
                statRandom(stats.fishing, catch.successLow, catch.successHigh, invisibleLvls)
            if (success) {
                // Consume bait on every successful catch
                if (tool.baitItem != null) {
                    val deleted = invDel(inv, tool.baitItem, count = 1, strict = false)
                    if (deleted.failure) {
                        mes("You need some ${tool.baitName} to fish here.")
                        return
                    }
                }

                val xp = catch.xp * xpMods.get(player, stats.fishing)
                spam("You catch ${articleFor(catch.name)} ${catch.name}!")
                statAdvance(stats.fishing, xp)
                invAddOrDrop(objRepo, catch.obj, count = 1)
                break
            }
        }

        // Post-catch checks
        if (inv.isFull()) {
            mes("Your inventory is too full to hold any more fish.")
            resetAnim()
            return
        }

        if (tool.baitItem != null && !inv.contains(tool.baitItem)) {
            mes("You need some ${tool.baitName} to fish here.")
            resetAnim()
            return
        }

        // Continue fishing
        requeue(npc, opSlot)
    }

    /** Dispatches a re-queue interaction to the same op slot the player originally used. */
    private fun ProtectedAccess.requeue(npc: Npc, opSlot: Int) {
        when (opSlot) {
            1 -> opNpc1(npc)
            2 -> opNpc2(npc)
            else -> opNpc1(npc)
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private fun articleFor(name: String): String {
        val vowels = setOf('a', 'e', 'i', 'o', 'u')
        return if (name.first().lowercaseChar() in vowels) "an" else "a"
    }

    // -----------------------------------------------------------------------
    // Spot / tool / catch data tables
    // -----------------------------------------------------------------------
    companion object {

        // -------------------------------------------------------------------
        // Tools
        // -------------------------------------------------------------------
        private val SMALL_NET =
            FishingTool(
                obj = FishingObjs.small_fishing_net,
                anim = FishingSeqs.human_fishing_net,
                baitItem = null,
                toolName = "small fishing net",
            )
        private val BIG_NET =
            FishingTool(
                obj = FishingObjs.big_fishing_net,
                anim = FishingSeqs.human_fishing_bignet,
                baitItem = null,
                toolName = "big fishing net",
            )
        private val FISHING_ROD =
            FishingTool(
                obj = FishingObjs.fishing_rod,
                anim = FishingSeqs.human_fishing_bait,
                baitItem = FishingObjs.fishing_bait,
                toolName = "fishing rod",
                baitName = "fishing bait",
            )
        private val FLY_FISHING_ROD =
            FishingTool(
                obj = FishingObjs.fly_fishing_rod,
                anim = FishingSeqs.human_fishing_bait,
                baitItem = FishingObjs.feather,
                toolName = "fly fishing rod",
                baitName = "feathers",
            )
        private val LOBSTER_POT =
            FishingTool(
                obj = FishingObjs.lobster_pot,
                anim = FishingSeqs.human_fishing_cage,
                baitItem = null,
                toolName = "lobster pot",
            )
        private val HARPOON =
            FishingTool(
                obj = FishingObjs.harpoon,
                anim = FishingSeqs.human_fishing_harpoon,
                baitItem = null,
                toolName = "harpoon",
            )
        // -------------------------------------------------------------------
        // Fish catch definitions
        // Wiki-accurate level reqs and XP.
        // successLow / successHigh: rough OSRS-equivalent scaled to 0-255.
        //   Formula from SkillingSuccessRate: success = low + (high-low)*(level/99)
        //   Ballpark values — tune with wiki tick-rate data when available.
        // -------------------------------------------------------------------

        // Net catches
        private val SHRIMPS =
            FishCatch(
                obj = FishingObjs.raw_shrimps,
                levelReq = 1,
                xp = 10.0,
                successLow = 64,
                successHigh = 164,
                name = "shrimp",
            )
        private val ANCHOVIES =
            FishCatch(
                obj = FishingObjs.raw_anchovies,
                levelReq = 15,
                xp = 40.0,
                successLow = 56,
                successHigh = 140,
                name = "anchovies",
            )

        // Bait catches (freshwater)
        private val SARDINE =
            FishCatch(
                obj = FishingObjs.raw_sardine,
                levelReq = 5,
                xp = 20.0,
                successLow = 64,
                successHigh = 152,
                name = "sardine",
            )
        private val HERRING =
            FishCatch(
                obj = FishingObjs.raw_herring,
                levelReq = 10,
                xp = 30.0,
                successLow = 64,
                successHigh = 140,
                name = "herring",
            )

        // Lure catches
        private val TROUT =
            FishCatch(
                obj = FishingObjs.raw_trout,
                levelReq = 20,
                xp = 50.0,
                successLow = 64,
                successHigh = 175,
                name = "trout",
            )
        private val SALMON =
            FishCatch(
                obj = FishingObjs.raw_salmon,
                levelReq = 30,
                xp = 70.0,
                successLow = 64,
                successHigh = 150,
                name = "salmon",
            )

        // Bait catch (pike)
        private val PIKE =
            FishCatch(
                obj = FishingObjs.raw_pike,
                levelReq = 25,
                xp = 60.0,
                successLow = 64,
                successHigh = 140,
                name = "pike",
            )

        // Big net catches
        private val MACKEREL =
            FishCatch(
                obj = FishingObjs.raw_mackerel,
                levelReq = 16,
                xp = 20.0,
                successLow = 64,
                successHigh = 140,
                name = "mackerel",
            )
        private val COD =
            FishCatch(
                obj = FishingObjs.raw_cod,
                levelReq = 23,
                xp = 45.0,
                successLow = 56,
                successHigh = 140,
                name = "cod",
            )
        private val BASS =
            FishCatch(
                obj = FishingObjs.raw_bass,
                levelReq = 46,
                xp = 100.0,
                successLow = 56,
                successHigh = 130,
                name = "bass",
            )

        // Cage catch
        private val LOBSTER =
            FishCatch(
                obj = FishingObjs.raw_lobster,
                levelReq = 40,
                xp = 90.0,
                successLow = 64,
                successHigh = 150,
                name = "lobster",
            )

        // Harpoon catches
        private val TUNA =
            FishCatch(
                obj = FishingObjs.raw_tuna,
                levelReq = 35,
                xp = 80.0,
                successLow = 64,
                successHigh = 140,
                name = "tuna",
            )
        private val SWORDFISH =
            FishCatch(
                obj = FishingObjs.raw_swordfish,
                levelReq = 50,
                xp = 100.0,
                successLow = 56,
                successHigh = 130,
                name = "swordfish",
            )
        private val SHARK =
            FishCatch(
                obj = FishingObjs.raw_shark,
                levelReq = 76,
                xp = 110.0,
                successLow = 32,
                successHigh = 80,
                name = "shark",
            )

        // Monkfish
        private val MONKFISH =
            FishCatch(
                obj = FishingObjs.raw_monkfish,
                levelReq = 62,
                xp = 120.0,
                successLow = 48,
                successHigh = 120,
                name = "monkfish",
            )

        // Anglerfish
        private val ANGLERFISH =
            FishCatch(
                obj = FishingObjs.raw_anglerfish,
                levelReq = 82,
                xp = 120.0,
                successLow = 24,
                successHigh = 60,
                name = "anglerfish",
            )

        // -------------------------------------------------------------------
        // Spot actions (tool + catch list pairs)
        // -------------------------------------------------------------------

        val NET_ACTION = SpotAction(tool = SMALL_NET, catches = listOf(SHRIMPS, ANCHOVIES))
        val BAIT_FRESHWATER_ACTION =
            SpotAction(tool = FISHING_ROD, catches = listOf(SARDINE, HERRING))
        val LURE_ACTION = SpotAction(tool = FLY_FISHING_ROD, catches = listOf(TROUT, SALMON))
        val BAIT_PIKE_ACTION = SpotAction(tool = FISHING_ROD, catches = listOf(PIKE))
        val CAGE_ACTION = SpotAction(tool = LOBSTER_POT, catches = listOf(LOBSTER))
        val HARPOON_TUNA_ACTION = SpotAction(tool = HARPOON, catches = listOf(TUNA, SWORDFISH))
        val BIG_NET_ACTION = SpotAction(tool = BIG_NET, catches = listOf(MACKEREL, COD, BASS))
        val HARPOON_SHARK_ACTION = SpotAction(tool = HARPOON, catches = listOf(SHARK))
        val MONKFISH_NET_ACTION = SpotAction(tool = SMALL_NET, catches = listOf(MONKFISH))
        val HARPOON_SWORDFISH_ACTION = SpotAction(tool = HARPOON, catches = listOf(SWORDFISH))
        val ANGLERFISH_ACTION = SpotAction(tool = FISHING_ROD, catches = listOf(ANGLERFISH))
    }
}
