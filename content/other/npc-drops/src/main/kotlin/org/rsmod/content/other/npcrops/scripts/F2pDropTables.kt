package org.rsmod.content.other.npcrops.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.npcs
import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.content.other.npcrops.dtx.Drop
import org.rsmod.content.other.npcrops.dtx.RollContext
import org.rsmod.content.other.npcrops.dtx.rsGuaranteedTable
import org.rsmod.content.other.npcrops.dtx.rsWeightedTable
import org.rsmod.content.other.npcrops.dtx.rsTertiaryTable
import org.rsmod.content.other.npcrops.dtx.registerDtx
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Drop table registrations for core F2P monsters, using the DTX library DSL.
 *
 * Table structure per NPC:
 *  - `guaranteed` — always-drop items (bones, hides, etc.)
 *  - `mainTable`  — one weighted-random roll selecting the loot item
 *  - `tertiaries` — rare independent-chance drops (clue scrolls, etc.)
 *
 * DSL quick-reference:
 *   `add(Drop(objs.bones))`               — guaranteed drop
 *   `10 weight Drop(objs.coins, 25)`      — weighted item drop, qty fixed
 *   `5 weight Drop(objs.feather, 5..15)`  — weighted item drop, qty range
 *   `1 weight Drop(objs.nothing)`         — nothing slot (reduces loot rate)
 *   `1 outOf 128 chance Drop(...)`        — tertiary 1-in-128 chance
 */
class F2pDropTables @Inject constructor(private val registry: NpcDropTableRegistry) : PluginScript() {

    override fun ScriptContext.startup() {
        registerGoblin()
        registerCow()
        registerChicken()
        registerGiantRat()
        registerGuard()
        registerMan()
        registerWoman()
        registerHillGiant()
        registerMossGiant()
        registerLesserDemon()
        registerGreaterDemon()
        registerBlackKnight()
        registerDarkWizard()
        registerSkeleton()
        registerZombie()
    }

    // ─────────────────────────────────────────────────────────────────────────

    private fun registerGoblin() = registry.registerDtx(
        npcType = npcs.goblin,
        label = "Goblin",
        guaranteed = rsGuaranteedTable { add(Drop(objs.bones)) },
        mainTable = rsWeightedTable {
            name("Goblin main loot")
            3 weight Drop(objs.bronze_sq_shield)
            4 weight Drop(objs.bronze_spear)
            5 weight Drop(objs.body_rune, 7)
            6 weight Drop(objs.water_rune, 6)
            3 weight Drop(objs.earth_rune, 4)
            3 weight Drop(objs.bronze_bolts, 8)
            32 weight Drop(objs.coins, 5)
            3 weight Drop(objs.coins, 9)
            3 weight Drop(objs.coins, 15)
            2 weight Drop(objs.coins, 20)
            1 weight Drop(objs.coins, 1)
            3 weight Drop(objs.bronze_axe)
            1 weight Drop(objs.bronze_scimitar)
            3 weight Drop(objs.bronze_arrow, 7)
            3 weight Drop(objs.mind_rune, 2)
            3 weight Drop(objs.earth_rune, 4)
            3 weight Drop(objs.body_rune, 2)
            1 weight Drop(objs.chaos_rune)
            1 weight Drop(objs.nature_rune)
            1 weight Drop(objs.grimy_guam_leaf)
            1 weight Drop(objs.grimy_marrentill)
            1 weight Drop(objs.grimy_tarromin)
            1 weight Drop(objs.grimy_harralander)
            1 weight Drop(objs.grimy_ranarr_weed)
            42 weight Drop(objs.coins, 1..3)
            14 weight Drop(objs.coins, 3)
            8 weight Drop(objs.coins, 5)
            7 weight Drop(objs.coins, 16)
            3 weight Drop(objs.coins, 24)
            2 weight Drop(objs.coins, 10)
            1 weight Drop(objs.tin_ore)
            42 weight Drop(objs.nothing_)
        }
    )

