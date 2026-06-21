package org.rsmod.content.other.agentbridge

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.rsmod.api.inv.map.InvMapInit
import org.rsmod.game.client.NoopClient
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.map.CoordGrid

@Singleton
class PlayerBotService
@Inject
constructor(
    private val playerList: PlayerList,
    private val invMapInit: InvMapInit,
) {
    private val logger = com.github.michaelbull.logging.InlineLogger()

    fun spawnBot(name: String, x: Int = 3222, z: Int = 3222): Player? {
        val slot = playerList.nextFreeSlot() ?: run {
            logger.warn { "[PlayerBot] No free player slots available" }
            return null
        }

        val player = Player(client = NoopClient)
        player.avatar.name = name
        player.coords = CoordGrid(x, z, 0)
        player.accountId = slot
        player.userId = slot.toLong()

        // Initialize inventory containers (inv, worn) same as real players
        invMapInit.init(player)

        playerList[slot] = player

        logger.info { "[PlayerBot] Spawned '${name}' at (${x}, ${z}) slot=${slot}" }
        return player
    }

    fun despawnBot(name: String): Boolean {
        val player = findBot(name) ?: return false
        playerList.remove(player.slotId)
        logger.info { "[PlayerBot] Despawned '${name}'" }
        return true
    }

    fun findBot(name: String): Player? {
        val lower = name.lowercase()
        for (player in playerList) {
            if (player != null && player.avatar.name.lowercase() == lower) {
                return player
            }
        }
        return null
    }

    fun botCount(): Int {
        var count = 0
        for (player in playerList) {
            if (player != null && player.client is NoopClient) {
                count++
            }
        }
        return count
    }
}
