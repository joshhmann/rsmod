# Kanban-to-Orchestrator Routing

## Purpose

Define how a kanban-dispatched worker loads the orchestrator, detects workflow requirements from card metadata, routes to the correct workflow, executes the lifecycle, and completes the card with structured handoff data.

## Target Host & Deployment

**All RSMod content work targets CT 123** (container 123, `192.168.0.175`).

The rsmod project is at `/root/osrs-ps-dev/OSRS-PS-DEV/rsmod/` on **CT 123**.

### Fleet Reference

| Host | IP | Purpose |
|:-----|:---:|:--------|
| **CT 123** | `192.168.0.175` | Primary build target (colloquially "175", hostname 2004scape) — rsmod project, game server, gradle, cache symbols |
| **CT 17** | `192.168.0.17` | Model server — 2x RTX 5060 Ti + RTX A4000, llama.cpp, corpus origin |
| **CT 111** | `192.168.0.162` | ComfyUI image generation server
Kanban workers interact with CT 123 via SSH from their sandbox workspace.

### Deployment Pattern: Sandbox → SCP → CT 123


```
+-----------------------------+
| Kanban Workspace (sandbox)  |  Local temp dir on worker host
|  - Write/modify files       |
|  - Query corpus data        |
|  - Generate staged output   |
+--------------+--------------+
               | SCP to CT 123
               v
+-----------------------------+
| CT 123: rsmod/ target dir   |  Remote build host (192.168.0.175)
|  - Compile (gradlew)        |
|  - Run raw-ID scan          |
|  - Verify via grep/git      |
|  - Commit to git            |
+--------------+--------------+
               | Live on CT 123
               v
+-----------------------------+
| Production (CT 123)         |  Committed and deployed
+-----------------------------+
```

### SSH Access

Workers in the **mai** profile have SSH access to CT 123 (`ssh root@192.168.0.175`).

Cross-profile workers (tai/rei/nei) that cannot directly execute on CT 123 **must produce a structured handoff package** instead of routing back:

1. Generate handoff to `staging/handoffs/<task-id>/`
2. Set card status to `SANDBOX_STAGED`
3. Mai picks up the handoff, applies to CT 123, validates, commits
4. See `docs/automation/sandbox-to-ct123-handoff.md` for full format

For docs-only or spec tasks where no code changes on CT 123 are needed:
- Use `target_host: local` and handle locally
- No handoff needed — commit directly to docs

### Why Sandbox

- Isolated from production until SCP'd
- Files are reviewed before transfer
- Compile/git tools exist on CT 123, not in sandbox
- CT 123 has the full cache and toolchain (JDK 21, Gradle 8.13, .sym files)
- Proven across 20+ commits and 10 regional batches

## Worker Startup Sequence

```
kanban_show() reads card
    |
    v
Detect workflow from card:
    1. body "workflow:" field (explicit)
    2. body "skills:" list (if orchestrator is first)
    3. Card title/body classification (fallback)
    |
    v
Load rsmod-content-orchestrator (at minimum)
Load specialized workflow skill
Execute lifecycle
    |
    v
kanban_complete() with structured metadata
```

## Card Metadata Format

Every kanban card should include these fields in its body:

```yaml
workflow: rsmod-corpus-drops
skills:
  - rsmod-content-orchestrator
  - <workflow-name>
content_type: drop_tables
content_area: edgeville
module_path: content/other/npc-drops
target_host: ct123
sync_pattern: scp
target_host: ct123
sync_pattern: scp
validation:
  - raw_id_scan
  - compile
  - worklog_update
```

## Workflow Assignment Table

| content_type | workflow | Kanban Assignee | Target Host | Deployment |
|--------------|----------|:---------------:|:-----------:|:----------:|
| drop_tables | rsmod-corpus-drops | mai | ct123 | scp sandbox |
| skill_validation | rsmod-skill-validation | mai/rei | ct123 | scp sandbox |
| shop_stock | rsmod-shop-stock | mai | ct123 | scp sandbox |
| zone_readiness | rsmod-zone-readiness | mai | any | local docs |
| minigame_spec | rsmod-minigame-spec | mai/nei | any | local docs |
| quest_spec | rsmod-quest-spec | mai/nei | any | local docs |
| playerbot_qa | rsmod-playerbot-qa | tai/rei | ct123 | ssh direct |
| agent_playtest | rsmod-agent-playtest | tai | ct123 | ssh direct |
| docs_update | rsmod-worklog-updater | any | any | local |

## Card Completion Handoff

After executing the lifecycle, call:

