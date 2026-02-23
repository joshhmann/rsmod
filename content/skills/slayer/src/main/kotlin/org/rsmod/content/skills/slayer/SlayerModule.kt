package org.rsmod.content.skills.slayer

import org.rsmod.plugin.module.PluginModule

class SlayerModule : PluginModule() {
    override fun bind() {
        // Slayer skill bindings - currently uses in-memory state
        // Future: add varp-based persistence for player slayer data
    }
}
