package org.rsmod.content.mechanics.poison.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.hitmark_groups
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.timers
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.hit.queueHit
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.onPlayerLogin
import org.rsmod.api.script.onPlayerSoftTimer
import org.rsmod.game.entity.Player
import org.rsmod.game.hit.HitType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.type.param.ParamType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// =============================================================================
// REQUIRED ADDITIONS BEFORE THIS MODULE WILL COMPILE
// =============================================================================
//
// ── 1. BaseVarps.kt  (server-side-only block) ────────────────────────────────
//
//      // Active poison damage per tick (0 = not poisoned).
//      val poison_damage         = find("poison_damage")
//
//      // Active venom damage per tick (0 = not venomed).
//      val venom_damage          = find("venom_damage")
//
//      // Sub-tick counter 0..5: every 6 fires, poison damage decreases by 1.
//      val poison_sub_tick       = find("poison_sub_tick")
//
//      // Remaining ticks of poison immunity (from antipoison potions).
//      val poison_immunity_ticks = find("poison_immunity_ticks")
//
//      // Remaining ticks of venom immunity (from antivenom potions).
//      val venom_immunity_ticks  = find("venom_immunity_ticks")
//
//      // HP-orb indicator: 0=none, 1=poison (green), 1000000=venom (yellow).
//      // Uses vanilla transmit-id 102 so the client renders the correct colour.
//      val hp_orb_toxin          = find("hp_orb_toxin", 102)
//
// ── 2. BaseSpotanims.kt ──────────────────────────────────────────────────────
//
//      val poison_hit = find("poison_hit", <numeric-id>)   // green cloud, spotanim 84
//      val venom_hit  = find("venom_hit",  <numeric-id>)   // purple cloud, spotanim 1303
//
// ── 3. BaseSynths.kt ─────────────────────────────────────────────────────────
//
//      val poison_inflict = find("poison_inflict")
//      val venom_inflict  = find("venom_inflict")
//
// ── 4. BaseObjs.kt ───────────────────────────────────────────────────────────
//    When antipoison potions are added, uncomment the onOpHeld3 blocks below.
//    Names to use (4 doses each):
//      antipoison_1..4
//      superantipoison_1..4
//      antidote_plus_1..4      (Antidote+)
//      antidote_plus_plus_1..4 (Antidote++)
//      antivenom_1..4
//      antivenom_plus_1..4     (Antivenom+)
//
// =============================================================================

/**
 * Poison and Venom mechanics — OSRS rev 228, wiki-accurate.
 *
 * ## Poison
 * Deals damage every [TOXIN_TICK_INTERVAL] = 18 game ticks (10.8 s). Damage starts at the inflicted
 * value and decreases by 1 after every [POISON_HITS_PER_DECREMENT] = 6 hits. Ends when damage
 * reaches 0. Uses a green hitsplat ([hitmark_groups.poison_damage]).
 *
 * ## Venom
 * Starts at [VENOM_START_DAMAGE] = 6 and increases by [VENOM_DAMAGE_INCREMENT] = 2 per tick, capped
 * at [VENOM_MAX_DAMAGE] = 20. Uses a yellow/orange hitsplat ([hitmark_groups.venom]). Supersedes
 * regular poison: if a venomed player drinks regular antipoison, venom is downgraded to poison at
 * level 6.
 *
 * ## Immunity
 * `params.poison_immunity > 0` on any worn item blocks poison. `params.venom_immunity > 0` on any
 * worn item blocks venom. Potions grant a timed immunity window stored in dedicated server-side
 * varps.
 *
 * ## State (server-side-only varps — see above for required additions)
 * | Varp                          | Meaning                                   |
 * |-------------------------------|-------------------------------------------|
 * | `varps.poison_damage`         | Poison damage/tick (0 = none)             |
 * | `varps.venom_damage`          | Venom damage/tick  (0 = none)             |
 * | `varps.poison_sub_tick`       | Hit counter 0..5 for poison decrement     |
 * | `varps.poison_immunity_ticks` | Remaining ticks of potion poison immunity |
 * | `varps.venom_immunity_ticks`  | Remaining ticks of potion venom immunity  |
 * | `varps.hp_orb_toxin`          | Client varp 102 — HP-orb colour indicator |
 *
 * ## Public API (callable from combat/item scripts)
 * - [applyPoison] — inflict poison at a given damage value
 * - [applyVenom] — inflict venom
 * - [curePoison] — cure poison / downgrade venom, set immunity
 * - [cureVenom] — full venom cure, set immunity
 *
 * Status checks on [Player] (any context, require [ObjTypeList]):
 * - [Player.isPoisoned]
 * - [Player.isVenomed]
 * - [Player.isPoisonImmune]
 * - [Player.isVenomImmune]
 */
