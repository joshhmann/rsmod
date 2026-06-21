package org.rsmod.content.areas.misc.crafting_guild.map

import org.rsmod.api.type.builders.map.obj.MapObjSpawnBuilder
import org.rsmod.content.areas.misc.crafting_guild.CraftingGuildScript

object CraftingGuildObjSpawns : MapObjSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<CraftingGuildScript>("objs.toml")
    }
}
