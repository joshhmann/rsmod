package org.rsmod.content.mechanics.poison.configs

import org.rsmod.api.type.refs.varp.VarpReferences

typealias poison_varps = PoisonVarps

object PoisonVarps : VarpReferences() {
    val poison_damage = find("poison_2")
    val venom_damage = find("pk_prey1")
    val poison_sub_tick = find("pk_prey2")
    val poison_immunity_ticks = find("pk_predator1")
    val venom_immunity_ticks = find("pk_predator2")
    val hp_orb_toxin = find("hp_orb_toxin", 102)
}
