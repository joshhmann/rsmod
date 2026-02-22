@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.cooksassistant.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias cooks_assistant_npcs = CooksAssistantNpcs

internal object CooksAssistantNpcs : NpcReferences() {
    val cook = find("cook")
}
