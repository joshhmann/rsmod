package org.rsmod.content.areas.misc.darkwizardtower.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.misc.darkwizardtower.DarkWizardTowerScript

object DarkWizardTowerNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<DarkWizardTowerScript>("npcs.toml")
    }
}
