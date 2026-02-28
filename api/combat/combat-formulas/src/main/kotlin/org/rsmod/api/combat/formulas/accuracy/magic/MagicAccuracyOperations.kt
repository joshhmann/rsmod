package org.rsmod.api.combat.formulas.accuracy.magic

import java.util.EnumSet
import org.rsmod.api.combat.accuracy.player.PlayerMagicAccuracy
import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.commons.styles.MagicAttackStyle
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatSpellAttributes
import org.rsmod.api.combat.formulas.attributes.CombatStaffAttributes
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.stat.defenceLvl
import org.rsmod.api.player.stat.magicLvl
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.vars.VarPlayerIntMap

private typealias SpellAttr = CombatSpellAttributes

private typealias StaffAttr = CombatStaffAttributes

private typealias NpcAttr = CombatNpcAttributes

/** Data class representing spell attack roll modification state. */
private data class SpellAttackRollState(
    val accumulated: Int,
    val base: Int,
    var additiveBonus: Int = 0,
    var applyBlackMaskMod: Boolean = false,
)

/** Type alias for a spell attack roll modifier function. */
private typealias SpellAttackRollModifier =
    (SpellAttackRollState, EnumSet<SpellAttr>, EnumSet<NpcAttr>) -> SpellAttackRollState

/** Data class representing staff attack roll modification state. */
private data class StaffAttackRollState(
    val accumulated: Int,
    val base: Int,
    var additiveBonus: Int = 0,
    var applyBlackMaskMod: Boolean = false,
)

/** Type alias for a staff attack roll modifier function. */
private typealias StaffAttackRollModifier =
    (StaffAttackRollState, EnumSet<StaffAttr>, EnumSet<NpcAttr>) -> StaffAttackRollState

public object MagicAccuracyOperations {
    /**
     * @param targetWeaknessPercent The target's elemental weakness percentage (e.g., `1` = `1%`).
     */
    public fun modifySpellAttackRoll(
        attackRoll: Int,
        targetWeaknessPercent: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val initialState = SpellAttackRollState(accumulated = attackRoll, base = attackRoll)

        val modifiers: List<SpellAttackRollModifier> =
            listOf(
                ::applyAmuletModifier,
                ::applySmokeStaffModifier,
                ::applyAdditiveBonusMultiplier,
                ::applyDraconicModifier,
                ::applyBlackMaskMultiplier,
                ::applyDemonbaneModifier,
                ::applyRevenantWeaponModifier,
                ::applyWaterTomeModifier,
                { state, spell, npc ->
                    applySpellWeaknessModifier(state, spell, npc, targetWeaknessPercent)
                },
            )

        return modifiers
            .fold(initialState) { state, mod -> mod(state, spellAttributes, npcAttributes) }
            .accumulated
    }

