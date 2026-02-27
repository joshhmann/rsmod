package org.rsmod.content.generic.locs.banks

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.game.type.loc.LocType

internal typealias bank_locs = BankLocs

internal object BankLocs : LocReferences() {
    val bankbooth = find("bankbooth")
    val aide_bankbooth = find("aide_bankbooth")
    val aide_bankbooth_multi = find("aide_bankbooth_multi")
    val bank_deposit_box = find("bank_deposit_box")
    val bank_deposit_box_2 = find("bank_deposit_box_2")
}

internal object BankLocEditor : LocEditor() {
    init {
        booth(bank_locs.bankbooth)
        booth(bank_locs.aide_bankbooth)
        booth(bank_locs.aide_bankbooth_multi)
        depositBox(bank_locs.bank_deposit_box)
        depositBox(bank_locs.bank_deposit_box_2)
    }

    private fun booth(type: LocType) {
        edit(type) { contentGroup = content.bank_booth }
    }

    private fun depositBox(type: LocType) {
        edit(type) { contentGroup = content.bank_deposit_box }
    }
}
