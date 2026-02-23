package org.rsmod.content.areas.city.alkharid.npcs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_npcs
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_objs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Silk Trader - buys and sells silk. Players can buy silk for 3 coins (or 2 if they refuse first).
 * Can sell silk to general stores elsewhere for profit.
 */
class SilkTrader : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(al_kharid_npcs.silk_merchant) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) { silkTraderDialogue(npc) }

    private suspend fun Dialogue.silkTraderDialogue(npc: Npc) {
        chatNpc(happy, "Do you want to buy any fine silks?")
        val choice =
            choice3(
                "How much are they?",
                1,
                "No. I don't want to buy any.",
                2,
                "I have some silk to sell.",
                3,
            )
        when (choice) {
            1 -> discussPrice()
            2 -> declineFirst()
            3 -> sellSilk()
        }
    }

    private suspend fun Dialogue.discussPrice() {
        chatPlayer(quiz, "How much are they?")
        chatNpc(happy, "3 coins.")
        val choice = choice2("Okay, that sounds reasonable.", 1, "No thanks, I'll leave it.", 2)
        if (choice == 1) {
            buySilk(3)
        } else {
            chatPlayer(neutral, "No thanks, I'll leave it.")
        }
    }

    private suspend fun Dialogue.declineFirst() {
        chatPlayer(neutral, "No. I don't want to buy any.")
        chatNpc(sad, "2 coins and that's as low as I'll go.")
        val choice = choice2("Okay, I'll buy some.", 1, "No, really, I'm not interested.", 2)
        if (choice == 1) {
            buySilk(2)
        } else {
            chatPlayer(neutral, "No, really, I'm not interested.")
        }
    }

    private suspend fun Dialogue.buySilk(price: Int) {
        // Check if player has coins
        if (objs.coins !in player.inv) {
            chatPlayer(sad, "I don't have enough coins.")
            return
        }

        if (!player.inv.hasFreeSpace()) {
            chatNpc(sad, "You don't have room for any silk.")
            return
        }

        // Remove coins and add silk
        if (player.invDel(player.inv, objs.coins, price).success) {
            if (player.invAdd(player.inv, al_kharid_objs.silk, 1).success) {
                chatNpc(happy, "Pleasure doing business with you!")
            } else {
                // Refund if couldn't add silk
                player.invAdd(player.inv, objs.coins, price)
                chatNpc(sad, "You don't have room for the silk.")
            }
        } else {
            chatPlayer(sad, "I don't have enough coins.")
        }
    }

    private suspend fun Dialogue.sellSilk() {
        chatPlayer(quiz, "I have some silk to sell.")
        // Silk trader doesn't buy silk from players in the traditional sense
        // Players sell silk to general stores elsewhere for profit
        chatNpc(angry, "I don't buy silk! You should try selling it to a general store elsewhere.")
    }
}
