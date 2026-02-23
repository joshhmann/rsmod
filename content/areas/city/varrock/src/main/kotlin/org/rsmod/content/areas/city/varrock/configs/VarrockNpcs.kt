@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.varrock.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.shops.config.ShopParams
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias varrock_npcs = VarrockNpcs

object VarrockNpcs : NpcReferences() {
    // Banks
    val banker_east = find("banker1_east")
    val banker_west = find("banker1_west")

    // General store
    val shop_keeper = find("generalshopkeeper2")
    val shop_assistant = find("generalassistant2")

    // Specialty shops
    val aubury = find("aubury")
    val lowe = find("lowe")
    val horvik = find("horvik_the_armourer")
    val thessalia = find("thessalia")
    val zaff = find("zaff")

    // Quest/utility NPCs
    val apothecary = find("apothecary")
    val romeo = find("romeo")
    val juliet = find("juliet")
    val father_lawrence = find("father_lawrence")
}

internal object VarrockNpcEditor : NpcEditor() {
    init {
        edit(varrock_npcs.banker_east) { contentGroup = content.banker }
        edit(varrock_npcs.banker_west) { contentGroup = content.banker }

        edit(varrock_npcs.shop_keeper) { moveRestrict = indoors }
        edit(varrock_npcs.shop_assistant) { moveRestrict = indoors }

        edit(varrock_npcs.aubury) {
            moveRestrict = indoors
            param[ShopParams.shop_sell_percentage] = 1000
            param[ShopParams.shop_buy_percentage] = 600
            param[ShopParams.shop_change_percentage] = 20
        }

        edit(varrock_npcs.lowe) {
            moveRestrict = indoors
            param[ShopParams.shop_sell_percentage] = 1000
            param[ShopParams.shop_buy_percentage] = 600
            param[ShopParams.shop_change_percentage] = 20
        }

        edit(varrock_npcs.horvik) {
            moveRestrict = indoors
            param[ShopParams.shop_sell_percentage] = 1000
            param[ShopParams.shop_buy_percentage] = 600
            param[ShopParams.shop_change_percentage] = 20
        }

        edit(varrock_npcs.thessalia) {
            moveRestrict = indoors
            param[ShopParams.shop_sell_percentage] = 1000
            param[ShopParams.shop_buy_percentage] = 600
            param[ShopParams.shop_change_percentage] = 20
        }

        edit(varrock_npcs.zaff) {
            moveRestrict = indoors
            param[ShopParams.shop_sell_percentage] = 1000
            param[ShopParams.shop_buy_percentage] = 600
            param[ShopParams.shop_change_percentage] = 20
        }

        edit(varrock_npcs.apothecary) { moveRestrict = indoors }
        edit(varrock_npcs.father_lawrence) { moveRestrict = indoors }
    }
}
