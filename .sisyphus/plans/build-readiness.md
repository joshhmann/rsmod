# RSMod v2 Build Readiness Work Plan

## TL;DR

> **Quick Summary**: Fix critical build blockers, enable hidden WIP content, and fill essential F2P gaps to achieve a playable server.
>
> **Deliverables**: 
> - Clean build with zero compile errors
> - Teleport spells (Varrock, Lumbridge, Falador)
> - High Alchemy + Superheat spells
> - Skeleton NPC combat + drops (for Edgeville Dungeon)
> - Prayer altar restoration working
> - Furnace interaction verified
>
> **Estimated Effort**: Medium (2-3 days focused work)
> **Parallel Execution**: YES - 4 waves
> **Critical Path**: Symbol Verification → CooksAssistant → PrayerAltar → Skeleton → Teleports → Alchemy

---

## Context

### Original Request
Review CORE_SYSTEMS_AUDIT.md and create a plan to tackle critical issues blocking server build readiness.

### Interview Summary
**Key Discussions**:
- User wants build-ready server with core F2P features functional
- Focus on unblocking gameplay, not perfection
- Priority on essential F2P content (teleports, alchemy, core NPCs)

### Research Findings
**Major Discovery**: Teleport spells, Alchemy, Superheat ALREADY EXIST in `_disabled_wip/` folder - just need to be moved and verified.

**Critical Corrections from Metis**:
- ✅ PoisonScript.kt is already working (varps exist in BaseVarps.kt)
- ✅ Zombie and Moss Giant drops already exist
- ✅ **Skeleton drops COMPLETED** (Task NPC-DROP-SKELETON in NPC-TRAINING-F2P)
- ❌ CooksAssistant has compile errors (QUEST-1) - **Create new task**
- ⚠️ PrayerAltar symbol refs covered by BUILD-CRIT-15 (gemini working on it)
- ⚠️ _disabled_wip content may not compile - needs verification
- ✅ Zombie and Moss Giant drops already exist
- ❌ Skeleton drops are 100% missing (critical for Edgeville Dungeon)
- ❌ CooksAssistant has compile errors (QUEST-1)
- ❌ PrayerAltar has null internalId references (more critical than assumed)
- ⚠️ _disabled_wip content may not compile - needs verification before enabling

### Key Pattern Discoveries
1. **NPC Drop Tables**: Create `tables/<Npc>DropTables.kt` → register in `NpcDropTablesScript.kt`
2. **Magic Spells**: Data-driven from cache enums, utility spells need handlers via `onIfModalButton`
3. **Symbol Verification**: ALL refs must be checked against `.sym` files to avoid null internalId

### Existing Task Registry Integration

This plan integrates with existing tasks from the agent-tasks registry:

| Registry Task ID | Plan Task | Status | Notes |
|------------------|-----------|--------|-------|
| **MAGIC-F2P-UTILITY** | Tasks 4-7 | Pending | F2P Utility Spells - **CLAIM THIS** for magic work |
| **NPC-TRAINING-F2P** | N/A | ✅ **COMPLETED** | Skeleton/Zombie/Moss Giant already done! |
| | Tasks 0-2, 8-10, MCP | Not in registry | New tasks for build readiness |

### Critical Discovery: Skeleton Already Done!

**Registry Task `NPC-TRAINING-F2P` notes confirm:**
- ✅ Hill Giant - DONE
- ✅ **Skeleton - DONE**
- ✅ Zombie - DONE
- ✅ Moss Giant - DONE

**Action**: Task 3 converted to **verification task** - implementation already completed!

### Claim Commands

**For Magic Spells (Tasks 4-7):**
```kotlin
claim_task(taskId="MAGIC-F2P-UTILITY", agent="<your-name>")
```

### New Tasks to Create in Registry

These tasks are NOT currently in the registry but should be added:

1. **BUILD-HYGIENE-SYMBOL** - Symbol verification script (Task 0)
2. **QUEST-1-FIX** - CooksAssistant compile errors (Task 1)
3. **PRAYER-ALTAR-FIX** - PrayerAltar symbol references (Task 2)



This plan integrates with existing tasks from the agent-tasks registry:

| Registry Task ID | Plan Task | Status | Notes |
|------------------|-----------|--------|-------|
| **MAGIC-F2P-UTILITY** | Tasks 4-7 | Pending | F2P Utility Spells (Teleports, Alch, Superheat) - WAVE 1 PRIORITY |
| **NPC-TRAINING-F2P** | Task 3 | ✅ **COMPLETED** | Verify Skeleton implementation already done |
| NPC-DROP-UNICORN | - | Blocked | Assigned to kimi-npc, not in this plan |
| SLAYER-8 | - | Blocked | Assigned to codex, not in this plan |

**Integration Notes**:
- Tasks 3-7 in this plan map directly to registry tasks MAGIC-F2P-UTILITY and NPC-TRAINING-F2P
- When executing, agents should `claim_task()` using the registry task IDs
- Plan task numbers are for internal coordination; registry IDs are the source of truth
- Blocked registry tasks (NPC-DROP-UNICORN, SLAYER-8, etc.) are explicitly NOT in this plan scope



---

## Work Objectives

### Core Objective
Achieve a build-ready, playable F2P server by fixing critical blockers and enabling essential content.

### Concrete Deliverables
- Zero compile errors across all modules
- Server starts without disabled scripts
- Player can: teleport between F2P cities, alch items, superheat ore, train combat on Skeletons
- Prayer restoration functional at altars

### Definition of Done
- [ ] `gradlew.bat :server:app:build --console=plain` passes
- [ ] Server starts without "Skipping script startup" errors
- [ ] Bot test passes for each enabled feature
- [ ] CONTENT_AUDIT.md updated with status changes

### Must Have
- CooksAssistant compile errors fixed
- PrayerAltar symbol references corrected
- Skeleton drop tables implemented
- Teleport spells enabled and tested
- High Alchemy enabled and tested
- Superheat Item enabled and tested
- Symbol verification script created (for future use)

### Must NOT Have (Guardrails)
- Do NOT enable _disabled_wip content without compile-checking first
- Do NOT fix adjacent code outside task scope (no "while I'm here" changes)
- Do NOT add members-only content (P2P teleports, etc.)
- Do NOT refactor working code - only fix what's broken
- Do NOT skip symbol verification for any new refs

---

## Verification Strategy

### Test Decision
- **Infrastructure exists**: YES
- **Automated tests**: Tests-after (no TDD - fixing existing code)
- **Framework**: Gradle build + bot scripts via AgentBridge
- **Symbol verification**: Custom script for null internalId detection

### QA Policy
Every task includes agent-executed verification:
- **Compile test**: `gradlew.bat :content:<module>:build --console=plain`
- **Symbol check**: `grep -r "null.*internalId" <module-path>/` must return nothing
- **Runtime test**: Bot script execution where applicable
- **Evidence**: Screenshots/logs saved to `.sisyphus/evidence/`

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 0 (Foundation - BUILD-CRIT-15 already in progress):
└── BUILD-CRIT-15: Resolve symbol name drift (gemini) [in_progress]
    └─ Status: 🔄 IN PROGRESS by gemini

Wave 1 (Quick Wins - Create & Claim):
├── Task 1: Fix CooksAssistant compile errors [quick] → Create QUEST-1-FIX
├── Task 2: Add remaining NPC-TRAINING-F2P monsters [quick] → Claim NPC-TRAINING-F2P
└── Task 3: Verify Skeleton drops [quick] → Part of NPC-TRAINING-F2P

Wave 2 (Magic Spells - Claim MAGIC-F2P-UTILITY):
├── Task 4: Verify _disabled_wip/magic compiles [quick]
├── Task 5: Port teleport spells (if Task 4 passes) [unspecified-high]
├── Task 6: Port High Alchemy (if Task 4 passes) [unspecified-high]
└── Task 7: Port Superheat Item (if Task 4 passes) [quick]

Wave 3 (Verify & Polish):
├── Task 8: Verify furnace interaction works [quick]
├── Task 9: Bot test all enabled magic spells [unspecified-high]
└── Task 10: Final build verification [quick]

Wave FINAL (Review):
├── Task F1: Plan compliance audit [oracle]
├── Task F2: Build verification [quick]
└── Task F3: Symbol audit [quick]

