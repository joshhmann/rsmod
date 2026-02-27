package org.rsmod.content.areas.city.alkharid.npcs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.giveQuestReward
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Chancellor Hassan - The chancellor of Al Kharid Palace.
 * - Contact for Prince Ali Rescue quest completion
 * - Gives quest rewards (700 coins, 3 Quest Points)
 * - Provides information about the Emir and Prince Ali
 */
class Hassan : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(al_kharid_npcs.hassan) { startHassanDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startHassanDialogue(npc: Npc) =
        startDialogue(npc) {
            val questStage = getQuestStage(QuestList.prince_ali_rescue)
            when (questStage) {
                0 -> hassanPreQuestDialogue()
                1 -> hassanInProgressDialogue()
                2 -> hassanCompletionDialogue()
                else -> hassanPostQuestDialogue()
            }
        }

    // Stage 0: Before starting Prince Ali Rescue
    private suspend fun Dialogue.hassanPreQuestDialogue() {
        chatNpc(quiz, "Greetings, adventurer. I am Chancellor Hassan, advisor to the Emir.")
        val choice =
            choice3(
                "What can you tell me about the Emir?",
                1,
                "Who is Prince Ali?",
                2,
                "Goodbye.",
                3,
            )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "What can you tell me about the Emir?")
                chatNpc(
                    sad,
                    "The Emir is... not well. He has been deeply troubled ever since his son was kidnapped.",
                )
                chatNpc(
                    sad,
                    "He rarely leaves the palace these days. The kidnapping has taken a heavy toll on him.",
                )
            }
            2 -> {
                chatPlayer(quiz, "Who is Prince Ali?")
                chatNpc(
                    sad,
                    "Prince Ali is the Emir's only son and heir to the throne of Al Kharid.",
                )
                chatNpc(
                    sad,
                    "He was kidnapped by the bandit Lady Keli and is being held for ransom.",
                )
                chatNpc(
                    quiz,
                    "If you wish to help rescue him, you should speak to Leela in Draynor Village.",
                )
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }

    // Stage 1: Quest in progress
    private suspend fun Dialogue.hassanInProgressDialogue() {
        chatNpc(quiz, "Have you any news of Prince Ali?")
        chatPlayer(sad, "I'm still working on rescuing him.")
        chatNpc(neutral, "Please hurry. The Emir grows more worried with each passing day.")
        chatNpc(neutral, "Speak to Leela in Draynor Village if you need guidance on the rescue.")
    }

    // Stage 2: Ready for completion (Prince Ali has been rescued)
    private suspend fun Dialogue.hassanCompletionDialogue() {
        chatNpc(happy, "You have returned! Is it true? Has Prince Ali been rescued?")
        chatPlayer(happy, "Yes! Prince Ali is safe and free!")
        chatNpc(
            happy,
            "This is wonderful news! The Emir will be overjoyed! You have our eternal gratitude.",
        )
        chatNpc(
            happy,
            "Please accept these 700 coins and our thanks. You shall always be welcome in Al Kharid.",
        )

        // Give quest rewards
        access.giveQuestReward(QuestList.prince_ali_rescue)

        // Complete the quest
        access.setQuestStage(QuestList.prince_ali_rescue, 3)

        // Show completion scroll
        access.showCompletionScroll(
            quest = QuestList.prince_ali_rescue,
            rewards =
                listOf("3 Quest Points", "700 Coins", "Free passage through Al Kharid toll gate"),
            itemModel = objs.coins,
        )
    }

    // Stage 3+: Post-quest dialogue
    private suspend fun Dialogue.hassanPostQuestDialogue() {
        chatNpc(happy, "Greetings, friend of Al Kharid!")
        val choice = choice3("How is Prince Ali?", 1, "How is the Emir?", 2, "Goodbye.", 3)
        when (choice) {
            1 -> {
                chatPlayer(quiz, "How is Prince Ali?")
                chatNpc(
                    happy,
                    "He is well, thanks to you! He has returned to his duties and is preparing to one day rule Al Kharid.",
                )
                chatNpc(happy, "He speaks highly of your bravery.")
            }
            2 -> {
                chatPlayer(quiz, "How is the Emir?")
                chatNpc(
                    happy,
                    "The Emir is like a new man! With his son returned, he has regained his spirit.",
                )
                chatNpc(
                    happy,
                    "He wishes to thank you personally, but royal duties keep him busy. Know that you have his gratitude.",
                )
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }
}
