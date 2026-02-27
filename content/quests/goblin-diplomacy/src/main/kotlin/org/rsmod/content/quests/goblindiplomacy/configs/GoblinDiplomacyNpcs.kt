@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.goblindiplomacy.configs

import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias goblin_diplomacy_npcs = GoblinDiplomacyNpcs

object GoblinDiplomacyNpcs : NpcReferences() {
    val general_bentnoze = find("general_bentnoze")
    val general_wartface = find("general_wartface")
}

internal object GoblinDiplomacyNpcEditor : NpcEditor() {
    init {
        edit(goblin_diplomacy_npcs.general_bentnoze) { wanderRange = 2 }
        edit(goblin_diplomacy_npcs.general_wartface) { wanderRange = 2 }
    }
}
