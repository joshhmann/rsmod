package org.rsmod.game.enums

import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.enums.EnumTypeList
import org.rsmod.game.type.enums.HashedEnumType
import org.rsmod.game.type.enums.UnpackedEnumType

public class EnumTypeMapResolver(private val types: EnumTypeList) {
    public operator fun <K : Any, V : Any> get(enum: EnumType<K, V>): EnumTypeMap<K, V> {
        val unpacked = types.getOrNull(enum)
        if (unpacked == null) {
            val empty = UnpackedEnumType.empty(enum.keyType(), enum.valType(), enum.internalName)
            return EnumTypeMap(empty)
        }
        return EnumTypeMap(unpacked)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <K : Any, V : Any> EnumType<K, V>.keyType(): kotlin.reflect.KClass<K> =
        when (this) {
            is HashedEnumType<K, V> -> keyType
            is UnpackedEnumType<K, V> -> keyType
        }

    @Suppress("UNCHECKED_CAST")
    private fun <K : Any, V : Any> EnumType<K, V>.valType(): kotlin.reflect.KClass<V> =
        when (this) {
            is HashedEnumType<K, V> -> valType
            is UnpackedEnumType<K, V> -> valType
        }
}
