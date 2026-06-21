package org.rsmod.content.quests.sheepshearer

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.giveQuestReward
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpcU
import org.rsmod.content.quests.sheepshearer.configs.sheep_shearer_npcs
import org.rsmod.content.quests.sheepshearer.configs.sheep_shearer_objs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class SheepShearer @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(sheep_shearer_npcs.fred_the_farmer) { startFredDialogue(it.npc) }
        onOpNpcU(content.sheep, objs.shears) { shearSheep(it.npc) }
    }

    private suspend fun ProtectedAccess.startFredDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.sheep_shearer)) {
                0 -> fredStartDialogue()
                1 -> fredInProgressDialogue()
                else -> fredFinishedDialogue()
            }
        }

    private suspend fun Dialogue.fredStartDialogue() {
        chatNpc(
            neutral,
            "Hello there, I'm Fred the Farmer. I need some wool for my wife's spinning, " +
                "but I've been so busy with the animals I haven't had time to shear my sheep.",
        )
        val option =
            choice2(
                "Can I help?",
                1,
                "Sorry, I'm busy.",
                2,
            )
        when (option) {
            1 -> {
                chatPlayer(neutral, "Can I help?")
                chatNpc(
                    happy,
                    "Would you? That'd be grand! I need 20 balls of wool. Use your " +
                        "shears on the sheep to get some wool!",
                )
                chatPlayer(happy, "I'll get right on it!")
                access.setQuestStage(QuestList.sheep_shearer, 1)
            }
            2 -> {
                chatPlayer(neutral, "Sorry, I'm busy.")
                chatNpc(neutral, "Oh, alright. Come back if you change your mind.")
            }
        }
    }

    private suspend fun Dialogue.fredInProgressDialogue() {
        val woolInInv = player.inv.count(sheep_shearer_objs.wool)

        if (woolInInv > 0) {
            chatNpc(happy, "Have you brought some wool for me?")
            chatPlayer(happy, "Yes, here you go!")

            player.invDel(player.inv, sheep_shearer_objs.wool, woolInInv)

            val stage = getQuestStage(QuestList.sheep_shearer)
            val givenSoFar = (stage - 1).coerceAtLeast(0)
            val newTotal = (givenSoFar + woolInInv).coerceAtMost(20)

            if (newTotal >= 20) {
                chatNpc(
                    happy,
                    "That's 20 balls of wool! Thank you, thank you! " +
                        "Here, let me reward you for all your hard work.",
                )
                completeQuest()
            } else {
                setQuestStage(QuestList.sheep_shearer, newTotal + 1)
                chatNpc(
                    happy,
                    "Great, that's $newTotal balls of wool so far. " +
                        "I still need ${20 - newTotal} more.",
                )
            }
        } else {
            val stage = getQuestStage(QuestList.sheep_shearer)
            val givenSoFar = (stage - 1).coerceAtLeast(0)

            if (givenSoFar == 0) {
                val hasShears = player.inv.contains(objs.shears)
                if (!hasShears) {
                    chatNpc(neutral, "You'll need some shears to shear the sheep!")
                } else {
                    chatNpc(
                        neutral,
                        "You haven't brought any wool yet. " +
                            "Use your shears on a sheep to get some!",
                    )
                }
                chatPlayer(neutral, "Right, I'll get on with it.")
            } else {
                chatNpc(
                    neutral,
                    "Have you brought me any wool yet? " +
                        "I still need wool, you know!",
                )
                chatPlayer(neutral, "Not yet, I'm still working on it.")
                chatNpc(happy, "Well, don't forget to use your shears on the sheep!")
            }
        }
    }

    private suspend fun Dialogue.fredFinishedDialogue() {
        chatNpc(happy, "Hello again, friend! Thanks for all your help with the shearing!")
        chatPlayer(happy, "No problem, Fred. Happy to help!")
    }

    private suspend fun Dialogue.completeQuest() {
        access.setQuestStage(QuestList.sheep_shearer, 2)
        access.giveQuestReward(QuestList.sheep_shearer)
        access.showCompletionScroll(
            quest = QuestList.sheep_shearer,
            rewards = listOf("1 Quest Point", "150 Crafting XP", "60 Coins"),
            itemModel = sheep_shearer_objs.wool,
            questPoints = 1,
        )
    }

    private suspend fun ProtectedAccess.shearSheep(npc: Npc) {
        val stage = getQuestStage(QuestList.sheep_shearer)
        if (stage != 1) {
            mes("You have no reason to shear the sheep.")
            return
        }

        if (player.inv.isFull()) {
            mes("Your inventory is too full to carry any wool.")
            return
        }

        anim(seqs.human_shearing)

        invAddOrDrop(objRepo, sheep_shearer_objs.wool, 1)

        val total = player.inv.count(sheep_shearer_objs.wool)
        if (total < 20) {
            mes("You shear the sheep and get some wool.")
        } else {
            mes("You have enough wool to take back to Fred!")
        }
    }
}
