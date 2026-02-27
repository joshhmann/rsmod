package org.rsmod.content.skills.slayer.configs

import org.rsmod.api.type.refs.inv.InvReferences

internal typealias slayer_invs = SlayerInvs

internal object SlayerInvs : InvReferences() {
    val slayer_shop = find("slayershop")
}
