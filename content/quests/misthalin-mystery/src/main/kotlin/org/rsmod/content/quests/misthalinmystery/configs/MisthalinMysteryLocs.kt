@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.misthalinmystery.configs

import org.rsmod.api.type.refs.loc.LocReferences

internal typealias misthalin_mystery_locs = MisthalinMysteryLocs

internal object MisthalinMysteryLocs : LocReferences() {
    // Boats
    val boat_lumbridge = find("mistmyst_boat_lumbridge")
    val boat_island = find("mistmyst_boat_island")

    // Manor doors
    val front_door_left = find("mistmyst_front_doorl")
    val front_door_right = find("mistmyst_front_doorr")
    val door_redtopaz = find("mistmyst_door_redtopaz")
    val door_ruby = find("mistmyst_door_ruby")
    val door_ruby_inactive = find("mistmyst_door_ruby_inactive")
    val door_emerald = find("mistmyst_door_emerald")
    val door_emerald_inactive = find("mistmyst_door_emerald_inactive")
    val door_sapphire = find("mistmyst_door_sapphire")
    val door_sapphire_inactive = find("mistmyst_door_sapphire_inactive")
    val kitchen_door_top = find("mistmyst_kitchen_door_top")

    // Rain barrel
    val barrel = find("mistmyst_barrel")
    val barrel_emptied = find("mistmyst_barrel_emptied")
    val barrel_water = find("mistmyst_barrel_water")

    // Painting
    val painting = find("mistmyst_painting")
    val painting_fixed = find("mistmyst_painting_fixed")
    val painting_slashed = find("mistmyst_painting_slashed")

    // Explosive barrel
    val explosive_barrel = find("mistmyst_explosive_barrel")
    val explosive_barrel_vis = find("mistmyst_explosive_barrel_vis")
    val explosive_barrel_remains = find("mistmyst_explosive_barrel_remains")

    // Candles
    val candle_unlit = find("mistmyst_candle_unlit")
    val candle_lit = find("mistmyst_candle_lit")

    // Wall
    val destructable_wall = find("mistmyst_destructable_wall")
    val destructable_wall_broken = find("mistmyst_destructable_wall_broken")
    val destructable_wall_damaged = find("mistmyst_destructable_wall_damaged")
    val wall_climbable = find("mistmyst_destructable_wall_climbable")
    val wall_climbable_broken = find("mistmyst_destructable_wall_climbable_broken")

    // Piano
    val piano_closed = find("mistmyst_piano_closed")
    val piano_open = find("mistmyst_piano_open")

    // Fireplace
    val fireplace_unlit = find("mistmyst_fireplace_unlit")
    val fireplace_revealed = find("mistmyst_fireplace_revealed")
    val fireplace_lit = find("mistmyst_fireplace_lit")

    // Switches
    val button = find("mistmyst_button")

    // Boss wardrobes
    val boss_wardrobe = find("mistmyst_boss_wardrobe")
    val boss_wardrobe_open = find("mistmyst_boss_wardrobe_open")

    // Mirror
    val mirror_blocker = find("mistmyst_mirror_blocker")
    val mirror_unblocker = find("mistmyst_mirror_unblocker")

    // Interaction locs
    val table_knife = find("mistmyst_table_knife")
    val shelves_tinderbox = find("mistmyst_shelves_tinderbox")
    val empty_bucket_loc = find("mistmyst_empty_bucket")
    val clue_library_loc = find("mistmyst_clue_library")
    val clue_outside_loc = find("mistmyst_clue_outside")
    val clue_kitchen_loc = find("mistmyst_clue_kitchen")
    val stairs_up = find("mistmyst_stairs_up")
}
