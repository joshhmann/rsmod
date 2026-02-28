package org.rsmod.content.other.p2pblocking.scripts

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
class F2PContentBlockingScript : PluginScript() {
    override fun ScriptContext.startup() {
        // Disabled placeholder.
        //
        // NOTE: The original implementation referenced non-canonical loc symbol names
        // (e.g., "members_gate_taverley"), which breaks strict `packCache` verification.
        //
        // If we want this feature, re-implement using canonical rev233 loc names from
        // `rsmod/.data/symbols/loc.sym` and avoid global mutable state.
    }
}
