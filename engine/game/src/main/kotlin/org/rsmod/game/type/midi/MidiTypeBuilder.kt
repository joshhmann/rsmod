package org.rsmod.game.type.midi

@DslMarker private annotation class MidiBuilderDsl

@MidiBuilderDsl
public class MidiTypeBuilder(
    public var internalName: String? = null,
    public var checksum: Int? = null,
) {
    public fun build(id: Int): MidiType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        val checksum = checksum ?: 0
        return MidiType(internalId = id, internalName = internalName, checksum = checksum)
    }
}
