@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.misc.crafting_guild.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias crafting_guild_npcs = CraftingGuildNpcs

object CraftingGuildNpcs : NpcReferences() {
    // TODO: Add NPC references as they are implemented
    // val example_npc = find("cache_symbol_name")
}

internal object CraftingGuildNpcEditor : NpcEditor() {
    init {
        // edit(crafting_guild_npcs.example_npc) { moveRestrict = indoors }
    }
}
