package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.statAdvance
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class FredTheFarmer : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.fred_the_farmer) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            val stage = getQuestStage(QuestList.sheep_shearer)
            when (stage) {
                0 -> startQuestDialogue()
                1 -> progressQuestDialogue()
                2 -> finishedQuestDialogue()
            }
        }

    private suspend fun Dialogue.startQuestDialogue() {
        chatNpc(
            happy,
            "What are you doing on my land? You're not one of those thieves from the castle, are you?",
        )
        when (
            choice3(
                "I'm looking for a quest.",
                1,
                "I'm just looking around.",
                2,
                "Can I kill your chickens?",
                3,
            )
        ) {
            1 -> lookingForQuest()
            2 -> justLooking()
            3 -> killChickens()
        }
    }

    private suspend fun Dialogue.lookingForQuest() {
        chatPlayer(quiz, "I'm looking for a quest.")
        chatNpc(quiz, "A quest, you say? Well, I suppose I could use some help.")
        chatNpc(
            quiz,
            "My sheep need shearing, and I'm far too busy to do it myself. " +
                "If you could shear them and bring me 20 balls of wool, I'd be very grateful.",
        )
        chatNpc(quiz, "So, what do you say? Will you help me out?")
        when (choice2("Yes, I'll help you.", 1, "No, I'm too busy.", 2)) {
            1 -> acceptQuest()
            2 -> declineQuest()
        }
    }

    private suspend fun Dialogue.acceptQuest() {
        chatPlayer(happy, "Yes, I'll help you.")
        chatNpc(happy, "Excellent! You can find my sheep in the field just outside.")
        chatNpc(happy, "Bring me 20 balls of wool when you're done.")
        chatPlayer(happy, "I'll get right on it.")
        access.setQuestStage(QuestList.sheep_shearer, 1)
    }

    private suspend fun Dialogue.declineQuest() {
        chatPlayer(sad, "No, I'm too busy.")
        chatNpc(sad, "Well, off you go then. I've got work to do.")
    }

    private suspend fun Dialogue.justLooking() {
        chatPlayer(happy, "I'm just looking around.")
        chatNpc(happy, "Well, look all you like, but stay out of trouble.")
    }

    private suspend fun Dialogue.killChickens() {
        chatPlayer(quiz, "Can I kill your chickens?")
        chatNpc(angry, "What?! Certainly not! Get off my land before I set the dogs on you!")
    }

    private suspend fun Dialogue.progressQuestDialogue() {
        chatNpc(happy, "How's the shearing going? Have you got those 20 balls of wool for me yet?")
        val woolCount =
            player.inv.objs.sumOf { if (it?.id == objs.ball_of_wool.id) it.count else 0 }
        if (woolCount >= 20) {
            chatPlayer(happy, "Yes, I have them right here.")
            chatNpc(happy, "Marvellous! Give them here, then.")
            val removed = player.invDel(player.inv, objs.ball_of_wool, 20).success
            if (removed) {
                chatNpc(happy, "Thank you very much! Here's a little something for your trouble.")
                finishQuest()
            } else {
                chatPlayer(sad, "Wait, I seem to have misplaced them...")
            }
        } else if (woolCount > 0) {
            chatPlayer(sad, "I have some, but not all 20 yet.")
            chatNpc(happy, "Well, keep at it! I need the full 20 to make it worth my while.")
        } else {
            chatPlayer(sad, "Not yet, I'm still working on it.")
            chatNpc(happy, "Well, hurry up! The sheep won't shear themselves!")
        }
    }

    private suspend fun Dialogue.finishQuest() {
        player.statAdvance(stats.crafting, 150.0)
        player.invAdd(player.inv, objs.coins, 60)
        access.setQuestStage(QuestList.sheep_shearer, 2)
        access.showCompletionScroll(
            quest = QuestList.sheep_shearer,
            rewards = listOf("150 Crafting Experience", "60 Gold Coins", "1 Quest Point"),
            itemModel = objs.ball_of_wool,
            questPoints = 1,
        )
    }

    private suspend fun Dialogue.finishedQuestDialogue() {
        chatNpc(happy, "Hello again! Thanks for all your help with those sheep.")
        chatPlayer(happy, "You're welcome!")
    }
}
