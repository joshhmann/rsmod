package org.rsmod.content.areas.city.falador.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.falador.configs.falador_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Wayne : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(falador_npcs.wayne) { startDialogue(it.npc) }
    }
    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatNpc(happy, "Welcome to Wayne's Chains! Finest armour in Falador!")
            mainMenu()
        }
    private suspend fun Dialogue.mainMenu() {
        val choice = choice3("Let me see your armour.", 1, "Tell me about your chains.", 2, "Goodbye.", 3)
        when (choice) { 1 -> browseShop(); 2 -> aboutChains(); 3 -> goodbye() }
    }
    private suspend fun Dialogue.browseShop() {
        chatPlayer(neutral, "Let me see your armour.")
        chatNpc(happy, "Of course! I've got chainbodies from bronze to steel.")
    }
    private suspend fun Dialogue.aboutChains() {
        chatPlayer(quiz, "Tell me about your chains.")
        chatNpc(neutral, "Chainbodies are excellent armour - they offer great protection while still allowing good mobility. Perfect for adventurers on the move!")
        chatNpc(happy, "I craft each chainbody myself. Quality you can trust!")
    }
    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "Stay safe out there, and keep your armour polished!")
    }
}