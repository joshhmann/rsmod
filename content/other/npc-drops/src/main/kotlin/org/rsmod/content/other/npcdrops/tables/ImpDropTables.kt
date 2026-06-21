package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Drop table registrations for Imp.
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 * - Existing inline handler preserved and enriched
 *
 * Enriched from corpus June 2026: added bead drops (Imp Catcher quest items),
 * food/misc table (egg, raw chicken, burnt bread/meat, cabbage, bread),
 * tool table (hammer, tinderbox, shears, bucket, jug, pot, ball of wool),
 * expanded coin entries, mind talisman, ashes, clay, cadava berries, grain.
 *
 * Skipped: bronze bolts, blue wizard hat (not in rev 233 cache),
 * flyer, potion apothecary (not in cache),
 * looting bag, ecumenical key (wilderness-only, post-2013),
 * imp champion scroll (not in cache),
 * fiendish ashes (post-rev-233 update).
 */
internal object ImpDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerImp(registry)
    }

    private fun registerImp(registry: NpcDropTableRegistry) {
        val table = dropTable {
            // Imp has no guaranteed drops in rev 233 era

            // Beads (Imp Catcher quest items)
            table("Beads", weight = 1) {
                item(ImpObjs.black_bead, weight = 5)
                item(ImpObjs.red_bead, weight = 5)
                item(ImpObjs.white_bead, weight = 5)
                item(ImpObjs.yellow_bead, weight = 5)
            }

            // Food / Misc
            table("Food/Misc", weight = 1) {
                item(objs.egg, weight = 5)
                item(ImpObjs.raw_chicken, weight = 5)
                item(ImpObjs.burnt_bread, weight = 4)
                item(ImpObjs.burnt_meat, weight = 4)
                item(objs.cabbage, weight = 2)
                item(ImpObjs.bread_dough, weight = 2)
                item(objs.bread, weight = 1)
                item(objs.cooked_meat, weight = 1)
                item(ImpObjs.cadavaberries, weight = 4)
                item(objs.grain, weight = 3)
            }

            // Tools / Items
            table("Tools/Items", weight = 1) {
                item(objs.hammer, weight = 8)
                item(objs.tinderbox, weight = 5)
                item(objs.shears, weight = 4)
                item(ImpObjs.bucket_empty, weight = 4)
                item(ImpObjs.jug_empty, weight = 2)
                item(ImpObjs.pot_empty, weight = 2)
                item(ImpObjs.ball_of_wool, weight = 8)
                item(ImpObjs.mind_talisman, weight = 7)
                item(objs.ashes, weight = 6)
                item(objs.clay, weight = 4)
                item(ImpObjs.chefs_hat, weight = 2)
            }

            // Coins
            table("Coins", weight = 1) {
                item(objs.coins, quantity = 1..10, weight = 20)
            }
        }

        registry.register(ImpNpcs.imp, table)
    }
}

internal object ImpNpcs : NpcReferences() {
    val imp = find("imp")
}

internal object ImpObjs : ObjReferences() {
    val black_bead = find("black_bead")
    val red_bead = find("red_bead")
    val white_bead = find("white_bead")
    val yellow_bead = find("yellow_bead")
    val raw_chicken = find("raw_chicken")
    val burnt_bread = find("burnt_bread")
    val burnt_meat = find("burnt_meat")
    val bread_dough = find("bread_dough")
    val cadavaberries = find("cadavaberries")
    val bucket_empty = find("bucket_empty")
    val jug_empty = find("jug_empty")
    val pot_empty = find("pot_empty")
    val ball_of_wool = find("ball_of_wool")
    val mind_talisman = find("mind_talisman")
    val chefs_hat = find("chefs_hat")
}
