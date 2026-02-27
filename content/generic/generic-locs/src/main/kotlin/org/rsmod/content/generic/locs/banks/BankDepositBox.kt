package org.rsmod.content.generic.locs.banks

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLocU
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class BankDepositBox : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(content.bank_deposit_box) { openDepositBox() }
        onOpLocU(content.bank_deposit_box) { openDepositBox() }
    }

    private fun ProtectedAccess.openDepositBox() {
        ifOpenMainModal(interfaces.bank_depositbox)
    }
}
