package org.rsmod.content.skills.mining.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.locParam
import org.rsmod.api.config.locXpParam
import org.rsmod.api.config.objParam
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.righthand
import org.rsmod.api.player.stat.miningLvl
import org.rsmod.api.random.GameRandom
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.InvObj
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// TODO:
// - Gem rock random table (opal, jade, red topaz, sapphire, emerald, ruby, diamond)
// - Prospector outfit XP bonus
// - Infernal pickaxe smelting effect
// - Dragon pickaxe special attack ore doubling
// - Mining guild invisible +7 Mining level boost (add InvisibleLevelMod + WoodcuttingModule analog)
// - Mining gloves depletion reduction (silver 33%, coal 50%, gold 33%, mithril 33%, etc.)
// - Geode clue scroll drops (1/250)
// - Unidentified minerals in the Mining Guild
class Mining
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val locRepo: LocRepository,
    private val xpMods: XpModifiers,
    private val invisibleLvls: InvisibleLevels,
    private val mapClock: MapClock,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(content.ore) { mine(it.loc, it.type) }
    }

    private fun ProtectedAccess.mine(rock: BoundLocInfo, type: UnpackedLocType) {
        val pickaxe = findPickaxe(player, objTypes)
        if (pickaxe == null) {
            mes("You need a pickaxe to mine this rock.")
            mes("You do not have a pickaxe which you have the Mining level to use.")
            soundSynth(synths.pillory_wrong)
            return
        }

        if (player.miningLvl < type.rockLevelReq) {
            mes("You need a Mining level of ${type.rockLevelReq} to mine this rock.")
            soundSynth(synths.pillory_wrong)
            return
        }

        if (inv.isFull()) {
            val product = objTypes[type.rockOre]
            mes("Your inventory is too full to hold any more ${product.name.lowercase()}.")
            soundSynth(synths.pillory_wrong)
            return
        }

        // First click: set up the action delay and queue the loop.
        if (actionDelay < mapClock) {
            actionDelay = mapClock + 3
            skillAnimDelay = mapClock + 3
            spam("You swing your pickaxe at the rock.")
            opLoc1(rock)
            return
        }

        // Refresh animation every ~4 ticks.
        if (skillAnimDelay <= mapClock) {
            skillAnimDelay = mapClock + 4
            anim(objTypes[pickaxe].pickaxeMiningAnim)
        }

        var minedOre = false

        if (actionDelay == mapClock) {
            // Success roll — attempt to mine ore this tick.
            val (low, high) = mineSuccessRate(type, objTypes[pickaxe])
            minedOre = statRandom(stats.mining, low, high, invisibleLvls)
            // Advance actionDelay so next roll happens in 3 ticks.
            actionDelay = mapClock + 3
        }

        // Roll for rock depletion: deplete_chance is stored as 0–255 (255 = always depletes).
        val depletes = minedOre && random.of(1, 255) <= type.rockDepleteChance

        if (minedOre) {
            val product = objTypes[type.rockOre]
            val xp = type.rockXp * xpMods.get(player, stats.mining)
            spam("You manage to mine some ${product.name.lowercase()}.")
            statAdvance(stats.mining, xp)
            invAdd(inv, product)
        }

        if (depletes) {
            val respawnTime = type.resolveRespawnTime(random)
            locRepo.change(rock, type.rockDepletedLoc, respawnTime)
            resetAnim()
            return
        }

        // Rock still up — check inv space before re-queuing.
        if (inv.isFull()) {
            val product = objTypes[type.rockOre]
            mes("Your inventory is too full to hold any more ${product.name.lowercase()}.")
            soundSynth(synths.pillory_wrong)
            resetAnim()
            return
        }

        opLoc1(rock)
    }

    companion object {
        // --- Loc param extensions (delegate properties on UnpackedLocType) ---

        val UnpackedLocType.rockLevelReq: Int by locParam(params.levelrequire)
        val UnpackedLocType.rockOre: ObjType by locParam(params.skill_productitem)
        val UnpackedLocType.rockXp: Double by locXpParam(params.skill_xp)
        val UnpackedLocType.rockDepletedLoc: LocType by locParam(params.next_loc_stage)

        /**
         * Packed as 0–255 in the cache param [params.deplete_chance]. A value of 255 means the rock
         * always depletes after one ore; lower values mean it is less likely to deplete (harder
         * rocks like runite).
         */
        val UnpackedLocType.rockDepleteChance: Int by locParam(params.deplete_chance)
        val UnpackedLocType.rockRespawnTime: Int by locParam(params.respawn_time)
        val UnpackedLocType.rockRespawnTimeLow: Int by locParam(params.respawn_time_low)
        val UnpackedLocType.rockRespawnTimeHigh: Int by locParam(params.respawn_time_high)

        // --- Obj param extensions ---

        val UnpackedObjType.pickaxeMiningReq: Int by objParam(params.levelrequire)
        val UnpackedObjType.pickaxeMiningAnim: SeqType by objParam(params.skill_anim)

        // --- Pickaxe selection ---

        /**
         * Returns the best usable pickaxe available to [player] — the highest-tier pickaxe whose
         * level requirement the player meets. The weapon slot is checked alongside inventory; if
         * both contain a valid pickaxe the one with the higher level requirement wins.
         */
        fun findPickaxe(player: Player, objTypes: ObjTypeList): InvObj? {
            val worn = player.wornPickaxe(objTypes)
            val carried = player.carriedPickaxe(objTypes)
            return when {
                worn != null && carried != null ->
                    if (objTypes[worn].pickaxeMiningReq >= objTypes[carried].pickaxeMiningReq) worn
                    else carried
                else -> worn ?: carried
            }
        }

        private fun Player.wornPickaxe(objTypes: ObjTypeList): InvObj? {
            val righthand = righthand ?: return null
            return righthand.takeIf { objTypes[it].isUsablePickaxe(miningLvl) }
        }

        private fun Player.carriedPickaxe(objTypes: ObjTypeList): InvObj? {
            return inv.filterNotNull { objTypes[it].isUsablePickaxe(miningLvl) }
                .maxByOrNull { objTypes[it].pickaxeMiningReq }
        }

        private fun UnpackedObjType.isUsablePickaxe(miningLevel: Int): Boolean =
            isContentType(content.mining_pickaxe) && miningLevel >= pickaxeMiningReq

        // --- Success rate ---

        /**
         * Returns a (low, high) pair for [statRandom], approximating the OSRS mining success
         * formula:
         *
         * successChance = clamp( (effectiveLevel - rockReq + 1 + pickaxeBonus) / rockDifficulty,
         * 0.0, 0.95 )
         *
         * Because RSMod's [statRandom] uses SkillingSuccessRate (which linearly interpolates
         * between `low/256` at level 1 and `high/256` at level 99), we map the OSRS formula onto
         * that [low, high] range using the pickaxe's relative tier bonus and the rock's
         * deplete_chance param as a stand-in for difficulty (higher deplete_chance = easier rock).
         *
         * If a full enum-table approach (analogous to woodcutting_axe_success_rates) is desired in
         * the future, create a MiningRates.kt following WoodcuttingRates.kt and thread it through
         * via a custom [params] entry.
         *
         * @param type The ore rock being mined.
         * @param pickaxe The [UnpackedObjType] of the pickaxe in use.
         */
        fun mineSuccessRate(type: UnpackedLocType, pickaxe: UnpackedObjType): Pair<Int, Int> {
            // Treat deplete_chance as an approximate difficulty proxy:
            //   255 → always depletes → easiest rock (copper/tin); allows high success.
            //   low  → rarely depletes → hard rock (runite); low success chance.
            val difficulty = type.rockDepleteChance.coerceIn(1, 255)
            // Normalise pickaxe tier into a [5..42] bonus factor.
            val bonus = pickaxe.pickaxeTierBonus()
            val low = (bonus * difficulty / 512).coerceIn(1, 64)
            val high = ((bonus + 24) * difficulty / 384).coerceIn(low + 1, 255)
            return low to high
        }

        /**
         * Returns a tier bonus for this pickaxe, matching the Kronos Pickaxe.points values which in
         * turn reflect OSRS's pickaxe "mining speed" tier:
         *
         * bronze/iron → 5 / 9 steel / black → 14 / 21 mithril / adamant → 26 / 30 rune / gilded →
         * 36 dragon and above → 42 crystal → 50 (slightly faster than dragon per OSRS)
         */
        private fun UnpackedObjType.pickaxeTierBonus(): Int {
            // Use the pickaxeMiningReq to map to tier bonus so we don't hard-code item IDs here.
            return when {
                pickaxeMiningReq >= 71 -> 50 // crystal
                pickaxeMiningReq >= 61 -> 42 // dragon and equivalents
                pickaxeMiningReq >= 41 -> 36 // rune / gilded
                pickaxeMiningReq >= 31 -> 30 // adamant
                pickaxeMiningReq >= 21 -> 26 // mithril
                pickaxeMiningReq >= 11 -> 21 // black
                pickaxeMiningReq >= 6 -> 14 // steel
                pickaxeMiningReq >= 1 -> 9 // iron (also bronze at lvl 1 gets 5, but 9 is safe)
                else -> 5 // bronze fallback
            }
        }

        private fun UnpackedLocType.resolveRespawnTime(random: GameRandom): Int {
            val fixed = rockRespawnTime
            if (fixed > 0) return fixed
            val low = rockRespawnTimeLow
            val high = rockRespawnTimeHigh
            if (low > 0 && high > 0) return random.of(low, high)
            // Fallback respawn times derived from Kronos Rock data (in ticks at 600 ms/tick):
            //   copper/tin/clay: 74160 ms ≈ ~2 min ≈ 200 ticks
            //   coal: 29064 ms ≈ ~50 ticks
            //   gold: ~50 ticks
            //   mithril: ~75 ticks
            //   adamant: ~16 ticks per depleted rock (game usually uses 150 ticks)
            //   runite: ~12 ticks average (game typically 3–5 min ≈ 300–500 ticks)
            return 50
        }
    }
}
