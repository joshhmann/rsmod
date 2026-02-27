package org.rsmod.content.areas.misc.outlawcamp

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class OutlawCampScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // Outlaw Camp is a wilderness area with bandits and rogues
        // NPC spawns are defined in npcs.toml
        // Combat definitions are handled by NPC-OUTLAW-COMB task
    }
}
