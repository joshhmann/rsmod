package org.rsmod.content.skills.construction.scripts

import jakarta.inject.Inject
import kotlin.random.Random
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.constructionLvl
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onIfModalButton
import org.rsmod.api.script.onOpLoc3
import org.rsmod.api.script.onOpLocU
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.content.skills.construction.configs.poh_components
import org.rsmod.content.skills.construction.configs.poh_locs
import org.rsmod.content.skills.construction.configs.poh_objs
import org.rsmod.content.skills.construction.configs.poh_npcs
import org.rsmod.content.skills.construction.configs.poh_seqs
import org.rsmod.content.skills.construction.configs.poh_varbits
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Construction
@Inject
constructor(
    private val xpMods: XpModifiers,
    private val objRepo: ObjRepository,
    private val locRepo: LocRepository,
) : PluginScript() {

    override fun ScriptContext.startup() {
        onOpNpc1(poh_npcs.estate_agent) { estateAgentDialogue(it.npc) }
        onOpNpc1(poh_npcs.sawmill_operator) { sawmillDialogue(it.npc) }
        onOpLoc3(poh_locs.exit_portal) { leaveHouse() }
        onIfModalButton(poh_components.build_mode_on) { toggleBuildMode(on = true) }
        onIfModalButton(poh_components.build_mode_off) { toggleBuildMode(on = false) }
        onIfModalButton(poh_components.leave_house) { leaveHouse() }

        // Register all furniture chains
        registerChain("Chairs", CHAIR_TIERS, listOf(
            poh_locs.chair1, poh_locs.chair2, poh_locs.chair3, poh_locs.chair4,
            poh_locs.chair5, poh_locs.chair6, poh_locs.chair7,
        ), poh_locs.chair1)

        registerChain("Beds", BED_TIERS, listOf(
            poh_locs.bed_1, poh_locs.bed_2, poh_locs.bed_3, poh_locs.bed_4,
            poh_locs.bed_5, poh_locs.bed_6, poh_locs.bed_7,
        ), poh_locs.bed_1)

        registerChain("Dining tables", DINING_TABLE_TIERS, listOf(
            poh_locs.diningtable_1, poh_locs.diningtable_2, poh_locs.diningtable_3,
            poh_locs.diningtable_4, poh_locs.diningtable_5, poh_locs.diningtable_6,
            poh_locs.diningtable_7,
        ), poh_locs.diningtable_1)

        registerChain("Dining chairs", DINING_CHAIR_TIERS, listOf(
            poh_locs.diningchairs_1, poh_locs.diningchairs_2, poh_locs.diningchairs_3,
            poh_locs.diningchairs_4, poh_locs.diningchairs_5, poh_locs.diningchairs_6,
            poh_locs.diningchairs_7,
        ), poh_locs.diningchairs_1)

        registerChain("Kitchens", KITCHEN_TIERS, listOf(
            poh_locs.kitchen_1, poh_locs.kitchen_2, poh_locs.kitchen_3,
            poh_locs.kitchen_4, poh_locs.kitchen_5, poh_locs.kitchen_6,
            poh_locs.kitchen_7, poh_locs.kitchen_8,
        ), poh_locs.kitchen_1)

        registerChain("Chapels", CHAPEL_TIERS, listOf(
            poh_locs.chapel_1, poh_locs.chapel_2, poh_locs.chapel_3,
            poh_locs.chapel_5_corner, poh_locs.chapel_5_middle, poh_locs.chapel_5_side,
            poh_locs.chapel_6, poh_locs.chapel_7,
        ), poh_locs.chapel_1)

        registerChain("Guthix altars", GUTHIX_ALTAR_TIERS, listOf(
            poh_locs.altar_guthix_1, poh_locs.altar_guthix_2, poh_locs.altar_guthix_3,
            poh_locs.altar_guthix_4, poh_locs.altar_guthix_5, poh_locs.altar_guthix_6,
            poh_locs.altar_guthix_7,
        ), poh_locs.altar_guthix_1)
    }

    /** Register build + remove handlers for a furniture chain. */
    private fun ScriptContext.registerChain(name: String, tiers: List<FurnitureTier>, allLocs: List<LocType>, firstLoc: LocType) {
        for (loc in allLocs) {
            onOpLocU(loc) { buildFurniture(it.loc, tiers) }
        }
        for (loc in allLocs.drop(1)) {
            onOpLoc3(loc) { removeFurniture(it.loc, tiers, firstLoc) }
        }
    }

    // -----------------------------------------------------------------------
    // Estate Agent
    // -----------------------------------------------------------------------

    private suspend fun ProtectedAccess.estateAgentDialogue(npc: Npc) {
        when (choice3("Enter your house.", 1, "Tell me about Construction.", 2, "Goodbye.", 3)) {
            1 -> { mes("You enter your house."); anim(poh_seqs.build); teleport(CoordGrid(3305, 9833, 0)) }
            2 -> mes("Construction allows you to build and decorate your own house!")
        }
    }

    // -----------------------------------------------------------------------
    // Sawmill
    // -----------------------------------------------------------------------

    private suspend fun ProtectedAccess.sawmillDialogue(npc: Npc) {
        when (choice3("Convert logs to planks.", 1, "Tell me about the sawmill.", 2, "Goodbye.", 3)) {
            1 -> convertToPlanks()
            2 -> mes("Bring me logs and a hammer, and I'll turn them into planks!")
        }
    }

    private suspend fun ProtectedAccess.convertToPlanks() {
        if (!inv.contains(poh_objs.hammer)) { mes("You need a hammer."); return }
        for ((log, out, xp) in listOf(
            Triple(poh_objs.logs, poh_objs.plank, 14.0),
            Triple(poh_objs.oak_logs, poh_objs.plank_oak, 30.0),
            Triple(poh_objs.teak_logs, poh_objs.plank_teak, 50.0),
            Triple(poh_objs.mahogany_logs, poh_objs.plank_mahogany, 90.0),
        )) {
            if (inv.contains(log)) {
                invDel(inv, log, count = 1, strict = false)
                invAddOrDrop(objRepo, out, count = 1)
                spam("You convert a log into planks.")
                statAdvance(stats.construction, xp)
                return
            }
        }
        mes("You need some logs.")
    }

    // -----------------------------------------------------------------------
    // Build Mode
    // -----------------------------------------------------------------------

    private fun ProtectedAccess.toggleBuildMode(on: Boolean) {
        player.inBuildMode = on
        ifCloseSub(interfaces.poh_options)
        mes("Building mode is now <col=${if (on) "00ff00" else "ff0000"}>${if (on) "on" else "off"}</col>.")
    }

    // -----------------------------------------------------------------------
    // Generic Building Helper
    // -----------------------------------------------------------------------

    private suspend fun ProtectedAccess.buildFurniture(at: BoundLocInfo, tiers: List<FurnitureTier>) {
        if (!player.inBuildMode) { mes("You need to be in building mode."); return }
        if (!inv.contains(poh_objs.hammer)) { mes("You need a hammer."); return }

        val plank = detectPlank() ?: run { mes("You need planks."); return }

        val curIdx = tiers.indexOfFirst { locRepo.findExact(at.coords, it.loc) != null }
        val nextIdx = curIdx + 1
        if (nextIdx >= tiers.size) { mes("Already fully upgraded."); return }
        val next = tiers[nextIdx]

        if (player.constructionLvl < next.level) { mes("Need Construction ${next.level}."); return }
        if (next.plankCount > 0 && !inv.contains(plank.obj)) { mes("Need ${next.plankCount} planks."); return }
        for ((e, _) in next.extra) { if (!inv.contains(e)) { mes("Missing materials."); return } }

        invDel(inv, plank.obj, count = next.plankCount, strict = false)
        if (next.needsNails) invDel(inv, plank.nail, count = 1, strict = false)
        for ((e, c) in next.extra) invDel(inv, e, count = c, strict = false)

        anim(poh_seqs.build)
        locRepo.add(at.coords, next.loc, Int.MAX_VALUE, at.angle, at.shape)
        spam("You build furniture (tier ${nextIdx + 1}).")
        statAdvance(stats.construction, next.xp * xpMods.get(player, stats.construction))

        if (Random.nextInt(100) < 10) {
            if (!invDel(inv, poh_objs.hammer, count = 1, strict = false).failure) spam("Your hammer breaks!")
        }
    }

    private suspend fun ProtectedAccess.removeFurniture(at: BoundLocInfo, tiers: List<FurnitureTier>, firstLoc: LocType) {
        if (!player.inBuildMode) { mes("Need building mode."); return }
        val curIdx = tiers.indexOfFirst { locRepo.findExact(at.coords, it.loc) != null }
        val revert = if (curIdx > 0) tiers[curIdx - 1].loc else firstLoc
        anim(poh_seqs.build)
        locRepo.add(at.coords, revert, Int.MAX_VALUE, at.angle, at.shape)
        spam("You remove the furniture.")
        statAdvance(stats.construction, 10.0)
    }

    private fun ProtectedAccess.detectPlank(): PlankMat? {
        if (inv.contains(poh_objs.plank_mahogany)) return PlankMat(poh_objs.plank_mahogany, poh_objs.rune_nails)
        if (inv.contains(poh_objs.plank_teak)) return PlankMat(poh_objs.plank_teak, poh_objs.mithril_nails)
        if (inv.contains(poh_objs.plank_oak)) return PlankMat(poh_objs.plank_oak, poh_objs.iron_nails)
        if (inv.contains(poh_objs.plank)) return PlankMat(poh_objs.plank, poh_objs.bronze_nails)
        return null
    }

    // -----------------------------------------------------------------------
    // Leave House
    // -----------------------------------------------------------------------

    private suspend fun ProtectedAccess.leaveHouse() {
        mes("You leave your house.")
        teleport(CoordGrid(3222, 3218, 0))
    }
}

