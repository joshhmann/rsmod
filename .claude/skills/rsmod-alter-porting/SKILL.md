---
name: rsmod-alter-porting
description: Port content from Alter (RSMod v1) to RSMod v2. Use when converting Kotlin plugins from Alter codebase to RSMod v2 architecture, migrating event handlers, updating API calls, or adapting content between the two framework versions.
---

# RSMod v1 (Alter) → v2 Porting Guide

## Task Coordination — Do This First

Multiple agents (Claude, OpenCode, Kimi, Codex) work this codebase simultaneously.
Before writing a single line of code, coordinate via the `agent-tasks` MCP server:

1. `list_tasks({ status: "pending" })` — find available work
2. `get_task("TASK-ID")` — read the full task description
3. `claim_task("TASK-ID", "your-agent-name")` — atomically claim it (fails if taken)
4. `check_conflicts(["path/file"])` — verify no one else is editing your files
5. `lock_file("path/file", "your-agent-name", "TASK-ID")` — lock every file before editing
6. Implement.
7. `complete_task("TASK-ID", "your-agent-name", "what I built")` — releases locks, marks done

If blocked: `block_task("TASK-ID", "your-agent-name", "exact reason")` so others know.
See `START_HERE.md` for the full project orientation.

Port Alter plugins to RSMod v2. Alter uses string-based IDs and PluginRepository pattern; v2 uses typed refs, Guice injection, and suspend functions.

## Architecture Changes

### Plugin Class Structure

**Alter v1:**
```kotlin
class MyPlugin(
    r: PluginRepository,
    world: World,
    server: Server,
) : KotlinPlugin(r, world, server) {
    init {
        // register handlers
    }
}
```

**RSMod v2:**
```kotlin
class MyPlugin @Inject constructor(
    private val objTypes: ObjTypeList,
    private val locRepo: LocRepository,
) : PluginScript() {
    override fun ScriptContext.startup() {
        // register handlers
    }
}
```

### Event Handler Mapping

| Alter (v1) | RSMod (v2) | Notes |
|------------|------------|-------|
| `onNpcOption(npc = "npc.name", option = "talk-to")` | `onOpNpc1(npcs.name)` | Op1 = first option |
| `onObjOption(obj = "object.name", option = "chop")` | `onOpLoc1(locs.name)` | Loc = game object |
| `onItemOption(item = "item.name", option = "eat")` | `onOpObj1(objs.name)` | Obj = inventory item |
| `onPlayerOption(option = "attack")` | `onPlayerOption1()` | Direct player interaction |
| `onNpcOption(npc, "pickpocket")` | `onOpNpc2(npcs.name)` | Op2 = second option |
| `onUseWith(obj = "object", item = "item")` | `onOpLocU(locs.name, objs.item)` | Item-on-loc |
| `onUseWith(inv = "inventory", itemUsed = "a", itemOn = "b")` | `onOpHeldU(objs.a, objs.b)` | Item-on-item |

### Op Number Mapping

- **Op1** = Left-click / first right-click option
- **Op2** = Second right-click option  
- **Op3** = Third option
- **Op4/Op5** = Fourth/Fifth options

### Context Access

**Alter v1:**
```kotlin
onNpcOption(npc = "npc.banker", option = "talk-to") {
    // `player` and `npc` in scope
    player.message("Hello")
    npc.forceChat("Welcome!")
}
```

**RSMod v2:**
```kotlin
onOpNpc1(npcs.banker) { event ->
    // `this` = ProtectedAccess, `event.npc` = the NPC
    mes("Hello")
    event.npc.say("Welcome!")
}
```

## Common API Translations

### Player Operations

| Alter | RSMod v2 |
|-------|----------|
| `player.message("text")` | `mes("text")` |
| `player.message("text", type = ChatMessageType.GAME)` | `mes("text")` |
| `player.addXp(Skills.WOODCUTTING, 25.0)` | `statAdvance(stats.woodcutting, 25.0)` |
| `player.animate(Animation.CHOP)` | `anim(seqs.woodcutting_axe)` |
| `player.graphic(245, 124)` | `graphic(spotanims.effect, height = 124)` |
| `player.inventory.add(item, amount)` | `invAdd(invs.inv, objs.item, amount)` |
| `player.inventory.remove(item, amount)` | `invDel(invs.inv, objs.item, amount)` |
| `player.getSkills().getCurrentLevel(Skill)` | `player.skillLvl` (extension) |
| `player.getInteractingNpc()` | `event.npc` or from handler param |
| `player.facePawn(npc)` | Automatic on interaction |
| `player.lock()` / `player.unlock()` | Handled by ProtectedAccess |
| `player.tile` | `player.coords` |

### NPC Operations

| Alter | RSMod v2 |
|-------|----------|
| `npc.forceChat("text")` | `npc.say("text")` |
| `npc.animate(Animation.ATTACK)` | Set via cache params, not runtime |
| `npc.getId()` | `npc.id` |
| `npc.name` | `npc.type.name` |

