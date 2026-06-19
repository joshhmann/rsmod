package org.rsmod.game.type.enums

public data class EnumTypeList(public val types: Map<Int, UnpackedEnumType<Any, Any>>) :
    Map<Int, UnpackedEnumType<Any, Any>> by types {
    @Suppress("UNCHECKED_CAST")
    public operator fun <K : Any, V : Any> get(type: EnumType<K, V>): UnpackedEnumType<K, V> {
        val mapped = getOrNull(type) ?: throw NoSuchElementException("Type is missing in the map: $type.")
        return mapped
    }

    @Suppress("UNCHECKED_CAST")
    public fun <K : Any, V : Any> getOrNull(type: EnumType<K, V>): UnpackedEnumType<K, V>? {
        val mapped = types[type.id] ?: return null
        return mapped as UnpackedEnumType<K, V>
    }
}
