# Sandbox-to-CT123 Handoff Contract

## Purpose

Structured handoff package for cross-profile kanban delegation. Any worker that cannot directly execute on CT 123 produces a handoff that Mai can apply, validate, and commit on CT 123. This makes cross-profile delegation safe and auditable.

## When to Use

- A **tai/rei/nei** profile worker needs to make code changes to the rsmod project
- A worker has been dispatched to a content task but lacks SSH access to CT 123
- Mai is the executor (applies + validates + commits) while another worker produced the staged output

## Handoff Directory Format

```
staging/handoffs/<task-id>/
├── manifest.yaml          # Required — machine-readable task spec
├── report.md              # Required — human-readable summary for Mai
├── validation-notes.md    # Required — what was checked, what needs checking
└── files/                 # Required — generated/modified files
    ├── content/other/npc-drops/.../FooDropTables.kt
    ├── content/other/npc-drops/.../NpcDropTablesScript.kt
    └── docs/automation/zone-readiness-checklist.md
```

All paths in `files/` mirror the target repo structure exactly, relative to `/root/osrs-ps-dev/OSRS-PS-DEV/rsmod/`.

---

## manifest.yaml Format

```yaml
# Required identifiers
task_id: "kanban-42"
source_worker: "rei"              # Who generated this handoff
target_host: "ct123"              # Always ct123
target_repo: "/root/osrs-ps-dev/OSRS-PS-DEV/rsmod/"

# Workflow classification
workflow: "rsmod-corpus-drops"
content_type: "drop_tables"
content_area: "frisd_village"

# Deployment
sync_pattern: "scp-sandbox"       # sandbox -> SCP -> CT 123

# Files — list every file in files/ with source and target paths
files:
  - source: "files/content/other/npc-drops/tables/FooDropTables.kt"
    target: "content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcdrops/tables/FooDropTables.kt"
    type: "create"
  - source: "files/content/other/npc-drops/NpcDropTablesScript.kt"
    target: "content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcdrops/NpcDropTablesScript.kt"
    type: "patch"

# Validation required before commit
validation_required:
  - raw_id_scan               # Scan all new/modified files for raw cache IDs
  - module_compile            # ./gradlew :module:compileKotlin
  - server_compile            # ./gradlew :server:app:compileKotlin

# Expected compile commands
compile_commands:
  - "cd /root/osrs-ps-dev/OSRS-PS-DEV/rsmod && ./gradlew :content:other:npc-drops:compileKotlin"
  - "cd /root/osrs-ps-dev/OSRS-PS-DEV/rsmod && ./gradlew :server:app:compileKotlin"

# Docs to update after commit
docs_to_update:
  - "docs/automation/zone-readiness-checklist.md"
  - "docs/roadmap/system-status-matrix.md"
  - "docs/worklog/2026-06/2026-06-21-<task-name>.md"

# Raw ID policy
raw_id_policy: "fail_on_any"    # Any raw ID found = block commit

# Skip rules applied
skip_rules:
  - filter: "post-2013 items not in rev 233 cache"
  - filter: "wilderness-only drops outside wilderness zones"
  - filter: "clue scrolls (all tiers)"

# Suggested commit message
commit_message_suggestion: "feat(drops): Frisd Village — Foo drop table batch"
```

---

## report.md Format

```markdown
# Handoff: <task-id> — <title>

## Summary
<1-3 sentence description of what was generated>

## Source Worker
<profile name>

## Staged Output
| File | Type | Target Path |
|------|:----:|-------------|

## Targets Classified
| Target | Classification | Reason |
|--------|:--------------:|--------|
| Foo NPC | PROMOTE | Has combat, no drops, corpus has 42 valid items |
| Bar NPC | SKIP | Wilderness-only drops |

## Skip Reasons
- <target>: <detailed reason>

## Content Completion Level
SANDBOX_STAGED

## What Mai Must Do
1. Review manifest.yaml
2. Copy files/ to CT 123 targets
3. Run validation commands
4. If validation passes: commit
5. Update docs
6. Set completion level to CT123_VALIDATED or COMMITTED
```

---

## validation-notes.md Format

```markdown
# Validation Notes — <task-id>

## Validated by Worker (Sandbox)
- Symbol resolution: <checked / not checked>
- Corpus item filter: <list of skipped items>
- Existing table comparison: <notes>
- Raw-ID preview: <findings>
- Known gaps: <list>

## Needs Validation on CT 123
- Module compile
- Server compile
- Final raw-ID scan
- Git diff review
```

---

## Interaction Rules

| Situation | Worker Action | Mai Action |
|:----------|:--------------|:------------|
| Worker generates code but can't reach CT 123 | Create handoff package, set status to `SANDBOX_STAGED` | Receive handoff, SCP files, compile, validate, commit, set status to `COMMITTED` |
| Worker finds a blocker during sandbox work | Block with `kanban_block(reason="...")` | Review and unblock when resolved |
| Mai finds compile error on CT 123 | N/A (worker already done) | Report failure, do NOT commit, return task to ready for fix |
| Mai validation passes | N/A | Commit, update worklog, complete card, record BOTH source_worker and applying_worker in worklog |

