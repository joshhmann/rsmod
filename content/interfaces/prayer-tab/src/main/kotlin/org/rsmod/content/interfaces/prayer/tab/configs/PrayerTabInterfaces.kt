package org.rsmod.content.interfaces.prayer.tab.configs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences

internal typealias prayer_components = PrayerTabComponents

internal typealias prayer_interfaces = PrayerTabInterfaces

object PrayerTabComponents : ComponentReferences() {
    val quick_prayers_orb = find("orbs:prayerbutton")
    val quick_prayers_close = find("quickprayer:close")
    val quick_prayers_setup = find("quickprayer:buttons")
    val filters = find("prayerbook:filtermenu")
}

object PrayerTabInterfaces : InterfaceReferences() {
    val quickprayer = find("quickprayer")
}
