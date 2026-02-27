package org.rsmod.content.skills.slayer.configs

import org.rsmod.api.type.refs.varbit.VarBitReferences

internal typealias slayer_varbits = SlayerVarBits

internal object SlayerVarBits : VarBitReferences() {
    val points = find("slayer_points")
}
