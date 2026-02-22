package org.rsmod.content.skills.crafting.scripts

// IMPLEMENTATION NOTES:
// Crafting is an artisan skill with multiple interaction types.
// This module implements basic crafting interactions.
//
// Note: Additional crafting interactions (leather, gems, jewellery) require
// additional symbols to be added to BaseObjs.kt.

import jakarta.inject.Inject
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Crafting skill implementation.
 *
 * TODO: Implement spinning wheel and other crafting interactions.
 */
class Crafting @Inject constructor(private val xpMods: XpModifiers) : PluginScript() {

    override fun ScriptContext.startup() {
        // TODO: Implement spinning wheel: wool -> ball of wool
        // Requires proper LocReferences setup for spinning wheel locs
    }
}
