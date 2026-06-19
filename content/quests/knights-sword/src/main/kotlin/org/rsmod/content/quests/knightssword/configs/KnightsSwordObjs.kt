@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.knightssword.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias knights_sword_objs = KnightsSwordObjs

internal object KnightsSwordObjs : ObjReferences() {
    val knights_portrait = find("knights_portrait")
    val faladian_sword = find("faladian_sword")
    val blurite_ore = find("blurite_ore")
    val iron_bar = find("iron_bar")
    val redberry_pie = find("redberry_pie")
}
