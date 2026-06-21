package org.rsmod.content.areas.city.portsarim.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.portsarim.configs.portsarim_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Betty : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(portsarim_npcs.betty) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) { bettyChat() }

    private suspend fun Dialogue.bettyChat() {
        chatNpc(happy, "Welcome to Betty's Magic Emporium! Can I interest you in some magical supplies?")
        val choice = choice3(
            "What do you sell?",
            1,
            "Tell me about magic.",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "What do you sell?")
                chatNpc(happy, "I sell runes, wands, and all sorts of magical paraphernalia!")
                chatNpc(neutral, "Right-click me and select Trade to browse my wares.")
            }
            2 -> {
                chatPlayer(quiz, "Tell me about magic.")
                chatNpc(neutral, "Magic is a powerful skill. You'll need runes to cast spells. " +
                    "Different spells require different runes.")
                chatNpc(happy, "Speak to the magic tutors in Lumbridge if you want to learn more. " +
                    "And stock up on runes before heading out!")
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }
}
