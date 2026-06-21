package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Doomsayer : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.doomsayer) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(sad, "Woe betide you, adventurer! The world is full of peril!")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "What dangers should I look out for?",
            1,
            "Can you stop reminding me of certain warnings?",
            2,
            "Goodbye, doomsayer.",
            3,
        )
        when (choice) {
            1 -> listDangers()
            2 -> toggleWarnings()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.listDangers() {
        chatPlayer(quiz, "What dangers should I look out for?")
        chatNpc(
            sad,
            "Oh, where to begin! The Wilderness to the north is filled with " +
                "deadly beasts and player killers! Deep dungeons hide terrible " +
                "monsters! The roads are beset by highwaymen!",
        )
        chatNpc(
            sad,
            "And that is not all! Poisonous spiders lurk in the shadows, " +
                "aggressive beasts guard valuable resources, and the very " +
                "ground itself can be hazardous in some areas!",
        )
    }

    private suspend fun Dialogue.toggleWarnings() {
        chatPlayer(quiz, "Can you stop reminding me of certain warnings?")
        chatNpc(
            neutral,
            "I can adjust some of my warnings for you. Speak to me again " +
                "when you wish to change which dangers I remind you about.",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye, doomsayer.")
        chatNpc(sad, "Farewell... and be careful out there!")
    }
}
