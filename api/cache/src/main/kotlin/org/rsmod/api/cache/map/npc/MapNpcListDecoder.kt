package org.rsmod.api.cache.map.npc

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntLists
import org.rsmod.api.cache.util.InlineByteBuf

public object MapNpcListDecoder {
    public fun decode(buf: InlineByteBuf): MapNpcListDefinition {
        val cursor = buf.newCursor()
        val countCursor = buf.readShort(cursor)
        val count = countCursor.value
        if (count == 0) {
            return MapNpcListDefinition(IntLists.EMPTY_LIST)
        }

        val npcs = IntArrayList(count)
        decodeNpcs(buf, countCursor, count, npcs)
        return MapNpcListDefinition(npcs)
    }

    private tailrec fun decodeNpcs(
        buf: InlineByteBuf,
        cursor: InlineByteBuf.Cursor,
        remaining: Int,
        npcs: IntArrayList,
    ) {
        if (remaining <= 0) {
            return
        }

        val newCursor = buf.readInt(cursor)
        npcs.add(newCursor.value)
        decodeNpcs(buf, newCursor, remaining - 1, npcs)
    }
}
