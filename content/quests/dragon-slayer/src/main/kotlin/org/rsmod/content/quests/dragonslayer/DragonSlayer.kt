package org.rsmod.content.quests.dragonslayer

import jakarta.inject.Inject
import kotlin.random.Random
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.hit.queueHit
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.giveQuestReward
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.script.onNpcHit
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.dragonslayer.configs.dragon_slayer_npcs
import org.rsmod.content.quests.dragonslayer.configs.dragon_slayer_objs
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.hit.HitType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Dragon Slayer I quest implementation for RSMod v2.
 *
 * Prove yourself a true hero by slaying the dragon Elvarg on Crandor Isle. Collect three map
 * pieces, obtain a ship, and face the green dragon.
 *
 * Rewards: 2 Quest Points, 18,650 Strength XP, 18,650 Defence XP, ability to equip rune platebody
 * and green d'hide body
 */
class DragonSlayer
@Inject
constructor(
    private val playerList: PlayerList,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {

    // Track dragonfire cooldown per Elvarg instance
    private val dragonfireCooldownByNpc = mutableMapOf<Int, Int>()

    override fun ScriptContext.startup() {
        // Guildmaster in Champions' Guild (quest start)
        onOpNpc1(dragon_slayer_npcs.guildmaster) { startGuildmasterDialogue(it.npc) }

        // Oziach in Edgeville (quest giver)
        onOpNpc1(dragon_slayer_npcs.oziach) { startOziachDialogue(it.npc) }

        // Elvarg on Crandor (boss)
        onOpNpc1(dragon_slayer_npcs.elvarg) { startElvargDialogue(it.npc) }

        // Elvarg's dragonfire special attack
        onNpcHit(dragon_slayer_npcs.elvarg) {
            val source = hit.resolvePlayerSource(playerList) ?: return@onNpcHit

            if (npc.hitpoints <= 0) {
                // Check if player should complete quest
                checkQuestCompletion(source, npc)
                return@onNpcHit
            }

            // Dragonfire breath every 4-6 ticks while in combat
            val now = npc.currentMapClock
            val npcKey = npc.uid.packed
            val nextAllowed = dragonfireCooldownByNpc[npcKey] ?: Int.MIN_VALUE

            if (now >= nextAllowed) {
                dragonfireCooldownByNpc[npcKey] = now + 4 + Random.nextInt(0, 3)

                // Play dragonfire animation (using dragon attack as fallback)
                npc.anim(seqs.dragon_attack)

                // Calculate dragonfire damage
                val hasShield = source.worn.contains(dragon_slayer_objs.antidragonbreathshield)
                val fireDamage =
                    if (hasShield) {
                        Random.nextInt(3, 10) // Reduced with shield
                    } else {
                        Random.nextInt(40, 65) // Devastating without shield
                    }

                // Queue the dragonfire hit
                source.queueHit(source = npc, delay = 1, type = HitType.Magic, damage = fireDamage)

                if (!hasShield) {
                    protectedAccess.launch(source) {
                        mes("You are horribly burnt by the dragon's breath!")
                    }
                }
            }
        }
    }

    private fun checkQuestCompletion(source: org.rsmod.game.entity.Player, npc: Npc) {
        protectedAccess.launch(source) {
            if (getQuestStage(QuestList.dragon_slayer_i) != 4) {
                return@launch
            }

            // Complete the quest
            setQuestStage(QuestList.dragon_slayer_i, 5)
            giveQuestReward(QuestList.dragon_slayer_i)
            showCompletionScroll(
                quest = QuestList.dragon_slayer_i,
                rewards =
                    listOf(
                        "2 Quest Points",
                        "18,650 Strength XP",
                        "18,650 Defence XP",
                        "Access to Rune platebody",
                    ),
                itemModel = dragon_slayer_objs.dragonmap,
                questPoints = 2,
            )
        }
    }

    // ==================== Guildmaster Dialogue ====================

    private suspend fun ProtectedAccess.startGuildmasterDialogue(npc: Npc) {
        startDialogue(npc) {
            when (getQuestStage(QuestList.dragon_slayer_i)) {
                0 -> guildmasterStartDialogue()
                1 -> guildmasterPostStartDialogue()
                in 2..4 -> guildmasterInProgressDialogue()
                else -> guildmasterFinishedDialogue()
            }
        }
    }

    private suspend fun Dialogue.guildmasterStartDialogue() {
        chatNpc(quiz, "Greetings! Welcome to the Champions' Guild.")
        chatPlayer(quiz, "What is this place?")
        chatNpc(
            neutral,
            "This is the Champions' Guild. Only the most accomplished adventurers are allowed here.",
        )
        chatNpc(quiz, "I see you have 32 Quest Points. Are you interested in a real challenge?")

        val choice = choice2("What challenge?", 1, "Not right now.", 2)
        when (choice) {
            1 -> {
                chatPlayer(quiz, "What challenge?")
                chatNpc(
                    neutral,
                    "There's a dragon called Elvarg terrorizing Crandor Isle. Many have tried to stop her.",
                )
                chatNpc(sad, "None have returned alive.")
                chatNpc(quiz, "If you want to take on this quest, speak to Oziach in Edgeville.")
                chatNpc(
                    neutral,
                    "He's the only one who knows where to find the secrets of Crandor.",
                )
                chatPlayer(happy, "I'll speak to Oziach right away!")
                access.setQuestStage(QuestList.dragon_slayer_i, 1)
            }
            2 -> chatPlayer(neutral, "Not right now, thanks.")
        }
    }

    private suspend fun Dialogue.guildmasterPostStartDialogue() {
        chatNpc(quiz, "Have you spoken to Oziach in Edgeville yet?")
        chatPlayer(neutral, "Not yet.")
        chatNpc(
            neutral,
            "He's in a small house in the west of Edgeville. He has information about the dragon.",
        )
    }

    private suspend fun Dialogue.guildmasterInProgressDialogue() {
        chatNpc(quiz, "How goes your quest to defeat Elvarg?")
        when (access.getQuestStage(QuestList.dragon_slayer_i)) {
            2 -> {
                chatPlayer(neutral, "I'm collecting the map pieces to find Crandor.")
                chatNpc(
                    neutral,
                    "Remember: one piece is in Melzar's Maze, one with Thalzar's kin, and one with Lozar's remains.",
                )
            }
            3 -> {
                chatPlayer(happy, "I have all three map pieces! Now I need to find a ship.")
                chatNpc(
                    neutral,
                    "Port Sarim is where most sailors dock. You might find someone there willing to sail to Crandor.",
                )
            }
            4 -> {
                chatPlayer(happy, "I've got a ship ready to sail to Crandor!")
                chatNpc(
                    neutral,
                    "Then you're nearly there. Remember to take an anti-dragon shield!",
                )
                chatNpc(
                    neutral,
                    "You can get one from Duke Horacio on the 2nd floor of Lumbridge Castle.",
                )
            }
        }
    }

    private suspend fun Dialogue.guildmasterFinishedDialogue() {
        chatNpc(happy, "Greetings, Dragon Slayer! You have proven yourself a true champion.")
        chatPlayer(happy, "Thank you! Elvarg was a worthy challenge.")
        chatNpc(neutral, "You may now purchase and wear rune platebodies.")
    }

    // ==================== Oziach Dialogue ====================

    private suspend fun ProtectedAccess.startOziachDialogue(npc: Npc) {
        startDialogue(npc) {
            when (getQuestStage(QuestList.dragon_slayer_i)) {
                0 -> oziachPreQuestDialogue()
                1 -> oziachStartDialogue()
                2 -> oziachMapPiecesDialogue()
                3 -> oziachShipDialogue()
                4 -> oziachReadyToSailDialogue()
                else -> oziachFinishedDialogue()
            }
        }
    }

    private suspend fun Dialogue.oziachPreQuestDialogue() {
        chatNpc(angry, "I don't talk to just anyone. Come back when you're more experienced.")
        chatPlayer(quiz, "What do you mean?")
        chatNpc(
            neutral,
            "Speak to the Guildmaster in the Champions' Guild. He vouches for adventurers.",
        )
    }

    private suspend fun Dialogue.oziachStartDialogue() {
        chatNpc(quiz, "So the Guildmaster sent you? You must be after the dragon then.")
        chatPlayer(quiz, "What can you tell me about Elvarg?")
        chatNpc(
            neutral,
            "Elvarg destroyed the island of Crandor years ago. Many brave souls tried to stop her.",
        )
        chatNpc(sad, "All failed. The island is cursed.")
        chatPlayer(quiz, "How do I get there?")
        chatNpc(
            neutral,
            "First you need a map. The map to Crandor was split into three pieces and hidden.",
        )
        chatNpc(neutral, "One piece is in Melzar's Maze - you'll need a key from the Guildmaster.")
        chatNpc(neutral, "One piece is held by Thalzar's kin in the Dwarven Mine.")
        chatNpc(
            neutral,
            "The last piece was taken by the goblins who killed Lozar. Check their village.",
        )
        chatNpc(
            quiz,
            "Once you have all three pieces, bring them to me and I'll make you the complete map.",
        )
        chatPlayer(happy, "I'll find those map pieces!")
        access.setQuestStage(QuestList.dragon_slayer_i, 2)
    }

    private suspend fun Dialogue.oziachMapPiecesDialogue() {
        val hasPiece1 = player.inv.contains(dragon_slayer_objs.mappart1)
        val hasPiece2 = player.inv.contains(dragon_slayer_objs.mappart2)
        val hasPiece3 = player.inv.contains(dragon_slayer_objs.mappart3)

        if (hasPiece1 && hasPiece2 && hasPiece3) {
            chatNpc(quiz, "Do you have all three map pieces?")
            chatPlayer(happy, "Yes! I have them all.")
            chatNpc(happy, "Excellent! Let me put them together...")

            // Remove map pieces and give complete map
            player.invDel(player.inv, dragon_slayer_objs.mappart1, 1)
            player.invDel(player.inv, dragon_slayer_objs.mappart2, 1)
            player.invDel(player.inv, dragon_slayer_objs.mappart3, 1)

            val addedMap = player.invAdd(player.inv, dragon_slayer_objs.dragonmap, 1).success
            if (addedMap) {
                chatNpc(happy, "There! The complete map to Crandor. Now you need a ship.")
                chatNpc(
                    neutral,
                    "There's a ship in Port Sarim, the Lady Lumbridge. It's in bad shape though.",
                )
                chatNpc(
                    neutral,
                    "You'll need to find a captain. Ned in Draynor Village used to be a sailor.",
                )
                access.setQuestStage(QuestList.dragon_slayer_i, 3)
            } else {
                chatNpc(
                    sad,
                    "You don't have room for the map. Clear an inventory slot and speak to me again.",
                )
            }
        } else {
            chatNpc(quiz, "Have you found all three map pieces yet?")
            chatPlayer(sad, "Not yet.")
            val missing = mutableListOf<String>()
            if (!hasPiece1) missing += "Melzar's Maze"
            if (!hasPiece2) missing += "Thalzar's kin"
            if (!hasPiece3) missing += "Lozar's remains (goblin village)"
            chatNpc(
                neutral,
                "You still need to find the pieces from: ${missing.joinToString(", ")}",
            )
        }
    }

    private suspend fun Dialogue.oziachShipDialogue() {
        chatNpc(quiz, "Have you found a way to Crandor?")
        chatPlayer(neutral, "I'm still working on getting a ship.")
        chatNpc(
            neutral,
            "Talk to Ned in Draynor. He might agree to captain the Lady Lumbridge for you.",
        )
        chatNpc(neutral, "You'll need to pay for repairs too.")
    }

    private suspend fun Dialogue.oziachReadyToSailDialogue() {
        chatNpc(quiz, "Ready to face Elvarg?")
        chatPlayer(happy, "Yes! I have a ship ready.")
        chatNpc(
            neutral,
            "Remember: take an anti-dragon shield. Without it, her fire will kill you.",
        )
        chatNpc(neutral, "Duke Horacio in Lumbridge Castle has one you can use.")
        chatPlayer(happy, "I'll get one before I go. Thanks for your help!")
    }

    private suspend fun Dialogue.oziachFinishedDialogue() {
        chatNpc(happy, "Well done, Dragon Slayer! Elvarg is finally defeated.")
        chatNpc(neutral, "I can now sell you rune platebodies. They require 40 Defence to wear.")
        chatPlayer(quiz, "How much?")
        chatNpc(neutral, "84,000 gold pieces each.")
    }

    // ==================== Elvarg Dialogue ====================

    private suspend fun ProtectedAccess.startElvargDialogue(npc: Npc) {
        startDialogue(npc) {
            when (getQuestStage(QuestList.dragon_slayer_i)) {
                in 0..3 -> {
                    chatNpc(angry, "ROOOOAAAAARRR!")
                    chatPlayer(sad, "I should get the map pieces and find a way here first...")
                }
                4 -> {
                    chatNpc(angry, "ROOOOAAAAARRR!")
                    chatPlayer(angry, "Your reign of terror ends today, dragon!")
                    // Combat starts automatically after dialogue
                }
                else -> {
                    // Elvarg respawns after quest completion
                    chatNpc(angry, "ROOOOAAAAARRR!")
                    chatPlayer(angry, "Time to hunt another dragon!")
                }
            }
        }
    }
}
