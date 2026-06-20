package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class FishingTutor : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.fishing_tutor) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatPlayer(happy, "Hello there.")
            chatNpc(happy, "Greetings, ${player.displayName}. I am the Fishing Tutor. I can teach you the fine art of fishing.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Tell me about fishing.",
            1,
            "Where should I start fishing?",
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
        chatPlayer(quiz, "Tell me about fishing.")
        chatNpc(
            neutral,
            "Fishing lets you catch fish from water sources all over the world. " +
                "You will need a fishing rod and bait, or a net, depending on what " +
                "you want to catch.",
        )
        chatNpc(
            happy,
            "Shrimp and anchovies can be caught with a small fishing net, " +
                "while trout and salmon require a fly fishing rod and feathers.",
        )
        chatNpc(neutral, "Is there anything else you would like to know?")
        val choice = choice2("Where should I start fishing?", 1, "Thanks, that is enough.", 2)
        if (choice == 1) skillAdvice() else goodbye()
    }

    private suspend fun Dialogue.skillAdvice() {
        chatPlayer(quiz, "Where should I start fishing?")
        chatNpc(
            neutral,
            "There is a good fishing spot just south of Lumbridge. You can " +
                "buy a fishing rod and bait from the general store.",
        )
        chatNpc(
            happy,
            "Cook your catch on a fire or range to turn it into food that " +
                "heals you in combat. Tight lines!",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Thanks, that is enough.")
        chatNpc(happy, "Feel free to come back if you need more advice!")
    }
}
