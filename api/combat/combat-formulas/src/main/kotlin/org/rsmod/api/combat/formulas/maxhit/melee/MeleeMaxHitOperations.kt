package org.rsmod.api.combat.formulas.maxhit.melee

import java.util.EnumSet
import kotlin.math.max
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.formulas.attributes.CombatMeleeAttributes
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.combat.maxhit.player.PlayerMeleeMaxHit
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.righthand
import org.rsmod.api.player.stat.stat
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.vars.VarPlayerIntMap

private typealias MeleeAttr = CombatMeleeAttributes

private typealias NpcAttr = CombatNpcAttributes

/**
 * Data class representing a damage modification step. Captures both the accumulated damage and
 * original base for modifiers that need it.
 */
private data class DamageState(val accumulated: Int, val base: Int)

/**
 * Type alias for a damage modifier function. Takes current state and attributes, returns new state.
 */
private typealias DamageModifier =
    (DamageState, EnumSet<MeleeAttr>, EnumSet<NpcAttr>) -> DamageState

public object MeleeMaxHitOperations {
    public fun modifyBaseDamage(
        baseDamage: Int,
        meleeAttributes: EnumSet<CombatMeleeAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val initialState = DamageState(accumulated = baseDamage, base = baseDamage)

        // Define all modifiers in order of application
        val modifiers: List<DamageModifier> =
            listOf(
                // Step 1: Amulet modifiers (mutually exclusive)
                ::applyAmuletModifier,
                // Step 2: Demon weapons
                ::applyArclightModifier,
                ::applyBurningClawsModifier,
                // Step 3: Obsidian (additive, uses base)
                ::applyObsidianModifier,
                // Step 4: Type-specific multipliers
                ::applyDragonHunterModifier,
                ::applyKerisModifier,
                ::applyBarroniteModifier,
                ::applyRevenantWeaponModifier,
                ::applySilverlightModifier,
                ::applyLeafBladedModifier,
                // Step 5: Additive bonuses
                ::applyColossalBladeModifier,
                ::applyRatBoneModifier,
                // Step 6: Inquisitor (conditional on attack style)
                { state, melee, npc -> applyInquisitorModifier(state, melee, npc) },
            )

        // Apply all modifiers sequentially using fold
        return modifiers
            .fold(initialState) { state, modifier ->
                modifier(state, meleeAttributes, npcAttributes)
            }
            .accumulated
    }

