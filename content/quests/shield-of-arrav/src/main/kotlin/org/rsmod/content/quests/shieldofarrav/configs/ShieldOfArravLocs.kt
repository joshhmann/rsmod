@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.shieldofarrav.configs

import org.rsmod.api.type.refs.loc.LocReferences

internal typealias shield_arrav_locs = ShieldOfArravLocs

internal object ShieldOfArravLocs : LocReferences() {
    val phoenixdoor = find("phoenixdoor")
    val phoenixdoor2 = find("phoenixdoor2")
    val blackarmdoor = find("blackarmdoor")
    val blackarmcupboardshut = find("blackarmcupboardshut")
    val blackarmcupboardopen = find("blackarmcupboardopen")
    val phoenixshutchest = find("phoenixshutchest")
    val phoenixopenchest = find("phoenixopenchest")
}
