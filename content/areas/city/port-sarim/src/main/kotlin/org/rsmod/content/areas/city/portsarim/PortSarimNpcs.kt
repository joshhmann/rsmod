@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.portsarim.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias portsarim_npcs = PortSarimNpcs

object PortSarimNpcs : NpcReferences() {
    // Quest NPCs
    val redbeard_frank = find("redbeard_frank") // Pirate's Treasure
    
    // Shop owners
    val wydin = find("wydin") // Wydin's Food Store
    val brian = find("brian") // Brian's Battleaxe Bazaar
    val captain_barnaby = find("captain_barnaby_rimmington") // Charter ship
    
    // Rimmington NPCs
    val chemist = find("uncerter_rimmington") // Chemist
    val rimmington_hengel = find("rimmington_hengel")
    val rimmington_anja = find("rimmington_anja")
    
    // Prison
    val prison_pete = find("prison_pete")
    
    // Monks
    val monk_entrana = find("monk_of_entrana")
    
    // Hobgoblins (Rimmington)
    val rimmington_hobgoblin_1 = find("rimmington_hobgoblin_unarmed_1")
    val rimmington_hobgoblin_2 = find("rimmington_hobgoblin_unarmed_2")
    val rimmington_hobgoblin_3 = find("rimmington_hobgoblin_unarmed_3")
    val rimmington_hobgoblin_armed = find("rimmington_hobgoblin_armed_1")
}

internal object PortSarimNpcEditor : NpcEditor() {
    init {
        edit(portsarim_npcs.redbeard_frank) { wanderRange = 1 }
        
        edit(portsarim_npcs.wydin) { moveRestrict = indoors }
        edit(portsarim_npcs.brian) { moveRestrict = indoors }
        edit(portsarim_npcs.captain_barnaby) { wanderRange = 1 }
        
        edit(portsarim_npcs.chemist) { moveRestrict = indoors }
        
        edit(portsarim_npcs.rimmington_hengel) { wanderRange = 1 }
        edit(portsarim_npcs.rimmington_anja) { wanderRange = 1 }
        
        edit(portsarim_npcs.prison_pete) { wanderRange = 1 }
        
        edit(portsarim_npcs.rimmington_hobgoblin_1) { wanderRange = 2 }
        edit(portsarim_npcs.rimmington_hobgoblin_2) { wanderRange = 2 }
        edit(portsarim_npcs.rimmington_hobgoblin_3) { wanderRange = 2 }
        edit(portsarim_npcs.rimmington_hobgoblin_armed) { wanderRange = 2 }
    }
}
