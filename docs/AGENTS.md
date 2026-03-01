# AGENTS.md — Multi-Agent Coordination Handbook

**Read this entire file before starting any work.**
Update the ownership table and blockers section when you start or finish a task.

Operational note:
- `README.md` is the definitive startup/workflow playbook.
- This file is authoritative for module ownership, blockers, role scopes, and DoD details.

Startup contract:
- Complete the **Start Here (Hard Gate)** checklist in `docs/README.md` before any edits.
- If assignment fields are missing (`task id`, `allowed/forbidden paths`, `insertion point`, `validation command`), return a clarification request and perform zero edits.
- See: `docs/README.md#start-here-hard-gate`.

---

## Key Docs — Read These First

| Doc                              | What it covers                                                                |
| -------------------------------- | ----------------------------------------------------------------------------- |
| `docs/DOC_AUTHORITY.md`          | Canonical doc precedence + per-agent primary docs                             |
| `docs/CONTENT_AUDIT.md`          | Full status of every skill, system, and area — check before starting anything |
| `docs/NEXT_STEPS.md`             | Current priorities and the implement+test workflow                            |
| `docs/REV_LOCK_POLICY.md`        | Revision authority + non-negotiable rev 233 guardrails                        |
| `docs/TRANSLATION_CHEATSHEET.md` | Complete Alter v1 → RSMod v2 API mapping                                      |
| `docs/LEGACY_IMPLEMENTATION_PLAYBOOK.md` | How to safely use 317/2004scape/older rev implementations for rev 233 work |
| `docs/LLM_TESTING_GUIDE.md`      | AgentBridge state schema, bot test methodology, MCP tools                     |
| `docs/RUNESERVER_NOTES.md`       | Best practices from RuneServer research (fill this in, Kimi)                  |
| `docs/NPC_DATA_TOOLS.md`         | Complete guide to NPC/drop table tools (symbol indexer, npc_lookup, batch processor) |
| `rsmod/.data/symbols/`           | Rev 233 symbol tables — ground truth for all IDs in Kotlin code               |
| `tools/`                         | Python tools for NPC data extraction and drop table generation                |

---

## Revision Source (Canonical)

RSMod v2 is locked to **revision 233**, sourced from **OpenRS2 cache `runescape/2293`**:
- https://archive.openrs2.org/caches/runescape/2293
- Build major: `233`
- Built at: `2025-09-10T16:47:47Z`

When dates conflict across docs, treat this source tuple (`rev 233` + `runescape/2293`) as authoritative.

---

## Operating Model — Dual Track Delivery

Run work in two concurrent tracks:

1. **Track A: Content Throughput**
   - quests, areas, skills, world interactions
   - maximize parallel module work

2. **Track B: Core Systems & Parity**
   - combat behavior, net/protocol parity, engine parity edges, social/economy loops
   - prioritize blockers that impact many content modules

### Capacity Guidance

- Default split: ~70% Track A, ~30% Track B.
- Shift to ~50/50 when boss-heavy quests or parity regressions increase.

### Gating Policy (Mandatory)

Do **not** mark content as `✅ complete` if it depends on unresolved core blockers.

Use:
- `🟡 partial` when implementation exists but parity/system dependency is unresolved.
- Explicit task dependency notes (e.g., blocked by `AGENTBRIDGE-6`, `MECH-1`, `NET-*`).

---

## Command Prefix Policy (Permission Stability)

Use stable command prefixes so Codex runtime approvals apply consistently and agents stop re-prompting.

- Always run Gradle from `rsmod/` using PowerShell 7 with this exact shape:
  - `& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat <args>"`
- Do not switch command wrappers for the same operation (for example, avoid mixing `cmd /c`, nested shell chains, or alternate PowerShell invocation formats).
- Keep command execution rooted in `Z:\Projects\OSRS-PS-DEV\rsmod` for code/build tasks.
- If a new recurring command pattern needs escalation, request and save a persistent prefix approval once, then standardize on that exact shape in future runs.
- Prefer repository scripts for repeated flows so one script prefix can be approved and reused.

Currently approved and expected prefix:
- `["C:\\Program Files\\PowerShell\\7\\pwsh.exe", "-Command", ".\\gradlew.bat"]`

---

## Kotlin Navigation & Analysis Tooling (Required)

Use Kotlin tooling in this order before ad-hoc grep-only investigation:

1. `cclsp` for diagnostics/symbol navigation:
   - `get_diagnostics`
   - `find_workspace_symbols`
   - `find_definition`
   - `find_references`
   - `get_hover`
2. If call hierarchy is needed, use the repo fallback script:
   - `& 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\kotlin-call-hierarchy.ps1" -Symbol <Name> -RepoRoot "Z:\Projects\OSRS-PS-DEV" -Scope rsmod -Mode both -MaxResults 200`

Notes:
- Current Kotlin LSP in this environment does not provide `textDocument/prepareCallHierarchy`, so `cclsp` call-hierarchy tools will fail by design.
- Do not keep manual `kotlin-lsp --stdio` sessions running during normal work; let `cclsp` manage server startup.
- If `cclsp` returns `cancelled`, kill stale kotlin-lsp processes, then run the README warmup sequence before continuing.

---

## Process Cleanup Policy (Required)

After any build/test/server run, agents must clean up Java background processes before handoff or task completion.

- Always stop Gradle daemons after active work:
  - `\.\\gradlew.bat --stop`
