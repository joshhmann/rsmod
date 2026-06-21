package org.rsmod.content.skills.ranged.configs

import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.seq.SeqReferences

internal object RangedObjs : ObjReferences() {
    // F2P Shortbows
    val shortbow = find("shortbow")
    val oak_shortbow = find("oak_shortbow")
    val willow_shortbow = find("willow_shortbow")
    val maple_shortbow = find("maple_shortbow")
    val yew_shortbow = find("yew_shortbow")
    val magic_shortbow = find("magic_shortbow")

    // F2P Longbows
    val longbow = find("longbow")
    val oak_longbow = find("oak_longbow")
    val willow_longbow = find("willow_longbow")
    val maple_longbow = find("maple_longbow")
    val yew_longbow = find("yew_longbow")
    val magic_longbow = find("magic_longbow")

    // F2P Arrows
    val bronze_arrow = find("bronze_arrow")
    val iron_arrow = find("iron_arrow")
    val steel_arrow = find("steel_arrow")
    val mithril_arrow = find("mithril_arrow")
    val adamant_arrow = find("adamant_arrow")
    val rune_arrow = find("rune_arrow")

    // Arrowheads (for reference)
    val bronze_arrowheads = find("bronze_arrowheads")
    val iron_arrowheads = find("iron_arrowheads")
    val steel_arrowheads = find("steel_arrowheads")
    val mithril_arrowheads = find("mithril_arrowheads")
    val adamant_arrowheads = find("adamant_arrowheads")
    val rune_arrowheads = find("rune_arrowheads")

    // Unstrung bows (fletching products)
    val shortbow_u = find("unstrung_shortbow")
    val longbow_u = find("unstrung_longbow")
    val oak_shortbow_u = find("unstrung_oak_shortbow")
    val oak_longbow_u = find("unstrung_oak_longbow")
    val willow_shortbow_u = find("unstrung_willow_shortbow")
    val willow_longbow_u = find("unstrung_willow_longbow")
    val maple_shortbow_u = find("unstrung_maple_shortbow")
    val maple_longbow_u = find("unstrung_maple_longbow")
    val yew_shortbow_u = find("unstrung_yew_shortbow")
    val yew_longbow_u = find("unstrung_yew_longbow")
    val magic_shortbow_u = find("unstrung_magic_shortbow")
    val magic_longbow_u = find("unstrung_magic_longbow")
}

internal object RangedSeqs : SeqReferences() {
    val human_bow = find("human_bow")
}
