package org.rsmod.content.travel.canoe.configs

import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.game.type.npc.NpcType

typealias canoe_npcs = CanoeNpcs

object CanoeNpcs : NpcReferences() {
    val canoeing_cave_scenery_1 = find("canoeing_cave_scenery_1")
    val canoeing_cave_scenery_2 = find("canoeing_cave_scenery_2")
    val canoeing_cave_scenery_3 = find("canoeing_cave_scenery_3")

    val canoeing_scenery_1 = find("canoeing_scenery_1")
    val canoeing_scenery_2 = find("canoeing_scenery_2")
    val canoeing_bullrush = find("canoeing_bullrush")
    val canoeing_bullrush_leaf = find("canoeing_bullrush_leaf")
}

internal object CanoeNpcEditor : NpcEditor() {
    init {
        scenery(canoe_npcs.canoeing_cave_scenery_1)
        scenery(canoe_npcs.canoeing_cave_scenery_2)
        scenery(canoe_npcs.canoeing_cave_scenery_3)

        scenery(canoe_npcs.canoeing_scenery_1)
        scenery(canoe_npcs.canoeing_scenery_2)
        scenery(canoe_npcs.canoeing_bullrush)
        scenery(canoe_npcs.canoeing_bullrush_leaf)
    }

    private fun scenery(npc: NpcType) {
        edit(npc) {
            defaultMode = none
            moveRestrict = passthru
            respawnDir = north
            timer = 1
        }
    }
}