Critical Path: BUILD-CRIT-15 → Task 1 → Task 2 → Task 4 → Task 5 → Task 9 → F1-F3
Parallel Speedup: ~50% faster than sequential
Max Concurrent: 4 tasks (Waves 1-2)
```
Wave 0 (Foundation - do first):
└── Task 0: Create symbol verification script
    └─ Provides: Automated null internalId detection for all subsequent tasks

Wave 1 (Unblock Build - P0/P1):
├── Task 1: Fix CooksAssistant compile errors [quick]
├── Task 2: Fix PrayerAltar symbol references [quick]
└── Task 3: Verify Skeleton drops [quick]

Wave 2 (Enable WIP - P2/P3, can parallel):
├── Task 4: Verify _disabled_wip/magic compiles [quick]
├── Task 5: Port teleport spells (if Task 4 passes) [unspecified-high]
├── Task 6: Port High Alchemy (if Task 4 passes) [unspecified-high]
└── Task 7: Port Superheat Item (if Task 4 passes) [quick]

Wave 3 (Verify & Polish - P4):
├── Task 8: Verify furnace interaction works [quick]
├── Task 9: Bot test all enabled magic spells [unspecified-high]
└── Task 10: Final build verification [quick]

Wave FINAL (Review):
├── Task F1: Plan compliance audit [oracle]
├── Task F2: Build verification [quick]
└── Task F3: Symbol audit [quick]

Critical Path: Task 0 → Task 1 → Task 2 → Task 4 → Task 5 → Task 9 → F1-F3
Parallel Speedup: ~50% faster than sequential
Max Concurrent: 4 tasks (Waves 1-2)
```

### Dependency Matrix

| Task | Depends On | Blocks |
|------|------------|--------|
| 0 (Symbol Script) | — | 1, 2, 3, 4, 5, 6, 7 |
| 1 (CooksAssistant) | — | Testing quest progression |
| 2 (PrayerAltar) | 0 | Prayer skill training |
| 3 (Skeleton) | 0 | F2P combat at Draynor Manor |
| 4 (Verify WIP) | 0 | 5, 6, 7 |
| 5 (Teleports) | 4 | Fast travel |
| 6 (Alchemy) | 4 | Money making |
| 7 (Superheat) | 4, 6 | Smithing training |
| 8 (Furnace) | — | — |
| 9 (Bot Tests) | 5, 6, 7 | — |

---

## TODOs

### Wave 0: Foundation

- [ ] **0. Create Symbol Verification Script**

  **What to do**:
  - Create a script/tool to detect null internalId references in Kotlin files
  - Script should search for `find("...")` patterns that return null
  - Output: List of files with potential symbol issues
  - Save to: `scripts/verify-symbols.ps1` (PowerShell) or similar

  **Must NOT do**:
  - Do NOT fix the symbols in this task (just detect)
  - Do NOT modify any content code

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: None (scripting only)

  **Parallelization**:
  - **Can Run In Parallel**: NO (must complete first)
  - **Blocks**: Tasks 1-7

  **Acceptance Criteria**:
  - [ ] Script created and tested
  - [ ] Script correctly identifies files with null internalId risk
  - [ ] Script can be run on any module: `.\scripts\verify-symbols.ps1 -Module "content:skills:prayer"`

  **QA Scenarios**:
  ```
  Scenario: Script detects missing symbol
    Tool: Bash (PowerShell)
    Preconditions: Target module has null internalId refs
    Steps:
      1. Run: .\scripts\verify-symbols.ps1 -Module "content:skills:prayer"
      2. Check output lists PrayerAltar.kt with specific lines
    Expected Result: Exit code 0, output shows detected issues
    Evidence: .sisyphus/evidence/task-0-script-test.txt
  ```

  **Commit**: NO (tooling script, commit with Task 1)

---

### Wave 1: Unblock Build

> **Registry Task Note**: Task 3 verifies `NPC-TRAINING-F2P` implementation.
> Task 3 verifies Skeleton drops (already implemented by claude).
> Task 3 adds Skeleton drops specifically (Zombie/Moss Giant already exist).


