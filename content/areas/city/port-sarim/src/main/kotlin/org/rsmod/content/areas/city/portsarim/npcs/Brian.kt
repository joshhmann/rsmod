package org.rsmod.content.areas.city.portsarim.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.portsarim.configs.portsarim_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Brian : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(portsarim_npcs.brian) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) { brianChat() }

    private suspend fun Dialogue.brianChat() {
        chatNpc(happy, "Welcome to Brian's Battleaxe Bazaar! The finest battleaxes in Port Sarim!")
        val choice = choice3(
            "What battleaxes do you sell?",
            1,
            "Why battleaxes?",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "What battleaxes do you sell?")
                chatNpc(happy, "I sell all kinds! Bronze through rune, and everything in between!")
                chatNpc(neutral, "Right-click me and select Trade to see my stock.")
            }
            2 -> {
                chatPlayer(quiz, "Why battleaxes?")
                chatNpc(neutral, "A battleaxe is a warrior's best friend! " +
                    "Good balance of strength and speed.")
                chatNpc(happy, "You can't go wrong with a quality battleaxe, mark my words!")
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }
}
