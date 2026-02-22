package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class GillieGroats : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.gillie_groats) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Hello, I'm Gillie. Are you here to learn about milking cows?")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice2("How do I milk a cow?", 1, "Goodbye.", 2)
        when (choice) {
            1 -> {
                chatPlayer(quiz, "How do I milk a cow?")
                chatNpc(
                    neutral,
                    "It's easy! Just take an empty bucket and use it on one of the cows in the field.",
                )
                chatNpc(
                    happy,
                    "Fresh milk is used in many cooking recipes, like the one the castle Cook is working on!",
                )
            }
            2 -> {
                chatPlayer(neutral, "Goodbye.")
            }
        }
    }
}
