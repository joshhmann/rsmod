package org.rsmod.content.quests.corsaircurse.configs

import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias cc_npcs = CorsairCurseNpcs
typealias cc_objs = CorsairCurseObjs
typealias cc_seqs = CorsairCurseSeqs
typealias cc_varbits = CorsairCurseVarBits
typealias cc_locs = CorsairCurseLocs

// =========================================================================
// NPCs — The Corsair Curse
// =========================================================================
object CorsairCurseNpcs : NpcReferences() {
    /** Captain Tock at the crossroads north of Port Sarim — quest start */
    val captain_tock_crossroads = find("corsair_captain_crossroads")

    /** Captain Tock at Corsair Cove (in his armour) */
    val captain_tock_cove = find("corscurs_lord_marshal")

    /** Ithoi the Navigator — healthy (upstairs in his hut) */
    val ithoi = find("corscurs_navigator")

    /** Ithoi — sick variant (lying in bed) */
    val ithoi_sick = find("corscurs_navigator_sick")

    /** Ithoi — running away animation */
    val ithoi_running = find("corscurs_navigator_running")

    /** Ithoi — boss fight variant */
    val ithoi_combat = find("corscurs_navigator_combat")

    /** Cabin Boy Colin — default */
    val colin = find("corscurs_cabinboy")

    /** Cabin Boy Colin — at telescope */
    val colin_telescope = find("corscurs_cabinboy_telescope")

    /** Cabin Boy Colin — sick */
    val colin_sick = find("corscurs_cabinboy_sick")

    /** Bugs — the ogre (Rantz's son) seen through the telescope */
    val bugs = find("corscurs_ogre")

    /** Gnocci the Cook — default */
    val gnocci = find("corscurs_cook")

    /** Gnocci — fishing */
    val gnocci_fishing = find("corscurs_cook_fishing")

    /** Gnocci — digging */
    val gnocci_digging = find("corscurs_cook_digging")

    /** Gnocci — sick */
    val gnocci_sick = find("corscurs_cook_sick")

    /** Gnocci — post-quest */
    val gnocci_post_quest = find("corscurs_cook_postquest")

    /** Possessed doll — interactive NPC on northern wall */
    val possessed_doll = find("corscurs_doll")

    /** Doll — multi/combat variant */
    val possessed_doll_multi = find("corscurs_doll_multi")

    /** Arsen the Thief — default */
    val arsen = find("corscurs_thief")

    /** Arsen — sick */
    val arsen_sick = find("corscurs_thief_sick")

    /** Arsen — post-quest */
    val arsen_post_quest = find("corscurs_thief_postquest")

    /** Arsen — digging variant */
    val arsen_digging = find("corscurs_digging_thief")

    /** Chief Tess — ogress leader of the Corsair Cove ogres */
    val chief_tess = find("ogress_tess")

    /** Ogress warriors */
    val ogress_warrior = find("ogress_warrior1")
    val ogress_warrior_2 = find("ogress_warrior2")

    /** Ogress shamans */
    val ogress_shaman = find("ogress_shaman1")
    val ogress_shaman_2 = find("ogress_shaman2")

    /** Banker Yusuf — locked (pre-quest) */
    val banker_locked = find("corscurs_banker_1op")

    /** Banker Yusuf — unlocked (post-quest) */
    val banker_unlocked = find("corscurs_banker_allops")

    /** Banker — multi variant */
    val banker_multi = find("corscurs_banker_multi")

    /** Ferry at Rimmington dock */
    val ferry_rimmington = find("corsair_ferry_rimmington")

    /** Ferry at Corsair Cove dock */
    val ferry_cove = find("corsair_ferry_covedocks")
}

// =========================================================================
// Items — quest items and required tools
// =========================================================================
object CorsairCurseObjs : ObjReferences() {
    /** Spade — required / obtainable during quest */
    val spade = find("spade")

    /** Tinderbox — required / obtainable during quest */
    val tinderbox = find("tinderbox")

    /** Ogre artefact — stolen by Arsen (quest item) */
    val ogre_artefact = find("ogre_artefact_the_corsair_curse")

    /** Coin reward */
    val coins = find("coins")
}

// =========================================================================
// Map LOCs — interactive objects
// =========================================================================
object CorsairCurseLocs : LocReferences() {
    /** Ithoi's telescope — on top of Ithoi's hut */
    val telescope = find("telescope")

    /** Dry driftwood — under Ithoi's hut, lit with tinderbox */
    val driftwood = find("driftwood")

    /** Wet driftwood — after Ithoi douses the fire */
    val wet_driftwood = find("wet_driftwood")

    /** Hole / entrance to Corsair Cove Dungeon */
    val dungeon_entrance = find("hole")

    /** Ladder up to Ithoi's boss room */
    val ladder_up = find("ladder")

    /** Spawn pad at Corsair Cove */
    val spawn_pad = find("spawn_pad")
}

// =========================================================================
// Animations
// =========================================================================
object CorsairCurseSeqs : SeqReferences() {
    /** Digging animation */
    val dig = find("human_dig")

    /** Lighting animation (tinderbox) */
    val light = find("human_light")
}

// =========================================================================
// VarBits — quest progress tracking
// =========================================================================
object CorsairCurseVarBits : VarBitReferences() {
    /**
     * Quest progress varbit (ID 6071).
     * Stages:
     *   0 = Not started
     *   1 = Talked to Captain Tock at crossroads, agreed to help
     *   2 = Arrived at Corsair Cove, spoke to Captain Tock there
     *   3 = Talked to Chief Tess and Bugs
     *   4 = Inspected the possessed doll
     *   5 = Talked to Arsen, Colin, Gnocci (all crew)
     *   6 = Investigated Arsen's curse (received ogre artefact, talked to Tess)
     *   7 = Investigated Colin's curse (used telescope)
     *   8 = Investigated Gnocci's curse (dug up doll)
     *   9 = Reported findings to Captain Tock
     *  10 = Found out Ithoi cooked the meal (talked to Gnocci)
     *  11 = Found out Ithoi is about to be fired (talked to Arsen/Francois)
     *  12 = Confronted Ithoi, he confessed
     *  13 = Proved Ithoi is faking (lit driftwood)
     *  14 = Boss fight ready (talked to Tock)
     *  15 = Ithoi defeated
     *  16 = Quest complete
     */
    val quest_progress = find("quest_the_corsair_curse_progress")
}
