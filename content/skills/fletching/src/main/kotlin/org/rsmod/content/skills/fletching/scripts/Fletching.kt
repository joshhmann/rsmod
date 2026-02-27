package org.rsmod.content.skills.fletching.scripts

// IMPLEMENTATION NOTES:
// Fletching is fully inventory-based (no loc interaction needed).
// Five interaction types:
//
// 1. KNIFE ON LOG → unstrung bow (shortbow only - longbow selection TODO)
//    onOpHeldU(log, objs.knife) — delay 4 ticks per bow, uses countDialog Make-X.
//
// 2. BOWSTRING ON UNSTRUNG BOW → strung bow
//    onOpHeldU(unstrung_bow, objs.bowstring) — delay 4 ticks per bow, uses countDialog Make-X.
//
// 3. ARROW SHAFTS ON FEATHERS → headless arrows (15 per action)
//    onOpHeldU(objs.arrow_shaft, objs.feather) — delay 4 ticks, 15 headless arrows, uses
// countDialog Make-X.
//
// 4. ARROWHEAD ON HEADLESS ARROWS → 15 arrows
//    onOpHeldU(arrowhead, objs.headless_arrow) — delay 4 ticks, 15 arrows produced, uses
// countDialog Make-X.
//
// 5. UNFEATHERED BOLT ON FEATHERS → bolts (10 per action)
//    onOpHeldU(unfeathered_bolt, objs.feather) — delay 4 ticks, 10 bolts, uses countDialog Make-X.
//
// NOTE: All fletching actions now use countDialog Make-X selection.
// Movement cancels the action (checks startCoords each iteration).
//
// Animations:
//   seqs.human_fletching_knife — added to BaseSeqs.kt alongside this module.

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.fletchingLvl
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.content.skills.fletching.configs.FletchingObjs as fletchObjs
import org.rsmod.content.skills.fletching.configs.FletchingSeqs as fletchSeqs
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

private data class BowDef(
    val log: ObjType,
    val shortbow_u: ObjType,
    val longbow_u: ObjType,
    val shortbowLevelReq: Int,
    val longbowLevelReq: Int,
    val shortbowXp: Double,
    val longbowXp: Double,
)

private data class StringDef(
    val unstrung: ObjType,
    val strung: ObjType,
    val levelReq: Int,
    val xp: Double,
)

private data class ArrowDef(
    val arrowhead: ObjType,
    val arrow: ObjType,
    val levelReq: Int,
    val xp: Double,
)

private data class BoltDef(
    val unfeathered: ObjType,
    val finished: ObjType,
    val levelReq: Int,
    val xp: Double,
)

