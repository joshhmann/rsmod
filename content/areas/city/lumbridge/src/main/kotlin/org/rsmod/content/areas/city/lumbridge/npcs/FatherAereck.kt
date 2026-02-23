package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class FatherAereck : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.father_aereck) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Welcome to the church of Saradomin. How can I help you today?")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice2("Who is Saradomin?", 1, "Goodbye.", 2)
        when (choice) {
            1 -> {
                chatPlayer(quiz, "Who is Saradomin?")
                chatNpc(
                    happy,
                    "Saradomin is the god of wisdom and order. He watches over all of us in Misthalin.",
                )
            }
            2 -> {
                chatPlayer(neutral, "Goodbye.")
            }
        }
    }
}
