package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.social.IgnoreListDel
import org.rsmod.api.account.character.social.removeIgnore
import org.rsmod.api.player.output.mes
import org.rsmod.game.entity.Player

class IgnoreListDelHandler @Inject constructor() : MessageHandler<IgnoreListDel> {
    override fun handle(player: Player, message: IgnoreListDel) {
        val name = message.name
        if (player.removeIgnore(name)) {
            player.mes("Removed $name from your ignore list.")
        } else {
            player.mes("$name is not on your ignore list.")
        }
    }
}
