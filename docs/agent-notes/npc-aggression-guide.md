# NPC Aggression Implementation Guide (MECH-1)

**Task ID:** MECH-1
**Status:** In Progress (codex)
**Priority:** Wave 2 — Core Gameplay Mechanics

---

## OSRS Aggression Behavior Specification

### Aggression Radius
- NPCs with aggression will attack players within their **aggression radius**
- Default radius: 4-6 tiles for most NPCs
- Larger NPCs (giants, dragons): 8-16 tiles
- Configured via `huntRange` in NpcEditor

### De-Aggro Timer (10-Minute Rule)
- Players become "safe" after approximately **10 minutes** (1000 ticks) of being in the same map region
- After this period, aggressive NPCs will no longer auto-attack the player
- Timer resets when player leaves and re-enters the region

### Wilderness Exception
- NPCs in the Wilderness **never de-aggro** — the 10-minute timer does not apply
- Must check if player is in Wilderness area

### Combat Level Considerations
- For most NPCs: combat level does not affect aggression
- Exceptions: Some slayer monsters have special aggression rules (e.g., require certain slayer level or equipment)
- Players under combat level 2x NPC level in Wilderness: special aggression behavior (not applicable for F2P baseline)

---

## Current RSMod v2 Implementation Status

### Existing Infrastructure

1. **NpcEditor fields** (available now):
   ```kotlin
   edit(npcs.dark_wizard) {
       hitpoints = 20
       attack = 12
       strength = 14
       defence = 5
       attackRange = 1
       huntRange = 5           // aggression radius (exists but not wired)
       giveChase = true        // pursuit behavior (exists)
       // huntMode = huntModes.aggressive   // NOT YET IMPLEMENTED
   }
   ```

2. **Hunt API** (`api/hunt/`):
   - `NpcSearch.kt` — Provides `hunt()`, `huntAll()`, `find()`, `findAll()` methods
   - `HuntVis` enum — Visibility flags for hunt searches
   - Already used by AgentBridge for finding nearby NPCs

3. **NPC Mode Processors** (`api/game-process/npc/mode/`):
   - `NpcModeProcessor.kt` — Base processor
   - `AiPlayerModeProcessor.kt` — AI targeting players
   - `AiNpcModeProcessor.kt` — AI targeting NPCs
   - `AiLocModeProcessor.kt` — AI targeting locs
   - `AiObjModeProcessor.kt` — AI targeting objs

### What's Missing

1. **Aggression timer tracking** — No player attribute for "time entered current region"
2. **Aggression check loop** — No periodic timer on NPCs to scan for nearby players
3. **huntMode enum** — Not defined in NpcEditor
4. **Wilderness detection** — No area check for Wilderness exception

---

## Implementation Plan

### Phase 1: Player Region Entry Tracking

Add a player attribute to track when they entered their current region:

```kotlin
// In BaseVarps.kt or BaseVarbits.kt
val region_entry_tick = find("region_entry_tick")  // Store mapClock when player enters region

// Or use a player attribute:
// player.attr[regionEntryTick] = mapClock
```

Hook into region change detection:
```kotlin
// Listen for player movement across zone/region boundaries
onPlayerMove { event ->
    if (player.currentZone != player.previousZone) {
        player.attr[regionEntryTick] = mapClock
    }
}
```

### Phase 2: Aggression Timer on NPCs

Add a timer that fires periodically on aggressive NPCs:

```kotlin
// In BaseTimers.kt
val aggression_check = find("aggression_check")  // Fires every 10-20 ticks

// Register timer on NPC spawn for aggressive NPCs
onNpcSpawn(npcs.goblin, npcs.cow, ...) { event ->
    val npc = event.npc
    if (npc.type.huntRange > 0) {  // Has aggression radius
        npc.timer(timers.aggression_check, ticks = 10)
    }
}

// Handle timer
onNpcTimer(timers.aggression_check) { event ->
    val npc = event.npc
    checkAggression(npc)
    npc.timer(timers.aggression_check, ticks = 10)  // Reschedule
}
```

### Phase 3: Aggression Check Logic

