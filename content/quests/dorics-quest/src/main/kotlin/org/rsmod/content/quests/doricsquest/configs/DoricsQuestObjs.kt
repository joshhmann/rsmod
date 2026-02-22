@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.doricsquest.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias dorics_quest_objs = DoricsQuestObjs

internal object DoricsQuestObjs : ObjReferences() {
    val clay = find("clay")
    val copper_ore = find("copper_ore")
    val iron_ore = find("iron_ore")
    val coins = find("coins")
}