    private fun registerCow() = registry.registerDtx(
        npcType = npcs.cow,
        label = "Cow",
        guaranteed = rsGuaranteedTable {
            add(Drop(objs.cowhide))
            add(Drop(objs.raw_beef))
            add(Drop(objs.bones))
        }
    )

    private fun registerChicken() = registry.registerDtx(
        npcType = npcs.chicken,
        label = "Chicken",
        guaranteed = rsGuaranteedTable {
            add(Drop(objs.bones))
            add(Drop(objs.raw_chicken))
        },
        mainTable = rsWeightedTable {
            name("Chicken feathers")
            64 weight Drop(objs.feather, 5)
            32 weight Drop(objs.feather, 15)
            32 weight Drop(objs.nothing_)
        }
    )

    private fun registerGiantRat() = registry.registerDtx(
        npcType = npcs.giant_rat,
        label = "Giant Rat",
        guaranteed = rsGuaranteedTable {
            add(Drop(objs.bones))
            add(Drop(objs.raw_rat_meat))
        }
    )

    private fun registerGuard() = registry.registerDtx(
        npcType = npcs.guard,
        label = "Guard",
        guaranteed = rsGuaranteedTable {
            add(Drop(objs.bones))
            add(Drop(objs.coins, 30))
        },
        mainTable = rsWeightedTable {
            name("Guard loot")
            4 weight Drop(objs.steel_arrow)
            3 weight Drop(objs.bronze_arrow)
            2 weight Drop(objs.air_rune, 6)
            2 weight Drop(objs.earth_rune, 3)
            2 weight Drop(objs.fire_rune, 2)
            1 weight Drop(objs.blood_rune)
            1 weight Drop(objs.chaos_rune)
            1 weight Drop(objs.nature_rune)
            21 weight Drop(objs.coins, 1)
            16 weight Drop(objs.coins, 7)
            18 weight Drop(objs.coins, 5)
            9 weight Drop(objs.coins, 12)
            8 weight Drop(objs.coins, 4)
            4 weight Drop(objs.coins, 25)
            4 weight Drop(objs.coins, 17)
            2 weight Drop(objs.coins, 30)
            1 weight Drop(objs.iron_ore)
            8 weight Drop(objs.nothing_)
        }
    )

    private fun registerMan() = registry.registerDtx(
        npcType = npcs.man,
        label = "Man",
        guaranteed = rsGuaranteedTable {
            add(Drop(objs.bones))
            add(Drop(objs.coins, 3))
        },
        mainTable = rsWeightedTable {
            name("Man loot")
            25 weight Drop(objs.bronze_bolts, 2..12)
            3 weight Drop(objs.bronze_arrow, 7)
            2 weight Drop(objs.earth_rune, 4)
            2 weight Drop(objs.fire_rune, 6)
            2 weight Drop(objs.mind_rune, 9)
            1 weight Drop(objs.chaos_rune, 2)
            5 weight Drop(objs.grimy_guam_leaf)
            4 weight Drop(objs.grimy_marrentill)
            3 weight Drop(objs.grimy_tarromin)
            2 weight Drop(objs.grimy_harralander)
            2 weight Drop(objs.grimy_ranarr_weed)
            1 weight Drop(objs.grimy_irit_leaf)
            1 weight Drop(objs.grimy_avantoe)
            1 weight Drop(objs.grimy_kwuarm)
            1 weight Drop(objs.grimy_cadantine)
            1 weight Drop(objs.grimy_lantadyme)
            1 weight Drop(objs.grimy_dwarf_weed)
            42 weight Drop(objs.coins, 3)
            25 weight Drop(objs.coins, 10)
            9 weight Drop(objs.coins, 5)
            4 weight Drop(objs.coins, 15)
            1 weight Drop(objs.coins, 25)
            2 weight Drop(objs.copper_ore)
            8 weight Drop(objs.nothing_)
        }
    )

