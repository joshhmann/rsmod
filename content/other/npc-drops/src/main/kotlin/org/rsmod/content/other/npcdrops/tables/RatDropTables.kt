package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.npc.NpcType

/**
 * Drop table registrations for Rat (regular, non-giant).
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 * - Existing inline handler preserved and enriched
 *
 * Enriched from corpus June 2026: added rat's tail as guaranteed always-drop
 * alongside bones (used in Witch's Potion quest).
 *
 * Skipped: rat bone (resolves to placeholder symbol, not a real cache item
 * in rev 233), looting bag (wilderness-only).
 */
internal object RatDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerRat(registry)
    }

    private fun registerRat(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)
            always(RatObjs.rats_tail)
        }

        val ratNpcs: List<NpcType> =
            listOf(
                RatNpcs.rat,
                RatNpcs.rat_indoors,
            )
        registry.register(ratNpcs.distinct(), table)
    }
}

internal object RatNpcs : NpcReferences() {
    val rat = find("rat")
    val rat_indoors = find("rat_indoors")
}

internal object RatObjs : ObjReferences() {
    val rats_tail = find("rats_tail")
}
