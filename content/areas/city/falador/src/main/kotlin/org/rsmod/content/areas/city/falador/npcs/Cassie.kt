package org.rsmod.content.areas.city.falador.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.falador.configs.falador_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Cassie : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(falador_npcs.cassie) { startDialogue(it.npc) }
    }
    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatNpc(happy, "Welcome to Cassie's Shield Shop! The best shields in Falador!")
            mainMenu()
        }
    private suspend fun Dialogue.mainMenu() {
        val choice = choice3("Let me see your shields.", 1, "Tell me about shields.", 2, "Goodbye.", 3)
        when (choice) { 1 -> browseShop(); 2 -> aboutShields(); 3 -> goodbye() }
    }
    private suspend fun Dialogue.browseShop() {
        chatPlayer(neutral, "Let me see your shields.")
        chatNpc(happy, "Of course! I have shields for every fighting style.")
    }
    private suspend fun Dialogue.aboutShields() {
        chatPlayer(quiz, "Tell me about shields.")
        chatNpc(neutral, "A good shield can mean the difference between life and death! I sell bronze, iron, and steel shields, as well as wooden ones.")
        chatNpc(happy, "Remember - you can also use a shield to bash your enemies! It's not just for defence.")
    }
    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "Keep your shield up and your wits about you!")
    }
}