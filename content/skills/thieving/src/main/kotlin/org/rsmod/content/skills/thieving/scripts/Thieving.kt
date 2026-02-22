package org.rsmod.content.skills.thieving.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.thievingLvl
import org.rsmod.api.random.GameRandom
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.script.onOpLoc2
import org.rsmod.api.script.onOpLoc3
import org.rsmod.api.script.onOpLoc4
import org.rsmod.api.script.onOpNpc2
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.game.entity.Npc
import org.rsmod.game.hit.HitType
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.type.loc.LocType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// TODO:
// - Ardy Diary bonus (double pickpocket chance for Knights of Ardougne at 100 diary)
// - Rogue equipment outfit bonus (duplicate loot on success)
// - Dodgy necklace (25% chance to avoid being stunned on failure)
// - Black mask / slayer helmet thieving bonus (not standard OSRS but easy to add later)
class Thieving
@Inject
constructor(private val locRepo: LocRepository, private val random: GameRandom) : PluginScript() {

    override fun ScriptContext.startup() {
        // --------------- Pickpocket handlers ---------------
        onOpNpc2(ThievingNpcs.man) { pickpocket(it.npc, Pickpockets.MAN_WOMAN) }
        onOpNpc2(ThievingNpcs.man2) { pickpocket(it.npc, Pickpockets.MAN_WOMAN) }
        onOpNpc2(ThievingNpcs.man3) { pickpocket(it.npc, Pickpockets.MAN_WOMAN) }
        onOpNpc2(ThievingNpcs.woman) { pickpocket(it.npc, Pickpockets.MAN_WOMAN) }
        onOpNpc2(ThievingNpcs.woman2) { pickpocket(it.npc, Pickpockets.MAN_WOMAN) }
        onOpNpc2(ThievingNpcs.woman3) { pickpocket(it.npc, Pickpockets.MAN_WOMAN) }
        onOpNpc2(ThievingNpcs.farmer) { pickpocket(it.npc, Pickpockets.FARMER) }
        onOpNpc2(ThievingNpcs.ham_member) { pickpocket(it.npc, Pickpockets.HAM_MEMBER) }
        onOpNpc2(ThievingNpcs.ham_guard) { pickpocket(it.npc, Pickpockets.HAM_GUARD) }
        onOpNpc2(ThievingNpcs.al_kharid_warrior) {
            pickpocket(it.npc, Pickpockets.AL_KHARID_WARRIOR)
        }
        onOpNpc2(ThievingNpcs.rogue) { pickpocket(it.npc, Pickpockets.ROGUE) }
        onOpNpc2(ThievingNpcs.cave_goblin) { pickpocket(it.npc, Pickpockets.CAVE_GOBLIN) }
        onOpNpc2(ThievingNpcs.master_farmer) { pickpocket(it.npc, Pickpockets.MASTER_FARMER) }
        onOpNpc2(ThievingNpcs.guard) { pickpocket(it.npc, Pickpockets.GUARD) }
        onOpNpc2(ThievingNpcs.knight_of_ardougne) {
            pickpocket(it.npc, Pickpockets.KNIGHT_OF_ARDOUGNE)
        }
        onOpNpc2(ThievingNpcs.menaphite_thug) { pickpocket(it.npc, Pickpockets.MENAPHITE_THUG) }
        onOpNpc2(ThievingNpcs.paladin) { pickpocket(it.npc, Pickpockets.PALADIN) }
        onOpNpc2(ThievingNpcs.hero) { pickpocket(it.npc, Pickpockets.HERO) }

        // --------------- Stall handlers ---------------
        // Vegetable stall (level 2) — op 3 is "Steal-from" on most market stalls
        onOpLoc3(ThievingLocs.veg_stall) { stealFromStall(it.loc, Stalls.VEGETABLE) }
        onOpLoc3(ThievingLocs.veg_stall_4708) { stealFromStall(it.loc, Stalls.VEGETABLE) }
        onOpLoc3(ThievingLocs.veg_stall_54781) { stealFromStall(it.loc, Stalls.VEGETABLE) }
        // Baker's stall (level 5)
        onOpLoc3(ThievingLocs.bakers_stall) { stealFromStall(it.loc, Stalls.BAKERS) }
        onOpLoc3(ThievingLocs.bakers_stall_11730) { stealFromStall(it.loc, Stalls.BAKERS) }
        onOpLoc3(ThievingLocs.bakers_stall_51559) { stealFromStall(it.loc, Stalls.BAKERS) }
        onOpLoc3(ThievingLocs.bakers_stall_51937) { stealFromStall(it.loc, Stalls.BAKERS) }
        // Tea stall (level 5) — Varrock tea stall has a different op index
        onOpLoc2(ThievingLocs.tea_stall) { stealFromStall(it.loc, Stalls.TEA) }
        onOpLoc2(ThievingLocs.tea_stall_6574) { stealFromStall(it.loc, Stalls.TEA) }
        onOpLoc2(ThievingLocs.tea_stall_20350) { stealFromStall(it.loc, Stalls.TEA) }
        // Silk stall (level 20)
        onOpLoc3(ThievingLocs.silk_stall) { stealFromStall(it.loc, Stalls.SILK) }
        onOpLoc3(ThievingLocs.silk_stall_6568) { stealFromStall(it.loc, Stalls.SILK) }
        onOpLoc3(ThievingLocs.silk_stall_11729) { stealFromStall(it.loc, Stalls.SILK) }
        onOpLoc3(ThievingLocs.silk_stall_20344) { stealFromStall(it.loc, Stalls.SILK) }
        onOpLoc3(ThievingLocs.silk_stall_36569) { stealFromStall(it.loc, Stalls.SILK) }
        onOpLoc3(ThievingLocs.silk_stall_41755) { stealFromStall(it.loc, Stalls.SILK) }
        onOpLoc3(ThievingLocs.silk_stall_51933) { stealFromStall(it.loc, Stalls.SILK) }
        // Ardougne market stall level 22 (silk variant)
        onOpLoc3(ThievingLocs.market_stall_14011) { stealFromStall(it.loc, Stalls.ARDOUGNE_SILK) }
        // Seed stall (level 27)
        onOpLoc3(ThievingLocs.seed_stall) { stealFromStall(it.loc, Stalls.SEED) }
        onOpLoc3(ThievingLocs.seed_stall_7053) { stealFromStall(it.loc, Stalls.SEED) }
        onOpLoc3(ThievingLocs.seed_stall_33639) { stealFromStall(it.loc, Stalls.SEED) }
        // Fur stall (level 35)
        onOpLoc3(ThievingLocs.fur_stall) { stealFromStall(it.loc, Stalls.FUR) }
        onOpLoc3(ThievingLocs.fur_stall_4278) { stealFromStall(it.loc, Stalls.FUR) }
        onOpLoc3(ThievingLocs.fur_stall_6571) { stealFromStall(it.loc, Stalls.FUR) }
        onOpLoc3(ThievingLocs.fur_stall_11732) { stealFromStall(it.loc, Stalls.FUR) }
        onOpLoc3(ThievingLocs.fur_stall_20347) { stealFromStall(it.loc, Stalls.FUR) }
        onOpLoc3(ThievingLocs.fur_stall_37405) { stealFromStall(it.loc, Stalls.FUR) }
        onOpLoc3(ThievingLocs.fur_stall_51934) { stealFromStall(it.loc, Stalls.FUR) }
        // Fish stall (level 42)
        onOpLoc3(ThievingLocs.fish_stall) { stealFromStall(it.loc, Stalls.FISH) }
        onOpLoc3(ThievingLocs.fish_stall_4705) { stealFromStall(it.loc, Stalls.FISH) }
        onOpLoc3(ThievingLocs.fish_stall_4707) { stealFromStall(it.loc, Stalls.FISH) }
        onOpLoc3(ThievingLocs.fish_stall_31712) { stealFromStall(it.loc, Stalls.FISH) }
        onOpLoc3(ThievingLocs.fish_stall_37404) { stealFromStall(it.loc, Stalls.FISH) }
        // Silver stall (level 50)
        onOpLoc3(ThievingLocs.silver_stall) { stealFromStall(it.loc, Stalls.SILVER) }
        onOpLoc3(ThievingLocs.silver_stall_6164) { stealFromStall(it.loc, Stalls.SILVER) }
        onOpLoc3(ThievingLocs.silver_stall_11734) { stealFromStall(it.loc, Stalls.SILVER) }
        onOpLoc3(ThievingLocs.silver_stall_36570) { stealFromStall(it.loc, Stalls.SILVER) }
        onOpLoc3(ThievingLocs.silver_stall_41757) { stealFromStall(it.loc, Stalls.SILVER) }
        // Spice stall (level 65)
        onOpLoc3(ThievingLocs.spice_stall) { stealFromStall(it.loc, Stalls.SPICE) }
        onOpLoc3(ThievingLocs.spice_stall_6572) { stealFromStall(it.loc, Stalls.SPICE) }
        onOpLoc3(ThievingLocs.spice_stall_11733) { stealFromStall(it.loc, Stalls.SPICE) }
        onOpLoc3(ThievingLocs.spice_stall_36572) { stealFromStall(it.loc, Stalls.SPICE) }
        onOpLoc3(ThievingLocs.spice_stall_51936) { stealFromStall(it.loc, Stalls.SPICE) }
        // Gem stall (level 75)
        onOpLoc3(ThievingLocs.gem_stall) { stealFromStall(it.loc, Stalls.GEM) }
        onOpLoc3(ThievingLocs.gem_stall_6162) { stealFromStall(it.loc, Stalls.GEM) }
        onOpLoc3(ThievingLocs.gem_stall_6570) { stealFromStall(it.loc, Stalls.GEM) }
        onOpLoc3(ThievingLocs.gem_stall_11731) { stealFromStall(it.loc, Stalls.GEM) }
        onOpLoc3(ThievingLocs.gem_stall_20346) { stealFromStall(it.loc, Stalls.GEM) }
        onOpLoc3(ThievingLocs.gem_stall_36571) { stealFromStall(it.loc, Stalls.GEM) }
        onOpLoc3(ThievingLocs.gem_stall_41756) { stealFromStall(it.loc, Stalls.GEM) }
        onOpLoc3(ThievingLocs.gem_stall_51935) { stealFromStall(it.loc, Stalls.GEM) }
        onOpLoc3(ThievingLocs.gem_stall_54780) { stealFromStall(it.loc, Stalls.GEM) }

        // --------------- H.A.M. chest handlers ---------------
        onOpLoc4(ThievingLocs.chest_11735) { searchChest(it.loc, HamChests.LEVEL_13) }
        onOpLoc4(ThievingLocs.chest_11736) { searchChest(it.loc, HamChests.LEVEL_28) }
        onOpLoc4(ThievingLocs.chest_11737) { searchChest(it.loc, HamChests.LEVEL_43) }
        onOpLoc4(ThievingLocs.chest_11738) { searchChest(it.loc, HamChests.LEVEL_47) }
        onOpLoc4(ThievingLocs.chest_11739) { searchChest(it.loc, HamChests.LEVEL_59) }
        onOpLoc4(ThievingLocs.chest_11740) { searchChest(it.loc, HamChests.LEVEL_72) }
        onOpLoc4(ThievingLocs.chest_11741) { searchChest(it.loc, HamChests.LEVEL_84) }
    }

    // -----------------------------------------------------------------------
    //  Pickpocket
    // -----------------------------------------------------------------------

    private suspend fun ProtectedAccess.pickpocket(npc: Npc, entry: PickpocketEntry) {
        val level = player.thievingLvl

        if (level < entry.levelReq) {
            mes("You need a Thieving level of ${entry.levelReq} to pickpocket this target.")
            return
        }

        if (inv.isFull()) {
            mes("Your inventory is too full to pickpocket.")
            return
        }

        anim(ThievingSeqs.human_pickpocket)
        delay(2)

        val success = rollPickpocketSuccess(level, entry)

        if (success) {
            onPickpocketSuccess(npc, entry)
        } else {
            onPickpocketFailure(npc, entry)
        }
    }

    private fun rollPickpocketSuccess(level: Int, entry: PickpocketEntry): Boolean {
        val successChance = computePickpocketChance(level, entry)
        return random.randomDouble() <= successChance
    }

    /**
     * Compute success probability clamped to [0.05, 0.95].
     *
     * Uses Alter's linear formula: baseSuccess + max(0, level - levelReq) * bonusPerLevel. This
     * approximates the OSRS hidden-level formula closely enough for RSPS purposes.
     */
    private fun computePickpocketChance(level: Int, entry: PickpocketEntry): Double {
        val levelsAbove = (level - entry.levelReq).coerceAtLeast(0)
        val chance = entry.baseSuccess + levelsAbove * entry.bonusPerLevel
        return chance.coerceIn(0.05, 0.95)
    }

    private fun ProtectedAccess.onPickpocketSuccess(npc: Npc, entry: PickpocketEntry) {
        statAdvance(stats.thieving, entry.xp)

        val loot = rollLoot(entry.loot)
        val amount = if (loot.min == loot.max) loot.min else random.of(loot.min, loot.max)
        invAdd(inv, loot.obj, amount)

        mes("You pick the ${npc.name.lowercase()}'s pocket.")
    }

    private suspend fun ProtectedAccess.onPickpocketFailure(npc: Npc, entry: PickpocketEntry) {
        mes("You fail to pick the ${npc.name.lowercase()}'s pocket.")
        npc.say("Hands off!")

        val stunDamage =
            if (entry.stunDamageMin == entry.stunDamageMax) {
                entry.stunDamageMin
            } else {
                random.of(entry.stunDamageMin, entry.stunDamageMax)
            }

        // Queue the typeless stun hit before delaying so it lands on the first stun tick.
        if (stunDamage > 0) {
            queueHit(delay = 0, type = HitType.Typeless, damage = stunDamage)
        }

        // Stun: the protected-access delay holds the player locked for the stun duration.
        if (entry.stunTicks > 0) {
            delay(entry.stunTicks)
        }
    }

    private fun <T : LootEntry> rollLoot(table: List<T>): T {
        if (table.size == 1) return table.first()
        val totalWeight = table.sumOf { it.weight }
        val roll = random.randomDouble() * totalWeight
        var cumulative = 0.0
        for (entry in table) {
            cumulative += entry.weight
            if (roll < cumulative) return entry
        }
        return table.last()
    }

    // -----------------------------------------------------------------------
    //  Stall thieving
    // -----------------------------------------------------------------------

    private suspend fun ProtectedAccess.stealFromStall(loc: BoundLocInfo, stall: StallEntry) {
        val level = player.thievingLvl

        if (level < stall.levelReq) {
            mes("You need a Thieving level of ${stall.levelReq} to steal from this stall.")
            return
        }

        if (inv.isFull()) {
            mes("Your inventory is too full to steal from this stall.")
            return
        }

        anim(ThievingSeqs.human_thieving_stall)
        delay(2)

        statAdvance(stats.thieving, stall.xp)

        val loot = rollLoot(stall.loot)
        val amount = if (loot.min == loot.max) loot.min else random.of(loot.min, loot.max)
        invAdd(inv, loot.obj, amount)

        mes("You steal from the stall.")

        // Replace the full stall with an empty placeholder for respawnTicks, then restore.
        locRepo.change(loc, stall.emptyLoc, stall.respawnTicks)
    }

    // -----------------------------------------------------------------------
    //  Chest thieving (H.A.M. dungeon)
    // -----------------------------------------------------------------------

    private suspend fun ProtectedAccess.searchChest(loc: BoundLocInfo, chest: ChestEntry) {
        val level = player.thievingLvl

        if (level < chest.levelReq) {
            mes("You need a Thieving level of ${chest.levelReq} to search this chest.")
            return
        }

        if (inv.isFull()) {
            mes("Your inventory is too full to loot this chest.")
            return
        }

        anim(ThievingSeqs.human_thieving_stall)
        delay(2)

        // Roll trap: if the chest has a trap and it fires, stun the player instead of rewarding.
        if (chest.trapMaxDamage > 0) {
            val dismantled = random.of(1, 100) <= chest.dismantleChance
            if (!dismantled) {
                val trapDamage =
                    if (chest.trapMinDamage == chest.trapMaxDamage) {
                        chest.trapMinDamage
                    } else {
                        random.of(chest.trapMinDamage, chest.trapMaxDamage)
                    }
                if (trapDamage > 0) {
                    queueHit(delay = 0, type = HitType.Typeless, damage = trapDamage)
                }
                mes("The trap springs and hits you!")
                return
            }
        }

        statAdvance(stats.thieving, chest.xp)

        for (loot in chest.loot) {
            val amount = if (loot.min == loot.max) loot.min else random.of(loot.min, loot.max)
            invAdd(inv, loot.obj, amount)
        }

        mes("You search the chest and find something.")

        locRepo.change(loc, ThievingLocs.chest_open_11743, chest.respawnTicks)
    }

    // -----------------------------------------------------------------------
    //  Data structures
    // -----------------------------------------------------------------------

    private interface LootEntry {
        val weight: Double
        val min: Int
        val max: Int
    }

    private data class PickpocketLoot(
        val obj: org.rsmod.game.type.obj.ObjType,
        override val min: Int,
        override val max: Int,
        override val weight: Double,
    ) : LootEntry

    private data class StallLoot(
        val obj: org.rsmod.game.type.obj.ObjType,
        override val min: Int = 1,
        override val max: Int = min,
        override val weight: Double = 1.0,
    ) : LootEntry

    private data class ChestLoot(
        val obj: org.rsmod.game.type.obj.ObjType,
        override val min: Int,
        override val max: Int,
        override val weight: Double = 1.0,
    ) : LootEntry

    private data class PickpocketEntry(
        val levelReq: Int,
        val xp: Double,
        val baseSuccess: Double,
        val bonusPerLevel: Double,
        val stunTicks: Int,
        val stunDamageMin: Int,
        val stunDamageMax: Int,
        val loot: List<PickpocketLoot>,
    )

    private data class StallEntry(
        val levelReq: Int,
        val xp: Double,
        val emptyLoc: LocType,
        val respawnTicks: Int,
        val loot: List<StallLoot>,
    )

    private data class ChestEntry(
        val levelReq: Int,
        val xp: Double,
        val respawnTicks: Int,
        val dismantleChance: Int,
        val trapMinDamage: Int,
        val trapMaxDamage: Int,
        val loot: List<ChestLoot>,
    )

    // -----------------------------------------------------------------------
    //  Pickpocket tables  (source: Alter pickpockets.json + OSRS wiki)
    // -----------------------------------------------------------------------

    private object Pickpockets {
        val MAN_WOMAN =
            PickpocketEntry(
                levelReq = 1,
                xp = 8.0,
                baseSuccess = 0.55,
                bonusPerLevel = 0.015,
                stunTicks = 7,
                stunDamageMin = 1,
                stunDamageMax = 1,
                loot =
                    listOf(
                        PickpocketLoot(ThievingObjs.coins, 3, 12, 75.0),
                        PickpocketLoot(ThievingObjs.bronze_bolts, 1, 2, 25.0),
                    ),
            )

        val FARMER =
            PickpocketEntry(
                levelReq = 10,
                xp = 14.5,
                baseSuccess = 0.52,
                bonusPerLevel = 0.012,
                stunTicks = 7,
                stunDamageMin = 1,
                stunDamageMax = 2,
                loot =
                    listOf(
                        PickpocketLoot(ThievingObjs.coins, 9, 30, 75.0),
                        PickpocketLoot(ThievingObjs.potato_seed, 1, 1, 25.0),
                    ),
            )

        val HAM_MEMBER =
            PickpocketEntry(
                levelReq = 15,
                xp = 18.5,
                baseSuccess = 0.48,
                bonusPerLevel = 0.01,
                stunTicks = 6,
                stunDamageMin = 1,
                stunDamageMax = 3,
                loot =
                    listOf(
                        PickpocketLoot(ThievingObjs.coins, 20, 40, 60.0),
                        PickpocketLoot(ThievingObjs.buttons, 1, 1, 20.0),
                        PickpocketLoot(ThievingObjs.rusty_sword, 1, 1, 20.0),
                    ),
            )

        val HAM_GUARD =
            PickpocketEntry(
                levelReq = 20,
                xp = 22.5,
                baseSuccess = 0.46,
                bonusPerLevel = 0.009,
                stunTicks = 6,
                stunDamageMin = 1,
                stunDamageMax = 3,
                loot =
                    listOf(
                        PickpocketLoot(ThievingObjs.coins, 25, 45, 60.0),
                        PickpocketLoot(ThievingObjs.iron_knife, 1, 1, 20.0),
                        PickpocketLoot(ThievingObjs.leather_gloves, 1, 1, 20.0),
                    ),
            )

        val AL_KHARID_WARRIOR =
            PickpocketEntry(
                levelReq = 25,
                xp = 26.0,
                baseSuccess = 0.45,
                bonusPerLevel = 0.009,
                stunTicks = 7,
                stunDamageMin = 1,
                stunDamageMax = 2,
                loot =
                    listOf(
                        PickpocketLoot(ThievingObjs.coins, 18, 35, 80.0),
                        PickpocketLoot(ThievingObjs.bronze_dagger, 1, 1, 20.0),
                    ),
            )

        val ROGUE =
            PickpocketEntry(
                levelReq = 32,
                xp = 35.5,
                baseSuccess = 0.42,
                bonusPerLevel = 0.009,
                stunTicks = 7,
                stunDamageMin = 1,
                stunDamageMax = 2,
                loot =
                    listOf(
                        PickpocketLoot(ThievingObjs.coins, 25, 50, 75.0),
                        PickpocketLoot(ThievingObjs.lockpick, 1, 1, 25.0),
                    ),
            )

        val CAVE_GOBLIN =
            PickpocketEntry(
                levelReq = 36,
                xp = 40.0,
                baseSuccess = 0.43,
                bonusPerLevel = 0.008,
                stunTicks = 7,
                stunDamageMin = 1,
                stunDamageMax = 1,
                loot =
                    listOf(
                        PickpocketLoot(ThievingObjs.coins, 12, 48, 75.0),
                        PickpocketLoot(ThievingObjs.iron_ore, 1, 2, 25.0),
                    ),
            )

        val MASTER_FARMER =
            PickpocketEntry(
                levelReq = 38,
                xp = 43.0,
                baseSuccess = 0.35,
                bonusPerLevel = 0.008,
                stunTicks = 8,
                stunDamageMin = 1,
                stunDamageMax = 3,
                loot =
                    listOf(
                        PickpocketLoot(ThievingObjs.potato_seed, 1, 1, 50.0),
                        PickpocketLoot(ThievingObjs.strawberry_seed, 1, 1, 25.0),
                        PickpocketLoot(ThievingObjs.ranarr_seed, 1, 1, 25.0),
                    ),
            )

        val GUARD =
            PickpocketEntry(
                levelReq = 40,
                xp = 46.8,
                baseSuccess = 0.38,
                bonusPerLevel = 0.008,
                stunTicks = 8,
                stunDamageMin = 1,
                stunDamageMax = 2,
                loot = listOf(PickpocketLoot(ThievingObjs.coins, 30, 60, 100.0)),
            )

        val KNIGHT_OF_ARDOUGNE =
            PickpocketEntry(
                levelReq = 55,
                xp = 84.3,
                baseSuccess = 0.34,
                bonusPerLevel = 0.007,
                stunTicks = 9,
                stunDamageMin = 2,
                stunDamageMax = 4,
                loot =
                    listOf(
                        PickpocketLoot(ThievingObjs.coins, 50, 90, 80.0),
                        PickpocketLoot(ThievingObjs.law_rune, 1, 2, 20.0),
                    ),
            )

        val MENAPHITE_THUG =
            PickpocketEntry(
                levelReq = 65,
                xp = 137.5,
                baseSuccess = 0.32,
                bonusPerLevel = 0.007,
                stunTicks = 8,
                stunDamageMin = 2,
                stunDamageMax = 5,
                loot =
                    listOf(
                        PickpocketLoot(ThievingObjs.coins, 40, 80, 80.0),
                        PickpocketLoot(ThievingObjs.earth_rune, 4, 8, 20.0),
                    ),
            )

        val PALADIN =
            PickpocketEntry(
                levelReq = 70,
                xp = 151.75,
                baseSuccess = 0.30,
                bonusPerLevel = 0.006,
                stunTicks = 9,
                stunDamageMin = 2,
                stunDamageMax = 3,
                loot =
                    listOf(
                        PickpocketLoot(ThievingObjs.coins, 80, 160, 80.0),
                        PickpocketLoot(ThievingObjs.chaos_rune, 1, 2, 20.0),
                    ),
            )

        val HERO =
            PickpocketEntry(
                levelReq = 80,
                xp = 275.0,
                baseSuccess = 0.26,
                bonusPerLevel = 0.0055,
                stunTicks = 10,
                stunDamageMin = 3,
                stunDamageMax = 4,
                loot =
                    listOf(
                        PickpocketLoot(ThievingObjs.coins, 200, 300, 60.0),
                        PickpocketLoot(ThievingObjs.blood_rune, 2, 3, 20.0),
                        PickpocketLoot(ThievingObjs.ruby, 1, 1, 20.0),
                    ),
            )
    }

    // -----------------------------------------------------------------------
    //  Stall tables  (source: Alter stalls.json + OSRS wiki)
    // -----------------------------------------------------------------------

    private object Stalls {
        val VEGETABLE =
            StallEntry(
                levelReq = 2,
                xp = 10.0,
                emptyLoc = ThievingLocs.market_stall_empty,
                respawnTicks = 14,
                loot =
                    listOf(
                        StallLoot(ThievingObjs.potato, weight = 30.0),
                        StallLoot(ThievingObjs.onion, weight = 25.0),
                        StallLoot(ThievingObjs.cabbage, weight = 25.0),
                        StallLoot(ThievingObjs.tomato, weight = 15.0),
                        StallLoot(ThievingObjs.garlic, weight = 5.0),
                    ),
            )

        val BAKERS =
            StallEntry(
                levelReq = 5,
                xp = 16.0,
                emptyLoc = ThievingLocs.market_stall_empty,
                respawnTicks = 14,
                loot =
                    listOf(
                        StallLoot(ThievingObjs.cake, weight = 35.0),
                        StallLoot(ThievingObjs.bread, weight = 35.0),
                        StallLoot(ThievingObjs.chocolate_slice, weight = 30.0),
                    ),
            )

        val TEA =
            StallEntry(
                levelReq = 5,
                xp = 16.0,
                emptyLoc = ThievingLocs.market_stall_empty,
                respawnTicks = 14,
                loot = listOf(StallLoot(ThievingObjs.cup_of_tea)),
            )

        val SILK =
            StallEntry(
                levelReq = 20,
                xp = 24.0,
                emptyLoc = ThievingLocs.market_stall_empty,
                respawnTicks = 16,
                loot = listOf(StallLoot(ThievingObjs.silk)),
            )

        // Ardougne market silk variant (level 22)
        val ARDOUGNE_SILK =
            StallEntry(
                levelReq = 22,
                xp = 27.0,
                emptyLoc = ThievingLocs.market_stall_empty,
                respawnTicks = 16,
                loot = listOf(StallLoot(ThievingObjs.silk)),
            )

        val SEED =
            StallEntry(
                levelReq = 27,
                xp = 10.0,
                emptyLoc = ThievingLocs.market_stall_empty,
                respawnTicks = 16,
                loot =
                    listOf(
                        StallLoot(ThievingObjs.potato_seed, weight = 20.0),
                        StallLoot(ThievingObjs.onion_seed, weight = 18.0),
                        StallLoot(ThievingObjs.cabbage_seed, weight = 15.0),
                        StallLoot(ThievingObjs.tomato_seed, weight = 12.0),
                        StallLoot(ThievingObjs.sweetcorn_seed, weight = 10.0),
                        StallLoot(ThievingObjs.strawberry_seed, weight = 8.0),
                        StallLoot(ThievingObjs.watermelon_seed, weight = 6.0),
                        StallLoot(ThievingObjs.snape_grass_seed, weight = 5.0),
                        StallLoot(ThievingObjs.guam_seed, weight = 4.0),
                        StallLoot(ThievingObjs.marrentill_seed, weight = 3.0),
                        StallLoot(ThievingObjs.harralander_seed, weight = 3.0),
                        StallLoot(ThievingObjs.jute_seed, weight = 3.0),
                        StallLoot(ThievingObjs.apple_tree_seed, weight = 1.5),
                        StallLoot(ThievingObjs.banana_tree_seed, weight = 1.5),
                    ),
            )

        val FUR =
            StallEntry(
                levelReq = 35,
                xp = 36.0,
                emptyLoc = ThievingLocs.market_stall_empty,
                respawnTicks = 18,
                loot =
                    listOf(
                        StallLoot(ThievingObjs.fur, weight = 40.0),
                        StallLoot(ThievingObjs.bear_fur, weight = 35.0),
                        StallLoot(ThievingObjs.grey_wolf_fur, weight = 25.0),
                    ),
            )

        val FISH =
            StallEntry(
                levelReq = 42,
                xp = 42.0,
                emptyLoc = ThievingLocs.market_stall_empty,
                respawnTicks = 18,
                loot =
                    listOf(
                        StallLoot(ThievingObjs.raw_trout, weight = 30.0),
                        StallLoot(ThievingObjs.raw_salmon, weight = 30.0),
                        StallLoot(ThievingObjs.raw_tuna, weight = 25.0),
                        StallLoot(ThievingObjs.raw_lobster, weight = 15.0),
                    ),
            )

        val SILVER =
            StallEntry(
                levelReq = 50,
                xp = 54.0,
                emptyLoc = ThievingLocs.market_stall_empty,
                respawnTicks = 20,
                loot =
                    listOf(
                        StallLoot(ThievingObjs.silver_ore, weight = 75.0),
                        StallLoot(ThievingObjs.silver_bar, weight = 25.0),
                    ),
            )

        val SPICE =
            StallEntry(
                levelReq = 65,
                xp = 81.0,
                emptyLoc = ThievingLocs.market_stall_empty,
                respawnTicks = 22,
                loot = listOf(StallLoot(ThievingObjs.spice, min = 1, max = 2)),
            )

        val GEM =
            StallEntry(
                levelReq = 75,
                xp = 160.0,
                emptyLoc = ThievingLocs.market_stall_empty,
                respawnTicks = 24,
                loot =
                    listOf(
                        StallLoot(ThievingObjs.uncut_sapphire, weight = 40.0),
                        StallLoot(ThievingObjs.uncut_emerald, weight = 30.0),
                        StallLoot(ThievingObjs.uncut_ruby, weight = 20.0),
                        StallLoot(ThievingObjs.uncut_diamond, weight = 9.0),
                        StallLoot(ThievingObjs.uncut_dragonstone, weight = 1.0),
                    ),
            )
    }

    // -----------------------------------------------------------------------
    //  H.A.M. chest tables  (source: Alter chests.json + OSRS wiki)
    // -----------------------------------------------------------------------

    private object HamChests {
        val LEVEL_13 =
            ChestEntry(
                levelReq = 13,
                xp = 7.5,
                respawnTicks = 12,
                dismantleChance = 70,
                trapMinDamage = 1,
                trapMaxDamage = 3,
                loot = listOf(ChestLoot(ThievingObjs.coins, 10, 10)),
            )

        val LEVEL_28 =
            ChestEntry(
                levelReq = 28,
                xp = 25.0,
                respawnTicks = 12,
                dismantleChance = 60,
                trapMinDamage = 2,
                trapMaxDamage = 4,
                loot = listOf(ChestLoot(ThievingObjs.nature_rune, 3, 3)),
            )

        val LEVEL_43 =
            ChestEntry(
                levelReq = 43,
                xp = 125.0,
                respawnTicks = 16,
                dismantleChance = 55,
                trapMinDamage = 3,
                trapMaxDamage = 5,
                loot = listOf(ChestLoot(ThievingObjs.coins, 50, 50)),
            )

        val LEVEL_47 =
            ChestEntry(
                levelReq = 47,
                xp = 150.0,
                respawnTicks = 16,
                dismantleChance = 55,
                trapMinDamage = 3,
                trapMaxDamage = 6,
                loot = listOf(ChestLoot(ThievingObjs.steel_arrowtips, 10, 10)),
            )

        val LEVEL_59 =
            ChestEntry(
                levelReq = 59,
                xp = 250.0,
                respawnTicks = 20,
                dismantleChance = 45,
                trapMinDamage = 4,
                trapMaxDamage = 8,
                loot = listOf(ChestLoot(ThievingObjs.blood_rune, 2, 2)),
            )

        val LEVEL_72 =
            ChestEntry(
                levelReq = 72,
                xp = 500.0,
                respawnTicks = 24,
                dismantleChance = 40,
                trapMinDamage = 4,
                trapMaxDamage = 10,
                loot = listOf(ChestLoot(ThievingObjs.coins, 500, 500)),
            )

        val LEVEL_84 =
            ChestEntry(
                levelReq = 84,
                xp = 500.0,
                respawnTicks = 24,
                dismantleChance = 35,
                trapMinDamage = 5,
                trapMaxDamage = 10,
                loot = listOf(ChestLoot(ThievingObjs.blood_rune, 4, 4)),
            )
    }
}

