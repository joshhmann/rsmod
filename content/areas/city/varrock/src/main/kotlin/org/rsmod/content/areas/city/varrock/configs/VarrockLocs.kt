@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.varrock.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.synths
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

internal typealias varrock_locs = VarrockLocs

object VarrockLocs : LocReferences() {
    // Varrock Palace double doors (south entrance)
    val palace_door_left = find("palacedoor_l")
    val palace_door_right = find("palacedoor_r")
    val palace_door_left_open = find("openpalacedoor_l")
    val palace_door_right_open = find("openpalacedoor_r")

    // Varrock Castle door (north-east, near castle entrance)
    val castle_door_closed = find("fai_varrock_castle_door")
    val castle_door_open = find("fai_varrock_castle_door_open")

    // Varrock standard doors (various buildings)
    val standard_door_closed = find("fai_varrock_door")
    val standard_door_open = find("fai_varrock_door_open")

    // Varrock Member gates (north-east, leads to member area)
    val member_gate_left_closed = find("fai_varrock_member_gatel")
    val member_gate_right_closed = find("fai_varrock_member_gater")
    val member_gate_left_open = find("fai_varrock_member_gatel_open")
    val member_gate_right_open = find("fai_varrock_member_gater_open")
}

internal object VarrockLocEdits : LocEditor() {
    init {
        // Palace double doors — left half
        edit(varrock_locs.palace_door_left) {
            param[params.next_loc_stage] = varrock_locs.palace_door_left_open
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_left_door
        }
        edit(varrock_locs.palace_door_left_open) {
            param[params.next_loc_stage] = varrock_locs.palace_door_left
            param[params.closesound] = synths.door_close
            contentGroup = content.opened_left_door
        }

        // Palace double doors — right half
        edit(varrock_locs.palace_door_right) {
            param[params.next_loc_stage] = varrock_locs.palace_door_right_open
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_right_door
        }
        edit(varrock_locs.palace_door_right_open) {
            param[params.next_loc_stage] = varrock_locs.palace_door_right
            param[params.closesound] = synths.door_close
            contentGroup = content.opened_right_door
        }

        // Castle door
        edit(varrock_locs.castle_door_closed) {
            param[params.next_loc_stage] = varrock_locs.castle_door_open
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_single_door
        }
        edit(varrock_locs.castle_door_open) {
            param[params.next_loc_stage] = varrock_locs.castle_door_closed
            param[params.closesound] = synths.door_close
            contentGroup = content.opened_single_door
        }

        // Standard door
        edit(varrock_locs.standard_door_closed) {
            param[params.next_loc_stage] = varrock_locs.standard_door_open
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_single_door
        }
        edit(varrock_locs.standard_door_open) {
            param[params.next_loc_stage] = varrock_locs.standard_door_closed
            param[params.closesound] = synths.door_close
            contentGroup = content.opened_single_door
        }

        // Member gates (left/right door system)
        edit(varrock_locs.member_gate_left_closed) {
            param[params.next_loc_stage] = varrock_locs.member_gate_left_open
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_left_door
        }
        edit(varrock_locs.member_gate_left_open) {
            param[params.next_loc_stage] = varrock_locs.member_gate_left_closed
            param[params.closesound] = synths.door_close
            contentGroup = content.opened_left_door
        }
        edit(varrock_locs.member_gate_right_closed) {
            param[params.next_loc_stage] = varrock_locs.member_gate_right_open
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_right_door
        }
        edit(varrock_locs.member_gate_right_open) {
            param[params.next_loc_stage] = varrock_locs.member_gate_right_closed
            param[params.closesound] = synths.door_close
            contentGroup = content.opened_right_door
        }
    }
}
