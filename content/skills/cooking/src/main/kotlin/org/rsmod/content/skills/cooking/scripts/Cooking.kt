package org.rsmod.content.skills.cooking.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.hands
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.cookingLvl
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.isQuestComplete
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
// Supports:
//   - 21 fish types (shrimp through manta ray, including karambwan)
//   - Meats: beef, chicken, rat, bear, rabbit, chompy, oomlie, ugthanki
//   - Breads: bread, pitta
//   - Pies: redberry, meat, apple, garden, wild, mud, admiral
//   - Cakes: regular cake, chocolate cake
//   - Pizzas: plain, meat, anchovies, pineapple
//   - Stews: regular stew, curry
//   - Wines: jug of wine (fermenting grapes)
//   - Potatoes: baked potato
//   - Cooking gauntlets
//   - Range vs fire differentials
// ======================================================================================

class Cooking @Inject constructor(private val objTypes: ObjTypeList) : PluginScript() {

    override fun ScriptContext.startup() {
        // --- Fish registration ---
        for (food in CookingFood.entries) {
            onOpLocU(cooking_locs.fire, food.rawObj) { cook(it.invSlot, isRange = false) }
            for (rangeLoc in cookingRangeLocs) {
                onOpLocU(rangeLoc, food.rawObj) { cook(it.invSlot, isRange = true) }
            }
        }
        // --- Non-fish registration ---
        for (food in CookingNonfish.entries) {
            onOpLocU(cooking_locs.fire, food.rawObj) { cookNonfish(it.invSlot, isRange = false) }
            for (rangeLoc in cookingRangeLocs) {
                onOpLocU(rangeLoc, food.rawObj) { cookNonfish(it.invSlot, isRange = true) }
            }
        }

        // Cook-o-matic 100 (Lumbridge kitchen range) — 12% burn reduction after quest
        for (food in CookingFood.entries) {
            onOpLocU(cooking_locs.cooksquestrange, food.rawObj) {
                cook(it.invSlot, isRange = true, isCookOMatic = true)
            }
        }
        for (food in CookingNonfish.entries) {
            onOpLocU(cooking_locs.cooksquestrange, food.rawObj) {
                cookNonfish(it.invSlot, isRange = true, isCookOMatic = true)
            }
        }
    }

    // ---------------------------------------------------------------------------------
    // Core fish cook logic
    // ---------------------------------------------------------------------------------

    private suspend fun ProtectedAccess.cook(invSlot: Int, isRange: Boolean, isCookOMatic: Boolean = false) {
        val rawItem = inv[invSlot] ?: return
        val food = CookingFood.fromRawObj(rawItem) ?: return

        if (player.cookingLvl < food.levelReq) {
            mes("You need a Cooking level of ${food.levelReq} to cook this.")
            return
        }

        val cookAnim: SeqType =
            if (isRange) cooking_seqs.human_cooking else cooking_seqs.human_firecooking
        anim(cookAnim)
        delay(4)

        val itemAfterDelay = inv[invSlot]
        if (itemAfterDelay == null || !itemAfterDelay.isType(food.rawObj)) {
            return
        }

        if (didBurnFood(food, isRange, isCookOMatic)) {
            invReplace(inv, food.rawObj, 1, food.burntObj)
            val cookedName = objTypes[food.cookedObj].name.lowercase()
            mes("You accidentally burn the $cookedName.")
        } else {
            invReplace(inv, food.rawObj, 1, food.cookedObj)
            val cookedName = objTypes[food.cookedObj].name.lowercase()
            mes("You successfully cook the $cookedName.")
            statAdvance(stats.cooking, food.xp)
        }
    }

    // ---------------------------------------------------------------------------------
    // Non-fish cook logic (simpler — no burn mechanics for most non-fish items)
    // ---------------------------------------------------------------------------------

