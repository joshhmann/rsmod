package org.rsmod.content.areas.city.alkharid.npcs

import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class BorderGuard : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(al_kharid_npcs.border_guard) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(neutral, "Halt! You must pay the toll to pass through Al Kharid.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "How much is the toll?",
            1,
            "I do not have any money.",
            2,
            "Alright, I will pay.",
            3,
        )
        when (choice) {
            1 -> tollPrice()
            2 -> noMoney()
            3 -> willPay()
        }
    }

    private suspend fun Dialogue.tollPrice() {
        chatPlayer(quiz, "How much is the toll?")
        chatNpc(
            neutral,
            "The toll is 10 coins to pass through the gate. This road " +
                "leads to Al Kharid, a bustling town with many shops " +
                "and opportunities for adventurers.",
        )
    }

    private suspend fun Dialogue.noMoney() {
        chatPlayer(sad, "I do not have any money.")
        chatNpc(
            neutral,
            "No money, no passage. Those are the rules, I am afraid. " +
                "Come back when you have 10 coins.",
        )
    }

    private suspend fun Dialogue.willPay() {
        chatPlayer(neutral, "Alright, I will pay.")
        chatNpc(happy, "Good choice! The gate is open - welcome to Al Kharid!")
    }
}
