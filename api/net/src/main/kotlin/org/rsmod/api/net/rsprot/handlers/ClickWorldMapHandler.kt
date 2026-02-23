package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.misc.user.ClickWorldMap
import org.rsmod.game.entity.Player

class ClickWorldMapHandler @Inject constructor() : MessageHandler<ClickWorldMap> {
    override fun handle(player: Player, message: ClickWorldMap) {
        /* no-op */
    }
}
