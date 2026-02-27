package org.rsmod.api.combat.formulas.accuracy.ranged

import java.util.EnumSet
import kotlin.math.min
import org.rsmod.api.combat.accuracy.player.PlayerRangedAccuracy
import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatRangedAttributes
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.stat.defenceLvl
import org.rsmod.api.player.stat.rangedLvl
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.vars.VarPlayerIntMap

private typealias RangeAttr = CombatRangedAttributes

private typealias NpcAttr = CombatNpcAttributes

/** Data class representing attack roll modification state. */
private data class AttackRollState(val accumulated: Int)

/** Type alias for an attack roll modifier function. */
private typealias AttackRollModifier =
    (AttackRollState, EnumSet<RangeAttr>, EnumSet<NpcAttr>) -> AttackRollState

public object RangedAccuracyOperations {
    /**
     * @param targetMagic The target's magic level or magic bonus, whichever of the two is greater.
     *   Required for the Twisted bow modifier.
     * @param targetDistance The chebyshev distance between the attacker's south-west coord and
     *   target's south-west coord. Required for the Chinchompa-fuse modifier.
     */
    public fun modifyAttackRoll(
        attackRoll: Int,
        targetMagic: Int,
        targetDistance: Int,
        rangeAttributes: EnumSet<CombatRangedAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val initialState = AttackRollState(accumulated = attackRoll)

        val modifiers: List<AttackRollModifier> =
            listOf(
                { state, range, npc -> applyCrystalModifier(state, range, npc) },
                { state, range, npc -> applyAmuletModifier(state, range, npc) },
                { state, range, npc -> applyTwistedBowModifier(state, range, npc, targetMagic) },
                { state, range, npc -> applyRevenantWeaponModifier(state, range, npc) },
                { state, range, npc -> applyDragonbaneModifier(state, range, npc) },
                { state, range, npc -> applyFuseModifier(state, range, npc, targetDistance) },
                { state, range, npc -> applyScorchingBowModifier(state, range, npc) },
            )

        return modifiers
            .fold(initialState) { state, mod -> mod(state, rangeAttributes, npcAttributes) }
            .accumulated
    }

    private fun applyCrystalModifier(
        state: AttackRollState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
    ): AttackRollState {
        if (RangeAttr.CrystalBow !in range) {
            return state
        }
        val helmAdditive = if (RangeAttr.CrystalHelm in range) 1 else 0
        val bodyAdditive = if (RangeAttr.CrystalBody in range) 3 else 0
        val legsAdditive = if (RangeAttr.CrystalLegs in range) 2 else 0
        val armourAdditive = helmAdditive + bodyAdditive + legsAdditive
        return state.copy(
            accumulated = scale(state.accumulated, multiplier = 20 + armourAdditive, divisor = 20)
        )
    }

    private fun applyAmuletModifier(
        state: AttackRollState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
    ): AttackRollState {
        val newAccumulated =
            when {
                RangeAttr.AmuletOfAvarice in range && NpcAttr.Revenant in npc -> {
                    val multiplier = if (RangeAttr.ForinthrySurge in range) 27 else 24
                    scale(state.accumulated, multiplier, divisor = 20)
                }
                RangeAttr.SalveAmuletEi in range && NpcAttr.Undead in npc -> {
                    scale(state.accumulated, multiplier = 6, divisor = 5)
                }
                RangeAttr.SalveAmuletI in range && NpcAttr.Undead in npc -> {
                    scale(state.accumulated, multiplier = 7, divisor = 6)
                }
                RangeAttr.BlackMaskI in range && NpcAttr.SlayerTask in npc -> {
                    scale(state.accumulated, multiplier = 23, divisor = 20)
                }
                else -> state.accumulated
            }
        return state.copy(accumulated = newAccumulated)
    }

    private fun applyTwistedBowModifier(
        state: AttackRollState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
        targetMagic: Int,
    ): AttackRollState {
        if (RangeAttr.TwistedBow !in range) {
            return state
        }
        val cap = if (NpcAttr.Xerician in npc) 350 else 250
        val magic = min(cap, targetMagic)

        val factor = 10
        val base = 140

        val linearBonus = (3 * magic - factor) / 100
        val deviation = (3 * magic / 10) - (10 * factor)
        val quadraticPenalty = (deviation * deviation) / 100

        val multiplier = base + linearBonus - quadraticPenalty
        return state.copy(accumulated = scale(state.accumulated, multiplier, divisor = 100))
    }

