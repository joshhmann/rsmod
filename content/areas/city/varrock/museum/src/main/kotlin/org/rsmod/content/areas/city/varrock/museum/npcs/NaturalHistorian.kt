package org.rsmod.content.areas.city.varrock.museum.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.museum.configs.museum_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class NaturalHistorian @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(museum_npcs.natural_historian_north) {
            historianDialogue(it.npc, "the creatures of the north")
        }
        onOpNpc1(museum_npcs.natural_historian_south) {
            historianDialogue(it.npc, "the creatures of the south")
        }
        onOpNpc1(museum_npcs.natural_historian_east) {
            historianDialogue(it.npc, "the creatures of the east")
        }
        onOpNpc1(museum_npcs.natural_historian_west) {
            historianDialogue(it.npc, "the creatures of the west")
        }
    }

    private suspend fun ProtectedAccess.historianDialogue(npc: Npc, region: String) =
        startDialogue(npc) {
            chatNpc(
                happy,
                "Welcome to the Natural History exhibit! Here we display creatures from $region.",
            )
            chatNpc(
                neutral,
                "Feel free to examine the displays. Each one contains fascinating information about Gielinor's wildlife.",
            )
        }
}
