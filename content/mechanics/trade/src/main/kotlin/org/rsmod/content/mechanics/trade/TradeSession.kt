package org.rsmod.content.mechanics.trade

import org.rsmod.game.entity.Player

public data class TradeSession(
    val player1: Player,
    val player2: Player,
    var stage: TradeStage = TradeStage.Offer,
    var player1Accepted: Boolean = false,
    var player2Accepted: Boolean = false,
) {
    public fun other(player: Player): Player? =
        when (player) {
            player1 -> player2
            player2 -> player1
            else -> null
        }

    public fun isAccepted(player: Player): Boolean =
        when (player) {
            player1 -> player1Accepted
            player2 -> player2Accepted
            else -> false
        }

    public fun setAccepted(player: Player, accepted: Boolean) {
        if (player == player1) player1Accepted = accepted else player2Accepted = accepted
    }

    public fun resetAccepted() {
        player1Accepted = false
        player2Accepted = false
    }

    public fun bothAccepted(): Boolean = player1Accepted && player2Accepted
}

public enum class TradeStage {
    Offer,
    Confirm,
}
