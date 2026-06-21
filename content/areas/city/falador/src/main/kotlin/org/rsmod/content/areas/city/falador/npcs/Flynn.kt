package org.rsmod.content.areas.city.falador.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.falador.configs.falador_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Flynn : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(falador_npcs.flynn) { startDialogue(it.npc) }
    }
    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatNpc(happy, "Welcome to Flynn's Mace Market! Best maces in Falador!")
            mainMenu()
        }
    private suspend fun Dialogue.mainMenu() {
        val choice = choice3("Let me see your maces.", 1, "Tell me about maces.", 2, "Goodbye.", 3)
        when (choice) { 1 -> browseShop(); 2 -> aboutMaces(); 3 -> goodbye() }
    }
    private suspend fun Dialogue.browseShop() {
        chatPlayer(neutral, "Let me see your maces.")
        chatNpc(happy, "Take a look! Maces of every metal type.")
    }
    private suspend fun Dialogue.aboutMaces() {
        chatPlayer(quiz, "Tell me about maces.")
        chatNpc(neutral, "The mace is a wonderful weapon! It may not be as fast as a scimitar, but it packs a serious punch and has a special prayer bonus.")
        chatNpc(happy, "Many adventurers favour the mace for its crushing damage - excellent against heavily armoured foes!")
    }
    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "Come back if you need a good mace!")
    }
}