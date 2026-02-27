package org.rsmod.content.areas.city.varrock.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Reldo @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.reldo) { reldoDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.reldoDialogue(npc: Npc) =
        startDialogue(npc) {
            // Check for active quests that Reldo helps with
            val shieldOfArravStage = getQuestStage(QuestList.shield_of_arrav)
            val knightsSwordStage = getQuestStage(QuestList.knights_sword)

            when {
                // Shield of Arrav - looking for the book
                shieldOfArravStage == 1 -> shieldOfArravBookDialogue(npc)
                // Knight's Sword - looking for Imcando dwarf info
                knightsSwordStage == 2 -> knightsSwordImcandoDialogue(npc)
                // Default librarian dialogue
                else -> standardDialogue(npc)
            }
        }

    private suspend fun Dialogue.standardDialogue(npc: Npc) {
        chatNpc(happy, "Greetings! Welcome to the palace library. I'm Reldo, the librarian.")
        val choice =
            choice3(
                "Do you have any quests?",
                1,
                "What do you do here?",
                2,
                "I'm just browsing.",
                3,
            )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "Do you have any quests?")
                chatNpc(
                    neutral,
                    "I don't have any quests myself, but I can help you with information. Have you checked the quest journal?",
                )
                chatNpc(
                    happy,
                    "If you're interested in Varrock's history, you should read about the Shield of Arrav in the books here.",
                )
            }
            2 -> {
                chatPlayer(quiz, "What do you do here?")
                chatNpc(
                    happy,
                    "I maintain the palace library. We have books on all sorts of subjects - history, geography, magic, and more!",
                )
                chatNpc(
                    happy,
                    "Feel free to search the bookcases. You might find something interesting.",
                )
            }
            3 -> {
                chatPlayer(neutral, "I'm just browsing.")
                chatNpc(happy, "Very well. Let me know if you need help finding anything.")
            }
        }
    }

    private suspend fun Dialogue.shieldOfArravBookDialogue(npc: Npc) {
        chatPlayer(quiz, "I'm looking for a book about the Shield of Arrav.")
        chatNpc(
            happy,
            "Ah yes, the Shield of Arrav. It's one of the most famous artifacts in Varrock's history.",
        )
        chatNpc(
            happy,
            "According to the book, the shield was stolen many years ago by a gang called the Phoenix Gang.",
        )
        chatNpc(
            happy,
            "But wait... there was another gang involved too. The Black Arm Gang! They took one half of the shield.",
        )
        chatNpc(
            happy,
            "If you're looking to recover the shield, you'll need to find both halves. Good luck!",
        )
    }

    private suspend fun Dialogue.knightsSwordImcandoDialogue(npc: Npc) {
        chatPlayer(quiz, "Do you know anything about the Imcando Dwarves?")
        chatNpc(
            happy,
            "The Imcando Dwarves? Fascinating subject! They were master smiths who could work with adamantite like no others.",
        )
        chatNpc(
            happy,
            "I believe the last of them lived in Asgarnia, near Port Sarim. Thurgo is the name of the one I've heard about.",
        )
        chatNpc(
            happy,
            "He lives near the Asgarnian Ice Dungeon. You might find him there, if he's still alive.",
        )
        chatNpc(happy, "Oh, and I've heard he has quite a fondness for redberry pie!")
    }
}
