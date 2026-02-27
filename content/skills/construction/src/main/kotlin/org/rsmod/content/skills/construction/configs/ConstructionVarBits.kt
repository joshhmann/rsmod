package org.rsmod.content.skills.construction.configs

import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias poh_varbits = ConstructionVarBits

object ConstructionVarBits : VarBitReferences() {
    val poh_building_mode = find("poh_building_mode")
}
