package org.rsmod.content.areas.city.varrock.configs

import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias varrock_varbits = VarrockVarBits

object VarrockVarBits : VarBitReferences() {
    val adventurepath_combat_free_potion_reward = find("adventurepath_combat_free_potion_reward")

    /* Compatibility aliases */
    val adventurepath_combat_reward_claimed
        get() = adventurepath_combat_free_potion_reward
}
