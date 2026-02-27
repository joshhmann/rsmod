package org.rsmod.api.cache.map.area

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMaps
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectMaps
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import it.unimi.dsi.fastutil.shorts.ShortArraySet
import it.unimi.dsi.fastutil.shorts.ShortSet
import it.unimi.dsi.fastutil.shorts.ShortSets
import org.rsmod.api.cache.util.InlineByteBuf

public object MapAreaDecoder {
    public fun decode(buf: InlineByteBuf): MapAreaDefinition {
        val cursor = buf.newCursor()

        // Decode areas associated with the entire map square block (all 64x64x4 tiles).
        val (cursor1, fullMapSqAreas) = decodeFullMapSquareAreas(buf, cursor)

        // Decode areas associated with entire zone blocks (8x8 tiles).
        val (cursor2, zoneAreas) = decodeZoneAreas(buf, cursor1)

        // Decode individual tile areas.
        val (_, coordAreas) = decodeCoordAreas(buf, cursor2)

        return MapAreaDefinition(
            mapSquareAreas = fullMapSqAreas,
            zoneAreas = zoneAreas,
            coordAreas = coordAreas,
        )
    }

    private fun decodeFullMapSquareAreas(
        buf: InlineByteBuf,
        initialCursor: InlineByteBuf.Cursor,
    ): Pair<InlineByteBuf.Cursor, ShortSet> {
        val cursor = buf.readUnsignedByte(initialCursor)
        val fullMapSqAreaCount = cursor.value
        if (fullMapSqAreaCount == 0) {
            return cursor to ShortSets.emptySet()
        }
        val areas = ShortArraySet()
        var currentCursor = cursor
        repeat(fullMapSqAreaCount) {
            currentCursor = buf.readShort(currentCursor)
            val area = currentCursor.value.toShort()
            areas.add(area)
        }
        return currentCursor to areas
    }

    private fun decodeZoneAreas(
        buf: InlineByteBuf,
        initialCursor: InlineByteBuf.Cursor,
    ): Pair<InlineByteBuf.Cursor, Byte2ObjectMap<ShortSet>> {
        val cursor = buf.readUnsignedByte(initialCursor)
        val zoneCount = cursor.value
        if (zoneCount == 0) {
            return cursor to Byte2ObjectMaps.emptyMap()
        }
        val areas = Byte2ObjectOpenHashMap<ShortSet>()
        var currentCursor = cursor
        repeat(zoneCount) {
            currentCursor = buf.readByte(currentCursor)
            val localZone = currentCursor.value.toByte()

            currentCursor = buf.readUnsignedByte(currentCursor)
            val areaCount = currentCursor.value

            val areaSet = ShortArraySet(areaCount)
            repeat(areaCount) {
                currentCursor = buf.readShort(currentCursor)
                val area = currentCursor.value.toShort()
                areaSet.add(area)
            }
            areas[localZone] = areaSet
        }
        return currentCursor to areas
    }

    private fun decodeCoordAreas(
        buf: InlineByteBuf,
        initialCursor: InlineByteBuf.Cursor,
    ): Pair<InlineByteBuf.Cursor, Short2ObjectMap<ShortSet>> {
        val cursor = buf.readUnsignedByte(initialCursor)
        val coordCount = cursor.value
        if (coordCount == 0) {
            return cursor to Short2ObjectMaps.emptyMap()
        }
        val areas = Short2ObjectOpenHashMap<ShortSet>()
        var currentCursor = cursor
        repeat(coordCount) {
            currentCursor = buf.readShort(currentCursor)
            val grid = currentCursor.value.toShort()

            currentCursor = buf.readUnsignedByte(currentCursor)
            val areaCount = currentCursor.value

            val areaSet = ShortArraySet(areaCount)
            repeat(areaCount) {
                currentCursor = buf.readShort(currentCursor)
                val area = currentCursor.value.toShort()
                areaSet.add(area)
            }
            areas[grid] = areaSet
        }
        return currentCursor to areas
    }
}
