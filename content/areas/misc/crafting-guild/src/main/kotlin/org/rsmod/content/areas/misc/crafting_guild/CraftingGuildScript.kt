package org.rsmod.content.areas.misc.crafting_guild

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class CraftingGuildScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // Zone initialization — interactions, handlers, etc.
        // onOpLoc1(crafting_guild_locs.example_loc) { handleInteraction(it.loc) }
    }
}
