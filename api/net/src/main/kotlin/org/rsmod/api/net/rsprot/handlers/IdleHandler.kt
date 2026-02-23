package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.misc.client.Idle
import org.rsmod.game.entity.Player

class IdleHandler @Inject constructor() : MessageHandler<Idle> {
    override fun handle(player: Player, message: Idle) {
        /* no-op */
    }
}
