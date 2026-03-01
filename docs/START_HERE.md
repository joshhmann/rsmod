# START HERE — RSMod v2 OSRS Private Server

> Read this entire document before touching any code.
> It covers the project, how to build content, and the traps that will waste your time.

---

## Authority Note (Read First)

- `README.md` is the definitive operational playbook for agent startup, tooling checks, build gate, and cleanup.
- Use this file primarily for implementation examples and API patterns.
- If this file conflicts with `README.md` or `AGENTS.md`, follow `README.md` first, then `AGENTS.md`.

---

## Before You Do Anything — Task Coordination

Multiple agents (Claude, OpenCode, Kimi, Codex) work this codebase simultaneously.
**Always coordinate through the `agent-tasks` MCP server before touching any file.**

### Agent workflow — follow this every time:

```
1. get_status()                    → see overall progress
2. list_tasks({ status: "pending" }) → find available work
3. get_task("QUEST-1")             → read full task description
4. claim_task("QUEST-1", "yourname") → atomically claim it (fails if already taken)
5. check_conflicts(["path/to/file"]) → verify no one else is editing your files
6. lock_file("path/to/file", "yourname", "QUEST-1") → declare every file you'll edit
7. ... implement ...
8. complete_task("QUEST-1", "yourname", "what I did") → release locks, mark done
```

**Never edit a file without locking it. Never start work without claiming a task.**
If you're stuck: `block_task("QUEST-1", "yourname", "exact reason")` so others know.

---

## Kotlin Tooling Workflow (Required)

Use Kotlin tooling in this order before manual deep grep:

1. `cclsp` first for:
   - `get_diagnostics`
   - `find_workspace_symbols`
   - `find_definition`
   - `find_references`
   - `get_hover`
2. For call hierarchy needs (callers/callees), use:
```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\kotlin-call-hierarchy.ps1" -Symbol <Name> -RepoRoot "Z:\Projects\OSRS-PS-DEV" -Scope rsmod -Mode both -MaxResults 200
```
3. `cclsp` + current Kotlin LSP in this project does not support `prepareCallHierarchy`; this is expected.
4. Do not leave manual long-running `kotlin-lsp --stdio` sessions active during normal development; let `cclsp` spawn/manage the LSP process.

### Session Operational Check (Copy/Paste)

Run this at the start of a session (or after any `cancelled` LSP errors):

1. Verify both config files contain the same Kotlin entry:
   - `C:\Users\CRIMS\.claude\cclsp.json`
   - `C:\Users\CRIMS\.config\cclsp\cclsp.json`

Expected Kotlin entry:
```json
{
  "extensions": ["kt", "kts"],
  "command": ["C:\\Windows\\System32\\cmd.exe", "/c", "C:\\tools\\kotlin-lsp\\kotlin-lsp.cmd", "--stdio"],
  "rootDir": "Z:\\Projects\\OSRS-PS-DEV\\rsmod",
  "timeout": 600000
}
```

2. Clean stale Kotlin LSP processes:
```powershell
Get-CimInstance Win32_Process | ? { ($_.Name -in @('java.exe','cmd.exe')) -and $_.CommandLine -match 'kotlin-lsp' } | % { Stop-Process -Id $_.ProcessId -Force }
```

3. Warmup order (required):
   - `find_workspace_symbols("GameServer")`
   - wait `10-15` seconds
   - `get_diagnostics("rsmod/server/app/src/main/kotlin/org/rsmod/server/app/GameServer.kt")`
   - `find_definition(GameServer)`

4. If tools are still `cancelled`, mark `cclsp` degraded for the session and continue with:
```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\kotlin-call-hierarchy.ps1" -Symbol <Name> -RepoRoot "Z:\Projects\OSRS-PS-DEV" -Scope rsmod -Mode both -MaxResults 200
```

5. End-of-session cleanup:
```powershell
\.\\gradlew.bat --stop
Get-CimInstance Win32_Process | ? { ($_.Name -in @('java.exe','cmd.exe')) -and $_.CommandLine -match 'kotlin-lsp' } | % { Stop-Process -Id $_.ProcessId -Force }
```

### Required Agent Report Format

When you finish, report in this exact structure:
1. Claimed task
2. Locked files
3. Commands run (exact order)
4. Results (pass/fail + first error line)
5. Cleanup performed (Gradle + kotlin-lsp)

---

## MANDATORY COMPLETION GATE

> **Do NOT call `complete_task()` until the code passes pre-flight checks, compiles clean, AND the server boots to World is Live.**

After every edit, run all four checks:

**Step 1 — Pre-Flight Hygiene:**
```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\preflight-ref-hygiene.ps1" -FailOnIssues
```
Must exit with 0 issues. Fix any private References, bindScript calls, or missing base symbols.

**Step 2 — Compile Sub-Modules:**
Ensure any touched API or Content modules compile individually (e.g., `./gradlew :api:spells:compileKotlin`).

**Step 3 — Full Boot Gate (The Only True Success Signal):**
```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat :server:app:run --console=plain --args='--skip-type-verification --allow-type-verification-failures'"
```
Must reach `[MainGameProcess] World is live`. Any crash before this means the task is incomplete.

**Step 4 — Startup WARNs (Zero Tolerance):**
Grep the boot log from Step 3 for `Skipping script startup`. Must return **zero lines**.
Any `Skipping script startup for` line means a script is silently disabled — treat it as a build failure and fix it.

Marking a task complete with broken code blocks other agents and creates emergency BUILD-CRIT tickets.

### Patterns that break the build — do not use these

