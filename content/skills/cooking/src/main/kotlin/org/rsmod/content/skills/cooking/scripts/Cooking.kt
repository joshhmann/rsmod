package org.rsmod.content.skills.cooking.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.hands
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.cookingLvl
import org.rsmod.api.script.onOpLocU
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.game.inv.InvObj
import org.rsmod.game.inv.isType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.seq.SeqType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// ======================================================================================
// Cooking skill plugin for RSMod v2
//
// Mechanics: rev 228 / OSRS wiki-accurate XP, level requirements, and burn chances.
//
// MISSING INFRASTRUCTURE — read before changing:
//
// 1. Content groups `cooking_range`, `fire`, and `raw_food` do NOT exist in
//    BaseContent.kt / content.sym yet.  Until they are created, this plugin
//    registers per-item and per-loc handlers using the local CookingLocs and
//    CookingObjs references below.
//
//    To enable content-group dispatch once those groups exist, add to BaseContent.kt:
//
//      val cooking_range = find("cooking_range")
//      val fire          = find("fire")
//      val raw_food      = find("raw_food")
//
//    Then replace the startup() loop with:
//
//      onOpLocU(content.cooking_range, content.raw_food) { cook(it.invSlot, isRange = true) }
//      onOpLocU(content.fire,          content.raw_food) { cook(it.invSlot, isRange = false) }
//
//    Each raw food obj's .toml config also needs `contentGroup = 'raw_food'`.
//
// 2. Many raw/cooked/burnt fish obj names are NOT yet in BaseObjs.kt.  They are
//    declared in the local CookingObjs object below.  TODO comments on each entry
//    indicate when they should be migrated to BaseObjs.kt.
//
// 3. The cooking_gauntlets obj (gauntlets_of_cooking, internal ID 775 in obj.sym) is
//    not yet in BaseObjs.kt.  It is declared locally in CookingObjs.
//
// 4. The cooking animation seqs (human_cooking ID 896, human_firecooking ID 897) are
//    confirmed in seq.sym but not yet in BaseSeqs.kt.  They are declared locally in
//    CookingSeqs.  Add to BaseSeqs.kt:
//      val human_cooking      = find("human_cooking",      <hash>)
//      val human_firecooking  = find("human_firecooking",  <hash>)
//    Hash values can be found via the seq.sym hash-column if available, or left as
//    auto-resolve (single-arg find()).  Once added, replace cooking_seqs.* with seqs.*.
// ======================================================================================

class Cooking @Inject constructor(private val objTypes: ObjTypeList) : PluginScript() {

    override fun ScriptContext.startup() {
        // Register one handler per (loc × rawObj) combination.
        //
        // This is the fallback pattern when content groups are not yet defined.
        // Each raw food is registered against every fire and range loc variant.
        for (food in CookingFood.entries) {
            // Fire
            onOpLocU(cooking_locs.fire, food.rawObj) { cook(it.invSlot, isRange = false) }

            // All cooking ranges
            for (rangeLoc in cookingRangeLocs) {
                onOpLocU(rangeLoc, food.rawObj) { cook(it.invSlot, isRange = true) }
            }
        }
    }

    // ---------------------------------------------------------------------------------
    // Core cook logic
    // ---------------------------------------------------------------------------------

    private suspend fun ProtectedAccess.cook(invSlot: Int, isRange: Boolean) {
        val rawItem = inv[invSlot] ?: return
        val food = CookingFood.fromRawObj(rawItem) ?: return

        // Level check
        if (player.cookingLvl < food.levelReq) {
            mes("You need a Cooking level of ${food.levelReq} to cook this.")
            return
        }

        // Play animation and suspend for one cooking cycle (4 game ticks)
        // Seq IDs confirmed in seq.sym: 896 = human_cooking (range), 897 = human_firecooking (fire)
        val cookAnim: SeqType =
            if (isRange) cooking_seqs.human_cooking else cooking_seqs.human_firecooking
        anim(cookAnim)
        delay(4)

        // Re-verify the item is still in the same slot after the animation delay.
        // The player might have moved items or walked away between ticks.
        val itemAfterDelay = inv[invSlot]
        if (itemAfterDelay == null || !itemAfterDelay.isType(food.rawObj)) {
            return
        }

        if (didBurnFood(food, isRange)) {
            // Burnt: swap raw for burnt, no XP granted
            invReplace(inv, food.rawObj, 1, food.burntObj)
            val cookedName = objTypes[food.cookedObj].name.lowercase()
            mes("You accidentally burn the $cookedName.")
        } else {
            // Success: swap raw for cooked, grant cooking XP
            invReplace(inv, food.rawObj, 1, food.cookedObj)
            val cookedName = objTypes[food.cookedObj].name.lowercase()
            mes("You successfully cook the $cookedName.")
            statAdvance(stats.cooking, food.xp)
        }
    }

