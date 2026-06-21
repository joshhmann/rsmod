package org.rsmod.content.other.npcdrops

import jakarta.inject.Inject
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
        GuardDropTables.registerAll(registry)
        JailGuardDropTables.registerAll(registry)
        registerScorpion()
        ImpDropTables.registerAll(registry)
        DarkWizardDropTables.registerAll(registry)
        WizardDropTables.registerAll(registry)
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
        SkeletonDropTables.registerAll(registry)
        BatDropTables.registerAll(registry)
        ThiefDropTables.registerAll(registry)
        ChaosDruidDropTables.registerAll(registry)
        RatDropTables.registerAll(registry)
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
                DropTableNpcs.goblin_unarmed_melee_1,
                DropTableNpcs.goblin_unarmed_melee_4,
                DropTableNpcs.goblin_unarmed_melee_5,
                DropTableNpcs.goblin_unarmed_melee_6,
                DropTableNpcs.goblin_unarmed_melee_7,
                DropTableNpcs.goblin_unarmed_melee_8,
                DropTableNpcs.goblin_armed,
            )
        registry.register(goblinNpcs.distinct(), goblinTable)
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
                DropTableNpcs.cow2_calf,
                DropTableNpcs.cow3_calf,
            )
        registry.register(cowNpcs.distinct(), cowTable)
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
        registry.register(chickenNpcs.distinct(), chickenTable)
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
            listOf(
                DropTableNpcs.giantrat,
                DropTableNpcs.giantrat2,
                DropTableNpcs.giantrat3,
                DropTableNpcs.giantrat_grey,
                DropTableNpcs.giantrat_grey2,
                DropTableNpcs.giantrat_grey3,
                DropTableNpcs.giantrat1_2,
                DropTableNpcs.giantrat1_3,
            )
        registry.register(giantRatNpcs.distinct(), giantRatTable)
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


    // -----------------------------------------------------------------------
    // Spider / Giant Spider
    // Drop table source: https://oldschool.runescape.wiki/w/Spider
    // Always: Nothing (no guaranteed drops)
    // -----------------------------------------------------------------------
    private fun registerSpider() {
        val spiderTable = dropTable {
            // Spider has no guaranteed drops, only random
            table("Other", weight = 1) {
                nothing(weight = 5)
                item(objs.coins, quantity = 1..5, weight = 3)
            }
        }

        val spiderNpcs: List<NpcType> =
            listOf(DropTableNpcs.spider, DropTableNpcs.giantspider1, DropTableNpcs.giantspider2)
        registry.register(spiderNpcs.distinct(), spiderTable)
    }
}
