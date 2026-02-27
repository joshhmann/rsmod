package org.rsmod.api.cache.map.obj

import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongLists
import org.rsmod.api.cache.util.InlineByteBuf

public object MapObjListDecoder {
    public fun decode(buf: InlineByteBuf): MapObjListDefinition {
        val cursor = buf.newCursor()
        val countCursor = buf.readShort(cursor)
        val count = countCursor.value
        if (count == 0) {
            return MapObjListDefinition(LongLists.EMPTY_LIST)
        }

        val objs = LongArrayList(count)
        decodeObjs(buf, countCursor, count, objs)
        return MapObjListDefinition(objs)
    }

    private tailrec fun decodeObjs(
        buf: InlineByteBuf,
        cursor: InlineByteBuf.Cursor,
        remaining: Int,
        objs: LongArrayList,
    ) {
        if (remaining <= 0) {
            return
        }

        val highCursor = buf.readInt(cursor)
        val high = highCursor.value.toLong()

        val lowCursor = buf.readInt(highCursor)
        val low = lowCursor.value.toLong()

        val packed = ((high and 0xFFFFFFFFL) shl 32) or (low and 0xFFFFFFFFL)
        objs.add(packed)

        decodeObjs(buf, lowCursor, remaining - 1, objs)
    }
}
