package org.rsmod.content.skills.smithing.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.smithingLvl
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLocU
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.content.skills.smithing.configs.SmithingLocs
import org.rsmod.content.skills.smithing.configs.SmithingObjs as objs
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Anvil interactions for Smithing skill.
 *
 * Supports:
 * - Using bar + hammer on anvil to smith items
 * - Clicking anvil with hammer to display hint message
 */
class SmithingAnvil
@Inject
constructor(private val xpMods: XpModifiers, private val objRepo: ObjRepository) : PluginScript() {

    override fun ScriptContext.startup() {
        // ---- Click anvil (no bar selected) ----
        onOpLoc1(SmithingLocs.anvil) {
            if (inv.contains(objs.hammer)) {
                mes("You should use a bar on the anvil to smith it.")
            } else {
                mes("You need a hammer to work the anvil.")
            }
        }
        onOpLoc1(SmithingLocs.dorics_anvil) {
            if (inv.contains(objs.hammer)) {
                mes("You should use a bar on the anvil to smith it.")
            } else {
                mes("You need a hammer to work the anvil.")
            }
        }

        // ---- Use bar on anvil (with hammer check) ----
        onOpLocU(SmithingLocs.anvil, objs.bronze_bar) { smithWithHammer(SMITH_BRONZE_DAGGER) }
        onOpLocU(SmithingLocs.anvil, objs.iron_bar) { smithWithHammer(SMITH_IRON_DAGGER) }
        onOpLocU(SmithingLocs.anvil, objs.steel_bar) { smithWithHammer(SMITH_STEEL_DAGGER) }
        onOpLocU(SmithingLocs.anvil, objs.mithril_bar) { smithWithHammer(SMITH_MITHRIL_DAGGER) }
        onOpLocU(SmithingLocs.anvil, objs.adamantite_bar) { smithWithHammer(SMITH_ADAMANT_DAGGER) }
        onOpLocU(SmithingLocs.anvil, objs.runite_bar) { smithWithHammer(SMITH_RUNE_DAGGER) }

        onOpLocU(SmithingLocs.dorics_anvil, objs.bronze_bar) {
            smithWithHammer(SMITH_BRONZE_DAGGER)
        }
        onOpLocU(SmithingLocs.dorics_anvil, objs.iron_bar) { smithWithHammer(SMITH_IRON_DAGGER) }
        onOpLocU(SmithingLocs.dorics_anvil, objs.steel_bar) { smithWithHammer(SMITH_STEEL_DAGGER) }
        onOpLocU(SmithingLocs.dorics_anvil, objs.mithril_bar) {
            smithWithHammer(SMITH_MITHRIL_DAGGER)
        }
        onOpLocU(SmithingLocs.dorics_anvil, objs.adamantite_bar) {
            smithWithHammer(SMITH_ADAMANT_DAGGER)
        }
        onOpLocU(SmithingLocs.dorics_anvil, objs.runite_bar) { smithWithHammer(SMITH_RUNE_DAGGER) }
    }

    private data class SmithDef(
        val bar: ObjType,
        val barCount: Int = 1,
        val levelReq: Int,
        val xp: Double,
        val product: ObjType,
        val productCount: Int = 1,
    )

    private suspend fun ProtectedAccess.smithWithHammer(def: SmithDef) {
        if (!inv.contains(objs.hammer)) {
            mes("You need a hammer to work the anvil.")
            return
        }
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
            if (!inv.contains(objs.hammer)) {
                mes("You need a hammer to work the anvil.")
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
            invAddOrDrop(objRepo, def.product, count = def.productCount)
            mes("You hammer the bars into an item.")
        }
    }

    companion object {
        // Default to dagger for each tier (simplest item, 1 bar)
        private val SMITH_BRONZE_DAGGER =
            SmithDef(
                bar = objs.bronze_bar,
                barCount = 1,
                levelReq = 1,
                xp = 12.5,
                product = objs.bronze_dagger,
            )
        private val SMITH_IRON_DAGGER =
            SmithDef(
                bar = objs.iron_bar,
                barCount = 1,
                levelReq = 15,
                xp = 25.0,
                product = objs.iron_dagger,
            )
        private val SMITH_STEEL_DAGGER =
            SmithDef(
                bar = objs.steel_bar,
                barCount = 1,
                levelReq = 30,
                xp = 37.5,
                product = objs.steel_dagger,
            )
        private val SMITH_MITHRIL_DAGGER =
            SmithDef(
                bar = objs.mithril_bar,
                barCount = 1,
                levelReq = 50,
                xp = 50.0,
                product = objs.mithril_dagger,
            )
        private val SMITH_ADAMANT_DAGGER =
            SmithDef(
                bar = objs.adamantite_bar,
                barCount = 1,
                levelReq = 70,
                xp = 62.5,
                product = objs.adamant_dagger,
            )
        private val SMITH_RUNE_DAGGER =
            SmithDef(
                bar = objs.runite_bar,
                barCount = 1,
                levelReq = 85,
                xp = 75.0,
                product = objs.rune_dagger,
            )
    }
}
