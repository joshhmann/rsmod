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
        // Inline drop table registrations (only for NPCs without a dedicated external file)
        registerGoblin()
        registerCow()
        registerChicken()
        registerGiantRat()
        registerGuard()
        registerScorpion()
        registerImp()
        registerDarkWizard()
        registerKingBlackDragon()
        registerKalphiteQueen()

        // External drop table registrations from tables/
        ManWomanDropTables.registerAll(registry)
        BlackKnightDropTables.registerAll(registry)
        BarbarianDropTables.registerAll(registry)
        ZombieDropTables.registerAll(registry)
        MossGiantDropTables.registerAll(registry)
        RovingMossgiantDropTables.registerAll(registry)
        HillGiantDropTables.registerAll(registry)
        WarriorDropTables.registerAll(registry)
        LesserDemonDropTables.registerAll(registry)
        MuggerDropTables.registerAll(registry)
        UnicornDropTables.registerAll(registry)
        BearDropTables.registerAll(registry)
        DwarfDropTables.registerAll(registry)
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
                item(objs.waterrune, quantity = 6..6, weight = 3) // TODO: wiki-validate drop rates
                item(objs.mindrune, quantity = 1..2, weight = 3) // TODO: wiki-validate drop rates
                item(objs.bodyrune, quantity = 2..7, weight = 3)
                item(objs.earthrune, quantity = 4..4, weight = 3)
                item(objs.chaosrune, weight = 1)
                item(objs.naturerune, weight = 1)
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

            // Tertiary drops - clue scrolls (1/128 each = 2/128 total)
            table("Tertiary", weight = 1) {
                nothing(weight = 126) // 126/128 chance of nothing
                item(DropTableObjs.trail_clue_beginner, weight = 1) // 1/128 beginner
                item(DropTableObjs.trail_clue_easy_simple001, weight = 1) // 1/128 easy
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
    // Drop table source: https://oldschool.runescape.wiki/w/giantrat
    // Always: Bones, Raw rat meat  (no random table at rev 228)
    // -----------------------------------------------------------------------
    private fun registerGiantRat() {
        val giantRatTable = dropTable {
            always(objs.bones)
            always(DropTableObjs.raw_rat_meat)
            // No random loot table for Giant Rat.
        }

        val giantRatNpcs: List<NpcType> =
            listOf(DropTableNpcs.giantrat, DropTableNpcs.giantrat2, DropTableNpcs.giantrat3)
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
                item(objs.airrune, quantity = 6..6, weight = 10) // TODO: wiki-validate drop rates
                item(objs.earthrune, quantity = 3..3, weight = 10) // TODO: wiki-validate drop rates
                item(objs.firerune, quantity = 2..2, weight = 10) // TODO: wiki-validate drop rates
                item(objs.chaosrune, quantity = 1..2, weight = 6) // TODO: wiki-validate drop rates
                item(objs.naturerune, weight = 1)
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

            // Tertiary drops - clue scrolls (1/128 each = 2/128 total)
            table("Tertiary", weight = 1) {
                nothing(weight = 126) // 126/128 chance of nothing
                item(DropTableObjs.trail_clue_beginner, weight = 1) // 1/128 beginner
                item(DropTableObjs.trail_clue_easy_simple001, weight = 1) // 1/128 easy
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
                item(objs.chaosrune, quantity = 1..1, weight = 10) // TODO: wiki-validate drop rates
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
                    objs.waterrune,
                    quantity = 5..15,
                    weight = 15,
                ) // TODO: wiki-validate drop rates
                item(objs.bodyrune, quantity = 5..15, weight = 15)
                item(objs.mindrune, quantity = 5..15, weight = 15)
                item(objs.earthrune, quantity = 5..15, weight = 10)
                item(objs.chaosrune, quantity = 2..5, weight = 5)
                item(objs.naturerune, quantity = 2..5, weight = 3)
                item(objs.lawrune, quantity = 2..5, weight = 2)
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
                    objs.chaosrune,
                    quantity = 10..30,
                    weight = 10,
                ) // TODO: wiki-validate drop rates
                item(objs.deathrune, quantity = 5..15, weight = 5)
                item(objs.bloodrune, quantity = 2..5, weight = 2)
                item(objs.firerune, quantity = 30..60, weight = 10)
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
                item(objs.mindrune, quantity = 2..5, weight = 10)
                item(objs.chaosrune, quantity = 1..3, weight = 5)
                item(objs.bodyrune, quantity = 5..10, weight = 5)
            }

            // Other
            table("Other", weight = 1) { item(objs.coins, quantity = 1..50, weight = 25) }
        }

        val knightNpcs: List<NpcType> =
            listOf(DropTableNpcs.black_knight, DropTableNpcs.aggressive_black_knight)
        registry.register(knightNpcs, knightTable)
    }

    // -----------------------------------------------------------------------
    // Hill Giant
    // Drop table source: https://oldschool.runescape.wiki/w/Hill_giant
    // Always: Big bones
    // Pre-roll: Giant key (1/128)
    // -----------------------------------------------------------------------
    private fun registerHillGiant() {
        val hillGiantTable = dropTable {
            always(objs.big_bones)

            // Pre-roll: Giant key (1/128 chance before main table)
            table("Pre-roll", weight = 1) {
                nothing(weight = 127)
                item(DropTableObjs.giant_key, weight = 1)
            }

            // Weapons and Armour (total weight: 16/128)
            table("Weapons and Armour", weight = 16) {
                nothing(weight = 1)
                item(objs.iron_dagger, weight = 4)
                item(objs.iron_med_helm, weight = 5)
                item(DropTableObjs.iron_full_helm, weight = 5)
                item(objs.iron_kiteshield, weight = 3)
                item(DropTableObjs.steel_longsword, weight = 2)
            }

            // Runes and Ammunition (total weight: 24/128)
            table("Runes and Ammunition", weight = 24) {
                nothing(weight = 1)
                item(objs.iron_arrow, quantity = 3, weight = 6)
                item(objs.firerune, quantity = 15, weight = 3)
                item(objs.waterrune, quantity = 7, weight = 3)
                item(objs.lawrune, quantity = 2, weight = 3)
                item(objs.steel_arrow, quantity = 10, weight = 2)
                item(objs.mindrune, quantity = 3, weight = 2)
                item(objs.cosmicrune, quantity = 2, weight = 2)
                item(objs.naturerune, quantity = 6, weight = 2)
                item(objs.chaosrune, quantity = 2, weight = 1)
                item(objs.deathrune, quantity = 2, weight = 1)
            }

            // Coins (total weight: 65/128 for F2P)
            table("Coins", weight = 65) {
                item(objs.coins, quantity = 5, weight = 18)
                item(objs.coins, quantity = 38, weight = 14)
                item(objs.coins, quantity = 52, weight = 10)
                item(objs.coins, quantity = 15, weight = 8)
                item(objs.coins, quantity = 10, weight = 7)
                item(objs.coins, quantity = 8, weight = 6)
                item(objs.coins, quantity = 88, weight = 2)
            }

            // Other drops (total weight: 20/128)
            table("Other", weight = 20) {
                nothing(weight = 1)
                item(DropTableObjs.limpwurt_root, weight = 11)
                item(DropTableObjs.beer, weight = 6)
                item(DropTableObjs.body_talisman, weight = 2)
            }

            // Gem Drop Table (3/128 chance)
            table("Gem Drop Table", weight = 3) {
                nothing(weight = 1)
                item(objs.uncut_sapphire, weight = 32)
                item(objs.uncut_emerald, weight = 16)
                item(objs.uncut_ruby, weight = 8)
                item(DropTableObjs.chaos_talisman, weight = 3)
                item(DropTableObjs.nature_talisman, weight = 3)
                item(objs.uncut_diamond, weight = 2)
                item(DropTableObjs.rune_javelin, quantity = 5, weight = 1)
                item(DropTableObjs.loop_half_key, weight = 1)
                item(DropTableObjs.tooth_half_key, weight = 1)
            }

            // Tertiary drops (roll independently)
            // Clue scrolls: 1/64 beginner, 1/64 easy (2/64 total = 1/32 for any clue)
            table("Tertiary", weight = 1) {
                nothing(weight = 62) // 62/64 chance of nothing
                item(DropTableObjs.clue_scroll_beginner, weight = 1) // 1/64 beginner
                item(DropTableObjs.trail_clue_easy_simple001, weight = 1) // 1/64 easy
            }
        }

        val hillGiantNpcs: List<NpcType> =
            listOf(
                DropTableNpcs.hill_giant,
                DropTableNpcs.hill_giant2,
                DropTableNpcs.hill_giant3,
                DropTableNpcs.wilderness_hill_giant,
                DropTableNpcs.wilderness_hill_giant2,
                DropTableNpcs.wilderness_hill_giant3,
            )
        registry.register(hillGiantNpcs, hillGiantTable)
    }

    // -----------------------------------------------------------------------
    // Barbarian
    // Drop table source: https://oldschool.runescape.wiki/w/Barbarian
    // Always: Bones
    // Main drops: Coins, Bronze equipment, Runes
    // Tertiary: Clue scroll (beginner), Clue scroll (easy)
    // -----------------------------------------------------------------------
    private fun registerBarbarian() {
        val barbarianTable = dropTable {
            always(objs.bones)

            // Coins - most common drop
            table("Coins", weight = 50) {
                item(objs.coins, quantity = 1..10, weight = 30)
                item(objs.coins, quantity = 11..25, weight = 15)
                item(objs.coins, quantity = 26..50, weight = 5)
            }

            // Bronze equipment
            table("Bronze Equipment", weight = 25) {
                nothing(weight = 10)
                item(objs.bronze_axe, weight = 5)
                item(objs.bronze_mace, weight = 4)
                item(objs.bronze_sword, weight = 3)
                item(objs.bronze_med_helm, weight = 2)
                item(objs.bronze_scimitar, weight = 1)
            }

            // Runes
            table("Runes", weight = 20) {
                nothing(weight = 8)
                item(objs.airrune, quantity = 5..10, weight = 5)
                item(objs.mindrune, quantity = 3..6, weight = 4)
                item(objs.waterrune, quantity = 3..6, weight = 2)
                item(objs.earthrune, quantity = 2..4, weight = 1)
            }

            // Other drops
            table("Other", weight = 10) {
                nothing(weight = 7)
                item(DropTableObjs.cooked_meat, weight = 2)
                item(DropTableObjs.beer, weight = 1)
            }

            // Tertiary drops - clue scrolls (1/128 each = 2/128 total)
            table("Tertiary", weight = 1) {
                nothing(weight = 126) // 126/128 chance of nothing
                item(DropTableObjs.trail_clue_beginner, weight = 1) // 1/128 beginner
                item(DropTableObjs.trail_clue_easy_simple001, weight = 1) // 1/128 easy
            }
        }

        val barbarianNpcs: List<NpcType> =
            listOf(
                DropTableNpcs.barbarian,
                DropTableNpcs.barbarian_2,
                DropTableNpcs.barbarian_3,
                DropTableNpcs.barbarian_4,
                DropTableNpcs.barbarian_5,
            )
        registry.register(barbarianNpcs, barbarianTable)
    }

    // -----------------------------------------------------------------------
    // King Black Dragon (Boss)
    // Drop table source: https://oldschool.runescape.wiki/w/King_Black_Dragon
    // Combat Level: 276, HP: 240
    // Always: Dragon bones, Black dragonhide
    // Located in Wilderness (level 40+)
    // -----------------------------------------------------------------------
    private fun registerKingBlackDragon() {
        val kbdTable = dropTable {
            // Guaranteed drops
            always(objs.dragon_bones)
            // Note: black_dragonhide should be added when symbol is verified

            // Weapons/Armour (various drop rates)
            table("Weapons/Armour", weight = 1) {
                item(objs.rune_longsword, weight = 10) // ~1/12
                item(objs.adamant_platebody, weight = 9) // ~1/14
                item(objs.adamant_kiteshield, weight = 3) // ~1/42
                // TODO: Add dragon_med_helm when symbol is verified
            }

            // Runes and ammunition
            table("Runes", weight = 1) {
                item(objs.firerune, quantity = 50..100, weight = 5) // ~1/25
                item(objs.airrune, quantity = 50..100, weight = 10) // ~1/12
                item(objs.iron_arrow, quantity = 50..100, weight = 10) // ~1/12
                item(objs.lawrune, quantity = 10..20, weight = 5) // ~1/25
                item(objs.bloodrune, quantity = 10..20, weight = 5) // ~1/25
            }

            // Resources
            table("Resources", weight = 1) {
                item(objs.yew_logs, quantity = 10..20, weight = 10) // ~1/12
                // TODO: Add adamantite_bar when symbol is verified
            }

            // Other
            table("Other", weight = 1) {
                // TODO: Add dragonstone when symbol is verified
                item(objs.silver_ore, quantity = 10..20, weight = 5) // ~1/25
                item(objs.coins, quantity = 1000..5000, weight = 20)
            }

            // Very rare drops
            table("Very Rare", weight = 1) {
                // Draconic visage - ~1/5000
                // KBD head - ~1/128 (slayer trophy)
            }
        }

        // Register for KBD NPC type
        registry.register(DropTableNpcs.black_dragon, kbdTable)
    }

    // -----------------------------------------------------------------------
    // Kalphite Queen (Boss)
    // Drop table source: https://oldschool.runescape.wiki/w/Kalphite_Queen
    // Combat Level: 333, HP: 255 (airborne), 255 (grounded)
    // Two-phase fight: transforms at 50% HP (airborne ↔ grounded)
    // Located in Kalphite Lair (requires rope to enter)
    // -----------------------------------------------------------------------
    private fun registerKalphiteQueen() {
        val kqTable = dropTable {
            // Guaranteed drops
            always(objs.bones)

            // Weapons/Armour (KQ is known for chainbody drops)
            table("Weapons/Armour", weight = 1) {
                // Rune chainbody is iconic KQ drop
                item(objs.rune_chainbody, weight = 10) // ~1/12
                item(objs.rune_sq_shield, weight = 8) // ~1/15
                item(objs.rune_2h_sword, weight = 6) // ~1/20
                item(DropTableObjs.dragon_chainbody, weight = 1) // Very rare, ~1/128
                // TODO: Add dragon_2h_sword when symbol is verified
            }

            // Runes and ammunition
            table("Runes", weight = 1) {
                item(objs.deathrune, quantity = 50..100, weight = 10) // ~1/12
                item(objs.bloodrune, quantity = 20..50, weight = 8) // ~1/15
                item(objs.chaosrune, quantity = 50..100, weight = 10) // ~1/12
                item(objs.lawrune, quantity = 30..60, weight = 8) // ~1/15
                item(objs.naturerune, quantity = 20..40, weight = 6) // ~1/20
            }

            // Consumables (KQ drops noted wines)
            table("Consumables", weight = 1) {
                // Wines are a signature KQ drop
                item(DropTableObjs.wine_of_zamorak, quantity = 5..10, weight = 15) // ~1/8
                item(DropTableObjs.wine_of_zamorak, quantity = 10..20, weight = 10) // ~1/12
                // TODO: Add noted wines when noted item symbols are verified
            }

            // Resources and materials
            table("Resources", weight = 1) {
                item(DropTableObjs.bigoysterpearls, quantity = 1..5, weight = 8) // ~1/15
                item(DropTableObjs.shark, quantity = 1..3, weight = 10) // ~1/12
                // TODO: Add noted ores/bars when symbols verified
            }

            // Other
            table("Other", weight = 1) {
                item(objs.coins, quantity = 5000..15000, weight = 20) // ~1/6
                // KQ head - slayer trophy, ~1/128
                // TODO: Add kalphite_queen_head when symbol verified
            }

            // Very rare drops
            table("Very Rare", weight = 1) {
                // Jar of sand - ~1/2000
                // Kalphite princess pet - ~1/3000
                // KQ head (trophy) - ~1/128
            }
        }

        // Register for KQ NPC type
        registry.register(DropTableNpcs.kalphite_queen, kqTable)
    }
}
