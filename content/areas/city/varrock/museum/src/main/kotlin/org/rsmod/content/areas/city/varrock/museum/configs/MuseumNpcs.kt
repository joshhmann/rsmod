@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.varrock.museum.configs

import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias museum_npcs = MuseumNpcs

object MuseumNpcs : NpcReferences() {
    // Museum guards
    val museum_guard = find("vm_museum_guard")
    val museum_guard2 = find("vm_museum_guard2")
    val museum_guard3 = find("vm_museum_guard3")

    // Historians
    val timeline_historian = find("vm_timeline_historian")
    val natural_historian_north = find("vm_natural_historian_north")
    val natural_historian_south = find("vm_natural_historian_south")
    val natural_historian_east = find("vm_natural_historian_east")
    val natural_historian_west = find("vm_natural_historian_west")

    // Other museum NPCs
    val art_critic = find("vm_art_critic")
    val info_booth_lady = find("vm_info_booth_lady")
}

internal object MuseumNpcEditor : NpcEditor() {
    init {
        // Museum guards should stay indoors
        edit(museum_npcs.museum_guard) { moveRestrict = indoors }
        edit(museum_npcs.museum_guard2) { moveRestrict = indoors }
        edit(museum_npcs.museum_guard3) { moveRestrict = indoors }

        // Historians stay indoors
        edit(museum_npcs.timeline_historian) { moveRestrict = indoors }
        edit(museum_npcs.natural_historian_north) { moveRestrict = indoors }
        edit(museum_npcs.natural_historian_south) { moveRestrict = indoors }
        edit(museum_npcs.natural_historian_east) { moveRestrict = indoors }
        edit(museum_npcs.natural_historian_west) { moveRestrict = indoors }

        // Other NPCs
        edit(museum_npcs.art_critic) { moveRestrict = indoors }
        edit(museum_npcs.info_booth_lady) { moveRestrict = indoors }
    }
}
