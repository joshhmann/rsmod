package org.rsmod.game.dbtable

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntArraySet
import it.unimi.dsi.fastutil.ints.IntSet
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.dbrow.DbRowTypeList
import org.rsmod.game.type.dbtable.DbTableType
import org.rsmod.game.type.dbtable.DbTableTypeList

public class DbTableResolver(private val cacheTypes: TypeListMap) {
    private val tableRows by lazy { associateTableRows() }

    private val rows: DbRowTypeList by cacheTypes::dbRows
    private val tables: DbTableTypeList by cacheTypes::dbTables

    public operator fun get(table: DbTableType): List<DbRow> {
        val unpacked = tables.getOrNull(table)
        if (unpacked == null) {
            System.err.println("WARNING: DbTable missing in map: $table. Returning empty list.")
            return emptyList()
        }
        val rowList = tableRows[unpacked.id] ?: return emptyList()
        return rowList.map { DbRow(cacheTypes, unpacked, rows.getValue(it)) }
    }

    private fun associateTableRows(): Int2ObjectMap<IntSet> {
        val mapped = Int2ObjectOpenHashMap<IntSet>()
        for (row in rows.values) {
            val list = mapped.computeIfAbsent(row.table) { IntArraySet() }
            list.add(row.id)
        }
        return mapped
    }
}
