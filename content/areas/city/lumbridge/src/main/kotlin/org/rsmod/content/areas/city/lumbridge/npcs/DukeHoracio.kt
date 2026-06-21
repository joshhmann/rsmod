package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class DukeHoracio : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.duke_of_lumbridge) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Greetings, welcome to my castle.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Who are you?",
            1,
            "Have you any quests for me?",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> introduceYourself()
            2 -> questInquiry()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.introduceYourself() {
        chatPlayer(quiz, "Who are you?")
        chatNpc(
            neutral,
            "I am Duke Horacio, the ruler of Lumbridge and the surrounding lands. " +
                "I have governed this realm for many years and take great pride " +
                "in the prosperity of the kingdom of Misthalin.",
        )
        chatNpc(
            happy,
            "If you ever need guidance, my door is always open to adventurers " +
                "such as yourself.",
        )
    }

    private suspend fun Dialogue.questInquiry() {
        chatPlayer(quiz, "Have you any quests for me?")
        chatNpc(
            quiz,
            "Not at the moment, but keep an eye out. There are always " +
                "people in need of a capable adventurer around here.",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "Farewell, adventurer. May your travels be safe.")
    }
}
