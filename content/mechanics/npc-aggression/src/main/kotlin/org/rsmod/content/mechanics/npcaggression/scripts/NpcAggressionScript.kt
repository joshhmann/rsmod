package org.rsmod.content.mechanics.npcaggression.scripts

import jakarta.inject.Inject
import org.rsmod.api.game.process.GameLifecycle
import org.rsmod.api.hunt.AggroTolerance
import org.rsmod.api.hunt.Hunt
import org.rsmod.api.script.onEvent
import org.rsmod.game.MapClock
import org.rsmod.game.entity.PlayerList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class NpcAggressionScript
@Inject
constructor(
    private val players: PlayerList,
    private val mapClock: MapClock,
    @Suppress("unused") private val hunt: Hunt,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onEvent<GameLifecycle.StartCycle> {
            players.forEach { player -> AggroTolerance.update(player, mapClock.cycle) }
        }
    }
}
