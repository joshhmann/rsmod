# RSPS Implementation Comparison Guide

**Kronos vs Alter vs RSMod v2: Architecture, Patterns & Best Practices**

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Architecture Comparison](#architecture-comparison)
3. [System-by-System Analysis](#system-by-system-analysis)
4. [Translation Matrices](#translation-matrices)
5. [Best Practices Summary](#best-practices-summary)
6. [Decision Flowchart](#decision-flowchart)
7. [Code Migration Examples](#code-migration-examples)

---

## Executive Summary

| Aspect | Kronos (Java) | Alter (RSMod v1) | RSMod v2 (Current) |
|--------|---------------|------------------|-------------------|
| **Language** | Java 8+ | Kotlin 2.0 | Kotlin 2.0 |
| **Revision** | 184 | 228 | 233 |
| **Architecture** | Singleton managers, static access | Plugin repository, string IDs | Guice DI, typed refs |
| **Entity IDs** | Raw integers | String-based RSCM | Compiled type refs |
| **Tick Handling** | `player.startEvent` with `event.delay` | `player.queue` with `task.wait` | Suspend functions with `delay` |
| **Data Configuration** | Code enums | DSL builders | Cache params + code |
| **Spawn System** | Programmatic | Programmatic | Data-driven (YAML) |

### Key Takeaways

- **Kronos**: Feature-complete reference, excellent for understanding OSRS mechanics
- **Alter**: Clean Kotlin DSL, good structural patterns, string IDs are human-readable
- **RSMod v2**: Type-safe, modern architecture, performance-focused, cache-driven configuration

---

## Architecture Comparison

### 1. Dependency Injection

#### Kronos (Static Access)
```java
// Static singleton pattern - hard to test, tight coupling
World.getWorld().getGlobalObjects().add(object);
player.getStats().addXp(StatType.Mining, xp);
```

**Pros:** Simple to understand, minimal boilerplate  
**Cons:** Untestable without heavy mocking, hidden dependencies

#### Alter (Plugin Repository)
```kotlin
// Constructor injection via PluginRepository
class MyPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {
    // Access to all dependencies through constructor
}
```

**Pros:** Clear dependencies, testable  
**Cons:** Requires manual repository wiring, all plugins get same deps

#### RSMod v2 (Guice DI)
```kotlin
// Precise injection of only what's needed
class MyPlugin @Inject constructor(
    private val objTypes: ObjTypeList,
    private val locRepo: LocRepository,
    private val random: GameRandom
) : PluginScript() {
    // Only what you need, nothing more
}
```

**Pros:** Minimal dependencies, highly testable, compiler-enforced  
**Cons:** Requires understanding of Guice, initial learning curve

### 2. Entity References

| System | Pattern | Example | Compile-Time Safety |
|--------|---------|---------|---------------------|
| Kronos | Raw int IDs | `int LOGS = 1511` | ❌ None |
| Alter | String RSCM | `"item.logs"` | ❌ Runtime lookup |
| RSMod v2 | Typed refs | `objs.logs` | ✅ Full |

### 3. Event Handler Registration

#### Kronos (Anonymous Classes)
```java
ObjectAction.register(objectId, "chop", (player, obj) -> {
    // Handler logic
});
```

#### Alter (DSL in init)
```kotlin
onObjOption(obj = "object.tree", option = "chop") {
    // `player` and `obj` in scope
}
```

#### RSMod v2 (DSL in startup)
```kotlin
override fun ScriptContext.startup() {
    onOpLoc1(content.tree) { event ->
        // `this` = ProtectedAccess, `event.loc` = location
    }
}
```

---

## System-by-System Analysis

### Skills

#### XP Granting

**Kronos:**
```java
player.getStats().addXp(StatType.Woodcutting, 25.0, true);
```

**Alter:**
```kotlin
player.addXp(Skills.WOODCUTTING, 25.0)
```

**RSMod v2:**
```kotlin
statAdvance(stats.woodcutting, 25.0)
// With modifiers:
val xp = baseXp * xpMods.get(player, stats.woodcutting)
statAdvance(stats.woodcutting, xp)
```

#### Tool Selection

**Kronos Pattern:**
```java
public static Tool find(Player player) {
    Tool best = null;
    for(Item item : player.getInventory().getItems())
        best = compare(player, item, best);
    Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
    return compare(player, weapon, best);
}
```

**RSMod v2 Pattern:**
```kotlin
fun findTool(player: Player, objTypes: ObjTypeList): InvObj? {
    val worn = player.righthand?.takeIf { objTypes[it].isUsableTool(player.skillLvl) }
    val carried = player.inv.filterNotNull { objTypes[it].isUsableTool(player.skillLvl) }
        .maxByOrNull { objTypes[it].toolLevelReq }
    return when {
        worn != null && carried != null ->
            if (objTypes[worn].toolLevelReq >= objTypes[carried].toolLevelReq) worn else carried
        else -> worn ?: carried
    }
}
```

#### Success Rate Calculation

**Kronos (OSRS formula):**
```java
public static double chance(int level, Rock type, Pickaxe pickaxe) {
    double points = ((level - type.levelReq) + 1 + (double) pickaxe.points);
    double denominator = (double) type.difficulty;
    return (Math.min(0.95, points / denominator) * 100);
}
```

**RSMod v2 (statRandom integration):**
```kotlin
fun successRate(resource: UnpackedLocType, tool: UnpackedObjType): Pair<Int, Int> {
    val difficulty = resource.depleteChance.coerceIn(1, 255)
    val bonus = tool.tierBonus()
    val low = (bonus * difficulty / 512).coerceIn(1, 64)
    val high = ((bonus + 24) * difficulty / 384).coerceIn(low + 1, 255)
    return low to high
}
// Usage:
val (low, high) = successRate(type, pickaxe)
val gotResource = statRandom(stats.mining, low, high, invisibleLvls)
```

#### Resource Depletion

**Kronos:**
```java
World.startEvent(worldEvent -> {
    rock.setId(emptyId);
    worldEvent.delay(rockData.respawnTime);
    rock.setId(rock.originalId);
});
```

**Alter:**
```kotlin
// Similar to Kronos but with Kotlin coroutines
world.queue {
    obj.transform(emptyId)
    wait(respawnTime)
    obj.revert()
}
```

**RSMod v2:**
```kotlin
// Repository-based with controller timers
locRepo.change(loc, type.depletedLoc, type.respawnTime)

// Controller for complex respawn logic (woodcutting)
onAiConTimer(controllers.woodcutting_tree_duration) {
    controller.treeDespawnTick()
}
```

### Quests

#### Stage Management

**Kronos:**
```java
// Manual varp/varbit management
player.getVarps().setVarp(QUEST_VARP, stage);
int stage = player.getVarps().getVarp(QUEST_VARP);
```

**Alter:**
```kotlin
// Slightly better but still manual
player.setVarp(Varp.COOKS_ASSISTANT, stage)
when (player.getVarp(Varp.COOKS_ASSISTANT)) { }
```

**RSMod v2:**
```kotlin
// Type-safe quest API
when (getQuestStage(QuestList.cooks_assistant)) {
    0 -> startQuest()
    1 -> inProgress()
    else -> completed()
}
access.setQuestStage(QuestList.cooks_assistant, 1)
```

#### Dialogue System

**Kronos:**
```java
// Manual interface management
player.getDialogueManager().startDialogue(new NPCMessage("Hello"), new Options("Yes", "No"));
```

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
            1 -> chatNpc(happy, "Great!")
            2 -> chatNpc(sad, "Goodbye")
        }
    }
```

### NPCs

#### Combat Definitions

**Kronos:**
```java
// Data-driven JSON files
{
  "npc_id": 118,
  "hitpoints": 5,
  "attack_level": 1,
  "attack_speed": 4,
  "attack_anim": 422
}
```

**Alter:**
```kotlin
// DSL-based in code
setCombatDef("npc.cow") {
    configs { attackSpeed = 6; respawnDelay = 45 }
    stats { hitpoints = 8; attack = 1; strength = 1 }
    anims { attack = Animation.COW_ATTACK }
}
```

**RSMod v2:**
```toml
# Cache params - no code needed for basic stats
params.attack_speed = 6
params.respawn_delay = 45
params.hitpoints = 8
params.attack = 1
params.attack_anim = <seq_id>
```

**RSMod v2 (behavior only):**
```kotlin
// Only implement special behaviors
onNpcHit(npcs.cow) {
    npc.queueCombatRetaliate(attacker)
}
```

### Drops

#### Table Structure

**Kronos (JSON):**
```json
{
  "npcIdentifiers": [118],
  "rollRange": 256,
  "availableTables": [
    {
      "lootTableType": "DYNAMIC",
      "loot": [
        {"itemIdentifier": 995, "weight": 10}
      ]
    }
  ]
}
```

**Alter (DSL):**
```kotlin
dropTable("npc.goblin") {
    guaranteed(objs.bones)
    table(weight = 1) {
        item(objs.coins, quantity = 1..10, weight = 10)
    }
}
```

**RSMod v2 (Kotlin DSL):**
```kotlin
val goblinTable = dropTable {
    always(objs.bones)
    table("Armour/Weapons", weight = 1) {
        item(DropTableObjs.bronze_sq_shield, weight = 9)
        item(DropTableObjs.bronze_bolts, quantity = 2..2, weight = 6)
    }
}
registry.register(goblinNpcs, goblinTable)
```

### Shops

**Kronos:**
```java
Shop shop = new Shop(shopId, "Bob's Brilliant Axes");
shop.addItem(new ShopItem(1265, 5)); // Bronze axe, 5 stock
shop.open(player);
```

**Alter:**
```kotlin
onNpcOption(npc = "npc.bob", option = "trade") {
    player.openShop(shopId)
}
```

**RSMod v2:**
```kotlin
// Shop definitions are data-driven
onOpNpc3(lumbridge_npcs.bob) { player.openShop(it.npc) }
```

---

## Translation Matrices

### Kronos Java → RSMod v2 Kotlin

| Kronos (Java) | RSMod v2 (Kotlin) | Notes |
|---------------|-------------------|-------|
| `player.startEvent(event -> { ... })` | Suspend function body | Handlers are already suspend |
| `event.delay(3)` | `delay(3)` | Direct suspend call |
| `player.animate(id)` | `anim(seqs.anim_name)` | Use typed seq refs |
| `player.resetAnimation()` | `resetAnim()` | - |
| `player.sendMessage("text")` | `mes("text")` | - |
| `player.getStats().addXp(skill, xp)` | `statAdvance(stats.skill, xp)` | - |
| `player.getInventory().add(id, count)` | `invAdd(invs.inv, objs.item, count)` | - |
| `player.getInventory().remove(id, count)` | `invDel(invs.inv, objs.item, count)` | - |
| `World.startEvent(...)` | Controller + `onAiConTimer` | For world-level events |
| `GameObject.setId(id)` | `locRepo.change(loc, newType, respawnTime)` | - |
| `Random.get(max)` | `random.of(1, max)` | - |
| `Random.getDouble()` | `random.randomDouble()` | - |
| `player.getEquipment().get(slot)` | `player.righthand` / `player.worn[slot]` | - |
| `Tile(x, y, z)` | `Coords(x, y, z)` | - |

### Alter v1 → RSMod v2

| Alter (v1) | RSMod (v2) | Notes |
|------------|------------|-------|
| `onNpcOption(npc, option)` | `onOpNpc1(npcs.name)` | Op number = option position |
| `onObjOption(obj, option)` | `onOpLoc1(locs.name)` | Loc = game object |
| `onItemOption(item, option)` | `onOpObj1(objs.name)` | Obj = inventory item |
| `onUseWith(obj, item)` | `onOpLocU(locs.name, objs.item)` | Item-on-loc |
| `onUseWith(inv, used, on)` | `onOpHeldU(objs.used, objs.on)` | Item-on-item |
| `player.queue { task.wait(3) }` | `delay(3)` | No wrapper needed |
| `player.message("text")` | `mes("text")` | - |
| `player.addXp(Skills.X, xp)` | `statAdvance(stats.x, xp)` | - |
| `player.animate(Animation.X)` | `anim(seqs.x)` | - |
| `player.inventory.add(item, count)` | `invAdd(invs.inv, objs.item, count)` | - |
| `world.random(1..100)` | `random.of(1, 100)` | - |
| `setCombatDef("npc") { }` | Cache params | Data-driven |
| `spawnNpc("npc", x, z)` | Data-driven YAML | Separate spawn files |

### Type Reference Mapping

| Concept | Alter String | RSMod v2 Typed |
|---------|--------------|----------------|
| Items | `"item.logs"` | `objs.logs` |
| NPCs | `"npc.cow"` | `npcs.cow` |
| Objects | `"object.tree"` | `locs.tree` |
| Content Groups | N/A | `content.tree` |
| Stats | `Skills.WOODCUTTING` | `stats.woodcutting` |
| Animations | `Animation.CHOP` | `seqs.woodcutting_axe` |
| Sounds | `Sound.CHOP` | `synths.axe_chop` |
| Graphics | `Graphic.LEVELUP` | `spotanims.levelup` |

---

## Best Practices Summary

### What Kronos Does Well

1. **Complete OSRS Mechanics**: Feature-complete skills with accurate formulas
2. **Data-Driven Drops**: JSON-based drop tables are easy to modify
3. **Comprehensive Skill Formulas**: Success rate calculations match OSRS
4. **Resource Management**: Clear patterns for depletion and respawn

**Adopt in RSMod v2:**
- Success rate formulas for gathering skills
- Drop table weight calculations
- Multi-table drop structure (guaranteed/dynamic/unique)

### What Alter Does Well

1. **Clean DSL**: Readable plugin definitions
2. **String IDs**: Human-readable entity references
3. **Service Pattern**: Good for shared data loading
4. **Extension Functions**: Rich player/NPC APIs

**Adopt in RSMod v2:**
- DSL patterns for table definitions
- Service pattern for complex data
- Extension function patterns

### What RSMod v2 Improves

1. **Type Safety**: Compiled refs eliminate runtime ID errors
2. **Suspend Functions**: Cleaner async code without wrappers
3. **Guice DI**: Proper dependency injection
4. **Cache Params**: Data-driven configuration reduces code
5. **Content Groups**: Generic handlers for similar entities
6. **Controller System**: Proper world-level event handling
7. **ProtectedAccess**: Safe player action context

**Key v2 Patterns to Embrace:**
- Use `content.group` for generic handlers
- Move static data to cache params
- Leverage suspend functions for tick delays
- Use `ProtectedAccess` for all player actions

---

## Decision Flowchart

### Starting a New Feature

```
START
  │
  ▼
Check docs/CONTENT_AUDIT.md
  │
  ├── Already Complete? ──► STOP
  │
  ▼
Gather Reference Material
  │
  ├── OSRS Wiki (primary behavior)
  │
  ├── Kronos-184 (skill formulas, drop tables)
  │
  └── Alter (DSL patterns, dialogue structure)
  │
  ▼
Check rsmod/.data/symbols/
  │
  ├── obj.sym ──► Item IDs
  │
  ├── npc.sym ──► NPC IDs  
  │
  └── loc.sym ──► Object IDs
  │
  ▼
Choose Implementation Approach
  │
  ├── Gathering Skill? ──► Woodcutting Pattern
  │
  ├── Processing Skill? ──► Firemaking Pattern
  │
  ├── Quest? ──► CooksAssistant Pattern
  │
  ├── NPC Combat? ──► Cache params + behavior script
  │
  └── Drops? ──► NpcDropTablesScript Pattern
  │
  ▼
Implement & Build
  │
  ▼
Write bot test (bots/<feature>.ts)
  │
  ▼
Update CONTENT_AUDIT.md
  │
  ▼
DONE
```

### Choosing Handler Type

```
What interaction type?
  │
  ├── NPC Option 1 (Talk-to) ──► onOpNpc1(npcs.name)
  │
  ├── NPC Option 2 (Pickpocket) ──► onOpNpc2(npcs.name)
  │
  ├── Object Option 1 (Chop/Mine) ──► onOpLoc1(content.group)
  │
  ├── Object Option 2 (Prospect) ──► onOpLoc2(locs.specific)
  │
  ├── Item on Object ──► onOpLocU(locs.name, objs.item)
  │
  ├── Item on Item ──► onOpHeldU(objs.a, objs.b)
  │
  └── Use Item ──► onOpObj1(objs.item)
```

### Data Storage Decision

```
Where to store configuration?
  │
  ├── Static constants (never changes) ──► Companion object const
  │
  ├── Skill-specific data (XP rates, levels) ──► Cache params
  │
  ├── NPC stats (HP, attack, defense) ──► Cache params
  │
  ├── Drop tables ──► Kotlin DSL in script
  │
  ├── Quest stages ──► QuestList + varps
  │
  └── Dynamic state ──► Varps/VarBits
```

---

## Code Migration Examples

### Example 1: Converting a Skill from Kronos

**Kronos (Mining.java):**
```java
private static void mine(Rock rockData, Player player, GameObject rock, int emptyId) {
    Pickaxe pickaxe = Pickaxe.find(player);
    if (pickaxe == null) { /* error */ return; }
    
    player.startEvent(event -> {
        int attempts = 0;
        while (true) {
            if (rock.id == emptyId) { player.resetAnimation(); return; }
            
            if(attempts == 0) {
                player.sendFilteredMessage("You swing your pick at the rock.");
                player.animate(pickaxe.regularAnimationID);
                attempts++;
            } else if (attempts % 2 == 0 && Random.get(100) <= chance(...)) {
                player.getInventory().add(rockData.ore, 1);
                player.getStats().addXp(StatType.Mining, rockData.experience, true);
                
                if (Random.get() < rockData.depleteChance) {
                    player.resetAnimation();
                    World.startEvent(worldEvent -> {
                        rock.setId(emptyId);
                        worldEvent.delay(rockData.respawnTime);
                        rock.setId(rock.originalId);
                    });
                    return;
                }
            }
            
            if(attempts++ % 4 == 0)
                player.animate(pickaxe.regularAnimationID);
            
            event.delay(1);
        }
    });
}
```

**RSMod v2:**
```kotlin
class Mining @Inject constructor(
    private val objTypes: ObjTypeList,
    private val locRepo: LocRepository,
    private val mapClock: MapClock,
) : PluginScript() {
    
    override fun ScriptContext.startup() {
        onOpLoc1(content.mining_rock) { mine(it.loc, it.type) }
    }
    
    private fun ProtectedAccess.mine(loc: BoundLocInfo, type: UnpackedLocType) {
        val pickaxe = findTool(player, objTypes) ?: run {
            mes("You need a pickaxe to mine this rock.")
            return
        }
        
        // First click setup
        if (actionDelay < mapClock) {
            actionDelay = mapClock + 3
            skillAnimDelay = mapClock + 3
            spam("You swing your pick at the rock.")
            opLoc1(loc)
            return
        }
        
        // Refresh animation every 4 ticks
        if (skillAnimDelay <= mapClock) {
            skillAnimDelay = mapClock + 4
            anim(objTypes[pickaxe].toolAnim)
        }
        
        // Success on action tick
        if (actionDelay == mapClock) {
            val (low, high) = successRate(type, pickaxe)
            val gotOre = statRandom(stats.mining, low, high, invisibleLvls)
            
            if (gotOre) {
                statAdvance(stats.mining, type.resourceXp)
                invAdd(inv, type.product)
                
                // Check depletion
                if (random.of(1, 255) <= type.depleteChance) {
                    locRepo.change(loc, type.depletedLoc, type.respawnTime)
                    resetAnim()
                    return
                }
            }
            actionDelay = mapClock + 3
        }
        
        opLoc1(loc)
    }
}
```

### Example 2: Converting a Quest from Alter

**Alter (Quest):**
```kotlin
class CooksAssistantPlugin(r: PluginRepository, world: World, server: Server) 
    : KotlinPlugin(r, world, server) {
    
    init {
        onNpcOption(npc = "npc.cook", option = "talk-to") {
            player.queue { dialog(player, npc) }
        }
    }
    
    suspend fun QueueTask.dialog(player: Player, npc: Npc) {
        when (player.getVarp(Varp.COOK_QUEST)) {
            0 -> {
                chatNpc(player, "What am I to do?", animation = 567)
                val choice = options(player, "What's wrong?", "Can you make me a cake?")
                when (choice) {
                    1 -> startQuest(player, npc)
                }
            }
        }
    }
}
```

**RSMod v2:**
```kotlin
class CooksAssistant : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(cooks_assistant_npcs.cook) { startCookDialogue(it.npc) }
    }
    
    private suspend fun ProtectedAccess.startCookDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.cooks_assistant)) {
                0 -> cookStartQuestDialogue()
                1 -> cookInProgressDialogue()
                else -> cookFinishedDialogue()
            }
        }
    
    private suspend fun Dialogue.cookStartQuestDialogue() {
        chatNpc(sad, "What am I to do?")
        val option = choice2("What's wrong?", 1, "Can you make me a cake?", 2)
        when (option) {
            1 -> {
                chatPlayer(quiz, "What's wrong?")
                explainQuestProblem()
            }
        }
    }
}
```

### Example 3: Converting Drops from Kronos

**Kronos (JSON):**
```json
{
  "npcIdentifiers": [118],
  "rollRange": 256,
  "availableTables": [
    {
      "lootTableType": "STATIC",
      "loot": [{"itemIdentifier": 526, "weight": 1}]
    },
    {
      "lootTableType": "DYNAMIC",
      "loot": [
        {"itemIdentifier": 995, "weight": 10, "minAmount": 1, "maxAmount": 10}
      ]
    }
  ]
}
```

**RSMod v2:**
```kotlin
private fun registerGoblin() {
    val goblinTable = dropTable {
        always(objs.bones)
        
        table("Armour/Weapons", weight = 1) {
            item(DropTableObjs.bronze_sq_shield, weight = 9)
            item(DropTableObjs.bronze_bolts, quantity = 2..2, weight = 6)
        }
        
        table("Other", weight = 1) {
            item(objs.coins, quantity = 1..10, weight = 10)
        }
    }
    
    registry.register(goblinNpcs, goblinTable)
}
```

---

## Quick Reference Card

### Essential Imports for RSMod v2

```kotlin
// Core
import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// Player access
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.output.ClientScripts

