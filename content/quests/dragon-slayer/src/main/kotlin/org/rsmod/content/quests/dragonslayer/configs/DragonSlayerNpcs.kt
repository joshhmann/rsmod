package org.rsmod.content.quests.dragonslayer.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias dragon_slayer_npcs = DragonSlayerNpcs

internal object DragonSlayerNpcs : NpcReferences() {
    val guildmaster = find("guildmaster") // Quest giver in Champions' Guild
    val oziach = find("oziach") // Rune platebody seller in Edgeville
    val elvarg = find("elvarg") // Dragon boss on Crandor
}
