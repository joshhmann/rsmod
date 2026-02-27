package org.rsmod.content.quests.cooksassistant

import jakarta.inject.Inject
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.cooksassistant.configs.cooks_assistant_npcs
import org.rsmod.content.quests.cooksassistant.configs.cooks_assistant_objs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class CooksAssistant @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(cooks_assistant_npcs.cook) { startCookDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startCookDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.cooks_assistant)) {
                0 -> cookStartQuestDialogue()
                1 -> cookInProgressDialogue()
                else -> cookFinishedDialogue()
            }
        }

    private suspend fun Dialogue.cookStartQuestDialogue() {
        chatNpc(sad, "What am I to do?")
        val option =
            choice4(
                "What's wrong?",
                1,
                "Can you make me a cake?",
                2,
                "You don't look very happy.",
                3,
                "Nice hat.",
                4,
            )
        when (option) {
            1 -> {
                chatPlayer(quiz, "What's wrong?")
                explainQuestProblem()
            }
            2 -> {
                chatPlayer(quiz, "Can you make me a cake?")
                chatNpc(
                    sad,
                    "I don't have time for that. The Duke is going to be very angry with me.",
                )
                val option2 = choice2("What's wrong?", 1, "Well, good luck.", 2)
                when (option2) {
                    1 -> {
                        chatPlayer(quiz, "What's wrong?")
                        explainQuestProblem()
                    }
                    2 -> chatPlayer(neutral, "Well, good luck.")
                }
            }
            3 -> {
                chatPlayer(quiz, "You don't look very happy.")
                explainQuestProblem()
            }
            4 -> {
                chatPlayer(happy, "Nice hat.")
                chatNpc(sad, "Thanks, but I need ingredients for a cake.")
                explainQuestProblem()
            }
        }
    }

    private suspend fun Dialogue.explainQuestProblem() {
        chatNpc(
            sad,
            "It's the Duke's birthday today, and I should be making him a big cake for the celebration.",
        )
        chatNpc(
            sad,
            "I've forgotten to buy the ingredients! I'll never get them in time now. He'll sack me!",
        )
        val option =
            choice2(
                "I'll help you. What do you need?",
                1,
                "I can't help, I have important things to do.",
                2,
            )
        when (option) {
            1 -> {
                chatPlayer(happy, "I'll help you. What do you need?")
                chatNpc(
                    happy,
                    "Oh, thank you! You must get me: a bucket of milk, an egg, and a pot of flour.",
                )
                chatNpc(
                    happy,
                    "You can find milk from the dairy cows near Lumbridge, eggs from chickens, " +
                        "and flour from the mill north of Lumbridge.",
                )
                access.setQuestStage(QuestList.cooks_assistant, 1)
            }
            2 -> chatPlayer(neutral, "I can't help, I have important things to do.")
        }
    }

    private suspend fun Dialogue.cookInProgressDialogue() {
        val hasMilk = cooks_assistant_objs.bucket_milk in player.inv
        val hasFlour = cooks_assistant_objs.pot_flour in player.inv
        val hasEgg = cooks_assistant_objs.egg in player.inv

        when {
            hasMilk && hasFlour && hasEgg -> {
                chatPlayer(happy, "I've got all the ingredients you asked for.")
                chatNpc(happy, "You've brought me everything I need! You're a lifesaver!")
                completeQuest()
            }
            hasMilk || hasFlour || hasEgg -> {
                chatNpc(quiz, "How are you getting on with finding the ingredients?")
                val itemsStillNeeded = buildList {
                    if (!hasMilk) add("a bucket of milk")
                    if (!hasFlour) add("a pot of flour")
                    if (!hasEgg) add("an egg")
                }
                chatPlayer(neutral, "I still need to get: ${itemsStillNeeded.joinToString(", ")}.")
                chatNpc(neutral, "Remember, you can find milk from dairy cows near Lumbridge.")
                chatNpc(neutral, "Eggs from chickens, and flour from the mill north of Lumbridge.")
            }
            else -> {
                chatNpc(quiz, "How are you getting on with finding the ingredients?")
                chatPlayer(
                    neutral,
                    "I still need to get: a bucket of milk, a pot of flour, and an egg.",
                )
                chatNpc(neutral, "Remember, you can find milk from dairy cows near Lumbridge.")
                chatNpc(neutral, "Eggs from chickens, and flour from the mill north of Lumbridge.")
            }
        }
    }

    private suspend fun Dialogue.completeQuest() {
        // Remove the ingredients
        val removedMilk = player.invDel(player.inv, cooks_assistant_objs.bucket_milk, 1).success
        val removedFlour = player.invDel(player.inv, cooks_assistant_objs.pot_flour, 1).success
        val removedEgg = player.invDel(player.inv, cooks_assistant_objs.egg, 1).success

        if (!removedMilk || !removedFlour || !removedEgg) {
            chatNpc(sad, "Hmm, it seems you don't have all the ingredients after all.")
            // Return any removed items
            if (removedMilk) {
                player.invAddOrDrop(objRepo, cooks_assistant_objs.bucket_milk, 1)
            }
            if (removedFlour) {
                player.invAddOrDrop(objRepo, cooks_assistant_objs.pot_flour, 1)
            }
            if (removedEgg) {
                player.invAddOrDrop(objRepo, cooks_assistant_objs.egg, 1)
            }
            return
        }

        access.setQuestStage(QuestList.cooks_assistant, 2)
        access.showCompletionScroll(
            quest = QuestList.cooks_assistant,
            rewards = listOf("1 Quest Point", "300 Cooking XP"),
            itemModel = cooks_assistant_objs.bucket_milk,
            questPoints = 1,
        )
    }

    private suspend fun Dialogue.cookFinishedDialogue() {
        chatNpc(happy, "Hello friend! Thanks to you, the Duke's cake was delicious!")
        chatPlayer(happy, "Glad I could help!")
    }
}
