package org.rsmod.content.areas.city.varrock.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Zaff : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.zaff) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) { zaffChat() }

    private suspend fun Dialogue.zaffChat() {
        chatNpc(happy, "Welcome to Zaff's Superior Staffs! The best staves in Varrock!")
        val choice = choice3(
            "What staves do you sell?",
            1,
            "Tell me about yourself.",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "What staves do you sell?")
                chatNpc(happy, "I sell all types of staves! Battlestaves, mystic staves, " +
                    "and everything in between!")
                chatNpc(neutral, "Right-click me and select Trade to see my collection.")
            }
            2 -> {
                chatPlayer(quiz, "Tell me about yourself.")
                chatNpc(neutral, "I'm Zaff, owner of the finest staff shop in all of Varrock! " +
                    "I've been in business for years.")
                chatNpc(happy, "Adventurers come from miles around for my quality staves!")
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }
}
