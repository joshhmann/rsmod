package org.rsmod.content.areas.city.draynor.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.draynor.configs.draynor_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Ned : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(draynor_npcs.ned) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(neutral, "Hello there, young fella. I am Ned.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Can you make me some rope?",
            1,
            "What do you do around here?",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> makeRope()
            2 -> whatIDo()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.makeRope() {
        chatPlayer(quiz, "Can you make me some rope?")
        chatNpc(
            happy,
            "I can indeed! Rope is very useful, you know. For climbing, " +
                "tying things up, all sorts of uses! Bring me 15 coins " +
                "and I will make you a good, strong rope.",
        )
    }

    private suspend fun Dialogue.whatIDo() {
        chatPlayer(quiz, "What do you do around here?")
        chatNpc(
            neutral,
            "I make rope mostly. Been doing it for years - there is " +
                "always a demand for good rope. I also know a thing " +
                "or two about ships and sailing, having been a sailor " +
                "in my younger days.",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "Goodbye! And remember, if you need rope, you know where to find me!")
    }
}
