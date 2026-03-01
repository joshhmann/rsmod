# Alter (RSMod v1) Repository Structure Analysis

## Overview

**Alter** is the RSMod v1 Kotlin codebase - the direct predecessor to RSMod v2. It uses a plugin-based architecture with string-based entity IDs and a `PluginRepository` pattern for registration.

**Location**: `Z:\Projects\OSRS-PS-DEV\Alter\`

---

## Repository Structure

```
Alter/
├── build.gradle.kts              # Root build configuration (Kotlin 2.0, Java 17)
├── settings.gradle.kts           # Module inclusion and plugin management
├── game-api/                     # Core API definitions
│   └── src/main/kotlin/org/alter/api/
│       ├── cfg/                  # Configuration constants (Animation, Sound, Varp, etc.)
│       ├── dsl/                  # DSL builders (NpcCombatDsl, LootTableDsl)
│       ├── ext/                  # Extension functions (PlayerExt, NpcExt, etc.)
│       └── *.kt                  # Core API classes (Skills, EquipmentType, etc.)
├── game-plugins/                 # Content plugins
│   └── src/main/kotlin/org/alter/plugins/content/
│       ├── skills/               # Skill implementations (thieving only)
│       ├── mechanics/            # Game mechanics (prayer, poison, trading, etc.)
│       ├── npcs/                 # NPC definitions and behaviors
│       ├── objects/              # Object interactions (bank booths, ladders, etc.)
│       ├── areas/                # Area-specific content (lumbridge NPCs)
│       ├── combat/               # Combat-related plugins
│       ├── items/                # Item-specific plugins
│       ├── magic/                # Magic and teleports
│       ├── interfaces/           # UI interface plugins
│       └── commands/             # Admin/player commands
├── game-server/                  # Core server implementation
│   └── src/main/kotlin/org/alter/game/
│       ├── model/                # Entity models (Player, Npc, World, etc.)
│       ├── plugin/               # Plugin system base classes
│       ├── service/              # Services (login, RSA, game, etc.)
│       ├── saving/               # Player persistence
│       └── task/                 # Game tick tasks
├── plugins/                      # Additional plugin modules
│   ├── tools/                    # Cache tools
│   ├── rscm/                     # RSCM string constants
│   └── filestore/                # Cache file handling
└── util/                         # Shared utilities
```

---

## Module Dependencies

```
util (base utilities)
  ↑
game-api (API definitions, depends on util)
  ↑
game-plugins (content, depends on game-api)
  ↑
game-server (engine, depends on game-plugins, game-api, util)
  ↑
