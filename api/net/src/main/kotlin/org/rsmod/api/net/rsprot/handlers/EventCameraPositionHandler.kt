package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.events.EventCameraPosition
import org.rsmod.game.entity.Player

class EventCameraPositionHandler @Inject constructor() : MessageHandler<EventCameraPosition> {
    override fun handle(player: Player, message: EventCameraPosition) {
        /* no-op */
    }
}
