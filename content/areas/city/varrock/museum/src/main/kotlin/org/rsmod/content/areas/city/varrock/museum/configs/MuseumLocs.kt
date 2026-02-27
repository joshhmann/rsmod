@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.varrock.museum.configs

import org.rsmod.api.type.refs.loc.LocReferences

typealias museum_locs = MuseumLocs

object MuseumLocs : LocReferences() {
    // Varrock Museum display cases - Ground floor (timeline displays)
    val display_saradomin_symbol_old = find("vm_digsite_finds_saradomin_symbol_old")
    val display_saradomin_symbol_ancient = find("vm_digsite_finds_saradomin_symbol_ancient")
    val display_pottery = find("vm_digsite_finds_pottery")
    val display_talisman = find("vm_digsite_finds_talisman")
    val display_tablet = find("vm_digsite_finds_tablet")
    val display_coin_senntisten = find("vm_digsite_finds_coin_senntisten")
    val display_coin_saranthium = find("vm_digsite_finds_coin_saranthium")

    // Natural History displays (first floor)
    val display_kalphite_queen = find("vm_nat_his_kalphite_queen")

    // Display case bases
    val displaycase_base = find("vm_displaycase_base_1x1")

    // Barriers and museum fixtures
    val barrier = find("vm_barrier")
    val barrier_corner = find("vm_barrier_corner")
    val barrier_end = find("vm_barrier_end")
}