| Wrong | Correct |
|-------|---------|
| `bindScript<MyScript>()` in PluginModule.bind() | Delete it — scripts are classpath-scanned, no manual binding needed |
| `mes(player, "text")` | `mes("text")` inside ProtectedAccess handler — no standalone `mes` exists |
| `private object X : NpcReferences()` | Must be `internal` — `private` TypeReferences crash startup with IllegalStateException |
| `private object X : ObjReferences()` | Same — any TypeReferences subclass must be `internal` or `public` |
| `private object X : ComponentReferences()` | Same rule |
| `find("name", fallbackId)` when sym entry exists | Use `find("name")` only — fallbackId is treated as supposedHash and fails check |
| Interface IDs > 924 in `.local/interface.sym` | Rev233 cache has max 924 interfaces — verify with `search_iftypes` first |
| Duplicate NpcEditor entries for same NPC type in two modules | Causes infinite CacheUpdateRequired loop at startup — one editor owns each NPC |
| `player.statMap.getFineXP(Int)` | Takes `StatType`, not `Int` |
| `player.inv.add(...)` | Use `invAdd(invs.inv, objs.item, count)` in ProtectedAccess |
| `invAdd(invs.inv, ...)` without checking `.success` | MUST check `.success` or use `invAddOrDrop` to prevent silent item deletion on full inventory |
| `println("test")` | Use SLF4J `private val logger = InlineLogger()` — raw println spam crashes the live console |
| `Thread.sleep(1000)` | BANNED — freezes the entire server engine. Use RSMod's suspend function `delay(ticks)` |
| `object MyState { var x = 0 }` | BANNED — Global mutable state breaks in multiplayer. Store state on the `Player` via varps/attributes |
| `player.magicLevel` | Use `magicLvl` inside ProtectedAccess |
| `String` placeholders for `ObjType`/`StatType` | Always use real type references: `objs.x`, `stats.x` |
| Duplicate `onOpNpc1(npcs.x)` in two scripts | Entire second script silently disabled at startup. Grep first: `grep -rn "onOpNpc1(npcs.x" rsmod/content --include="*.kt"` |
| `player.mes("text")` in ProtectedAccess handler | Use `mes("text")` directly — `player.mes()` only works in Player extension contexts (e.g. AdminCommands) |

### Sym entry rules

1. Check `.data/symbols/<type>.sym` before calling `find("name")`.
2. If the name isn't there, add to `.data/symbols/.local/<type>.sym` with the real cache ID.
3. **Look up real IDs** with `search_objtypes` / `search_npctypes` / `search_loctypes` on the `osrs-cache` MCP. Never guess.
4. `find("name")` with no fallback is always correct once the sym entry exists.

---

## Delivery Methodology (Mandatory)

Use a dual-track model at all times:

1. **Track A — Content Throughput**
   - quests, skills, areas, world interactions
2. **Track B — Core Systems & Parity**
   - combat behavior, net/protocol edge parity, engine/system dependencies

### Gating Rule

If a content task depends on unresolved core systems, do **not** mark it fully complete.
Keep it `🟡 partial` (or in-progress) and note the blocking task IDs.

### Source-of-Truth Order (when docs conflict)

1. Task registry (`agent-tasks`) for execution state
2. `README.md` for definitive startup/workflow playbook
3. `AGENTS.md` for ownership/blockers/role scopes
4. `docs/NEXT_STEPS.md` for active sequencing
5. `docs/CONTENT_AUDIT.md` for feature status
6. `docs/MASTER_ROADMAP.md` for long-horizon scope

---

## What Is This Project?

We are building a **full OSRS private server** using **RSMod v2** (Kotlin/Java 21, Gradle), targeting **Revision 233 — Varlamore Part 2 (September 25, 2024)**. The goal is 1:1 parity with vanilla OSRS at that revision: every skill, quest, NPC, boss, area, and mechanic.

The server is **already running** at port 43594. The AgentBridge is at port 43595 for bot testing.

---

## Directory Layout

```
Z:\Projects\OSRS-PS-DEV\
├── rsmod/                    ← OUR CODEBASE (Kotlin/Java 21, Gradle)
│   ├── api/                  ← Framework APIs (quest, combat, shops, skills, etc.)
│   │   ├── config/refs/      ← BaseVarps, BaseNpcs, BaseObjs, BaseSeqs, BaseContent, etc.
│   │   └── quest/            ← Quest.kt, QuestScript.kt, QuestList.kt, QuestExtensions.kt
│   ├── content/
│   │   ├── skills/           ← Woodcutting, Fishing, Mining, Cooking, Smithing, etc.
│   │   ├── areas/city/       ← lumbridge/, (draynor/, varrock/ etc. to be created)
│   │   ├── quests/           ← Quest content scripts (to be created)
│   │   ├── generic/          ← Sheep, doors, ladders, generic locs/NPCs
│   │   └── other/npc-drops/  ← Drop table plugin
│   └── .data/symbols/        ← CRITICAL: sym files for all cache IDs
│       ├── obj.sym            ← Item sym names
│       ├── npc.sym            ← NPC sym names
│       ├── loc.sym            ← Location/object sym names
│       ├── seq.sym            ← Animation sym names
│       ├── varp.sym           ← Varp sym names
│       └── .local/            ← Local overrides (add aliases here)
├── alter/                    ← RSMod v1 content donor (Kotlin — port from here first)
├── Kronos-184-Fixed/         ← Older Java RSPS (rev 184 — fallback for NPC stats/drops)
├── docs/
│   ├── MASTER_ROADMAP.md     ← COMPLETE feature list: what's done, what's not
│   ├── NEXT_STEPS.md         ← Active sprint planning and task sharding assignments
│   ├── CONTENT_AUDIT.md      ← Skill/system pass/fail status
│   ├── TRANSLATION_CHEATSHEET.md ← v1→v2 API mapping table
│   ├── CORE_SYSTEMS_GUIDE.md ← Food/potions/prayer/make-X/shops — how to implement each
│   ├── LEGACY_IMPLEMENTATION_PLAYBOOK.md ← How to extract content from Kronos/Alter/wiki-data safely
│   └── SYM_NAMING_GUIDE.md   ← 80+ known sym name quirks
├── mcp/                      ← MCP TypeScript server (game control)
└── bots/                     ← Bot test scripts (TypeScript)
```

