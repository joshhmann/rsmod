package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Perdu : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.perdu) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Hello there, adventurer! I am Perdu, the lost property merchant.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "What do you sell?",
            1,
            "Can you hold onto items for me?",
            2,
            "I am fine, thanks.",
            3,
        )
        when (choice) {
            1 -> whatISell()
            2 -> lostProperty()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.whatISell() {
        chatPlayer(quiz, "What do you sell?")
        chatNpc(
            happy,
            "I sell various bits and bobs that adventurers have lost over " +
                "the years. Buckets, ropes, tinderboxes, you name it! " +
                "If you have lost something essential, I might have a spare.",
        )
    }

    private suspend fun Dialogue.lostProperty() {
        chatPlayer(quiz, "Can you hold onto items for me?")
        chatNpc(
            happy,
            "I can certainly help you recover lost items. Come speak to me " +
                "if you ever lose something important - I might be able " +
                "to help you get it back!",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "I am fine, thanks.")
        chatNpc(happy, "Alright then. Come back if you need anything!")
    }
}
