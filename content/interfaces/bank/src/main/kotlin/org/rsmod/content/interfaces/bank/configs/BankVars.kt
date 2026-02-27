package org.rsmod.content.interfaces.bank.configs

import org.rsmod.api.type.builders.varbit.VarBitBuilder
import org.rsmod.api.type.builders.varp.VarpBuilder
import org.rsmod.api.type.refs.varbit.VarBitReferences
import org.rsmod.api.type.refs.varp.VarpReferences

internal typealias bank_varbits = BankVarBits

typealias bank_varps = BankVarps

object BankVarBits : VarBitReferences() {
    val rearrange_mode = find("bank_insertmode")
    val withdraw_mode = find("bank_withdrawnotes")
    val placeholders = find("bank_leaveplaceholders")
    val last_quantity_input = find("bank_requestedquantity")
    val left_click_quantity = find("bank_quantity_type")
    val bank_filler_quantity = find("bank_fillermode")
    val tab_display = find("bank_tab_display")
    val incinerator = find("bank_showincinerator")
    val tutorial_button = find("bank_hidebanktut")
    val inventory_item_options = find("bank_hidesideops")
    val deposit_inventory_button = find("bank_hidedepositinv")
    val deposit_worn_items_button = find("bank_hidedepositworn")
    val always_deposit_to_potion_store = find("bank_depositpotion")
    val tutorial_current_page = find("hnt_hint_step")
    val tutorial_total_pages = find("hnt_hint_max_step")

    val tab_size1 = find("bank_tab_1")
    val tab_size2 = find("bank_tab_2")
    val tab_size3 = find("bank_tab_3")
    val tab_size4 = find("bank_tab_4")
    val tab_size5 = find("bank_tab_5")
    val tab_size6 = find("bank_tab_6")
    val tab_size7 = find("bank_tab_7")
    val tab_size8 = find("bank_tab_8")
    val tab_size9 = find("bank_tab_9")
    val tab_size_main = find("bank_tab_main")

    val selected_tab = find("bank_currenttab")

    val disable_ifevents = find("bank_disable_ifevents")
}

internal object BankVarBitBuilder : VarBitBuilder() {
    init {
        build("bank_tab_main") {
            baseVar = BankVarps.bank_serverside_vars
            startBit = 0
            endBit = 12
        }
        build("bank_disable_ifevents") {
            baseVar = BankVarps.bank_serverside_vars
            startBit = 13
            endBit = 13
        }
    }
}

object BankVarps : VarpReferences() {
    val bank_serverside_vars = find("bank_serverside_vars")
    val bankpin_2 = find("bankpin_2")

    // Bank PIN system varps (using internal names for custom varps)
    val bankpin_state = find("bankpin_state")
    val bankpin_verified = find("bankpin_verified")
    val bankpin_recovery_delay = find("bankpin_recovery_delay")
    val bankpin_entry_mode = find("bankpin_entry_mode")
    val bankpin_entry_progress = find("bankpin_entry_progress")
    val bankpin_entry_buffer = find("bankpin_entry_buffer")
    val bankpin_value = find("bankpin_value")
    val bankpin_pending = find("bankpin_pending")
}

internal object BankVarpBuilder : VarpBuilder() {
    init {
        build("bank_serverside_vars")

        // Bank PIN system varps
        build("bankpin_state")
        build("bankpin_verified")
        build("bankpin_recovery_delay")
        build("bankpin_entry_mode")
        build("bankpin_entry_progress")
        build("bankpin_entry_buffer")
        build("bankpin_value")
        build("bankpin_pending")
    }
}
