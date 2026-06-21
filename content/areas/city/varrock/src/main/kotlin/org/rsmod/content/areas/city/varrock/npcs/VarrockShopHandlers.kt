package org.rsmod.content.areas.city.varrock.npcs

import jakarta.inject.Inject
import org.rsmod.api.script.onOpNpc2
import org.rsmod.game.entity.Player
import org.rsmod.api.shops.Shops
import org.rsmod.content.areas.city.varrock.configs.varrock_invs
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class VarrockShopHandlers @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc2(varrock_npcs.varrock_shop_keeper) { player.openGeneralStore(it.npc) }
        onOpNpc2(varrock_npcs.varrock_shop_assistant) { player.openGeneralStore(it.npc) }
        onOpNpc2(varrock_npcs.lowe) { player.openArcheryShop(it.npc) }
        onOpNpc2(varrock_npcs.horvik) { player.openArmourShop(it.npc) }
        onOpNpc2(varrock_npcs.thessalia) { player.openClothesShop(it.npc) }
        onOpNpc2(varrock_npcs.zaff) { player.openStaffShop(it.npc) }
    }

    private fun Player.openGeneralStore(npc: Npc) {
        shops.open(this, npc, "Varrock General Store", varrock_invs.general_store)
    }

    private fun Player.openArcheryShop(npc: Npc) {
        shops.open(this, npc, "Lowe's Archery Emporium", varrock_invs.archery_shop)
    }

    private fun Player.openArmourShop(npc: Npc) {
        shops.open(this, npc, "Horvik's Armour Shop", varrock_invs.armour_shop)
    }

    private fun Player.openClothesShop(npc: Npc) {
        shops.open(this, npc, "Thessalia's Fine Clothes", varrock_invs.clothes_shop)
    }

    private fun Player.openStaffShop(npc: Npc) {
        shops.open(this, npc, "Zaff's Superior Staffs!", varrock_invs.staff_shop)
    }
}
