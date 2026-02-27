package org.rsmod.content.other.bosses

import jakarta.inject.Inject
import org.rsmod.api.script.onNpcHit
import org.rsmod.content.other.bosses.configs.KingBlackDragonNpcs
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * King Black Dragon boss implementation.
 *
 * Combat Level: 276 HP: 240 Attack Speed: 4 ticks (2.4 seconds) Attack Style: Dragonfire (melee
 * when in range)
 *
 * Special attacks: Four types of dragonfire breath
 * - Regular dragonfire - high damage
 * - Poison dragonfire - applies poison
 * - Ice dragonfire - freezes player
 * - Shock dragonfire - reduces stats
 *
 * Located in Wilderness (level 40+), accessed via lever in Edgeville or through the Wilderness at
 * coordinates (3015, 3850).
 *
 * Note: The actual combat behavior (retaliation, aggression, dragonfire attacks) is handled
 * automatically by the combat engine when NPCs have combat stats defined in npcs.toml. This script
 * registers onNpcHit handlers for any additional custom behavior.
 */
class KingBlackDragon @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        registerKingBlackDragon()
    }

    private fun ScriptContext.registerKingBlackDragon() {
        // King Black Dragon (ID: 252) - one of the most iconic OSRS bosses
        // Combat stats defined in npcs.toml:
        // - Combat Level: 276
        // - HP: 240
        // - Attack Speed: 4 ticks
        // - Attack Type: Dragonfire (with melee fallback)
        onNpcHit(KingBlackDragonNpcs.king_black_dragon) {
            // Dragonfire attack types are handled by the combat engine
            // KBD has 4 different fire breath attacks that it rotates between:
            // 1. Regular dragonfire - high damage if no protection
            // 2. Poison dragonfire - applies poison effect
            // 3. Ice dragonfire - freezes player in place
            // 4. Shock dragonfire - drains player stats
            //
            // Custom behavior (e.g., special attack rotation) can be added here if needed
        }
    }
}
