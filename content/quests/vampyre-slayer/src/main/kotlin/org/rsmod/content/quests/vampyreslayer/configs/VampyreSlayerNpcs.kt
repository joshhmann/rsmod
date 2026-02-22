package org.rsmod.content.quests.vampyreslayer.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias vampyre_slayer_npcs = VampyreSlayerNpcs

internal object VampyreSlayerNpcs : NpcReferences() {
    val morgan = find("morgan") // Quest giver in Draynor
    val dr_harlow = find("dr_harlow") // Stake giver in Varrock
    val count_draynor = find("count_draynor") // Boss in Draynor Manor
}
