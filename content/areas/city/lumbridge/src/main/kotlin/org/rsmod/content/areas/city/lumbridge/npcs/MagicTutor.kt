package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class MagicTutor : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.magic_tutor) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatPlayer(happy, "Hello there.")
            chatNpc(happy, "Greetings, ${player.displayName}. I am the Magic Tutor. I can teach you about the magical arts.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Tell me about magic.",
            1,
            "How do I start learning magic?",
            2,
            "Thanks, that is enough.",
            3,
        )
        when (choice) {
            1 -> skillExplanation()
            2 -> skillAdvice()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.skillExplanation() {
        chatPlayer(quiz, "Tell me about magic.")
        chatNpc(
            neutral,
            "Magic is a skill that allows you to cast powerful spells. You can " +
                "use it for combat, teleportation, and all sorts of utility.",
        )
        chatNpc(
            happy,
            "Each spell requires specific runes to cast. You can buy runes " +
                "from rune shops or craft them yourself with the Runecraft skill.",
        )
        chatNpc(neutral, "Is there anything else you would like to know?")
        val choice = choice2("How do I start learning magic?", 1, "Thanks, that is enough.", 2)
        if (choice == 1) skillAdvice() else goodbye()
    }

    private suspend fun Dialogue.skillAdvice() {
        chatPlayer(quiz, "How do I start learning magic?")
        chatNpc(
            neutral,
            "Open your spellbook to see what spells you can cast. For combat " +
                "magic, the Wind Strike spell is a good starting point.",
        )
        chatNpc(
            happy,
            "You will need air and mind runes to cast it. Train your Magic " +
                "level to unlock more powerful spells. Good luck!",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Thanks, that is enough.")
        chatNpc(happy, "Feel free to come back if you need more advice!")
    }
}
