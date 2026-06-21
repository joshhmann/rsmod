package org.rsmod.content.areas.city.varrock.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class FatherLawrence : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.father_lawrence) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Blessings of Saradomin be upon you, my child.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Tell me about the Church of Saradomin.",
            1,
            "Do you know Romeo and Juliet?",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> aboutChurch()
            2 -> romeoAndJuliet()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.aboutChurch() {
        chatPlayer(quiz, "Tell me about the Church of Saradomin.")
        chatNpc(
            neutral,
            "The Church of Saradomin is dedicated to the worship of the god of " +
                "wisdom and order. We provide guidance and spiritual support " +
                "to all who seek it.",
        )
        chatNpc(happy, "All are welcome here, regardless of their past or their deeds.")
    }

    private suspend fun Dialogue.romeoAndJuliet() {
        chatPlayer(quiz, "Do you know Romeo and Juliet?")
        chatNpc(
            neutral,
            "Ah, yes. Those two young lovers from feuding families. " +
                "It is a delicate situation. I have been trying to help them, " +
                "but the hatred between the Montagues and Capulets " +
                "runs deep.",
        )
        chatNpc(
            happy,
            "With Saradomin's guidance, I hope to find a way to unite them.",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "May Saradomin watch over you on your journey.")
    }
}