```yaml
kanban_complete(
    summary="Edgeville batch: Black Knight + Hill Giant promoted",
    metadata={
        "commit": "11a026d6",
        "target_host": "ct123",
        "deployment": "sandbox-scp",
        "files_changed": ["content/other/npc-drops/tables/BlackKnightDropTables.kt"],
        "promoted": ["Black Knight", "Hill Giant"],
        "skipped": [{"target": "Monk", "reason": "No combat registration"}],
        "validations_passed": ["compile", "raw_id"],
        "workflow_changes": false,
        "completion_level": "CONTENT_DONE",
        "next_recommendation": "Continue to Falador batch"
    }
)
```

## Multi-Task Decomposition

When the orchestrator receives a high-level goal (e.g., "Complete all drops for F2P zones"), it should:

1. Decompose into per-region kanban cards
2. Create each card with proper `workflow:`, `content_area:`, `skills:`
3. Set `parents: []` to express dependencies
4. Dispatch to the appropriate assignee profiles
5. Complete its own card with the decomposition summary

---

## Night Run Mode Integration


## Night Run Mode Integration

When operating under NIGHT_RUN_MODE, kanban workers receive additional constraints:

### Risk Classification Filter
Every card is classified using `docs/automation/task-risk-classifier.md` before dispatch:
- **Level 1-2** → dispatched normally
- **Level 3** → dispatched with batch limit enforcement (max 10 NPC families, max 5 files per task)
- **Level 4** → blocked, human-approval card created instead
- **Level 5** → always blocked, human required

### Night Run Worker Startup
Workers dispatched during NIGHT_RUN_MODE must check `/root/.night-run-state.json` (if exists) for current mode before proceeding. If the file indicates LOCKDOWN_MODE, the worker exits immediately without executing.

### Metadata Extension for Night Run
```yaml
# In card metadata, add:
risk_level: 3                    # from task-risk-classifier
mode_allowed: NIGHT_RUN_MODE     # highest mode this task runs in
batch_remaining: 7               # slot tracking if part of a night batch
```

### Completion States (Night Run)
| State | Auto-Approved? | Notes |
|:------|:--------------:|:------|
| SANDBOX_STAGED | X | Always allowed |
| CT123_APPLIED | X | After SCP |
| CT123_VALIDATED | X | Only if compile + raw-ID pass |
| COMMITTED | X | Level 1-3 only, within batch limits |
| DOCUMENTED | X | Required for all night-run tasks |

Policy references:
- `night-run-policy.md` — autonomous mode policy
- `task-risk-classifier.md` — risk classification
- `autonomous-stop-conditions.md` — stop conditions
- `morning-report-template.md` — report format

---

## Command Alias Support in Card Bodies

Cards can now use RS_* command aliases in their body to specify workflow behavior. This allows short commands to replace verbose metadata blocks.

### Card Body Format with Command Alias

Instead of specifying all metadata fields explicitly, a card body can reference a command shortcut:



### Expansion Rules

When the orchestrator detects a  field in a card body:

1. Load the command definition from 
2. Fill in all metadata fields from the command default expansion
3. Override with any capsule fields present in the card body
4. Merge with explicit , ,  etc. if also present (explicit fields win)
5. Route the task normally

### Example: Before (verbose)



### Example: After (with command alias)



### Integration with Orchestrator Startup

The orchestration startup sequence now includes command alias resolution:



### See Also

-  -- full command definitions
-  -- capsule field reference


---

## Command Alias Support in Card Bodies

Cards can now use RS_* command aliases in their body to specify workflow behavior. This allows short commands to replace verbose metadata blocks.

### Card Body Format with Command Alias

Instead of specifying all metadata fields explicitly, a card body can reference a command shortcut:

```
command: RS_STEP_2_HANDOFF
# Optional capsule overrides:
worker: rei
candidate: Karamja Pirate
promotion_allowed: false
```

### Expansion Rules

When the orchestrator detects a `command:` field in a card body:

1. Load the command definition from `docs/automation/orchestrator-command-registry.md`
2. Fill in all metadata fields from the command default expansion
3. Override with any capsule fields present in the card body
4. Merge with explicit `workflow:`, `content_type:`, `risk_level:` etc. if also present (explicit fields win)
5. Route the task normally

### Example: Before (verbose)

```
workflow: rsmod-corpus-drops
skills:
  - rsmod-content-orchestrator
  - rsmod-corpus-drops
content_type: drop_tables
content_area: staged-code-test
risk_level: 2
target_host: ct123
sync_pattern: sandbox_to_ct123
```

### Example: After (with command alias)

```
command: RS_STEP_2_HANDOFF
candidate: Karamja Pirate
```

### Integration with Orchestrator Startup

The orchestration startup sequence now includes command alias resolution:

