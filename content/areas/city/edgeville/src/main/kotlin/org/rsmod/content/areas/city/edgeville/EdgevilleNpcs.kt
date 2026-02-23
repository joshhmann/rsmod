@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.edgeville.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias edgeville_npcs = EdgevilleNpcs

object EdgevilleNpcs : NpcReferences() {
    // Banker
    val banker = find("misc_banker")
    
    // Shop
    val general_store = find("generalshopkeeper1")
    val shop_assistant = find("generalassistant1")
    
    // Monastery
    val brother_jered = find("brother_jered")
    
    // Barbarian Village
    val peksa = find("peksa") // Helm shop
    
    // Wilderness Dungeon NPCs
    val hill_giant = find("wilderness_hill_giant")
    val hill_giant_2 = find("wilderness_hill_giant2")
    val hill_giant_3 = find("wilderness_hill_giant3")
}

internal object EdgevilleNpcEditor : NpcEditor() {
    init {
        edit(edgeville_npcs.banker) { contentGroup = content.banker }
        
        edit(edgeville_npcs.general_store) { moveRestrict = indoors }
        edit(edgeville_npcs.shop_assistant) { moveRestrict = indoors }
        
        edit(edgeville_npcs.brother_jered) { moveRestrict = indoors }
        
        edit(edgeville_npcs.peksa) { moveRestrict = indoors }
        
        edit(edgeville_npcs.hill_giant) { wanderRange = 3 }
        edit(edgeville_npcs.hill_giant_2) { wanderRange = 3 }
        edit(edgeville_npcs.hill_giant_3) { wanderRange = 3 }
    }
}
