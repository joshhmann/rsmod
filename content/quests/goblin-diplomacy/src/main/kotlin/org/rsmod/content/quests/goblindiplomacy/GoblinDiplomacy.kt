package org.rsmod.content.quests.goblindiplomacy

import jakarta.inject.Inject
import org.rsmod.api.config.refs.BaseStats
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
import org.rsmod.content.quests.goblindiplomacy.configs.goblin_diplomacy_npcs
import org.rsmod.content.quests.goblindiplomacy.configs.goblin_diplomacy_objs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Goblin Diplomacy quest implementation.
 *
 * Quest stages: 0 - Not started 1 - Started, talked to generals, need to find orange goblin mail
 * 2 - Gave orange mail, generals argued, now need blue mail 3 - Quest complete
 *
 * This quest involves helping goblin generals decide on a new armor color. The solution is to make
 * orange dye (red + yellow) and blue dye, then dye goblin mail.
 */
class GoblinDiplomacy @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        // NPC dialogues - both generals share the same dialogue
        onOpNpc1(goblin_diplomacy_npcs.general_bentnoze) { generalBentnozeDialogue(it.npc) }
        onOpNpc1(goblin_diplomacy_npcs.general_wartface) { generalWartfaceDialogue(it.npc) }

        // Orange dye crafting (red + yellow)
        onOpHeldU(goblin_diplomacy_objs.reddye, goblin_diplomacy_objs.yellowdye) { makeOrangeDye() }

        // Dye goblin mail orange
        onOpHeldU(goblin_diplomacy_objs.goblin_armour, goblin_diplomacy_objs.orangedye) {
            dyeGoblinMailOrange()
        }

        // Dye goblin mail blue
        onOpHeldU(goblin_diplomacy_objs.goblin_armour, goblin_diplomacy_objs.bluedye) {
            dyeGoblinMailBlue()
        }
    }

    // ---- General Bentnoze Dialogue ----
    private suspend fun ProtectedAccess.generalBentnozeDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.goblin_diplomacy)) {
                0 -> generalsStartQuest()
                1 -> generalsNeedOrangeMail()
                2 -> generalsNeedBlueMail()
                else -> generalsAfterQuest()
            }
        }

    // ---- General Wartface Dialogue ----
    private suspend fun ProtectedAccess.generalWartfaceDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.goblin_diplomacy)) {
                0 -> generalsStartQuest()
                1 -> generalsNeedOrangeMail()
                2 -> generalsNeedBlueMail()
                else -> generalsAfterQuest()
            }
        }

    // ---- Quest Start Dialogue ----
    private suspend fun Dialogue.generalsStartQuest() {
        chatNpc(angry, "Green armor best! Red armor no good!")
        chatNpc2(angry, "No! Red armor best! Green armor for cowards!")

        val option = choice2("Why are you arguing?", 1, "I don't care what color your armor is!", 2)

        when (option) {
            1 -> {
                chatPlayer(quiz, "Why are you arguing?")
                chatNpc(angry, "We can't decide on armor color! General Wartface want red!")
                chatNpc2(angry, "No! Green! Green is color of true goblin!")
                chatNpc(angry, "Red! Red like blood of our enemies!")
                chatPlayer(quiz, "What about a different color?")
                chatNpc(quiz, "Different color? What you mean?")
                chatPlayer(quiz, "How about orange? That's a nice color.")
                chatNpc(happy, "Orange... Yes! Orange like fire!")
                chatNpc2(happy, "Orange... That sound good!")
                chatNpc(quiz, "You bring us orange armor! Then we decide!")
                access.setQuestStage(QuestList.goblin_diplomacy, 1)
            }
            2 -> chatPlayer(neutral, "I don't care what color your armor is!")
        }
    }

    // ---- Need Orange Mail ----
    private suspend fun Dialogue.generalsNeedOrangeMail() {
        val hasOrangeMail = goblin_diplomacy_objs.goblin_armour_orange in player.inv

        when {
            hasOrangeMail -> {
                chatPlayer(happy, "I've brought some orange goblin mail for you!")
                chatNpc(happy, "Ooh! Orange armor! Give here!")

                // Remove orange mail
                val removed =
                    player.invDel(player.inv, goblin_diplomacy_objs.goblin_armour_orange, 1).success

                if (!removed) {
                    chatNpc(sad, "You no have orange mail!")
                    return
                }

                chatNpc(angry, "Hmm... This orange...")
                chatNpc2(angry, "Too bright! Hurt eyes!")
                chatNpc(quiz, "What other color there is?")
                chatPlayer(quiz, "How about blue?")
                chatNpc(happy, "Blue! Yes! Deep blue like night sky!")
                chatNpc2(happy, "Blue good! Bring us blue armor!")
                access.setQuestStage(QuestList.goblin_diplomacy, 2)
            }
            else -> {
                chatNpc(quiz, "You bring orange armor yet?")
                chatPlayer(neutral, "Not yet. How do I make orange dye?")
                chatNpc(neutral, "You need red and yellow. Mix together.")
                chatNpc2(neutral, "Red from redberries. Yellow from onions.")
                chatNpc(neutral, "Go to dye maker in Draynor village.")
            }
        }
    }

    // ---- Need Blue Mail ----
    private suspend fun Dialogue.generalsNeedBlueMail() {
        val hasBlueMail = goblin_diplomacy_objs.goblin_armour_darkblue in player.inv

        when {
            hasBlueMail -> {
                chatPlayer(happy, "I've brought some blue goblin mail!")
                chatNpc(happy, "Blue armor! Yes!")
                chatNpc2(happy, "This perfect! Deep blue!")
                chatNpc(happy, "We wear blue now! All goblins wear blue!")

                // Remove blue mail
                val removed =
                    player
                        .invDel(player.inv, goblin_diplomacy_objs.goblin_armour_darkblue, 1)
                        .success

                if (!removed) {
                    chatNpc(sad, "You no have blue mail!")
                    return
                }

                completeQuest()
            }
            else -> {
                chatNpc(quiz, "Where blue armor?")
                chatPlayer(neutral, "I'm working on it. How do I make blue dye?")
                chatNpc(neutral, "Need woad leaves. Gardener in Falador sell them.")
                chatNpc2(neutral, "Then take to dye maker.")
            }
        }
    }

    // ---- After Quest ----
    private suspend fun Dialogue.generalsAfterQuest() {
        chatNpc(happy, "You friend of goblins! We wear blue now!")
        chatNpc2(happy, "All thanks to you!")
        chatNpc(happy, "Blue armor best armor!")
    }

    private suspend fun Dialogue.chatNpc2(
        mesanim: org.rsmod.game.type.mesanim.UnpackedMesAnimType,
        text: String,
    ) {
        val currentNpc = npc ?: return
        val other =
            if (currentNpc.type == goblin_diplomacy_npcs.general_bentnoze) {
                goblin_diplomacy_npcs.general_wartface
            } else {
                goblin_diplomacy_npcs.general_bentnoze
            }
        chatNpcSpecific(other.internalName ?: "General", other, mesanim, text)
    }

    // ---- Quest Completion ----
    private suspend fun Dialogue.completeQuest() {
        access.setQuestStage(QuestList.goblin_diplomacy, 3)

        // Give 200 crafting XP
        access.statAdvance(BaseStats.crafting, 200.0)

        // Give gold bar reward
        val gaveBar = player.invAdd(player.inv, goblin_diplomacy_objs.gold_bar, 1).success
        if (!gaveBar) {
            player.invAddOrDrop(objRepo, goblin_diplomacy_objs.gold_bar, 1)
        }

        access.showCompletionScroll(
            quest = QuestList.goblin_diplomacy,
            rewards = listOf("5 Quest Points", "200 Crafting XP", "1 Gold Bar"),
            itemModel = goblin_diplomacy_objs.goblin_armour_darkblue,
            questPoints = 5,
        )
    }

    // ---- Orange Dye Crafting ----
    private suspend fun ProtectedAccess.makeOrangeDye() {
        if (
            !inv.contains(goblin_diplomacy_objs.reddye) ||
                !inv.contains(goblin_diplomacy_objs.yellowdye)
        ) {
            mes("You need both red dye and yellow dye to do this.")
            return
        }

        invDel(inv, goblin_diplomacy_objs.reddye, 1)
        invDel(inv, goblin_diplomacy_objs.yellowdye, 1)
        val added = invAdd(inv, goblin_diplomacy_objs.orangedye, 1).success
        if (!added) {
            player.invAddOrDrop(objRepo, goblin_diplomacy_objs.orangedye, 1)
        }
        mes("You mix the red and yellow dyes to make orange dye.")
    }

    // ---- Dye Goblin Mail Orange ----
    private suspend fun ProtectedAccess.dyeGoblinMailOrange() {
        if (
            !inv.contains(goblin_diplomacy_objs.goblin_armour) ||
                !inv.contains(goblin_diplomacy_objs.orangedye)
        ) {
            mes("You need goblin mail and orange dye to do this.")
            return
        }

        invDel(inv, goblin_diplomacy_objs.goblin_armour, 1)
        invDel(inv, goblin_diplomacy_objs.orangedye, 1)
        val added = invAdd(inv, goblin_diplomacy_objs.goblin_armour_orange, 1).success
        if (!added) {
            player.invAddOrDrop(objRepo, goblin_diplomacy_objs.goblin_armour_orange, 1)
        }
        mes("You dye the goblin mail orange.")
    }

    // ---- Dye Goblin Mail Blue ----
    private suspend fun ProtectedAccess.dyeGoblinMailBlue() {
        if (
            !inv.contains(goblin_diplomacy_objs.goblin_armour) ||
                !inv.contains(goblin_diplomacy_objs.bluedye)
        ) {
            mes("You need goblin mail and blue dye to do this.")
            return
        }

        invDel(inv, goblin_diplomacy_objs.goblin_armour, 1)
        invDel(inv, goblin_diplomacy_objs.bluedye, 1)
        val added = invAdd(inv, goblin_diplomacy_objs.goblin_armour_darkblue, 1).success
        if (!added) {
            player.invAddOrDrop(objRepo, goblin_diplomacy_objs.goblin_armour_darkblue, 1)
        }
        mes("You dye the goblin mail blue.")
    }
}
