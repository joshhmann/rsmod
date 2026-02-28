@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.interf.InterfaceReferences

typealias interfaces = BaseInterfaces

object BaseInterfaces : InterfaceReferences() {
    val fade_overlay = find("fade_overlay")

    val bank_main = find("bankmain")
    val bank_side = find("bankside")
    val bank_depositbox = find("bank_depositbox")
    val bankpin_settings = find("bankpin_settings")

    val toplevel = find("toplevel")
    val toplevel_osrs_stretch = find("toplevel_osrs_stretch")
    val toplevel_pre_eoc = find("toplevel_pre_eoc")

    val buff_bar = find("buff_bar")
    val stat_boosts_hud = find("stat_boosts_hud")
    val pvp_icons = find("pvp_icons")
    val ehc_worldhop = find("ehc_worldhop")
    val chatbox = find("chatbox")
    val popout = find("popout")
    val pm_chat = find("pm_chat")
    val orbs = find("orbs")
    val xp_drops = find("xp_drops")
    val stats = find("stats")
    val side_journal = find("side_journal")
    val questlist = find("questlist")
    val inventory = find("inventory")
    val wornitems = find("wornitems")
    val side_channels = find("side_channels")
    val settings_side = find("settings_side")
    val prayerbook = find("prayerbook")
    val magic_spellbook = find("magic_spellbook")
    val friends = find("friends")
    val account = find("account")
    val logout = find("logout")
    val emote = find("emote")
    val music = find("music")
    val chatchannel_current = find("chatchannel_current")
    val worldswitcher = find("worldswitcher")
    val combat_interface = find("combat_interface")
    val hpbar_hud = find("hpbar_hud")

    val trade_main = find("trademain")
    val trade_side = find("tradeside")
    val trade_confirm = find("tradeconfirm")

    val account_summary_sidepanel = find("account_summary_sidepanel")
    val area_task = find("area_task")

    val chat_right = find("chat_right")
    val chat_left = find("chat_left")
    val chatmenu = find("chatmenu")
    val messagebox = find("messagebox")
    val obj_dialogue = find("objectbox")
    val double_obj_dialogue = find("objectbox_double")
    val destroy_obj_dialogue = find("confirmdestroy")
    val menu = find("menu")

    val popupoverlay = find("popupoverlay")
    val ge_collection_box = find("ge_collect")
    val ge_exchange_main = find("ge_offers")
    val ge_exchange_side = find("ge_offers_side")
    val ca_overview = find("ca_overview")
    val collection = find("collection")
    val bond_main = find("bond_main")
    val poh_options = find("poh_options")
    val settings = find("settings")
    val quest_scroll = find("questscroll")
    val makeover_mage = find("makeover_mage")
}
