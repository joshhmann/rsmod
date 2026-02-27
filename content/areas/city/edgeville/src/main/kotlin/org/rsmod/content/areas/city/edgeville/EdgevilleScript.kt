package org.rsmod.content.areas.city.edgeville

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class EdgevilleScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // NPC interaction handlers can be added here
        // - Banker: default banking behavior
        // - General Store: default shop behavior
        // - Brother Jered: monastery, prayer/crafting
        // - Barbarian Village: Peka's helm shop
        // - Wilderness dungeon: Hill Giants (combat)
    }
}
