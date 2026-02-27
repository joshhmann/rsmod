package org.rsmod.game.type.jingle

@DslMarker private annotation class JingleBuilderDsl

@JingleBuilderDsl
public class JingleTypeBuilder(
    public var internalName: String? = null,
    public var checksum: Int? = null,
) {
    public fun build(id: Int): JingleType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        val checksum = checksum ?: 0
        return JingleType(internalId = id, internalName = internalName, checksum = checksum)
    }
}
