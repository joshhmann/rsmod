package org.rsmod.content.areas.misc.essencemine

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Rune Essence Mine area script.
 *
 * The Rune Essence Mine is an instanced area accessible via:
 * - Aubury in Varrock (F2P)
 * - Sedridor in Wizards' Tower (F2P)
 * - Carwen Essencebinder in Burthorpe (Members)
 *
 * Features:
 * - Rune essence rocks (mineable for rune essence)
 * - Teleport portal to exit
 * - Pure essence for members with level 30+ Mining
 *
 * Note: This is an instanced area. The teleport NPCs are defined in their respective area modules
 * (Varrock for Aubury, Wizards' Tower for Sedridor). This script handles the mine itself.
 */
class EssenceMineScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // Rune essence rock mining is handled by the mining skill module
        // Teleport to/from mine is handled by NPC dialogue scripts
        // This module provides the area definition
    }
}
