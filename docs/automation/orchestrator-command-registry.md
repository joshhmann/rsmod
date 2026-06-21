# Orchestrator Command Registry

## Purpose

Define short RS_* commands that reduce Josh's orchestration prompts from long paragraphs to single line commands. Mai expands each command using the registry entries below.

## Command Shortcuts

### RS_STATUS
Recover latest commit, latest worklog, system status, zone readiness, recommended next task.

**Expansion:**
1. SSH CT 123: `git log -1 --oneline`, read latest worklog
2. Read `docs/automation/zone-readiness-checklist.md`
3. Read `docs/roadmap/system-status-matrix.md`
4. Read `docs/automation/NEXT_ACTION.md`
5. Report: last commit, last worklog entry, status summary, recommended next task

**Workflow:** rsmod-worklog-updater
**Mode:** ANY
**Risk Level:** 0 (read-only)
**Target Host:** CT123
**Validation:** None (read-only)
**Documentation:** Report only

### RS_CONTINUE
Recover state and continue the highest-priority non-blocked recommended task.

**Expansion:**
1. Run RS_STATUS to get current state
2. From NEXT_ACTION.md or zone-readiness-checklist, identify highest-priority ready task
3. If task has a command alias, expand it
4. Otherwise, decompose into kanban card and dispatch

**Workflow:** rsmod-content-orchestrator (router)
**Mode:** ASSISTED_MODE or SAFE_AUTONOMOUS_MODE
**Risk Level:** Up to 3 (depends on task)
**Target Host:** CT123
**Validation:** Per workflow
**Documentation:** Per workflow

### RS_NEXT_SAFE
Choose the next task allowed by the current operating mode and risk policy.

**Expansion:**
1. Read `/root/.night-run-state.json` (if exists) for current mode
2. Filter zone-readiness-checklist by mode's max risk level
3. Recommend the highest-priority task within those constraints
4. If mode is NIGHT_RUN_MODE, enforce batch limits

**Workflow:** rsmod-content-orchestrator (router)
**Mode:** Current operating mode
**Risk Level:** Dynamic (filtered by mode)
**Target Host:** CT123
**Validation:** Per task
**Documentation:** Per task

### RS_STEP_2_HANDOFF
Run staged-code delegated handoff test. Risk level 2, ASSISTED_MODE, promotion_allowed: false.

**Expansion:**
1. Create kanban card with:
   - workflow: rsmod-corpus-drops (or specified workflow)
   - content_type: drop_tables
   - content_area: staged-code-test
   - risk_level: 2
   - mode: ASSISTED_MODE
   - target_host: CT123
   - sync_pattern: sandbox_to_ct123
   - promotion_allowed: false
2. Assign to specified worker (default: rei)
3. Worker generates staged code + handoff artifacts
4. Worker blocks with handoff-ready
5. Mai applies to CT 123 staging only
6. Mai validates (raw-ID scan, production path check)
7. Mai commits staging artifact
8. Report: card id, commit hash, validation results

**Workflow:** rsmod-corpus-drops (or specified)
**Mode:** ASSISTED_MODE
**Risk Level:** 2
**Target Host:** CT123
**Validation:** Raw-ID scan, production path check, no combat registration
**Documentation:** Worklog + kanban complete

### RS_M1_MINING_QA
Run Mining 1-30 playerbot QA workflow.

**Expansion:**
1. Route to `rsmod-skill-validation` + `rsmod-playerbot-qa`
2. Create kanban card(s) with:
   - content_type: skill_validation
   - content_area: mining
   - skill_range: 1-30
3. Assign to rei or tai
4. Worker runs validation scenario against test bots
5. Report: pass/fail per level bracket, regression findings

**Workflow:** rsmod-skill-validation, rsmod-playerbot-qa
**Mode:** ASSISTED_MODE or SAFE_AUTONOMOUS_MODE
**Risk Level:** 3
**Target Host:** CT123
**Validation:** Playerbot regression suite
**Documentation:** Test report + worklog

### RS_M1_COOKING_WC_QA
Run Cooking + Woodcutting 1-30 validation.

**Expansion:**
1. Route to `rsmod-skill-validation` + `rsmod-playerbot-qa`
2. Create kanban cards with:
   - content_type: skill_validation
   - content_area: cooking, woodcutting
   - skill_range: 1-30
3. Assign to rei or tai
4. Workers run validation scenarios
5. Report: aggregated pass/fail per skill

**Workflow:** rsmod-skill-validation, rsmod-playerbot-qa
**Mode:** ASSISTED_MODE or SAFE_AUTONOMOUS_MODE
**Risk Level:** 3
**Target Host:** CT123
**Validation:** Playerbot regression suite
**Documentation:** Test report + worklog

### RS_KANBAN_DISPATCH
Decompose safe work into kanban cards with workflow/skills metadata.

