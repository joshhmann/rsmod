package org.rsmod.content.areas.misc.darkwizardtower

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Dark Wizards Tower area script.
 *
 * The Dark Wizards Tower is located west of Falador and contains:
 * - Level 7 Dark Wizards (young) - Ground floor
 * - Level 20 Dark Wizards (bearded) - First and second floors
 *
 * NPC spawns are loaded from npcs.toml via DarkWizardTowerNpcSpawns. Combat behavior for dark
 * wizards is handled by WizardCombatScript in the npc-combat module.
 */
class DarkWizardTowerScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // NPC spawns are automatically loaded from npcs.toml
        // Combat behavior is handled by WizardCombatScript
    }
}