- If an RSMod server was started during the task, stop the server Java process before leaving:
  - Check first: `Get-Process java -ErrorAction SilentlyContinue`
  - Stop owned server process(es) if still running.
- Do not leave orphaned `java`/Gradle processes between tasks; this is mandatory for multi-agent stability.
- If cleanup fails, note it in the task and mark as blocked rather than silently continuing.
- Recommended safe kotlin-lsp cleanup command:
  - `Get-CimInstance Win32_Process | ? { ($_.Name -in @('java.exe','cmd.exe')) -and $_.CommandLine -match 'kotlin-lsp' } | % { Stop-Process -Id $_.ProcessId -Force }`

---

## MCP Data Source Policy (Temporary)

Until further notice, treat `osrs-wiki-rev233` as unavailable for task execution.

- Primary data MCP for content/ID lookups: `osrs-cache`
- Use `osrs-cache` tools (`search_objtypes`, `search_npctypes`, `search_loctypes`, `osrs_wiki_search`, `osrs_wiki_parse_page`) for all routine work
- Do not block a task on `get_npc_rev233*` / `get_item_rev233*` availability
- If rev233 wrapper transport is down, continue with `osrs-cache` + local symbols in `rsmod/.data/symbols/`

---

## Truth Sources and Drift Control

Use this authority order when docs disagree:

1. `agent-tasks` registry — execution state and ownership truth
2. `README.md` — definitive startup/workflow playbook
3. `AGENTS.md` — ownership/blockers/role scope + DoD details
4. `docs/NEXT_STEPS.md` — active sequencing truth
5. `docs/CONTENT_AUDIT.md` — feature status truth
6. `docs/MASTER_ROADMAP.md` — long-horizon scope catalog

`docs/REV233_COMPLETION_ROADMAP.md` is historical/contextual and may be stale; do not use it as execution truth.
See `docs/DOC_AUTHORITY.md` for per-agent doc routing and reference-only docs.

---

## Agent Roles and Guidelines

### Claude Code — Content Implementer + Tester

**Owns**: All `rsmod/content/skills/`, `rsmod/content/mechanics/`, `mcp/`, `bots/`, `agent-runner/`, `rsmod/content/other/agent-bridge/`

**Primary tools** (Claude Code slash commands — invoke with `/skill-name`):

| Skill                       | When to use                                                                                         |
| --------------------------- | --------------------------------------------------------------------------------------------------- |
| `/implement-skill <name>`   | Full workflow: research → implement → build → test → fix. Start here for any new skill.             |
| `/rsmod-skill-implementer`  | Deep-dive skill implementation guidance — module scaffold, event handlers, XP, resource depletion   |
| `/rsmod-alter-porting`      | Translating an existing Alter v1 plugin to RSMod v2. Use alongside `docs/TRANSLATION_CHEATSHEET.md` |
| `/rsmod-test-writer`        | Writing a `bots/<skill>.ts` test script for MCP execution                                           |
| `/rsmod-content-verifier`   | Validating an implemented skill's XP rates, animations, and item IDs against the OSRS wiki          |
| `/rsmod-wiki-oracle`        | Looking up or creating a `wiki-data/skills/<skill>.json` oracle file                                |
| `/rsmod-npc-combat-definer` | Porting NPC combat stats from Kronos JSON and writing combat behavior plugins                       |
| `/rsmod-npc-data-tools`     | **NEW** — Use Python tools for automated NPC drop table generation from wiki-data + Kronos          |
| `/java-reference-expert`    | Deep-dive analysis and porting from legacy Java codebases (Kronos, Tarnish, etc.)                   |
| `/rsmod-infra-architect`    | Rev 233 infrastructure setup, build optimization, and troubleshooting                               |

**MCP tools** (available when game server is running):

| Tool             | Usage                                                                    |
| ---------------- | ------------------------------------------------------------------------ |
| `server_status`  | Check if server + AgentBridge are up before any test                     |
| `build_server`   | Build a specific module: `{ module: "smithing" }`                        |
| `run_bot_file`   | Run a test from `bots/`: `{ player: "TestBot", file: "woodcutting.ts" }` |
| `execute_script` | Run ad-hoc TypeScript bot code with `bot.*` and `sdk.*` globals          |
| `get_state`      | Inspect current player state snapshot                                    |
| `list_players`   | See who's connected to AgentBridge                                       |

**Agent Coordination MCP** (all agents must use):

| Tool                 | Usage                                                                    |
| -------------------- | ------------------------------------------------------------------------ |
| `list_tasks`         | See all tasks and their status — START HERE to find available work       |
| `claim_task`         | Atomically claim a pending task before starting work                     |
| `complete_task`      | Mark your task done (releases file locks)                                |
| `lock_file`          | Declare you are editing a file to prevent conflicts                      |
| `list_file_locks`    | Check what files other agents are editing                                |
| `agent_heartbeat`    | ⭐ **CALL EVERY 1-2 MINUTES** — report status, task, file, message       |
| `list_active_agents` | See who's online and what they're working on right now                   |
| `get_agent_activity` | Get detailed activity for a specific agent                               |

**Agent Heartbeat Protocol** (ALL AGENTS MUST FOLLOW):
```
Every 1-2 minutes while working, call:
  agent_heartbeat(agent, status, current_task, current_file, message)

Status options: "idle" | "working" | "blocked" | "waiting"
Example:
  agent_heartbeat(
    agent: "kimi",
    status: "working",
    current_task: "CRAFT-1",
    current_file: "rsmod/content/skills/crafting/Gems.kt",
    message: "Implementing gem cutting"
  )
```
This lets other agents see what you're doing in real-time via `list_active_agents`.