// -----------------------------------------------------------------------
//  Local NPC references
// -----------------------------------------------------------------------

internal object ThievingNpcs : NpcReferences() {
    // Man / Woman — multiple IDs from Alter pickpockets.json
    val man = find("man")
    val man2 = find("man2")
    val man3 = find("man3")
    val woman = find("woman")
    val woman2 = find("woman2")
    val woman3 = find("woman3")
    val farmer = find("farmer")
    val ham_member = find("ham_member")
    val ham_guard = find("ham_guard")
    val al_kharid_warrior = find("al_kharid_warrior")
    val rogue = find("rogue")
    val cave_goblin = find("cave_goblin")
    val master_farmer = find("master_farmer")
    val guard = find("guard")
    val knight_of_ardougne = find("knight_of_ardougne")
    val menaphite_thug = find("menaphite_thug")
    val paladin = find("paladin")
    val hero = find("hero")
}

// -----------------------------------------------------------------------
//  Local Loc references  (stalls + chests + their empty/open variants)
// -----------------------------------------------------------------------

internal object ThievingLocs : LocReferences() {
    // Generic empty stall placeholder (shared by most Ardougne/Keldagrim market stalls)
    val market_stall_empty = find("market_stall")

    // Vegetable stall variants
    val veg_stall = find("veg_stall")
    val veg_stall_4708 = find("veg_stall_4708")
    val veg_stall_54781 = find("veg_stall_54781")

