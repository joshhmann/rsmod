package org.rsmod.content.areas.city.varrock.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Lowe : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.lowe) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) { loweChat() }

    private suspend fun Dialogue.loweChat() {
        chatNpc(happy, "Hello there! Welcome to Lowe's Archery Emporium!")
        val choice = choice3(
            "What do you sell?",
            1,
            "Any tips for an archer?",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "What do you sell?")
                chatNpc(happy, "I sell all kinds of ranged equipment! Bows, arrows, and more!")
                chatNpc(neutral, "Right-click me and select Trade to browse my wares.")
            }
            2 -> {
                chatPlayer(quiz, "Any tips for an archer?")
                chatNpc(neutral, "Always make sure you have enough arrows before heading out. " +
                    "And train on creatures that match your skill level.")
                chatNpc(happy, "Chickens and cows are great for beginners, " +
                    "then move up to goblins and zombies!")
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }
}
