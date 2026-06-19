@file:Suppress("unused")

package org.rsmod.content.interfaces.gameframe.config

import org.rsmod.api.config.aliases.EnumComp
import org.rsmod.api.config.refs.components
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.dbrow.DbRowType

typealias gameframe_enums = GameframeEnums

object GameframeEnums : EnumReferences() {
    val toplevel = find<EnumComp, EnumComp>("fixed_pane_redirect", 4205535)
    val toplevel_osrs_stretch = find<EnumComp, EnumComp>("resizable_basic_pane_redirect", 4209256)
    val toplevel_pre_eoc = find<EnumComp, EnumComp>("side_panels_resizable_pane_redirect", 4212977)

    val list = find<Int, DbRowType>("gameframe_dbrows")
    val move_events = find<ComponentType, ComponentType>("toplevel_move_events")
}

public object GameframeEnumBuilder : EnumBuilder() {
    init {
        buildAutoInt<DbRowType>("gameframe_dbrows") {
            addSafe(gameframe_rows.toplevel)
            addSafe(gameframe_rows.osrs_stretch)
            addSafe(gameframe_rows.pre_eoc)
        }

        build<ComponentType, ComponentType>("toplevel_move_events") {
            putSafe(components.toplevel_osrs_stretch_side0, gameframe_components.toplevel_stone0)
            putSafe(components.toplevel_osrs_stretch_side1, gameframe_components.toplevel_stone1)
            putSafe(components.toplevel_osrs_stretch_side2, gameframe_components.toplevel_stone2)
            putSafe(components.toplevel_osrs_stretch_side3, gameframe_components.toplevel_stone3)
            putSafe(components.toplevel_osrs_stretch_side4, gameframe_components.toplevel_stone4)
            putSafe(components.toplevel_osrs_stretch_side5, gameframe_components.toplevel_stone5)
            putSafe(components.toplevel_osrs_stretch_side6, gameframe_components.toplevel_stone6)
            putSafe(components.toplevel_osrs_stretch_side7, gameframe_components.toplevel_stone7)
            putSafe(components.toplevel_osrs_stretch_side8, gameframe_components.toplevel_stone8)
            putSafe(components.toplevel_osrs_stretch_side9, gameframe_components.toplevel_stone9)
            putSafe(components.toplevel_osrs_stretch_side10, gameframe_components.toplevel_stone10)
            putSafe(components.toplevel_osrs_stretch_side11, gameframe_components.toplevel_stone11)
            putSafe(components.toplevel_osrs_stretch_side12, gameframe_components.toplevel_stone12)
            putSafe(components.toplevel_osrs_stretch_side13, gameframe_components.toplevel_stone13)
        }
    }
}
