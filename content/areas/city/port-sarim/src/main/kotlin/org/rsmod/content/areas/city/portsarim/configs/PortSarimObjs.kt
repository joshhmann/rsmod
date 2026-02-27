@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.portsarim.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias portsarim_objs = PortSarimObjs

internal object PortSarimObjs : ObjReferences() {
    // Food items for Wydin's Food Store
    val bread = find("bread")
    val cheese = find("cheese")
    val tomato = find("tomato")
    val cooked_meat = find("cooked_meat")

    // Pies
    val apple_pie = find("apple_pie")
    val redberry_pie = find("redberry_pie")
    val meat_pie = find("meat_pie")

    // Raw ingredients
    val potato = find("potato")
    val cabbage = find("cabbage")
    val onion = find("onion")
    val egg = find("egg")
    val pot_of_flour = find("pot_flour")

    // Battleaxes for Brian's shop
    val bronze_battleaxe = find("bronze_battleaxe")
    val iron_battleaxe = find("iron_battleaxe")
    val steel_battleaxe = find("steel_battleaxe")
    val black_battleaxe = find("black_battleaxe")
    val mithril_battleaxe = find("mithril_battleaxe")
    val adamant_battleaxe = find("adamant_battleaxe")
    val rune_battleaxe = find("rune_battleaxe")
}
