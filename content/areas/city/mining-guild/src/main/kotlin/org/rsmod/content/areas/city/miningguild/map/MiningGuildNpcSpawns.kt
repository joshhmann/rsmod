package org.rsmod.content.areas.city.miningguild.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.city.miningguild.MiningGuildScript

/**
 * NPC spawns for the Mining Guild.
 *
 * The Mining Guild is located in:
 * - Above ground: South-east Falador (region 47, 52)
 * - Underground: Connected to Dwarven Mine
 *
 * Coordinates use the format: plane_regionX_regionY_localX_localY
 */
object MiningGuildNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<MiningGuildScript>("npcs.toml")
    }
}
