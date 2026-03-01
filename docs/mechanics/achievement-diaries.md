# Achievement Diaries System

## Overview

The Achievement Diaries system tracks player progress across various regions in OSRS. Each diary has Easy, Medium, Hard, and Elite tiers with specific tasks to complete.

## Architecture

### Core Components

1. **AchievementDiaryScript** - Core tracking and management
2. **DiaryTaskHooks** - Event subscriber for task completion
3. **DiaryEvents** - Event definitions for diary-related actions
4. **AchievementDiaryConfigs** - Varp references and region definitions

### File Structure

```
rsmod/content/mechanics/achievement-diaries/
├── configs/
│   └── AchievementDiaryConfigs.kt    # Varps, regions, tiers
├── events/
│   └── DiaryEvents.kt                 # Event definitions
├── hooks/
│   └── DiaryTaskHooks.kt              # Event subscribers
├── scripts/
│   └── AchievementDiaryScript.kt      # Core system
├── tasks/
│   ├── VarrockEasyTasks.kt            # Varrock Easy implementation
│   └── FaladorEasyTasks.kt            # Falador Easy implementation
└── triggers/
    └── VarrockEasyTriggers.kt         # Location helpers
```

## Event-Based Integration

The diary system uses events to decouple task tracking from skill implementations. Skill scripts publish events, and the diary system subscribes to them.

### Available Events

| Event | Description | Used For |
|-------|-------------|----------|
| `OreMinedEvent` | Player mined ore | Mining tasks |
| `TreeChoppedEvent` | Player chopped tree | Woodcutting tasks |
| `PotteryFiredEvent` | Player fired pottery | Crafting tasks |
| `RunesCraftedEvent` | Player crafted runes | Runecrafting tasks |
| `FishCaughtEvent` | Player caught fish | Fishing tasks |
| `StallThievedEvent` | Player stole from stall | Thieving tasks |
| `NpcKilledEvent` | Player killed NPC | Combat tasks |
| `AgilityShortcutCompletedEvent` | Player used shortcut | Agility tasks |
| `DungeonLevelEnteredEvent` | Player entered dungeon | Dungeon tasks |
| `ShopBrowsedEvent` | Player browsed shop | Shop tasks |
| `ItemPurchasedEvent` | Player bought item | Purchase tasks |
| `NpcTalkedToEvent` | Player talked to NPC | Dialogue tasks |
| `NpcTeleportEvent` | NPC teleported player | Teleport tasks |
| `ItemUsedOnNpcEvent` | Player used item on NPC | Item use tasks |

### Publishing Events from Skill Scripts

```kotlin
// Example: Mining script publishing event
class Mining @Inject constructor(
    private val eventBus: EventBus,  // Inject event bus
    // ... other dependencies
) : PluginScript() {
    
    private fun ProtectedAccess.mine(rock: BoundLocInfo, type: UnpackedLocType) {
        // ... mining logic ...
        
        if (minedOre) {
            val product = resolveOreProduct(type)
            // ... give ore and xp ...
            
            // Publish diary event
            val event = OreMinedEvent(
                player = player,
                oreType = objTypes[product],
                locX = rock.coords.x,
                locZ = rock.coords.z
            )
            eventBus.publish(event)
        }
    }
}
```

### Event Bus Access

The `EventBus` is available through `ScriptContext`:

```kotlin
// Access via ScriptContext
override fun ScriptContext.startup() {
    // eventBus is available here
    onEvent<OreMinedEvent> { 
        // Handle event
    }
}
```

## Varrock Easy Tasks (F2P)

| # | Task | Event | Location Check |
|---|------|-------|----------------|
| 1 | Browse Thessalia's store | `ShopBrowsedEvent` | npcName == "thessalia" |
| 2 | Aubury teleport to Essence mine | `NpcTeleportEvent` | npcName == "aubury" |
| 3 | Mine iron SE Varrock | `OreMinedEvent` | x: 3280-3295, z: 3360-3375 |
| 4 | Make plank at Sawmill | TODO | Sawmill location |
| 5 | Enter Stronghold of Security level 2 | `DungeonLevelEnteredEvent` | dungeon == "stronghold_of_security", level == 2 |
| 6 | Jump fence south of Varrock | `AgilityShortcutCompletedEvent` | x: 3235-3245, z: 3330-3340 |
| 7 | Chop dying tree in Lumber Yard | `TreeChoppedEvent` | x: 3295-3310, z: 3490-3510, tree == "dying_tree" |
| 8 | Buy a newspaper | `ItemPurchasedEvent` | item == "newspaper" |
| 9 | Give a dog a bone | `ItemUsedOnNpcEvent` | item == "bones", npc == "stray_dog" |
| 10 | Fire bowl in Barbarian Village | `PotteryFiredEvent` | x: 3070-3085, z: 3400-3415, product == "bowl" |
| 11 | Speak to Haig Halen with 50+ Kudos | `NpcTalkedToEvent` | npcName == "haig_halen", kudos >= 50 |
| 12 | Craft Earth runes | `RunesCraftedEvent` | runeType == "earth_rune" |
| 13 | Catch trout at Barbarian Village | `FishCaughtEvent` | x: 3100-3115, z: 3420-3435, fish == "trout" |
| 14 | Steal from Tea stall | `StallThievedEvent` | stallType == "tea_stall" |

## Falador Easy Tasks (F2P)

| # | Task | Event | Location Check |
|---|------|-------|----------------|
| 4 | Kill a duck in Falador Park | `NpcKilledEvent` | x: 2985-3025, z: 3375-3410, npc contains "duck" |

## Task Completion API

### Checking Task Completion

```kotlin
// Check if specific task is complete
val isComplete = player.isVarrockEasyTaskComplete(taskNumber)

// Check entire tier completion
val allEasyComplete = (0..13).all { player.isVarrockEasyTaskComplete(it) }

// Count completed tasks
val completedCount = (0..13).count { player.isVarrockEasyTaskComplete(it) }
```

### Completing Tasks Programmatically

```kotlin
// Complete a specific task
player.completeVarrockEasyTask(taskNumber)

// This will:
// 1. Check if already complete (no-op if so)
// 2. Set the appropriate bit in the varp
// 3. Send completion message to player
```

## Varp Structure

Each diary region uses two varps:
- **Varp 1**: Tasks 1-31 (bits 0-30)
- **Varp 2**: Tasks 32+ and completion status

Varrock Easy uses bits 0-13 of `varrock_achievement_diary`.

## Adding New Tasks

1. **Define the event** in `DiaryEvents.kt` if it doesn't exist
2. **Add event handler** in `DiaryTaskHooks.kt`
3. **Publish the event** from the appropriate skill script
4. **Update this documentation**

## Testing

### Manual Testing Commands

```kotlin
// Complete specific task (cheat/debug)
player.completeVarrockEasyTask(2)  // Complete Task 3 (mine iron)

// Check completion status
val status = player.isVarrockEasyTaskComplete(2)
```

### Unit Tests

See `AchievementDiaryScriptTest.kt` for examples.

## Future Enhancements

- [ ] Kudos system integration (Task 11)
- [ ] Sawmill integration (Task 4)
- [ ] Stronghold of Security level tracking (Task 5)
- [ ] Agility shortcut framework (Task 6)
- [ ] Medium/Hard/Elite tier implementations
- [ ] Reward claiming system
- [ ] Diary interface