    // Baker's stall variants
    val bakers_stall = find("bakers_stall")
    val bakers_stall_11730 = find("bakers_stall_11730")
    val bakers_stall_51559 = find("bakers_stall_51559")
    val bakers_stall_51937 = find("bakers_stall_51937")

    // Tea stall variants (Varrock)
    val tea_stall = find("tea_stall")
    val tea_stall_6574 = find("tea_stall_6574")
    val tea_stall_20350 = find("tea_stall_20350")

    // Silk stall variants
    val silk_stall = find("silk_stall")
    val silk_stall_6568 = find("silk_stall_6568")
    val silk_stall_11729 = find("silk_stall_11729")
    val silk_stall_20344 = find("silk_stall_20344")
    val silk_stall_36569 = find("silk_stall_36569")
    val silk_stall_41755 = find("silk_stall_41755")
    val silk_stall_51933 = find("silk_stall_51933")

    // Ardougne market stall (level 22 silk variant)
    val market_stall_14011 = find("market_stall_14011")

    // Seed stall variants
    val seed_stall = find("seed_stall")
    val seed_stall_7053 = find("seed_stall_7053")
    val seed_stall_33639 = find("seed_stall_33639")

    // Fur stall variants
    val fur_stall = find("fur_stall")
    val fur_stall_4278 = find("fur_stall_4278")
    val fur_stall_6571 = find("fur_stall_6571")
    val fur_stall_11732 = find("fur_stall_11732")
    val fur_stall_20347 = find("fur_stall_20347")
    val fur_stall_37405 = find("fur_stall_37405")
    val fur_stall_51934 = find("fur_stall_51934")

