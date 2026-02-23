package org.rsmod.content.areas.city.draynor

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class DraynorScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // NPC interaction handlers can be added here
        // For now, Draynor Village has basic NPC interactions
        // - Banker: default banking behavior
        // - General store: default shop behavior
        // - Quest NPCs: handled by individual quest plugins
    }
}
