package org.rsmod.api.game.process.player

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import jakarta.inject.Inject
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Logger
import net.rsprot.protocol.common.client.OldSchoolClientType
import net.rsprot.protocol.game.outgoing.zone.header.UpdateZoneFullFollows
import net.rsprot.protocol.game.outgoing.zone.header.UpdateZonePartialEnclosed
import net.rsprot.protocol.game.outgoing.zone.header.UpdateZonePartialFollows
import net.rsprot.protocol.message.ZoneProt
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.api.registry.zone.ZoneUpdateMap
import org.rsmod.api.registry.zone.ZoneUpdateTransformer
import org.rsmod.api.registry.zone.ZoneUpdateTransformer.priority
import org.rsmod.api.utils.map.BuildAreaUtils
import org.rsmod.api.utils.zone.SharedZoneEnclosedBuffers
import org.rsmod.game.entity.Player
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.obj.Obj
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

public class PlayerZoneUpdateProcessor
@Inject
constructor(
    private val updates: ZoneUpdateMap,
    private val locReg: LocRegistry,
    private val objReg: ObjRegistry,
    private val enclosedBuffers: SharedZoneEnclosedBuffers,
) {
    public fun computeEnclosedBuffers() {
        enclosedBuffers.computeSharedBuffers()
    }

    public fun process(player: Player) {
        player.processZoneUpdates()
    }

    public fun clearEnclosedBuffers() {
        enclosedBuffers.clear()
    }

    public fun clearPendingZoneUpdates() {
        updates.clear()
    }

    private fun Player.processZoneUpdates() {
        val processStart = if (ZoneUpdateTelemetry.enabled) System.nanoTime() else 0L
        var newVisibleNanos = 0L
        var visibleUpdateNanos = 0L
        var changedZone = false

        val currZone = ZoneKey.from(coords)
        val visibleZones = visibleZoneKeys
        val prevZone = lastProcessedZone
        val buildArea = buildArea

        if (currZone != prevZone) {
            changedZone = true
            // Compute neighbouring zones based on the player's current zone.
            val currZones =
                currZone.computeVisibleNeighbouringZones().filterWithinBuildArea(buildArea)

            // Determine the newly visible zones that were not previously visible.
            // These are zones that need to be reset and have persistent updates/entities sent.
            val newZones = IntArrayList(currZones).apply { removeAll(visibleZones) }
            val newVisibleStart = if (ZoneUpdateTelemetry.enabled) System.nanoTime() else 0L
            processNewVisibleZones(buildArea, newZones)
            if (ZoneUpdateTelemetry.enabled) {
                newVisibleNanos += System.nanoTime() - newVisibleStart
            }

            // Update the player's cached visible zone keys to reflect the current visible zones.
            refreshVisibleZoneKeys(currZones)

            // Identify zones that have been visible for more than one cycle (or one call to this
            // processor). These zones will have their transient updates sent. This prevents a newly
            // visible zone from immediately sending a transient update (e.g., an `ObjAdd` update)
            // right after a persistent entity update, which could occur if an obj is spawned on the
            // ground the same cycle the zone becomes visible to the player.
            val oldZones = IntArrayList(currZones).apply { removeAll(newZones) }
            val visibleUpdateStart = if (ZoneUpdateTelemetry.enabled) System.nanoTime() else 0L
            processVisibleZoneUpdates(buildArea, oldZones)
            if (ZoneUpdateTelemetry.enabled) {
                visibleUpdateNanos += System.nanoTime() - visibleUpdateStart
            }
        } else {
            // If the player hasn't moved to a new zone, process updates for currently visible
            // zones.
            val visibleUpdateStart = if (ZoneUpdateTelemetry.enabled) System.nanoTime() else 0L
            processVisibleZoneUpdates(buildArea, visibleZones)
            if (ZoneUpdateTelemetry.enabled) {
                visibleUpdateNanos += System.nanoTime() - visibleUpdateStart
            }
        }

        lastProcessedZone = currZone

        if (ZoneUpdateTelemetry.enabled) {
            ZoneUpdateTelemetry.record(
                totalProcessNanos = System.nanoTime() - processStart,
                newVisibleNanos = newVisibleNanos,
                visibleUpdateNanos = visibleUpdateNanos,
                changedZone = changedZone,
            )
        }
    }

    private fun Player.processNewVisibleZones(buildArea: CoordGrid, zones: IntList) {
        for (zone in zones.intIterator()) {
            val key = ZoneKey(zone)
            sendZoneResetUpdate(buildArea, key.toCoords())
            sendZonePersistentUpdates(key)
        }
    }

    private fun Player.sendZoneResetUpdate(buildArea: CoordGrid, zoneBase: CoordGrid) {
        val deltaX = zoneBase.x - buildArea.x
        val deltaZ = zoneBase.z - buildArea.z
        val message = UpdateZoneFullFollows(deltaX, deltaZ, zoneBase.level)
        client.write(message)
    }

    private fun Player.sendZonePersistentUpdates(zone: ZoneKey) {
        val spawnedLocs = locReg.findAllSpawned(zone)
        sendPersistentLocs(spawnedLocs)

        val spawnedObjs = objReg.findAll(zone)
        sendPersistentObjs(spawnedObjs, observerUUID)
    }

    private fun Player.sendPersistentLocs(locs: Sequence<LocInfo>) {
        for (loc in locs) {
            val prot = ZoneUpdateTransformer.toPersistentLocChange(loc)
            client.write(prot)
        }
    }

    private fun Player.sendPersistentObjs(objs: Sequence<Obj>, observerId: Long?) {
        for (obj in objs) {
            val prot = ZoneUpdateTransformer.toPersistentObjAdd(obj, observerId) ?: continue
            client.write(prot)
        }
    }

    private fun Player.refreshVisibleZoneKeys(zones: IntList) {
        visibleZoneKeys.clear()
        visibleZoneKeys.addAll(zones)
    }

    private fun Player.processVisibleZoneUpdates(buildArea: CoordGrid, currZones: List<Int>) {
        val iterationOrder = selectZoneIterationOrder(currZones)
        for (zone in iterationOrder) {
            val zoneKey = ZoneKey(zone)
            val zoneBase = zoneKey.toCoords()
            sendZonePartialFollowsUpdates(buildArea, zoneKey, zoneBase)
            sendZoneSharedEnclosedUpdates(buildArea, zoneKey, zoneBase)
        }
    }

    private fun selectZoneIterationOrder(currZones: List<Int>): Iterable<Int> {
        if (!STRICT_ZONE_SEND_ORDER) {
            return currZones
        }
        if (currZones.isEmpty()) {
            return emptyList()
        }
        val visibleSet = IntOpenHashSet(currZones)
        val ordered = updates.orderedZoneKeys()
        val filtered = IntArrayList(ordered.size)
        for (zone in ordered) {
            if (visibleSet.contains(zone)) {
                filtered.add(zone)
            }
        }
        return filtered
    }

    private fun Player.sendZonePartialFollowsUpdates(
        buildArea: CoordGrid,
        zone: ZoneKey,
        zoneBase: CoordGrid,
    ) {
        val updates = updates[zone] ?: return
        check(updates.isNotEmpty) { "`updates` for zone should not be empty: $zone" }

        // Only updates implementing [ZoneProtTransformer.PartialFollowsZoneProt] should be sent
        // as part of the `UpdateZonePartialFollows` packet. To avoid sending a header with no
        // payload under the scenario where all zone updates are "hidden" (i.e., none of the objs
        // can be seen by the observer), we also filter updates that return `isHidden` as true.
        val filtered =
            updates
                .filterIsInstance<ZoneUpdateTransformer.PartialFollowsZoneProt>()
                .filterNot { it.isHidden(observerUUID) }
                .sortedByDescending { it.priority }
        if (filtered.isEmpty()) {
            return
        }
        val deltaX = zoneBase.x - buildArea.x
        val deltaZ = zoneBase.z - buildArea.z
        val message = UpdateZonePartialFollows(deltaX, deltaZ, zoneBase.level)
        client.write(message)
        for (prot in filtered) {
            client.write(prot.backing)
        }
    }

    private fun Player.sendZoneSharedEnclosedUpdates(
        buildArea: CoordGrid,
        zone: ZoneKey,
        zoneBase: CoordGrid,
    ) {
        val enclosed = enclosedBuffers[zone] ?: return
        val buffer = enclosed[OldSchoolClientType.DESKTOP] ?: return
        val deltaX = zoneBase.x - buildArea.x
        val deltaZ = zoneBase.z - buildArea.z
        val prot = UpdateZonePartialEnclosed(deltaX, deltaZ, zoneBase.level, buffer)
        client.write(prot)
    }

    private fun ZoneProt.isHidden(observerId: Long?): Boolean =
        this is ZoneUpdateTransformer.ObjPrivateZoneProt && !isVisibleTo(observerId) ||
            this is ZoneUpdateTransformer.ObjReveal && observerId == obj.receiverId

    private fun ZoneKey.computeVisibleNeighbouringZones(): IntList {
        val zones = IntArrayList(ZONE_VIEW_TOTAL_COUNT)
        for (x in -ZONE_VIEW_RADIUS..ZONE_VIEW_RADIUS) {
            for (z in -ZONE_VIEW_RADIUS..ZONE_VIEW_RADIUS) {
                val zone = translate(x, z)
                zones.add(zone.packed)
            }
        }
        return zones
    }

    private fun IntList.filterWithinBuildArea(buildArea: CoordGrid): IntList {
        val zones = IntArrayList(size)
        forEach { zone ->
            val zoneBase = ZoneKey(zone).toCoords()
            val deltaX = zoneBase.x - buildArea.x
            val deltaZ = zoneBase.z - buildArea.z
            val viewable = deltaX in BUILD_AREA_BOUNDS && deltaZ in BUILD_AREA_BOUNDS
            if (viewable) {
                zones.add(zone)
            }
        }
        return zones
    }

    public companion object {
        /**
         * When enabled, we emit zone updates using the chronological zone insertion order for the
         * current cycle instead of fixed visible-zone iteration order.
         */
        public val STRICT_ZONE_SEND_ORDER: Boolean =
            java.lang.Boolean.getBoolean("rsmod.zone_updates.strict_send_order")

        public const val ZONE_VIEW_RADIUS: Int = 3
        public const val ZONE_VIEW_TOTAL_COUNT: Int =
            (2 * ZONE_VIEW_RADIUS + 1) * (2 * ZONE_VIEW_RADIUS + 1)

        public val BUILD_AREA_BOUNDS: IntRange = 0 until BuildAreaUtils.SIZE
    }
}

