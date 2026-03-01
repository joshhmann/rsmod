# OpenRune vs RSMod v2 - Architectural Comparison

## Executive Summary

Both are Kotlin-based OSRS servers, but they have fundamentally different design philosophies:

| Aspect | OpenRune | RSMod v2 |
|--------|----------|----------|
| **Philosophy** | Content-first, area-based | System-first, type-safe |
| **ID System** | Runtime string resolution (RSCM) | Compile-time type references |
| **Event System** | Hash map storage | EventBus with sealed classes |
| **Async** | Manual queue tasks | Suspend functions |
| **DI** | Manual | Guice |
| **Plugin System** | Dual (legacy + new) | Unified |

---

## 1. Plugin Architecture

### OpenRune: Dual Plugin System

OpenRune has **two different plugin systems** running simultaneously:

#### System A: Legacy `KotlinPlugin`
```kotlin
// Old RSMod v1 style
class MyPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {
    
    init {
        // Bind using string IDs
        onNpcOption("npcs.hans", "talk-to") {
            player.queue { 
                chatNpc("Welcome to Lumbridge!")
            }
        }
    }
}
```

#### System B: New `PluginEvent`
```kotlin
// New annotation-driven style
@PluginConfig(yamlFile = "plugins/myplugin.yml")
class MyPlugin : PluginEvent() {
    
    override fun init() {
        on<NpcClickEvent> {
            where { npc.id == "npcs.hans".asRSCM() }
            then { 
                player.queue { dialog() }
            }
        }
    }
}
```

**Problems:**
- Two systems = confusion
- Legacy system requires passing `PluginRepository` to constructor
- Mixed patterns in same codebase

---

### RSMod v2: Unified `PluginScript`

```kotlin
// Single, clean pattern
class HansScript @Inject constructor(
    private val repo: NpcRepository
) : PluginScript() {
    
    override fun ScriptContext.startup() {
        // Type-safe with compile-time checks
        onOpNpc1(npcs.hans) {
            chatNpc("Welcome to Lumbridge!")
        }
    }
}
```

**Advantages:**
- Single pattern throughout
- Guice handles dependencies
- `ScriptContext` provides DSL scope

---

## 2. ID Resolution System

### OpenRune: RSCM (Runtime String Resolution)

```kotlin
// String IDs resolved at runtime
onNpcOption("npcs.hans", "talk-to") { ... }
onItemOption("items.coins", "examine") { ... }

// Resolution happens via:
fun getRSCM(name: String): Int {
    // Look up in npc.rscm, item.rscm files
    return idMappings[name] ?: error("Unknown: $name")
}
```

**RSCM Files:**
```
data/cfg/rscm/npc.rscm
npc.man=3078
npc.woman=3079
npc.hans=3080
```

**Pros:**
- Human-readable
- Easy to write
- No recompile for ID changes

**Cons:**
- Runtime errors for typos
- No IDE autocomplete
- Refactoring is hard

---

### RSMod v2: Type References (Compile-Time)

```kotlin
// Compile-time safe references
object BaseNpcs : NpcReferences() {
    val hans = find("hans")  // Resolved at compile time
    val man = find("man")
}

// Usage:
onOpNpc1(npcs.hans) { ... }  // Type-safe, IDE autocomplete
```

**Pros:**
- Compile-time safety
- IDE autocomplete/refactoring
- Catch errors before runtime

**Cons:**
- Need to regenerate refs when cache changes
- Slightly more setup

---

## 3. Event Handling

### OpenRune: Hash Map Storage

```kotlin
// Events stored in Int2ObjectOpenHashMap
class PluginRepository {
    private val npcPlugins = Int2ObjectOpenHashMap<
        Int2ObjectOpenHashMap<Plugin.() -> Unit>
    >()
    
    fun bindNpc(npc: Int, opt: Int, plugin: Plugin.() -> Unit) {
        val optMap = npcPlugins[npc] ?: Int2ObjectOpenHashMap(1)
        optMap[opt] = plugin
        npcPlugins[npc] = optMap
    }
    
    fun executeNpc(p: Player, id: Int, opt: Int): Boolean {
        val logic = npcPlugins[id]?.get(opt) ?: return false
        p.executePlugin(logic)
        return true
    }
}
```

**Characteristics:**
- Fast lookups
- Simple implementation
- Harder to debug
- No type safety

---

### RSMod v2: EventBus with Sealed Classes

```kotlin
// Sealed class hierarchy
sealed class NpcEvents {
    sealed class Op(val npc: Npc) : OpEvent(npc.id.toLong()) {
        class Op1(npc: Npc) : Op(npc)
        class Op2(npc: Npc) : Op(npc)
    }
}

// EventBus subscription
class EventBus {
    fun <T : Event> subscribe(
        type: Class<T>,
        key: Long,
        action: (T) -> Unit
    )
}

// Type-safe registration
inline fun <reified T : Event> onEvent(
    key: Long,
    action: (T) -> Unit
) = eventBus.subscribe(T::class.java, key, action)
```

**Characteristics:**
- Type-safe events
- Easier debugging
- Supports suspend functions
- More extensible

---

## 4. Async/Task Handling

### OpenRune: Manual Queue System

```kotlin
// Manual queue with tick delays
player.queue {
    chatNpc("Hello!")
    wait(2)  // Wait 2 ticks
    chatPlayer("Hi!")
    wait(3)
    // ...
}

// Implementation:
class QueueTask {
    suspend fun wait(ticks: Int) {
        delay(ticks * 600L)  // Manual timing
    }
}
```

