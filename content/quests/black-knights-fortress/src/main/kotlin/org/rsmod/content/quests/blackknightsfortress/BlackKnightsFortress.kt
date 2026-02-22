package org.rsmod.content.quests.blackknightsfortress

import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.blackknightsfortress.configs.black_knights_fortress_npcs
import org.rsmod.content.quests.blackknightsfortress.configs.black_knights_fortress_objs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Black Knights' Fortress quest implementation.
 *
 * Quest Flow:
 * 1. Talk to Sir Amik Varze in Falador (requires 12 QP) to start (Stage 0 -> 1)
 * 2. Infiltrate fortress with iron chainbody + bronze med helm disguise
 * 3. Listen at grill to overhear witch's plan
 * 4. Use cabbage on hole to sabotage the cauldron (Stage 1 -> 2)
 * 5. Return to Sir Amik Varze for reward (Stage 2 -> 3)
 */
class BlackKnightsFortress : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(black_knights_fortress_npcs.sir_amik_varze) { startSirAmikDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startSirAmikDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.black_knights_fortress)) {
                0 -> sirAmikStartQuestDialogue()
                1 -> sirAmikInProgressDialogue()
                2 -> sirAmikSabotagedDialogue()
                else -> sirAmikFinishedDialogue()
            }
        }

    private suspend fun Dialogue.sirAmikStartQuestDialogue() {
        chatNpc(quiz, "Hello. What can I do for you?")

        val option = choice2("I seek a quest!", 1, "Nothing, thanks.", 2)
        when (option) {
            1 -> {
                chatPlayer(happy, "I seek a quest!")
                sirAmikExplainQuest()
            }
            2 -> chatPlayer(neutral, "Nothing, thanks.")
        }
    }

    private suspend fun Dialogue.sirAmikExplainQuest() {
        chatNpc(
            neutral,
            "Your mission, should you decide to accept it, is to infiltrate the Black Knights' Fortress.",
        )
        chatNpc(
            sad,
            "The Black Knights claim to have developed a powerful new secret weapon. " +
                "This is a threat to the security of Asgarnia, and possibly all of Gielinor.",
        )
        chatNpc(
            neutral,
            "I need a spy to infiltrate their fortress and sabotage this weapon. " +
                "Are you up to the task?",
        )

        val option = choice2("I laugh in the face of danger!", 1, "No, I'm a coward.", 2)
        when (option) {
            1 -> {
                chatPlayer(laugh, "I laugh in the face of danger!")
                chatNpc(happy, "Excellent! That's the spirit!")
                chatNpc(
                    neutral,
                    "Take this dossier. It contains everything we know about their operation. " +
                        "Read it carefully - it will self-destruct.",
                )
                giveDossierAndStartQuest()
            }
            2 -> chatPlayer(sad, "No, I'm a coward.")
        }
    }

    private suspend fun Dialogue.giveDossierAndStartQuest() {
        if (!player.inv.hasFreeSpace()) {
            chatNpc(
                sad,
                "You don't have room for the dossier. Clear some inventory space and speak to me again.",
            )
            return
        }

        val added = player.invAdd(player.inv, black_knights_fortress_objs.bk_dossier, 1).success
        if (added) {
            chatNpc(
                happy,
                "Infiltrate the fortress, find their secret weapon, and sabotage it. " +
                    "Good luck!",
            )
            access.setQuestStage(QuestList.black_knights_fortress, 1)
        } else {
            chatNpc(sad, "I couldn't give you the dossier. Make sure you have inventory space.")
        }
    }

    private suspend fun Dialogue.sirAmikInProgressDialogue() {
        chatNpc(quiz, "How is the mission going?")
        chatPlayer(neutral, "I'm working on it.")
        chatNpc(
            neutral,
            "Remember, you need to infiltrate the fortress and sabotage their secret weapon. " +
                "Be careful - the Black Knights are dangerous.",
        )

        // If player lost dossier, remind them they can still continue
        if (black_knights_fortress_objs.bk_dossier !in player.inv) {
            chatNpc(
                quiz,
                "Where is your dossier? No matter - the mission is more important. " +
                    "Don't lose your focus.",
            )
        }
    }

    private suspend fun Dialogue.sirAmikSabotagedDialogue() {
        chatNpc(quiz, "Have you sabotaged the Black Knights' weapon?")
        chatPlayer(
            happy,
            "Yes! I threw a cabbage into their cauldron and destroyed their invincibility potion!",
        )
        chatNpc(
            happy,
            "Excellent work! You've dealt a significant blow to the Black Knights. " +
                "The White Knights are in your debt.",
        )
        completeQuest()
    }

    private suspend fun Dialogue.completeQuest() {
        // Remove dossier if player still has it
        if (black_knights_fortress_objs.bk_dossier in player.inv) {
            player.invDel(player.inv, black_knights_fortress_objs.bk_dossier, 1)
        }

        access.setQuestStage(QuestList.black_knights_fortress, 3)
        access.showCompletionScroll(
            quest = QuestList.black_knights_fortress,
            rewards = listOf("3 Quest Points", "2,500 Coins"),
            itemModel = black_knights_fortress_objs.cabbage,
            questPoints = 3,
        )
    }

    private suspend fun Dialogue.sirAmikFinishedDialogue() {
        chatNpc(
            happy,
            "Hello again! Thanks to you, the Black Knights' plans have been thwarted. " +
                "The White Knights are forever grateful.",
        )
        chatPlayer(happy, "Glad I could help!")
    }
}
