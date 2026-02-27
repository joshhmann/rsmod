package org.rsmod.content.areas.misc.lumbridgeswamp.map

import org.rsmod.api.type.builders.map.obj.MapObjSpawnBuilder
import org.rsmod.content.areas.misc.lumbridgeswamp.LumbridgeSwampScript

object LumbridgeSwampObjSpawns : MapObjSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<LumbridgeSwampScript>("objs.toml")
    }
}