// =============================================================================
// Data
// =============================================================================

private data class PlankMat(val obj: ObjType, val nail: ObjType)
private data class FurnitureTier(val level: Int, val xp: Double, val loc: LocType, val plankCount: Int, val needsNails: Boolean = false, val extra: List<Pair<ObjType, Int>> = emptyList())

// Chairs: 7 tiers
private val CHAIR_TIERS = listOf(
    FurnitureTier(1, 58.0, poh_locs.chair1, 2, needsNails = true),
    FurnitureTier(8, 87.0, poh_locs.chair2, 3, needsNails = true),
    FurnitureTier(14, 87.0, poh_locs.chair3, 3, needsNails = true),
    FurnitureTier(19, 120.0, poh_locs.chair4, 2),
    FurnitureTier(26, 180.0, poh_locs.chair5, 3),
    FurnitureTier(35, 180.0, poh_locs.chair6, 2),
    FurnitureTier(50, 280.0, poh_locs.chair7, 2),
)

// Beds: 7 tiers
private val BED_TIERS = listOf(
    FurnitureTier(1, 60.0, poh_locs.bed_1, 3, needsNails = true),
    FurnitureTier(8, 90.0, poh_locs.bed_2, 4, needsNails = true),
    FurnitureTier(14, 120.0, poh_locs.bed_3, 3),
    FurnitureTier(20, 180.0, poh_locs.bed_4, 4),
    FurnitureTier(30, 270.0, poh_locs.bed_5, 3),
    FurnitureTier(45, 400.0, poh_locs.bed_6, 4),
    FurnitureTier(60, 600.0, poh_locs.bed_7, 4),
)

