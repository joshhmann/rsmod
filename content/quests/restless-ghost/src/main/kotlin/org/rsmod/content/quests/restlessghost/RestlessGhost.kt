package org.rsmod.content.quests.restlessghost

import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLocU
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.restlessghost.configs.restless_ghost_locs
import org.rsmod.content.quests.restlessghost.configs.restless_ghost_npcs
import org.rsmod.content.quests.restlessghost.configs.restless_ghost_objs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * The Restless Ghost quest implementation.
 *
 * Quest Flow:
 * 1. Talk to Father Aereck in Lumbridge church to start (Stage 0 -> 1)
 * 2. Talk to Father Urhney in Lumbridge Swamp for ghostspeak amulet
 * 3. Talk to Restless Ghost in cemetery with amulet equipped
 * 4. Search the skull altar in Wizards' Tower basement to get skull
 * 5. Use skull on coffin to complete quest
 */
class RestlessGhost : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(restless_ghost_npcs.father_aereck) { startFatherAereckDialogue(it.npc) }
        onOpNpc1(restless_ghost_npcs.father_urhney) { startFatherUrhneyDialogue(it.npc) }
        onOpNpc1(restless_ghost_npcs.restless_ghost) { startGhostDialogue(it.npc) }
        onOpLoc1(restless_ghost_locs.skull_altar) { searchSkullAltar() }
        onOpLocU(restless_ghost_locs.coffin, restless_ghost_objs.ghostskull) { useSkullOnCoffin() }
    }

    // ==================== Father Aereck Dialogue ====================

    private suspend fun ProtectedAccess.startFatherAereckDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.restless_ghost)) {
                0 -> aereckStartQuestDialogue()
                1 -> aereckInProgressDialogue()
                else -> aereckFinishedDialogue()
            }
        }

    private suspend fun Dialogue.aereckStartQuestDialogue() {
        chatNpc(sad, "Oh, hello there. I'm having some troubles with the church.")
        val option = choice2("What's wrong?", 1, "I'm fine, thanks.", 2)
        when (option) {
            1 -> {
                chatPlayer(quiz, "What's wrong?")
                aereckExplainQuest()
            }
            2 -> chatPlayer(neutral, "I'm fine, thanks.")
        }
    }

    private suspend fun Dialogue.aereckExplainQuest() {
        chatNpc(
            sad,
            "You may have heard that there is a ghost haunting the graveyard. " +
                "It's becoming quite a problem, scaring people away from the church.",
        )
        chatNpc(
            sad,
            "If you could help me lay the ghost to rest, I'd be most grateful. " +
                "Father Urhney in the swamp south of here might be able to help.",
        )
        chatNpc(
            sad,
            "He's something of an expert on ghosts and spirits. " +
                "He can probably tell you what needs to be done.",
        )
        val option = choice2("I'll see what I can do.", 1, "Sorry, I can't help.", 2)
        when (option) {
            1 -> {
                chatPlayer(happy, "I'll see what I can do.")
                chatNpc(happy, "Excellent! Speak to Father Urhney in the swamp.")
                access.setQuestStage(QuestList.restless_ghost, 1)
            }
            2 -> chatPlayer(neutral, "Sorry, I can't help.")
        }
    }

    private suspend fun Dialogue.aereckInProgressDialogue() {
        when {
            restless_ghost_objs.ghostskull in player.inv -> {
                chatNpc(quiz, "Have you managed to lay the ghost to rest yet?")
                chatPlayer(neutral, "I'm working on it. I need to put the skull in the coffin.")
                chatNpc(happy, "Good luck!")
            }
            else -> {
                chatNpc(quiz, "Have you managed to lay the ghost to rest yet?")
                chatPlayer(neutral, "Not yet. I'm still working on it.")
                chatNpc(neutral, "Speak to Father Urhney in the swamp. He'll know what to do.")
            }
        }
    }

    private suspend fun Dialogue.aereckFinishedDialogue() {
        chatNpc(happy, "Thank you again for helping with the ghost. The church is peaceful again!")
    }

    // ==================== Father Urhney Dialogue ====================

    private suspend fun ProtectedAccess.startFatherUrhneyDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.restless_ghost)) {
                0 -> urhneyBeforeQuestDialogue()
                1 -> urhneyInProgressDialogue()
                else -> urhneyFinishedDialogue()
            }
        }

    private suspend fun Dialogue.urhneyBeforeQuestDialogue() {
        chatNpc(quiz, "Go away! I'm meditating!")
        chatPlayer(confused, "...")
    }

    private suspend fun Dialogue.urhneyInProgressDialogue() {
        chatNpc(quiz, "Go away! I'm meditating!")
        val option =
            choice3(
                "Father Aereck sent me to talk to you.",
                1,
                "I've lost the Ghostspeak amulet.",
                2,
                "I'll leave you alone.",
                3,
            )
        when (option) {
            1 -> {
                chatPlayer(neutral, "Father Aereck sent me to talk to you.")
                chatNpc(quiz, "Oh? Well, what's the problem?")
                chatPlayer(quiz, "There's a ghost haunting the Lumbridge graveyard.")
                urhneyGiveAmulet()
            }
            2 -> {
                chatPlayer(sad, "I've lost the Ghostspeak amulet.")
                if (restless_ghost_objs.amulet_of_ghostspeak in player.inv) {
                    chatNpc(quiz, "No you haven't. You're wearing it!")
                } else {
                    chatNpc(neutral, "Careless of you. Here, have another.")
                    giveGhostspeakAmulet()
                }
            }
            3 -> chatPlayer(neutral, "I'll leave you alone.")
        }
    }

    private suspend fun Dialogue.urhneyGiveAmulet() {
        chatNpc(
            neutral,
            "Oh dear, oh dear. A ghost haunting the graveyard, you say? " +
                "That's not good at all.",
        )
        chatNpc(
            neutral,
            "The poor thing must be confused. You'll need this Ghostspeak amulet " +
                "to communicate with it.",
        )
        giveGhostspeakAmulet()
    }

    private suspend fun Dialogue.giveGhostspeakAmulet() {
        if (!player.inv.hasFreeSpace()) {
            chatNpc(sad, "You don't have room for the amulet. Clear some inventory space.")
            return
        }

        val added = player.invAdd(player.inv, restless_ghost_objs.amulet_of_ghostspeak, 1).success
        if (added) {
            chatNpc(happy, "There you go. Wear this amulet and speak to the ghost.")
            chatNpc(
                neutral,
                "The ghost is probably trapped here because it's missing something. " +
                    "Find out what and return it to the coffin.",
            )
        } else {
            chatNpc(sad, "I couldn't give you the amulet. Make sure you have space.")
        }
    }

    private suspend fun Dialogue.urhneyFinishedDialogue() {
        chatNpc(happy, "Ah, hello again. How is the ghost doing?")
    }

    // ==================== Restless Ghost Dialogue ====================

    private suspend fun ProtectedAccess.startGhostDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.restless_ghost)) {
                0 -> ghostBeforeQuestDialogue()
                1 -> ghostInProgressDialogue(npc)
                else -> ghostFinishedDialogue()
            }
        }

    private suspend fun Dialogue.ghostBeforeQuestDialogue() {
        chatNpc(sad, "Wooo wooo wooooo")
        chatPlayer(confused, "I can't understand the ghost...")
    }

    private suspend fun Dialogue.ghostInProgressDialogue(npc: Npc) {
        val hasAmulet = restless_ghost_objs.amulet_of_ghostspeak in player.inv

        if (!hasAmulet) {
            chatNpc(sad, "Wooo wooo wooooo")
            chatPlayer(confused, "I still can't understand it. I need the Ghostspeak amulet.")
            return
        }

        // With amulet, ghost can talk
        if (restless_ghost_objs.ghostskull in player.inv) {
            chatNpc(happy, "Oooo... the skull... please put it in my coffin...")
            chatPlayer(quiz, "I'll put it back for you.")
        } else {
            chatNpc(sad, "Oooo... I'm so sad... my body is missing a piece...")
            chatPlayer(quiz, "What do you mean?")
            chatNpc(
                sad,
                "Someone took my skull from my coffin... it's somewhere in the " +
                    "Wizards' Tower...",
            )
            chatNpc(sad, "Please find it and return it to my coffin so I can rest in peace...")
            chatPlayer(neutral, "I'll try to find it.")
        }
    }

    private suspend fun Dialogue.ghostFinishedDialogue() {
        chatNpc(happy, "Thank you... now I can rest in peace...")
    }

    // ==================== Skull Altar Interaction ====================

    private suspend fun ProtectedAccess.searchSkullAltar() {
        when (getQuestStage(QuestList.restless_ghost)) {
            0 -> mes("You search the altar but find nothing of interest.")
            1 -> {
                if (restless_ghost_objs.ghostskull in player.inv) {
                    mes("You've already taken the skull.")
                } else {
                    mes("You search the altar...")
                    mes("You find a skull among the dusty bones.")
                    val added = invAdd(player.inv, restless_ghost_objs.ghostskull, 1).success
                    if (added) {
                        mes("You take the skull.")
                    } else {
                        mes("You don't have room for the skull.")
                    }
                }
            }
            else -> mes("You search the altar but find nothing of interest.")
        }
    }

    // ==================== Coffin Completion ====================

    private suspend fun ProtectedAccess.useSkullOnCoffin() {
        when (getQuestStage(QuestList.restless_ghost)) {
            0 -> mes("The coffin seems empty. Perhaps there's something inside?")
            1 -> {
                if (restless_ghost_objs.ghostskull !in player.inv) {
                    mes("You need the ghost's skull to place in the coffin.")
                    return
                }

                mes("You place the skull back in the coffin.")

                val removed = invDel(player.inv, restless_ghost_objs.ghostskull, 1).success
                if (!removed) {
                    mes("You don't seem to have the skull anymore.")
                    return
                }

                completeQuest()
            }
            else -> mes("The ghost has been laid to rest.")
        }
    }

    private suspend fun ProtectedAccess.completeQuest() {
        setQuestStage(QuestList.restless_ghost, 2)
        showCompletionScroll(
            quest = QuestList.restless_ghost,
            rewards = listOf("1 Quest Point", "1,162 Prayer XP"),
            itemModel = restless_ghost_objs.amulet_of_ghostspeak,
            questPoints = 1,
        )
    }
}
