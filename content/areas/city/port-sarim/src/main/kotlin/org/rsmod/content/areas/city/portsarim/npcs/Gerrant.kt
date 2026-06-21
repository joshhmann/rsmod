package org.rsmod.content.areas.city.portsarim.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.portsarim.configs.portsarim_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Gerrant : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(portsarim_npcs.gerrant) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) { gerrantChat() }

    private suspend fun Dialogue.gerrantChat() {
        chatNpc(happy, "Welcome to Gerrant's Fishy Business! Best fishing supplies in Port Sarim!")
        val choice = choice3(
            "What do you sell?",
            1,
            "Any fishing tips?",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "What do you sell?")
                chatNpc(happy, "I sell fishing rods, nets, bait, and all the fishing supplies you need!")
                chatNpc(neutral, "Right-click me and select Trade to see my wares.")
            }
            2 -> {
                chatPlayer(quiz, "Any fishing tips?")
                chatNpc(neutral, "Start with a net in the Lumbridge River to catch shrimp and anchovies. " +
                    "Then move on to trout and salmon in the river north of Lumbridge!")
                chatNpc(happy, "When you're more experienced, you can fish lobsters and swordfish " +
                    "right here in Port Sarim or on Karamja!")
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }
}
