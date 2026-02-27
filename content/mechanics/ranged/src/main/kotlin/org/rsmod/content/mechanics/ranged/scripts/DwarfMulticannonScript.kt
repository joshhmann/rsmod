package org.rsmod.content.mechanics.ranged.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.onOpHeld1
import org.rsmod.api.script.onPlayerLogin
import org.rsmod.content.mechanics.ranged.configs.CannonObjs
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Dwarf Multicannon mechanics — baseline implementation.
 *
 * ## Overview
 * The Dwarf Multicannon is a powerful ranged weapon that can be placed in the world and will
 * automatically fire at nearby NPCs. It requires cannonballs to operate.
 *
 * ## Setup Process
 * 1. Player uses "set_cannon" item on a valid tile
 * 2. Base loc is spawned, player interacts to add stand
 * 3. Stand added, player interacts to add barrels
 * 4. Barrels added, player interacts to add furnace
 * 5. Cannon is complete and ready to load cannonballs
 *
 * ## Operation
 * - Right-click "Fire" on cannon to start (requires cannonballs)
 * - Cannon rotates and fires at NPCs within range (8 tiles)
 * - Automatically stops when out of ammo or no targets
 * - Can be picked up to return the cannon set to inventory
 *
 * ## State Tracking
 * - Player varp stores cannon state: 0=none, 1=placed, 2=loaded, 3=firing
 * - Cannonball count stored in separate varp
 * - Cannon coordinates stored in player state
 *
 * ## Future Enhancements
 * - Cannon decay after 20 minutes of inactivity
 * - PvP world restrictions
 * - Slayer task benefits (auto-destruction on task completion)
 */
