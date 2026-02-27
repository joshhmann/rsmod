package org.rsmod.content.interfaces.ge.configs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.npc.NpcReferences

typealias ge_components = GrandExchangeComponents

typealias ge_npcs = GrandExchangeNpcs

object GrandExchangeComponents : ComponentReferences() {
    // Offer slot buttons (index_0..index_7)
    val index_0 = find("ge_offers:index_0")
    val index_1 = find("ge_offers:index_1")
    val index_2 = find("ge_offers:index_2")
    val index_3 = find("ge_offers:index_3")
    val index_4 = find("ge_offers:index_4")
    val index_5 = find("ge_offers:index_5")
    val index_6 = find("ge_offers:index_6")
    val index_7 = find("ge_offers:index_7")

    // Offer setup confirmation
    val setup_confirm = find("ge_offers:setup_confirm")

    // Active offer details collect
    val details_collect = find("ge_offers:details_collect")

    // Collect all
    val collectall = find("ge_offers:collectall")

    // Close / back
    val back = find("ge_offers:back")
}

object GrandExchangeNpcs : NpcReferences() {
    val clerk_1 = find("ge_clerk_1")
    val clerk_2 = find("ge_clerk_2")
    val clerk_3 = find("ge_clerk_3")
    val clerk_4 = find("ge_clerk_4")
}
