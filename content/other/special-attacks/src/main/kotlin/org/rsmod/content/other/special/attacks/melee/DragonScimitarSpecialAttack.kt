package org.rsmod.content.other.special.attacks.melee

import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.specials.SpecialAttackManager
import org.rsmod.api.specials.SpecialAttackMap
import org.rsmod.api.specials.SpecialAttackRepository
import org.rsmod.api.specials.combat.MeleeSpecialAttack
import org.rsmod.content.other.special.attacks.configs.special_objs
import org.rsmod.content.other.special.attacks.configs.special_seqs
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player

class DragonScimitarSpecialAttack : SpecialAttackMap {
    override fun SpecialAttackRepository.register(manager: SpecialAttackManager) {
        registerMelee(special_objs.dragon_scimitar, DragonScimitar(manager))
    }

    private class DragonScimitar(private val manager: SpecialAttackManager) : MeleeSpecialAttack {
        override suspend fun ProtectedAccess.attack(
            target: Npc,
            attack: CombatAttack.Melee,
        ): Boolean {
            sever(target, attack)
            return true
        }

        override suspend fun ProtectedAccess.attack(
            target: Player,
            attack: CombatAttack.Melee,
        ): Boolean {
            sever(target, attack)
            // In PvP, disable protection prayers for 8 ticks (4.8 seconds)
            disableProtectionPrayers(target)
            return true
        }

        private fun ProtectedAccess.sever(target: PathingEntity, attack: CombatAttack.Melee) {
            anim(special_seqs.dragon_scimitar)

            // Sever: 25% increased accuracy, rolls against slash defence
            val damage =
                manager.rollMeleeDamage(
                    source = this,
                    target = target,
                    attack = attack,
                    accuracyMultiplier = 1.25,
                    maxHitMultiplier = 1.0,
                    blockType = MeleeAttackType.Slash,
                )
            manager.giveCombatXp(this, target, attack, damage)
            manager.queueMeleeHit(this, target, damage)
            manager.continueCombat(this, target)
        }

        private fun disableProtectionPrayers(target: Player) {
            // TODO: Implement prayer disabling for PvP
            // This should disable Protect from Magic, Protect from Missiles, and Protect from Melee
            // for 8 ticks (4.8 seconds)
        }
    }
}