private object ZoneUpdateTelemetry {
    private const val enabledKey: String = "rsmod.telemetry.zone-updates"
    private const val intervalKey: String = "rsmod.telemetry.zone-updates.interval-ms"

    private val logger: Logger = Logger.getLogger(ZoneUpdateTelemetry::class.java.name)

    val enabled: Boolean = java.lang.Boolean.getBoolean(enabledKey)

    private val intervalNanos: Long =
        TimeUnit.MILLISECONDS.toNanos(
            java.lang.Long.getLong(intervalKey, 5_000L).coerceAtLeast(1_000L)
        )

    private val lastLogAtNanos: AtomicLong = AtomicLong(System.nanoTime())
    private val processCount: AtomicLong = AtomicLong()
    private val zoneChangeCount: AtomicLong = AtomicLong()
    private val totalProcessNanos: AtomicLong = AtomicLong()
    private val totalNewVisibleNanos: AtomicLong = AtomicLong()
    private val totalVisibleUpdateNanos: AtomicLong = AtomicLong()
    private val maxProcessNanos: AtomicLong = AtomicLong()

    fun record(
        totalProcessNanos: Long,
        newVisibleNanos: Long,
        visibleUpdateNanos: Long,
        changedZone: Boolean,
    ) {
        processCount.incrementAndGet()
        if (changedZone) {
            zoneChangeCount.incrementAndGet()
        }
        this.totalProcessNanos.addAndGet(totalProcessNanos)
        this.totalNewVisibleNanos.addAndGet(newVisibleNanos)
        this.totalVisibleUpdateNanos.addAndGet(visibleUpdateNanos)
        maxProcessNanos.accumulateAndGet(totalProcessNanos, ::maxOf)
        maybeLog()
    }

