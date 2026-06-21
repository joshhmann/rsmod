@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.draynor.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.synths
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

internal typealias draynor_locs = DraynorLocs

object DraynorLocs : LocReferences() {
    // Draynor Manor panelled doors (front entrance)
    val manor_door_closed = find("draynor_panelled_door")
    val manor_door_open = find("draynor_panelled_door_open")

    // Draynor village standard door (no open variant available)
    val village_door = find("draynor_door")
}

internal object DraynorLocEdits : LocEditor() {
    init {
        // Manor panelled door — open/close pair
        edit(draynor_locs.manor_door_closed) {
            param[params.next_loc_stage] = draynor_locs.manor_door_open
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_single_door
        }
        edit(draynor_locs.manor_door_open) {
            param[params.next_loc_stage] = draynor_locs.manor_door_closed
            param[params.closesound] = synths.door_close
            contentGroup = content.opened_single_door
        }

        // Village door — wired to generic door but self-referencing for open
        // (uses same loc model for both states)
        edit(draynor_locs.village_door) {
            param[params.next_loc_stage] = draynor_locs.village_door
            param[params.opensound] = synths.door_open
            contentGroup = content.closed_single_door
        }
    }
}