---

### RSMod v2: Suspend Functions + ProtectedAccess

```kotlin
// Suspend function with automatic protection
onOpNpc1(npcs.hans) {
    // This is a suspend function
    chatNpc("Hello!")
    delay(2)  // Built-in tick delay
    chatPlayer("Hi!")
    delay(3)
    // Auto-cleanup on logout/death
}

// ProtectedAccess ensures safe execution
class ProtectedAccess {
    suspend fun delay(ticks: Int)
    // Automatically handles:
    // - Player logout
    // - Player death
    // - Teleportation
    // - Queue clearing
}
```

---

## 5. Content Organization

### OpenRune: Area-Based

```
content/src/main/kotlin/org/alter/
├── areas/lumbridge/
│   ├── npcs/Hans.kt
│   ├── npcs/Cook.kt
│   └── objs/CookingRange.kt
├── areas/varrock/
│   ├── npcs/
│   └── objs/
├── skills/woodcutting/
├── skills/mining/
└── quest/cooks_assistant/
```

**Philosophy:** Organize by where content exists in game world

**Pros:**
- Easy to find area-specific content
- Matches mental model of game world

**Cons:**
- Duplicated patterns across areas
- Harder to share code

---

### RSMod v2: System-Based

```
api/
├── player/           # Player systems
├── npc/              # NPC systems
├── combat/           # Combat formulas
├── skills/           # Skill implementations
├── invtx/            # Inventory transactions
├── shops/            # Shop system
└── type/             # Type system
    ├── type-builders/
    ├── type-editors/
    └── type-references/

content/
├── areas/city/lumbridge/
├── skills/woodcutting/
├── interfaces/bank/
└── generic/
```

**Philosophy:** Organize by system/domain, separate from content

**Pros:**
- Reusable systems
- Clear separation of concerns
- Easier testing

**Cons:**
- Less obvious where area content lives

---

## 6. Type System

### OpenRune: Simple Types + JSON

```kotlin
// Types are simple data classes
class NpcType(
    val id: Int,
    val name: String,
    val level: Int
)

// Loaded from JSON/cache
fun loadNpcTypes() {
    val json = loadJson("npcs.json")
    return gson.fromJson(json)
}
```

---

### RSMod v2: Three-Phase Type System

```kotlin
// 1. BUILDERS - Create new types
class NpcTypeBuilder : TypeBuilder<NpcType>() {
    var name: String = ""
    var desc: String = ""
    var size: Int = 1
    
    override fun build(): NpcType {
        return NpcType(internalId, name, desc, size)
    }
}

// 2. EDITORS - Modify existing types
class NpcTypeEditor : TypeEditor<NpcType>() {
    fun changeLevel(newLevel: Int) {
        type.level = newLevel
    }
}

// 3. REFERENCES - Use types safely
object Npcs : NpcReferences() {
    val hans = find("hans")
}

// Usage:
npcs.hans.name  // Compile-time safe
```

---

## 7. Dependency Injection

### OpenRune: Manual/Partial

```kotlin
// Some DI via constructor, but manual in places
class MyPlugin(
    r: PluginRepository,      // Passed manually
    world: World,              // Passed manually
    server: Server             // Passed manually
) : KotlinPlugin(r, world, server) {
    
    // Access other things manually:
    val npcs = world.npcs
    val players = world.players
}
```

---

### RSMod v2: Full Guice Integration

```kotlin
// Full DI via Guice
class Woodcutting @Inject constructor(
    private val objTypes: ObjTypeList,      // Injected
    private val locTypes: LocTypeList,      // Injected
    private val enumTypes: EnumTypeList,    // Injected
    private val locRepo: LocRepository,     // Injected
    private val mapClock: MapClock,         // Injected
) : PluginScript() {
    // Everything is provided by Guice
}
```

---

## 8. DSL Patterns

### OpenRune: Specialized DSLs

```kotlin
// Specialized DSL for each domain
setCombatDef("npcs.goblin") {
    configs {
        attackSpeed = 4
    }
    stats {
        hitpoints = 5
    }
    drops {
        always { add("items.bones") }
    }
}

// Different DSL for drops
dropTable("goblin_drops") {
    main(weight = 128) {
        add("items.coins", min = 5, max = 5, weight = 35)
    }
}
```

---

### RSMod v2: Uniform Extension Functions

```kotlin
// Uniform DSL via ScriptContext extensions
onOpNpc1(npcs.goblin) {
    // All actions use same DSL
    mes("You attack the goblin!")
    anim(anims.attack)
    soundSynth(synths.sword_slash)
}

// Type building uses similar pattern
buildType<NpcTypeBuilder>("my_npc") {
    name = "My NPC"
    desc = "Description"
}
```

---

## Summary: Which is Better?

### Use OpenRune Patterns When:
- Want quick content iteration
- Prefer string-based IDs
- Like area-based organization
- Need specialized DSLs

### Use RSMod Patterns When:
- Want compile-time safety
- Need robust async handling
- Prefer dependency injection
- Want unified event system

### Reality:
Both work. RSMod v2 is more "production-ready" with better engineering practices, while OpenRune is more "content-developer-friendly" with simpler patterns.

---

*Choose based on your priorities: safety vs convenience.*

