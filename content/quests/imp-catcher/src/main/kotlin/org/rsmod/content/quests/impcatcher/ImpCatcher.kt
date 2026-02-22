package org.rsmod.content.quests.impcatcher

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.impcatcher.configs.imp_catcher_npcs
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
        chatNpc(sad, "Please find my beads. The imps have scattered them all over Gielinor!")
        // TODO: Check if player has all 4 beads (black, red, white, yellow)
        // TODO: Remove beads from inventory
        // TODO: Grant Amulet of Accuracy and 875 Magic XP
        // TODO: Complete quest
    }

    private suspend fun Dialogue.mizgogFinishedDialogue() {
        chatNpc(happy, "Thank you again for helping me recover my beads!")
    }
}
