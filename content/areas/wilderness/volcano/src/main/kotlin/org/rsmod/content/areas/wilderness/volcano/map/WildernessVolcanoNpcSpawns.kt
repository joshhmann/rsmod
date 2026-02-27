package org.rsmod.content.areas.wilderness.volcano.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.wilderness.volcano.WildernessVolcanoScript

object WildernessVolcanoNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<WildernessVolcanoScript>("npcs.toml")
    }
}
