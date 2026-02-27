package org.rsmod.content.skills.agility

import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Agility skill implementation for RSMod v2.
 *
 * Implements F2P Agility Courses with obstacles + completion bonus:
 * - Draynor Village Rooftop (Level 10)
 * - Al Kharid Rooftop (Level 20)
 * - Varrock Rooftop (Level 30)
 *
 * Course implementations are owned by dedicated submodules.
 */
class Agility : PluginScript() {
    override fun ScriptContext.startup() {
        // Base agility script intentionally does not wire course module classes directly.
        // Each course submodule registers its own handlers.
    }
}
