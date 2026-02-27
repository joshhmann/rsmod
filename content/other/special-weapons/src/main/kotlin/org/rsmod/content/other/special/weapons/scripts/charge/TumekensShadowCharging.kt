package org.rsmod.content.other.special.weapons.scripts.charge

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpHeld1
import org.rsmod.api.script.onOpHeld2
import org.rsmod.api.script.onOpHeld3
import org.rsmod.api.script.onOpHeld4
import org.rsmod.api.script.onOpHeld5
import org.rsmod.content.other.special.weapons.configs.SpecialWeaponObjs
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class TumekensShadowCharging @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpHeld1(SpecialWeaponObjs.tumekens_shadow_uncharged) { wieldUncharged() }
        onOpHeld2(SpecialWeaponObjs.tumekens_shadow_uncharged) {
            charge(it.slot, SpecialWeaponObjs.tumekens_shadow_uncharged)
        }
        onOpHeld2(SpecialWeaponObjs.tumekens_shadow) {
            charge(it.slot, SpecialWeaponObjs.tumekens_shadow)
        }
        onOpHeld3(SpecialWeaponObjs.tumekens_shadow) { uncharge(it.slot) }
        onOpHeld4(SpecialWeaponObjs.tumekens_shadow) {
            checkCharges(SpecialWeaponObjs.tumekens_shadow)
        }
        onOpHeld5(SpecialWeaponObjs.tumekens_shadow) {
            checkCharges(SpecialWeaponObjs.tumekens_shadow)
        }
    }

    private fun ProtectedAccess.wieldUncharged() {
        mes("This staff has no charges.")
    }

    private suspend fun ProtectedAccess.charge(invSlot: Int, obj: ObjType) {
        mes("You charge the staff.")
    }

    private suspend fun ProtectedAccess.uncharge(invSlot: Int) {
        mes("You uncharge the staff.")
    }

    private fun ProtectedAccess.checkCharges(obj: ObjType) {
        mes("The staff has many charges remaining.")
    }
}
