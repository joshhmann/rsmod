package org.rsmod.content.areas.misc.lumbridgeswamp.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.misc.lumbridgeswamp.LumbridgeSwampScript

object LumbridgeSwampNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<LumbridgeSwampScript>("npcs.toml")
    }
}
