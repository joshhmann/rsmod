@file:Suppress("unused", "SpellCheckingInspection")
package org.rsmod.content.areas.city.karamja.configs
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
typealias karamja_npcs = KaramjaNpcs
object KaramjaNpcs : NpcReferences() {
    val luthas = find("luthas")
    val customs_officer = find("customs_officer")
    val karamja_man = find("karamja_man")
    val man_musa_point = find("man4_for_musa_point")
    val gub_musa_child_1 = find("gub_musa_child_1")
    val gub_musa_child_2 = find("gub_musa_child_2")
    val gub_musa_child_3 = find("gub_musa_child_3")
    val captain_barnaby_karamja = find("captain_barnaby_karamja")
}
internal object KaramjaNpcEditor : NpcEditor() {
    init {
        edit(karamja_npcs.luthas) { wanderRange = 2 }
        edit(karamja_npcs.customs_officer) { wanderRange = 1 }
        edit(karamja_npcs.karamja_man) { wanderRange = 3 }
        edit(karamja_npcs.man_musa_point) { wanderRange = 3 }
        edit(karamja_npcs.gub_musa_child_1) { wanderRange = 4 }
        edit(karamja_npcs.gub_musa_child_2) { wanderRange = 4 }
        edit(karamja_npcs.gub_musa_child_3) { wanderRange = 4 }
    }
}
