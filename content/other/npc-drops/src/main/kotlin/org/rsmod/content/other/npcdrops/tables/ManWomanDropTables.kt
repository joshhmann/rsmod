package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Man and Woman NPCs.
 *
 * Data sources:
 * - Primary: drops_by_source.json (OSRS wiki corpus, 28 items per NPC after rev 233 filtering)
 * - Secondary: https://oldschool.runescape.wiki/w/Man
 *
 * Man/Woman are found throughout Gielinor (Lumbridge, Varrock, Falador, etc.).
 * Level 2/3 combat, non-aggressive — attackable only when player initiates combat.
 *
 * Conditional drops filtered out (not valid for Lumbridge standard NPCs):
 * - Clue scroll (beginner) — post-2018
 * - Clue scroll (easy) — reserved for DropTableObjs pattern
 * - Grimy marrentill — not in rev 233 cache
 * - Key (medium) — post-2013 RDT expansion
 * - Looting bag — Wilderness-only
 */
internal object ManWomanDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerMen(registry)
        registerWomen(registry)
    }

    private fun registerMen(registry: NpcDropTableRegistry) {
        val table = manWomanDropTable()
        registry.register(DropTableNpcs.man, table)
        registry.register(DropTableNpcs.man2, table)
        registry.register(DropTableNpcs.man3, table)
    }

    private fun registerWomen(registry: NpcDropTableRegistry) {
        val table = manWomanDropTable()
        registry.register(DropTableNpcs.woman, table)
        registry.register(DropTableNpcs.woman2, table)
        registry.register(DropTableNpcs.woman3, table)
    }

    private fun manWomanDropTable() = dropTable {
        // Always drops — guaranteed on every kill
        always(objs.bones)
        always(objs.coins, quantity = 3)

        // Main coins table (weight 75 of 126 = ~60% chance to roll)
        table("Coins", weight = 75) {
            item(objs.coins, quantity = 3, weight = 38)  // ~9.5%: 3 coins
            item(objs.coins, quantity = 10, weight = 23) // ~5.7%: 10 coins
            item(objs.coins, quantity = 5, weight = 9)   // ~2.2%: 5 coins
            item(objs.coins, quantity = 15, weight = 4)  // ~1.0%: 15 coins
            item(objs.coins, quantity = 25, weight = 1)  // ~0.2%: 25 coins
        }

        // Weapon / Armour drops (weight 28 of 126 = ~22% chance to roll)
        table("Weapons/Armour", weight = 28) {
            item(objs.bronze_med_helm, weight = 2)
            item(DropTableObjs.iron_dagger, weight = 1)
            item(DropTableObjs.bronze_bolts, quantity = 2..12, weight = 22)
            item(objs.bronze_arrow, weight = 3)
        }

        // Rune drops (weight 7 of 126 = ~5.5% chance to roll)
        table("Runes", weight = 7) {
            item(objs.earthrune, quantity = 4, weight = 2)
            item(objs.firerune, quantity = 6, weight = 2)
            item(objs.mindrune, quantity = 9, weight = 2)
            item(objs.chaosrune, quantity = 2, weight = 1)
        }

        // Rare misc drops (weight 15 of 126 = ~12% chance to roll)
        table("Rare", weight = 15) {
            nothing(weight = 6)
            item(objs.fishing_bait, weight = 5)
            item(objs.copper_ore, weight = 2)
            item(objs.earth_talisman, weight = 2)
            item(objs.cabbage, weight = 2)
            item(ManWomanObjs.grimy_guam, weight = 1)
            item(ManWomanObjs.grimy_tarromin, weight = 1)
            item(ManWomanObjs.grimy_harralander, weight = 1)
            item(ManWomanObjs.grimy_ranarr, weight = 1)
        }

        // Tertiary herbs table — rare herb drops (weight 1 of 126 = ~0.8% chance to roll)
        table("Herbs", weight = 1) {
            nothing(weight = 900)
            item(ManWomanObjs.grimy_guam, weight = 32)
            item(ManWomanObjs.grimy_tarromin, weight = 24)
            item(ManWomanObjs.grimy_harralander, weight = 18)
            item(ManWomanObjs.grimy_ranarr, weight = 14)
            item(ManWomanObjs.grimy_irit, weight = 11)
            item(ManWomanObjs.grimy_avantoe, weight = 9)
            item(ManWomanObjs.grimy_kwuarm, weight = 7)
            item(ManWomanObjs.grimy_cadantine, weight = 5)
            item(ManWomanObjs.grimy_lantadyme, weight = 4)
            item(ManWomanObjs.grimy_dwarf_weed, weight = 4)
        }
    }
}

/**
 * Object type references for Man/Woman drops not in BaseObjs or DropTableObjs.
 *
 * Grimy (unidentified) herbs are used for monster drops. Clean variants
 * (guam_leaf, tarromin, etc.) are in BaseObjs but the grimy versions
 * are not — they're declared here.
 *
 * Symbol names verified against rev 233 obj.sym.
 */
internal object ManWomanObjs : ObjReferences() {
    val grimy_guam = find("unidentified_guam")
    val grimy_tarromin = find("unidentified_tarromin")
    val grimy_harralander = find("unidentified_harralander")
    val grimy_ranarr = find("unidentified_ranarr")
    val grimy_irit = find("unidentified_irit")
    val grimy_avantoe = find("unidentified_avantoe")
    val grimy_kwuarm = find("unidentified_kwuarm")
    val grimy_cadantine = find("unidentified_cadantine")
    val grimy_lantadyme = find("unidentified_lantadyme")
    val grimy_dwarf_weed = find("unidentified_dwarf_weed")
}
