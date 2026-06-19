package org.rsmod.content.quests.xmarksthespot.scripts

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpObj1
import org.rsmod.content.quests.xmarksthespot.configs.xm_npcs
import org.rsmod.content.quests.xmarksthespot.configs.xm_objs
import org.rsmod.content.quests.xmarksthespot.configs.xm_seqs
import org.rsmod.content.quests.xmarksthespot.configs.xm_varbits
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// =============================================================================
// X Marks the Spot — Novice Quest (Great Kourend #1)
//
// Quest stages (varbit: quest_x_marks_the_spot_progress):
//   0 = Not started
//   1 = Started — has treasure map from Veos (Lumbridge pub)
//   2 = First dig complete — found clue near Bob's Brilliant Axes
//   3 = Second dig complete — got Mysterious Orb behind Lumbridge Castle
//   4 = Third dig complete — found clue near Draynor wheat field
//   5 = Fourth dig complete — has Ancient Casket from Draynor pig pen
//   6 = QUEST COMPLETE — returned casket to Veos at Port Sarim
// =============================================================================

/**
 * Dig site coordinates (OSRS world tile grid, plane 0):
 *   1 — Near Bob's Brilliant Axes (Lumbridge SE) — 3226, 3210
 *   2 — Behind Lumbridge Castle (kitchen door) — 3214, 3219
 *   3 — East of Draynor (wheat field area) — 3092, 3282
 *   4 — Draynor pig pen (centre) — 3087, 3289
 */
private val DIG_SPOT_1 = CoordGrid(3226, 3210, 0)
private val DIG_SPOT_2 = CoordGrid(3214, 3219, 0)
private val DIG_SPOT_3 = CoordGrid(3092, 3282, 0)
private val DIG_SPOT_4 = CoordGrid(3087, 3289, 0)

