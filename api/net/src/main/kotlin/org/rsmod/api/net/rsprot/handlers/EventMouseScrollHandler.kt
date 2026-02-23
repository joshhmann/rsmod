package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.events.EventMouseScroll
import org.rsmod.game.entity.Player

class EventMouseScrollHandler @Inject constructor() : MessageHandler<EventMouseScroll> {
    override fun handle(player: Player, message: EventMouseScroll) {
        /* no-op */
    }
}
