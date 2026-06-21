package org.rsmod.content.skills.ranged.scripts

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class RangedPlugin
@Inject
constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // Ranged combat is handled by the combat API and the mechanics layer.
        // Bow/arrow configs (attack rate, range, level reqs, ammo recovery,
        // projectile types) are set in content/mechanics/ranged/configs/RangedObjs.kt
        // via BowObjs and ArrowObjs ObjEditor classes.
        //
        // This plugin registers the ranged skill module so the content system
        // can discover it. No additional interaction handlers are needed for
        // F2P ranged combat.
    }
}
