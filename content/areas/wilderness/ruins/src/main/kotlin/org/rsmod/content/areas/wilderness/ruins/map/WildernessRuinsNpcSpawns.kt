package org.rsmod.content.areas.wilderness.ruins.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.wilderness.ruins.WildernessRuinsScript

object WildernessRuinsNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<WildernessRuinsScript>("npcs.toml")
    }
}