class PoisonScript @Inject constructor(private val objTypes: ObjTypeList) : PluginScript() {

    // =========================================================================
    // Var delegates  (server-side-only varps — must be added to BaseVarps.kt)
    // =========================================================================

    /** Active poison damage per tick. 0 = not poisoned. */
    private var Player.poisonDamage: Int by intVarp(varps.poison_damage)

    /** Active venom damage per tick. 0 = not venomed. */
    private var Player.venomDamage: Int by intVarp(varps.venom_damage)

    /**
     * Sub-tick counter in range 0..[POISON_HITS_PER_DECREMENT]-1. After [POISON_HITS_PER_DECREMENT]
     * fires the counter wraps to 0 and the poison damage is reduced by 1.
     */
    private var Player.poisonSubTick: Int by intVarp(varps.poison_sub_tick)

    /** Remaining ticks of poison immunity granted by potions. */
    private var Player.poisonImmunityTicks: Int by intVarp(varps.poison_immunity_ticks)

    /** Remaining ticks of venom immunity granted by potions. */
    private var Player.venomImmunityTicks: Int by intVarp(varps.venom_immunity_ticks)

    /**
     * Vanilla varp 102 — HP-orb toxin indicator: `0` = no toxin `1` = poisoned (green overlay)
     * `1_000_000` = venomed (yellow/orange overlay)
     */
    private var Player.hpOrbToxin: Int by intVarp(varps.hp_orb_toxin)

    // =========================================================================
    // Startup
    // =========================================================================

    override fun ScriptContext.startup() {
        // Soft timer: fires every TOXIN_TICK_INTERVAL ticks while active.
        // Player-level context (not ProtectedAccess), so we use Player.queueHit
        // directly — no suspended coroutine needed.
        onPlayerSoftTimer(timers.toxins) { player.toxinTick() }

        // Re-start the timer on login if the player has active toxin state.
        onPlayerLogin { player.restoreToxinTimerOnLogin() }
    }

    // =========================================================================
    // Soft-timer callback
    // =========================================================================

