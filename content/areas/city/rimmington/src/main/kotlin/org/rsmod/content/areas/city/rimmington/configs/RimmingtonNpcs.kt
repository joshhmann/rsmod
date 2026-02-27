@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.rimmington.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias rimmington_npcs = RimmingtonNpcs

object RimmingtonNpcs : NpcReferences() {
    // Chemist (for Biohazard quest)
    val chemist = find("uncerter_rimmington")

    // Citizens
    val hengel = find("rimmington_hengel")
    val anja = find("rimmington_anja")

    // Hobgoblins
    val hobgoblin_unarmed_1 = find("rimmington_hobgoblin_unarmed_1")
    val hobgoblin_unarmed_2 = find("rimmington_hobgoblin_unarmed_2")
    val hobgoblin_unarmed_3 = find("rimmington_hobgoblin_unarmed_3")
    val hobgoblin_armed = find("rimmington_hobgoblin_armed_1")

    // General Store
    val shop_keeper = find("generalshopkeeper6")
    val shop_assistant = find("generalassistant6")
}

internal object RimmingtonNpcEditor : NpcEditor() {
    init {
        edit(rimmington_npcs.shop_keeper) {
            contentGroup = content.shop_keeper
            moveRestrict = indoors
        }

        edit(rimmington_npcs.shop_assistant) {
            contentGroup = content.shop_assistant
            moveRestrict = indoors
        }

        edit(rimmington_npcs.chemist) { moveRestrict = indoors }
    }
}
