package org.rsmod.content.interfaces.bank.configs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences

internal typealias bank_interfaces = BankInterfaces

internal typealias bank_components = BankComponents

internal typealias bank_comsubs = BankSubComponents

object BankInterfaces : InterfaceReferences() {
    val tutorial_overlay = find("screenhighlight")
    val bankpin_settings = find("bankpin_settings")
    val bankpin_keypad = find("bankpin_keypad")
}

object BankComponents : ComponentReferences() {
    val tutorial_button = find("bankmain:bank_tut")
    val capacity_container = find("bankmain:capacity_layer")
    val capacity_text = find("bankmain:capacity")
    val main_inventory = find("bankmain:items")
    val tabs = find("bankmain:tabs")
    val incinerator_confirm = find("bankmain:incinerator_confirm")
    val potionstore_items = find("bankmain:potionstore_items")
    val worn_off_stab = find("bankmain:stabatt")
    val worn_off_slash = find("bankmain:slashatt")
    val worn_off_crush = find("bankmain:crushatt")
    val worn_off_magic = find("bankmain:magicatt")
    val worn_off_range = find("bankmain:rangeatt")
    val worn_speed_base = find("bankmain:attackspeedbase")
    val worn_speed = find("bankmain:attackspeedactual")
    val worn_def_stab = find("bankmain:stabdef")
    val worn_def_slash = find("bankmain:slashdef")
    val worn_def_crush = find("bankmain:crushdef")
    val worn_def_range = find("bankmain:rangedef")
    val worn_def_magic = find("bankmain:magicdef")
    val worn_melee_str = find("bankmain:meleestrength")
    val worn_ranged_str = find("bankmain:rangestrength")
    val worn_magic_dmg = find("bankmain:magicdamage")
    val worn_prayer = find("bankmain:prayer")
    val worn_undead = find("bankmain:typemultiplier")
    val worn_slayer = find("bankmain:slayermultiplier")
    val tutorial_overlay_target = find("bankmain:bank_highlight")
    val confirmation_overlay_target = find("bankmain:popup")
    val tooltip = find("bankmain:tooltip")

    val rearrange_mode_swap = find("bankmain:swap")
    val rearrange_mode_insert = find("bankmain:insert")
    val withdraw_mode_item = find("bankmain:item")
    val withdraw_mode_note = find("bankmain:note")
    val always_placehold = find("bankmain:placeholder")
    val deposit_inventory = find("bankmain:depositinv")
    val deposit_worn = find("bankmain:depositworn")
    val quantity_1 = find("bankmain:quantity1")
    val quantity_5 = find("bankmain:quantity5")
    val quantity_10 = find("bankmain:quantity10")
    val quantity_x = find("bankmain:quantityx")
    val quantity_all = find("bankmain:quantityall")

    val incinerator_toggle = find("bankmain:incinerator_toggle")
    val tutorial_button_toggle = find("bankmain:banktut_toggle")
    val inventory_item_options_toggle = find("bankmain:sideops_toggle")
    val deposit_inv_toggle = find("bankmain:depositinv_toggle")
    val deposit_worn_toggle = find("bankmain:depositworn_toggle")
    val release_placehold = find("bankmain:release_placeholders")
    val bank_fillers_1 = find("bankmain:bank_filler_1")
    val bank_fillers_10 = find("bankmain:bank_filler_10")
    val bank_fillers_50 = find("bankmain:bank_filler_50")
    val bank_fillers_x = find("bankmain:bank_filler_x")
    val bank_fillers_all = find("bankmain:bank_filler_all")
    val bank_fillers_fill = find("bankmain:bank_filler_confirm")
    val bank_tab_display = find("bankmain:dropdown_content")

    val side_inventory = find("bankside:items")
    val worn_inventory = find("bankside:wornops")
    val lootingbag_inventory = find("bankside:lootingbag_items")
    val league_inventory = find("bankside:league_secondinv_items")
    val bankside_highlight = find("bankside:bankside_highlight")

    val tutorial_close_button = find("screenhighlight:pausebutton")
    val tutorial_next_page = find("screenhighlight:continue")
    val tutorial_prev_page = find("screenhighlight:previous")

    val deposit_box_inventory = find("bank_depositbox:inventory")
    val deposit_box_deposit_inv = find("bank_depositbox:deposit_inv")
    val deposit_box_deposit_worn = find("bank_depositbox:deposit_worn")
    val deposit_box_deposit_lootingbag = find("bank_depositbox:deposit_lootingbag")

    // Bank PIN Settings components
    val bankpin_statusoutput = find("bankpin_settings:statusoutput")
    val bankpin_delayoutput = find("bankpin_settings:delayoutput")
    val bankpin_logoutoutput = find("bankpin_settings:logoutoutput")
    val bankpin_set = find("bankpin_settings:set")
    val bankpin_delay0 = find("bankpin_settings:delay0")
    val bankpin_change = find("bankpin_settings:change")
    val bankpin_delete = find("bankpin_settings:delete")
    val bankpin_delay1 = find("bankpin_settings:delay1")
    val bankpin_cancel = find("bankpin_settings:cancel")

    // Bank PIN Keypad components
    val bankpin_keypad_title = find("bankpin_keypad:title")
    val bankpin_keypad_digit1 = find("bankpin_keypad:digit1")
    val bankpin_keypad_digit2 = find("bankpin_keypad:digit2")
    val bankpin_keypad_digit3 = find("bankpin_keypad:digit3")
    val bankpin_keypad_digit4 = find("bankpin_keypad:digit4")
    val bankpin_keypad_digithint = find("bankpin_keypad:digithint")
}

@Suppress("ConstPropertyName")
object BankSubComponents {
    const val main_tab = 10
    val other_tabs = 11..19

    val tab_extended_slots_offset = 19..28
}
