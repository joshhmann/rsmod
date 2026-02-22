@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.romeojuliet.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias romeo_juliet_npcs = RomeoJulietNpcs

internal object RomeoJulietNpcs : NpcReferences() {
    val romeo = find("romeo")
    val juliet = find("juliet")
    val father_lawrence = find("father_lawrence")
    val apothecary = find("apothecary")
}
