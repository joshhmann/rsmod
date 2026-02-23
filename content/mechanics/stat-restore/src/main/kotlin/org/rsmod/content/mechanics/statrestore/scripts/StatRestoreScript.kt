package org.rsmod.content.mechanics.statrestore.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.timers
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.stat.stat
import org.rsmod.api.player.stat.statAdd
import org.rsmod.api.player.stat.statBase
import org.rsmod.api.player.stat.statSub
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.script.onPlayerLogin
import org.rsmod.api.script.onPlayerSoftTimer
import org.rsmod.game.entity.Player
import org.rsmod.game.type.stat.StatTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Stat restoration and HP regeneration — OSRS wiki-accurate.
 *
 * ## Stat Restoration
 * Drained/boosted stats tick back toward base by 1 point every [RESTORE_INTERVAL] = 100 ticks
 * (60 seconds). The Rapid Restore prayer halves the interval to 50 ticks (30 seconds) for all
 * skills except Hitpoints.
 *
 * ## HP Regeneration
 * Hitpoints regenerate 1 per [HP_REGEN_INTERVAL] = 100 ticks. The Rapid Heal prayer halves
 * the interval to 50 ticks. Tracked via a separate [timers.health_regen] soft-timer.
 */
class StatRestoreScript @Inject constructor(private val statTypes: StatTypeList) : PluginScript() {

    private val Player.rapidRestore by boolVarBit(varbits.rapid_restore)
    private val Player.rapidHeal by boolVarBit(varbits.rapid_heal)

    override fun ScriptContext.startup() {
        onPlayerLogin {
            player.softTimer(timers.stat_boost_restore, RESTORE_INTERVAL)
            player.softTimer(timers.health_regen, HP_REGEN_INTERVAL)
        }
        onPlayerSoftTimer(timers.stat_boost_restore) { player.statRestoreTick() }
        onPlayerSoftTimer(timers.health_regen) { player.hpRegenTick() }
    }

    private fun Player.statRestoreTick() {
        for (statType in statTypes.values) {
            if (statType == stats.hitpoints) continue // HP handled by health_regen timer
            val base = statBase(statType)
            val current = stat(statType)
            when {
                current < base -> statAdd(statType, constant = 1, percent = 0)
                current > base -> statSub(statType, constant = 1, percent = 0)
            }
        }
        val interval = if (rapidRestore) RESTORE_INTERVAL / 2 else RESTORE_INTERVAL
        softTimer(timers.stat_boost_restore, interval)
    }

    private fun Player.hpRegenTick() {
        val hpBase = statBase(stats.hitpoints)
        val hpCurrent = stat(stats.hitpoints)
        if (hpCurrent < hpBase) {
            statAdd(stats.hitpoints, constant = 1, percent = 0)
        }
        val interval = if (rapidHeal) HP_REGEN_INTERVAL / 2 else HP_REGEN_INTERVAL
        softTimer(timers.health_regen, interval)
    }

    companion object {
        /** Ticks between each stat restoration step (100 ticks = 60 seconds). */
        const val RESTORE_INTERVAL = 100

        /** Ticks between each HP regeneration step (100 ticks = 60 seconds). */
        const val HP_REGEN_INTERVAL = 100
    }
}
