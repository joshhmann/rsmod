package org.rsmod.api.config.editors

import org.rsmod.api.config.refs.npcs
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.game.type.npc.NpcType

private val fishingSpots = setOf(npcs.rod_fishing_spot_1527, npcs.fishing_spot_1530)
private val imps = setOf(npcs.imp)

// NPCs that can be pickpocketed - need to add Pickpocket as op2
private val pickpocketNpcs =
    setOf(npcs.man, npcs.man2, npcs.man3, npcs.woman, npcs.woman2, npcs.woman3)

internal object NpcEdits : NpcEditor() {
    init {
        fishingSpots.forEach(::fishingSpot)
        imps.forEach(::imp)
        pickpocketNpcs.forEach(::pickpocketNpc)

        edit(npcs.farming_tools_leprechaun) {
            respawnDir = south
            wanderRange = 0
        }
    }
}

private fun NpcEditor.fishingSpot(type: NpcType) {
    // Some fishing spot npc symbols drift between revisions/naming conventions. If a spot is
    // unresolved and maps to the placeholder "blankobject", skip editing to avoid packCache
    // failing on invalid cache edits.
    if (type.internalName == "blankobject") {
        return
    }
    edit(type) {
        moveRestrict = nomove
        wanderRange = 0
    }
}

private fun NpcEditor.imp(type: NpcType) = edit(type) { giveChase = false }

private fun NpcEditor.pickpocketNpc(type: NpcType) = edit(type) { op2 = "Pickpocket" }
