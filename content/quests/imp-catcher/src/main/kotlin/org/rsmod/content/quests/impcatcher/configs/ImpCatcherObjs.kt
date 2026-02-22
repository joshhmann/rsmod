@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.impcatcher.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias imp_catcher_objs = ImpCatcherObjs

internal object ImpCatcherObjs : ObjReferences() {
    val black_bead = find("black_bead")
    val red_bead = find("red_bead")
    val white_bead = find("white_bead")
    val yellow_bead = find("yellow_bead")
    val amulet_of_accuracy = find("amulet_of_accuracy")
}