**Guidelines:**
- Always read `docs/CONTENT_AUDIT.md` before claiming a module — it may already be done
- Use module-scoped builds: `build_server({ module: "smithing" })` — not a full build
- Cross-reference all item/NPC IDs against `rsmod/.data/symbols/obj.sym` / `npc.sym` (rev 233)
- Use `cclsp` for Kotlin symbol/definition/reference checks before broad text searches; use `scripts/kotlin-call-hierarchy.ps1` when you need caller/callee mapping
- Write a `bots/<skill>.ts` test for every skill implemented — use `bots/woodcutting.ts` as template
- Update `docs/CONTENT_AUDIT.md` when a skill is complete (change ❌ to ✅)
- Never edit `rsmod/engine/` or `rsmod/api/` — those belong to Gemini

---

### Gemini — Engine / Infrastructure

**Owns**: `rsmod/server/app/`, `rsmod/api/`, `rsmod/engine/`

**Primary responsibility**: RSMod server reliability, engine bugs, build system, performance

**MCP tools**: Use `agent-tasks` server for coordination — call `agent_heartbeat` every 1-2 min so others see what you're working on.

**Guidelines:**
- **Server startup fix**: `game.key` is missing. Run `\.\\gradlew.bat generateRsa --console=plain` then `gradlew.bat build --console=plain`. Cache is already downloaded.
- Run `scripts/diagnose.bat` first — it checks all 11 prerequisites and tells you exactly what's wrong
- When fixing engine code, check `rsmod/api/testing/` for existing `GameTestExtension` unit tests
- Use `cclsp` for cross-module Kotlin refactors and symbol tracing; for call hierarchy use `scripts/kotlin-call-hierarchy.ps1` until LSP hierarchy is supported
- Do not modify content plugins in `rsmod/content/` — those belong to Claude
- When you add or change a game API, update `docs/TRANSLATION_CHEATSHEET.md` so Claude can use it
- Use `--daemon` flag for faster Gradle builds on repeated runs
- **Canonical path rule (strict)**: do not create alternate sibling module folders with different naming (for example `alkharid/` vs `al-kharid/`). Reuse the existing module path listed in **Module Ownership**.
- **No duplicate package roots**: if `package org.rsmod.content.areas.city.<area>` already exists, add files under that existing module only; never introduce a second physical source tree for the same package.
- **Task focus rule**: Gemini should keep at most one active `in_progress` task unless explicitly assigned parallel work by a coordinator.
- **Lock hygiene rule**: if paused/blocked for more than 10 minutes, either post a blocker note with concrete error output or release file locks.
- **Blocker quality rule**: blocker notes must include exact failing command, first compile/runtime error line, and affected file path(s).

**Current task**: Get the server to start and load. Fix: `gradlew.bat generateRsa` then `gradlew.bat build`.

---

### Kimi — Full-Stack Content Implementation + Research

**Owns**: `wiki-data/`, `OSRSWikiScraper/`, `docs/RUNESERVER_NOTES.md`, plus any `rsmod/content/` modules claimed via task registry

**Primary responsibility**: Full content implementation (skills, quests, areas, NPCs, shops), wiki data extraction, NPC combat & drops, bot testing, and cross-project fixes (e.g., rs-sdk web).

**MCP tools**: Use `agent-tasks` server for coordination — call `agent_heartbeat` every 1-2 min so others see what you're working on.

**Guidelines:**
- **Full implementation scope**: Kimi agents handle complete feature delivery — from research → implementation → build → test → fix
- All output goes into `wiki-data/skills/<name>.json` or `wiki-data/monsters/<name>.json` — see format below
- **Revision alignment**: RSMod targets rev 233. Cross-reference any item/NPC IDs against `rsmod/.data/symbols/obj.sym` and `npc.sym` before writing them into wiki-data files or Kotlin code.
- **USE THE TOOLS**: Run `python tools/npc_lookup.py "NPC Name"` before manual data entry — it auto-maps rev 184→233 IDs
- Use rev 233 cache symbols as source of truth for Kotlin refs.
- Always include `test_locations` in skill JSONs (x/z coordinates of training spots) so test bots can teleport
- Capture RuneServer findings in `docs/RUNESERVER_NOTES.md` — tick timings, combat formulas, spawn table conventions
- **Build verification**: Always run `build_server({ module: "your-module" })` before marking complete
- **Bot testing**: Write `bots/<feature>.ts` test scripts for integration verification

**Primary Skills:**
- `/rsmod-npc-data-tools` — NPC data extraction and drop table generation
- `/rsmod-skill-implementer` — Skill implementation (same as Claude)
- `/rsmod-test-writer` — Bot test script creation
- `/rsmod-content-verifier` — Validation against OSRS wiki

**NPC Data Tools:**
```bash
# Quick lookup for any NPC
python tools/npc_lookup.py "Hill Giant"

# Generate Kotlin drop table skeleton
python tools/npc_lookup.py "Hill Giant" --output kotlin

# Batch process all Tier 1 F2P NPCs
python tools/batch_npc_processor.py --tier 1 --dry-run
python tools/batch_npc_processor.py --tier 1

# Convert drop rates to weights
python tools/drop_rate_converter.py "1/128"
```

