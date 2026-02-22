package org.rsmod.content.quests.witchspotion

import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.giveQuestReward
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.witchspotion.configs.witchs_potion_npcs
import org.rsmod.content.quests.witchspotion.configs.witchs_potion_objs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class WitchsPotion : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(witchs_potion_npcs.hetty) { startHettyDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startHettyDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.witchs_potion)) {
                0 -> hettyStartDialogue()
                1 -> hettyInProgressDialogue()
                else -> hettyFinishedDialogue()
            }
        }

    private suspend fun Dialogue.hettyStartDialogue() {
        chatNpc(quiz, "What could you want with an old woman like me?")
        val option = choice2("Do you have any work for me?", 1, "Nothing, sorry.", 2)
        when (option) {
            1 -> {
                chatPlayer(quiz, "Do you have any work for me?")
                chatNpc(neutral, "I can teach you a little witchcraft. Bring me four ingredients:")
                chatNpc(neutral, "A rat's tail, an eye of newt, a burnt meat and an onion.")
                chatPlayer(happy, "I'll bring them to you.")
                access.setQuestStage(QuestList.witchs_potion, 1)
            }
            2 -> chatPlayer(neutral, "Nothing, sorry.")
        }
    }

    private suspend fun Dialogue.hettyInProgressDialogue() {
        if (!hasAllIngredients()) {
            chatNpc(quiz, "Have you brought me the ingredients?")
            chatPlayer(neutral, "Not all of them yet.")
            val missing = missingIngredients()
            if (missing.isNotEmpty()) {
                chatNpc(neutral, "You still need: ${missing.joinToString(", ")}.")
            }
            return
        }

        chatPlayer(happy, "I've brought everything you asked for.")
        chatNpc(happy, "Excellent. Into the cauldron they go...")
        removeIngredients()
        chatNpc(happy, "Now drink from the cauldron and your mind shall expand.")
        chatPlayer(happy, "I can feel the magic already.")
        completeQuest()
    }

    private suspend fun Dialogue.hettyFinishedDialogue() {
        chatNpc(happy, "You have learned all I can teach for now.")
    }

    private suspend fun Dialogue.completeQuest() {
        access.setQuestStage(QuestList.witchs_potion, 2)
        access.giveQuestReward(QuestList.witchs_potion)
        access.showCompletionScroll(
            quest = QuestList.witchs_potion,
            rewards = listOf("1 Quest Point", "325 Magic XP"),
            questPoints = 1,
        )
    }

    private suspend fun Dialogue.hasAllIngredients(): Boolean {
        val inv = player.inv
        return inv.contains(witchs_potion_objs.rats_tail) &&
            inv.contains(witchs_potion_objs.eye_of_newt) &&
            inv.contains(witchs_potion_objs.burnt_meat) &&
            inv.contains(witchs_potion_objs.onion)
    }

    private suspend fun Dialogue.removeIngredients() {
        player.invDel(player.inv, witchs_potion_objs.rats_tail, 1)
        player.invDel(player.inv, witchs_potion_objs.eye_of_newt, 1)
        player.invDel(player.inv, witchs_potion_objs.burnt_meat, 1)
        player.invDel(player.inv, witchs_potion_objs.onion, 1)
    }

    private suspend fun Dialogue.missingIngredients(): List<String> {
        val missing = mutableListOf<String>()
        if (!player.inv.contains(witchs_potion_objs.rats_tail)) missing += "rat's tail"
        if (!player.inv.contains(witchs_potion_objs.eye_of_newt)) missing += "eye of newt"
        if (!player.inv.contains(witchs_potion_objs.burnt_meat)) missing += "burnt meat"
        if (!player.inv.contains(witchs_potion_objs.onion)) missing += "onion"
        return missing
    }
}
