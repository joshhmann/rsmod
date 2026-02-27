package org.rsmod.content.other.p2pblocking.scripts

import jakarta.inject.Inject
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * F2P P2P Content Blocking Script.
 *
 * Blocks access to members-only areas by intercepting interactions with boundary objects (gates,
 * doors, stairs) that lead to P2P content.
 *
 * IMPORTANT: This script is DISABLED by default for development. To enable P2P blocking, set the
 * varbit or use the ::p2pblock command.
 *
 * Blocked areas (when enabled):
 * - Taverley gate (west of Falador)
 * - Karamja members bridge
 * - Canifis gate (Haunted Woods)
 * - Taverley dungeon
 * - Heroes' Guild basement
 * - Legends' Guild
 * - Castle Wars
 * - Fight Arena
 * - Wilderness P2P shortcuts
 *
 * Enable with: ::p2pblock on Disable with: ::p2pblock off
 */
class F2PContentBlockingScript @Inject constructor() : PluginScript() {

    companion object {
        /** Set to true to enable P2P content blocking. Default: false for development. */
        var BLOCKING_ENABLED: Boolean = false
    }

    override fun ScriptContext.startup() {
        // Taverley gate (west of Falador)
        onOpLoc1(MembersLocs.taverley_gate) {
            if (isBlockingEnabled()) {
                mes("You need to be a member to access this area.")
            }
        }

        // Karamja members bridge
        onOpLoc1(MembersLocs.karamja_bridge) {
            if (isBlockingEnabled()) {
                mes("You need to be a member to access this area.")
            }
        }

        // Canifis gate
        onOpLoc1(MembersLocs.canifis_gate) {
            if (isBlockingEnabled()) {
                mes("You need to be a member to access this area.")
            }
        }

        // Taverley dungeon
        onOpLoc1(MembersLocs.taverley_dungeon_entrance) {
            if (isBlockingEnabled()) {
                mes("You need to be a member to access this area.")
            }
        }

        // Heroes' Guild basement
        onOpLoc1(MembersLocs.heroes_guild_basement) {
            if (isBlockingEnabled()) {
                mes("You need to be a member to access this area.")
            }
        }

        // Legends' Guild
        onOpLoc1(MembersLocs.legends_guild_gate) {
            if (isBlockingEnabled()) {
                mes("You need to be a member to access this area.")
            }
        }

        // Castle Wars
        onOpLoc1(MembersLocs.castle_wars_entrance) {
            if (isBlockingEnabled()) {
                mes("You need to be a member to access this area.")
            }
        }

        // Fight Arena
        onOpLoc1(MembersLocs.fight_arena_entrance) {
            if (isBlockingEnabled()) {
                mes("You need to be a member to access this area.")
            }
        }

        // Wilderness P2P shortcuts
        onOpLoc1(MembersLocs.shortcut_wilderness_p2p) {
            if (isBlockingEnabled()) {
                mes("You need to be a member to use this shortcut.")
            }
        }
    }

    /** Check if P2P blocking is enabled. Returns false by default (disabled for development). */
    private fun isBlockingEnabled(): Boolean {
        return BLOCKING_ENABLED
    }
}

/** Members-only location references. */
internal object MembersLocs : LocReferences() {
    // Taverley gate (west of Falador)
    val taverley_gate = find("members_gate_taverley")

    // Karamja members bridge
    val karamja_bridge = find("members_bridge_karamja")

    // Canifis gate
    val canifis_gate = find("members_gate_canifis")

    // Taverley dungeon
    val taverley_dungeon_entrance = find("members_dungeon_taverley_entrance")

    // Heroes' Guild basement
    val heroes_guild_basement = find("members_basement_heroes")

    // Legends' Guild
    val legends_guild_gate = find("members_gate_legends")

    // Castle Wars
    val castle_wars_entrance = find("members_castle_wars")

    // Fight Arena
    val fight_arena_entrance = find("members_fight_arena")

    // Wilderness P2P shortcuts
    val shortcut_wilderness_p2p = find("members_wilderness_shortcut")
}
