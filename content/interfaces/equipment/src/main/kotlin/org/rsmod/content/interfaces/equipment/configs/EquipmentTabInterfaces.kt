package org.rsmod.content.interfaces.equipment.configs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences

typealias equip_components = EquipmentTabComponents

typealias equip_interfaces = EquipmentTabInterfaces

object EquipmentTabComponents : ComponentReferences() {
    val equipment = find("wornitems:equipment")
    val guide_prices = find("wornitems:pricechecker")
    val items_kept_on_death = find("wornitems:deathkeep")
    val call_follower = find("wornitems:call_follower")

    val equipment_stats_side_inv = find("equipment_side:items")
    val equipment_stats_off_stab = find("equipment:stabatt")
    val equipment_stats_off_slash = find("equipment:slashatt")
    val equipment_stats_off_crush = find("equipment:crushatt")
    val equipment_stats_off_magic = find("equipment:magicatt")
    val equipment_stats_off_range = find("equipment:rangeatt")
    val equipment_stats_speed_base = find("equipment:attackspeedbase")
    val equipment_stats_speed = find("equipment:attackspeedactual")
    val equipment_stats_def_stab = find("equipment:stabdef")
    val equipment_stats_def_slash = find("equipment:slashdef")
    val equipment_stats_def_crush = find("equipment:crushdef")
    val equipment_stats_def_range = find("equipment:magicdef")
    val equipment_stats_def_magic = find("equipment:rangedef")
    val equipment_stats_melee_str = find("equipment:meleestrength")
    val equipment_stats_ranged_str = find("equipment:rangestrength")
    val equipment_stats_magic_dmg = find("equipment:magicdamage")
    val equipment_stats_prayer = find("equipment:prayer")
    val equipment_stats_undead = find("equipment:typemultiplier")
    val equipment_stats_undead_tooltip = find("equipment:tooltip")
    val equipment_stats_slayer = find("equipment:slayermultiplier")

    val guide_prices_side_inv = find("ge_pricechecker_side:items")
    val guide_prices_main_inv = find("ge_pricechecker:items")
    val guide_prices_search = find("ge_pricechecker:other")
    val guide_prices_search_obj = find("ge_pricechecker:otheritem")
    val guide_prices_add_all = find("ge_pricechecker:all")
    val guide_prices_total_price_text = find("ge_pricechecker:output")

    val items_kept_on_death_pbutton = find("deathkeep:right")
    val items_kept_on_death_risk = find("deathkeep:value")
}

object EquipmentTabInterfaces : InterfaceReferences() {
    val equipment_stats_main = find("equipment")
    val equipment_stats_side = find("equipment_side")
    val guide_prices_main = find("ge_pricechecker")
    val guide_prices_side = find("ge_pricechecker_side")
    val deathkeep = find("deathkeep")
}
