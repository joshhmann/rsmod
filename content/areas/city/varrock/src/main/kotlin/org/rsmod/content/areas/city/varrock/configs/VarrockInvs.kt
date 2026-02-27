@file:Suppress("SpellCheckingInspection")

package org.rsmod.content.areas.city.varrock.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.editors.inv.InvEditor
import org.rsmod.api.type.refs.inv.InvReferences
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvStackType

typealias varrock_invs = VarrockInvs

// All of these are cache-defined inv types with stock already set.
// Overriding general_store to ensure complete F2P stock.
object VarrockInvs : InvReferences() {
    val general_store = find("generalshop2")
    val rune_shop = find("runeshop")
    val archery_shop = find("archeryshop")
    val armour_shop = find("armourshop")
    val clothes_shop = find("clotheshop")
    val staff_shop = find("staffshop")
}

object VarrockInvBuilder : InvEditor() {
    init {
        // Varrock General Store
        edit(varrock_invs.general_store) {
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