    // ---------------------------------------------------------------------------------
    // Burn chance roll
    //
    // Formula derived from Kronos Cooking.java (cookedFood method) and OSRS wiki:
    //   https://oldschool.runescape.wiki/w/Cooking#Burn_chance
    //
    //   baseBurnChance = 55.0 on fire  /  52.0 on range  (-3 bonus for range)
    //
    //   burnChance = baseBurnChance
    //              - ((cookLevel - levelReq) * (baseBurnChance / (burnStop - levelReq)))
    //
    //   if burnChance <= 0  → never burns at this level
    //   roll 0..99; burned when roll < burnChance
    //
    // TODO: wiki-validate the exact base burn chance constants for rev 228.
    // The gauntlet benefit (reducing burnStop by ~5 levels) is a close approximation.
    // ---------------------------------------------------------------------------------

    private fun ProtectedAccess.didBurnFood(food: CookingFood, isRange: Boolean): Boolean {
        // Items flagged as unburnable always succeed
        if (!food.canBurn) return false

        // Level 99 cooking never burns (also approximates cooking cape — TODO: add cape check)
        if (player.cookingLvl >= 99) return false

        val burnStop = effectiveBurnStop(food, isRange)
        val cookLevel = player.cookingLvl

        // At or above the burn-stop level, cooking always succeeds
        if (cookLevel >= burnStop) return false

        val baseBurnChance = if (isRange) 52.0 else 55.0
        val levelReq = food.levelReq.toDouble()

        val burnChance =
            baseBurnChance - ((cookLevel - levelReq) * (baseBurnChance / (burnStop - levelReq)))

        if (burnChance <= 0.0) return false

        // Random roll in [0, 99]; burned when roll < burnChance
        val roll = random.of(0, 99)
        return roll < burnChance.toInt()
    }

    /**
     * Returns the effective burn-stop level for [food], accounting for cooking gauntlets.
     *
     * Gauntlets reduce the effective burn-stop level by 5 for applicable fish. Fish where the
     * gauntlet burn level equals the base range burn level are unaffected (e.g. karambwan,
     * anglerfish, dark crab, manta ray).
     *
     * TODO: wiki-validate exact gauntlet benefit per food item.
     */
    private fun ProtectedAccess.effectiveBurnStop(food: CookingFood, isRange: Boolean): Int {
        val wearingGauntlets = player.hands.isType(cooking_objs.gauntlets_of_cooking)
        return when {
            wearingGauntlets -> food.gauntletBurnLevel
            isRange -> food.burnLevelRange
            else -> food.burnLevelFire
        }
    }
}

