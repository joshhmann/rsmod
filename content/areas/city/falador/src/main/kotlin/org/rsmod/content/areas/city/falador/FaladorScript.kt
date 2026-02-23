package org.rsmod.content.areas.city.falador

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class FaladorScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // NPC interaction handlers can be added here
        // For now, Falador has basic NPC interactions:
        // - Banker: default banking behavior
        // - Shop owners (Wayne's Chains, Flynn's Mace): default shop behavior
        // - White Knights: patrol around castle
        // - Quest NPCs: handled by individual quest plugins
    }
}
