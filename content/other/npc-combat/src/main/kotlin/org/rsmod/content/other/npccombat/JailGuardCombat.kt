package org.rsmod.content.other.npccombat

import jakarta.inject.Inject
import org.rsmod.api.script.onNpcHit
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Jail Guard NPC combat handlers.
 *
 * Jail Guards are level 26 NPCs found guarding prisons:
 * - Draynor Jail: Guards the jail near Draynor Village (Prince Ali Rescue quest)
 * - Port Sarim Jail: Guards the jail in Port Sarim
 *
 * Combat characteristics (from OSRS wiki):
 * - Combat level: 26
 * - Hitpoints: 32
 * - Attack level: 20
 * - Strength level: 20
 * - Defence level: 24
 * - Attack speed: 5 ticks (3.0 seconds)
 * - Attack style: Crush
 * - Aggressive: Yes (to low combat level players)
 *
 * Note: The actual combat behavior (retaliation, aggression) is handled automatically by the combat
 * engine when NPCs have combat stats defined in npcs.toml. This script registers onNpcHit handlers
 * for any additional custom behavior.
 */
class JailGuardCombatScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        registerJailGuards()
    }

    /**
     * Register Jail Guard combat handlers. Jail Guards are level 26 NPCs found in Draynor and Port
     * Sarim jails. Aggressive to low combat level players.
     */
    private fun ScriptContext.registerJailGuards() {
        // Jail Guard variants - found in Draynor and Port Sarim jails
        // Level 26, aggressive to low combat players

        onNpcHit(JailGuardTypes.jail_guard_1) {
            // Aggression handled by combat engine via npcs.toml stats
            // Combat stats: 32 HP, attack 20, strength 20, defence 24
        }

        onNpcHit(JailGuardTypes.jail_guard_2) {
            // Variant 2 - same behavior
        }

        onNpcHit(JailGuardTypes.jail_guard_3) {
            // Variant 3 - same behavior
        }

        onNpcHit(JailGuardTypes.jail_guard_4) {
            // Variant 4 - same behavior
        }

        onNpcHit(JailGuardTypes.jail_guard_5) {
            // Variant 5 - same behavior
        }
    }
}

/** NPC type references for Jail Guard types. */
internal object JailGuardTypes : NpcReferences() {
    // Jail Guard variants - level 26, found in Draynor and Port Sarim jails
    val jail_guard_1 = find("jail_guard_1")
    val jail_guard_2 = find("jail_guard_2")
    val jail_guard_3 = find("jail_guard_3")
    val jail_guard_4 = find("jail_guard_4")
    val jail_guard_5 = find("jail_guard_5")
}
