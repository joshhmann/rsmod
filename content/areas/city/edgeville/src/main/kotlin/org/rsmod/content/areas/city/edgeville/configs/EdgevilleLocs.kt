@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.edgeville.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

internal typealias edgeville_locs = EdgevilleLocs

object EdgevilleLocs : LocReferences() {
    // Monastery ladder (Edgeville area — climbs down to wilderness/edgeville dungeon area)
    val monastery_ladder = find("monasteryladder")

    // Edgeville standard doors (various buildings)
    // Note: Edgeville dungeon entrance trapdoor isn't a simple ladder; needs custom handling
}

internal object EdgevilleLocEdits : LocEditor() {
    init {
        // Monastery ladder — wired to generic ladder_up (climb from ground level)
        edit(edgeville_locs.monastery_ladder) {
            param[params.climb_anim] = seqs.human_pickupfloor
            contentGroup = content.ladder_up
        }
    }
}
