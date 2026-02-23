package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.messaging.MessagePrivate
import org.rsmod.game.entity.Player

class MessagePrivateHandler @Inject constructor() : MessageHandler<MessagePrivate> {
    override fun handle(player: Player, message: MessagePrivate) {
        /* no-op */
    }
}
