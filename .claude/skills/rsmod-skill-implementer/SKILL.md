---
name: rsmod-skill-implementer
description: Implement OSRS skills for RSMod v2. Use when creating new skill content, writing Kotlin plugins for skills, or implementing gathering/processing/artisan skills. Covers module scaffolding, event handlers, resource depletion, controllers, and XP grants.
---

# RSMod v2 Skill Implementer

## Task Coordination — Do This First

Multiple agents (Claude, OpenCode, Kimi, Codex) work this codebase simultaneously.
Before writing a single line of code, coordinate via the `agent-tasks` MCP server:

1. `list_tasks({ status: "pending" })` — find available work
2. `get_task("TASK-ID")` — read the full task description
3. `claim_task("TASK-ID", "your-agent-name")` — atomically claim it (fails if taken)
4. `check_conflicts(["path/file"])` — verify no one else is editing your files
5. `lock_file("path/file", "your-agent-name", "TASK-ID")` — lock every file before editing
6. Implement.
7. Run the mandatory Build Gate (`scripts\preflight-ref-hygiene.ps1` AND `gradlew compileKotlin`).
8. `complete_task("TASK-ID", "your-agent-name", "what I built")` — releases locks, marks done

### Mandatory Pre-Flight & Build Gate (Do NOT Skip)
Before compiling, you MUST run:
```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\preflight-ref-hygiene.ps1" -FailOnIssues
```
Then compile:
```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat :server:app:compileKotlin --console=plain"
```
**You cannot mark a task complete unless both scripts return successfully.** Do not use `bindScript`, do not make `TypeReferences` objects `private`, and always verify IDs against `.sym` files.

If blocked: `block_task("TASK-ID", "your-agent-name", "exact reason")` so others know.
See `START_HERE.md` for the full project orientation.

Implement OSRS skills following RSMod v2 patterns. This skill covers module structure, event registration, resource lifecycle (depletion/respawn), and skill-specific mechanics.

## Skill Archetypes

### 1. Gathering Skills (Woodcutting/Mining Pattern)

**Key characteristics:**
- Click resource loc → animation loop → success roll → item to inventory
- Resources deplete and respawn via Controller
- Tool tier affects success rate

**Template structure:**
```kotlin
class SkillName @Inject constructor(
    private val objTypes: ObjTypeList,
    private val locRepo: LocRepository,
    private val conRepo: ControllerRepository,
    private val xpMods: XpModifiers,
    private val invisibleLvls: InvisibleLevels,
    private val mapClock: MapClock,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(content.resource_type) { gather(it.loc, it.type) }
        onAiConTimer(controllers.resource_respawn) { controller.respawnTick() }
    }

    private fun ProtectedAccess.gather(loc: BoundLocInfo, type: UnpackedLocType) {
        // 1. Check tool
        val tool = findTool(player, objTypes) ?: run {
            mes("You need a tool.")
            return
        }
        
        // 2. Check level requirement
        if (player.skillLvl < type.skillLevelReq) {
            mes("You need level ${type.skillLevelReq}.")
            return
        }
        
        // 3. Check inventory space
        if (inv.isFull()) {
            mes("Your inventory is full.")
            return
        }
        
        // 4. First click setup (3-tick delay pattern)
        if (actionDelay < mapClock) {
            actionDelay = mapClock + 3
            skillAnimDelay = mapClock + 3
            spam("You start gathering...")
            opLoc1(loc)
            return
        }
        
        // 5. Refresh animation every 4 ticks
        if (skillAnimDelay <= mapClock) {
            skillAnimDelay = mapClock + 4
            anim(objTypes[tool].toolAnim)
        }
        
        // 6. Success roll on action tick
        var gotResource = false
        if (actionDelay == mapClock) {
            val (low, high) = successRate(type, tool)
            gotResource = statRandom(stats.skillname, low, high, invisibleLvls)
            actionDelay = mapClock + 3
        }
        
        // 7. Grant resource and XP
        if (gotResource) {
            val product = objTypes[type.resourceProduct]
            val xp = type.resourceXp * xpMods.get(player, stats.skillname)
            statAdvance(stats.skillname, xp)
            invAdd(inv, product)
        }
        
        // 8. Depletion check
        val depletes = gotResource && random.of(1, 255) <= type.depleteChance
        if (depletes) {
            locRepo.change(loc, type.depletedLoc, type.respawnTime)
            resetAnim()
        }
        
        // 9. Continue loop
        opLoc1(loc)
    }
}
```

### 2. Processing Skills (Cooking/Smithing Pattern)

**Key characteristics:**
- Use raw item on processing loc (range/furnace/anvil)
- Success/fail roll (burn/smelt failure)
- Item transformation (raw → cooked/burnt, ore → bar)

**Template structure:**
```kotlin
override fun ScriptContext.startup() {
    // Register per-item handlers (until content groups exist)
    for (item in ProcessableItem.entries) {
        onOpLocU(processing_loc, item.rawObj) { process(it.invSlot, isRange = true) }
    }
}

private fun ProtectedAccess.process(slot: Int, isRange: Boolean) {
    val item = inv[slot] ?: return
    val data = ProcessableItem.fromRaw(item) ?: return
    
    // Level check
    if (player.skillLvl < data.levelReq) {
        mes("You need level ${data.levelReq}.")
        return
    }
    
    // Animation + delay
    anim(if (isRange) seqs.range_anim else seqs.fire_anim)
    delay(4)
    
    // Verify item still there
    if (inv[slot]?.id != item.id) return
    
    // Success/burn roll
    if (didFail(data, isRange)) {
        invReplace(inv, data.rawObj, 1, data.burntObj)
        mes("You fail to process it.")
    } else {
        invReplace(inv, data.rawObj, 1, data.cookedObj)
        mes("You successfully process it.")
        statAdvance(stats.skillname, data.xp)
    }
}
```

