package org.rsmod.api.combat.formulas.maxhit.ranged

import java.util.EnumSet
import kotlin.math.max
import kotlin.math.min
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatRangedAttributes
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.combat.maxhit.player.PlayerRangedMaxHit
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.stat.stat
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.vars.VarPlayerIntMap

private typealias RangeAttr = CombatRangedAttributes

private typealias NpcAttr = CombatNpcAttributes

/** Data class representing damage modification state for PvE (with target magic). */
private data class PvEDamageState(
    val accumulated: Int,
    val base: Int,
    val targetMagic: Int,
    var applyRevWeaponMod: Boolean = false,
    var applyDragonbaneMod: Boolean = false,
    var applyDemonbaneMod: Boolean = false,
)

/** Data class representing damage modification state for PvP (no target magic). */
private data class PvPDamageState(val accumulated: Int, val base: Int)

/** Type alias for a PvE damage modifier function. */
private typealias PvEDamageModifier =
    (PvEDamageState, EnumSet<RangeAttr>, EnumSet<NpcAttr>) -> PvEDamageState

/** Type alias for a PvP damage modifier function. */
private typealias PvPDamageModifier =
    (PvPDamageState, EnumSet<RangeAttr>, EnumSet<NpcAttr>) -> PvPDamageState

public object RangedMaxHitOperations {
    /**
     * @param targetMagic The target's magic level or magic bonus, whichever of the two is greater.
     *   Required for the Twisted bow modifier.
     */
    public fun modifyBaseDamage(
        baseDamage: Int,
        targetMagic: Int,
        rangeAttributes: EnumSet<CombatRangedAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val initialState =
            PvEDamageState(accumulated = baseDamage, base = baseDamage, targetMagic = targetMagic)

        val modifiers: List<PvEDamageModifier> =
            listOf(
                ::applyCrystalModifier,
                ::applyAmuletModifier,
                ::applyTwistedBowModifier,
                ::applyPendingModifiers,
                ::applyRatBoneModifier,
            )

        return modifiers
            .fold(initialState) { state, mod -> mod(state, rangeAttributes, npcAttributes) }
            .accumulated
    }

    private fun applyCrystalModifier(
        state: PvEDamageState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
    ): PvEDamageState {
        if (RangeAttr.CrystalBow !in range) {
            return state
        }
        val helmAdditive = if (RangeAttr.CrystalHelm in range) 1 else 0
        val bodyAdditive = if (RangeAttr.CrystalBody in range) 3 else 0
        val legsAdditive = if (RangeAttr.CrystalLegs in range) 2 else 0
        val armourAdditive = helmAdditive + bodyAdditive + legsAdditive
        return state.copy(
            accumulated = scale(state.accumulated, multiplier = 40 + armourAdditive, divisor = 40)
        )
    }

