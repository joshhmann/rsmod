package org.rsmod.content.areas.city.varrock.museum.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.museum.configs.museum_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class TimelineHistorian @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(museum_npcs.timeline_historian) { historianDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.historianDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Welcome to the Varrock Museum's timeline exhibit!")
            chatNpc(
                neutral,
                "Here you can see artifacts from different eras of Gielinor's history, from the Second Age through to modern times.",
            )
            chatNpc(
                neutral,
                "The displays show the progression of human civilisation and the various gods who have influenced our world.",
            )
        }
}
