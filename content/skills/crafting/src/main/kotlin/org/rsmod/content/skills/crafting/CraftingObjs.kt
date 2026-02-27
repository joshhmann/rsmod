package org.rsmod.content.skills.crafting

import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Crafting-related object references.
 *
 * All IDs confirmed against rev 233 obj.sym.
 */
internal typealias craft_objs = CraftingObjs

internal object CraftingObjs : ObjReferences() {
    // ---- Tools ----
    val chisel = find("chisel") // ID 1755
    val needle = find("needle") // ID 1733
    val thread = find("thread") // ID 1734

    // ---- Gems: Uncut ----
    val uncut_opal = find("uncut_opal") // ID 1625
    val uncut_jade = find("uncut_jade") // ID 1627
    val uncut_red_topaz = find("uncut_red_topaz") // ID 1629
    val uncut_sapphire = find("uncut_sapphire") // ID 1623
    val uncut_emerald = find("uncut_emerald") // ID 1621
    val uncut_ruby = find("uncut_ruby") // ID 1619
    val uncut_diamond = find("uncut_diamond") // ID 1617
    val uncut_dragonstone = find("uncut_dragonstone") // ID 1631

    // ---- Gems: Cut ----
    val opal = find("opal") // ID 1609
    val jade = find("jade") // ID 1611
    val red_topaz = find("red_topaz") // ID 1613
    val sapphire = find("sapphire") // ID 1607
    val emerald = find("emerald") // ID 1605
    val ruby = find("ruby") // ID 1603
    val diamond = find("diamond") // ID 1601
    val dragonstone = find("dragonstone") // ID 1615

    // ---- Pottery ----
    val clay = find("clay") // ID 434
    val softclay = find("softclay") // ID 1761
    val pot_unfired = find("pot_unfired") // ID 1787
    val bowl_unfired = find("bowl_unfired") // ID 1791
    val pot_empty = find("pot_empty") // ID 1931
    val bowl_empty = find("bowl_empty") // ID 1923

    // ---- Leather ----
    val cow_hide = find("cow_hide") // ID 1739
    val leather = find("leather") // ID 1741
    val hard_leather = find("hard_leather") // ID 1743

    // ---- Leather Products ----
    val leather_gloves = find("leather_gloves") // ID 1059
    val leather_boots = find("leather_boots") // ID 1061
    val leather_vambraces = find("leather_vambraces") // ID 1063
    val leather_chaps = find("leather_chaps") // ID 1095
    val leather_armour = find("leather_armour") // ID 1129 (body)
    val leather_cowl = find("leather_cowl") // ID 1167
    val hardleather_body = find("hardleather_body") // ID 1131

    // ---- Jewelry Moulds ----
    val ring_mould = find("ring_mould") // ID 1592
    val necklace_mould = find("necklace_mould") // ID 1597
    val amulet_mould = find("amulet_mould") // ID 1595

    // ---- Jewelry Materials ----
    val gold_bar = find("gold_bar") // ID 2357
    val silver_bar = find("silver_bar") // ID 2355

    // ---- Gold Jewelry (unstrung/unenchanted) ----
    val gold_ring = find("gold_ring") // ID 1635
    val gold_necklace = find("gold_necklace") // ID 1654
    val unstrung_gold_amulet = find("unstrung_gold_amulet") // ID 1673

    // ---- Stringing Materials ----
    val wool = find("wool") // ID 1737
    val ball_of_wool = find("ball_of_wool") // ID 1759

    // ---- Strung Gold Amulet ----
    val gold_amulet = find("strung_gold_amulet") // ID 1692
}
