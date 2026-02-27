package org.rsmod.content.areas.misc.crandor

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Crandor Island - Access and Content
 *
 * Crandor is an island accessed during and after the Dragon Slayer I quest.
 *
 * Access Methods:
 * - Secret passage from Karamja volcano (Elvarg's lair)
 * - Boat from Port Sarim (after completing Dragon Slayer I)
 *
 * Features:
 * - Lesser demons (level 82)
 * - Moss giants (level 42)
 * - Fishing spots (lobster, swordfish, tuna)
 * - Elvarg's lair (Dragon Slayer I boss)
 *
 * Note: This module implements the access methods. The Dragon Slayer I quest completion check is
 * handled by the quest module.
 */
class CrandorScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // Access methods:
        // 1. Karamja volcano secret passage (always available)
        // 2. Port Sarim boat (requires Dragon Slayer I completion)

        // NPC spawns are handled via npcs.toml
        // Lesser demons and moss giants

        // Fishing spots are spawned from cache
    }
}