    private suspend fun ProtectedAccess.cookNonfish(invSlot: Int, isRange: Boolean, isCookOMatic: Boolean = false) {
        val rawItem = inv[invSlot] ?: return
        val food = CookingNonfish.fromRawObj(rawItem) ?: return

        if (player.cookingLvl < food.levelReq) {
            mes("You need a Cooking level of ${food.levelReq} to cook this.")
            return
        }

        val cookAnim: SeqType =
            if (isRange) cooking_seqs.human_cooking else cooking_seqs.human_firecooking
        anim(cookAnim)
        delay(4)

        val itemAfterDelay = inv[invSlot]
        if (itemAfterDelay == null || !itemAfterDelay.isType(food.rawObj)) {
            return
        }

        if (food.burntObj != null && didBurnNonfish(food, isCookOMatic)) {
            invReplace(inv, food.rawObj, 1, food.burntObj!!)
            mes("You accidentally burn the ${objTypes[food.cookedObj].name.lowercase()}.")
        } else {
            invReplace(inv, food.rawObj, 1, food.cookedObj)
            val name = objTypes[food.cookedObj].name.lowercase()
            mes("You successfully cook the $name.")
            statAdvance(stats.cooking, food.xp)
        }
    }

    // ---------------------------------------------------------------------------------
    // Burn chance roll (fish)
    // ---------------------------------------------------------------------------------

    private fun ProtectedAccess.didBurnFood(food: CookingFood, isRange: Boolean, isCookOMatic: Boolean = false): Boolean {
        if (!food.canBurn) return false
        if (player.cookingLvl >= 99) return false

        val burnStop = effectiveBurnStop(food, isRange, isCookOMatic)
        val cookLevel = player.cookingLvl

        if (cookLevel >= burnStop) return false

        val baseBurnChance = if (isRange) 52.0 else 55.0
        val levelReq = food.levelReq.toDouble()

        val burnChance =
            baseBurnChance - ((cookLevel - levelReq) * (baseBurnChance / (burnStop - levelReq)))

        if (burnChance <= 0.0) return false

        val roll = random.of(0, 99)
        return roll < burnChance.toInt()
    }

    private fun ProtectedAccess.effectiveBurnStop(food: CookingFood, isRange: Boolean, isCookOMatic: Boolean = false): Int {
        val wearingGauntlets = player.hands.isType(cooking_objs.gauntlets_of_cooking)
        val baseStop = when {
            wearingGauntlets -> food.gauntletBurnLevel
            isRange -> food.burnLevelRange
            else -> food.burnLevelFire
        }
        // Cook-o-matic 100 adds +3 to effective burn stop (~12% burn reduction)
        return if (isCookOMatic && isQuestComplete(QuestList.cooks_assistant)) baseStop + 3 else baseStop
    }

    // ---------------------------------------------------------------------------------
    // Simplified burn for non-fish foods
    // ---------------------------------------------------------------------------------

    private fun ProtectedAccess.didBurnNonfish(food: CookingNonfish, isCookOMatic: Boolean = false): Boolean {
        if (player.cookingLvl >= 99) return false
        val cookLevel = player.cookingLvl
        val effectiveBurnStop = if (isCookOMatic && isQuestComplete(QuestList.cooks_assistant)) {
            food.burnStop + 3
        } else {
            food.burnStop
        }
        if (cookLevel >= effectiveBurnStop) return false

        val baseChance = if (food.isRangeOnly) 30.0 else 40.0
        val levelReq = food.levelReq.toDouble()

        val burnChance = baseChance - ((cookLevel - levelReq) * (baseChance / (effectiveBurnStop - levelReq)))
        if (burnChance <= 0.0) return false

        val roll = random.of(0, 99)
        return roll < burnChance.toInt()
    }
}

