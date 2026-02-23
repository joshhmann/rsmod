@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.draynor.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias draynor_npcs = DraynorNpcs

object DraynorNpcs : NpcReferences() {
    val morgan = find("morgan") // Wandering merchant
    val aggie = find("aggie") // Witch
    val leela = find("leela") // Jolly block
    val count_draynor = find("count_draynor") // Vampire
    val draynor_skeleton = find("draynor_skeleton") // Skeleton
    val banker = find("misc_banker")
    val shop_keeper = find("generalshopkeeper1")
    val shop_assistant = find("generalassistant1")
    val town_crier = find("pmod_town_crier_draynor")
}

internal object DraynorNpcEditor : NpcEditor() {
    init {
        edit(draynor_npcs.shop_keeper) { moveRestrict = indoors }

        edit(draynor_npcs.shop_assistant) { moveRestrict = indoors }

        edit(draynor_npcs.banker) { contentGroup = content.banker }

        edit(draynor_npcs.morgan) { wanderRange = 2 }

        edit(draynor_npcs.aggie) { moveRestrict = indoors }

        edit(draynor_npcs.leela) { moveRestrict = indoors }

        edit(draynor_npcs.count_draynor) { moveRestrict = indoors }

        edit(draynor_npcs.town_crier) { wanderRange = 3 }
    }
}
