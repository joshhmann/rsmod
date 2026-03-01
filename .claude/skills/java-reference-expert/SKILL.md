---
name: java-reference-expert
description: Expert in analyzing and porting RSPS logic from legacy Java codebases (Kronos, Tarnish, OpenRune) to modern Kotlin RSMod v2 architecture.
---

# Java Reference Expert Skill

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

Use this skill to extract, translate, and verify game logic from Java-based private server projects.

## Core Reference Locations

| Project        | Revision | Strength                                             | Root                                                                                 |
| -------------- | -------- | ---------------------------------------------------- | ------------------------------------------------------------------------------------ |
| **Kronos-184** | 184      | Feature-complete skills, NPC drops, combat patterns. | `Kronos-184-Fixed/Kronos-master/kronos-server/src/main/java/io/ruin/model/skills/`  |
| **Kronos data**| 184      | NPC combat JSON, drop table JSON                     | `Kronos-184-Fixed/Kronos-master/kronos-server/data/npcs/`                            |
| **Alter**      | 228      | Kotlin plugin patterns, dialogue, thieving.          | `Alter/game-plugins/src/main/kotlin/org/alter/plugins/content/`                      |
| **Tarnish**    | ~218     | Event handler reference (mostly compiled binaries).  | `tarnish/game-server/src/main/kotlin/`                                               |

> **See `docs/LEGACY_PLAYBOOK.md`** for complete source priority, concrete file paths, field mappings, and practical workflows for each source. Read that before starting any porting work.

## Java → RSMod v2 (Kotlin) Translation Patterns

### 1. Data Models (Item/NPC Definitions)

**Java (Kronos):**
```java
public class ItemDefinition {
    public int id;
    public String name;
    public boolean stackable;
    // ... manual loading from JSON/Binary
}
```

**Kotlin (RSMod v2):**
Configured via `api/config/refs/` and cache params. Never define "dummy" classes; use the `type` system.

### 2. Singleton Managers vs Guice

**Java (Legacy):**
```java
World.getWorld().getGlobalObjects().add(object);
```

**Kotlin (RSMod v2):**
```kotlin
@Inject constructor(private val locRepo: LocRepository)
// ...
locRepo.add(object)
```

### 3. Loop Intervals

**Java:**
Many legacy servers use `World.submit(new Tickable(delay) { ... })`.

**Kotlin (RSMod v2):**
Use handler-scoped `delay(ticks)` for interactions, or `TimerSystem` for long-running state.

## Porting Checklist

1. **Stats (primary)**: Use `get_npc_rev233({ name: "X" })` MCP tool first — Rev 233 is our target.
2. **Stats (fallback)**: `Kronos-184-Fixed/Kronos-master/kronos-server/data/npcs/combat/<Name>.json` — always cross-check vs wiki.
3. **Drop Tables**: Kronos `data/npcs/drops/eco/<Name>.json` has the structure. Item IDs are Rev 184 raw IDs — translate to sym names via `search_objtypes`.
4. **Logic verification**: Java `delay(n)` in events = RSMod v2 `delay(n)` in suspend handlers. OSRS ticks are 600ms.
5. **Drop Table DSL**: `dropTable { always(objs.bones); table("Name", weight=1) { item(objs.x, weight=N) } }` then `registry.register(listOf(npcs.x), table)`. See `content/other/npc-drops/NpcDropTablesScript.kt`.

## Anti-Patterns to Avoid
- **Static Access**: Do not use `World.player` or similar. Always use injected components.
- **Manual ID Typing**: Never use `int id = 4151`. Use `objs.abyssal_whip`.
- **Java Streams in Kotlin**: Use Kotlin standard library `map`, `filter`, and sequences for better performance and readability.
