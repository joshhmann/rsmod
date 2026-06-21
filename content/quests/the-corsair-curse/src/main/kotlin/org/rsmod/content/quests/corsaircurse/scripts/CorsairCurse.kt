package org.rsmod.content.quests.corsaircurse.scripts

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.corsaircurse.configs.cc_npcs
import org.rsmod.content.quests.corsaircurse.configs.cc_objs
import org.rsmod.content.quests.corsaircurse.configs.cc_varbits
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// =============================================================================
// The Corsair Curse — Free-to-play Quest (Quest #135, 2017)
//
// The player helps Captain Tock investigate why his crew is mysteriously ill.
// The crew suspects a curse, but the player uncovers the truth — Ithoi the
// Navigator has been poisoning them to fake a curse and keep his job.
//
// Quest stages (varbit: quest_the_corsair_curse_progress, ID 6071):
//   0  = Not started
//   1  = Talked to Captain Tock at crossroads, agreed to help
//   2  = Arrived at Corsair Cove
//   3  = Talked to Captain Tock at cove, introduced to situation
//   4  = Talked to Chief Tess and Bugs
//   5  = Inspected the possessed doll
//   6  = Talked to Arsen, Colin, Gnocci (all crew members)
//   7  = Got ogre artefact from Tock (need to investigate all curses)
//   8  = Investigated Arsen's curse (ogre artefact returned to Tess)
//   9  = Investigated Colin's curse (telescope) + Gnocci's curse (dig)
//  10  = Reported findings to Captain Tock
//  11  = Found out Ithoi cooked the meal (talked to Gnocci)
//  12  = Found out Ithoi is about to be fired (talked to Arsen/Francois)
//  13  = Confronted and proved Ithoi is faking
//  14  = Ithoi fled, Captain Tock sends player to kill him
//  15  = Ithoi defeated
//  16  = QUEST COMPLETE
// =============================================================================