// ======================================================================================
// Fish food data table
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
        cookedObj = objs.shrimp,
        burntObj = cooking_objs.burntfish1,
        levelReq = 1, xp = 30.0,
        burnLevelFire = 34, burnLevelRange = 34, gauntletBurnLevel = 34,
    ),
    ANCHOVIES(
        rawObj = cooking_objs.raw_anchovies,
        cookedObj = cooking_objs.anchovies,
        burntObj = cooking_objs.burntfish1,
        levelReq = 1, xp = 30.0,
        burnLevelFire = 34, burnLevelRange = 34, gauntletBurnLevel = 34,
    ),
    SARDINE(
        rawObj = cooking_objs.raw_sardine,
        cookedObj = cooking_objs.sardine,
        burntObj = cooking_objs.burntfish5,
        levelReq = 1, xp = 40.0,
        burnLevelFire = 38, burnLevelRange = 38, gauntletBurnLevel = 38,
    ),
    HERRING(
        rawObj = objs.raw_herring,
        cookedObj = objs.herring,
        burntObj = cooking_objs.burntfish5,
        levelReq = 5, xp = 50.0,
        burnLevelFire = 41, burnLevelRange = 41, gauntletBurnLevel = 41,
    ),
    MACKEREL(
        rawObj = cooking_objs.raw_mackerel,
        cookedObj = cooking_objs.mackerel,
        burntObj = cooking_objs.burntfish3,
        levelReq = 10, xp = 60.0,
        burnLevelFire = 45, burnLevelRange = 45, gauntletBurnLevel = 45,
    ),
    TROUT(
        rawObj = cooking_objs.raw_trout,
        cookedObj = cooking_objs.trout,
        burntObj = cooking_objs.burntfish2,
        levelReq = 15, xp = 70.0,
        burnLevelFire = 50, burnLevelRange = 50, gauntletBurnLevel = 45,
    ),
    COD(
        rawObj = cooking_objs.raw_cod,
        cookedObj = cooking_objs.cod,
        burntObj = cooking_objs.burntfish2,
        levelReq = 18, xp = 75.0,
        burnLevelFire = 52, burnLevelRange = 52, gauntletBurnLevel = 47,
    ),
    PIKE(
        rawObj = cooking_objs.raw_pike,
        cookedObj = cooking_objs.pike,
        burntObj = cooking_objs.burntfish2,
        levelReq = 20, xp = 80.0,
        burnLevelFire = 64, burnLevelRange = 64, gauntletBurnLevel = 59,
    ),
    SALMON(
        rawObj = cooking_objs.raw_salmon,
        cookedObj = cooking_objs.salmon,
        burntObj = cooking_objs.burntfish2,
        levelReq = 25, xp = 90.0,
        burnLevelFire = 58, burnLevelRange = 58, gauntletBurnLevel = 53,
    ),
    TUNA(
        rawObj = cooking_objs.raw_tuna,
        cookedObj = cooking_objs.tuna,
        burntObj = cooking_objs.burntfish4,
        levelReq = 30, xp = 100.0,
        burnLevelFire = 64, burnLevelRange = 63, gauntletBurnLevel = 59,
    ),
    KARAMBWAN(
        rawObj = cooking_objs.raw_karambwan,
        cookedObj = cooking_objs.cooked_karambwan,
        burntObj = cooking_objs.burnt_karambwan,
        levelReq = 30, xp = 190.0,
        burnLevelFire = 99, burnLevelRange = 99, gauntletBurnLevel = 99,
    ),
    LOBSTER(
        rawObj = cooking_objs.raw_lobster,
        cookedObj = cooking_objs.lobster,
        burntObj = cooking_objs.burnt_lobster,
        levelReq = 40, xp = 120.0,
        burnLevelFire = 74, burnLevelRange = 74, gauntletBurnLevel = 68,
    ),
    BASS(
        rawObj = cooking_objs.raw_bass,
        cookedObj = cooking_objs.bass,
        burntObj = cooking_objs.burntfish4,
        levelReq = 43, xp = 130.0,
        burnLevelFire = 80, burnLevelRange = 80, gauntletBurnLevel = 75,
    ),
    SWORDFISH(
        rawObj = cooking_objs.raw_swordfish,
        cookedObj = cooking_objs.swordfish,
        burntObj = cooking_objs.burnt_swordfish,
        levelReq = 45, xp = 140.0,
        burnLevelFire = 86, burnLevelRange = 86, gauntletBurnLevel = 81,
    ),
    MONKFISH(
        rawObj = cooking_objs.raw_monkfish,
        cookedObj = cooking_objs.monkfish,
        burntObj = cooking_objs.burnt_monkfish,
        levelReq = 62, xp = 150.0,
        burnLevelFire = 92, burnLevelRange = 92, gauntletBurnLevel = 90,
    ),
    SHARK(
        rawObj = cooking_objs.raw_shark,
        cookedObj = cooking_objs.shark,
        burntObj = cooking_objs.burnt_shark,
        levelReq = 80, xp = 210.0,
        burnLevelFire = 99, burnLevelRange = 99, gauntletBurnLevel = 94,
    ),
    ANGLERFISH(
        rawObj = cooking_objs.raw_anglerfish,
        cookedObj = cooking_objs.anglerfish,
        burntObj = cooking_objs.burnt_anglerfish,
        levelReq = 84, xp = 230.0,
        burnLevelFire = 99, burnLevelRange = 99, gauntletBurnLevel = 99,
    ),
    DARK_CRAB(
        rawObj = cooking_objs.raw_dark_crab,
        cookedObj = cooking_objs.dark_crab,
        burntObj = cooking_objs.burnt_dark_crab,
        levelReq = 90, xp = 215.0,
        burnLevelFire = 99, burnLevelRange = 99, gauntletBurnLevel = 99,
    ),
    MANTA_RAY(
        rawObj = cooking_objs.raw_mantaray,
        cookedObj = cooking_objs.mantaray,
        burntObj = cooking_objs.burnt_mantaray,
        levelReq = 91, xp = 216.2,
        burnLevelFire = 99, burnLevelRange = 99, gauntletBurnLevel = 99,
    );

    companion object {
        private val byRawId: Map<Int, CookingFood> by lazy { entries.associateBy { it.rawObj.id } }
        fun fromRawObj(obj: InvObj): CookingFood? = byRawId[obj.id]
    }
}