- [ ] **1. Fix CooksAssistant Compile Errors (QUEST-1)**

  **What to do**:
  - Read `rsmod/content/quests/cooks-assistant/` module
  - Identify compile errors from build output
  - Fix unresolved references, type mismatches, etc.
  - Likely issues: Missing imports, undefined symbols, syntax errors

  **Must NOT do**:
  - Do NOT refactor quest logic (just fix compile errors)
  - Do NOT add new quest features
  - Do NOT touch working quest stages

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: None

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Task 2)
  - **Blocked By**: Task 0

  **References**:
  - Pattern: Other quest modules in `content/quests/`
  - Build error log: Check `rsmod/run_default.log` for QUEST-1 errors

  **Acceptance Criteria**:
  - [ ] `gradlew.bat :content:quests:cooks-assistant:build --console=plain` passes
  - [ ] No compile errors in module
  - [ ] Symbol verification script shows no null internalId refs

  **QA Scenarios**:
  ```
  Scenario: Module builds successfully
    Tool: Bash (Gradle)
    Steps:
      1. Run: cd rsmod && .\gradlew.bat :content:quests:cooks-assistant:build --console=plain
      2. Verify: BUILD SUCCESSFUL
    Expected Result: Exit code 0, no compile errors
    Evidence: .sisyphus/evidence/task-1-build.log
  ```

  **Commit**: YES
  - Message: `fix(cooks-assistant): resolve compile errors for QUEST-1`
  - Files: `rsmod/content/quests/cooks-assistant/**/*.kt`

---

- [ ] **2. Fix PrayerAltar Symbol References**

  **What to do**:
  - Read `rsmod/content/skills/prayer/scripts/PrayerAltar.kt`
  - Identify loc refs with null internalId (causing BUILD-CRIT-15)
  - Fix by adding correct symbols to loc refs or updating symbol names
  - Symbols likely needed: `prayer_altar`, `chaos_altar`, etc.

  **Must NOT do**:
  - Do NOT change prayer mechanics (just fix refs)
  - Do NOT add new altar types
  - Do NOT modify prayer drain logic

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: None

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Task 1)
  - **Blocked By**: Task 0

  **References**:
  - Symbol file: `rsmod/.data/symbols/loc.sym`
  - Pattern: `rsmod/content/skills/prayer/scripts/Priest.kt` (working)
  - Altar locs: Search for altar references in `content/areas/`

  **Acceptance Criteria**:
  - [ ] `gradlew.bat :content:skills:prayer:build --console=plain` passes
  - [ ] Symbol verification shows no null internalId in prayer module
  - [ ] Altar interaction restores prayer points (bot test)

  **QA Scenarios**:
  ```
  Scenario: Altar restores prayer
    Tool: rsmod-game MCP (run_bot_file)
    Preconditions: Player has < max prayer points
    Steps:
      1. Teleport player to Lumbridge church
      2. Interact with altar
      3. Check prayer points restored to max
    Expected Result: Prayer points = base level
    Evidence: .sisyphus/evidence/task-2-prayer-test.json
  ```

  **Commit**: YES
  - Message: `fix(prayer): correct altar loc refs for BUILD-CRIT-15`
  - Files: `rsmod/content/skills/prayer/scripts/PrayerAltar.kt`

---

### Wave 2: Enable WIP Content

> **Registry Task Note**: Tasks 4-7 are part of registry task `MAGIC-F2P-UTILITY`.
> Agents should claim this task and work on Tasks 4-7 as sub-components.
> Task 4 must complete (Go) before Tasks 5-7 can proceed.

> **Note**: Task 3 (Skeleton) is a **verification task** - implementation already completed per `NPC-TRAINING-F2P`!

---

### Wave 2: Enable WIP Content

> **Registry Task Note**: Tasks 4-7 are part of registry task `MAGIC-F2P-UTILITY`.
> Agents should claim this task and work on Tasks 4-7 as sub-components.
> Task 4 must complete (Go) before Tasks 5-7 can proceed.


