package org.rsmod.content.areas.city.varrock.npcs

import jakarta.inject.Inject
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Reldo @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.reldo) { reldoDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.reldoDialogue(npc: Npc) =
        startDialogue(npc) {
            val shieldOfArravStage = getQuestStage(QuestList.shield_of_arrav)
            val knightsSwordStage = getQuestStage(QuestList.knights_sword)

            when {
                // Shield of Arrav not started - offer the quest
                shieldOfArravStage == 0 -> questStartDialogue(npc)
                // Shield of Arrav - looking for or read the book
                shieldOfArravStage == 1 -> shieldOfArravBookDialogue(npc)
                shieldOfArravStage == 2 -> shieldOfArravAfterBookDialogue(npc)
                // Knight's Sword - looking for Imcando dwarf info (stage 1 or 2)
                knightsSwordStage == 1 || knightsSwordStage == 2 -> knightsSwordImcandoDialogue(npc)
                // Default librarian dialogue
                else -> standardDialogue(npc)
            }
        }

    private suspend fun Dialogue.questStartDialogue(npc: Npc) {
        chatNpc(happy, "Greetings! Welcome to the palace library. I'm Reldo, the librarian.")
        val choice =
            choice3(
                "I'm in search of a quest.",
                1,
                "Do you have anything to trade?",
                2,
                "What do you do?",
                3,
            )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "I'm in search of a quest.")
                chatNpc(neutral, "Hmm. I don't believe there are any here...")
                chatNpc(neutral, "Let me think actually...")
                chatNpc(
                    happy,
                    "Ah yes! If you look in a book called 'The Shield of Arrav', you'll find a quest in there.",
                )
                chatNpc(
                    neutral,
                    "I'm not sure where the book is mind you... but I'm sure it's around here somewhere.",
                )
                chatPlayer(happy, "Thank you!")
                access.setQuestStage(QuestList.shield_of_arrav, 1)
            }
            2 -> {
                chatPlayer(quiz, "Do you have anything to trade?")
                chatNpc(neutral, "Only knowledge.")
                chatPlayer(quiz, "How much do you want for that then?")
                chatNpc(neutral, "No, sorry, that was just my little joke. I'm not the trading type.")
                chatPlayer(neutral, "Ah well.")
            }
            3 -> {
                chatPlayer(quiz, "What do you do?")
                chatNpc(happy, "I am the palace librarian.")
                chatPlayer(neutral, "Ah. That's why you're in the library then.")
                chatNpc(neutral, "Yes.")
            }
        }
    }

    private suspend fun Dialogue.standardDialogue(npc: Npc) {
        chatNpc(happy, "Greetings! Welcome to the palace library.")
        val choice =
            choice2(
                "Do you have anything to trade?",
                1,
                "What do you do?",
                2,
            )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "Do you have anything to trade?")
                chatNpc(neutral, "Only knowledge.")
            }
            2 -> {
                chatPlayer(quiz, "What do you do?")
                chatNpc(happy, "I am the palace librarian.")
                chatPlayer(neutral, "Ah. That's why you're in the library then.")
                chatNpc(neutral, "Yes.")
            }
        }
    }

    private suspend fun Dialogue.shieldOfArravBookDialogue(npc: Npc) {
        chatPlayer(quiz, "About that book... where is it again?")
        chatNpc(neutral, "I'm not sure where it is exactly... but I'm sure it's around here somewhere.")
    }

    private suspend fun Dialogue.shieldOfArravAfterBookDialogue(npc: Npc) {
        chatPlayer(quiz, "Ok. I've read the book. Do you know where I can find the Phoenix Gang?")
        chatNpc(neutral, "No, I don't. I think I know someone who might, however.")
        chatNpc(
            neutral,
            "If I were you I would talk to Baraek, the fur trader in the market place. I've heard he has connections with the Phoenix Gang.",
        )
        chatPlayer(happy, "Thanks, I'll try that!")
        access.setQuestStage(QuestList.shield_of_arrav, 3)
    }

    private suspend fun Dialogue.knightsSwordImcandoDialogue(npc: Npc) {
        if (access.getQuestStage(QuestList.knights_sword) == 1) {
            chatPlayer(quiz, "I'm looking for information about the Imcando Dwarves.")
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
            access.setQuestStage(QuestList.knights_sword, 2)
        } else {
            chatNpc(happy, "I've told you all I know about the Imcando dwarves.")
            chatNpc(happy, "Seek out Thurgo near Port Sarim, south of the Ice Dungeon.")
            chatNpc(happy, "And don't forget the redberry pie!")
        }
    }
}
