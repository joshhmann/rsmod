# Combat Event System Research — AGENTBRIDGE-6

**Task**: Port Combat System with Events for AgentBridge integration  
**Research Date**: 2026-02-22  
**Status**: Research Complete — Ready for Planning

---

## Executive Summary

This research documents the current combat event architecture in RSMod v2 and identifies the gaps that need to be filled for AgentBridge integration. The key finding is that **NPC hit events exist but Player hit events do not**, which is the primary gap to address.

---

## 1. Current Combat Event Architecture

### 1.1 Event Bus System

**Location**: `rsmod/engine/events/src/main/kotlin/org/rsmod/events/`

The EventBus supports 3 event dispatch patterns:

| Event Type | Purpose | Use Case |
|------------|---------|----------|
| `UnboundEvent` | Broadcast to all subscribers | Global events |
| `KeyedEvent` | Targeted to specific entity via `id: Long` | Per-NPC/Player events |
| `SuspendEvent` | Suspend-aware events for async handlers | Coroutine-compatible events |

**EventBus API**:
```kotlin
// Publish event
eventBus.publish(event)

// Subscribe to keyed events (entity-specific)
eventBus.subscribeKeyed(NpcHitEvents.Modify::class.java, npcId) { event ->
    // Handle event
}
```

### 1.2 NPC Combat Events (EXISTING)

**Location**: `rsmod/api/npc/src/main/kotlin/org/rsmod/api/npc/events/NpcCombatEvents.kt`

```kotlin
object NpcHitEvents {
    /** Pre-hit, mutable - allows damage modification */
    data class Modify(val npc: Npc, val hit: HitBuilder) : KeyedEvent
    
    /** Post-hit, immutable - fired after damage applied */
    data class Impact(val npc: Npc, val hit: Hit) : KeyedEvent
}
```

**Event Firing Points**:

| Event | Firing Location | Purpose |
|-------|-----------------|---------|
| `NpcHitEvents.Modify` | `StandardNpcHitModifier.kt:19-22` | Pre-hit, can modify damage |
| `NpcHitEvents.Impact` | `StandardNpcHitProcessor.kt:90-93` | Post-hit, damage applied |

### 1.3 Combat Flow Architecture

```
PvNCombat.attack()
    |
    +-> PlayerAttackManager.rollMeleeDamage()
    |       |
    |       +-> AccuracyFormulae.rollMeleeAccuracy() -> Boolean
    |       +-> MaxHitFormulae.getMeleeMaxHit() -> Int
    |       +-> Random damage = random(0..maxHit)
    |
    +-> PlayerAttackManager.queueMeleeHit()
            |
            +-> StandardNpcHitModifier (NpcHitEvents.Modify fired)
            +-> StandardNpcHitProcessor
                    |
                    +-> NpcHitEvents.Impact fired (damage applied)
                    +-> NPC death check
```

### 1.4 Combat Formula APIs

**AccuracyFormulae** (`rsmod/api/combat/combat-formulas/.../AccuracyFormulae.kt`):
- `rollMeleeAccuracy(player, target, ...)` → Boolean (hit/miss)
- `getMeleeHitChance(player, target, ...)` → Int (0-10000, where 10000=100%)
- Supports: PvP, PvN, NvP, NvN across melee/ranged/magic

**MaxHitFormulae** (`rsmod/api/combat/combat-formulas/.../MaxHitFormulae.kt`):
- `getMeleeMaxHit(player, target, ...)` → Int
- `getRangedMaxHit(...)` → Int
- `getSpellMaxHitRange(...)` → IntRange

### 1.5 Hit Data Structure

**Location**: `rsmod/engine/game/src/main/kotlin/org/rsmod/game/hit/Hit.kt`

```kotlin
data class Hit(
    val type: HitType,           // Melee, Ranged, Magic
    val hitmark: Hitmark,        // Visual metadata
    val sourceUid: Int?,         // Source entity UID
    val righthandObj: Int?,      // Weapon used
    val secondaryObj: Int?       // Ammo (for ranged)
) {
    val damage: Int
    val isFromNpc: Boolean
    val isFromPlayer: Boolean
}
```

---

## 2. AgentBridge Event Architecture

### 2.1 WebSocket Server

**Location**: `rsmod/content/other/agent-bridge/src/main/kotlin/org/rsmod/content/other/agentbridge/AgentBridgeServer.kt`

- **Port**: 43595
- **Protocol**: WebSocket JSON messages
- **Broadcast**: Per-tick state snapshots to all connected clients

### 2.2 Current Event Types

**Location**: `rsmod/content/other/agent-bridge/src/main/kotlin/org/rsmod/content/other/agentbridge/StateSnapshot.kt`

