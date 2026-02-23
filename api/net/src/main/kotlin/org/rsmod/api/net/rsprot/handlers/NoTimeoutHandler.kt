package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.misc.client.NoTimeout
import org.rsmod.game.entity.Player

class NoTimeoutHandler @Inject constructor() : MessageHandler<NoTimeout> {
    override fun handle(player: Player, message: NoTimeout) {
        /* no-op */
    }
}
