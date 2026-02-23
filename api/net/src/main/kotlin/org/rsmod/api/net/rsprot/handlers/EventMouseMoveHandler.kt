package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.events.EventMouseMove
import org.rsmod.game.entity.Player

class EventMouseMoveHandler @Inject constructor() : MessageHandler<EventMouseMove> {
    override fun handle(player: Player, message: EventMouseMove) {
        /* no-op */
    }
}
