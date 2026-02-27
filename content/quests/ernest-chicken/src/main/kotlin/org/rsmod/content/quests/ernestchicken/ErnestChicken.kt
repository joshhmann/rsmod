package org.rsmod.content.quests.ernestchicken

import jakarta.inject.Inject
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.ernestchicken.configs.ernest_chicken_npcs
import org.rsmod.content.quests.ernestchicken.configs.ernest_chicken_objs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Ernest the Chicken quest implementation.
 *
 * Quest stages: 0 - Not started 1 - Started, need to find Ernest in Draynor Manor 2 - Talked to
 * Professor Oddenstein, need to collect 3 machine parts 3 - Quest complete
 */
class ErnestChicken @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        // NPC dialogues
        onOpNpc1(ernest_chicken_npcs.veronica) { veronicaDialogue(it.npc) }
        onOpNpc1(ernest_chicken_npcs.professor_oddenstein) { professorDialogue(it.npc) }
        onOpNpc1(ernest_chicken_npcs.ernest_chicken) { ernestChickenDialogue(it.npc) }
        onOpNpc1(ernest_chicken_npcs.ernest) { ernestDialogue(it.npc) }

        // Item crafting
        onOpHeldU(ernest_chicken_objs.fish_food, ernest_chicken_objs.poison) {
            makePoisonedFishFood()
        }
    }

    // ---- Veronica Dialogue ----
    private suspend fun ProtectedAccess.veronicaDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.ernest_the_chicken)) {
                0 -> veronicaStartQuest()
                1 -> veronicaInProgress()
                2 -> veronicaInProgressCollecting()
                else -> veronicaFinished()
            }
        }

    private suspend fun Dialogue.veronicaStartQuest() {
        chatNpc(sad, "What am I to do?")
        val option = choice2("What's wrong?", 1, "You don't look very happy. What's wrong?", 2)
        when (option) {
            1,
            2 -> {
                chatPlayer(
                    quiz,
                    if (option == 1) "What's wrong?" else "You don't look very happy. What's wrong?",
                )
                chatNpc(
                    sad,
                    "It's my fiancé Ernest. He went into Draynor Manor over an hour ago to ask for directions. He hasn't come back yet!",
                )
                val option2 =
                    choice2("I'll go and look for him.", 1, "Well, good luck finding him.", 2)
                when (option2) {
                    1 -> {
                        chatPlayer(happy, "I'll go and look for him.")
                        chatNpc(happy, "Oh thank you! Please bring him back safe!")
                        access.setQuestStage(QuestList.ernest_the_chicken, 1)
                    }
                    2 -> chatPlayer(neutral, "Well, good luck finding him.")
                }
            }
        }
    }

    private suspend fun Dialogue.veronicaInProgress() {
        chatNpc(quiz, "Have you found Ernest yet?")
        chatPlayer(neutral, "Not yet. I'm still looking.")
        chatNpc(sad, "Please hurry! He could be in danger!")
    }

    private suspend fun Dialogue.veronicaInProgressCollecting() {
        chatNpc(quiz, "Have you found Ernest yet?")
        chatPlayer(
            neutral,
            "I found him! He's been turned into a chicken by a mad scientist. I'm collecting parts to fix the machine.",
        )
        chatNpc(sad, "A chicken?! Oh my! Please hurry and save him!")
    }

    private suspend fun Dialogue.veronicaFinished() {
        chatNpc(happy, "Thank you so much for saving Ernest!")
        chatPlayer(happy, "No problem. Glad I could help.")
    }

    // ---- Professor Oddenstein Dialogue ----
    private suspend fun ProtectedAccess.professorDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.ernest_the_chicken)) {
                0,
                1 -> professorMeet()
                2 -> professorCollecting()
                else -> professorAfterQuest()
            }
        }

    private suspend fun Dialogue.professorMeet() {
        chatNpc(happy, "Ah, a visitor! How wonderful! Come to see my amazing pouletmorph machine?")
        val option = choice2("I'm looking for Ernest.", 1, "What does this machine do?", 2)
        when (option) {
            1 -> {
                chatPlayer(
                    quiz,
                    "I'm looking for Ernest. His fiancé Veronica is worried about him.",
                )
                chatNpc(
                    neutral,
                    "Ernest? Oh yes, he was here earlier. He helped me with my experiment.",
                )
                chatNpc(
                    neutral,
                    "Unfortunately, there was a slight accident with the pouletmorph machine.",
                )
                chatNpc(sad, "He's been turned into a chicken!")
                chatPlayer(angry, "A chicken?! Can you turn him back?")
                chatNpc(quiz, "Well yes, but I need three parts that were stolen by poltergeists.")
                chatPlayer(quiz, "What do you need?")
                explainPartsNeeded()
            }
            2 -> {
                chatPlayer(quiz, "What does this machine do?")
                chatNpc(happy, "It can transform humans into chickens and back again!")
                chatNpc(neutral, "A man named Ernest helped me test it earlier...")
                chatNpc(sad, "But there was an accident. He's stuck as a chicken!")
                chatPlayer(quiz, "I'm looking for Ernest. Can you help me find him?")
                chatNpc(
                    quiz,
                    "I can turn him back, but I need three parts that were stolen by poltergeists.",
                )
                explainPartsNeeded()
            }
        }
    }

    private suspend fun Dialogue.explainPartsNeeded() {
        chatNpc(neutral, "I need a pressure gauge, a rubber tube, and an oil can.")
        chatNpc(
            neutral,
            "They're hidden somewhere in the manor. If you bring them to me, I can fix the machine.",
        )
        val option = choice2("I'll find them.", 1, "That sounds too hard.", 2)
        when (option) {
            1 -> {
                chatPlayer(happy, "I'll find them.")
                chatNpc(happy, "Excellent! The parts should be somewhere in the manor.")
                if (access.getQuestStage(QuestList.ernest_the_chicken) < 2) {
                    access.setQuestStage(QuestList.ernest_the_chicken, 2)
                }
            }
            2 -> chatPlayer(neutral, "That sounds too hard.")
        }
    }

    private suspend fun Dialogue.professorCollecting() {
        val hasPressureGauge = ernest_chicken_objs.pressure_gauge in player.inv
        val hasRubberTube = ernest_chicken_objs.rubber_tube in player.inv
        val hasOilCan = ernest_chicken_objs.oil_can in player.inv

        when {
            hasPressureGauge && hasRubberTube && hasOilCan -> {
                chatPlayer(happy, "I've found all the parts!")
                chatNpc(happy, "Excellent! Let me fix the machine right away!")
                completeQuest()
            }
            hasPressureGauge || hasRubberTube || hasOilCan -> {
                chatNpc(quiz, "How is your search going?")
                val itemsStillNeeded = buildList {
                    if (!hasPressureGauge) add("pressure gauge")
                    if (!hasRubberTube) add("rubber tube")
                    if (!hasOilCan) add("oil can")
                }
                chatPlayer(neutral, "I still need to find: ${itemsStillNeeded.joinToString(", ")}.")
                chatNpc(neutral, "Keep looking! The parts should be somewhere in the manor.")
            }
            else -> {
                chatNpc(quiz, "How is your search going?")
                chatPlayer(neutral, "I haven't found any parts yet.")
                chatNpc(neutral, "Keep looking! Try searching everywhere in the manor.")
            }
        }
    }

    private suspend fun Dialogue.professorAfterQuest() {
        chatNpc(happy, "Thanks to you, my pouletmorph machine is working perfectly!")
        chatPlayer(neutral, "Just... don't turn any more people into chickens.")
        chatNpc(quiz, "No promises!")
    }

    private suspend fun Dialogue.completeQuest() {
        // Remove the parts
        val removedPressure =
            player.invDel(player.inv, ernest_chicken_objs.pressure_gauge, 1).success
        val removedTube = player.invDel(player.inv, ernest_chicken_objs.rubber_tube, 1).success
        val removedOil = player.invDel(player.inv, ernest_chicken_objs.oil_can, 1).success

        if (!removedPressure || !removedTube || !removedOil) {
            chatNpc(sad, "Hmm, it seems you don't have all the parts after all.")
            if (removedPressure) player.invAddOrDrop(objRepo, ernest_chicken_objs.pressure_gauge, 1)
            if (removedTube) player.invAddOrDrop(objRepo, ernest_chicken_objs.rubber_tube, 1)
            if (removedOil) player.invAddOrDrop(objRepo, ernest_chicken_objs.oil_can, 1)
            return
        }

        access.setQuestStage(QuestList.ernest_the_chicken, 3)
        access.showCompletionScroll(
            quest = QuestList.ernest_the_chicken,
            rewards = listOf("4 Quest Points", "300 Coins"),
            itemModel = ernest_chicken_objs.pressure_gauge,
            questPoints = 4,
        )

        // Give coins as reward
        val gaveCoins = player.invAdd(player.inv, org.rsmod.api.config.refs.objs.coins, 300).success
        if (!gaveCoins) {
            player.invAddOrDrop(objRepo, org.rsmod.api.config.refs.objs.coins, 300)
        }
    }

    // ---- Ernest the Chicken Dialogue ----
    private suspend fun ProtectedAccess.ernestChickenDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(quiz, "Cluck cluck cluck! Cluuuuck!")
            chatPlayer(quiz, "You must be Ernest. Professor Oddenstein turned you into a chicken.")
            chatNpc(sad, "Cluck...")
        }

    // ---- Ernest (Human) Dialogue ----
    private suspend fun ProtectedAccess.ernestDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Thank you for saving me! I can't believe I was a chicken!")
            chatPlayer(happy, "You should thank Veronica. She sent me to find you.")
            chatNpc(happy, "I need to go see her right away!")
        }

    // ---- Item Crafting ----
    private suspend fun ProtectedAccess.makePoisonedFishFood() {
        if (
            !inv.contains(ernest_chicken_objs.fish_food) ||
                !inv.contains(ernest_chicken_objs.poison)
        ) {
            mes("You need both fish food and poison to do this.")
            return
        }
        invDel(inv, ernest_chicken_objs.fish_food, 1)
        invDel(inv, ernest_chicken_objs.poison, 1)
        val added = invAdd(inv, ernest_chicken_objs.poisoned_fish_food, 1).success
        if (!added) {
            player.invAddOrDrop(objRepo, ernest_chicken_objs.poisoned_fish_food, 1)
        }
        mes("You mix the poison with the fish food.")
    }
}