    private fun applyAmuletModifier(
        state: SpellAttackRollState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellAttackRollState {
        when {
            SpellAttr.AmuletOfAvarice in spell && NpcAttr.Revenant in npc -> {
                state.additiveBonus += if (SpellAttr.ForinthrySurge in spell) 35 else 20
            }
            SpellAttr.SalveAmuletEi in spell && NpcAttr.Undead in npc -> {
                state.additiveBonus += 20
            }
            SpellAttr.SalveAmuletI in spell && NpcAttr.Undead in npc -> {
                state.additiveBonus += 15
            }
            SpellAttr.BlackMaskI in spell && NpcAttr.SlayerTask in npc -> {
                state.applyBlackMaskMod = true
            }
        }
        return state
    }

    private fun applySmokeStaffModifier(
        state: SpellAttackRollState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellAttackRollState {
        if (SpellAttr.SmokeStaff in spell && SpellAttr.StandardBook in spell) {
            state.additiveBonus += 10
        }
        return state
    }

    private fun applyAdditiveBonusMultiplier(
        state: SpellAttackRollState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellAttackRollState {
        return state.copy(accumulated = scale(state.accumulated, 100 + state.additiveBonus, 100))
    }

    private fun applyDraconicModifier(
        state: SpellAttackRollState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellAttackRollState {
        if (NpcAttr.Draconic !in npc) {
            return state
        }
        val newAccumulated =
            when {
                SpellAttr.DragonHunterLance in spell -> {
                    scale(state.accumulated, multiplier = 6, divisor = 5)
                }
                SpellAttr.DragonHunterWand in spell -> {
                    scale(state.accumulated, multiplier = 3, divisor = 2)
                }
                SpellAttr.DragonHunterCrossbow in spell -> {
                    scale(state.accumulated, multiplier = 13, divisor = 10)
                }
                else -> state.accumulated
            }
        return state.copy(accumulated = newAccumulated)
    }

    private fun applyBlackMaskMultiplier(
        state: SpellAttackRollState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellAttackRollState {
        if (!state.applyBlackMaskMod) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 23, divisor = 20))
    }

    private fun applyDemonbaneModifier(
        state: SpellAttackRollState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellAttackRollState {
        if (SpellAttr.Demonbane !in spell || NpcAttr.Demon !in npc) {
            return state
        }
        var percent =
            if (NpcAttr.DemonbaneResistance in npc) {
                if (SpellAttr.MarkOfDarkness in spell) 28 else 14
            } else {
                if (SpellAttr.MarkOfDarkness in spell) 40 else 20
            }

        if (SpellAttr.PurgingStaff in spell) {
            percent *= 2
        }

        return state.copy(accumulated = scale(state.accumulated, 100 + percent, divisor = 100))
    }

    private fun applyRevenantWeaponModifier(
        state: SpellAttackRollState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellAttackRollState {
        if (SpellAttr.RevenantWeapon !in spell || NpcAttr.Revenant !in npc) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 3, divisor = 2))
    }

    private fun applyWaterTomeModifier(
        state: SpellAttackRollState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellAttackRollState {
        // Note: The wiki states that the Tome of water boosts water combat spell accuracy by 10%
        // against npcs; however, the dps calculator uses 20% alongside bind spells. Since we have
        // aligned all other values with the dps calculator, we are keeping it at 20% here.
        val applyWaterTomeMod = SpellAttr.WaterSpell in spell || SpellAttr.BindSpell in spell
        if (SpellAttr.WaterTome !in spell || !applyWaterTomeMod) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 6, divisor = 5))
    }

    private fun applySpellWeaknessModifier(
        state: SpellAttackRollState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
        targetWeaknessPercent: Int,
    ): SpellAttackRollState {
        val applySpellWeaknessMod =
            NpcAttr.WindWeakness in npc && SpellAttr.WindSpell in spell ||
                NpcAttr.EarthWeakness in npc && SpellAttr.EarthSpell in spell ||
                NpcAttr.WaterWeakness in npc && SpellAttr.WaterSpell in spell ||
                NpcAttr.FireWeakness in npc && SpellAttr.FireSpell in spell

        if (!applySpellWeaknessMod) {
            return state
        }
        val additive = (state.base * (targetWeaknessPercent / 100.0)).toInt()
        return state.copy(accumulated = state.accumulated + additive)
    }

    public fun modifySpellAttackRoll(
        attackRoll: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
    ): Int {
        val initialState = SpellAttackRollState(accumulated = attackRoll, base = attackRoll)

        val modifiers: List<SpellAttackRollModifier> =
            listOf(
                ::applySmokeStaffModifier,
                ::applyAdditiveBonusMultiplier,
                ::applyWaterTomeModifier,
            )

        return modifiers
            .fold(initialState) { state, mod ->
                mod(state, spellAttributes, EnumSet.noneOf(NpcAttr::class.java))
            }
            .accumulated
    }

    public fun modifyStaffAttackRoll(
        attackRoll: Int,
        staffAttributes: EnumSet<CombatStaffAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val initialState = StaffAttackRollState(accumulated = attackRoll, base = attackRoll)

        val modifiers: List<StaffAttackRollModifier> =
            listOf(
                ::applyStaffAmuletModifier,
                ::applyStaffAdditiveBonusMultiplier,
                ::applyStaffBlackMaskMultiplier,
                ::applyStaffRevenantWeaponModifier,
            )

        return modifiers
            .fold(initialState) { state, mod -> mod(state, staffAttributes, npcAttributes) }
            .accumulated
    }

    private fun applyStaffAmuletModifier(
        state: StaffAttackRollState,
        staff: EnumSet<StaffAttr>,
        npc: EnumSet<NpcAttr>,
    ): StaffAttackRollState {
        when {
            StaffAttr.AmuletOfAvarice in staff && NpcAttr.Revenant in npc -> {
                state.additiveBonus += if (StaffAttr.ForinthrySurge in staff) 35 else 20
            }
            StaffAttr.SalveAmuletEi in staff && NpcAttr.Undead in npc -> {
                state.additiveBonus += 20
            }
            StaffAttr.SalveAmuletI in staff && NpcAttr.Undead in npc -> {
                state.additiveBonus += 15
            }
            StaffAttr.BlackMaskI in staff && NpcAttr.SlayerTask in npc -> {
                state.applyBlackMaskMod = true
            }
        }
        return state
    }

    private fun applyStaffAdditiveBonusMultiplier(
        state: StaffAttackRollState,
        staff: EnumSet<StaffAttr>,
        npc: EnumSet<NpcAttr>,
    ): StaffAttackRollState {
        return state.copy(accumulated = scale(state.accumulated, 100 + state.additiveBonus, 100))
    }

    private fun applyStaffBlackMaskMultiplier(
        state: StaffAttackRollState,
        staff: EnumSet<StaffAttr>,
        npc: EnumSet<NpcAttr>,
    ): StaffAttackRollState {
        if (!state.applyBlackMaskMod) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 23, divisor = 20))
    }

