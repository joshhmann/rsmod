package org.rsmod.content.areas.city.lumbridge.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.editors.inv.InvEditor
import org.rsmod.api.type.refs.inv.InvReferences
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvStackType

typealias lumbridge_invs = LumbridgeInvs

object LumbridgeInvs : InvReferences() {
    val axeshop = find("axeshop")
    val generalshop1 = find("generalshop1")
}

object LumbridgeInvBuilder : InvEditor() {
    init {
        edit(lumbridge_invs.axeshop) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(objs.bronze_pickaxe, count = 5, restockCycles = 100)
            stock += stock(objs.bronze_axe, count = 10, restockCycles = 100)
            stock += stock(objs.iron_axe, count = 5, restockCycles = 200)
            stock += stock(objs.steel_axe, count = 3, restockCycles = 400)
            stock += stock(objs.iron_battleaxe, count = 5, restockCycles = 100)
            stock += stock(objs.steel_battleaxe, count = 2, restockCycles = 200)
            stock += stock(objs.mithril_battleaxe, count = 1, restockCycles = 3000)
        }

        edit(lumbridge_invs.generalshop1) {
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
