package org.rsmod.content.quests.runemysteries

import jakarta.inject.Inject
import org.rsmod.api.invtx.invAdd
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
import org.rsmod.content.quests.runemysteries.configs.rune_mysteries_npcs
import org.rsmod.content.quests.runemysteries.configs.rune_mysteries_objs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class RuneMysteries @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(rune_mysteries_npcs.duke_of_lumbridge) { startDukeDialogue(it.npc) }

        onOpNpc1(rune_mysteries_npcs.sedridor) { startSedridorDialogue(it.npc) }
        onOpNpc1(rune_mysteries_npcs.sedridor_1op) { startSedridorDialogue(it.npc) }
        onOpNpc1(rune_mysteries_npcs.sedridor_2op) { startSedridorDialogue(it.npc) }

        onOpNpc1(rune_mysteries_npcs.aubury) { startAuburyDialogue(it.npc) }
        onOpNpc1(rune_mysteries_npcs.aubury_2op) { startAuburyDialogue(it.npc) }
        onOpNpc1(rune_mysteries_npcs.aubury_3op) { startAuburyDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDukeDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.rune_mysteries)) {
                0 -> dukeStartQuestDialogue()
                1 -> dukeInProgressDialogue()
                else -> dukeFinishedDialogue()
            }
        }

    private suspend fun ProtectedAccess.startSedridorDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.rune_mysteries)) {
                0 -> sedridorBeforeStartDialogue()
                1 -> sedridorInProgressDialogue()
                else -> sedridorFinishedDialogue()
            }
        }

    private suspend fun ProtectedAccess.startAuburyDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.rune_mysteries)) {
                0 -> auburyBeforeStartDialogue()
                1 -> auburyInProgressDialogue()
                else -> auburyFinishedDialogue()
            }
        }

    private suspend fun Dialogue.dukeStartQuestDialogue() {
        chatNpc(happy, "Greetings. Welcome to my castle.")
        val option =
            choice3("Have you any quests for me?", 1, "Where can I find money?", 2, "Goodbye.", 3)
        when (option) {
            1 -> {
                chatPlayer(quiz, "Have you any quests for me?")
                chatNpc(
                    quiz,
                    "Actually yes. Recently I have been sent a rather strange talisman, " +
                        "but I have no idea what it is for.",
                )
                chatNpc(
                    quiz,
                    "Could you take it to the head wizard in the Wizards' Tower? " +
                        "I am sure he will know more than I do.",
                )
                giveTalismanAndStartQuest()
            }
            2 -> {
                chatPlayer(quiz, "Where can I find money?")
                chatNpc(
                    neutral,
                    "I've heard that there are many ways to earn gold in this kingdom.",
                )
                chatNpc(
                    neutral,
                    "You could try killing goblins, or perhaps cow hides are valuable.",
                )
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }

    private suspend fun Dialogue.giveTalismanAndStartQuest() {
        if (!player.inv.hasFreeSpace()) {
            chatNpc(sad, "It seems your inventory is full. Make some room and speak to me again.")
            return
        }

        val added = player.invAdd(player.inv, rune_mysteries_objs.digtalisman, 1).success
        if (!added) {
            chatNpc(sad, "It seems your inventory is full. Make some room and speak to me again.")
            return
        }
        access.setQuestStage(QuestList.rune_mysteries, 1)
        chatNpc(happy, "Take this talisman to Sedridor, the head wizard, for me.")
    }

    private suspend fun Dialogue.dukeInProgressDialogue() {
        chatNpc(quiz, "Have you been able to identify that strange talisman yet?")

        when {
            rune_mysteries_objs.digtalisman in player.inv -> {
                chatPlayer(quiz, "Not yet. I still need to take it to Sedridor.")
                chatNpc(happy, "The Wizards' Tower is south of Draynor Village.")
            }
            rune_mysteries_objs.research_package in player.inv -> {
                chatPlayer(quiz, "I have a package for Aubury in Varrock.")
                chatNpc(happy, "Excellent. He runs the rune shop in Varrock square.")
            }
            else -> {
                chatPlayer(sad, "I seem to have lost the talisman.")
                if (!player.inv.hasFreeSpace()) {
                    chatNpc(sad, "You will need a free inventory slot before I can replace it.")
                    return
                }
                val added = player.invAdd(player.inv, rune_mysteries_objs.digtalisman, 1).success
                if (!added) {
                    chatNpc(sad, "You will need a free inventory slot before I can replace it.")
                    return
                }
                chatNpc(happy, "Do be careful this time. Please take it to Sedridor.")
            }
        }
    }

    private suspend fun Dialogue.dukeFinishedDialogue() {
        chatNpc(happy, "Thank you for your help. I understand the talisman now.")
        chatPlayer(happy, "Glad I could be of assistance.")
    }

    private suspend fun Dialogue.sedridorBeforeStartDialogue() {
        chatNpc(quiz, "Greetings adventurer. The essence of runes is fascinating, isn't it?")
    }

    private suspend fun Dialogue.sedridorInProgressDialogue() {
        when {
            rune_mysteries_objs.digtalisman in player.inv -> handInTalismanToSedridor()
            rune_mysteries_objs.research_package in player.inv -> {
                chatNpc(quiz, "Please take that research package to Aubury in Varrock for me.")
            }
            else -> {
                chatNpc(
                    sad,
                    "You do not have the talisman. The Duke can provide another if needed.",
                )
            }
        }
    }

    private suspend fun Dialogue.handInTalismanToSedridor() {
        chatPlayer(quiz, "The Duke of Lumbridge sent me with this talisman.")
        chatNpc(quiz, "Ah, remarkable! This is exactly the sort of artifact I study.")
        chatNpc(
            quiz,
            "Please take this research package to Aubury in Varrock. " +
                "He can assist with my findings.",
        )

        val removed = player.invDel(player.inv, rune_mysteries_objs.digtalisman, 1).success
        if (!removed) {
            chatNpc(sad, "Hmm. It seems you no longer have the talisman.")
            return
        }

        val added = player.invAdd(player.inv, rune_mysteries_objs.research_package, 1).success
        if (!added) {
            chatNpc(
                sad,
                "You need a free inventory slot to carry the package. Return when you have space.",
            )
            player.invAddOrDrop(objRepo, rune_mysteries_objs.digtalisman, 1)
            return
        }
    }

    private suspend fun Dialogue.sedridorFinishedDialogue() {
        chatNpc(happy, "Excellent work. You now understand enough to begin Runecrafting.")
    }

    private suspend fun Dialogue.auburyBeforeStartDialogue() {
        chatNpc(quiz, "Can I help you? I stock all manner of runes.")
    }

    private suspend fun Dialogue.auburyInProgressDialogue() {
        if (rune_mysteries_objs.research_package !in player.inv) {
            chatNpc(quiz, "If Sedridor has sent you, bring me his research package.")
            return
        }

        chatPlayer(quiz, "Sedridor sent me with this package.")
        chatNpc(
            happy,
            "Excellent. I shall make sure it reaches him immediately. " + "Thank you for your help.",
        )

        val removed = player.invDel(player.inv, rune_mysteries_objs.research_package, 1).success
        if (!removed) {
            chatNpc(sad, "Hmm. It seems the package is no longer in your pack.")
            return
        }

        access.setQuestStage(QuestList.rune_mysteries, 2)
        access.showCompletionScroll(
            quest = QuestList.rune_mysteries,
            rewards = listOf("1 Quest Point", "Access to the Runecrafting skill"),
            itemModel = rune_mysteries_objs.digtalisman,
            questPoints = 1,
        )
    }

    private suspend fun Dialogue.auburyFinishedDialogue() {
        chatNpc(happy, "Good to see you again. How goes your Runecrafting?")
    }
}
