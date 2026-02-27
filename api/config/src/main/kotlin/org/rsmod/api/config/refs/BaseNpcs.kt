@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.npc.NpcReferences

typealias npcs = BaseNpcs

object BaseNpcs : NpcReferences() {
    val man = find("man", 8763388999323217354)
    val man2 = find("man2", 8763388999323217355)
    val man3 = find("man3", 8763388999323217356)
    val woman = find("woman", 3528871281014633303)
    val woman2 = find("woman2", 3528871281014633304)
    val woman3 = find("woman3", 3528871281014633305)
    val man_indoor = find("man_indoor", 1329978397240903284)
    val trail_master_uri = find("trail_master_uri", 2255033183848053726)
    val uri_emote = find("uri_emote", 4998005704130117537)

    /* Compatibility aliases */
    val uri_emote_1
        get() = trail_master_uri

    val uri_emote_2
        get() = uri_emote

    val diary_emote_npc = find("diary_emote_npc", 8481684566064664364)
    val corp_beast = find("corp_beast", 5604977903323694725)
    val imp = find("imp", 61762237712635356)
    val farming_tools_leprechaun = find("farming_tools_leprechaun", 5428755996588687321)
    val rod_fishing_spot_1527 = find("0_50_50_freshfish", 1358863933022409758)
    val fishing_spot_1530 = find("0_50_49_saltfish", 8943009170502558049)

    val goblin = find("goblin")
    val cow = find("cow")
    val chicken = find("chicken")
    val giant_rat = find("giant_rat")
    val guard = find("guard")
    val hill_giant = find("hill_giant")
    val moss_giant = find("moss_giant")
    val lesser_demon = find("lesser_demon")
    val greater_demon = find("greater_demon")
    val black_knight = find("black_knight")
    val dark_wizard = find("dark_wizard")
    val skeleton = find("skeleton")
    val zombie = find("zombie")
}
