package org.rsmod.api.cache.types.midi

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.game.type.midi.MidiType
import org.rsmod.game.type.midi.MidiTypeList

public object MidiTypeDecoder {
    public fun decodeAll(cache: Cache): MidiTypeList {
        val types = Int2ObjectOpenHashMap<MidiType>()
        val groups = cache.list(Js5Archives.SONGS)
        for (group in groups) {
            types[group.id] =
                MidiType(checksum = group.checksum, internalId = group.id, internalName = null)
        }
        return MidiTypeList(types)
    }
}
