package org.rsmod.content.quests.belowicemountain.configs

import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias bim_npcs = BelowIceNpcs
typealias bim_objs = BelowIceObjs
typealias bim_seqs = BelowIceSeqs
typealias bim_varbits = BelowIceVarBits
typealias bim_locs = BelowIceLocs

// --- NPCs ---
object BelowIceNpcs : NpcReferences() {
    /** Willow outside Ice Mountain — starts the quest */
    val willow_outside = find("bim_willow_outside")

    /** Willow (generic variant) */
    val willow = find("bim_willow")

    /** Willow inside the temple (post-crew) */
    val willow_inside = find("bim_willow_inside")

    /** Checkal in Barbarian Village — needs flex emote to recruit */
    val checkal_barb = find("bim_checkal_barb")

    /** Checkal (generic) */
    val checkal = find("bim_checkal")

    /** Atlas in The Long Hall — teaches flex emote */
    val atlas = find("bim_atlas")

    /** Burntof in Rising Sun Inn (Falador) — drunk, needs beer */
    val burntof_pub = find("bim_burntof_pub")

    /** Burntof (generic) */
    val burntof = find("bim_burntof")

    /** Burntof without drink (no beer in hand) */
    val burntof_nodrink = find("bim_burntof_nodrink")

    /** Marley in Edgeville ruins — wants steak sandwich */
    val marley_edge = find("bim_marley_edge")

    /** Marley (generic) */
    val marley = find("bim_marley")

    /** Ancient Guardian boss (level 25) */
    val golem_boss = find("bim_golem_boss")

    /** Ramarno the Imcando dwarf */
    val ramarno = find("camzodaal_ramarno")

    /** Ramarno at entrance */
    val ramarno_entrance = find("camzodaal_ramarno_entrance")

    /** Cutscene variants */
    val cutscene_willow = find("bim_cutscene_willow")
    val cutscene_atlas = find("bim_cutscene_atlas")
    val cutscene_burntof = find("bim_cutscene_burntof")
    val cutscene_checkal = find("bim_cutscene_checkal")
    val cutscene_marley = find("bim_cutscene_marley")
    val golem_boss_inactive = find("bim_golem_boss_inactive_cutscene")
    val golem_guardian_cutscene = find("bim_golem_guardian_cutscene")
    val ramarno_cutscene = find("bim_camzodaal_ramarno_cutscene")
}

// --- Items ---
object BelowIceObjs : ObjReferences() {
    /** Beer — required for Burntof */
    val beer = find("beer")

    /** Cooked meat — ingredient for steak sandwich */
    val cooked_meat = find("cooked_meat")

    /** Bread — ingredient for steak sandwich */
    val bread = find("bread")

    /** Knife — tool for steak sandwich */
    val knife = find("knife")

    /** Asgarnian ale — alternative to beer */
    val asgarnian_ale = find("asgarnian_ale")

    /** Dwarven stout — alternative to beer */
    val dwarven_stout = find("dwarven_stout")

    /** Wizard's mind bomb — alternative to beer */
    val wizards_mind_bomb = find("wizards_mind_bomb")

    /** Steak sandwich — given to Marley (cache name: bim_steak_sandwich) */
    val steak_sandwich = find("bim_steak_sandwich")

    /** Coins — reward */
    val coins = find("coins")

    /** Quest reward lamp — XP reward */
    val veos_lamp = find("veos_lamp")
}

// --- Animations ---
object BelowIceSeqs : SeqReferences() {
    /** Dig animation */
    val dig = find("human_dig")

    /** Flex emote animation (cache name: emote_flex) */
    val flex_emote = find("emote_flex")
}

// --- Map Objects ---
object BelowIceLocs : LocReferences() {
    /** Ancient doors / Camdozaal entrance */
    val ancient_doors = find("bim_door")
}

// --- VarBits (quest progress tracking) ---
object BelowIceVarBits : VarBitReferences() {
    /**
     * Quest progress varpbit.
     * Stages:
     *   0 = Not started
     *   1 = Started (talked to Willow, agreed to recruit crew)
     *   2 = Trained with Atlas (flex emote unlocked)
     *   3 = Checkal recruited (used flex emote)
     *   4 = Burntof recruited (gave beer + won RPS)
     *   5 = Marley recruited (gave steak sandwich)
     *   6 = Returned to Willow with full crew
     *   7 = Boss defeated
     *   8 = QUEST COMPLETE
     */
    val quest_progress = find("quest_below_ice_mountain_progress")
}