    // Fish stall variants
    val fish_stall = find("fish_stall")
    val fish_stall_4705 = find("fish_stall_4705")
    val fish_stall_4707 = find("fish_stall_4707")
    val fish_stall_31712 = find("fish_stall_31712")
    val fish_stall_37404 = find("fish_stall_37404")

    // Silver stall variants
    val silver_stall = find("silver_stall")
    val silver_stall_6164 = find("silver_stall_6164")
    val silver_stall_11734 = find("silver_stall_11734")
    val silver_stall_36570 = find("silver_stall_36570")
    val silver_stall_41757 = find("silver_stall_41757")

    // Spice stall variants
    val spice_stall = find("spice_stall")
    val spice_stall_6572 = find("spice_stall_6572")
    val spice_stall_11733 = find("spice_stall_11733")
    val spice_stall_36572 = find("spice_stall_36572")
    val spice_stall_51936 = find("spice_stall_51936")

    // Gem stall variants
    val gem_stall = find("gem_stall")
    val gem_stall_6162 = find("gem_stall_6162")
    val gem_stall_6570 = find("gem_stall_6570")
    val gem_stall_11731 = find("gem_stall_11731")
    val gem_stall_20346 = find("gem_stall_20346")
    val gem_stall_36571 = find("gem_stall_36571")
    val gem_stall_41756 = find("gem_stall_41756")
    val gem_stall_51935 = find("gem_stall_51935")
    val gem_stall_54780 = find("gem_stall_54780")

