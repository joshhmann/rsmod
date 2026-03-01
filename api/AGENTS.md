# API — Public Interface Layer

## OVERVIEW
Public API for content developers. Type-safe abstractions over engine.  
**Owner: Gemini** — Coordinate before changes.

## PROCESS CLEANUP POLICY (REQUIRED)

After any build/test/server run, clean up Java background processes before handoff or task completion.

- Stop Gradle daemons when done with active work:
  - `cd rsmod && gradlew.bat --stop`
- If a server was started during the task, stop owned Java server processes before leaving.
- Do not leave orphaned `java` or Gradle processes between tasks.
- If cleanup fails, record it in task notes and mark blocked instead of silently continuing.

## SPRINT WORKFLOW (IMPLEMENT -> BUILD -> QA LIST)

Use batch execution per sprint:

1. Implement the full scoped set of code changes first.
2. Run a single scoped build/verification pass for the sprint.
3. Produce a concise QA checklist of what must be tested manually/integration.
4. Only then move to the next sprint.

## QA REFERENCE

- Use `../docs/testing/MASTER_QA_PLAYBOOK.md` for sprint QA sign-off.

## DELEGATION SAFETY RULES (REQUIRED)

Use external/secondary agents for low-risk content work only (dialogue, shop inventory wiring,
spawn tables, data-entry style config updates, and isolated quest text/content scripts).

Do **not** assign external/secondary agents ownership of critical systems:
- `engine/**`
- `api/death/**`, `api/combat/**`, `api/net/**`, `api/player/**`
- `content/mechanics/**`
- `content/interfaces/**` (UI)
- protocol/networking, persistence, or cross-module refactors

Critical-path tasks must be implemented by the primary maintainer and only delegated in narrowly
scoped follow-ups after design and guardrails are defined.

## DELEGATED TASK CONTRACT (REQUIRED FOR ALL TASKS)

Any task delegated to another agent/model must include this contract in the assignment:

1. Exact objective (single scoped outcome).
2. Allowed file list (explicit paths only).
3. Forbidden paths (`engine/**`, `api/death/**`, `content/mechanics/**`, `content/interfaces/**`,
   plus any task-specific exclusions).
4. Reuse requirement: follow an existing in-repo pattern file.
5. Validation command (module-scoped Gradle compile/build task).
6. Output requirement: changed files, key edits, assumptions, unresolved questions.
7. Uncertainty rule: if uncertain, stop and leave a TODO note instead of guessing.

## TASK QUALITY CHECKS (REQUIRED)

Before editing:
1. Read target file first.
2. Identify exact insertion point (for example: `inside startup(), immediately before closing brace`).
3. Verify no duplicate declaration/handler/ref will be created.
4. Confirm allowed/forbidden path boundaries for the task.

After editing:
1. Verify structure (no orphaned code outside class/object/function blocks).
2. Run validation in mandatory order:
   - `spotlessApply`
   - scoped build
3. If blocked, provide blocker output with:
   - exact command,
   - first error line,
   - affected file path(s).

Preflight helper:
- `pwsh -File ..\scripts\preflight-ref-hygiene.ps1 -RepoRoot .. -FailOnIssues`

## STRUCTURE
```
api/
├── config/refs/        # BaseObjs, BaseNpcs, BaseSeqs (find("symbol"))
├── player/protect/     # ProtectedAccess facade
├── repo/               # LocRepository, NpcRepository, etc.
├── script/             # *ScriptEventExtensions.kt (event DSL)
├── testing/            # GameTestExtension (JUnit 5)
├── combat/             # Combat subsystem APIs
├── controller/         # Entity lifecycle controllers
├── type/               # Type definitions and extensions
└── [40+ modules]       # Modular API packages
```

## WHERE TO LOOK

