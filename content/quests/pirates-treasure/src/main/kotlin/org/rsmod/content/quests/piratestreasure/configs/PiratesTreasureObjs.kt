@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.piratestreasure.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias pirates_treasure_objs = PiratesTreasureObjs

internal object PiratesTreasureObjs : ObjReferences() {
    val karamja_rum = find("karamja_rum")
    val chest_key = find("chest_key")
    val piratemessage = find("piratemessage")
    val spade = find("spade")
    val coins = find("coins")
}