**Track Record**: 100+ tasks completed including:
- Full skills: Mining (gem rocks, guild boost), Fletching, Herblore, Crafting
- Areas: Falador, Varrock, Al Kharid, Lumbridge, Mining Guild, Dwarven Mine
- NPCs: Full combat definitions + drop tables for Hill Giants, Skeletons, Zombies, Moss Giants, Bears, Chaos Druids, etc.
- Quests: Cook's Assistant, Restless Ghost, Black Knights' Fortress
- Core systems: Food, Potions, Shops, Trading, Random Events

---

## Module Ownership

| Module path                         | Owner  | Status                               |
| ----------------------------------- | ------ | ------------------------------------ |
| `rsmod/content/skills/woodcutting/` | Claude | ✅ Complete                           |
| `rsmod/content/skills/fishing/`     | Claude | ✅ Complete                           |
| `rsmod/content/skills/cooking/`     | Claude | ✅ Complete                           |
| `rsmod/content/skills/firemaking/`  | Claude | ✅ Complete                           |
| `rsmod/content/skills/mining/`      | Claude | ✅ Complete                           |
| `rsmod/content/skills/thieving/`    | Claude | ✅ Complete                           |
| `rsmod/content/skills/prayer/`      | Claude | ✅ Complete                           |
| `rsmod/content/quests/rune-mysteries/` | codex  | ✅ Complete                           |
| `rsmod/content/quests/cooks-assistant/` | kimi  | ✅ Complete                           |
| `rsmod/content/quests/restless-ghost/` | kimi   | ✅ Complete                           |
| `rsmod/content/quests/black-knights-fortress/` | kimi | ✅ Complete                           |
| `rsmod/content/quests/sheep-shearer/` | codex  | ✅ Complete                           |
| `rsmod/content/quests/dorics-quest/` | worker-doric | ✅ Complete                    |
| `rsmod/content/quests/witchs-potion/` | codex  | ✅ Complete                           |
| `rsmod/content/quests/pirates-treasure/` | worker-pirate | ✅ Complete                |
| `rsmod/content/quests/romeo-juliet/` | opencode  | ✅ Complete                           |
| `rsmod/content/quests/imp-catcher/` | opencode  | ✅ Complete                           |
| `rsmod/content/areas/city/draynor/` | opencode  | ✅ Complete                           |
| `rsmod/content/areas/city/falador/` | kimi  | ✅ Complete (shops + npcs + Mining Guild) |
| `rsmod/content/areas/city/varrock/` | kimi  | ✅ Complete (npcs + shops + museum)     |
| `rsmod/content/areas/city/al-kharid/` | kimi | ✅ Complete (shops + npcs + quests + gem trader) |
| `rsmod/content/areas/city/lumbridge/` | kimi | ✅ Complete (shops + npcs + altar)      |
| `rsmod/content/areas/city/mining-guild/` | kimi | ✅ Complete (guild entry + npcs)     |
| `rsmod/content/areas/dungeons/dwarven-mine/` | kimi | ✅ Complete (full dungeon)      |
| `rsmod/content/skills/mining/` | kimi | ✅ Complete (gem rocks + guild boost)   |
| `rsmod/content/areas/city/port-sarim/` | opencode  | ✅ Complete                           |
| `rsmod/content/areas/city/edgeville/` | opencode  | ✅ Complete                           |
| `rsmod/content/skills/runecrafting/` | codex | ✅ Complete                           |
| `rsmod/content/skills/farming/`    | codex  | 🟡 Baseline complete (herb patches)   |
| `rsmod/content/mechanics/poison/`   | Claude | ✅ Complete                           |
| `rsmod/content/other/agent-bridge/` | Claude | ✅ Complete                           |
| `rsmod/content/other/npc-drops/`    | codex  | 🔄 Spider drop tables in progress     |
| `rsmod/server/app/`                 | Gemini | 🔄 Startup fix in progress            |
| `rsmod/api/`                        | Gemini | 🔄 Engine fixes                       |
| `rsmod/engine/`                     | Gemini | 🔒 Engine only                        |
| `wiki-data/`                        | opencode| ✅ smithing.json + agility.json       |
| `mcp/`                              | Claude | ✅ Complete                           |
| `bots/`                             | Claude | 🔄 Adding test scripts                |
| `agent-runner/`                     | Claude | ✅ Complete                           |
| `docs/`                             | All    | Shared — coordinate before rewriting |

**To claim a module**: add your name to the Owner column and set status to 🔄.
**To release a module**: set status to ✅ and clear your name if another agent needs it.

---

## Current Blockers

| Blocker                           | Blocking                              | Assigned                  | Fix / Status                                       |
| --------------------------------- | ------------------------------------- | ------------------------- | -------------------------------------------------- |
| NPC combat defs missing           | Combat gameplay                       | Claude (after server fix) | Port from `Kronos-184-Fixed/.../data/npcs/combat/` |

