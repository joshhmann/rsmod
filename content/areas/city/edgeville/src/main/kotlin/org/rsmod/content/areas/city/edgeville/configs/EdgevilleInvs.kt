@file:Suppress("SpellCheckingInspection")

package org.rsmod.content.areas.city.edgeville.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.editors.inv.InvEditor
import org.rsmod.api.type.refs.inv.InvReferences
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvStackType

typealias edgeville_invs = EdgevilleInvs

// Cache-defined inv types with stock already set
object EdgevilleInvs : InvReferences() {
    val general_store = find("generalshop8")
    val peksa_shop = find("helmetshop")
}

object EdgevilleInvBuilder : InvEditor() {
    init {
        // Edgeville General Store
        edit(edgeville_invs.general_store) {
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

        // Peksa's Helms
        edit(edgeville_invs.peksa_shop) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(edgeville_objs.bronze_med_helm, count = 5, restockCycles = 100)
            stock += stock(edgeville_objs.iron_med_helm, count = 5, restockCycles = 200)
            stock += stock(edgeville_objs.steel_med_helm, count = 3, restockCycles = 400)
            stock += stock(edgeville_objs.black_med_helm, count = 2, restockCycles = 600)
            stock += stock(edgeville_objs.mithril_med_helm, count = 2, restockCycles = 1500)
            stock += stock(edgeville_objs.adamant_med_helm, count = 1, restockCycles = 3000)
            stock += stock(edgeville_objs.rune_med_helm, count = 1, restockCycles = 6000)
            stock += stock(edgeville_objs.dragon_med_helm, count = 1, restockCycles = 12000)
        }
    }
}