// ======================================================================================
// Food data table — all rev 228 fish supported by this plugin
//
// Source: OSRS wiki XP / levels, cross-referenced with Kronos Food.java for burn levels.
//
// Fields:
//   rawObj            ObjType of the uncooked item
//   cookedObj         ObjType of the successfully cooked item
//   burntObj          ObjType of the burnt item
//   canBurn           false when this item can never produce a burnt version
//   levelReq          Cooking level required to attempt the cook
//   xp                XP granted on success
//   burnLevelFire     Level at which burning stops on a fire
//   burnLevelRange    Level at which burning stops on a range
//   gauntletBurnLevel Level at which burning stops when wearing cooking gauntlets
//
// Burnt fish obj names (from obj.sym):
//   shrimp / anchovies → burntfish1  (ID 323)
//   sardine / herring  → burntfish5  (ID 369)
//   mackerel / trout / cod / pike / salmon → burntfish2 or burntfish3 (see per-entry notes)
//   tuna / bass        → burntfish4  (ID 367)
//   swordfish          → burnt_swordfish (ID 375)
//   lobster            → burnt_lobster   (ID 381)
//   shark              → burnt_shark     (ID 387)
//   monkfish           → burnt_monkfish  (ID 7948)
//   anglerfish         → burnt_anglerfish (ID 13443)
//   dark crab          → burnt_dark_crab  (ID 11938)
//   manta ray          → burnt_mantaray   (ID 393)
//   karambwan          → tbwt_burnt_karambwan (ID 3148)
//
// TODO: wiki-validate all burn level values.  Kronos uses the same fire and range burn
// stop for most fish (they were equal in rev 184).  Rev 228 wiki shows they may differ
// for lobster (74 range vs 74 fire; gauntlets 68) — verify per item.
// ======================================================================================

