package org.rsmod.content.areas.city.portsarim.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.portsarim.configs.portsarim_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Wydin : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(portsarim_npcs.wydin) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Welcome to Wydin's Food Store! Fresh groceries for all your cooking needs!")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "What food do you sell?",
            1,
            "Can you give me cooking advice?",
            2,
            "I am fine, thanks.",
            3,
        )
        when (choice) {
            1 -> foodSelection()
            2 -> cookingAdvice()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.foodSelection() {
        chatPlayer(quiz, "What food do you sell?")
        chatNpc(
            happy,
            "I sell all sorts of fine foods! Raw chicken, raw beef, " +
                "bread, pies, and much more. I source only the freshest " +
                "ingredients from local farms and fishermen.",
        )
    }

    private suspend fun Dialogue.cookingAdvice() {
        chatPlayer(quiz, "Can you give me cooking advice?")
        chatNpc(
            neutral,
            "The key to good cooking is patience! Do not rush your food " +
                "or you will burn it. If you are just starting out, try cooking " +
                "shrimp or anchovies - they are easy and do not require " +
                "much skill.",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "I am fine, thanks.")
        chatNpc(happy, "Come back anytime! Fresh food daily!")
    }
}
