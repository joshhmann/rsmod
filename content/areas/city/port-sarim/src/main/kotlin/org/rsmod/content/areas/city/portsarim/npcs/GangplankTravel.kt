package org.rsmod.content.areas.city.portsarim.npcs

import jakarta.inject.Inject
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.obj.ObjType
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

object GangplankTravelLocs : LocReferences() {
    val sarimshipplank_on = find("sarimshipplank_on")
    val gangplank = find("gangplank")
}

object GangplankObjs : ObjReferences() {
    val coins = find("coins")
}

class GangplankTravel @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(GangplankTravelLocs.sarimshipplank_on) { travelToKaramja() }
        onOpLoc1(GangplankTravelLocs.gangplank) { travelToKaramja() }
    }

    private suspend fun ProtectedAccess.travelToKaramja() {
        val coins = player.inv.filterNotNull { it.id == GangplankObjs.coins.id }.sumOf { it.count }
        if (coins < 30) {
            mes("You need 30 gold pieces to sail to Karamja.")
            return
        }
        player.invDel(player.inv, GangplankObjs.coins, 30)
        mes("You pay 30 gold and board the ship.")
        teleport(CoordGrid(0, 46, 49, 15, 32))
        mes("You arrive safely at Karamja Musa Point.")
    }
}
