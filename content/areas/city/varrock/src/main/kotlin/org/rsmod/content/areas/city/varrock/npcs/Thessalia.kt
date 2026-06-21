package org.rsmod.content.areas.city.varrock.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Thessalia : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.thessalia) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) { thessaliaChat() }

    private suspend fun Dialogue.thessaliaChat() {
        chatNpc(happy, "Welcome to Thessalia's Fine Clothes! The finest fashion in all of Varrock!")
        val choice = choice3(
            "What do you sell?",
            1,
            "Tell me about fashion.",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "What do you sell?")
                chatNpc(happy, "I sell the finest clothes in all of Varrock! " +
                    "From elegant skirts to sturdy tunics!")
                chatNpc(neutral, "Right-click me and select Trade to see my full collection.")
            }
            2 -> {
                chatPlayer(quiz, "Tell me about fashion.")
                chatNpc(happy, "Fashion is about expressing yourself! " +
                    "In Varrock, everyone wants to look their best.")
                chatNpc(neutral, "I import fabrics from all over Gielinor to make unique designs. " +
                    "No two pieces are quite the same!")
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }
}
