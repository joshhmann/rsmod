package org.rsmod.content.interfaces.emotes

import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.varbit.VarBitReferences

internal object EmotesObjs : ObjReferences() {
    val music_cape = find("music_cape")
    val music_cape_trimmed = find("music_cape_trimmed")
    val skillcape_qp = find("skillcape_qp")
    val skillcape_qp_trimmed = find("skillcape_qp_trimmed")
    val skillcape_ad = find("skillcape_ad")
    val skillcape_ad_trimmed = find("skillcape_ad_trimmed")

    /* Compatibility aliases */
    val music_cape_t
        get() = music_cape_trimmed

    val quest_point_cape
        get() = skillcape_qp

    val quest_point_cape_t
        get() = skillcape_qp_trimmed

    val achievement_diary_cape
        get() = skillcape_ad

    val achievement_diary_cape_t
        get() = skillcape_ad_trimmed
}

internal object EmotesVarBits : VarBitReferences() {
    val lost_tribe_quest = find("lost_tribe_quest")

    /* Compatibility aliases */
    val lost_tribe_progress
        get() = lost_tribe_quest
}

internal object EmotesNpcs : NpcReferences() {
    val trail_master_uri = find("trail_master_uri")
    val uri_emote = find("uri_emote")

    /* Compatibility aliases */
    val uri_emote_1
        get() = trail_master_uri

    val uri_emote_2
        get() = uri_emote
}
