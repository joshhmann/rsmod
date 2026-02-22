@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.skills.runecrafting.scripts.configs

import org.rsmod.api.type.refs.loc.LocReferences

internal typealias runecrafting_locs = RunecraftingLocs

internal object RunecraftingLocs : LocReferences() {
    val runetemple_ruined = find("runetemple_ruined")
    val runetemple = find("runetemple")
    val runetemple_altar_new = find("runetemple_altar_new")
    val runetemple_altar_old = find("runetemple_altar_old")
}
