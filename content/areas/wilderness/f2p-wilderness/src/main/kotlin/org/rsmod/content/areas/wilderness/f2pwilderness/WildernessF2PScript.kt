package org.rsmod.content.areas.wilderness.f2pwilderness

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc1
import org.rsmod.content.areas.wilderness.f2pwilderness.configs.wilderness_ditch_locs
import org.rsmod.content.areas.wilderness.f2pwilderness.configs.wilderness_f2p_seqs
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class WildernessF2PScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(wilderness_ditch_locs.ditch_wilderness1_ground) { crossDitch(it.loc) }
        onOpLoc1(wilderness_ditch_locs.ditch_wilderness1a_ground) { crossDitch(it.loc) }
        onOpLoc1(wilderness_ditch_locs.ditch_wilderness3_ground) { crossDitch(it.loc) }
        onOpLoc1(wilderness_ditch_locs.ditch_wilderness3a_ground) { crossDitch(it.loc) }
        onOpLoc1(wilderness_ditch_locs.ditch_wilderness4_ground) { crossDitch(it.loc) }
        onOpLoc1(wilderness_ditch_locs.ditch_wilderness4a_ground) { crossDitch(it.loc) }
        onOpLoc1(wilderness_ditch_locs.ditch_wildernesse_ground) { crossDitch(it.loc) }
        onOpLoc1(wilderness_ditch_locs.ditch_wildernessea_ground) { crossDitch(it.loc) }
    }

    private suspend fun ProtectedAccess.crossDitch(loc: BoundLocInfo) {
        val playerZ = coords.z
        val ditchZ = loc.z

        val newZ: Int
        val direction: String
        if (playerZ <= ditchZ) {
            newZ = ditchZ + 3
            direction = "north"
        } else {
            newZ = ditchZ - 3
            direction = "south"
        }

        anim(wilderness_f2p_seqs.human_jump_hurdle)
        teleport(CoordGrid(coords.x, newZ, coords.level))
        mes("You jump $direction over the ditch.")
    }
}
