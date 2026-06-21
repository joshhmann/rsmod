package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Bat NPCs.
 *
 * Data sources:
 * - Primary: drops_by_source.json (OSRS wiki corpus)
 * - https://oldschool.runescape.wiki/w/Bat
 *
 * Drop structure:
 * - Always: Bat bones (guaranteed)
 * - No random loot at rev 228.
 */
internal object BatDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        val table = dropTable {
            // Guaranteed
            always(BatObjs.bat_bones)

            // No random loot for standard bats at rev 228.
        }

        registry.register(BatNpcs.bat, table)
    }
}

internal object BatNpcs : NpcReferences() {
    val bat = find("bat")
}

internal object BatObjs : ObjReferences() {
    val bat_bones = find("bat_bones")
}
