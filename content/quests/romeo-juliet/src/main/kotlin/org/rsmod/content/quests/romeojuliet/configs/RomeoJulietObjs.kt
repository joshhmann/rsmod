@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.romeojuliet.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias romeo_juliet_objs = RomeoJulietObjs

internal object RomeoJulietObjs : ObjReferences() {
    val julietmessage = find("julietmessage")
    val cadavaberries = find("cadavaberries")
    val cadava = find("cadava")
}
