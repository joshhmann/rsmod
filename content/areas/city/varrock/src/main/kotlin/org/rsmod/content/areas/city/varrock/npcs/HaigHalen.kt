package org.rsmod.content.areas.city.varrock.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Haig Halen - Varrock Museum Curator Located in Varrock Museum, provides information about the
 * museum's history and exhibits. Part of the Kudos system for museum completion.
 */
class HaigHalen @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.curator) { haigHalenDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.haigHalenDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Welcome to the Varrock Museum, traveller!")
            chatNpc(
                happy,
                "I am Haig Halen, curator of this fine establishment. Are you interested in learning about our exhibits?",
            )

            val choice =
                choice3(
                    "Tell me about the museum's history.",
                    1,
                    "What exhibits do you have?",
                    2,
                    "I'm just browsing, thanks.",
                    3,
                )

            when (choice) {
                1 -> {
                    chatPlayer(quiz, "Tell me about the museum's history.")
                    chatNpc(
                        happy,
                        "The Varrock Museum was founded many years ago to preserve the rich history of Gielinor.",
                    )
                    chatNpc(
                        happy,
                        "We have specimens and artifacts from all across the land, from the lowest depths of the earth to the highest peaks.",
                    )
                    chatNpc(
                        happy,
                        "Our paleontology section contains fossils of ancient creatures that once roamed these lands.",
                    )
                    chatNpc(
                        happy,
                        "If you're interested in natural history, you should definitely explore our dig site exhibit!",
                    )
                }
                2 -> {
                    chatPlayer(quiz, "What exhibits do you have?")
                    chatNpc(
                        happy,
                        "We have several fascinating exhibits! On the ground floor, you'll find our natural history section.",
                    )
                    chatNpc(
                        happy,
                        "Upstairs is our art gallery, featuring works from talented artists across Gielinor.",
                    )
                    chatNpc(
                        happy,
                        "And if you go down to the basement, you'll find our archaeological dig site - quite popular with adventurers!",
                    )
                    chatNpc(
                        happy,
                        "We also have a section dedicated to the history of Varrock and the surrounding areas.",
                    )
                }
                3 -> {
                    chatPlayer(neutral, "I'm just browsing, thanks.")
                    chatNpc(
                        happy,
                        "Very well! Feel free to explore at your leisure. If you have any questions, don't hesitate to ask.",
                    )
                }
            }
        }
}
