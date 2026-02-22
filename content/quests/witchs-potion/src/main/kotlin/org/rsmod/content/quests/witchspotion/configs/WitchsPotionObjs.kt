@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.witchspotion.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias witchs_potion_objs = WitchsPotionObjs

internal object WitchsPotionObjs : ObjReferences() {
    val rats_tail = find("rats_tail")
    val eye_of_newt = find("eye_of_newt")
    val burnt_meat = find("burnt_meat")
    val onion = find("onion")
}
