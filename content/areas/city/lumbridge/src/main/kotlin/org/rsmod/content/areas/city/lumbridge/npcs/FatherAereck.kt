package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class FatherAereck : PluginScript() {
    override fun ScriptContext.startup() {
        // Op1 removed: RestlessGhost quest script owns father_aereck Op1 across all quest stages
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
