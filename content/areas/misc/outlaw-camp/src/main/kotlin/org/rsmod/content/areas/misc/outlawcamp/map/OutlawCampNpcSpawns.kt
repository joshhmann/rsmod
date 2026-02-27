package org.rsmod.content.areas.misc.outlawcamp.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.misc.outlawcamp.OutlawCampScript

object OutlawCampNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<OutlawCampScript>("npcs.toml")
    }
}
