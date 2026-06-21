package org.rsmod.content.areas.city.falador.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.falador.configs.falador_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Herquin : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(falador_npcs.herquin) { startDialogue(it.npc) }
    }
    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatNpc(happy, "Welcome to Herquin's Gems! The finest gems in Falador!")
            mainMenu()
        }
    private suspend fun Dialogue.mainMenu() {
        val choice = choice3("Let me see your gems.", 1, "Tell me about gems.", 2, "Goodbye.", 3)
        when (choice) { 1 -> browseShop(); 2 -> aboutGems(); 3 -> goodbye() }
    }
    private suspend fun Dialogue.browseShop() {
        chatPlayer(neutral, "Let me see your gems.")
        chatNpc(happy, "Of course! I have a fine selection of precious stones.")
    }
    private suspend fun Dialogue.aboutGems() {
        chatPlayer(quiz, "Tell me about gems.")
        chatNpc(neutral, "Gems are valuable and versatile! You can cut them and use them in jewellery crafting, or sell them for a nice profit.")
        chatNpc(happy, "I sell uncut gems at good prices. Cut them with a chisel and you can make some beautiful jewellery!")
    }
    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "Come back if you need any gems!")
    }
}