### Delay/Suspend

**Alter v1:**
```kotlin
player.queue {
    player.animate(Animation.CHOP)
    task.wait(3)  // wait 3 ticks
    // continue
}
```

**RSMod v2:**
```kotlin
// Handlers are already suspend
onOpLoc1(content.tree) {
    anim(seqs.woodcutting_axe)
    delay(3)  // suspend 3 ticks
    // continue
}
```

### Random Numbers

| Alter | RSMod v2 |
|-------|----------|
| `world.random(1..100)` | `random.of(1, 100)` |
| `world.randomDouble()` | `random.randomDouble()` |

### Inventory Checks

| Alter | RSMod v2 |
|-------|----------|
| `player.inventory.hasItem(item)` | `inv.contains(objs.item)` |
| `player.inventory.getItemCount(item)` | `inv.count(objs.item)` |
| `player.inventory.freeSlotCount` | `inv.freeSpace()` |
| `player.inventory.isFull` | `inv.isFull()` |

### Type References

| Alter (string) | RSMod (typed) |
|----------------|---------------|
| `"item.logs"` | `objs.logs` |
| `"npc.banker"` | `npcs.banker_lumbridge` |
| `"object.tree"` | `locs.oak_tree_10820` |
| `"animation.chop"` | `seqs.woodcutting_axe` |
| `"sound.chop"` | `synths.axe_chop` |
| `Skills.WOODCUTTING` | `stats.woodcutting` |

## Content Group Migration

Alter uses string IDs; v2 prefers content groups for generic handlers.

**Alter:**
```kotlin
// Register 50 individual handlers
onObjOption(obj = "object.oak_tree_10820", option = "chop") { }
onObjOption(obj = "object.oak_tree_10831", option = "chop") { }
// ...
```

**RSMod v2:**
```kotlin
// One handler for all content-grouped entities
onOpLoc1(content.tree) { 
    // Handles ALL tree variants automatically
}
```

Content groups are assigned in cache data. To add:
1. Add to `BaseContent.kt`: `val tree = find("tree")`
2. Tag loc types in cache with `contentGroup = 'tree'`

## Param-Driven Configuration

RSMod v2 stores configuration in cache params instead of code:

**Alter:**
```kotlin
setCombatDef("npc.cow") {
    configs { attackSpeed = 6; respawnDelay = 45 }
    stats { hitpoints = 8; attack = 1; strength = 1 }
    anims { attack = Animation.COW_ATTACK }
}
```

**RSMod v2:**
```toml
# In NPC type config or cache params
params.attack_speed = 6
params.respawn_delay = 45
params.hitpoints = 8
params.attack = 1
params.strength = 1
params.attack_anim = <seq_id>
```

## Spawning Changes

**Alter:** Programmatic spawning in plugins
```kotlin
spawnNpc("npc.banker", x = 3207, z = 3220, level = 0)
spawnObj("object.bank_booth", tile = Tile(3207, 3220))
```

**RSMod v2:** Data-driven spawning via TOML + MapNpcSpawnBuilder:
```toml
# resources/.../npcs.toml
[[spawn]]
npc = 'banker'
coords = '0_48_50_10_16'   # level_regionX_regionZ_localX_localZ
```
- Dynamic locs: `locRepo.change()` for depletion
- See `LumbridgeNpcSpawns.kt` for the full pattern

## Dialogue Conversion

**Alter:**
```kotlin
player.queue {
    it.chatNpc(player, "Hello", animation = 568)
    when (it.options(player, "Yes", "No")) {
        1 -> { }
        2 -> { }
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
            1 -> { }
            2 -> { }
        }
    }

onOpNpc1(npcs.npc_name) { talkToNpc(it.npc) }
```

## Common Pitfalls

1. **Event scope:** v2 handlers run in `ProtectedAccess` context - many methods directly available
2. **Type safety:** v2 uses typed refs - check `api/config/refs/` for correct names
3. **Timing:** v2 `delay()` is suspend function, no queue wrapper needed
4. **Injection:** Use `@Inject constructor(...)` for dependencies, not PluginRepository
5. **Params:** Move configuration to cache params instead of hardcoding

## File Locations

| Alter Path | RSMod v2 Path |
|------------|---------------|
| `game-plugins/src/main/kotlin/org/alter/plugins/content/skills/` | `content/skills/` |
| `game-plugins/src/main/kotlin/org/alter/plugins/content/npcs/` | `content/generic/generic-npcs/` or `content/areas/` |
| `game-api/src/main/kotlin/org/alter/api/cfg/` | `api/config/refs/` |

## Testing After Port

1. Build via MCP: `build_server({ module: "your-module-name" })`
2. Check server: `server_status()` — must show AgentBridge on port 43595
3. Verify in-game: level gates, XP rates, animations, item handling
4. Check `docs/CONTENT_AUDIT.md` for expected behavior
5. See `docs/LEGACY_PLAYBOOK.md` for source priority and DSL reference
