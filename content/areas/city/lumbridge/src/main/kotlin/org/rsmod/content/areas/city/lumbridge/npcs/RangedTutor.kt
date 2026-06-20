package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class RangedTutor : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.ranged_tutor) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatPlayer(happy, "Hello there.")
            chatNpc(happy, "Greetings, ${player.displayName}. I am the Ranged Combat Tutor. I can teach you about the art of ranged combat.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Tell me about ranged combat.",
            1,
            "What equipment do I need?",
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
        chatPlayer(quiz, "Tell me about ranged combat.")
        chatNpc(
            neutral,
            "Ranged combat allows you to attack from a distance using bows, " +
                "crossbows, and other projectile weapons. It uses the Ranged skill, " +
                "which determines your accuracy and the weapons you can wield.",
        )
        chatNpc(
            happy,
            "Ranged is very useful because you can attack enemies before they " +
                "reach you. Just make sure you have plenty of arrows or bolts!",
        )
        chatNpc(neutral, "Is there anything else you would like to know?")
        val choice = choice2("What equipment do I need?", 1, "Thanks, that is enough.", 2)
        if (choice == 1) skillAdvice() else goodbye()
    }

    private suspend fun Dialogue.skillAdvice() {
        chatPlayer(quiz, "What equipment do I need?")
        chatNpc(
            neutral,
            "You will need a bow and some arrows. You can buy a shortbow and " +
                "bronze arrows from Lowe\u2019s Archery Store in Varrock.",
        )
        chatNpc(
            happy,
            "As your Ranged level increases, you will be able to use better " +
                "bows and more accurate ammunition. Happy hunting!",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Thanks, that is enough.")
        chatNpc(happy, "Feel free to come back if you need more advice!")
    }
}
