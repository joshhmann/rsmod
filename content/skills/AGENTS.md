# SKILLS — Content Implementation Guide

**Owner**: Claude (content implementer)

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

Delegate only low-risk skill/content changes to secondary agents (npc dialogue text, shop stock
lists, spawn placement, and isolated script/config edits).

Do **not** delegate critical work to secondary agents:
- `content/mechanics/**`
- `content/interfaces/**` (UI)
- `api/**` core behavior changes
- `engine/**`
- cross-module refactors or protocol-sensitive logic

If a task touches mechanics, UI, networking, death/combat internals, or persistence, keep it with
the primary maintainer and split out safe subtasks if needed.

## DELEGATED TASK CONTRACT (REQUIRED FOR ALL TASKS)

Any task delegated to another agent/model must include this contract in the assignment:

1. Exact objective (single scoped outcome).
2. Allowed file list (explicit paths only).
3. Forbidden paths (`engine/**`, `api/**` core internals, `content/mechanics/**`,
   `content/interfaces/**`, plus any task-specific exclusions).
4. Reuse requirement: follow an existing in-repo pattern file.
5. Validation command (module-scoped Gradle compile/build task).
6. Output requirement: changed files, key edits, assumptions, unresolved questions.
7. Uncertainty rule: if uncertain, stop and leave a TODO note instead of guessing.

## OVERVIEW

Skill plugins for RSMod v2. 10 complete skills (woodcutting, mining, fishing, cooking, firemaking, thieving, prayer, herblore, fletching, smithing).

## STRUCTURE

```
skill-name/
├── build.gradle.kts           # Module deps
├── src/main/kotlin/.../skill-name/
│   ├── SkillModule.kt         # PluginModule bindings
│   ├── configs/               # Data classes (rates, tools)
│   └── scripts/               # PluginScript handlers
└── src/integration/kotlin/    # GameTestExtension tests
```

## WHERE TO LOOK

| Task | Location | Notes |
|------|----------|-------|
| Template | `woodcutting/` | Gold standard |
| Patterns | `docs/TRANSLATION_CHEATSHEET.md` | Alter → RSMod v2 |
| XP data | `wiki-data/skills/` | Oracle JSON files |

## CONVENTIONS

- **Plugin:** `class Skill @Inject constructor(...) : PluginScript()`
- **Handlers:** `onOpLoc1`, `onOpNpc1`, `onOpHeldU`, `onOpObj1`
- **Data:** Companion object or enum; large tables → `wiki-data/` JSON
- **Player actions:** Wrap in `ProtectedAccess` for safe inv ops

## ANTI-PATTERNS

| ❌ Don't | ✅ Do |
|----------|-------|
| Hardcode IDs | `find("sym_name")` from BaseObjs |
| No tests | Write `bots/<skill>.ts` |
| Edit `engine/` | Gemini owns — request via AGENTS.md |
| Skip wiki-data | Add XP rates, test locations |

## IMPLEMENTATION CHECKLIST

- [ ] Check `docs/CONTENT_AUDIT.md` — confirm not done
- [ ] Copy `woodcutting/` pattern
- [ ] Add `wiki-data/skills/<name>.json`
- [ ] Write `bots/<skill>.ts` test
- [ ] Run `:content:skills:<name>:build`
- [ ] Update CONTENT_AUDIT.md → ✅

## SKILL ARCHETYPES

| Type | Skills | Pattern |
|------|--------|---------|
| Gathering (Loc) | Woodcutting, Mining | `onOpLoc` → tick loop → depletion |
| Gathering (NPC) | Fishing | `onOpNpc` → tick loop |
| Processing (Loc) | Cooking | `onOpLocU` → delay → transform |
| Processing (Item) | Firemaking, Herblore | `onOpHeldU` → delay → transform |
| Interactive | Thieving | `onOpNpc` → delay → success roll |
| Consumption | Prayer | `onOpHeld` → immediate effect |

## BUILD

```bash
# Single module (10s)
gradlew.bat :content:skills:smithing:build -x test

# With tests
gradlew.bat :content:skills:smithing:test
```
