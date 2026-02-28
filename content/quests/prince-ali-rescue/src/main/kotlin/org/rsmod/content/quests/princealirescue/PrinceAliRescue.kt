package org.rsmod.content.quests.princealirescue

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.princealirescue.configs.prince_ali_rescue_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Prince Ali Rescue quest implementation for RSMod v2.
 *
 * Rescue Prince Ali from Lady Keli's prison in Al Kharid by creating a disguise (wig, skin paste,
 * yellow dye) and sneaking him out.
 *
 * Reward: 700 coins + 1 Quest Point
 */
class PrinceAliRescue : PluginScript() {
    override fun ScriptContext.startup() {
        // Leela in Draynor Village (quest start)
        onOpNpc1(prince_ali_rescue_npcs.leela) { startLeelaDialogue(it.npc) }

        // Lady Keli in Al Kharid prison
        onOpNpc1(prince_ali_rescue_npcs.lady_keli) { startKeliDialogue(it.npc) }

        // Prince Ali in prison cell
        onOpNpc1(prince_ali_rescue_npcs.prince_ali) { startPrinceAliDialogue(it.npc) }

        // Hassan in Al Kharid palace (completion)
        // onOpNpc1(prince_ali_rescue_npcs.hassan) { startHassanDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startLeelaDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.prince_ali_rescue)) {
                0 -> leelaStartQuestDialogue()
                1 -> leelaInProgressDialogue()
                2 -> leelaFinishedDialogue()
                else -> leelaPostQuestDialogue()
            }
        }

    private suspend fun ProtectedAccess.startKeliDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.prince_ali_rescue)) {
                1 -> keliPrisonDialogue()
                else -> keliDefaultDialogue()
            }
        }

    private suspend fun ProtectedAccess.startPrinceAliDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.prince_ali_rescue)) {
                1 -> princeAliRescueDialogue()
                else -> princeAliPostRescueDialogue()
            }
        }

    private suspend fun ProtectedAccess.startHassanDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.prince_ali_rescue)) {
                2 -> hassanCompletionDialogue()
                else -> hassanDefaultDialogue()
            }
        }

    // Stage 0: Start quest
    private suspend fun Dialogue.leelaStartQuestDialogue() {
        chatNpc(quiz, "Greetings. I need your help with a matter of some urgency.")
        chatNpc(sad, "Prince Ali of Al Kharid has been kidnapped!")
        chatNpc(sad, "He's being held prisoner by Lady Keli somewhere in Al Kharid.")

        val option = choice2("I'll help rescue him!", 1, "Sorry, I'm too busy.", 2)
        when (option) {
            1 -> {
                chatPlayer(happy, "I'll help rescue him!")
                chatNpc(happy, "Excellent! You need to create a disguise.")
                chatNpc(quiz, "You'll need a wig made from wool, some skin paste, and yellow dye.")
                chatNpc(quiz, "Once you have the disguise, find Lady Keli in Al Kharid.")
                access.setQuestStage(QuestList.prince_ali_rescue, 1)
            }
            2 -> chatNpc(sad, "Very well. Please return if you change your mind.")
        }
    }

    private suspend fun Dialogue.leelaInProgressDialogue() {
        chatNpc(quiz, "Have you rescued Prince Ali yet?")
        chatPlayer(sad, "Not yet. I'm still working on it.")
        chatNpc(
            neutral,
            "Remember, you need to make a disguise using wool, skin paste, and yellow dye.",
        )
        chatNpc(neutral, "Lady Keli is holding Prince Ali in Al Kharid.")
    }

    private suspend fun Dialogue.leelaFinishedDialogue() {
        chatNpc(happy, "You did it! Prince Ali is free!")
        chatNpc(happy, "You should speak to Hassan in the Al Kharid palace for your reward.")
    }

    private suspend fun Dialogue.leelaPostQuestDialogue() {
        chatNpc(happy, "Thanks again for rescuing Prince Ali!")
    }

    // Lady Keli dialogue
    private suspend fun Dialogue.keliPrisonDialogue() {
        chatNpc(angry, "What do you want? This is a restricted area!")
        // TODO: Check if player has disguise (wig, skin paste, yellow dye)
        // TODO: If disguised, tie up Keli with rope and rescue Prince Ali
        // TODO: If not disguised, Keli refuses entry
    }

    private suspend fun Dialogue.keliDefaultDialogue() {
        chatNpc(angry, "Leave this area at once!")
    }

    // Prince Ali dialogue
    private suspend fun Dialogue.princeAliRescueDialogue() {
        chatNpc(sad, "I'm trapped in here! Can you help me escape?")
        // TODO: If player has disguise, allow rescue
        // TODO: Set quest stage to 2 (rescued)
    }

    private suspend fun Dialogue.princeAliPostRescueDialogue() {
        chatNpc(happy, "Thank you for rescuing me! Please speak to Hassan for your reward.")
    }

    // Hassan completion dialogue
    private suspend fun Dialogue.hassanCompletionDialogue() {
        chatNpc(happy, "You rescued my son! I am forever in your debt!")
        chatNpc(happy, "Please accept these 700 coins as a token of my gratitude.")
        // TODO: Give 700 coins
        // TODO: Complete quest (1 QP)
        access.setQuestStage(QuestList.prince_ali_rescue, 3)
    }

    private suspend fun Dialogue.hassanDefaultDialogue() {
        chatNpc(quiz, "Greetings, adventurer.")
    }
}
