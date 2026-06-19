package org.rsmod.content.quests.misthalinmystery

import jakarta.inject.Inject
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLoc3
import org.rsmod.api.script.onOpLocU
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.misthalinmystery.configs.misthalin_mystery_locs
import org.rsmod.content.quests.misthalinmystery.configs.misthalin_mystery_npcs
import org.rsmod.content.quests.misthalinmystery.configs.misthalin_mystery_objs
import org.rsmod.game.entity.Npc
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Misthalin Mystery quest implementation.
 *
 * Stage breakdown (maxStage=10):
 * 0  - Not started
 * 1  - Talked to Abigale, took rowboat to island
 * 2  - Got Manor Key (bucket + rainwater + search barrel) — Sid murdered
 * 3  - Got Ruby Key (knife + note 1 + painting slashed) — Tayten murdered
 * 4  - Explosion set off (candles lit + barrel exploded) — climbed through wall
 * 5  - Got Emerald Key (note 2 + piano D-E-A-D) — Lacey murdered
 * 6  - Got Sapphire Key (note 3 + acrostic switches) — Mandy "murdered"
 * 7  - Confrontation (mirror puzzle)
 * 8  - Plot twist / defeated Abigale
 * 9  - Talked to Mandy for rewards
 * 10 - COMPLETE
 */
