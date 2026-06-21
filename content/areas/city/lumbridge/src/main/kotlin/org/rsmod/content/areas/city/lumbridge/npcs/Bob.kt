package org.rsmod.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.shops.Shops
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_invs
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Bob @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.bob) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Welcome to Bob's Brilliant Axes! Would you like to see my wares?")
            val choice =
                choice2(
                    "Yes please.",
                    1,
                    "No, thank you. I'm just looking.",
                    2,
                )
            when (choice) {
                1 -> player.openAxeShop(npc)
                2 -> declineDialogue()
            }
        }

    private suspend fun Dialogue.declineDialogue() {
        chatPlayer(neutral, "No, thank you. I'm just looking.")
        chatNpc(happy, "That's fine. Feel free to have a look around.")
    }

    private fun Player.openAxeShop(npc: Npc) {
        shops.open(this, npc, "Bob's Brilliant Axes", lumbridge_invs.axeshop)
    }
}