---

## How RSMod v2 Plugins Work

Every piece of content is a **PluginScript**. There is one per NPC/mechanic/skill.

```kotlin
class MyNpc @Inject constructor(/* optional injected deps */) : PluginScript() {
    override fun ScriptContext.startup() {
        // Register event handlers here
        onOpNpc1(npcs.my_npc) { myDialogue(it.npc) }
        onOpNpc2(npcs.my_npc) { anotherAction(it.npc) }
        onOpLoc1(locs.my_object) { interactWithObject() }
    }

    private suspend fun ProtectedAccess.myDialogue(npc: Npc) {
        startDialogue(npc) {
            chatNpc(neutral, "Hello, adventurer!")
            chatPlayer(happy, "Hello!")
        }
    }
}
```

**Key rules:**
- Class name = filename (Kotlin convention)
- `@Inject constructor(...)` for Guice DI — only inject what you need
- Handlers are `suspend` — you can `delay(ticks)` directly, no queue wrapper
- `ProtectedAccess` is the player's scripting context — all player APIs live here
- Modules are **auto-discovered** — just add a `build.gradle.kts` to your module dir

---

## Creating a New Module

Every new module needs exactly three things:

### 1. Directory structure
```
rsmod/content/<category>/<module-name>/
└── src/main/kotlin/org/rsmod/content/<category>/<modulename>/
    ├── configs/       ← Local NPC/Loc/Obj references (optional)
    └── MyScript.kt    ← The PluginScript
```

**Category examples:** `skills/`, `quests/`, `areas/city/`, `generic/`

### 2. build.gradle.kts
```kotlin
plugins {
    id("base-conventions")
}

dependencies {
    implementation(projects.api.pluginCommons)
}
```

That is the **minimum**. The module is auto-discovered by Gradle via directory walk — no other registration needed.

### 3. The PluginScript class

See `rsmod/content/areas/city/lumbridge/npcs/Hans.kt` for a simple NPC example.
See `rsmod/content/skills/woodcutting/` for a full skill example.
See `rsmod/content/areas/city/lumbridge/npcs/FredTheFarmer.kt` for a quest NPC example.

---

## Event Handlers Reference

| Event | Usage | When to use |
|-------|-------|-------------|
| `onOpNpc1(npc)` | Player left-clicks NPC | Talk-to |
| `onOpNpc2(npc)` | Player option 2 on NPC | Attack, Trade |
| `onOpNpcU(npc, obj)` | Player uses item on NPC | Use item on NPC |
| `onOpLoc1(loc)` | Player left-clicks object | Open door, climb ladder |
| `onOpLoc2(loc)` | Player option 2 on object | Search, pickpocket |
| `onOpLoc3(loc)` | Player option 3 on object | Examine |
| `onOpLocU(loc, obj)` | Player uses item on object | Use tinderbox on logs |
| `onOpObj1(obj)` | Player uses inventory item | Eat food, drink potion |
| `onNpcHit(npc)` | NPC takes damage | Combat retaliation |

**Content groups** (match multiple types at once):
```kotlin
onOpLoc1(content.tree)        // all tree variants
onOpNpc1(content.banker)      // all banker NPCs
onOpLocU(content.fire, content.raw_food)  // any raw food on any fire
```

---

## Player APIs (ProtectedAccess)

```kotlin
// XP
statAdvance(stats.woodcutting, 25.0)

// Inventory
invAdd(invs.inv, objs.logs, 1)          // add item (fails silently if full)
invDel(invs.inv, objs.coins, 60)        // remove item
invAddOrDrop(objRepo, objs.logs)        // add or drop on ground if full
inv.count(objs.ball_of_wool)            // count items in inventory
inv.remove(objs.ball_of_wool, 20)       // remove items, returns result

// Animation
anim(seqs.human_woodcutting_axe_rune)   // play animation — use seqs.* NOT int IDs

// Messages
mes("You chop down the tree.")          // chatbox message
npc.say("Ouch!")                        // NPC overhead text

// Delays (inside suspend handlers)
delay(3)                                // wait 3 game ticks (1.8 seconds)

// Level checks
woodcuttingLvl                          // player's current woodcutting level (with boosts)
player.skills.woodcutting               // base (unboosted) level
```

---

## Dialogue System

```kotlin
private suspend fun ProtectedAccess.talkToNpc(npc: Npc) =
    startDialogue(npc) {
        chatNpc(neutral, "Hello there!")
        chatPlayer(happy, "Hello!")

        val choice = choice3(
            "Tell me about the quest.", 1,
            "What is this place?",     2,
            "Goodbye.",                3,
        )
        when (choice) {
            1 -> questBranch()
            2 -> locationBranch()
            3 -> chatPlayer(neutral, "Bye.")
        }
    }
```

