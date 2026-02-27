package org.rsmod.content.areas.misc.wizardstower.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.misc.wizardstower.WizardsTowerScript

object WizardsTowerNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<WizardsTowerScript>("npcs.toml")
    }
}
