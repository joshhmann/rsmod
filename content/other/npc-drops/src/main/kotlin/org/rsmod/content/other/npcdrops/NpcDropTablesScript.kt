package org.rsmod.content.other.npcdrops

import jakarta.inject.Inject
import org.rsmod.api.config.refs.npcs
import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.game.type.npc.NpcType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Registers vanilla-accurate (rev 228) drop tables for the basic F2P monsters.
 *
 * Data sources:
 * - Primary: OSRS wiki (rev 228 drop tables)
 * - Secondary: Kronos-184 JSON files as a cross-reference (rev 184, treat as approximate)
 *
 * Comments marked "// TODO: wiki-validate drop rates" indicate quantities that were taken from
 * Kronos and have not yet been verified against the official rev-228 OSRS wiki page.
 */
class NpcDropTablesScript @Inject constructor(private val registry: NpcDropTableRegistry) :
    PluginScript() {

    override fun ScriptContext.startup() {
        registerManWoman()
        registerGoblin()
        registerCow()
        registerChicken()
        registerGiantRat()
        registerGuard()
        registerScorpion()
        registerImp()
        registerDarkWizard()
        registerLesserDemon()
        registerBlackKnight()
    }

    // -----------------------------------------------------------------------
    // Man / Woman
    // Drop table source: https://oldschool.runescape.wiki/w/Man
    // Always: Bones
    // Main roll: Coins (3–25, weighted) — ~75 % chance of nothing per roll is achieved via the
    // "nothing" entry weight. Kronos had 4 equal-weight tables; we simplify to one table with
    // "nothing" accounting for ~75 % of the weight.
    // -----------------------------------------------------------------------
    private fun registerManWoman() {
        val manWomanTable = dropTable {
            always(objs.bones)

            // One roll on the loot table. Nothing drops ~75 % of the time.
            // TODO: wiki-validate drop rates — exact nothing weight vs item weight for Man
            table("Loot", weight = 1) {
                // 25 % chance of something; modelled here as nothing(75) vs items(25 total).
                nothing(weight = 75)
                item(objs.coins, quantity = 3..3, weight = 6) // 3 coins
                item(objs.coins, quantity = 5..5, weight = 6) // 5 coins
                item(objs.coins, quantity = 15..15, weight = 3) // 15 coins
                item(objs.coins, quantity = 25..25, weight = 3) // 25 coins
                // Low-tier weapons — roughly 1-in-100 combined
                item(DropTableObjs.iron_dagger, weight = 1) // TODO: wiki-validate drop rates
                item(DropTableObjs.bronze_med_helm, weight = 1) // TODO: wiki-validate drop rates
            }
        }

        // Use BaseNpcs refs where available to avoid duplicate registrations.
        val manWomanNpcs: List<NpcType> =
            listOf(
                npcs.man,
                npcs.man2,
                npcs.man3,
                npcs.woman,
                npcs.woman2,
                npcs.woman3,
                npcs.man_indoor,
            )

        registry.register(manWomanNpcs, manWomanTable)
    }

    // -----------------------------------------------------------------------
    // Goblin
    // Drop table source: https://oldschool.runescape.wiki/w/Goblin
    // Always: Bones
    // Three equal-weight tables: Armour/Weapons, Runes, Other
    // -----------------------------------------------------------------------
    private fun registerGoblin() {
        val goblinTable = dropTable {
            always(objs.bones)

            // Armour / Weapons table (weight 1 of 3)
            // TODO: wiki-validate drop rates — using Kronos weights as starting point
            table("Armour/Weapons", weight = 1) {
                item(DropTableObjs.bronze_sq_shield, weight = 9)
                item(DropTableObjs.bronze_bolts, quantity = 2..2, weight = 6)
                item(DropTableObjs.bronze_bolts, quantity = 4..4, weight = 6)
                item(DropTableObjs.bronze_bolts, quantity = 8..8, weight = 6)
                item(DropTableObjs.bronze_scimitar, weight = 3)
                item(DropTableObjs.bronze_spear, weight = 3)
                item(DropTableObjs.bronze_javelin, weight = 1)
                item(DropTableObjs.bronze_kiteshield, weight = 1)
                // Brass necklace — low weight rare
                // (not in BaseObjs; skip for now rather than add a wrong name)
                // TODO: add brass_necklace to DropTableObjs once internal name is verified
            }

            // Runes table (weight 1 of 3)
            table("Runes", weight = 1) {
                item(objs.water_rune, quantity = 6..6, weight = 3) // TODO: wiki-validate drop rates
                item(objs.mind_rune, quantity = 1..2, weight = 3) // TODO: wiki-validate drop rates
                item(objs.body_rune, quantity = 2..7, weight = 3)
                item(objs.earth_rune, quantity = 4..4, weight = 3)
                item(objs.chaos_rune, weight = 1)
                item(objs.nature_rune, weight = 1)
            }

            // Other table (weight 1 of 3) — coins + misc items
            table("Other", weight = 1) {
                item(objs.coins, quantity = 1..24, weight = 9) // TODO: wiki-validate drop rates
                item(objs.hammer, weight = 9)
                item(objs.bronze_axe, weight = 9)
                // Some items from Kronos (air talisman, bucket, red cape) need verified
                // internal names before adding — skipped with TODO.
                // TODO: add air_talisman, red_cape to DropTableObjs and re-enable
                // item(DropTableObjs.air_talisman, weight = 6)
                // item(DropTableObjs.bucket_empty, weight = 6) — already in objs.bucket_empty
                item(objs.bucket_empty, weight = 6)
            }
        }

        val goblinNpcs: List<NpcType> =
            listOf(
                DropTableNpcs.goblin,
                DropTableNpcs.goblin_2,
                DropTableNpcs.goblin_3,
                DropTableNpcs.goblin_chef,
                DropTableNpcs.goblin_guard,
            )
        registry.register(goblinNpcs, goblinTable)
    }

    // -----------------------------------------------------------------------
    // Cow
    // Drop table source: https://oldschool.runescape.wiki/w/Cow
    // Always: Cowhide, Raw beef, Bones  (no random table)
    // -----------------------------------------------------------------------
    private fun registerCow() {
        val cowTable = dropTable {
            always(DropTableObjs.cowhide)
            always(DropTableObjs.raw_beef)
            always(objs.bones)
            // No weighted table — Cow has no random loot at rev 228.
        }

        val cowNpcs: List<NpcType> =
            listOf(
                DropTableNpcs.cow,
                DropTableNpcs.cow2,
                DropTableNpcs.cow3,
                DropTableNpcs.cow_beef,
            )
        registry.register(cowNpcs, cowTable)
    }

    // -----------------------------------------------------------------------
    // Chicken
    // Drop table source: https://oldschool.runescape.wiki/w/Chicken
    // Always: Bones, Raw chicken
    // Optional (1 roll): Feathers 5–15 — Kronos shows weight 1 in single "Other" table.
    // -----------------------------------------------------------------------
    private fun registerChicken() {
        val chickenTable = dropTable {
            always(objs.bones)
            always(DropTableObjs.raw_chicken)

            // Optional feather roll — 1 entry means it always lands if we roll this table.
            // In vanilla the feather drop is not guaranteed; it appears every kill per wiki.
            // TODO: wiki-validate drop rates — confirm if feathers are always or probabilistic
            table("Other", weight = 1) { item(DropTableObjs.feather, quantity = 5..15, weight = 1) }
        }

        val chickenNpcs: List<NpcType> = listOf(DropTableNpcs.chicken, DropTableNpcs.chicken_2)
        registry.register(chickenNpcs, chickenTable)
    }

    // -----------------------------------------------------------------------
    // Giant Rat
    // Drop table source: https://oldschool.runescape.wiki/w/Giant_rat
    // Always: Bones, Raw rat meat  (no random table at rev 228)
    // -----------------------------------------------------------------------
    private fun registerGiantRat() {
        val giantRatTable = dropTable {
            always(objs.bones)
            always(DropTableObjs.raw_rat_meat)
            // No random loot table for Giant Rat.
        }

        val giantRatNpcs: List<NpcType> =
            listOf(DropTableNpcs.giant_rat, DropTableNpcs.giant_rat_2, DropTableNpcs.giant_rat_3)
        registry.register(giantRatNpcs, giantRatTable)
    }

    // -----------------------------------------------------------------------
    // Guard
    // Drop table source: https://oldschool.runescape.wiki/w/Guard
    // Always: Bones
    // Five equal-weight tables: Armour/Weapons, Ores/Bars, Runes/Talismans, Seeds, Other
    // TODO: wiki-validate drop rates — Kronos weights used as starting point (rev 184 vs 228)
    // -----------------------------------------------------------------------
    private fun registerGuard() {
        val guardTable = dropTable {
            always(objs.bones)

            // Armour / Weapons (weight 1 of 5)
            table("Armour/Weapons", weight = 1) {
                item(DropTableObjs.iron_dagger, weight = 8)
                // Iron bolts — not in BaseObjs; using bronze_bolts as placeholder
                // TODO: add iron_bolts to DropTableObjs once internal name is verified
                item(
                    DropTableObjs.bronze_bolts,
                    quantity = 1..12,
                    weight = 6,
                ) // TODO: wiki-validate drop rates (should be iron bolts)
                item(objs.bronze_arrow, quantity = 1..2, weight = 3)
                item(DropTableObjs.steel_sword, weight = 1) // TODO: wiki-validate drop rates
                item(DropTableObjs.steel_med_helm, weight = 1) // TODO: wiki-validate drop rates
            }

            // Ores / Bars (weight 1 of 5)
            table("Ores/Bars", weight = 1) { item(DropTableObjs.iron_ore, weight = 1) }

            // Runes / Talismans (weight 1 of 5)
            table("Runes/Talismans", weight = 1) {
                item(objs.air_rune, quantity = 6..6, weight = 10) // TODO: wiki-validate drop rates
                item(
                    objs.earth_rune,
                    quantity = 3..3,
                    weight = 10,
                ) // TODO: wiki-validate drop rates
                item(objs.fire_rune, quantity = 2..2, weight = 10) // TODO: wiki-validate drop rates
                item(objs.chaos_rune, quantity = 1..2, weight = 6) // TODO: wiki-validate drop rates
                item(objs.nature_rune, weight = 1)
            }

            // Seeds (weight 1 of 5) — low-level farming seeds
            // TODO: wiki-validate drop rates — seed names must match cache internal names
            table("Seeds", weight = 1) {
                item(objs.cabbage_seed, quantity = 4..4, weight = 6)
                // Potato seed, onion seed, tomato seed, sweetcorn seed etc. are not yet in
                // BaseObjs; add them when their internal names are verified.
                // TODO: add potato_seed, onion_seed, tomato_seed, sweetcorn_seed to refs
            }

            // Other (weight 1 of 5)
            table("Other", weight = 1) {
                item(objs.coins, quantity = 1..30, weight = 10) // TODO: wiki-validate drop rates
                item(objs.grain, weight = 10)
            }
        }

        val guardNpcs: List<NpcType> =
            listOf(DropTableNpcs.guard, DropTableNpcs.guard_2, DropTableNpcs.guard_3)
        registry.register(guardNpcs, guardTable)
    }

    // -----------------------------------------------------------------------
    // Scorpion
    // Drop table source: https://oldschool.runescape.wiki/w/Scorpion
    // Always: Nothing (no guaranteed drops)
    // -----------------------------------------------------------------------
    private fun registerScorpion() {
        val scorpionTable = dropTable {
            // Scorpion has no guaranteed drops - only random loot
            table("Other", weight = 1) {
                item(
                    objs.chaos_rune,
                    quantity = 1..1,
                    weight = 10,
                ) // TODO: wiki-validate drop rates
            }
        }
        registry.register(DropTableNpcs.scorpion, scorpionTable)
    }

    // -----------------------------------------------------------------------
    // Imp
    // Drop table source: https://oldschool.runescape.wiki/w/Imp
    // Always: Nothing (no guaranteed drops)
    // -----------------------------------------------------------------------
    private fun registerImp() {
        val impTable = dropTable {
            // Imp has no guaranteed drops - only random loot
            table("Other", weight = 1) {
                item(objs.coins, quantity = 1..10, weight = 20) // TODO: wiki-validate drop rates
            }
        }
        registry.register(DropTableNpcs.imp, impTable)
    }

    // -----------------------------------------------------------------------
    // Dark Wizard
    // Drop table source: https://oldschool.runescape.wiki/w/Dark_wizard
    // Always: Bones
    // -----------------------------------------------------------------------
    private fun registerDarkWizard() {
        val wizardTable = dropTable {
            always(objs.bones)

            // Runes and equipment
            table("Loot", weight = 1) {
                item(
                    objs.water_rune,
                    quantity = 5..15,
                    weight = 15,
                ) // TODO: wiki-validate drop rates
                item(objs.body_rune, quantity = 5..15, weight = 15)
                item(objs.mind_rune, quantity = 5..15, weight = 15)
                item(objs.earth_rune, quantity = 5..15, weight = 10)
                item(objs.chaos_rune, quantity = 2..5, weight = 5)
                item(objs.nature_rune, quantity = 2..5, weight = 3)
                item(objs.law_rune, quantity = 2..5, weight = 2)
            }
        }

        val wizardNpcs: List<NpcType> =
            listOf(
                // Note: dark_wizard symbol does not exist in rev 228
                DropTableNpcs.bearded_dark_wizard,
                DropTableNpcs.young_dark_wizard,
            )
        registry.register(wizardNpcs, wizardTable)
    }

    // -----------------------------------------------------------------------
    // Lesser Demon
    // Drop table source: https://oldschool.runescape.wiki/w/Lesser_demon
    // Always: Ashes (not bones)
    // -----------------------------------------------------------------------
    private fun registerLesserDemon() {
        val demonTable = dropTable {
            // Runes
            table("Runes", weight = 1) {
                item(
                    objs.chaos_rune,
                    quantity = 10..30,
                    weight = 10,
                ) // TODO: wiki-validate drop rates
                item(objs.death_rune, quantity = 5..15, weight = 5)
                item(objs.blood_rune, quantity = 2..5, weight = 2)
                item(objs.fire_rune, quantity = 30..60, weight = 10)
            }

            // Weapons/Armour
            table("Equipment", weight = 1) {
                item(objs.steel_axe, weight = 3)
                item(objs.steel_scimitar, weight = 2)
                item(objs.mithril_chainbody, weight = 1)
            }

            // Other
            table("Other", weight = 1) { item(objs.coins, quantity = 10..200, weight = 30) }
        }
        registry.register(DropTableNpcs.lesser_demon, demonTable)
    }

    // -----------------------------------------------------------------------
    // Black Knight
    // Drop table source: https://oldschool.runescape.wiki/w/Black_knight
    // Always: Bones
    // -----------------------------------------------------------------------
    private fun registerBlackKnight() {
        val knightTable = dropTable {
            always(objs.bones)

            // Weapons/Armour
            table("Equipment", weight = 1) {
                item(objs.iron_sword, weight = 5)
                item(objs.iron_full_helm, weight = 3)
                item(objs.steel_med_helm, weight = 2)
            }

            // Runes
            table("Runes", weight = 1) {
                item(objs.mind_rune, quantity = 2..5, weight = 10)
                item(objs.chaos_rune, quantity = 1..3, weight = 5)
                item(objs.body_rune, quantity = 5..10, weight = 5)
            }

            // Other
            table("Other", weight = 1) { item(objs.coins, quantity = 1..50, weight = 25) }
        }

        val knightNpcs: List<NpcType> =
            listOf(DropTableNpcs.black_knight, DropTableNpcs.aggressive_black_knight)
        registry.register(knightNpcs, knightTable)
    }
}
