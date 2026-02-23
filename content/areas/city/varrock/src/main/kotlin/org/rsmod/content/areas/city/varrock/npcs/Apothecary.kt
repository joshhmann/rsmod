package org.rsmod.content.areas.city.varrock.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Apothecary @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.apothecary) { apothecaryDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.apothecaryDialogue(npc: Npc) =
        startDialogue(npc) { apothecaryChat() }

    private suspend fun Dialogue.apothecaryChat() {
        chatNpc(neutral, "Good day. I can mix potions or identify herbs for you.")
        chatPlayer(quiz, "Do you have anything for me?")
        chatNpc(neutral, "I'm afraid not at the moment.")
    }
}
