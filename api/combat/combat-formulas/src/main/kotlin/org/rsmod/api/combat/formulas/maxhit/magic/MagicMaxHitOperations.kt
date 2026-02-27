package org.rsmod.api.combat.formulas.maxhit.magic

import java.util.EnumSet
import kotlin.math.max
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatSpellAttributes
import org.rsmod.api.combat.formulas.attributes.CombatStaffAttributes
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.game.entity.Player

private typealias SpellAttr = CombatSpellAttributes

private typealias StaffAttr = CombatStaffAttributes

private typealias NpcAttr = CombatNpcAttributes

/** Data class representing spell base damage modification state for PvE. */
private data class SpellBaseDamageState(
    val accumulated: Int,
    val base: Int,
    val sourceMagic: Int,
    val sourceBaseMagicDmgBonus: Int,
    val sourceMagicPrayerBonus: Int,
    var modifiedMagicDmgBonus: Int = 0,
    var applyBlackMaskMod: Boolean = false,
)

/** Data class representing spell base damage modification state for PvP. */
private data class SpellBaseDamagePvPState(
    val accumulated: Int,
    val base: Int,
    val sourceMagic: Int,
    val sourceBaseMagicDmgBonus: Int,
    val sourceMagicPrayerBonus: Int,
    var modifiedMagicDmgBonus: Int = 0,
)

/** Data class representing spell damage range modification state for PvE. */
private data class SpellRangeState(
    var min: Int,
    var max: Int,
    val baseDamage: Int,
    val attackRate: Int,
    val targetWeaknessPercent: Int,
)

/** Data class representing spell damage range modification state for PvP. */
private data class SpellRangePvPState(var min: Int, var max: Int)

/** Data class representing staff base damage modification state for PvE. */
private data class StaffBaseDamageState(
    val accumulated: Int,
    val base: Int,
    val sourceBaseMagicDmgBonus: Int,
    val sourceMagicPrayerBonus: Int,
    var modifiedMagicDmgBonus: Int = 0,
    var applyBlackMaskMod: Boolean = false,
)

/** Data class representing staff base damage modification state for PvP. */
private data class StaffBaseDamagePvPState(
    val accumulated: Int,
    val base: Int,
    val sourceBaseMagicDmgBonus: Int,
    val sourceMagicPrayerBonus: Int,
    var modifiedMagicDmgBonus: Int = 0,
)

/** Type alias for a spell base damage modifier function (PvE). */
private typealias SpellBaseModifier =
    (SpellBaseDamageState, EnumSet<SpellAttr>, EnumSet<NpcAttr>) -> SpellBaseDamageState

/** Type alias for a spell base damage modifier function (PvP). */
private typealias SpellBasePvPModifier =
    (SpellBaseDamagePvPState, EnumSet<SpellAttr>) -> SpellBaseDamagePvPState

/** Type alias for a spell range modifier function (PvE). */
private typealias SpellRangeModifier =
    (SpellRangeState, EnumSet<SpellAttr>, EnumSet<NpcAttr>) -> SpellRangeState

/** Type alias for a spell range modifier function (PvP). */
private typealias SpellRangePvPModifier =
    (SpellRangePvPState, EnumSet<SpellAttr>) -> SpellRangePvPState

/** Type alias for a staff base damage modifier function (PvE). */
private typealias StaffBaseModifier =
    (StaffBaseDamageState, EnumSet<StaffAttr>, EnumSet<NpcAttr>) -> StaffBaseDamageState

/** Type alias for a staff base damage modifier function (PvP). */
private typealias StaffBasePvPModifier =
    (StaffBaseDamagePvPState, EnumSet<StaffAttr>) -> StaffBaseDamagePvPState

