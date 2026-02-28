package org.rsmod.content.quests.piratestreasure

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.seqs
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
import org.rsmod.api.script.onOpHeld2
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.content.quests.piratestreasure.configs.pirates_treasure_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

internal object pirates_treasure_objs : ObjReferences() {
    val karamja_rum = find("karamja_rum")
}

class PiratesTreasure @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(pirates_treasure_npcs.redbeard_frank) { startFrankDialogue(it.npc) }
        onOpNpc1(pirates_treasure_npcs.luthas) { startLuthasDialogue(it.npc) }
        onOpNpc1(pirates_treasure_npcs.customs_officer) { startCustomsDialogue(it.npc) }
        onOpHeld2(objs.spade) { digForTreasure() }
    }

    private suspend fun ProtectedAccess.startFrankDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.pirates_treasure)) {
                0 -> frankStartDialogue()
                1,
                2 -> frankRumDialogue()
                3 -> frankDigReminderDialogue()
                else -> frankPostQuestDialogue()
            }
        }

    private suspend fun Dialogue.frankStartDialogue() {
        chatNpc(quiz, "Arrr, what d'ye want?")
        val option = choice2("I'm in search of treasure.", 1, "Nothing. Sorry to bother you.", 2)
        when (option) {
            1 -> {
                chatPlayer(happy, "I'm in search of treasure.")
                chatNpc(
                    happy,
                    "Aye, I knows where some be buried. Bring me a bottle o' Karamja rum first.",
                )
                chatNpc(
                    neutral,
                    "Customs at Port Sarim don't let rum through, so you'll need to smuggle it.",
                )
                access.setQuestStage(QuestList.pirates_treasure, 1)
            }
            2 -> chatPlayer(neutral, "Nothing. Sorry to bother you.")
        }
    }

    private suspend fun Dialogue.frankRumDialogue() {
        when {
            pirates_treasure_objs.karamja_rum in player.inv -> {
                chatPlayer(happy, "I got your Karamja rum.")
                handInRumAndGiveKey()
            }
            objs.piratemessage in player.inv -> {
                chatNpc(
                    quiz,
                    "That rum's still hidden in a crate. Get it past customs and bring it here.",
                )
            }
            else -> {
                chatNpc(quiz, "No rum yet? Talk to Luthas on Karamja if ye need help smuggling it.")
            }
        }
    }

    private suspend fun Dialogue.handInRumAndGiveKey() {
        val removedRum = player.invDel(player.inv, pirates_treasure_objs.karamja_rum, 1).success
        if (!removedRum) {
            chatNpc(sad, "Stop yer tricks. Ye don't have the rum.")
            return
        }

        val addedKey = player.invAdd(player.inv, objs.chest_key, 1).success
        if (!addedKey) {
            chatNpc(sad, "Make some room in yer inventory and speak to me again.")
            player.invAddOrDrop(objRepo, pirates_treasure_objs.karamja_rum, 1)
            return
        }

        chatNpc(happy, "Aye, that's the stuff. Here's the key to the chest.")
        chatNpc(
            happy,
            "Dig in Falador Park, near the statue in the gardens. That's where the treasure be.",
        )
        access.setQuestStage(QuestList.pirates_treasure, 3)
    }

    private suspend fun Dialogue.frankDigReminderDialogue() {
        if (objs.chest_key in player.inv) {
            chatNpc(happy, "Ye have the key. Dig in Falador Park and claim yer treasure.")
        } else {
            chatNpc(neutral, "Lost the key, did ye? Keep searching around where ye dug.")
        }
    }

    private suspend fun Dialogue.frankPostQuestDialogue() {
        chatNpc(happy, "Enjoying yer treasure, matey?")
    }

    private suspend fun ProtectedAccess.startLuthasDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.pirates_treasure)) {
                0 -> {
                    chatNpc(neutral, "Busy, mate. Got bananas to shift.")
                }
                1,
                2 -> luthasSmugglingDialogue()
                else -> chatNpc(neutral, "Business as usual. Bananas all day.")
            }
        }

    private suspend fun Dialogue.luthasSmugglingDialogue() {
        when {
            objs.piratemessage in player.inv -> {
                chatNpc(
                    neutral,
                    "I've packed your rum in a crate. Go through Port Sarim customs, then open it.",
                )
            }
            objs.karamja_rum in player.inv -> {
                chatNpc(quiz, "Need that rum hidden in a banana crate?")
                val option = choice2("Yes, hide it for me.", 1, "No, I'll handle it.", 2)
                when (option) {
                    1 -> hideRumInCrate()
                    2 -> chatPlayer(neutral, "No, I'll handle it.")
                }
            }
            else -> chatNpc(neutral, "Buy a bottle of Karamja rum and bring it here.")
        }
    }

    private suspend fun Dialogue.hideRumInCrate() {
        val removed = player.invDel(player.inv, objs.karamja_rum, 1).success
        if (!removed) {
            chatNpc(sad, "Looks like ye don't have any rum for me to hide.")
            return
        }

        val added = player.invAdd(player.inv, objs.piratemessage, 1).success
        if (!added) {
            chatNpc(sad, "Clear an inventory slot and I'll hide it in a crate for ye.")
            player.invAddOrDrop(objRepo, objs.karamja_rum, 1)
            return
        }

        chatNpc(happy, "Done. That's your hidden rum crate token.")
        chatNpc(neutral, "Go through customs in Port Sarim, then claim the rum.")
        if (access.getQuestStage(QuestList.pirates_treasure) < 2) {
            access.setQuestStage(QuestList.pirates_treasure, 2)
        }
    }

    private suspend fun ProtectedAccess.startCustomsDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.pirates_treasure)) {
                0 -> chatNpc(neutral, "Welcome to Port Sarim.")
                1,
                2 -> customsSmugglingDialogue()
                else -> chatNpc(neutral, "Move along, please.")
            }
        }

    private suspend fun Dialogue.customsSmugglingDialogue() {
        when {
            objs.karamja_rum in player.inv -> {
                chatNpc(angry, "No Karamja rum allowed through customs. Confiscated.")
                player.invDel(player.inv, objs.karamja_rum, 1)
            }
            objs.piratemessage in player.inv -> {
                chatNpc(neutral, "All clear. You may pass.")
                val removed = player.invDel(player.inv, objs.piratemessage, 1).success
                if (!removed) {
                    chatNpc(sad, "Hold on... looks like something went wrong there.")
                    return
                }
                val added = player.invAdd(player.inv, objs.karamja_rum, 1).success
                if (!added) {
                    chatNpc(sad, "Inventory full. I can't hand over your crate contents.")
                    player.invAddOrDrop(objRepo, objs.piratemessage, 1)
                    return
                }
                chatNpc(happy, "You recover your smuggled bottle of Karamja rum.")
            }
            else -> chatNpc(neutral, "Nothing to declare? Move along.")
        }
    }

    private suspend fun ProtectedAccess.digForTreasure() {
        val stage = getQuestStage(QuestList.pirates_treasure)
        if (stage < 3) {
            mes("You dig a hole in the ground.")
            mes("You find nothing.")
            return
        }

        anim(seqs.human_pickupfloor)
        delay(2)

        if (!isAtFaladorParkDigSpot()) {
            mes("You dig a hole in the ground.")
            mes("Nothing interesting here. The key points to Falador Park.")
            return
        }

        val removedKey = invDel(player.inv, objs.chest_key, 1).success
        if (!removedKey) {
            mes("You need the chest key from Redbeard Frank to claim the treasure.")
            return
        }

        val addedCoins = invAdd(player.inv, objs.coins, 450).success
        if (!addedCoins) {
            mes("You need inventory space to take the treasure.")
            invAddOrDrop(objRepo, objs.chest_key, 1)
            return
        }

        setQuestStage(QuestList.pirates_treasure, 4)
        showCompletionScroll(
            quest = QuestList.pirates_treasure,
            rewards = listOf("2 Quest Points", "450 Coins"),
            itemModel = objs.coins,
            questPoints = 2,
        )
    }

    private fun ProtectedAccess.isAtFaladorParkDigSpot(): Boolean {
        val x = player.coords.x
        val z = player.coords.z
        return x in 2998..3011 && z in 3370..3384
    }
}
