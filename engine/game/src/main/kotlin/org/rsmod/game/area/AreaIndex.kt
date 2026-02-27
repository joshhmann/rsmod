package org.rsmod.game.area

import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.shorts.ShortArrayList
import org.rsmod.annotations.InternalApi
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.util.FastPack
import org.rsmod.map.zone.ZoneKey

public class AreaIndex {
    /*
     * Note: For optimization, areas can be registered against coarser granularity (zones and map
     * squares). This reduces memory usage and startup time but means a single coordinate can be
     * matched by multiple overlapping entries. Each category (coord, zone, map square) supports up
     * to 5 areas, so in total a coord may resolve to up to 15 areas.
     *
     * This index delegates emulation accuracy; it is up to the registry to ensure a coordinate does
     * not resolve to more than 5 areas.
     */
    private val coordAreas = PackedAreaMap()
    private val zoneAreas = PackedAreaMap()
    private val mapSquareAreas = PackedAreaMap()

    /**
     * Appends all areas found at the given coordinate to [dest].
     *
     * _Note: [dest] must be cleared by caller when appropriate._
     */
    public fun putAreas(coord: CoordGrid, dest: ShortArrayList) {
        coordAreas.get(coord.packed, dest)

        val zone = FastPack.zoneKey(coord)
        zoneAreas.get(zone, dest)

        val mapSquare = FastPack.mapSquareKey(coord)
        mapSquareAreas.get(mapSquare, dest)
    }

    @InternalApi
    public fun registerAll(coord: CoordGrid, areas: Iterator<Short>) {
        val list = areas.nextList()
        if (list.isNotEmpty()) {
            coordAreas.register(coord.packed, list)
        }
    }

    @InternalApi
    public fun registerAll(zone: ZoneKey, areas: Iterator<Short>) {
        val list = areas.nextList()
        if (list.isNotEmpty()) {
            zoneAreas.register(zone.packed, list)
        }
    }

    @InternalApi
    public fun registerAll(mapSquare: MapSquareKey, areas: Iterator<Short>) {
        val list = areas.nextList()
        if (list.isNotEmpty()) {
            mapSquareAreas.register(mapSquare.id, list)
        }
    }

    private fun Iterator<Short>.nextList(): List<Short> {
        val list = mutableListOf<Short>()
        while (hasNext()) {
            list += next()
        }
        return list
    }

    private class PackedAreaMap {
        private val areas = Int2LongOpenHashMap()
        private val overflow = Int2ObjectOpenHashMap<ShortArray>()

        fun get(key: Int, dest: ShortArrayList) {
            val packed = areas[key]
            if (packed == areas.defaultReturnValue()) {
                return
            }
            unpackInto(packed, dest)
            val extra = overflow[key]
            if (extra != null) {
                for (area in extra) {
                    dest.add(area)
                }
            }
        }

        fun register(key: Int, list: List<Short>) {
            require(key !in areas) { "Key already registered: $key" }

            val area1 = list.getOrNull(0)?.plusOne() ?: SHORT_ZERO
            val area2 = list.getOrNull(1)?.plusOne() ?: SHORT_ZERO
            val area3 = list.getOrNull(2)?.plusOne() ?: SHORT_ZERO
            val area4 = list.getOrNull(3)?.plusOne() ?: SHORT_ZERO

            areas[key] = pack(area1, area2, area3, area4)

            if (list.size > 4) {
                val extra = list.subList(4, list.size).toShortArray()
                overflow[key] = extra
            }
        }

        private fun Short.plusOne(): Short = (toInt() + 1).toShort()

        private companion object {
            private const val AREA_BIT_COUNT = 16
            private const val AREA_BIT_MASK = (1L shl AREA_BIT_COUNT) - 1

            private const val SLOT_1_OFFSET = 0
            private const val SLOT_2_OFFSET = SLOT_1_OFFSET + AREA_BIT_COUNT
            private const val SLOT_3_OFFSET = SLOT_2_OFFSET + AREA_BIT_COUNT
            private const val SLOT_4_OFFSET = SLOT_3_OFFSET + AREA_BIT_COUNT

            private fun unpackInto(packed: Long, dest: ShortArrayList) {
                val area1 = ((packed shr SLOT_1_OFFSET) and AREA_BIT_MASK).toShort()
                if (area1 != SHORT_ZERO) {
                    dest.add((area1 - 1).toShort())
                }

                val area2 = ((packed shr SLOT_2_OFFSET) and AREA_BIT_MASK).toShort()
                if (area2 != SHORT_ZERO) {
                    dest.add((area2 - 1).toShort())
                }

                val area3 = ((packed shr SLOT_3_OFFSET) and AREA_BIT_MASK).toShort()
                if (area3 != SHORT_ZERO) {
                    dest.add((area3 - 1).toShort())
                }

                val area4 = ((packed shr SLOT_4_OFFSET) and AREA_BIT_MASK).toShort()
                if (area4 != SHORT_ZERO) {
                    dest.add((area4 - 1).toShort())
                }
            }

            private fun pack(area1: Short, area2: Short, area3: Short, area4: Short): Long {
                var result = 0L
                result = result or ((area1.toLong() and AREA_BIT_MASK) shl SLOT_1_OFFSET)
                result = result or ((area2.toLong() and AREA_BIT_MASK) shl SLOT_2_OFFSET)
                result = result or ((area3.toLong() and AREA_BIT_MASK) shl SLOT_3_OFFSET)
                result = result or ((area4.toLong() and AREA_BIT_MASK) shl SLOT_4_OFFSET)
                return result
            }
        }
    }

    public companion object {
        private const val SHORT_ZERO: Short = 0
    }
}
