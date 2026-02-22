@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.restlessghost.configs

import org.rsmod.api.type.refs.loc.LocReferences

internal typealias restless_ghost_locs = RestlessGhostLocs

internal object RestlessGhostLocs : LocReferences() {
    /** The marble coffin in the Lumbridge graveyard where the ghost resides. */
    val coffin = find("coffin")
    /** The altar in the Wizards' Tower basement where the skull is hidden. */
    val skull_altar = find("restless_ghost_altar")
}
