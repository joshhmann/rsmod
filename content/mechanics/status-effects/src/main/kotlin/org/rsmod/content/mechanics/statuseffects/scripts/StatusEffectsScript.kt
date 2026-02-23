package org.rsmod.content.mechanics.statuseffects.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.walktriggers
import org.rsmod.api.player.clearInteractionRoute
import org.rsmod.api.player.output.mes
import org.rsmod.api.script.onPlayerLogout
import org.rsmod.api.script.onPlayerSoftTimer
import org.rsmod.api.script.onPlayerWalkTrigger
import org.rsmod.api.type.refs.timer.TimerReferences
import org.rsmod.content.mechanics.statuseffects.StatusEffectController
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

private object StatusEffectTimers : TimerReferences() {
    val map_clock = find("map_clock")
}

class StatusEffectsScript @Inject constructor(private val statusEffects: StatusEffectController) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerSoftTimer(StatusEffectTimers.map_clock) {
            val player = player
            statusEffects.processTick(player)
            if (statusEffects.isFrozen(player)) {
                player.walkTrigger(walktriggers.frozen)
            }
            if (statusEffects.isStunned(player)) {
                player.walkTrigger(walktriggers.stunned)
            }
        }

        onPlayerWalkTrigger(walktriggers.frozen) {
            player.clearInteractionRoute()
            if (statusEffects.shouldShowFrozenMessage(player)) {
                player.mes("A magical force prevents you from moving.")
            }
        }

        onPlayerWalkTrigger(walktriggers.stunned) {
            player.clearInteractionRoute()
            if (statusEffects.shouldShowStunnedMessage(player)) {
                player.mes("You're stunned and can't move.")
            }
        }

        onPlayerLogout { statusEffects.cleanup(player) }
    }
}
