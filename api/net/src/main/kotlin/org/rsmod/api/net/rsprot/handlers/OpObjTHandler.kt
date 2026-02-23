package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.objs.OpObjT
import org.rsmod.game.entity.Player

class OpObjTHandler @Inject constructor() : MessageHandler<OpObjT> {
    override fun handle(player: Player, message: OpObjT) {
        /* no-op */
    }
}
