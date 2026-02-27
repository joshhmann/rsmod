package org.rsmod.content.generic.locs.banks

import kotlin.math.min
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLoc2
import org.rsmod.api.script.onOpLocU
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class BankChest : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(content.bank_chest) { openBank() }
        onOpLoc2(content.bank_chest) { openCollectionBox() }
        onOpLocU(content.bank_chest) { banknote(it.invSlot, it.objType) }
    }

    private fun ProtectedAccess.openBank() {
        ifOpenMainSidePair(main = interfaces.bank_main, side = interfaces.bank_side)
    }

    private fun ProtectedAccess.openCollectionBox() {
        ifOpenMainModal(interfaces.ge_collection_box)
    }

    private suspend fun ProtectedAccess.banknote(invSlot: Int, objType: UnpackedObjType) {
        if (!objType.isCert) {
            mes("Hand me a banknote, and I'll try to convert it to an item.")
            return
        }

        if (inv.isFull()) {
            mes("You don't have any inventory space.")
            return
        }

        val confirmation = choice2("Yes", true, "No", false, "Un-note the banknote?")
        if (!confirmation) {
            return
        }

        val invObj = inv[invSlot]
        if (invObj == null || invObj.id != objType.id) {
            return
        }

        val count = min(inv.freeSpace(), invObj.count)
        if (count == 0) {
            mes("You don't have any inventory space.")
            return
        }

        val uncert = ocUncert(objType)
        val replace = invReplace(inv, invSlot, count, uncert)
        if (replace.success) {
            objbox(uncert, 400, "The bank exchanges your banknote for an item.")
        }
    }
}
