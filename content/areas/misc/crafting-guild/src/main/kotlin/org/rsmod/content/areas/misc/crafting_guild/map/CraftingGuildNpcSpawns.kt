package org.rsmod.content.areas.misc.crafting_guild.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.misc.crafting_guild.CraftingGuildScript

object CraftingGuildNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<CraftingGuildScript>("npcs.toml")
    }
}
