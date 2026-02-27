package org.rsmod.content.areas.dungeons.asgarnianice.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.dungeons.asgarnianice.AsgarnianIceDungeonScript

object AsgarnianIceNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<AsgarnianIceDungeonScript>("npcs.toml")
    }
}
