# COORDINATOR SOP

This SOP defines the minimum operating standard for session coordinators managing parallel work in RSMod. It aligns directly with the required checklists, cadence, and assignment template guidance in `docs/AGENTS.md`.

## 1) Pre-Session Checklist

Before assigning any work, complete all items below:

- [ ] Read `docs/AGENTS.md` start-to-finish for current ownership, blockers, and operating constraints.
- [ ] Review `docs/CONTENT_AUDIT.md` to avoid duplicate assignments and to confirm priority modules.
- [ ] Review `docs/NEXT_STEPS.md` for session objectives and ordering.
- [ ] Confirm known core blockers from the **Current Blockers** section in `docs/AGENTS.md`.
- [ ] Confirm revision/source guardrails (rev 233 / OpenRS2 cache `runescape/2293`) are unchanged.
- [ ] Check active agents and current claims before issuing new work.
- [ ] Prepare task prompts using the **Coordinator Task Assignment Template** from `docs/AGENTS.md`.
- [ ] Ensure each task has:
  - a concrete target (no vague module-wide asks),
  - one task ID,
  - allowed/forbidden paths,
  - exact insertion point,
  - validation command sequence.
- [ ] Establish reporting rhythm: every assignee must post `agent_heartbeat` updates every 1–2 minutes while actively editing.

---

## 2) Assignment Issuance Procedure

Use this sequence for every assignment.

### Step A — Define the unit of work

1. Pick a single concrete outcome (e.g., one NPC behavior, one quest stage fix, one module-specific parity patch).
2. Verify it is not already completed or currently owned in `docs/AGENTS.md` and task registry.
3. Identify dependencies (engine/API blockers, symbol gaps, protocol constraints).

### Step B — Build the assignment packet

Create assignment text from the `docs/AGENTS.md` coordinator template with all required fields:

- **Task ID**
- **Objective** (single concrete outcome)
- **Claim command** (`claim_task`)
- **Allowed Paths**
- **Forbidden Paths** (always include `rsmod/engine/**` and `rsmod/api/**` unless specifically authorized)
- **Exact Insertion Point**
- **Pre-Flight checks**
- **Validation Order**
- **Validation Command**
- **Completion Output requirements**
- **Blocker Rule** (>10 min blocked ⇒ post command + first error line + affected files + unlock)

### Step C — Issue and confirm

1. Send the packet to the assignee.
2. Require explicit acknowledgment of:
   - task ID,
   - allowed path boundaries,
   - validation command(s).
3. Require assignee to claim task and lock target files before editing.
4. Record ownership/status in `docs/AGENTS.md` where applicable.

### Step D — Enforce assignment quality gate

Reject and rewrite any assignment that is:

- vague (“work on skills”, “improve combat”),
- missing path boundaries,
- missing insertion point,
- missing a concrete validation command,
- incompatible with known blockers.

---

## 3) Heartbeat Monitoring Cadence

### Required cadence

- **Active editing phase:** heartbeat every **1–2 minutes**.
- **Build/test running:** heartbeat at start, and again on completion/failure.
- **Blocked state:** immediate heartbeat with blocker details; then update every 5–10 minutes until resolved/reassigned.

### Minimum heartbeat payload

Each heartbeat should include:

- agent name,
- current status (`active`, `blocked`, `validating`, `done`),
- current task ID,
- current file/module,
- concise message (what changed or what failed).

### Coordinator watch actions

- If no heartbeat for >3 minutes during active work, ping once.
- If no heartbeat for >5 minutes, treat as potential stall and trigger triage.
- If blocked >10 minutes without required blocker detail, halt task progress and request compliant blocker report.

---

## 4) Blocker Triage Flow

Use this deterministic flow:

1. **Classify blocker**
   - Scope: local task issue vs shared/systemic blocker.
   - Type: compile, test, runtime, data/symbol mismatch, ownership/lock conflict.
2. **Require complete blocker report**
   - exact failing command,
   - first error line,
   - affected files/paths,
   - elapsed blocked time.
3. **Check for known blocker match**
   - compare against `docs/AGENTS.md` Current Blockers.
4. **Choose action path**
   - **Quick fix (<10 min):** assignee resolves and continues.
   - **Dependency blocker:** mark task `partial/in_progress`, document dependency, re-scope assignee.
   - **Ownership/lock issue:** run lock recovery protocol and re-establish single owner.
   - **Systemic failure:** escalate to core systems track and pause dependent tasks.
