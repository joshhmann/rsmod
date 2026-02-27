package org.rsmod.content.areas.misc.cookingguild.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.misc.cookingguild.CookingGuildScript

object CookingGuildNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<CookingGuildScript>("npcs.toml")
    }
}
