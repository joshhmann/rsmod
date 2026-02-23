package org.rsmod.content.quests.impcatcher

import org.rsmod.api.config.refs.stats
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.impcatcher.configs.imp_catcher_npcs
import org.rsmod.content.quests.impcatcher.configs.imp_catcher_objs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Imp Catcher quest implementation for RSMod v2.
 *
 * Wizard Mizgog in Wizard's Tower needs help retrieving 4 magical beads stolen by imps. Reward:
 * Amulet of Accuracy (875 Magic XP).
 */
class ImpCatcher : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(imp_catcher_npcs.wizard_mizgog) { startMizgogDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startMizgogDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.imp_catcher)) {
                0 -> mizgogStartQuestDialogue()
                1 -> mizgogInProgressDialogue()
                else -> mizgogFinishedDialogue()
            }
        }

    private suspend fun Dialogue.mizgogStartQuestDialogue() {
        chatNpc(quiz, "Can I help you?")

        val option = choice2("Have you any quests for me?", 1, "No thanks.", 2)
        when (option) {
            1 -> {
                chatPlayer(quiz, "Have you any quests for me?")
                chatNpc(
                    sad,
                    "Oh dear, oh dear. The wizard Grayzag has summoned hundreds of little imps, " +
                        "and they've stolen my magical beads!",
                )
                chatNpc(
                    sad,
                    "I need you to find my beads. There are four of them: black, red, white, and yellow.",
                )
                chatNpc(
                    happy,
                    "Please find them and bring them back to me. I'll reward you with an amulet of accuracy!",
                )
                access.setQuestStage(QuestList.imp_catcher, 1)
            }
            2 -> chatNpc(neutral, "Very well.")
        }
    }

    private suspend fun Dialogue.mizgogInProgressDialogue() {
        val hasBlack = imp_catcher_objs.black_bead in player.inv
        val hasRed = imp_catcher_objs.red_bead in player.inv
        val hasWhite = imp_catcher_objs.white_bead in player.inv
        val hasYellow = imp_catcher_objs.yellow_bead in player.inv

        if (hasBlack && hasRed && hasWhite && hasYellow) {
            // Remove all beads
            val removedBlack = player.invDel(player.inv, imp_catcher_objs.black_bead, 1).success
            val removedRed = player.invDel(player.inv, imp_catcher_objs.red_bead, 1).success
            val removedWhite = player.invDel(player.inv, imp_catcher_objs.white_bead, 1).success
            val removedYellow = player.invDel(player.inv, imp_catcher_objs.yellow_bead, 1).success

            if (!removedBlack || !removedRed || !removedWhite || !removedYellow) {
                chatNpc(sad, "Hmm, it seems you no longer have all the beads.")
                return
            }

            // Add amulet of accuracy
            val added = player.invAdd(player.inv, imp_catcher_objs.amulet_of_accuracy, 1).success
            if (!added) {
                // Restore beads if no space
                player.invAdd(player.inv, imp_catcher_objs.black_bead, 1)
                player.invAdd(player.inv, imp_catcher_objs.red_bead, 1)
                player.invAdd(player.inv, imp_catcher_objs.white_bead, 1)
                player.invAdd(player.inv, imp_catcher_objs.yellow_bead, 1)
                chatNpc(sad, "You don't have enough inventory space.")
                return
            }

            // Grant Magic XP
            access.statAdvance(stats.magic, 875.0)

            // Complete the quest
            access.setQuestStage(QuestList.imp_catcher, 2)
            access.showCompletionScroll(
                quest = QuestList.imp_catcher,
                rewards = listOf("875 Magic XP", "Amulet of Accuracy"),
                itemModel = imp_catcher_objs.amulet_of_accuracy,
                questPoints = 1,
            )
        } else {
            // List missing beads
            chatNpc(sad, "Please find my beads. The imps have scattered them all over Gielinor!")
            val missing = mutableListOf<String>()
            if (!hasBlack) missing.add("black")
            if (!hasRed) missing.add("red")
            if (!hasWhite) missing.add("white")
            if (!hasYellow) missing.add("yellow")
            chatPlayer(sad, "I still need to find the ${missing.joinToString(", ")} bead(s).")
        }
    }

    private suspend fun Dialogue.mizgogFinishedDialogue() {
        chatNpc(happy, "Thank you again for helping me recover my beads!")
    }
}
