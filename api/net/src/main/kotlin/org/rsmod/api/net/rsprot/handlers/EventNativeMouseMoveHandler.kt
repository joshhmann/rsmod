package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.events.EventNativeMouseMove
import org.rsmod.game.entity.Player

class EventNativeMouseMoveHandler @Inject constructor() : MessageHandler<EventNativeMouseMove> {
    override fun handle(player: Player, message: EventNativeMouseMove) {
        /* no-op */
    }
}
