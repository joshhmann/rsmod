@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.ernestchicken.configs

import org.rsmod.api.type.refs.obj.ObjReferences

typealias ernest_chicken_objs = ErnestChickenObjs

object ErnestChickenObjs : ObjReferences() {
    val fish_food = find("fish_food")
    val poison = find("poison")
    val poisoned_fish_food = find("poisoned_fish_food")
    val pressure_gauge = find("pressure_gauge")
    val rubber_tube = find("rubber_tube")
    val oil_can = find("oil_can")
}
