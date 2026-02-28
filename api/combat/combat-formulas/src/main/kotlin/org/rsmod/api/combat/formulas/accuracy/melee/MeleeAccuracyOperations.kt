package org.rsmod.api.combat.formulas.accuracy.melee

import java.util.EnumSet
import org.rsmod.api.combat.accuracy.player.PlayerMeleeAccuracy
import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.formulas.HIT_CHANCE_SCALE
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.formulas.attributes.CombatMeleeAttributes
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.stat.attackLvl
import org.rsmod.api.player.stat.defenceLvl
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.vars.VarPlayerIntMap

private typealias MeleeAttr = CombatMeleeAttributes

private typealias NpcAttr = CombatNpcAttributes

/**
 * Data class representing an accuracy roll modification state. Captures both the accumulated roll
 * and original base for modifiers that need it.
 */
private data class AttackRollState(val accumulated: Int, val base: Int)

/** Type alias for an attack roll modifier function. */
private typealias AttackRollModifier =
    (AttackRollState, EnumSet<MeleeAttr>, EnumSet<NpcAttr>) -> AttackRollState

public object MeleeAccuracyOperations {
    public fun modifyAttackRoll(
        attackRoll: Int,
        meleeAttributes: EnumSet<CombatMeleeAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val initialState = AttackRollState(accumulated = attackRoll, base = attackRoll)

        val modifiers: List<AttackRollModifier> =
            listOf(
                ::applyAmuletModifier,
                ::applyObsidianModifier,
                ::applyRevenantWeaponModifier,
                ::applyArclightModifier,
                ::applyBurningClawsModifier,
                ::applyDragonHunterModifier,
                ::applyKerisBreachModifier,
                ::applyKerisSunModifier,
                ::applyInquisitorModifier,
            )

        return modifiers
            .fold(initialState) { state, modifier ->
                modifier(state, meleeAttributes, npcAttributes)
            }
            .accumulated
    }

    private fun applyAmuletModifier(
        state: AttackRollState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): AttackRollState {
        val newAccumulated =
            when {
                MeleeAttr.AmuletOfAvarice in melee && NpcAttr.Revenant in npc -> {
                    val multiplier = if (MeleeAttr.ForinthrySurge in melee) 27 else 24
                    scale(state.base, multiplier, divisor = 20)
                }
                MeleeAttr.SalveAmuletE in melee && NpcAttr.Undead in npc -> {
                    scale(state.base, multiplier = 6, divisor = 5)
                }
                MeleeAttr.SalveAmulet in melee && NpcAttr.Undead in npc -> {
                    scale(state.base, multiplier = 7, divisor = 6)
                }
                MeleeAttr.BlackMask in melee && NpcAttr.SlayerTask in npc -> {
                    scale(state.base, multiplier = 7, divisor = 6)
                }
                else -> state.accumulated
            }
        return state.copy(accumulated = newAccumulated)
    }

    private fun applyObsidianModifier(
        state: AttackRollState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): AttackRollState {
        if (MeleeAttr.Obsidian !in melee || MeleeAttr.TzHaarWeapon !in melee) {
            return state
        }
        return state.copy(accumulated = state.accumulated + state.base / 10)
    }

