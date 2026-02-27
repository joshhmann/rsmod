@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.inv.InvReferences

typealias invs = BaseInvs

object BaseInvs : InvReferences() {
    val tradeoffer = find("tradeoffer", 850951859)
    val inv = find("inv", 850981630)
    val worn = find("worn", 847803897)
    val bank = find("bank", 1135478129)

    val generalshop1 = find("generalshop1", 108596219232)
    val generalshop2 = find("generalshop2")
    val generalshop3 = find("generalshop3")
    val generalshop4 = find("generalshop4")
    val generalshop5 = find("generalshop5")
    val generalshop6 = find("generalshop6")

    val memberstaffshop = find("memberstaffshop")
    val fishingshop = find("fishingshop")
}
