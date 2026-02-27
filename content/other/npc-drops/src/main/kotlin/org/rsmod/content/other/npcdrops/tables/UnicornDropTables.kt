package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Unicorn NPCs.
 *
 * Drop table source: https://oldschool.runescape.wiki/w/Unicorn
 * - Found in Barbarian Village pen, Wilderness (various locations)
 * - Combat level 15 (unicorn), 27 (black unicorn)
 * - Always drops: Bones, Unicorn horn
 *
 * Drop table source: https://oldschool.runescape.wiki/w/Black_unicorn
 * - Found in Wilderness (east of Wilderness Crater, south of Wilderness Agility Course)
 * - Combat level 27
 * - Always drops: Bones, Unicorn horn
 */
internal object UnicornDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerUnicorn(registry)
        registerBlackUnicorn(registry)
    }

    // -----------------------------------------------------------------------
    // Unicorn (Level 15)
    // Drop table source: https://oldschool.runescape.wiki/w/Unicorn
    // Always: Bones, Unicorn horn
    // Main drops: None (unicorn only has guaranteed drops)
    // -----------------------------------------------------------------------
    private fun registerUnicorn(registry: NpcDropTableRegistry) {
        val unicornTable = dropTable {
            always(objs.bones)
            always(UnicornObjs.unicorn_horn)

            // Unicorns have no random loot table - only guaranteed drops
        }

        registry.register(UnicornNpcs.unicorn, unicornTable)
    }

    // -----------------------------------------------------------------------
    // Black Unicorn (Level 27)
    // Drop table source: https://oldschool.runescape.wiki/w/Black_unicorn
    // Always: Bones, Unicorn horn
    // Main drops: None (black unicorn only has guaranteed drops)
    // -----------------------------------------------------------------------
    private fun registerBlackUnicorn(registry: NpcDropTableRegistry) {
        val blackUnicornTable = dropTable {
            always(objs.bones)
            always(UnicornObjs.unicorn_horn)

            // Black unicorns have no random loot table - only guaranteed drops
        }

        registry.register(UnicornNpcs.black_unicorn, blackUnicornTable)
    }
}

/** NPC type references for Unicorn variants. */
internal object UnicornNpcs : NpcReferences() {
    // Standard unicorn - level 15, found in Barbarian Village pen
    val unicorn = find("unicorn")

    // Black unicorn - level 27, found in Wilderness
    val black_unicorn = find("black_unicorn")
}

/** Object type references for Unicorn drops not in BaseObjs. */
internal object UnicornObjs : ObjReferences() {
    // Unicorn horn - 100% drop from both unicorn types
    val unicorn_horn = find("unicorn_horn")
}
