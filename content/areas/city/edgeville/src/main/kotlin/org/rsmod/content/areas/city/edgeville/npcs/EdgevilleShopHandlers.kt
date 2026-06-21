package org.rsmod.content.areas.city.edgeville.npcs

import jakarta.inject.Inject
import org.rsmod.api.script.onOpNpc2
import org.rsmod.game.entity.Player
import org.rsmod.api.shops.Shops
import org.rsmod.content.areas.city.edgeville.configs.edgeville_invs
import org.rsmod.content.areas.city.edgeville.configs.edgeville_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class EdgevilleShopHandlers @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc2(edgeville_npcs.edgeville_general_store) { player.openGeneralStore(it.npc) }
        onOpNpc2(edgeville_npcs.edgeville_shop_assistant) { player.openGeneralStore(it.npc) }
        onOpNpc2(edgeville_npcs.peksa) { player.openPeksaHelms(it.npc) }
    }

    private fun Player.openGeneralStore(npc: Npc) {
        shops.open(this, npc, "Edgeville General Store", edgeville_invs.general_store)
    }

    private fun Player.openPeksaHelms(npc: Npc) {
        shops.open(this, npc, "Peksa's Helmet Shop", edgeville_invs.peksa_shop)
    }
}
