package org.rsmod.content.areas.city.portsarim.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.portsarim.configs.portsarim_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class RedbeardFrank : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(portsarim_npcs.redbeard_frank) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Arr! What brings a young adventurer like you to these shores?")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Are you a pirate?",
            1,
            "Do you know any treasure locations?",
            2,
            "Nothing, I am just passing through.",
            3,
        )
        when (choice) {
            1 -> pirateQuestion()
            2 -> treasureTalk()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.pirateQuestion() {
        chatPlayer(quiz, "Are you a pirate?")
        chatNpc(
            shifty,
            "A pirate? Me? Never! I am a legitimate sailor and trader, " +
                "I will have you know! Been sailing these seas for forty " +
                "years, transporting cargo and... other goods.",
        )
        chatNpc(
            happy,
            "Though I may have been known to bend the rules once or twice, " +
                "back in my younger days!",
        )
    }

    private suspend fun Dialogue.treasureTalk() {
        chatPlayer(quiz, "Do you know any treasure locations?")
        chatNpc(
            shifty,
            "Treasure, you say? I might know a thing or two about buried " +
                "loot. But a sailor never gives up his secrets for free! " +
                "Bring me some good grog and we will talk business.",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Nothing, I am just passing through.")
        chatNpc(happy, "Fair winds to ye, then! Give my regards to the tavern keeper!")
    }
}
