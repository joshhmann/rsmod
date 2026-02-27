package org.rsmod.content.areas.misc.goblinvillage.map

import org.rsmod.api.type.builders.map.obj.MapObjSpawnBuilder
import org.rsmod.content.areas.misc.goblinvillage.GoblinVillageScript

object GoblinVillageObjSpawns : MapObjSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<GoblinVillageScript>("objs.toml")
    }
}
