@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.demonslayer.configs

import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias demon_slayer_npcs = DemonSlayerNpcs

object DemonSlayerNpcs : NpcReferences() {
    val hundred_aris = find("hundred_aris") // Gypsy Aris - quest start in Varrock Square
    val sir_prysin = find("sir_prysin") // Sir Prysin - in Varrock Palace
    val captain_rovin = find("captain_rovin") // Captain Rovin - in Varrock Palace tower
    val traiborn = find("traiborn") // Wizard Traiborn - Wizards' Tower
    val delrith = find("delrith") // The demon to defeat
    val delrith_weakened = find("delrith_weakened") // Weakened form of Delrith
}

internal object DemonSlayerNpcEditor : NpcEditor() {
    init {
        edit(demon_slayer_npcs.hundred_aris) { wanderRange = 2 }
        edit(demon_slayer_npcs.sir_prysin) { wanderRange = 1 }
        edit(demon_slayer_npcs.captain_rovin) { wanderRange = 1 }
        edit(demon_slayer_npcs.traiborn) { wanderRange = 2 }
        edit(demon_slayer_npcs.delrith) { wanderRange = 0 }
        edit(demon_slayer_npcs.delrith_weakened) { wanderRange = 0 }
    }
}
