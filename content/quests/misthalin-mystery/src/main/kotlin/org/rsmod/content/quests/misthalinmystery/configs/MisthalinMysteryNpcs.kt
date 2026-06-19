@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.misthalinmystery.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias misthalin_mystery_npcs = MisthalinMysteryNpcs

internal object MisthalinMysteryNpcs : NpcReferences() {
    val abigale = find("mistmyst_abigale")
    val abigale_killer = find("mistmyst_abigale_killer")
    val abigale_killer_unmasked = find("mistmyst_abigale_killer_unmasked")
    val abigale_cutscene_multi = find("mistmyst_abigale_cutscene_multi")
    val abigale_killer_attackable = find("mistmyst_abigale_killer_attackable")
    val hewey = find("mistmyst_hewey")
    val hewey_killer = find("mistmyst_hewey_killer")
    val hewey_killer_unmasked = find("mistmyst_hewey_killer_unmasked")
    val sid = find("mistmyst_sid")
    val tayten = find("mistmyst_tayten")
    val lacey = find("mistmyst_lacey")
    val mandy = find("mistmyst_mandy")
    val mandy_post = find("mistmyst_mandy_post")
    val killer_background = find("mistmyst_killer_background")
    val mirror_npc = find("mistmyst_mirror")
    val mirror_fixed = find("mistmyst_mirror_fixed")
    val mirror_movable = find("mistmyst_mirror_movable")
}
