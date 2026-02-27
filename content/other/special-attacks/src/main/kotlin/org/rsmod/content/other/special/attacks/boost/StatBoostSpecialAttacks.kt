package org.rsmod.content.other.special.attacks.boost

import jakarta.inject.Inject
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.spotanims
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.world.WorldRepository
import org.rsmod.api.specials.SpecialAttackManager
import org.rsmod.api.specials.SpecialAttackMap
import org.rsmod.api.specials.SpecialAttackRepository
import org.rsmod.content.other.special.attacks.configs.special_objs
import org.rsmod.content.other.special.attacks.configs.special_seqs
import org.rsmod.content.other.special.attacks.configs.special_spots
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType

class StatBoostSpecialAttacks @Inject constructor(private val worldRepo: WorldRepository) :
    SpecialAttackMap {
    override fun SpecialAttackRepository.register(manager: SpecialAttackManager) {
        registerInstant(special_objs.dragon_axe, ::lumberUpRed)
        registerInstant(special_objs.`3a_axe`, ::lumberUpSilver)
        registerInstant(special_objs.infernal_axe, ::lumberUpRed)
        registerInstant(special_objs.crystal_axe, ::lumberUpSilver)

        registerInstant(special_objs.dragon_harpoon, ::fishstabberDragonHarpoon)
        registerInstant(special_objs.infernal_harpoon, ::fishstabberInfernalHarpoon)
        registerInstant(special_objs.crystal_harpoon, ::fishstabberCrystalHarpoon)

        registerInstant(special_objs.dragon_pickaxe, ::rockKnockerDragonPickaxe)
        registerInstant(special_objs.dragon_pickaxe_or_zalcano, ::rockKnockerDragonPickaxeOrZalcano)
        registerInstant(
            special_objs.dragon_pickaxe_or_trailblazer,
            ::rockKnockerDragonPickaxeOrTrailblazer,
        )
        registerInstant(special_objs.infernal_pickaxe, ::rockKnockerInfernalPickaxe)
        registerInstant(special_objs.infernal_pickaxe_uncharged, ::rockKnockerInfernalPickaxe)
        registerInstant(special_objs.`3a_pickaxe`, ::rockKnockerThirdAgePickaxe)
        registerInstant(special_objs.crystal_pickaxe, ::rockKnockerCrystalPickaxe)
    }

    private fun lumberUpRed(access: ProtectedAccess): Boolean {
        return access.lumberUp(special_spots.lumber_up_red)
    }

    private fun lumberUpSilver(access: ProtectedAccess): Boolean {
        return access.lumberUp(special_spots.lumber_up_silver)
    }

    private fun ProtectedAccess.lumberUp(spot: SpotanimType): Boolean {
        statBoost(stats.woodcutting, constant = 3, percent = 0)
        say("Chop chop!")
        anim(special_seqs.lumber_up)
        spotanim(spot, height = 96, slot = constants.spotanim_slot_combat)
        soundArea(worldRepo, coords, synths.clobber, radius = 1)
        return true
    }

    private fun fishstabberDragonHarpoon(access: ProtectedAccess): Boolean {
        return access.fishstabber(
            special_seqs.fishstabber_dragon_harpoon,
            spotanims.sp_attackglow_red,
        )
    }

    private fun fishstabberDragonHarpoonOr(access: ProtectedAccess): Boolean {
        // Uses the same seq as Infernal harpoon (or).
        return access.fishstabber(
            special_seqs.fishstabber_infernal_harpoon_or,
            spotanims.sp_attackglow_red,
        )
    }

    private fun fishstabberInfernalHarpoon(access: ProtectedAccess): Boolean {
        return access.fishstabber(
            special_seqs.fishstabber_infernal_harpoon,
            spotanims.sp_attackglow_red,
        )
    }

    private fun fishstabberInfernalHarpoonOr(access: ProtectedAccess): Boolean {
        return access.fishstabber(
            special_seqs.fishstabber_infernal_harpoon_or,
            spotanims.sp_attackglow_red,
        )
    }

    private fun fishstabberCrystalHarpoon(access: ProtectedAccess): Boolean {
        return access.fishstabber(
            special_seqs.fishstabber_crystal_harpoon,
            special_spots.fishstabber_silver,
        )
    }

    private fun ProtectedAccess.fishstabber(seq: SeqType, spot: SpotanimType): Boolean {
        statBoost(stats.fishing, constant = 3, percent = 0)
        say("Here fishy fishies!")
        anim(seq)
        spotanim(spot)
        soundArea(worldRepo, coords, synths.rampage, radius = 1)
        return true
    }

    private fun rockKnockerDragonPickaxe(access: ProtectedAccess): Boolean {
        return access.rockKnocker(special_seqs.rock_knocker_dragon_pickaxe)
    }

    private fun rockKnockerDragonPickaxeOrTrailblazer(access: ProtectedAccess): Boolean {
        return access.rockKnocker(special_seqs.rock_knocker_dragon_pickaxe_or_trailblazer)
    }

    private fun rockKnockerDragonPickaxeOrZalcano(access: ProtectedAccess): Boolean {
        return access.rockKnocker(special_seqs.rock_knocker_dragon_pickaxe_or_zalcano)
    }

    private fun rockKnockerDragonPickaxeUpgraded(access: ProtectedAccess): Boolean {
        return access.rockKnocker(special_seqs.rock_knocker_dragon_pickaxe_upgraded)
    }

    private fun rockKnockerInfernalPickaxe(access: ProtectedAccess): Boolean {
        return access.rockKnocker(special_seqs.rock_knocker_infernal_pickaxe)
    }

    private fun rockKnockerInfernalPickaxeOr(access: ProtectedAccess): Boolean {
        // Uses same seq as Dragon pickaxe (or).
        return access.rockKnocker(special_seqs.rock_knocker_dragon_pickaxe_or_trailblazer)
    }

    private fun rockKnockerThirdAgePickaxe(access: ProtectedAccess): Boolean {
        return access.rockKnocker(special_seqs.rock_knocker_3rd_age_pickaxe)
    }

    private fun rockKnockerCrystalPickaxe(access: ProtectedAccess): Boolean {
        return access.rockKnocker(special_seqs.rock_knocker_crystal_pickaxe)
    }

    private fun ProtectedAccess.rockKnocker(seq: SeqType): Boolean {
        statBoost(stats.mining, constant = 3, percent = 0)
        say("Smashing!")
        anim(seq)
        soundArea(worldRepo, coords, synths.found_gem, radius = 1)
        return true
    }
}
