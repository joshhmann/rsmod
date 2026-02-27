package org.rsmod.content.areas.city.alkharid.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Osman - The Emir's spymaster in Al Kharid Palace.
 * - Starts Prince Ali Rescue quest (stage 0 -> 1)
 * - Provides spy-themed dialogue
 * - Gives directions to Leela in Draynor
 * - Information about Prince Ali's kidnapping
 */
class Osman : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(al_kharid_npcs.osman) { startOsmanDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startOsmanDialogue(npc: Npc) =
        startDialogue(npc) {
            val questStage = getQuestStage(QuestList.prince_ali_rescue)
            when (questStage) {
                0 -> osmanStartQuestDialogue()
                1 -> osmanInProgressDialogue()
                2 -> osmanRescuedDialogue()
                else -> osmanPostQuestDialogue()
            }
        }

    // Stage 0: Start quest dialogue
    private suspend fun Dialogue.osmanStartQuestDialogue() {
        chatNpc(shifty, "Psst! Come closer. I have need of someone with your... talents.")
        chatPlayer(quiz, "Who are you?")
        chatNpc(neutral, "I am Osman, the Emir's spymaster. I serve Al Kharid from the shadows.")
        chatNpc(
            sad,
            "Prince Ali, the Emir's son and heir, has been kidnapped by the bandit Lady Keli.",
        )
        chatNpc(sad, "He is being held for ransom somewhere in Al Kharid. The Emir is distraught.")

        val choice =
            choice3(
                "I can help rescue Prince Ali.",
                1,
                "Tell me more about this kidnapping.",
                2,
                "I want nothing to do with spies.",
                3,
            )

        when (choice) {
            1 -> {
                chatPlayer(happy, "I can help rescue Prince Ali.")
                chatNpc(
                    happy,
                    "Excellent! A brave soul. You will be well rewarded for your service.",
                )
                chatNpc(
                    quiz,
                    "I have an agent in Draynor Village named Leela. She has been gathering intelligence.",
                )
                chatNpc(
                    neutral,
                    "Go to Draynor and speak with her. She will tell you what we know of the prison.",
                )
                chatNpc(
                    shifty,
                    "Be discreet. Lady Keli has spies everywhere. Trust no one but Leela.",
                )
                access.setQuestStage(QuestList.prince_ali_rescue, 1)
            }
            2 -> {
                chatPlayer(quiz, "Tell me more about this kidnapping.")
                chatNpc(
                    sad,
                    "Lady Keli is a dangerous bandit with a reputation for capturing important people.",
                )
                chatNpc(
                    neutral,
                    "She has Prince Ali held in a secure prison somewhere in Al Kharid.",
                )
                chatNpc(neutral, "We believe she plans to demand a hefty ransom from the Emir.")
                chatNpc(
                    quiz,
                    "If you wish to help, speak with Leela in Draynor Village. She has the details.",
                )

                val helpChoice =
                    choice2("I'll help rescue Prince Ali.", 1, "I need to think about this.", 2)

                when (helpChoice) {
                    1 -> {
                        chatPlayer(happy, "I'll help rescue Prince Ali.")
                        chatNpc(
                            happy,
                            "Splendid! Go to Draynor Village and find Leela. She awaits your arrival.",
                        )
                        chatNpc(
                            shifty,
                            "Tell her 'the falcon flies at midnight' - she will know you are my contact.",
                        )
                        access.setQuestStage(QuestList.prince_ali_rescue, 1)
                    }
                    2 -> chatNpc(neutral, "Very well. Time is of the essence, however.")
                }
            }
            3 -> {
                chatPlayer(angry, "I want nothing to do with spies.")
                chatNpc(
                    neutral,
                    "A pity. But I understand. Not everyone has the stomach for such work.",
                )
                chatNpc(
                    neutral,
                    "If you change your mind, you know where to find me. The Prince's life hangs in the balance.",
                )
            }
        }
    }

    // Stage 1: Quest in progress
    private suspend fun Dialogue.osmanInProgressDialogue() {
        chatNpc(quiz, "Have you spoken with Leela in Draynor Village?")
        chatPlayer(neutral, "Not yet. I'm on my way.")
        chatNpc(neutral, "Make haste. Every day Prince Ali remains captive, the danger grows.")
        chatNpc(
            quiz,
            "Leela has gathered valuable intelligence on Lady Keli's prison. She will guide you.",
        )
        chatNpc(shifty, "Remember: trust no one else with this mission. The walls have ears.")
    }

    // Stage 2: Prince Ali rescued
    private suspend fun Dialogue.osmanRescuedDialogue() {
        chatNpc(happy, "Word has reached me that Prince Ali has been freed!")
        chatPlayer(happy, "Yes! I rescued him from Lady Keli's prison.")
        chatNpc(happy, "Magnificent work! You have proven yourself a true friend to Al Kharid.")
        chatNpc(
            happy,
            "Speak with Chancellor Hassan in the palace. He will see that you are rewarded.",
        )
        chatNpc(shifty, "And if you ever need work of a... discreet nature... come see me again.")
    }

    // Stage 3+: Post-quest dialogue
    private suspend fun Dialogue.osmanPostQuestDialogue() {
        chatNpc(happy, "Ah, the hero of Al Kharid returns!")
        val choice = choice3("How is Prince Ali?", 1, "Any more spy work?", 2, "Goodbye.", 3)

        when (choice) {
            1 -> {
                chatPlayer(quiz, "How is Prince Ali?")
                chatNpc(
                    happy,
                    "He is well, thanks to you. The Emir has doubled his guard detail, much to the Prince's annoyance.",
                )
                chatNpc(
                    happy,
                    "Prince Ali speaks highly of your bravery and cunning. High praise indeed.",
                )
            }
            2 -> {
                chatPlayer(quiz, "Any more spy work?")
                chatNpc(
                    shifty,
                    "Always. But nothing that concerns you... for now. Check back with me later.",
                )
                chatNpc(
                    happy,
                    "A good spymaster always has need of capable agents. You have proven your worth.",
                )
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }
}
