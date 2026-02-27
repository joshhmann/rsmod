package org.rsmod.content.skills.magic.spell.attacks.standard

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.manager.MagicRuneManager.Companion.isFailure
import org.rsmod.api.config.refs.categories
import org.rsmod.api.config.refs.projanims
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.spotanims
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.magicLvl
import org.rsmod.api.spells.attack.SpellAttack
import org.rsmod.api.spells.attack.SpellAttackManager
import org.rsmod.api.spells.attack.SpellAttackMap
import org.rsmod.api.spells.attack.SpellAttackRepository
import org.rsmod.content.mechanics.statuseffects.StatusEffectController
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.content.skills.magic.spell.attacks.standard.StandardSpellObjs as spellObjs

class BindingSpells
@Inject
constructor(private val objTypes: ObjTypeList, private val statusEffects: StatusEffectController) :
    SpellAttackMap {
    override fun SpellAttackRepository.register(manager: SpellAttackManager) {
        register(
            spell = spellObjs.spell_bind,
            attack =
                BindingSpellAttack(
                    manager = manager,
                    objTypes = objTypes,
                    statusEffects = statusEffects,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.windstrike_casting,
                    travel = spotanims.windstrike_travel,
                    impact = spotanims.stunned,
                    castSound = synths.bind_cast,
                    hitSound = synths.bind_impact,
                    freezeTicks = 8,
                    getMaxHit = { lvl -> if (lvl >= 20) 2 else 0 },
                ),
        )

        register(
            spell = spellObjs.spell_snare,
            attack =
                BindingSpellAttack(
                    manager = manager,
                    objTypes = objTypes,
                    statusEffects = statusEffects,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.waterstrike_casting,
                    travel = spotanims.waterstrike_travel,
                    impact = spotanims.stunned,
                    castSound = synths.snare_all,
                    hitSound = synths.snare_all,
                    freezeTicks = 17,
                    getMaxHit = { lvl -> if (lvl >= 50) 3 else 0 },
                ),
        )

        register(
            spell = spellObjs.spell_entangle,
            attack =
                BindingSpellAttack(
                    manager = manager,
                    objTypes = objTypes,
                    statusEffects = statusEffects,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.earthstrike_casting,
                    travel = spotanims.earthstrike_travel,
                    impact = spotanims.stunned,
                    castSound = synths.entangle_cast_and_fire,
                    hitSound = synths.entangle_hit,
                    freezeTicks = 33,
                    getMaxHit = { lvl -> if (lvl >= 79) 5 else 0 },
                ),
        )
    }

    private class BindingSpellAttack(
        private val manager: SpellAttackManager,
        private val objTypes: ObjTypeList,
        private val statusEffects: StatusEffectController,
        private val staffAnim: SeqType,
        private val unarmedAnim: SeqType,
        private val launch: SpotanimType,
        private val travel: SpotanimType,
        private val impact: SpotanimType,
        private val castSound: SynthType,
        private val hitSound: SynthType,
        private val freezeTicks: Int,
        private val getMaxHit: (Int) -> Int,
    ) : SpellAttack {
        override suspend fun ProtectedAccess.attack(target: Npc, attack: CombatAttack.Spell) {
            cast(target, attack)
        }

        override suspend fun ProtectedAccess.attack(target: Player, attack: CombatAttack.Spell) {
            cast(target, attack)
        }

        private fun ProtectedAccess.cast(target: PathingEntity, attack: CombatAttack.Spell) {
            val castResult = manager.attemptCast(this, attack)
            if (castResult.isFailure()) {
                return
            }

            val weaponType = objTypes.getOrNull(attack.weapon)
            val castAnim = weaponType.castStrikeAnim()
            anim(castAnim)
            spotanim(launch, height = 92)

            val proj = manager.spawnProjectile(this, target, travel, projanims.magic_spell)
            val (serverDelay, clientDelay) = proj.durations
            val spell = attack.spell.obj

            val splash = manager.rollSplash(this, target, attack, castResult)
            if (splash) {
                manager.playSplashFx(this, target, clientDelay, castSound, soundRadius = 8)
                manager.queueSplashHit(this, target, spell, clientDelay, serverDelay)
                manager.continueCombatIfAutocast(this, target)
                return
            }

            val baseMaxHit = getMaxHit(player.magicLvl)
            val damage = manager.rollMaxHit(this, target, attack, castResult, baseMaxHit)
            manager.playHitFx(
                source = this,
                target = target,
                clientDelay = clientDelay,
                castSound = castSound,
                soundRadius = 8,
                hitSpot = impact,
                hitSpotHeight = 124,
                hitSound = hitSound,
            )
            manager.giveCombatXp(this, target, attack, damage)
            manager.queueMagicHit(this, target, spell, damage, clientDelay, serverDelay)
            statusEffects.queueFreeze(this, target, hitDelay = serverDelay, duration = freezeTicks)
            manager.continueCombatIfAutocast(this, target)
        }

        private fun UnpackedObjType?.castStrikeAnim(): SeqType =
            if (this != null && isCategoryType(categories.staff)) {
                staffAnim
            } else {
                unarmedAnim
            }
    }
}
