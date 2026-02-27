package org.rsmod.content.generic.landmarks

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Strange Rocks and Monuments - Landmark Examine Texts
 *
 * This module provides examine texts for various landmarks throughout Gielinor. The examine texts
 * are loaded from locs.toml and applied via the cache enricher.
 *
 * Landmarks covered:
 * - Stone circle south of Varrock (Demon Slayer quest connection)
 * - Statues in various locations
 * - Ancient monuments and ruins
 *
 * Note: Most examine texts are set via the TOML config file. This script can be extended to add
 * special interactions for specific landmarks.
 */
class LandmarksScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // Examine texts are loaded automatically from locs.toml via cache enricher
        // Special interactions can be added here if needed
    }
}
