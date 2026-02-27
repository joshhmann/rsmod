package org.rsmod.api.combat.formulas.maxhit

import java.util.EnumSet
import kotlin.math.max
import org.rsmod.api.combat.formulas.attributes.DamageReductionAttributes
import org.rsmod.api.combat.formulas.scale

/** Data class representing damage reduction state. */
private data class DamageState(val damage: Int, val activeDefenceBonus: Int?)

/** Type alias for a damage reduction modifier function. */
private typealias DamageModifier = (DamageState, EnumSet<DamageReductionAttributes>) -> DamageState

internal object MaxHitOperations {
    fun applyDamageReductions(
        startDamage: Int,
        activeDefenceBonus: Int?,
        reductionAttributes: EnumSet<DamageReductionAttributes>,
    ): Int {
        val initialState =
            DamageState(damage = startDamage, activeDefenceBonus = activeDefenceBonus)

        val modifiers: List<DamageModifier> =
            listOf(::applyElysianModifier, ::applyDinhsModifier, ::applyJusticiarModifier)

        return modifiers.fold(initialState) { state, mod -> mod(state, reductionAttributes) }.damage
    }

    private fun applyElysianModifier(
        state: DamageState,
        attrs: EnumSet<DamageReductionAttributes>,
    ): DamageState {
        if (DamageReductionAttributes.ElysianProc !in attrs) {
            return state
        }
        return state.copy(damage = scale(state.damage, multiplier = 3, divisor = 4))
    }

    private fun applyDinhsModifier(
        state: DamageState,
        attrs: EnumSet<DamageReductionAttributes>,
    ): DamageState {
        if (DamageReductionAttributes.DinhsBlock !in attrs) {
            return state
        }
        return state.copy(damage = scale(state.damage, multiplier = 4, divisor = 5))
    }

    private fun applyJusticiarModifier(
        state: DamageState,
        attrs: EnumSet<DamageReductionAttributes>,
    ): DamageState {
        if (DamageReductionAttributes.Justiciar !in attrs) {
            return state
        }
        val defenceBonus = checkNotNull(state.activeDefenceBonus)
        val factor = defenceBonus / 3000.0
        // Damage reduction effect will always reduce at least `1`.
        val reduction = max(1, (state.damage * factor).toInt())
        return state.copy(damage = max(0, state.damage - reduction))
    }
}