// Dining tables: 7 tiers
private val DINING_TABLE_TIERS = listOf(
    FurnitureTier(10, 115.0, poh_locs.diningtable_1, 4, needsNails = true),
    FurnitureTier(22, 240.0, poh_locs.diningtable_2, 4),
    FurnitureTier(31, 360.0, poh_locs.diningtable_3, 6),
    FurnitureTier(38, 360.0, poh_locs.diningtable_4, 4),
    FurnitureTier(45, 600.0, poh_locs.diningtable_5, 6, extra = listOf(poh_objs.cloth to 4)),
    FurnitureTier(52, 840.0, poh_locs.diningtable_6, 6),
    FurnitureTier(72, 3100.0, poh_locs.diningtable_7, 6, extra = listOf(poh_objs.cloth to 4, poh_objs.gold_leaf to 4, poh_objs.marble_block to 2)),
)

// Dining chairs: 7 tiers
private val DINING_CHAIR_TIERS = listOf(
    FurnitureTier(10, 115.0, poh_locs.diningchairs_1, 4, needsNails = true),
    FurnitureTier(22, 240.0, poh_locs.diningchairs_2, 4),
    FurnitureTier(31, 240.0, poh_locs.diningchairs_3, 4),
    FurnitureTier(38, 360.0, poh_locs.diningchairs_4, 4),
    FurnitureTier(44, 360.0, poh_locs.diningchairs_5, 4),
    FurnitureTier(52, 560.0, poh_locs.diningchairs_6, 6),
    FurnitureTier(61, 1760.0, poh_locs.diningchairs_7, 4, extra = listOf(poh_objs.gold_leaf to 4)),
)