### Resolved (Archived)
- ~~`game.key` missing~~ ✅ Resolved (generated via `generateRsa`, located in `.data/game.key`)
- ~~`api:invtx` compile errors~~ ✅ Resolved (Transaction refs fixed)
- ~~`wiki-data/smithing.json`~~ ✅ Created
- ~~NPC data tools~~ ✅ `tools/npc_lookup.py` + `tools/batch_npc_processor.py`
- ~~`docs/RUNESERVER_NOTES.md`~~ ✅ 526 lines complete
- ~~Ghost module `romeo-and-juliet/`~~ ✅ Deleted (CLEANUP-2)
- ~~WoodsmanTutor runtime crash~~ ✅ Fixed (CLEANUP-1)
- ~~5 orphaned dialogue files~~ ✅ Deleted (CLEANUP-3)
- ~~Dead code in `NpcDropTablesScript.kt`~~ ✅ Removed (CLEANUP-4)

---

## How to Start a Task (Checklist)

```
□ 1. Read docs/CONTENT_AUDIT.md — confirm the task isn't already done
□ 2. Check AGENTS.md Module Ownership — confirm no one else owns it
□ 3. list_active_agents() — see who's online and what they're doing
□ 4. claim_task(taskId, agent) — atomically claim from the task registry
□ 5. lock_file(path, agent, taskId) — for every file you will edit
□ 6. Read the relevant doc(s) listed in "Key Docs" above
□ 7. Invoke the appropriate slash command (Claude) or run diagnose.bat (Gemini)
□ 8. Do the work — call agent_heartbeat() every 1-2 minutes!
□ 8a. Run `spotlessApply` before module build (`edit -> spotlessApply -> build`)
□ 8b. Verify file structure after edits (no orphaned code outside class/object/function blocks)
□ 9. Verify gating: if core dependency is unresolved, keep status as 🟡/in_progress
□ 10. complete_task(taskId, agent, notes) — mark done, releases file locks
□ 11. Update CONTENT_AUDIT.md and this file when done
□ 12. Write a note in docs/agent-notes/<your-name>.md
```

---

## Common Failure Patterns (And Required Countermeasures)

These are recurring causes of agent rework loops. Treat countermeasures as mandatory controls:

1. **Scope drift** (editing outside assignment bounds)
   - Countermeasure: explicit allowed/forbidden path lists in every assignment + file locks before edit.
2. **Stub-complete false positives** (handlers/messages without state/inventory/reward wiring)
   - Countermeasure: enforce DoD checklist and keep status `🔄`/`🟡` until full gameplay loop is implemented.
3. **Prompt ambiguity** (missing target entity, insertion point, or validation command)
   - Countermeasure: reject task prompt until it matches the assignment template exactly.
4. **Skipped validation order** (build before preflight/format)
   - Countermeasure: run validation in mandatory order and paste exact commands in completion notes.
5. **Silent blocker handling** (guessing through uncertainty)
   - Countermeasure: if blocked >10 minutes, record exact command + first error line + impacted paths, then release locks if pausing.

---

## Task Prompt Quality (Required)

Do not assign vague commands like "work on skill implementations."

Every task assignment must include:
1. Exact target entity/system (example: `Implement Hill Giant combat` or `Claim NPC-HILL-GIANT-F2P`).
2. Exact task ID to claim.
3. Explicit allowed file paths.
4. Explicit forbidden file paths.
5. Validation command.

If the target NPC/feature is not named explicitly, the assignee must ask for clarification before editing.

---

## Coordinator Task Assignment Template (Copy/Paste)

Use this exact structure when delegating:

```text
Task ID: <TASK-ID>
Objective: <single concrete outcome with exact target name>

Claim:
- claim_task(taskId="<TASK-ID>", agent="<agent-name>")

Allowed Paths:
- <path 1>
- <path 2>

Forbidden Paths:
- rsmod/engine/**
- rsmod/api/**
- <task-specific forbidden paths>

Exact Insertion Point:
- <file>: <inside function/class + placement rule>
  Example: "append inside startup(), immediately before closing brace"

Pre-Flight (must confirm before edit):
- Read target file first
- Check for existing refs/handlers/declarations
- Add only missing entries (no duplicates)
- Verify symbols using search_objtypes/search_npctypes or grep against .sym files

Validation Order (mandatory):
1) scripts\preflight-ref-hygiene.ps1
2) spotlessApply
3) scoped build
4) (optional) scoped test/bot check

Validation Command:
- & 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\preflight-ref-hygiene.ps1" -FailOnIssues
- & 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat <module-task> --console=plain"

Completion Output:
- changed files
- key edits
- exact build/test command(s) + result
- assumptions/open questions

Blocker Rule:
- if blocked >10 min: add blocker note with exact command, first error line, affected file paths; unlock files.
```

Task acceptance rule:
- If any required field is missing, assignee must not edit files and must request a corrected prompt.

Quick example:

```text
Task ID: NPC-HILL-GIANT-F2P
Objective: Implement Hill Giant combat behavior and wiring.
Allowed Paths:
- rsmod/content/other/npc-drops/**
- rsmod/content/mechanics/aggression/**
Forbidden Paths:
- rsmod/engine/**
- rsmod/api/**
Exact Insertion Point:
- NpcDropTablesScript.kt: append register call inside startup(), before closing brace.
Validation Command:
- & 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat :content:other:npc-drops:build --console=plain"
```

---

## API and Plugin Change Contract (Required)

Any task that changes behavior in `api/**` or shared plugin wiring must include:

1. Impacted public APIs (function/type names).
2. Expected downstream content impact (`content/**` modules affected).
3. Migration notes for callers (if signatures or semantics changed).
4. Validation command(s) for changed API/plugin modules.
5. Test evidence (new or updated tests, or explicit blocker reason).

