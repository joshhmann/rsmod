package org.rsmod.content.generic.locs.banks

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.game.type.loc.LocType

internal typealias bank_locs = BankLocs

internal object BankLocs : LocReferences() {
    val bankbooth = find("bankbooth")
    val bankbooth_multi = find("bankbooth_multi")
    val aide_bankbooth = find("aide_bankbooth")
    val aide_bankbooth_multi = find("aide_bankbooth_multi")
    val elid_bankbooth = find("elid_bankbooth")
    val fai_falador_bankbooth = find("fai_falador_bankbooth")
    val fai_falador_bankbooth_multi = find("fai_falador_bankbooth_multi")
    val fai_varrock_bankbooth = find("fai_varrock_bankbooth")
    val fai_varrock_bankbooth_multi = find("fai_varrock_bankbooth_multi")
    val newbiebankbooth = find("newbiebankbooth")

    val bank_deposit_box = find("bank_deposit_box")
    val bank_deposit_box_2 = find("bank_deposit_box_2")
    val sarim_deposit_box = find("sarim_deposit_box")
    val kr_bank_deposit_box = find("kr_bank_deposit_box")

    val fai_falador_bank_chest = find("fai_falador_bank_chest")
    val fai_varrock_bank_chest = find("fai_varrock_bank_chest")
}

internal object BankLocEditor : LocEditor() {
    init {
        booth(bank_locs.bankbooth)
        booth(bank_locs.bankbooth_multi)
        booth(bank_locs.aide_bankbooth)
        booth(bank_locs.aide_bankbooth_multi)
        booth(bank_locs.elid_bankbooth)
        booth(bank_locs.fai_falador_bankbooth)
        booth(bank_locs.fai_falador_bankbooth_multi)
        booth(bank_locs.fai_varrock_bankbooth)
        booth(bank_locs.fai_varrock_bankbooth_multi)
        booth(bank_locs.newbiebankbooth)
        depositBox(bank_locs.bank_deposit_box)
        depositBox(bank_locs.bank_deposit_box_2)
        depositBox(bank_locs.sarim_deposit_box)
        depositBox(bank_locs.kr_bank_deposit_box)
        chest(bank_locs.fai_falador_bank_chest)
        chest(bank_locs.fai_varrock_bank_chest)
    }

    private fun booth(type: LocType) {
        edit(type) { contentGroup = content.bank_booth }
    }

    private fun depositBox(type: LocType) {
        edit(type) { contentGroup = content.bank_deposit_box }
    }

    private fun chest(type: LocType) {
        edit(type) { contentGroup = content.bank_chest }
    }
}
