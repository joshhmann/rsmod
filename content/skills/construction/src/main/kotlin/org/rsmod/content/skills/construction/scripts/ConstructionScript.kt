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

        // === Parlour ===
        registerChain("Chairs", CHAIR_TIERS, listOf(
            poh_locs.chair1, poh_locs.chair2, poh_locs.chair3, poh_locs.chair4,
            poh_locs.chair5, poh_locs.chair6, poh_locs.chair7,
        ), poh_locs.chair1)

        registerChain("Bookcases", BOOKCASE_TIERS, listOf(
            poh_locs.bookcase1, poh_locs.bookcase2, poh_locs.bookcase3,
        ), poh_locs.bookcase1)

        registerChain("Scroll bookcases", BOOKCASE_SCROLL_TIERS, listOf(
            poh_locs.bookcase_scrolls1, poh_locs.bookcase_scrolls2, poh_locs.bookcase_scrolls3,
        ), poh_locs.bookcase_scrolls1)

        registerChain("Fireplaces", FIREPLACE_TIERS, listOf(
            poh_locs.fireplace_1, poh_locs.fireplace_2, poh_locs.fireplace_3,
        ), poh_locs.fireplace_1)

        registerChain("Curtains", CURTAIN_TIERS, listOf(
            poh_locs.curtains_1, poh_locs.curtains_2, poh_locs.curtains_3,
        ), poh_locs.curtains_1)

        registerChain("Wall decorations", WALL_DECO_TIERS, listOf(
            poh_locs.wall_deco_1, poh_locs.wall_deco_2, poh_locs.wall_deco_3,
        ), poh_locs.wall_deco_1)

        // === Bedroom ===
        registerChain("Beds", BED_TIERS, listOf(
            poh_locs.bed_1, poh_locs.bed_2, poh_locs.bed_3, poh_locs.bed_4,
            poh_locs.bed_5, poh_locs.bed_6, poh_locs.bed_7,
        ), poh_locs.bed_1)

        registerChain("Wardrobes", WARDROBE_TIERS, listOf(
            poh_locs.wardrobe_1, poh_locs.wardrobe_2, poh_locs.wardrobe_3, poh_locs.wardrobe_4,
            poh_locs.wardrobe_5, poh_locs.wardrobe_6, poh_locs.wardrobe_7,
        ), poh_locs.wardrobe_1)

        registerChain("Mirrors", MIRROR_TIERS, listOf(
            poh_locs.mirror_1, poh_locs.mirror_2, poh_locs.mirror_3, poh_locs.mirror_4,
            poh_locs.mirror_5, poh_locs.mirror_6, poh_locs.mirror_7,
        ), poh_locs.mirror_1)

        registerChain("Clocks", CLOCK_TIERS, listOf(
            poh_locs.clock_1, poh_locs.clock_2, poh_locs.clock_3,
        ), poh_locs.clock_1)

        // === Dining Room ===
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

        // === Kitchens ===
        registerChain("Kitchens", KITCHEN_TIERS, listOf(
            poh_locs.kitchen_1, poh_locs.kitchen_2, poh_locs.kitchen_3,
            poh_locs.kitchen_4, poh_locs.kitchen_5, poh_locs.kitchen_6,
            poh_locs.kitchen_7, poh_locs.kitchen_8,
        ), poh_locs.kitchen_1)

        // === Chapel ===
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

        registerChain("Saradomin altars", SARADOMIN_ALTAR_TIERS, listOf(
            poh_locs.altar_saradomin_1, poh_locs.altar_saradomin_2, poh_locs.altar_saradomin_3,
            poh_locs.altar_saradomin_4, poh_locs.altar_saradomin_5, poh_locs.altar_saradomin_6,
            poh_locs.altar_saradomin_7,
        ), poh_locs.altar_saradomin_1)

        registerChain("Zamorak altars", ZAMORAK_ALTAR_TIERS, listOf(
            poh_locs.altar_zamorak_1, poh_locs.altar_zamorak_2, poh_locs.altar_zamorak_3,
            poh_locs.altar_zamorak_4, poh_locs.altar_zamorak_5, poh_locs.altar_zamorak_6,
            poh_locs.altar_zamorak_7,
        ), poh_locs.altar_zamorak_1)

        registerChain("Icons", ICON_TIERS, listOf(
            poh_locs.icon_1, poh_locs.icon_2, poh_locs.icon_3, poh_locs.icon_4,
            poh_locs.icon_5, poh_locs.icon_6, poh_locs.icon_7,
        ), poh_locs.icon_1)

        registerChain("Torches", TORCH_TIERS, listOf(
            poh_locs.torch_1, poh_locs.torch_2, poh_locs.torch_3, poh_locs.torch_4,
            poh_locs.torch_5, poh_locs.torch_6, poh_locs.torch_7,
        ), poh_locs.torch_1)

        // === Throne Room ===
        registerChain("Thrones", THRONE_TIERS, listOf(
            poh_locs.throne_1, poh_locs.throne_2, poh_locs.throne_3, poh_locs.throne_4,
            poh_locs.throne_5, poh_locs.throne_6, poh_locs.throne_7,
        ), poh_locs.throne_1)

        // === Study ===
        registerChain("Lecterns", LECTERN_TIERS, listOf(
            poh_locs.lectern_1, poh_locs.lectern_2, poh_locs.lectern_3, poh_locs.lectern_4,
            poh_locs.lectern_5, poh_locs.lectern_6, poh_locs.lectern_7,
        ), poh_locs.lectern_1)

        registerChain("Globes", GLOBE_TIERS, listOf(
            poh_locs.globe_1, poh_locs.globe_2, poh_locs.globe_3, poh_locs.globe_4,
            poh_locs.globe_5, poh_locs.globe_6, poh_locs.globe_7,
        ), poh_locs.globe_1)

        registerChain("Telescopes", TELESCOPE_TIERS, listOf(
            poh_locs.telescope_1, poh_locs.telescope_2, poh_locs.telescope_3,
        ), poh_locs.telescope_1)

        registerChain("Crystal balls", CRYSTALBALL_TIERS, listOf(
            poh_locs.crystalball_1, poh_locs.crystalball_2, poh_locs.crystalball_3,
        ), poh_locs.crystalball_1)

        // === Workshop ===
        registerChain("Workbenches", WORKBENCH_TIERS, listOf(
            poh_locs.workbench_1, poh_locs.workbench_2, poh_locs.workbench_3,
            poh_locs.workbench_4, poh_locs.workbench_5,
        ), poh_locs.workbench_1)

        registerChain("Stools", STOOL_TIERS, listOf(
            poh_locs.stool_1, poh_locs.stool_2,
        ), poh_locs.stool_1)

        // === Costume Room ===
        registerChain("Cape racks", CAPE_RACK_TIERS, listOf(
            poh_locs.cape_rack_oak, poh_locs.cape_rack_teak, poh_locs.cape_rack_mahogany,
            poh_locs.cape_rack_gilded, poh_locs.cape_rack_marble, poh_locs.cape_rack_magic,
        ), poh_locs.cape_rack_oak)

        registerChain("Armour cases", ARMOUR_CASE_TIERS, listOf(
            poh_locs.armour_case_oak, poh_locs.armour_case_teak, poh_locs.armour_case_mahogany,
        ), poh_locs.armour_case_oak)

        registerChain("Toy boxes", TOY_BOX_TIERS, listOf(
            poh_locs.toy_box_oak, poh_locs.toy_box_teak, poh_locs.toy_box_mahogany,
        ), poh_locs.toy_box_oak)

        registerChain("Treasure chests", TREASURE_CHEST_TIERS, listOf(
            poh_locs.treasure_chest_oak, poh_locs.treasure_chest_teak, poh_locs.treasure_chest_mahogany,
        ), poh_locs.treasure_chest_oak)

        // === Floor decor (rugs — 3 corners, sides, middles per tier) ===
        registerRugTiers()
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

    private fun ScriptContext.registerRugTiers() {
        // Rugs have 3 tiers × 3 shapes (corner, side, middle)
        val rugLocs = listOf(
            listOf(poh_locs.rug_corner1, poh_locs.rug_corner2, poh_locs.rug_corner3),
            listOf(poh_locs.rug_side1, poh_locs.rug_side2, poh_locs.rug_side3),
            listOf(poh_locs.rug_middle1, poh_locs.rug_middle2, poh_locs.rug_middle3),
        )
        for ((shapeIdx, shape) in rugLocs.withIndex()) {
            registerChain("Rugs (shape ${shapeIdx + 1})", RUG_TIERS, shape, shape.first())
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

// --- Parlour ---

private val CHAIR_TIERS = listOf(
    FurnitureTier(1, 58.0, poh_locs.chair1, 2, needsNails = true),
    FurnitureTier(8, 87.0, poh_locs.chair2, 3, needsNails = true),
    FurnitureTier(14, 87.0, poh_locs.chair3, 3, needsNails = true),
    FurnitureTier(19, 120.0, poh_locs.chair4, 2),
    FurnitureTier(26, 180.0, poh_locs.chair5, 3),
    FurnitureTier(35, 180.0, poh_locs.chair6, 2),
    FurnitureTier(50, 280.0, poh_locs.chair7, 2),
)

private val BOOKCASE_TIERS = listOf(
    FurnitureTier(4, 60.0, poh_locs.bookcase1, 4, needsNails = true),
    FurnitureTier(29, 180.0, poh_locs.bookcase2, 6),
    FurnitureTier(55, 480.0, poh_locs.bookcase3, 6, extra = listOf(poh_objs.molten_glass to 4)),
)

private val BOOKCASE_SCROLL_TIERS = listOf(
    FurnitureTier(14, 120.0, poh_locs.bookcase_scrolls1, 4, needsNails = true),
    FurnitureTier(40, 240.0, poh_locs.bookcase_scrolls2, 6),
    FurnitureTier(65, 600.0, poh_locs.bookcase_scrolls3, 6),
)

private val FIREPLACE_TIERS = listOf(
    FurnitureTier(3, 60.0, poh_locs.fireplace_1, 3, extra = listOf(poh_objs.steel_bar to 1, poh_objs.soft_clay to 4)),
    FurnitureTier(33, 280.0, poh_locs.fireplace_2, 3, extra = listOf(poh_objs.steel_bar to 2, poh_objs.soft_clay to 4)),
    FurnitureTier(63, 680.0, poh_locs.fireplace_3, 3, extra = listOf(poh_objs.marble_block to 4, poh_objs.steel_bar to 2)),
)

private val CURTAIN_TIERS = listOf(
    FurnitureTier(2, 40.0, poh_locs.curtains_1, 2, extra = listOf(poh_objs.cloth to 2)),
    FurnitureTier(46, 200.0, poh_locs.curtains_2, 2, extra = listOf(poh_objs.cloth to 3)),
    FurnitureTier(74, 540.0, poh_locs.curtains_3, 2, extra = listOf(poh_objs.cloth to 4)),
)

private val WALL_DECO_TIERS = listOf(
    FurnitureTier(16, 120.0, poh_locs.wall_deco_1, 4, extra = listOf(poh_objs.molten_glass to 2, poh_objs.gold_bar to 1)),
    FurnitureTier(42, 400.0, poh_locs.wall_deco_2, 4, extra = listOf(poh_objs.molten_glass to 3, poh_objs.gold_bar to 2)),
    FurnitureTier(68, 960.0, poh_locs.wall_deco_3, 4, extra = listOf(poh_objs.molten_glass to 4, poh_objs.gold_bar to 3)),
)

// --- Bedroom ---

private val BED_TIERS = listOf(
    FurnitureTier(1, 60.0, poh_locs.bed_1, 3, needsNails = true),
    FurnitureTier(8, 90.0, poh_locs.bed_2, 4, needsNails = true),
    FurnitureTier(14, 120.0, poh_locs.bed_3, 3),
    FurnitureTier(20, 180.0, poh_locs.bed_4, 4),
    FurnitureTier(30, 270.0, poh_locs.bed_5, 3),
    FurnitureTier(45, 400.0, poh_locs.bed_6, 4),
    FurnitureTier(60, 600.0, poh_locs.bed_7, 4),
)

private val WARDROBE_TIERS = listOf(
    FurnitureTier(4, 60.0, poh_locs.wardrobe_1, 4, needsNails = true),
    FurnitureTier(16, 90.0, poh_locs.wardrobe_2, 4),
    FurnitureTier(25, 160.0, poh_locs.wardrobe_3, 4),
    FurnitureTier(34, 280.0, poh_locs.wardrobe_4, 4),
    FurnitureTier(42, 400.0, poh_locs.wardrobe_5, 6),
    FurnitureTier(53, 600.0, poh_locs.wardrobe_6, 6),
    FurnitureTier(64, 1500.0, poh_locs.wardrobe_7, 8, extra = listOf(poh_objs.gold_bar to 2)),
)

private val MIRROR_TIERS = listOf(
    FurnitureTier(6, 60.0, poh_locs.mirror_1, 3, needsNails = true),
    FurnitureTier(17, 100.0, poh_locs.mirror_2, 3, extra = listOf(poh_objs.molten_glass to 2)),
    FurnitureTier(27, 160.0, poh_locs.mirror_3, 3, extra = listOf(poh_objs.molten_glass to 3)),
    FurnitureTier(36, 280.0, poh_locs.mirror_4, 4, extra = listOf(poh_objs.molten_glass to 4)),
    FurnitureTier(45, 420.0, poh_locs.mirror_5, 6, extra = listOf(poh_objs.molten_glass to 4)),
    FurnitureTier(54, 600.0, poh_locs.mirror_6, 6, extra = listOf(poh_objs.molten_glass to 6)),
    FurnitureTier(66, 1500.0, poh_locs.mirror_7, 8, extra = listOf(poh_objs.molten_glass to 8, poh_objs.gold_bar to 2)),
)

private val CLOCK_TIERS = listOf(
    FurnitureTier(8, 60.0, poh_locs.clock_1, 3, extra = listOf(poh_objs.molten_glass to 2)),
    FurnitureTier(38, 280.0, poh_locs.clock_2, 4, extra = listOf(poh_objs.molten_glass to 3)),
    FurnitureTier(68, 1000.0, poh_locs.clock_3, 6, extra = listOf(poh_objs.molten_glass to 4, poh_objs.marble_block to 2)),
)

// --- Dining Room ---

private val DINING_TABLE_TIERS = listOf(
    FurnitureTier(10, 115.0, poh_locs.diningtable_1, 4, needsNails = true),
    FurnitureTier(22, 240.0, poh_locs.diningtable_2, 4),
    FurnitureTier(31, 360.0, poh_locs.diningtable_3, 6),
    FurnitureTier(38, 360.0, poh_locs.diningtable_4, 4),
    FurnitureTier(45, 600.0, poh_locs.diningtable_5, 6, extra = listOf(poh_objs.cloth to 4)),
    FurnitureTier(52, 840.0, poh_locs.diningtable_6, 6),
    FurnitureTier(72, 3100.0, poh_locs.diningtable_7, 6, extra = listOf(poh_objs.cloth to 4, poh_objs.gold_leaf to 4, poh_objs.marble_block to 2)),
)

private val DINING_CHAIR_TIERS = listOf(
    FurnitureTier(10, 115.0, poh_locs.diningchairs_1, 4, needsNails = true),
    FurnitureTier(22, 240.0, poh_locs.diningchairs_2, 4),
    FurnitureTier(31, 240.0, poh_locs.diningchairs_3, 4),
    FurnitureTier(38, 360.0, poh_locs.diningchairs_4, 4),
    FurnitureTier(44, 360.0, poh_locs.diningchairs_5, 4),
    FurnitureTier(52, 560.0, poh_locs.diningchairs_6, 6),
    FurnitureTier(61, 1760.0, poh_locs.diningchairs_7, 4, extra = listOf(poh_objs.gold_leaf to 4)),
)

// --- Kitchens ---

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

// --- Chapel ---

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

private val GUTHIX_ALTAR_TIERS = listOf(
    FurnitureTier(45, 240.0, poh_locs.altar_guthix_1, 4),
    FurnitureTier(50, 360.0, poh_locs.altar_guthix_2, 4),
    FurnitureTier(56, 390.0, poh_locs.altar_guthix_3, 4, extra = listOf(poh_objs.cloth to 2)),
    FurnitureTier(60, 590.0, poh_locs.altar_guthix_4, 4, extra = listOf(poh_objs.cloth to 2)),
    FurnitureTier(64, 910.0, poh_locs.altar_guthix_5, 6, extra = listOf(poh_objs.cloth to 2, poh_objs.limestone_brick to 2)),
    FurnitureTier(70, 1030.0, poh_locs.altar_guthix_6, 0, extra = listOf(poh_objs.marble_block to 2, poh_objs.cloth to 2)),
    FurnitureTier(75, 2230.0, poh_locs.altar_guthix_7, 0, extra = listOf(poh_objs.marble_block to 2, poh_objs.cloth to 2, poh_objs.gold_leaf to 4)),
)

private val SARADOMIN_ALTAR_TIERS = listOf(
    FurnitureTier(45, 240.0, poh_locs.altar_saradomin_1, 4),
    FurnitureTier(50, 360.0, poh_locs.altar_saradomin_2, 4),
    FurnitureTier(56, 390.0, poh_locs.altar_saradomin_3, 4, extra = listOf(poh_objs.cloth to 2)),
    FurnitureTier(60, 590.0, poh_locs.altar_saradomin_4, 4, extra = listOf(poh_objs.cloth to 2)),
    FurnitureTier(64, 910.0, poh_locs.altar_saradomin_5, 6, extra = listOf(poh_objs.cloth to 2, poh_objs.limestone_brick to 2)),
    FurnitureTier(70, 1030.0, poh_locs.altar_saradomin_6, 0, extra = listOf(poh_objs.marble_block to 2, poh_objs.cloth to 2)),
    FurnitureTier(75, 2230.0, poh_locs.altar_saradomin_7, 0, extra = listOf(poh_objs.marble_block to 2, poh_objs.cloth to 2, poh_objs.gold_leaf to 4)),
)

private val ZAMORAK_ALTAR_TIERS = listOf(
    FurnitureTier(45, 240.0, poh_locs.altar_zamorak_1, 4),
    FurnitureTier(50, 360.0, poh_locs.altar_zamorak_2, 4),
    FurnitureTier(56, 390.0, poh_locs.altar_zamorak_3, 4, extra = listOf(poh_objs.cloth to 2)),
    FurnitureTier(60, 590.0, poh_locs.altar_zamorak_4, 4, extra = listOf(poh_objs.cloth to 2)),
    FurnitureTier(64, 910.0, poh_locs.altar_zamorak_5, 6, extra = listOf(poh_objs.cloth to 2, poh_objs.limestone_brick to 2)),
    FurnitureTier(70, 1030.0, poh_locs.altar_zamorak_6, 0, extra = listOf(poh_objs.marble_block to 2, poh_objs.cloth to 2)),
    FurnitureTier(75, 2230.0, poh_locs.altar_zamorak_7, 0, extra = listOf(poh_objs.marble_block to 2, poh_objs.cloth to 2, poh_objs.gold_leaf to 4)),
)

private val ICON_TIERS = listOf(
    FurnitureTier(47, 240.0, poh_locs.icon_1, 4),
    FurnitureTier(52, 370.0, poh_locs.icon_2, 4),
    FurnitureTier(58, 490.0, poh_locs.icon_3, 4, extra = listOf(poh_objs.cloth to 2)),
    FurnitureTier(62, 690.0, poh_locs.icon_4, 4, extra = listOf(poh_objs.cloth to 2)),
    FurnitureTier(66, 980.0, poh_locs.icon_5, 6, extra = listOf(poh_objs.cloth to 2, poh_objs.limestone_brick to 2)),
    FurnitureTier(72, 1200.0, poh_locs.icon_6, 0, extra = listOf(poh_objs.marble_block to 2, poh_objs.cloth to 2)),
    FurnitureTier(78, 2500.0, poh_locs.icon_7, 0, extra = listOf(poh_objs.marble_block to 4, poh_objs.gold_leaf to 4)),
)

private val TORCH_TIERS = listOf(
    FurnitureTier(46, 240.0, poh_locs.torch_1, 4, extra = listOf(poh_objs.steel_bar to 2)),
    FurnitureTier(49, 320.0, poh_locs.torch_2, 4, extra = listOf(poh_objs.steel_bar to 2)),
    FurnitureTier(52, 380.0, poh_locs.torch_3, 4, extra = listOf(poh_objs.steel_bar to 3)),
    FurnitureTier(55, 440.0, poh_locs.torch_4, 4, extra = listOf(poh_objs.steel_bar to 3)),
    FurnitureTier(60, 560.0, poh_locs.torch_5, 4, extra = listOf(poh_objs.steel_bar to 4)),
    FurnitureTier(65, 720.0, poh_locs.torch_6, 4, extra = listOf(poh_objs.steel_bar to 4)),
    FurnitureTier(70, 1200.0, poh_locs.torch_7, 4, extra = listOf(poh_objs.gold_leaf to 2, poh_objs.steel_bar to 6)),
)

// --- Throne Room ---

private val THRONE_TIERS = listOf(
    FurnitureTier(60, 600.0, poh_locs.throne_1, 6, extra = listOf(poh_objs.limestone_brick to 4)),
    FurnitureTier(63, 720.0, poh_locs.throne_2, 6, extra = listOf(poh_objs.marble_block to 4)),
    FurnitureTier(66, 840.0, poh_locs.throne_3, 6, extra = listOf(poh_objs.marble_block to 6)),
    FurnitureTier(69, 1000.0, poh_locs.throne_4, 6, extra = listOf(poh_objs.marble_block to 6, poh_objs.gold_leaf to 2)),
    FurnitureTier(72, 1200.0, poh_locs.throne_5, 6, extra = listOf(poh_objs.marble_block to 6, poh_objs.gold_leaf to 4)),
    FurnitureTier(75, 1600.0, poh_locs.throne_6, 8, extra = listOf(poh_objs.marble_block to 8, poh_objs.gold_leaf to 6)),
    FurnitureTier(79, 2200.0, poh_locs.throne_7, 8, extra = listOf(poh_objs.marble_block to 10, poh_objs.gold_leaf to 8, poh_objs.gold_bar to 2)),
)

// --- Study ---

private val LECTERN_TIERS = listOf(
    FurnitureTier(40, 320.0, poh_locs.lectern_1, 4, extra = listOf(poh_objs.molten_glass to 2)),
    FurnitureTier(47, 420.0, poh_locs.lectern_2, 4, extra = listOf(poh_objs.molten_glass to 3)),
    FurnitureTier(52, 520.0, poh_locs.lectern_3, 4, extra = listOf(poh_objs.molten_glass to 4)),
    FurnitureTier(57, 640.0, poh_locs.lectern_4, 6, extra = listOf(poh_objs.molten_glass to 4)),
    FurnitureTier(62, 780.0, poh_locs.lectern_5, 6, extra = listOf(poh_objs.molten_glass to 6)),
    FurnitureTier(67, 1000.0, poh_locs.lectern_6, 6, extra = listOf(poh_objs.molten_glass to 6, poh_objs.gold_leaf to 4)),
    FurnitureTier(72, 1400.0, poh_locs.lectern_7, 8, extra = listOf(poh_objs.molten_glass to 8, poh_objs.gold_leaf to 4, poh_objs.gold_bar to 2)),
)

private val GLOBE_TIERS = listOf(
    FurnitureTier(41, 340.0, poh_locs.globe_1, 4, extra = listOf(poh_objs.molten_glass to 2)),
    FurnitureTier(48, 440.0, poh_locs.globe_2, 4, extra = listOf(poh_objs.molten_glass to 3)),
    FurnitureTier(53, 540.0, poh_locs.globe_3, 4, extra = listOf(poh_objs.molten_glass to 4)),
    FurnitureTier(58, 660.0, poh_locs.globe_4, 6, extra = listOf(poh_objs.molten_glass to 4)),
    FurnitureTier(63, 800.0, poh_locs.globe_5, 6, extra = listOf(poh_objs.molten_glass to 6)),
    FurnitureTier(68, 1020.0, poh_locs.globe_6, 6, extra = listOf(poh_objs.molten_glass to 6, poh_objs.gold_leaf to 4)),
    FurnitureTier(73, 1420.0, poh_locs.globe_7, 8, extra = listOf(poh_objs.molten_glass to 8, poh_objs.gold_leaf to 4, poh_objs.gold_bar to 2)),
)

private val TELESCOPE_TIERS = listOf(
    FurnitureTier(43, 380.0, poh_locs.telescope_1, 4, extra = listOf(poh_objs.molten_glass to 3)),
    FurnitureTier(54, 580.0, poh_locs.telescope_2, 6, extra = listOf(poh_objs.molten_glass to 5)),
    FurnitureTier(64, 1200.0, poh_locs.telescope_3, 8, extra = listOf(poh_objs.molten_glass to 6, poh_objs.gold_leaf to 4)),
)

private val CRYSTALBALL_TIERS = listOf(
    FurnitureTier(42, 360.0, poh_locs.crystalball_1, 4, extra = listOf(poh_objs.molten_glass to 4)),
    FurnitureTier(55, 600.0, poh_locs.crystalball_2, 6, extra = listOf(poh_objs.molten_glass to 6)),
    FurnitureTier(66, 1300.0, poh_locs.crystalball_3, 8, extra = listOf(poh_objs.molten_glass to 8, poh_objs.gold_bar to 3)),
)

// --- Workshop ---

private val WORKBENCH_TIERS = listOf(
    FurnitureTier(17, 140.0, poh_locs.workbench_1, 3, needsNails = true, extra = listOf(poh_objs.steel_bar to 2)),
    FurnitureTier(25, 240.0, poh_locs.workbench_2, 4, extra = listOf(poh_objs.steel_bar to 4)),
    FurnitureTier(33, 360.0, poh_locs.workbench_3, 4, extra = listOf(poh_objs.steel_bar to 6)),
    FurnitureTier(41, 480.0, poh_locs.workbench_4, 6, extra = listOf(poh_objs.mithril_bar to 6)),
    FurnitureTier(50, 680.0, poh_locs.workbench_5, 6, extra = listOf(poh_objs.mithril_bar to 8, poh_objs.gold_leaf to 2)),
)

private val STOOL_TIERS = listOf(
    FurnitureTier(1, 40.0, poh_locs.stool_1, 2, needsNails = true),
    FurnitureTier(23, 160.0, poh_locs.stool_2, 2),
)

// --- Costume Room ---

private val CAPE_RACK_TIERS = listOf(
    FurnitureTier(54, 600.0, poh_locs.cape_rack_oak, 4),
    FurnitureTier(62, 800.0, poh_locs.cape_rack_teak, 4),
    FurnitureTier(76, 1200.0, poh_locs.cape_rack_mahogany, 4),
    FurnitureTier(82, 1800.0, poh_locs.cape_rack_gilded, 6, extra = listOf(poh_objs.gold_leaf to 4)),
    FurnitureTier(87, 2400.0, poh_locs.cape_rack_marble, 8, extra = listOf(poh_objs.marble_block to 2)),
    FurnitureTier(92, 3600.0, poh_locs.cape_rack_magic, 8, extra = listOf(poh_objs.gold_bar to 2)),
)

private val ARMOUR_CASE_TIERS = listOf(
    FurnitureTier(57, 700.0, poh_locs.armour_case_oak, 4),
    FurnitureTier(67, 1000.0, poh_locs.armour_case_teak, 6),
    FurnitureTier(78, 1600.0, poh_locs.armour_case_mahogany, 6),
)

private val TOY_BOX_TIERS = listOf(
    FurnitureTier(56, 650.0, poh_locs.toy_box_oak, 4),
    FurnitureTier(66, 900.0, poh_locs.toy_box_teak, 6),
    FurnitureTier(77, 1400.0, poh_locs.toy_box_mahogany, 6),
)

private val TREASURE_CHEST_TIERS = listOf(
    FurnitureTier(58, 750.0, poh_locs.treasure_chest_oak, 4),
    FurnitureTier(68, 1100.0, poh_locs.treasure_chest_teak, 6),
    FurnitureTier(80, 2000.0, poh_locs.treasure_chest_mahogany, 8),
)

// --- Rugs (3 tiers, reused for corner/side/middle shapes) ---

private val RUG_TIERS = listOf(
    FurnitureTier(1, 0.0, poh_locs.rug_corner1, 0), // placeholder - not upgradeable, just decorative
    FurnitureTier(1, 0.0, poh_locs.rug_corner2, 0),
    FurnitureTier(1, 0.0, poh_locs.rug_corner3, 0),
)

/** Build mode varbit delegate */
private var Player.inBuildMode: Boolean by boolVarBit(poh_varbits.poh_building_mode)