- [ ] **3. Verify Skeleton Drops** (Registry: **NPC-TRAINING-F2P** - ✅ COMPLETED)

  **Registry Task**: `NPC-TRAINING-F2P` - F2P Training Monsters
  **Status**: ✅ COMPLETED by claude (per registry)
  **This Task**: Verify the implementation works correctly

  **What to do**:
  - Verify Skeleton drop tables exist in `NpcDropTablesScript.kt`
  - Run bot test to confirm skeletons drop bones + loot
  - Check combat stats are defined
  - Document any issues if found

  **Must NOT do**:
  - Do NOT reimplement drops (already done)
  - Do NOT modify existing drop tables unless broken

  **Acceptance Criteria**:
  - [ ] Skeleton registered in `NpcDropTablesScript.kt`
  - [ ] Bot test: Kill skeleton → drops bones + loot
  - [ ] No errors in console
  - [ ] Combat stats functional
  
  **Registry Task**: `NPC-TRAINING-F2P` - F2P Training Monsters (Giants, Skeletons, Zombies)
  **Claim Command**: `claim_task(taskId="NPC-TRAINING-F2P", agent="<your-name>")`

  **What to do**:
  - Create `rsmod/content/other/npc-drops/tables/SkeletonDropTables.kt`
  - Define Skeleton-specific drops (bones, iron equipment, runes, coins)
  - Create `SkeletonNpcs` refs for skeleton variants
  - Create `SkeletonObjs` refs for drops not in BaseObjs
  - Register in `NpcDropTablesScript.kt`
  - Data source: OSRS wiki (https://oldschool.runescape.wiki/w/Skeleton)

  **Must NOT do**:
  - Do NOT modify existing NPC drops
  - Do NOT add members-only skeleton drops
  - Do NOT create combat definitions (just drops)

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Skills**: `rsmod-wiki-oracle`, `rsmod-npc-data-tools`

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Task 1, 2)
  - **Blocked By**: Task 0

  **References**:
  - Pattern: `rsmod/content/other/npc-drops/tables/HillGiantDropTables.kt`
  - Drop table DSL: `rsmod/api/drop-table/`
  - NPC refs: `rsmod/content/other/npc-drops/DropTableNpcs.kt`
  - Objs refs: `rsmod/content/other/npc-drops/DropTableObjs.kt`

  **Acceptance Criteria**:
  - [ ] `gradlew.bat :content:other:npc-drops:build --console=plain` passes
  - [ ] Skeleton drop table registered in `NpcDropTablesScript.kt`
  - [ ] Symbol verification shows no null internalId refs
  - [ ] Bot test: Kill skeleton → drops bones + loot

  **QA Scenarios**:
  ```
  Scenario: Skeleton drops loot
    Tool: rsmod-game MCP (run_bot_file)
    Preconditions: Player at Edgeville Dungeon with weapon
    Steps:
      1. Find skeleton NPC
      2. Kill skeleton
      3. Check inventory for: bones + random drop
    Expected Result: Bones always, 50%+ chance of additional loot
    Evidence: .sisyphus/evidence/task-3-skeleton-drops.log
  ```

  **Commit**: YES
  - Message: `feat(npc-drops): add Skeleton drop tables`
  - Files: `rsmod/content/other/npc-drops/tables/SkeletonDropTables.kt`

---

### Wave 2: Enable WIP Content

> **Registry Task Note**: Tasks 4-7 are part of registry task `MAGIC-F2P-UTILITY`. 
> Agents should claim this task and work on Tasks 4-7 as sub-components.
> Task 4 must complete (Go) before Tasks 5-7 can proceed.


- [ ] **4. Verify _disabled_wip/magic Compiles**

  **What to do**:
  - Copy files from `_disabled_wip/strict-startup-pass2/content/skills/magic/teleports/` to temp location
  - Attempt build of teleport/alchemy modules
  - Identify any compile errors
  - Document issues if found (don't fix in this task)

  **Must NOT do**:
  - Do NOT commit to main codebase yet
  - Do NOT fix compile errors in this task
  - Do NOT modify original _disabled_wip files

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: None

  **Parallelization**:
  - **Can Run In Parallel**: YES (after Task 0)
  - **Blocks**: Task 5, 6, 7

  **Acceptance Criteria**:
  - [ ] Temp build of teleport module attempted
  - [ ] Compile status documented (pass/fail with errors)
  - [ ] Go/No-Go decision for Tasks 5-7

  **QA Scenarios**:
  ```
  Scenario: WIP content compiles
    Tool: Bash (Gradle)
    Steps:
      1. Copy teleport files to test location
      2. Run: .\gradlew.bat compileKotlin on test module
      3. Document result
    Expected Result: Clear pass/fail with error list if failed
    Evidence: .sisyphus/evidence/task-4-wip-verify.log
  ```

  **Commit**: NO (verification task only)

---

- [ ] **5. Port Teleport Spells (Conditional on Task 4)** (Registry: **MAGIC-F2P-UTILITY**)

  **Registry Task**: `MAGIC-F2P-UTILITY` - F2P Utility Spells (Teleports, Alch, Superheat)
  **Claim Command**: `claim_task(taskId="MAGIC-F2P-UTILITY", agent="<your-name>")`

  **What to do**:
  - Move `TeleportSpells.kt` from `_disabled_wip` to `content/skills/magic/teleports/`
  - Create module structure if needed
  - Wire in module to build
  - Verify spellbook buttons work
  - Test: Varrock, Lumbridge, Falador teleports

  **Must NOT do**:
  - Do NOT add P2P teleports (Ardougne, Watchtower, etc.)
  - Do NOT modify spellbook UI
  - Do NOT change rune requirements

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Skills**: None

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Task 6, 7 after Task 4)
  - **Blocked By**: Task 4 (if Task 4 fails, SKIP this task)

  **References**:
  - Source: `_disabled_wip/strict-startup-pass2/content/skills/magic/teleports/`
  - Pattern: `content/skills/magic/spell-attacks/` for module structure
  - Spellbook buttons: Check `api/config/refs/components` for spellbook refs

  **Acceptance Criteria**:
  - [ ] `gradlew.bat :content:skills:magic:teleports:build --console=plain` passes
  - [ ] Teleport spells show in spellbook
  - [ ] Each teleport consumes correct runes
  - [ ] Player teleports to correct coordinates

  **QA Scenarios**:
  ```
  Scenario: Varrock teleport works
    Tool: rsmod-game MCP (run_bot_file)
    Preconditions: Player has 25 Magic, Law rune, 3 Air, 1 Fire
    Steps:
      1. Click Varrock teleport in spellbook
      2. Verify runes consumed
      3. Verify position = (3212, 3424)
      4. Verify 35 Magic XP gained
    Expected Result: All checks pass
    Evidence: .sisyphus/evidence/task-5-teleport-test.json
  ```

  **Commit**: YES
  - Message: `feat(magic): enable F2P teleport spells`
  - Files: `rsmod/content/skills/magic/teleports/`

