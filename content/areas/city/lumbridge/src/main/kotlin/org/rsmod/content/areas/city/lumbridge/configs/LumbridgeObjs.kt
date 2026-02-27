package org.rsmod.content.areas.city.lumbridge.configs

import org.rsmod.api.type.refs.obj.ObjReferences

typealias lumbridge_objs = LumbridgeObjs

object LumbridgeObjs : ObjReferences() {
    val furnace_icon = find("furnace_icon_dummy")
    val mining_icon = find("mining_site_icon_dummy")
    val bank_icon = find("bank_icon_dummy")
    val smithing_icon = find("smithing_tutor_icon_dummy")
    val woodcutting_icon = find("woodcutting_tutor_icon_dummy")
}
