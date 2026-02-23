@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.falador.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.shops.config.ShopParams
import org.rsmod.api.type.editors.inv.InvEditor
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.inv.InvReferences
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvStackType

typealias falador_invs = FaladorInvs

object FaladorInvs : InvReferences() {
    val wayne_chains = find("wayne_chains")
    val flynn_maces = find("flynn_maces")
    val general_store = find("falador_general")
}

object FaladorInvBuilder : InvEditor() {
    init {
        // Wayne's Chains - Chainbody shop
        edit(falador_invs.wayne_chains) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(objs.bronze_chainbody, count = 5, restockCycles = 100)
            stock += stock(objs.iron_chainbody, count = 3, restockCycles = 200)
            stock += stock(objs.steel_chainbody, count = 2, restockCycles = 400)
            // Note: black_chainbody not in BaseObjs
            stock += stock(objs.mithril_chainbody, count = 1, restockCycles = 3000)
            stock += stock(objs.adamant_chainbody, count = 1, restockCycles = 6000)
        }

        // Flynn's Maces - Mace shop
        edit(falador_invs.flynn_maces) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(objs.bronze_mace, count = 5, restockCycles = 100)
            stock += stock(objs.iron_mace, count = 3, restockCycles = 200)
            stock += stock(objs.steel_mace, count = 2, restockCycles = 400)
            stock += stock(objs.mithril_mace, count = 1, restockCycles = 3000)
            stock += stock(objs.adamant_mace, count = 1, restockCycles = 6000)
        }

        // Falador General Store
        edit(falador_invs.general_store) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(objs.pot_empty, count = 5, restockCycles = 100)
            stock += stock(objs.jug_empty, count = 2, restockCycles = 100)
            stock += stock(objs.shears, count = 2, restockCycles = 100)
            stock += stock(objs.bucket_empty, count = 3, restockCycles = 100)
            stock += stock(objs.bowl_empty, count = 2, restockCycles = 100)
            stock += stock(objs.cake_tin, count = 2, restockCycles = 100)
            stock += stock(objs.tinderbox, count = 2, restockCycles = 100)
            stock += stock(objs.chisel, count = 2, restockCycles = 100)
            stock += stock(objs.hammer, count = 5, restockCycles = 100)
            stock += stock(objs.newcomer_map, count = 5, restockCycles = 100)
        }
    }
}

internal object FaladorShopNpcEditor : NpcEditor() {
    init {
        edit(falador_npcs.wayne) {
            param[ShopParams.shop_sell_percentage] = 1000
            param[ShopParams.shop_buy_percentage] = 600
            param[ShopParams.shop_change_percentage] = 20
        }
        edit(falador_npcs.flynn) {
            param[ShopParams.shop_sell_percentage] = 1000
            param[ShopParams.shop_buy_percentage] = 600
            param[ShopParams.shop_change_percentage] = 20
        }
    }
}
