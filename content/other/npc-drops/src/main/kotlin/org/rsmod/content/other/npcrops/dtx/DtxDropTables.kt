package org.rsmod.content.other.npcrops.dtx

import dtx.core.ArgMap
import dtx.core.RollResult
import dtx.core.Rollable
import dtx.core.Single
import dtx.core.flattenToList
import dtx.impl.chance.ChanceRollable
import dtx.impl.chance.ChanceRollableImpl
import dtx.impl.chance.MultiChanceTable
import dtx.impl.chance.MultiChanceTableBuilder
import dtx.impl.chance.MultiChanceTableImpl
import dtx.impl.misc.Percent
import dtx.impl.weighted.WeightedTableBuilder
import dtx.table.AbstractTableBuilder
import dtx.table.DefaultTableHooksBuilder
import dtx.table.Table
import dtx.table.TableHooks
import org.rsmod.api.drop.table.DroppedItem
import org.rsmod.api.drop.table.NpcDropTable
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.random.GameRandom
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.ObjType

// ─────────────────────────────────────────────────────────────────────────────
// Core RSMod drop item model
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A drop result item produced by a DTX table roll. [qty] may be a fixed value or drawn from
 * [minQty]..[maxQty] each roll.
 */
data class Drop(val obj: ObjType, val qty: Int = 1, val minQty: Int = qty, val maxQty: Int = qty) {
    companion object {
        operator fun invoke(obj: ObjType, qty: Int = 1) = Drop(obj, qty, qty, qty)

        operator fun invoke(obj: ObjType, range: IntRange) =
            Drop(obj, range.first, range.first, range.last)
    }

    fun resolve(random: GameRandom): DroppedItem {
        val amount = if (minQty == maxQty) minQty else random.of(minQty, maxQty)
        return DroppedItem(obj, amount)
    }
}

/** Convenience infix to create a ranged [Drop]. */
infix fun ObjType.qty(range: IntRange): Drop = Drop(this, range)

// ─────────────────────────────────────────────────────────────────────────────
// Context passed through DTX rolls (target type)
// ─────────────────────────────────────────────────────────────────────────────

/** Roll context — carries the [GameRandom] instance through the DTX roll chain. */
data class RollContext(val random: GameRandom)

// ─────────────────────────────────────────────────────────────────────────────
// RS-style table interfaces (mirrors DTX example rs_tables structure)
// ─────────────────────────────────────────────────────────────────────────────

interface RSTable<T, R> : Table<T, R>

// ─────────────────────────────────────────────────────────────────────────────
// RSGuaranteedTable — drops every entry unconditionally
// ─────────────────────────────────────────────────────────────────────────────

class RSGuaranteedTable<T, R>(
    tableIdentifier: String,
    tableEntries: Collection<Rollable<T, R>>,
    tableHooks: TableHooks<T, R> = TableHooks.Default(),
) :
    RSTable<T, R>,
    MultiChanceTable<T, R> by MultiChanceTableImpl(
        tableIdentifier,
        tableEntries.map { ChanceRollableImpl(100.0, it) },
        tableHooks,
    ) {
    companion object {
        val EmptyTable = RSGuaranteedTable<Any?, Any?>("", emptyList())

        fun <T, R> Empty() = EmptyTable as RSGuaranteedTable<T, R>
    }
}

class RSGuaranteedTableBuilder<T, R> :
    AbstractTableBuilder<
        T,
        R,
        Rollable<T, R>,
        RSGuaranteedTable<T, R>,
        TableHooks<T, R>,
        DefaultTableHooksBuilder<T, R>,
        RSGuaranteedTableBuilder<T, R>,
    >(DefaultTableHooksBuilder.new()) {

    protected override val entries: MutableCollection<Rollable<T, R>> = mutableListOf()

    fun add(item: R): RSGuaranteedTableBuilder<T, R> {
        addEntry(Single(item))
        return this
    }

    fun add(rollable: Rollable<T, R>): RSGuaranteedTableBuilder<T, R> {
        addEntry(rollable)
        return this
    }

    init {
        construct { RSGuaranteedTable(tableIdentifier, entries, hooks.build()) }
    }
}

fun <T, R> rsGuaranteedTable(
    block: RSGuaranteedTableBuilder<T, R>.() -> Unit
): RSGuaranteedTable<T, R> = RSGuaranteedTableBuilder<T, R>().apply(block).build()

// ─────────────────────────────────────────────────────────────────────────────
// RSWeightedTable — pick one entry proportionally by weight
// ─────────────────────────────────────────────────────────────────────────────

class RSWeightedTable<T, R>(
    tableIdentifier: String,
    tableEntries: Collection<dtx.impl.weighted.WeightedRollable<T, R>>,
    tableHooks: TableHooks<T, R> = TableHooks.Default(),
) :
    RSTable<T, R>,
    dtx.impl.weighted.WeightedTable<T, R> by dtx.impl.weighted.WeightedTableImpl(
        tableIdentifier,
        tableEntries.toList(),
        tableHooks,
    ) {
    companion object {
        val EmptyTable = RSWeightedTable<Any?, Any?>("", emptyList())

        fun <T, R> Empty() = EmptyTable as RSWeightedTable<T, R>
    }
}

