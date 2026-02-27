package org.rsmod.content.generic.guilds

import org.rsmod.api.player.back
import org.rsmod.api.player.feet
import org.rsmod.api.player.front
import org.rsmod.api.player.hands
import org.rsmod.api.player.hat
import org.rsmod.api.player.lefthand
import org.rsmod.api.player.legs
import org.rsmod.api.player.quiver
import org.rsmod.api.player.righthand
import org.rsmod.api.player.ring
import org.rsmod.api.player.torso
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.isType
import org.rsmod.game.type.obj.ObjType

/**
 * Guild entry requirement system.
 *
 * Provides standardized equipment slot checks for guild entry requirements.
 *
 * Usage:
 * ```kotlin
 * // Check for chef's hat
 * if (!player.hasEquipped(objs.chefs_hat, EquipmentSlot.Hat)) {
 *     mes("You need to be wearing a chef's hat to enter.")
 *     return
 * }
 * ```
 */
public object GuildEntry {

    /**
     * Checks if a player has a specific item equipped in the given slot.
     *
     * @param player The player to check
     * @param obj The required object type
     * @param slot The equipment slot to check
     * @return True if the player has the item equipped
     */
    public fun hasEquipped(player: Player, obj: ObjType, slot: EquipmentSlot): Boolean {
        val wornItem =
            when (slot) {
                EquipmentSlot.Hat -> player.hat
                EquipmentSlot.Cape -> player.back
                EquipmentSlot.Amulet -> player.front
                EquipmentSlot.Weapon -> player.righthand
                EquipmentSlot.Torso -> player.torso
                EquipmentSlot.Shield -> player.lefthand
                EquipmentSlot.Legs -> player.legs
                EquipmentSlot.Hands -> player.hands
                EquipmentSlot.Feet -> player.feet
                EquipmentSlot.Ring -> player.ring
                EquipmentSlot.Ammo -> player.quiver
            }
        return wornItem.isType(obj)
    }
}

/** Equipment slots for guild entry requirements. */
public enum class EquipmentSlot {
    Hat,
    Cape,
    Amulet,
    Weapon,
    Torso,
    Shield,
    Legs,
    Hands,
    Feet,
    Ring,
    Ammo,
}
