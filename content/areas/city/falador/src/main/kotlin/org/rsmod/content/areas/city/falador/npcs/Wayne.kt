package org.rsmod.content.areas.city.falador.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpc3
import org.rsmod.api.shops.Shops
import org.rsmod.content.areas.city.falador.configs.falador_invs
import org.rsmod.content.areas.city.falador.configs.falador_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Wayne @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(falador_npcs.wayne) { shopDialogue(it.npc) }
        onOpNpc3(falador_npcs.wayne) { player.openShop(it.npc) }
    }

    private fun Player.openShop(npc: Npc) {
        shops.open(this, npc, "Wayne's Chains", falador_invs.wayne_chains)
    }

    private suspend fun ProtectedAccess.shopDialogue(npc: Npc) =
        startDialogue(npc) { wayneDialogue(npc) }

    private suspend fun Dialogue.wayneDialogue(npc: Npc) {
        chatNpc(happy, "Welcome to Wayne's Chains! Would you like to buy some chainmail?")
        val choice = choice2("Yes please.", 1, "No thanks.", 2)
        if (choice == 1) {
            player.openShop(npc)
        } else {
            chatPlayer(neutral, "No thanks.")
        }
    }
}
