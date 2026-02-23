package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.events.EventKeyboard
import org.rsmod.game.entity.Player

class EventKeyboardHandler @Inject constructor() : MessageHandler<EventKeyboard> {
    override fun handle(player: Player, message: EventKeyboard) {
        /* no-op */
    }
}