```kotlin
sealed class PlayerEvent {
    abstract val tick: Int

    // Animation events
    data class AnimationStart(override val tick: Int, val animationId: Int) : PlayerEvent()
    data class AnimationEnd(override val tick: Int, val animationId: Int) : PlayerEvent()

    // XP events
    data class XpGain(override val tick: Int, val skill: String, val amount: Int, val newTotal: Int) : PlayerEvent()

    // Inventory events
    data class ItemAdded(override val tick: Int, val itemId: Int, val name: String, val qty: Int, val slot: Int) : PlayerEvent()
    data class ItemRemoved(override val tick: Int, val itemId: Int, val name: String, val qty: Int, val slot: Int) : PlayerEvent()

    // Movement events
    data class PositionChanged(override val tick: Int, val x: Int, val z: Int, val plane: Int) : PlayerEvent()

    // Door events
    data class DoorOpened(override val tick: Int, val x: Int, val z: Int, val plane: Int, val name: String) : PlayerEvent()
    data class DoorLocked(override val tick: Int, val x: Int, val z: Int, val plane: Int, val name: String, val reason: String) : PlayerEvent()

    // Bank events
    data class BankOpened(override val tick: Int, val itemCount: Int) : PlayerEvent()
    data class BankClosed(override val tick: Int) : PlayerEvent()
    data class ItemDeposited(override val tick: Int, val slot: Int, val id: Int, val name: String, val count: Int) : PlayerEvent()
    data class ItemWithdrawn(override val tick: Int, val slot: Int, val id: Int, val name: String, val count: Int) : PlayerEvent()

    // Shop events
    data class ShopOpened(override val tick: Int, val shopTitle: String, val itemCount: Int) : PlayerEvent()
    data class ShopClosed(override val tick: Int) : PlayerEvent()
    data class ItemBought(override val tick: Int, val itemId: Int, val name: String, val count: Int, val totalCost: Int) : PlayerEvent()
    data class ItemSold(override val tick: Int, val itemId: Int, val name: String, val count: Int, val totalValue: Int) : PlayerEvent()
    data class ShopRestocked(override val tick: Int, val shopTitle: String) : PlayerEvent()

    // Ground item events (sealed subclass)
    sealed class GroundItemEvent : PlayerEvent() { ... }

    // Prayer events (sealed subclass)
    sealed class PrayerEvent : PlayerEvent() { ... }
}
```

### 2.3 Event Detection Method

**Location**: `rsmod/content/other/agent-bridge/src/main/kotlin/org/rsmod/content/other/agentbridge/AgentBridgeScript.kt:499-582`

Events are detected via **state diffing** — comparing previous tick state to current tick state:

```kotlin
private fun detectEvents(
    prev: PlayerStateTracker,
    current: PlayerStateTracker,
    tick: Int,
): List<PlayerEvent> {
    val events = mutableListOf<PlayerEvent>()
    
    // Animation events (state diff)
    if (prev.animation != current.animation) { ... }
    
    // Position events (state diff)
    if (prev.x != current.x || prev.z != current.z) { ... }
    
    // XP events (state diff)
    current.skills.forEach { (skill, xp) -> ... }
    
    // Inventory events (state diff)
    current.inventory.forEach { (slot, item) -> ... }
    
    return events
}
```

### 2.4 State Broadcast

```kotlin
fun broadcast(
    player: Player,
    nearbyNpcs: List<NearbyNpcSnapshot>,
    nearbyLocs: List<NearbyLocSnapshot>,
    events: List<PlayerEvent> = emptyList(),
    actionResult: ActionResult? = null,
    waitResult: WaitResult? = null,
) {
    // JSON payload with events array
    val payload = mapOf(
        "type" to "state",
        "tick" to clock.cycle,
        "player" to snapshot.player,
        "events" to events,  // <-- Events included here
        ...
    )
    
    // Broadcast to all connected WebSocket clients
    clients.filter { it.isOpen }.forEach { it.send(json) }
}
```

---

## 3. Gap Analysis: What's Missing for Combat Events

### 3.1 CRITICAL GAP: No Player Hit Events

**Problem**: When a player takes damage from an NPC attack, there is NO event fired.

**Current State**:
- `NpcHitEvents.Modify` and `NpcHitEvents.Impact` exist for NPCs
- No equivalent `PlayerHitEvents` class exists
- No event firing in player hit processing code

**Impact**: AgentBridge cannot:
- Detect when player takes damage
- Track combat health changes
- Report kill/death events
- Enable combat-aware bot testing

### 3.2 Missing Event Types

