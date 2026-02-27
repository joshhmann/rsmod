package org.rsmod.content.areas.city.rimmington

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// House portal reference
internal object RimmingtonLocs : LocReferences() {
    val house_portal = find("poh_rimmington_portal")
}

class RimmingtonScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // House portal - message for now (Construction skill not implemented)
        onOpLoc1(RimmingtonLocs.house_portal) { enterHousePortal(it.loc) }
    }

    private fun ProtectedAccess.enterHousePortal(loc: BoundLocInfo) {
        mes("You would enter a player-owned house here.")
        mes("The Construction skill is not yet implemented.")
    }
}
