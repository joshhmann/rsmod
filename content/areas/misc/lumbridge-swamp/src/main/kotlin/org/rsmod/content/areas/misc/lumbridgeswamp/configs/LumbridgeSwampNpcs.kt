@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.misc.lumbridgeswamp.configs

import org.rsmod.api.type.refs.npc.NpcReferences

typealias lumbridge_swamp_npcs = LumbridgeSwampNpcs

object LumbridgeSwampNpcs : NpcReferences() {
    // Fishing spots
    val fishing_spot_net_bait = find("0_50_50_freshfish")

    // Goblins (various levels 2-5)
    val goblin_unarmed_melee_1 = find("goblin_unarmed_melee_1")
    val goblin_unarmed_melee_2 = find("goblin_unarmed_melee_2")
    val goblin_unarmed_melee_3 = find("goblin_unarmed_melee_3")
    val goblin_unarmed_melee_4 = find("goblin_unarmed_melee_4")
    val goblin_unarmed_melee_5 = find("goblin_unarmed_melee_5")

    // Cows
    val cow = find("cow")
    val cow2 = find("cow2")
    val cow3 = find("cow3")

    // Father Urhney (referenced from restless ghost quest)
    val father_urhney = find("father_urhney")
}
