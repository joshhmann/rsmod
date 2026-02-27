package org.rsmod.content.areas.city.portsarim.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.portsarim.configs.portsarim_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class CaptainBarnaby @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(portsarim_npcs.captain_barnaby) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) { barnabyDialogue(npc) }
    }

    private suspend fun Dialogue.barnabyDialogue(npc: Npc) {
        chatNpc(happy, "Hello there, adventurer! Would you like to sail to somewhere?")
        val choice =
            choice3(
                "Can you take me somewhere?",
                1,
                "Tell me about charter ships.",
                2,
                "No thanks.",
                3,
            )
        when (choice) {
            1 -> charterTravel(npc)
            2 -> aboutCharterShips()
            3 -> noThanks()
        }
    }

    private suspend fun Dialogue.charterTravel(npc: Npc) {
        chatPlayer(quiz, "Can you take me somewhere?")
        chatNpc(sad, "Sorry, I can't take you anywhere right now.")
    }

    private suspend fun Dialogue.aboutCharterShips() {
        chatPlayer(quiz, "Tell me about charter ships.")
        chatNpc(
            happy,
            "Charter ships can take you to various ports around " +
                "Gielinor! I can sail you to many destinations for a fee.",
        )
        chatNpc(happy, "Just talk to me when you're ready to travel.")
    }

    private suspend fun Dialogue.noThanks() {
        chatPlayer(neutral, "No thanks.")
        chatNpc(happy, "Safe travels, adventurer!")
    }
}