**Emotions:** `neutral`, `happy`, `sad`, `angry`, `confused`, `quiz`, `laugh`, `shifty`, `scared`

**Choice functions:** `choice2(...)`, `choice3(...)`, `choice4(...)`, `choice5(...)`

Each choice takes pairs of `"Label text", value` — the returned `Int` is the value.

---

## Quest Engine

The quest engine is fully implemented at `rsmod/api/quest/`.

### Step 1 — Add to QuestList.kt
```kotlin
// rsmod/api/quest/QuestList.kt
val my_quest = Quest(
    id = 11,
    name = "My Quest",
    varp = BaseVarps.my_varp,
    maxStage = 2,   // 0=not started, 1=in progress, 2=complete
    rewards = questRewards {
        xp(BaseStats.cooking, 300)
        item(objs.coins, 100)
        extra("1 Quest Point")
    }
)
```

### Step 2 — NPC script uses stage checks
```kotlin
class QuestNpc : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(npcs.my_npc) { doDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.doDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.my_quest)) {
                0 -> offerQuest()
                1 -> checkProgress()
                2 -> alreadyDone()
            }
        }

    private suspend fun Dialogue.offerQuest() {
        chatNpc(neutral, "I need your help.")
        setQuestStage(QuestList.my_quest, 1)     // accept quest
    }

    private suspend fun Dialogue.checkProgress() {
        // check items, advance stage, give rewards
        setQuestStage(QuestList.my_quest, 2)
        showCompletionScroll(
            quest = QuestList.my_quest,
            rewards = listOf("300 Cooking XP", "100 coins", "1 Quest Point"),
            itemModel = objs.coins,
            questPoints = 1
        )
    }
}
```

**Quest imports:**
```kotlin
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
```

---

## Local NPC/Loc/Obj References

When your module has its own NPCs, define a local references object:

```kotlin
// configs/MyModuleNpcs.kt
typealias my_module_npcs = MyModuleNpcs

object MyModuleNpcs : NpcReferences() {
    val quest_giver = find("npc_sym_name")
    val guard       = find("guard_sym_name")
}
```

Get the sym name and hash from MCP tools (see below). Then use `my_module_npcs.quest_giver` in your scripts.

---

## Data Lookups — MCP Tools

**Five MCP servers are available.** Use the right one:

| Need | Tool | Server |
|------|------|--------|
| NPC/item wiki pages and combat/drop context | `osrs_wiki_search` + `osrs_wiki_parse_page` | `osrs-cache` |
| Item sym name + ID | `search_objtypes` | `osrs-cache` |
| NPC sym name + ID | `search_npctypes` | `osrs-cache` |
| Animation sym name + ID | `search_seqtypes` | `osrs-cache` |
| Location/object sym name | `search_loctypes` | `osrs-cache` |
| Varp sym name | `search_varptypes` | `osrs-cache` |
| Full wiki page (drop tables) | `osrs_wiki_parse_page` | `osrs-cache` |
| Rev 233 symbol truth | `.sym` files under `rsmod/.data/symbols/` | local repo |
| RSMod API docs | `resolve-library-id` + `query-docs` | `context7` |

### Example lookups:
```javascript
// Get NPC wiki page context (combat, drops, locations)
osrs_wiki_parse_page({ page: "Goblin" })

// Get NPC sym name and hash for find()
search_npctypes({ query: "fred_the_farmer", pageSize: 5 })
// → id: 3313, name: "fred_the_farmer"

// Get animation ID
search_seqtypes({ query: "goblin attack", pageSize: 5 })
// → id: 6184, name: "goblin_attack"
```

---

## CRITICAL: Sym File Naming

**RSMod sym files use OLD internal cache names — NOT modern OSRS wiki names.**

This is the #1 cause of build failures. Always verify before using `find("name")`.

| Wiki name | Sym name | Notes |
|-----------|----------|-------|
| Small fishing net | `net` | |
| Grimy guam leaf | `unidentified_guam` | |
| Earth rune | `earthrune` | no underscore |
| Bowstring | `bow_string` | |
| Ball of wool | `ball_of_wool` | matches wiki |
| Coins | `coins` | matches wiki |

**How to verify:**
1. Use `search_objtypes({ query: "fishing net" })` — look at the `name` field returned
2. Or check `.data/symbols/obj.sym` directly with `grep "net" .data/symbols/obj.sym`
3. If the sym name isn't in the file, add it to `.data/symbols/.local/obj.sym`

**TRAP — never do this:**
```kotlin
// BAD: fallbackId causes hash mismatch if ID exists in cache
val my_item = find("some_name", 12345)

// GOOD: once name is in sym, just use find()
val my_item = find("some_name")
```

See `docs/SYM_NAMING_GUIDE.md` for 80+ confirmed sym name mappings.

---

## Module Ownership Rules

Each agent must own exactly one module directory. **Never edit shared files in parallel:**

| File | Rule |
|------|------|
| `rsmod/api/config/refs/BaseVarps.kt` | Pre-population pass only — sequential |
| `rsmod/api/config/refs/BaseNpcs.kt` | Pre-population pass only — sequential |
| `rsmod/api/config/refs/BaseSeqs.kt` | Pre-population pass only — sequential |
| `rsmod/api/quest/QuestList.kt` | Pre-population pass only — sequential |
| Your own module directory | Freely edit |
| `.data/symbols/.local/*.sym` | One agent at a time |

If you need a new entry in a shared file that doesn't exist yet, **stop and report it** rather than adding it yourself if another agent might be running.

