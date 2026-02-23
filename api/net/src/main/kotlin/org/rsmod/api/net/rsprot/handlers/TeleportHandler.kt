package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.misc.user.Teleport
import org.rsmod.api.net.rsprot.player.protectedTelejump
import org.rsmod.api.realm.Realm
import org.rsmod.game.entity.Player
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.collision.CollisionFlagMap

class TeleportHandler
@Inject
constructor(private val realm: Realm, private val collision: CollisionFlagMap) :
    MessageHandler<Teleport> {
    override fun handle(player: Player, message: Teleport) {
        if (realm.config.devMode) {
            val dest = CoordGrid(message.x, message.z, message.level)
            player.protectedTelejump(collision, dest)
        }
    }
}
