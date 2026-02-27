package org.rsmod.content.areas.misc.essencemine.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.misc.essencemine.EssenceMineScript

object EssenceMineNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<EssenceMineScript>("npcs.toml")
    }
}
