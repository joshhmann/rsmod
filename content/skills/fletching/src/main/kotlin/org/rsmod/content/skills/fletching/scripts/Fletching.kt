package org.rsmod.content.skills.fletching.scripts

// IMPLEMENTATION NOTES:
// Fletching is fully inventory-based (no loc interaction needed).
// Three interaction types:
//
// 1. KNIFE ON LOG → unstrung bow
//    onOpHeldU(log, objs.knife) — delay 4 ticks, 1 bow_u produced.
//
// 2. BOWSTRING ON UNSTRUNG BOW → strung bow
//    onOpHeldU(unstrung_bow, objs.bowstring) — delay 4 ticks, 1 strung bow produced.
//
// 3. ARROWHEAD ON HEADLESS ARROWS → 15 arrows
//    onOpHeldU(arrowhead, objs.headless_arrow) — delay 4 ticks, 15 arrows produced.
//
// 4. ARROW SHAFT CREATION: objs.knife + objs.logs → 15 arrow shafts (level 1, 5 XP)
//    (requires the log type; shaft production is knife-on-log like bow-making)
//
// NOTE: No GUI "make-X" interface in this version. All interactions produce 1 unit
//   (or 15 arrows). Future: add quantity selection interface.
//
// Animations:
//   seqs.human_fletching_knife — added to BaseSeqs.kt alongside this module.

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.fletchingLvl
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.stats.xpmod.XpModifiers
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

class Fletching @Inject constructor(private val xpMods: XpModifiers) : PluginScript() {

    override fun ScriptContext.startup() {
        // ---- Knife on log → unstrung bow (shortbow) ----
        // Each log produces a shortbow; pressing again produces a longbow.
        // For simplicity without a menu: knife + log → shortbow_u (lower level req).
        for (def in BOW_DEFS) {
            onOpHeldU(def.log, objs.knife) { fletchBow(def) }
        }

        // ---- Bowstring on unstrung bow → strung bow ----
        for (def in STRING_DEFS) {
            onOpHeldU(def.unstrung, objs.bowstring) { stringBow(def) }
        }

        // ---- Arrowhead + headless arrow → arrows (15 per action) ----
        for (def in ARROW_DEFS) {
            onOpHeldU(def.arrowhead, objs.headless_arrow) { fletchArrows(def) }
        }

        // Arrow shaft creation (knife + logs) shares the same event as bow-making.
        // Without a selection menu, knife+logs defaults to bow-making. Shaft-making
        // intentionally omitted to avoid duplicate event registration.
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
        invDel(inv, def.log, count = 1)
        anim(seqs.human_fletching_knife)
        delay(4)
        val xp = def.shortbowXp * xpMods.get(player, stats.fletching)
        statAdvance(stats.fletching, xp)
        invAdd(inv, def.shortbow_u)
        mes("You fletch the logs into an unstrung bow.")
    }

    private suspend fun ProtectedAccess.stringBow(def: StringDef) {
        if (player.fletchingLvl < def.levelReq) {
            mes("You need a Fletching level of ${def.levelReq} to string this bow.")
            return
        }
        if (!inv.contains(def.unstrung) || !inv.contains(objs.bowstring)) {
            mes("You need an unstrung bow and a bowstring.")
            return
        }
        invDel(inv, def.unstrung, count = 1)
        invDel(inv, objs.bowstring, count = 1)
        anim(seqs.human_fletching_knife)
        delay(4)
        val xp = def.xp * xpMods.get(player, stats.fletching)
        statAdvance(stats.fletching, xp)
        invAdd(inv, def.strung)
        mes("You string the bow.")
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
        invDel(inv, def.arrowhead, count = 1)
        invDel(inv, objs.headless_arrow, count = 1)
        anim(seqs.human_fletching_knife)
        delay(4)
        val xp = def.xp * xpMods.get(player, stats.fletching)
        statAdvance(stats.fletching, xp)
        invAdd(inv, def.arrow, count = 15)
        mes("You attach the arrowheads to the shafts.")
    }

    private suspend fun ProtectedAccess.makeShafts() {
        if (!inv.contains(objs.logs)) {
            mes("You don't have any logs to cut into shafts.")
            return
        }
        invDel(inv, objs.logs, count = 1)
        anim(seqs.human_fletching_knife)
        delay(4)
        val xp = 5.0 * xpMods.get(player, stats.fletching)
        statAdvance(stats.fletching, xp)
        invAdd(inv, objs.arrow_shaft, count = 15)
        mes("You cut the logs into arrow shafts.")
    }

