@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.falador.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.shops.config.ShopParams
import org.rsmod.api.type.editors.inv.InvEditor
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.inv.InvReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvStackType

typealias falador_invs = FaladorInvs

object FaladorInvs : InvReferences() {
    val wayne_chains = find("armourshop")
    val flynn_maces = find("maceshop")
    val cassie_shields = find("shieldshop")
    val herquin_gems = find("gemshop")
    val general_store = find("generalshop4")
}

object FaladorInvBuilder : InvEditor() {
    init {
        // Wayne's Chains - Chainbody shop
        edit(falador_invs.wayne_chains) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(falador_objs.bronze_chainbody, count = 5, restockCycles = 100)
            stock += stock(falador_objs.iron_chainbody, count = 3, restockCycles = 200)
            stock += stock(falador_objs.steel_chainbody, count = 2, restockCycles = 400)
            stock += stock(falador_objs.black_chainbody, count = 1, restockCycles = 1500)
            stock += stock(objs.mithril_chainbody, count = 1, restockCycles = 3000)
            stock += stock(falador_objs.adamant_chainbody, count = 1, restockCycles = 6000)
        }

        // Cassie's Shield Shop
        edit(falador_invs.cassie_shields) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(objs.bronze_sq_shield, count = 5, restockCycles = 100)
            stock += stock(falador_objs.bronze_kiteshield, count = 3, restockCycles = 200)
            stock += stock(falador_objs.iron_sq_shield, count = 3, restockCycles = 200)
            stock += stock(objs.iron_kiteshield, count = 2, restockCycles = 400)
            stock += stock(falador_objs.steel_sq_shield, count = 2, restockCycles = 400)
            stock += stock(objs.steel_kiteshield, count = 1, restockCycles = 1500)
            stock += stock(falador_objs.black_sq_shield, count = 1, restockCycles = 1500)
            stock += stock(falador_objs.black_kiteshield, count = 1, restockCycles = 1500)
            stock += stock(objs.mithril_sq_shield, count = 1, restockCycles = 3000)
            stock += stock(falador_objs.mithril_kiteshield, count = 1, restockCycles = 3000)
            stock += stock(falador_objs.adamant_sq_shield, count = 1, restockCycles = 6000)
            stock += stock(objs.adamant_kiteshield, count = 1, restockCycles = 6000)
        }

        // Flynn's Maces - Mace shop
        edit(falador_invs.flynn_maces) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(objs.bronze_mace, count = 5, restockCycles = 100)
            stock += stock(falador_objs.iron_mace, count = 3, restockCycles = 200)
            stock += stock(falador_objs.steel_mace, count = 2, restockCycles = 400)
            stock += stock(falador_objs.mithril_mace, count = 1, restockCycles = 3000)
            stock += stock(falador_objs.adamant_mace, count = 1, restockCycles = 6000)
        }

        // Falador General Store
        edit(falador_invs.general_store) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(objs.pot_empty, count = 5, restockCycles = 10)
            stock += stock(objs.jug_empty, count = 2, restockCycles = 100)
            stock += stock(objs.pack_jug_empty, count = 5, restockCycles = 20)
            stock += stock(objs.shears, count = 2, restockCycles = 100)
            stock += stock(objs.knife, count = 5, restockCycles = 100)
            stock += stock(objs.bucket_empty, count = 3, restockCycles = 10)
            stock += stock(objs.pack_bucket, count = 15, restockCycles = 10)
            stock += stock(objs.bowl_empty, count = 2, restockCycles = 50)
            stock += stock(objs.cake_tin, count = 2, restockCycles = 50)
            stock += stock(objs.tinderbox, count = 3, restockCycles = 100)
            stock += stock(objs.chisel, count = 2, restockCycles = 100)
            stock += stock(objs.hammer, count = 5, restockCycles = 100)
            stock += stock(objs.newcomer_map, count = 5, restockCycles = 100)
            stock += stock(objs.sos_security_book, count = 5, restockCycles = 100)
            stock += stock(objs.rope, count = 5, restockCycles = 100)
        }
    }
}

internal object falador_objs : ObjReferences() {
    val bronze_chainbody = find("bronze_chainbody")
    val iron_chainbody = find("iron_chainbody")
    val steel_chainbody = find("steel_chainbody")
    val black_chainbody = find("black_chainbody")
    val adamant_chainbody = find("adamant_chainbody")
    val bronze_kiteshield = find("bronze_kiteshield")
    val iron_sq_shield = find("iron_sq_shield")
    val steel_sq_shield = find("steel_sq_shield")
    val black_sq_shield = find("black_sq_shield")
    val black_kiteshield = find("black_kiteshield")
    val mithril_kiteshield = find("mithril_kiteshield")
    val adamant_sq_shield = find("adamant_sq_shield")
    val iron_mace = find("iron_mace")
    val steel_mace = find("steel_mace")
    val mithril_mace = find("mithril_mace")
    val adamant_mace = find("adamant_mace")
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
        edit(falador_npcs.cassie) {
            param[ShopParams.shop_sell_percentage] = 1000
            param[ShopParams.shop_buy_percentage] = 600
            param[ShopParams.shop_change_percentage] = 20
        }
        edit(falador_npcs.herquin) {
            param[ShopParams.shop_sell_percentage] = 1000
            param[ShopParams.shop_buy_percentage] = 600
            param[ShopParams.shop_change_percentage] = 20
        }
    }
}