    private fun applyRevenantWeaponModifier(
        state: AttackRollState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
    ): AttackRollState {
        if (RangeAttr.RevenantWeapon !in range || NpcAttr.Wilderness !in npc) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 3, divisor = 2))
    }

    private fun applyDragonbaneModifier(
        state: AttackRollState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
    ): AttackRollState {
        val applies = RangeAttr.DragonHunterCrossbow in range && NpcAttr.Draconic in npc
        if (!applies) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 13, divisor = 10))
    }

    private fun applyFuseModifier(
        state: AttackRollState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
        targetDistance: Int,
    ): AttackRollState {
        val multiplier =
            when {
                RangeAttr.ShortFuse in range -> {
                    when {
                        targetDistance >= 7 -> 2
                        targetDistance >= 4 -> 3
                        else -> 4
                    }
                }
                RangeAttr.MediumFuse in range -> {
                    if (targetDistance < 4 || targetDistance >= 7) 3 else 4
                }
                RangeAttr.LongFuse in range -> {
                    when {
                        targetDistance < 4 -> 2
                        targetDistance < 7 -> 3
                        else -> 4
                    }
                }
                else -> return state
            }
        return state.copy(accumulated = scale(state.accumulated, multiplier, divisor = 4))
    }

    private fun applyScorchingBowModifier(
        state: AttackRollState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
    ): AttackRollState {
        if (RangeAttr.ScorchingBow !in range || NpcAttr.Demon !in npc) {
            return state
        }
        val newAccumulated =
            if (NpcAttr.DemonbaneResistance in npc) {
                scale(state.accumulated, multiplier = 121, divisor = 100)
            } else {
                scale(state.accumulated, multiplier = 130, divisor = 100)
            }
        return state.copy(accumulated = newAccumulated)
    }

    public fun modifyAttackRoll(
        attackRoll: Int,
        targetDistance: Int,
        rangeAttributes: EnumSet<CombatRangedAttributes>,
    ): Int {
        val initialState = AttackRollState(accumulated = attackRoll)

        val modifiers: List<AttackRollModifier> =
            listOf(
                { state, range, npc -> applyCrystalModifier(state, range, npc) },
                { state, range, npc -> applyFuseModifier(state, range, npc, targetDistance) },
            )

        return modifiers
            .fold(initialState) { state, mod ->
                mod(state, rangeAttributes, EnumSet.noneOf(NpcAttr::class.java))
            }
            .accumulated
    }

    public fun calculateEffectiveRanged(player: Player, attackStyle: RangedAttackStyle?): Int =
        calculateEffectiveRanged(
            visLevel = player.rangedLvl,
            vars = player.vars,
            worn = player.worn,
            attackStyle = attackStyle,
        )

    private fun calculateEffectiveRanged(
        visLevel: Int,
        vars: VarPlayerIntMap,
        worn: Inventory,
        attackStyle: RangedAttackStyle?,
    ): Int {
        val styleBonus = attackStyle.offensiveStyleBonus()
        val prayerBonus = vars.offensivePrayerBonus()
        val voidBonus = worn.offensiveVoidBonus()
        return PlayerRangedAccuracy.calculateEffectiveRanged(
            visibleRangedLvl = visLevel,
            styleBonus = styleBonus,
            prayerBonus = prayerBonus,
            voidBonus = voidBonus,
        )
    }

    private fun RangedAttackStyle?.offensiveStyleBonus(): Int =
        when (this) {
            RangedAttackStyle.Accurate -> 11
            else -> 8
        }

    private fun VarPlayerIntMap.offensivePrayerBonus(): Double =
        when {
            this[varbits.sharp_eye] == 1 -> 1.05
            this[varbits.hawk_eye] == 1 -> 1.1
            this[varbits.eagle_eye] == 1 -> {
                if (this[varbits.prayer_deadeye_unlocked] == 1) 1.18 else 1.15
            }
            this[varbits.rigour] == 1 -> 1.2
            else -> 1.0
        }

    private fun Inventory.offensiveVoidBonus(): Double {
        val helm = this[Wearpos.Hat.slot]
        if (!EquipmentChecks.isVoidRangerHelm(helm)) {
            return 1.0
        }

        val top = this[Wearpos.Torso.slot]
        if (!EquipmentChecks.isVoidTop(top)) {
            return 1.0
        }

        val legs = this[Wearpos.Legs.slot]
        if (!EquipmentChecks.isVoidRobe(legs)) {
            return 1.0
        }

        val gloves = this[Wearpos.Hands.slot]
        if (!EquipmentChecks.isVoidGloves(gloves)) {
            return 1.0
        }

        return 1.1
    }

    public fun calculateEffectiveDefence(player: Player, attackStyle: AttackStyle?): Int {
        val armourBonus = AccuracyOperations.defensiveArmourBonus(player)
        return calculateEffectiveDefence(
            visLevel = player.defenceLvl,
            armourBonus = armourBonus,
            vars = player.vars,
            attackStyle = attackStyle,
        )
    }

    private fun calculateEffectiveDefence(
        visLevel: Int,
        armourBonus: Double,
        vars: VarPlayerIntMap,
        attackStyle: AttackStyle?,
    ): Int {
        val styleBonus = AccuracyOperations.defensiveStyleBonus(attackStyle)
        val prayerBonus = AccuracyOperations.defensivePrayerBonus(vars)
        return PlayerRangedAccuracy.calculateEffectiveDefence(
            visibleDefenceLvl = visLevel,
            styleBonus = styleBonus,
            prayerBonus = prayerBonus,
            armourBonus = armourBonus,
        )
    }
}
