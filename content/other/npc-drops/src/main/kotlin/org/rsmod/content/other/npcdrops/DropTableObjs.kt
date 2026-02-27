package org.rsmod.content.other.npcdrops

import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Item type references used within the drop-table content module. Items already defined in
 * [org.rsmod.api.config.refs.BaseObjs] are referenced from there. Only items not yet present in
 * BaseObjs are declared here.
 */
internal object DropTableObjs : ObjReferences() {
    // Food / creature products
    val raw_chicken = find("raw_chicken")
    val raw_rat_meat = find("raw_rat_meat")
    val raw_beef = find("raw_beef")
    val cowhide = find("cowhide")
    val feather = find("feather")
    val cooked_meat = find("cooked_meat")
    val beer = find("beer")

    // Body parts / prayer
    val big_bones = find("big_bones")

    // F2P Moss Giant drops
    val black_sq_shield = find("black_sq_shield")
    val magic_staff = find("magic_staff")
    val mithril_sword = find("mithril_sword")
    val steel_kiteshield = find("steel_kiteshield")
    val steel_bar = find("steel_bar")
    val coal = find("coal")
    val spinach_roll = find("spinach_roll")

    // Bronze weapons & armour
    val bronze_sword = find("bronze_sword")
    val bronze_scimitar = find("bronze_scimitar")
    val bronze_spear = find("bronze_spear")
    val bronze_javelin = find("bronze_javelin")
    val bronze_bolts = find("bronze_bolts")
    val bronze_sq_shield = find("bronze_sq_shield")
    val bronze_kiteshield = find("bronze_kiteshield")
    val bronze_med_helm = find("bronze_med_helm")

    // Iron weapons & armour
    val iron_dagger = find("iron_dagger")
    val iron_ore = find("iron_ore")

    // Steel weapons & armour (guard loot)
    val steel_sword = find("steel_sword") // TODO: wiki-validate drop rates
    val steel_mace = find("steel_mace") // TODO: wiki-validate drop rates
    val steel_med_helm = find("steel_med_helm")

    // Runes — body rune not in BaseObjs
    val body_rune = find("bodyrune")
    val cosmic_rune = find("cosmic_rune")

    // Clue scrolls (basic drop tables use beginner and easy clues)
    val trail_clue_beginner = find("trail_clue_beginner")
    val trail_clue_easy_simple001 = find("trail_clue_easy_simple001")

    // Boss drops - Kalphite Queen
    val wine_of_zamorak = find("wine_of_zamorak")

    // Food items not in BaseObjs
    val shark = find("shark")

    // Oyster pearls (KQ drop)
    val bigoysterpearls = find("bigoysterpearls")

    // Dragon equipment not in BaseObjs
    val dragon_chainbody = find("dragon_chainbody")

    // Hill Giant drops
    val iron_full_helm = find("iron_full_helm")
    val steel_longsword = find("steel_longsword")
    val limpwurt_root = find("limpwurt_root")
    val body_talisman = find("body_talisman")
    val chaos_talisman = find("chaos_talisman")
    val nature_talisman = find("nature_talisman")
    val rune_javelin = find("rune_javelin")
    val loop_half_key = find("loop_half_key")
    val tooth_half_key = find("tooth_half_key")
    val giant_key = find("giant_key")
    val clue_scroll_beginner = find("clue_scroll_beginner")

    // Mugger drops
    val rope = find("rope")
    val fishing_bait = find("fishing_bait")
    val copper_ore = find("copper_ore")
    val knife = find("knife")
    val cabbage = find("cabbage")
    val mind_rune = find("mindrune")
    val water_rune = find("waterrune")
    val earth_rune = find("earthrune")
}
