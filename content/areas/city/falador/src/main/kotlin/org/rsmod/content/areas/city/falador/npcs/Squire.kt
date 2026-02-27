package org.rsmod.content.areas.city.falador.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.falador.configs.FaladorNpcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class SquireScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(FaladorNpcs.squire) { squireDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.squireDialogue(npc: Npc) =
        startDialogue(npc) {
            chatPlayer(quiz, "Hello there.")
            chatNpc(sad, "Oh, hello. I'm in such trouble...")
            val choice =
                choice3(
                    "What's wrong?",
                    1,
                    "Can you tell me about the White Knights?",
                    2,
                    "Goodbye.",
                    3,
                )
            when (choice) {
                1 -> {
                    chatPlayer(quiz, "What's wrong?")
                    chatNpc(sad, "I've lost Sir Vyvin's ceremonial sword!")
                    chatNpc(sad, "I was polishing it and I accidentally dropped it down a drain!")
                    chatNpc(sad, "Sir Vyvin will have my head if he finds out!")
                    val helpChoice =
                        choice2(
                            "I could try to get it back for you.",
                            4,
                            "That's too bad. Good luck with that!",
                            5,
                        )
                    when (helpChoice) {
                        4 -> {
                            chatPlayer(happy, "I could try to get it back for you.")
                            chatNpc(happy, "Would you really? That would be wonderful!")
                            chatNpc(neutral, "The drain leads to the sewers beneath Falador.")
                            chatNpc(neutral, "But be careful - there are monsters down there.")
                            // TODO: Start The Knight's Sword quest
                            chatNpc(happy, "Please find my sword!")
                        }
                        5 -> {
                            chatPlayer(neutral, "That's too bad. Good luck with that!")
                            chatNpc(sad, "*sniff* I'm doomed...")
                        }
                    }
                }
                2 -> {
                    chatPlayer(quiz, "Can you tell me about the White Knights?")
                    chatNpc(happy, "The White Knights are the protectors of Falador!")
                    chatNpc(happy, "We serve under Sir Amik Varze, our leader.")
                    chatNpc(neutral, "We wear different colored plumes to show our rank.")
                    chatNpc(neutral, "I'm just a squire, so I don't have a plume yet.")
                }
                3 -> chatPlayer(neutral, "Goodbye.")
            }
        }
}
