package org.rsmod.api.hunt

import org.rsmod.game.entity.Player
import org.rsmod.map.CoordGrid

public object AggroTolerance {
    /** Default NPC aggro scan range in tiles (Chebyshev). */
    public const val DEFAULT_AGGRO_RANGE: Int = 8

    /** Ticks before a player becomes tolerant in an area (~10 min at 0.6 s/tick). */
    public const val TOLERANCE_TICKS: Int = 1000

    /** Zone granularity in tiles (matches in-game 8×8 chunk size). */
    public const val TOLERANCE_ZONE_SIZE: Int = 8

    /** Tolerance entries are cleared when player moves more than this many zones away. */
    public const val TOLERANCE_ZONE_CLEAR_RADIUS: Int = 2

    /**
     * Returns `true` if [player] has been in the vicinity of [npcCoords] long enough to
     * have built up tolerance.
     */
    public fun isTolerant(
        player: Player,
        npcCoords: CoordGrid,
        currentTick: Int,
    ): Boolean {
        val zone = toleranceZoneKey(npcCoords)
        val firstEntry = player.aggroTolerance[zone] ?: return false
        return (currentTick - firstEntry) >= TOLERANCE_TICKS
    }

    /**
     * Updates the player's tolerance map each tick.
     */
    public fun update(
        player: Player,
        currentTick: Int,
    ) {
        val coords = player.coords
        val currentZone = toleranceZoneKey(coords)
        val playerZoneX = coords.x / TOLERANCE_ZONE_SIZE
        val playerZoneZ = coords.z / TOLERANCE_ZONE_SIZE
        player.aggroTolerance.keys.removeAll { zoneKey ->
            val zoneX = ((zoneKey shr 16) and 0xFFFF).toInt()
            val zoneZ = (zoneKey and 0xFFFF).toInt()
            val distX = kotlin.math.abs(zoneX - playerZoneX)
            val distZ = kotlin.math.abs(zoneZ - playerZoneZ)
            distX > TOLERANCE_ZONE_CLEAR_RADIUS || distZ > TOLERANCE_ZONE_CLEAR_RADIUS
        }
        player.aggroTolerance.getOrPut(currentZone) { currentTick }
    }

    /**
     * Computes a coarse zone key for [coords] using an [TOLERANCE_ZONE_SIZE]-tile grid.
     */
    public fun toleranceZoneKey(coords: CoordGrid): Long {
        val zoneX = coords.x / TOLERANCE_ZONE_SIZE
        val zoneZ = coords.z / TOLERANCE_ZONE_SIZE
        return (zoneX.toLong() shl 16) or zoneZ.toLong()
    }
}
