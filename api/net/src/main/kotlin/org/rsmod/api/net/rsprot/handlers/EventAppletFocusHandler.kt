package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.events.EventAppletFocus
import org.rsmod.game.entity.Player

class EventAppletFocusHandler @Inject constructor() : MessageHandler<EventAppletFocus> {
    override fun handle(player: Player, message: EventAppletFocus) {
        /* no-op */
    }
}
