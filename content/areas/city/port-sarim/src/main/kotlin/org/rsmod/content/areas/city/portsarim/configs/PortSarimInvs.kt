package org.rsmod.content.areas.city.portsarim.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.editors.inv.InvEditor
import org.rsmod.api.type.refs.inv.InvReferences
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvStackType

typealias portsarim_invs = PortSarimInvs

object PortSarimInvs : InvReferences() {
    val brian_shop = find("battleaxeshop")
    val wydin_store = find("wydinstore")
    val general_store = find("generalshop5")
    val rimmington_general_store = find("generalshop6")
}

object PortSarimInvBuilder : InvEditor() {
    init {
        // Port Sarim General Store
        edit(portsarim_invs.general_store) {
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

        // Rimmington General Store
        edit(portsarim_invs.rimmington_general_store) {
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

        edit(portsarim_invs.brian_shop) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(portsarim_objs.bronze_battleaxe, count = 10, restockCycles = 100)
            stock += stock(portsarim_objs.iron_battleaxe, count = 8, restockCycles = 150)
            stock += stock(portsarim_objs.steel_battleaxe, count = 5, restockCycles = 250)
            stock += stock(portsarim_objs.black_battleaxe, count = 3, restockCycles = 400)
            stock += stock(portsarim_objs.mithril_battleaxe, count = 2, restockCycles = 800)
            stock += stock(portsarim_objs.adamant_battleaxe, count = 1, restockCycles = 1500)
            stock += stock(portsarim_objs.rune_battleaxe, count = 1, restockCycles = 3000)
        }

        // Wydin's Food Store
        edit(portsarim_invs.wydin_store) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(portsarim_objs.bread, count = 10, restockCycles = 100)
            stock += stock(portsarim_objs.cheese, count = 10, restockCycles = 100)
            stock += stock(portsarim_objs.tomato, count = 10, restockCycles = 100)
            stock += stock(portsarim_objs.cooked_meat, count = 10, restockCycles = 100)
            stock += stock(portsarim_objs.apple_pie, count = 5, restockCycles = 100)
            stock += stock(portsarim_objs.redberry_pie, count = 5, restockCycles = 100)
            stock += stock(portsarim_objs.meat_pie, count = 5, restockCycles = 100)
            stock += stock(portsarim_objs.potato, count = 10, restockCycles = 100)
            stock += stock(portsarim_objs.cabbage, count = 10, restockCycles = 100)
            stock += stock(portsarim_objs.onion, count = 10, restockCycles = 100)
            stock += stock(portsarim_objs.egg, count = 10, restockCycles = 100)
            stock += stock(portsarim_objs.pot_of_flour, count = 5, restockCycles = 100)
        }
    }
}