```kotlin
private fun checkAggression(npc: Npc) {
    // Skip if already in combat
    if (npc.inCombat) return
    
    val huntRange = npc.type.huntRange
    if (huntRange <= 0) return
    
    // Find nearest player within huntRange
    val nearbyPlayers = hunt.findPlayers(npc.coords, huntRange, HuntVis.Normal)
    for (player in nearbyPlayers) {
        // Check de-aggro timer
        if (!isInWilderness(player)) {
            val entryTick = player.attr[regionEntryTick] ?: 0
            val ticksInRegion = mapClock - entryTick
            if (ticksInRegion > 1000) {  // ~10 minutes
                continue  // Player is safe
            }
        }
        
        // Attack this player
        npc.setAttackTarget(player)
        return
    }
}

private fun isInWilderness(player: Player): Boolean {
    // Check Wilderness area (x: 2944-3392, z: 3520-3968, level 0-3)
    val x = player.coords.x
    val z = player.coords.z
    return x in 2944..3392 && z in 3520..3968
}
```

### Phase 4: NpcEditor huntMode Integration

Add huntMode enum to NpcEditor:

```kotlin
enum class HuntMode {
    Passive,      // Never attacks player first
    Aggressive,   // Attacks player within huntRange (with 10-min de-aggro)
    AlwaysAggro,  // Always attacks (Wilderness behavior)
}

// In NpcEditor:
edit(npcs.goblin) {
    huntRange = 5
    huntMode = HuntMode.Aggressive
}

edit(npcs.greater_demon) {
    huntRange = 8
    huntMode = HuntMode.AlwaysAggro  // Wilderness demon
}
```

---

## Testing Checklist

### Unit Tests
- [ ] Player region entry tick is recorded on zone change
- [ ] Aggression timer fires on NPCs with huntRange > 0
- [ ] Player is not targeted after 10 minutes in same region
- [ ] Wilderness players are always targeted regardless of timer

### Integration Tests (Bot Scripts)
```typescript
// bots/aggression-test.ts

// Test 1: Basic aggression
await bot.teleport(3222, 3219, 0);  // Lumbridge
const goblin = sdk.findNearbyNpc(n => n.name?.includes("Goblin"));
// Wait and check if goblin attacks player

// Test 2: De-aggro after 10 minutes
// (Would need to mock time or fast-forward)

// Test 3: Wilderness no de-aggro
await bot.teleport(3100, 3550, 0);  // Edge of Wilderness
// Verify NPC continues attacking
```

### Manual Verification
- [ ] Goblins attack player when nearby
- [ ] After 10 min in Lumbridge, goblins stop attacking
- [ ] Re-entering area resets timer
- [ ] Wilderness NPCs always attack

---

## Files to Create/Modify

### New Files
1. `rsmod/content/mechanics/aggression/scripts/AggressionScript.kt` — Main aggression logic
2. `rsmod/content/mechanics/aggression/configs/AggressionTimers.kt` — Timer refs

### Modified Files
1. `rsmod/api/config/src/.../refs/BaseTimers.kt` — Add `aggression_check` timer
2. `rsmod/api/game-process/src/.../npc/mode/` — May need hunt mode processor updates
3. `rsmod/content/generic/generic-npcs/` — Add aggression config to F2P NPCs

---

## Related Tasks

- **AGENTBRIDGE-6** — Combat events for detecting when player enters combat from aggression
- **MECH-2** — Freeze/stun may need to pause aggression timer
- **WORLD-1 through WORLD-7** — Area population will configure aggression for local NPCs

---

## Reference Code

### Similar System: Poison Mechanics
See `rsmod/content/mechanics/poison/scripts/PoisonScript.kt` for:
- Timer-based periodic checks
- Player attribute tracking
- State persistence

### Hunt API Usage
See `rsmod/content/other/agent-bridge/src/.../AgentBridgeServer.kt` for:
- `hunt.findNpcs()` usage
- `HuntVis` enum values
- Distance calculations

---

## Notes

- Aggression should not trigger during dialogue
- Aggression should not trigger if NPC is already in combat
- Consider NPC "idle" animations when roaming for aggression
- Some NPCs have "patrol" routes that affect aggression timing

