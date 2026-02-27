package org.rsmod.api.cache.map.loc

import it.unimi.dsi.fastutil.longs.LongArrayList
import org.rsmod.api.cache.util.InlineByteBuf

public object MapLocListDecoder {
    public fun decode(buf: InlineByteBuf): MapLocListDefinition {
        val locs = LongArrayList(buf.backing.size * 5 / 2)
        val initialCursor = buf.newCursor()
        val (currLocId, cursor) = decodeLocations(buf, initialCursor, -1, locs)
        return MapLocListDefinition(locs)
    }

    private tailrec fun decodeLocations(
        buf: InlineByteBuf,
        cursor: InlineByteBuf.Cursor,
        currLocId: Int,
        locs: LongArrayList,
    ): Pair<Int, InlineByteBuf.Cursor> {
        if (!buf.isReadable(cursor)) {
            return currLocId to cursor
        }

        val newCursor = buf.readIncrShortSmart(cursor)
        val offset = newCursor.value
        if (offset == 0) {
            return currLocId to newCursor
        }

        val nextLocId = currLocId + offset
        val (finalCursor, finalLocalCoords) =
            decodeLocationCoords(buf, newCursor, 0, nextLocId, locs)
        return decodeLocations(buf, finalCursor, nextLocId, locs)
    }

    private tailrec fun decodeLocationCoords(
        buf: InlineByteBuf,
        cursor: InlineByteBuf.Cursor,
        localCoords: Int,
        currLocId: Int,
        locs: LongArrayList,
    ): Pair<InlineByteBuf.Cursor, Int> {
        if (!buf.isReadable(cursor)) {
            return cursor to localCoords
        }

        val newCursor = buf.readShortSmart(cursor)
        val diff = newCursor.value
        if (diff == 0) {
            return newCursor to localCoords
        }

        val nextLocalCoords = localCoords + diff - 1
        val attribsCursor = buf.readByte(newCursor)
        val attribs = attribsCursor.value
        locs += MapLocDefinition(currLocId, nextLocalCoords, attribs).packed

        return decodeLocationCoords(buf, attribsCursor, nextLocalCoords, currLocId, locs)
    }
}
