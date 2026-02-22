package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class DukeHoracio : PluginScript() {
    override fun ScriptContext.startup() {
        // Quest-specific handlers (e.g. Rune Mysteries) own Duke interactions.
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Greetings, welcome to my castle.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice2("Have you any quests for me?", 1, "Goodbye.", 2)
        when (choice) {
            1 -> {
                chatPlayer(quiz, "Have you any quests for me?")
                chatNpc(neutral, "I'm sorry, I don't have anything for you to do right now.")
                chatNpc(
                    neutral,
                    "However, I believe the Cook is having some trouble in the kitchen.",
                )
            }
            2 -> {
                chatPlayer(neutral, "Goodbye.")
            }
        }
    }
}
