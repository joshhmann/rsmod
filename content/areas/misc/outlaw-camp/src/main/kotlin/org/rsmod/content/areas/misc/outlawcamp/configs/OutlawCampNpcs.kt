package org.rsmod.content.areas.misc.outlawcamp.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias outlaw_camp_npcs = OutlawCampNpcs

object OutlawCampNpcs : NpcReferences() {
    // Wilderness bandits/outlaws at the Outlaw Camp
    val wilderness_bandit = find("wilderness_bandit")
    val wilderness_rogue = find("wilderness_rogue")
}
