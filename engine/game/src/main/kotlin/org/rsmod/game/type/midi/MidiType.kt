package org.rsmod.game.type.midi

import org.rsmod.game.type.CacheType

public data class MidiType(
    public val checksum: Int,
    override var internalId: Int?,
    override var internalName: String?,
) : CacheType() {
    override fun toString(): String =
        "MidiType(internalName='$internalName', internalId=$internalId, checksum=$checksum)"

    // Note: This is a placeholder as we will likely add an UnpackedMidiType/HashedMidiType in the
    // future. We do not want to force ourselves to remember all the places where this should be
    // called once that happens.
    public fun toHashedType(): MidiType = this
}
