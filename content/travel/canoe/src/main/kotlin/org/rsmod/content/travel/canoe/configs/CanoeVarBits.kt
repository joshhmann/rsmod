package org.rsmod.content.travel.canoe.configs

import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias canoe_varbits = CanoeVarBits

object CanoeVarBits : VarBitReferences() {
    val current_station = find("canoe_startfrom")
    val lumbridge_state = find("canoestation_state_lumbridge")
    val champs_guild_state = find("canoestation_state_championsguild")
    val barb_village_state = find("canoestation_state_barbarianvillage")
    val edgeville_state = find("canoestation_state_edgeville")
    val ferox_enclave_state = find("canoestation_state_sanctuary")

    val canoe_type = find("canoe_type")
    val canoe_avoid_if = find("canoe_avoid_if")

    val disable_wild_pond_warning = find("wildy_canoe_warning")
}
