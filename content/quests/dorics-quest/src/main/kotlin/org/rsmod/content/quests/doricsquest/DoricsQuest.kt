package org.rsmod.content.quests.doricsquest

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
import org.rsmod.content.quests.doricsquest.configs.dorics_quest_npcs
import org.rsmod.content.quests.doricsquest.configs.dorics_quest_objs
import org.rsmod.game.entity.Npc
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class DoricsQuest @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(dorics_quest_npcs.doric) { startDoricDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDoricDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.dorics_quest)) {
                0 -> doricStartQuestDialogue()
                1 -> doricInProgressDialogue()
                else -> doricFinishedDialogue()
            }
        }

    private suspend fun Dialogue.doricStartQuestDialogue() {
        chatNpc(sad, "Hello traveller, I need some help with my supplies.")
        val option = choice2("Do you have a quest for me?", 1, "Can I use your anvils?", 2)
        when (option) {
            1 -> {
                chatPlayer(quiz, "Do you have a quest for me?")
                chatNpc(sad, "I am sick of people sneaking into my house to use my anvils.")
                chatNpc(
                    quiz,
                    "If you want me to let you use them, bring me 6 clay, 4 copper ore, and 2 iron ore.",
                )
                access.setQuestStage(QuestList.dorics_quest, 1)
            }
            2 -> {
                chatPlayer(quiz, "Can I use your anvils?")
                chatNpc(
                    angry,
                    "Not unless you help me first. Bring me 6 clay, 4 copper ore, and 2 iron ore.",
                )
                access.setQuestStage(QuestList.dorics_quest, 1)
            }
        }
    }

    private suspend fun Dialogue.doricInProgressDialogue() {
        val hasClay = inventoryCount(dorics_quest_objs.clay) >= 6
        val hasCopper = inventoryCount(dorics_quest_objs.copper_ore) >= 4
        val hasIron = inventoryCount(dorics_quest_objs.iron_ore) >= 2

        when {
            hasClay && hasCopper && hasIron -> {
                chatPlayer(happy, "I've brought all the materials you asked for.")
                chatNpc(happy, "Excellent! This is exactly what I needed.")
                completeQuest()
            }
            hasClay || hasCopper || hasIron -> {
                chatNpc(quiz, "Did you bring me the materials?")
                val missing = buildList {
                    if (!hasClay) add("6 clay")
                    if (!hasCopper) add("4 copper ore")
                    if (!hasIron) add("2 iron ore")
                }
                chatPlayer(neutral, "Not yet. I still need: ${missing.joinToString(", ")}.")
                chatNpc(neutral, "Bring them to me and I'll reward you.")
            }
            else -> {
                chatNpc(quiz, "Did you bring me 6 clay, 4 copper ore, and 2 iron ore?")
                chatPlayer(neutral, "Not yet. I'm still gathering them.")
            }
        }
    }

    private suspend fun Dialogue.completeQuest() {
        val removedClay = player.invDel(player.inv, dorics_quest_objs.clay, 6).success
        val removedCopper = player.invDel(player.inv, dorics_quest_objs.copper_ore, 4).success
        val removedIron = player.invDel(player.inv, dorics_quest_objs.iron_ore, 2).success

        if (!removedClay || !removedCopper || !removedIron) {
            chatNpc(sad, "Hmm, you don't seem to have everything I asked for.")
            if (removedClay) player.invAddOrDrop(objRepo, dorics_quest_objs.clay, 6)
            if (removedCopper) player.invAddOrDrop(objRepo, dorics_quest_objs.copper_ore, 4)
            if (removedIron) player.invAddOrDrop(objRepo, dorics_quest_objs.iron_ore, 2)
            return
        }

        val gaveCoins = player.invAdd(player.inv, dorics_quest_objs.coins, 180).success
        if (!gaveCoins) {
            chatNpc(sad, "You need one free inventory slot for your coin reward.")
            player.invAddOrDrop(objRepo, dorics_quest_objs.clay, 6)
            player.invAddOrDrop(objRepo, dorics_quest_objs.copper_ore, 4)
            player.invAddOrDrop(objRepo, dorics_quest_objs.iron_ore, 2)
            return
        }

        access.setQuestStage(QuestList.dorics_quest, 2)
        access.showCompletionScroll(
            quest = QuestList.dorics_quest,
            rewards =
                listOf("1 Quest Point", "1,300 Mining XP", "180 coins", "Use of Doric's anvils"),
            itemModel = dorics_quest_objs.iron_ore,
            questPoints = 1,
        )
    }

    private suspend fun Dialogue.doricFinishedDialogue() {
        chatNpc(happy, "Good to see you again. Feel free to use my anvils anytime.")
        chatPlayer(happy, "Thanks, Doric.")
    }

    private fun Dialogue.inventoryCount(obj: ObjType): Int {
        return player.inv.filterNotNull { it.id == obj.id }.sumOf { it.count }
    }
}
