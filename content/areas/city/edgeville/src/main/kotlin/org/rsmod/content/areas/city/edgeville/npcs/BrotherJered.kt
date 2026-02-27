package org.rsmod.content.areas.city.edgeville.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.edgeville.configs.edgeville_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class BrotherJered @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(edgeville_npcs.brother_jered) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) { brotherJeredDialogue() }
    }

    private suspend fun Dialogue.brotherJeredDialogue() {
        chatNpc(happy, "Welcome to the Monastery, friend. May Saradomin bless your travels.")

        val choice = choice2("Can you bless me?", 1, "Tell me about the monastery.", 2)

        when (choice) {
            1 -> blessPlayer()
            2 -> aboutMonastery()
        }
    }

    private suspend fun Dialogue.blessPlayer() {
        chatPlayer(neutral, "Can you bless me?")
        chatNpc(happy, "Of course! May Saradomin's light guide your path and protect your soul.")
        chatNpc(
            happy,
            "Visit our altar if you wish to strengthen your prayer. We offer a " +
                "sanctuary for all who seek divine protection.",
        )
    }

    private suspend fun Dialogue.aboutMonastery() {
        chatPlayer(neutral, "Tell me about the monastery.")
        chatNpc(
            happy,
            "This is the Monastery of Saradomin, home to the Order of " + "the Holy Grail Knights.",
        )
        chatNpc(
            happy,
            "We maintain the sacred altar and train our brothers in the ways " +
                "of prayer and devotion.",
        )
        chatNpc(
            neutral,
            "Many adventurers come here to strengthen their connection to Saradomin. " +
                "The altar can restore your prayer points when you use it.",
        )
    }
}
