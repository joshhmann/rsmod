package org.rsmod.api.inv.weight

import kotlin.collections.iterator
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList

public object InvWeight {
    public fun calculateWeightInGrams(player: Player, objTypes: ObjTypeList): Int {
        return player.transmittedInvs.intIterator().asSequence().sumOf { transmitted ->
            val inv = player.invMap.backing[transmitted]
            checkNotNull(inv) { "Inv expected in `invMap`: $transmitted (invMap=${player.invMap})" }

            if (!inv.type.runWeight) {
                0
            } else {
                inv.indices.asSequence().sumOf { i ->
                    val obj = inv[i]
                    if (obj != null) objTypes[obj].weight else 0
                }
            }
        }
    }
}
