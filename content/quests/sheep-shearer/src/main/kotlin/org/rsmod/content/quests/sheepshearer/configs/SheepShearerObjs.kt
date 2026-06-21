@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.sheepshearer.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias sheep_shearer_objs = SheepShearerObjs

internal object SheepShearerObjs : ObjReferences() {
    val wool = find("wool")
    val shears = find("shears")
}
