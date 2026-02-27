package org.rsmod.content.quests.demonslayer

import jakarta.inject.Inject
import kotlin.math.max
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.giveQuestReward
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onModifyNpcHit
import org.rsmod.api.script.onNpcHit
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.demonslayer.configs.demon_slayer_npcs
import org.rsmod.content.quests.demonslayer.configs.demon_slayer_objs
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.entity.player.PlayerUid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Demon Slayer quest implementation for RSMod v2.
 *
 * Quest stages: 0 - Not started 1 - Started, talked to Gypsy Aris, need to talk to Sir Prysin 2 -
 * Talked to Sir Prysin, need to collect 3 keys 3 - Has Silverlight, need to defeat Delrith 4 -
 * Quest complete
 *
 * Reward: 3 Quest Points, Silverlight sword
 */
class DemonSlayer
@Inject
constructor(
    private val playerList: PlayerList,
    private val protectedAccess: ProtectedAccessLauncher,
    private val objRepo: ObjRepository,
) : PluginScript() {

    override fun ScriptContext.startup() {
        // Gypsy Aris - Quest start
        onOpNpc1(demon_slayer_npcs.hundred_aris) { gypsyArisDialogue(it.npc) }

        // Sir Prysin - Gives information about Silverlight
        onOpNpc1(demon_slayer_npcs.sir_prysin) { sirPrysinDialogue(it.npc) }

        // Captain Rovin - Has key 1
        onOpNpc1(demon_slayer_npcs.captain_rovin) { captainRovinDialogue(it.npc) }

        // Wizard Traiborn - Has key 2 (needs 25 bones)
        onOpNpc1(demon_slayer_npcs.traiborn) { wizardTraibornDialogue(it.npc) }

        // Delrith - The demon boss
        onOpNpc1(demon_slayer_npcs.delrith) { delrithDialogue(it.npc) }

        // Delrith combat mechanics - only Silverlight can damage him properly
        onModifyNpcHit(demon_slayer_npcs.delrith) {
            if (!hit.isFromPlayer) {
                return@onModifyNpcHit
            }
            val sourceUid = hit.sourceUid ?: return@onModifyNpcHit
            val source = PlayerUid(sourceUid).resolve(playerList) ?: return@onModifyNpcHit

            // Check if player is using Silverlight
            val usingSilverlight = hit.isRighthandObj(demon_slayer_objs.silverlight)

            if (!usingSilverlight) {
                // Without Silverlight, damage is severely reduced
                hit.damage = max(1, hit.damage / 10)

                if (npc.hitpoints - hit.damage <= 0) {
                    // Cannot kill Delrith without Silverlight
                    hit.damage = max(0, npc.hitpoints - 1)
                }

                protectedAccess.launch(source) {
                    mes("You need Silverlight to effectively damage Delrith.")
                }
            }
        }

        onNpcHit(demon_slayer_npcs.delrith) {
            val source = hit.resolvePlayerSource(playerList) ?: return@onNpcHit
            val usingSilverlight = hit.isRighthandObj(demon_slayer_objs.silverlight)

            // Handle death with Silverlight
            if (npc.hitpoints - hit.damage <= 0 && usingSilverlight) {
                protectedAccess.launch(source) {
                    if (getQuestStage(QuestList.demon_slayer) == 3) {
                        setQuestStage(QuestList.demon_slayer, 4)
                        giveQuestReward(QuestList.demon_slayer)
                        showCompletionScroll(
                            quest = QuestList.demon_slayer,
                            rewards = listOf("3 Quest Points", "Silverlight"),
                            itemModel = demon_slayer_objs.silverlight,
                            questPoints = 3,
                        )
                    }
                }
            }
        }
    }

    // ---- Gypsy Aris Dialogue ----
    private suspend fun ProtectedAccess.gypsyArisDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.demon_slayer)) {
                0 -> gypsyStartQuest()
                1 -> gypsyInProgress()
                else -> gypsyFinished()
            }
        }

    private suspend fun Dialogue.gypsyStartQuest() {
        chatNpc(happy, "Hello, young adventurer. I am Gypsy Aris.")
        chatNpc(quiz, "I can see into the future, and I see dark times ahead.")
        val option = choice2("What do you see?", 1, "I don't believe in fortune telling.", 2)
        when (option) {
            1 -> {
                chatPlayer(quiz, "What do you see?")
                gypsyExplainVision()
            }
            2 -> {
                chatPlayer(neutral, "I don't believe in fortune telling.")
                chatNpc(quiz, "You should, for your own sake. A great evil is coming.")
                val option2 = choice2("Tell me more.", 1, "I have to go.", 2)
                when (option2) {
                    1 -> gypsyExplainVision()
                    2 -> chatPlayer(neutral, "I have to go.")
                }
            }
        }
    }

    private suspend fun Dialogue.gypsyExplainVision() {
        chatNpc(sad, "I see a demon named Delrith returning to this world.")
        chatNpc(sad, "He was banished long ago by a hero named Wally.")
        chatNpc(quiz, "The demon is buried beneath the stone circle south of Varrock.")
        chatNpc(quiz, "Only the sword Silverlight can defeat him.")
        val option = choice2("Where can I find Silverlight?", 1, "I'll stop him.", 2)
        when (option) {
            1 -> {
                chatPlayer(quiz, "Where can I find Silverlight?")
                chatNpc(neutral, "Sir Prysin in Varrock Palace has it locked away.")
                chatNpc(neutral, "Speak to him. He will know what to do.")
                access.setQuestStage(QuestList.demon_slayer, 1)
            }
            2 -> {
                chatPlayer(happy, "I'll stop him.")
                chatNpc(happy, "Brave words! Speak to Sir Prysin in Varrock Palace.")
                chatNpc(neutral, "He keeps Silverlight locked away.")
                access.setQuestStage(QuestList.demon_slayer, 1)
            }
        }
    }

    private suspend fun Dialogue.gypsyInProgress() {
        chatNpc(quiz, "Have you found Silverlight yet?")
        chatPlayer(neutral, "Not yet. I'm working on it.")
        chatNpc(neutral, "Speak to Sir Prysin in Varrock Palace. He has the sword.")
    }

    private suspend fun Dialogue.gypsyFinished() {
        chatNpc(happy, "You have done well, adventurer. Delrith will trouble us no more.")
        chatPlayer(happy, "It was my pleasure.")
    }

    // ---- Sir Prysin Dialogue ----
    private suspend fun ProtectedAccess.sirPrysinDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.demon_slayer)) {
                0 -> prysinGeneric()
                1 -> prysinStartQuest()
                2 -> prysinCollectingKeys()
                3 -> prysinHasSilverlight()
                else -> prysinFinished()
            }
        }

    private suspend fun Dialogue.prysinGeneric() {
        chatNpc(neutral, "Hello. I'm Sir Prysin. I am a knight of Varrock.")
        chatPlayer(quiz, "What do you do here?")
        chatNpc(neutral, "I guard the palace and protect King Roald.")
    }

    private suspend fun Dialogue.prysinStartQuest() {
        chatPlayer(quiz, "Gypsy Aris said you have Silverlight.")
        chatNpc(sad, "Ah, Silverlight. Yes, I have it locked away.")
        chatNpc(sad, "But I lost the key! Well, actually...")
        chatNpc(neutral, "I had three keys made and gave them to trusted people.")
        chatNpc(quiz, "Do you want me to collect the keys?")
        chatNpc(happy, "Would you? That would be splendid!")
        explainKeysNeeded()
    }

    private suspend fun Dialogue.explainKeysNeeded() {
        chatNpc(neutral, "Captain Rovin has one key. He's in the northwest tower.")
        chatNpc(neutral, "Wizard Traiborn has another. He's at the Wizards' Tower.")
        chatNpc(neutral, "The third key... I dropped it down the drain in the kitchen!")
        chatPlayer(quiz, "I'll find all three keys.")
        chatNpc(happy, "Bring them to me and I'll give you Silverlight!")
        access.setQuestStage(QuestList.demon_slayer, 2)
    }

    private suspend fun Dialogue.prysinCollectingKeys() {
        val hasKey1 = player.inv.contains(demon_slayer_objs.silverlight_key_1)
        val hasKey2 = player.inv.contains(demon_slayer_objs.silverlight_key_2)
        val hasKey3 = player.inv.contains(demon_slayer_objs.silverlight_key_3)

        when {
            hasKey1 && hasKey2 && hasKey3 -> {
                chatPlayer(happy, "I have all three keys!")
                chatNpc(happy, "Excellent! Let me get Silverlight for you.")

                // Remove keys
                val removed1 =
                    player.invDel(player.inv, demon_slayer_objs.silverlight_key_1, 1).success
                val removed2 =
                    player.invDel(player.inv, demon_slayer_objs.silverlight_key_2, 1).success
                val removed3 =
                    player.invDel(player.inv, demon_slayer_objs.silverlight_key_3, 1).success

                if (!removed1 || !removed2 || !removed3) {
                    chatNpc(sad, "Hmm, you seem to be missing a key after all.")
                    if (removed1)
                        player.invAddOrDrop(objRepo, demon_slayer_objs.silverlight_key_1, 1)
                    if (removed2)
                        player.invAddOrDrop(objRepo, demon_slayer_objs.silverlight_key_2, 1)
                    if (removed3)
                        player.invAddOrDrop(objRepo, demon_slayer_objs.silverlight_key_3, 1)
                    return
                }

                // Give Silverlight
                val added = player.invAdd(player.inv, demon_slayer_objs.silverlight, 1).success
                if (added) {
                    chatNpc(happy, "Here is Silverlight. Use it to defeat Delrith!")
                    chatNpc(
                        neutral,
                        "The demon is buried beneath the stone circle south of Varrock.",
                    )
                    chatNpc(quiz, "Remember: only Silverlight can defeat him!")
                    access.setQuestStage(QuestList.demon_slayer, 3)
                } else {
                    chatNpc(sad, "You need a free inventory space for Silverlight.")
                    // Return keys
                    player.invAddOrDrop(objRepo, demon_slayer_objs.silverlight_key_1, 1)
                    player.invAddOrDrop(objRepo, demon_slayer_objs.silverlight_key_2, 1)
                    player.invAddOrDrop(objRepo, demon_slayer_objs.silverlight_key_3, 1)
                }
            }
            hasKey1 || hasKey2 || hasKey3 -> {
                chatNpc(quiz, "How is the key hunt going?")
                val keysStillNeeded = buildList {
                    if (!hasKey1) add("Captain Rovin's key")
                    if (!hasKey2) add("Wizard Traiborn's key")
                    if (!hasKey3) add("the key from the drain")
                }
                chatPlayer(neutral, "I still need: ${keysStillNeeded.joinToString(", ")}.")
                chatNpc(neutral, "Keep looking!")
            }
            else -> {
                chatNpc(quiz, "Have you found any keys yet?")
                chatPlayer(sad, "Not yet.")
                chatNpc(neutral, "Captain Rovin is in the northwest tower.")
                chatNpc(neutral, "Wizard Traiborn is at the Wizards' Tower.")
                chatNpc(neutral, "And my key is somewhere in the kitchen drain...")
            }
        }
    }

    private suspend fun Dialogue.prysinHasSilverlight() {
        chatNpc(quiz, "Have you defeated Delrith yet?")
        chatPlayer(neutral, "Not yet.")
        chatNpc(neutral, "Go to the stone circle south of Varrock.")
        chatNpc(neutral, "Use Silverlight to banish the demon!")
    }

    private suspend fun Dialogue.prysinFinished() {
        chatNpc(happy, "Thank you for defeating Delrith! You are a true hero.")
    }

    // ---- Captain Rovin Dialogue ----
    private suspend fun ProtectedAccess.captainRovinDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.demon_slayer)) {
                0,
                1 -> rovinGeneric()
                2 -> rovinGiveKey()
                else -> rovinFinished()
            }
        }

    private suspend fun Dialogue.rovinGeneric() {
        chatNpc(angry, "Stand to attention when you speak to the Captain of the Guard!")
    }

    private suspend fun Dialogue.rovinGiveKey() {
        if (player.inv.contains(demon_slayer_objs.silverlight_key_1)) {
            chatNpc(neutral, "You already have the key I was guarding.")
            return
        }

        chatPlayer(quiz, "Sir Prysin said you have a key to Silverlight's case.")
        chatNpc(quiz, "Why should I give it to you?")

        val option =
            choice2("I need it to fight Delrith.", 1, "Gypsy Aris foretold the demon returning.", 2)
        when (option) {
            1 -> {
                chatPlayer(angry, "I need it to fight Delrith. A demon threatens Varrock!")
                chatNpc(quiz, "A demon you say? That's serious.")
            }
            2 -> {
                chatPlayer(quiz, "Gypsy Aris foretold the demon Delrith returning.")
                chatNpc(quiz, "Gypsy Aris is never wrong about such things...")
            }
        }

        chatNpc(neutral, "Very well. Take the key. Protect Varrock!")
        val added = player.invAdd(player.inv, demon_slayer_objs.silverlight_key_1, 1).success
        if (added) {
            chatNpc(happy, "Here's the key. Good luck defeating the demon!")
        } else {
            chatNpc(sad, "You need a free inventory space for the key.")
        }
    }

    private suspend fun Dialogue.rovinFinished() {
        chatNpc(happy, "Well done defeating that demon!")
    }

    // ---- Wizard Traiborn Dialogue ----
    private suspend fun ProtectedAccess.wizardTraibornDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.demon_slayer)) {
                0,
                1 -> traibornGeneric()
                2 -> traibornGiveKey()
                else -> traibornFinished()
            }
        }

    private suspend fun Dialogue.traibornGeneric() {
        chatNpc(quiz, "Hello, dearie. I'm Wizard Traiborn.")
        chatPlayer(quiz, "What do you do here?")
        chatNpc(happy, "I study the magical arts and summon demons!")
        chatPlayer(quiz, "Demons?!")
        chatNpc(quiz, "Oh, just imps. They're mostly harmless.")
    }

    private suspend fun Dialogue.traibornGiveKey() {
        if (player.inv.contains(demon_slayer_objs.silverlight_key_2)) {
            chatNpc(quiz, "You already have the key, dearie.")
            return
        }

        chatPlayer(quiz, "Sir Prysin said you have a key to Silverlight's case.")
        chatNpc(quiz, "Silverlight? Oh, that sword!")
        chatNpc(neutral, "I do have a key, but I need something first.")
        chatNpc(quiz, "I'm researching a spell to summon lots of imps.")
        chatNpc(neutral, "I need 25 sets of bones for my research.")

        // Count bones
        val bonesCount = access.invTotal(player.inv, demon_slayer_objs.bones)

        if (bonesCount >= 25) {
            chatPlayer(happy, "I have 25 sets of bones right here!")
            chatNpc(happy, "Wonderful! Let me take them.")

            // Remove 25 bones
            var removed = 0
            repeat(25) {
                if (player.invDel(player.inv, demon_slayer_objs.bones, 1).success) {
                    removed++
                }
            }

            if (removed >= 25) {
                val added =
                    player.invAdd(player.inv, demon_slayer_objs.silverlight_key_2, 1).success
                if (added) {
                    chatNpc(happy, "Here's the key, dearie. Good luck with the demon!")
                } else {
                    chatNpc(sad, "You need a free inventory space for the key.")
                    // Return bones
                    repeat(25) { player.invAddOrDrop(objRepo, demon_slayer_objs.bones, 1) }
                }
            } else {
                chatNpc(sad, "It seems you don't have enough bones after all.")
            }
        } else {
            chatPlayer(sad, "I only have $bonesCount sets of bones.")
            chatNpc(neutral, "Bring me 25 sets of bones and I'll give you the key.")
            chatNpc(quiz, "You can get bones from skeletons, goblins, or other creatures.")
        }
    }

    private suspend fun Dialogue.traibornFinished() {
        chatNpc(happy, "Thank you for the bones, dearie!")
        chatNpc(quiz, "My imp summoning research is going splendidly!")
    }

    // ---- Delrith Dialogue ----
    private suspend fun ProtectedAccess.delrithDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.demon_slayer)) {
                0,
                1 -> {
                    chatNpc(angry, "...")
                    chatPlayer(quiz, "A strange dark presence emanates from the stone circle.")
                }
                2 -> {
                    chatNpc(angry, "...")
                    chatPlayer(sad, "I need Silverlight before I can face this demon.")
                }
                3 -> {
                    chatNpc(angry, "Who dares disturb me?")
                    chatPlayer(angry, "I am here to banish you, Delrith!")
                    chatNpc(angry, "Foolish mortal! You cannot stop me!")
                }
                else -> {
                    chatNpc(angry, "You may have banished me, but I will return one day...")
                }
            }
        }
}
