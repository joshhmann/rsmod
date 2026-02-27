package org.rsmod.api.cache.types.jingle

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.game.type.jingle.JingleType
import org.rsmod.game.type.jingle.JingleTypeList

public object JingleTypeDecoder {
    public fun decodeAll(cache: Cache): JingleTypeList {
        val types = Int2ObjectOpenHashMap<JingleType>()
        val groups = cache.list(Js5Archives.JINGLES)
        for (group in groups) {
            types[group.id] =
                JingleType(checksum = group.checksum, internalId = group.id, internalName = null)
        }
        return JingleTypeList(types)
    }
}
