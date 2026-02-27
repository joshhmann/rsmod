package org.rsmod.content.areas.city.lumbridge

import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Flour mill functionality for Lumbridge.
 *
 * The actual flour mill mechanics (hopper, controls, flour bin) are implemented in the windmill
 * module at [org.rsmod.content.other.windmill.WindmillScript].
 *
 * This script serves as a placeholder for any Lumbridge-specific flour mill customizations if
 * needed in the future.
 */
class FlourMill : PluginScript() {
    override fun ScriptContext.startup() {
        // Flour mill mechanics are handled by WindmillScript in content/other/windmill/
        // This includes:
        // - Using grain on hopper
        // - Operating hopper controls
        // - Collecting flour with empty pot
    }
}
