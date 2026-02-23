package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.events.EventMouseClickV2
import org.rsmod.game.entity.Player

class EventMouseClickV2Handler @Inject constructor() : MessageHandler<EventMouseClickV2> {
    override fun handle(player: Player, message: EventMouseClickV2) {
        /* no-op */
    }
}
