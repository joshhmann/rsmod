package org.rsmod.content.interfaces.combat.tab.configs

import org.rsmod.api.type.refs.comp.ComponentReferences

typealias combat_components = CombatTabComponents

object CombatTabComponents : ComponentReferences() {
    val stance1 = find("combat_interface:0")
    val stance2 = find("combat_interface:1")
    val stance3 = find("combat_interface:2")
    val stance4 = find("combat_interface:3")
    val auto_retaliate = find("combat_interface:retaliate")
    val special_attack = find("combat_interface:special_attack")

    val special_attack_orb = find("orbs:specbutton")
}
