package org.rsmod.api.drop.table

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.npc.NpcType

/**
 * Global registry that maps NPC type IDs to their [NpcDropTable] definitions.
 *
 * Content modules inject this singleton during [org.rsmod.plugin.scripts.PluginScript.startup] and
 * call [register] for each NPC type they own. [NpcDeath][org.rsmod.api.death.NpcDeath] then looks
 * up the table by the dead NPC's resolved type ID and rolls it.
 *
 * **Thread-safety:** All registrations happen during single-threaded server startup before any game
 * tick runs, so no additional synchronisation is needed.
 */
@Singleton
public class NpcDropTableRegistry @Inject constructor() {
    private val tables: HashMap<Int, NpcDropTable> = HashMap()

    /**
     * Associate [table] with [npcType].
     *
     * If [npcType] has not been resolved to a cache ID (i.e. its internal name was not found in the
     * name-symbol map) this call is silently skipped so a bad NPC name does not crash the server at
     * startup. A negative resolved ID indicates the type was found in the builder cache but not yet
     * assigned a real ID; this is also silently skipped.
     *
     * @throws IllegalArgumentException if [npcType] already has a table registered.
     */
    public fun register(npcType: NpcType, table: NpcDropTable) {
        val id = TypeResolver[npcType] ?: return // type was never resolved — skip quietly
        if (id < 0) return // builder-assigned sentinel — skip
        require(id !in tables) {
            "Drop table already registered for npc '${npcType.internalName}' (id=$id). " +
                "Only one table per NPC type is supported."
        }
        tables[id] = table
    }

    /**
     * Convenience overload: register the same [table] for multiple [npcTypes] at once. Useful when
     * several NPC variants (e.g. man, man2, man3) share identical loot.
     */
    public fun register(npcTypes: Iterable<NpcType>, table: NpcDropTable) {
        for (type in npcTypes) register(type, table)
    }

    /** Look up the [NpcDropTable] for the NPC with the given resolved [npcTypeId]. */
    public fun find(npcTypeId: Int): NpcDropTable? = tables[npcTypeId]
}
