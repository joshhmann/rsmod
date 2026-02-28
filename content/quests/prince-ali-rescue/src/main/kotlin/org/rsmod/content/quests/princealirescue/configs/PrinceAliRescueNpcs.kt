package org.rsmod.content.quests.princealirescue.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias prince_ali_rescue_npcs = PrinceAliRescueNpcs

internal object PrinceAliRescueNpcs : NpcReferences() {
    val leela = find("leela") // Quest giver in Draynor
    val lady_keli = find("lady_keli") // Villain in Al Kharid prison
    val prince_ali = find("prince_ali_palace") // The captive prince
    val hassan = find("hassan") // Reward giver in Al Kharid
}
