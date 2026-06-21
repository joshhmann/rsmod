package org.rsmod.content.skills.magic.teleports

import org.rsmod.api.type.refs.component.ComponentReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.map.CoordGrid

object TeleportComponents : ComponentReferences() {
    val spellbook_home_teleport = find("magic_spellbook:teleport_home_standard")
}

object TeleportSpellObjs : ObjReferences() {
    val spell_home_teleport = find("48_home_teleport")
    val spell_varrock_teleport = find("25_varrock_teleport")
    val spell_lumbridge_teleport = find("31_lumbridge_teleport")
    val spell_falador_teleport = find("37_falador_teleport")
}

object TeleportDestinations {
    val lumbridge = CoordGrid(3222, 3218, 0)
    val varrock = CoordGrid(3213, 3424, 0)
    val falador = CoordGrid(2964, 3378, 0)
}

enum class TeleportSpell(
    val level: Int,
    val xp: Double,
    val castTicks: Int,
) {
    HOME_TELEPORT(level = 0, xp = 0.0, castTicks = 7),
    VARROCK_TELEPORT(level = 25, xp = 35.0, castTicks = 4),
    LUMBRIDGE_TELEPORT(level = 31, xp = 41.0, castTicks = 4),
    FALADOR_TELEPORT(level = 37, xp = 48.0, castTicks = 4),
}
