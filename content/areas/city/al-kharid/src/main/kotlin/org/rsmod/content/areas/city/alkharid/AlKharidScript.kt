package org.rsmod.content.areas.city.alkharid

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Al Kharid area script. Handles:
 * - Bank booth interactions
 * - Furnace access (via content groups in other modules)
 */
class AlKharidScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // Furnace, bank booths, and other locs work via content groups
        // already defined in other modules (e.g., furnace content group)
    }
}
