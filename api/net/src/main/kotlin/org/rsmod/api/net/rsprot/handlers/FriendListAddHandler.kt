package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.social.FriendListAdd
import org.rsmod.game.entity.Player

class FriendListAddHandler @Inject constructor() : MessageHandler<FriendListAdd> {
    override fun handle(player: Player, message: FriendListAdd) {
        /* no-op */
    }
}
