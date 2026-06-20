package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class CookingTutor : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.cooking_tutor) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatPlayer(happy, "Hello there.")
            chatNpc(happy, "Greetings, ${player.displayName}. I am the Cooking Tutor. I can teach you how to prepare delicious food.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Tell me about cooking.",
            1,
            "What should I cook first?",
            2,
            "Thanks, that is enough.",
            3,
        )
        when (choice) {
            1 -> skillExplanation()
            2 -> skillAdvice()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.skillExplanation() {
        chatPlayer(quiz, "Tell me about cooking.")
        chatNpc(
            neutral,
            "Cooking is how you prepare food to heal yourself during and after " +
                "combat. Raw meat, fish, and other ingredients can be cooked on a " +
                "fire or a range.",
        )
        chatNpc(
            happy,
            "Be careful though \u2014 if you cook at too low a level, you " +
                "might burn your food! Higher levels mean less burnt food.",
        )
        chatNpc(neutral, "Is there anything else you would like to know?")
        val choice = choice2("What should I cook first?", 1, "Thanks, that is enough.", 2)
        if (choice == 1) skillAdvice() else goodbye()
    }

    private suspend fun Dialogue.skillAdvice() {
        chatPlayer(quiz, "What should I cook first?")
        chatNpc(
            neutral,
            "Start with shrimp or anchovies from the fishing tutor\u2019s spot " +
                "south of town. They are easy to catch and quick to cook.",
        )
        chatNpc(
            happy,
            "You can use the cooking range in Lumbridge Castle kitchen or " +
                "light a fire anywhere with logs and a tinderbox.",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Thanks, that is enough.")
        chatNpc(happy, "Feel free to come back if you need more advice!")
    }
}
