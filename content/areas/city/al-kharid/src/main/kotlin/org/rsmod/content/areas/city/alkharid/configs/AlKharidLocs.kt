@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.alkharid.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.synths
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

internal typealias alkharid_locs = AlKharidLocs

object AlKharidLocs : LocReferences() {
    // Al Kharid metal toll gate (south entrance, main gate into Al Kharid)
    // Note: these are the "closed" variants of the toll gate.
    // The open variants may use different IDs — these provide basic open/close via
    // the generic door system as a temporary measure.
    val gate_closed_left = find("kharidmetalgateclosedl")
    val gate_closed_right = find("kharidmetalgateclosedr")
}

internal object AlKharidLocEdits : LocEditor() {
    init {
        // Toll gate left — wired to closed_left_door for paired handling
        // (uses next_loc_stage = self since no explicit open variant)
        edit(alkharid_locs.gate_closed_left) {
            param[params.next_loc_stage] = alkharid_locs.gate_closed_left
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_left_door
        }

        // Toll gate right
        edit(alkharid_locs.gate_closed_right) {
            param[params.next_loc_stage] = alkharid_locs.gate_closed_right
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_right_door
        }
    }
}
