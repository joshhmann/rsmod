package org.rsmod.content.areas.city.falador.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.giveQuestReward
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.content.areas.city.falador.configs.FaladorNpcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

object SquireObjs : ObjReferences() {
    val faladian_sword = find("faladian_sword")
}

class SquireScript @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(FaladorNpcs.squire) { squireDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.squireDialogue(npc: Npc) =
        startDialogue(npc) {
            val stage = getQuestStage(QuestList.knights_sword)
            when {
                stage == 0 -> squireOfferQuest()
                stage in 1..5 -> squireInProgress()
                stage >= 7 -> squireCompleted()
                else -> squireOfferQuest()
            }
        }

    private suspend fun Dialogue.squireOfferQuest() {
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
                        chatNpc(neutral, "Maybe you should talk to Reldo, the librarian in Varrock.")
                        chatNpc(neutral, "He knows a lot about ancient smithing techniques!")
                        chatNpc(happy, "Please, I'm counting on you!")
                        access.setQuestStage(QuestList.knights_sword, 1)
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

    private suspend fun Dialogue.squireInProgress() {
        val hasSword = player.inv.contains(SquireObjs.faladian_sword)

        if (hasSword) {
            chatNpc(sad, "Have you found my sword yet?")
            chatPlayer(happy, "I have it! A master smith forged a perfect replica!")
            chatNpc(happy, "You did? Let me see it!")
            completeSwordReturn()
        } else {
            chatNpc(sad, "Have you found my sword yet?")
            val option =
                choice2(
                    "I'm working on it. Any leads?",
                    1,
                    "Not yet. I'll keep looking.",
                    2,
                )
            when (option) {
                1 -> {
                    chatPlayer(quiz, "I'm working on it. Any leads?")
                    chatNpc(neutral, "I heard Thurgo lives south of Port Sarim, near the Ice Dungeon.")
                    chatNpc(neutral, "He's an Imcando dwarf - a brilliant smith if you can find him.")
                }
                2 -> {
                    chatPlayer(neutral, "Not yet. I'll keep looking.")
                    chatNpc(sad, "Please hurry... Sir Vyvin is starting to ask questions.")
                }
            }
        }
    }

    private suspend fun Dialogue.completeSwordReturn() {
        val removedSword = player.invDel(player.inv, SquireObjs.faladian_sword, 1).success
        if (!removedSword) {
            chatNpc(sad, "Hmm, it seems to have vanished. Do you still have it?")
            return
        }

        chatNpc(happy, "This is incredible! It looks exactly like the original!")
        chatNpc(happy, "Sir Vyvin will never know it was lost! You've saved my life!")
        chatNpc(happy, "Thank you! Thank you so much!")
        chatPlayer(happy, "The Imcando craftsmanship is remarkable. You'd never tell it apart!")

        access.setQuestStage(QuestList.knights_sword, 7)
        access.giveQuestReward(QuestList.knights_sword)
        access.showCompletionScroll(
            quest = QuestList.knights_sword,
            rewards = listOf("1 Quest Point", "12,725 Smithing XP"),
            itemModel = SquireObjs.faladian_sword,
            questPoints = 1,
        )
    }

    private suspend fun Dialogue.squireCompleted() {
        chatNpc(happy, "Hello again! Sir Vyvin still hasn't noticed the difference!")
        chatNpc(happy, "You're a lifesaver. If you ever need anything, just ask!")
        chatPlayer(happy, "Glad I could help. Take better care of this one!")
    }
}