// ======================================================================================
// Non-fish food data table
// ======================================================================================

enum class CookingNonfish(
    val rawObj: ObjType,
    val cookedObj: ObjType,
    val burntObj: ObjType? = null,
    val levelReq: Int,
    val xp: Double,
    val burnStop: Int = 99,
    val isRangeOnly: Boolean = false,
) {
    // --- Meats ---
    BEEF(
        rawObj = cooking_objs.raw_beef,
        cookedObj = cooking_objs.cooked_meat,
        burntObj = cooking_objs.burnt_meat,
        levelReq = 1, xp = 30.0, burnStop = 40,
    ),
    CHICKEN(
        rawObj = cooking_objs.raw_chicken,
        cookedObj = cooking_objs.cooked_chicken,
        burntObj = cooking_objs.burnt_chicken,
        levelReq = 1, xp = 30.0, burnStop = 40,
    ),
    RAT_MEAT(
        rawObj = cooking_objs.raw_rat_meat,
        cookedObj = cooking_objs.cooked_meat,
        burntObj = cooking_objs.burnt_meat,
        levelReq = 1, xp = 30.0, burnStop = 40,
    ),
    BEAR_MEAT(
        rawObj = cooking_objs.raw_bear_meat,
        cookedObj = cooking_objs.cooked_meat,
        burntObj = cooking_objs.burnt_meat,
        levelReq = 1, xp = 30.0, burnStop = 40,
    ),
    RABBIT(
        rawObj = cooking_objs.raw_rabbit,
        cookedObj = cooking_objs.cooked_rabbit,
        levelReq = 1, xp = 30.0,
    ),
    CHOMPY(
        rawObj = cooking_objs.raw_chompy,
        cookedObj = cooking_objs.cooked_chompy,
        levelReq = 30, xp = 140.0,
    ),
    OOMLIE(
        rawObj = cooking_objs.raw_oomlie,
        cookedObj = cooking_objs.cooked_oomlie,
        burntObj = cooking_objs.burnt_oomlie,
        levelReq = 40, xp = 100.0, burnStop = 70,
    ),
    UGTHANKI(
        rawObj = cooking_objs.raw_ugthanki_meat,
        cookedObj = cooking_objs.cooked_ugthanki_meat,
        burntObj = cooking_objs.burnt_meat,
        levelReq = 1, xp = 40.0, burnStop = 50,
    ),

    // --- Breads ---
    BREAD(
        rawObj = cooking_objs.bread_dough,
        cookedObj = cooking_objs.bread,
        burntObj = cooking_objs.burnt_bread,
        levelReq = 1, xp = 40.0, burnStop = 40,
    ),
    PITTA(
        rawObj = cooking_objs.uncooked_pitta_bread,
        cookedObj = cooking_objs.pitta_bread,
        burntObj = cooking_objs.burnt_pitta_bread,
        levelReq = 1, xp = 40.0, burnStop = 40,
    ),

    // --- Pies ---
    REDBERRY_PIE(
        rawObj = cooking_objs.uncooked_redberry_pie,
        cookedObj = cooking_objs.redberry_pie,
        burntObj = cooking_objs.burnt_pie,
        levelReq = 10, xp = 78.0, burnStop = 40,
    ),
    MEAT_PIE(
        rawObj = cooking_objs.uncooked_meat_pie,
        cookedObj = cooking_objs.meat_pie,
        burntObj = cooking_objs.burnt_pie,
        levelReq = 20, xp = 110.0, burnStop = 50,
    ),
    APPLE_PIE(
        rawObj = cooking_objs.uncooked_apple_pie,
        cookedObj = cooking_objs.apple_pie,
        burntObj = cooking_objs.burnt_pie,
        levelReq = 30, xp = 130.0, burnStop = 60,
    ),

    // --- Cakes ---
    CAKE(
        rawObj = cooking_objs.uncooked_cake,
        cookedObj = cooking_objs.cake,
        burntObj = cooking_objs.burnt_cake,
        levelReq = 40, xp = 180.0, burnStop = 60, isRangeOnly = true,
    ),

    // --- Pizzas ---
    PLAIN_PIZZA(
        rawObj = cooking_objs.uncooked_pizza,
        cookedObj = cooking_objs.plain_pizza,
        burntObj = cooking_objs.burnt_pizza,
        levelReq = 35, xp = 143.0, burnStop = 55,
    ),

    // --- Stews & Curries ---
    STEW(
        rawObj = cooking_objs.uncooked_stew,
        cookedObj = cooking_objs.stew,
        burntObj = cooking_objs.burnt_stew,
        levelReq = 25, xp = 117.0, burnStop = 50, isRangeOnly = true,
    ),
    CURRY(
        rawObj = cooking_objs.uncooked_curry,
        cookedObj = cooking_objs.curry,
        burntObj = cooking_objs.burnt_curry,
        levelReq = 60, xp = 280.0, burnStop = 80, isRangeOnly = true,
    ),

    // --- Wine ---
    WINE(
        rawObj = cooking_objs.jug_unfermented_wine,
        cookedObj = cooking_objs.jug_wine,
        burntObj = cooking_objs.jug_bad_wine,
        levelReq = 35, xp = 200.0, burnStop = 68,
    ),

    // --- Potatoes ---
    POTATO(
        rawObj = cooking_objs.potato,
        cookedObj = cooking_objs.potato_baked,
        burntObj = null, // no burnt potato in cache
        levelReq = 7, xp = 15.0, burnStop = 25, isRangeOnly = true,
    ),
    ;

    companion object {
        private val byRawId: Map<Int, CookingNonfish> by lazy { entries.associateBy { it.rawObj.id } }
        fun fromRawObj(obj: InvObj): CookingNonfish? = byRawId[obj.id]
    }
}

