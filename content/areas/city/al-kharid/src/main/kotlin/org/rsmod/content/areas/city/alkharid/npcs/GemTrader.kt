package org.rsmod.content.areas.city.alkharid.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpc3
import org.rsmod.api.shops.Shops
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_invs
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class GemTrader @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(al_kharid_npcs.gem_trader) { shopDialogue(it.npc) }
        onOpNpc3(al_kharid_npcs.gem_trader) { player.openShop(it.npc) }
    }

    private fun Player.openShop(npc: Npc) {
        shops.open(this, npc, "Gem Trader", al_kharid_invs.gem_trader)
    }

    private suspend fun ProtectedAccess.shopDialogue(npc: Npc) =
        startDialogue(npc) { gemTraderDialogue(npc) }

    private suspend fun Dialogue.gemTraderDialogue(npc: Npc) {
        chatNpc(happy, "I deal in gems. Would you like to trade?")
        val choice = choice2("Yes please.", 1, "No thank you.", 2)
        if (choice == 1) {
            player.openShop(npc)
        } else {
            chatPlayer(neutral, "No thank you.")
        }
    }
}