5. **Document outcome**
   - update blocker registry section,
   - update assignment status,
   - communicate reroute or wait condition to affected assignees.

---

## 5) Completion Verification Checklist

A task is not complete until all items pass.

- [ ] Assignee ran required validation commands in prescribed order.
- [ ] Build/test output is attached with exact command strings.
- [ ] Changes are confined to allowed paths.
- [ ] No forbidden paths touched.
- [ ] Insertion point/integration location matches assignment.
- [ ] No duplicate registration/handler wiring was introduced.
- [ ] Gating policy respected (tasks with unresolved core dependency remain partial).
- [ ] Task registry marked complete and file locks released.
- [ ] Relevant docs updated (`docs/CONTENT_AUDIT.md`, `docs/AGENTS.md`, notes as required).
- [ ] Any residual risk or assumption is explicitly recorded.

Coordinator close-out output should include:

- changed files,
- validation command list + pass/fail,
- dependency notes,
- final status (`complete` or `partial with blocker`).

---

## 6) Post-Session Cleanup

At session end, coordinator must ensure:

- [ ] All in-flight tasks are either completed or explicitly handed off.
- [ ] Ownership table reflects actual state (no stale owners).
- [ ] Current Blockers list is current and deduplicated.
- [ ] Stale locks/claims are cleared.
- [ ] Session notes are captured for each participating agent.
- [ ] Follow-up tasks are created with concrete IDs and scoped paths.
- [ ] Deferred items include dependency references and unblock criteria.
- [ ] Summary posted with:
  - completed tasks,
  - partial tasks,
  - active blockers,
  - next-session recommended start order.

---

## 7) One-Page Reference: Bad vs Good Assignment Examples

> Based on the mandatory assignment quality rules and coordinator template in `docs/AGENTS.md`.

### Bad Example 1 (Too vague)

**Bad**

```text
Please improve combat for giants.
```

**Why bad**

- No task ID.
- No specific target entity.
- No allowed/forbidden paths.
- No insertion point.
- No validation command.

**Good rewrite**

```text
Task ID: NPC-HILL-GIANT-F2P
Objective: Implement Hill Giant combat behavior and wiring.

Claim:
- claim_task(taskId="NPC-HILL-GIANT-F2P", agent="<agent-name>")

Allowed Paths:
- rsmod/content/other/npc-drops/**
- rsmod/content/mechanics/aggression/**

Forbidden Paths:
- rsmod/engine/**
- rsmod/api/**

Exact Insertion Point:
- NpcDropTablesScript.kt: append register call inside startup(), immediately before closing brace.

Validation Command:
- & 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat :content:other:npc-drops:build --console=plain"

Blocker Rule:
- if blocked >10 min: post exact command, first error line, affected file paths; unlock files.
```

### Bad Example 2 (Scope creep)

**Bad**

```text
Take care of all f2p NPCs and quest cleanups today.
```

**Why bad**

- Multiple outcomes bundled.
- No completion criteria per unit.
- Impossible to verify or parallelize cleanly.

**Good rewrite**

```text
Task ID: NPC-BARBARIAN-F2P-01
Objective: Add drop-table registration for Barbarian (specific NPC ID set only).

Allowed Paths:
- rsmod/content/other/npc-drops/**

Forbidden Paths:
- rsmod/engine/**
- rsmod/api/**
- rsmod/content/quests/**

Validation Command:
- & 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat :content:other:npc-drops:build --console=plain"

Completion Output:
- changed files
- exact command + result
- open questions
```

### Bad Example 3 (No validation)

**Bad**

```text
Patch this quickly; no need to run builds.
```

**Why bad**

- Violates mandatory validation workflow.
- Increases risk of silent regressions.

**Good rewrite**

```text
Task ID: QUEST-ROMEO-DIALOGUE-FIX-02
Objective: Correct dialogue branch state transition for Romeo stage <X>.

Validation Order (mandatory):
1) scripts\preflight-ref-hygiene.ps1
2) spotlessApply
3) scoped build
4) optional scoped test/bot check

Validation Command:
- & 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\preflight-ref-hygiene.ps1" -FailOnIssues
- & 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat :content:quests:romeo-and-juliet:build --console=plain"
```

### Quick Litmus Test for Coordinators

If an assignee cannot answer all five questions immediately, the assignment is not ready:

1. What exact task ID am I claiming?
2. What exact outcome defines done?
3. Where exactly can/can’t I edit?
4. Where exactly in file(s) should insertion happen?
5. What exact command proves success?
