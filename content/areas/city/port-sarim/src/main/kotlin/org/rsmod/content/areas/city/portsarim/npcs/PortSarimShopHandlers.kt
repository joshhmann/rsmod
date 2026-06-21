package org.rsmod.content.areas.city.portsarim.npcs

import jakarta.inject.Inject
import org.rsmod.api.script.onOpNpc2
import org.rsmod.game.entity.Player
import org.rsmod.api.shops.Shops
import org.rsmod.content.areas.city.portsarim.configs.portsarim_invs
import org.rsmod.content.areas.city.portsarim.configs.portsarim_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class PortSarimShopHandlers @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc2(portsarim_npcs.portsarim_shop_keeper) { player.openGeneralStore(it.npc) }
        onOpNpc2(portsarim_npcs.portsarim_shop_assistant) { player.openGeneralStore(it.npc) }
        onOpNpc2(portsarim_npcs.brian) { player.openBrianBattleaxes(it.npc) }
        onOpNpc2(portsarim_npcs.wydin) { player.openWydinFood(it.npc) }
    }

    private fun Player.openGeneralStore(npc: Npc) {
        shops.open(this, npc, "Port Sarim General Store", portsarim_invs.general_store)
    }

    private fun Player.openBrianBattleaxes(npc: Npc) {
        shops.open(this, npc, "Brian's Battleaxe Bazaar", portsarim_invs.brian_shop)
    }

    private fun Player.openWydinFood(npc: Npc) {
        shops.open(this, npc, "Wydin's Food Store", portsarim_invs.wydin_store)
    }
}
