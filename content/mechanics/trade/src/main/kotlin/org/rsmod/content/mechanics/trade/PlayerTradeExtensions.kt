package org.rsmod.content.mechanics.trade

import java.util.WeakHashMap
import org.rsmod.game.entity.Player

private val tradeSessions = WeakHashMap<Player, TradeSession>()
private val tradeRequests = WeakHashMap<Player, Player>()

public var Player.tradeSession: TradeSession?
    get() = tradeSessions[this]
    set(value) {
        if (value == null) {
            tradeSessions.remove(this)
        } else {
            tradeSessions[this] = value
        }
    }

public fun Player.isTrading(): Boolean = tradeSession != null

public fun Player.isTradingWith(other: Player): Boolean = tradeSession?.other(this) == other

public fun Player.linkTradeSession(other: Player, session: TradeSession) {
    this.tradeSession = session
    other.tradeSession = session
}

public fun Player.clearTradeSession(other: Player? = null) {
    tradeSession = null
    if (other != null) {
        other.tradeSession = null
    }
}

public fun Player.setTradeRequest(target: Player) {
    tradeRequests[this] = target
}

public fun Player.consumeTradeRequestFrom(source: Player): Boolean {
    val requested = tradeRequests[source]
    if (requested == this) {
        tradeRequests.remove(source)
        return true
    }
    return false
}

public fun Player.clearTradeRequest() {
    tradeRequests.remove(this)
}
