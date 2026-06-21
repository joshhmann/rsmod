package org.rsmod.content.areas.city.edgeville.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.edgeville.configs.edgeville_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Peksa : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(edgeville_npcs.peksa) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Welcome to my helmet shop! Best helmets in all of Asgarnia!")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "What helmets do you sell?",
            1,
            "Why are helmets important?",
            2,
            "Just browsing, thanks.",
            3,
        )
        when (choice) {
            1 -> helmetSelection()
            2 -> whyHelmets()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.helmetSelection() {
        chatPlayer(quiz, "What helmets do you sell?")
        chatNpc(
            happy,
            "I sell a fine selection of helmets! Bronze, iron, steel, " +
                "and even mithril for the more experienced adventurer. " +
                "Take a look at my wares - I am sure you will find " +
                "something that fits!",
        )
    }

    private suspend fun Dialogue.whyHelmets() {
        chatPlayer(quiz, "Why are helmets important?")
        chatNpc(
            neutral,
            "A good helmet can save your life! It protects your head " +
                "from crushing blows and arrows. Never go into battle " +
                "without one, I always say!",
        )
        chatNpc(happy, "And of course, you will look much more dashing with one on!")
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Just browsing, thanks.")
        chatNpc(happy, "Take your time! Let me know if you need anything.")
    }
}
