@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.alkharid.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias al_kharid_npcs = AlKharidNpcs

internal object AlKharidNpcs : NpcReferences() {
    val al_kharid_man = find("al_kharid_man")
    val al_kharid_warrior = find("al_kharid_warrior")
    val banker = find("banker") // Using generic banker
    val kharid_banker_1 = find("kharidbanker1")
    val kharid_banker_2 = find("kharidbanker2")
    val dommik = find("dommik")
    val ellis_tanner = find("ellis_tanner")
    val gem_trader = find("gem_trader")
    val hassan = find("hassan")
    val louie_legs = find("louie_legs")
    val ranael = find("ranael")
    val shantay = find("shantay")
    val shantay_guard = find("shantay_guard")
    val silk_merchant = find("silk_merchant")
    val zeke = find("zeke")
    val scorpion = find("scorpion")
    // Border guards for the toll gate
    val border_guard = find("borderguard1")
    // General store
    val alkharid_shop_keeper = find("generalshopkeeper3")
    val alkharid_shop_assistant = find("generalassistant3")
    // Osman - Emir's spymaster (Prince Ali Rescue quest)
    val osman = find("osman")
}

internal object AlKharidNpcEditor : NpcEditor() {
    init {
        edit(al_kharid_npcs.banker) { contentGroup = content.banker }
        edit(al_kharid_npcs.kharid_banker_1) { contentGroup = content.banker }
        edit(al_kharid_npcs.kharid_banker_2) { contentGroup = content.banker }

        edit(al_kharid_npcs.alkharid_shop_keeper) { contentGroup = content.shop_keeper }

        edit(al_kharid_npcs.alkharid_shop_assistant) { contentGroup = content.shop_assistant }
    }
}
