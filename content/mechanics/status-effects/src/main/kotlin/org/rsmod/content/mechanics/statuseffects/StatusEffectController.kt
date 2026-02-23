package org.rsmod.content.mechanics.statuseffects

import jakarta.inject.Singleton
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player

@Singleton
class StatusEffectController {
    private val states = mutableMapOf<Int, PlayerStatusState>()
    private val pending = mutableMapOf<Int, MutableList<PendingEffect>>()

    fun queueFreeze(
        source: ProtectedAccess,
        target: PathingEntity,
        hitDelay: Int,
        duration: Int,
        immunity: Int = FREEZE_IMMUNITY_TICKS,
    ) {
        queueEffect(source, target, hitDelay, duration, immunity, EffectType.Freeze)
    }

    fun queueStun(
        source: ProtectedAccess,
        target: PathingEntity,
        hitDelay: Int,
        duration: Int,
        immunity: Int = STUN_IMMUNITY_TICKS,
    ) {
        queueEffect(source, target, hitDelay, duration, immunity, EffectType.Stun)
    }

    fun processTick(player: Player) {
        val slot = player.slotId
        if (slot == INVALID_SLOT) {
            return
        }

        val state = states.getOrPut(slot) { PlayerStatusState() }
        val pend = pending[slot]
        if (pend != null) {
            applyPending(player, state, pend)
            if (pend.isEmpty()) {
                pending.remove(slot)
            }
        }
    }

    fun cleanup(player: Player) {
        val slot = player.slotId
        states.remove(slot)
        pending.remove(slot)
    }

    fun isFrozen(player: Player): Boolean {
        val state = states[player.slotId] ?: return false
        return state.freezeUntil > player.currentMapClock
    }

    fun isStunned(player: Player): Boolean {
        val state = states[player.slotId] ?: return false
        return state.stunUntil > player.currentMapClock
    }

    fun shouldShowFrozenMessage(player: Player): Boolean =
        shouldShowMessage(player, MessageType.Frozen)

    fun shouldShowStunnedMessage(player: Player): Boolean =
        shouldShowMessage(player, MessageType.Stunned)

    private fun queueEffect(
        source: ProtectedAccess,
        target: PathingEntity,
        hitDelay: Int,
        duration: Int,
        immunity: Int,
        effectType: EffectType,
    ) {
        if (duration <= 0) {
            return
        }
        val targetPlayer = target as? Player ?: return
        val slot = targetPlayer.slotId
        if (slot == INVALID_SLOT) {
            return
        }

        val applyTick = source.player.currentMapClock + hitDelay
        val entry = PendingEffect(effectType, applyTick, duration, immunity)
        pending.getOrPut(slot, ::mutableListOf).add(entry)
    }

    private fun applyPending(
        player: Player,
        state: PlayerStatusState,
        pend: MutableList<PendingEffect>,
    ) {
        val mapClock = player.currentMapClock
        val iterator = pend.iterator()
        while (iterator.hasNext()) {
            val effect = iterator.next()
            if (effect.applyTick > mapClock) {
                continue
            }
            iterator.remove()
            when (effect.type) {
                EffectType.Freeze -> applyFreeze(player, state, mapClock, effect)
                EffectType.Stun -> applyStun(player, state, mapClock, effect)
            }
        }
    }

    private fun applyFreeze(
        player: Player,
        state: PlayerStatusState,
        mapClock: Int,
        effect: PendingEffect,
    ) {
        if (mapClock < state.freezeImmuneUntil) {
            return
        }
        val freezeEnd = mapClock + effect.duration
        if (freezeEnd > state.freezeUntil) {
            state.freezeUntil = freezeEnd
        }
        val immuneUntil = freezeEnd + effect.immunity
        if (immuneUntil > state.freezeImmuneUntil) {
            state.freezeImmuneUntil = immuneUntil
        }
        player.abortRoute()
    }

    private fun applyStun(
        player: Player,
        state: PlayerStatusState,
        mapClock: Int,
        effect: PendingEffect,
    ) {
        if (mapClock < state.stunImmuneUntil) {
            return
        }
        val stunEnd = mapClock + effect.duration
        if (stunEnd > state.stunUntil) {
            state.stunUntil = stunEnd
        }
        val immuneUntil = stunEnd + effect.immunity
        if (immuneUntil > state.stunImmuneUntil) {
            state.stunImmuneUntil = immuneUntil
        }

        val currentDelay = player.delay
        if (currentDelay < stunEnd) {
            player.delay(stunEnd - mapClock)
        }
        player.abortRoute()
    }

    private fun shouldShowMessage(player: Player, type: MessageType): Boolean {
        val state = states[player.slotId] ?: return true
        val mapClock = player.currentMapClock
        val next =
            when (type) {
                MessageType.Frozen -> state.nextFrozenMessage
                MessageType.Stunned -> state.nextStunnedMessage
            }
        if (mapClock < next) {
            return false
        }
        when (type) {
            MessageType.Frozen -> state.nextFrozenMessage = mapClock + MESSAGE_COOLDOWN
            MessageType.Stunned -> state.nextStunnedMessage = mapClock + MESSAGE_COOLDOWN
        }
        return true
    }

    private data class PendingEffect(
        val type: EffectType,
        val applyTick: Int,
        val duration: Int,
        val immunity: Int,
    )

    private data class PlayerStatusState(
        var freezeUntil: Int = Int.MIN_VALUE,
        var freezeImmuneUntil: Int = Int.MIN_VALUE,
        var stunUntil: Int = Int.MIN_VALUE,
        var stunImmuneUntil: Int = Int.MIN_VALUE,
        var nextFrozenMessage: Int = Int.MIN_VALUE,
        var nextStunnedMessage: Int = Int.MIN_VALUE,
    )

    private enum class EffectType {
        Freeze,
        Stun,
    }

    private enum class MessageType {
        Frozen,
        Stunned,
    }

    companion object {
        const val FREEZE_IMMUNITY_TICKS = 5
        const val STUN_IMMUNITY_TICKS = 5
        private const val MESSAGE_COOLDOWN = 2
        private const val INVALID_SLOT = -1
    }
}
