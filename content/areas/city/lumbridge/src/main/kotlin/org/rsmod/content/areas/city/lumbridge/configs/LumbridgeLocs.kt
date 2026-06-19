package org.rsmod.content.areas.city.lumbridge.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.synths
import org.rsmod.api.type.editors.loc.LocEditor
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
    val castle_front_door = find("hundred_lumbridge_door")

    // Lumbridge Castle inner double doors
    val castle_double_door_left = find("hundred_lumbridge_doubledoorl")
    val castle_double_door_right = find("hundred_lumbridge_doubledoorr")

    // Generic open variants used as next_loc_stage targets
    val generic_double_door_left_open = find("opencastledoubledoorl")
    val generic_double_door_right_open = find("opencastledoubledoorr")
}

internal object LumbridgeDoorEdits : LocEditor() {
    init {
        edit(lumbridge_locs.castle_double_door_left) {
            param[params.next_loc_stage] = lumbridge_locs.generic_double_door_left_open
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_left_door
        }
        edit(lumbridge_locs.castle_double_door_right) {
            param[params.next_loc_stage] = lumbridge_locs.generic_double_door_right_open
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_right_door
        }
    }
}
