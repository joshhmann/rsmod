package org.rsmod.content.areas.city.draynor.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.draynor.configs.draynor_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class DraynorTownCrier : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(draynor_npcs.town_crier) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) { townCrierChat() }

    private suspend fun Dialogue.townCrierChat() {
        chatNpc(happy, "Hear ye, hear ye! Welcome to Draynor Village!")
        val choice = choice3(
            "What's the news?",
            1,
            "Tell me about Draynor.",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "What's the news?")
                chatNpc(neutral, "I've heard rumours of strange happenings in the manor to the north. " +
                    "The old count has become quite reclusive lately.")
                chatNpc(worried, "And some say they've seen a mysterious stranger lurking near " +
                    "the Wizard's Tower to the south!")
            }
            2 -> {
                chatPlayer(quiz, "Tell me about Draynor.")
                chatNpc(neutral, "Draynor Village is a peaceful settlement between Lumbridge and " +
                    "Port Sarim. We have a bank, a general store, and a manor to the north.")
                chatNpc(happy, "Aggie the witch lives here, and Ned the rope maker. " +
                    "We also have the famous Draynor Manor, though most folk avoid it...")
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }
}
