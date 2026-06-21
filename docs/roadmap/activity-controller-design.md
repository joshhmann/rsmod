# Activity Controller Design

## Purpose

A reusable area/activity controller framework that minigames, bosses, and special zones can plug into without reinventing state management, participant tracking, and tick loops.

## Current State (June 2026)

RSMod has NO generic activity controller. Each zone with special behavior is independently implemented:

- **Woodcutting** uses a `ControllerRepository` + `onAiConTimer` for tree respawn logic
- **Agility courses** are hand-wired per-course scripts
- **Aggressive NPC combat** uses a shared `NpcAggressionScript` for aggression radius
- **Trade** has its own `TradeSession` state machine

No shared abstraction exists for: area membership, round-based logic, contribution tracking, reward pools, or safe-death handling.

## Requirements

A generic activity controller should support:

| Feature | Description | Needed By |
|---------|-------------|-----------|
| Area membership | Who's in the activity, entry/exit rules | All minigames |
| State machine | Phase transitions (waiting → active → reward → reset) | Wintertodt, Pest Control |
| Tick loop | Run every N game ticks for periodic updates | All minigames |
| Participant tracking | Score, contribution, damage dealt | Bosses, Wintertodt |
| Safe death | No item loss, respawn inside activity | Bosses, minigames |
| Object state management | Reset objects when activity resets | Motherlode Mine veins |
| NPC state management | Spawn/despawn NPCs per phase | Pest Control |
| Interface overlays | Score/health/timer widgets | Wintertodt, Tempoross |
| Reward payout | Distribute points/items on completion | All minigames |
| World messages | Broadcast phase changes | All minigames |

## Proposed Design

```kotlin
abstract class Activity(
    val area: AreaDefinition,
    val maxParticipants: Int
) {
    // State
    var phase: ActivityPhase = ActivityPhase.WAITING
    val participants: MutableSet<String> = mutableSetOf()
    val contribution: MutableMap<String, Int> = mutableMapOf()
    var tickCount: Int = 0
    
    // Lifecycle
    abstract fun onEnter(player: Player): Boolean  // return false to reject
    abstract fun onLeave(player: Player)
    abstract fun onTick()
    abstract fun onPhaseChange(from: ActivityPhase, to: ActivityPhase)
    abstract fun onComplete()
    
    // Rewards
    open fun calculateReward(player: Player): List<ItemDrop> = emptyList()
}

enum class ActivityPhase {
    WAITING,
    ACTIVE,
    PAUSED,
    COMPLETING,
    REWARD,
    RESETTING
}
```

## Implementation Strategy

### Phase 1: Core Controller (M5 milestone)
- Build the base `Activity` class with phase management
- Add participant tracking (area membership via square/radius)
- Implement basic tick loop (`onPlayerQueue` or `onTimer`)
- Add world message helper

### Phase 2: Object/NPC State Management
- Snapshot/restore object states on activity reset
- NPC spawn/despawn per phase
- Depleted resource reset (Motherlode Mine veins)

### Phase 3: Reward & Scoring
- Contribution tracking
- Reward table integration with existing drop framework
- Point-based reward shops

### Phase 4: Interface Integration
- Overlay widgets for phase timer, score, health
- CS2 script calls for client widgets

## Integration Points

| System | How It Connects |
|--------|----------------|
| Drop framework | Reward payout uses same `dropTable` or `NpcDropTableRegistry` |
| Resource node framework | Activity can define/deplete/respawn resource nodes |
| Skill action framework | Activity actions can use skill checks |
| Shop framework | Point-based rewards link to shop inventory |

## References

- OSRS Wiki: Minigame mechanics and phase descriptions
- RSMod existing: `TradeSession.kt`, `ControllerRepository`, `AggressionScript.kt`
- Activity area: Use polygon or square boundary definitions from `BoundLocInfo`
