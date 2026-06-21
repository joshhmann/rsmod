package org.rsmod.content.areas.city.alkharid.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Ranael : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(al_kharid_npcs.ranael) { startDialogue(it.npc) }
    }
    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatNpc(happy, "Welcome! I'm Ranael. I sell the finest skirts and platelegs in Al Kharid!")
            mainMenu()
        }
    private suspend fun Dialogue.mainMenu() {
        val choice = choice3("Let me see your wares.", 1, "What do you recommend?", 2, "Goodbye.", 3)
        when (choice) { 1 -> browseShop(); 2 -> recommendations(); 3 -> goodbye() }
    }
    private suspend fun Dialogue.browseShop() {
        chatPlayer(neutral, "Let me see your wares.")
        chatNpc(happy, "Take a look! I have both platelegs and plateskirts to suit any adventurer.")
    }
    private suspend fun Dialogue.recommendations() {
        chatPlayer(quiz, "What do you recommend?")
        chatNpc(happy, "For a starting adventurer, I'd recommend bronze platelegs. They offer decent protection without being too heavy.")
        chatNpc(neutral, "As you gain more experience, you can upgrade to iron and steel. I stock all the basic metals!")
    }
    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "Come back if you need new legwear!")
    }
}