package org.rsmod.content.areas.city.falador.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.falador.configs.falador_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class SirAmikVarze : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(falador_npcs.sir_amik_varze) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Welcome to the White Knights' Castle. I am Sir Amik Varze, leader of the White Knights.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Tell me about the White Knights.",
            1,
            "Do you have any work for an adventurer?",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> aboutKnights()
            2 -> workInquiry()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.aboutKnights() {
        chatPlayer(quiz, "Tell me about the White Knights.")
        chatNpc(
            neutral,
            "The White Knights of Falador are the noble protectors of Asgarnia. " +
                "We are sworn to uphold justice, defend the innocent, and serve " +
                "the people with honour and integrity.",
        )
        chatNpc(
            happy,
            "Our order has stood for centuries, and we are always looking " +
                "for capable individuals to join our cause.",
        )
    }

    private suspend fun Dialogue.workInquiry() {
        chatPlayer(quiz, "Do you have any work for an adventurer?")
        chatNpc(
            neutral,
            "Not at this time, but keep your wits about you. The kingdom " +
                "of Asgarnia always has need of capable adventurers. " +
                "Speak to Sir Vyvin if you are looking for more specific tasks.",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "Farewell, adventurer. May honour guide your path.")
    }
}
