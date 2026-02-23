package org.rsmod.content.quests.princealirescue.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias prince_ali_rescue_objs = PrinceAliRescueObjs

internal object PrinceAliRescueObjs : ObjReferences() {
    // Disguise items
    val wig = find("wig") // Made from wool
    val skin_paste = find("skin_paste") // From ashes + water
    val yellow_dye = find("yellowdye") // From onions
    val rope = find("rope") // To tie up Keli

    // Key and coins
    val cell_key = find("cell_door_key") // From Keli
    val coins_700 = find("coins_700") // Reward
}
