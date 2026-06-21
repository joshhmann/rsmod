package org.rsmod.content.areas.city.karamja.npcs
import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.karamja.configs.karamja_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext
class KaramjaManScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(karamja_npcs.karamja_man) { startDialogue(it.npc) }
    }
    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) { karamjaManDialogue() }
    }
    private suspend fun Dialogue.karamjaManDialogue() {
        chatNpc(happy, "Greetings! Welcome to the tropical paradise of Karamja!")
        val choice = choice3("Tell me about Karamja.", 1, "Where can I find things around here?", 2, "Goodbye.", 3)
        when (choice) {
            1 -> {
                chatPlayer(quiz, "Tell me about Karamja.")
                chatNpc(happy, "Karamja is the largest tropical island in Gielinor!")
                chatNpc(neutral, "We have beautiful beaches, dense jungles, and the famous Brimhaven.")
            }
            2 -> {
                chatPlayer(quiz, "Where can I find things around here?")
                chatNpc(happy, "You're at Musa Point! Luthas runs the banana plantation north of here.")
                chatNpc(neutral, "The customs office is just by the docks.")
            }
            3 -> { chatPlayer(neutral, "Goodbye."); chatNpc(happy, "Come back anytime!") }
        }
    }
}
