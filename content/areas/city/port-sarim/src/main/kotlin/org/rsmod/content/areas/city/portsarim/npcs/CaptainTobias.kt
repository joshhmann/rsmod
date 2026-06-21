package org.rsmod.content.areas.city.portsarim.npcs

import jakarta.inject.Inject
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
    override fun ScriptContext.startup() {
        onOpNpc1(portsarim_npcs.captain_tobias) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) { tobiasDialogue() }
    }

    private suspend fun Dialogue.tobiasDialogue() {
        chatNpc(happy, "Hello there, adventurer! Would you like to sail to Karamja?")
        val choice = choice3("Yes, please take me to Karamja.", 1, "How much does it cost?", 2, "No thanks.", 3)
        when (choice) {
            1 -> sailToKaramja()
            2 -> showCost()
            3 -> chatNpc(happy, "Safe travels!")
        }
    }

    private suspend fun Dialogue.sailToKaramja() {
        chatPlayer(quiz, "Yes, please take me to Karamja.")
        chatNpc(neutral, "The trip costs 30 gold pieces. Are you sure you want to go?")
        val confirm = choice2("Yes, I have 30 gold.", 1, "No, I'll come back later.", 2)
        when (confirm) {
            1 -> {
                if (player.inv.contains(TobiasObjs.coins, 30)) {
                    player.inv.del(TobiasObjs.coins, 30)
                    chatNpc(happy, "Enjoy your trip to Karamja!")
                    mes("You pay 30 gold and board the ship.")
                    teleport(CoordGrid(0, 46, 49, 15, 32))
                    mes("You arrive safely at Karamja Musa Point.")
                } else {
                    chatNpc(sad, "You don't have enough gold! The trip costs 30 gold pieces.")
                }
            }
            2 -> chatNpc(happy, "Come back when you're ready to travel!")
        }
    }

    private suspend fun Dialogue.showCost() {
        chatPlayer(quiz, "How much does it cost?")
        chatNpc(neutral, "The passage to Karamja costs 30 gold pieces.")
        chatNpc(happy, "It's a fair price for a safe journey across the sea!")
    }
}
