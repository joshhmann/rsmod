package org.rsmod.content.skills.smithing.scripts

// IMPLEMENTATION NOTES:
// Smithing is implemented in two distinct phases:
//
// 1. SMELTING (furnace): Use item-on-item — each ore/ore-combo + hammer (actually ore-on-ore
//    for coal+metal, or single ore for bronze) → bar via onOpHeldU.
//    For simplicity this first batch implements: bronze (copper+tin), iron, steel (iron+coal),
//    mithril (mithril+coal×4 by wiki… but we do 1 coal here), adamant, rune.
//
// 2. SMITHING AT ANVIL: bar + hammer → product via onOpHeldU.
//    Each bar type maps to a list of items the player can make.
//    For this implementation we produce the first (simplest) item of each tier automatically
//    when bar+hammer is used (no smithing interface). Full interface support is a future TODO.
//
// Animations:
//   seqs.human_smithing — added to BaseSeqs.kt alongside this module.
//
// TODO:
//   - Smithing interface item selection (make-X quantity now supported per selected product)
//   - Gold bars / silver bars from gold/silver ore (jewellery crafting)
//   - Cannonballs, nails, bolts
//   - Platebody requiring 5 bars

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.smithingLvl
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

private data class SmeltDef(
    val bar: ObjType,
    val ore1: ObjType,
    val ore2: ObjType? = null,
    val levelReq: Int,
    val xp: Double,
)

private data class AnvilDef(
    val bar: ObjType,
    val barCount: Int = 1,
    val levelReq: Int,
    val xp: Double,
    val product: ObjType,
    val productCount: Int = 1,
)

class Smithing @Inject constructor(private val xpMods: XpModifiers) : PluginScript() {

    override fun ScriptContext.startup() {
        // ---- Smelting: ore + ore → bar (use-item-on-item) ----
        // Bronze: copper + tin
        onOpHeldU(objs.copper_ore, objs.tin_ore) { smelt(SMELT_BRONZE) }
        // Iron: iron ore alone
        onOpHeldU(objs.iron_ore, objs.hammer) { smeltSingle(SMELT_IRON) }
        // Steel: iron + coal
        onOpHeldU(objs.iron_ore, objs.coal) { smelt(SMELT_STEEL) }
        // Mithril: mithril + coal
        onOpHeldU(objs.mithril_ore, objs.coal) { smelt(SMELT_MITHRIL) }
        // Adamant: adamantite + coal
        onOpHeldU(objs.adamantite_ore, objs.coal) { smelt(SMELT_ADAMANT) }
        // Rune: runite + coal
        onOpHeldU(objs.runite_ore, objs.coal) { smelt(SMELT_RUNE) }
        // Gold / silver — single ore
        onOpHeldU(objs.gold_ore, objs.hammer) { smeltSingle(SMELT_GOLD) }
        onOpHeldU(objs.silver_ore, objs.hammer) { smeltSingle(SMELT_SILVER) }

        // ---- Smithing: bar + hammer → product at anvil ----
        // Register one handler per bar type (first product per bar is produced without a menu).
        for (def in ANVIL_DEFS.distinctBy { it.bar.id }) {
            onOpHeldU(def.bar, objs.hammer) { smithBar(def) }
        }
    }

    // ---- Smelting helpers ----

    private suspend fun ProtectedAccess.smelt(def: SmeltDef) {
        if (player.smithingLvl < def.levelReq) {
            mes("You need a Smithing level of ${def.levelReq} to smelt this bar.")
            return
        }
        val ore2 = def.ore2 ?: return
        if (!inv.contains(def.ore1) || !inv.contains(ore2)) {
            mes("You don't have the required ores to smelt this bar.")
            return
        }
        invDel(inv, def.ore1, count = 1)
        invDel(inv, ore2, count = 1)
        anim(seqs.human_smithing)
        delay(5)
        val xp = def.xp * xpMods.get(player, stats.smithing)
        statAdvance(stats.smithing, xp)
        invAdd(inv, def.bar)
        mes("You smelt the ores into a bar.")
    }