    // H.A.M. chests (closed) and shared open variant
    val chest_11735 = find("chest_11735")
    val chest_11736 = find("chest_11736")
    val chest_11737 = find("chest_11737")
    val chest_11738 = find("chest_11738")
    val chest_11739 = find("chest_11739")
    val chest_11740 = find("chest_11740")
    val chest_11741 = find("chest_11741")
    val chest_open_11743 = find("null_11743")
}

// -----------------------------------------------------------------------
//  Local Seq references
// -----------------------------------------------------------------------

internal object ThievingSeqs : SeqReferences() {
    // Animation played by the player when pickpocketing an NPC (OSRS anim ID 881)
    val human_pickpocket = find("human_pickpocket")
    // Animation played by the player when stealing from a stall / searching a chest (OSRS anim ID
    // 881)
    val human_thieving_stall = find("human_thieving_stall")
}

// -----------------------------------------------------------------------
//  Local Obj references  (loot items not already in BaseObjs)
// -----------------------------------------------------------------------

internal object ThievingObjs : ObjReferences() {
    // Common
    val coins = find("coins")
    val bronze_bolts = find("bronze_bolts")
    val potato_seed = find("potato_seed")
    val buttons = find("buttons")
    val rusty_sword = find("rusty_sword")
    val iron_knife = find("iron_knife")
    val leather_gloves = find("leather_gloves")
    val bronze_dagger = find("bronze_dagger")
    val lockpick = find("lockpick")
    val iron_ore = find("iron_ore")
    val strawberry_seed = find("strawberry_seed")
    val ranarr_seed = find("ranarr_seed")
    val law_rune = find("law_rune")
    val earth_rune = find("earth_rune")
    val chaos_rune = find("chaos_rune")
    val blood_rune = find("blood_rune")
    val ruby = find("ruby")
    val nature_rune = find("nature_rune")
    val steel_arrowtips = find("steel_arrowtips")