class DwarfMulticannonScript @Inject constructor(private val objTypes: ObjTypeList) :
    PluginScript() {

    // =========================================================================
    // Player State (server-side varps)
    // =========================================================================

    /** Cannon state: 0=none, 1=placed, 2=loaded, 3=firing */
    private var Player.cannonState: Int by intVarp(varps.generic_temp_state_65516)

    /** Cannonballs loaded: 0-30 (max capacity) */
    private var Player.cannonAmmo: Int by intVarp(varps.generic_storage_65531)

    /** Cannon X coordinate (relative to player) */
    private var Player.cannonX: Int by intVarp(varps.generic_temp_coords_65529)

    /** Cannon Z coordinate (relative to player) */
    private var Player.cannonZ: Int by intVarp(varps.temp_restore_65527)

    // =========================================================================
    // Startup
    // =========================================================================

    override fun ScriptContext.startup() {
        // Handle login - restore any active cannon timers/state
        onPlayerLogin { player.restoreCannonStateOnLogin() }

        // Place cannon from inventory
        onOpHeld1(CannonObjs.set_cannon) { placeCannon() }
    }

    // =========================================================================
    // Cannon Placement
    // =========================================================================

    /**
     * Attempts to place the cannon at the player's current location. Validates position, checks for
     * existing cannons, and spawns the base.
     */
    private suspend fun ProtectedAccess.placeCannon() {
        // Check if player already has a cannon placed
        if (player.cannonState != CANNON_STATE_NONE) {
            mes("You already have a cannon placed.")
            return
        }

        // Validate placement location
        if (!isValidCannonPlacement()) {
            mes("You cannot place the cannon here.")
            return
        }

        // Check for existing cannons nearby (one cannon per 3x3 area)
        if (hasNearbyCannon()) {
            mes("There is already a cannon nearby.")
            return
        }

        // Consume the cannon set from inventory
        // TODO: Implement inventory deduction

        // Store cannon location
        player.cannonX = player.coords.x
        player.cannonZ = player.coords.z
        player.cannonState = CANNON_STATE_PLACED
        player.cannonAmmo = 0

        // Spawn cannon base loc
        // TODO: Spawn cannon base at player location
        // val baseLoc = locRepo.add(player.coords, CannonObjs.base)

        mes("You place the cannon base.")
    }

    /**
     * Validates if the current location allows cannon placement. Cannot place in banks, near
     * doorways, on certain terrain, etc.
     */
    private fun ProtectedAccess.isValidCannonPlacement(): Boolean {
        // TODO: Implement proper validation
        // - Not in bank areas
        // - Not in certain instanced areas
        // - Not on blocked tiles
        // - Not in PvP-safe zones if PvP world
        return true
    }

    /** Checks if there's already a cannon within the exclusion radius. */
    private fun ProtectedAccess.hasNearbyCannon(): Boolean {
        // TODO: Check for nearby player cannons
        return false
    }

    // =========================================================================
    // Cannon Loading
    // =========================================================================

    /** Loads cannonballs into the cannon. Maximum capacity is 30 cannonballs. */
    private suspend fun ProtectedAccess.loadCannon() {
        if (player.cannonState < CANNON_STATE_PLACED) {
            mes("You need to set up the cannon first.")
            return
        }

        if (player.cannonAmmo >= CANNON_MAX_AMMO) {
            mes("The cannon is already full.")
            return
        }

        // TODO: Check inventory for cannonballs and consume them
        // TODO: Add ammo to cannon state

        val toLoad = minOf(CANNON_MAX_AMMO - player.cannonAmmo, 30)
        player.cannonAmmo += toLoad

        mes("You load the cannon with $toLoad cannonballs.")
    }

    // =========================================================================
    // Cannon Firing
    // =========================================================================

    /** Starts the cannon firing at nearby NPCs. */
    private fun ProtectedAccess.fireCannon() {
        if (player.cannonState < CANNON_STATE_PLACED) {
            mes("You need to set up the cannon first.")
            return
        }

        if (player.cannonAmmo <= 0) {
            mes("The cannon is empty. You need to load it with cannonballs.")
            return
        }

        if (player.cannonState == CANNON_STATE_FIRING) {
            mes("The cannon is already firing.")
            return
        }

        player.cannonState = CANNON_STATE_FIRING
        mes("You light the cannon's fuse.")

        // TODO: Start cannon rotation and firing cycle
        // Schedule periodic checks for targets in range
    }

    /** Cannon firing tick - called on a timer when cannon is active. */
    private fun Player.cannonFireTick() {
        if (cannonState != CANNON_STATE_FIRING) {
            return
        }

        if (cannonAmmo <= 0) {
            // Out of ammo - stop firing
            cannonState = CANNON_STATE_PLACED
            // TODO: Notify player
            return
        }

        // TODO: Find targets in 8-tile radius
        // TODO: Rotate cannon toward target
        // TODO: Fire and deal damage
        // TODO: Consume cannonball

        cannonAmmo--
    }

    // =========================================================================
    // Cannon Pickup
    // =========================================================================

    /** Picks up the cannon and returns it to inventory. */
    private suspend fun ProtectedAccess.pickupCannon() {
        if (player.cannonState < CANNON_STATE_PLACED) {
            return
        }

        // TODO: Validate player is near cannon
        // TODO: Remove cannon locs from world
        // TODO: Add cannon set to inventory (or drop if full)

        val remainingAmmo = player.cannonAmmo
        player.cannonState = CANNON_STATE_NONE
        player.cannonAmmo = 0

        if (remainingAmmo > 0) {
            // TODO: Return remaining cannonballs to inventory or drop
            mes("You pick up the cannon. $remainingAmmo cannonballs fall out.")
        } else {
            mes("You pick up the cannon.")
        }
    }

    // =========================================================================
    // Login Restoration
    // =========================================================================

    private fun Player.restoreCannonStateOnLogin() {
        // If cannon was firing, resume it
        if (cannonState == CANNON_STATE_FIRING) {
            // TODO: Restart cannon timer
        }
    }

    // =========================================================================
    // Constants
    // =========================================================================

    companion object {
        /** Cannon not placed */
        const val CANNON_STATE_NONE = 0

        /** Cannon placed but not loaded */
        const val CANNON_STATE_PLACED = 1

        /** Cannon loaded but not firing */
        const val CANNON_STATE_LOADED = 2

        /** Cannon actively firing */
        const val CANNON_STATE_FIRING = 3

        /** Maximum cannonball capacity */
        const val CANNON_MAX_AMMO = 30

        /** Cannon firing interval in ticks */
        const val CANNON_FIRE_INTERVAL = 6 // 3.6 seconds per shot

        /** Cannon range in tiles */
        const val CANNON_RANGE = 8

        /** Cannon base hitpoints for damage calculation */
        const val CANNON_BASE_DAMAGE = 30
    }
}
