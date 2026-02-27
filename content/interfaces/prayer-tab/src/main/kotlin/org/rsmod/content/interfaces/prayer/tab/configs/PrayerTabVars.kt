package org.rsmod.content.interfaces.prayer.tab.configs

import org.rsmod.api.type.refs.varbit.VarBitReferences

internal typealias prayer_varbits = PrayerTabVarBits

object PrayerTabVarBits : VarBitReferences() {
    val filter_show_lower_tiers = find("prayer_filter_blocklowtier")
    val filter_show_tiered_prayers = find("prayer_filter_allowcombinedtier")
    val filter_show_rapid_healing = find("prayer_filter_blockhealing")
    val filter_show_prayers_fail_lvl = find("prayer_filter_blocklacklevel")
    val filter_show_prayers_fail_req = find("prayer_filter_blocklocked")
    val quickprayer_selected = find("quickprayer_selected")
    val enabled_prayers = find("prayer_allactive")
}