| Event | Required For | Priority |
|-------|--------------|----------|
| `CombatStart` | Detect when player enters combat | Medium |
| `HitDealt` | Track damage player deals to NPC | High |
| `HitTaken` | Track damage player receives | **CRITICAL** |
| `Kill` | Detect when player kills NPC | High |
| `Death` | Detect when player dies | High |
| `CombatEnd` | Detect when combat concludes | Low |

### 3.3 Missing State Tracking

Current `PlayerStateTracker` does NOT track:
- Current HP (hitpoints)
- Current target (NPC being attacked)
- Combat state (in combat / out of combat)
- Recent damage dealt/received

---

## 4. Implementation Recommendations

### 4.1 Create PlayerHitEvents Class

**New File**: `rsmod/api/player/src/main/kotlin/org/rsmod/api/player/events/PlayerCombatEvents.kt`

```kotlin
object PlayerHitEvents {
    /** Pre-hit on player, mutable - allows damage reduction */
    data class Modify(val player: Player, val hit: HitBuilder, val source: Npc?) : KeyedEvent
    
    /** Post-hit on player, immutable - fired after damage applied */
    data class Impact(val player: Player, val hit: Hit, val source: Npc?) : KeyedEvent
}

object PlayerCombatEvents {
    /** Player entered combat with an NPC */
    data class CombatStart(val player: Player, val target: Npc) : KeyedEvent
    
    /** Player killed an NPC */
    data class Kill(val player: Player, val npc: Npc, val npcType: Int) : KeyedEvent
    
    /** Player died */
    data class Death(val player: Player, val killer: Npc?) : KeyedEvent
    
    /** Player left combat */
    data class CombatEnd(val player: Player) : KeyedEvent
}
```

### 4.2 Fire Events in Combat Code

**Locations to modify**:

1. **NvPCombat** (`rsmod/api/combat/combat-scripts/.../NvPCombat.kt`):
   - Fire `PlayerHitEvents.Modify` before damage
   - Fire `PlayerHitEvents.Impact` after damage

2. **PlayerAttackManager** (`rsmod/api/combat/combat-manager/.../PlayerAttackManager.kt`):
   - Fire `PlayerCombatEvents.CombatStart` when attack begins
   - Fire `PlayerCombatEvents.Kill` when NPC dies
   - Fire `PlayerCombatEvents.CombatEnd` when combat ends

3. **Player death handler**:
   - Fire `PlayerCombatEvents.Death` when player dies

### 4.3 Add Combat Events to PlayerEvent

**Modify**: `rsmod/content/other/agent-bridge/src/main/kotlin/org/rsmod/content/other/agentbridge/StateSnapshot.kt`

```kotlin
sealed class PlayerEvent {
    // ... existing events ...
    
    // Combat events (NEW)
    sealed class CombatEvent : PlayerEvent() {
        /** Player entered combat */
        data class CombatStarted(
            override val tick: Int,
            val targetNpcId: Int,
            val targetName: String,
            val targetIndex: Int,
        ) : CombatEvent()
        
        /** Player dealt damage to NPC */
        data class HitDealt(
            override val tick: Int,
            val targetNpcId: Int,
            val targetName: String,
            val damage: Int,
            val hitType: String, // "melee", "ranged", "magic"
            val isKill: Boolean,
        ) : CombatEvent()
        
        /** Player took damage from NPC */
        data class HitTaken(
            override val tick: Int,
            val sourceNpcId: Int,
            val sourceName: String,
            val damage: Int,
            val currentHp: Int,
            val maxHp: Int,
        ) : CombatEvent()
        
        /** Player killed an NPC */
        data class NpcKilled(
            override val tick: Int,
            val npcId: Int,
            val npcName: String,
            val combatXp: Int,
        ) : CombatEvent()
        
        /** Player died */
        data class PlayerDied(
            override val tick: Int,
            val killerNpcId: Int?,
            val killerName: String?,
        ) : CombatEvent()
        
        /** Combat ended */
        data class CombatEnded(
            override val tick: Int,
            val reason: String, // "kill", "escape", "timeout"
        ) : CombatEvent()
    }
}
```

### 4.4 Extend PlayerStateTracker

**Modify**: `rsmod/content/other/agent-bridge/src/main/kotlin/org/rsmod/content/other/agentbridge/AgentBridgeScript.kt`

```kotlin
private data class PlayerStateTracker(
    // ... existing fields ...
    val currentHp: Int,           // NEW
    val maxHp: Int,               // NEW
    val inCombat: Boolean,        // NEW
    val targetNpcIndex: Int?,     // NEW (index of NPC being attacked)
    val lastHitDealtTick: Int,    // NEW (for combat end detection)
    val lastHitTakenTick: Int,    // NEW (for combat end detection)
)
```

