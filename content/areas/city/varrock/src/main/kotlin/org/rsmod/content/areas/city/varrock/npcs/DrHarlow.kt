package org.rsmod.content.areas.city.varrock.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class DrHarlow : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.dr_harlow) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Aha! A friendly face! Pull up a chair, have a drink!")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Who are you?",
            1,
            "Do you know anything useful?",
            2,
            "I have to go.",
            3,
        )
        when (choice) {
            1 -> whoAreYou()
            2 -> usefulKnowledge()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.whoAreYou() {
        chatPlayer(quiz, "Who are you?")
        chatNpc(
            happy,
            "I am Dr Harlow! Physician, philosopher, and connoisseur of fine ales! " +
                "I know a thing or two about the comings and goings of this city.",
        )
        chatNpc(
            shifty,
            "And I may have picked up some interesting gossip over a pint or two...",
        )
    }

    private suspend fun Dialogue.usefulKnowledge() {
        chatPlayer(quiz, "Do you know anything useful?")
        chatNpc(
            neutral,
            "Useful? I know lots of things! For instance, did you know " +
                "that the Blue Moon Inn serves the finest ale in all of " +
                "Varrock? The brewer has a secret recipe, you know...",
        )
        chatNpc(
            shifty,
            "If you are looking for information, the best place to start " +
                "is the bartender. They hear everything in this town.",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "I have to go.")
        chatNpc(happy, "Come back anytime! The drinks are on... well, they are on you, actually!")
    }
}
