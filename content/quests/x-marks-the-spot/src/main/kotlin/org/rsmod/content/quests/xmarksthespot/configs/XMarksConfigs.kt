package org.rsmod.content.quests.xmarksthespot.configs

import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias xm_npcs = XMarksNpcs
typealias xm_objs = XMarksObjs
typealias xm_seqs = XMarksSeqs
typealias xm_varbits = XMarksVarBits

// --- NPCs ---
object XMarksNpcs : NpcReferences() {
    /** Veos inside The Sheared Ram in Lumbridge — starts the quest */
    val veos_lumbridge = find("veos_lumbridge")

    /** Veos at the northernmost dock in Port Sarim — turns in the quest */
    val veos_sarim = find("veos_sarim")
}

// --- Items ---
object XMarksObjs : ObjReferences() {
    /** Spade — required to dig at marked locations */
    val spade = find("spade")

    /** Coin reward */
    val coins = find("coins")

    /** Treasure map piece (first — from Veos) */
    val treasure_map = find("treasure_map")

    /** Treasure map piece (second — from first dig) */
    val treasure_map_second = find("treasure_map_two")

    /** Mysterious orb — hot/cold hint item */
    val mysterious_orb = find("mysterious_orb")

    /** Ancient casket — final dig item, turned in to Veos */
    val ancient_casket = find("ancient_casket")

    /** Antique lamp — rewards 300 XP in any skill */
    val antique_lamp = find("antique_lamp")

    /** Beginner scroll box (from Veos after quest) */
    val scroll_box_beginner = find("scroll_box_beginner")
}

// --- Animations ---
object XMarksSeqs : SeqReferences() {
    /** Digging animation when using spade */
    val dig = find("human_dig")
}

// --- VarBits (quest progress tracking) ---
object XMarksVarBits : VarBitReferences() {
    /**
     * Quest progress varpbit.
     * Stages:
     *   0 = Not started
     *   1 = Started (received treasure map from Veos)
     *   2 = Solved first clue (dug at Bob's path)
     *   3 = Solved second clue (got Mysterious Orb behind castle)
     *   4 = Solved third clue (dug near Draynor wheat field)
     *   5 = Solved cipher (dug in Draynor pig pen, has Ancient Casket)
     *   6 = QUEST COMPLETE
     */
    val quest_progress = find("quest_x_marks_the_spot_progress")
}