### 4.5 Subscribe to Combat Events in AgentBridgeScript

```kotlin
// In AgentBridgeScript.startup()
eventBus.subscribeKeyed(PlayerHitEvents.Impact::class.java, playerUid) { event ->
    // Queue combat event for broadcast
    pendingCombatEvents.add(PlayerEvent.CombatEvent.HitTaken(...))
}

eventBus.subscribeKeyed(PlayerCombatEvents.Kill::class.java, playerUid) { event ->
    pendingCombatEvents.add(PlayerEvent.CombatEvent.NpcKilled(...))
}
```

---

## 5. Implementation Approach

### Phase 1: Core Combat Events (Required for MVP)

1. Create `PlayerHitEvents` class in `api/player/`
2. Fire `PlayerHitEvents.Impact` in NvPCombat
3. Add `CombatEvent.HitTaken` to PlayerEvent
4. Update AgentBridgeScript to subscribe and broadcast

### Phase 2: Extended Combat Events

1. Create `PlayerCombatEvents` class
2. Fire combat start/kill/death events
3. Add remaining CombatEvent types to PlayerEvent
4. Update state tracking for combat state

### Phase 3: Bot Testing Support

1. Add combat-specific BotAction types (attack_npc, wait_for_kill, etc.)
2. Add combat wait conditions (wait_for_kill, wait_for_no_combat)
3. Create combat test scripts in `bots/`

---

## 6. Files to Modify

| File | Change |
|------|--------|
| `rsmod/api/player/src/main/kotlin/org/rsmod/api/player/events/PlayerCombatEvents.kt` | **NEW** — Create event classes |
| `rsmod/api/combat/combat-scripts/src/main/kotlin/org/rsmod/api/combat/NvPCombat.kt` | Fire PlayerHitEvents |
| `rsmod/api/combat/combat-manager/src/main/kotlin/org/rsmod/api/combat/manager/PlayerAttackManager.kt` | Fire PlayerCombatEvents |
| `rsmod/content/other/agent-bridge/src/main/kotlin/org/rsmod/content/other/agentbridge/StateSnapshot.kt` | Add CombatEvent sealed class |
| `rsmod/content/other/agent-bridge/src/main/kotlin/org/rsmod/content/other/agentbridge/AgentBridgeScript.kt` | Subscribe to combat events, update state tracking |
| `rsmod/content/other/agent-bridge/src/main/kotlin/org/rsmod/content/other/agentbridge/BotAction.kt` | Add combat actions |

---

## 7. Reference Files

### Combat System
- `rsmod/api/npc/src/main/kotlin/org/rsmod/api/npc/events/NpcCombatEvents.kt` — NPC hit events (template)
- `rsmod/api/combat/combat-scripts/src/main/kotlin/org/rsmod/api/combat/PvNCombat.kt` — Player vs NPC combat
- `rsmod/api/combat/combat-scripts/src/main/kotlin/org/rsmod/api/combat/NvPCombat.kt` — NPC vs Player combat
- `rsmod/api/combat/combat-formulas/src/main/kotlin/org/rsmod/api/combat/formulas/AccuracyFormulae.kt` — Accuracy calculations
- `rsmod/api/combat/combat-formulas/src/main/kotlin/org/rsmod/api/combat/formulas/MaxHitFormulae.kt` — Max hit calculations

### AgentBridge
- `rsmod/content/other/agent-bridge/src/main/kotlin/org/rsmod/content/other/agentbridge/AgentBridgeServer.kt` — WebSocket server
- `rsmod/content/other/agent-bridge/src/main/kotlin/org/rsmod/content/other/agentbridge/AgentBridgeScript.kt` — Event handling
- `rsmod/content/other/agent-bridge/src/main/kotlin/org/rsmod/content/other/agentbridge/StateSnapshot.kt` — Event types

### Event System
- `rsmod/engine/events/src/main/kotlin/org/rsmod/events/EventBus.kt` — Core event bus
- `rsmod/engine/events/src/main/kotlin/org/rsmod/events/Event.kt` — Event interfaces

---

## 8. Notes

- Alter v1 combat references exist in `_references_archive/osrs-ub3r-monorepo/plugins/content/combat/` but are minimal (only special attack constants)
- No Kronos combat JSON files found in workspace
- RSMod v2 already has solid event infrastructure — just needs to be extended to players
- State diffing approach in AgentBridge works well for passive events, but combat events should be event-driven (subscribe/publish) for accuracy

---

**Research Complete** — Ready to proceed with planning phase.
