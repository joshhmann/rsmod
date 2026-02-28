@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.inv.InvReferences

typealias invs = BaseInvs

object BaseInvs : InvReferences() {
    val tradeoffer = find("tradeoffer")
    val inv = find("inv")
    val worn = find("worn")
    val bank = find("bank")

    val generalshop1 = find("generalshop1")
    val generalshop2 = find("generalshop2")
    val generalshop3 = find("generalshop3")
    val generalshop4 = find("generalshop4")
    val generalshop5 = find("generalshop5")
    val generalshop6 = find("generalshop6")

    val memberstaffshop = find("memberstaffshop")
    val fishingshop = find("fishingshop")
}
