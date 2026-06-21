package org.rsmod.content.areas.city.falador.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.falador.configs.falador_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Wyson : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(falador_npcs.wyson) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Hello there! I am Wyson, the gardener of Falador Park.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Do you have any woad leaves?",
            1,
            "Tell me about the park.",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> woadLeaves()
            2 -> aboutPark()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.woadLeaves() {
        chatPlayer(quiz, "Do you have any woad leaves?")
        chatNpc(
            happy,
            "Woad leaves? Indeed I do! I grow them in the park. " +
                "They are used to make blue dye, you know. " +
                "I can sell you some if you like - 20 coins per leaf.",
        )
    }

    private suspend fun Dialogue.aboutPark() {
        chatPlayer(quiz, "Tell me about the park.")
        chatNpc(
            happy,
            "Falador Park is the pride of the city! Beautiful flowers, " +
                "well-manicured hedges, and a peaceful atmosphere. " +
                "It is the perfect place to relax after a long day of " +
                "adventuring.",
        )
        chatNpc(neutral, "I take great pride in keeping it looking its best.")
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "Goodbye! Come back if you need any woad leaves!")
    }
}
