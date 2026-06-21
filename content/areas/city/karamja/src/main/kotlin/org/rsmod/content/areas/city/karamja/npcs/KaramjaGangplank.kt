package org.rsmod.content.areas.city.karamja.npcs

import jakarta.inject.Inject
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.content.areas.city.karamja.configs.karamja_objs
import org.rsmod.game.type.obj.ObjType
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

object KaramjaTravelLocs : LocReferences() {
    val karamjashipplank_on = find("karamjashipplank_on")
    val karamjashipplank_off = find("karamjashipplank_off")
}

class KaramjaGangplank @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(KaramjaTravelLocs.karamjashipplank_on) { travelToPortSarim() }
    }

    private suspend fun ProtectedAccess.travelToPortSarim() {
        val coins = player.inv.filterNotNull { it.id == karamja_objs.coins.id }.sumOf { it.count }
        if (coins < 30) {
            mes("You need 30 gold pieces to sail back to Port Sarim.")
            return
        }
        player.invDel(player.inv, karamja_objs.coins, 30)
        mes("You pay 30 gold and board the ship bound for Port Sarim.")
        teleport(CoordGrid(0, 47, 50, 29, 17))
        mes("You arrive safely at Port Sarim.")
    }
}
