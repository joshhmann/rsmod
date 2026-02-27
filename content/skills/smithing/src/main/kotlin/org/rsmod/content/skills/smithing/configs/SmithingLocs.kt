package org.rsmod.content.skills.smithing.configs

import org.rsmod.api.type.refs.loc.LocReferences

/**
 * Smithing-related location references for furnaces and anvils.
 *
 * Loc IDs verified from loctypes.txt (rev 233):
 * - furnace: 2030
 * - furnace2: 2099
 * - anvil: 2097
 * - dorics_anvil: 2031
 */
object SmithingLocs : LocReferences() {
    // Standard furnace found in most cities
    val furnace = find("furnace")

    // Alternative furnace model (e.g., Lumbridge, Edgeville)
    val furnace2 = find("furnace2")

    // Standard anvil found in most cities
    val anvil = find("anvil")

    // Doric's anvil (for Doric's Quest)
    val dorics_anvil = find("dorics_anvil")
}
