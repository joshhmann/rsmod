package org.rsmod.content.areas.city.varrock.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Juliet : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.juliet) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(sad, "Oh, Romeo, Romeo! Wherefore art thou, Romeo?")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Romeo sent me.",
            1,
            "Are you alright?",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> romeoMessage()
            2 -> areYouAlright()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.romeoMessage() {
        chatPlayer(neutral, "Romeo sent me.")
        chatNpc(
            happy,
            "He did? Oh, how wonderful! Please, tell him that I still love him " +
                "and that I will find a way for us to be together!",
        )
        chatNpc(
            sad,
            "My father keeps me locked away in this tower. It is so unfair! " +
                "If only there were a way to escape...",
        )
    }

    private suspend fun Dialogue.areYouAlright() {
        chatPlayer(quiz, "Are you alright?")
        chatNpc(
            sad,
            "No, I am not alright! My family forbids me from seeing my one " +
                "true love. It is a tragedy that would make even the " +
                "hardest heart weep!",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(sad, "Goodbye, kind traveller. If you see Romeo, tell him I still love him!")
    }
}
