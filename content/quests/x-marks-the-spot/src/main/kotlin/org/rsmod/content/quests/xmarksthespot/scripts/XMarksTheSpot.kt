package org.rsmod.content.quests.xmarksthespot.scripts

import jakarta.inject.Inject
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpHeld2
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.xmarksthespot.configs.xm_npcs
import org.rsmod.content.quests.xmarksthespot.configs.xm_objs
import org.rsmod.content.quests.xmarksthespot.configs.xm_seqs
import org.rsmod.content.quests.xmarksthespot.configs.xm_varbits
import org.rsmod.game.entity.Npc
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class XMarksTheSpot
@Inject
constructor(
    private val objRepo: ObjRepository,
) : PluginScript() {

    override fun ScriptContext.startup() {
        onOpNpc1(xm_npcs.veos_lumbridge) { veosLumbridgeDialogue(it.npc) }
        onOpNpc1(xm_npcs.veos_sarim) { veosSarimDialogue(it.npc) }
        onOpHeld2(xm_objs.spade) { digForTreasure() }
        onOpHeld2(xm_objs.mysterious_orb) { feelOrb() }
    }

    private val ProtectedAccess.questStage: Int
        get() = vars[xm_varbits.quest_progress]

    private fun ProtectedAccess.setQuestStage(stage: Int) {
        vars[xm_varbits.quest_progress] = stage
    }

    private val ProtectedAccess.hasAncientCasket: Boolean
        get() = xm_objs.ancient_casket in inv

    private suspend fun ProtectedAccess.veosLumbridgeDialogue(npc: Npc) {
        when (questStage) {
            0 -> startQuest(npc)
            1, 2, 3, 4 -> {
                chatNpc(npc, null, "Have you found the treasure yet? Follow the clues on that map!")
                chatPlayer(null, "I'm working on it! The riddles are tricky!")
            }
            5 -> {
                chatPlayer(null, "I've found the Ancient Casket! Now where do I take it?")
                chatNpc(npc, null, "Take it to Veos at the northern dock in Port Sarim. He'll know what to do with it!")
            }
            6 -> {
                chatNpc(npc, null, "Ah, the treasure hunter! You've already completed that quest! Well done!")
            }
        }
    }

    private suspend fun ProtectedAccess.startQuest(npc: Npc) {
        chatNpc(npc, null, "Hello there, adventurer! You look like someone who's not afraid of a bit of treasure hunting!")
        chatNpc(npc, null, "I'm Veos, a merchant and explorer. I've come across an old treasure map, but I'm too old for such adventures now.")
        chatNpc(npc, null, "How would you like to follow this map and see what treasure lies at the end of it?")

        val choice = simChoice2(
            "Sure, I'd love to go on a treasure hunt!",
            "Sounds dangerous. No thanks.",
        )
        if (choice != 1) {
            chatNpc(npc, null, "Ah, that's a shame. If you change your mind, I'll be here.")
            return
        }

        chatNpc(npc, null, "Excellent! Here's the treasure map. It has a series of clues that will lead you to a great treasure!")
        chatNpc(npc, null, "You'll need a spade to dig up the treasure. You can buy one from the general store if you don't have one.")
        invAddOrDrop(objRepo, xm_objs.treasure_map, 1)
        setQuestStage(1)
        mes("<col=0000ff>You have started X Marks the Spot!</col>")
    }

    private suspend fun ProtectedAccess.veosSarimDialogue(npc: Npc) {
        when (questStage) {
            0, 1, 2, 3, 4 -> {
                chatNpc(npc, null, "I'm Veos, an explorer. I hear there's treasure to be found in Misthalin. Have you checked in Lumbridge?")
            }
            5 -> {
                if (hasAncientCasket) {
                    completeQuest(npc)
                } else {
                    chatNpc(npc, null, "You decoded the cipher? Good work! The final treasure should be in that pig pen. Keep digging!")
                    chatPlayer(null, "I must have lost the casket somewhere...")
                }
            }
            6 -> {
                chatNpc(npc, null, "Ah, the treasure hunter! You've already completed that quest. Thank you!")
            }
        }
    }

    private suspend fun ProtectedAccess.completeQuest(npc: Npc) {
        chatNpc(npc, null, "You found it! The Ancient Casket! I never thought I'd see it again!")
        chatNpc(npc, null, "This casket has been in my family for generations. It was lost in Misthalin during one of my expeditions. Thank you for returning it!")
        chatNpc(npc, null, "Here's your reward - you've earned it!")
        invDel(player.inv, xm_objs.ancient_casket, 1)
        invAddOrDrop(objRepo, xm_objs.coins, 200)
        invAddOrDrop(objRepo, xm_objs.antique_lamp, 1)
        invAddOrDrop(objRepo, xm_objs.scroll_box_beginner, 1)
        setQuestStage(6)
        mes("<col=ff0000>Congratulations! You've completed X Marks the Spot!</col>")
        mes("You are awarded:")
        mes("1 Quest Point")
        mes("200 Coins")
        mes("An antique lamp worth 300 XP in any skill of your choice")
        mes("A beginner scroll box")
        chatNpc(npc, null, "If you ever want to go on another adventure, you know where to find me!")
        chatPlayer(null, "Thank you, Veos! That was quite an adventure!")
    }

    private suspend fun ProtectedAccess.digForTreasure() {
        val stage = questStage
        if (stage == 0 || stage == 6) {
            mes("You dig a hole in the ground. Nothing interesting here.")
            return
        }
        anim(xm_seqs.dig)
        delay(2)
        when (stage) {
            1 -> digStage1()
            2 -> digStage2()
            3 -> digStage3()
            4 -> digStage4()
            5 -> {
                if (hasAncientCasket) {
                    mes("You already have the Ancient Casket. Take it to Veos at the Port Sarim dock!")
                } else {
                    mes("You dig a hole but find nothing. The real treasure was already found nearby.")
                }
            }
        }
    }

    private suspend fun ProtectedAccess.digStage1() {
        if (isAtBobPathDigSpot()) {
            mes("You dig in the soft earth and find a piece of a treasure map!")
            invAddOrDrop(objRepo, xm_objs.treasure_map_second, 1)
            setQuestStage(2)
            mes("<col=0000ff>You've solved the first clue! The map reveals a second clue pointing behind Lumbridge Castle.</col>")
        } else {
            mes("You dig a hole but find nothing. The clue mentioned Bob's path in Lumbridge...")
        }
    }

    private suspend fun ProtectedAccess.digStage2() {
        if (isAtCastleDigSpot()) {
            mes("You dig behind the castle and uncover a Mysterious Orb!")
            invAddOrDrop(objRepo, xm_objs.mysterious_orb, 1)
            setQuestStage(3)
            mes("<col=0000ff>You've solved the second clue! The orb feels warm and seems to be pointing toward Draynor Village...</col>")
        } else {
            mes("You dig a hole but find nothing. You should try behind Lumbridge Castle, near the kitchen.")
        }
    }

    private suspend fun ProtectedAccess.digStage3() {
        if (isAtDraynorWheatDigSpot()) {
            mes("You dig near the wheat field and find a tattered note with strange writing on it!")
            mes("The note reads: 'ESBZOPS QJH QFO'")
            mes("It looks like some kind of cipher...")
            invAddOrDrop(objRepo, xm_objs.treasure_map_second, 1)
            setQuestStage(4)
            mes("<col=0000ff>You've solved the third clue! The cipher needs to be decoded. Each letter is shifted back by one in the alphabet...</col>")
        } else {
            val dist = distanceTo(player.coords, DIG_SPOT_3)
            val msg = when {
                dist <= 2 -> "The orb pulses warmly in your pocket! It's very close!"
                dist <= 8 -> "The orb vibrates gently. You're getting warmer."
                dist <= 20 -> "The orb feels slightly warm."
                dist <= 50 -> "The orb feels lukewarm."
                else -> "The orb feels cold."
            }
            mes(msg)
        }
    }

    private suspend fun ProtectedAccess.digStage4() {
        if (isAtPigPenDigSpot()) {
            mes("You dig in the pig pen and uncover an Ancient Casket!")
            mes("As you pick it up, you hear a faint whispering...")
            mes("\"Must have been the wind.\"")
            invAddOrDrop(objRepo, xm_objs.ancient_casket, 1)
            setQuestStage(5)
            mes("<col=0000ff>You've uncovered the Ancient Casket! Take it to Veos at the northern dock in Port Sarim!</col>")
        } else {
            mes("You dig a hole but find nothing. The decoded message said 'DRAYNOR PIG PEN'... maybe try the pig pen in Draynor Village.")
        }
    }

    private suspend fun ProtectedAccess.feelOrb() {
        if (questStage != 3) {
            mes("The mysterious orb doesn't seem to react here.")
            return
        }
        val dist = distanceTo(player.coords, DIG_SPOT_3)
        val msg = when {
            dist <= 2 -> "The orb burns hot! The treasure is very close!"
            dist <= 8 -> "The orb is warm. You're getting closer."
            dist <= 20 -> "The orb feels warm, but not quite close yet."
            dist <= 50 -> "The orb feels lukewarm."
            else -> "The orb feels cold."
        }
        mes(msg)
    }

    private fun ProtectedAccess.isAtBobPathDigSpot(): Boolean {
        val x = player.coords.x
        val z = player.coords.z
        return x in 3202..3204 && z in 3223..3225 && player.coords.level == 0
    }

    private fun ProtectedAccess.isAtCastleDigSpot(): Boolean {
        val x = player.coords.x
        val z = player.coords.z
        return x in 3206..3208 && z in 3211..3213 && player.coords.level == 0
    }

    private fun ProtectedAccess.isAtDraynorWheatDigSpot(): Boolean {
        val x = player.coords.x
        val z = player.coords.z
        return x in 3083..3085 && z in 3251..3253 && player.coords.level == 0
    }

    private fun ProtectedAccess.isAtPigPenDigSpot(): Boolean {
        val x = player.coords.x
        val z = player.coords.z
        return x in 3082..3084 && z in 3254..3256 && player.coords.level == 0
    }

    private fun distanceTo(from: CoordGrid, to: CoordGrid): Int {
        val dx = kotlin.math.abs(from.x - to.x)
        val dy = kotlin.math.abs(from.z - to.z)
        return maxOf(dx, dy)
    }

    private suspend fun ProtectedAccess.simChoice2(
        opt1: String,
        opt2: String,
    ): Int {
        mes("Choose an option:")
        mes("1. $opt1")
        mes("2. $opt2")
        return 1
    }

    companion object {
        private val DIG_SPOT_3 = CoordGrid(3084, 3252, 0)
    }
}
