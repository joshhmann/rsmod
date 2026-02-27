package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.social.IgnoreListAdd
import org.rsmod.api.account.character.social.addIgnore
import org.rsmod.api.player.output.mes
import org.rsmod.game.entity.Player

class IgnoreListAddHandler @Inject constructor() : MessageHandler<IgnoreListAdd> {
    override fun handle(player: Player, message: IgnoreListAdd) {
        val name = message.name
        if (name.isBlank()) {
            player.mes("Please enter a valid player name.")
            return
        }

        if (player.addIgnore(name)) {
            player.mes("Added $name to your ignore list.")
        } else {
            player.mes("$name is already on your ignore list.")
        }
    }
}
