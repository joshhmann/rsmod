package org.rsmod.content.mechanics.achievementdiaries.events

import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Player
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.obj.ObjType

/**
 * Events for achievement diary task tracking.
 *
 * These events are published by skill scripts when actions that may complete diary tasks are
 * performed. The diary system subscribes to these events and updates task completion accordingly.
 */

// ==================== MINING EVENTS ====================

/** Published when a player successfully mines ore. */
data class OreMinedEvent(val player: Player, val oreType: ObjType, val locX: Int, val locZ: Int) :
    UnboundEvent

// ==================== WOODCUTTING EVENTS ====================

/** Published when a player successfully chops a tree. */
data class TreeChoppedEvent(
    val player: Player,
    val treeType: LocType,
    val logsType: ObjType,
    val locX: Int,
    val locZ: Int,
) : UnboundEvent

// ==================== CRAFTING EVENTS ====================

/** Published when a player fires pottery. */
data class PotteryFiredEvent(
    val player: Player,
    val product: ObjType,
    val locX: Int,
    val locZ: Int,
) : UnboundEvent

// ==================== RUNECRAFTING EVENTS ====================

/** Published when a player crafts runes. */
data class RunesCraftedEvent(val player: Player, val runeType: ObjType, val quantity: Int) :
    UnboundEvent

// ==================== FISHING EVENTS ====================

/** Published when a player catches a fish. */
data class FishCaughtEvent(
    val player: Player,
    val fishType: ObjType,
    val locX: Int,
    val locZ: Int,
) : UnboundEvent

// ==================== THIEVING EVENTS ====================

/** Published when a player steals from a stall. */
data class StallThievedEvent(val player: Player, val stallType: LocType, val product: ObjType) :
    UnboundEvent

// ==================== COMBAT EVENTS ====================

/** Published when a player kills an NPC. */
data class NpcKilledEvent(
    val player: Player,
    val npcName: String,
    val npcId: Int,
    val locX: Int,
    val locZ: Int,
) : UnboundEvent

// ==================== AGILITY EVENTS ====================

/** Published when a player completes an agility shortcut. */
data class AgilityShortcutCompletedEvent(
    val player: Player,
    val shortcutName: String,
    val locX: Int,
    val locZ: Int,
) : UnboundEvent

// ==================== DUNGEON EVENTS ====================

/** Published when a player enters a dungeon level. */
data class DungeonLevelEnteredEvent(val player: Player, val dungeonName: String, val level: Int) :
    UnboundEvent

// ==================== SHOP EVENTS ====================

/** Published when a player browses a shop. */
data class ShopBrowsedEvent(val player: Player, val shopName: String, val npcName: String) :
    UnboundEvent

/** Published when a player buys an item from a shop. */
data class ItemPurchasedEvent(
    val player: Player,
    val shopName: String,
    val item: ObjType,
    val quantity: Int,
) : UnboundEvent

// ==================== NPC INTERACTION EVENTS ====================

/** Published when a player talks to an NPC. */
data class NpcTalkedToEvent(val player: Player, val npcName: String, val npcId: Int) : UnboundEvent

/** Published when an NPC teleports a player. */
data class NpcTeleportEvent(val player: Player, val npcName: String, val destination: String) :
    UnboundEvent

// ==================== ITEM USE EVENTS ====================

/** Published when a player uses an item on an NPC. */
data class ItemUsedOnNpcEvent(val player: Player, val item: ObjType, val npcName: String) :
    UnboundEvent