    // Stall loot
    val potato = find("potato")
    val onion = find("onion")
    val cabbage = find("cabbage")
    val tomato = find("tomato")
    val garlic = find("garlic")
    val cake = find("cake")
    val bread = find("bread")
    val chocolate_slice = find("chocolate_slice")
    val cup_of_tea = find("cup_of_tea")
    val silk = find("silk")
    val onion_seed = find("onion_seed")
    val cabbage_seed = find("cabbage_seed")
    val tomato_seed = find("tomato_seed")
    val sweetcorn_seed = find("sweetcorn_seed")
    val watermelon_seed = find("watermelon_seed")
    val snape_grass_seed = find("snape_grass_seed")
    val guam_seed = find("guam_seed")
    val marrentill_seed = find("marrentill_seed")
    val harralander_seed = find("harralander_seed")
    val jute_seed = find("jute_seed")
    val apple_tree_seed = find("apple_tree_seed")
    val banana_tree_seed = find("banana_tree_seed")
    val fur = find("fur")
    val bear_fur = find("bear_fur")
    val grey_wolf_fur = find("grey_wolf_fur")
    val raw_trout = find("raw_trout")
    val raw_salmon = find("raw_salmon")
    val raw_tuna = find("raw_tuna")
    val raw_lobster = find("raw_lobster")
    val silver_ore = find("silver_ore")
    val silver_bar = find("silver_bar")
    val spice = find("spice")
    val uncut_sapphire = find("uncut_sapphire")
    val uncut_emerald = find("uncut_emerald")
    val uncut_ruby = find("uncut_ruby")
    val uncut_diamond = find("uncut_diamond")
    val uncut_dragonstone = find("uncut_dragonstone")
}
