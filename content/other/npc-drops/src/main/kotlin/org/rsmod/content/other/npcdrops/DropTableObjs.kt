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

    // Body parts / prayer
    val big_bones = find("big_bones") // kept for future use

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
}
