package org.rsmod.content.skills.prayer.scripts

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * F2P Church Priests Found in Lumbridge and Varrock churches Provides religious dialogue and
 * blessings
 */
class Priest @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // Lumbridge Church Priest (Father Aereck) is handled in RestlessGhost.kt
        // onOpNpc1(PrayerNpcRefs.lumbridge_priest) { lumbridgePriestDialogue(it.npc) }

        // Varrock Church Priest (Father Lawrence) is handled in RomeoJuliet.kt
        // onOpNpc1(PrayerNpcRefs.varrock_priest) { varrockPriestDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.lumbridgePriestDialogue(npc: Npc) =
        startDialogue(npc) { lumbridgePriestChat(npc) }

    private suspend fun Dialogue.lumbridgePriestChat(npc: Npc) {
        chatNpc(happy, "Greetings, child. Welcome to the church.")
        chatNpc(neutral, "I see you have come to pray.")
        val choice = choice3("Can you bless me?", 1, "Tell me about the gods.", 2, "Goodbye.", 3)
        when (choice) {
            1 -> {
                chatPlayer(quiz, "Can you bless me?")
                chatNpc(happy, "May Saradomin bless you on your journey.")
                chatNpc(happy, "Your prayer will restore faster when you pray at an altar.")
            }
            2 -> {
                chatPlayer(quiz, "Tell me about the gods.")
                chatNpc(neutral, "Saradomin is the god of order and wisdom.")
                chatNpc(neutral, "He brings light to the darkness of this world.")
                chatNpc(happy, "Pray to him at the altar for guidance.")
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }

    private suspend fun ProtectedAccess.varrockPriestDialogue(npc: Npc) =
        startDialogue(npc) { varrockPriestChat(npc) }

    private suspend fun Dialogue.varrockPriestChat(npc: Npc) {
        chatNpc(happy, "Welcome to the church of Saradomin.")
        chatNpc(neutral, "How may I help you today?")
        val choice = choice3("Can you bless me?", 1, "Tell me about the church.", 2, "Goodbye.", 3)
        when (choice) {
            1 -> {
                chatPlayer(quiz, "Can you bless me?")
                chatNpc(happy, "Saradomin's blessings upon you.")
                chatNpc(happy, "Pray at the altar to restore your prayer points.")
            }
            2 -> {
                chatPlayer(quiz, "Tell me about the church.")
                chatNpc(neutral, "This church has stood in Varrock for centuries.")
                chatNpc(neutral, "Many adventurers come here to pray.")
                chatNpc(happy, "The altar will restore your prayer points.")
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }
}

object PrayerNpcRefs : NpcReferences() {
    val lumbridge_priest = find("father_aereck")
    val varrock_priest = find("father_lawrence")
}
