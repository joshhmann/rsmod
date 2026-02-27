@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.demonslayer.configs

import org.rsmod.api.type.refs.obj.ObjReferences

typealias demon_slayer_objs = DemonSlayerObjs

object DemonSlayerObjs : ObjReferences() {
    val silverlight = find("silverlight") // The demon-slaying sword
    val silverlight_key_1 = find("silverlight_key_1") // Key from Captain Rovin
    val silverlight_key_2 = find("silverlight_key_2") // Key from Wizard Traiborn
    val silverlight_key_3 = find("silverlight_key_3") // Key from Sir Prysin's location
    val bones = find("bones") // Regular bones for Wizard Traiborn
    val coins = find("coins") // For buying information
}
