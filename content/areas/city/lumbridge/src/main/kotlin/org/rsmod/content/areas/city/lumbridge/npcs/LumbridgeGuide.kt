package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class LumbridgeGuide : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.guide) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Greetings, adventurer! Welcome to Lumbridge!")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Tell me about this area.",
            1,
            "Where should I start?",
            2,
            "Thanks, I will be on my way.",
            3,
        )
        when (choice) {
            1 -> areaInfo()
            2 -> getStarted()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.areaInfo() {
        chatPlayer(quiz, "Tell me about this area.")
        chatNpc(
            neutral,
            "Lumbridge is a peaceful town in the kingdom of Misthalin. " +
                "It is home to Lumbridge Castle, ruled by the wise Duke Horacio. " +
                "There are many training areas nearby, including chicken farms, " +
                "cow pastures, and goblins just across the river.",
        )
        chatNpc(
            happy,
            "You can find various tutors here who will teach you the basics " +
                "of many skills. There are shops to buy supplies, and " +
                "adventurers come from all over to begin their journeys here.",
        )
    }

    private suspend fun Dialogue.getStarted() {
        chatPlayer(quiz, "Where should I start?")
        chatNpc(
            happy,
            "I recommend speaking to the tutors in the castle courtyard. " +
                "They can teach you the basics of combat and gathering skills. " +
                "When you feel ready, explore the surrounding areas " +
                "to fight monsters and complete quests!",
        )
        chatNpc(
            neutral,
            "Also, speak to the cook in the castle kitchen - he always needs " +
                "help with something! And do not forget to check Bob's Axes " +
                "if you need tools.",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(happy, "Thanks, I will be on my way.")
        chatNpc(happy, "Good luck on your adventures! You are always welcome here.")
    }
}