    /** Smelting from a single ore (iron, gold, silver). */
    private suspend fun ProtectedAccess.smeltSingle(def: SmeltDef) {
        if (player.smithingLvl < def.levelReq) {
            mes("You need a Smithing level of ${def.levelReq} to smelt this bar.")
            return
        }
        if (!inv.contains(def.ore1)) {
            mes("You don't have the required ore to smelt this bar.")
            return
        }
        invDel(inv, def.ore1, count = 1)
        anim(seqs.human_smithing)
        delay(5)
        val xp = def.xp * xpMods.get(player, stats.smithing)
        statAdvance(stats.smithing, xp)
        invAdd(inv, def.bar)
        mes("You smelt the ore into a bar.")
    }

    // ---- Smithing at anvil helper ----

    private suspend fun ProtectedAccess.smithBar(def: AnvilDef) {
        if (player.smithingLvl < def.levelReq) {
            mes("You need a Smithing level of ${def.levelReq} to smith this.")
            return
        }
        if (invTotal(inv, def.bar) < def.barCount) {
            mes("You don't have enough bars to smith this item.")
            return
        }
        val count = countDialog("How many would you like to make?")
        if (count == 0) {
            return
        }
        val startCoords = player.coords
        repeat(count) {
            if (player.coords != startCoords) {
                return
            }
            val removed = invDel(inv, def.bar, count = def.barCount, strict = true)
            if (removed.failure) {
                mes("You don't have enough bars to smith this item.")
                return
            }
            anim(seqs.human_smithing)
            delay(5)
            val xp = def.xp * xpMods.get(player, stats.smithing)
            statAdvance(stats.smithing, xp)
            invAdd(inv, def.product, count = def.productCount)
            mes("You hammer the bars into an item.")
        }
    }

