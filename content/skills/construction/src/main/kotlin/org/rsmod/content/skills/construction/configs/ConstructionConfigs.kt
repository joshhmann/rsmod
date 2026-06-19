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
    val chair1 = find("poh_chair1")
    val chair2 = find("poh_chair2")
    val chair3 = find("poh_chair3")
    val bookcase1 = find("poh_bookcase1")
    val bookcase2 = find("poh_bookcase2")
    val bookcase3 = find("poh_bookcase3")
    val fireplace1 = find("poh_fireplace_1")
    val fireplace2 = find("poh_fireplace_2")
    val fireplace3 = find("poh_fireplace_3")

    // === Phase 2: Additional Furniture LOCs ===
    val chair4 = find("poh_chair4")
    val chair5 = find("poh_chair5")
    val chair6 = find("poh_chair6")
    val chair7 = find("poh_chair7")
    val bookcase_scrolls1 = find("poh_bookcase_scrolls1")
    val bookcase_scrolls2 = find("poh_bookcase_scrolls2")
    val bookcase_scrolls3 = find("poh_bookcase_scrolls3")
    val parlour_1 = find("poh_parlour_1")
    val parlour_2 = find("poh_parlour_2")
    val parlour_3 = find("poh_parlour_3")
    val parlour_4_corner = find("poh_parlour_4_corner")
    val parlour_4_middle = find("poh_parlour_4_middle")
    val parlour_4_side = find("poh_parlour_4_side")
    val parlour_5 = find("poh_parlour_5")
    val parlour_5_scrolls = find("poh_parlour_5_scrolls")
    val parlour_6 = find("poh_parlour_6")
    val parlour_7 = find("poh_parlour_7")
    val bed_1 = find("poh_bed_1")
    val bed_2 = find("poh_bed_2")
    val bed_3 = find("poh_bed_3")
    val bed_4 = find("poh_bed_4")
    val bed_5 = find("poh_bed_5")
    val bed_6 = find("poh_bed_6")
    val bed_7 = find("poh_bed_7")
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
    val chapel_1 = find("poh_chapel_1")
    val chapel_2 = find("poh_chapel_2")
    val chapel_3 = find("poh_chapel_3")
    val chapel_5_corner = find("poh_chapel_5_corner")
    val chapel_5_middle = find("poh_chapel_5_middle")
    val chapel_5_side = find("poh_chapel_5_side")
    val chapel_6 = find("poh_chapel_6")
    val chapel_7 = find("poh_chapel_7")
    val altar_guthix_1 = find("poh_altar_guthix_1")
    val altar_guthix_2 = find("poh_altar_guthix_2")
    val altar_guthix_3 = find("poh_altar_guthix_3")
    val altar_guthix_4 = find("poh_altar_guthix_4")
    val altar_guthix_5 = find("poh_altar_guthix_5")
    val altar_guthix_6 = find("poh_altar_guthix_6")
    val altar_guthix_7 = find("poh_altar_guthix_7")

    val kitchen_1 = find("poh_kitchen_1")
    val kitchen_2 = find("poh_kitchen_2")
    val kitchen_3 = find("poh_kitchen_3")
    val kitchen_4 = find("poh_kitchen_4")
    val kitchen_5 = find("poh_kitchen_5")
    val kitchen_6 = find("poh_kitchen_6")
    val kitchen_7 = find("poh_kitchen_7")
    val kitchen_8 = find("poh_kitchen_8")
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
