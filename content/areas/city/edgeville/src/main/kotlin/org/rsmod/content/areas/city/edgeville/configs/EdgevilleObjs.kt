@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.edgeville.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias edgeville_objs = EdgevilleObjs

internal object EdgevilleObjs : ObjReferences() {
    // Medium Helmets (for Peksa's shop)
    val bronze_med_helm = find("bronze_med_helm")
    val iron_med_helm = find("iron_med_helm")
    val steel_med_helm = find("steel_med_helm")
    val black_med_helm = find("black_med_helm")
    val mithril_med_helm = find("mithril_med_helm")
    val adamant_med_helm = find("adamant_med_helm")
    val rune_med_helm = find("rune_med_helm")
    val dragon_med_helm = find("dragon_med_helm")
}
