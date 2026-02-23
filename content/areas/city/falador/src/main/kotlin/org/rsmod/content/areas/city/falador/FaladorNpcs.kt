@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.falador.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias falador_npcs = FaladorNpcs

object FaladorNpcs : NpcReferences() {
    // Bankers
    val banker_west = find("falador_banker")

    // Shop owners
    val wayne = find("wayne") // Wayne's Chains (armor shop)
    val flynn = find("flynn") // Flynn's Mace (weapon shop)

    // Quest NPCs
    val sir_amik_varze = find("sir_amik_varze") // White Knights leader

    // Guards
    val falador_guard = find("fai_falador_guard1")

    // White Knights
    val white_knight = find("white_knight")
    val white_knight_yellow = find("white_knight_yellow_plumes")
    val white_knight_green = find("white_knight_green_plumes")
    val white_knight_blue = find("white_knight_blue_plumes")
}

internal object FaladorNpcEditor : NpcEditor() {
    init {
        edit(falador_npcs.banker_west) { contentGroup = content.banker }

        edit(falador_npcs.wayne) { moveRestrict = indoors }

        edit(falador_npcs.flynn) { moveRestrict = indoors }

        edit(falador_npcs.sir_amik_varze) { moveRestrict = indoors }

        edit(falador_npcs.falador_guard) { wanderRange = 2 }

        edit(falador_npcs.white_knight) { wanderRange = 1 }
        edit(falador_npcs.white_knight_yellow) { wanderRange = 1 }
        edit(falador_npcs.white_knight_green) { wanderRange = 1 }
        edit(falador_npcs.white_knight_blue) { wanderRange = 1 }
    }
}