---

## Build Verification

**Always build before declaring your work done.**

Use the `build_server` MCP tool:
```javascript
build_server({ module: "my-module-name" })
// → returns build output + pass/fail
```

Or build the full server:
```javascript
build_server({})
```

If the build fails, fix the error — do not declare the task complete with a broken build.

---

## Template Files (copy these)

| Template | Path | Use for |
|----------|------|---------|
| Simple skill | `rsmod/content/skills/woodcutting/` | Gathering/processing skill |
| NPC dialogue | `rsmod/content/areas/city/lumbridge/npcs/Hans.kt` | Simple NPC dialogue |
| Quest NPC | `rsmod/content/areas/city/lumbridge/npcs/FredTheFarmer.kt` | Quest dialogue + stage tracking |
| NPC configs | `rsmod/content/areas/city/lumbridge/configs/LumbridgeNpcs.kt` | Local NPC ref objects |
| Drop table | `rsmod/content/other/npc-drops/src/.../NpcDropTablesScript.kt` | NPC drops |
| Area module | `rsmod/content/areas/city/lumbridge/` | City/area module structure |

---

## MANDATORY: Before Writing Any Code

Do these three steps before writing a single line of Kotlin:

### Step 1 — Verify every sym name before using it

Never invent or guess a sym name. Every `find("name")` must be verified first.

```bash
# Check if a name exists — use the right sym file for the type
grep -i "keyword" rsmod/.data/symbols/loc.sym   | head -10
grep -i "keyword" rsmod/.data/symbols/obj.sym   | head -10
grep -i "keyword" rsmod/.data/symbols/npc.sym   | head -10
grep -i "keyword" rsmod/.data/symbols/seq.sym   | head -10
grep -i "keyword" rsmod/.data/symbols/varp.sym  | head -10
grep -i "keyword" rsmod/.data/symbols/varbit.sym | head -10

# Or use MCP cache tools (faster)
search_loctypes({ query: "gate taverley", pageSize: 10 })
search_objtypes({ query: "fishing net", pageSize: 5 })
search_npctypes({ query: "fred farmer", pageSize: 5 })
```

If the name is not found:
- **Option A** — use the real cache name (often different from wiki; see `docs/SYM_NAMING_GUIDE.md`)
- **Option B** — add a custom entry to `.data/symbols/.local/<type>.sym`

**Do NOT write `find("invented_name")` and add a TODO.** It creates an unresolved reference
that breaks strict verification at runtime even if it compiles.

### Step 2 — Read an existing file before using any engine class

Before calling `.level`, `.type`, `.coords`, `.plane`, or any property you're not 100% sure about — **grep for existing usages** in working content files.

```bash
# What properties does CoordGrid have?
grep -rn "\.level\b\|\.x\b\|\.z\b\|player\.coords" rsmod/content --include="*.kt" | grep -v /build/ | head -5

# How is BoundLocInfo used with locRepo?
grep -rn "locRepo\.change" rsmod/content --include="*.kt" | grep -v /build/ | head -5

# What does an event handler's `this` context expose?
grep -rn "fun ProtectedAccess\." rsmod/api/player/src --include="*.kt" | head -20
```

**If you can't find a working example, the API probably doesn't exist the way you think.**

### Step 3 — Build before marking done

```javascript
build_server({ module: "my-module-name" })
```

Zero tolerance for broken builds. Fix every compile error before `complete_task`.

---

## Common Mistakes

### 1. Invented sym names — #1 build-breaker

```kotlin
// WRONG — invented name not in loc.sym
val taverley_gate = find("members_gate_taverley")

// WRONG — wiki display name, not cache name
val net = find("small_fishing_net")

// CORRECT — verified via grep or search_loctypes / search_objtypes
val taverley_gate = find("members_gate")   // whatever the sym file actually contains
val net           = find("net")            // confirmed in obj.sym
```

**Rule:** `grep "your_name" rsmod/.data/symbols/loc.sym` returning nothing = wrong name.
See `docs/SYM_NAMING_GUIDE.md` for 80+ confirmed mappings.

### 2. `CoordGrid.plane` does not exist — use `.level`

```kotlin
val current = player.coords

current.plane + 1   // WRONG — no such property
current.level + 1   // CORRECT

teleport(CoordGrid(current.x, current.z, current.level + 1))
```

CoordGrid properties: **`x`**, **`z`**, **`level`** — that is all.

### 3. `player.chatPlayer()` does not compile

```kotlin
// WRONG — chatPlayer is NOT an extension on Player
player.chatPlayer("You can't go there.")

// WRONG — chatPlayer(String) overload does not exist
chatPlayer("You can't go there.")

// CORRECT — plain chatbox message, available everywhere in ProtectedAccess
mes("You can't go there.")

// CORRECT — dialogue chat-head, ONLY inside startDialogue { } blocks
startDialogue(npc) {
    chatNpc(neutral, "Hello!")
    chatPlayer(happy, "Hi!")
}
```

`mes("text")` is the general-purpose message. `chatPlayer`/`chatNpc` are dialogue-only.

### 4. `BoundLocInfo.type` does not exist

```kotlin
// WRONG — BoundLocInfo has no .type property
locRepo.change(loc, loc.type, 50)

// CORRECT — pass a LocType from your LocReferences object
locRepo.change(loc, my_locs.door_open, 50)
```

`BoundLocInfo` actual fields: `.coords`, `.entity`, `.layer`, `.width`, `.length`.
`.entity.id` gives the raw loc ID if you need it.
If you need an "open" state loc, define it as a separate entry in your `LocReferences` object.

