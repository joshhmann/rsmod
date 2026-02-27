@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.lumbridge.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.map.CoordGrid

typealias lumbridge_npcs = LumbridgeNpcs

object LumbridgeNpcs : NpcReferences() {
    val barfy_bill = find("canoeing_bill")
    val banker = find("deadman_banker_blue_south")
    val banker_tutor = find("aide_tutor_banker")
    val lumbridge_shop_keeper = find("generalshopkeeper1")
    val lumbridge_shop_assistant = find("generalassistant1")
    val gee = find("lumbridge_guide2_man")
    val donie = find("lumbridge_guide2_woman")
    val hans = find("hans")
    val bartender = find("ram_bartender")
    val arthur_the_clue_hunter = find("aide_tutor_clues")
    val prayer_tutor = find("aide_tutor_prayer")
    val hatius_lumbridge_diary = find("hatius_lumbridge_diary")
    val bob = find("bob")
    val woodsman_tutor = find("aide_tutor_woodsman")
    val smithing_apprentice = find("aide_tutor_smithing_apprentice")
    val father_aereck = find("father_aereck")
    val lost_tribe_sigmund = find("lost_tribe_sigmund")
    val cook = find("cook")
    val perdu = find("lost_property_merchant_standard")
    val guide = find("lumbridge_guide")
    val doomsayer = find("cws_doomsayer")
    val abigaila = find("tob_spectator_misthalin")
    val count_check = find("count_check")
    val veos = find("veos_lumbridge")
    val adventurer_jon = find("ap_guide_parent")
    val hewey = find("mistmyst_hewey")
    val fishing_tutor = find("aide_tutor_fishing")
    val melee_tutor = find("aide_tutor_melee")
    val ranged_tutor = find("aide_tutor_ranging")
    val magic_tutor = find("aide_tutor_magic")
    val cooking_tutor = find("aide_tutor_cooking")
    val crafting_tutor = find("aide_tutor_crafting")
    val mining_tutor = find("aide_tutor_mining")
    val millie = find("millie_the_miller")
    val duke_of_lumbridge = find("duke_of_lumbridge")
    val gillie_groats = find("gillie_the_milkmaid")
    val fred_the_farmer = find("fred_the_farmer")
    val sir_vant = find("white_knight")
}

internal object LumbridgeNpcEditor : NpcEditor() {
    init {
        edit(lumbridge_npcs.sir_vant) {
            name = "Sir Vant"
            wanderRange = 0
        }

        edit(lumbridge_npcs.lumbridge_shop_keeper) {
            contentGroup = content.shop_keeper
            moveRestrict = indoors
        }

        edit(lumbridge_npcs.lumbridge_shop_assistant) {
            contentGroup = content.shop_assistant
            moveRestrict = indoors
        }

        edit(lumbridge_npcs.banker) { contentGroup = content.banker }

        edit(lumbridge_npcs.banker_tutor) { contentGroup = content.banker_tutor }

        edit(lumbridge_npcs.prayer_tutor) { moveRestrict = indoors }

        edit(lumbridge_npcs.father_aereck) { moveRestrict = indoors }

        edit(lumbridge_npcs.hans) {
            defaultMode = patrol
            patrol1 = patrol(CoordGrid(0, 50, 50, 7, 33), 0)
            patrol2 = patrol(CoordGrid(0, 50, 50, 11, 30), 0)
            patrol3 = patrol(CoordGrid(0, 50, 50, 19, 30), 0)
            patrol4 = patrol(CoordGrid(0, 50, 50, 19, 22), 10)
            patrol5 = patrol(CoordGrid(0, 50, 50, 21, 22), 0)
            patrol6 = patrol(CoordGrid(0, 50, 50, 21, 12), 0)
            patrol7 = patrol(CoordGrid(0, 50, 50, 18, 9), 0)
            patrol8 = patrol(CoordGrid(0, 50, 50, 14, 5), 0)
            patrol9 = patrol(CoordGrid(0, 50, 50, 2, 5), 0)
            patrol10 = patrol(CoordGrid(0, 50, 50, 2, 32), 0)
            maxRange = 40
        }

        edit(lumbridge_npcs.cook) { moveRestrict = indoors }

        edit(lumbridge_npcs.perdu) {
            respawnDir = west
            wanderRange = 0
        }

        edit(lumbridge_npcs.guide) {
            respawnDir = west
            wanderRange = 0
        }

        edit(lumbridge_npcs.doomsayer) {
            respawnDir = east
            wanderRange = 0
        }

        edit(lumbridge_npcs.abigaila) {
            respawnDir = south
            wanderRange = 0
        }

        edit(lumbridge_npcs.count_check) {
            respawnDir = east
            wanderRange = 0
        }

        edit(lumbridge_npcs.arthur_the_clue_hunter) {
            respawnDir = north
            wanderRange = 0
            timer = 20
        }

        edit(lumbridge_npcs.bartender) {
            respawnDir = west
            wanderRange = 0
        }

        edit(lumbridge_npcs.smithing_apprentice) { moveRestrict = indoors }

        edit(lumbridge_npcs.lost_tribe_sigmund) {
            name = "Lumbridge Advisor"
            respawnDir = south
            wanderRange = 0
        }

        edit(lumbridge_npcs.veos) {
            respawnDir = south
            wanderRange = 0
        }

        edit(lumbridge_npcs.adventurer_jon) {
            respawnDir = south
            wanderRange = 0
        }

        edit(lumbridge_npcs.hewey) {
            respawnDir = east
            wanderRange = 0
        }

        edit(lumbridge_npcs.fishing_tutor) {
            respawnDir = east
            wanderRange = 0
        }

        edit(lumbridge_npcs.melee_tutor) { wanderRange = 0 }
        edit(lumbridge_npcs.ranged_tutor) { wanderRange = 0 }
        edit(lumbridge_npcs.magic_tutor) { wanderRange = 0 }
        edit(lumbridge_npcs.cooking_tutor) { wanderRange = 0 }
        edit(lumbridge_npcs.crafting_tutor) { wanderRange = 0 }
        edit(lumbridge_npcs.mining_tutor) { wanderRange = 0 }

        edit(lumbridge_npcs.bob) { moveRestrict = indoors }

        edit(lumbridge_npcs.millie) { wanderRange = 1 }

        edit(lumbridge_npcs.duke_of_lumbridge) { moveRestrict = indoors }

        edit(lumbridge_npcs.gillie_groats) { wanderRange = 1 }
        edit(lumbridge_npcs.fred_the_farmer) { moveRestrict = indoors }
    }
}