    private fun registerWoman() = registry.registerDtx(
        npcType = npcs.woman,
        label = "Woman",
        guaranteed = rsGuaranteedTable {
            add(Drop(objs.bones))
            add(Drop(objs.coins, 3))
        },
        mainTable = rsWeightedTable {
            name("Woman loot")
            25 weight Drop(objs.bronze_bolts, 2..12)
            3 weight Drop(objs.bronze_arrow, 7)
            2 weight Drop(objs.earth_rune, 4)
            2 weight Drop(objs.fire_rune, 6)
            2 weight Drop(objs.mind_rune, 9)
            1 weight Drop(objs.chaos_rune, 2)
            5 weight Drop(objs.grimy_guam_leaf)
            4 weight Drop(objs.grimy_marrentill)
            3 weight Drop(objs.grimy_tarromin)
            2 weight Drop(objs.grimy_harralander)
            2 weight Drop(objs.grimy_ranarr_weed)
            1 weight Drop(objs.grimy_irit_leaf)
            42 weight Drop(objs.coins, 3)
            25 weight Drop(objs.coins, 10)
            9 weight Drop(objs.coins, 5)
            4 weight Drop(objs.coins, 15)
            2 weight Drop(objs.copper_ore)
            8 weight Drop(objs.nothing_)
        }
    )

    private fun registerHillGiant() = registry.registerDtx(
        npcType = npcs.hill_giant,
        label = "Hill Giant",
        guaranteed = rsGuaranteedTable { add(Drop(objs.big_bones)) },
        mainTable = rsWeightedTable {
            name("Hill Giant loot")
            4 weight Drop(objs.iron_dagger)
            6 weight Drop(objs.iron_arrow, 3)
            3 weight Drop(objs.fire_rune, 15)
            3 weight Drop(objs.water_rune, 7)
            3 weight Drop(objs.law_rune, 2)
            2 weight Drop(objs.steel_arrow, 10)
            2 weight Drop(objs.mind_rune, 3)
            2 weight Drop(objs.nature_rune, 6)
            1 weight Drop(objs.chaos_rune, 2)
            1 weight Drop(objs.death_rune, 2)
            1 weight Drop(objs.grimy_guam_leaf)
            1 weight Drop(objs.grimy_marrentill)
            1 weight Drop(objs.grimy_tarromin)
            1 weight Drop(objs.grimy_harralander)
            1 weight Drop(objs.grimy_ranarr_weed)
            1 weight Drop(objs.grimy_irit_leaf)
            18 weight Drop(objs.coins, 5)
            14 weight Drop(objs.coins, 38)
            10 weight Drop(objs.coins, 52)
            8 weight Drop(objs.coins, 15)
            7 weight Drop(objs.coins, 10)
            6 weight Drop(objs.coins, 8)
            2 weight Drop(objs.coins, 88)
            1 weight Drop(objs.nothing_)
        },
        tertiaries = rsTertiaryTable {
            name("Hill Giant tertiaries")
            1 outOf 128 chance Drop(objs.giant_key)
        }
    )

    private fun registerMossGiant() = registry.registerDtx(
        npcType = npcs.moss_giant,
        label = "Moss Giant",
        guaranteed = rsGuaranteedTable {
            add(Drop(objs.big_bones))
            add(Drop(objs.grimy_lantadyme))
            add(Drop(objs.grimy_dwarf_weed))
        },
        mainTable = rsWeightedTable {
            name("Moss Giant loot")
            4 weight Drop(objs.law_rune, 3)
            3 weight Drop(objs.air_rune, 18)
            3 weight Drop(objs.earth_rune, 27)
            3 weight Drop(objs.chaos_rune, 7)
            3 weight Drop(objs.nature_rune, 6)
            2 weight Drop(objs.iron_arrow, 15)
            1 weight Drop(objs.steel_arrow, 30)
            1 weight Drop(objs.death_rune, 3)
            1 weight Drop(objs.blood_rune)
            1 weight Drop(objs.grimy_guam_leaf)
            1 weight Drop(objs.grimy_marrentill)
            1 weight Drop(objs.grimy_tarromin)
            1 weight Drop(objs.grimy_harralander)
            1 weight Drop(objs.grimy_ranarr_weed)
            1 weight Drop(objs.grimy_irit_leaf)
            1 weight Drop(objs.grimy_avantoe)
            1 weight Drop(objs.grimy_kwuarm)
            1 weight Drop(objs.grimy_cadantine)
            42 weight Drop(objs.coins, 5)
            21 weight Drop(objs.coins, 37)
            11 weight Drop(objs.coins, 2)
            10 weight Drop(objs.coins, 119)
            5 weight Drop(objs.coins, 10)
            2 weight Drop(objs.coins, 300)
            1 weight Drop(objs.coal)
            2 weight Drop(objs.nothing_)
        }
    )