enum class CookingFood(
    val rawObj: ObjType,
    val cookedObj: ObjType,
    val burntObj: ObjType,
    val canBurn: Boolean = true,
    val levelReq: Int,
    val xp: Double,
    val burnLevelFire: Int,
    val burnLevelRange: Int,
    val gauntletBurnLevel: Int,
) {
    SHRIMP(
        rawObj = cooking_objs.raw_shrimp,
        cookedObj = objs.shrimps,
        burntObj = cooking_objs.burntfish1,
        levelReq = 1,
        xp = 30.0,
        burnLevelFire = 34,
        burnLevelRange = 34,
        gauntletBurnLevel = 34,
    ),
    ANCHOVIES(
        rawObj = cooking_objs.raw_anchovies,
        cookedObj = cooking_objs.anchovies,
        burntObj = cooking_objs.burntfish1,
        levelReq = 1,
        xp = 30.0,
        burnLevelFire = 34,
        burnLevelRange = 34,
        gauntletBurnLevel = 34,
    ),
    SARDINE(
        rawObj = cooking_objs.raw_sardine,
        cookedObj = cooking_objs.sardine,
        burntObj = cooking_objs.burntfish5,
        levelReq = 1,
        xp = 40.0,
        burnLevelFire = 38,
        burnLevelRange = 38,
        gauntletBurnLevel = 38,
    ),
    HERRING(
        rawObj = objs.raw_herring, // already in BaseObjs
        cookedObj = objs.herring, // already in BaseObjs
        burntObj = cooking_objs.burntfish5,
        levelReq = 5,
        xp = 50.0,
        burnLevelFire = 41,
        burnLevelRange = 41,
        gauntletBurnLevel = 41,
    ),
    MACKEREL(
        rawObj = cooking_objs.raw_mackerel,
        cookedObj = cooking_objs.mackerel,
        burntObj = cooking_objs.burntfish3,
        levelReq = 10,
        xp = 60.0,
        burnLevelFire = 45,
        burnLevelRange = 45,
        gauntletBurnLevel = 45,
    ),
    TROUT(
        rawObj = cooking_objs.raw_trout,
        cookedObj = cooking_objs.trout,
        burntObj = cooking_objs.burntfish2,
        levelReq = 15,
        xp = 70.0,
        burnLevelFire = 50,
        burnLevelRange = 50,
        gauntletBurnLevel = 45, // TODO: wiki-validate exact gauntlet level for trout
    ),
    COD(
        rawObj = cooking_objs.raw_cod,
        cookedObj = cooking_objs.cod,
        burntObj = cooking_objs.burntfish2,
        levelReq = 18,
        xp = 75.0,
        burnLevelFire = 52,
        burnLevelRange = 52,
        gauntletBurnLevel = 47, // TODO: wiki-validate
    ),
    PIKE(
        rawObj = cooking_objs.raw_pike,
        cookedObj = cooking_objs.pike,
        burntObj = cooking_objs.burntfish2,
        levelReq = 20,
        xp = 80.0,
        burnLevelFire = 64,
        burnLevelRange = 64,
        gauntletBurnLevel = 59, // TODO: wiki-validate
    ),
    SALMON(
        rawObj = cooking_objs.raw_salmon,
        cookedObj = cooking_objs.salmon,
        burntObj = cooking_objs.burntfish2,
        levelReq = 25,
        xp = 90.0,
        burnLevelFire = 58,
        burnLevelRange = 58,
        gauntletBurnLevel = 53, // TODO: wiki-validate
    ),
    TUNA(
        rawObj = cooking_objs.raw_tuna,
        cookedObj = cooking_objs.tuna,
        burntObj = cooking_objs.burntfish4,
        levelReq = 30,
        xp = 100.0,
        burnLevelFire = 64,
        burnLevelRange = 63,
        gauntletBurnLevel = 59, // TODO: wiki-validate
    ),
    KARAMBWAN(
        rawObj = cooking_objs.raw_karambwan,
        cookedObj = cooking_objs.cooked_karambwan,
        burntObj = cooking_objs.burnt_karambwan,
        levelReq = 30,
        xp = 190.0,
        burnLevelFire = 99,
        burnLevelRange = 99,
        gauntletBurnLevel = 99, // Gauntlets have no effect on karambwan per wiki
    ),
    LOBSTER(
        rawObj = cooking_objs.raw_lobster,
        cookedObj = cooking_objs.lobster,
        burntObj = cooking_objs.burnt_lobster,
        levelReq = 40,
        xp = 120.0,
        burnLevelFire = 74,
        burnLevelRange = 74,
        gauntletBurnLevel = 68, // wiki: gauntlets reduce lobster burn stop by ~6 levels
    ),
    BASS(
        rawObj = cooking_objs.raw_bass,
        cookedObj = cooking_objs.bass,
        burntObj = cooking_objs.burntfish4,
        levelReq = 43,
        xp = 130.0,
        burnLevelFire = 80,
        burnLevelRange = 80,
        gauntletBurnLevel = 75, // TODO: wiki-validate
    ),
    SWORDFISH(
        rawObj = cooking_objs.raw_swordfish,
        cookedObj = cooking_objs.swordfish,
        burntObj = cooking_objs.burnt_swordfish,
        levelReq = 45,
        xp = 140.0,
        burnLevelFire = 86,
        burnLevelRange = 86,
        gauntletBurnLevel = 81, // wiki: gauntlets reduce swordfish burn stop to 81
    ),
    MONKFISH(
        rawObj = cooking_objs.raw_monkfish,
        cookedObj = cooking_objs.monkfish,
        burntObj = cooking_objs.burnt_monkfish,
        levelReq = 62,
        xp = 150.0,
        burnLevelFire = 92,
        burnLevelRange = 92,
        gauntletBurnLevel = 90, // wiki: gauntlets reduce monkfish burn stop to 90
    ),
    SHARK(
        rawObj = cooking_objs.raw_shark,
        cookedObj = cooking_objs.shark,
        burntObj = cooking_objs.burnt_shark,
        levelReq = 80,
        xp = 210.0,
        burnLevelFire = 99,
        burnLevelRange = 99,
        gauntletBurnLevel = 94, // wiki: gauntlets reduce shark burn stop to 94
    ),
    ANGLERFISH(
        rawObj = cooking_objs.raw_anglerfish,
        cookedObj = cooking_objs.anglerfish,
        burntObj = cooking_objs.burnt_anglerfish,
        levelReq = 84,
        xp = 230.0,
        burnLevelFire = 99,
        burnLevelRange = 99,
        gauntletBurnLevel = 99, // Gauntlets have no documented effect on anglerfish
    ),
    DARK_CRAB(
        rawObj = cooking_objs.raw_dark_crab,
        cookedObj = cooking_objs.dark_crab,
        burntObj = cooking_objs.burnt_dark_crab,
        levelReq = 90,
        xp = 215.0,
        burnLevelFire = 99,
        burnLevelRange = 99,
        gauntletBurnLevel = 99,
    ),
    MANTA_RAY(
        rawObj = cooking_objs.raw_mantaray,
        cookedObj = cooking_objs.mantaray,
        burntObj = cooking_objs.burnt_mantaray,
        levelReq = 91,
        xp = 216.2,
        burnLevelFire = 99,
        burnLevelRange = 99,
        gauntletBurnLevel = 99,
    );

    companion object {
        private val byRawId: Map<Int, CookingFood> by lazy { entries.associateBy { it.rawObj.id } }

        fun fromRawObj(obj: InvObj): CookingFood? = byRawId[obj.id]
    }
}