class XMarksTheSpot
@Inject
constructor(
    private val objRepo: ObjRepository,
) : PluginScript() {

    override fun ScriptContext.startup() {
        // Veos in The Sheared Ram, Lumbridge — quest start
        onOpNpc1(xm_npcs.veos_lumbridge) { veosLumbridgeDialogue(it.npc) }

        // Veos at Port Sarim dock — quest completion
        onOpNpc1(xm_npcs.veos_sarim) { veosSarimDialogue(it.npc) }

        // Spade "Dig" action
        onOpObj1(xm_objs.spade) { handleDig() }

        // Mysterious orb "Feel" action (hot/cold triangulation)
        onOpObj1(xm_objs.mysterious_orb) { handleOrbFeel() }
    }

    // =========================================================================
    // Quest stage helpers
    // =========================================================================

    private val Player.questStage: Int
        get() = varp[xm_varbits.quest_progress]

    private fun Player.setQuestStage(stage: Int) {
        varp[xm_varbits.quest_progress] = stage
    }

    // =========================================================================
    // Veos in Lumbridge — The Sheared Ram (Quest Start + Hints)
    // =========================================================================

    private suspend fun ProtectedAccess.veosLumbridgeDialogue(npc: Npc) {
        when (player.questStage) {
            0 -> startQuest(npc)
            1, 2, 3, 4 -> questHintDialogue(npc)
            5 -> turnInCasket(npc)
            else -> chatNpc(npc, mood = "happy", "Ah, my favourite treasure hunter! Thanks again for your help.")
        }
    }

    private suspend fun ProtectedAccess.startQuest(npc: Npc) {
        chatNpc(npc, mood = "happy", "Hello again adventurer! Can I help you with something?")
        val choice = choice2(
            "I'm looking for a quest.", 1,
            "No, I'm fine thanks.", 2,
        )
        if (choice != 1) return

        chatNpc(
            npc, mood = "happy",
            "Well, I am looking for a treasure! I found a mysterious scroll in Great Kourend " +
                "and it led me here to Lumbridge. I've been studying it, but I'm not as familiar " +
                "with this area as I'd like.",
        )
        chatNpc(
            npc, mood = "happy",
            "Perhaps you could help me find the treasure? " +
                "I'm sure there's something in it for you!",
        )
        val choice2 = choice2(
            "Sounds good, what do I do?", 1,
            "Sorry, I'm busy.", 2,
        )
        if (choice2 != 1) return

        chatNpc(
            npc, mood = "happy",
            "Excellent! Take a look at this treasure scroll.",
        )
        chatNpc(
            npc, mood = "happy",
            "It appears to be a clue of some sort. Follow it and see where it leads!",
        )
        chatNpc(
            npc, mood = "happy",
            "Make sure you have a spade with you. You'll need it to dig up the treasure.",
        )

        if (inv.isFull()) {
            mes("You don't have enough inventory space to hold the treasure scroll.")
            return
        }
        invAddOrDrop(objRepo, xm_objs.treasure_map, count = 1)
        player.setQuestStage(1)
        chatPlayer(mood = "happy", "I'll get right on it!")
    }

    private suspend fun ProtectedAccess.questHintDialogue(npc: Npc) {
        chatNpc(npc, mood = "neutral", "Have you found the treasure yet? Follow the clues on that scroll!")
        when (player.questStage) {
            1 -> chatNpc(
                npc, mood = "neutral",
                "Try looking around Bob's Brilliant Axes in south-east Lumbridge. " +
                    "The clue mentions something about a man named Bob...",
            )
            2 -> chatNpc(
                npc, mood = "neutral",
                "Try behind Lumbridge Castle, by the Cook's kitchen. " +
                    "That's where the second map points to.",
            )
            3 -> chatNpc(
                npc, mood = "neutral",
                "I think the next spot is east of Draynor Village. " +
                    "Use the mysterious orb to home in on it!",
            )
            4 -> chatNpc(
                npc, mood = "neutral",
                "The final clue is a cipher. I believe it points to the pig pen in Draynor. " +
                    "The letters are all shifted by one position.",
            )
        }
    }

    private suspend fun ProtectedAccess.turnInCasket(npc: Npc) {
        if (!inv.contains(xm_objs.ancient_casket)) {
            chatNpc(npc, mood = "neutral", "You need to find the ancient casket first! Keep following those clues.")
            return
        }
        chatPlayer(mood = "happy", "I found this ancient casket during the treasure hunt. Here you go!")
        chatNpc(npc, mood = "happy", "You found it! Well done, adventurer!")
        chatNpc(npc, mood = "neutral", "Let me take that casket off your hands. It's nothing important, really.")
        chatNpc(npc, mood = "neutral", "Just some old Kourend relics. Thank you for your help!")

        invDel(inv, xm_objs.ancient_casket, count = 1, strict = false)

        // Rewards
        invAddOrDrop(objRepo, xm_objs.coins, count = 200)
        if (!inv.contains(xm_objs.antique_lamp)) {
            invAddOrDrop(objRepo, xm_objs.antique_lamp, count = 1)
        }
        if (!inv.contains(xm_objs.scroll_box_beginner)) {
            invAddOrDrop(objRepo, xm_objs.scroll_box_beginner, count = 1)
        }

        player.setQuestStage(6)
        mes("<col=00ff00>Congratulations! You've completed X Marks the Spot!</col>")
        mes("Quest complete! You are awarded 1 Quest Point.")
        mes("Rewards: 200 coins, Antique lamp, Beginner scroll box")
    }

    // =========================================================================
    // Veos in Port Sarim — northernmost dock (Quest Completion)
    // =========================================================================

    private suspend fun ProtectedAccess.veosSarimDialogue(npc: Npc) {
        when (player.questStage) {
            in 0..4 -> {
                chatNpc(
                    npc, mood = "happy",
                    "Hello adventurer! I'm based in The Sheared Ram pub in Lumbridge, " +
                        "if you're looking for me. Come find me there for the treasure hunt!",
                )
            }
            5 -> turnInCasket(npc)
            6 -> {
                chatNpc(
                    npc, mood = "happy",
                    "Ah, my favourite treasure hunter! I've already sent the casket to Kourend. " +
                        "Thank you again for your help!",
                )
            }
        }
    }

    // =========================================================================
    // Dig mechanics — Spade "Dig" action
    // =========================================================================

    private suspend fun ProtectedAccess.handleDig() {
        if (player.questStage == 0) {
            mes("You don't have any reason to dig here.")
            return
        }
        if (player.questStage == 6) {
            mes("You've already completed the treasure hunt. There's nothing more to find.")
            return
        }

        val coords = player.coords

        when (player.questStage) {
            1 -> tryDigSpot1(coords)
            2 -> tryDigSpot2(coords)
            3 -> tryDigSpot3(coords)
            4 -> tryDigSpot4(coords)
            5 -> mes("You already have the ancient casket. Take it to Veos at the Port Sarim docks!")
        }
    }

    private suspend fun ProtectedAccess.tryDigSpot1(coords: CoordGrid) {
        if (coords != DIG_SPOT_1) {
            mes("You dig around but find nothing.")
            return
        }
        anim(xm_seqs.dig)
        mes("You dig a hole and uncover a scroll!")
        invAddOrDrop(objRepo, xm_objs.treasure_map_second, count = 1)
        player.setQuestStage(2)
        mes("<col=7f0000>The clue reads: \"West of the Lumbridge Castle kitchen, " +
            "south-west of the large crate.\"</col>")
    }

    private suspend fun ProtectedAccess.tryDigSpot2(coords: CoordGrid) {
        if (coords != DIG_SPOT_2) {
            mes("You dig around but find nothing.")
            return
        }
        anim(xm_seqs.dig)
        mes("You dig and uncover a mysterious orb!")
        invAddOrDrop(objRepo, xm_objs.mysterious_orb, count = 1)
        player.setQuestStage(3)
        mes("<col=7f0000>The mysterious orb pulses with energy. " +
            "You should try feeling it to find the next spot.</col>")
    }

    private suspend fun ProtectedAccess.tryDigSpot3(coords: CoordGrid) {
        if (coords != DIG_SPOT_3) {
            // Hot/cold feedback when digging at wrong spot in this stage
            val dist = distanceTo(coords, DIG_SPOT_3)
            val msg = when {
                dist <= 2 -> "The orb pulses warmly from your pocket. It's very close!"
                dist <= 8 -> "The orb vibrates gently. You're getting warmer."
                dist <= 20 -> "The orb feels slightly warm."
                dist <= 50 -> "The orb feels lukewarm."
                else -> "The orb feels cold."
            }
            mes(msg)
            return
        }
        anim(xm_seqs.dig)
        mes("You dig and uncover a scroll covered in strange letters!")
        invAddOrDrop(objRepo, xm_objs.treasure_map, count = 1)
        player.setQuestStage(4)
        mes("<col=7f0000>The scroll reads: \"ESBZOPS QJH QFO\"</col>")
        mes("<col=7f0000>The letters seem to be shifted... Perhaps a Caesar cipher?</col>")
    }

    private suspend fun ProtectedAccess.tryDigSpot4(coords: CoordGrid) {
        if (coords != DIG_SPOT_4) {
            mes("You dig around but find nothing.")
            return
        }
        anim(xm_seqs.dig)
        mes("You dig up a heavy ancient casket!")
        mes("As you dig it up, you hear a faint whispering, though you can't make out what it says.")
        mes("Hmmmm... Must have been the wind.")
        invAddOrDrop(objRepo, xm_objs.ancient_casket, count = 1)
        player.setQuestStage(5)
        mes("You found the ancient casket! Return it to Veos at the Port Sarim docks.")
    }

    // =========================================================================
    // Mysterious Orb — "Feel" action (hot/cold hint system)
    // =========================================================================

    private suspend fun ProtectedAccess.handleOrbFeel() {
        if (player.questStage != 3) {
            mes("The mysterious orb doesn't seem to react here.")
            return
        }
        val coords = player.coords
        val dist = distanceTo(coords, DIG_SPOT_3)
        val msg = when {
            dist <= 2 -> "The orb burns hot! The treasure is very close!"
            dist <= 8 -> "The orb is warm. You're getting closer."
            dist <= 20 -> "The orb feels warm, but not quite close yet."
            dist <= 50 -> "The orb feels lukewarm."
            else -> "The orb feels cold."
        }
        mes(msg)
    }

    // =========================================================================
    // Utility helpers
    // =========================================================================

    /** Chebyshev distance between two coordinate tiles */
    private fun distanceTo(from: CoordGrid, to: CoordGrid): Int {
        val dx = kotlin.math.abs(from.x - to.x)
        val dy = kotlin.math.abs(from.y - to.y)
        return maxOf(dx, dy)
    }
}