**Expansion:**
1. Read current zone-readiness-checklist for ready zones
2. For each unstarted zone/NPC family, create a kanban card with:
   - Proper workflow assignment (drops -> rsmod-corpus-drops, etc.)
   - content_type, content_area, risk_level
   - target_host, sync_pattern
   - Appropriate assignee
3. Dispatch cards via kanban_create with dependency links
4. Report: cards created, total work items, estimated batch count

**Workflow:** rsmod-content-orchestrator (decomposition)
**Mode:** ASSISTED_MODE or SAFE_AUTONOMOUS_MODE
**Risk Level:** 2 (planning only)
**Target Host:** CT123 (cards reference it)
**Validation:** Card metadata completeness check
**Documentation:** Card list + worklog

### RS_NIGHT_DRY_RUN
NIGHT_RUN_MODE dry run. No production code commits. Docs, staged outputs, QA, reports only.

**Expansion:**
1. Load NIGHT_RUN_MODE from night-run-policy.md
2. Read `/root/.night-run-state.json` for state
3. Filter zone-readiness-checklist to unstarted, low-risk zones
4. For each candidate:
   - Generate staged output in sandbox
   - Write handoff artifacts (report, validation-notes, manifest)
   - DO NOT commit to production
5. Write morning report-style summary of what was produced
6. Report: total staged items, estimated production time

**Workflow:** rsmod-content-orchestrator (night-run)
**Mode:** NIGHT_RUN_MODE
**Risk Level:** 3 (staging only, no commits)
**Target Host:** CT123 (staging dir only)
**Validation:** No production path changes
**Documentation:** Morning report + staged artifacts

### RS_NIGHT_LIMITED
Limited live night run. Max one production commit. Level 5 workflows only. Stop after first success or first failure.

**Expansion:**
1. Load NIGHT_RUN_MODE from night-run-policy.md
2. Read `/root/.night-run-state.json` for state
3. Find highest-priority Level 5 certified task
4. Execute full lifecycle (sandbox -> SCP -> validate -> commit)
5. If success: stop (only one commit allowed)
6. If failure: block card, write failure report, stop

**Workflow:** rsmod-content-orchestrator (night-run)
**Mode:** NIGHT_RUN_MODE (limited)
**Risk Level:** 3
**Target Host:** CT123
**Validation:** Compile + raw-ID + worklog
**Documentation:** Night run report + worklog

### RS_BLOCKED_REVIEW
Summarize blocked cards, reasons, needed decisions.

**Expansion:**
1. Query kanban board for blocked cards
2. For each card, extract: title, block reason, assignee, blocked since
3. Group by block reason category
4. Report: count, per-card summary, recommendations for unblocking

**Workflow:** rsmod-content-orchestrator (review)
**Mode:** ANY
**Risk Level:** 0 (read-only)
**Target Host:** Local (kanban board)
**Validation:** None
**Documentation:** Report only

### RS_WORKLOG
Update worklog/status/roadmap from latest completed work.

**Expansion:**
1. Query kanban board for recently completed tasks (last 24h)
2. For each completed task: extract summary, metadata, commit hash
3. Update docs/worklog/ with entry
4. Update docs/roadmap/system-status-matrix.md with progress
5. Commit docs changes

**Workflow:** rsmod-worklog-updater
**Mode:** ANY
**Risk Level:** 1 (docs only)
**Target Host:** CT123
**Validation:** None (docs only)
**Documentation:** Updated worklog + status matrix

## Command-to-Workflow Mapping

| Command | Primary Workflow | Secondary Workflow |
|:--------|:-----------------|:-------------------|
| RS_STATUS | rsmod-worklog-updater | -- |
| RS_CONTINUE | rsmod-content-orchestrator | Per task |
| RS_NEXT_SAFE | rsmod-content-orchestrator | Per task |
| RS_STEP_2_HANDOFF | rsmod-corpus-drops | -- |
| RS_M1_MINING_QA | rsmod-skill-validation | rsmod-playerbot-qa |
| RS_M1_COOKING_WC_QA | rsmod-skill-validation | rsmod-playerbot-qa |
| RS_KANBAN_DISPATCH | rsmod-content-orchestrator | -- |
| RS_NIGHT_DRY_RUN | rsmod-content-orchestrator | -- |
| RS_NIGHT_LIMITED | rsmod-content-orchestrator | -- |
| RS_BLOCKED_REVIEW | rsmod-content-orchestrator | -- |
| RS_WORKLOG | rsmod-worklog-updater | -- |

## Usage Rules

1. Josh sends a command (e.g., "RS_STATUS") or command + capsule
2. Mai expands using this registry
3. Mai does NOT ask Josh to restate known global rules
4. If ambiguous, Mai runs RS_STATUS and recommends safest next action
5. Every command expands to: workflow, mode, risk level, target host, validation, documentation, completion report format

## See Also

- `task-capsule-format.md` -- task capsule format
- `next-action-pointer.md` -- NEXT_ACTION.md pointer
- `orchestrator-routing-table.md` -- routing table
- `kanban-to-orchestrator-routing.md` -- kanban card routing