    // Step 1: Amulet modifiers (mutually exclusive)
    private fun applyAmuletModifier(
        state: DamageState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): DamageState {
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

    // Step 2a: Arclight
    private fun applyArclightModifier(
        state: DamageState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): DamageState {
        if (MeleeAttr.Arclight !in melee || NpcAttr.Demon !in npc) {
            return state
        }
        val multiplier = if (NpcAttr.DemonbaneResistance in npc) 149 else 170
        return state.copy(accumulated = scale(state.accumulated, multiplier, divisor = 100))
    }

    // Step 2b: Burning Claws
    private fun applyBurningClawsModifier(
        state: DamageState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): DamageState {
        if (MeleeAttr.BurningClaws !in melee || NpcAttr.Demon !in npc) {
            return state
        }
        val multiplier = if (NpcAttr.DemonbaneResistance in npc) 207 else 210
        return state.copy(accumulated = scale(state.accumulated, multiplier, divisor = 200))
    }

    // Step 3: Obsidian (additive - adds base/10 to accumulated)
    private fun applyObsidianModifier(
        state: DamageState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): DamageState {
        if (MeleeAttr.Obsidian !in melee || MeleeAttr.TzHaarWeapon !in melee) {
            return state
        }
        return state.copy(accumulated = state.accumulated + state.base / 10)
    }

    // Step 4a: Dragon Hunter weapons
    private fun applyDragonHunterModifier(
        state: DamageState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): DamageState {
        if (NpcAttr.Draconic !in npc) {
            return state
        }
        val applies = MeleeAttr.DragonHunterLance in melee || MeleeAttr.DragonHunterWand in melee
        if (!applies) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 6, divisor = 5))
    }

    // Step 4b: Keris weapons
    private fun applyKerisModifier(
        state: DamageState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): DamageState {
        if (NpcAttr.Kalphite !in npc) {
            return state
        }
        val applies =
            MeleeAttr.KerisWeapon in melee ||
                MeleeAttr.KerisBreachPartisan in melee ||
                MeleeAttr.KerisSunPartisan in melee
        if (!applies) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 133, divisor = 100))
    }

    // Step 4c: Barronite Mace
    private fun applyBarroniteModifier(
        state: DamageState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): DamageState {
        if (MeleeAttr.BarroniteMaceWeapon !in melee || NpcAttr.Golem !in npc) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 23, divisor = 20))
    }

    // Step 4d: Revenant weapons
    private fun applyRevenantWeaponModifier(
        state: DamageState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): DamageState {
        if (MeleeAttr.RevenantWeapon !in melee || NpcAttr.Wilderness !in npc) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 3, divisor = 2))
    }

    // Step 4e: Silverlight
    private fun applySilverlightModifier(
        state: DamageState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): DamageState {
        if (MeleeAttr.Silverlight !in melee || NpcAttr.Demon !in npc) {
            return state
        }
        val multiplier = if (NpcAttr.DemonbaneResistance in npc) 71 else 80
        return state.copy(accumulated = scale(state.accumulated, multiplier, divisor = 50))
    }

    // Step 4f: Leaf-bladed
    private fun applyLeafBladedModifier(
        state: DamageState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): DamageState {
        if (MeleeAttr.LeafBladed !in melee || NpcAttr.Leafy !in npc) {
            return state
        }
        return state.copy(accumulated = scale(state.accumulated, multiplier = 47, divisor = 40))
    }

    // Step 5a: Colossal Blade (additive)
    private fun applyColossalBladeModifier(
        state: DamageState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): DamageState {
        if (MeleeAttr.ColossalBlade !in melee) {
            return state
        }
        val additive =
            when {
                NpcAttr.Size2 in npc -> 4
                NpcAttr.Size3 in npc -> 6
                NpcAttr.Size4 in npc -> 8
                NpcAttr.Size5OrMore in npc -> 10
                else -> 2
            }
        return state.copy(accumulated = state.accumulated + additive)
    }

    // Step 5b: Rat Bone (additive)
    private fun applyRatBoneModifier(
        state: DamageState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): DamageState {
        if (MeleeAttr.RatBoneWeapon !in melee || NpcAttr.Rat !in npc) {
            return state
        }
        return state.copy(accumulated = state.accumulated + 10)
    }

    // Step 6: Inquisitor (conditional on attack style)
    private fun applyInquisitorModifier(
        state: DamageState,
        melee: EnumSet<MeleeAttr>,
        npc: EnumSet<NpcAttr>,
    ): DamageState {
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

    public fun modifyPostSpec(
        modifiedDamage: Int,
        attackRate: Int,
        currHp: Int,
        maxHp: Int,
        meleeAttributes: EnumSet<CombatMeleeAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        // Gadderhammer modifier
        val afterGadderhammer =
            if (NpcAttr.Shade in npcAttributes) {
                when {
                    MeleeAttr.GadderhammerProc in meleeAttributes -> modifiedDamage * 2
                    MeleeAttr.Gadderhammer in meleeAttributes ->
                        scale(modifiedDamage, multiplier = 5, divisor = 4)
                    else -> modifiedDamage
                }
            } else {
                modifiedDamage
            }

        // Keris proc modifier (3x damage)
        val afterKerisProc =
            if (MeleeAttr.KerisProc in meleeAttributes && NpcAttr.Kalphite in npcAttributes) {
                afterGadderhammer * 3
            } else {
                afterGadderhammer
            }

        // Tormented Demon unshielded modifier
        val afterTormentedDemon =
            if (
                MeleeAttr.Crush in meleeAttributes &&
                    NpcAttr.TormentedDemonUnshielded in npcAttributes
            ) {
                val bonusDamage = max(0, (attackRate * attackRate) - 16)
                afterKerisProc + bonusDamage
            } else {
                afterKerisProc
            }

        // Dharok's damage based on missing HP
        val afterDharoks =
            if (MeleeAttr.Dharoks in meleeAttributes) {
                val multiplier = (maxHp - currHp) * maxHp
                afterTormentedDemon + scale(afterTormentedDemon, multiplier, divisor = 10_000)
            } else {
                afterTormentedDemon
            }

        // Berserker Necklace passive
        val afterBerserker =
            if (
                MeleeAttr.BerserkerNeck in meleeAttributes &&
                    MeleeAttr.TzHaarWeapon in meleeAttributes
            ) {
                scale(afterDharoks, multiplier = 6, divisor = 5)
            } else {
                afterDharoks
            }

        // TODO(combat): Vampyre mods

        // Corporeal Beast damage reduction
        return if (
            NpcAttr.CorporealBeast in npcAttributes && MeleeAttr.CorpBaneWeapon !in meleeAttributes
        ) {
            afterBerserker / 2
        } else {
            afterBerserker
        }
    }

    public fun calculateEffectiveStrength(player: Player, attackStyle: MeleeAttackStyle?): Int {
        val strengthLevel = player.stat(stats.strength)
        val soulreaperAxe = EquipmentChecks.isSoulreaperAxe(player.righthand)
        val soulStackBonus = if (soulreaperAxe) player.vars.soulStackBonus() else 1.0
        return calculateEffectiveStrength(
            visLevel = strengthLevel,
            weaponBonus = soulStackBonus,
            vars = player.vars,
            worn = player.worn,
            attackStyle = attackStyle,
        )
    }

    private fun calculateEffectiveStrength(
        visLevel: Int,
        weaponBonus: Double,
        vars: VarPlayerIntMap,
        worn: Inventory,
        attackStyle: MeleeAttackStyle?,
    ): Int {
        val styleBonus = attackStyle.styleBonus()
        val prayerBonus = vars.prayerBonus()
        val voidBonus = worn.voidBonus()
        return PlayerMeleeMaxHit.calculateEffectiveStrength(
            visibleStrengthLvl = visLevel,
            styleBonus = styleBonus,
            prayerBonus = prayerBonus,
            voidBonus = voidBonus,
            weaponBonus = weaponBonus,
        )
    }

    private fun MeleeAttackStyle?.styleBonus(): Int =
        when (this) {
            MeleeAttackStyle.Controlled -> 9
            MeleeAttackStyle.Aggressive -> 11
            else -> 8
        }

    private fun VarPlayerIntMap.prayerBonus(): Double =
        when {
            this[varbits.prayer_burstofstrength] == 1 -> 1.05
            this[varbits.prayer_superhumanstrength] == 1 -> 1.1
            this[varbits.prayer_ultimatestrength] == 1 -> 1.15
            this[varbits.prayer_chivalry] == 1 -> 1.18
            this[varbits.prayer_piety] == 1 -> 1.23
            else -> 1.0
        }

    private fun Inventory.voidBonus(): Double {
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

    private fun VarPlayerIntMap.soulStackBonus(): Double {
        val souls = this[varps.soulreaper_stacks]
        return 1.0 + (souls * 0.06)
    }
}
