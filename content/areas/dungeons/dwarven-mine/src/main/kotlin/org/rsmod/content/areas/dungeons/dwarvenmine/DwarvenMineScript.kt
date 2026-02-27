package org.rsmod.content.areas.dungeons.dwarvenmine

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Dwarven Mine script - Large mine beneath Asgarnia.
 *
 * The Dwarven Mine is located beneath Falador and Ice Mountain. It can be accessed from:
 * - Main entrance north of Falador (south of Ice Mountain)
 * - Falador building in north-east Falador
 * - Mining Guild entrance (requires 60 Mining)
 *
 * F2P Monsters:
 * - Scorpions (level 14) - aggressive to players under combat level 29
 * - King Scorpions (level 32) - aggressive to players under combat level 65
 * - Dwarves (level 10) - non-aggressive
 *
 * Ore Rocks:
 * - Clay: 5 rocks (south of main entrance)
 * - Copper: 11 rocks (south of main entrance)
 * - Tin: 10 rocks (south of main entrance)
 * - Iron: 9 rocks (scattered throughout)
 * - Coal: 11 rocks (near Mining Guild)
 * - Gold: 2 rocks (east of Mining Guild)
 * - Mithril: 2 rocks (north-west area)
 * - Adamantite: 3 rocks (north-west area)
 *
 * Note: Ore rocks are spawned from cache. This script handles NPC spawns.
 *
 * Future additions:
 * - Dwarf NPCs and dialogue
 * - Nurmof's Pickaxe Shop
 * - Drogo's Mining Emporium
 * - Dwarven Shopping Store (general store)
 * - Hura's Crossbow Shop (P2P)
 * - Anvil in northern section
 * - Mine cart transportation (P2P)
 * - Motherlode Mine entrance (P2P - requires 30 Mining)
 * - Mining Guild entry requirement check (60 Mining)
 * - Fire lighting restriction ("The dwarves won't be happy if you light a fire here")
 */
class DwarvenMineScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // NPC spawns are handled via npcs.toml
        // Scorpion and King Scorpion combat definitions are handled by separate NPC combat tasks
        // Dwarf combat definitions are handled by NPC-DWARF-COMB task

        // Future additions:
        // - Shop NPCs (Nurmof, Drogo, Hura, etc.)
        // - Dwarf dialogue and interactions
        // - Mining Guild entry logic
        // - Fire lighting restriction
    }
}
