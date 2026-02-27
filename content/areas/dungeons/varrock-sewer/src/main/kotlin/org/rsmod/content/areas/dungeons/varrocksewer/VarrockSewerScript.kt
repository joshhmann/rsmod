package org.rsmod.content.areas.dungeons.varrocksewer

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Varrock Sewers dungeon implementation.
 *
 * Features:
 * - Multiple entrances: Varrock Palace courtyard, Cooking Guild area
 * - F2P monsters: Moss Giants (level 42), Zombies, Skeletons, Giant Rats, Spiders
 * - Connection to Edgeville Dungeon (members only)
 *
 * NPC spawns are defined in npcs.toml Ladder/entrance handling is managed by generic ladder content
 * groups
 */
class VarrockSewerScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // NPC spawns are handled via npcs.toml
        // Moss Giant combat stats handled by NPC-MOSS-GIANT-F2P task
        // Zombie/Skeleton combat stats handled by their respective tasks
        // Drop tables handled by NPC-DROP-* tasks

        // Future additions:
        // - Ladder entrance/exit handlers (Palace courtyard, Cooking Guild)
        // - Edgeville Dungeon gate connection (P2P)
        // - Specific sewer location interactions
    }
}