| Component | Location | Purpose |
|-----------|----------|---------|
| Symbol refs | config/src/.../refs/BaseObjs.kt | `find("symbol_name")` for type-safe IDs |
| Player API | player/src/.../protect/ProtectedAccess.kt | Safe player actions facade |
| Event DSL | script/*ScriptEventExtensions.kt | onOpLoc, onOpNpc, onOpHeld handlers |
| Repositories | repo/src/.../repo/{loc,npc,obj,player}/ | Entity lookup patterns |
| Testing | testing/src/.../GameTestExtension.kt | Module-scoped JUnit 5 tests |

## CONVENTIONS
**Type References:**
```kotlin
// Use symbol-based find(), never raw IDs
val axe = objs.find("rune_axe")    // ✅ Good
val axe = find(1271)               // ✅ Also valid (obj sym name)
val axe = 1271                     // ❌ Bad - magic number

**Player Actions:**
```kotlin
// ProtectedAccess provides type-safe methods
suspend fun ProtectedAccess.gather(resource: LocType) {
    anim(tool.anim)                 // Play animation
    delay(3)                        // Wait ticks
    statAdvance(stats.woodcutting, xp)  // Grant XP
}
```

**Extensions:**
```kotlin
// Define extensions on unpacked types
val UnpackedObjType.axeWoodcuttingReq: Int by objParam(params.skill_requirement)
val UnpackedLocType.treeLevelReq: Int by locParam(params.tree_level_req)
```

## ⚠️ KNOWN ISSUES

**ProtectedAccess is a God Class** (~3600 lines, 200+ methods)
- Impact: High cognitive load, difficult to test
- Solution planned: Split into domain-specific facades
  - `MovementAccess` (walk, teleport)
  - `InventoryAccess` (add, delete, transfer)
  - `CombatAccess` (queue hits, modifiers)
  - `DialogueAccess` (ifChatNpc, ifMesbox, etc.)

## DO NOT TOUCH

| Path | Owner | Reason |
|------|-------|--------|
| `rsmod/engine/` | Gemini | Core engine |
| `rsmod/.data/symbols/` | Generated | Rev 228 symbol tables |

## API Change Contract

For any PR that adds, removes, or modifies public API behavior, include an explicit API contract summary.

Required contract items:
1. **Affected public methods/types**
   - List each public method/type changed.
   - Mark compatibility as additive, behavior change, deprecation, or breaking change.
2. **Caller impact summary (`content/**` modules)**
   - Name impacted content modules/files under `content/**`.
   - State whether changes are required now or can be deferred.
3. **Migration guidance**
   - Provide before/after usage patterns.
   - Include required follow-up steps and expected rollout order.
4. **Test additions in `api/testing`**
   - Identify coverage added in `api/testing` (new or updated tests).
   - If no tests were added, explain why and record a follow-up TODO.
5. **Docs update reference**
   - Reference docs updated for the API change.
   - If translation/API mapping changed, include `docs/TRANSLATION_CHEATSHEET.md` update status.

### API PR Release-Note Template (for content-agent handoff)

Use this in PR descriptions so content agents can adapt without guesswork:

```md
## API Change Contract

### 1) Affected public methods/types
- `<module/path>::<PublicTypeOrFunction>` — `<additive | behavior change | deprecation | breaking>`

### 2) Caller impact (`content/**`)
- `content/<area>/<file>.kt` — `<impact summary>`
- Required now? `<yes/no>`

### 3) Migration guidance
- Before:
  - `<old usage>`
- After:
  - `<new usage>`
- Rollout steps:
  1. `<step 1>`
  2. `<step 2>`

### 4) Tests (`api/testing`)
- Added/Updated: `<test file or class>`
- Coverage notes: `<what behavior is now verified>`

### 5) Docs
- `docs/TRANSLATION_CHEATSHEET.md` — `<updated | not relevant>`
- Other docs: `<paths>`

## Release Notes (Content Agents)
- **What changed:** `<one-paragraph summary>`
- **Who must act:** `<content modules/owners>`
- **Action required:** `<exact update content agents should make>`
- **Safe default if unchanged:** `<runtime fallback/behavior>`
- **Deadline/version target:** `<milestone or release window>`
```
