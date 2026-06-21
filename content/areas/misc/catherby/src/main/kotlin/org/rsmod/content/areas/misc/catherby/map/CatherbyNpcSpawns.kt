package org.rsmod.content.areas.misc.catherby.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.misc.catherby.CatherbyScript

object CatherbyNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<CatherbyScript>("npcs.toml")
    }
}
