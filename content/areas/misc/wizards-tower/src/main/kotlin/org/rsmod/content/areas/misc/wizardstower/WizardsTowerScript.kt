package org.rsmod.content.areas.misc.wizardstower

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Wizards Tower area script.
 *
 * The Wizards Tower is located south of Draynor Village, over a bridge. Features:
 * - Ground Floor: Archmage Sedridor (teleport to Rune Essence Mine), wizards
 * - First Floor: Wizard Mizgog (Imp Catcher quest), Traiborn (Demon Slayer quest)
 * - Second Floor: Wizards
 * - Basement: Rune Mysteries quest content (lesser demon cage, rune guardian)
 */
class WizardsTowerScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // NPC spawns loaded from npcs.toml via WizardsTowerNpcSpawns
        // Quest interactions handled by individual quest modules
    }
}
