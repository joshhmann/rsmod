package org.rsmod.content.areas.dungeons.dwarvenmine.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.dungeons.dwarvenmine.DwarvenMineScript

object DwarvenMineNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<DwarvenMineScript>("npcs.toml")
    }
}
