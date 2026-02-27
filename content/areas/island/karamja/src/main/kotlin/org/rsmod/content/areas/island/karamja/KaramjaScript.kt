package org.rsmod.content.areas.island.karamja

import jakarta.inject.Inject
import org.rsmod.api.script.onOpNpc1
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Karamja F2P area script.
 *
 * Musa Point is the F2P portion of Karamja island, accessible via boat from Port Sarim. Features:
 * - Banana plantation (Luthas) with banana filling job
 * - Fishing spots (lobster and swordfish)
 * - General store
 * - Volcano entrance (leads to Crandor)
 * - Jungle wildlife (monkeys, spiders, scorpions, skeletons)
 * - Pirates wandering the docks
 * - Pub (Zambo)
 *
 * NPC spawns are loaded from npcs.toml via KaramjaNpcSpawns. NPC dialogue is handled by individual
 * NPC scripts in the npcs package.
 */
class KaramjaScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // Generic pirate dialogue
        onOpNpc1(KaramjaNpcs.pirate) {
            val messages =
                listOf(
                    "Arrr!",
                    "What ye be lookin' at?",
                    "This be pirate territory!",
                    "Got any rum?",
                    "The seas be rough today.",
                )
            it.npc.say(messages.random())
        }

        // Dock worker
        onOpNpc1(KaramjaNpcs.karamja_dock_worker) {
            it.npc.say("Hard work, this. But someone has to unload the ships.")
        }

        // Seamen
        onOpNpc1(KaramjaNpcs.seaman_lorris) {
            it.npc.say("The Lady Lumbridge is ready to sail when you are.")
        }

        onOpNpc1(KaramjaNpcs.seaman_thresnor) {
            it.npc.say("Smooth sailing to Port Sarim, just 30 coins.")
        }
    }
}
