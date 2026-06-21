package org.rsmod.content.areas.city.alkharid.npcs

import jakarta.inject.Inject
import org.rsmod.api.script.onOpNpc2
import org.rsmod.game.entity.Player
import org.rsmod.api.shops.Shops
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_invs
import org.rsmod.content.areas.city.alkharid.configs.al_kharid_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class AlKharidShopHandlers @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc2(al_kharid_npcs.alkharid_shop_keeper) { player.openGeneralStore(it.npc) }
        onOpNpc2(al_kharid_npcs.alkharid_shop_assistant) { player.openGeneralStore(it.npc) }
        onOpNpc2(al_kharid_npcs.dommik) { player.openDommikCrafting(it.npc) }
        onOpNpc2(al_kharid_npcs.gem_trader) { player.openGemTrader(it.npc) }
        onOpNpc2(al_kharid_npcs.louie_legs) { player.openLouieLegs(it.npc) }
        onOpNpc2(al_kharid_npcs.ranael) { player.openRanaelSkirts(it.npc) }
        onOpNpc2(al_kharid_npcs.zeke) { player.openZekeScimitars(it.npc) }
    }

    private fun Player.openGeneralStore(npc: Npc) {
        shops.open(this, npc, "Al Kharid General Store", al_kharid_invs.general_store)
    }

    private fun Player.openDommikCrafting(npc: Npc) {
        shops.open(this, npc, "Dommik's Crafting Store", al_kharid_invs.dommik_crafting)
    }

    private fun Player.openGemTrader(npc: Npc) {
        shops.open(this, npc, "Gem Trader", al_kharid_invs.gem_trader)
    }

    private fun Player.openLouieLegs(npc: Npc) {
        shops.open(this, npc, "Louie's Armoured Legs Bazaar", al_kharid_invs.louie_legs)
    }

    private fun Player.openRanaelSkirts(npc: Npc) {
        shops.open(this, npc, "Ranael's Super Skirt Store", al_kharid_invs.ranael_skirt)
    }

    private fun Player.openZekeScimitars(npc: Npc) {
        shops.open(this, npc, "Zeke's Superior Scimitars", al_kharid_invs.zeke_scimitar)
    }
}
