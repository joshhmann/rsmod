package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.social.FriendListDel
import org.rsmod.api.account.character.social.removeFriend
import org.rsmod.api.player.output.mes
import org.rsmod.game.entity.Player

class FriendListDelHandler @Inject constructor() : MessageHandler<FriendListDel> {
    override fun handle(player: Player, message: FriendListDel) {
        val name = message.name
        if (player.removeFriend(name)) {
            player.mes("Removed $name from your friend list.")
        } else {
            player.mes("$name is not on your friend list.")
        }
    }
}
