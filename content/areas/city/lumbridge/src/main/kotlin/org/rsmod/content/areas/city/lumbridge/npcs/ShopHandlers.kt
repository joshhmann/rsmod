package org.rsmod.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.rsmod.api.script.onOpNpc2
import org.rsmod.game.entity.Player
import org.rsmod.api.shops.Shops
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_invs
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class LumbridgeShopHandlers @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc2(lumbridge_npcs.bob) { player.openBobShop(it.npc) }
        onOpNpc2(lumbridge_npcs.lumbridge_shop_keeper) { player.openGeneralStore(it.npc) }
        onOpNpc2(lumbridge_npcs.lumbridge_shop_assistant) { player.openGeneralStore(it.npc) }
    }

    private fun Player.openBobShop(npc: Npc) {
        shops.open(this, npc, "Bob's Brilliant Axes", lumbridge_invs.axeshop)
    }

    private fun Player.openGeneralStore(npc: Npc) {
        shops.open(this, npc, "Lumbridge General Store", lumbridge_invs.generalshop1)
    }
}
