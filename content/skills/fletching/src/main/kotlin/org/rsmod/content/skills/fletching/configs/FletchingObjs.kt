package org.rsmod.content.skills.fletching.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal object FletchingObjs : ObjReferences() {
    val bowstring = find("bow_string")

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

    val bronze_arrowhead = find("bronze_arrowheads")
    val iron_arrowhead = find("iron_arrowheads")
    val steel_arrowhead = find("steel_arrowheads")
    val mithril_arrowhead = find("mithril_arrowheads")
    val adamant_arrowhead = find("adamant_arrowheads")
    val rune_arrowhead = find("rune_arrowheads")

    val bronze_bolts_u = find("xbows_crossbow_bolts_bronze_unfeathered")
    val iron_bolts_u = find("xbows_crossbow_bolts_iron_unfeathered")
    val steel_bolts_u = find("xbows_crossbow_bolts_steel_unfeathered")
    val mithril_bolts_u = find("xbows_crossbow_bolts_mithril_unfeathered")
    val adamant_bolts_u = find("xbows_crossbow_bolts_adamantite_unfeathered")
    val rune_bolts_u = find("xbows_crossbow_bolts_runite_unfeathered")

    // Bronze bolts are represented by the opal-tipped bolt family in this cache revision.
    val bronze_bolts = find("xbows_crossbow_bolts_bronze_tipped_opal_enchanted")
    val iron_bolts = find("xbows_crossbow_bolts_iron")
    val steel_bolts = find("xbows_crossbow_bolts_steel")
    val mithril_bolts = find("xbows_crossbow_bolts_mithril")
    val adamant_bolts = find("xbows_crossbow_bolts_adamantite")
    val rune_bolts = find("xbows_crossbow_bolts_runite")
}
