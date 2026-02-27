package org.rsmod.content.areas.misc.cookingguild.configs

import org.rsmod.api.type.refs.loc.LocReferences

internal typealias cooking_guild_locs = CookingGuildLocs

internal object CookingGuildLocs : LocReferences() {
    /** Cooking Guild entry door - requires 32 Cooking and chef's hat to enter */
    val door = find("fai_varrock_cook_guild_door")
}
