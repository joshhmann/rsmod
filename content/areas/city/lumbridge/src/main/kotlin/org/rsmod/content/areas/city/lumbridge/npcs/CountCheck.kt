package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class CountCheck : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.count_check) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Hello there! I am Count Check. I can see you are an adventurer!")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice2(
            "Tell me what my total level is.",
            1,
            "Goodbye.",
            2,
        )
        when (choice) {
            1 -> checkTotalLevel()
            2 -> goodbye()
        }
    }

    private suspend fun Dialogue.checkTotalLevel() {
        chatPlayer(quiz, "Tell me what my total level is.")
        chatNpc(
            happy,
            "Your total level is impressive! Keep training your skills " +
                "and you will become a true master of all trades.",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "Take care, adventurer! Keep those levels rising!")
    }
}
