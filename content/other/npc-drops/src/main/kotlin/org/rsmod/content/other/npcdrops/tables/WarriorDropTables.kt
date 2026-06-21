package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Warrior Woman.
 *
 * Data sources:
 * - OSRS wiki (existing manually-written table preserved)
 * - Corpus drops_by_source (herb and rune expansion)
 *
 * Al Kharid regional batch (Level 5 operational mode).
 * Warrior Woman — level 24, found in Al Kharid palace.
 * Always: Bones.
 *
 * Al-Kharid Warrior: NOT implemented — no combat registration (thieving only).
 */
internal object WarriorDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerWarriorWoman(registry)
    }

    private fun registerWarriorWoman(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Coins
            table("Coins", weight = 50) {
                item(objs.coins, quantity = 5..20, weight = 30)
                item(objs.coins, quantity = 21..50, weight = 15)
                item(objs.coins, quantity = 51..100, weight = 5)
            }

            // Steel equipment
            table("Steel Equipment", weight = 25) {
                item(WarriorObjs.steel_axe, weight = 8)
                item(WarriorObjs.steel_dagger, weight = 7)
                item(WarriorObjs.steel_mace, weight = 6)
                item(WarriorObjs.steel_scimitar, weight = 4)
            }

            // Runes and talismans
            table("Runes/Talismans", weight = 15) {
                item(objs.mindrune, quantity = 5..10, weight = 8)
                item(WarriorObjs.mind_talisman, weight = 3)
                item(objs.waterrune, quantity = 5..10, weight = 3)
                item(objs.earthrune, quantity = 5..10, weight = 1)
                item(objs.chaosrune, quantity = 6, weight = 3)
                item(objs.bloodrune, quantity = 2, weight = 1)
                item(objs.deathrune, quantity = 2, weight = 2)
            }

            // Herbs (grimy herb table — expanded)
            table("Herbs", weight = 10) {
                item(WarriorObjs.unidentified_guam, weight = 10)
                item(WarriorObjs.unidentified_marentill, weight = 8)
                item(WarriorObjs.unidentified_tarromin, weight = 6)
                item(WarriorObjs.unidentified_harralander, weight = 5)
                item(WarriorObjs.unidentified_ranarr, weight = 4)
                item(WarriorObjs.unidentified_irit, weight = 3)
                item(WarriorObjs.unidentified_avantoe, weight = 2)
                item(WarriorObjs.unidentified_kwuarm, weight = 2)
                item(WarriorObjs.unidentified_cadantine, weight = 1)
                item(WarriorObjs.unidentified_lantadyme, weight = 1)
                item(WarriorObjs.unidentified_dwarf_weed, weight = 1)
            }

            // Gem / Rare Drop Table
            table("Gem/RDT", weight = 5) {
                item(objs.uncut_sapphire, weight = 4)
                item(objs.uncut_emerald, weight = 2)
                item(objs.uncut_ruby, weight = 1)
                item(objs.uncut_diamond, weight = 1)
            }
        }

        registry.register(WarriorNpcs.warrior_woman, table)
    }
}

internal object WarriorNpcs : NpcReferences() {
    val warrior_woman = find("warrior_woman")
}

internal object WarriorObjs : ObjReferences() {
    val steel_axe = find("steel_axe")
    val steel_dagger = find("steel_dagger")
    val steel_mace = find("steel_mace")
    val steel_scimitar = find("steel_scimitar")
    val mind_talisman = find("mind_talisman")
    // Herbs — use cache symbol names (unidentified / clean)
    val unidentified_guam = find("unidentified_guam")
    val unidentified_marentill = find("unidentified_marentill")
    val unidentified_tarromin = find("unidentified_tarromin")
    val unidentified_harralander = find("unidentified_harralander")
    val unidentified_ranarr = find("unidentified_ranarr")
    val unidentified_irit = find("unidentified_irit")
    val unidentified_avantoe = find("unidentified_avantoe")
    val unidentified_kwuarm = find("unidentified_kwuarm")
    val unidentified_cadantine = find("unidentified_cadantine")
    val unidentified_lantadyme = find("unidentified_lantadyme")
    val unidentified_dwarf_weed = find("unidentified_dwarf_weed")
}