Do not merge API/plugin behavior changes without caller impact notes.

---

## Escalation Matrix (Required)

When blocked by boundary ownership or missing primitives, escalate with this format:

1. Boundary crossed (content -> api, api -> engine, content -> engine).
2. Exact failing command.
3. First relevant error line.
4. Impacted file path(s).
5. Requested owner action.

Blockers without this data are considered incomplete.

---

## Pre-Flight Checklist Before Editing (Required)

Before touching any Kotlin/TOML file:

- [ ] Read the target file first.
- [ ] Check whether refs/constants/handlers already exist.
- [ ] Add only missing entries (no duplicate declarations).
- [ ] Verify every symbol name using `search_npctypes` / `.sym` files BEFORE writing `find()`.
- [ ] Identify exact insertion point (line/section), not just "add in startup()".
- [ ] Verify no duplicate package roots or duplicate modules will be created.

## 🛡️ Content Integrity Standards (Babysitter Mandate)

Following a comprehensive audit of agent work, these standards are now **mandatory** for all content:

1. **Idiomatic Transactions**: Never use `player.inv.freeSpace()`. Always use `if (invAdd(...).success)` or `invAddOrDrop()`.
2. **Messaging**: Use the standard `mes("text")` extension. Never define custom `message()` functions with TODO stubs.
3. **Task Completion**: Achievement diary or quest tasks are not "complete" until they are **hooked up to the world**. Orphaned logic functions in a separate file do not count.
4. **State Locality**: `var` is BANNED inside `object` singletons. All persistent state must be on the `Player`.

---

## Lock Recovery (Coordinator/Admin)

If locks are stale or ownership drift occurs, use admin recovery tools in this order:

1. `cleanup_stale_locks(actor, reason)` to remove invalid locks where task/owner state no longer matches.
2. `force_release_task_locks(taskId, actor, reason)` to clear all locks for one dead/stuck task.
3. `force_unlock_file(path, actor, reason)` for one-off file unlocks.

Rules:
- Always leave a reason in the tool call for auditability.
- Prefer `cleanup_stale_locks` first; use force tools only when targeted cleanup is required.
- After forced unlock, notify affected agent/task in notes and re-check `list_file_locks`.

---

## NPC Drops File Ownership (Required)

To avoid lock contention, NPC drop tables must be split by NPC (or small NPC group) into
separate files under `rsmod/content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcdrops/tables/`.

Rules:
1. One task → one drop-table file (example: `tables/ZombieDropTables.kt`).
2. Do NOT edit shared monoliths. The only shared file is the aggregator:
   `NpcDropTablesScript.kt`, which should only call `registerAll(...)` for each table file.
3. If a shared ref is needed (e.g., `DropTableObjs`), add only the missing symbol and keep
   changes minimal.
4. Legacy DTX-based tables are deprecated. Do not register them:
   `rsmod/content/other/npcrops/scripts/F2pDropTables.kt` (kept for reference only).
   Using it will duplicate drops and cause conflicts.
5. When a drop item ref is missing:
   - Prefer BaseObjs refs first (check `api/config/refs/BaseObjs.kt`).
   - If missing, add a module-local ref in your NPC table file via `ObjReferences`.
   - Use the rev 233 `.sym` internal name (not the wiki name) to avoid mismatches.
   - Example fixes: `grimy_guam` (not `grimy_guam_leaf`), `raw_tuna` (not `tuna`).

### ⚠️ CRITICAL: Drop Table Registration

**Creating a drop table file is NOT enough — you MUST register it!**

After creating `tables/YourNpcDropTables.kt` with a `registerAll(registry)` function:

```kotlin
// In NpcDropTablesScript.kt, add to startup():
override fun ScriptContext.startup() {
    // ... existing registrations ...
    YourNpcDropTables.registerAll(registry)  // <-- ADD THIS LINE
}
```

**Common mistake:** Many drop table files exist in `tables/` but were never wired up,
so NPCs had no drops in-game. Always verify your registration call exists!

**Verification:** After adding, build the module:
```bash
\.\\gradlew.bat :content:other:npc-drops:build --console=plain
```

---

## Kotlin Edit Safety Rules

When appending registration calls (e.g., in `startup()`):

1. Confirm you are editing *inside* the intended function block.
2. State exact insertion rule in task notes (for example: "append at end of startup(), before closing brace").
3. After edit, re-open file and verify:
   - braces balance,
   - no orphaned calls outside class/object/function,
   - no duplicate function declarations.

---

## Git Branch and Commit Policy

1. Do not work directly on `main` during active multi-agent sprints.
2. Create one branch per task:
   - `agent/<name>/<task-id>-<short-slug>`
3. Keep commits task-scoped and small enough to review.
4. Before `complete_task`, include commit metadata:
   - branch name
   - commit hash
5. Preferred completion note format:
   - `Implemented <summary> | branch=<branch> | commit=<sha>`

---

## Definition of Done (DoD)

A task is only `✅ Complete` when all of the following are true:

