package org.rsmod.content.areas.misc.goblinvillage

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Goblin Village area content.
 *
 * Features:
 * - General Bentnoze (red goblin general)
 * - General Wartface (green goblin general)
 * - Goblin inhabitants (various goblin NPCs)
 * - Goblin mail spawns (for Goblin Diplomacy quest)
 *
 * Note: The Goblin Diplomacy quest dialogue and logic are handled by the goblin-diplomacy quest
 * module. This module provides the area spawns and any additional village-specific interactions.
 */
class GoblinVillageScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // Village-specific interactions can be added here
        // Goblin Diplomacy quest dialogue is handled by GoblinDiplomacy quest module
    }
}
