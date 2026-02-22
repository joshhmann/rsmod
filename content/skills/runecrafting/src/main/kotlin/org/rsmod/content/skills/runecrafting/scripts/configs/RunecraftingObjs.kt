@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.skills.runecrafting.scripts.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias runecrafting_objs = RunecraftingObjs

internal object RunecraftingObjs : ObjReferences() {
    val blankrune = find("blankrune")

    val air_talisman = find("air_talisman")
    val mind_talisman = find("mind_talisman")
    val water_talisman = find("water_talisman")
    val earth_talisman = find("earth_talisman")
    val fire_talisman = find("fire_talisman")

    val tiara_air = find("tiara_air")
    val tiara_mind = find("tiara_mind")
    val tiara_water = find("tiara_water")
    val tiara_earth = find("tiara_earth")
    val tiara_fire = find("tiara_fire")
}
