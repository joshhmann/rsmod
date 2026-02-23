package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.social.IgnoreListDel
import org.rsmod.game.entity.Player

class IgnoreListDelHandler @Inject constructor() : MessageHandler<IgnoreListDel> {
    override fun handle(player: Player, message: IgnoreListDel) {
        /* no-op */
    }
}
