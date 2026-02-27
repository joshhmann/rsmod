@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.wilderness.ruins.configs

import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias wilderness_ruins_npcs = WildernessRuinsNpcs

object WildernessRuinsNpcs : NpcReferences() {
    // Eastern Ruins NPCs
    val zombie = find("zombie_unarmed")
    val zombie_2 = find("zombie_unarmed2")
    val zombie_3 = find("zombie_unarmed3")
    val deadly_red_spider = find("deadly_red_spider")

    // Western Ruins NPCs (Dareeyak)
    val grizzly_bear = find("grizzly_bear")
    val giant_rat = find("giant_rat")
    val spider = find("spider")
}

internal object WildernessRuinsNpcEditor : NpcEditor() {
    init {
        // Zombies - wander in Eastern Ruins
        edit(wilderness_ruins_npcs.zombie) { wanderRange = 3 }
        edit(wilderness_ruins_npcs.zombie_2) { wanderRange = 3 }
        edit(wilderness_ruins_npcs.zombie_3) { wanderRange = 3 }

        // Deadly red spiders - aggressive
        edit(wilderness_ruins_npcs.deadly_red_spider) { wanderRange = 2 }

        // Western Ruins NPCs
        edit(wilderness_ruins_npcs.grizzly_bear) { wanderRange = 3 }
        edit(wilderness_ruins_npcs.giant_rat) { wanderRange = 2 }
        edit(wilderness_ruins_npcs.spider) { wanderRange = 2 }
    }
}