class RSWeightedTableBuilder<T, R> :
    dtx.impl.weighted.WeightedTableBuilder<T, R, RSWeightedTable<T, R>>(::RSWeightedTable)

fun <T, R> rsWeightedTable(block: RSWeightedTableBuilder<T, R>.() -> Unit): RSWeightedTable<T, R> =
    RSWeightedTableBuilder<T, R>().apply(block).build()

// ─────────────────────────────────────────────────────────────────────────────
// RSPreRollTable / Tertiary — each entry has its own independent chance
// ─────────────────────────────────────────────────────────────────────────────

class RSPreRollTable<T, R>(
    tableIdentifier: String,
    tableEntries: List<ChanceRollable<T, R>>,
    tableHooks: TableHooks<T, R> = TableHooks.Default(),
) :
    RSTable<T, R>,
    MultiChanceTable<T, R> by MultiChanceTableImpl(tableIdentifier, tableEntries, tableHooks) {
    companion object {
        val EmptyTable = RSPreRollTable<Any?, Any?>("", emptyList())

        fun <T, R> Empty() = EmptyTable as RSPreRollTable<T, R>
    }
}

class RSPrerollTableBuilder<T, R> : MultiChanceTableBuilder<T, R>() {
    infix fun Int.outOf(other: Int) = Percent((toDouble() / other.toDouble()) * 100.0)

    init {
        construct { RSPreRollTable<T, R>(tableIdentifier, entries, hooks.build()) }
    }
}

fun <T, R> rsTertiaryTable(block: RSPrerollTableBuilder<T, R>.() -> Unit): RSPreRollTable<T, R> {
    val builder = RSPrerollTableBuilder<T, R>()
    builder.apply(block)
    return builder.build() as RSPreRollTable<T, R>
}

// ─────────────────────────────────────────────────────────────────────────────
// RSDropTable — combines all layers into one full OSRS-style drop table
// ─────────────────────────────────────────────────────────────────────────────

class RSDropTable<T, R>(
    override val tableIdentifier: String,
    private val guaranteed: RSTable<T, R> = RSGuaranteedTable.Empty(),
    private val preRoll: RSTable<T, R> = RSPreRollTable.Empty(),
    private val mainTable: RSTable<T, R> = RSWeightedTable.Empty(),
    private val tertiaries: RSTable<T, R> = RSPreRollTable.Empty(),
    private val hooks: TableHooks<T, R> = TableHooks.Default(),
) : RSTable<T, R>, TableHooks<T, R> by hooks {
    override val tableEntries: Collection<Rollable<T, R>> =
        listOf(guaranteed, preRoll, mainTable, tertiaries)

    override fun selectResult(target: T, otherArgs: ArgMap): RollResult<R> {
        val results = mutableListOf<RollResult<R>>()
        results.add(guaranteed.roll(target, otherArgs))
        results.add(preRoll.roll(target, otherArgs))
        results.add(mainTable.roll(target, otherArgs))
        results.add(tertiaries.roll(target, otherArgs))
        return results.flattenToList()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bridge: DTX RSDropTable<RollContext, Drop> → RSMod NpcDropTableRegistry
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Converts a DTX [RSDropTable] of [Drop] items into RSMod's [NpcDropTable] and registers it.
 *
 * Usage:
 * ```
 * registry.registerDtx(npcs.goblin, "Goblin",
 *     guaranteed = rsGuaranteedTable { add(Drop(objs.bones)) },
 *     mainTable = rsWeightedTable {
 *         10 weight Drop(objs.coins, 1..25)
 *         5 weight Drop(objs.bronze_arrow)
 *         1 weight Drop(objs.nothing)
 *     }
 * )
 * ```
 */
fun NpcDropTableRegistry.registerDtx(
    npcType: NpcType,
    label: String = npcType.internalName ?: "",
    guaranteed: RSTable<RollContext, Drop> = RSGuaranteedTable.Empty(),
    preRoll: RSTable<RollContext, Drop> = RSPreRollTable.Empty(),
    mainTable: RSTable<RollContext, Drop> = RSWeightedTable.Empty(),
    tertiaries: RSTable<RollContext, Drop> = RSPreRollTable.Empty(),
) {
    val dtxTable = RSDropTable(label, guaranteed, preRoll, mainTable, tertiaries)
    register(npcType, DtxWrappedDropTable(dtxTable))
}

/**
 * Adapts a DTX [RSDropTable] into RSMod's [NpcDropTable] interface.
 *
 * [NpcDropTable.roll] is called by [org.rsmod.api.death.NpcDeath] passing a [GameRandom]; we
 * forward it into the DTX table as a [RollContext] and unwrap the resulting [Drop]s.
 */
class DtxWrappedDropTable(private val dtxTable: RSDropTable<RollContext, Drop>) :
    NpcDropTable(always = emptyList(), tables = emptyList()) {

    override fun roll(random: GameRandom): List<DroppedItem> {
        val ctx = RollContext(random)
        return when (val result = dtxTable.roll(ctx)) {
            is RollResult.Nothing -> emptyList()
            is RollResult.Single -> listOf(result.result.resolve(random))
            is RollResult.ListOf -> result.results.map { it.resolve(random) }
        }
    }
}
