# Combat Events Implementation Guide (AGENTBRIDGE-6)

**Task ID:** AGENTBRIDGE-6
**Status:** In Progress (codex)
**Priority:** Wave 2 — Core Gameplay Mechanics
**Blocks:** QUEST-9-IMPL, QUEST-10-IMPL (boss combat)

---

## Goal

Port the combat system to expose events that AgentBridge can subscribe to, enabling:
1. Boss scripts to react to combat state changes
2. Quest scripts to detect combat completion
3. LLM agents to make decisions based on combat events
4. Testing infrastructure to verify combat behavior

---

## Current Combat Architecture

### Combat Scripts
- `api/combat/combat-scripts/src/.../PvPCombatScript.kt` — Player vs Player
- `api/combat/combat-scripts/src/.../PvNCombat.kt` — Player vs NPC
- `api/combat/combat-scripts/src/.../NvPCombat.kt` — NPC vs Player

### Combat APIs
- `api/combat/combat-accuracy/` — Hit chance calculations
- `api/combat/combat-maxhit/` — Max hit calculations
- `api/combat/combat-manager/` — Combat state management
- `api/combat/combat-commons/` — Shared utilities

### Current State Broadcasting
AgentBridge already broadcasts `nearbyNpcs` which includes:
- NPC index, id, name
- Position (x, z, level)
- Current animation
- Current HP (if available)

### What's Missing

1. **Combat start event** — When player attacks or is attacked
2. **Hit landed event** — When damage is dealt
3. **Hit taken event** — When player takes damage
4. **Kill event** — When NPC or player dies
5. **Combat end event** — When combat concludes
6. **Special attack event** — When special attack is used

---

## Event Types to Define

```kotlin
// CombatEvents.kt

sealed class CombatEvent {
    abstract val timestamp: Long
    abstract val source: String  // "player" or npc name
}

data class CombatStartEvent(
    override val timestamp: Long,
    override val source: String,
    val target: String,
    val targetId: Int,
) : CombatEvent()

data class HitLandedEvent(
    override val timestamp: Long,
    override val source: String,
    val target: String,
    val targetId: Int,
    val damage: Int,
    val hitType: HitType,  // NORMAL, MAX_HIT, SPLASH, etc.
) : CombatEvent()

data class HitTakenEvent(
    override val timestamp: Long,
    val attacker: String,
    val attackerId: Int,
    override val source: String = "player",
    val damage: Int,
    val currentHp: Int,
    val maxHp: Int,
) : CombatEvent()

data class KillEvent(
    override val timestamp: Long,
    override val source: String,
    val victim: String,
    val victimId: Int,
    val victimCombatLevel: Int,
) : CombatEvent()

data class CombatEndEvent(
    override val timestamp: Long,
    override val source: String,
    val duration: Long,  // milliseconds
    val totalDamageDealt: Int,
    val totalDamageTaken: Int,
) : CombatEvent()

data class SpecialAttackEvent(
    override val timestamp: Long,
    override val source: String,
    val weapon: String,
    val target: String,
    val targetId: Int,
) : CombatEvent()

enum class HitType {
    NORMAL,
    MAX_HIT,
    SPLASH,      // Magic miss
    BLOCKED,     // 0 damage blocked
    CRITICAL,    // If applicable
}
```

---

## Implementation Plan

### Phase 1: EventBus Integration

The RSMod engine already has an EventBus. Combat scripts need to publish events:

```kotlin
// In PvNCombat.kt

class PvNCombat @Inject constructor(
    private val eventBus: EventBus,
    // ... other dependencies
) {
    fun attack(player: Player, npc: Npc) {
        // Publish combat start
        eventBus.publish(
            CombatStartEvent(
                timestamp = System.currentTimeMillis(),
                source = player.name,
                target = npc.type.name,
                targetId = npc.id,
            )
        )
        
        // ... existing attack logic
        
        if (hitLanded) {
            // Publish hit event
            eventBus.publish(
                HitLandedEvent(
                    timestamp = System.currentTimeMillis(),
                    source = player.name,
                    target = npc.type.name,
                    targetId = npc.id,
                    damage = damage,
                    hitType = hitType,
                )
            )
        }
    }
}
```

### Phase 2: AgentBridge Subscription

```kotlin
// In AgentBridgeScript.kt

class AgentBridgeScript @Inject constructor(
    private val eventBus: EventBus,
) : PluginScript() {
    override fun ScriptContext.startup() {
        // Subscribe to combat events
        eventBus.subscribe<CombatEvent> { event ->
            when (event) {
                is CombatStartEvent -> broadcastCombatStart(event)
                is HitLandedEvent -> broadcastHitLanded(event)
                is KillEvent -> broadcastKill(event)
                // ... etc
            }
        }
    }
    
    private fun broadcastKill(event: KillEvent) {
        val message = JSONObject().apply {
            put("type", "combat_kill")
            put("timestamp", event.timestamp)
            put("victim", event.victim)
            put("victimId", event.victimId)
        }
        broadcastToAll(message.toString())
    }
}
```

