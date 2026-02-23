package org.rsmod.content.areas.city.portsarim

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class PortSarimScript
@Inject
constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // NPC interaction handlers can be added here
        // - Redbeard Frank: Pirate's Treasure quest
        // - Wydin's Food Store: shop
        // - Brian's Battleaxe Bazaar: shop
        // - Captain Barnaby: charter ships
        // - Chemist: herblore supplies
        // - Quest NPCs: handled by individual quest modules
    }
}