### 3. Artisan Skills (Fletching/Herblore Pattern)

**Key characteristics:**
- Item-on-item in inventory (use knife on log, herb on vial)
- Multi-step crafting chains (log → unstrung bow → strung bow)
- Interface selection for output choice

**Template structure:**
```kotlin
override fun ScriptContext.startup() {
    // Item-on-item handler (order normalized by engine)
    onOpHeldU(content.tool, content.material) { craft(it.first, it.second) }
}

private fun ProtectedAccess.craft(tool: UnpackedObjType, material: UnpackedObjType) {
    val recipe = Recipe.find(tool, material) ?: run {
        mes("Nothing interesting happens.")
        return
    }
    
    if (player.skillLvl < recipe.levelReq) {
        mes("You need level ${recipe.levelReq}.")
        return
    }
    
    // For single-step: animate, delay, replace items
    anim(seqs.crafting_anim)
    delay(2)
    
    invDel(inv, recipe.input, recipe.inputQty)
    invAdd(inv, recipe.output, recipe.outputQty)
    statAdvance(stats.skillname, recipe.xp)
}
```

### 4. Movement Skills (Agility Pattern)

**Key characteristics:**
- Force-movement via `forceMove()`
- Obstacle sequences (pipe squeeze → log balance → net climb)
- Lap completion bonus XP
- Mark of grace spawning

**Template structure:**
```kotlin
override fun ScriptContext.startup() {
    onOpLoc1(content.obstacle_type) { obstacle ->
        val course = AgilityCourse.fromObstacle(obstacle.type) ?: return@onOpLoc1
        
        // Level check per obstacle
        if (player.agilityLvl < obstacle.levelReq) {
            mes("You need level ${obstacle.levelReq} Agility.")
            return@onOpLoc1
        }
        
        // Force movement sequence
        forceMove(
            from = player.coords,
            to = obstacle.destination,
            speed = if (obstacle.isClimb) 20 else 30,
            direction = obstacle.direction
        )
        anim(obstacle.anim)
        delay(obstacle.ticks)
        
        // Grant XP on completion
        statAdvance(stats.agility, obstacle.xp)
        
        // Lap tracking
        if (obstacle.isFinal) {
            val lapCount = player.incrementLapCounter(course)
            if (lapCount % course.markOfGraceInterval == 0) {
                spawnMarkOfGrace(course)
            }
        }
    }
}
```

## Module Structure

```
content/skills/skillname/
├── build.gradle.kts
└── src/main/kotlin/org/rsmod/content/skills/skillname/
    ├── scripts/
    │   └── SkillName.kt          # Main plugin script
    ├── configs/
    │   └── SkillNameConfigs.kt   # Type builders, param refs (optional)
    └── SkillNameModule.kt        # Guice module (optional)
```

**build.gradle.kts template:**
```kotlin
plugins {
    id("base-conventions")
    id("integration-test-suite")
}

dependencies {
    implementation(projects.api.pluginCommons)
    implementation(projects.api.config)
    implementation(projects.api.stats)
    implementation(projects.api.player)
    implementation(projects.api.random)
    implementation(projects.api.repo.loc)
    implementation(projects.api.repo.controller)
    integrationImplementation(projects.api.player)
}
```

## Common Patterns Reference

### Param Extensions
```kotlin
// Loc params (for resource nodes)
val UnpackedLocType.levelReq: Int by locParam(params.levelrequire)
val UnpackedLocType.product: ObjType by locParam(params.skill_productitem)
val UnpackedLocType.xp: Double by locXpParam(params.skill_xp)
val UnpackedLocType.depletedLoc: LocType by locParam(params.next_loc_stage)

// Obj params (for tools)
val UnpackedObjType.toolLevelReq: Int by objParam(params.levelrequire)
val UnpackedObjType.toolAnim: SeqType by objParam(params.skill_anim)
```

### Tool Selection
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

private fun UnpackedObjType.isUsableTool(skillLevel: Int): Boolean =
    isContentType(content.tool_content_group) && skillLevel >= toolLevelReq
```

### Success Rate (statRandom)
```kotlin
// Returns (low, high) for statRandom - higher is better chance
// low/256 at level 1, high/256 at level 99, linear interpolation
fun successRate(resource: UnpackedLocType, tool: UnpackedObjType): Pair<Int, Int> {
    val difficulty = resource.depleteChance.coerceIn(1, 255)
    val bonus = tool.tierBonus()
    val low = (bonus * difficulty / 512).coerceIn(1, 64)
    val high = ((bonus + 24) * difficulty / 384).coerceIn(low + 1, 255)
    return low to high
}
```

## Key API References

| Task | RSMod v2 API |
|------|--------------|
| XP grant | `statAdvance(stats.skillname, xpAmount)` |
| Animation | `anim(seqs.anim_name)` |
| Delay | `delay(ticks)` |
| Inventory add | `invAdd(inv, objs.item, count)` |
| Inventory delete | `invDel(inv, objs.item, count)` |
| Inventory replace | `invReplace(inv, fromObj, qty, toObj)` |
| Message | `mes("Text")` or `spam("Text")` |
| Sound | `soundSynth(synths.sound_name)` |
| Random | `random.of(min, max)` |
| Loc change | `locRepo.change(loc, newType, respawnTicks)` |

## See Also

- `docs/TRANSLATION_CHEATSHEET.md` for Alter→v2 API mapping
- Woodcutting.kt for gathering template
- Cooking.kt for processing template
- `api/config/refs/` for available symbol names
