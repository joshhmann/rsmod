@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.falador.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.synths
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

internal typealias falador_locs = FaladorLocs

object FaladorLocs : LocReferences() {
    // Falador poor castle doors (main castle entrance)
    val poor_castle_door_closed = find("fai_falador_poor_castle_door")
    val poor_castle_door_open = find("fai_falador_poor_castle_door_open")

    // Falador poor doors (various F2P buildings)
    val poor_door_closed = find("fai_falador_poor_door")
    val poor_door_open = find("fai_falador_poor_door_open")
    val poor_door_closed_m = find("fai_falador_poor_door_closed_m")
    val poor_door_open_m = find("fai_falador_poor_door_open_m")

    // Falador castle double doors
    val double_door_left_closed = find("fai_falador_double_door_l")
    val double_door_left_open = find("fai_falador_double_door_l_open")
    val double_door_right_closed = find("fai_falador_double_door_r")
    val double_door_right_open = find("fai_falador_double_door_r_open")
}

internal object FaladorLocEdits : LocEditor() {
    init {
        // Poor castle door
        edit(falador_locs.poor_castle_door_closed) {
            param[params.next_loc_stage] = falador_locs.poor_castle_door_open
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_single_door
        }
        edit(falador_locs.poor_castle_door_open) {
            param[params.next_loc_stage] = falador_locs.poor_castle_door_closed
            param[params.closesound] = synths.door_close
            contentGroup = content.opened_single_door
        }

        // Poor door
        edit(falador_locs.poor_door_closed) {
            param[params.next_loc_stage] = falador_locs.poor_door_open
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_single_door
        }
        edit(falador_locs.poor_door_open) {
            param[params.next_loc_stage] = falador_locs.poor_door_closed
            param[params.closesound] = synths.door_close
            contentGroup = content.opened_single_door
        }

        // Poor door medium
        edit(falador_locs.poor_door_closed_m) {
            param[params.next_loc_stage] = falador_locs.poor_door_open_m
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_single_door
        }
        edit(falador_locs.poor_door_open_m) {
            param[params.next_loc_stage] = falador_locs.poor_door_closed_m
            param[params.closesound] = synths.door_close
            contentGroup = content.opened_single_door
        }

        // Castle double doors - left
        edit(falador_locs.double_door_left_closed) {
            param[params.next_loc_stage] = falador_locs.double_door_left_open
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_left_door
        }
        edit(falador_locs.double_door_left_open) {
            param[params.next_loc_stage] = falador_locs.double_door_left_closed
            param[params.closesound] = synths.door_close
            contentGroup = content.opened_left_door
        }

        // Castle double doors - right
        edit(falador_locs.double_door_right_closed) {
            param[params.next_loc_stage] = falador_locs.double_door_right_open
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_right_door
        }
        edit(falador_locs.double_door_right_open) {
            param[params.next_loc_stage] = falador_locs.double_door_right_closed
            param[params.closesound] = synths.door_close
            contentGroup = content.opened_right_door
        }
    }
}
