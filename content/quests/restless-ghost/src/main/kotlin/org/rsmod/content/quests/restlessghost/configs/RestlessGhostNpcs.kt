@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.restlessghost.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias restless_ghost_npcs = RestlessGhostNpcs

internal object RestlessGhostNpcs : NpcReferences() {
    val father_aereck = find("father_aereck")
    val father_urhney = find("father_urhney")
    val restless_ghost = find("ghost")
}