    private fun applyStaffRevenantWeaponModifier(
        state: StaffAttackRollState,
        staff: EnumSet<StaffAttr>,
        npc: EnumSet<NpcAttr>,
    ): StaffAttackRollState {
        if (StaffAttr.RevenantWeapon !in staff || NpcAttr.Revenant !in npc) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 3, divisor = 2))
    }

    // Note: Though currently empty, this serves as the attack roll modifier for pvp as a way to
    // differentiate between player and npc targets.
    public fun modifyStaffAttackRoll(attackRoll: Int): Int = attackRoll

    public fun calculateEffectiveMagic(player: Player, attackStyle: MagicAttackStyle?): Int =
        calculateEffectiveMagic(
            visLevel = player.magicLvl,
            vars = player.vars,
            worn = player.worn,
            attackStyle = attackStyle,
        )

    private fun calculateEffectiveMagic(
        visLevel: Int,
        vars: VarPlayerIntMap,
        worn: Inventory,
        attackStyle: MagicAttackStyle?,
    ): Int {
        val styleBonus = attackStyle.offensiveStyleBonus()
        val prayerBonus = vars.offensivePrayerBonus()
        val voidBonus = worn.offensiveVoidBonus()
        return PlayerMagicAccuracy.calculateEffectiveMagic(
            visibleMagicLvl = visLevel,
            styleBonus = styleBonus,
            prayerBonus = prayerBonus,
            voidBonus = voidBonus,
        )
    }

    private fun MagicAttackStyle?.offensiveStyleBonus(): Int =
        when (this) {
            MagicAttackStyle.Accurate -> 11
            else -> 9
        }

    private fun VarPlayerIntMap.offensivePrayerBonus(): Double =
        when {
            this[varbits.prayer_mysticwill] == 1 -> 1.05
            this[varbits.prayer_mysticlore] == 1 -> 1.1
            this[varbits.prayer_mysticmight] == 1 -> {
                if (this[varbits.prayer_mystic_vigour_unlocked] == 1) 1.18 else 1.15
            }
            this[varbits.prayer_augury] == 1 -> 1.25
            else -> 1.0
        }

    private fun Inventory.offensiveVoidBonus(): Double {
        val helm = this[Wearpos.Hat.slot]
        if (!EquipmentChecks.isVoidMageHelm(helm)) {
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

        return 1.45
    }

    public fun calculateEffectiveDefence(player: Player, attackStyle: AttackStyle?): Int {
        val armourBonus = AccuracyOperations.defensiveArmourBonus(player)
        return calculateEffectiveDefence(
            visDefenceLevel = player.defenceLvl,
            visMagicLevel = player.magicLvl,
            armourBonus = armourBonus,
            vars = player.vars,
            attackStyle = attackStyle,
        )
    }

    private fun calculateEffectiveDefence(
        visDefenceLevel: Int,
        visMagicLevel: Int,
        armourBonus: Double,
        vars: VarPlayerIntMap,
        attackStyle: AttackStyle?,
    ): Int {
        val styleBonus = AccuracyOperations.defensiveStyleBonus(attackStyle)
        val defencePrayerBonus = AccuracyOperations.defensivePrayerBonus(vars)
        val magicPrayerBonus = vars.magicDefencePrayerBonus()
        return PlayerMagicAccuracy.calculateEffectiveDefence(
            visibleDefenceLvl = visDefenceLevel,
            visibleMagicLvl = visMagicLevel,
            styleBonus = styleBonus,
            defencePrayerBonus = defencePrayerBonus,
            magicPrayerBonus = magicPrayerBonus,
            armourBonus = armourBonus,
        )
    }

    private fun VarPlayerIntMap.magicDefencePrayerBonus(): Double = offensivePrayerBonus()
}
