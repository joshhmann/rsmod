package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class MeleeTutor : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.melee_tutor) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatPlayer(happy, "Hello there.")
            chatNpc(happy, "Greetings, ${player.displayName}. I am the Melee Combat Tutor. I can teach you about the art of melee combat.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Tell me about melee combat.",
            1,
            "What weapons should I use?",
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
        chatPlayer(quiz, "Tell me about melee combat.")
        chatNpc(
            neutral,
            "Melee is fighting up close with hand-to-hand weapons such as " +
                "swords, battleaxes, maces, and scimitars. It relies on three skills: " +
                "Attack, Strength, and Defence.",
        )
        chatNpc(
            happy,
            "Attack determines your accuracy and what weapons you can use. " +
                "Strength determines how hard you hit. Defence helps you avoid getting hit.",
        )
        chatNpc(neutral, "Is there anything else you would like to know?")
        val choice = choice2("What weapons should I use?", 1, "Thanks, that is enough.", 2)
        if (choice == 1) skillAdvice() else goodbye()
    }

    private suspend fun Dialogue.skillAdvice() {
        chatPlayer(quiz, "What weapons should I use?")
        chatNpc(
            neutral,
            "Start with bronze weapons \u2014 you can buy them from Bob\u2019s " +
                "Axes in Lumbridge. As you train your combat stats, you will unlock " +
                "iron, steel, and much stronger weapons.",
        )
        chatNpc(
            happy,
            "Remember to always bring food to heal during battle. " +
                "Good luck on your adventures!",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Thanks, that is enough.")
        chatNpc(happy, "Feel free to come back if you need more advice!")
    }
}
