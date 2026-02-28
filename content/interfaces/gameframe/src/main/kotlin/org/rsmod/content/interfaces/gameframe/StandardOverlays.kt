package org.rsmod.content.interfaces.gameframe

import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.game.type.comp.ComponentType

internal object StandardOverlays {
    val open: List<GameframeOverlay> =
        listOf(
            GameframeOverlay(interfaces.chatbox, components.toplevel_osrs_stretch_chat_container),
            GameframeOverlay(interfaces.buff_bar, components.toplevel_osrs_stretch_buff_bar),
            GameframeOverlay(
                interfaces.stat_boosts_hud,
                components.toplevel_osrs_stretch_stat_boosts_hud,
            ),
            GameframeOverlay(interfaces.pm_chat, components.toplevel_osrs_stretch_pm_container),
            GameframeOverlay(interfaces.hpbar_hud, components.toplevel_osrs_stretch_hpbar_hud),
            GameframeOverlay(interfaces.pvp_icons, components.toplevel_osrs_stretch_pvp_icons),
            GameframeOverlay(interfaces.orbs, components.toplevel_osrs_stretch_orbs),
            GameframeOverlay(interfaces.xp_drops, components.toplevel_osrs_stretch_xp_drops),
            GameframeOverlay(interfaces.popout, components.toplevel_osrs_stretch_popout),
            GameframeOverlay(interfaces.ehc_worldhop, components.toplevel_osrs_stretch_ehc_listener),
            GameframeOverlay(interfaces.stats, components.toplevel_osrs_stretch_side1),
            GameframeOverlay(interfaces.side_journal, components.toplevel_osrs_stretch_side2),
            GameframeOverlay(interfaces.inventory, components.toplevel_osrs_stretch_side3),
            GameframeOverlay(interfaces.wornitems, components.toplevel_osrs_stretch_side4),
            GameframeOverlay(interfaces.prayerbook, components.toplevel_osrs_stretch_side5),
            GameframeOverlay(interfaces.magic_spellbook, components.toplevel_osrs_stretch_side6),
            GameframeOverlay(interfaces.friends, components.toplevel_osrs_stretch_side9),
            GameframeOverlay(interfaces.account, components.toplevel_osrs_stretch_side8),
            GameframeOverlay(interfaces.logout, components.toplevel_osrs_stretch_side10),
            GameframeOverlay(interfaces.settings_side, components.toplevel_osrs_stretch_side11),
            GameframeOverlay(interfaces.emote, components.toplevel_osrs_stretch_side12),
            GameframeOverlay(interfaces.music, components.toplevel_osrs_stretch_side13),
            GameframeOverlay(interfaces.side_channels, components.toplevel_osrs_stretch_side7),
            GameframeOverlay(interfaces.combat_interface, components.toplevel_osrs_stretch_side0),
        )

    val move: List<ComponentType> =
        listOf(
            components.toplevel_osrs_stretch_chat_container,
            components.mainmodal,
            components.toplevel_osrs_stretch_maincrm,
            components.toplevel_osrs_stretch_overlay_atmosphere,
            components.toplevel_osrs_stretch_overlay_hud,
            components.sidemodal,
            components.toplevel_osrs_stretch_side0,
            components.toplevel_osrs_stretch_side1,
            components.toplevel_osrs_stretch_side2,
            components.toplevel_osrs_stretch_side3,
            components.toplevel_osrs_stretch_side4,
            components.toplevel_osrs_stretch_side5,
            components.toplevel_osrs_stretch_side6,
            components.toplevel_osrs_stretch_side7,
            components.toplevel_osrs_stretch_side8,
            components.toplevel_osrs_stretch_side9,
            components.toplevel_osrs_stretch_side10,
            components.toplevel_osrs_stretch_side11,
            components.toplevel_osrs_stretch_side12,
            components.toplevel_osrs_stretch_side13,
            components.toplevel_osrs_stretch_sidecrm,
            components.toplevel_osrs_stretch_pvp_icons,
            components.toplevel_osrs_stretch_pm_container,
            components.toplevel_osrs_stretch_orbs,
            components.toplevel_osrs_stretch_xp_drops,
            components.toplevel_osrs_stretch_zeah,
            components.toplevel_osrs_stretch_floater,
            components.toplevel_osrs_stretch_buff_bar,
            components.toplevel_osrs_stretch_stat_boosts_hud,
            components.toplevel_osrs_stretch_helper_content,
            components.toplevel_osrs_stretch_hpbar_hud,
            components.toplevel_osrs_stretch_popout,
            components.toplevel_osrs_stretch_ehc_listener,
        )
}
