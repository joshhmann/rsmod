package org.rsmod.content.quests.dragonslayer.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias dragon_slayer_objs = DragonSlayerObjs

internal object DragonSlayerObjs : ObjReferences() {
    // Map pieces
    val mappart1 = find("mappart1") // From Melzar's Maze
    val mappart2 = find("mappart2") // From Thalzar's notes
    val mappart3 = find("mappart3") // From Lozar's chest
    val dragonmap = find("dragonmap") // Combined map

    // Key and shield
    val melzarkey = find("melzarkey") // Key to Melzar's Maze
    val antidragonbreathshield =
        find("antidragonbreathshield") // Anti-dragon shield from Duke Horacio
}
