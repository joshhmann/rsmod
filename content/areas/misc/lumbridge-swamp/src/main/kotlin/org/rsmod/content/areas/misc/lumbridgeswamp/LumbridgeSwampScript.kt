package org.rsmod.content.areas.misc.lumbridgeswamp

import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Lumbridge Swamp area content.
 *
 * Features:
 * - Father Urhney's hut (for Restless Ghost quest)
 * - Fishing spots (net/bait)
 * - Copper and tin mining rocks
 * - Goblin spawns (level 2-5)
 * - Cow spawns
 * - Swamp atmosphere (swamp tar spawns)
 *
 * Note: Father Urhney NPC and his dialogue are handled by the restless-ghost quest module. This
 * module provides the area spawns and any additional swamp-specific interactions.
 */
class LumbridgeSwampScript : PluginScript() {
    override fun ScriptContext.startup() {
        // Swamp-specific interactions can be added here
        // Father Urhney dialogue is handled by RestlessGhost quest module
    }
}