### Phase 3: Quest Script Integration

```kotlin
// Example: Vampyre Slayer quest boss

class CountDraynorCombat @Inject constructor(
    private val eventBus: EventBus,
) : PluginScript() {
    override fun ScriptContext.startup() {
        // Listen for Count Draynor kill
        eventBus.subscribe<KillEvent> { event ->
            if (event.victimId == npcs.count_draynor.id) {
                // Player killed Count Draynor
                handleQuestCompletion(event.source)
            }
        }
    }
}
```

### Phase 4: Bot Script Support

```typescript
// bots/combat-test.ts

// Subscribe to combat events via AgentBridge
const killPromise = bot.waitForKillEvent("Goblin", 30000);

await actions.attackNpc(goblinIndex);
const killEvent = await killPromise;

console.log(`Killed ${killEvent.victim} after ${killEvent.duration}ms`);
console.log(`Total damage dealt: ${killEvent.totalDamageDealt}`);
```

---

## AgentBridge Message Format

### Combat Start
```json
{
  "type": "combat_start",
  "timestamp": 1708612345678,
  "player": "TestBot",
  "target": "Goblin",
  "targetId": 12345
}
```

### Hit Landed
```json
{
  "type": "combat_hit",
  "timestamp": 1708612345878,
  "player": "TestBot",
  "target": "Goblin",
  "targetId": 12345,
  "damage": 5,
  "hitType": "NORMAL"
}
```

### Kill
```json
{
  "type": "combat_kill",
  "timestamp": 1708612349234,
  "player": "TestBot",
  "victim": "Goblin",
  "victimId": 12345,
  "victimCombatLevel": 2
}
```

---

## Files to Create/Modify

### New Files
1. `rsmod/api/combat/combat-events/src/.../CombatEvent.kt` — Event definitions
2. `rsmod/api/combat/combat-events/src/.../CombatEventPublisher.kt` — Publishing helper
3. `rsmod/content/other/agent-bridge/src/.../CombatEventBroadcaster.kt` — AgentBridge integration

### Modified Files
1. `rsmod/api/combat/combat-scripts/src/.../PvNCombat.kt` — Publish events
2. `rsmod/api/combat/combat-scripts/src/.../NvPCombat.kt` — Publish events
3. `rsmod/content/other/agent-bridge/src/.../AgentBridgeScript.kt` — Subscribe to events

---

## Testing Checklist

### Unit Tests
- [ ] CombatStartEvent fires on attack initiation
- [ ] HitLandedEvent fires with correct damage
- [ ] HitTakenEvent fires when player is hit
- [ ] KillEvent fires on NPC death
- [ ] CombatEndEvent fires when combat concludes

### Integration Tests
```typescript
// Test combat event flow
const events = [];

bot.onCombatEvent((event) => {
    events.push(event);
});

await actions.attackNpc(goblinIndex);
await bot.delay(100); // Wait for combat to start

expect(events.find(e => e.type === "combat_start")).toBeDefined();
expect(events.find(e => e.type === "combat_hit")).toBeDefined();

await bot.waitForKillEvent("Goblin", 30000);
expect(events.find(e => e.type === "combat_kill")).toBeDefined();
```

---

## Boss Combat Integration (for QUEST-9-IMPL, QUEST-10-IMPL)

### Count Draynor (Vampyre Slayer)
```kotlin
// Special: Only killable with stake
eventBus.subscribe<KillEvent> { event ->
    if (event.victimId == npcs.count_draynor.id) {
        if (!player.hasItem(objs.stake)) {
            // Count Draynor doesn't die, just respawns
            return@subscribe
        }
        completeQuest(player, QuestList.vampyre_slayer)
    }
}
```

### Elvarg (Dragon Slayer)
```kotlin
// Special: Requires anti-dragon shield
eventBus.subscribe<HitTakenEvent> { event ->
    if (event.attackerId == npcs.elvarg.id) {
        if (!player.hasEquipped(objs.anti_dragon_shield)) {
            // Massive damage from dragonfire
            player.dealDamage(50) // Very high damage
        }
    }
}
```

---

## Notes

- Events should be lightweight (no large object allocations)
- Consider rate-limiting hit events for high-speed combat
- Add option to disable event broadcasting for performance
- Cache frequently-accessed NPC names/types
- Thread safety: events fire on game thread, AgentBridge broadcasts on its own thread

