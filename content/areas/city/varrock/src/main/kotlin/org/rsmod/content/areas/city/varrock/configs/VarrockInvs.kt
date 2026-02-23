@file:Suppress("SpellCheckingInspection")

package org.rsmod.content.areas.city.varrock.configs

import org.rsmod.api.type.refs.inv.InvReferences

typealias varrock_invs = VarrockInvs

// All of these are cache-defined inv types with stock already set.
// No InvEditor definitions required.
object VarrockInvs : InvReferences() {
    val general_store = find("generalshop2")
    val rune_shop = find("runeshop")
    val archery_shop = find("archeryshop")
    val armour_shop = find("armourshop")
    val clothes_shop = find("clotheshop")
    val staff_shop = find("staffshop")
}
