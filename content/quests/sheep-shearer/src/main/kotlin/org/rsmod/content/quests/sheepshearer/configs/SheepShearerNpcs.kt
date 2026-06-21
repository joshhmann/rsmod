@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.sheepshearer.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias sheep_shearer_npcs = SheepShearerNpcs

internal object SheepShearerNpcs : NpcReferences() {
    val fred_the_farmer = find("fred_the_farmer")
}
