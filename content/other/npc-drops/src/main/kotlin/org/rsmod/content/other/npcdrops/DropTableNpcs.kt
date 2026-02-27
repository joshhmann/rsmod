package org.rsmod.content.other.npcdrops

import org.rsmod.api.type.refs.npc.NpcReferences

/**
 * NPC type references used only within the drop-table content module. NPCs that already exist in
 * [org.rsmod.api.config.refs.BaseNpcs] are imported directly from there; only truly absent NPC
 * types are declared here.
 */
internal object DropTableNpcs : NpcReferences() {
    // Goblin variants present in the OSRS cache at rev 228
    val goblin = find("goblin")
    val goblin_2 = find("goblin_2")
    val goblin_3 = find("goblin_3")
    val goblin_chef = find("goblin_chef")
    val goblin_guard = find("goblin_guard")

    // Chicken
    val chicken = find("chicken")
    val chicken_2 = find("chicken_2") // TODO: verify internal name for rev-228 variant

    // Giant rat variants
    val giant_rat = find("giant_rat")
    val giant_rat_2 = find("giant_rat_2")
    val giant_rat_3 = find("giant_rat_3")

    // Cow — variant names cross-referenced with CowNpcs in generic-npcs.
    // cow_beef is the "ready to milk" variant; cow2/cow3 are graphical variants.
    val cow = find("cow")
    val cow2 = find("cow2")
    val cow3 = find("cow3")
    val cow_beef = find("cow_beef")

    // Guard variants (Lumbridge / Al-Kharid)
    val guard = find("guard")
    val guard_2 = find("guard_2")
    val guard_3 = find("guard_3")

    // Scorpion
    val scorpion = find("scorpion")

    // Imp
    val imp = find("imp")

    // Dark Wizard variants
    // Note: "dark_wizard" symbol does not exist in rev 228 cache, only variants
    val bearded_dark_wizard = find("bearded_dark_wizard")
    val young_dark_wizard = find("young_dark_wizard")

    // Wizard variants
    val wizard = find("wizard")
    val air_wizard = find("air_wizard")
    val water_wizard = find("water_wizard")
    val earth_wizard = find("earth_wizard")
    val fire_wizard = find("fire_wizard")

    // Lesser Demon
    val lesser_demon = find("lesser_demon")

    // Black Knight variants
    val black_knight = find("black_knight")
    val aggressive_black_knight = find("aggressive_black_knight")

    // Additional chicken variants
    val chicken_brown = find("chicken_brown")
    val rooster = find("rooster")

    // Hill Giant variants
    val hill_giant = find("hill_giant")
    val hill_giant2 = find("hill_giant2")
    val hill_giant3 = find("hill_giant3")
    val wilderness_hill_giant = find("wilderness_hill_giant")
    val wilderness_hill_giant2 = find("wilderness_hill_giant2")
    val wilderness_hill_giant3 = find("wilderness_hill_giant3")

    // Skeleton variants
    val skeleton_unarmed = find("skeleton_unarmed")
    val skeleton_unarmed2 = find("skeleton_unarmed2")
    val skeleton_unarmed3 = find("skeleton_unarmed3")
    val skeleton_unarmed4 = find("skeleton_unarmed4")
    val skeleton_armed = find("skeleton_armed")
    val skeleton_armed2 = find("skeleton_armed2")
    val skeleton_armed3 = find("skeleton_armed3")
    val skeleton_armed4 = find("skeleton_armed4")
    val skeleton_armed5 = find("skeleton_armed5")

    // Zombie variants (F2P - found in Edgeville Dungeon, Draynor Sewers, Varrock Sewers)
    val zombie_unarmed = find("zombie_unarmed")
    val zombie2 = find("zombie2")
    val zombie2_b = find("zombie2_b")
    val zombie2_c = find("zombie2_c")
    val zombie2_rural1 = find("zombie2_rural1")

    // Spider variants
    val spider = find("spider")
    val giantspider1 = find("giantspider1")
    val giantspider2 = find("giantspider2")

