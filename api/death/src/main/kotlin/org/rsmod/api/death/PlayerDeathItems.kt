package org.rsmod.api.death

import jakarta.inject.Inject
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.market.MarketPrices
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.InvObj
import org.rsmod.game.type.obj.ObjTypeList

public class PlayerDeathItems
@Inject
constructor(private val objTypes: ObjTypeList, private val marketPrices: MarketPrices) {

    public fun calculateDeathItems(player: Player): DeathItems {
        val carried = sortedCarriedObjs(player).toMutableList()
        val keepCount = calculateKeepCount(player)
        val (kept, lost) = carried.partition(keepCount)
        return DeathItems(kept, lost)
    }

    private fun sortedCarriedObjs(player: Player): Sequence<InvObj> {
        val overall = player.inv.filterNotNull() + player.worn.filterNotNull()
        return overall
            .asSequence()
            .filterNot { objTypes[it].param(params.bond_item) }
            .sortedByDescending(::marketPriceSingle)
    }

    private fun MutableList<InvObj>.partition(keepCount: Int): Pair<List<InvObj>, List<InvObj>> {
        val safeKeepCount = keepCount.coerceAtLeast(0)
        val kept = take(safeKeepCount)
        val lost = drop(safeKeepCount)
        return kept to lost
    }

    private fun calculateKeepCount(player: Player): Int {
        val skullActive = player.skullIcon != null
        val protectItemPrayer = player.vars[varbits.prayer_protectitem] != 0
        var keep = if (skullActive) 0 else 3
        if (protectItemPrayer) {
            keep++
        }
        return keep
    }

    private fun marketPriceSingle(obj: InvObj?): Long {
        if (obj == null) {
            return 0
        }
        val type = objTypes[obj]
        val price = marketPrices[type] ?: type.cost
        return price.toLong()
    }

    public data class DeathItems(val kept: List<InvObj>, val lost: List<InvObj>)
}