    private fun registerLesserDemon() = registry.registerDtx(
        npcType = npcs.lesser_demon,
        label = "Lesser Demon",
        guaranteed = rsGuaranteedTable {
            add(Drop(objs.vile_ashes))
            add(Drop(objs.grimy_harralander))
            add(Drop(objs.grimy_ranarr_weed))
        },
        mainTable = rsWeightedTable {
            name("Lesser Demon loot")
            8 weight Drop(objs.fire_rune, 60)
            5 weight Drop(objs.chaos_rune, 12)
            3 weight Drop(objs.death_rune, 3)
            1 weight Drop(objs.fire_rune, 30)
            64 weight Drop(objs.grimy_irit_leaf)
            64 weight Drop(objs.grimy_avantoe)
            42 weight Drop(objs.grimy_kwuarm)
            32 weight Drop(objs.grimy_cadantine)
            25 weight Drop(objs.grimy_lantadyme)
            25 weight Drop(objs.grimy_dwarf_weed)
            42 weight Drop(objs.coins, 120)
            32 weight Drop(objs.coins, 40)
            10 weight Drop(objs.coins, 200)
            7 weight Drop(objs.coins, 10)
            1 weight Drop(objs.coins, 450)
            2 weight Drop(objs.gold_ore)
            2 weight Drop(objs.nothing_)
        }
    )

    private fun registerGreaterDemon() = registry.registerDtx(
        npcType = npcs.greater_demon,
        label = "Greater Demon",
        guaranteed = rsGuaranteedTable { add(Drop(objs.vile_ashes)) },
        mainTable = rsWeightedTable {
            name("Greater Demon loot")
            8 weight Drop(objs.fire_rune, 75)
            3 weight Drop(objs.chaos_rune, 15)
            3 weight Drop(objs.death_rune, 5)
            1 weight Drop(objs.fire_rune, 37)
            42 weight Drop(objs.coins, 132)
            32 weight Drop(objs.coins, 44)
            10 weight Drop(objs.coins, 220)
            7 weight Drop(objs.coins, 11)
            1 weight Drop(objs.coins, 460)
            3 weight Drop(objs.tuna)
            2 weight Drop(objs.gold_bar)
            2 weight Drop(objs.nothing_)
        }
    )

    private fun registerBlackKnight() = registry.registerDtx(
        npcType = npcs.black_knight,
        label = "Black Knight",
        guaranteed = rsGuaranteedTable { add(Drop(objs.bones)) },
        mainTable = rsWeightedTable {
            name("Black Knight loot")
            3 weight Drop(objs.body_rune, 9)
            3 weight Drop(objs.chaos_rune, 6)
            3 weight Drop(objs.earth_rune, 10)
            2 weight Drop(objs.death_rune, 2)
            2 weight Drop(objs.law_rune, 3)
            1 weight Drop(objs.mind_rune, 2)
            1 weight Drop(objs.grimy_guam_leaf)
            1 weight Drop(objs.grimy_marrentill)
            1 weight Drop(objs.grimy_tarromin)
            1 weight Drop(objs.grimy_harralander)
            1 weight Drop(objs.grimy_ranarr_weed)
            1 weight Drop(objs.grimy_irit_leaf)
            1 weight Drop(objs.grimy_avantoe)
            21 weight Drop(objs.coins, 35)
            18 weight Drop(objs.coins, 5)
            11 weight Drop(objs.coins, 6)
            10 weight Drop(objs.coins, 58)
            9 weight Drop(objs.coins, 12)
            3 weight Drop(objs.coins, 10)
            2 weight Drop(objs.coins, 80)
            1 weight Drop(objs.tin_ore)
            2 weight Drop(objs.nothing_)
        }
    )

