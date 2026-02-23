package org.rsmod.content.skills.farming.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.farmingLvl
import org.rsmod.api.random.GameRandom
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLocU
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.content.skills.farming.scripts.configs.farming_locs
import org.rsmod.content.skills.farming.scripts.configs.farming_objs
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

private data class HerbDef(
    val seed: ObjType,
    val produce: ObjType,
    val levelReq: Int,
    val plantXp: Double,
    val harvestXp: Double,
    val seedlingLoc: LocType,
    val stage1Loc: LocType,
    val stage2Loc: LocType,
    val stage3Loc: LocType,
    val fullyGrownLoc: LocType,
)

private data class PatchState(
    val herb: HerbDef,
    var stage: Int,
    var diseased: Boolean,
    var dead: Boolean,
)

class Farming
@Inject
constructor(
    private val random: GameRandom,
    private val locRepo: LocRepository,
    private val xpMods: XpModifiers,
) : PluginScript() {
    private val patchStates = mutableMapOf<CoordGrid, PatchState>()

    override fun ScriptContext.startup() {
        registerWeeding()
        registerPlanting()
        registerCuring()
        registerDeadPatchClear()
        registerHarvesting()
        registerPatchMessages()
    }

    private fun ScriptContext.registerWeeding() {
        val weeds =
            listOf(
                farming_locs.herb_patch_weeds_3,
                farming_locs.herb_patch_weeds_2,
                farming_locs.herb_patch_weeds_1,
            )
        for (weed in weeds) {
            onOpLoc1(weed) { weedPatch(it.loc, rakeFromOp1 = true) }
            onOpLocU(weed, farming_objs.rake) { weedPatch(it.loc, rakeFromOp1 = false) }
        }
    }

    private fun ScriptContext.registerPlanting() {
        for (herb in HERBS) {
            onOpLocU(farming_locs.herb_patch_weeded, herb.seed) { plantHerb(it.loc, herb) }
        }
    }

    private fun ScriptContext.registerCuring() {
        onOpLocU(farming_locs.herb_1_diseased, farming_objs.plant_cure) { curePatch(it.loc) }
    }

    private fun ScriptContext.registerDeadPatchClear() {
        onOpLoc1(farming_locs.herb_1_dead) { clearDeadPatch(it.loc, usingItem = false) }
        onOpLocU(farming_locs.herb_1_dead, objs.spade) { clearDeadPatch(it.loc, usingItem = true) }
    }

    private fun ScriptContext.registerHarvesting() {
        for (herb in HERBS) {
            onOpLoc1(herb.fullyGrownLoc) { harvest(it.loc, herb) }
        }
    }

    private fun ScriptContext.registerPatchMessages() {
        onOpLoc1(farming_locs.herb_patch_weeded) {
            mes("This herb patch has been weeded and is ready for planting.")
        }
        for (herb in HERBS) {
            onOpLoc1(herb.seedlingLoc) { mes("Your herb has only just been planted.") }
            onOpLoc1(herb.stage1Loc) { mes("Your herb is growing.") }
            onOpLoc1(herb.stage2Loc) { mes("Your herb is growing well.") }
            onOpLoc1(herb.stage3Loc) { mes("Your herb is almost fully grown.") }
        }
        onOpLoc1(farming_locs.herb_1_diseased) {
            mes("This patch is diseased. Use plant cure quickly or it will die.")
        }
    }

    private fun ProtectedAccess.weedPatch(loc: BoundLocInfo, rakeFromOp1: Boolean) {
        if (!inv.contains(farming_objs.rake)) {
            mes("You need a rake to clear this patch.")
            return
        }
        val next =
            when (loc.id) {
                farming_locs.herb_patch_weeds_3.id -> farming_locs.herb_patch_weeds_2
                farming_locs.herb_patch_weeds_2.id -> farming_locs.herb_patch_weeds_1
                farming_locs.herb_patch_weeds_1.id -> farming_locs.herb_patch_weeded
                else -> null
            }
        if (next == null) {
            mes("Nothing interesting happens.")
            return
        }
        locRepo.change(loc, next, Int.MAX_VALUE)
        if (next == farming_locs.herb_patch_weeded) {
            patchStates.remove(loc.coords)
            mes("You rake the patch clean.")
        } else if (rakeFromOp1) {
            mes("You clear some weeds from the patch.")
        }
    }

    private suspend fun ProtectedAccess.plantHerb(patch: BoundLocInfo, herb: HerbDef) {
        if (player.farmingLvl < herb.levelReq) {
            mes("You need a Farming level of ${herb.levelReq} to plant this seed.")
            return
        }
        if (!inv.contains(farming_objs.dibber)) {
            mes("You need a seed dibber to plant this seed.")
            return
        }
        val removed = invDel(inv, herb.seed, count = 1, strict = true).success
        if (!removed) {
            mes("You don't have any seeds to plant.")
            return
        }
        val xp = herb.plantXp * xpMods.get(player, stats.farming)
        statAdvance(stats.farming, xp)
        mes("You plant the seed in the herb patch.")
        locRepo.change(patch, herb.seedlingLoc, Int.MAX_VALUE)
        patchStates[patch.coords] =
            PatchState(herb = herb, stage = 0, diseased = false, dead = false)
        progressGrowth(patch.coords)
    }

    private suspend fun ProtectedAccess.progressGrowth(coords: CoordGrid) {
        advanceToStage(coords, stage = 1)
        advanceToStage(coords, stage = 2)
        advanceToStage(coords, stage = 3)
        val state = patchStates[coords] ?: return
        if (state.dead || state.diseased || state.stage != 3) {
            return
        }
        delay(GROWTH_STAGE_TICKS)
        promote(coords, state.herb.stage3Loc, state.herb.fullyGrownLoc)
    }

    private suspend fun ProtectedAccess.advanceToStage(coords: CoordGrid, stage: Int) {
        val state = patchStates[coords] ?: return
        if (state.dead || state.diseased) {
            return
        }
        val from = stateLocFor(state.herb, state.stage)
        val into = stateLocFor(state.herb, stage)
        delay(GROWTH_STAGE_TICKS)
        if (!promote(coords, from, into)) {
            return
        }
        state.stage = stage
        rollDisease(coords, into)
    }

    private suspend fun ProtectedAccess.rollDisease(coords: CoordGrid, currentLoc: LocType) {
        if (random.of(1, 100) > DISEASE_CHANCE_PERCENT) {
            return
        }
        val state = patchStates[coords] ?: return
        if (state.dead || state.diseased) {
            return
        }
        val current = locRepo.findExact(coords, currentLoc) ?: return
        locRepo.change(current, farming_locs.herb_1_diseased, Int.MAX_VALUE)
        state.diseased = true
        delay(DISEASE_TO_DEATH_TICKS)
        val diseased = locRepo.findExact(coords, farming_locs.herb_1_diseased) ?: return
        locRepo.change(diseased, farming_locs.herb_1_dead, Int.MAX_VALUE)
        state.dead = true
    }

    private suspend fun ProtectedAccess.curePatch(patch: BoundLocInfo) {
        val state = patchStates[patch.coords]
        if (state == null || state.dead) {
            mes("This patch has nothing to cure.")
            return
        }
        val removed = invDel(inv, farming_objs.plant_cure, count = 1, strict = true).success
        if (!removed) {
            mes("You need plant cure to treat this patch.")
            return
        }
        state.diseased = false
        state.stage = 3
        locRepo.change(patch, state.herb.stage3Loc, Int.MAX_VALUE)
        mes("You cure the patch.")
        delay(GROWTH_STAGE_TICKS)
        val stage3 = locRepo.findExact(patch.coords, state.herb.stage3Loc) ?: return
        locRepo.change(stage3, state.herb.fullyGrownLoc, Int.MAX_VALUE)
    }

    private fun ProtectedAccess.clearDeadPatch(deadPatch: BoundLocInfo, usingItem: Boolean) {
        if (!usingItem && !inv.contains(objs.spade)) {
            mes("This patch is dead. You need a spade to clear it.")
            return
        }
        if (usingItem) {
            mes("You clear away the dead crop.")
        } else {
            mes("This patch is dead. Use a spade to clear it.")
            return
        }
        locRepo.del(deadPatch, Int.MAX_VALUE)
        patchStates.remove(deadPatch.coords)
    }

    private fun ProtectedAccess.harvest(patch: BoundLocInfo, herb: HerbDef) {
        if (inv.isFull()) {
            mes("Your inventory is too full to harvest this patch.")
            return
        }
        val harvestCount = random.of(HARVEST_MIN, HARVEST_MAX)
        var harvested = 0
        repeat(harvestCount) {
            if (inv.isFull()) return@repeat
            invAdd(inv, herb.produce)
            harvested++
        }
        if (harvested == 0) {
            mes("You have no room to harvest this patch.")
            return
        }
        val xp = herb.harvestXp * harvested * xpMods.get(player, stats.farming)
        statAdvance(stats.farming, xp)
        mes("You harvest $harvested herbs from the patch.")
        locRepo.del(patch, Int.MAX_VALUE)
        patchStates.remove(patch.coords)
    }

    private fun promote(coords: CoordGrid, from: LocType, into: LocType): Boolean {
        val current = locRepo.findExact(coords, from) ?: return false
        locRepo.change(current, into, Int.MAX_VALUE)
        return true
    }

    private fun stateLocFor(herb: HerbDef, stage: Int): LocType =
        when (stage) {
            0 -> herb.seedlingLoc
            1 -> herb.stage1Loc
            2 -> herb.stage2Loc
            3 -> herb.stage3Loc
            else -> herb.fullyGrownLoc
        }

    private companion object {
        private const val GROWTH_STAGE_TICKS = 30
        private const val DISEASE_TO_DEATH_TICKS = 30
        private const val DISEASE_CHANCE_PERCENT = 18

        private const val HARVEST_MIN = 2
        private const val HARVEST_MAX = 4

        private val HERBS: List<HerbDef> =
            listOf(
                HerbDef(
                    seed = farming_objs.guam_seed,
                    produce = objs.grimy_guam,
                    levelReq = 9,
                    plantXp = 11.0,
                    harvestXp = 12.5,
                    seedlingLoc = farming_locs.herb_guam_leaf_seed,
                    stage1Loc = farming_locs.herb_guam_leaf_1,
                    stage2Loc = farming_locs.herb_guam_leaf_2,
                    stage3Loc = farming_locs.herb_guam_leaf_3,
                    fullyGrownLoc = farming_locs.herb_guam_leaf_fullygrown,
                ),
                HerbDef(
                    seed = farming_objs.marrentill_seed,
                    produce = objs.grimy_marrentill,
                    levelReq = 14,
                    plantXp = 13.5,
                    harvestXp = 15.0,
                    seedlingLoc = farming_locs.herb_marrentill_seed,
                    stage1Loc = farming_locs.herb_marrentill_1,
                    stage2Loc = farming_locs.herb_marrentill_2,
                    stage3Loc = farming_locs.herb_marrentill_3,
                    fullyGrownLoc = farming_locs.herb_marrentill_fullygrown,
                ),
            )
    }
}