    /**
     * Processes one toxin tick (every [TOXIN_TICK_INTERVAL] game ticks).
     *
     * Execution order:
     * 1. Venom damage — deal hit, increase damage toward [VENOM_MAX_DAMAGE].
     * 2. Poison damage (skipped while venomed) — deal hit, decrement cycle.
     * 3. Immunity windows — countdown both poison and venom.
     * 4. Re-schedule soft-timer, or let it lapse when nothing is active.
     */
    private fun Player.toxinTick() {
        var stillActive = false

        // --- Venom ----------------------------------------------------------------
        if (venomDamage > 0) {
            stillActive = true
            val dmg = venomDamage

            queueHit(
                delay = 0,
                type = HitType.Typeless,
                damage = dmg,
                hitmark = hitmark_groups.venom,
            )

            val nextDmg = minOf(dmg + VENOM_DAMAGE_INCREMENT, VENOM_MAX_DAMAGE)
            if (nextDmg != dmg) {
                venomDamage = nextDmg
            }
        }

        // --- Poison (only when not venomed) ----------------------------------------
        if (venomDamage == 0 && poisonDamage > 0) {
            stillActive = true
            val dmg = poisonDamage

            queueHit(
                delay = 0,
                type = HitType.Typeless,
                damage = dmg,
                hitmark = hitmark_groups.poison_damage,
            )

            // Advance sub-tick counter; every 6 hits decrease the damage by 1.
            val nextSubTick = (poisonSubTick + 1) % POISON_HITS_PER_DECREMENT
            poisonSubTick = nextSubTick
            if (nextSubTick == 0) {
                val newDmg = dmg - 1
                if (newDmg <= 0) {
                    poisonDamage = 0
                    poisonSubTick = 0
                    syncHpOrb()
                    if (poisonImmunityTicks == 0) {
                        mes("The poison has worked its way out of your system.")
                    }
                } else {
                    poisonDamage = newDmg
                }
            }

            // Re-check: damage may have just been cleared above.
            if (poisonDamage > 0) stillActive = true
        }

        // --- Immunity countdowns ---------------------------------------------------
        if (poisonImmunityTicks > 0) {
            stillActive = true
            poisonImmunityTicks--
        }

        if (venomImmunityTicks > 0) {
            stillActive = true
            venomImmunityTicks--
        }

        // --- Reschedule or stop ---------------------------------------------------
        if (stillActive) {
            softTimer(timers.toxins, TOXIN_TICK_INTERVAL)
        }
        // If !stillActive, the soft-timer naturally lapses — no explicit clear needed.
    }

    // =========================================================================
    // Login restoration
    // =========================================================================

