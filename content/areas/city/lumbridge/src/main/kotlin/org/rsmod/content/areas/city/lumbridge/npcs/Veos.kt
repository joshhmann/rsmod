package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Veos : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.veos) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Ah, hello there! The name is Veos. I am a collector of rare and interesting items.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice2(
            "What kind of items do you collect?",
            1,
            "I will be on my way.",
            2,
        )
        when (choice) {
            1 -> explainCollection()
            2 -> goodbye()
        }
    }

    private suspend fun Dialogue.explainCollection() {
        chatPlayer(quiz, "What kind of items do you collect?")
        chatNpc(
            happy,
            "Oh, all sorts of things! Treasure maps, ancient artifacts, " +
                "curious trinkets... I have travelled all over the world " +
                "in search of rare finds.",
        )
        chatNpc(
            shifty,
            "If you ever come across anything interesting, do let me know. " +
                "I might be able to make it worth your while, if you catch my drift.",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "I will be on my way.")
        chatNpc(happy, "Take care! And keep your eyes peeled for treasure!")
    }
}