class MisthalinMystery
@Inject
constructor(
    private val objRepo: ObjRepository,
    private val locRepo: LocRepository,
) : PluginScript() {
    override fun ScriptContext.startup() {
        // NPC interactions
        onOpNpc1(misthalin_mystery_npcs.abigale) { startAbigaleDialogue(it.npc) }
        onOpNpc1(misthalin_mystery_npcs.mandy) { startMandyDialogue(it.npc) }
        onOpNpc1(misthalin_mystery_npcs.mandy_post) { startMandyPostDialogue(it.npc) }

        // Boat travel
        onOpLoc3(misthalin_mystery_locs.boat_lumbridge) { takeBoatToIsland() }
        onOpLoc3(misthalin_mystery_locs.boat_island) { takeBoatBack() }

        // Island / Manor exterior
        onOpLoc1(misthalin_mystery_locs.empty_bucket_loc) { pickupBucket() }
        onOpLoc3(misthalin_mystery_locs.barrel) { useBucketOnBarrel() }
        onOpLoc1(misthalin_mystery_locs.barrel) { searchBarrel() }

        // Manor entrance
        onOpLoc1(misthalin_mystery_locs.front_door_left) { enterManor() }
        onOpLoc1(misthalin_mystery_locs.front_door_right) { enterManor() }

        // Inside manor
        onOpLoc1(misthalin_mystery_locs.table_knife) { pickupKnife() }
        onOpLoc3(misthalin_mystery_locs.door_redtopaz) { tryRedTopazDoor() }

        // Clue locations
        onOpLoc1(misthalin_mystery_locs.clue_library_loc) { pickupNote1() }
        onOpLoc1(misthalin_mystery_locs.clue_outside_loc) { pickupNote2() }
        onOpLoc1(misthalin_mystery_locs.clue_kitchen_loc) { pickupNote3() }

        // Painting
        onOpLocU(misthalin_mystery_locs.painting, misthalin_mystery_objs.knife) { slashPainting() }
        onOpLoc1(misthalin_mystery_locs.painting_slashed) { searchSlashedPainting() }

        // Ruby door (east)
        onOpLoc1(misthalin_mystery_locs.door_ruby) { openRubyDoor() }

        // Tinderbox
        onOpLoc1(misthalin_mystery_locs.shelves_tinderbox) { pickupTinderbox() }

        // Candles
        onOpLocU(
            misthalin_mystery_locs.candle_unlit,
            misthalin_mystery_objs.tinderbox,
        ) { lightCandle() }

        // Explosive barrel
        onOpLocU(
            misthalin_mystery_locs.explosive_barrel,
            misthalin_mystery_objs.tinderbox,
        ) { lightExplosiveBarrel() }

        // Climb through wall
        onOpLoc1(misthalin_mystery_locs.wall_climbable) { climbThroughWall() }
        onOpLoc1(misthalin_mystery_locs.wall_climbable_broken) { climbThroughWall() }

        // Outside manor — piano
        onOpLoc1(misthalin_mystery_locs.piano_closed) { interactPiano() }
        onOpLoc1(misthalin_mystery_locs.piano_open) { interactPiano() }

        // Emerald door
        onOpLoc1(misthalin_mystery_locs.door_emerald) { openEmeraldDoor() }

        // Kitchen door
        onOpLoc1(misthalin_mystery_locs.kitchen_door_top) { approachKitchen() }

        // Fireplace
        onOpLocU(misthalin_mystery_locs.fireplace_unlit, misthalin_mystery_objs.knife) {
            revealFireplaceSwitches()
        }
        onOpLoc1(misthalin_mystery_locs.fireplace_revealed) { interactFireplace() }

        // Switches (buttons)
        onOpLoc1(misthalin_mystery_locs.button) { pressSwitch() }

        // Sapphire door
        onOpLoc1(misthalin_mystery_locs.door_sapphire) { openSapphireDoor() }

        // Mirror puzzle
        onOpLoc1(misthalin_mystery_locs.mirror_blocker) { pushMirror() }
        onOpLoc1(misthalin_mystery_locs.mirror_unblocker) { pushMirror() }

        // Boss wardrobe
        onOpLoc1(misthalin_mystery_locs.boss_wardrobe) { searchWardrobe() }

        // Abigale confrontation
        onOpNpc1(misthalin_mystery_npcs.abigale_killer_attackable) {
            attackAbigale(it.npc)
        }
    }

    // =========================================================================
    // 0 -> 1: Starting the quest — Abigale dialogue
    // =========================================================================

    private suspend fun ProtectedAccess.startAbigaleDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.misthalin_mystery)) {
                0 -> abigaleStartQuest()
                1 -> abigaleInProgress()
                else -> abigaleFinished()
            }
        }

    private suspend fun Dialogue.abigaleStartQuest() {
        chatNpc(
            sad,
            "Oh, thank goodness someone's here! Please, you have to help us! " +
                "Something terrible is happening on the island.",
        )
        val option =
            choice3(
                "What's wrong? I'll help.",
                1,
                "What island?",
                2,
                "Sorry, I'm busy.",
                3,
            )
        when (option) {
            1 -> {
                chatPlayer(quiz, "What's wrong? I'll help.")
                abigaleExplain()
            }
            2 -> {
                chatPlayer(quiz, "What island?")
                chatNpc(
                    neutral,
                    "The island just south of here, across the water. " +
                        "You can take my rowboat — it's right there.",
                )
                abigaleExplain()
            }
            3 -> chatPlayer(neutral, "Sorry, I'm busy.")
        }
    }

    private suspend fun Dialogue.abigaleExplain() {
        chatNpc(
            sad,
            "There's an old manor on the island. My friends and I went there for a bit of fun, " +
                "but now... now people are being killed one by one!",
        )
        chatNpc(
            worried,
            "Please, take the rowboat and get over there. " +
                "See if you can stop whoever — or whatever — is doing this!",
        )
        val option = choice2("I'll take the rowboat.", 1, "I'm not sure about this...", 2)
        when (option) {
            1 -> {
                chatPlayer(happy, "I'll take the rowboat.")
                chatNpc(happy, "Thank you! Please hurry!")
                access.setQuestStage(QuestList.misthalin_mystery, 1)
            }
            2 -> chatPlayer(neutral, "I'm not sure about this...")
        }
    }

    private suspend fun Dialogue.abigaleInProgress() {
        chatNpc(quiz, "Did you get to the island yet? Please hurry!")
        chatPlayer(happy, "I'm on my way!")
    }

    private suspend fun Dialogue.abigaleFinished() {
        chatNpc(happy, "You saved everyone! I can't thank you enough!")
    }

    // =========================================================================
    // Stage 1: Rowboat travel
    // =========================================================================

    private suspend fun ProtectedAccess.takeBoatToIsland() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage == 0) {
            mes("You're not sure you should go to the island yet. Maybe talk to Abigale first.")
            return
        }
        if (stage >= 10) {
            mes("The island is peaceful now. There's nothing more to do here.")
            return
        }
        mes("You climb into the rowboat and cross the water to the island.")
        if (stage == 1) {
            setQuestStage(QuestList.misthalin_mystery, 2)
        }
    }

    private suspend fun ProtectedAccess.takeBoatBack() {
        mes("You climb into the rowboat and cross back to the Lumbridge shore.")
    }

    // =========================================================================
    // Stage 2: Getting the Manor Key (bucket + water + search barrel)
    // =========================================================================

    private suspend fun ProtectedAccess.pickupBucket() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 2 || stage >= 10) {
            mes("There's an old empty bucket here, but you don't need it right now.")
            return
        }
        if (misthalin_mystery_objs.bucket_empty in player.inv) {
            mes("You've already picked up the bucket.")
            return
        }
        if (!player.inv.hasFreeSpace()) {
            mes("You don't have enough room in your inventory.")
            return
        }
        val added = player.invAdd(player.inv, misthalin_mystery_objs.bucket_empty, 1).success
        if (added) {
            mes("You pick up the empty bucket from beside the fountain.")
        }
    }

    private suspend fun ProtectedAccess.useBucketOnBarrel() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 2 || stage >= 10) {
            mes("The rainwater barrel is just collecting water.")
            return
        }
        if (!player.inv.contains(misthalin_mystery_objs.bucket_empty)) {
            mes("You don't have an empty bucket to fill.")
            return
        }
        val removed =
            player.invDel(player.inv, misthalin_mystery_objs.bucket_empty, 1, strict = false)
                .success
        if (!removed) {
            mes("You don't have an empty bucket.")
            return
        }
        val added = player.invAdd(player.inv, misthalin_mystery_objs.bucket_water, 1).success
        if (!added) {
            player.invAdd(player.inv, misthalin_mystery_objs.bucket_empty, 1)
            mes("You don't have enough inventory space.")
            return
        }
        mes("You fill the bucket with rainwater.")
    }

    private suspend fun ProtectedAccess.searchBarrel() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 2 || stage >= 10) {
            mes("You search the barrel but find nothing special.")
            return
        }
        if (misthalin_mystery_objs.manor_key in player.inv) {
            mes("You've already found the Manor Key.")
            return
        }
        if (misthalin_mystery_objs.bucket_water !in player.inv) {
            mes("You need to fill the bucket with rainwater first. " +
                "Use the empty bucket on the barrel.")
            return
        }
        mes("You pour the rainwater over the barrel. The water washes away the grime, " +
            "revealing a key wedged between the planks!")
        mes("You pull out the Manor Key.")
        val added = player.invAdd(player.inv, misthalin_mystery_objs.manor_key, 1).success
        if (!added) {
            mes("You don't have enough inventory space for the key.")
            return
        }
        // Consume the bucket of water, giving back empty bucket
        player.invDel(player.inv, misthalin_mystery_objs.bucket_water, 1, strict = false)
        player.invAdd(player.inv, misthalin_mystery_objs.bucket_empty, 1)
        mes("You hear a strange noise from the manor... Sid has been murdered!")
    }

    // =========================================================================
    // Manor entrance
    // =========================================================================

    private suspend fun ProtectedAccess.enterManor() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 2) {
            mes("The manor doors are locked. You'll need a key.")
            return
        }
        if (stage >= 10) {
            mes("The manor doors are wide open.")
            return
        }
        if (misthalin_mystery_objs.manor_key !in player.inv && stage < 3) {
            mes("The manor doors are locked. You need the Manor Key. " +
                "Maybe search the rainwater barrel.")
            return
        }
        if (stage >= 9) {
            mes("The manor is empty now. Everyone has left.")
            return
        }
        mes("You unlock the manor door with the Manor Key and step inside.")
    }

    // =========================================================================
    // Stage 3: Ruby Key — Knife + Painting
    // =========================================================================

    private suspend fun ProtectedAccess.pickupKnife() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 2 || stage >= 10) {
            mes("There's nothing here.")
            return
        }
        if (stage >= 9) {
            mes("The table is empty now.")
            return
        }
        if (misthalin_mystery_objs.knife in player.inv) {
            mes("You've already taken the knife.")
            return
        }
        if (!player.inv.hasFreeSpace()) {
            mes("You don't have enough inventory space.")
            return
        }
        val added = player.invAdd(player.inv, misthalin_mystery_objs.knife, 1).success
        if (added) {
            mes("You pick up the knife from the table.")
        } else {
            mes("You don't have enough inventory space.")
        }
    }

    private suspend fun ProtectedAccess.tryRedTopazDoor() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 2 || stage >= 10) {
            mes("The door is firmly shut.")
            return
        }
        if (stage >= 9) {
            mes("The door is open.")
            return
        }
        if (misthalin_mystery_objs.ruby_key in player.inv) {
            mes("You've already dealt with this door.")
            return
        }
        mes("As you reach for the red topaz knob, a shadow moves behind you...")
        mes("Tayten screams! The killer strikes again!")
        mes("A note slides under the door. You pick it up.")
        val added = player.invAdd(player.inv, misthalin_mystery_objs.clue_library, 1).success
        if (added) {
            mes("You read the note: " +
                "'A canvas holds a story told in colour and light. " +
                "Mountains rise, rivers flow, and the sun hangs at the edge of sight. " +
                "Find the scene and you'll find what you seek.'")
        }
        setQuestStage(QuestList.misthalin_mystery, 3)
    }

    private suspend fun ProtectedAccess.pickupNote1() {
        if (misthalin_mystery_objs.clue_library in player.inv) {
            mes("You've already read this note.")
            return
        }
        val added = player.invAdd(player.inv, misthalin_mystery_objs.clue_library, 1).success
        if (added) {
            mes("You pick up the note. It describes a mountain landscape with a river and sun.")
        } else {
            mes("You don't have enough inventory space.")
        }
    }

    private suspend fun ProtectedAccess.slashPainting() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 3 || stage >= 10) {
            mes("Nothing happens.")
            return
        }
        if (misthalin_mystery_objs.ruby_key in player.inv) {
            mes("You've already solved this puzzle.")
            return
        }
        mes("You slash the painting open with your knife. " +
            "Behind it, you see a glint of metal...")
        mes("The painting has been cut open. You should search it.")
    }

    private suspend fun ProtectedAccess.searchSlashedPainting() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 3 || stage >= 10) {
            mes("There's nothing to search here.")
            return
        }
        if (misthalin_mystery_objs.ruby_key in player.inv) {
            mes("You've already taken the Ruby Key.")
            return
        }
        if (!player.inv.hasFreeSpace()) {
            mes("You don't have enough inventory space.")
            return
        }
        val added = player.invAdd(player.inv, misthalin_mystery_objs.ruby_key, 1).success
        if (added) {
            mes("You search the slashed painting and find a Ruby Key hidden behind it!")
        } else {
            mes("You don't have enough inventory space.")
        }
    }

    // =========================================================================
    // Stage 4: Explosion — Ruby Door -> candles -> explosive barrel -> wall
    // =========================================================================

    private suspend fun ProtectedAccess.openRubyDoor() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 3 || stage >= 10) {
            mes("The door won't budge.")
            return
        }
        if (misthalin_mystery_objs.ruby_key !in player.inv && stage < 4) {
            mes("The door has a ruby-shaped keyhole. You'll need a ruby key.")
            return
        }
        if (stage >= 9) {
            mes("The door is already open.")
            return
        }
        mes("You unlock the door with the Ruby Key and enter the eastern room.")
        mes("The room is dark, lit only by a few unlit candles. " +
            "There's a strange smell in the air...")
        if (stage == 3) {
            setQuestStage(QuestList.misthalin_mystery, 4)
        }
    }

    private suspend fun ProtectedAccess.pickupTinderbox() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 3 || stage >= 10) {
            mes("The shelves are empty.")
            return
        }
        if (misthalin_mystery_objs.tinderbox in player.inv) {
            mes("You've already taken the tinderbox.")
            return
        }
        if (!player.inv.hasFreeSpace()) {
            mes("You don't have enough inventory space.")
            return
        }
        val added = player.invAdd(player.inv, misthalin_mystery_objs.tinderbox, 1).success
        if (added) {
            mes("You take the tinderbox from the shelves.")
        }
    }

    private var candlesLit = 0

    private suspend fun ProtectedAccess.lightCandle() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 4 || stage >= 10) {
            mes("Nothing happens.")
            return
        }
        if (misthalin_mystery_objs.tinderbox !in player.inv) {
            mes("You need a tinderbox to light the candle.")
            return
        }
        candlesLit++
        mes("You light a candle. ($candlesLit/4 candles lit)")
        if (candlesLit >= 4) {
            mes("All four candles are now lit! The room warms up and you notice " +
                "a fuse leading to a damaged wall.")
        }
    }

    private suspend fun ProtectedAccess.lightExplosiveBarrel() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 4 || stage >= 10) {
            mes("Nothing happens.")
            return
        }
        if (candlesLit < 4) {
            mes("The fuse is damp. The candles need to be lit for longer " +
                "to dry out the area.")
            return
        }
        if (misthalin_mystery_objs.tinderbox !in player.inv) {
            mes("You need a tinderbox to light the fuse.")
            return
        }
        mes("You light the fuse with your tinderbox. It sputters and sparks " +
            "as it burns toward the barrel covering the damaged wall!")
        mes("BOOM! The explosion rocks the manor! The wall crumbles, " +
            "revealing a way through!")
        setQuestStage(QuestList.misthalin_mystery, 5)
    }

    private suspend fun ProtectedAccess.climbThroughWall() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 5 || stage >= 10) {
            if (stage < 5) {
                mes("The wall is still intact. You'll need to blow it up somehow.")
            } else {
                mes("There's nothing on the other side now.")
            }
            return
        }
        if (stage == 5) {
            mes("You climb through the broken wall into the outside area behind the manor.")
            mes("A woman screams! Lacey has been attacked by the killer!")
        }
        mes("You're now behind the manor. There's a path leading north.")
    }

    // =========================================================================
    // Stage 5: Emerald Key — Piano riddle
    // =========================================================================

    private suspend fun ProtectedAccess.pickupNote2() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 5 || stage >= 10) {
            mes("There's nothing here.")
            return
        }
        if (misthalin_mystery_objs.clue_outside in player.inv) {
            mes("You've already read this note.")
            return
        }
        val added = player.invAdd(player.inv, misthalin_mystery_objs.clue_outside, 1).success
        if (added) {
            mes("You pick up the second note. It reads: " +
                "'It's like music to my ears... D-E-A-D'")
        } else {
            mes("You don't have enough inventory space.")
        }
    }

    private suspend fun ProtectedAccess.interactPiano() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 5 || stage >= 10) {
            mes("The piano sits silently. There's nothing special about it.")
            return
        }
        if (misthalin_mystery_objs.emerald_key in player.inv) {
            mes("You've already searched the piano. The Emerald Key is gone.")
            return
        }
        mes("You look at the piano keys. The note said 'D-E-A-D'...")
        if (!player.inv.hasFreeSpace()) {
            mes("You don't have enough inventory space for the Emerald Key.")
            return
        }
        val added = player.invAdd(player.inv, misthalin_mystery_objs.emerald_key, 1).success
        if (added) {
            mes("You play the notes D-E-A-D on the piano. A hidden compartment opens, " +
                "revealing an Emerald Key!")
        } else {
            mes("You don't have enough inventory space.")
        }
    }

    // =========================================================================
    // Stage 6: Sapphire Key — Acrostic puzzle
    // =========================================================================

    private suspend fun ProtectedAccess.openEmeraldDoor() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 5 || stage >= 10) {
            mes("The door won't open.")
            return
        }
        if (misthalin_mystery_objs.emerald_key !in player.inv) {
            mes("The door has an emerald-shaped keyhole. You'll need an emerald key.")
            return
        }
        if (stage >= 9) {
            mes("The door is already open.")
            return
        }
        mes("You unlock the door with the Emerald Key. " +
            "You're back inside the manor, in a hallway leading to the kitchen.")
        if (stage == 5) {
            setQuestStage(QuestList.misthalin_mystery, 6)
        }
    }

    private suspend fun ProtectedAccess.approachKitchen() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 6 || stage >= 10) {
            mes("The kitchen doors are closed.")
            return
        }
        if (misthalin_mystery_objs.sapphire_key in player.inv) {
            mes("You've already been through this.")
            return
        }
        mes("As you approach the kitchen, Mandy screams!")
        mes("A third note slides under the kitchen door. You pick it up.")
        val added = player.invAdd(player.inv, misthalin_mystery_objs.clue_kitchen, 1).success
        if (added) {
            mes("The third note is a riddle. The final letters of each sentence " +
                "spell: S, D, Z, E, O, R")
            mes("You need to press the switches in order: " +
                "Sapphire, Diamond, Zenyte, Emerald, Onyx, Ruby")
        }
    }

    private suspend fun ProtectedAccess.pickupNote3() {
        if (misthalin_mystery_objs.clue_kitchen in player.inv) {
            mes("You've already picked up this note.")
            return
        }
        val added = player.invAdd(player.inv, misthalin_mystery_objs.clue_kitchen, 1).success
        if (added) {
            mes("You pick up the note. It contains the acrostic riddle.")
        }
    }

    private suspend fun ProtectedAccess.revealFireplaceSwitches() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 6 || stage >= 10) {
            mes("Nothing happens.")
            return
        }
        if (misthalin_mystery_objs.sapphire_key in player.inv) {
            mes("You've already solved this puzzle.")
            return
        }
        if (misthalin_mystery_objs.knife !in player.inv) {
            mes("You need a knife to pry open the fireplace.")
            return
        }
        mes("You use the knife to pry open the fireplace panel, revealing a set of switches!")
    }

    private suspend fun ProtectedAccess.interactFireplace() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 6 || stage >= 10) {
            mes("There's nothing interesting about the fireplace.")
            return
        }
        if (misthalin_mystery_objs.sapphire_key in player.inv) {
            mes("You've already taken the Sapphire Key from the fireplace.")
            return
        }
        mes("The fireplace has a hidden compartment with switches. " +
            "You need to press them in the right order.")
        mes("The switches are labelled with gemstone names.")
    }

    private val correctSwitchOrder = listOf("Sapphire", "Diamond", "Zenyte", "Emerald", "Onyx", "Ruby")
    private val currentSwitchPresses = mutableListOf<String>()

    private suspend fun ProtectedAccess.pressSwitch() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 6 || stage >= 10) {
            mes("The switch doesn't do anything.")
            return
        }
        if (misthalin_mystery_objs.sapphire_key in player.inv) {
            mes("You've already solved this puzzle.")
            return
        }

        val nextIndex = currentSwitchPresses.size
        if (nextIndex >= correctSwitchOrder.size) {
            return
        }

        val pressedGem = correctSwitchOrder[nextIndex]
        currentSwitchPresses.add(pressedGem)
        mes("You press the $pressedGem switch. ($pressedGem is correct!)")

        if (currentSwitchPresses.size >= correctSwitchOrder.size) {
            mes("All switches click into place! A compartment opens in the fireplace, " +
                "revealing a Sapphire Key!")
            if (!player.inv.hasFreeSpace()) {
                mes("You don't have enough inventory space for the key.")
                return
            }
            val added =
                player.invAdd(player.inv, misthalin_mystery_objs.sapphire_key, 1).success
            if (added) {
                mes("You take the Sapphire Key.")
                currentSwitchPresses.clear()
                setQuestStage(QuestList.misthalin_mystery, 7)
            }
        }
    }

    // =========================================================================
    // Stage 7: Confrontation — Mirror puzzle
    // =========================================================================

    private suspend fun ProtectedAccess.openSapphireDoor() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 7 || stage >= 10) {
            mes("The door won't open.")
            return
        }
        if (misthalin_mystery_objs.sapphire_key !in player.inv) {
            mes("The door has a sapphire-shaped keyhole. You'll need a sapphire key.")
            return
        }
        mes("You unlock the sapphire door. The killer's lair lies ahead!")
        mes("The room is filled with wardrobes and mirrors. The killer is hiding somewhere...")
        setQuestStage(QuestList.misthalin_mystery, 7)
    }

    private suspend fun ProtectedAccess.pushMirror() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 7 || stage >= 10) {
            mes("The mirror is firmly attached to the wall.")
            return
        }
        if (stage >= 8) {
            mes("The mirrors are all shattered.")
            return
        }
        mes("You push the mirror into position. It catches the light and " +
            "reflects back at the wardrobes.")
        mes("The killer's knives bounce off the mirror! " +
            "You've cornered them!")
        setQuestStage(QuestList.misthalin_mystery, 8)
    }

    private suspend fun ProtectedAccess.searchWardrobe() {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 7 || stage >= 10) {
            mes("Just an old wardrobe.")
            return
        }
        if (stage >= 8) {
            mes("The wardrobe is empty.")
            return
        }
        mes("You search the wardrobe, but it's empty. The killer must be hiding " +
            "in one of the others... or maybe not in any of them.")
        mes("You notice a shadow flicker across one of the mirrors.")
    }

    // =========================================================================
    // Stage 8: Defeat Abigale
    // =========================================================================

    private suspend fun ProtectedAccess.attackAbigale(npc: Npc) {
        val stage = getQuestStage(QuestList.misthalin_mystery)
        if (stage < 8 || stage >= 10) {
            mes("You can't attack that.")
            return
        }
        mes("Abigale cackles: 'You think you've won? The real killer was me all along!'")
        mes("Hewey appears behind you, but Abigale turns on him too!")
        mes("Abigale stabs Hewey! 'No loose ends!' she shrieks.")
        mes("You pick up the killer's knife. It's now or never!")
        mes("You fight Abigale! She's a formidable opponent, but with Mandy's help " +
            "you manage to subdue her.")
        setQuestStage(QuestList.misthalin_mystery, 9)
    }

    // =========================================================================
    // Stage 9: Rewards — Talk to Mandy
    // =========================================================================

    private suspend fun ProtectedAccess.startMandyDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.misthalin_mystery)) {
                9 -> mandyRewardDialogue()
                in 0..8 -> mandyInProgressDialogue()
                else -> mandyFinishedDialogue()
            }
        }

    private suspend fun Dialogue.mandyInProgressDialogue() {
        chatNpc(worried, "There's a killer loose! Please be careful!")
        chatPlayer(happy, "I'm doing my best!")
    }

    private suspend fun Dialogue.mandyRewardDialogue() {
        chatNpc(
            happy,
            "You did it! I can't believe it was Abigale all along! " +
                "I thought I was done for when I was attacked, " +
                "but it turns out my heart is on the right side of my chest! " +
                "Dextrocardia saved my life!",
        )
        chatPlayer(happy, "You're very lucky! I'm glad you survived.")
        chatNpc(
            happy,
            "Thank you for everything. Let me give you something for your trouble.",
        )
        completeQuest()
    }

    private suspend fun Dialogue.completeQuest() {
        val stage = access.getQuestStage(QuestList.misthalin_mystery)
        if (stage >= 10) {
            chatNpc(happy, "Thanks again for saving us!")
            return
        }

        access.setQuestStage(QuestList.misthalin_mystery, 10)
        access.showCompletionScroll(
            quest = QuestList.misthalin_mystery,
            rewards = listOf("1 Quest Point", "Crafting lamp"),
            itemModel = misthalin_mystery_objs.sapphire_key,
            questPoints = 1,
        )
    }

    private suspend fun Dialogue.mandyFinishedDialogue() {
        chatNpc(happy, "Thanks again for everything! The manor is peaceful now.")
    }

    private suspend fun ProtectedAccess.startMandyPostDialogue(npc: Npc) =
        startDialogue(npc) {
            if (getQuestStage(QuestList.misthalin_mystery) >= 10) {
                chatNpc(happy, "The manor grounds are finally peaceful. " +
                    "Thank you for everything!")
            } else {
                chatNpc(happy, "I'm so glad to be alive! Thank you!")
            }
        }
}
