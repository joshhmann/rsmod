package org.rsmod.content.areas.wilderness.f2pwilderness.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.wilderness.f2pwilderness.WildernessF2PScript

object WildernessF2PNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<WildernessF2PScript>("npcs.toml")
    }
}
