package org.rsmod.content.skills.prayer.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.game.process.GameLifecycle
import org.rsmod.api.player.stat.stat
import org.rsmod.api.player.stat.statAdd
import org.rsmod.api.player.stat.statBase
import org.rsmod.api.player.stat.statSub
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// ---------------------------------------------------------------------------
// F2P Prayer Definitions
// ---------------------------------------------------------------------------

private data class PrayerDef(
    val index: Int,
    val name: String,
    val level: Int,
    val drainRate: Double,
    val varbit: VarBitType,
)

/**
 * All F2P prayers with level requirements, drain rates (points/minute), and
 * their corresponding varbit references for reading/writing activation state.
 *
 * Drain rate reference (100 game ticks = 60 seconds = 1 minute):
 *   0.2  -> 1 point per 500 ticks (~5 min)
 *   0.4  -> 1 point per 250 ticks (~2.5 min)
 *   0.5  -> 1 point per 200 ticks (~2 min)
 *   1.0  -> 1 point per 100 ticks (~1 min)
 *   2.0  -> 1 point per 50 ticks (~30 sec)
 *   3.0  -> 1 point per 33 ticks (~20 sec)
 */
private val F2P_PRAYERS: List<PrayerDef> by lazy {
    listOf(
        PrayerDef(0, "Thick Skin", 1, 0.5, varbits.prayer_thickskin),
        PrayerDef(1, "Burst of Strength", 4, 0.5, varbits.prayer_burstofstrength),
        PrayerDef(2, "Clarity of Thought", 7, 0.5, varbits.prayer_clarityofthought),
        PrayerDef(3, "Rock Skin", 10, 1.0, varbits.prayer_rockskin),
        PrayerDef(4, "Superhuman Strength", 13, 1.0, varbits.prayer_superhumanstrength),
        PrayerDef(5, "Improved Reflexes", 16, 1.0, varbits.prayer_improvedreflexes),
        PrayerDef(6, "Rapid Restore", 19, 0.2, varbits.prayer_rapidrestore),
        PrayerDef(7, "Rapid Heal", 22, 0.4, varbits.prayer_rapidheal),
        PrayerDef(8, "Protect Item", 25, 0.5, varbits.prayer_protectitem),
        PrayerDef(9, "Steel Skin", 28, 2.0, varbits.prayer_steelskin),
        PrayerDef(10, "Ultimate Strength", 31, 2.0, varbits.prayer_ultimatestrength),
        PrayerDef(11, "Incredible Reflexes", 34, 2.0, varbits.prayer_incrediblereflexes),
        PrayerDef(12, "Protect from Magic", 37, 3.0, varbits.prayer_protectfrommagic),
        PrayerDef(13, "Protect from Missiles", 40, 3.0, varbits.prayer_protectfrommissiles),
        PrayerDef(14, "Protect from Melee", 43, 3.0, varbits.prayer_protectfrommelee),
    )
}

// ---------------------------------------------------------------------------
// Plugin
// ---------------------------------------------------------------------------

/**
 * F2P Prayer activation/drain/regen system for the rsmod rev 233 server.
 *
 * ## Lifecycle (every game tick via LateCycle):
 * 1. Read active prayers from varp bits (set by client via prayer interface)
 * 2. Calculate total drain rate from active prayers
 * 3. Accumulate fractional drain; when >= 1.0, subtract 1 prayer point
 * 4. If prayer points reach 0, deactivate all prayers and clear varp bits
 * 5. If no prayers active, apply slow passive regen
 * 6. Apply Rapid Restore / Rapid Heal periodic effects
 *
 * ## Protection prayers (PROTECT_FROM_MAGIC / MISSILES / MELEE):
 * Damage reduction is handled by the engine's StandardPlayerHitModifier,
 * which reads the varbit activation state from the player's vars. No
 * additional hooking is needed from this module.
 *
 * ## Combat integration:
 * The stat-boosting effects (Thick Skin -> Steel Skin -> etc.) are applied
 * client-side based on the varp bits, matching OSRS behavior.
 */