### 5. Inventing varbit/varp names for custom data

```kotlin
// WRONG — slayer_blocked_count is not in varbit.sym
private var Player.slayerBlocked by intVarBit(slayer_varbits.blocked_count)
```

If a name is not in `varbit.sym`/`varp.sym`:
- **Option A** — use an existing canonical sym entry (`grep -i "slayer" rsmod/.data/symbols/varbit.sym`)
- **Option B** — add `build("my_var_name") { ... }` to `VarBitBuilds.kt`/`VarpBuilds.kt` AND add
  the ID to `.data/symbols/.local/varbit.sym`

See `rsmod/api/config/src/main/kotlin/org/rsmod/api/config/builders/VarBitBuilds.kt` for the pattern.

### 6. Using int IDs for animations

```kotlin
anim(879)                              // WRONG — hard-coded ID
anim(seqs.human_woodcutting_axe_rune)  // CORRECT
```

### 7. Wrapping suspend handlers in queue

```kotlin
onOpNpc1(npcs.goblin) { player.queue { doThing() } }  // WRONG
onOpNpc1(npcs.goblin) { doThing() }                    // CORRECT
```

### 8. Inventory add when full

```kotlin
invAdd(invs.inv, objs.logs, 1)     // silently fails if inventory full
invAddOrDrop(objRepo, objs.logs)   // drops on ground if full (preferred)
```

### 9. Using find() with fallback ID for existing cache entries

```kotlin
val goblin = find("goblin", 99999)  // WRONG — hash mismatch if ID is in cache
val goblin = find("goblin")          // CORRECT once sym entry exists
```

---

## Quick Reference

### Package structure for a quest at `rsmod/content/quests/cooks-assistant/`
```
package org.rsmod.content.quests.cooksassistant
```

### Dialogue emotions
```kotlin
neutral, happy, sad, angry, confused, quiz, laugh, shifty, scared, calm
```

### Key type ref objects
```kotlin
stats.*    // woodcutting, mining, cooking, etc.
objs.*     // items
locs.*     // world objects/locations
npcs.*     // NPCs
seqs.*     // animations
synths.*   // sounds
params.*   // cache params
content.*  // content groups
varps.*    // player varps
invs.*     // inventory slots (inv, bank, equipment)
```

---

## Area / City Module Guide

City content lives at `rsmod/content/areas/city/<cityname>/`. Use Lumbridge as the reference implementation.

### Directory structure
```
rsmod/content/areas/city/draynor/
├── build.gradle.kts
└── src/main/
    ├── kotlin/org/rsmod/content/areas/city/draynor/
    │   ├── configs/
    │   │   ├── DraynorNpcs.kt        ← NpcReferences + NpcEditor
    │   │   └── DraynorLocs.kt        ← LocReferences (if needed)
    │   ├── map/
    │   │   └── DraynorNpcSpawns.kt   ← MapNpcSpawnBuilder (reads npcs.toml)
    │   ├── npcs/
    │   │   ├── Banker.kt             ← one file per NPC or small group
    │   │   └── Morgan.kt
    │   └── DraynorScript.kt          ← world loc interactions
    └── resources/org/rsmod/content/areas/city/draynor/
        └── npcs.toml                 ← NPC spawn coords
```

### NPC spawn toml format
```toml
[[spawn]]
npc = 'hans'
coords = '0_50_50_7_33'   # level_regionX_regionZ_localX_localZ

[[spawn]]
npc = 'banker'
coords = '0_48_50_10_16'
```

Spawn coordinates use the format `level_rx_rz_lx_lz` where rx/rz are region coords and lx/lz (0–63) are local tile offsets within the region. Use the OSRS wiki map viewer or `search_loctypes` to find coordinates.

### MapNpcSpawnBuilder
```kotlin
// map/DraynorNpcSpawns.kt
object DraynorNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<DraynorScript>("npcs.toml")
    }
}
```

### NpcReferences + NpcEditor pattern
```kotlin
// configs/DraynorNpcs.kt
typealias draynor_npcs = DraynorNpcs

object DraynorNpcs : NpcReferences() {
    val banker = find("deadman_banker_blue_south")
    val morgan  = find("morgan")
}

internal object DraynorNpcEditor : NpcEditor() {
    init {
        edit(draynor_npcs.banker) { contentGroup = content.banker }
        edit(draynor_npcs.morgan) { moveRestrict = indoors; wanderRange = 0 }
    }
}
```

**NpcEditor fields** (set in `edit { ... }`):
| Field | Type | Notes |
|-------|------|-------|
| `contentGroup` | `ContentGroupType` | `content.banker`, `content.banker_tutor`, etc. |
| `moveRestrict` | `MoveRestrict` | `indoors` prevents NPC leaving building |
| `wanderRange` | `Int` | 0 = stationary, >0 = wander radius in tiles |
| `respawnDir` | `Direction` | `north`, `south`, `east`, `west` |
| `defaultMode` | `NpcMode` | `patrol` for patrol-path NPCs |
| `patrol1..10` | `NpcPatrolWaypoint` | `patrol(CoordGrid(...), waitTicks)` |
| `maxRange` | `Int` | Max distance NPC can wander from spawn |
| `timer` | `Int` | Tick interval for timer-based NPC behaviour |

