@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.skills.farming.scripts.configs

import org.rsmod.api.type.refs.loc.LocReferences

internal typealias farming_locs = FarmingLocs

internal object FarmingLocs : LocReferences() {
    val herb_patch_weeds_1 = find("herb_patch_weeds_1")
    val herb_patch_weeds_2 = find("herb_patch_weeds_2")
    val herb_patch_weeds_3 = find("herb_patch_weeds_3")
    val herb_patch_weeded = find("herb_patch_weeded")

    val herb_1_diseased = find("herb_1_diseased")
    val herb_1_dead = find("herb_1_dead")

    val herb_guam_leaf_seed = find("herb_guam_leaf_seed")
    val herb_guam_leaf_1 = find("herb_guam_leaf_1")
    val herb_guam_leaf_2 = find("herb_guam_leaf_2")
    val herb_guam_leaf_3 = find("herb_guam_leaf_3")
    val herb_guam_leaf_fullygrown = find("herb_guam_leaf_fullygrown")

    val herb_marrentill_seed = find("herb_marrentill_seed")
    val herb_marrentill_1 = find("herb_marrentill_1")
    val herb_marrentill_2 = find("herb_marrentill_2")
    val herb_marrentill_3 = find("herb_marrentill_3")
    val herb_marrentill_fullygrown = find("herb_marrentill_fullygrown")
}