    private fun Player.restoreToxinTimerOnLogin() {
        val needsTimer =
            poisonDamage > 0 || venomDamage > 0 || poisonImmunityTicks > 0 || venomImmunityTicks > 0
        if (needsTimer) {
            softTimer(timers.toxins, TOXIN_TICK_INTERVAL)
        }
        // Re-sync the HP-orb in case client state was stale after login.
        syncHpOrb()
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    /** Sends the appropriate HP-orb toxin indicator to the client. */
    private fun Player.syncHpOrb() {
        hpOrbToxin =
            when {
                venomDamage > 0 -> HP_ORB_VENOM
                poisonDamage > 0 -> HP_ORB_POISON
                else -> HP_ORB_NONE
            }
    }

    // =========================================================================
    // Public ProtectedAccess API — callable from combat, items, and other scripts
    // =========================================================================

    /**
     * Inflicts poison on the player at [damage] per tick.
     *
     * Silently ignored if:
     * - The player is currently venomed (venom supersedes poison).
     * - The player is immune (gear or active potion immunity window).
     * - [damage] ≤ the player's current active poison damage (no downgrade).
     *
     * On first infliction:
     * - Sends "You have been poisoned!" game message.
     * - Plays the poison-infliction spotanim (TODO: once ref exists).
     * - Starts the toxin soft-timer at [TOXIN_TICK_INTERVAL].
     *
     * @param damage Damage dealt per tick (e.g. 4 for "Poison 4", 6 for "Poison 6").
     */
    fun ProtectedAccess.applyPoison(damage: Int) {
        require(damage > 0) { "applyPoison: damage must be > 0, got $damage" }

        if (player.venomDamage > 0) return // venom supersedes poison
        if (player.isPoisonImmune(objTypes)) return // gear or potion immunity
        if (damage <= player.poisonDamage) return // never downgrade

        val firstInfliction = player.poisonDamage == 0

        player.poisonDamage = damage
        player.poisonSubTick = 0
        player.syncHpOrb()

        if (firstInfliction) {
            mes("You have been poisoned!")
            // TODO: spotanim(spotanims.poison_hit) — add ref to BaseSpotanims.kt
            // TODO: soundSynth(synths.poison_inflict) — add ref to BaseSynths.kt
        }

        softTimer(timers.toxins, TOXIN_TICK_INTERVAL)
    }

    /**
     * Inflicts venom on the player.
     * - Always starts at [VENOM_START_DAMAGE] = 6.
     * - If the player is already venomed, this is a no-op.
     * - Clears any active poison (venom supersedes it).
     * - Ignored if the player is immune (gear or antivenom window).
     *
     * On success:
     * - Sends "You have been envenomed!" game message.
     * - Starts the toxin soft-timer.
     */
    fun ProtectedAccess.applyVenom() {
        if (player.isVenomImmune(objTypes)) return // gear or potion immunity
        if (player.venomDamage > 0) return // already venomed

        // Clear any existing poison — venom supersedes it.
        player.poisonDamage = 0
        player.poisonSubTick = 0

        player.venomDamage = VENOM_START_DAMAGE
        player.syncHpOrb()

        mes("You have been envenomed!")
        // TODO: spotanim(spotanims.venom_hit) — add ref to BaseSpotanims.kt
        // TODO: soundSynth(synths.venom_inflict) — add ref to BaseSynths.kt

        softTimer(timers.toxins, TOXIN_TICK_INTERVAL)
    }

    /**
     * Cures poison, or downgrades venom → poison, and starts the poison immunity window.
     *
     * OSRS behaviour:
     * - **Venomed**: regular antipoison converts venom → poison at level 6, not a full cure.
     *   [cureVenom] is required for antivenom potions.
     * - **Poisoned**: clears poison and grants [immunityTicks] of immunity.
     * - **Neither**: still grants the immunity window (drinking while healthy prevents future
     *   infliction for the window duration).
     *
     * The soft-timer is (re-)started if [immunityTicks] > 0 so the countdown runs even when the
     * player is not actively poisoned.
     *
     * @param immunityTicks Use one of [IMMUNITY_ANTIPOISON], [IMMUNITY_SUPERANTIPOISON],
     *   [IMMUNITY_ANTIDOTE_PLUS], or [IMMUNITY_ANTIDOTE_PLUS_PLUS].
     */
    fun ProtectedAccess.curePoison(immunityTicks: Int = IMMUNITY_ANTIPOISON) {
        when {
            player.venomDamage > 0 -> {
                // Antipoison: venom → poison 6 (not a full cure).
                player.venomDamage = 0
                player.poisonDamage = VENOM_START_DAMAGE
                player.poisonSubTick = 0
                mes("The antipoison potion has weakened the venom to a regular poison.")
            }
            player.poisonDamage > 0 -> {
                player.poisonDamage = 0
                player.poisonSubTick = 0
                mes("You are no longer poisoned.")
            }
        // else: healthy — grant immunity silently.
        }

        player.poisonImmunityTicks = immunityTicks
        player.syncHpOrb()

        // TODO: spotanim(spotanims.antipoison_cure) — add ref to BaseSpotanims.kt
        // TODO: soundSynth(synths.antipoison) — add ref to BaseSynths.kt

        // (Re-)start the timer so the immunity window counts down.
        if (
            immunityTicks > 0 ||
                player.venomImmunityTicks > 0 ||
                player.venomDamage > 0 ||
                player.poisonDamage > 0
        ) {
            softTimer(timers.toxins, TOXIN_TICK_INTERVAL)
        }
    }

    /**
     * Cures venom (and any active poison) and starts immunity windows.
     *
     * Used for Antivenom and Antivenom+ potions:
     * - Antivenom (4): `cureVenom(venomImmunityTicks = IMMUNITY_ANTIVENOM)`
     * - Antivenom+(4): `cureVenom(IMMUNITY_ANTIVENOM_PLUS, IMMUNITY_ANTIVENOM_PLUS)`
     *
     * @param venomImmunityTicks Duration of venom immunity after curing.
     * @param poisonImmunityTicks Concurrent poison immunity duration. Leave at 0 for basic
     *   Antivenom (it does not grant poison immunity). Set to [IMMUNITY_ANTIVENOM_PLUS] for
     *   Antivenom+.
     */
    fun ProtectedAccess.cureVenom(
        venomImmunityTicks: Int = IMMUNITY_ANTIVENOM,
        poisonImmunityTicks: Int = 0,
    ) {
        val wasVenomed = player.venomDamage > 0
        val wasPoisoned = player.poisonDamage > 0

        player.venomDamage = 0
        player.poisonDamage = 0
        player.poisonSubTick = 0

        player.venomImmunityTicks = venomImmunityTicks
        if (poisonImmunityTicks > 0) {
            player.poisonImmunityTicks = poisonImmunityTicks
        }

        player.syncHpOrb()

        when {
            wasVenomed -> mes("You have cured the venom.")
            wasPoisoned -> mes("You are no longer poisoned.")
            else -> mes("You feel protected against toxins.")
        }

        // TODO: spotanim(spotanims.antivenom_cure) — add ref to BaseSpotanims.kt
        // TODO: soundSynth(synths.antivenom) — add ref to BaseSynths.kt

        if (venomImmunityTicks > 0 || poisonImmunityTicks > 0) {
            softTimer(timers.toxins, TOXIN_TICK_INTERVAL)
        }
    }

    // =========================================================================
    // Antipoison / Antivenom drink handlers  (obj refs pending — see header)
    //
    // Uncomment each block when the corresponding obj refs are added to
    // BaseObjs.kt.  All potions use `onOpHeld3` (the "Drink" inventory option,
    // which is click-slot 3 in the right-click menu).
    //
    // The helper functions below the block stubs show the exact implementation
    // pattern to use once the obj refs exist.
    // =========================================================================

    /*
     * --- Antipoison (90-second immunity) ---
     * onOpHeld3(objs.antipoison_4) { drinkAntipoison(it.obj, objs.antipoison_3, IMMUNITY_ANTIPOISON) }
     * onOpHeld3(objs.antipoison_3) { drinkAntipoison(it.obj, objs.antipoison_2, IMMUNITY_ANTIPOISON) }
     * onOpHeld3(objs.antipoison_2) { drinkAntipoison(it.obj, objs.antipoison_1, IMMUNITY_ANTIPOISON) }
     * onOpHeld3(objs.antipoison_1) { drinkAntipoison(it.obj, objs.vial,         IMMUNITY_ANTIPOISON) }
     *
     * --- Superantipoison (3-minute immunity) ---
     * onOpHeld3(objs.superantipoison_4) { drinkAntipoison(it.obj, objs.superantipoison_3, IMMUNITY_SUPERANTIPOISON) }
     * onOpHeld3(objs.superantipoison_3) { drinkAntipoison(it.obj, objs.superantipoison_2, IMMUNITY_SUPERANTIPOISON) }
     * onOpHeld3(objs.superantipoison_2) { drinkAntipoison(it.obj, objs.superantipoison_1, IMMUNITY_SUPERANTIPOISON) }
     * onOpHeld3(objs.superantipoison_1) { drinkAntipoison(it.obj, objs.vial,              IMMUNITY_SUPERANTIPOISON) }
     *
     * --- Antidote+ (6-minute immunity) ---
     * onOpHeld3(objs.antidote_plus_4) { drinkAntipoison(it.obj, objs.antidote_plus_3, IMMUNITY_ANTIDOTE_PLUS) }
     * onOpHeld3(objs.antidote_plus_3) { drinkAntipoison(it.obj, objs.antidote_plus_2, IMMUNITY_ANTIDOTE_PLUS) }
     * onOpHeld3(objs.antidote_plus_2) { drinkAntipoison(it.obj, objs.antidote_plus_1, IMMUNITY_ANTIDOTE_PLUS) }
     * onOpHeld3(objs.antidote_plus_1) { drinkAntipoison(it.obj, objs.vial,            IMMUNITY_ANTIDOTE_PLUS) }
     *
     * --- Antidote++ (12-minute immunity) ---
     * onOpHeld3(objs.antidote_plus_plus_4) { drinkAntipoison(it.obj, objs.antidote_plus_plus_3, IMMUNITY_ANTIDOTE_PLUS_PLUS) }
     * onOpHeld3(objs.antidote_plus_plus_3) { drinkAntipoison(it.obj, objs.antidote_plus_plus_2, IMMUNITY_ANTIDOTE_PLUS_PLUS) }
     * onOpHeld3(objs.antidote_plus_plus_2) { drinkAntipoison(it.obj, objs.antidote_plus_plus_1, IMMUNITY_ANTIDOTE_PLUS_PLUS) }
     * onOpHeld3(objs.antidote_plus_plus_1) { drinkAntipoison(it.obj, objs.vial,                 IMMUNITY_ANTIDOTE_PLUS_PLUS) }
     *
     * --- Antivenom (3-minute venom immunity, no poison immunity) ---
     * onOpHeld3(objs.antivenom_4) { drinkAntivenom(it.obj, objs.antivenom_3) }
     * onOpHeld3(objs.antivenom_3) { drinkAntivenom(it.obj, objs.antivenom_2) }
     * onOpHeld3(objs.antivenom_2) { drinkAntivenom(it.obj, objs.antivenom_1) }
     * onOpHeld3(objs.antivenom_1) { drinkAntivenom(it.obj, objs.vial) }
     *
     * --- Antivenom+ (~16-min venom + poison immunity) ---
     * onOpHeld3(objs.antivenom_plus_4) { drinkAntivenomPlus(it.obj, objs.antivenom_plus_3) }
     * onOpHeld3(objs.antivenom_plus_3) { drinkAntivenomPlus(it.obj, objs.antivenom_plus_2) }
     * onOpHeld3(objs.antivenom_plus_2) { drinkAntivenomPlus(it.obj, objs.antivenom_plus_1) }
     * onOpHeld3(objs.antivenom_plus_1) { drinkAntivenomPlus(it.obj, objs.vial) }
     */

    /*
     * Implementation helpers — uncomment together with the handler stubs above.
     *
     * private suspend fun ProtectedAccess.drinkAntipoison(
     *     obj: InvObj,
     *     remainder: ObjType,
     *     immunityTicks: Int,
     * ) {
     *     invDel(invs.inv, obj)
     *     invAdd(invs.inv, remainder, 1)
     *     curePoison(immunityTicks)
     * }
     *
     * private suspend fun ProtectedAccess.drinkAntivenom(
     *     obj: InvObj,
     *     remainder: ObjType,
     * ) {
     *     invDel(invs.inv, obj)
     *     invAdd(invs.inv, remainder, 1)
     *     cureVenom(venomImmunityTicks = IMMUNITY_ANTIVENOM, poisonImmunityTicks = 0)
     * }
     *
     * private suspend fun ProtectedAccess.drinkAntivenomPlus(
     *     obj: InvObj,
     *     remainder: ObjType,
     * ) {
     *     invDel(invs.inv, obj)
     *     invAdd(invs.inv, remainder, 1)
     *     cureVenom(
     *         venomImmunityTicks  = IMMUNITY_ANTIVENOM_PLUS,
     *         poisonImmunityTicks = IMMUNITY_ANTIVENOM_PLUS,
     *     )
     * }
     */

    // =========================================================================
    // Constants and Player extension functions  (companion object)
    // =========================================================================

    companion object {

        // ------------------------------------------------------------------
        // Timing
        // ------------------------------------------------------------------

        /** Poison and venom both tick every 18 game ticks (10.8 seconds). */
        const val TOXIN_TICK_INTERVAL = 18

        /** Poison damage decreases by 1 after every 6 ticks (hits). */
        const val POISON_HITS_PER_DECREMENT = 6

        // ------------------------------------------------------------------
        // Venom damage progression
        // ------------------------------------------------------------------

        /** Initial venom damage when first inflicted. */
        const val VENOM_START_DAMAGE = 6

        /** Venom damage increases by this amount per tick. */
        const val VENOM_DAMAGE_INCREMENT = 2

        /** Venom damage cap. */
        const val VENOM_MAX_DAMAGE = 20

        // ------------------------------------------------------------------
        // Immunity durations in game ticks (18 ticks = 1 poison period)
        // ------------------------------------------------------------------

        /** Antipoison (4): ~90 seconds — 150 ticks. */
        const val IMMUNITY_ANTIPOISON = 150

        /** Superantipoison (4): ~3 minutes — 300 ticks. */
        const val IMMUNITY_SUPERANTIPOISON = 300

        /** Antidote+ (4): ~6 minutes — 600 ticks. */
        const val IMMUNITY_ANTIDOTE_PLUS = 600

        /** Antidote++ (4): ~12 minutes — 1200 ticks. */
        const val IMMUNITY_ANTIDOTE_PLUS_PLUS = 1200

        /** Antivenom (4): ~3 minutes of venom immunity — 300 ticks. */
        const val IMMUNITY_ANTIVENOM = 300

        /**
         * Antivenom+ (4): ~16 minutes of venom AND poison immunity — 1600 ticks. Antivenom+ is the
         * only potion that grants both simultaneously.
         */
        const val IMMUNITY_ANTIVENOM_PLUS = 1600

        // ------------------------------------------------------------------
        // HP-orb varp values  (vanilla client varp 102)
        // ------------------------------------------------------------------

        private const val HP_ORB_NONE = 0
        private const val HP_ORB_POISON = 1
        private const val HP_ORB_VENOM = 1_000_000

        // ------------------------------------------------------------------
        // Player status queries  (callable from ANY context — no PA needed)
        //
        // These read directly from Player.vars to avoid needing a class
        // instance (which matters for combat and other external callers).
        // ------------------------------------------------------------------

        /**
         * Returns `true` if the player is actively poisoned (and not venomed).
         *
         * Venom supersedes poison, so a venomed player is NOT considered poisoned for the purposes
         * of damage/immunity checks.
         */
        fun Player.isPoisoned(): Boolean =
            vars[varps.poison_damage] > 0 && vars[varps.venom_damage] == 0

        /** Returns `true` if the player is actively venomed. */
        fun Player.isVenomed(): Boolean = vars[varps.venom_damage] > 0

        /**
         * Returns `true` if the player cannot be poisoned right now.
         *
         * Checks in order:
         * 1. Potion-granted immunity window ([varps.poison_immunity_ticks] > 0).
         * 2. Any worn item with `params.poison_immunity > 0` (e.g. serpentine helm, tanzanite helm,
         *    magma helm).
         *
         * @param objTypes Required to inspect worn-equipment param values.
         */
        fun Player.isPoisonImmune(objTypes: ObjTypeList): Boolean {
            if (vars[varps.poison_immunity_ticks] > 0) return true
            return hasWornImmunityParam(objTypes, params.poison_immunity)
        }

        /**
         * Returns `true` if the player cannot be venomed right now.
         *
         * Checks in order:
         * 1. Potion-granted venom immunity window ([varps.venom_immunity_ticks] > 0).
         * 2. Any worn item with `params.venom_immunity > 0`.
         *
         * @param objTypes Required to inspect worn-equipment param values.
         */
        fun Player.isVenomImmune(objTypes: ObjTypeList): Boolean {
            if (vars[varps.venom_immunity_ticks] > 0) return true
            return hasWornImmunityParam(objTypes, params.venom_immunity)
        }

        /**
         * Scans all worn equipment slots and returns `true` if any item has [immunityParam] set to
         * a value > 0.
         *
         * This mirrors the pattern used in [WornBonuses.calculate] which iterates `Wearpos.entries`
         * and calls `objTypes[obj].param(...)`.
         */
        private fun Player.hasWornImmunityParam(
            objTypes: ObjTypeList,
            immunityParam: ParamType<Int>,
        ): Boolean {
            for (wearpos in Wearpos.entries) {
                val invObj = worn[wearpos.slot] ?: continue
                val type = objTypes.getOrNull(invObj) ?: continue
                if (type.param(immunityParam) > 0) return true
            }
            return false
        }
    }
}
