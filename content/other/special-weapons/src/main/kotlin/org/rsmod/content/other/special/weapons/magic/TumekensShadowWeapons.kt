package org.rsmod.content.other.special.weapons.magic

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpHeld1
import org.rsmod.content.other.special.weapons.configs.SpecialWeaponObjs
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class TumekensShadowWeapons @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpHeld1(SpecialWeaponObjs.tumekens_shadow) { wieldCharged() }
    }

    private fun ProtectedAccess.wieldCharged() {
        mes("You wield the Tumeken's shadow.")
    }
}
