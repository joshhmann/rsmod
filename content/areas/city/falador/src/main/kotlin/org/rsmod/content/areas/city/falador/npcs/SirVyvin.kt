package org.rsmod.content.areas.city.falador.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.falador.configs.falador_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class SirVyvin : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(falador_npcs.sir_vyvin) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            val stage = getQuestStage(QuestList.knights_sword)

            when {
                stage == 0 -> vyvinGenericDialogue()
                stage in 1..6 -> vyvinQuestActiveDialogue()
                stage >= 7 -> vyvinPostQuestDialogue()
                else -> vyvinGenericDialogue()
            }
        }

    private suspend fun Dialogue.vyvinGenericDialogue() {
        chatNpc(happy, "Hello there. I am Sir Vyvin, armourer to the White Knights.")
        mainMenu()
    }

    private suspend fun Dialogue.vyvinQuestActiveDialogue() {
        chatNpc(happy, "Hello. How can I help you?")
        val choice = choice3(
            "I'm looking for a portrait of your father with a ceremonial sword.",
            1,
            "Tell me about your work.",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "I'm looking for a portrait of your father with a ceremonial sword.")
                chatNpc(neutral, "Ah yes, that old painting. It's gathering dust in a cupboard upstairs.")
                chatNpc(neutral, "I keep meaning to hang it somewhere, but I've never gotten around to it.")
                chatNpc(neutral, "Feel free to take a look if you're interested. It's in the cupboard in my chambers.")
                chatNpc(happy, "Just be careful with it - it's been in the family for generations!")
            }
            2 -> {
                chatPlayer(quiz, "Tell me about your work.")
                chatNpc(neutral, "I am responsible for maintaining the armour and weapons of the White Knights. It's a demanding job, but someone has to do it!")
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }

    private suspend fun Dialogue.vyvinPostQuestDialogue() {
        chatNpc(happy, "Hello there. I am Sir Vyvin.")
        val choice = choice2(
            "Tell me about your work.",
            1,
            "Goodbye.",
            2,
        )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "Tell me about your work.")
                chatNpc(neutral, "I am responsible for maintaining the armour and weapons of the White Knights. It's a demanding job, but someone has to do it!")
            }
            2 -> chatPlayer(neutral, "Goodbye.")
        }
    }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Tell me about your work.",
            1,
            "Do you know anything about Imcando dwarves?",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> aboutWork()
            2 -> imcandoDwarves()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.aboutWork() {
        chatPlayer(quiz, "Tell me about your work.")
        chatNpc(neutral, "I am responsible for maintaining the armour and weapons of the White Knights. It's a demanding job, but someone has to do it! I work with the finest smiths in Asgarnia to keep our knights properly equipped.")
    }

    private suspend fun Dialogue.imcandoDwarves() {
        chatPlayer(quiz, "Do you know anything about Imcando dwarves?")
        chatNpc(neutral, "The Imcando dwarves are legendary smiths, known throughout the land for their masterful metalwork. There is one called Thurgo who lives near Port Sarim - a crusty old fellow, but a brilliant smith.")
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "Take care! If you need any armour repaired, you know where to find me.")
    }
}