1. **No stub logic**: no placeholder flows, no `TODO`-only handlers, no unconditional “success” shortcuts.
2. **Core gameplay loop works**: start → progress → completion paths are implemented end-to-end.
3. **Quest/skill state is persisted correctly**: quest stage/varp transitions and required item checks are validated.
4. **Rewards are real**: XP/items/quest points are actually awarded through existing APIs.
5. **Build passes for the module**: run scoped build for the target module (plus `spotlessApply` if needed).
6. **FULL BOOT GATE**: For tasks involving symbols or global configs, the server MUST boot successfully to the `[MainGameProcess] World is live` state using `scripts\start-server.bat`.
7. **Test artifact exists**: add or update a bot script (`bots/<feature>.ts`) for integration verification when applicable.
7. **Docs updated**: reflect completion accurately in `docs/CONTENT_AUDIT.md` and module ownership in this file.
8. **Dependency gate passed**: task is not blocked by unresolved core systems/parity dependencies.

If any item above is missing, status must remain `🔄 in_progress` or `🟡 partial`, not `✅ complete`.

---

## Stub vs Implementation

Use this distinction before marking work complete:

- **Stub** (NOT complete):
  - Registers handlers but does not perform real item/state transitions.
  - Dialogue exists but never advances quest stages correctly.
  - Returns success messages without gameplay effect.
  - Contains “TODO implement logic” in critical path.
- **Implementation** (complete-ready):
  - Uses real game APIs for inventory, XP, quest stages, movement, and rewards.
  - Handles fail paths (missing items, no space, wrong stage).
  - Supports the expected interaction flow in-world, not just compile-time structure.

---

## Common API Patterns

Use these proven patterns to avoid subtle regressions:

- **Quest stage checks**
  - `when (getQuestStage(QuestList.some_quest)) { ... }`
  - Set via `access.setQuestStage(QuestList.some_quest, stage)`
- **Inventory transactions**
  - Remove: `player.invDel(player.inv, obj, count).success`
  - Add: `player.invAdd(player.inv, obj, count).success`
  - Always branch on `.success` for rollback/fail messaging.
- **XP rewards**
  - Quest rewards: `access.giveQuestReward(QuestList.some_quest)` when quest rewards are defined in `QuestList`.
  - Skill XP: `statAdvance(stats.some_skill, xpAmount)`
- **Completion UI**
  - `access.showCompletionScroll(...)` with explicit reward strings and quest points.
- **Interaction handlers**
  - NPC: `onOpNpc1(...) { ... }`
  - Loc item-use: `onOpLocU(loc, obj) { ... }`
  - Held item ops: `onOpHeldU(objA, objB) { ... }` / `onOpHeld2(obj) { ... }`

---

## Symbol Verification Process (Rev 233)

Before hardcoding any ID-backed reference:

1. Check symbol names in:
   - `rsmod/.data/symbols/obj.sym`
   - `rsmod/.data/symbols/npc.sym`
   - `rsmod/.data/symbols/loc.sym`
 2. Prefer ref access via `BaseObjs`, `BaseNpcs`, or module-local `*Refs` wrappers.
 3. If symbol is missing from base refs but present in `.sym`, define module-local refs first.
 4. **Do Not Rename IDs With `.local/*.sym`**: Never map an existing ID to a different name in `.local/*.sym`. If the base `.sym` already has the ID, use its canonical internal name in Kotlin instead.
 5. **NO Duplicate Functional Stubs**: NEVER map multiple functional symbols (e.g. two different bows) to `blankobject`. This crashes `EnumBuilders` and `ObjEditors` at boot due to duplicate map keys.
 6. Do not trust raw wiki IDs directly for Kotlin constants; map by symbol name in rev 233 `.sym`.

---

## Legacy Research Intake Protocol (Alter / Kronos / 317 / 2004scape)

When using newly collected legacy docs/code:

1. Start with **target behavior**, not source browsing.
2. Pull only the smallest relevant slice (one mechanic/quest/skill path).
3. Extract:
   - state transitions,
   - fail paths,
   - reward flow,
   - timing/cadence assumptions.
4. Re-implement in RSMod v2 APIs; do not copy legacy IDs/opcodes/constants.
5. Verify all refs against rev 233 `.sym` files before Kotlin refs are added.
6. Record behavior deltas if legacy behavior differs from target OSRS behavior.
7. Require scoped build + bot artifact before task completion.

Reference: `docs/LEGACY_IMPLEMENTATION_PLAYBOOK.md`.

---

## Build Troubleshooting Checklist

When scoped build fails, run this sequence:

1. First-pass order is mandatory: `edit -> spotlessApply -> scoped build`.
2. If still failing, run `spotlessApply` then rebuild target module again.
3. If Gradle lock/cache errors occur:
   - retry once;
   - if still failing, run `gradlew --stop` and retry;
   - report environment-level lock issue in task notes.
4. Verify compile errors are in your module vs unrelated workspace churn.
5. Do not mark task complete until target module build is green.

---

## Wave 3/4 Quest Complexity Warning

Wave 3/4 quests (e.g., `Prince Ali Rescue`, `Vampyre Slayer`, `Dragon Slayer I`) are high-risk for false-complete states.

- Expect multi-NPC, multi-item, multi-stage branching and location-sensitive interactions.
- Treat “dialogue scaffold only” as incomplete.
- Require at minimum:
  - full state transitions,
  - required item flow,
  - reward delivery,
  - scoped green build,
  - bot/integration test script prepared.

If any of these are missing, leave quest status as in-progress and document blockers explicitly.

---

## Server Startup Reference

```bat
:: If game.key is missing (one-time fix):
gradlew.bat generateRsa --console=plain
gradlew.bat build --console=plain

:: Every time:
scripts\start-server.bat

:: Diagnose startup failures:
scripts\diagnose.bat

:: Port 43595 (AgentBridge) opens after first player logs in
:: Check: netstat -ano | findstr :43595
```

