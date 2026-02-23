package org.rsmod.content.areas.city.alkharid.npcs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpc3
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Border Guard - controls the toll gate between Lumbridge and Al Kharid. Costs 10 coins to pass, or
 * free if Prince Ali Rescue is complete.
 */
class BorderGuard : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(al_kharid_npcs.border_guard) { startDialogue(it.npc) }
        onOpNpc3(al_kharid_npcs.border_guard) { quickPayToll(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) { borderGuardDialogue(npc) }

    private suspend fun Dialogue.borderGuardDialogue(npc: Npc) {
        // Check if player has completed Prince Ali Rescue
        val questComplete = access.getQuestStage(QuestList.prince_ali_rescue) >= 2

        if (questComplete) {
            chatNpc(
                happy,
                "Hello friend! You may pass for free thanks to your help with Prince Ali.",
            )
            return
        }

        chatNpc(neutral, "Halt! It costs 10 coins to pass through this gate.")
        val choice =
            choice3(
                "Okay, I'll pay.",
                1,
                "No thanks, I'll walk around.",
                2,
                "Why do I have to pay?",
                3,
            )
        when (choice) {
            1 -> payToll()
            2 -> chatPlayer(neutral, "No thanks, I'll walk around.")
            3 -> explainToll()
        }
    }

    private suspend fun Dialogue.explainToll() {
        chatPlayer(quiz, "Why do I have to pay?")
        chatNpc(
            neutral,
            "The town of Al Kharid needs the money to maintain the palace and keep the peace. " +
                "If you complete the Prince Ali Rescue quest, you can pass for free.",
        )
        val choice = choice2("Okay, I'll pay the 10 coins.", 1, "No thanks.", 2)
        if (choice == 1) {
            payToll()
        } else {
            chatPlayer(neutral, "No thanks.")
        }
    }

    private suspend fun ProtectedAccess.quickPayToll(npc: Npc) {
        startDialogue(npc) {
            // Check if player has completed Prince Ali Rescue
            val questComplete = access.getQuestStage(QuestList.prince_ali_rescue) >= 2

            if (questComplete) {
                chatNpc(happy, "Hello friend! You may pass for free.")
                return@startDialogue
            }
            payToll()
        }
    }

    private suspend fun Dialogue.payToll() {
        // Check for coins
        if (objs.coins !in player.inv) {
            chatNpc(angry, "You don't have 10 coins! Come back when you do.")
            return
        }

        if (player.invDel(player.inv, objs.coins, 10).success) {
            chatNpc(happy, "Thank you. You may pass.")
            // Open the gate (this would need loc interaction)
        } else {
            chatNpc(angry, "You don't have 10 coins! Come back when you do.")
        }
    }
}
