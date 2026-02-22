package org.rsmod.content.quests.princealirescue.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias prince_ali_rescue_objs = PrinceAliRescueObjs

internal object PrinceAliRescueObjs : ObjReferences() {
    // Disguise items
    val wig = find("plainwig") // Made from wool
    val skin_paste = find("skinpaste") // From ashes + water
    val yellow_dye = find("yellowdye") // From onions
    val rope = find("rope") // To tie up Keli

    // Key and coins
    val cell_key = find("princeskey") // From Keli
    val coins_700 = find("coins") // Reward quantity handled in quest logic
}
