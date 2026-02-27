package org.rsmod.content.other.special.attacks.configs

import org.rsmod.api.type.refs.seq.SeqReferences

typealias special_seqs = SpecialAttackSeqs

object SpecialAttackSeqs : SeqReferences() {
    val lumber_up = find("dragon_smallaxe_anim")

    val fishstabber_dragon_harpoon = find("fishstabber")
    val fishstabber_infernal_harpoon = find("fishstabber_infernal")
    val fishstabber_crystal_harpoon = find("fishstabber_crystal")
    val fishstabber_infernal_harpoon_or = find("fishstabber_trailblazer")

    val rock_knocker_dragon_pickaxe = find("rockknocker")
    val rock_knocker_dragon_pickaxe_or_zalcano = find("rockknocker_zalcano")
    val rock_knocker_dragon_pickaxe_or_trailblazer = find("rockknocker_trailblazer")
    val rock_knocker_dragon_pickaxe_upgraded = find("rockknocker_pretty")
    val rock_knocker_infernal_pickaxe = find("rockknocker_infernal")
    val rock_knocker_3rd_age_pickaxe = find("rockknocker_3a")
    val rock_knocker_crystal_pickaxe = find("rockknocker_crystal")

    val dragon_longsword = find("cleave")

    // Dragon dagger special attack - Puncture
    val puncture = find("puncture")

    // Dragon scimitar special attack - Sever
    val dragon_scimitar = find("sp_attack_dragon_scimitar")
}
