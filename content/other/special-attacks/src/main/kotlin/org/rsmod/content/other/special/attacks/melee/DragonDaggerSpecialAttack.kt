package org.rsmod.content.other.special.attacks.melee

import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.config.constants
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.specials.SpecialAttackManager
import org.rsmod.api.specials.SpecialAttackMap
import org.rsmod.api.specials.SpecialAttackRepository
import org.rsmod.api.specials.combat.MeleeSpecialAttack
import org.rsmod.content.other.special.attacks.configs.special_objs
import org.rsmod.content.other.special.attacks.configs.special_seqs
import org.rsmod.content.other.special.attacks.configs.special_spots
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player

class DragonDaggerSpecialAttack : SpecialAttackMap {
    override fun SpecialAttackRepository.register(manager: SpecialAttackManager) {
        registerMelee(special_objs.dragon_dagger, DragonDagger(manager))
        registerMelee(special_objs.dragon_dagger_p, DragonDagger(manager))
        registerMelee(special_objs.`dragon_dagger_p+`, DragonDagger(manager))
        registerMelee(special_objs.`dragon_dagger_p++`, DragonDagger(manager))
    }

    private class DragonDagger(private val manager: SpecialAttackManager) : MeleeSpecialAttack {
        override suspend fun ProtectedAccess.attack(
            target: Npc,
            attack: CombatAttack.Melee,
        ): Boolean {
            puncture(target, attack)
            return true
        }

        override suspend fun ProtectedAccess.attack(
            target: Player,
            attack: CombatAttack.Melee,
        ): Boolean {
            puncture(target, attack)
            return true
        }

        private fun ProtectedAccess.puncture(target: PathingEntity, attack: CombatAttack.Melee) {
            anim(special_seqs.puncture)
            spotanim(
                spot = special_spots.puncture,
                slot = constants.spotanim_slot_combat,
                height = 96,
            )

            // First hit - rolls with 15% accuracy boost and 15% damage boost
            val firstDamage =
                manager.rollMeleeDamage(
                    source = this,
                    target = target,
                    attack = attack,
                    accuracyMultiplier = 1.15,
                    maxHitMultiplier = 1.15,
                    blockType = MeleeAttackType.Stab,
                )
            manager.giveCombatXp(this, target, attack, firstDamage)
            manager.queueMeleeHit(this, target, firstDamage)

            // Second hit - rolls with 15% accuracy boost and 15% damage boost
            val secondDamage =
                manager.rollMeleeDamage(
                    source = this,
                    target = target,
                    attack = attack,
                    accuracyMultiplier = 1.15,
                    maxHitMultiplier = 1.15,
                    blockType = MeleeAttackType.Stab,
                )
            manager.giveCombatXp(this, target, attack, secondDamage)
            manager.queueMeleeHit(this, target, secondDamage)

            manager.continueCombat(this, target)
        }
    }
}
