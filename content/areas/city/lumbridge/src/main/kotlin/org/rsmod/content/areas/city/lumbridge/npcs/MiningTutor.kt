package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class MiningTutor : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.mining_tutor) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatPlayer(happy, "Hello there.")
            chatNpc(happy, "Greetings, ${player.displayName}. I am the Mining Tutor. I can teach you how to extract ores from the earth.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Tell me about mining.",
            1,
            "What should I mine first?",
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
        chatPlayer(quiz, "Tell me about mining.")
        chatNpc(
            neutral,
            "Mining is the skill of extracting ores from rocks found throughout " +
                "Gielinor. You will need a pickaxe to start mining.",
        )
        chatNpc(
            happy,
            "Different rocks yield different ores. Copper and tin are great " +
                "for beginners \u2014 smelt them together to make bronze bars!",
        )
        chatNpc(neutral, "Is there anything else you would like to know?")
        val choice = choice2("What should I mine first?", 1, "Thanks, that is enough.", 2)
        if (choice == 1) skillAdvice() else goodbye()
    }

    private suspend fun Dialogue.skillAdvice() {
        chatPlayer(quiz, "What should I mine first?")
        chatNpc(
            neutral,
            "Start with copper and tin rocks, which you can find in the swamp " +
                "south of Lumbridge. Smelt the ores into bars using the furnace.",
        )
        chatNpc(
            happy,
            "Higher Mining levels let you mine valuable ores like iron, coal, " +
                "silver, and gold. Keep practicing!",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Thanks, that is enough.")
        chatNpc(happy, "Feel free to come back if you need more advice!")
    }
}
