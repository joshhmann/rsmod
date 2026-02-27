package org.rsmod.content.areas.city.portsarim.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.portsarim.configs.portsarim_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Klarense - Shipwright in Port Sarim.
 *
 * Sells the Lady Lumbridge ship for 2,000 gold (Dragon Slayer I quest). Location: Port Sarim docks
 * (near the ship).
 */
class Klarense @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(portsarim_npcs.klarense) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) { klarenseDialogue(npc) }
    }

    private suspend fun Dialogue.klarenseDialogue(npc: Npc) {
        chatNpc(quiz, "Ah, I see you're interested in my fine ship here.")
        chatNpc(happy, "She's called the Lady Lumbridge. Beautiful, isn't she?")

        val choice =
            choice3("Yes, she's a fine ship.", 1, "Is she for sale?", 2, "I don't really care.", 3)

        when (choice) {
            1 -> complimentShip(npc)
            2 -> askForSale(npc)
            3 -> dontCare(npc)
        }
    }

    private suspend fun Dialogue.complimentShip(npc: Npc) {
        chatPlayer(happy, "Yes, she's a fine ship.")
        chatNpc(happy, "Aye, that she is! I've sailed the seas in her many times.")
        chatNpc(quiz, "Are you interested in buying her?")

        val choice = choice2("Yes, how much?", 1, "No, not really.", 2)
        when (choice) {
            1 -> askForSale(npc)
            2 -> notInterested(npc)
        }
    }

    private suspend fun Dialogue.askForSale(npc: Npc) {
        chatPlayer(quiz, "Is she for sale?")
        chatNpc(happy, "I could sell you a ship... for 2,000 gold coins.")
        chatNpc(quiz, "What do you say?")

        val choice = choice2("I'll buy her!", 1, "That's too expensive.", 2)
        when (choice) {
            1 -> attemptPurchase(npc)
            2 -> tooExpensive(npc)
        }
    }

    private suspend fun Dialogue.attemptPurchase(npc: Npc) {
        chatPlayer(happy, "I'll buy her!")
        // TODO: Check for 2,000 gold and complete Dragon Slayer I ship purchase
        // For now, just acknowledge the intent
        chatNpc(happy, "Excellent! Come back when you have 2,000 gold coins ready.")
        chatNpc(quiz, "I'll need to see the gold before I hand over the keys.")
    }

    private suspend fun Dialogue.tooExpensive(npc: Npc) {
        chatPlayer(sad, "That's too expensive.")
        chatNpc(neutral, "Well, a fine ship like this doesn't come cheap.")
        chatNpc(quiz, "Come back if you change your mind.")
    }

    private suspend fun Dialogue.dontCare(npc: Npc) {
        chatPlayer(angry, "I don't really care.")
        chatNpc(sad, "Well, no need to be rude about it.")
        chatNpc(neutral, "If you're not interested in ships, why are you bothering me?")
    }

    private suspend fun Dialogue.notInterested(npc: Npc) {
        chatPlayer(neutral, "No, not really.")
        chatNpc(neutral, "Fair enough. She's not for everyone.")
        chatNpc(quiz, "Come back if you change your mind.")
    }
}
