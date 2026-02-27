package org.rsmod.content.other.special.attacks.configs

import org.rsmod.api.type.refs.spot.SpotanimReferences

typealias special_spots = SpecialAttackSpotanims

object SpecialAttackSpotanims : SpotanimReferences() {
    val lumber_up_red = find("dragon_smallaxe_swoosh_spotanim")
    val lumber_up_silver = find("crystal_smallaxe_swoosh_spotanim")
    val fishstabber_silver = find("sp_attackglow_crystal")
    val dragon_longsword = find("sp_attack_cleave_spotanim")

    // Dragon dagger special attack spotanim
    val puncture = find("sp_attack_puncture_spotanim")
}