---

## Rev 233 Infrastructure Guidelines

When setting up or troubleshooting the Rev 233 environment:
1. **Opcode Alignment**: Use `/rsmod-infra-architect` to verify packet structures against `_references_archive/rsinf_233`.
2. **Build Persistence**: Always use the Gradle daemon (`--daemon`) for 10x faster subsequent builds.
3. **Symbol Integrity**: If IDs seem shifted, verify `rsmod/api/config/src/.../refs/` against the `.sym` files in `rsmod/.data/symbols/`.
4. **Client Connectivity**: Ensure the test client `params.txt` matches the server's configured revision and RSA keys.

---

## Build Reference

```bash
# Full build (slow — avoid for single-module changes):
\.\\gradlew.bat build -x test --console=plain

# Single module (use this — 10s vs 3min):
\.\\gradlew.bat :content:skills:smithing:build --console=plain

# Common module short names (also accepted by MCP build_server tool):
#   woodcutting, fishing, cooking, firemaking, mining, thieving, prayer
#   smithing, crafting, fletching, herblore, agility, runecrafting
#   agent-bridge

# MCP caveat:
# build_server module shortcut resolution is unreliable for several non-skills modules
# (example: content:other:npc-drops). Use direct Gradle module path fallback:
# \.\\gradlew.bat :content:other:npc-drops:build --console=plain

# Run unit tests for a module:
\.\\gradlew.bat :content:skills:smithing:test --console=plain
```

---

## Do Not Touch

| Path                             | Reason                                                              |
| -------------------------------- | ------------------------------------------------------------------- |
| `rsmod/engine/`                  | Core engine — Gemini only                                           |
| `.claude/skills/`                | Agent specialized instructions — do not modify without coordination |
| `rsmod/api/config/src/.../refs/` | Symbol tables — coordinate before adding new symbols                |
| `rsmod/.data/` (base sym files)  | Generated from cache — do not modify base `.sym` files              |
| `rsmod/.data/symbols/.local/`    | Custom entries OK — agents add aliases here (coordinate via lock)   |
| `rsmod/.data/.gitignore`         | Leave alone                                                         |
| `mcp/`                           | MCP server — Claude only unless coordinated                         |

---

## Data Compatibility: Revision 233

RSMod v2 targets **revision 233**.

| Data type        | Safe to use directly? | Action needed                             |
| ---------------- | --------------------- | ----------------------------------------- |
| XP rates         | ✅ Same as wiki baseline | None                                      |
| Animation IDs    | ✅ Generally stable    | Verify against `seqs.sym` if unsure       |
| Item IDs (obj)   | ⚠️ May have shifted    | Cross-check `rsmod/.data/symbols/obj.sym` |
| NPC IDs          | ⚠️ May have shifted    | Cross-check `rsmod/.data/symbols/npc.sym` |
| Loc (object) IDs | ⚠️ May have shifted    | Cross-check `rsmod/.data/symbols/loc.sym` |

**Rule**: Any hardcoded ID in Kotlin must match the rev 233 `.sym` file, not the wiki.

---

## Wiki Data Format

### Skill oracle (`wiki-data/skills/<name>.json`)

```json
{
  "skill": "smithing",
  "tiers": [
    {
      "name": "Bronze bar",
      "level_req": 1,
      "xp": 6.2,
      "animation": 898,
      "input_ids": [436, 438],
      "output_id": 2349,
      "loc_pattern": "Furnace"
    }
  ],
  "test_locations": [
    { "desc": "Lumbridge furnace", "x": 3228, "z": 3256 },
    { "desc": "Al Kharid furnace", "x": 3277, "z": 3185 }
  ]
}
```

### Monster oracle (`wiki-data/monsters/<name>.json`)

```json
{
  "npc": "goblin",
  "npc_id": 118,
  "hp": 5,
  "attack_level": 1,
  "strength_level": 1,
  "defence_level": 1,
  "attack_speed": 4,
  "attack_anim": 422,
  "death_anim": 2304,
  "drops": [
    { "id": 526, "name": "Bones", "qty": 1, "weight": 1, "guaranteed": true },
    { "id": 995, "name": "Coins", "qty_min": 1, "qty_max": 4, "weight": 4 }
  ]
}
```

---

## Handoff Protocol

When you finish:
1. Set module status to ✅ in the ownership table
2. Remove or resolve your entry in Current Blockers
3. Add any new blockers you discovered
4. Write a session note in `docs/agent-notes/<your-name>.md`

When you discover something unexpected (engine bug, wrong ID, missing API):
1. Add it to Current Blockers with the assigned agent
2. Do not silently work around it — document it

---

## Testing Quick Reference

**Without server** (unit tests — for XP/logic bugs, fast):
```bash
\.\\gradlew.bat :content:skills:smithing:test
```
Uses `GameTestExtension` in `rsmod/api/testing/`. No server needed.

**With server** (integration — full click-through):
```
1. scripts\start-server.bat
2. Log in a player
3. Claude: use MCP tool run_bot_file { player: "TestBot", file: "woodcutting.ts" }
   OR invoke /implement-skill <name> which drives the full loop automatically
```

See `docs/LLM_TESTING_GUIDE.md` for state snapshot schema and full methodology.
See `bots/woodcutting.ts` as the reference test script template.




