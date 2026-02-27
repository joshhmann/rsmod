@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.ernestchicken.configs

import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias ernest_chicken_npcs = ErnestChickenNpcs

object ErnestChickenNpcs : NpcReferences() {
    val veronica = find("veronica")
    val professor_oddenstein = find("professor_oddenstein")
    val ernest = find("ernest")
    val ernest_chicken = find("ernest_the_chicken")
}

internal object ErnestChickenNpcEditor : NpcEditor() {
    init {
        edit(ernest_chicken_npcs.veronica) { wanderRange = 2 }
        edit(ernest_chicken_npcs.professor_oddenstein) { wanderRange = 1 }
        edit(ernest_chicken_npcs.ernest) { wanderRange = 1 }
        edit(ernest_chicken_npcs.ernest_chicken) { wanderRange = 1 }
    }
}