    private fun applyAmuletModifier(
        state: PvEDamageState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
    ): PvEDamageState {
        // Pre-calculate flags for later use
        state.applyRevWeaponMod = RangeAttr.RevenantWeapon in range && NpcAttr.Wilderness in npc
        state.applyDragonbaneMod =
            RangeAttr.DragonHunterCrossbow in range && NpcAttr.Draconic in npc
        state.applyDemonbaneMod = RangeAttr.ScorchingBow in range && NpcAttr.Demon in npc

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
                    var multiplier = 23

                    if (state.applyRevWeaponMod) {
                        state.applyRevWeaponMod = false
                        multiplier += 10
                    }

                    if (state.applyDragonbaneMod) {
                        state.applyDragonbaneMod = false
                        multiplier += 5
                    }

                    if (state.applyDemonbaneMod) {
                        state.applyDemonbaneMod = false
                        multiplier += 6
                    }

                    scale(state.accumulated, multiplier, divisor = 20)
                }
                else -> state.accumulated
            }
        return state.copy(accumulated = newAccumulated)
    }

    private fun applyTwistedBowModifier(
        state: PvEDamageState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
    ): PvEDamageState {
        if (RangeAttr.TwistedBow !in range) {
            return state
        }
        val cap = if (NpcAttr.Xerician in npc) 350 else 250
        val magic = min(cap, state.targetMagic)

        val factor = 14
        val base = 250

        val linearBonus = (3 * magic - factor) / 100
        val deviation = (3 * magic / 10) - (10 * factor)
        val quadraticPenalty = (deviation * deviation) / 100

        val multiplier = base + linearBonus - quadraticPenalty
        return state.copy(accumulated = scale(state.accumulated, multiplier, divisor = 100))
    }

    private fun applyPendingModifiers(
        state: PvEDamageState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
    ): PvEDamageState {
        var result = state.accumulated

        if (state.applyRevWeaponMod) {
            result = scale(result, multiplier = 3, divisor = 2)
        }

        if (state.applyDragonbaneMod) {
            result = scale(result, multiplier = 5, divisor = 4)
        }

        if (state.applyDemonbaneMod) {
            result =
                if (NpcAttr.DemonbaneResistance in npc) {
                    scale(result, multiplier = 121, divisor = 100)
                } else {
                    scale(result, multiplier = 130, divisor = 100)
                }
        }

        return state.copy(accumulated = result)
    }

    private fun applyRatBoneModifier(
        state: PvEDamageState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
    ): PvEDamageState {
        if (RangeAttr.RatBoneWeapon !in range || NpcAttr.Rat !in npc) {
            return state
        }
        return state.copy(accumulated = state.accumulated + 10)
    }

    public fun modifyBaseDamage(
        baseDamage: Int,
        rangeAttributes: EnumSet<CombatRangedAttributes>,
    ): Int {
        val initialState = PvPDamageState(accumulated = baseDamage, base = baseDamage)

        val modifiers: List<PvPDamageModifier> = listOf(::applyCrystalModifierPvP)

        return modifiers
            .fold(initialState) { state, mod ->
                mod(state, rangeAttributes, EnumSet.noneOf(NpcAttr::class.java))
            }
            .accumulated
    }

    private fun applyCrystalModifierPvP(
        state: PvPDamageState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
    ): PvPDamageState {
        if (RangeAttr.CrystalBow !in range) {
            return state
        }
        val helmAdditive = if (RangeAttr.CrystalHelm in range) 1 else 0
        val bodyAdditive = if (RangeAttr.CrystalBody in range) 3 else 0
        val legsAdditive = if (RangeAttr.CrystalLegs in range) 2 else 0
        val armourAdditive = helmAdditive + bodyAdditive + legsAdditive
        return state.copy(
            accumulated = scale(state.accumulated, multiplier = 40 + armourAdditive, divisor = 40)
        )
    }

    public fun modifyPostSpec(
        modifiedDamage: Int,
        boltSpecDamage: Int,
        attackRate: Int,
        rangeAttributes: EnumSet<CombatRangedAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val initialState =
            PvEDamageState(accumulated = modifiedDamage, base = modifiedDamage, targetMagic = 0)

        val modifiers: List<PvEDamageModifier> =
            listOf(
                { state, range, npc -> applyTormentedDemonModifier(state, range, npc, attackRate) },
                { state, range, npc -> applyBoltSpecModifier(state, range, npc, boltSpecDamage) },
                ::applyCorpBeastModifier,
            )

        return modifiers
            .fold(initialState) { state, mod -> mod(state, rangeAttributes, npcAttributes) }
            .accumulated
    }

    private fun applyTormentedDemonModifier(
        state: PvEDamageState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
        attackRate: Int,
    ): PvEDamageState {
        val unshieldedTormentedDemon =
            RangeAttr.Heavy in range && NpcAttr.TormentedDemonUnshielded in npc
        if (!unshieldedTormentedDemon) {
            return state
        }
        val bonusDamage = max(0, (attackRate * attackRate) - 16)
        return state.copy(accumulated = state.accumulated + bonusDamage)
    }

    private fun applyBoltSpecModifier(
        state: PvEDamageState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
        boltSpecDamage: Int,
    ): PvEDamageState {
        // TODO(combat): Vampyre mods
        return state.copy(accumulated = state.accumulated + boltSpecDamage)
    }

    private fun applyCorpBeastModifier(
        state: PvEDamageState,
        range: EnumSet<RangeAttr>,
        npc: EnumSet<NpcAttr>,
    ): PvEDamageState {
        val corpBeastReduction = NpcAttr.CorporealBeast in npc && RangeAttr.CorpBaneWeapon !in range
        if (!corpBeastReduction) {
            return state
        }
        return state.copy(accumulated = state.accumulated / 2)
    }

    public fun calculateEffectiveRanged(player: Player, attackStyle: RangedAttackStyle?): Int =
        calculateEffectiveRanged(
            visLevel = player.stat(stats.ranged),
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
        val styleBonus = attackStyle.styleBonus()
        val prayerBonus = vars.prayerBonus()
        val voidBonus = worn.voidBonus()
        return PlayerRangedMaxHit.calculateEffectiveRanged(
            visibleRangedLvl = visLevel,
            styleBonus = styleBonus,
            prayerBonus = prayerBonus,
            voidBonus = voidBonus,
        )
    }

    private fun RangedAttackStyle?.styleBonus(): Int =
        when (this) {
            RangedAttackStyle.Accurate -> 11
            else -> 8
        }

    private fun VarPlayerIntMap.prayerBonus(): Double =
        when {
            this[varbits.sharp_eye] == 1 -> 1.05
            this[varbits.hawk_eye] == 1 -> 1.1
            this[varbits.eagle_eye] == 1 -> {
                if (this[varbits.prayer_deadeye_unlocked] == 1) 1.18 else 1.15
            }
            this[varbits.rigour] == 1 -> 1.23
            else -> 1.0
        }

    private fun Inventory.voidBonus(): Double {
        val helm = this[Wearpos.Hat.slot]
        if (!EquipmentChecks.isVoidRangerHelm(helm)) {
            return 1.0
        }

        val gloves = this[Wearpos.Hands.slot]
        if (!EquipmentChecks.isVoidGloves(gloves)) {
            return 1.0
        }

        val top = this[Wearpos.Torso.slot]
        val legs = this[Wearpos.Legs.slot]

        if (EquipmentChecks.isEliteVoidTop(top) && EquipmentChecks.isEliteVoidRobe(legs)) {
            return 1.125
        }

        if (EquipmentChecks.isRegularVoidTop(top) && EquipmentChecks.isRegularVoidRobe(legs)) {
            return 1.1
        }

        return 1.0
    }
}