### Area implementation checklist
For each city, implement in this order:
1. `build.gradle.kts` (just `base-conventions` + `api.pluginCommons`)
2. `configs/CityNpcs.kt` — NpcReferences + NpcEditor for all key NPCs
3. `map/CityNpcSpawns.kt` + `resources/.../npcs.toml` — spawn all NPCs
4. `npcs/Banker.kt` etc. — dialogue scripts (one file per NPC or group)
5. `CityScript.kt` — world loc interactions (furnace, spinning wheel, etc.)
6. `build_server({ module: "draynor" })` — must pass before marking done

**Always add banker NPCs with `contentGroup = content.banker`** so the bank interface opens automatically.

---

## NPC Combat Stats Guide

Combat stats are set via `NpcEditor` in the area or content module's configs file.

### Setting combat stats
```kotlin
internal object MyAreaNpcEditor : NpcEditor() {
    init {
        edit(my_npcs.goblin) {
            hitpoints  = 5       // max HP
            attack     = 1       // attack level
            strength   = 1       // strength level
            defence    = 1       // defence level
            ranged     = 1       // ranged level (1 if melee-only)
            magic      = 1       // magic level (1 if melee-only)
            attackRange = 1      // 1 = melee, >1 = ranged/magic
            respawnRate = 50     // ticks before respawn (30 ticks = ~18 sec)
        }
    }
}
```

**Data source for stats:** Prefer `osrs_wiki_parse_page` + rev 233 `.sym` verification (`rsmod/.data/symbols/`) over legacy codebases.

### Aggression (hunt)
```kotlin
edit(my_npcs.goblin) {
    huntRange = 5             // aggro radius in tiles
    huntMode  = someHuntMode  // check existing usages in HuntModeType
    giveChase = true          // chases player when they flee
}
```

NPCs with `huntRange > 0` will aggro players within that radius. The aggression system (MECH-1) handles the de-aggro timer and combat level exceptions.

### On-hit handlers (custom behaviour)
```kotlin
// Only needed for NPCs that do something special on hit
// Retaliation is automatic when combat stats are defined
onNpcHit(my_npcs.goblin) {
    // e.g. call for help, apply special effect
}
```

### Drop tables
Register in `NpcDropTablesScript.kt` using the `dropTable { }` DSL:
```kotlin
private fun registerGoblin() {
    val table = dropTable {
        always(objs.bones)
        table("Loot", weight = 1) {
            nothing(weight = 75)
            item(objs.coins, quantity = 1..5, weight = 10)
        }
    }
    registry.register(my_npcs.goblin, table)
}
```

See `content/other/npc-drops/NpcDropTablesScript.kt` for full examples.

---

## What To Read — Priority Order

**Read these in order on every new session before touching code:**

### Tier 1 — Always read first (required)
| Doc | Why |
|-----|-----|
| `README.md` | Definitive startup/workflow playbook |
| `START_HERE.md` (this file) | Implementation examples and API patterns |
| `docs/NEXT_STEPS.md` | Your shard assignment — what task to pick up |
| `docs/SYM_NAMING_GUIDE.md` | 80+ sym name quirks — consult before every `find()` call |

### Tier 2 — Read before implementing anything
| Doc | Why |
|-----|-----|
| `docs/CORE_SYSTEMS_GUIDE.md` | Exact API + templates for food/potions/prayer/make-X/shops |
| `docs/TRANSLATION_CHEATSHEET.md` | Full v1→v2 API mapping (especially Section 13: NpcEditor) |
| `docs/LEGACY_IMPLEMENTATION_PLAYBOOK.md` | How to extract content from Kronos/Alter/wiki-data correctly |

### Tier 3 — Reference (look up as needed)
| Doc | When |
|-----|------|
| `docs/MASTER_ROADMAP.md` | See what's done vs. what needs building |
| `docs/OSRS_MECHANICS_REFERENCE.md` | Wiki-accurate formulas for combat, prayer, run energy, etc. |
| `docs/CONTENT_AUDIT.md` | Current pass/fail status per system |
| `docs/CODEBASE_AUDIT_2026-02-26.md` | Known issues, dead code, agent failure patterns, doc gap analysis |
| `docs/DOC_AUTHORITY.md` | Which docs are authoritative vs archived — check before acting on old docs |

---

## Server Status

```javascript
// Check if server is up
server_status()

// Build a specific module
build_server({ module: "my-module" })

// Run a test bot
execute_script({ player: "TestBot", code: "..." })
```

Server runs on port **43594** (game), AgentBridge on **43595**.

---

## Known Tech Debt

These modules are **intentionally stubbed** and need proper implementation when their dependencies are ready. Do NOT add functionality to them until the blocking system exists.

| Module | File | What's stubbed | Blocked by |
|--------|------|---------------|------------|
| Random Events | `content/mechanics/random-events/scripts/RandomEventScript.kt` | Entire script — empty PluginScript | Needs full item/XP reward API design |
| Enchant Spells | `content/skills/magic/enchant/scripts/EnchantSpells.kt` | Entire script — empty PluginScript | Needs enchant item → output mapping, jewellery types |
| Thessalia Makeover | `content/areas/city/varrock/npcs/Thessalia.kt` | `player_kit_tailor_*` interface calls stubbed | Interfaces don't exist in rev233 cache |
| Haig Halen Kudos check | `content/mechanics/achievement-diaries/hooks/DiaryTaskHooks.kt` | Task 11 (Speak with 50+ Kudos) commented out | Needs Kudos varp wired |

---

## Agent Learnings (BUILD-CRIT-13)

These are real mistakes made by AI agents and the exact fixes, so no agent repeats them.

