package org.rsmod.content.areas.city.varrock.npcs

import jakarta.inject.Inject
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Haig Halen - Varrock Museum Curator
 *
 * Located in Varrock Museum, provides information about the museum's history and exhibits.
 * Also handles the Shield of Arrav quest certificate exchange.
 */
class HaigHalen @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.curator) { haigHalenDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.haigHalenDialogue(npc: Npc) =
        startDialogue(npc) {
            val shieldStage = getQuestStage(QuestList.shield_of_arrav)
            val hasShield1 = player.inv.contains(SOA_ITEMS.arravshield1)
            val hasShield2 = player.inv.contains(SOA_ITEMS.arravshield2)
            val hasAnyShieldStage = shieldStage >= 5 && (hasShield1 || hasShield2)

            if (hasAnyShieldStage) {
                arravCertificateDialogue(hasShield1, hasShield2)
                return@startDialogue
            }

            defaultDialogue()
        }

    private suspend fun Dialogue.defaultDialogue() {
        chatNpc(happy, "Welcome to the Varrock Museum, traveller!")
        chatNpc(
            happy,
            "I am Haig Halen, curator of this fine establishment. Are you interested in learning about our exhibits?",
        )

        val choice =
            choice3(
                "Tell me about the museum's history.",
                1,
                "What exhibits do you have?",
                2,
                "I'm just browsing, thanks.",
                3,
            )

        when (choice) {
            1 -> {
                chatPlayer(quiz, "Tell me about the museum's history.")
                chatNpc(
                    happy,
                    "The Varrock Museum was founded many years ago to preserve the rich history of Gielinor.",
                )
                chatNpc(
                    happy,
                    "We have specimens and artifacts from all across the land, from the lowest depths of the earth to the highest peaks.",
                )
                chatNpc(
                    happy,
                    "Our paleontology section contains fossils of ancient creatures that once roamed these lands.",
                )
                chatNpc(
                    happy,
                    "If you're interested in natural history, you should definitely explore our dig site exhibit!",
                )
            }
            2 -> {
                chatPlayer(quiz, "What exhibits do you have?")
                chatNpc(
                    happy,
                    "We have several fascinating exhibits! On the ground floor, you'll find our natural history section.",
                )
                chatNpc(
                    happy,
                    "Upstairs is our art gallery, featuring works from talented artists across Gielinor.",
                )
                chatNpc(
                    happy,
                    "And if you go down to the basement, you'll find our archaeological dig site - quite popular with adventurers!",
                )
                chatNpc(
                    happy,
                    "We also have a section dedicated to the history of Varrock and the surrounding areas.",
                )
            }
            3 -> {
                chatPlayer(neutral, "I'm just browsing, thanks.")
                chatNpc(
                    happy,
                    "Very well! Feel free to explore at your leisure. If you have any questions, don't hesitate to ask.",
                )
            }
        }
    }

    private suspend fun Dialogue.arravCertificateDialogue(hasShield1: Boolean, hasShield2: Boolean) {
        if (hasShield1 && hasShield2) {
            // Player has BOTH shield halves
            chatPlayer(quiz, "Curator, I have both halves of the Shield of Arrav here!")
            chatNpc(
                shocked,
                "The Shield of Arrav! Both halves! Goodness, the Museum has been searching for that for years!",
            )
            chatNpc(
                happy,
                "The late King Roald II offered a reward for it years ago! Let me have a look at it.",
            )

            objbox(SOA_ITEMS.arravshield1, "The curator peers at the shield.")

            chatNpc(happy, "This is incredible! That shield has been missing for over twenty-five years!")
            chatNpc(
                happy,
                "Leave the shield here with me and I'll write you a certificate saying that you have returned the shield, so that you can claim your reward from the King.",
            )
            chatPlayer(quiz, "Can I have two certificates please?")
            chatNpc(happy, "Yes, certainly. Please hand over the shield halves.")

            // Remove both shield halves and give combined certificate
            access.invDel(access.inv, SOA_ITEMS.arravshield1, 1, strict = false)
            access.invDel(access.inv, SOA_ITEMS.arravshield2, 1, strict = false)
            val added = access.invAddOrDrop(objRepo, SOA_ITEMS.arravcertificate, 1)
            if (added) {
                objbox(SOA_ITEMS.arravcertificate, "The curator writes out a reward certificate and hands it to you.")
            }
            chatNpc(
                happy,
                "Take this certificate to King Roald in the palace to claim your reward. Well done, adventurer!",
            )
        } else {
            // Player has only ONE shield half
            val shieldHalf = if (hasShield1) SOA_ITEMS.arravshield1 else SOA_ITEMS.arravshield2
            val halfCert = if (hasShield1) SOA_ITEMS.arravcertificate_lft else SOA_ITEMS.arravcertificate_rht

            chatPlayer(quiz, "I have half of the Shield of Arrav here. Can I get a reward?")
            chatNpc(
                shocked,
                "The Shield of Arrav! Goodness, the Museum has been searching for that for years!",
            )
            chatNpc(
                happy,
                "The late King Roald II offered a reward for it years ago! Let me have a look at it first.",
            )

            objbox(shieldHalf, "The curator peers at the shield half.")

            chatNpc(happy, "This is incredible! That shield has been missing for over twenty-five years!")
            chatNpc(
                happy,
                "Leave the shield here with me and I'll write you a certificate saying that you have returned the shield.",
            )
            chatPlayer(quiz, "Can I have two certificates please?")
            chatNpc(happy, "Yes, certainly. Please hand over the shield.")

            objbox(shieldHalf, "You hand over the shield half.")

            val removed = access.invDel(access.inv, shieldHalf, 1, strict = false).success

            if (removed) {
                val added = access.invAddOrDrop(objRepo, halfCert, 1)
                if (added) {
                    objbox(halfCert, "The curator writes out a half-certificate.")
                }
                chatNpc(
                    neutral,
                    "Of course, you won't actually be able to claim the reward with only half the reward certificate...",
                )
                chatPlayer(
                    angry,
                    "What? I went through a lot of trouble to get that shield piece and now you tell me it was for nothing?",
                )
                chatNpc(
                    neutral,
                    "Well, if you were to get me the other half of the shield, I could give you the other half of the reward certificate.",
                )
                chatNpc(
                    neutral,
                    "It's rumoured to be in the possession of the other gang. Beyond that, I can't help you.",
                )
                chatPlayer(neutral, "Okay, I'll see what I can do.")
            }
        }
    }

    internal object SOA_ITEMS : org.rsmod.api.type.refs.obj.ObjReferences() {
        val arravshield1 = find("arravshield1")
        val arravshield2 = find("arravshield2")
        val arravcertificate = find("arravcertificate")
        val arravcertificate_lft = find("arravcertificate_lft")
        val arravcertificate_rht = find("arravcertificate_rht")
    }
}
