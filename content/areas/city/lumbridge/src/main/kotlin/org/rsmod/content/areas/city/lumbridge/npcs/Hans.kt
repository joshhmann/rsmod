package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Hans : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.hans) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Hello. What are you doing here?")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "I am looking for whoever is in charge of this place.",
            1,
            "I have been here for a while, can you tell me how long?",
            2,
            "I am just looking around.",
            3,
        )
        when (choice) {
            1 -> inCharge()
            2 -> timePlayed()
            3 -> justLooking()
        }
    }

    private suspend fun Dialogue.inCharge() {
        chatPlayer(quiz, "I am looking for whoever is in charge of this place.")
        chatNpc(
            neutral,
            "The Duke of Lumbridge is in charge of this castle. You can find him " +
                "upstairs in the throne room.",
        )
    }

    private suspend fun Dialogue.timePlayed() {
        chatPlayer(quiz, "I have been here for a while, can you tell me how long?")
        chatNpc(happy, "You have spent quite a while in this world since you arrived. Keep up the good work!")
    }

    private suspend fun Dialogue.justLooking() {
        chatPlayer(happy, "I am just looking around.")
        chatNpc(happy, "Well, make yourself at home. There is plenty to see in Lumbridge Castle!")
    }
}