    companion object {
        // ---- Smelt definitions (wiki-accurate XP and level requirements) ----
        private val SMELT_BRONZE =
            SmeltDef(objs.bronze_bar, objs.copper_ore, objs.tin_ore, levelReq = 1, xp = 6.2)
        private val SMELT_IRON =
            SmeltDef(objs.iron_bar, objs.iron_ore, null, levelReq = 15, xp = 12.5)
        private val SMELT_STEEL =
            SmeltDef(objs.steel_bar, objs.iron_ore, objs.coal, levelReq = 30, xp = 17.5)
        private val SMELT_MITHRIL =
            SmeltDef(objs.mithril_bar, objs.mithril_ore, objs.coal, levelReq = 50, xp = 30.0)
        private val SMELT_ADAMANT =
            SmeltDef(objs.adamant_bar, objs.adamantite_ore, objs.coal, levelReq = 70, xp = 37.5)
        private val SMELT_RUNE =
            SmeltDef(objs.rune_bar, objs.runite_ore, objs.coal, levelReq = 85, xp = 50.0)
        private val SMELT_GOLD =
            SmeltDef(objs.gold_bar, objs.gold_ore, null, levelReq = 40, xp = 22.5)
        private val SMELT_SILVER =
            SmeltDef(objs.silver_bar, objs.silver_ore, null, levelReq = 20, xp = 13.7)

        // ---- Anvil definitions (wiki-accurate XP, level requirements, bar costs) ----
        // Each bar registers one handler; without a smithing menu the simplest product is made.
        // Full product lists per bar are defined but only the first entry per bar is registered
        // in startup(). Extend to a full menu UI in a future iteration.
        private val ANVIL_DEFS: List<AnvilDef> =
            listOf(
                // Bronze (level 1) — dagger costs 1 bar
                AnvilDef(
                    objs.bronze_bar,
                    barCount = 1,
                    levelReq = 1,
                    xp = 12.5,
                    product = objs.bronze_dagger,
                ),
                AnvilDef(
                    objs.bronze_bar,
                    barCount = 1,
                    levelReq = 1,
                    xp = 12.5,
                    product = objs.bronze_sword,
                ),
                AnvilDef(
                    objs.bronze_bar,
                    barCount = 1,
                    levelReq = 2,
                    xp = 12.5,
                    product = objs.bronze_mace,
                ),
                AnvilDef(
                    objs.bronze_bar,
                    barCount = 1,
                    levelReq = 3,
                    xp = 25.0,
                    product = objs.bronze_med_helm,
                ),
                AnvilDef(
                    objs.bronze_bar,
                    barCount = 2,
                    levelReq = 5,
                    xp = 25.0,
                    product = objs.bronze_scimitar,
                ),
                AnvilDef(
                    objs.bronze_bar,
                    barCount = 2,
                    levelReq = 6,
                    xp = 25.0,
                    product = objs.bronze_sq_shield,
                ),
                AnvilDef(
                    objs.bronze_bar,
                    barCount = 2,
                    levelReq = 8,
                    xp = 25.0,
                    product = objs.bronze_longsword,
                ),
                AnvilDef(
                    objs.bronze_bar,
                    barCount = 2,
                    levelReq = 9,
                    xp = 25.0,
                    product = objs.bronze_full_helm,
                ),
                AnvilDef(
                    objs.bronze_bar,
                    barCount = 3,
                    levelReq = 11,
                    xp = 37.5,
                    product = objs.bronze_plateskirt,
                ),
                AnvilDef(
                    objs.bronze_bar,
                    barCount = 3,
                    levelReq = 11,
                    xp = 37.5,
                    product = objs.bronze_platelegs,
                ),
                AnvilDef(
                    objs.bronze_bar,
                    barCount = 3,
                    levelReq = 12,
                    xp = 37.5,
                    product = objs.bronze_chainbody,
                ),
                AnvilDef(
                    objs.bronze_bar,
                    barCount = 3,
                    levelReq = 13,
                    xp = 37.5,
                    product = objs.bronze_kiteshield,
                ),
                AnvilDef(
                    objs.bronze_bar,
                    barCount = 3,
                    levelReq = 14,
                    xp = 37.5,
                    product = objs.bronze_2h_sword,
                ),
                AnvilDef(
                    objs.bronze_bar,
                    barCount = 5,
                    levelReq = 18,
                    xp = 62.5,
                    product = objs.bronze_platebody,
                ),
                // Iron (level 15)
                AnvilDef(
                    objs.iron_bar,
                    barCount = 1,
                    levelReq = 15,
                    xp = 25.0,
                    product = objs.iron_dagger,
                ),
                AnvilDef(
                    objs.iron_bar,
                    barCount = 1,
                    levelReq = 15,
                    xp = 25.0,
                    product = objs.iron_sword,
                ),
                AnvilDef(
                    objs.iron_bar,
                    barCount = 1,
                    levelReq = 16,
                    xp = 25.0,
                    product = objs.iron_mace,
                ),
                AnvilDef(
                    objs.iron_bar,
                    barCount = 1,
                    levelReq = 18,
                    xp = 25.0,
                    product = objs.iron_med_helm,
                ),
                AnvilDef(
                    objs.iron_bar,
                    barCount = 2,
                    levelReq = 20,
                    xp = 50.0,
                    product = objs.iron_scimitar,
                ),
                AnvilDef(
                    objs.iron_bar,
                    barCount = 2,
                    levelReq = 21,
                    xp = 50.0,
                    product = objs.iron_sq_shield,
                ),
                AnvilDef(
                    objs.iron_bar,
                    barCount = 2,
                    levelReq = 23,
                    xp = 50.0,
                    product = objs.iron_longsword,
                ),
                AnvilDef(
                    objs.iron_bar,
                    barCount = 2,
                    levelReq = 24,
                    xp = 50.0,
                    product = objs.iron_full_helm,
                ),
                AnvilDef(
                    objs.iron_bar,
                    barCount = 3,
                    levelReq = 26,
                    xp = 75.0,
                    product = objs.iron_plateskirt,
                ),
                AnvilDef(
                    objs.iron_bar,
                    barCount = 3,
                    levelReq = 26,
                    xp = 75.0,
                    product = objs.iron_platelegs,
                ),
                AnvilDef(
                    objs.iron_bar,
                    barCount = 3,
                    levelReq = 27,
                    xp = 75.0,
                    product = objs.iron_chainbody,
                ),
                AnvilDef(
                    objs.iron_bar,
                    barCount = 3,
                    levelReq = 28,
                    xp = 75.0,
                    product = objs.iron_kiteshield,
                ),
                AnvilDef(
                    objs.iron_bar,
                    barCount = 3,
                    levelReq = 29,
                    xp = 75.0,
                    product = objs.iron_2h_sword,
                ),
                AnvilDef(
                    objs.iron_bar,
                    barCount = 5,
                    levelReq = 33,
                    xp = 125.0,
                    product = objs.iron_platebody,
                ),
                // Steel (level 30)
                AnvilDef(
                    objs.steel_bar,
                    barCount = 1,
                    levelReq = 30,
                    xp = 37.5,
                    product = objs.steel_dagger,
                ),
                AnvilDef(
                    objs.steel_bar,
                    barCount = 1,
                    levelReq = 30,
                    xp = 37.5,
                    product = objs.steel_sword,
                ),
                AnvilDef(
                    objs.steel_bar,
                    barCount = 1,
                    levelReq = 31,
                    xp = 37.5,
                    product = objs.steel_mace,
                ),
                AnvilDef(
                    objs.steel_bar,
                    barCount = 1,
                    levelReq = 33,
                    xp = 37.5,
                    product = objs.steel_med_helm,
                ),
                AnvilDef(
                    objs.steel_bar,
                    barCount = 2,
                    levelReq = 35,
                    xp = 75.0,
                    product = objs.steel_scimitar,
                ),
                AnvilDef(
                    objs.steel_bar,
                    barCount = 2,
                    levelReq = 36,
                    xp = 75.0,
                    product = objs.steel_sq_shield,
                ),
                AnvilDef(
                    objs.steel_bar,
                    barCount = 2,
                    levelReq = 38,
                    xp = 75.0,
                    product = objs.steel_longsword,
                ),
                AnvilDef(
                    objs.steel_bar,
                    barCount = 2,
                    levelReq = 39,
                    xp = 75.0,
                    product = objs.steel_full_helm,
                ),
                AnvilDef(
                    objs.steel_bar,
                    barCount = 3,
                    levelReq = 41,
                    xp = 112.5,
                    product = objs.steel_plateskirt,
                ),
                AnvilDef(
                    objs.steel_bar,
                    barCount = 3,
                    levelReq = 41,
                    xp = 112.5,
                    product = objs.steel_platelegs,
                ),
                AnvilDef(
                    objs.steel_bar,
                    barCount = 3,
                    levelReq = 42,
                    xp = 112.5,
                    product = objs.steel_chainbody,
                ),
                AnvilDef(
                    objs.steel_bar,
                    barCount = 3,
                    levelReq = 43,
                    xp = 112.5,
                    product = objs.steel_kiteshield,
                ),
                AnvilDef(
                    objs.steel_bar,
                    barCount = 3,
                    levelReq = 44,
                    xp = 112.5,
                    product = objs.steel_2h_sword,
                ),
                AnvilDef(
                    objs.steel_bar,
                    barCount = 5,
                    levelReq = 48,
                    xp = 187.5,
                    product = objs.steel_platebody,
                ),
                // Mithril (level 50)
                AnvilDef(
                    objs.mithril_bar,
                    barCount = 1,
                    levelReq = 50,
                    xp = 50.0,
                    product = objs.mithril_dagger,
                ),
                AnvilDef(
                    objs.mithril_bar,
                    barCount = 1,
                    levelReq = 50,
                    xp = 50.0,
                    product = objs.mithril_sword,
                ),
                AnvilDef(
                    objs.mithril_bar,
                    barCount = 1,
                    levelReq = 51,
                    xp = 50.0,
                    product = objs.mithril_mace,
                ),
                AnvilDef(
                    objs.mithril_bar,
                    barCount = 1,
                    levelReq = 53,
                    xp = 50.0,
                    product = objs.mithril_med_helm,
                ),
                AnvilDef(
                    objs.mithril_bar,
                    barCount = 2,
                    levelReq = 55,
                    xp = 100.0,
                    product = objs.mithril_scimitar,
                ),
                AnvilDef(
                    objs.mithril_bar,
                    barCount = 2,
                    levelReq = 56,
                    xp = 100.0,
                    product = objs.mithril_sq_shield,
                ),
                AnvilDef(
                    objs.mithril_bar,
                    barCount = 2,
                    levelReq = 58,
                    xp = 100.0,
                    product = objs.mithril_longsword,
                ),
                AnvilDef(
                    objs.mithril_bar,
                    barCount = 2,
                    levelReq = 59,
                    xp = 100.0,
                    product = objs.mithril_full_helm,
                ),
                AnvilDef(
                    objs.mithril_bar,
                    barCount = 3,
                    levelReq = 61,
                    xp = 150.0,
                    product = objs.mithril_plateskirt,
                ),
                AnvilDef(
                    objs.mithril_bar,
                    barCount = 3,
                    levelReq = 61,
                    xp = 150.0,
                    product = objs.mithril_platelegs,
                ),
                AnvilDef(
                    objs.mithril_bar,
                    barCount = 3,
                    levelReq = 62,
                    xp = 150.0,
                    product = objs.mithril_chainbody,
                ),
                AnvilDef(
                    objs.mithril_bar,
                    barCount = 3,
                    levelReq = 63,
                    xp = 150.0,
                    product = objs.mithril_kiteshield,
                ),
                AnvilDef(
                    objs.mithril_bar,
                    barCount = 3,
                    levelReq = 64,
                    xp = 150.0,
                    product = objs.mithril_2h_sword,
                ),
                AnvilDef(
                    objs.mithril_bar,
                    barCount = 5,
                    levelReq = 68,
                    xp = 250.0,
                    product = objs.mithril_platebody,
                ),
                // Adamant (level 70)
                AnvilDef(
                    objs.adamant_bar,
                    barCount = 1,
                    levelReq = 70,
                    xp = 62.5,
                    product = objs.adamant_dagger,
                ),
                AnvilDef(
                    objs.adamant_bar,
                    barCount = 1,
                    levelReq = 70,
                    xp = 62.5,
                    product = objs.adamant_sword,
                ),
                AnvilDef(
                    objs.adamant_bar,
                    barCount = 1,
                    levelReq = 71,
                    xp = 62.5,
                    product = objs.adamant_mace,
                ),
                AnvilDef(
                    objs.adamant_bar,
                    barCount = 1,
                    levelReq = 73,
                    xp = 62.5,
                    product = objs.adamant_med_helm,
                ),
                AnvilDef(
                    objs.adamant_bar,
                    barCount = 2,
                    levelReq = 75,
                    xp = 125.0,
                    product = objs.adamant_scimitar,
                ),
                AnvilDef(
                    objs.adamant_bar,
                    barCount = 2,
                    levelReq = 76,
                    xp = 125.0,
                    product = objs.adamant_sq_shield,
                ),
                AnvilDef(
                    objs.adamant_bar,
                    barCount = 2,
                    levelReq = 78,
                    xp = 125.0,
                    product = objs.adamant_longsword,
                ),
                AnvilDef(
                    objs.adamant_bar,
                    barCount = 2,
                    levelReq = 79,
                    xp = 125.0,
                    product = objs.adamant_full_helm,
                ),
                AnvilDef(
                    objs.adamant_bar,
                    barCount = 3,
                    levelReq = 81,
                    xp = 187.5,
                    product = objs.adamant_plateskirt,
                ),
                AnvilDef(
                    objs.adamant_bar,
                    barCount = 3,
                    levelReq = 81,
                    xp = 187.5,
                    product = objs.adamant_platelegs,
                ),
                AnvilDef(
                    objs.adamant_bar,
                    barCount = 3,
                    levelReq = 82,
                    xp = 187.5,
                    product = objs.adamant_chainbody,
                ),
                AnvilDef(
                    objs.adamant_bar,
                    barCount = 3,
                    levelReq = 83,
                    xp = 187.5,
                    product = objs.adamant_kiteshield,
                ),
                AnvilDef(
                    objs.adamant_bar,
                    barCount = 3,
                    levelReq = 84,
                    xp = 187.5,
                    product = objs.adamant_2h_sword,
                ),
                AnvilDef(
                    objs.adamant_bar,
                    barCount = 5,
                    levelReq = 88,
                    xp = 312.5,
                    product = objs.adamant_platebody,
                ),
                // Rune (level 85)
                AnvilDef(
                    objs.rune_bar,
                    barCount = 1,
                    levelReq = 85,
                    xp = 75.0,
                    product = objs.rune_dagger,
                ),
                AnvilDef(
                    objs.rune_bar,
                    barCount = 1,
                    levelReq = 85,
                    xp = 75.0,
                    product = objs.rune_sword,
                ),
                AnvilDef(
                    objs.rune_bar,
                    barCount = 1,
                    levelReq = 86,
                    xp = 75.0,
                    product = objs.rune_mace,
                ),
                AnvilDef(
                    objs.rune_bar,
                    barCount = 1,
                    levelReq = 88,
                    xp = 75.0,
                    product = objs.rune_med_helm,
                ),
                AnvilDef(
                    objs.rune_bar,
                    barCount = 2,
                    levelReq = 90,
                    xp = 150.0,
                    product = objs.rune_scimitar,
                ),
                AnvilDef(
                    objs.rune_bar,
                    barCount = 2,
                    levelReq = 91,
                    xp = 150.0,
                    product = objs.rune_sq_shield,
                ),
                AnvilDef(
                    objs.rune_bar,
                    barCount = 2,
                    levelReq = 93,
                    xp = 150.0,
                    product = objs.rune_longsword,
                ),
                AnvilDef(
                    objs.rune_bar,
                    barCount = 2,
                    levelReq = 94,
                    xp = 150.0,
                    product = objs.rune_full_helm,
                ),
                AnvilDef(
                    objs.rune_bar,
                    barCount = 3,
                    levelReq = 96,
                    xp = 225.0,
                    product = objs.rune_plateskirt,
                ),
                AnvilDef(
                    objs.rune_bar,
                    barCount = 3,
                    levelReq = 96,
                    xp = 225.0,
                    product = objs.rune_platelegs,
                ),
                AnvilDef(
                    objs.rune_bar,
                    barCount = 3,
                    levelReq = 97,
                    xp = 225.0,
                    product = objs.rune_chainbody,
                ),
                AnvilDef(
                    objs.rune_bar,
                    barCount = 3,
                    levelReq = 98,
                    xp = 225.0,
                    product = objs.rune_kiteshield,
                ),
                AnvilDef(
                    objs.rune_bar,
                    barCount = 3,
                    levelReq = 99,
                    xp = 225.0,
                    product = objs.rune_2h_sword,
                ),
                AnvilDef(
                    objs.rune_bar,
                    barCount = 5,
                    levelReq = 99,
                    xp = 375.0,
                    product = objs.rune_platebody,
                ),
            )
    }
}
