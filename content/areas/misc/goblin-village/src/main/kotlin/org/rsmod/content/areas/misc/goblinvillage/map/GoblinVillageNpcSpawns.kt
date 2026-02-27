package org.rsmod.content.areas.misc.goblinvillage.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.misc.goblinvillage.GoblinVillageScript

object GoblinVillageNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<GoblinVillageScript>("npcs.toml")
    }
}
