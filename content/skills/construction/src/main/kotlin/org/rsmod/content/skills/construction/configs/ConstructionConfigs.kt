package org.rsmod.content.skills.construction.configs

import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias poh_npcs = ConstructionNpcs
typealias poh_locs = ConstructionLocs
typealias poh_objs = ConstructionObjs
typealias poh_seqs = ConstructionSeqs
typealias poh_components = ConstructionComponents
typealias poh_varbits = ConstructionVarBits

// --- NPCs ---
object ConstructionNpcs : NpcReferences() {
    val estate_agent = find("poh_estate_agent")
    val sawmill_operator = find("poh_sawmill_opp")
}

// --- Map LOCs ---
object ConstructionLocs : LocReferences() {
    val exit_portal = find("poh_exit_portal")
    val hotspot_doorl = find("poh_hotspot_doorl_lumbridge")

    // === Phase 1: Parlour furniture ===
    val chair1 = find("poh_chair1")
    val chair2 = find("poh_chair2")
    val chair3 = find("poh_chair3")
    val chair4 = find("poh_chair4")
    val chair5 = find("poh_chair5")
    val chair6 = find("poh_chair6")
    val chair7 = find("poh_chair7")

    val bookcase1 = find("poh_bookcase1")
    val bookcase2 = find("poh_bookcase2")
    val bookcase3 = find("poh_bookcase3")
    val bookcase_scrolls1 = find("poh_bookcase_scrolls1")
    val bookcase_scrolls2 = find("poh_bookcase_scrolls2")
    val bookcase_scrolls3 = find("poh_bookcase_scrolls3")

    val curtains_1 = find("poh_curtains_1")
    val curtains_2 = find("poh_curtains_2")
    val curtains_3 = find("poh_curtains_3")

    val rug_corner1 = find("poh_rugcorner1")
    val rug_corner2 = find("poh_rugcorner2")
    val rug_corner3 = find("poh_rugcorner3")
    val rug_side1 = find("poh_rugside1")
    val rug_side2 = find("poh_rugside2")
    val rug_side3 = find("poh_rugside3")
    val rug_middle1 = find("poh_rugmiddle1")
    val rug_middle2 = find("poh_rugmiddle2")
    val rug_middle3 = find("poh_rugmiddle3")

    val fireplace_1 = find("poh_fireplace_1")
    val fireplace_2 = find("poh_fireplace_2")
    val fireplace_3 = find("poh_fireplace_3")

    val wall_deco_1 = find("poh_wall_deco_1")
    val wall_deco_2 = find("poh_wall_deco_2")
    val wall_deco_3 = find("poh_wall_deco_3")

    // === Phase 2: Bedroom furniture ===
    val bed_1 = find("poh_bed_1")
    val bed_2 = find("poh_bed_2")
    val bed_3 = find("poh_bed_3")
    val bed_4 = find("poh_bed_4")
    val bed_5 = find("poh_bed_5")
    val bed_6 = find("poh_bed_6")
    val bed_7 = find("poh_bed_7")

    val wardrobe_1 = find("poh_wardrobe_1")
    val wardrobe_2 = find("poh_wardrobe_2")
    val wardrobe_3 = find("poh_wardrobe_3")
    val wardrobe_4 = find("poh_wardrobe_4")
    val wardrobe_5 = find("poh_wardrobe_5")
    val wardrobe_6 = find("poh_wardrobe_6")
    val wardrobe_7 = find("poh_wardrobe_7")

    val mirror_1 = find("poh_mirror_1")
    val mirror_2 = find("poh_mirror_2")
    val mirror_3 = find("poh_mirror_3")
    val mirror_4 = find("poh_mirror_4")
    val mirror_5 = find("poh_mirror_5")
    val mirror_6 = find("poh_mirror_6")
    val mirror_7 = find("poh_mirror_7")

    val clock_1 = find("poh_clock_1")
    val clock_2 = find("poh_clock_2")
    val clock_3 = find("poh_clock_3")

    // === Phase 3: Dining room furniture ===
    val diningtable_1 = find("poh_diningtable_1")
    val diningtable_2 = find("poh_diningtable_2")
    val diningtable_3 = find("poh_diningtable_3")
    val diningtable_4 = find("poh_diningtable_4")
    val diningtable_5 = find("poh_diningtable_5")
    val diningtable_6 = find("poh_diningtable_6")
    val diningtable_7 = find("poh_diningtable_7")

