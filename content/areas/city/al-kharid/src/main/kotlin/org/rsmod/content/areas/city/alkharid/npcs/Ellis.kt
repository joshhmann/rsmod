package org.rsmod.content.areas.city.alkharid.npcs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpc3
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_npcs
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_objs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Ellis the Tanner - converts hides into leather for a fee. Soft leather: 1 coin per hide Hard
 * leather: 3 coins per hide
 */
class Ellis : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(al_kharid_npcs.ellis_tanner) { startDialogue(it.npc) }
        onOpNpc3(al_kharid_npcs.ellis_tanner) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) { ellisDialogue(npc) }

    private suspend fun Dialogue.ellisDialogue(npc: Npc) {
        chatNpc(happy, "Hello there! Want to have some hides tanned?")
        val choice =
            choice3(
                "Can I have some soft leather?",
                1,
                "Can I have some hard leather?",
                2,
                "No thanks.",
                3,
            )
        when (choice) {
            1 -> tanSoftLeather()
            2 -> tanHardLeather()
            3 -> chatPlayer(neutral, "No thanks.")
        }
    }

    private suspend fun Dialogue.tanSoftLeather() {
        chatPlayer(quiz, "Can I have some soft leather?")
        // Check if player has cowhides and coins
        if (al_kharid_objs.cow_hide !in player.inv) {
            chatNpc(sad, "You don't have any cowhides for me to tan.")
            return
        }
        if (objs.coins !in player.inv) {
            chatNpc(sad, "You don't have any coins. I charge 1 coin per hide.")
            return
        }

        // Count how many we can tan (limited by hides, coins, and inventory space)
        val maxHides = access.invTotal(player.inv, al_kharid_objs.cow_hide)
        val maxCoins = access.invTotal(player.inv, objs.coins)
        val freeSpaces = player.inv.freeSpace()

        val amountToTan = minOf(maxHides, maxCoins, freeSpaces)

        if (amountToTan == 0) {
            if (freeSpaces == 0) {
                chatNpc(sad, "You don't have enough inventory space.")
            } else {
                chatNpc(sad, "You don't have enough coins. I charge 1 coin per hide.")
            }
            return
        }

        // Remove cowhides and coins, add soft leather
        player.invDel(player.inv, al_kharid_objs.cow_hide, amountToTan)
        player.invDel(player.inv, objs.coins, amountToTan)

        for (i in 0 until amountToTan) {
            if (!player.invAdd(player.inv, al_kharid_objs.leather, 1).success) {
                // Inventory full, drop on ground
                break
            }
        }

        chatNpc(happy, "There you go! I've tanned $amountToTan hides into soft leather.")
    }

    private suspend fun Dialogue.tanHardLeather() {
        chatPlayer(quiz, "Can I have some hard leather?")
        if (al_kharid_objs.cow_hide !in player.inv) {
            chatNpc(sad, "You don't have any cowhides for me to tan.")
            return
        }
        if (objs.coins !in player.inv) {
            chatNpc(sad, "You don't have any coins. I charge 3 coins per hide for hard leather.")
            return
        }

        val maxHides = access.invTotal(player.inv, al_kharid_objs.cow_hide)
        val maxCoins = access.invTotal(player.inv, objs.coins) / 3 // 3 coins per hide
        val freeSpaces = player.inv.freeSpace()

        val amountToTan = minOf(maxHides, maxCoins, freeSpaces)

        if (amountToTan == 0) {
            if (freeSpaces == 0) {
                chatNpc(sad, "You don't have enough inventory space.")
            } else {
                chatNpc(
                    sad,
                    "You don't have enough coins. I charge 3 coins per hide for hard leather.",
                )
            }
            return
        }

        val cost = amountToTan * 3
        player.invDel(player.inv, al_kharid_objs.cow_hide, amountToTan)
        player.invDel(player.inv, objs.coins, cost)

        for (i in 0 until amountToTan) {
            if (!player.invAdd(player.inv, al_kharid_objs.hard_leather, 1).success) {
                break
            }
        }

        chatNpc(happy, "There you go! I've tanned $amountToTan hides into hard leather.")
    }
}
