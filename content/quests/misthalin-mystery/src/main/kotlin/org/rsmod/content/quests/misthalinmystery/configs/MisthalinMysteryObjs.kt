@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.misthalinmystery.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias misthalin_mystery_objs = MisthalinMysteryObjs

internal object MisthalinMysteryObjs : ObjReferences() {
    // Quest keys
    val manor_key = find("mistmyst_frontdoor_key")
    val ruby_key = find("mistmyst_ruby_key")
    val emerald_key = find("mistmyst_emerald_key")
    val sapphire_key = find("mistmyst_sapphire_key")

    // Quest clues (notes)
    val clue_library = find("mistmyst_clue_library")
    val clue_outside = find("mistmyst_clue_outside")
    val clue_kitchen = find("mistmyst_clue_kitchen")

    // Quest items
    val cutscene_knife = find("mistmyst_cutscene_knife")
    val bgs_prop = find("mistmyst_bgs_prop")

    // Common items used during quest
    val bucket_empty = find("bucket_empty")
    val bucket_water = find("bucket_water")
    val knife = find("knife")
    val tinderbox = find("tinderbox")
}
