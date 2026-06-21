package org.rsmod.content.areas.city.varrock.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Horvik : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.horvik) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) { horvikChat() }

    private suspend fun Dialogue.horvikChat() {
        chatNpc(happy, "Welcome! I'm Horvik, Varrock's finest armourer!")
        val choice = choice3(
            "What armour do you sell?",
            1,
            "Any advice for a warrior?",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "What armour do you sell?")
                chatNpc(happy, "I sell a full range of armour! From basic bronze to sturdy rune!")
                chatNpc(neutral, "Right-click me and select Trade to see what I have in stock.")
            }
            2 -> {
                chatPlayer(quiz, "Any advice for a warrior?")
                chatNpc(neutral, "Always wear the best armour you can afford! " +
                    "And remember to train your Defence skill to wear better gear.")
                chatNpc(happy, "The goblins north of Varrock are great for beginners. " +
                    "Stay away from the Wilderness until you're well-prepared!")
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }
}
