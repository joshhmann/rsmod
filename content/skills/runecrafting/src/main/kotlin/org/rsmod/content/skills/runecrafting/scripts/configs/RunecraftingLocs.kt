@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.skills.runecrafting.scripts.configs

import org.rsmod.api.type.refs.loc.LocReferences

internal typealias runecrafting_locs = RunecraftingLocs

internal object RunecraftingLocs : LocReferences() {
    val runetemple_ruined = find("runetemple_ruined")
    val runetemple = find("runetemple")
    val runetemple_altar_new = find("runetemple_altar_new")
    val runetemple_altar_old = find("runetemple_altar_old")

    // Essence mine portals (exit portals back to surface)
    val essencemine_portal_1 = find("essencemine_portal_1")
    val essencemine_portal_2 = find("essencemine_portal_2")
    val essencemine_portal_3 = find("essencemine_portal_3")
    val essencemine_portal_4 = find("essencemine_portal_4")
    val essencemine_portal_5 = find("essencemine_portal_5")
}