---

- [ ] **6. Port High Alchemy (Conditional on Task 4)** (Registry: **MAGIC-F2P-UTILITY**)

  **Registry Task**: `MAGIC-F2P-UTILITY` - F2P Utility Spells (Teleports, Alch, Superheat)
  **Note**: Part of same registry task as Task 5, coordinate with agent working on teleports

  **What to do**:
  - Move `HighAlchemy.kt` from `_disabled_wip/magic/teleports/`
  - Wire in module
  - Test alchemy on various items
  - Verify coin value calculation

  **Must NOT do**:
  - Do NOT add Low Alchemy (separate spell)
  - Do NOT modify item values

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Skills**: None

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Task 5, 7 after Task 4)
  - **Blocked By**: Task 4

  **Acceptance Criteria**:
  - [ ] `gradlew.bat :content:skills:magic:teleports:build --console=plain` passes
  - [ ] High Alchemy spell shows in spellbook
  - [ ] Spell consumes 1 Nature + 5 Fire runes
  - [ ] Item converted to coins (0.6 * shop price)
  - [ ] 65 Magic XP gained per cast

  **QA Scenarios**:
  ```
  Scenario: Alch steel platebody
    Tool: rsmod-game MCP
    Preconditions: Player has spell runes, steel platebody
    Steps:
      1. Use High Alchemy on steel platebody
      2. Verify: item removed, coins added (1200g), XP gained
    Expected Result: All checks pass
    Evidence: .sisyphus/evidence/task-6-alchemy-test.json
  ```

  **Commit**: YES (group with Task 5)

---

- [ ] **7. Port Superheat Item (Conditional on Task 4)** (Registry: **MAGIC-F2P-UTILITY**)

  **Registry Task**: `MAGIC-F2P-UTILITY` - F2P Utility Spells (Teleports, Alch, Superheat)
  **Note**: Part of same registry task as Tasks 5-6, coordinate with agent working on magic

  **What to do**:
  - Move `SuperheatItem.kt` from `_disabled_wip/magic/teleports/`
  - Wire in module
  - Test superheat on various ores
  - Verify bar creation

  **Must NOT do**:
  - Do NOT modify smelting success rates

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: None

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Task 5, 6 after Task 4)
  - **Blocked By**: Task 4

  **Acceptance Criteria**:
  - [ ] Superheat Item spell works
  - [ ] Consumes 1 Nature + 4 Fire runes
  - [ ] Ore converted to bar (respecting level requirements)
  - [ ] 53 Magic XP + Smithing XP gained

  **QA Scenarios**:
  ```
  Scenario: Superheat iron ore
    Tool: rsmod-game MCP
    Preconditions: Player has runes, iron ore, 15 Smithing
    Steps:
      1. Use Superheat on iron ore
      2. Verify: ore removed, iron bar added, XP gained
    Expected Result: All checks pass
    Evidence: .sisyphus/evidence/task-7-superheat-test.json
  ```

  **Commit**: YES (group with Tasks 5-6)