// Inventory
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invDel
import org.rsmod.api.invtx.invReplace

// Stats/XP
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.api.stats.xpmod.XpModifiers

// Events
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.script.onAiConTimer

// Repos
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.controller.ControllerRepository

// Types
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType

// Random
import org.rsmod.api.random.GameRandom
```

### Common Handler Patterns

```kotlin
// Gathering skill (woodcutting, mining)
override fun ScriptContext.startup() {
    onOpLoc1(content.resource) { gather(it.loc, it.type) }
    onOpLoc3(content.resource) { gather(it.loc, it.type) }
    onAiConTimer(controllers.respawn) { controller.respawnTick() }
}

// Processing skill (firemaking, herblore)
override fun ScriptContext.startup() {
    onOpHeldU(objs.tool, objs.material) { process(it) }
}

// NPC interaction
override fun ScriptContext.startup() {
    onOpNpc1(npcs.npc_name) { talkToNpc(it.npc) }
    onOpNpc2(npcs.npc_name) { pickpocket(it.npc) }
}

// Quest NPC
override fun ScriptContext.startup() {
    onOpNpc1(quest_npcs.npc) { startDialogue(it.npc) }
}
```

### Tick Timing Quick Reference

| Action | Ticks | Pattern |
|--------|-------|---------|
| First action delay | 3 | `actionDelay = mapClock + 3` |
| Animation refresh | 4 | `skillAnimDelay = mapClock + 4` |
| Woodcutting cycle | 4 | `delay(4)` between attempts |
| Mining cycle | 3 | `delay(3)` between attempts |
| Cooking action | 4 | `delay(4)` per item |
| Pickpocket | 2 | `delay(2)` for animation |

---

## Additional Resources

- [TRANSLATION_CHEATSHEET.md](../../docs/TRANSLATION_CHEATSHEET.md) - Complete API mapping
- [RUNESERVER_NOTES.md](../../docs/RUNESERVER_NOTES.md) - OSRS mechanics research
- [LEGACY_IMPLEMENTATION_PLAYBOOK.md](../../docs/LEGACY_IMPLEMENTATION_PLAYBOOK.md) - Safe porting guidelines
- [kronos-skills.md](./kronos-skills.md) - Kronos skill extraction
- [alter-structure.md](./alter-structure.md) - Alter architecture analysis

---

*Generated for RSMod v2 development. Last updated: February 2026*
