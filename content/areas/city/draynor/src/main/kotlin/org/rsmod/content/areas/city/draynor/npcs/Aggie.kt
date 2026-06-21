package org.rsmod.content.areas.city.draynor.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.draynor.configs.draynor_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Aggie : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(draynor_npcs.aggie) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Hello, dearie. Would you like some dye?")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "What dyes can you make?",
            1,
            "Can you clean this item for me?",
            2,
            "Not right now, thanks.",
            3,
        )
        when (choice) {
            1 -> dyeOptions()
            2 -> cleaning()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.dyeOptions() {
        chatPlayer(quiz, "What dyes can you make?")
        chatNpc(
            happy,
            "Oh, I can make all sorts of lovely colours! Red dye from " +
                "berries, yellow dye from onions, and blue dye from " +
                "woad leaves. Bring me the right ingredients and I will " +
                "mix them up for you!",
        )
        chatNpc(
            neutral,
            "You will need 3 of the right ingredient and 5 coins for " +
                "each dye I make.",
        )
    }

    private suspend fun Dialogue.cleaning() {
        chatPlayer(quiz, "Can you clean this item for me?")
        chatNpc(
            happy,
            "Of course, dearie. If you have something that needs cleaning, " +
                "I can wash it in my special solution. Just bring it to me " +
                "and I will see what I can do!",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Not right now, thanks.")
        chatNpc(happy, "Come back if you ever need any dyes or cleaning, dearie!")
    }
}
