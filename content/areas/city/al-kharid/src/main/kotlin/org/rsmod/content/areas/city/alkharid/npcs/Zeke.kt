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

class Zeke @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(al_kharid_npcs.zeke) { shopDialogue(it.npc) }
        onOpNpc3(al_kharid_npcs.zeke) { player.openShop(it.npc) }
    }

    private fun Player.openShop(npc: Npc) {
        shops.open(this, npc, "Zeke's Superior Scimitars", al_kharid_invs.zeke_scimitar)
    }

    private suspend fun ProtectedAccess.shopDialogue(npc: Npc) =
        startDialogue(npc) { zekeDialogue(npc) }

    private suspend fun Dialogue.zekeDialogue(npc: Npc) {
        chatNpc(happy, "A thousand greetings! Would you like to buy a scimitar?")
        val choice =
            choice3("Yes please.", 1, "Do you sell any dragon scimitars?", 2, "No thank you.", 3)
        when (choice) {
            1 -> player.openShop(npc)
            2 -> dragonScimitarDialogue()
            3 -> chatPlayer(neutral, "No thank you.")
        }
    }

    private suspend fun Dialogue.dragonScimitarDialogue() {
        chatPlayer(quiz, "Do you sell any dragon scimitars?")
        chatNpc(
            laugh,
            "The banana-brained nitwits who make them would never sell any to me. " +
                "Seriously, you'll be a monkey's uncle before you hold a Dragon Scimitar.",
        )
    }
}
