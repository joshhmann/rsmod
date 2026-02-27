package org.rsmod.content.other.npccombat

import org.rsmod.api.type.refs.npc.NpcReferences

/**
 * NPC type references for the npc-combat content module. These reference the Chaos Druid NPC types
 * from the cache.
 */
internal object NpcCombatTypes : NpcReferences() {
    /** Standard Chaos Druid (level 13) - found in Edgeville Dungeon, Taverley Dungeon, etc. */
    val chaos_druid = find("chaos_druid")

    /** Chaos Druid Warrior - stronger variant */
    val chaos_druid_warrior = find("chaos_druid_warrior")

    /** Wilderness Chaos Druid - found in Edgeville Dungeon wilderness area */
    val wilderness_chaos_druid = find("wilderness_chaos_druid")
}
