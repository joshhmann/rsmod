package org.rsmod.content.areas.city.lumbridge.configs

import org.rsmod.api.type.refs.loc.LocReferences

internal typealias lumbridge_locs = LumbridgeLocs

object LumbridgeLocs : LocReferences() {
    val winch = find("winch")
    val farmerfred_axe_logs = find("log_withaxe")
    val farmerfred_logs = find("log_withoutaxe")
    val hopper = find("hopper1")
    val hopper_controls = find("hopperlevers1")
    val flour_bin = find("millbase")
    val kitchen_trapdoor_closed = find("trapdoor")
    val kitchen_trapdoor_open = find("trapdoor_open")
    val cellar_ladder = find("ladder")
}