// Kitchens: 8 tiers (stove upgrades)
private val KITCHEN_TIERS = listOf(
    FurnitureTier(5, 40.0, poh_locs.kitchen_1, 3, extra = listOf(poh_objs.soft_clay to 2, poh_objs.steel_bar to 1)),
    FurnitureTier(11, 60.0, poh_locs.kitchen_2, 3, extra = listOf(poh_objs.soft_clay to 2, poh_objs.steel_bar to 2)),
    FurnitureTier(17, 80.0, poh_locs.kitchen_3, 3, extra = listOf(poh_objs.soft_clay to 2, poh_objs.steel_bar to 3)),
    FurnitureTier(24, 80.0, poh_locs.kitchen_4, 0, extra = listOf(poh_objs.steel_bar to 4)),
    FurnitureTier(29, 100.0, poh_locs.kitchen_5, 0, extra = listOf(poh_objs.steel_bar to 5)),
    FurnitureTier(34, 120.0, poh_locs.kitchen_6, 0, extra = listOf(poh_objs.steel_bar to 6)),
    FurnitureTier(42, 160.0, poh_locs.kitchen_7, 0, extra = listOf(poh_objs.steel_bar to 8)),
    FurnitureTier(50, 250.0, poh_locs.kitchen_8, 4, extra = listOf(poh_objs.steel_bar to 8, poh_objs.cloth to 2)),
)

// Chapels: 8 tiers
private val CHAPEL_TIERS = listOf(
    FurnitureTier(45, 240.0, poh_locs.chapel_1, 4),
    FurnitureTier(50, 360.0, poh_locs.chapel_2, 4),
    FurnitureTier(56, 390.0, poh_locs.chapel_3, 4, extra = listOf(poh_objs.cloth to 2)),
    FurnitureTier(60, 590.0, poh_locs.chapel_5_corner, 4, extra = listOf(poh_objs.cloth to 2)),
    FurnitureTier(64, 910.0, poh_locs.chapel_5_middle, 6, extra = listOf(poh_objs.cloth to 2, poh_objs.limestone_brick to 2)),
    FurnitureTier(70, 1030.0, poh_locs.chapel_5_side, 0, extra = listOf(poh_objs.marble_block to 2, poh_objs.cloth to 2)),
    FurnitureTier(75, 2230.0, poh_locs.chapel_6, 0, extra = listOf(poh_objs.marble_block to 2, poh_objs.cloth to 2, poh_objs.gold_leaf to 4)),
    FurnitureTier(80, 3200.0, poh_locs.chapel_7, 0, extra = listOf(poh_objs.marble_block to 4, poh_objs.gold_leaf to 6)),
)

// Guthix altars: 7 tiers
private val GUTHIX_ALTAR_TIERS = listOf(
    FurnitureTier(45, 240.0, poh_locs.altar_guthix_1, 4),
    FurnitureTier(50, 360.0, poh_locs.altar_guthix_2, 4),
    FurnitureTier(56, 390.0, poh_locs.altar_guthix_3, 4, extra = listOf(poh_objs.cloth to 2)),
    FurnitureTier(60, 590.0, poh_locs.altar_guthix_4, 4, extra = listOf(poh_objs.cloth to 2)),
    FurnitureTier(64, 910.0, poh_locs.altar_guthix_5, 6, extra = listOf(poh_objs.cloth to 2, poh_objs.limestone_brick to 2)),
    FurnitureTier(70, 1030.0, poh_locs.altar_guthix_6, 0, extra = listOf(poh_objs.marble_block to 2, poh_objs.cloth to 2)),
    FurnitureTier(75, 2230.0, poh_locs.altar_guthix_7, 0, extra = listOf(poh_objs.marble_block to 2, poh_objs.cloth to 2, poh_objs.gold_leaf to 4)),
)

/** Build mode varbit delegate */
private var Player.inBuildMode: Boolean by boolVarBit(poh_varbits.poh_building_mode)
