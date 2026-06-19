@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.shieldofarrav.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias shield_arrav_npcs = ShieldOfArravNpcs

internal object ShieldOfArravNpcs : NpcReferences() {
    val baraek = find("baraek")
    val katrine = find("katrine")
    val weaponsmaster = find("weaponsmaster")
    val straven = find("straven")
    val jonny_the_beard = find("jonny_the_beard")
    val king_roald = find("king_roald")
    val tramp = find("tramp")
}