    private fun applyRevenantWeaponModifier(
        state: AttackRollState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): AttackRollState {
        if (MeleeAttr.RevenantWeapon !in melee || NpcAttr.Wilderness !in npc) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 3, divisor = 2))
    }

    private fun applyArclightModifier(
        state: AttackRollState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): AttackRollState {
        if (MeleeAttr.Arclight !in melee || NpcAttr.Demon !in npc) {
            return state
        }
        val multiplier = if (NpcAttr.DemonbaneResistance in npc) 149 else 170
        return state.copy(accumulated = scale(state.accumulated, multiplier, divisor = 100))
    }

    private fun applyBurningClawsModifier(
        state: AttackRollState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): AttackRollState {
        if (MeleeAttr.BurningClaws !in melee || NpcAttr.Demon !in npc) {
            return state
        }
        val multiplier = if (NpcAttr.DemonbaneResistance in npc) 207 else 210
        return state.copy(accumulated = scale(state.accumulated, multiplier, divisor = 200))
    }

    private fun applyDragonHunterModifier(
        state: AttackRollState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): AttackRollState {
        if (NpcAttr.Draconic !in npc) {
            return state
        }
        return when {
            MeleeAttr.DragonHunterLance in melee -> {
                state.copy(accumulated = scale(state.accumulated, multiplier = 6, divisor = 5))
            }
            MeleeAttr.DragonHunterWand in melee -> {
                state.copy(accumulated = scale(state.accumulated, multiplier = 3, divisor = 2))
            }
            else -> state
        }
    }

    private fun applyKerisBreachModifier(
        state: AttackRollState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): AttackRollState {
        if (MeleeAttr.KerisBreachPartisan !in melee || NpcAttr.Kalphite !in npc) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 133, divisor = 100))
    }

    private fun applyKerisSunModifier(
        state: AttackRollState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): AttackRollState {
        if (MeleeAttr.KerisSunPartisan !in melee || NpcAttr.Amascut !in npc) {
            return state
        }
        if (NpcAttr.QuarterHealth !in npc) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 5, divisor = 4))
    }

    private fun applyInquisitorModifier(
        state: AttackRollState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): AttackRollState {
        if (MeleeAttr.Crush !in melee) {
            return state
        }

        val inquisitorPieces =
            (if (MeleeAttr.InquisitorHelm in melee) 1 else 0) +
                (if (MeleeAttr.InquisitorTop in melee) 1 else 0) +
                (if (MeleeAttr.InquisitorBottom in melee) 1 else 0)

        if (inquisitorPieces == 0) {
            return state
        }

        val multiplierAdditive =
            when {
                MeleeAttr.InquisitorWeapon in melee -> inquisitorPieces * 5
                inquisitorPieces == 3 -> 5
                else -> inquisitorPieces
            }

        if (multiplierAdditive <= 0) {
            return state
        }

        return state.copy(
            accumulated =
                scale(state.accumulated, multiplier = 200 + multiplierAdditive, divisor = 200)
        )
    }

    public fun modifyHitChance(
        hitChance: Int,
        attackRoll: Int,
        defenceRoll: Int,
        meleeAttributes: EnumSet<CombatMeleeAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        // Osmumten's Fang special hit chance calculation
        if (MeleeAttr.OsmumtensFang !in meleeAttributes || MeleeAttr.Stab !in meleeAttributes) {
            return hitChance
        }

        return if (NpcAttr.Amascut in npcAttributes) {
            val scale = HIT_CHANCE_SCALE
            scale - ((scale - hitChance) * (scale - hitChance) / scale)
        } else {
            AccuracyOperations.calculateFangHitRoll(attackRoll, defenceRoll)
        }
    }

    public fun calculateEffectiveAttack(player: Player, attackStyle: MeleeAttackStyle?): Int =
        calculateEffectiveAttack(
            visLevel = player.attackLvl,
            vars = player.vars,
            worn = player.worn,
            attackStyle = attackStyle,
        )

    private fun calculateEffectiveAttack(
        visLevel: Int,
        vars: VarPlayerIntMap,
        worn: Inventory,
        attackStyle: MeleeAttackStyle?,
    ): Int {
        val styleBonus = attackStyle.offensiveStyleBonus()
        val prayerBonus = vars.offensivePrayerBonus()
        val voidBonus = worn.offensiveVoidBonus()
        return PlayerMeleeAccuracy.calculateEffectiveAttack(
            visibleAttackLvl = visLevel,
            styleBonus = styleBonus,
            prayerBonus = prayerBonus,
            voidBonus = voidBonus,
        )
    }

    private fun MeleeAttackStyle?.offensiveStyleBonus(): Int =
        when (this) {
            MeleeAttackStyle.Controlled -> 9
            MeleeAttackStyle.Accurate -> 11
            else -> 8
        }

    private fun VarPlayerIntMap.offensivePrayerBonus(): Double =
        when {
            this[varbits.prayer_clarityofthought] == 1 -> 1.05
            this[varbits.prayer_improvedreflexes] == 1 -> 1.1
            this[varbits.prayer_incrediblereflexes] == 1 -> 1.15
            this[varbits.prayer_chivalry] == 1 -> 1.15
            this[varbits.prayer_piety] == 1 -> 1.20
            else -> 1.0
        }

    private fun Inventory.offensiveVoidBonus(): Double {
        val helm = this[Wearpos.Hat.slot]
        if (!EquipmentChecks.isVoidMeleeHelm(helm)) {
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
        return PlayerMeleeAccuracy.calculateEffectiveDefence(
            visibleDefenceLvl = visLevel,
            styleBonus = styleBonus,
            prayerBonus = prayerBonus,
            armourBonus = armourBonus,
        )
    }
}
