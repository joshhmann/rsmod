package org.rsmod.content.areas.city.varrock.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Romeo : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.romeo) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(sad, "Oh, woe is me! My beloved Juliet! The world is so cruel!")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "What is wrong?",
            1,
            "Who are you?",
            2,
            "I have not got time for this.",
            3,
        )
        when (choice) {
            1 -> whatsWrong()
            2 -> whoAreYou()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.whatsWrong() {
        chatPlayer(quiz, "What is wrong?")
        chatNpc(
            sad,
            "It is Juliet! The love of my life! Her father, the Count, " +
                "forbids us from seeing each other. It is a tragedy of " +
                "Shakespearean proportions!",
        )
        chatNpc(
            neutral,
            "I need someone to deliver a message to her. Would you " +
                "be so kind as to help two star-crossed lovers?",
        )
    }

    private suspend fun Dialogue.whoAreYou() {
        chatPlayer(quiz, "Who are you?")
        chatNpc(
            neutral,
            "I am Romeo, of the house of Montague. I have fallen deeply " +
                "in love with the fair Juliet, of the house of Capulet. " +
                "But alas, our families are sworn enemies!",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "I have not got time for this.")
        chatNpc(sad, "Oh, woe! Will no one help a poor lovesick soul?")
    }
}