class PrayerBook
@Inject
constructor(
    private val players: PlayerList,
    private val mapClock: MapClock,
) : PluginScript() {

    /** Per-player tick counters indexed by player name. */
    private val tickCounters = java.util.concurrent.ConcurrentHashMap<String, Int>()

    /** Per-player drain accumulator indexed by player name. */
    private val drainAccum = java.util.concurrent.ConcurrentHashMap<String, Double>()

    override fun ScriptContext.startup() {
        onEvent<GameLifecycle.LateCycle> { processPrayerCycle() }
    }

    // -----------------------------------------------------------------------
    // Cycle logic
    // -----------------------------------------------------------------------

    private fun processPrayerCycle() {
        val tick = mapClock.cycle
        players.forEach { player ->
            processPlayerTick(player, tick)
        }
    }

    private fun processPlayerTick(player: Player, tick: Int) {
        val key = player.username ?: return
        val activePrayers = getActivePrayers(player)

        if (activePrayers.isNotEmpty()) {
            drainPlayer(player, key, activePrayers)

            // Periodic effects: check tick counter (every 50 ticks)
            val counter = tickCounters.getOrPut(key) { 0 }
            val newCounter = (counter + 1) % 50
            tickCounters[key] = newCounter

            if (newCounter == 0) {
                if (isActive(player, varbits.prayer_rapidrestore)) {
                    applyRapidRestore(player)
                }
                if (isActive(player, varbits.prayer_rapidheal)) {
                    applyRapidHeal(player)
                }
            }
        } else {
            regenPlayer(player, key)
        }
    }

    // -----------------------------------------------------------------------
    // Drain system
    // -----------------------------------------------------------------------

    private fun drainPlayer(player: Player, key: String, prayers: List<PrayerDef>) {
        val totalDrain = prayers.sumOf { it.drainRate }
        var acc = drainAccum.getOrElse(key) { 0.0 }

        // Add this tick's contribution (drainRate points per minute = per 100 ticks)
        acc += totalDrain / 100.0

        if (acc >= 1.0) {
            val current = player.stat(stats.prayer)
            if (current <= 0) {
                deactivateAll(player, key)
                return
            }

            val drain = minOf(acc.toInt(), current)
            acc -= drain
            player.statSub(stats.prayer, drain, 0)

            // Check for prayer depletion
            if (player.stat(stats.prayer) <= 0) {
                deactivateAll(player, key)
            }
        }

        drainAccum[key] = acc
    }

    /**
     * Deactivates all prayers by clearing their varp bits.
     * Clears per-player state.
     */
    private fun deactivateAll(player: Player, key: String) {
        for (prayer in F2P_PRAYERS) {
            player.vars[prayer.varbit] = 0
        }
        drainAccum.remove(key)
        tickCounters.remove(key)
    }

    // -----------------------------------------------------------------------
    // Passive regen
    // -----------------------------------------------------------------------

    /**
     * Passive prayer point regeneration when no prayers are active.
     * Rate: 1 point per 150 ticks (~90 seconds).
     * Only restores if below maximum prayer level.
     */
    private fun regenPlayer(player: Player, key: String) {
        val current = player.stat(stats.prayer)
        val max = player.statBase(stats.prayer)
        if (current >= max) {
            tickCounters.remove(key)
            drainAccum.remove(key)
            return
        }

        val tick = tickCounters.getOrPut(key) { 0 }
        val newTick = tick + 1
        tickCounters[key] = newTick

        if (newTick >= 150) {
            player.statAdd(stats.prayer, 1, 0)
            tickCounters[key] = 0
        }
    }

    // -----------------------------------------------------------------------
    // Prayer state queries
    // -----------------------------------------------------------------------

    private fun getActivePrayers(player: Player): List<PrayerDef> {
        return F2P_PRAYERS.filter { player.vars[it.varbit] == 1 }
    }

    private fun isActive(player: Player, varbit: VarBitType): Boolean {
        return player.vars[varbit] == 1
    }

    // -----------------------------------------------------------------------
    // Rapid Restore / Rapid Heal
    // -----------------------------------------------------------------------

    /**
     * Rapid Restore (level 19): Restores 1 point of all drained
     * combat stats every 50 ticks (~30s). In OSRS this is 3x faster than
     * the natural 1-point-per-100-ticks rate.
     */
    private fun applyRapidRestore(player: Player) {
        val restoreTargets = listOf(
            stats.attack,
            stats.strength,
            stats.defence,
            stats.ranged,
            stats.magic,
        )
        for (stat in restoreTargets) {
            val cur = player.stat(stat)
            val base = player.statBase(stat)
            if (cur < base) {
                player.statAdd(stat, 1, 0)
            }
        }
    }

    /**
     * Rapid Heal (level 22): Heals 1 hitpoint every 50 ticks (~30s)
     * when active.
     */
    private fun applyRapidHeal(player: Player) {
        val cur = player.stat(stats.hitpoints)
        val max = player.statBase(stats.hitpoints)
        if (cur < max) {
            player.statAdd(stats.hitpoints, 1, 0)
        }
    }
}
