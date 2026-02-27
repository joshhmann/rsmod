package org.rsmod.content.other.npccombat

import jakarta.inject.Inject
import org.rsmod.api.script.onNpcHit
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * F2P Wizard combat handlers.
 *
 * Wizards are magic-using NPCs found throughout Fielinor:
 * - Dark Wizards: Level 7/20, found at Draynor Manor and Dark Wizard Circle (south of Varrock)
 * - Wizards: Level 9, found at Wizard's Tower
 * - Elemental Wizards: Level 13, found south of Falador
 *
 * Combat stats (from OSRS wiki):
 * - Dark Wizard (bearded): Level 20, 24 HP, aggressive
 * - Dark Wizard (young): Level 7, 12 HP, aggressive
 * - Wizard: Level 9, 14 HP, not aggressive
 * - Elemental Wizards: Level 13, 25 HP, aggressive
 *
 * Note: The actual combat behavior (retaliation, aggression, magic attacks) is handled
 * automatically by the combat engine when NPCs have combat stats defined in npcs.toml. This script
 * registers onNpcHit handlers for any additional custom behavior.
 */
class WizardCombatScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        registerDarkWizards()
        registerWizards()
        registerElementalWizards()
    }

    /**
     * Register Dark Wizard combat handlers. Dark Wizards are aggressive magic users found at
     * Draynor Manor and Dark Wizard Circle.
     */
    private fun ScriptContext.registerDarkWizards() {
        // Bearded Dark Wizard (Level 20) - ID: 510
        // Found at Dark Wizard Circle south of Varrock and Draynor Manor
        onNpcHit(WizardTypes.bearded_dark_wizard) {
            // Retaliation and aggression handled by combat engine via npcs.toml stats
            // Custom behavior (e.g., weaken spell, bind spell) can be added here if needed
        }

        // Young Dark Wizard (Level 7) - ID: 512
        // Found at Dark Wizard Circle south of Varrock
        onNpcHit(WizardTypes.young_dark_wizard) {
            // Retaliation and aggression handled by combat engine
        }
    }

    /**
     * Register Wizard combat handlers. Wizards are found at Wizard's Tower and are not aggressive.
     */
    private fun ScriptContext.registerWizards() {
        // Wizard (Level 9) - ID: 514
        // Found at Wizard's Tower
        onNpcHit(WizardTypes.wizard) {
            // Not aggressive - will only retaliate when attacked
            // Combat handled by combat engine
        }
    }

    /**
     * Register Elemental Wizard combat handlers. Elemental Wizards are aggressive and found south
     * of Falador.
     */
    private fun ScriptContext.registerElementalWizards() {
        // Air Wizard (Level 13) - ID: 1559
        // Found south of Falador
        onNpcHit(WizardTypes.air_wizard) {
            // Aggressive - attacks with air strike
        }

        // Water Wizard (Level 13) - ID: 1557
        // Found south of Falador
        onNpcHit(WizardTypes.water_wizard) {
            // Aggressive - attacks with water strike
        }

        // Earth Wizard (Level 13) - ID: 1558
        // Found south of Falador
        onNpcHit(WizardTypes.earth_wizard) {
            // Aggressive - attacks with earth strike
        }

        // Fire Wizard (Level 13) - ID: 1556
        // Found south of Falador
        onNpcHit(WizardTypes.fire_wizard) {
            // Aggressive - attacks with fire strike
        }
    }
}

/** NPC type references for Wizard types used in combat scripts. */
internal object WizardTypes : NpcReferences() {
    // Dark Wizards
    val bearded_dark_wizard = find("bearded_dark_wizard")
    val young_dark_wizard = find("young_dark_wizard")

    // Standard Wizards
    val wizard = find("wizard")

    // Elemental Wizards
    val air_wizard = find("air_wizard")
    val water_wizard = find("water_wizard")
    val earth_wizard = find("earth_wizard")
    val fire_wizard = find("fire_wizard")
}
