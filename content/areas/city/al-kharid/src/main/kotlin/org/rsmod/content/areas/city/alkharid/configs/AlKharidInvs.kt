@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.alkharid.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.editors.inv.InvEditor
import org.rsmod.api.type.refs.inv.InvReferences
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvStackType

typealias al_kharid_invs = AlKharidInvs

object AlKharidInvs : InvReferences() {
    val dommik_crafting = find("craftingshop_free")
    val gem_trader = find("gemshop")
    val louie_legs = find("legsshop")
    val ranael_skirt = find("skirtshop")
    val zeke_scimitar = find("scimitarshop")
    val general_store = find("generalshop3")
}

object AlKharidInvBuilder : InvEditor() {
    init {
        // Dommik's Crafting Store
        edit(al_kharid_invs.dommik_crafting) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(objs.chisel, count = 2, restockCycles = 100)
            stock += stock(al_kharid_objs.ring_mould, count = 2, restockCycles = 100)
            stock += stock(al_kharid_objs.necklace_mould, count = 2, restockCycles = 100)
            stock += stock(al_kharid_objs.amulet_mould, count = 2, restockCycles = 100)
            stock += stock(al_kharid_objs.needle, count = 3, restockCycles = 100)
            stock += stock(al_kharid_objs.thread, count = 100, restockCycles = 100)
            stock += stock(objs.ball_of_wool, count = 30, restockCycles = 100)
        }

        // Gem Trader
        edit(al_kharid_invs.gem_trader) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(al_kharid_objs.uncut_sapphire, count = 1, restockCycles = 100)
            stock += stock(al_kharid_objs.uncut_emerald, count = 1, restockCycles = 100)
            stock += stock(al_kharid_objs.sapphire, count = 1, restockCycles = 100)
            stock += stock(al_kharid_objs.emerald, count = 1, restockCycles = 100)
        }

        // Louie's Armoured Legs Bazaar
        edit(al_kharid_invs.louie_legs) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(al_kharid_objs.bronze_platelegs, count = 5, restockCycles = 100)
            stock += stock(al_kharid_objs.iron_platelegs, count = 3, restockCycles = 200)
            stock += stock(al_kharid_objs.steel_platelegs, count = 2, restockCycles = 400)
            stock += stock(al_kharid_objs.black_platelegs, count = 1, restockCycles = 1000)
            stock += stock(al_kharid_objs.mithril_platelegs, count = 1, restockCycles = 3000)
            stock += stock(al_kharid_objs.adamant_platelegs, count = 1, restockCycles = 6000)
        }

        // Ranael's Super Skirt Store
        edit(al_kharid_invs.ranael_skirt) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(al_kharid_objs.bronze_plateskirt, count = 5, restockCycles = 100)
            stock += stock(al_kharid_objs.iron_plateskirt, count = 3, restockCycles = 200)
            stock += stock(al_kharid_objs.steel_plateskirt, count = 2, restockCycles = 400)
            stock += stock(al_kharid_objs.black_plateskirt, count = 1, restockCycles = 1000)
            stock += stock(al_kharid_objs.mithril_plateskirt, count = 1, restockCycles = 3000)
            stock += stock(al_kharid_objs.adamant_plateskirt, count = 1, restockCycles = 6000)
        }

        // Zeke's Superior Scimitars
        edit(al_kharid_invs.zeke_scimitar) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(al_kharid_objs.bronze_scimitar, count = 5, restockCycles = 100)
            stock += stock(al_kharid_objs.iron_scimitar, count = 3, restockCycles = 200)
            stock += stock(al_kharid_objs.steel_scimitar, count = 2, restockCycles = 400)
            stock += stock(al_kharid_objs.mithril_scimitar, count = 1, restockCycles = 3000)
        }

        // Al Kharid General Store
        edit(al_kharid_invs.general_store) {
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
