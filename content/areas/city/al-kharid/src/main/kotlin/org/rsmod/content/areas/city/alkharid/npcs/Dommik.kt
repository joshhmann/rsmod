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

class Dommik @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(al_kharid_npcs.dommik) { shopDialogue(it.npc) }
        onOpNpc3(al_kharid_npcs.dommik) { player.openShop(it.npc) }
    }

    private fun Player.openShop(npc: Npc) {
        shops.open(this, npc, "Dommik's Crafting Store", al_kharid_invs.dommik_crafting)
    }

    private suspend fun ProtectedAccess.shopDialogue(npc: Npc) =
        startDialogue(npc) { dommikDialogue(npc) }

    private suspend fun Dialogue.dommikDialogue(npc: Npc) {
        chatNpc(happy, "Would you like to buy some crafting equipment?")
        val choice = choice2("Yes please.", 1, "No thank you.", 2)
        if (choice == 1) {
            player.openShop(npc)
        } else {
            chatPlayer(neutral, "No thank you.")
        }
    }
}
