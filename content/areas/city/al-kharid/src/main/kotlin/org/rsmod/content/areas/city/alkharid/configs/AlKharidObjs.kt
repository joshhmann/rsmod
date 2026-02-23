@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.alkharid.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias al_kharid_objs = AlKharidObjs

internal object AlKharidObjs : ObjReferences() {
    // Hides and leather
    val cow_hide = find("cow_hide")
    val leather = find("leather")
    val hard_leather = find("hard_leather")

    // Crafting supplies (for Dommik's shop)
    val ring_mould = find("ring_mould")
    val necklace_mould = find("necklace_mould")
    val amulet_mould = find("amulet_mould")
    val needle = find("needle")
    val thread = find("thread")

    // Silk for silk trader
    val silk = find("silk")

    // Gems for gem trader
    val uncut_sapphire = find("uncut_sapphire")
    val uncut_emerald = find("uncut_emerald")
    val sapphire = find("sapphire")
    val emerald = find("emerald")

    // Platelegs/plateskirts (for shop stock)
    val bronze_platelegs = find("bronze_platelegs")
    val iron_platelegs = find("iron_platelegs")
    val steel_platelegs = find("steel_platelegs")
    val black_platelegs = find("black_platelegs")
    val mithril_platelegs = find("mithril_platelegs")
    val adamant_platelegs = find("adamant_platelegs")

    val bronze_plateskirt = find("bronze_plateskirt")
    val iron_plateskirt = find("iron_plateskirt")
    val steel_plateskirt = find("steel_plateskirt")
    val black_plateskirt = find("black_plateskirt")
    val mithril_plateskirt = find("mithril_plateskirt")
    val adamant_plateskirt = find("adamant_plateskirt")

    // Scimitars (for Zeke)
    val bronze_scimitar = find("bronze_scimitar")
    val iron_scimitar = find("iron_scimitar")
    val steel_scimitar = find("steel_scimitar")
    val mithril_scimitar = find("mithril_scimitar")
}
