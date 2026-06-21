package org.rsmod.content.areas.city.portsarim.npcs

import jakarta.inject.Inject
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.content.areas.city.portsarim.configs.portsarim_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

object TobiasObjs : ObjReferences() {
    val coins = find("coins")
}

class CaptainTobias @Inject constructor() : PluginScript() {
    private var sailConfirmed = false

    override fun ScriptContext.startup() {
        onOpNpc1(portsarim_npcs.captain_tobias) { handleCaptain(it.npc) }
    }

    private suspend fun ProtectedAccess.handleCaptain(npc: Npc) {
        sailConfirmed = false
        startDialogue(npc) { tobiasDialogue() }
        if (sailConfirmed) {
            doSail()
        }
    }

    private suspend fun Dialogue.tobiasDialogue() {
        chatNpc(happy, "Hello there, adventurer! Would you like to sail to Karamja?")
        val choice = choice3("Yes, please take me to Karamja.", 1, "How much does it cost?", 2, "No thanks.", 3)
        when (choice) {
            1 -> confirmSail()
            2 -> showCost()
            3 -> chatNpc(happy, "Safe travels!")
        }
    }

    private suspend fun Dialogue.confirmSail() {
        chatPlayer(quiz, "Yes, please take me to Karamja.")
        chatNpc(neutral, "The trip costs 30 gold pieces. Are you sure you want to go?")
        val confirm = choice2("Yes, I have 30 gold.", 1, "No, I will come back later.", 2)
        when (confirm) {
            1 -> {
                chatNpc(happy, "Enjoy your trip to Karamja!")
                sailConfirmed = true
            }
            2 -> chatNpc(happy, "Come back when you are ready to travel!")
        }
    }

    private suspend fun ProtectedAccess.doSail() {
        val coins = player.inv.filterNotNull { it.id == TobiasObjs.coins.id }.sumOf { it.count }
        if (coins >= 30) {
            invDel(player.inv, TobiasObjs.coins, 30)
            mes("You pay 30 gold and board the ship.")
            teleport(CoordGrid(0, 46, 49, 15, 32))
            mes("You arrive safely at Karamja Musa Point.")
        } else {
            mes("You do not have enough gold! The trip costs 30 gold pieces.")
        }
    }

    private suspend fun Dialogue.showCost() {
        chatPlayer(quiz, "How much does it cost?")
        chatNpc(neutral, "The passage to Karamja costs 30 gold pieces.")
        chatNpc(happy, "It is a fair price for a safe journey across the sea!")
    }
}
