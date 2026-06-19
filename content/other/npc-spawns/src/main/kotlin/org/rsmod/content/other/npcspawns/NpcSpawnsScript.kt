package org.rsmod.content.other.npcspawns

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder

object NpcSpawnsScript : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<NpcSpawnsScript>("npcs.toml")
    }
}
