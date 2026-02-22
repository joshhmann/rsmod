package org.rsmod.api.drop.table

import org.rsmod.api.random.GameRandom
import org.rsmod.game.type.obj.ObjType

/**
 * A complete drop table for one NPC type. Roll order:
 * 1. Every [always] entry is dropped unconditionally.
 * 2. One [WeightedTable] is selected from [tables] proportionally to its [WeightedTable.weight].
 *    Within that table one [TableEntry] is selected the same way. A [TableEntry.Nothing] entry
 *    means no item is dropped for that roll.
 *
 * If [tables] is empty only the [always] items are dropped.
 */
public data class NpcDropTable(
    public val always: List<AlwaysDrop>,
    public val tables: List<WeightedTable>,
) {
    /** Pre-computed total weight across all [tables] for fast rolling. */
    public val totalTableWeight: Int = tables.sumOf { it.weight }

    /**
     * Roll the table and return the list of [DroppedItem] instances. Always-drops are always
     * included. One weighted table roll is performed if [tables] is non-empty.
     */
    public fun roll(random: GameRandom): List<DroppedItem> {
        val results = mutableListOf<DroppedItem>()

        // Guaranteed drops — always included.
        for (drop in always) {
            val qty =
                if (drop.minQuantity == drop.maxQuantity) {
                    drop.minQuantity
                } else {
                    random.of(drop.minQuantity, drop.maxQuantity)
                }
            results += DroppedItem(drop.obj, qty)
        }

        // Weighted table roll — pick one table, then one entry inside it.
        if (tables.isNotEmpty() && totalTableWeight > 0) {
            val tableRoll = random.of(1, totalTableWeight)
            var cumulative = 0
            for (table in tables) {
                cumulative += table.weight
                if (tableRoll <= cumulative) {
                    val entry = table.roll(random)
                    if (entry is TableEntry.Item) {
                        val qty =
                            if (entry.minQuantity == entry.maxQuantity) {
                                entry.minQuantity
                            } else {
                                random.of(entry.minQuantity, entry.maxQuantity)
                            }
                        results += DroppedItem(entry.obj, qty)
                    }
                    // TableEntry.Nothing — intentionally nothing added.
                    break
                }
            }
        }

        return results
    }
}

/** An item that is always dropped when the NPC dies. */
public data class AlwaysDrop(
    public val obj: ObjType,
    public val minQuantity: Int,
    public val maxQuantity: Int,
)

/**
 * A named group of [TableEntry] items, selected as a unit during one drop roll. The probability of
 * selecting this table is proportional to [weight] vs the sum of all table weights in the parent
 * [NpcDropTable].
 */
public data class WeightedTable(
    public val name: String,
    public val weight: Int,
    public val entries: List<TableEntry>,
) {
    /** Pre-computed total weight of non-nothing entries. */
    public val totalEntryWeight: Int = entries.sumOf { it.weight }

    /** Roll one entry from this table. Returns [TableEntry.Nothing] if the table is empty. */
    public fun roll(random: GameRandom): TableEntry {
        if (entries.isEmpty() || totalEntryWeight <= 0) return TableEntry.Nothing(weight = 1)
        val roll = random.of(1, totalEntryWeight)
        var cumulative = 0
        for (entry in entries) {
            cumulative += entry.weight
            if (roll <= cumulative) return entry
        }
        // Should not be reached, but return last entry as fallback.
        return entries.last()
    }
}

/** An entry inside a [WeightedTable]. Either an actual item or an empty slot. */
public sealed class TableEntry {
    public abstract val weight: Int

    /** A concrete item entry. */
    public data class Item(
        public val obj: ObjType,
        public val minQuantity: Int,
        public val maxQuantity: Int,
        public override val weight: Int,
    ) : TableEntry()

    /**
     * An explicit "nothing" slot. Including this in a table reduces the probability of obtaining
     * any item from that table roll, effectively making the drop rare.
     */
    public data class Nothing(public override val weight: Int) : TableEntry()
}

/** Describes a single item resolved from a table roll, ready to be spawned as a ground item. */
public data class DroppedItem(public val obj: ObjType, public val quantity: Int)

// ---------------------------------------------------------------------------
// DSL builder
// ---------------------------------------------------------------------------

/** Top-level DSL entry point. */
public fun dropTable(init: DropTableBuilder.() -> Unit): NpcDropTable =
    DropTableBuilder().apply(init).build()

public class DropTableBuilder {
    private val alwaysList = mutableListOf<AlwaysDrop>()
    private val tableList = mutableListOf<WeightedTable>()

    /** Register an always-drop item with a fixed quantity. */
    public fun always(obj: ObjType, quantity: Int = 1) {
        alwaysList += AlwaysDrop(obj, quantity, quantity)
    }

    /** Register an always-drop item with a quantity range. */
    public fun always(obj: ObjType, quantity: IntRange) {
        alwaysList += AlwaysDrop(obj, quantity.first, quantity.last)
    }

    /**
     * Register a weighted table. The [weight] controls how likely this table is to be selected
     * relative to other tables in the same [NpcDropTable]. Use [WeightedTableBuilder] to add items
     * or nothing-slots inside.
     */
    public fun table(name: String, weight: Int, init: WeightedTableBuilder.() -> Unit) {
        val builder = WeightedTableBuilder().apply(init)
        tableList += WeightedTable(name, weight, builder.entries())
    }

    public fun build(): NpcDropTable = NpcDropTable(alwaysList.toList(), tableList.toList())
}

public class WeightedTableBuilder {
    private val entryList = mutableListOf<TableEntry>()

    /** Add an item with a fixed quantity and a given selection weight within this table. */
    public fun item(obj: ObjType, quantity: Int = 1, weight: Int = 1) {
        entryList += TableEntry.Item(obj, quantity, quantity, weight)
    }

    /** Add an item with a quantity range and a given selection weight within this table. */
    public fun item(obj: ObjType, quantity: IntRange, weight: Int = 1) {
        entryList += TableEntry.Item(obj, quantity.first, quantity.last, weight)
    }

    /**
     * Add a nothing-slot with the given [weight]. This acts as an "empty" result — if it is
     * selected, no item is dropped from this table roll.
     */
    public fun nothing(weight: Int) {
        entryList += TableEntry.Nothing(weight)
    }

    public fun entries(): List<TableEntry> = entryList.toList()
}
