package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.events.EventMouseClickV1
import org.rsmod.game.entity.Player

class EventMouseClickV1Handler @Inject constructor() : MessageHandler<EventMouseClickV1> {
    override fun handle(player: Player, message: EventMouseClickV1) {
        /* no-op */
    }
}
