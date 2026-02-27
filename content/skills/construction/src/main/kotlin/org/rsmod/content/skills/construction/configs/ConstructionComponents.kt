package org.rsmod.content.skills.construction.configs

import org.rsmod.api.type.refs.comp.ComponentReferences

typealias poh_components = ConstructionComponents

object ConstructionComponents : ComponentReferences() {
    val build_mode_on = find("poh_options:build_mode_on")
    val build_mode_off = find("poh_options:build_mode_off")
    val leave_house = find("poh_options:leave_house")
}
