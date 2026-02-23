@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.skills.farming.scripts.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias farming_objs = FarmingObjs

internal object FarmingObjs : ObjReferences() {
    val rake = find("rake")
    val dibber = find("dibber")
    val plant_cure = find("plant_cure")
    val guam_seed = find("guam_seed")
    val marrentill_seed = find("marrentill_seed")
}
