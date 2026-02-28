package org.rsmod.content.areas.island.karamja

import org.rsmod.api.type.refs.npc.NpcReferences

/** NPC type references for Karamja F2P area. */
internal object KaramjaNpcs : NpcReferences() {
    // Banana Plantation
    val luthas = find("luthas")

    // General Store
    val karamja_general_store = find("generalshopkeeper4")

    // Fishing Spots
    val fishing_spot_cage_harpoon = find("0_50_49_saltfish")
    val fishing_spot_net_bait = find("0_50_50_freshfish")

    // Wildlife
    val monkey = find("monkey")
    val jungle_spider = find("jungle_spider")
    val skeleton = find("skeleton_unarmed")
    val scorpion = find("scorpion")

    // Pirates (F2P)
    val pirate = find("pirate1")

    // Port Staff
    val customs_officer = find("customs_officer")
    val karamja_dock_worker = find("karamja_man")
    val seaman_lorris = find("seaman_lorris")
    val seaman_thresnor = find("seaman_thresnor")
}
