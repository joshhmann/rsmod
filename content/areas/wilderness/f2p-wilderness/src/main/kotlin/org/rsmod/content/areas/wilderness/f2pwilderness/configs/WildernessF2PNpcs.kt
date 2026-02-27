@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.wilderness.f2pwilderness.configs

import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias wilderness_f2p_npcs = WildernessF2PNpcs

object WildernessF2PNpcs : NpcReferences() {
    // Hill Giants (Wilderness) - Level 28 combat
    val hill_giant = find("wilderness_hill_giant")
    val hill_giant_2 = find("wilderness_hill_giant2")
    val hill_giant_3 = find("wilderness_hill_giant3")

    // Dark Wizards - Level 7 and 20 combat
    val dark_wizard_bearded = find("bearded_dark_wizard")
    val dark_wizard_young = find("young_dark_wizard")

    // Skeletons - Various levels (unarmed variants)
    val skeleton = find("skeleton_unarmed")
    val skeleton_2 = find("skeleton_unarmed2")
    val skeleton_3 = find("skeleton_unarmed3")
    val skeleton_4 = find("skeleton_unarmed4")

    // Zombies - Various levels (unarmed variants)
    val zombie = find("zombie_unarmed")
    val zombie_2 = find("zombie_unarmed2")
    val zombie_3 = find("zombie_unarmed3")

    // Lesser Demons - Level 82 combat (Wilderness Volcano)
    val lesser_demon = find("lesser_demon")
    val lesser_demon_2 = find("lesser_demon2")
    val lesser_demon_3 = find("lesser_demon3")

    // Hobgoblins - Level 28 and 42 combat
    val hobgoblin = find("hobgoblin_unarmed")
    val hobgoblin_armed = find("hobgoblin_armed")
}

internal object WildernessF2PNpcEditor : NpcEditor() {
    init {
        // Hill Giants - aggressive, wander in wilderness
        edit(wilderness_f2p_npcs.hill_giant) { wanderRange = 4 }
        edit(wilderness_f2p_npcs.hill_giant_2) { wanderRange = 4 }
        edit(wilderness_f2p_npcs.hill_giant_3) { wanderRange = 4 }

        // Dark Wizards
        edit(wilderness_f2p_npcs.dark_wizard_bearded) { wanderRange = 3 }
        edit(wilderness_f2p_npcs.dark_wizard_young) { wanderRange = 3 }

        // Skeletons
        edit(wilderness_f2p_npcs.skeleton) { wanderRange = 3 }
        edit(wilderness_f2p_npcs.skeleton_2) { wanderRange = 3 }
        edit(wilderness_f2p_npcs.skeleton_3) { wanderRange = 3 }
        edit(wilderness_f2p_npcs.skeleton_4) { wanderRange = 3 }

        // Zombies
        edit(wilderness_f2p_npcs.zombie) { wanderRange = 3 }
        edit(wilderness_f2p_npcs.zombie_2) { wanderRange = 3 }
        edit(wilderness_f2p_npcs.zombie_3) { wanderRange = 3 }

        // Lesser Demons (aggressive, high level)
        edit(wilderness_f2p_npcs.lesser_demon) { wanderRange = 4 }
        edit(wilderness_f2p_npcs.lesser_demon_2) { wanderRange = 4 }
        edit(wilderness_f2p_npcs.lesser_demon_3) { wanderRange = 4 }

        // Hobgoblins
        edit(wilderness_f2p_npcs.hobgoblin) { wanderRange = 3 }
        edit(wilderness_f2p_npcs.hobgoblin_armed) { wanderRange = 3 }
    }
}