// ======================================================================================
// Local loc references
// ======================================================================================

internal typealias cooking_locs = CookingLocs

internal object CookingLocs : LocReferences() {
    val fire = find("fire")

    // Standard cooking range (Lumbridge kitchen, etc.) — ID 26181 in loc.sym
    val range = find("range")

    // Named cooking ranges
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
            cooking_locs.range,
            cooking_locs.hos_cooking_range,
            cooking_locs.hos_cooking_range_02,
            cooking_locs.ds2_guild_cooking_range,
            cooking_locs.dorgesh_cooking_range1,
            cooking_locs.dorgesh_cooking_range2,
            cooking_locs.dorgesh_nursery_cooking_range,
            cooking_locs.lunar_pirate_cooking_range,
        )

// ======================================================================================
// Local obj references
// ======================================================================================

internal typealias cooking_objs = CookingObjs

internal object CookingObjs : ObjReferences() {
    // --- Raw fish ---
    val raw_shrimp = find("raw_shrimp")
    val raw_anchovies = find("raw_anchovies")
    val raw_sardine = find("raw_sardine")
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
    val raw_dark_crab = find("raw_dark_crab")
    val raw_mantaray = find("raw_mantaray")
    val raw_karambwan = find("tbwt_raw_karambwan")

    // --- Cooked fish ---
    val anchovies = find("anchovies")
    val sardine = find("sardine")
    val mackerel = find("mackerel")
    val trout = find("trout")
    val cod = find("cod")
    val pike = find("pike")
    val salmon = find("salmon")
    val tuna = find("tuna")
    val lobster = find("lobster")
    val bass = find("bass")
    val swordfish = find("swordfish")
    val monkfish = find("monkfish")
    val shark = find("shark")
    val anglerfish = find("anglerfish")
    val dark_crab = find("dark_crab")
    val mantaray = find("mantaray")
    val cooked_karambwan = find("tbwt_cooked_karambwan")