    val diningchairs_1 = find("poh_diningchairs_1")
    val diningchairs_2 = find("poh_diningchairs_2")
    val diningchairs_3 = find("poh_diningchairs_3")
    val diningchairs_4 = find("poh_diningchairs_4")
    val diningchairs_5 = find("poh_diningchairs_5")
    val diningchairs_6 = find("poh_diningchairs_6")
    val diningchairs_7 = find("poh_diningchairs_7")

    // === Phase 4: Chapel furniture ===
    val chapel_1 = find("poh_chapel_1")
    val chapel_2 = find("poh_chapel_2")
    val chapel_3 = find("poh_chapel_3")
    val chapel_5_corner = find("poh_chapel_5_corner")
    val chapel_5_middle = find("poh_chapel_5_middle")
    val chapel_5_side = find("poh_chapel_5_side")
    val chapel_6 = find("poh_chapel_6")
    val chapel_7 = find("poh_chapel_7")

    val icon_1 = find("poh_icon_1")
    val icon_2 = find("poh_icon_2")
    val icon_3 = find("poh_icon_3")
    val icon_4 = find("poh_icon_4")
    val icon_5 = find("poh_icon_5")
    val icon_6 = find("poh_icon_6")
    val icon_7 = find("poh_icon_7")

    val torch_1 = find("poh_torch_1")
    val torch_2 = find("poh_torch_2")
    val torch_3 = find("poh_torch_3")
    val torch_4 = find("poh_torch_4")
    val torch_5 = find("poh_torch_5")
    val torch_6 = find("poh_torch_6")
    val torch_7 = find("poh_torch_7")

    val altar_guthix_1 = find("poh_altar_guthix_1")
    val altar_guthix_2 = find("poh_altar_guthix_2")
    val altar_guthix_3 = find("poh_altar_guthix_3")
    val altar_guthix_4 = find("poh_altar_guthix_4")
    val altar_guthix_5 = find("poh_altar_guthix_5")
    val altar_guthix_6 = find("poh_altar_guthix_6")
    val altar_guthix_7 = find("poh_altar_guthix_7")

    val altar_saradomin_1 = find("poh_altar_saradomin_1")
    val altar_saradomin_2 = find("poh_altar_saradomin_2")
    val altar_saradomin_3 = find("poh_altar_saradomin_3")
    val altar_saradomin_4 = find("poh_altar_saradomin_4")
    val altar_saradomin_5 = find("poh_altar_saradomin_5")
    val altar_saradomin_6 = find("poh_altar_saradomin_6")
    val altar_saradomin_7 = find("poh_altar_saradomin_7")

    val altar_zamorak_1 = find("poh_altar_zamorak_1")
    val altar_zamorak_2 = find("poh_altar_zamorak_2")
    val altar_zamorak_3 = find("poh_altar_zamorak_3")
    val altar_zamorak_4 = find("poh_altar_zamorak_4")
    val altar_zamorak_5 = find("poh_altar_zamorak_5")
    val altar_zamorak_6 = find("poh_altar_zamorak_6")
    val altar_zamorak_7 = find("poh_altar_zamorak_7")

    // === Phase 5: Kitchen furniture ===
    val kitchen_1 = find("poh_kitchen_1")
    val kitchen_2 = find("poh_kitchen_2")
    val kitchen_3 = find("poh_kitchen_3")
    val kitchen_4 = find("poh_kitchen_4")
    val kitchen_5 = find("poh_kitchen_5")
    val kitchen_6 = find("poh_kitchen_6")
    val kitchen_7 = find("poh_kitchen_7")
    val kitchen_8 = find("poh_kitchen_8")

    // === Phase 6: Throne room furniture ===
    val throne_1 = find("poh_throne_1")
    val throne_2 = find("poh_throne_2")
    val throne_3 = find("poh_throne_3")
    val throne_4 = find("poh_throne_4")
    val throne_5 = find("poh_throne_5")
    val throne_6 = find("poh_throne_6")
    val throne_7 = find("poh_throne_7")

