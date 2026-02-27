@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.draynor.manor.configs

import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias draynor_manor_npcs = DraynorManorNpcs

/** NPC references for Draynor Manor. Includes skeletons, ghosts, tree spirit, and the witch. */
object DraynorManorNpcs : NpcReferences() {
    // Aggressive NPCs inside the manor
    val skeleton_unarmed = find("skeleton_unarmed")
    val skeleton_unarmed2 = find("skeleton_unarmed2")
    val skeleton_unarmed3 = find("skeleton_unarmed3")
    val skeleton_armed = find("skeleton_armed")
    val skeleton_armed2 = find("skeleton_armed2")

    val ghost = find("ghost")
    val ghost2 = find("ghost2")
    val ghost3 = find("ghost3")

    // Tree spirit (level 101) - aggressive tree NPC
    val tree_spirit = find("tree_spirit")

    // Witch (level 25) - aggressive
    val witch = find("witch1")

    // Count Draynor (Vampyre Slayer boss) - handled by quest plugin
    val count_draynor = find("count_draynor")
}

/** NPC editor for Draynor Manor. Sets up wander ranges and movement restrictions for manor NPCs. */
internal object DraynorManorNpcEditor : NpcEditor() {
    init {
        // Skeletons - wander within the manor
        edit(draynor_manor_npcs.skeleton_unarmed) { wanderRange = 3 }
        edit(draynor_manor_npcs.skeleton_unarmed2) { wanderRange = 3 }
        edit(draynor_manor_npcs.skeleton_unarmed3) { wanderRange = 3 }
        edit(draynor_manor_npcs.skeleton_armed) { wanderRange = 3 }
        edit(draynor_manor_npcs.skeleton_armed2) { wanderRange = 3 }

        // Ghosts - wander within the manor
        edit(draynor_manor_npcs.ghost) { wanderRange = 3 }
        edit(draynor_manor_npcs.ghost2) { wanderRange = 3 }
        edit(draynor_manor_npcs.ghost3) { wanderRange = 3 }

        // Tree spirit - stays near its tree
        edit(draynor_manor_npcs.tree_spirit) {
            wanderRange = 2
            respawnDir = south
        }

        // Witch - stays near her house
        edit(draynor_manor_npcs.witch) {
            wanderRange = 2
            respawnDir = west
        }

        // Count Draynor - stays in his coffin room
        edit(draynor_manor_npcs.count_draynor) {
            wanderRange = 2
            moveRestrict = indoors
        }
    }
}
