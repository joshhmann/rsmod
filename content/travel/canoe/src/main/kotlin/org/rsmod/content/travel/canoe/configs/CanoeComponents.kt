package org.rsmod.content.travel.canoe.configs

import org.rsmod.api.type.refs.comp.ComponentReferences

typealias canoe_components = CanoeComponents

object CanoeComponents : ComponentReferences() {
    val shape_log = find("canoeing:log")
    val shape_dugout = find("canoeing:dugout")
    val shape_stable_dugout = find("canoeing:stable_dugout")
    val shape_waka = find("canoeing:waka")
    val shape_close = find("canoeing:close")

    val destination_edgeville = find("canoe_map:edgeville")
    val destination_lumbridge = find("canoe_map:lumbridge")
    val destination_champs_guild = find("canoe_map:champions")
    val destination_barb_village = find("canoe_map:barbarian")
    val destination_wild_pond = find("canoe_map:wilderness")
    val destination_ferox_enclave = find("canoe_map:sanctuary")
}
