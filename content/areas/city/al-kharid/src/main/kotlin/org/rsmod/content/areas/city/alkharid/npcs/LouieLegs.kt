package org.rsmod.content.areas.city.alkharid.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class LouieLegs : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(al_kharid_npcs.louie_legs) { startDialogue(it.npc) }
    }
    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatNpc(happy, "Welcome! Louie Legs here - finest legwear in Al Kharid!")
            mainMenu()
        }
    private suspend fun Dialogue.mainMenu() {
        val choice = choice3("Let me see your wares.", 1, "What kind of armour do you sell?", 2, "Goodbye.", 3)
        when (choice) { 1 -> browseShop(); 2 -> aboutArmour(); 3 -> goodbye() }
    }
    private suspend fun Dialogue.browseShop() {
        chatPlayer(neutral, "Let me see your wares.")
        chatNpc(happy, "Take a look! I've got leather chaps, vambraces, and boots.")
    }
    private suspend fun Dialogue.aboutArmour() {
        chatPlayer(quiz, "What kind of armour do you sell?")
        chatNpc(neutral, "I specialise in light armour for rangers and adventurers. Leather chaps, vambraces, and sturdy boots for your travels.")
        chatNpc(happy, "My leather goods are made from the finest hides in the desert! Light, comfortable, and offering decent protection.")
    }
    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "Take care of your legs, friend!")
    }
}