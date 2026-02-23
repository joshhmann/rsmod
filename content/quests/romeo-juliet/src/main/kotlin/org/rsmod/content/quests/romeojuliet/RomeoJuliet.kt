package org.rsmod.content.quests.romeojuliet

import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.romeojuliet.configs.romeo_juliet_npcs
import org.rsmod.content.quests.romeojuliet.configs.romeo_juliet_objs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Romeo & Juliet quest implementation for RSMod v2.
 *
 * Help Romeo and Juliet be together by delivering messages, obtaining a Cadava potion, and
 * reuniting the lovers. Reward: 5 Quest Points.
 */
class RomeoJuliet : PluginScript() {
    override fun ScriptContext.startup() {
        // Romeo in Varrock Square
        onOpNpc1(romeo_juliet_npcs.romeo) { startRomeoDialogue(it.npc) }

        // Juliet in Capulet house
        onOpNpc1(romeo_juliet_npcs.juliet) { startJulietDialogue(it.npc) }

        // Father Lawrence in Varrock Church
        onOpNpc1(romeo_juliet_npcs.father_lawrence) { startFatherLawrenceDialogue(it.npc) }

        // Apothecary in Varrock
        onOpNpc1(romeo_juliet_npcs.apothecary) { startApothecaryDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startRomeoDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.romeo_and_juliet)) {
                0 -> romeoStartDialogue()
                1 -> romeoNeedJulietDialogue()
                2,
                3 -> romeoWaitingDialogue()
                4 -> romeoFatherLawrenceDialogue()
                5,
                6,
                7 -> romeoNeedsPotionDialogue()
                8 -> romeoCompleteDialogue()
                else -> romeoPostQuestDialogue()
            }
        }

    private suspend fun Dialogue.romeoStartDialogue() {
        chatPlayer(sad, "*sigh*")
        chatNpc(sad, "Oh I am. I'm a ghost of my former self.")
        chatPlayer(neutral, "You look sad.")
        chatNpc(quiz, "You know, I really think you could.")
        chatNpc(sad, "My love is in danger, and I can do nothing!")
        chatNpc(
            neutral,
            "Well, you could deliver a message for me. You'd need to be trustworthy, though.",
        )

        val option =
            choice2(
                "Ok Romeo, I'll deliver the message for you.",
                1,
                "I can't help you right now.",
                2,
            )
        when (option) {
            1 -> romeoAcceptQuestAction()
            2 -> romeoDeclineQuest()
        }
    }

    private suspend fun Dialogue.romeoAcceptQuestAction() {
        chatPlayer(happy, "Ok Romeo, I'll deliver the message for you.")
        chatNpc(happy, "Oh thank you so much!")
        chatNpc(sad, "My beloved Juliet is in danger!")
        chatNpc(
            angry,
            "Her father wants to force her to marry a noble - a rich man called Count Draynor.",
        )
        chatNpc(
            sad,
            "And because I could never compete with such wealth, she's taken a vial of poison!",
        )
        chatNpc(
            neutral,
            "It was a sleeping draught from the apothecary. But they won't allow me in to see her!",
        )
        chatNpc(sad, "Please, can you help me?")
        chatPlayer(happy, "Yes, of course.")

        // Check inventory space and give message
        if (!player.inv.hasFreeSpace()) {
            chatNpc(
                sad,
                "But first, make some room in your inventory. You'll need to carry a message.",
            )
            return
        }

        val added = player.invAdd(player.inv, romeo_juliet_objs.julietmessage, 1).success
        if (added) {
            chatNpc(happy, "Ok then. Give her this message from me.")
            chatNpc(neutral, "Please hurry! And be careful, adventurer.")
            access.setQuestStage(QuestList.romeo_and_juliet, 1)
        } else {
            chatNpc(sad, "I couldn't give you the message. Make sure you have inventory space.")
        }
    }

    private suspend fun Dialogue.romeoDeclineQuest() {
        chatPlayer(neutral, "I can't help you right now.")
        chatNpc(sad, "Oh, the world is so cruel!")
    }

    private suspend fun Dialogue.romeoNeedJulietDialogue() {
        chatPlayer(quiz, "Any idea where Juliet is?")
        chatNpc(
            neutral,
            "She is her father's house in the small house opposite mine. He won't let me in there!",
        )
        chatNpc(quiz, "Can you deliver this message to her?")
    }

    private suspend fun Dialogue.romeoWaitingDialogue() {
        chatPlayer(neutral, "I still need to deliver the message to Juliet.")
        chatNpc(quiz, "Ah yes, please do.")
    }

    private suspend fun Dialogue.romeoFatherLawrenceDialogue() {
        chatPlayer(happy, "Father Lawrence has an idea to help you two.")
        chatNpc(quiz, "Really? I hope he's not going to marry us right now!")
        chatNpc(neutral, "We should wait until my love's father has a change of heart.")
        chatNpc(quiz, "What was the priest's idea?")
    }

    private suspend fun Dialogue.romeoNeedsPotionDialogue() {
        chatPlayer(neutral, "I'm working on getting the Cadava potion.")
        chatNpc(quiz, "The apothecary needs cadava berries.")
        chatNpc(neutral, "He says they're found near the stone circle to the south of Varrock.")
        chatNpc(sad, "Please find some!")
    }

    private suspend fun Dialogue.romeoCompleteDialogue() {
        chatPlayer(happy, "I've reunited you with Juliet!")
        chatNpc(happy, "Yes! Thank you so much!")
        chatNpc(happy, "We can finally be together!")
        chatNpc(happy, "I'll be forever in your debt!")

        // Complete the quest
        completeQuest()
    }

    private suspend fun Dialogue.completeQuest() {
        access.setQuestStage(QuestList.romeo_and_juliet, 9)
        access.showCompletionScroll(
            quest = QuestList.romeo_and_juliet,
            rewards = listOf("5 Quest Points"),
            itemModel = romeo_juliet_objs.cadava,
            questPoints = 5,
        )
    }

    private suspend fun Dialogue.romeoPostQuestDialogue() {
        chatNpc(happy, "Thanks again for all your help!")
        chatNpc(
            neutral,
            "We have decided to leave Varrock for a while, until Juliet's father sees things differently.",
        )
    }

    // ============ JULIET ============
    private suspend fun ProtectedAccess.startJulietDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.romeo_and_juliet)) {
                0 -> julietFirstMeetingDialogue()
                1 -> julietDeliverMessageDialogue()
                2 -> julietAfterMessageDialogue()
                3,
                4,
                5,
                6 -> julietWaitingForPotionDialogue()
                7 -> julietGivePotionDialogue()
                8,
                9 -> julietPostQuestDialogue()
                else -> julietPostQuestDialogue()
            }
        }

    private suspend fun Dialogue.julietFirstMeetingDialogue() {
        chatPlayer(neutral, "Hello there.")
        chatNpc(sad, "*sniff*")
        chatNpc(sad, "There's no point in anything any more. I am utterly without hope.")
        chatPlayer(quiz, "What's wrong?")
        chatNpc(sad, "My poor Romeo! Where is he?")
    }

    private suspend fun Dialogue.julietDeliverMessageDialogue() {
        chatPlayer(happy, "I have a message from Romeo.")
        chatNpc(happy, "Oh! My heart leaps with joy!")

        // Check if player has message by trying to delete it
        if (romeo_juliet_objs.julietmessage !in player.inv) {
            chatNpc(sad, "But you don't have the message! Have you lost it?")
            chatPlayer(neutral, "I must have. I'll go back to Romeo for another.")
            return
        }

        player.invDel(player.inv, romeo_juliet_objs.julietmessage, 1)
        chatPlayer(happy, "Here you go.")
        chatNpc(neutral, "*reading the message*")
        chatNpc(sad, "Oh, I do wish I could see him, but my father won't let me out of the house!")
        chatNpc(sad, "Can you help us?")
        chatPlayer(quiz, "What can I do?")
        chatNpc(quiz, "I don't know! But Father Lawrence might have an idea.")
        chatNpc(neutral, "He lives in the church to the north-east of the main square.")
        access.setQuestStage(QuestList.romeo_and_juliet, 2)
    }

    private suspend fun Dialogue.julietAfterMessageDialogue() {
        chatNpc(happy, "Please speak with Father Lawrence, he may have a plan!")
    }

    private suspend fun Dialogue.julietWaitingForPotionDialogue() {
        chatNpc(sad, "I'm waiting for the Cadava potion...")
        chatNpc(sad, "Hurry back to me when you have it!")
    }

    private suspend fun Dialogue.julietGivePotionDialogue() {
        chatPlayer(happy, "I have the Cadava potion.")

        // Check if player has the potion
        if (romeo_juliet_objs.cadava !in player.inv) {
            chatNpc(quiz, "But you don't have it with you!")
            chatPlayer(sad, "I must have dropped it. I'll go get another.")
            return
        }

        player.invDel(player.inv, romeo_juliet_objs.cadava, 1)
        chatNpc(happy, "Oh wonderful! Let me drink it now.")
        chatNpc(neutral, "*drinks the potion*")
        chatNpc(sad, "Tell Romeo... I will be with... him...")
        chatNpc(sad, "*Juliet collapses into a deep sleep*")
        chatPlayer(sad, "Oh no! She's not... Well, that's what was supposed to happen.")
        chatPlayer(neutral, "I'd better tell Romeo!")
        access.setQuestStage(QuestList.romeo_and_juliet, 8)
    }

    private suspend fun Dialogue.julietPostQuestDialogue() {
        chatNpc(happy, "Thanks for all your help, brave adventurer!")
        chatNpc(neutral, "We're going to leave Varrock for a while.")
    }

    // ============ FATHER LAWRENCE ============
    private suspend fun ProtectedAccess.startFatherLawrenceDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.romeo_and_juliet)) {
                0,
                1 -> fatherLawrenceIdleDialogue()
                2 -> fatherLawrencePlanDialogue()
                3,
                4,
                5,
                6 -> fatherLawrenceExplainDialogue()
                7,
                8,
                9 -> fatherLawrencePostQuestDialogue()
                else -> fatherLawrenceIdleDialogue()
            }
        }

    private suspend fun Dialogue.fatherLawrenceIdleDialogue() {
        chatNpc(neutral, "May Saradomin bless you, my child.")
    }

    private suspend fun Dialogue.fatherLawrencePlanDialogue() {
        chatPlayer(quiz, "Father, Romeo and Juliet need your help.")
        chatNpc(neutral, "Ah yes, I've heard of their troubles.")
        chatNpc(quiz, "I may have a solution, but it's rather... unorthodox.")
        chatNpc(happy, "You see, I specialize in potions.")
        chatNpc(neutral, "And there is one, a Cadava potion, that could help them.")
        chatNpc(neutral, "If Juliet were to drink it, she would appear dead for a short time.")
        chatNpc(neutral, "Her father would think her dead and put her in the crypt.")
        chatNpc(happy, "Then Romeo could come and rescue her!")
        chatPlayer(happy, "That might work!")
        chatNpc(neutral, "I cannot make the potion myself, but the apothecary can.")
        chatNpc(neutral, "Speak with him, his shop is near the south-west corner of Varrock.")
        access.setQuestStage(QuestList.romeo_and_juliet, 3)
    }

    private suspend fun Dialogue.fatherLawrenceExplainDialogue() {
        chatPlayer(neutral, "The apothecary needs cadava berries.")
        chatNpc(neutral, "Ah yes, they grow to the south of Varrock.")
        chatNpc(neutral, "Near the stone circle.")
        chatNpc(sad, "Be careful, some of them are poisonous!")
    }

    private suspend fun Dialogue.fatherLawrencePostQuestDialogue() {
        chatNpc(happy, "Ah, the hero of the hour!")
        chatNpc(neutral, "You did a wonderful thing uniting those two lovebirds.")
    }

    // ============ APOTHECARY ============
    private suspend fun ProtectedAccess.startApothecaryDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.romeo_and_juliet)) {
                0,
                1,
                2 -> apothecaryDefaultDialogue()
                3 -> apothecaryRequestDialogue()
                4,
                5 -> apothecaryNeedBerriesDialogue()
                6 -> apothecaryMakePotionDialogue()
                7,
                8,
                9 -> apothecaryPostQuestDialogue()
                else -> apothecaryDefaultDialogue()
            }
        }

    private suspend fun Dialogue.apothecaryDefaultDialogue() {
        chatNpc(neutral, "I am the apothecary.")
        chatNpc(happy, "I have potions that can achieve almost anything.")
    }

    private suspend fun Dialogue.apothecaryRequestDialogue() {
        chatPlayer(quiz, "I need a Cadava potion for Father Lawrence.")
        chatNpc(sad, "Cadava potion? That's a dangerous brew.")
        chatNpc(neutral, "I need some cadava berries to make it.")
        chatNpc(neutral, "They grow near the stone circle south of Varrock.")
        chatNpc(neutral, "Bring me some and I'll make the potion.")
        access.setQuestStage(QuestList.romeo_and_juliet, 4)
    }

    private suspend fun Dialogue.apothecaryNeedBerriesDialogue() {
        chatPlayer(neutral, "I've come for the Cadava potion.")
        chatNpc(quiz, "Do you have the berries?")

        // Check if player has berries
        if (romeo_juliet_objs.cadavaberries !in player.inv) {
            chatPlayer(sad, "Not yet.")
            chatNpc(neutral, "Well, find some! They are south of Varrock near the stone circle.")
            return
        }

        player.invDel(player.inv, romeo_juliet_objs.cadavaberries, 1)
        chatNpc(happy, "Excellent! I shall make the potion.")
        chatNpc(neutral, "Give me a moment...")
        chatNpc(neutral, "*mixes ingredients*")

        // Check if player has inventory space for potion
        if (!player.inv.hasFreeSpace()) {
            chatNpc(
                sad,
                "Hmm, you don't have room for the potion. Make some space and speak to me again.",
            )
            return
        }

        val added = player.invAdd(player.inv, romeo_juliet_objs.cadava, 1).success
        if (added) {
            chatNpc(happy, "Here is the Cadava potion.")
            chatNpc(sad, "Be careful with it!")
            chatNpc(neutral, "And tell Romeo to be ready to act when Juliet drinks it!")
            access.setQuestStage(QuestList.romeo_and_juliet, 7)
        } else {
            chatNpc(sad, "I couldn't give you the potion. Make sure you have inventory space.")
        }
    }

    private suspend fun Dialogue.apothecaryMakePotionDialogue() {
        chatPlayer(neutral, "Can I have the Cadava potion?")
        chatNpc(sad, "I'm out of cadava berries. Bring me some and I'll make it.")
    }

    private suspend fun Dialogue.apothecaryPostQuestDialogue() {
        chatNpc(happy, "Ah, the hero of Varrock's greatest love story!")
        chatNpc(neutral, "My potion worked perfectly.")
    }
}
