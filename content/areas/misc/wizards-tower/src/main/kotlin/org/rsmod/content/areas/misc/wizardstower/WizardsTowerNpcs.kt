package org.rsmod.content.areas.misc.wizardstower

import org.rsmod.api.type.refs.npc.NpcReferences

/** NPC type references for Wizards Tower. */
internal object WizardsTowerNpcs : NpcReferences() {
    // Ground floor
    val sedridor = find("head_wizard")
    val wizard = find("wizard")

    // First floor
    val wizard_mizgog = find("wizard_mizgog")
    val traiborn = find("traiborn")

    // Basement
    // val lesser_demon_caged = find("lesser_demon")
}
