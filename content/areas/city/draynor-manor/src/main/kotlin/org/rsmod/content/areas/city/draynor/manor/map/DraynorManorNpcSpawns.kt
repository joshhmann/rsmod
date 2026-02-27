package org.rsmod.content.areas.city.draynor.manor.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.city.draynor.manor.DraynorManorScript

/** NPC spawns for Draynor Manor. Loads spawn data from npcs.toml resource file. */
object DraynorManorNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<DraynorManorScript>("npcs.toml")
    }
}