```
kanban_show() reads card
    |
    v
Detect "command:" field in body?
    Yes -> Expand command from registry
            Apply capsule overrides
            Merge with any explicit fields
    No  -> Fall through to standard metadata detection
    |
    v
Load rsmod-content-orchestrator
Load specialized workflow skill
Execute lifecycle
    |
    v
kanban_complete() with structured metadata
```

### See Also

- `orchestrator-command-registry.md` -- full command definitions
- `task-capsule-format.md` -- capsule field reference

---

## Pre-Dispatch Sizing Gate (v3.4)

**Before any card is dispatched**, the orchestrator must classify it by size using `docs/automation/task-sizing-policy.md`.

### Dispatch Decision Flow

```yaml
Card arrives:
  1. Classify: XS? S? M? L? XL?
  2. Check auto-decomposition triggers (see §3 of sizing policy)
  3. If XL: decompose or spec-first, DO NOT dispatch
  4. If L: decompose or spec-first
  5. If S/M: dispatch with pre-flight requirement
  6. If XS: dispatch without pre-flight
```

### What Gets Added to Every Card

For S/M dispatch, the orchestrator must add these to the card body:

```yaml
mandatory:
  preflight_required: true           # for S/M
  preflight_files_to_search:         # based on card scope
    - "find content/ -name '*.kt' -path '*<target>*'"
    - "git log --oneline -5 -- content/<target>/"
  compile_command: "./gradlew :<module>:compileKotlin"
  max_iterations: <see sizing table>
```

### Sizing Reference

See `docs/automation/task-sizing-policy.md` for:
- Full class definitions (XS/S/M/L/XL)
- Auto-decomposition trigger phrases
- Iteration budget guard tables
- Already-exists closure protocol
- Delegation decision tree

### Existing Card Reclassification

When reclassifying existing cards:

1. Read card title + body
2. Classify by scope and trigger phrases
3. If L/XL: comment with reclassification notice, block card, create decomposition
4. If S/M but has pre-flight: add pre-flight requirement to card body
5. Update NEXT_ACTION.md with reclassification summary

### Context Snapshot Requirement

Every card created from an audit finding MUST include a `context_snapshot` block in its body. This allows the downstream worker to diff against the original probe results and detect stale data in ~2 iterations instead of ~90.

#### Format

```yaml
context_snapshot:
  taken_at: "2026-06-21T00:00:00Z"    # ISO timestamp of when snapshot was taken
  target_host: ct123                   # target hostname
  repo_path: /root/osrs-ps-dev/OSRS-PS-DEV/rsmod
  probes:                               # ordered list of probes (2-3 is sufficient)
    - type: find
      command: find content/skills/<name> -name '*.kt' 2>/dev/null | wc -l
      result: "0 files found"
    - type: git_log
      command: git log --oneline -3 -- content/<area>/
      result: "no commits found"
    - type: dir_check
      command: ls -la content/<area>/ 2>/dev/null | head -3
      result: "ls: cannot access: No such file or directory"
```

#### Worker Diff Protocol

1. Extract `context_snapshot.probes` from card body
2. Re-run each probe command on CT 123 (SSH)
3. Compare output with snapshot's `result` field:
   - **All match** → gap still valid → PROCEED_IMPLEMENT
   - **Any mismatch** → gap was closed since card creation → CLOSE_STALE_SNAPSHOT (2 iterations)
4. If no `context_snapshot` on card, fall through to full manual pre-flight

#### Probe Types

| Type | Checks | Output Example |
|:-----|:-------|:---------------|
| `find` | File existence count | "0 files found" / "5 files found (790 lines)" |
| `git_log` | Recent commits in area | "no commits found" / "a1b2c3 feat: add cooking module" |
| `dir_check` | Directory existence | "No such directory" / "Cooking.kt  Firemaking.kt  module.kt" |
| `grep` | Pattern presence | "0 matches" / "3 files match 'onOpNpc1'" |
| `stat` | File metadata | "stat: cannot stat: No such file or directory" |

#### Example: Context Snapshot in Action

**Card body excerpt:**
```yaml
workflow: rsmod-corpus-drops
content_type: drop_tables
content_area: karamja-pirate
risk_level: 3
context_snapshot:
  taken_at: "2026-06-21T05:30:00Z"
  target_host: ct123
  probes:
    - type: find
      command: find content/ -name '*pirate*' -o -name '*Pirate*' 2>/dev/null
      result: "0 files found"
```

**Worker pre-flight result (2 iterations later):**
```yaml
Re-running probe: find content/ -name '*pirate*'
  Snapshot: "0 files found"
  Current:  "0 files found"
  ✅ Match — gap valid, proceed
```
