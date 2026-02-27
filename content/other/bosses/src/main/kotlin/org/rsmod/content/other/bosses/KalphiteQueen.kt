package org.rsmod.content.other.bosses

import jakarta.inject.Inject
import org.rsmod.api.script.onNpcHit
import org.rsmod.content.other.bosses.configs.KalphiteQueenNpcs
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Kalphite Queen boss implementation.
 *
 * Combat Level: 333 HP: 255 Attack Speed: 4 ticks (2.4 seconds) Attack Styles: Stab (melee),
 * Ranged, Magic
 *
 * Special mechanics:
 * - Two phases: Airborne (form 1) and Grounded (form 2)
 * - Phase transition at 50% HP
 * - High defence against most attack styles
 * - Requires keris dagger for effective melee damage
 *
 * Located in Kalphite Lair (accessed via tunnel west of Shantay Pass).
 *
 * Note: The actual combat behavior (retaliation, aggression, phase transitions) is handled
 * automatically by the combat engine when NPCs have combat stats defined in npcs.toml. This script
 * registers onNpcHit handlers for any additional custom behavior.
 */
class KalphiteQueen @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        registerKalphiteQueen()
    }

    private fun ScriptContext.registerKalphiteQueen() {
        // Kalphite Queen (ID: 963) - one of the hardest OSRS bosses
        // Combat stats defined in npcs.toml:
        // - Combat Level: 333
        // - HP: 255
        // - Attack Speed: 4 ticks
        // - Attack Styles: Stab, Ranged, Magic
        onNpcHit(KalphiteQueenNpcs.kalphite_queen) {
            // KQ has two phases that it transitions between at 50% HP:
            // - Phase 1 (Airborne): Uses ranged and magic attacks
            // - Phase 2 (Grounded): Uses melee and magic attacks
            //
            // The phase transition is handled by the combat engine.
            // KQ is known for having very high defence against most attacks.
            //
            // Custom behavior (e.g., phase transition effects) can be added here if needed
        }
    }
}
