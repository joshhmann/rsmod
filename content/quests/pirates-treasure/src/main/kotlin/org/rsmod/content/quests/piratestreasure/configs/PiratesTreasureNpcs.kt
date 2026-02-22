@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.piratestreasure.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias pirates_treasure_npcs = PiratesTreasureNpcs

internal object PiratesTreasureNpcs : NpcReferences() {
    val redbeard_frank = find("redbeard_frank")
    val luthas = find("luthas")
    val customs_officer = find("customs_officer")
}
