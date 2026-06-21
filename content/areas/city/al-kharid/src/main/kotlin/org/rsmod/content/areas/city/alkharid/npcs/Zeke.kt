package org.rsmod.content.areas.city.alkharid.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Zeke : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(al_kharid_npcs.zeke) { startDialogue(it.npc) }
    }
    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatNpc(happy, "Welcome to Zeke's Superior Scimitars! Finest blades in Al Kharid!")
            mainMenu()
        }
    private suspend fun Dialogue.mainMenu() {
        val choice = choice3("Let me see your scimitars.", 1, "Tell me about scimitars.", 2, "Goodbye.", 3)
        when (choice) { 1 -> browseShop(); 2 -> aboutScimitars(); 3 -> goodbye() }
    }
    private suspend fun Dialogue.browseShop() {
        chatPlayer(neutral, "Let me see your scimitars.")
        chatNpc(happy, "Of course! I've got the best curved blades this side of the desert.")
    }
    private suspend fun Dialogue.aboutScimitars() {
        chatPlayer(quiz, "Tell me about scimitars.")
        chatNpc(happy, "Ah, the scimitar! The finest weapon for any warrior. Fast, accurate, and deadly in the right hands.")
        chatNpc(neutral, "A scimitar's curved design allows for swift slashing attacks. Many say it's the best all-around weapon in Gielinor!")
    }
    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "May your blade stay sharp and your aim true!")
    }
}