package org.rsmod.content.areas.misc.catherby.map

import org.rsmod.api.type.builders.map.obj.MapObjSpawnBuilder
import org.rsmod.content.areas.misc.catherby.CatherbyScript

object CatherbyObjSpawns : MapObjSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<CatherbyScript>("objs.toml")
    }
}
