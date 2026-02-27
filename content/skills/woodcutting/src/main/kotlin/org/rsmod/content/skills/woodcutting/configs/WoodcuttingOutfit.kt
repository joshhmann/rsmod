package org.rsmod.content.skills.woodcutting.configs

import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.content.skills.woodcutting.configs.WoodcuttingObjs as wcObjs
import org.rsmod.game.type.obj.ObjType

internal object WoodcuttingOutfit : ObjEditor() {
    init {
        outfitXpMod(wcObjs.ramble_lumberjack_hat, percent = 4)
        outfitXpMod(wcObjs.forestry_lumberjack_hat, percent = 4)

        outfitXpMod(wcObjs.ramble_lumberjack_top, percent = 8)
        outfitXpMod(wcObjs.forestry_lumberjack_top, percent = 8)

        outfitXpMod(wcObjs.ramble_lumberjack_legs, percent = 6)
        outfitXpMod(wcObjs.forestry_lumberjack_legs, percent = 6)

        outfitXpMod(wcObjs.ramble_lumberjack_boots, percent = 2)
        outfitXpMod(wcObjs.forestry_lumberjack_boots, percent = 2)
    }

    private fun outfitXpMod(type: ObjType, percent: Int) {
        edit(type) {
            param[params.xpmod_stat] = stats.woodcutting
            param[params.xpmod_percent] = percent
        }
    }
}
