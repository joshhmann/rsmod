package org.rsmod.api.net.rsprot.player

import net.rsprot.protocol.api.Session
import net.rsprot.protocol.game.outgoing.info.npcinfo.NpcInfo
import net.rsprot.protocol.game.outgoing.info.playerinfo.PlayerInfo
import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Player

data class SessionStart(
    val player: Player,
    val session: Session<Player>,
    val reconnect: Boolean = false,
    val playerInfo: PlayerInfo? = null,
    val npcInfo: NpcInfo? = null,
) : UnboundEvent
