package org.rsmod.content.areas.island.karamja.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.island.karamja.KaramjaScript

object KaramjaNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<KaramjaScript>("npcs.toml")
    }
}
