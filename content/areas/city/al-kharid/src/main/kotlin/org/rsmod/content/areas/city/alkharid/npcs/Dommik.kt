package org.rsmod.content.areas.city.alkharid.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Dommik : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(al_kharid_npcs.dommik) { startDialogue(it.npc) }
    }
    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatNpc(happy, "Welcome to my craft store! I'm Dommik - finest crafting supplies in Al Kharid!")
            mainMenu()
        }
    private suspend fun Dialogue.mainMenu() {
        val choice = choice3("Let me see what you have.", 1, "Tell me about crafting.", 2, "Goodbye.", 3)
        when (choice) { 1 -> browseShop(); 2 -> aboutCrafting(); 3 -> goodbye() }
    }
    private suspend fun Dialogue.browseShop() {
        chatPlayer(neutral, "Let me see what you have.")
        chatNpc(happy, "Of course! Take a look at my wares.")
    }
    private suspend fun Dialogue.aboutCrafting() {
        chatPlayer(quiz, "Tell me about crafting.")
        chatNpc(neutral, "Crafting is a versatile skill! You can make jewellery, leather armour, glass items, and much more.")
        chatNpc(happy, "I sell needles, thread, leather, and other supplies you'll need for your crafting adventures.")
    }
    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "Come back when you need more supplies!")
    }
}