### 1. `bindScript<T>()` does not exist
Agents wrote `bindScript<RandomEventScript>()` in `PluginModule.bind()`. This method does not exist on `PluginModule`. RSMod discovers all `PluginScript` subclasses via ClassGraph classpath scan — no manual binding needed. `PluginModule.bind()` is only for Guice bindings (e.g., `addSetBinding`).

**Fix:** Delete `bindScript` calls. Leave `bind()` empty or add only real Guice bindings.

### 2. `private` TypeReferences subclasses crash at startup
`private object X : NpcReferences()` compiles fine but throws `IllegalStateException: TypeReferences subclasses must not be marked as private` at startup. The `TypeReferencesLoader` reflection pass rejects private visibility.

**Fix:** Always use `internal object X : NpcReferences()`.

### 3. String placeholders used as `ObjType`/`StatType`
Agents wrote `val x = "bronze_bar"` as a placeholder for `ObjType`. This compiles when the field is typed as `String`, but breaks every function expecting `ObjType`. Similarly `val magic = "magic"` shadowed the real `stats.magic` `StatType`.

**Fix:** Always use real type refs. If a sym entry is missing, add it to `.local/` sym before using `find()`.

### 4. Duplicate NpcEditor entries cause infinite pack loop
Two modules (`EdgevilleNpcEditor`, `WildernessF2PNpcEditor`) both edited the same 3 hill giant NPC types with different `wanderRange` values. The type verifier sees one set as packed by the other as unpacked, requiring a new pack on every startup. Infinite loop.

**Fix:** One editor module owns each NPC type. Remove the duplicate edits from the second module.

### 5. Interface IDs that don't exist in cache
Adding sym entries for interface IDs > 924 (rev233 max) causes `references point to cache types that do not exist` at startup. The `player_kit_tailor_*` interfaces were at IDs 1000-1003 which are beyond the cache.

**Fix:** Always verify interface IDs with `search_iftypes` on `osrs-cache` MCP before adding to sym or `BaseInterfaces.kt`.

### 6. Duplicate event registration silently disables entire scripts
If two scripts both call `onOpNpc1(npcs.x)` for the same NPC, or `onOpLocU(obj1, obj2)` for the same item combo, the **second** script to load is **entirely disabled** at startup with a WARN log. The server does not crash — it just silently drops all 50+ handlers in that script. This has already killed 16 scripts including all quests, all crafting, prayer, and drop tables.

**Fix:** Before registering any handler, grep the codebase for the NPC/loc/obj name to verify no other script already handles it. NPC scripts and quest scripts frequently conflict — a quest that talks to NPC X must own that NPC's Op1, not share it with a separate area script.

### 7. `internalId must not be null` silently disables the whole script
If a script calls `find("name")` and the name isn't in any `.sym` file, the returned type has `internalId=null`. When that type is used as an event key (e.g. `onOpLoc1(locs.my_loc)`), the null ID causes the **entire script** to be disabled at startup — not just that one handler.

**Fix:** Before using any `find("name")`, verify the name exists in `.data/symbols/<type>.sym` or `.data/symbols/.local/<type>.sym`. Use `search_objtypes`/`search_npctypes`/`search_loctypes` to look up the real ID and add it.

### 8. Always check startup WARNs — the server does not crash on broken scripts
The server starts successfully even when 23 scripts are silently disabled. There is no build failure, no exception, no obvious signal. You **must** check the startup log for `Skipping script startup for` lines after every server run. If you see any, treat them as build failures.

**Fix:** After `./gradlew :server:app:run`, grep the output for `Skipping script startup`. Zero lines = clean. Any lines = broken scripts that need fixing before `complete_task()`.

### 9. Idiomatic Inventory Transactions
Agents wrote `if (player.inv.freeSpace() >= 1) { player.invAdd(...) }`. This is non-idiomatic and prone to errors. RSMod transactions return a result that should be checked directly.

**Fix:** Use `if (player.invAdd(player.inv, objs.item, count).success)` or `invAddOrDrop(objRepo, objs.item, count)`.

### 10. `mes(player, "text")` vs `mes("text")`
Agents tried to use `mes(player, "text")` which often doesn't exist or is the wrong API. Inside `ProtectedAccess` (which all `PluginScript` handlers are), use the extension method directly.

**Fix:** Use `mes("Your message here")`.

### 11. BANNED: Global Mutable State in `object`
Agents wrote `object MyState { var x = 0 }`. This variable is shared across all 2,000+ players on the server. If one player changes it, it changes for everyone.

**Fix:** Store state on the `Player` entity using `varp` or `varbit` delegates (e.g., `private var Player.questStage by intVarp(varps.my_quest)`).

### 12. The "Blankobject Trap": Duplicate dummy mapping crashes boot
Agents mapped multiple missing functional symbols (e.g. `dharoks_helm` and `ahrims_hood`) to the same ID (`blankobject`). RSMod's EnumBuilders and ObjEditors use these symbols as map keys. When multiple keys point to the same ID, the server crashes at boot with a duplicate key error.

**Fix:** NEVER map more than one functional symbol to a dummy ID if it is used in an Enum or Editor. Always grep `rsmod/.data/symbols/*.sym` for the real name first. Every added symbol must be unique unless it's a non-functional placeholder.

### 13. Shallow Verification vs. Full Boot Gate
Compilation of `server:app` is NOT a success signal. Sub-modules can still be broken, and many logic errors (duplicate keys, null IDs) only manifest when the server actually starts running the builders.

**Fix:** A task is ONLY complete when the server boots to the `[MainGameProcess] World is live` state.