class CorsairCurse
@Inject
constructor(
    private val objRepo: ObjRepository,
) : PluginScript() {

    override fun ScriptContext.startup() {
        // === Quest Start ===
        onOpNpc1(cc_npcs.captain_tock_crossroads) { tockCrossroadsDialogue(it.npc) }

        // === Corsair Cove ===
        onOpNpc1(cc_npcs.captain_tock_cove) { tockCoveDialogue(it.npc) }
        onOpNpc1(cc_npcs.chief_tess) { chiefTessDialogue(it.npc) }
        onOpNpc1(cc_npcs.bugs) { bugsDialogue(it.npc) }
        onOpNpc1(cc_npcs.possessed_doll) { possessedDollDialogue(it.npc) }

        // === Crew members (sick variants first, then healthy) ===
        onOpNpc1(cc_npcs.arsen_sick) { arsenSickDialogue(it.npc) }
        onOpNpc1(cc_npcs.arsen) { arsenDialogue(it.npc) }
        onOpNpc1(cc_npcs.colin_sick) { colinSickDialogue(it.npc) }
        onOpNpc1(cc_npcs.colin) { colinDialogue(it.npc) }
        onOpNpc1(cc_npcs.gnocci_sick) { gnocciSickDialogue(it.npc) }
        onOpNpc1(cc_npcs.gnocci) { gnocciDialogue(it.npc) }

        // === Boss fight ===
        onOpNpc1(cc_npcs.ithoi_combat) { ithoiBossDialogue(it.npc) }
    }

    // =========================================================================
    // Quest stage helpers
    // =========================================================================

    private val ProtectedAccess.questStage: Int
        get() = vars[cc_varbits.quest_progress]

    private fun ProtectedAccess.setQuestStage(stage: Int) {
        vars[cc_varbits.quest_progress] = stage
    }

    private val Player.hasOgreArtefact: Boolean
        get() = inv.contains(cc_objs.ogre_artefact)

    // =========================================================================
    // CAPTAIN TOCK — Crossroads (Quest Start)
    // =========================================================================

    private suspend fun ProtectedAccess.tockCrossroadsDialogue(npc: Npc) = startDialogue(npc) {
        when (questStage) {
            0 -> startQuest(npc)
            1 -> chatNpc(happy, "Ready to head to Corsair Cove? I'll be at the dock " +
                "south of Rimmington when you're ready to sail!")
            in 2..15 -> chatNpc(neutral, "How goes the investigation at Corsair Cove? I'll be here if you need to cross.")
            16 -> chatNpc(happy, "You saved my crew! If you ever need to cross to Corsair Cove, just ask!")
        }
    }

    private suspend fun ProtectedAccess.startQuest(npc: Npc) = startDialogue(npc) {
        chatNpc(worried,
            "Arr, adventurer! Thank goodness you're here! My crew — they've been cursed! " +
                "Every last one of them has fallen ill!")
        chatNpc(neutral,
            "I'm Captain Tock of Corsair Cove. My crew and I sailed north to seek help, " +
                "but now I'm stranded here with a ship full of sick sailors.")

        if (choice2("What kind of help do you need?", 1, "That sounds like your problem.", 2) != 1) {
            chatNpc(sad, "Aye... I suppose it is. Fair winds to you.")
        }

        chatNpc(happy,
            "I need someone to investigate what's happening! My crew thinks it's a curse — " +
                "ogre relics, demonic dolls, vengeful mermaids... It's all superstitious nonsense.")
        chatNpc(neutral,
            "But something IS making them sick, and I need to get to the bottom of it " +
                "before my entire crew is bedridden!")

        if (choice2("Sure, I'll try to help with your curse.", 1, "I don't believe in curses.", 2) != 1) {
            chatNpc(sad, "Aye... I understand. Safe travels.")
        }

        chatNpc(happy, "Wonderful! Let's set sail for Corsair Cove right away!")
        chatNpc(neutral, "Follow me to the dock. It's just south of here, west of Rimmington.")

        if (choice2("Okay, I'm ready to go to Corsair Cove.", 1, "Let me prepare first.", 2) == 1) {
            setQuestStage(1)
            chatNpc(happy, "Alright, follow me!")
            mes("Captain Tock leads you to the dock south of Rimmington.")
            teleport(COORDS_FERRY_RIMMINGTON)
            mes("You board the ferry to Corsair Cove.")
            teleport(COORDS_CORSAIR_COVE_DOCK)
            setQuestStage(2)
            mes("You arrive at Corsair Cove! The dock creaks under your feet.")
            mes("Captain Tock says: \"Welcome to Corsair Cove. Let's go find my crew.\"")
        } else {
            chatNpc(neutral, "I'll be at the dock south of Rimmington when you're ready. Don't keep me waiting!")
            setQuestStage(1)
        }
    }

    // =========================================================================
    // CAPTAIN TOCK — Corsair Cove
    // =========================================================================

    private suspend fun ProtectedAccess.tockCoveDialogue(npc: Npc) = startDialogue(npc) {
        when (questStage) {
            2 -> {
                chatNpc(happy, "Welcome to Corsair Cove, adventurer!")
                chatNpc(worried,
                    "My crew are all in their bunks, struck down by some strange illness. " +
                        "They each have their own theories about what caused it.")
                chatNpc(neutral,
                    "Talk to Chief Tess and Bugs first — they know the area. " +
                        "Then speak to my crew: Arsen, Colin, and Gnocci.")
                setQuestStage(3)
            }
            3 -> chatNpc(neutral, "Go talk to Chief Tess and Bugs, then speak to my crew.")
            in 4..5 -> chatNpc(neutral, "Have you spoken to my crew yet? Arsen, Colin, and Gnocci are all in their bunks.")
            6 -> {
                chatNpc(neutral, "So you've spoken to all of them. Each one blames something different.")
                chatNpc(neutral,
                    "I'll give you the ogre artefact that Arsen stole. Take it to Chief Tess — " +
                        "she'll know the truth about it.")
                if (!player.hasOgreArtefact && !inv.isFull()) {
                    invAddOrDrop(objRepo, cc_objs.ogre_artefact, count = 1)
                    mes("Captain Tock hands you the ogre artefact.")
                }
                setQuestStage(7)
            }
            7, 8 -> chatNpc(neutral, "Made any progress? The crew is counting on you!")
            9 -> chatNpc(neutral,
                "I hear you've been asking questions. Ithoi is also sick, you know. " +
                    "That rules out the food — he eats the same meals as everyone else.")
            10, 11, 12 -> chatNpc(neutral, "Have you found anything? The crew is still sick.")
            13 -> {
                chatNpc(angry, "Ithoi! I can't believe it. He's been faking this whole time!")
                chatNpc(neutral,
                    "He's fled to his lookout post upstairs. Climb the ladder in his hut " +
                        "and deal with him.")
                setQuestStage(14)
            }
            14 -> chatNpc(neutral, "What are you waiting for? Climb the ladder in Ithoi's hut!")
            15 -> {
                chatNpc(happy, "You did it! Ithoi won't be bothering anyone anymore!")
                completeQuest()
            }
            16 -> chatNpc(happy,
                "Welcome to Corsair Cove! The bank's open, resources are yours. " +
                    "You're a hero to the Corsairs!")
            else -> chatNpc(neutral, "How goes the investigation?")
        }
    }

    // =========================================================================
    // CHIEF TESS — Ogress Leader
    // =========================================================================

    private suspend fun ProtectedAccess.chiefTessDialogue(npc: Npc) = startDialogue(npc) {
        when {
            questStage in 0..2 -> chatNpc(angry, "What do you want, surface-dweller? Go away.")
            questStage in 3..4 -> {
                chatNpc(neutral, "You're with the corsairs, aren't you? Your captain already explained.")
                chatNpc(neutral,
                    "Yes, one of your crew — Arsen — stole something from us. " +
                        "But it was just a toothpick! Hardly worth cursing anyone over.")
                setQuestStage(4)
            }
            questStage in 5..6 -> chatNpc(neutral, "I told you, it was just a toothpick. We ogres are not petty.")
            questStage == 7 -> {
                if (player.hasOgreArtefact) {
                    chatNpc(happy, "Oh, you brought it back? Let me see that!")
                    chatNpc(happy, "HAHAHA! This is my toothpick! I use it to pick my teeth after meals!")
                    chatNpc(neutral, "Tell your friend Arsen that ogres don't curse people over toothpicks.")
                    invDel(inv, cc_objs.ogre_artefact, count = 1, strict = false)
                    mes("Chief Tess takes the ogre artefact and laughs heartily.")
                    setQuestStage(8)
                } else {
                    chatNpc(neutral, "Captain Tock mentioned you might have something of mine. A toothpick?")
                }
            }
            questStage >= 8 -> chatNpc(neutral, "We ogres are friendly enough once you get to know us.")
        }
    }

    // =========================================================================
    // BUGS — Rantz's Son (the "mermaid" seen through the telescope)
    // =========================================================================

    private suspend fun ProtectedAccess.bugsDialogue(npc: Npc) = startDialogue(npc) {
        when {
            questStage in 0..3 -> chatNpc(happy, "...")
            questStage in 4..7 -> {
                chatNpc(neutral, "Ogga booga! I'm practicing my scary face. My dad says I need to be scarier.")
                if (questStage == 4) setQuestStage(5)
            }
            questStage == 8 -> {
                mes("Hey! You're the one Colin saw through the telescope!")
                chatNpc(happy, "Yeah, I like waving at the boat people! Booga booga!")
                mes("It's not a mermaid... it's just an ogre child.")
                chatNpc(happy, "Mermaid? I'm not a fish! I'm Bugs!")
                if (questStage == 8) setQuestStage(9)
            }
            questStage >= 9 -> chatNpc(happy, "Booga booga! Did I scare you?")
        }
    }

    // =========================================================================
    // POSSESSED DOLL — on the northern wall of the main building
    // =========================================================================

    private suspend fun ProtectedAccess.possessedDollDialogue(npc: Npc) = startDialogue(npc) {
        when {
            questStage < 4 -> chatNpc(neutral, "A creepy-looking doll nailed to the wall. Better leave it alone.")
            questStage == 4 -> {
                chatNpc(neutral,
                    "A crude doll made of old sailcloth and driftwood, " +
                        "nailed to the wall with button eyes.")
                chatNpc(neutral,
                    "On closer inspection, it's filled with clockwork gears. " +
                        "This isn't a demonic doll — it's a mechanical toy!")
                setQuestStage(5)
            }
            questStage >= 5 -> chatNpc(neutral, "The doll hangs limply. Now that you know it's just clockwork, it's not scary.")
        }
    }

    // =========================================================================
    // ARSEN THE THIEF
    // =========================================================================

    private suspend fun ProtectedAccess.arsenSickDialogue(npc: Npc) = startDialogue(npc) {
        if (questStage < 4) {
            chatNpc(worried, "Ughhh... I've been cursed! I'm a dead man!")
        }
        when (questStage) {
            4, 5 -> {
                chatNpc(worried,
                    "It's all my fault! I stole a sacred artefact from the ogres under Corsair Cove. " +
                        "It must be cursed — ever since then, we've all been getting sick!")
                mes("Let me look into it. I'll talk to Captain Tock.")
                setQuestStage(6)
            }
            6 -> chatNpc(worried, "That's it, we're doomed! I stole a sacred ogre relic!")
            7 -> chatNpc(worried, "Did you find out anything? Was it the relic?")
            8 -> {
                mes("It was just a toothpick! Chief Tess uses it to pick her teeth!")
                chatNpc(happy,
                    "A toothpick? All this worry over a toothpick? I feel ridiculous! " +
                        "And actually... I feel a bit better already!")
                chatNpc(neutral, "Maybe it was just something I ate...")
                if (questStage == 8) setQuestStage(9)
            }
            9 -> chatNpc(happy, "A toothpick! I still can't believe it!")
            in 10..11 -> {
                chatNpc(neutral,
                    "You know, I've been thinking... Ithoi was the one who cooked dinner that night. " +
                        "My brother Francois said the Captain's been thinking of letting him go.")
                setQuestStage(12)
            }
            12 -> chatNpc(neutral, "Ithoi cooked the dinner. That's got to be connected.")
            in 13..15 -> chatNpc(angry, "Ithoi! That scoundrel! He poisoned us just to keep his job!")
            16 -> chatNpc(happy, "Thanks for sorting that mess out. I'll think twice before stealing again!")
        }
    }

    private suspend fun ProtectedAccess.arsenDialogue(npc: Npc) = startDialogue(npc) {
        arsenSickDialogue(npc)
    }

    // =========================================================================
    // CABIN BOY COLIN
    // =========================================================================

    private suspend fun ProtectedAccess.colinSickDialogue(npc: Npc) = startDialogue(npc) {
        if (questStage < 4) {
            chatNpc(worried, "I'm sorry, mermaid lady! I didn't mean it!")
        }
        when (questStage) {
            4, 5 -> {
                chatNpc(worried,
                    "I was looking through Ithoi's telescope and I saw a mermaid in the water. " +
                        "I shouted something rude and now she's cursed us!")
                mes("Let me take a look through that telescope.")
                setQuestStage(6)
            }
            6 -> chatNpc(worried, "She's going to curse us all!")
            7, 8 -> chatNpc(worried, "Did you talk to the mermaid? Is she still angry?")
            9 -> {
                mes("I looked through the telescope. It's not a mermaid — it's an ogre named Bugs!")
                chatNpc(happy,
                    "An ogre? Not a mermaid? Oh thank goodness! I feel so silly! " +
                        "And I think I'm feeling better already!")
                if (questStage == 8) setQuestStage(9)
            }
            in 10..15 -> chatNpc(happy, "An ogre! I can't believe I was scared of an ogre child!")
            16 -> chatNpc(happy, "I'm taking over the boat to Rimmington! Captain Tock promoted me!")
        }
    }

    private suspend fun ProtectedAccess.colinDialogue(npc: Npc) = startDialogue(npc) {
        colinSickDialogue(npc)
    }

    // =========================================================================
    // GNOCCHI THE COOK
    // =========================================================================

    private suspend fun ProtectedAccess.gnocciSickDialogue(npc: Npc) = startDialogue(npc) {
        if (questStage < 4) {
            chatNpc(worried, "Obby-lobby! We're all cursed! It's the demon doll!")
        }
        when (questStage) {
            4, 5 -> {
                chatNpc(worried,
                    "I found a creepy doll washed up on the beach. I buried it near the fishing spot, " +
                        "but I think it's cursed!")
                mes("Let me take a look at this doll.")
                setQuestStage(6)
            }
            6 -> chatNpc(worried, "It's that doll! I knew it was possessed!")
            7, 8 -> chatNpc(worried, "Did you find the doll? Is it still cursed?")
            9 -> {
                mes("I dug up what you buried. It's a clockwork toy! " +
                    "Gears and springs — nothing demonic about it!")
                chatNpc(happy,
                    "A toy? Obby-lobby! All that fuss over a child's plaything! " +
                        "I feel so relieved — and my stomach feels better too!")
                if (questStage == 8) setQuestStage(9)
            }
            9 -> chatNpc(happy, "A clockwork toy! Who'd have thought!")
            in 10..11 -> {
                chatNpc(neutral,
                    "You know, Ithoi cooked the meal that night. He made a big pot of stew. " +
                        "I woke up feeling sick just hours after dinner. " +
                        "Ithoi claimed he was fine the next morning though...")
                chatNpc(neutral, "Maybe you should talk to him.")
                setQuestStage(11)
            }
            12 -> chatNpc(neutral, "Ithoi cooked the dinner. That's when we all got sick.")
            in 13..15 -> chatNpc(angry, "Ithoi! He poisoned our own stew!")
            16 -> chatNpc(happy, "Obby-lobby! No more curses, just good cooking! Thanks to you!")
        }
    }

    private suspend fun ProtectedAccess.gnocciDialogue(npc: Npc) = startDialogue(npc) {
        gnocciSickDialogue(npc)
    }

    // =========================================================================
    // ITHOI THE NAVIGATOR — Boss Fight
    // =========================================================================

    private suspend fun ProtectedAccess.ithoiBossDialogue(npc: Npc) = startDialogue(npc) {
        if (questStage < 14) {
            mes("Ithoi glares at you menacingly, but doesn't attack.")
        }
        if (questStage >= 15) {
            mes("Ithoi lies defeated on the ground.")
        }

        mes("Ithoi the Navigator attacks! He hurls bolts of magical energy at you!")
        chatNpc(angry,
            "You think you can stop me, you meddlesome fool? " +
                "I'll drown you just like I sank those ships!")
        setQuestStage(15)
    }

    // =========================================================================
    // QUEST COMPLETION
    // =========================================================================

    private suspend fun ProtectedAccess.completeQuest() {
        setQuestStage(16)
        mes("Congratulations! You have completed <col=00ff00>The Corsair Curse</col>!")
        mes("You are awarded <col=ffff00>2 Quest Points</col>!")
        mes("<col=ffff00>Rewards:</col>")
        mes("  - Access to Yusuf's bank in Corsair Cove")
        mes("  - Ability to dock your boat at Corsair Cove Port")
        mes("  - Access to the Corsair Cove Dungeon")
        mes("  - Corsair Cove resources and supply crates")
    }

    // =========================================================================
    // Dialogue helpers
    // =========================================================================

    
    private suspend fun ProtectedAccess.choice2(
        opt1: String, val1: Int,
        opt2: String, val2: Int,
    ): Int {
        mes("Choose an option:")
        mes("1. $opt1")
        mes("2. $opt2")
        return 1
    }

    companion object {
        // Quest dialogue animations
                                                        
        // Key coordinates
        private val COORDS_FERRY_RIMMINGTON = CoordGrid(2965, 3240, 0)
        private val COORDS_CORSAIR_COVE_DOCK = CoordGrid(2548, 2955, 0)
    }
}
