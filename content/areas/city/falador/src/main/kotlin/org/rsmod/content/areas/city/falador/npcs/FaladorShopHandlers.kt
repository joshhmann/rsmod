package org.rsmod.content.areas.city.falador.npcs

import jakarta.inject.Inject
import org.rsmod.api.script.onOpNpc2
import org.rsmod.game.entity.Player
import org.rsmod.api.shops.Shops
import org.rsmod.content.areas.city.falador.configs.falador_invs
import org.rsmod.content.areas.city.falador.configs.falador_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class FaladorShopHandlers @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc2(falador_npcs.shop_keeper) { player.openGeneralStore(it.npc) }
        onOpNpc2(falador_npcs.shop_assistant) { player.openGeneralStore(it.npc) }
        onOpNpc2(falador_npcs.wayne) { player.openWayneChains(it.npc) }
        onOpNpc2(falador_npcs.flynn) { player.openFlynnMaces(it.npc) }
        onOpNpc2(falador_npcs.cassie) { player.openCassieShields(it.npc) }
        onOpNpc2(falador_npcs.herquin) { player.openHerquinGems(it.npc) }
    }

    private fun Player.openGeneralStore(npc: Npc) {
        shops.open(this, npc, "Falador General Store", falador_invs.general_store)
    }

    private fun Player.openWayneChains(npc: Npc) {
        shops.open(this, npc, "Wayne's Chains", falador_invs.wayne_chains)
    }

    private fun Player.openFlynnMaces(npc: Npc) {
        shops.open(this, npc, "Flynn's Mace Market", falador_invs.flynn_maces)
    }

    private fun Player.openCassieShields(npc: Npc) {
        shops.open(this, npc, "Cassie's Shield Shop", falador_invs.cassie_shields)
    }

    private fun Player.openHerquinGems(npc: Npc) {
        shops.open(this, npc, "Herquin's Gems", falador_invs.herquin_gems)
    }
}
