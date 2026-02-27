package org.rsmod.content.areas.dungeons.edgevilledungeon

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Edgeville Dungeon script - F2P and P2P dungeon content.
 *
 * This dungeon has two main sections:
 * - Southern (F2P): Hill Giants, Skeletons, Hobgoblins, Zombies
 * - Northern (P2P/Wilderness): Chaos Druids, Earth Warriors, Black Demons, Poison Spiders
 *
 * F2P Content:
 * - Hill Giants (level 28) - popular training spot, drop big bones
 * - Hobgoblins (level 28) - aggressive
 * - Skeletons (levels 13, 18, 22) - aggressive
 * - Zombies (levels 13, 18, 24) - aggressive
 *
 * P2P Content:
 * - Chaos Druids - popular herb farming spot
 * - Earth Warriors (level 51) - require 15 Agility
 * - Black Demons (level 172)
 * - Poison Spiders (level 64)
 */
class EdgevilleDungeonScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // All NPC spawns are handled via npcs.toml
        // Combat stats are handled by F2PMonsterCombatScript (F2P) and individual combat tasks
        // (P2P)
        // Drop tables are handled by NpcDropTablesScript

        // Future additions:
        // - Vannaka (Slayer Master) dialogue
        // - Wilderness area boundary/zone definitions
        // - Brass key shed entrance handling
        // - Edgeville trapdoor entrance handling
        // - Chronozon boss (Family Crest quest)
        // - Obelisks of Earth and Air
    }
}
