@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.varrock.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias varrock_npcs = VarrockNpcs

object VarrockNpcs : NpcReferences() {
    // Banks
    val banker_east = find("banker1_east")
    val banker_west = find("banker1_west")

    // General store
    val varrock_shop_keeper = find("generalshopkeeper2")
    val varrock_shop_assistant = find("generalassistant2")

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
    val dr_harlow = find("dr_harlow")
    val reldo = find("reldo")
    val curator = find("curator")
}

internal object VarrockNpcEditor : NpcEditor() {
    init {
        edit(varrock_npcs.banker_east) { contentGroup = content.banker }
        edit(varrock_npcs.banker_west) { contentGroup = content.banker }

        edit(varrock_npcs.varrock_shop_keeper) {
            contentGroup = content.shop_keeper
            moveRestrict = indoors
        }

        edit(varrock_npcs.varrock_shop_assistant) {
            contentGroup = content.shop_assistant
            moveRestrict = indoors
        }

        edit(varrock_npcs.aubury) { moveRestrict = indoors }

        edit(varrock_npcs.lowe) { moveRestrict = indoors }

        edit(varrock_npcs.horvik) { moveRestrict = indoors }

        edit(varrock_npcs.thessalia) { moveRestrict = indoors }

        edit(varrock_npcs.zaff) { moveRestrict = indoors }

        edit(varrock_npcs.apothecary) { moveRestrict = indoors }
        edit(varrock_npcs.father_lawrence) { moveRestrict = indoors }
        edit(varrock_npcs.dr_harlow) { moveRestrict = indoors }
        edit(varrock_npcs.reldo) { moveRestrict = indoors }
        edit(varrock_npcs.curator) { moveRestrict = indoors }
    }
}