class Fletching
@Inject
constructor(private val xpMods: XpModifiers, private val objRepo: ObjRepository) : PluginScript() {

    override fun ScriptContext.startup() {
        // ---- Knife on log → unstrung bow (shortbow) ----
        // Each log produces a shortbow; pressing again produces a longbow.
        // For simplicity without a menu: knife + log → shortbow_u (lower level req).
        for (def in BOW_DEFS) {
            onOpHeldU(def.log, objs.knife) { fletchBow(def) }
        }

        // ---- Bowstring on unstrung bow → strung bow ----
        for (def in STRING_DEFS) {
            onOpHeldU(def.unstrung, fletchObjs.bowstring) { stringBow(def) }
        }

        // ---- Arrow shafts + feathers → headless arrows (15 per action) ----
        onOpHeldU(objs.arrow_shaft, objs.feather) { makeHeadlessArrows() }

        // ---- Arrowhead + headless arrow → arrows (15 per action) ----
        for (def in ARROW_DEFS) {
            onOpHeldU(def.arrowhead, objs.headless_arrow) { fletchArrows(def) }
        }

        // ---- Unfeathered bolts + feathers → bolts (10 per action) ----
        for (def in BOLT_DEFS) {
            onOpHeldU(def.unfeathered, objs.feather) { fletchBolts(def) }
        }
    }

    // ---- Fletching helpers ----

    private suspend fun ProtectedAccess.fletchBow(def: BowDef) {
        if (player.fletchingLvl < def.shortbowLevelReq) {
            mes("You need a Fletching level of ${def.shortbowLevelReq} to fletch this bow.")
            return
        }
        if (!inv.contains(def.log)) {
            mes("You don't have any logs to fletch.")
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
            val deleted = invDel(inv, def.log, count = 1, strict = true)
            if (deleted.failure) {
                mes("You don't have any logs to fletch.")
                return
            }
            anim(fletchSeqs.human_fletching_knife)
            delay(4)
            val xp = def.shortbowXp * xpMods.get(player, stats.fletching)
            statAdvance(stats.fletching, xp)
            invAddOrDrop(objRepo, def.shortbow_u)
            mes("You fletch the logs into an unstrung bow.")
        }
    }

    private suspend fun ProtectedAccess.stringBow(def: StringDef) {
        if (player.fletchingLvl < def.levelReq) {
            mes("You need a Fletching level of ${def.levelReq} to string this bow.")
            return
        }
        if (!inv.contains(def.unstrung) || !inv.contains(fletchObjs.bowstring)) {
            mes("You need an unstrung bow and a bowstring.")
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
            val removedBow = invDel(inv, def.unstrung, count = 1, strict = true)
            val removedString = invDel(inv, fletchObjs.bowstring, count = 1, strict = true)
            if (removedBow.failure || removedString.failure) {
                mes("You need an unstrung bow and a bowstring.")
                return
            }
            anim(fletchSeqs.human_fletching_knife)
            delay(4)
            val xp = def.xp * xpMods.get(player, stats.fletching)
            statAdvance(stats.fletching, xp)
            invAddOrDrop(objRepo, def.strung)
            mes("You string the bow.")
        }
    }

    private suspend fun ProtectedAccess.fletchArrows(def: ArrowDef) {
        if (player.fletchingLvl < def.levelReq) {
            mes("You need a Fletching level of ${def.levelReq} to make these arrows.")
            return
        }
        if (!inv.contains(def.arrowhead) || !inv.contains(objs.headless_arrow)) {
            mes("You don't have the materials to make arrows.")
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
            val removedHead = invDel(inv, def.arrowhead, count = 1, strict = true)
            val removedShaft = invDel(inv, objs.headless_arrow, count = 1, strict = true)
            if (removedHead.failure || removedShaft.failure) {
                mes("You don't have the materials to make arrows.")
                return
            }
            anim(fletchSeqs.human_fletching_knife)
            delay(4)
            val xp = def.xp * xpMods.get(player, stats.fletching)
            statAdvance(stats.fletching, xp)
            invAddOrDrop(objRepo, def.arrow, count = 15)
            mes("You attach the arrowheads to the shafts.")
        }
    }

    // ---- Headless arrow creation ----
    private suspend fun ProtectedAccess.makeHeadlessArrows() {
        // Requires 1 Fletching to attach feathers to arrow shafts
        if (player.fletchingLvl < 1) {
            mes("You need a Fletching level of 1 to make headless arrows.")
            return
        }
        if (!inv.contains(objs.arrow_shaft) || !inv.contains(objs.feather)) {
            mes("You need arrow shafts and feathers to make headless arrows.")
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
            val removedShaft = invDel(inv, objs.arrow_shaft, count = 1, strict = true)
            val removedFeather = invDel(inv, objs.feather, count = 1, strict = true)
            if (removedShaft.failure || removedFeather.failure) {
                mes("You need arrow shafts and feathers to make headless arrows.")
                return
            }
            anim(fletchSeqs.human_fletching_knife)
            delay(4)
            val xp = 1.0 * xpMods.get(player, stats.fletching)
            statAdvance(stats.fletching, xp)
            invAddOrDrop(objRepo, objs.headless_arrow, count = 15)
            mes("You attach feathers to the arrow shafts.")
        }
    }

    // ---- Bolt creation ----
    private suspend fun ProtectedAccess.fletchBolts(def: BoltDef) {
        if (player.fletchingLvl < def.levelReq) {
            mes("You need a Fletching level of ${def.levelReq} to make these bolts.")
            return
        }
        if (!inv.contains(def.unfeathered) || !inv.contains(objs.feather)) {
            mes("You need unfinished bolts and feathers.")
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
            val removedBolt = invDel(inv, def.unfeathered, count = 1, strict = true)
            val removedFeather = invDel(inv, objs.feather, count = 1, strict = true)
            if (removedBolt.failure || removedFeather.failure) {
                mes("You need unfinished bolts and feathers.")
                return
            }
            anim(fletchSeqs.human_fletching_knife)
            delay(4)
            val xp = def.xp * xpMods.get(player, stats.fletching)
            statAdvance(stats.fletching, xp)
            invAddOrDrop(objRepo, def.finished, count = 10)
            mes("You attach feathers to the bolts.")
        }
    }

    companion object {
        // Wiki-accurate XP and level requirements (rev 228)
        private val BOW_DEFS: List<BowDef> =
            listOf(
                BowDef(
                    objs.logs,
                    fletchObjs.shortbow_u,
                    fletchObjs.longbow_u,
                    shortbowLevelReq = 5,
                    longbowLevelReq = 10,
                    shortbowXp = 5.0,
                    longbowXp = 10.0,
                ),
                BowDef(
                    objs.oak_logs,
                    fletchObjs.oak_shortbow_u,
                    fletchObjs.oak_longbow_u,
                    shortbowLevelReq = 20,
                    longbowLevelReq = 25,
                    shortbowXp = 16.5,
                    longbowXp = 25.0,
                ),
                BowDef(
                    objs.willow_logs,
                    fletchObjs.willow_shortbow_u,
                    fletchObjs.willow_longbow_u,
                    shortbowLevelReq = 35,
                    longbowLevelReq = 40,
                    shortbowXp = 33.3,
                    longbowXp = 41.5,
                ),
                BowDef(
                    objs.maple_logs,
                    fletchObjs.maple_shortbow_u,
                    fletchObjs.maple_longbow_u,
                    shortbowLevelReq = 50,
                    longbowLevelReq = 55,
                    shortbowXp = 50.0,
                    longbowXp = 58.3,
                ),
                BowDef(
                    objs.yew_logs,
                    fletchObjs.yew_shortbow_u,
                    fletchObjs.yew_longbow_u,
                    shortbowLevelReq = 65,
                    longbowLevelReq = 70,
                    shortbowXp = 67.5,
                    longbowXp = 75.0,
                ),
                BowDef(
                    objs.magic_logs,
                    fletchObjs.magic_shortbow_u,
                    fletchObjs.magic_longbow_u,
                    shortbowLevelReq = 80,
                    longbowLevelReq = 85,
                    shortbowXp = 83.3,
                    longbowXp = 91.5,
                ),
            )

        private val STRING_DEFS: List<StringDef> =
            listOf(
                StringDef(fletchObjs.shortbow_u, objs.shortbow, levelReq = 5, xp = 5.0),
                StringDef(fletchObjs.longbow_u, objs.longbow, levelReq = 10, xp = 10.0),
                StringDef(fletchObjs.oak_shortbow_u, objs.oak_shortbow, levelReq = 20, xp = 16.5),
                StringDef(fletchObjs.oak_longbow_u, objs.oak_longbow, levelReq = 25, xp = 25.0),
                StringDef(
                    fletchObjs.willow_shortbow_u,
                    objs.willow_shortbow,
                    levelReq = 35,
                    xp = 33.3,
                ),
                StringDef(
                    fletchObjs.willow_longbow_u,
                    objs.willow_longbow,
                    levelReq = 40,
                    xp = 41.5,
                ),
                StringDef(
                    fletchObjs.maple_shortbow_u,
                    objs.maple_shortbow,
                    levelReq = 50,
                    xp = 50.0,
                ),
                StringDef(fletchObjs.maple_longbow_u, objs.maple_longbow, levelReq = 55, xp = 58.3),
                StringDef(fletchObjs.yew_shortbow_u, objs.yew_shortbow, levelReq = 65, xp = 67.5),
                StringDef(fletchObjs.yew_longbow_u, objs.yew_longbow, levelReq = 70, xp = 75.0),
                StringDef(
                    fletchObjs.magic_shortbow_u,
                    objs.magic_shortbow,
                    levelReq = 80,
                    xp = 83.3,
                ),
                StringDef(fletchObjs.magic_longbow_u, objs.magic_longbow, levelReq = 85, xp = 91.5),
            )

        private val ARROW_DEFS: List<ArrowDef> =
            listOf(
                ArrowDef(fletchObjs.bronze_arrowhead, objs.bronze_arrow, levelReq = 1, xp = 1.3),
                ArrowDef(fletchObjs.iron_arrowhead, objs.iron_arrow, levelReq = 15, xp = 2.5),
                ArrowDef(fletchObjs.steel_arrowhead, objs.steel_arrow, levelReq = 30, xp = 3.8),
                ArrowDef(fletchObjs.mithril_arrowhead, objs.mithril_arrow, levelReq = 45, xp = 5.0),
                ArrowDef(fletchObjs.adamant_arrowhead, objs.adamant_arrow, levelReq = 60, xp = 7.0),
                ArrowDef(fletchObjs.rune_arrowhead, objs.rune_arrow, levelReq = 75, xp = 10.0),
            )

        private val BOLT_DEFS: List<BoltDef> =
            listOf(
                BoltDef(fletchObjs.bronze_bolts_u, fletchObjs.bronze_bolts, levelReq = 9, xp = 0.5),
                BoltDef(fletchObjs.iron_bolts_u, fletchObjs.iron_bolts, levelReq = 39, xp = 1.5),
                BoltDef(fletchObjs.steel_bolts_u, fletchObjs.steel_bolts, levelReq = 46, xp = 3.5),
                BoltDef(
                    fletchObjs.mithril_bolts_u,
                    fletchObjs.mithril_bolts,
                    levelReq = 54,
                    xp = 5.0,
                ),
                BoltDef(
                    fletchObjs.adamant_bolts_u,
                    fletchObjs.adamant_bolts,
                    levelReq = 61,
                    xp = 7.0,
                ),
                BoltDef(fletchObjs.rune_bolts_u, fletchObjs.rune_bolts, levelReq = 69, xp = 10.0),
            )
    }
}
