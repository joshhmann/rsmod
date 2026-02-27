package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.social.FriendListAdd
import org.rsmod.api.account.character.social.addFriend
import org.rsmod.api.player.output.mes
import org.rsmod.game.entity.Player

class FriendListAddHandler @Inject constructor() : MessageHandler<FriendListAdd> {
    override fun handle(player: Player, message: FriendListAdd) {
        val name = message.name
        if (name.isBlank()) {
            player.mes("Please enter a valid player name.")
            return
        }

        if (player.addFriend(name)) {
            player.mes("Added $name to your friend list.")
        } else {
            player.mes("$name is already on your friend list.")
        }
    }
}
