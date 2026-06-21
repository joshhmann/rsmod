package org.rsmod.content.areas.city.draynor.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.draynor.configs.draynor_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Morgan : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(draynor_npcs.morgan) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(sad, "Please, you must help me! A great evil has befallen our village!")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "What is the matter?",
            1,
            "Who are you?",
            2,
            "I am not interested.",
            3,
        )
        when (choice) {
            1 -> whatsWrong()
            2 -> whoAreYou()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.whatsWrong() {
        chatPlayer(quiz, "What is the matter?")
        chatNpc(
            sad,
            "It is Count Draynor! He is a vampire who lives in the manor " +
                "to the north. He has been terrorizing our village for years, " +
                "and we need someone brave enough to confront him!",
        )
        chatNpc(
            neutral,
            "I have heard that Father Urhney, a priest who lives in the " +
                "swamp south of here, might have some knowledge that " +
                "could help us. Perhaps you should speak with him.",
        )
    }

    private suspend fun Dialogue.whoAreYou() {
        chatPlayer(quiz, "Who are you?")
        chatNpc(
            neutral,
            "I am Morgan, a resident of Draynor Village. I have lived here " +
                "all my life and seen the suffering that the vampire " +
                "in the manor has caused.",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "I am not interested.")
        chatNpc(sad, "If you change your mind, please come back. We need all the help we can get.")
    }
}
