package org.rsmod.content.other.windmill

import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.varbit.VarBitReferences

internal typealias windmill_locs = WindmillLocs

internal typealias windmill_varbits = WindmillVarBits

internal typealias windmill_objs = WindmillObjs

internal object WindmillLocs : LocReferences() {
    // Ladders for Cooking Guild windmill
    val ladder_up = find("qip_cook_ladder")
    val ladder_option = find("qip_cook_ladder_middle")
    val ladder_down = find("qip_cook_ladder_top")

    // Hopper locs (used in Lumbridge and other windmills)
    val hopper = find("hopper1")
    val hopper_controls = find("hopperlevers1")

    // Flour bin locs (empty and full variants)
    val flour_bin_empty = find("millbase")
    val flour_bin_full = find("millbase_flour")
}

internal object WindmillVarBits : VarBitReferences() {
    // Varbit to track flour in the hopper (0 = none, 1 = grain added)
    val mill_flour = find("mill_flour")
}

internal object WindmillObjs : ObjReferences() {
    val pot_of_flour = find("pot_flour")
}
