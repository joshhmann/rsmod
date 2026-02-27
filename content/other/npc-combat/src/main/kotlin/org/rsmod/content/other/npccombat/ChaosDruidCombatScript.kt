package org.rsmod.content.other.npccombat

import jakarta.inject.Inject
import org.rsmod.api.script.onNpcHit
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Chaos Druid combat handlers. Chaos Druids are level 13 monsters found in Edgeville Dungeon
 * (Wilderness area), Taverley Dungeon, Yanille Agility Dungeon, and the Chaos Druid Tower.
 *
 * Combat stats (from OSRS wiki):
 * - Combat level: 13
 * - Hitpoints: 20
 * - Attack style: Crush (with magic-based bind spell effect)
 * - Attack speed: 4 ticks (2.4 seconds)
 * - Aggressive: Yes
 *
 * Note: The actual combat behavior (retaliation, aggression) is handled automatically by the combat
 * engine when NPCs have combat stats defined in npcs.toml. This script registers onNpcHit handlers
 * for any additional custom behavior.
 */
class ChaosDruidCombatScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        registerChaosDruid()
        registerChaosDruidWarrior()
        registerWildernessChaosDruid()
    }

    private fun ScriptContext.registerChaosDruid() {
        // Chaos Druid (ID: 520) - standard version found in dungeons
        onNpcHit(NpcCombatTypes.chaos_druid) {
            // Retaliation and aggression handled by combat engine via npcs.toml stats
            // Custom behavior (e.g., bind spell effect) can be added here if needed
        }
    }

    private fun ScriptContext.registerChaosDruidWarrior() {
        // Chaos Druid Warrior (ID: 532) - stronger variant
        onNpcHit(NpcCombatTypes.chaos_druid_warrior) {
            // Retaliation and aggression handled by combat engine
        }
    }

    private fun ScriptContext.registerWildernessChaosDruid() {
        // Wilderness Chaos Druid (ID: 6607) - found in Edgeville Dungeon wilderness area
        onNpcHit(NpcCombatTypes.wilderness_chaos_druid) {
            // Retaliation and aggression handled by combat engine
        }
    }
}