public object MagicMaxHitOperations {
    /**
     * @param sourceMagic The source's **current** magic level. Required for the Magic dart
     *   modifier.
     * @param sourceBaseMagicDmgBonus The source's base magic damage bonus as calculated by
     *   [WornBonuses.magicDamageBonusBase].
     * @param sourceMagicPrayerBonus The source's current prayer magic damage bonus as calculated by
     *   [getMagicDamagePrayerBonus].
     */
    public fun modifySpellBaseDamage(
        baseDamage: Int,
        sourceMagic: Int,
        sourceBaseMagicDmgBonus: Int,
        sourceMagicPrayerBonus: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val initialState =
            SpellBaseDamageState(
                accumulated = baseDamage,
                base = baseDamage,
                sourceMagic = sourceMagic,
                sourceBaseMagicDmgBonus = sourceBaseMagicDmgBonus,
                sourceMagicPrayerBonus = sourceMagicPrayerBonus,
                modifiedMagicDmgBonus = sourceBaseMagicDmgBonus,
            )

        val modifiers: List<SpellBaseModifier> =
            listOf(
                ::applyMagicDartModifier,
                ::applyChaosGauntletsModifier,
                ::applyChargeSpellModifier,
                ::applySmokeStaffDmgModifier,
                ::applyAmuletDmgModifier,
                ::applyPrayerDmgBonus,
                ::applyMagicDmgBonusMultiplier,
                ::applyBlackMaskDmgModifier,
                ::applyDraconicDmgModifier,
                ::applyRevenantWeaponDmgModifier,
            )

        return modifiers
            .fold(initialState) { state, mod -> mod(state, spellAttributes, npcAttributes) }
            .accumulated
    }

