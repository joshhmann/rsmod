@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.runemysteries.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias rune_mysteries_npcs = RuneMysteriesNpcs

internal object RuneMysteriesNpcs : NpcReferences() {
    val duke_of_lumbridge = find("duke_of_lumbridge")

    val sedridor = find("head_wizard")
    val sedridor_1op = find("head_wizard_1op")
    val sedridor_2op = find("head_wizard_2op")

    val aubury = find("aubury")
    val aubury_2op = find("aubury_2op")
    val aubury_3op = find("aubury_3op")
}