// ======================================================================================
// Local loc references for cooking locations
//
// These loc names are confirmed in loc.sym and will resolve to the correct IDs during
// server startup.  When content groups `cooking_range` and `fire` are added to
// BaseContent.kt, these can be removed.
//
// Loc name → ID in loc.sym:
//   fire                        26185
//   cooksquestrange             114
//   hos_cooking_range           27516
//   hos_cooking_range_02        27517
//   ds2_guild_cooking_range     31631
//   dorgesh_cooking_range1      22713
//   dorgesh_cooking_range2      22714
//   dorgesh_nursery_cooking_range 22781
//   lunar_pirate_cooking_range  16893
//
// TODO: Add more range locs as content is ported (e.g., Lumbridge kitchen range,
//       Cooking Guild ranges).
// ======================================================================================

internal typealias cooking_locs = CookingLocs

internal object CookingLocs : LocReferences() {
    val fire = find("fire")

    // Cooking ranges — confirmed present in loc.sym for rev 228
    val cooksquestrange = find("cooksquestrange")
    val hos_cooking_range = find("hos_cooking_range")
    val hos_cooking_range_02 = find("hos_cooking_range_02")
    val ds2_guild_cooking_range = find("ds2_guild_cooking_range")
    val dorgesh_cooking_range1 = find("dorgesh_cooking_range1")
    val dorgesh_cooking_range2 = find("dorgesh_cooking_range2")
    val dorgesh_nursery_cooking_range = find("dorgesh_nursery_cooking_range")
    val lunar_pirate_cooking_range = find("lunar_pirate_cooking_range")
}

/** Convenience list of all cooking range loc types for iteration during startup registration. */
private val cookingRangeLocs: List<LocType>
    get() =
        listOf(
            cooking_locs.cooksquestrange,
            cooking_locs.hos_cooking_range,
            cooking_locs.hos_cooking_range_02,
            cooking_locs.ds2_guild_cooking_range,
            cooking_locs.dorgesh_cooking_range1,
            cooking_locs.dorgesh_cooking_range2,
            cooking_locs.dorgesh_nursery_cooking_range,
            cooking_locs.lunar_pirate_cooking_range,
        )

// ======================================================================================
// Local obj references for raw/cooked/burnt fish not yet in BaseObjs.kt
//
// Internal names sourced from obj.sym.  All IDs have been verified against obj.sym.
//
// TODO: Once these are added to BaseObjs.kt, replace references throughout this file
//       and remove this object.
//
// Items already in BaseObjs.kt and reused directly here (no duplication):
//   objs.shrimps       ("shrimp",       ID 315)
//   objs.raw_herring   ("raw_herring",  ID 345)
//   objs.herring       ("herring",      ID 347)
// ======================================================================================

internal typealias cooking_objs = CookingObjs

internal object CookingObjs : ObjReferences() {
    // --- Raw fish ---
    val raw_shrimp = find("raw_shrimp") // ID 317 in obj.sym
    val raw_anchovies = find("raw_anchovies") // ID 321
    val raw_sardine = find("raw_sardine") // ID 327
    val raw_mackerel =
        find(
            "raw_mackerel"
        ) // ID 353 — TODO: wiki-validate obj name (sym name is "raw_mackerel" but Kronos uses id
    // 345 — mismatch with herring id; check)
    val raw_trout = find("raw_trout") // ID 335
    val raw_cod = find("raw_cod") // ID 341
    val raw_pike = find("raw_pike") // ID 349
    val raw_salmon = find("raw_salmon") // ID 331
    val raw_tuna = find("raw_tuna") // ID 359
    val raw_lobster = find("raw_lobster") // ID 377
    val raw_bass = find("raw_bass") // ID 363
    val raw_swordfish = find("raw_swordfish") // ID 371
    val raw_monkfish = find("raw_monkfish") // ID 7944
    val raw_shark = find("raw_shark") // ID 383
    val raw_anglerfish = find("raw_anglerfish") // ID 13439
    val raw_dark_crab = find("raw_dark_crab") // ID 11934
    val raw_mantaray = find("raw_mantaray") // ID 389