---

### Wave 3: Verify & Polish

- [ ] **8. Verify Furnace Interaction**

  **What to do**:
  - Verify `SmithingFurnace.kt` is wired in module
  - Test: Click furnace → smelting menu opens
  - Test: Use ore on furnace → smelts correctly
  - Verify existing implementation works

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: None

  **Parallelization**:
  - **Can Run In Parallel**: YES

  **Acceptance Criteria**:
  - [ ] Furnace interaction works (OpLoc1)
  - [ ] Ore-on-furnace works (OpLocU)
  - [ ] Smelting produces correct bar

  **QA Scenarios**:
  ```
  Scenario: Smelt bronze at furnace
    Tool: rsmod-game MCP
    Preconditions: Player at Al Kharid furnace with copper + tin
    Steps:
      1. Click furnace → menu opens
      2. Select bronze bar → bars created
    Expected Result: Bars created, XP gained
    Evidence: .sisyphus/evidence/task-8-furnace-test.json
  ```

  **Commit**: NO (verification only, no code changes expected)

---

- [ ] **9. Bot Test All Enabled Magic Spells**

  **What to do**:
  - Write bot test script: `bots/magic-teleports.ts`
  - Write bot test script: `bots/magic-alchemy.ts`
  - Run tests, fix any issues

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Skills**: `rsmod-test-writer`

  **Parallelization**:
  - **Can Run In Parallel**: NO (depends on Tasks 5-7)

  **Acceptance Criteria**:
  - [ ] All teleport spells tested
  - [ ] High Alchemy tested
  - [ ] Superheat tested

  **QA Scenarios**:
  ```
  Scenario: Complete magic test suite
    Tool: rsmod-game MCP
    Steps:
      1. Run: run_bot_file { player: "TestBot", file: "magic-teleports.ts" }
      2. Run: run_bot_file { player: "TestBot", file: "magic-alchemy.ts" }
    Expected Result: All tests pass
    Evidence: .sisyphus/evidence/task-9-bot-tests.log
  ```

  **Commit**: YES
  - Message: `test: add bot tests for magic spells`
  - Files: `bots/magic-*.ts`

---

- [ ] **10. Final Build Verification**

  **What to do**:
  - Full build: `gradlew.bat :server:app:build --console=plain`
  - Verify no "Skipping script startup" errors
  - Document any remaining issues

  **Acceptance Criteria**:
  - [ ] Full server build passes
  - [ ] Server starts without disabled scripts
  - [ ] CONTENT_AUDIT.md updated

  **QA Scenarios**:
  ```
  Scenario: Full build passes
    Tool: Bash (Gradle)
    Steps:
      1. Run: .\gradlew.bat :server:app:build --console=plain
      2. Check: BUILD SUCCESSFUL
      3. Run: .\gradlew.bat :server:app:run
      4. Check: No "Skipping script startup" errors
    Expected Result: Clean build, clean startup
    Evidence: .sisyphus/evidence/task-10-final-build.log
  ```

---

## Final Verification Wave

- [ ] **F1. Plan Compliance Audit**

  Read the plan end-to-end. For each "Must Have": verify implementation exists. For each "Must NOT Have": search codebase for forbidden patterns. Check evidence files exist.

  Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT: APPROVE/REJECT`

- [ ] **F2. Build Verification**

  Run full build + tests. Verify all modules compile. Document any failures.

- [ ] **F3. Symbol Audit**

  Run symbol verification script on all modified modules. Verify no null internalId references.

---

## Success Criteria

### Verification Commands
```bash
# Full build
cd rsmod && .\gradlew.bat :server:app:build --console=plain

# Check for disabled scripts
cd rsmod && .\gradlew.bat :server:app:run 2>&1 | findstr "Skipping script startup"

