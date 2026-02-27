@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.wilderness.volcano.configs

import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias wilderness_volcano_npcs = WildernessVolcanoNpcs

object WildernessVolcanoNpcs : NpcReferences() {
    // Lesser Demons - Level 82 combat (F2P accessible at volcano)
    val lesser_demon = find("lesser_demon")
    val lesser_demon_2 = find("lesser_demon2")
    val lesser_demon_3 = find("lesser_demon3")
    val lesser_demon_4 = find("lesser_demon4")
    val lesser_demon_5 = find("lesser_demon5")

    // Greater Demons - Level 92 combat (P2P)
    val greater_demon = find("greater_demon")
    val greater_demon_2 = find("greater_demon2")

    // Chaos Dwarves - Level 48 combat
    val chaos_dwarf = find("dwarf_chaos")
}

internal object WildernessVolcanoNpcEditor : NpcEditor() {
    init {
        // Lesser Demons - aggressive, high level, wider wander range in multi-combat
        edit(wilderness_volcano_npcs.lesser_demon) { wanderRange = 5 }
        edit(wilderness_volcano_npcs.lesser_demon_2) { wanderRange = 5 }
        edit(wilderness_volcano_npcs.lesser_demon_3) { wanderRange = 5 }
        edit(wilderness_volcano_npcs.lesser_demon_4) { wanderRange = 5 }
        edit(wilderness_volcano_npcs.lesser_demon_5) { wanderRange = 5 }

        // Greater Demons - aggressive, high level
        edit(wilderness_volcano_npcs.greater_demon) { wanderRange = 5 }
        edit(wilderness_volcano_npcs.greater_demon_2) { wanderRange = 5 }

        // Chaos Dwarves
        edit(wilderness_volcano_npcs.chaos_dwarf) { wanderRange = 4 }
    }
}
