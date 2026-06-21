package org.rsmod.content.areas.misc.catherby

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class CatherbyScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // Zone initialization — interactions, handlers, etc.
        // onOpLoc1(catherby_locs.example_loc) { handleInteraction(it.loc) }
    }
}