plugins/* (additional modules)
```

**Key Dependencies** (from build.gradle.kts):
- Kotlin 2.0
- Netty (networking)
- Jackson (YAML/TOML/JSON parsing)
- Kotlinx Serialization
- FastUtil
- Logback (logging)

---

## Plugin Architecture

### Base Plugin Class

All plugins extend `KotlinPlugin` and receive dependencies via constructor:

```kotlin
class MyPlugin(
    r: PluginRepository,      // Plugin registration
    world: World,              // World instance
    server: Server            // Server instance
) : KotlinPlugin(r, world, server) {
    init {
        // Register handlers in init block
    }
}
```

### Plugin Registration Patterns

#### 1. NPC Interactions
```kotlin
onNpcOption(npc = "npc.cow", option = "talk-to") {
    player.queue { dialog(player) }
}

onNpcSpawn(npc = "npc.cow") {
    val npc = npc
    npc.timers[TIMER_KEY] = world.random(100..200)
}
```

#### 2. Object Interactions
```kotlin
onObjOption(obj = "object.bank_booth", option = "bank") {
    player.openBank()
}
```

#### 3. Item Interactions
```kotlin
onItemOption(item = "item.spade", option = "dig") {
    player.animate(Animation.DIG_WITH_SPADE)
}
```

#### 4. World Events
```kotlin
onWorldInit {
    // Initialize world-level data
}

onLogin {
    // Player login handling
}

onLogout {
    // Player logout handling
}

onTimer(TIMER_KEY) {
    // Timer tick handling
}

onButton(interfaceId = 541, component = 1) {
    // Interface button click
}
```

### String-Based IDs

Alter uses string-based entity references via RSCM (Runescape Content Mapping):

```kotlin
// NPCs
"npc.cow"
"npc.goblin"
"npc.banker"

// Objects
"object.bank_booth"
"object.tree"
"object.furnace"

// Items
"item.logs"
"item.bronze_axe"

// Convert to internal ID
import org.alter.rscm.RSCM.getRSCM
val itemId = getRSCM("item.logs")
```

---

## API Patterns

### Player Operations

```kotlin
// Messages
player.message("Hello world", ChatMessageType.GAME_MESSAGE)
player.filterableMessage("Spam message")

// Experience
player.addXp(Skills.WOODCUTTING, 25.0)

// Animations
player.animate(Animation.WOODCUTTING_BRONZE_AXE)

// Inventory
player.inventory.add(item = "item.logs", amount = 1)
player.inventory.remove(item = "item.bronze_axe", amount = 1)
player.inventory.hasItem("item.coins", amount = 100)

// Queue tasks
player.queue {
    task.wait(3)  // Wait 3 ticks
    player.message("Done!")
}

// Variables
player.setVarp(Varp.PLAYER_HAS_DISPLAY_NAME, 1)
player.getVarbit(Varbit.COMBAT_LEVEL_VARBIT)
player.setVarbit(Varbit.ESC_CLOSES_CURRENT_INTERFACE, 1)
```

### NPC Combat DSL

```kotlin
setCombatDef("npc.cow") {
    configs {
        attackSpeed = 6
        respawnDelay = 45
        poisonChance = 0.0
        venomChance = 0.0
    }
    stats {
        hitpoints = 8
        attack = 1
        strength = 1
        defence = 1
        magic = 1
        ranged = 1
    }
    bonuses {
        defenceStab = -21
        defenceSlash = -21
        defenceCrush = -21
    }
    anims {
        attack = Animation.COW_ATTACK
        block = Animation.COW_HIT
        death = Animation.COW_DEATH
    }
    sound {
        attackSound = Sound.COW_ATTACK
        blockSound = Sound.COW_HIT
        deathSound = Sound.COW_DEATH
    }
}
```

### Services Pattern

```kotlin
// Define service
class PickpocketService : Service {
    val entries = mutableListOf<PickpocketEntry>()
    
    override fun init() {
        // Load data
    }
}

// Register in plugin
init {
    loadService(PickpocketService())
}

// Access service
val service = world.getService(PickpocketService::class.java)
```

---

## Key Files Worth Porting

### Skill Implementations

| File | Description | Port Priority |
|------|-------------|---------------|
| `skills/thieving/pickpocket/PickpocketPlugin.kt` | Pickpocketing system | High |
| `skills/thieving/pickpocket/PickpocketData.kt` | Pickpocket data classes | High |
| `skills/thieving/pickpocket/PickpocketService.kt` | Service pattern example | Medium |
| `skills/thieving/stall/StallThievingPlugin.kt` | Stall thieving | Medium |
| `skills/thieving/chest/ChestThievingPlugin.kt` | Chest thieving | Low |

### Game Mechanics

| File | Description | Port Priority |
|------|-------------|---------------|
| `mechanics/poison/PoisonPluginPlugin.kt` | Poison system | High |
| `mechanics/poison/Poison.kt` | Poison logic | High |
| `mechanics/prayer/PrayersPlugin.kt` | Prayer activation | High |
| `mechanics/prayer/Prayers.kt` | Prayer drain logic | High |
| `mechanics/trading/TradingPlugin.kt` | Player trading | Medium |
| `mechanics/run/RunEnergyPlugin.kt` | Run energy | Medium |
| `mechanics/shops/ShopsPlugin.kt` | Shop system | Medium |
| `mechanics/equipment/EquipmentPlugin.kt` | Equipment handling | Medium |
| `mechanics/water/WaterPlugin.kt` | Water containers | Low |

### NPC Definitions

| File | Description | Port Priority |
|------|-------------|---------------|
| `npcs/CowPlugin.kt` | Simple NPC example | High |
| `npcs/banker/BankerPlugin.kt` | Banker interactions | High |
| `npcs/kbd/KbdCombatPlugin.kt` | Boss combat example | Medium |
| `npcs/barrows/*.kt` | Barrows brothers | Low |

### Object Interactions

| File | Description | Port Priority |
|------|-------------|---------------|
| `objects/bankbooth/BankBoothsPlugin.kt` | Bank booths | High |
| `objects/ladder/LadderPlugin.kt` | Ladders | Medium |
| `objects/door/DoorPlugin.kt` | Doors | Medium |
| `objects/gates/GatePlugin.kt` | Gates | Low |

### Area Content

| File | Description | Port Priority |
|------|-------------|---------------|
| `areas/lumbridge/npcs/CookPlugin.kt` | NPC dialogue example | High |
| `areas/lumbridge/npcs/BobPlugin.kt` | Shopkeeper example | Medium |

### API Extensions

| File | Description | Port Priority |
|------|-------------|---------------|
| `game-api/ext/PlayerExt.kt` | Player utilities | High |
| `game-api/ext/NpcExt.kt` | NPC utilities | Medium |
| `game-api/ext/QueueTaskExt.kt` | Dialogue/chat helpers | High |
| `game-api/dsl/NpcCombatDsl.kt` | Combat definition DSL | Medium |
| `game-api/cfg/Animation.kt` | Animation constants | High |
| `game-api/cfg/Sound.kt` | Sound constants | Medium |
| `game-api/cfg/Varp.kt` | Varp definitions | High |
| `game-api/cfg/Varbit.kt` | Varbit definitions | High |

---

## v1 → v2 Translation Notes

### Architecture Changes

| Aspect | Alter (v1) | RSMod v2 |
|--------|------------|----------|
| Plugin base | `KotlinPlugin(r, world, server)` | `PluginScript()` with `@Inject` |
| IDs | String-based (`"npc.cow"`) | Typed refs (`npcs.cow`) |
| Registration | `PluginRepository` | Guice injection |
| Handlers | Direct registration in `init` | DSL in `startup()` |
| Suspend | `player.queue { task.wait(3) }` | `delay(3)` in suspend context |

### Event Handler Mapping

| Alter (v1) | RSMod (v2) |
|------------|------------|
| `onNpcOption(npc, option)` | `onOpNpc1(npcs.name)`, `onOpNpc2(npcs.name)` |
| `onObjOption(obj, option)` | `onOpLoc1(locs.name)`, `onOpLoc2(locs.name)` |
| `onItemOption(item, option)` | `onOpObj1(objs.name)` |
| `onUseWith(obj, item)` | `onOpLocU(locs.name, objs.item)` |
| `onUseWith(inv, itemUsed, itemOn)` | `onOpHeldU(objs.a, objs.b)` |
| `onPlayerOption(option)` | `onPlayerOption1()` |

### Player Operations Translation

| Alter (v1) | RSMod (v2) |
|------------|------------|
| `player.message("text")` | `mes("text")` |
| `player.addXp(Skills.WOODCUTTING, xp)` | `statAdvance(stats.woodcutting, xp)` |
| `player.animate(Animation.CHOP)` | `anim(seqs.woodcutting_axe)` |
| `player.inventory.add(item, amount)` | `invAdd(invs.inv, objs.item, amount)` |
| `player.inventory.remove(item, amount)` | `invDel(invs.inv, objs.item, amount)` |
| `player.lock()` / `player.unlock()` | Handled by `ProtectedAccess` |
| `player.tile` | `player.coords` |

### NPC Operations Translation

| Alter (v1) | RSMod (v2) |
|------------|------------|
| `npc.forceChat("text")` | `npc.say("text")` |
| `spawnNpc("npc.cow", x, z)` | Data-driven YAML spawning |
| `setCombatDef("npc") { }` | Cache params-based |

### Dialogue Translation

**Alter v1:**
```kotlin
suspend fun QueueTask.dialog(player: Player) {
    chatPlayer(player, "Hello", animation = 568)
    val choice = options(player, "Yes", "No")
    when (choice) {
        1 -> chatNpc(player, "Great!")
        2 -> chatNpc(player, "Goodbye")
    }
}
```

**RSMod v2:**
```kotlin
private suspend fun ProtectedAccess.talkToNpc(npc: Npc) =
    startDialogue(npc) {
        chatNpc(happy, "Hello")
        val choice = choice2("Yes", "No")
        when (choice) {
            1 -> chatNpc(happy, "Great!")
            2 -> chatNpc(sad, "Goodbye")
        }
    }

onOpNpc1(npcs.cook) { talkToNpc(it.npc) }
```

### Combat Definition Translation

**Alter v1:**
```kotlin
setCombatDef("npc.cow") {
    configs { attackSpeed = 6; respawnDelay = 45 }
    stats { hitpoints = 8; attack = 1 }
    anims { attack = Animation.COW_ATTACK }
}
```

**RSMod v2:**
```toml
# In cache params or NPC config
params.attack_speed = 6
params.respawn_delay = 45
params.hitpoints = 8
params.attack = 1
params.attack_anim = <seq_id>
```

---

## Notable Implementation Details

### Timer System
```kotlin
// Define timer key
val COW_YELL_DELAY = TimerKey()

// Set timer
npc.timers[COW_YELL_DELAY] = world.random(100..200)

// Handle timer
onTimer(COW_YELL_DELAY) {
    npc.forceChat("Moo")
    npc.timers[COW_YELL_DELAY] = world.random(100..200)
}
```

### Random Number Generation
```kotlin
world.random(1..100)           // Int range
world.randomDouble()           // Double 0.0-1.0
```

### Inventory Transactions
```kotlin
val transaction = player.inventory.add(item = "item.logs", amount = 1)
if (transaction.hasFailed()) {
    // Handle failure
}

// Check space
if (!player.inventory.isFull) { }
```

### Ground Item Spawning
```kotlin
player.world.spawn(
    GroundItem(
        item = getRSCM("item.logs"),
        amount = amount,
        tile = player.tile,
        owner = player,
    )
)
```

---

## Files NOT Worth Porting

These are engine-level or already implemented in RSMod v2:

- `game-server/model/` - Entity models (Player, Npc, etc.)
- `game-server/plugin/` - Plugin system base classes
- `game-server/task/` - Game tick tasks
- `game-server/service/` - Core services (login, RSA, etc.)
- `game-server/saving/` - Persistence layer
- Network/protocol code (rsprot package)
- Cache handling (filestore module)

---

## Summary

**Alter's Strengths:**
- Clean DSL for NPC combat definitions
- String-based IDs are human-readable
- Service pattern for shared data
- Good extension function patterns

**v2 Improvements:**
- Type-safe entity references (no string lookups)
- Suspend functions (no queue wrapper)
- Guice DI (no manual repository)
- Param-driven configuration (data over code)
- Content groups for generic handlers

**Porting Strategy:**
1. Copy logic and algorithms from Alter plugins
2. Translate string IDs to typed refs using `.sym` files
3. Convert `player.queue { task.wait(n) }` to `delay(n)`
4. Replace `PluginRepository` registration with DSL handlers
5. Move combat definitions to cache params where applicable
