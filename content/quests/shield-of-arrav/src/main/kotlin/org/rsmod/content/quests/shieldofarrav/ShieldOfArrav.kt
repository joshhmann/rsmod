package org.rsmod.content.quests.shieldofarrav

import jakarta.inject.Inject
import org.rsmod.api.config.refs.BaseVarps
import org.rsmod.api.config.refs.objs
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.giveQuestReward
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpObj1
import org.rsmod.content.quests.shieldofarrav.configs.shield_arrav_locs
import org.rsmod.content.quests.shieldofarrav.configs.shield_arrav_npcs
import org.rsmod.content.quests.shieldofarrav.configs.shield_arrav_objs
import org.rsmod.game.entity.Npc
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class ShieldOfArrav @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {

    override fun ScriptContext.startup() {
        // --- NPC Dialogues ---
        onOpNpc1(shield_arrav_npcs.baraek) { baraekDialogue(it.npc) }
        onOpNpc1(shield_arrav_npcs.katrine) { katrineDialogue(it.npc) }
        onOpNpc1(shield_arrav_npcs.straven) { stravenDialogue(it.npc) }
        onOpNpc1(shield_arrav_npcs.jonny_the_beard) { jonnyDialogue(it.npc) }
        onOpNpc1(shield_arrav_npcs.weaponsmaster) { weaponsMasterDialogue(it.npc) }
        onOpNpc1(shield_arrav_npcs.king_roald) { kingRoaldDialogue(it.npc) }
        onOpNpc1(shield_arrav_npcs.tramp) { charlieDialogue(it.npc) }

        // --- LOC Interactions ---
        onOpLoc1(shield_arrav_locs.phoenixdoor) { interactPhoenixDoor(it.loc) }
        onOpLoc1(shield_arrav_locs.phoenixdoor2) { interactWeaponStashDoor(it.loc) }
        onOpLoc1(shield_arrav_locs.blackarmdoor) { interactBlackArmDoor(it.loc) }
        onOpLoc1(shield_arrav_locs.blackarmcupboardshut) { interactBlackArmCupboard(it.loc) }
        onOpLoc1(shield_arrav_locs.phoenixshutchest) { interactPhoenixChest(it.loc) }

        // --- Item Interactions ---
        onOpObj1(shield_arrav_objs.the_shield_of_arrav) { readShieldBook() }
        onOpObj1(shield_arrav_objs.intelligence_report) { readIntelReport() }
        onOpObj1(shield_arrav_objs.arravcertificate) { readCertificate() }
    }

    // ======================== QUEST STAGE HELPERS ========================
    // These must be called from ProtectedAccess context (outside Dialogue lambdas)

    private suspend fun ProtectedAccess.setSOAStage(stage: Int) {
        setQuestStage(QuestList.shield_of_arrav, stage)
    }

    private fun ProtectedAccess.isPhoenix(): Boolean = vars[BaseVarps.arrav_blackarm] == 1
    private fun ProtectedAccess.isBlackArm(): Boolean = vars[BaseVarps.arrav_blackarm] == 2
    private fun ProtectedAccess.isPhoenixMission(): Boolean = vars[BaseVarps.arrav_blackarm] == 3
    private fun ProtectedAccess.isBlackArmMission(): Boolean = vars[BaseVarps.arrav_blackarm] == 4

    private suspend fun ProtectedAccess.setPhoenix() { vars[BaseVarps.arrav_blackarm] = 1 }
    private suspend fun ProtectedAccess.setBlackArm() { vars[BaseVarps.arrav_blackarm] = 2 }
    private suspend fun ProtectedAccess.setPhoenixMission() { vars[BaseVarps.arrav_blackarm] = 3 }
    private suspend fun ProtectedAccess.setBlackArmMission() { vars[BaseVarps.arrav_blackarm] = 4 }

    // ======================== QUEST COMPLETION ========================

    private suspend fun ProtectedAccess.completeSOA() {
        setSOAStage(6)
        giveQuestReward(QuestList.shield_of_arrav)
        showCompletionScroll(
            quest = QuestList.shield_of_arrav,
            rewards = listOf("600 Coins", "1 Quest Point"),
            itemModel = shield_arrav_objs.arravcertificate,
            questPoints = 1,
        )
    }

    // ======================== NPC DIALOGUES ========================

    // ----- Baraek the Fur Trader -----
    private suspend fun ProtectedAccess.baraekDialogue(npc: Npc) =
        startDialogue(npc) {
            val stage = access.getQuestStage(QuestList.shield_of_arrav)
            when {
                stage == 0 || stage == 1 -> {
                    chatNpc(neutral, "Hello there. Would you be interested in some fine fur?")
                    val choice =
                        choice2(
                            "I'm looking for the Phoenix Gang.",
                            1,
                            "No thanks, just browsing.",
                            2,
                        )
                    when (choice) {
                        1 -> {
                            chatPlayer(quiz, "I'm looking for the Phoenix Gang.")
                            chatNpc(
                                shifty,
                                "The Phoenix Gang? I don't know what you're talking about...",
                            )
                            chatNpc(
                                shifty,
                                "...although, I might have heard a rumour or two.",
                            )
                            if (stage <= 1) {
                                chatNpc(
                                    shifty,
                                    "If you're looking for the Phoenix Gang, they operate out of a building in the south-east of Varrock. They pretend to be the VTAM Corporation.",
                                )
                                if (stage < 2) access.setSOAStage(2)
                            }
                        }
                        2 -> {
                            chatPlayer(neutral, "No thanks, just browsing.")
                            chatNpc(happy, "Suit yourself.")
                        }
                    }
                }
                stage >= 2 -> {
                    chatNpc(neutral, "Back again? Here for some fur?")
                    chatPlayer(quiz, "Tell me more about the Phoenix Gang.")
                    chatNpc(
                        shifty,
                        "They're in the south-east of Varrock, disguised as the VTAM Corporation. That's all I know.",
                    )
                }
                else -> {
                    chatNpc(neutral, "Hello there. Interested in some fur?")
                    chatPlayer(neutral, "Not right now, thanks.")
                    chatNpc(happy, "Very well.")
                }
            }
        }

    // ----- Charlie the Tramp -----
    private suspend fun ProtectedAccess.charlieDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(sad, "Spare some change, guv?")
            val choice =
                choice5(
                    "Who are you?", 1,
                    "Sorry, I haven't got any.", 2,
                    "Go get a job!", 3,
                    "Is there anything down this alleyway?", 4,
                    "Ok. Here you go.", 5,
                )
            when (choice) {
                1 -> {
                    chatPlayer(quiz, "Who are you?")
                    chatNpc(happy, "Charlie the Tramp, at your service. Now, about that change...")
                }
                2 -> {
                    chatPlayer(neutral, "Sorry, I haven't got any.")
                    chatNpc(sad, "Thanks anyway!")
                }
                3 -> {
                    chatPlayer(angry, "Go get a job!")
                    chatNpc(angry, "You startin'? I hope your nose falls off!")
                }
                4 -> {
                    chatPlayer(quiz, "Is there anything down this alleyway?")
                    chatNpc(
                        worried,
                        "The ruthless and notorious criminal gang known as the Black Arm Gang have their headquarters down there.",
                    )
                    val choice2 =
                        choice2("Thanks for the warning!", 1, "Do you think they'd let me join?", 2)
                    when (choice2) {
                        1 -> {
                            chatPlayer(neutral, "Thanks for the warning!")
                            chatNpc(happy, "Don't worry about it.")
                        }
                        2 -> {
                            chatPlayer(quiz, "Do you think they'd let me join?")
                            if (!access.isPhoenix() && !access.isBlackArm()) {
                                chatNpc(
                                    shifty,
                                    "You never know. You'll find a lady down there called Katrine. Speak to her.",
                                )
                                chatNpc(worried, "But don't upset her, she's pretty dangerous.")
                                val stage = access.getQuestStage(QuestList.shield_of_arrav)
                                if (stage < 2) access.setSOAStage(2)
                            } else if (access.isBlackArm()) {
                                chatNpc(shifty, "I was under the impression you were already a member...")
                            } else {
                                chatNpc(angry, "No. You're a collaborator with the Phoenix Gang. There's no way they'll let you join now.")
                            }
                        }
                    }
                }
                5 -> {
                    chatPlayer(neutral, "Ok, here you go.")
                    if (player.inv.contains(objs.coins)) {
                        access.invDel(access.inv, objs.coins, 1)
                        chatNpc(happy, "Hey, thanks a lot!")
                    } else {
                        mesbox("You need one coin to give away.")
                    }
                }
            }
        }

    // ----- Straven (Phoenix Gang) -----
    private suspend fun ProtectedAccess.stravenDialogue(npc: Npc) =
        startDialogue(npc) {
            val stage = access.getQuestStage(QuestList.shield_of_arrav)
            when {
                stage == 4 && access.isPhoenixMission() -> {
                    chatNpc(neutral, "How's your little mission going?")
                    if (player.inv.contains(shield_arrav_objs.intelligence_report)) {
                        chatPlayer(neutral, "I have the intelligence report!")
                        chatNpc(neutral, "Let's see it then.")
                        mesbox("You hand over the report. The man reads the report.")
                        chatNpc(happy, "Yes. Yes, this is very good.")
                        chatNpc(happy, "Ok! You can join the Phoenix Gang! I am Straven, one of the gang leaders.")
                        chatPlayer(happy, "Nice to meet you.")
                        chatNpc(neutral, "Take this key.")
                        val removed = access.invDel(access.inv, shield_arrav_objs.intelligence_report, 1, strict = false).success
                        if (removed) {
                            access.invAddOrDrop(objRepo, shield_arrav_objs.phoenixkey1, 1)
                            objbox(shield_arrav_objs.phoenixkey1, "Straven hands you a key.")
                            access.setPhoenix()
                            access.setSOAStage(5)
                            chatNpc(neutral, "This key will give you access to our weapons supply depot round the front of this building.")
                        }
                    } else {
                        chatPlayer(neutral, "I haven't managed to find the report yet...")
                        chatNpc(neutral, "You need to kill Johnny the Beard, who should be in the Blue Moon Inn.")
                        chatNpc(neutral, "...I would guess. Not being a member of the Phoenix Gang and all.")
                    }
                }
                stage >= 5 && access.isPhoenix() -> {
                    chatNpc(happy, "Greetings, fellow gang member.")
                    if (!player.inv.contains(shield_arrav_objs.phoenixkey1)) {
                        chatPlayer(neutral, "I'm afraid I've lost the key you gave me...")
                        chatNpc(angry, "You really need to be more careful. Don't lose THIS one.")
                        access.invAddOrDrop(objRepo, shield_arrav_objs.phoenixkey1, 1)
                        objbox(shield_arrav_objs.phoenixkey1, "Straven hands you a key.")
                    } else {
                        val choice2 = choice3(
                            "I've heard you've got some cool treasure in this place.", 1,
                            "Any suggestions for where I can go thieving?", 2,
                            "Where's the Black Arm Gang hideout?", 3,
                        )
                        when (choice2) {
                            1 -> {
                                chatPlayer(quiz, "I've heard you've got some cool treasure in this place.")
                                chatNpc(happy, "Oh yeah, we've all stolen some stuff. And the Shield of Arrav... we stole that years ago!")
                                chatNpc(neutral, "We don't even have all of the shield anymore - we only have one half.")
                            }
                            2 -> {
                                chatPlayer(quiz, "Any suggestions on where I can go thieving?")
                                chatNpc(neutral, "You can always try the marketplace in Ardougne. Lots of opportunity there!")
                            }
                            3 -> {
                                chatPlayer(quiz, "Where's the Black Arm Gang hideout? I wanna go sabotage 'em!")
                                chatNpc(neutral, "That would be a little tricky. Their security is pretty good.")
                            }
                        }
                    }
                }
                stage >= 2 && stage < 4 -> {
                    chatNpc(angry, "Hey! You can't go in there. Only authorised personnel of the VTAM Corporation are allowed beyond this point.")
                    val choice2 = choice3(
                        "I know who you are!", 1,
                        "How do I get a job with the VTAM Corporation?", 2,
                        "Why not?", 3,
                    )
                    when (choice2) {
                        1 -> {
                            chatPlayer(quiz, "I know who you are!")
                            chatNpc(neutral, "Really? Well? Who are we then?")
                            chatPlayer(quiz, "This is the headquarters of the Phoenix Gang, the most powerful crime syndicate this city has ever seen!")
                            chatNpc(neutral, "No, this is a legitimate business. Supposing we were, what would you want with us?")
                            val choice3 = choice2("I'd like to offer you my services.", 1, "I want nothing. I was just making sure.", 2)
                            when (choice3) {
                                1 -> {
                                    chatPlayer(neutral, "I'd like to offer you my services.")
                                    chatNpc(neutral, "You mean you'd like to join? Well, the gang doesn't let people join just like that.")
                                    chatNpc(neutral, "Generally someone has to prove their loyalty first.")
                                    chatPlayer(quiz, "How would I go about doing that?")
                                    chatNpc(shifty, "A rival gang of ours, the Black Arm Gang, is supposedly meeting a contact from Port Sarim today in the Blue Moon Inn.")
                                    chatNpc(shifty, "The name of the contact is Jonny the Beard.")
                                    chatNpc(shifty, "If SOMEBODY were to kill him and bring back his intelligence report, they would be considered loyal enough to join.")
                                    chatPlayer(neutral, "Ok, I'll get right on it.")
                                    access.setPhoenixMission()
                                    if (stage < 4) access.setSOAStage(4)
                                }
                                2 -> {
                                    chatPlayer(neutral, "I want nothing. I was just making sure.")
                                    chatNpc(angry, "Well then get lost and stop wasting my time.")
                                }
                            }
                        }
                        2 -> {
                            chatPlayer(quiz, "How do I get a job with the VTAM Corporation?")
                            chatNpc(neutral, "Get a copy of the Varrock Herald. Any positions will be advertised there.")
                        }
                        3 -> {
                            chatPlayer(quiz, "Why not?")
                            chatNpc(angry, "Sorry. That's classified information.")
                        }
                    }
                }
                else -> {
                    chatNpc(angry, "Hey! You can't go in there. Only authorised personnel of the VTAM Corporation are allowed beyond this point.")
                    chatPlayer(neutral, "Ok, sorry.")
                }
            }
        }

    // ----- Katrine (Black Arm Gang leader) -----
    private suspend fun ProtectedAccess.katrineDialogue(npc: Npc) =
        startDialogue(npc) {
            val stage = access.getQuestStage(QuestList.shield_of_arrav)
            when {
                stage >= 5 && access.isBlackArm() -> {
                    if (access.isPhoenix()) {
                        chatNpc(angry, "You've got some guts coming here, Phoenix guy!")
                    } else {
                        chatPlayer(neutral, "Hey.")
                        chatNpc(neutral, "Hey.")
                    }
                }
                stage == 4 && access.isBlackArmMission() -> {
                    chatNpc(neutral, "Have you got those crossbows for me yet?")
                    val hasCrossbows = player.inv.contains(shield_arrav_objs.phoenix_crossbow)
                    if (hasCrossbows && access.invTotal(access.inv, shield_arrav_objs.phoenix_crossbow) >= 2) {
                        chatPlayer(happy, "Yes, I have.")
                        mesbox("You give the crossbows to Katrine.")
                        val removed = access.invDel(access.inv, shield_arrav_objs.phoenix_crossbow, 2, strict = false).success
                        if (removed) {
                            chatNpc(happy, "Ok. You can join our gang now. Feel free to enter any of the rooms of the ganghouse.")
                            access.setBlackArm()
                            access.setSOAStage(5)
                        }
                    } else {
                        chatPlayer(neutral, "No, I haven't found them yet.")
                        chatNpc(neutral, "I need two crossbows stolen from the Phoenix Gang weapons stash, which if you head east for a bit, is a building on the south side of the road.")
                        chatNpc(neutral, "Come back when you got 'em.")
                    }
                }
                stage >= 2 && stage < 4 -> {
                    chatNpc(neutral, "It's a private business. Can I help you at all?")
                    val choice2 = choice3(
                        "I've heard you're the Black Arm Gang.", 1,
                        "What sort of business?", 2,
                        "I'm looking for fame and riches.", 3,
                    )
                    when (choice2) {
                        1 -> {
                            chatPlayer(quiz, "I've heard you're the Black Arm Gang.")
                            chatNpc(angry, "Who told you that?")
                            val choice3 = choice3(
                                "I'd rather not reveal my sources.", 1,
                                "It was Charlie, the tramp outside.", 2,
                                "Everyone knows - it's no great secret.", 3,
                            )
                            when (choice3) {
                                1 -> {
                                    chatPlayer(neutral, "I'd rather not reveal my sources.")
                                    chatNpc(neutral, "Yes, I can understand that. So what do you want with us?")
                                    val choice4 = choice3(
                                        "I want to become a member of your gang.", 1,
                                        "I want some hints for becoming a thief.", 2,
                                        "I'm looking for the door out of here.", 3,
                                    )
                                    when (choice4) {
                                        1 -> gangJoinPath()
                                        2 -> {
                                            chatPlayer(quiz, "I want some hints for becoming a thief.")
                                            chatNpc(angry, "Well, I'm sorry, luv, I'm not giving away any of my secrets.")
                                        }
                                        3 -> {
                                            chatPlayer(neutral, "I'm looking for the door out of here.")
                                            mesbox("Katrine groans.")
                                            chatNpc(angry, "Try... the one you just came in?")
                                        }
                                    }
                                }
                                2 -> {
                                    chatPlayer(neutral, "It was Charlie, the tramp outside.")
                                    chatNpc(angry, "Is that guy still out there? Remind me to send someone to kill him.")
                                }
                                3 -> {
                                    chatPlayer(quiz, "Everyone knows - it's no great secret.")
                                    chatNpc(shocked, "I thought we were safe back here!")
                                }
                            }
                        }
                        2 -> {
                            chatPlayer(quiz, "What sort of business?")
                            chatNpc(neutral, "A small, family business. We give financial advice to other companies.")
                        }
                        3 -> {
                            chatPlayer(quiz, "I'm looking for fame and riches.")
                            chatNpc(angry, "And you expect to find it up the back streets of Varrock?")
                        }
                    }
                }
                else -> {
                    chatNpc(neutral, "It's a private business. Can I help you?")
                    val choice2 = choice2("What sort of business?", 1, "I'm looking for fame and riches.", 2)
                    when (choice2) {
                        1 -> {
                            chatPlayer(quiz, "What sort of business?")
                            chatNpc(neutral, "A small, family business. We give financial advice to other companies.")
                        }
                        2 -> {
                            chatPlayer(quiz, "I'm looking for fame and riches.")
                            chatNpc(angry, "And you expect to find it up the back streets of Varrock?")
                        }
                    }
                }
            }
        }

    // Helper for Katrine's gang join path
    private suspend fun Dialogue.gangJoinPath() {
        chatPlayer(neutral, "I want to become a member of your gang.")
        chatNpc(neutral, "How unusual. Normally we recruit by watching local thugs. How can I be sure you can be trusted?")
        val choice5 = choice2("Well, you can give me a try, can't you?", 1, "Well, people tell me I have an honest face.", 2)
        when (choice5) {
            1 -> {
                chatPlayer(quiz, "Well, you can give me a try, can't you?")
                chatNpc(neutral, "I'm not so sure...")
            }
            2 -> {
                chatPlayer(neutral, "Well, people tell me I have an honest face.")
                chatNpc(shifty, "How unusual. Someone honest wanting to join a gang of thieves.")
            }
        }
        chatNpc(neutral, "Our rival gang - the Phoenix Gang - has a weapons stash a little east of here.")
        chatNpc(neutral, "We're fresh out of crossbows, so if you could steal a couple of Phoenix crossbows for us, I'll be happy to call you a Black Arm.")
        chatPlayer(quiz, "Sounds simple enough. Any particular reason you need two of them?")
        chatNpc(neutral, "I have an idea for framing a local merchant. So, are you going to get those crossbows or not?")
        val choice6 = choice2("Ok, no problem.", 1, "Sounds a little tricky. Got anything easier?", 2)
        when (choice6) {
            1 -> {
                chatPlayer(neutral, "Ok, no problem.")
                chatNpc(neutral, "Great! You'll find the Phoenix gang's weapon stash just next to a temple, due east of here.")
                access.setBlackArmMission()
                val stage = access.getQuestStage(QuestList.shield_of_arrav)
                if (stage < 4) access.setSOAStage(4)
            }
            2 -> {
                chatPlayer(neutral, "Sounds a little tricky. Got anything easier?")
                chatNpc(angry, "If you're not up to a bit of danger, I don't think you've got anything to offer our gang.")
            }
        }
    }

    // ----- Jonny the Beard -----
    private suspend fun ProtectedAccess.jonnyDialogue(npc: Npc) =
        startDialogue(npc) {
            if (access.isPhoenixMission()) {
                mes("Johnny the Beard is not interested in talking.")
            } else {
                chatNpc(neutral, "Will you buy me a beer?")
                chatPlayer(neutral, "No, I don't think I will.")
            }
        }

    // ----- Weapons Master (Phoenix weapons room guard) -----
    private suspend fun ProtectedAccess.weaponsMasterDialogue(npc: Npc) =
        startDialogue(npc) {
            if (access.isPhoenix()) {
                chatNpc(happy, "Hello fellow Phoenix! What are you after?")
                val choice2 = choice2("I'm after a weapon or two.", 1, "I'm looking for treasure.", 2)
                when (choice2) {
                    1 -> {
                        chatPlayer(neutral, "I'm after a weapon or two.")
                        chatNpc(happy, "No problem. Feel free to look around.")
                    }
                    2 -> {
                        chatPlayer(quiz, "I'm looking for treasure.")
                        chatNpc(neutral, "Aren't we all? We've not got any up here. Go mug someone somewhere.")
                    }
                }
            } else {
                chatNpc(angry, "Hey! Who are you? I'm gonna teach you not to stick your nose where it don't belong!")
                // Weapons master attacks - combat handled by engine
            }
        }

    // ----- King Roald -----
    private suspend fun ProtectedAccess.kingRoaldDialogue(npc: Npc) =
        startDialogue(npc) {
            // Has full combined certificate?
            if (player.inv.contains(shield_arrav_objs.arravcertificate)) {
                chatPlayer(neutral, "Your majesty, I have come to claim the reward for the return of the Shield of Arrav.")
                objbox(shield_arrav_objs.arravcertificate, "You show the certificate to the king.")
                chatNpc(happy, "My goodness! This claim is for the reward offered by my father many years ago!")
                chatNpc(happy, "I never thought I would live to see the day! I heard that you found half the shield, so I will give you half of the bounty. That comes to exactly 600 gp!")
                objbox(shield_arrav_objs.arravcertificate, "You hand over the certificate. The king gives you 600 gp.")
                val removed = access.invDel(access.inv, shield_arrav_objs.arravcertificate, 1, strict = false).success
                if (removed) {
                    access.completeSOA()
                }
                return@startDialogue
            }

            // Has shield half (not yet verified by curator)?
            if (player.inv.contains(shield_arrav_objs.arravshield1) ||
                player.inv.contains(shield_arrav_objs.arravshield2)
            ) {
                chatPlayer(quiz, "Your majesty, I have recovered the Shield of Arrav. I would like to claim the reward.")
                chatNpc(happy, "The Shield of Arrav, eh? Yes, I do recall my father put a reward out for that. Very well.")
                chatNpc(neutral, "If you get the authenticity of the shield verified by the curator at the museum and then return here with authentication, I will grant your reward.")
                return@startDialogue
            }

            // Has half certificate?
            if (player.inv.contains(shield_arrav_objs.arravcertificate_lft) ||
                player.inv.contains(shield_arrav_objs.arravcertificate_rht)
            ) {
                chatPlayer(neutral, "Your majesty, I have come to claim the reward for the return of the Shield of Arrav.")
                val certItem = if (player.inv.contains(shield_arrav_objs.arravcertificate_lft))
                    shield_arrav_objs.arravcertificate_lft else shield_arrav_objs.arravcertificate_rht
                objbox(certItem, "You show the certificate to the king.")
                chatNpc(neutral, "I'm afraid that's only half the reward certificate. You'll have to get the other half and join them together if you want to claim the reward.")
                return@startDialogue
            }

            // Default
            chatPlayer(neutral, "Your majesty.")
            chatNpc(happy, "Greetings, adventurer.")
        }

    // ======================== LOC INTERACTIONS ========================

    private suspend fun ProtectedAccess.interactPhoenixDoor(loc: BoundLocInfo) {
        if (isPhoenix()) {
            mes("The door automatically opens for you.")
        } else {
            mes("The door is securely locked.")
        }
    }

    private suspend fun ProtectedAccess.interactWeaponStashDoor(loc: BoundLocInfo) {
        val stage = getQuestStage(QuestList.shield_of_arrav)
        if (stage >= 4 && isPhoenixMission() && !player.inv.contains(shield_arrav_objs.phoenixkey1)) {
            mes("This is the door to the weapon stash you were looking for. Maybe if you can find another adventurer who happens to be a member of the Phoenix Gang, they could help you.")
        } else if (player.inv.contains(shield_arrav_objs.phoenixkey1)) {
            mes("You unlock the door with your key.")
        } else {
            mes("The door is securely locked.")
        }
    }

    private suspend fun ProtectedAccess.interactBlackArmDoor(loc: BoundLocInfo) {
        if (isBlackArm()) {
            mes("The door opens for you.")
        } else {
            mes("This door seems to be locked from the inside.")
        }
    }

    private suspend fun ProtectedAccess.interactBlackArmCupboard(loc: BoundLocInfo) {
        val stage = getQuestStage(QuestList.shield_of_arrav)
        if (stage >= 5 && isBlackArm()) {
            if (!player.inv.contains(shield_arrav_objs.arravshield2)) {
                val added = invAddOrDrop(objRepo, shield_arrav_objs.arravshield2, 1)
                if (added) {
                    objbox(shield_arrav_objs.arravshield2, "You find half of a shield, which you take.")
                }
            } else {
                mes("The cupboard is bare.")
            }
        } else {
            mes("It's just an empty cupboard.")
        }
    }

    private suspend fun ProtectedAccess.interactPhoenixChest(loc: BoundLocInfo) {
        val stage = getQuestStage(QuestList.shield_of_arrav)
        if (stage >= 5 && isPhoenix()) {
            if (!player.inv.contains(shield_arrav_objs.arravshield1)) {
                val added = invAddOrDrop(objRepo, shield_arrav_objs.arravshield1, 1)
                if (added) {
                    objbox(shield_arrav_objs.arravshield1, "You find half of a shield, which you take.")
                }
            } else {
                mes("The chest is empty.")
            }
        } else {
            mes("It's just an old chest.")
        }
    }

    // ======================== ITEM INTERACTIONS ========================

    private suspend fun ProtectedAccess.readShieldBook() {
        val stage = getQuestStage(QuestList.shield_of_arrav)
        mesbox(
            "The Shield of Arrav by A.R Wright.\n\n" +
                "Arrav is a well-known hero of the 4th age. The shield is believed to have once belonged to " +
                "Arrav and is now known as the Shield of Arrav.\n\n" +
                "In the year 143 of the 5th age, a gang of thieves called the Phoenix Gang broke into the " +
                "museum and stole the shield. King Roald put a 1200 gold bounty on its return."
        )
        if (stage == 1) {
            setSOAStage(2)
            mes("You've learnt about the Shield of Arrav.")
        }
    }

    private suspend fun ProtectedAccess.readIntelReport() {
        mesbox(
            "Intelligence Report\n\n" +
                "There is an archaeologist hanging around the statue outside of the city, with mining equipment. " +
                "Could she be onto a hidden treasure buried near the statue?\n\n" +
                "There is a new channel being dug and a barge being assembled near the dig site. " +
                "The Varrock Museum seems to be paying for it."
        )
    }

    private suspend fun ProtectedAccess.readCertificate() {
        mesbox(
            "The bearer of this certificate has brought both halves of the legendary Shield of Arrav to me, " +
                "Haig Halen, Curator of the Varrock Museum. I have examined the shield and am satisfied as to " +
                "its authenticity. I recommend to His Majesty, King Roald III, that the bearer is rewarded " +
                "as per Proclamation 262 of the year 143 of the 5th Age, by King Roald II."
        )
    }
}