    private fun applyMagicDartModifier(
        state: SpellBaseDamageState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellBaseDamageState {
        if (SpellAttr.MagicDart !in spell) {
            return state
        }
        val slayerStaffBoost = SpellAttr.SlayerStaffE in spell && NpcAttr.SlayerTask in npc
        val newAccumulated =
            if (slayerStaffBoost) {
                13 + (state.sourceMagic / 6)
            } else {
                10 + (state.sourceMagic / 10)
            }
        return state.copy(accumulated = newAccumulated)
    }

    private fun applyChaosGauntletsModifier(
        state: SpellBaseDamageState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellBaseDamageState {
        if (SpellAttr.ChaosGauntlets !in spell || SpellAttr.BoltSpell !in spell) {
            return state
        }
        return state.copy(accumulated = state.accumulated + 3)
    }

    private fun applyChargeSpellModifier(
        state: SpellBaseDamageState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellBaseDamageState {
        if (SpellAttr.ChargeSpell !in spell) {
            return state
        }
        return state.copy(accumulated = state.accumulated + 10)
    }

    private fun applySmokeStaffDmgModifier(
        state: SpellBaseDamageState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellBaseDamageState {
        if (SpellAttr.SmokeStaff in spell && SpellAttr.StandardBook in spell) {
            state.modifiedMagicDmgBonus += 100
        }
        return state
    }

    private fun applyAmuletDmgModifier(
        state: SpellBaseDamageState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellBaseDamageState {
        when {
            SpellAttr.AmuletOfAvarice in spell && NpcAttr.Revenant in npc -> {
                val additive = if (SpellAttr.ForinthrySurge in spell) 350 else 200
                state.modifiedMagicDmgBonus += additive
            }
            SpellAttr.SalveAmuletEi in spell && NpcAttr.Undead in npc -> {
                state.modifiedMagicDmgBonus += 200
            }
            SpellAttr.SalveAmuletI in spell && NpcAttr.Undead in npc -> {
                state.modifiedMagicDmgBonus += 150
            }
            SpellAttr.BlackMaskI in spell && NpcAttr.SlayerTask in npc -> {
                state.applyBlackMaskMod = true
            }
        }
        return state
    }

    private fun applyPrayerDmgBonus(
        state: SpellBaseDamageState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellBaseDamageState {
        state.modifiedMagicDmgBonus += state.sourceMagicPrayerBonus
        return state
    }

    private fun applyMagicDmgBonusMultiplier(
        state: SpellBaseDamageState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellBaseDamageState {
        val maxAdditive =
            scale(state.accumulated, multiplier = state.modifiedMagicDmgBonus, divisor = 1000)
        return state.copy(accumulated = state.accumulated + maxAdditive)
    }

    private fun applyBlackMaskDmgModifier(
        state: SpellBaseDamageState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellBaseDamageState {
        if (!state.applyBlackMaskMod) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 23, divisor = 20))
    }

    private fun applyDraconicDmgModifier(
        state: SpellBaseDamageState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellBaseDamageState {
        if (NpcAttr.Draconic !in npc) {
            return state
        }
        val newAccumulated =
            when {
                SpellAttr.DragonHunterLance in spell -> {
                    scale(state.accumulated, multiplier = 6, divisor = 5)
                }
                SpellAttr.DragonHunterWand in spell -> {
                    scale(state.accumulated, multiplier = 6, divisor = 5)
                }
                SpellAttr.DragonHunterCrossbow in spell -> {
                    scale(state.accumulated, multiplier = 5, divisor = 4)
                }
                else -> state.accumulated
            }
        return state.copy(accumulated = newAccumulated)
    }

    private fun applyRevenantWeaponDmgModifier(
        state: SpellBaseDamageState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellBaseDamageState {
        if (SpellAttr.RevenantWeapon !in spell || NpcAttr.Wilderness !in npc) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 3, divisor = 2))
    }

    /**
     * @param sourceMagic The source's **current** magic level. Required for the Magic dart
     *   modifier.
     * @param sourceBaseMagicDmgBonus The source's base magic damage bonus as calculated by
     *   [WornBonuses.magicDamageBonusBase].
     * @param sourceMagicPrayerBonus The source's current prayer magic damage bonus as calculated by
     *   [getMagicDamagePrayerBonus].
     */
    public fun modifySpellBaseDamage(
        baseDamage: Int,
        sourceMagic: Int,
        sourceBaseMagicDmgBonus: Int,
        sourceMagicPrayerBonus: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
    ): Int {
        val initialState =
            SpellBaseDamagePvPState(
                accumulated = baseDamage,
                base = baseDamage,
                sourceMagic = sourceMagic,
                sourceBaseMagicDmgBonus = sourceBaseMagicDmgBonus,
                sourceMagicPrayerBonus = sourceMagicPrayerBonus,
                modifiedMagicDmgBonus = sourceBaseMagicDmgBonus,
            )

        val modifiers: List<SpellBasePvPModifier> =
            listOf(
                ::applyMagicDartModifierPvP,
                ::applyChaosGauntletsModifierPvP,
                ::applyChargeSpellModifierPvP,
                ::applySmokeStaffDmgModifierPvP,
                ::applyPrayerDmgBonusPvP,
                ::applyMagicDmgBonusMultiplierPvP,
            )

        return modifiers
            .fold(initialState) { state, mod -> mod(state, spellAttributes) }
            .accumulated
    }

    private fun applyMagicDartModifierPvP(
        state: SpellBaseDamagePvPState,
        spell: EnumSet<SpellAttr>,
    ): SpellBaseDamagePvPState {
        if (SpellAttr.MagicDart !in spell) {
            return state
        }
        return state.copy(accumulated = 10 + (state.sourceMagic / 10))
    }

    private fun applyChaosGauntletsModifierPvP(
        state: SpellBaseDamagePvPState,
        spell: EnumSet<SpellAttr>,
    ): SpellBaseDamagePvPState {
        if (SpellAttr.ChaosGauntlets !in spell || SpellAttr.BoltSpell !in spell) {
            return state
        }
        return state.copy(accumulated = state.accumulated + 3)
    }

    private fun applyChargeSpellModifierPvP(
        state: SpellBaseDamagePvPState,
        spell: EnumSet<SpellAttr>,
    ): SpellBaseDamagePvPState {
        if (SpellAttr.ChargeSpell !in spell) {
            return state
        }
        return state.copy(accumulated = state.accumulated + 10)
    }

    private fun applySmokeStaffDmgModifierPvP(
        state: SpellBaseDamagePvPState,
        spell: EnumSet<SpellAttr>,
    ): SpellBaseDamagePvPState {
        if (SpellAttr.SmokeStaff in spell && SpellAttr.StandardBook in spell) {
            state.modifiedMagicDmgBonus += 100
        }
        return state
    }

    private fun applyPrayerDmgBonusPvP(
        state: SpellBaseDamagePvPState,
        spell: EnumSet<SpellAttr>,
    ): SpellBaseDamagePvPState {
        state.modifiedMagicDmgBonus += state.sourceMagicPrayerBonus
        return state
    }

    private fun applyMagicDmgBonusMultiplierPvP(
        state: SpellBaseDamagePvPState,
        spell: EnumSet<SpellAttr>,
    ): SpellBaseDamagePvPState {
        val maxAdditive =
            scale(state.accumulated, multiplier = state.modifiedMagicDmgBonus, divisor = 1000)
        return state.copy(accumulated = state.accumulated + maxAdditive)
    }

    /**
     * @param baseDamage The initial base damage as input into [modifySpellBaseDamage]. Required for
     *   elemental weakness modifiers.
     * @param targetWeaknessPercent The target's elemental weakness percentage (e.g., `1` = `1%`).
     */
    public fun modifySpellDamageRange(
        modifiedDamage: Int,
        baseDamage: Int,
        attackRate: Int,
        targetWeaknessPercent: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): IntRange {
        val initialState =
            SpellRangeState(
                min = 0,
                max = modifiedDamage,
                baseDamage = baseDamage,
                attackRate = attackRate,
                targetWeaknessPercent = targetWeaknessPercent,
            )

        val modifiers: List<SpellRangeModifier> =
            listOf(
                ::applySpellWeaknessModifier,
                ::applySunfireRuneModifier,
                ::applyTomeModifier,
                ::applyMarkOfDarknessModifier,
                ::applyAhrimModifier,
                ::applyTormentedDemonModifier,
            )

        val finalState =
            modifiers.fold(initialState) { state, mod ->
                mod(state, spellAttributes, npcAttributes)
            }
        return finalState.min..finalState.max
    }

    private fun applySpellWeaknessModifier(
        state: SpellRangeState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellRangeState {
        val applySpellWeaknessMod =
            NpcAttr.WindWeakness in npc && SpellAttr.WindSpell in spell ||
                NpcAttr.EarthWeakness in npc && SpellAttr.EarthSpell in spell ||
                NpcAttr.WaterWeakness in npc && SpellAttr.WaterSpell in spell ||
                NpcAttr.FireWeakness in npc && SpellAttr.FireSpell in spell

        if (!applySpellWeaknessMod) {
            return state
        }
        val additive = (state.baseDamage * (state.targetWeaknessPercent / 100.0)).toInt()
        state.max += additive
        return state
    }

    private fun applySunfireRuneModifier(
        state: SpellRangeState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellRangeState {
        if (SpellAttr.SunfireRunePassive !in spell) {
            return state
        }
        state.min = state.max / 10
        return state
    }

    private fun applyTomeModifier(
        state: SpellRangeState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellRangeState {
        val applyTomeMod =
            SpellAttr.EarthTome in spell && SpellAttr.EarthSpell in spell ||
                SpellAttr.WaterTome in spell && SpellAttr.WaterSpell in spell ||
                SpellAttr.FireTome in spell && SpellAttr.FireSpell in spell

        if (!applyTomeMod) {
            return state
        }
        state.max = scale(state.max, multiplier = 11, divisor = 10)
        return state
    }

    private fun applyMarkOfDarknessModifier(
        state: SpellRangeState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellRangeState {
        val applyMarkOfDarknessMod =
            SpellAttr.MarkOfDarkness in spell &&
                SpellAttr.Demonbane in spell &&
                NpcAttr.Demon in npc
        if (!applyMarkOfDarknessMod) {
            return state
        }
        val multiplier = if (SpellAttr.PurgingStaff in spell) 50 else 25
        state.max = scale(state.max, multiplier = 100 + multiplier, divisor = 100)
        return state
    }

    private fun applyAhrimModifier(
        state: SpellRangeState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellRangeState {
        if (SpellAttr.AhrimPassive !in spell) {
            return state
        }
        state.max = scale(state.max, multiplier = 13, divisor = 10)
        return state
    }

    private fun applyTormentedDemonModifier(
        state: SpellRangeState,
        spell: EnumSet<SpellAttr>,
        npc: EnumSet<NpcAttr>,
    ): SpellRangeState {
        if (NpcAttr.TormentedDemonUnshielded !in npc) {
            return state
        }
        val bonusDamage = max(0, (state.attackRate * state.attackRate) - 16)
        state.max += bonusDamage
        return state
    }

    public fun modifySpellDamageRange(
        modifiedDamage: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
    ): IntRange {
        val initialState = SpellRangePvPState(min = 0, max = modifiedDamage)

        val modifiers: List<SpellRangePvPModifier> =
            listOf(::applySunfireRuneModifierPvP, ::applyTomeModifierPvP, ::applyAhrimModifierPvP)

        val finalState = modifiers.fold(initialState) { state, mod -> mod(state, spellAttributes) }
        return finalState.min..finalState.max
    }

    private fun applySunfireRuneModifierPvP(
        state: SpellRangePvPState,
        spell: EnumSet<SpellAttr>,
    ): SpellRangePvPState {
        if (SpellAttr.SunfireRunePassive !in spell) {
            return state
        }
        state.min = state.max / 10
        return state
    }

    private fun applyTomeModifierPvP(
        state: SpellRangePvPState,
        spell: EnumSet<SpellAttr>,
    ): SpellRangePvPState {
        val applyTomeMod =
            SpellAttr.EarthTome in spell && SpellAttr.EarthSpell in spell ||
                SpellAttr.WaterTome in spell && SpellAttr.WaterSpell in spell ||
                SpellAttr.FireTome in spell && SpellAttr.FireSpell in spell

        if (!applyTomeMod) {
            return state
        }
        state.max = scale(state.max, multiplier = 12, divisor = 10)
        return state
    }

    private fun applyAhrimModifierPvP(
        state: SpellRangePvPState,
        spell: EnumSet<SpellAttr>,
    ): SpellRangePvPState {
        if (SpellAttr.AhrimPassive !in spell) {
            return state
        }
        state.max = scale(state.max, multiplier = 13, divisor = 10)
        return state
    }

    /**
     * @param sourceBaseMagicDmgBonus The source's base magic damage bonus as calculated by
     *   [WornBonuses.magicDamageBonusBase].
     * @param sourceMagicPrayerBonus The source's current prayer magic damage bonus as calculated by
     *   [getMagicDamagePrayerBonus].
     */
    public fun modifyStaffBaseDamage(
        baseDamage: Int,
        sourceBaseMagicDmgBonus: Int,
        sourceMagicPrayerBonus: Int,
        staffAttributes: EnumSet<CombatStaffAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val initialState =
            StaffBaseDamageState(
                accumulated = baseDamage,
                base = baseDamage,
                sourceBaseMagicDmgBonus = sourceBaseMagicDmgBonus,
                sourceMagicPrayerBonus = sourceMagicPrayerBonus,
                modifiedMagicDmgBonus = sourceBaseMagicDmgBonus,
            )

        val modifiers: List<StaffBaseModifier> =
            listOf(
                ::applyTumekensShadowModifier,
                ::applyStaffAmuletDmgModifier,
                ::applyStaffPrayerDmgBonus,
                ::applyStaffMagicDmgBonusMultiplier,
                ::applyStaffBlackMaskDmgModifier,
                ::applyStaffRevenantWeaponDmgModifier,
            )

        return modifiers
            .fold(initialState) { state, mod -> mod(state, staffAttributes, npcAttributes) }
            .accumulated
    }

    private fun applyTumekensShadowModifier(
        state: StaffBaseDamageState,
        staff: EnumSet<StaffAttr>,
        npc: EnumSet<NpcAttr>,
    ): StaffBaseDamageState {
        if (StaffAttr.TumekensShadow !in staff) {
            return state
        }
        val multiplier = if (NpcAttr.Amascut in npc) 4 else 3
        state.modifiedMagicDmgBonus *= multiplier
        return state
    }

    private fun applyStaffAmuletDmgModifier(
        state: StaffBaseDamageState,
        staff: EnumSet<StaffAttr>,
        npc: EnumSet<NpcAttr>,
    ): StaffBaseDamageState {
        when {
            StaffAttr.AmuletOfAvarice in staff && NpcAttr.Revenant in npc -> {
                val additive = if (StaffAttr.ForinthrySurge in staff) 350 else 200
                state.modifiedMagicDmgBonus += additive
            }
            StaffAttr.SalveAmuletEi in staff && NpcAttr.Undead in npc -> {
                state.modifiedMagicDmgBonus += 200
            }
            StaffAttr.SalveAmuletI in staff && NpcAttr.Undead in npc -> {
                state.modifiedMagicDmgBonus += 150
            }
            StaffAttr.BlackMaskI in staff && NpcAttr.SlayerTask in npc -> {
                state.applyBlackMaskMod = true
            }
        }
        return state
    }

    private fun applyStaffPrayerDmgBonus(
        state: StaffBaseDamageState,
        staff: EnumSet<StaffAttr>,
        npc: EnumSet<NpcAttr>,
    ): StaffBaseDamageState {
        state.modifiedMagicDmgBonus += state.sourceMagicPrayerBonus
        return state
    }

    private fun applyStaffMagicDmgBonusMultiplier(
        state: StaffBaseDamageState,
        staff: EnumSet<StaffAttr>,
        npc: EnumSet<NpcAttr>,
    ): StaffBaseDamageState {
        val maxAdditive =
            scale(state.accumulated, multiplier = state.modifiedMagicDmgBonus, divisor = 1000)
        return state.copy(accumulated = state.accumulated + maxAdditive)
    }

    private fun applyStaffBlackMaskDmgModifier(
        state: StaffBaseDamageState,
        staff: EnumSet<StaffAttr>,
        npc: EnumSet<NpcAttr>,
    ): StaffBaseDamageState {
        if (!state.applyBlackMaskMod) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 23, divisor = 20))
    }

    private fun applyStaffRevenantWeaponDmgModifier(
        state: StaffBaseDamageState,
        staff: EnumSet<StaffAttr>,
        npc: EnumSet<NpcAttr>,
    ): StaffBaseDamageState {
        if (StaffAttr.RevenantWeapon !in staff || NpcAttr.Wilderness !in npc) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 3, divisor = 2))
    }

    public fun modifyStaffBaseDamage(
        baseDamage: Int,
        sourceBaseMagicDmgBonus: Int,
        sourceMagicPrayerBonus: Int,
    ): Int {
        val initialState =
            StaffBaseDamagePvPState(
                accumulated = baseDamage,
                base = baseDamage,
                sourceBaseMagicDmgBonus = sourceBaseMagicDmgBonus,
                sourceMagicPrayerBonus = sourceMagicPrayerBonus,
                modifiedMagicDmgBonus = sourceBaseMagicDmgBonus,
            )

        val modifiers: List<StaffBasePvPModifier> =
            listOf(::applyStaffPrayerDmgBonusPvP, ::applyStaffMagicDmgBonusMultiplierPvP)

        return modifiers
            .fold(initialState) { state, mod -> mod(state, EnumSet.noneOf(StaffAttr::class.java)) }
            .accumulated
    }

    private fun applyStaffPrayerDmgBonusPvP(
        state: StaffBaseDamagePvPState,
        staff: EnumSet<StaffAttr>,
    ): StaffBaseDamagePvPState {
        state.modifiedMagicDmgBonus += state.sourceMagicPrayerBonus
        return state
    }

    private fun applyStaffMagicDmgBonusMultiplierPvP(
        state: StaffBaseDamagePvPState,
        staff: EnumSet<StaffAttr>,
    ): StaffBaseDamagePvPState {
        val maxAdditive =
            scale(state.accumulated, multiplier = state.modifiedMagicDmgBonus, divisor = 1000)
        return state.copy(accumulated = state.accumulated + maxAdditive)
    }

    public fun getMagicDamagePrayerBonus(player: Player): Int =
        when {
            player.vars[varbits.mystic_lore] == 1 -> 10
            player.vars[varbits.mystic_might] == 1 -> {
                if (player.vars[varbits.prayer_mystic_vigour_unlocked] == 1) 30 else 20
            }
            player.vars[varbits.augury] == 1 -> 40
            else -> 0
        }
}
