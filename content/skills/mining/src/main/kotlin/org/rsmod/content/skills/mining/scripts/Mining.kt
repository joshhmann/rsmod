package org.rsmod.content.skills.mining.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.locParam
import org.rsmod.api.config.locXpParam
import org.rsmod.api.config.objParam
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.righthand
import org.rsmod.api.player.stat.miningLvl
import org.rsmod.api.random.GameRandom
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.api.type.refs.obj.ObjReferences
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
// - Prospector outfit XP bonus
// - Infernal pickaxe smelting effect
// - Dragon pickaxe special attack ore doubling
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
    private val random: GameRandom,
    private val objRepo: ObjRepository,
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
            val productName = resolveOreProductName(type)
            mes("Your inventory is too full to hold any more $productName.")
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
            val product = resolveOreProduct(type)
            val xp = type.rockXp * xpMods.get(player, stats.mining)
            spam("You manage to mine some ${objTypes[product].name.lowercase()}.")
            statAdvance(stats.mining, xp)
            invAddOrDrop(objRepo, product)
        }

        if (depletes) {
            val respawnTime = type.resolveRespawnTime(random)
            locRepo.change(rock, type.rockDepletedLoc, respawnTime)
            resetAnim()
            return
        }

        // Rock still up — check inv space before re-queuing.
        if (inv.isFull()) {
            val productName = resolveOreProductName(type)
            mes("Your inventory is too full to hold any more $productName.")
            soundSynth(synths.pillory_wrong)
            resetAnim()
            return
        }

        opLoc1(rock)
    }

    // --- Gem rock handling ---

    /**
     * Resolves the ore product for a rock. For gem rocks, rolls on the gem drop table. For regular
     * rocks, returns the configured product item.
     */
    private fun ProtectedAccess.resolveOreProduct(type: UnpackedLocType): ObjType {
        return if (type.isGemRock()) {
            rollGemDrop()
        } else {
            objTypes[type.rockOre]
        }
    }

    /** Returns the product name for inventory full messages. */
    private fun ProtectedAccess.resolveOreProductName(type: UnpackedLocType): String {
        return if (type.isGemRock()) {
            "gems"
        } else {
            objTypes[type.rockOre].name.lowercase()
        }
    }

    /**
     * Checks if the rock is a gem rock based on its loc type. Gem rocks are identified by their
     * content group being [content.ore] and having uncut_sapphire as their configured product (the
     * placeholder for gem rocks).
     */
    private fun UnpackedLocType.isGemRock(): Boolean {
        // Check if this is a gem rock by looking at the configured product
        // Gem rocks have uncut_sapphire as their placeholder product
        return rockOre == objs.uncut_sapphire && rockLevelReq == 40
    }

    /**
     * Rolls on the gem rock drop table.
     *
     * Gem rock drop rates (OSRS):
     * - Opal: ~46.8% (60/128)
     * - Jade: ~22.7% (29/128)
     * - Red topaz: ~14.8% (19/128)
     * - Sapphire: ~7.0% (9/128)
     * - Emerald: ~4.7% (6/128)
     * - Ruby: ~3.1% (4/128)
     * - Diamond: ~0.8% (1/128)
     */
    private fun ProtectedAccess.rollGemDrop(): ObjType {
        val roll = random.of(1, 128)
        return when {
            roll <= 60 -> MiningGemObjs.uncut_opal // 60/128 = 46.9%
            roll <= 89 -> MiningGemObjs.uncut_jade // 29/128 = 22.7%
            roll <= 108 -> MiningGemObjs.uncut_red_topaz // 19/128 = 14.8%
            roll <= 117 -> objs.uncut_sapphire // 9/128 = 7.0%
            roll <= 123 -> objs.uncut_emerald // 6/128 = 4.7%
            roll <= 127 -> objs.uncut_ruby // 4/128 = 3.1%
            else -> objs.uncut_diamond // 1/128 = 0.8%
        }
    }

    /** Local obj references for gems not in [objs] (BaseObjs). */
    internal object MiningGemObjs : ObjReferences() {
        val uncut_opal = find("uncut_opal")
        val uncut_jade = find("uncut_jade")
        val uncut_red_topaz = find("uncut_red_topaz")
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
