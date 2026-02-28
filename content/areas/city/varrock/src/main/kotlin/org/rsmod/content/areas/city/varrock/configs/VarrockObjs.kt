@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.varrock.configs

import org.rsmod.api.type.refs.obj.ObjReferences

typealias varrock_objs = VarrockObjs

object VarrockObjs : ObjReferences() {
    // Runes
    val airrune = find("airrune")
    val mindrune = find("mindrune")

    // Quest/Minigame items
    val beacon_ring = find("beacon_ring")
    val gb_moss_essence = find("gb_moss_essence")
    val battlestaff = find("battlestaff")
    val adventurepath_combat_voucher = find("adventurepath_combat_voucher")
    val energy_potion_3 = find("br_energy3")
    val strength_potion_3 = find("3dose1strength")
    val combat_potion_3 = find("set_combat_potion")
}
