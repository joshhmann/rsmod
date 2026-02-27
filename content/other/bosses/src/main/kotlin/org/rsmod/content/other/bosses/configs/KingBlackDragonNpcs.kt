package org.rsmod.content.other.bosses.configs

import org.rsmod.api.type.refs.npc.NpcReferences

/**
 * NPC references for King Black Dragon boss. KBD is a level 276 boss found in the Wilderness (level
 * 40+).
 */
internal object KingBlackDragonNpcs : NpcReferences() {
    val king_black_dragon = find("black_dragon")
    val deadman_kbd = find("deadman_breach_king_black_dragon")
}
