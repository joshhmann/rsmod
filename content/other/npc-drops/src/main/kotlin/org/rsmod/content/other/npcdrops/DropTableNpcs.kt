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

    // Lesser Demon
    val lesser_demon = find("lesser_demon")

    // Black Knight variants
    val black_knight = find("black_knight")
    val aggressive_black_knight = find("aggressive_black_knight")

    // Additional chicken variants
    val chicken_brown = find("chicken_brown")
    val rooster = find("rooster")
}
