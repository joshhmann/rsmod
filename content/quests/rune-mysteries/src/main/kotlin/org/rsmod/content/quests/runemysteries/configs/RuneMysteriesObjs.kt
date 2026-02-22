@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.runemysteries.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias rune_mysteries_objs = RuneMysteriesObjs

internal object RuneMysteriesObjs : ObjReferences() {
    val digtalisman = find("digtalisman")
    val research_package = find("research_package")
}
