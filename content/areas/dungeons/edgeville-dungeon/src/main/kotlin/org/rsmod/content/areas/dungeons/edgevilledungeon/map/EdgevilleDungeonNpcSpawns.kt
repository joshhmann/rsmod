package org.rsmod.content.areas.dungeons.edgevilledungeon.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.dungeons.edgevilledungeon.EdgevilleDungeonScript

object EdgevilleDungeonNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<EdgevilleDungeonScript>("npcs.toml")
    }
}
