package org.rsmod.content.areas.city.karamja
import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.city.karamja.KaramjaScript
object KaramjaNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() { resourceFile<KaramjaScript>("npcs.toml") }
}