    private fun maybeLog() {
        val now = System.nanoTime()
        val last = lastLogAtNanos.get()
        if (now - last < intervalNanos || !lastLogAtNanos.compareAndSet(last, now)) {
            return
        }

        val count = processCount.getAndSet(0)
        if (count <= 0L) {
            return
        }

        val changed = zoneChangeCount.getAndSet(0)
        val total = totalProcessNanos.getAndSet(0)
        val newVisible = totalNewVisibleNanos.getAndSet(0)
        val visibleUpdates = totalVisibleUpdateNanos.getAndSet(0)
        val max = maxProcessNanos.getAndSet(0)

        val divisor = count.toDouble()
        val avgTotalMicros = total / divisor / 1_000.0
        val avgNewVisibleMicros = newVisible / divisor / 1_000.0
        val avgVisibleUpdateMicros = visibleUpdates / divisor / 1_000.0
        val maxMicros = max / 1_000.0

        logger.info(
            "ZoneUpdateTelemetry[$enabledKey=true]: count=$count, zoneChanges=$changed, " +
                "avgTotalMicros=%.2f, avgNewVisibleMicros=%.2f, avgVisibleUpdateMicros=%.2f, maxMicros=%.2f"
                    .format(avgTotalMicros, avgNewVisibleMicros, avgVisibleUpdateMicros, maxMicros)
        )
    }
}