---

## Completion States

| State | Meaning | Who Sets | Gate |
|:------|:--------|:---------|:------|
| **SANDBOX_STAGED** | Worker produced handoff package, files staged in `staging/handoffs/` | Source worker | manifest.yaml present |
| **CT123_APPLIED** | Mai copied files to CT 123 target paths | Mai | Files on CT 123 |
| **CT123_VALIDATED** | Compile + raw-ID pass on CT 123 | Mai | All validations green |
| **COMMITTED** | Changes committed to git on CT 123 | Mai | git log confirms |
| **DOCUMENTED** | Worklog, status matrix, docs updated | Mai | All docs updated |

A card should never skip states — each is a gate that prevents regressions.

---

## Example Handoffs

### Example 1: Drop Table Batch

```
staging/handoffs/kanban-42/
├── manifest.yaml
├── report.md
├── validation-notes.md
└── files/
    └── content/other/npc-drops/
        ├── src/.../tables/FooDropTables.kt        (new)
        └── src/.../NpcDropTablesScript.kt          (patched)
```

Target: CT 123, rsmod project.
Workflow: rsmod-corpus-drops.
Mai: SCP `files/` content to matching paths, run gradlew, commit.

### Example 2: Skill Validation Report

```
staging/handoffs/kanban-43/
├── manifest.yaml
├── report.md
├── validation-notes.md
└── files/
    └── docs/validation/skills/mining-1-30-report.md   (new)
```

Target: CT 123, rsmod project.
Workflow: rsmod-skill-validation.
No code changed — docs only. Mai copies report, commits.

### Example 3: Minigame Spec

```
staging/handoffs/kanban-44/
├── manifest.yaml
├── report.md
├── validation-notes.md
└── files/
    └── docs/specs/minigames/motherlode-mine-spec.md   (new)
```

Target: CT 123, rsmod project.
Workflow: rsmod-minigame-spec.
No code changed — spec only. Mai copies, commits.

---


## Night Run State Chain Extension

Under NIGHT_RUN_MODE, the completion state chain extends to handle autonomous validation:

| State | Who | Gate | Auto-Approved? |
|:------|:----|:-----|:--------------:|
| SANDBOX_STAGED | Source | manifest.yaml present | X |
| CT123_APPLIED | Mai | Files on target | X |
| CT123_VALIDATED | Mai | Compile + raw-ID pass | X (must pass) |
| COMMITTED | Mai | git log confirms | X (Level 1-3 only) |
| DOCUMENTED | Mai | All docs updated | X |

### Auto-Stop Integration
If any stop condition from `docs/automation/autonomous-stop-conditions.md` fires during the CT123_APPLIED -> COMMITTED phase:
1. Stop immediately
2. Do NOT commit unvalidated changes
3. Block the card with the stop reason
4. Write failure to run log (`docs/reports/<date>-failure.md`)

### Rollback on Night Run Failure
If a night-run commit later fails gameplay QA:
- Create revert card per `docs/automation/rollback-policy.md`
- Never auto-revert -- always record reason and create fix task
- Link to original worklog entry

### Handoff manifest.yaml extension for night run
```yaml
# Add to manifest.yaml:
risk_level: 3
mode_generated_in: NIGHT_RUN_MODE
stop_conditions_checked:
  - compile
  - raw_id
  - batch_limit
batch_slot: 3                      # which slot in the night batch sequence
```


## Pre-Flight Verification (Reality Check)

**Before writing ANY code, verify the claimed gap actually exists on CT 123.**

Workers dispatched from stale audit data routinely burn 90-iteration budgets trying to implement systems that already exist. This step prevents that.

### Required Checks

For each claim in the card body (e.g. "no bank booth handler", "no ditch crossing"):

```bash
# Verify the gap is real
ssh root@192.168.0.175 "cd /root/osrs-ps-dev/OSRS-PS-DEV/rsmod && \
  find content/ -name '*.kt' -path '*suspected-module*' 2>/dev/null | head -5 && \
  grep -rl 'suspected-symbol' content/ --include='*.kt' 2>/dev/null | head -5 && \
  git log --oneline -5 -- content/<suspected-area>/"
```

### Decision Tree

| Pre-Flight Result | Action |
|:------------------|:-------|
| Files already exist on CT 123 | **Close card as already-implemented.** Do NOT write new code. Note existing paths in handoff. |
| Files partially exist with gaps | Only write code for the genuinely missing parts. Do not rewrite existing working code. |
| Nothing exists | Proceed with normal handoff lifecycle. |

### Compile Pattern

Always compile individual modules first, never the full server build:

```bash
# GOOD -- individual module, ~15-30s
./gradlew :content:other:npc-drops:compileKotlin

# BAD -- full server build, ~120s+, catches errors from unrelated modules
./gradlew :server:app:compileKotlin
```

This rule was added after 3 workers exhausted 90-iteration budgets on already-implemented systems (bank booth, cooking/smithing/firemaking, wilderness ditch stub).