# Symbol verification
.\scripts\verify-symbols.ps1 -Module "content:skills:magic:teleports"
```

### Final Checklist
- [ ] All "Must Have" items present
- [ ] All "Must NOT Have" items absent
- [ ] All tests pass
- [ ] CONTENT_AUDIT.md updated
- [ ] No compile errors
- [ ] No null internalId references
- [ ] Server starts cleanly

---

## Commit Strategy

- **Task 1**: `fix(cooks-assistant): resolve compile errors for QUEST-1`
- **Task 2**: `fix(prayer): correct altar loc refs for BUILD-CRIT-15`
- **Task 3**: `test(npc-drops): verify Skeleton drops` (no code changes expected)
- **Task 5-7**: `feat(magic): enable F2P teleport and alchemy spells`
- **Task 9**: `test: add bot tests for magic spells`

---

## Notes

### Auto-Resolved Items
- ✅ Zombie drops: Already exist (discovered by Metis)
- ✅ Moss Giant drops: Already exist (discovered by Metis)
- ✅ PoisonScript: Already working, varps exist in BaseVarps.kt

### Decisions Made
- Use dependency order from Metis (Tasks 0-4 are prerequisites)
- Verify _disabled_wip compiles before enabling (risk mitigation)
- Add Skeleton drops even though Zombie/Moss exist (Edgeville Dungeon gap)
- Skip Low Alchemy (lower priority than High Alchemy)

### Risk Mitigation
- Symbol verification script prevents null internalId regressions
- Task 4 (verify WIP compiles) prevents wasted effort on broken code
- Parallel execution limited to reduce coordination overhead
- Acceptance criteria include specific test commands

### Assumptions
- _disabled_wip/magic content is complete and just needs porting
- Symbol names in .sym files are correct for rev 233
- OSRS wiki data is accurate for rev 233
- AgentBridge bot testing infrastructure is functional


---

## MCP Server Tasks

The following tasks ensure MCP servers are operational for testing and verification:

### MCP-1: Verify rsmod-game MCP Server

**What to do**:
- Test MCP server startup: `cd mcp && bun server-enhanced.ts`
- Verify connection to RSMod AgentBridge (port 43595)
- Test basic tools: `execute_script`, `get_state`, `build_server`
- Document any connection issues

**Acceptance Criteria**:
- [ ] MCP server starts without errors
- [ ] Can execute basic bot script
- [ ] Can query player state
- [ ] Can trigger module builds

**QA Scenarios**:
```
Scenario: MCP server responds
  Tool: rsmod-game MCP
  Steps:
    1. Start server: cd mcp && bun server-enhanced.ts
    2. Test: get_state({ player: "TestBot" })
    3. Test: build_server({ module: "woodcutting" })
  Expected Result: All calls return successfully
  Evidence: .sisyphus/evidence/mcp-1-server-test.log
```

**Commit**: NO (infrastructure verification)

---

### MCP-2: Verify osrs-cache MCP Server

**What to do**:
- Test MCP server startup: `cd mcp-osrs && npm start`
- Test ID lookups: items, NPCs, animations
- Verify cache data files are present

**Acceptance Criteria**:
- [ ] MCP server starts
- [ ] Can lookup item IDs
- [ ] Can lookup NPC IDs
- [ ] Can lookup animation IDs

**QA Scenarios**:
```
Scenario: Cache lookups work
  Tool: osrs-cache MCP
  Steps:
    1. Start server
    2. Test: getItem({ name: "Bronze bar" }) → returns ID 2349
    3. Test: getNpc({ name: "Goblin" }) → returns valid NPC
  Expected Result: All lookups return correct data
  Evidence: .sisyphus/evidence/mcp-2-cache-test.log
```

**Commit**: NO (infrastructure verification)

---

### MCP-3: Create MCP Bot Test Templates

**What to do**:
- Create `bots/templates/mcp-test-template.ts`
- Document common MCP bot patterns
- Add examples for: walking, interacting, skilling, combat

**Acceptance Criteria**:
- [ ] Template file created
- [ ] Examples cover major bot actions
- [ ] Documentation clear and usable

**Commit**: YES
- Message: `docs: add MCP bot test templates`
- Files: `bots/templates/*`

---

## Updated Task Count

| Category | Tasks | Status |
|----------|-------|--------|
| Build Blockers | Tasks 0-3 | 4 tasks |
| WIP Content | Tasks 4-7 | 4 tasks |
| Verification | Tasks 8-10 | 3 tasks |
| MCP Tasks | MCP-1 to MCP-3 | 3 tasks |
| Final Review | F1-F3 | 3 tasks |
| **Total** | **17 tasks** | **Ready** |

