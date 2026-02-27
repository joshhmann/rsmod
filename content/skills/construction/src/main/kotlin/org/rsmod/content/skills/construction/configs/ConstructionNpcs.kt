package org.rsmod.content.skills.construction.configs

import org.rsmod.api.type.refs.npc.NpcReferences

typealias poh_npcs = ConstructionNpcs

object ConstructionNpcs : NpcReferences() {
    val estate_agent = find("poh_estate_agent")
}
