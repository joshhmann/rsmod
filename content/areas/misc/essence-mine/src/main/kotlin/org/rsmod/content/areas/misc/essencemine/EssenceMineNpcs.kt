package org.rsmod.content.areas.misc.essencemine

import org.rsmod.api.type.refs.npc.NpcReferences

/**
 * NPC type references for Rune Essence Mine.
 *
 * The essence mine is an instanced area accessed via:
 * - Aubury in Varrock (south of east bank)
 * - Sedridor in Wizards' Tower
 * - Carwen Essencebinder in Burthorpe (members)
 */
internal object EssenceMineNpcs : NpcReferences() {
    // NPCs that teleport players to the mine
    val aubury = find("aubury")
    // val sedridor = find("sedridor")  // TODO: Add when Wizards' Tower is implemented
    // val carwen_essencebinder = find("carwen_essencebinder")  // Members
}