    // --- Burnt fish ---
    val burntfish1 = find("burntfish1")
    val burntfish2 = find("burntfish2")
    val burntfish3 = find("burntfish3")
    val burntfish4 = find("burntfish4")
    val burntfish5 = find("burntfish5")
    val burnt_swordfish = find("burnt_swordfish")
    val burnt_lobster = find("burnt_lobster")
    val burnt_shark = find("burnt_shark")
    val burnt_monkfish = find("burnt_monkfish")
    val burnt_anglerfish = find("burnt_anglerfish")
    val burnt_dark_crab = find("burnt_dark_crab")
    val burnt_mantaray = find("burnt_mantaray")
    val burnt_karambwan = find("tbwt_burnt_karambwan")

    // --- Raw meats ---
    val raw_beef = find("raw_beef")
    val raw_chicken = find("raw_chicken")
    val raw_rat_meat = find("raw_rat_meat")
    val raw_bear_meat = find("raw_bear_meat")
    val raw_rabbit = find("raw_rabbit")
    val raw_chompy = find("raw_chompy")
    val raw_oomlie = find("raw_oomlie")
    val raw_ugthanki_meat = find("raw_ugthanki_meat")

    // --- Cooked meats ---
    val cooked_meat = find("cooked_meat")
    val cooked_chicken = find("cooked_chicken")
    val cooked_rabbit = find("cooked_rabbit")
    val cooked_chompy = find("cooked_chompy")
    val cooked_oomlie = find("cooked_oomlie")
    val cooked_ugthanki_meat = find("cooked_ugthanki_meat")

    // --- Burnt meats ---
    val burnt_meat = find("burnt_meat")
    val burnt_chicken = find("burnt_chicken")
    val burnt_oomlie = find("burnt_oomlie")

    // --- Bread ---
    val bread_dough = find("bread_dough")
    val bread = find("bread")
    val burnt_bread = find("burnt_bread")
    val uncooked_pitta_bread = find("uncooked_pitta_bread")
    val pitta_bread = find("pitta_bread")
    val burnt_pitta_bread = find("burnt_pitta_bread")

    // --- Pies ---
    val uncooked_redberry_pie = find("uncooked_redberry_pie")
    val uncooked_meat_pie = find("uncooked_meat_pie")
    val uncooked_apple_pie = find("uncooked_apple_pie")
    val redberry_pie = find("redberry_pie")
    val meat_pie = find("meat_pie")
    val apple_pie = find("apple_pie")
    val burnt_pie = find("burnt_pie")

    // --- Cakes ---
    val uncooked_cake = find("uncooked_cake")
    val cake = find("cake")
    val chocolate_cake = find("chocolate_cake")
    val burnt_cake = find("burnt_cake")

    // --- Pizzas ---
    val uncooked_pizza = find("uncooked_pizza")
    val plain_pizza = find("plain_pizza")
    val meat_pizza = find("meat_pizza")
    val anchovie_pizza = find("anchovie_pizza")
    val pineapple_pizza = find("pineapple_pizza")
    val burnt_pizza = find("burnt_pizza")

    // --- Stews ---
    val uncooked_stew = find("uncooked_stew")
    val stew = find("stew")
    val burnt_stew = find("burnt_stew")
    val uncooked_curry = find("uncooked_curry")
    val curry = find("curry")
    val burnt_curry = find("burnt_curry")

    // --- Wines ---
    val jug_unfermented_wine = find("jug_unfermented_wine")
    val jug_wine = find("jug_wine")
    val jug_bad_wine = find("jug_bad_wine")

    // --- Potatoes ---
    val potato = find("potato")
    val potato_baked = find("potato_baked")

    // --- Equipment ---
    val gauntlets_of_cooking = find("gauntlets_of_cooking")
}

// ======================================================================================
// Local seq references for cooking animations
// ======================================================================================

internal typealias cooking_seqs = CookingSeqs

internal object CookingSeqs : SeqReferences() {
    val human_cooking = find("human_cooking")
    val human_firecooking = find("human_firecooking")
}
