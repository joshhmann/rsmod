package org.rsmod.content.areas.city.barbarian_village

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder

object BarbarianVillageNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<BarbarianVillageScript>("npcs.toml")
    }
}
