package org.rsmod.content.skills.crafting.scripts

// ======================================================================================
// Crafting skill plugin for RSMod v2
//
// Crafting is an artisan skill with multiple interaction types:
//
// 1. GEM CUTTING (GemCutting.kt):
//    - Use chisel on uncut gem → cut gem
//    - Opal (1), Jade (13), Red topaz (16), Sapphire (20), Emerald (27), Ruby (34), Diamond (43)
//    - Semi-precious gems (opal, jade, red topaz) can be crushed
//
// 2. POTTERY (Pottery.kt):
//    - Use soft clay on pottery wheel → unfired pot/bowl
//    - Use unfired item on pottery oven → finished fired item
//    - Pot: level 1, Bowl: level 8
//
// 3. LEATHERWORKING (Leatherworking.kt):
//    - Tan cowhide at Ellis (Al Kharid) → leather/hard leather
//    - Use needle + thread + leather → leather items
//    - Gloves(1), Boots(7), Cowl(9), Vambraces(11), Body(14), Chaps(18)
//    - Hard leather body: level 28
//
// 4. SPINNING (Spinning.kt):
//    - Use wool on spinning wheel → ball of wool
//    - Level 1, 2.5 XP per wool
//
// 5. JEWELRY MAKING (Jewelry.kt):
//    - Use gold bar on furnace (with mould) → gold jewelry
//    - Ring(5), Necklace(6), Amulet(8)
//    - String amulet with ball of wool
//
// ======================================================================================

import jakarta.inject.Inject
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Main Crafting skill plugin.
 *
 * Delegates to sub-modules for specific crafting mechanics.
 */
class Crafting @Inject constructor(private val xpMods: XpModifiers) : PluginScript() {

    override fun ScriptContext.startup() {
        // Sub-modules are auto-discovered and initialized by the script loader.
        // This main class serves as the entry point and documentation hub.
    }
}
