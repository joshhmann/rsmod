@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.misc.cookingguild.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias cooking_guild_npcs = CookingGuildNpcs

internal object CookingGuildNpcs : NpcReferences() {
    val head_chef = find("head_chef")
}