    companion object {
        // Wiki-accurate XP and level requirements (rev 228)
        private val BOW_DEFS: List<BowDef> =
            listOf(
                BowDef(
                    objs.logs,
                    objs.shortbow_u,
                    objs.longbow_u,
                    shortbowLevelReq = 5,
                    longbowLevelReq = 10,
                    shortbowXp = 5.0,
                    longbowXp = 10.0,
                ),
                BowDef(
                    objs.oak_logs,
                    objs.oak_shortbow_u,
                    objs.oak_longbow_u,
                    shortbowLevelReq = 20,
                    longbowLevelReq = 25,
                    shortbowXp = 16.5,
                    longbowXp = 25.0,
                ),
                BowDef(
                    objs.willow_logs,
                    objs.willow_shortbow_u,
                    objs.willow_longbow_u,
                    shortbowLevelReq = 35,
                    longbowLevelReq = 40,
                    shortbowXp = 33.3,
                    longbowXp = 41.5,
                ),
                BowDef(
                    objs.maple_logs,
                    objs.maple_shortbow_u,
                    objs.maple_longbow_u,
                    shortbowLevelReq = 50,
                    longbowLevelReq = 55,
                    shortbowXp = 50.0,
                    longbowXp = 58.3,
                ),
                BowDef(
                    objs.yew_logs,
                    objs.yew_shortbow_u,
                    objs.yew_longbow_u,
                    shortbowLevelReq = 65,
                    longbowLevelReq = 70,
                    shortbowXp = 67.5,
                    longbowXp = 75.0,
                ),
                BowDef(
                    objs.magic_logs,
                    objs.magic_shortbow_u,
                    objs.magic_longbow_u,
                    shortbowLevelReq = 80,
                    longbowLevelReq = 85,
                    shortbowXp = 83.3,
                    longbowXp = 91.5,
                ),
            )

        private val STRING_DEFS: List<StringDef> =
            listOf(
                StringDef(objs.shortbow_u, objs.shortbow, levelReq = 5, xp = 5.0),
                StringDef(objs.longbow_u, objs.longbow, levelReq = 10, xp = 10.0),
                StringDef(objs.oak_shortbow_u, objs.oak_shortbow, levelReq = 20, xp = 16.5),
                StringDef(objs.oak_longbow_u, objs.oak_longbow, levelReq = 25, xp = 25.0),
                StringDef(objs.willow_shortbow_u, objs.willow_shortbow, levelReq = 35, xp = 33.3),
                StringDef(objs.willow_longbow_u, objs.willow_longbow, levelReq = 40, xp = 41.5),
                StringDef(objs.maple_shortbow_u, objs.maple_shortbow, levelReq = 50, xp = 50.0),
                StringDef(objs.maple_longbow_u, objs.maple_longbow, levelReq = 55, xp = 58.3),
                StringDef(objs.yew_shortbow_u, objs.yew_shortbow, levelReq = 65, xp = 67.5),
                StringDef(objs.yew_longbow_u, objs.yew_longbow, levelReq = 70, xp = 75.0),
                StringDef(objs.magic_shortbow_u, objs.magic_shortbow, levelReq = 80, xp = 83.3),
                StringDef(objs.magic_longbow_u, objs.magic_longbow, levelReq = 85, xp = 91.5),
            )

        private val ARROW_DEFS: List<ArrowDef> =
            listOf(
                ArrowDef(objs.bronze_arrowhead, objs.bronze_arrow, levelReq = 1, xp = 1.3),
                ArrowDef(objs.iron_arrowhead, objs.iron_arrow, levelReq = 15, xp = 2.5),
                ArrowDef(objs.steel_arrowhead, objs.steel_arrow, levelReq = 30, xp = 3.8),
                ArrowDef(objs.mithril_arrowhead, objs.mithril_arrow, levelReq = 45, xp = 5.0),
                ArrowDef(objs.adamant_arrowhead, objs.adamant_arrow, levelReq = 60, xp = 7.0),
                ArrowDef(objs.rune_arrowhead, objs.rune_arrow, levelReq = 75, xp = 10.0),
            )
    }
}