    private fun registerDarkWizard() = registry.registerDtx(
        npcType = npcs.dark_wizard,
        label = "Dark Wizard",
        guaranteed = rsGuaranteedTable { add(Drop(objs.bones)) },
        mainTable = rsWeightedTable {
            name("Dark Wizard loot")
            4 weight Drop(objs.earth_rune, 36)
            3 weight Drop(objs.air_rune, 10)
            3 weight Drop(objs.water_rune, 10)
            3 weight Drop(objs.earth_rune, 10)
            3 weight Drop(objs.fire_rune, 10)
            2 weight Drop(objs.air_rune, 18)
            2 weight Drop(objs.water_rune, 18)
            2 weight Drop(objs.earth_rune, 18)
            2 weight Drop(objs.fire_rune, 18)
            7 weight Drop(objs.nature_rune, 4)
            6 weight Drop(objs.chaos_rune, 5)
            3 weight Drop(objs.mind_rune, 10)
            3 weight Drop(objs.body_rune, 10)
            2 weight Drop(objs.mind_rune, 18)
            2 weight Drop(objs.body_rune, 18)
            2 weight Drop(objs.blood_rune, 2)
            1 weight Drop(objs.law_rune, 3)
            18 weight Drop(objs.coins, 1)
            16 weight Drop(objs.coins, 2)
            7 weight Drop(objs.coins, 4)
            3 weight Drop(objs.coins, 29)
            1 weight Drop(objs.coins, 30)
            16 weight Drop(objs.nothing_)
        }
    )

    private fun registerSkeleton() = registry.registerDtx(
        npcType = npcs.skeleton,
        label = "Skeleton",
        guaranteed = rsGuaranteedTable { add(Drop(objs.bones)) },
        mainTable = rsWeightedTable {
            name("Skeleton loot")
            5 weight Drop(objs.bronze_arrow, 7)
            3 weight Drop(objs.iron_arrow, 5)
            3 weight Drop(objs.air_rune, 6)
            3 weight Drop(objs.mind_rune, 3)
            2 weight Drop(objs.earth_rune, 4)
            1 weight Drop(objs.chaos_rune)
            42 weight Drop(objs.coins, 3)
            16 weight Drop(objs.coins, 10)
            8 weight Drop(objs.coins, 25)
            10 weight Drop(objs.nothing_)
        }
    )

    private fun registerZombie() = registry.registerDtx(
        npcType = npcs.zombie,
        label = "Zombie",
        guaranteed = rsGuaranteedTable { add(Drop(objs.bones)) },
        mainTable = rsWeightedTable {
            name("Zombie loot")
            5 weight Drop(objs.bronze_axe)
            3 weight Drop(objs.iron_dagger)
            5 weight Drop(objs.bronze_arrow, 7)
            3 weight Drop(objs.iron_arrow, 5)
            4 weight Drop(objs.air_rune, 6)
            3 weight Drop(objs.mind_rune, 4)
            3 weight Drop(objs.earth_rune, 3)
            2 weight Drop(objs.chaos_rune, 2)
            1 weight Drop(objs.death_rune)
            42 weight Drop(objs.coins, 5)
            21 weight Drop(objs.coins, 18)
            7 weight Drop(objs.coins, 30)
            3 weight Drop(objs.coins, 50)
            10 weight Drop(objs.nothing_)
        }
    )
}