    // Moss Giant variants (F2P - found in Varrock Sewers, Wilderness, Crandor)
    val mossgiant = find("mossgiant")
    val mossgiant2 = find("mossgiant2")
    val mossgiant3 = find("mossgiant3")
    val mossgiant4 = find("mossgiant4")

    // Outlaw variants (surok_outlaw1-10) - level 32 NPCs in Outlaw Camp
    val surok_outlaw1 = find("surok_outlaw1")
    val surok_outlaw2 = find("surok_outlaw2")
    val surok_outlaw3 = find("surok_outlaw3")
    val surok_outlaw4 = find("surok_outlaw4")
    val surok_outlaw5 = find("surok_outlaw5")
    val surok_outlaw6 = find("surok_outlaw6")
    val surok_outlaw7 = find("surok_outlaw7")
    val surok_outlaw8 = find("surok_outlaw8")
    val surok_outlaw9 = find("surok_outlaw9")
    val surok_outlaw10 = find("surok_outlaw10")

    // Bear variants - brownbear (level 21 Grizzly) and darkbear (level 19 Black bear)
    val brownbear = find("brownbear")
    val darkbear = find("darkbear")
    val brownbear_cub_1 = find("brownbear_cub_1")
    val brownbear_cub_2 = find("brownbear_cub_2")

    // Thief variants - level 16 NPCs found in Varrock, Port Sarim, etc.
    val thief1 = find("thief1")
    val thief2 = find("thief2")

    // Rogue variants - level 15 NPCs found in Wilderness (Rogues' Castle)
    val rogue = find("rogue")
    val wilderness_rogue = find("wilderness_rogue")

    // Chaos Druid variants - level 13 NPCs found in Edgeville Dungeon, Taverley Dungeon, etc.
    val chaos_druid = find("chaos_druid")
    val wilderness_chaos_druid = find("wilderness_chaos_druid")

    // Mugger - level 6 NPC found in Lumbridge, Varrock, Wilderness
    val mugger = find("mugger")

    // Unicorn variants - level 15 (unicorn) and level 27 (black_unicorn)
    // Found in Barbarian Village pen and Wilderness
    val unicorn = find("unicorn")
    val black_unicorn = find("black_unicorn")

    // Barbarian variants - levels 9, 10, 15, 17
    // Found in Barbarian Village (using fai_barbarian variants for different levels)
    val barbarian = find("barbarian")
    val barbarian_2 = find("fai_barbarian_1") // Level 10 variant
    val barbarian_3 = find("fai_barbarian_2") // Level 10 variant
    val barbarian_4 = find("fai_barbarian_3") // Level 10 variant
    val barbarian_5 = find("fai_barbarian_4") // Level 17 variant

    // Dwarf variants - levels 10-11 (normal), 10 (chaos), 14 (mountain)
    // Found in Dwarven Mine and Ice Mountain, non-aggressive
    val dwarf_normal = find("dwarf_normal")
    val dwarf_chaos = find("dwarf_chaos")
    val dwarf_mountain = find("dwarf_mountain")

    // Jail Guard variants - level 26 NPCs found in Draynor and Port Sarim jails
    val jail_guard_1 = find("jail_guard_1")
    val jail_guard_2 = find("jail_guard_2")
    val jail_guard_3 = find("jail_guard_3")
    val jail_guard_4 = find("jail_guard_4")
    val jail_guard_5 = find("jail_guard_5")

    // Man and Woman variants - levels 2-3, found in cities
    val man = find("man")
    val man2 = find("man2")
    val man3 = find("man3")
    val woman = find("woman")
    val woman2 = find("woman2")
    val woman3 = find("woman3")

    // King Black Dragon (Boss) - Combat Level 276
    // Found in Wilderness (level 40+), accessed via lever in Edgeville
    val black_dragon = find("black_dragon")

    // Kalphite Queen (Boss) - Combat Level 333
    // Found in Kalphite Lair (west of Shantay Pass)
    val kalphite_queen = find("kalphite_queen")
}
