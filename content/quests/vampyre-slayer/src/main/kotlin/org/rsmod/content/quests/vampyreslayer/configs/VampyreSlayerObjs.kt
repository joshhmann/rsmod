package org.rsmod.content.quests.vampyreslayer.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias vampyre_slayer_objs = VampyreSlayerObjs

internal object VampyreSlayerObjs : ObjReferences() {
    val stake = find("stake")
    val garlic = find("garlic")
}