    // === Phase 7: Study furniture ===
    val lectern_1 = find("poh_lectern_1")
    val lectern_2 = find("poh_lectern_2")
    val lectern_3 = find("poh_lectern_3")
    val lectern_4 = find("poh_lectern_4")
    val lectern_5 = find("poh_lectern_5")
    val lectern_6 = find("poh_lectern_6")
    val lectern_7 = find("poh_lectern_7")

    val globe_1 = find("poh_globe_1")
    val globe_2 = find("poh_globe_2")
    val globe_3 = find("poh_globe_3")
    val globe_4 = find("poh_globe_4")
    val globe_5 = find("poh_globe_5")
    val globe_6 = find("poh_globe_6")
    val globe_7 = find("poh_globe_7")

    val telescope_1 = find("poh_telescope_1")
    val telescope_2 = find("poh_telescope_2")
    val telescope_3 = find("poh_telescope_3")

    val crystalball_1 = find("poh_crystalball_1")
    val crystalball_2 = find("poh_crystalball_2")
    val crystalball_3 = find("poh_crystalball_3")

    // === Phase 8: Workshop ===
    val workbench_1 = find("poh_workbench_1")
    val workbench_2 = find("poh_workbench_2")
    val workbench_3 = find("poh_workbench_3")
    val workbench_4 = find("poh_workbench_4")
    val workbench_5 = find("poh_workbench_5")

    val stool_1 = find("poh_stool_1")
    val stool_2 = find("poh_stool_2")

    // === Phase 9: Costume room ===
    val cape_rack_oak = find("poh_cos_room_cape_rack_oak")
    val cape_rack_teak = find("poh_cos_room_cape_rack_teak")
    val cape_rack_mahogany = find("poh_cos_room_cape_rack_mahogany")
    val cape_rack_gilded = find("poh_cos_room_cape_rack_mahogany_gilded")
    val cape_rack_marble = find("poh_cos_room_cape_rack_marble")
    val cape_rack_magic = find("poh_cos_room_cape_rack_magic_stone")

    val armour_case_oak = find("poh_cos_room_armour_case_oak")
    val armour_case_teak = find("poh_cos_room_armour_case_teak")
    val armour_case_mahogany = find("poh_cos_room_armour_case_mahogany")

    val toy_box_oak = find("poh_cos_room_toy_box_oak")
    val toy_box_teak = find("poh_cos_room_toy_box_teak")
    val toy_box_mahogany = find("poh_cos_room_toy_box_mahogany")

    val treasure_chest_oak = find("poh_cos_room_tresure_chest_oak")
    val treasure_chest_teak = find("poh_cos_room_tresure_chest_teak")
    val treasure_chest_mahogany = find("poh_cos_room_tresure_chest_mahogany")
}

// --- Items ---
object ConstructionObjs : ObjReferences() {
    val hammer = find("hammer")
    val saw = find("poh_saw")
    val plank = find("woodplank")
    val plank_oak = find("plank_oak")
    val plank_teak = find("plank_teak")
    val plank_mahogany = find("plank_mahogany")
    val bronze_nails = find("nails_bronze")
    val iron_nails = find("nails_iron")
    val black_nails = find("nails_black")
    val mithril_nails = find("nails_mithril")
    val adamant_nails = find("nails_adamant")
    val rune_nails = find("nails_rune")
    val cloth = find("cloth")
    val gold_leaf = find("gold_leaf")
    val marble_block = find("marble_block")
    val limestone_brick = find("limestonebrick")
    val soft_clay = find("softclay")
    val steel_bar = find("steel_bar")
    val logs = find("logs")
    val oak_logs = find("oak_logs")
    val teak_logs = find("teak_logs")
    val mahogany_logs = find("mahogany_logs")
    val molten_glass = find("molten_glass")
    val gold_bar = find("gold_bar")
    val mithril_bar = find("mithril_bar")
}

// --- Animations ---
object ConstructionSeqs : SeqReferences() {
    val build = find("human_smithing")
}

// --- UI Components ---
object ConstructionComponents : ComponentReferences() {
    val build_mode_on = find("poh_options:build_mode_on")
    val build_mode_off = find("poh_options:build_mode_off")
    val leave_house = find("poh_options:leave_house")
}

// --- VarBits ---
object ConstructionVarBits : VarBitReferences() {
    val poh_building_mode = find("poh_building_mode")
}
