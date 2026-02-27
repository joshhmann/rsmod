@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.miningguild.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

typealias mining_guild_npcs = MiningGuildNpcs

/**
 * NPC references for the Mining Guild.
 *
 * Note: The specific Mining Guild NPCs (Gadrin, Yarsul, Hendor, Dusuri) are not present in rev 233
 * cache. We use available dwarf NPCs as placeholders. In a full implementation, these would be
 * custom NPCs with proper dialogue.
 */
object MiningGuildNpcs : NpcReferences() {
    // Guild entrance guard - checks 60 Mining requirement
    // Using Falador dwarf as placeholder for Mining Guild door guard
    val door_guard = find("fai_falador_dwarf_normal1")

    // Guildmaster - sells Mining cape at 99 Mining
    // Using another Falador dwarf variant as placeholder for Gadrin
    val guildmaster = find("fai_falador_dwarf_normal2")

    // Shop NPCs
    // Yarsul - Pickaxe shop owner (placeholder)
    val yarsul = find("fai_falador_dwarf_normal3")

    // Hendor - Ore shop owner (placeholder)
    val hendor = find("dwarf_normal")

    // Dusuri - Star sprite shop (P2P - placeholder)
    val dusuri = find("dwarf_mountain")

    // Additional miners in the guild
    val miner1 = find("fai_dwarf_worker_01")
    val miner2 = find("fai_dwarf_worker_02")
}

internal object MiningGuildNpcEditor : NpcEditor() {
    init {
        // Door guard stays near entrance
        edit(mining_guild_npcs.door_guard) { wanderRange = 2 }

        // Guildmaster stays inside
        edit(mining_guild_npcs.guildmaster) {
            moveRestrict = indoors
            wanderRange = 2
        }

        // Shop keepers stay inside their shops
        edit(mining_guild_npcs.yarsul) {
            contentGroup = content.shop_keeper
            moveRestrict = indoors
            wanderRange = 1
        }

        edit(mining_guild_npcs.hendor) {
            contentGroup = content.shop_keeper
            moveRestrict = indoors
            wanderRange = 1
        }

        // Miners wander around the guild
        edit(mining_guild_npcs.miner1) { wanderRange = 3 }
        edit(mining_guild_npcs.miner2) { wanderRange = 3 }
    }
}