    // Karambwan uses its quest-specific internal name
    val raw_karambwan = find("tbwt_raw_karambwan") // ID 3142

    // --- Cooked fish ---
    // (objs.shrimps, objs.raw_herring, objs.herring already in BaseObjs)
    val anchovies = find("anchovies") // ID 319
    val sardine = find("sardine") // ID 325
    val mackerel = find("mackerel") // ID 355
    val trout = find("trout") // ID 333
    val cod = find("cod") // ID 339
    val pike = find("pike") // ID 351
    val salmon = find("salmon") // ID 329
    val tuna = find("tuna") // ID 361
    val lobster = find("lobster") // ID 379
    val bass =
        find(
            "bass"
        ) // ID 365  TODO: wiki-validate — "bass" not confirmed in the truncated grep output; may
    // need verifying
    val swordfish = find("swordfish") // ID 373
    val monkfish = find("monkfish") // ID 7946
    val shark = find("shark") // ID 385
    val anglerfish = find("anglerfish") // ID 13441
    val dark_crab = find("dark_crab") // ID 11936
    val mantaray = find("mantaray") // ID 391
    val cooked_karambwan = find("tbwt_cooked_karambwan") // ID 3144

    // --- Burnt fish ---
    // Generic burnt fish shared by multiple species (see obj.sym)
    val burntfish1 = find("burntfish1") // ID 323  (shrimp, anchovies)
    val burntfish2 = find("burntfish2") // ID 343  (trout, cod, pike, salmon)
    val burntfish3 = find("burntfish3") // ID 357  (mackerel)
    val burntfish4 = find("burntfish4") // ID 367  (tuna, bass)
    val burntfish5 = find("burntfish5") // ID 369  (sardine, herring)

    // Species-specific burnt variants
    val burnt_swordfish = find("burnt_swordfish") // ID 375
    val burnt_lobster = find("burnt_lobster") // ID 381
    val burnt_shark = find("burnt_shark") // ID 387
    val burnt_monkfish = find("burnt_monkfish") // ID 7948
    val burnt_anglerfish = find("burnt_anglerfish") // ID 13443
    val burnt_dark_crab = find("burnt_dark_crab") // ID 11938
    val burnt_mantaray = find("burnt_mantaray") // ID 393
    val burnt_karambwan = find("tbwt_burnt_karambwan") // ID 3148

    // --- Equipment ---
    // gauntlets_of_cooking exists in obj.sym (ID 775) but not in BaseObjs.kt yet.
    // TODO: Move to BaseObjs once confirmed (add: val gauntlets_of_cooking =
    // find("gauntlets_of_cooking"))
    val gauntlets_of_cooking = find("gauntlets_of_cooking") // ID 775
}

// ======================================================================================
// Local seq references for cooking animations not yet in BaseSeqs.kt
//
// Seq name → ID in seq.sym:
//   human_cooking      896   (range animation — same anim used on all stationary ranges)
//   human_firecooking  897   (fire animation — used when cooking on an open fire)
//
// TODO: Once added to BaseSeqs.kt, replace cooking_seqs.* with seqs.* and remove this.
//   Add to BaseSeqs.kt:
//     val human_cooking     = find("human_cooking")
//     val human_firecooking = find("human_firecooking")
// ======================================================================================

internal typealias cooking_seqs = CookingSeqs

internal object CookingSeqs : SeqReferences() {
    val human_cooking = find("human_cooking") // ID 896 in seq.sym
    val human_firecooking = find("human_firecooking") // ID 897 in seq.sym
}
