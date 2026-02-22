package org.rsmod.content.other.agentbridge

/**
 * Represents an action command received from an LLM agent via WebSocket.
 *
 * Inbound JSON format:
 * ```
 * { "player": "TestBot", "type": "walk", "x": 3222, "z": 3219 }
 * { "player": "TestBot", "type": "teleport", "x": 3222, "z": 3219, "plane": 0 }
 * { "player": "TestBot", "type": "interact_loc", "id": 10820, "x": 3223, "z": 3219, "option": 1 }
 * { "player": "TestBot", "type": "interact_npc", "index": 7, "option": 1 }
 * { "player": "TestBot", "type": "spawn_item", "item_id": 1511, "count": 1 }
 * ```
 *
 * `player` is optional — if omitted, the command targets all logged-in players (broadcast).
 */
sealed class BotAction {
    /** Walk to (x, z) using pathfinding. */
    data class Walk(val x: Int, val z: Int) : BotAction()

    /** Teleport instantly to (x, z, plane). */
    data class Teleport(val x: Int, val z: Int, val plane: Int = 0) : BotAction()

    /** Interact with a game object (loc) at (x, z) using the given option (1-5). */
    data class InteractLoc(val id: Int, val x: Int, val z: Int, val option: Int = 1) : BotAction()

    /** Interact with an NPC at the given server index using the given option (1-5). */
    data class InteractNpc(val index: Int, val option: Int = 1) : BotAction()

    /** Spawn an item directly into the player's inventory. */
    data class SpawnItem(val itemId: Int, val count: Int = 1) : BotAction()
}
