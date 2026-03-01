# Master QA Playbook

This is the canonical QA document for OSRS-PS-DEV.

Last consolidated: 2026-02-24
Sources merged:
- docs/testing/IMPLEMENTATION_QA_CHECKLIST.md
- docs/testing/OSRS_QA_REPORT_TEMPLATE.md
- docs/QUEST_QA_REPORT.md
- docs/F2P_COMPLETION_CHECKLIST.md

---

## 1) Sprint QA Checklist (Execution)

Use this for implement -> build -> QA passes.

- Start server and log in with a test character.
- Run module-specific smoke tests (feature touched in sprint).
- Run cross-system regressions (combat, death, inventory, UI, networking as applicable).
- Record PASS/FAIL/PARTIAL with exact repro notes.
- For failures, create/attach task IDs immediately.

### Core Regression Buckets

- Skills (gathering, artisan, support)
- Combat/mechanics (splash XP, poison/venom, aggression, freeze/stun, specials)
- Death/kept-items/drop behavior
- Quests touched by sprint
- Area/NPC interactions
- UI systems (bank, trade, GE, friends/ignore, music)
- AgentBridge and protocol-sensitive flows

---

## 2) QA Report Template (Use Per Feature)

## Metadata
- Session: `<session-name>`
- Feature: `<quest/skill/mechanic>`
- Player: `<player-name>`
- Date: `<YYYY-MM-DD>`
- Tester: `<agent-or-human>`
- Sources: `<wiki/docs/code refs>`

## Expected Behavior
- `<expected behavior>`

## Test Steps
1. `<setup>`
2. `<action>`
3. `<verify state>`
4. `<verify UI/chat/output>`
5. `<verify rewards/fail paths>`

## Observed Results
- Worked:
  - `<confirmed behavior>`
- Missing/Broken:
  - `<defect>`

## Verdict
- `PASS` | `PARTIAL` | `FAIL`

Reason:
- `<short justification>`

## Live Alignment
- Matches:
  - `<aligned behavior>`
- Diverges:
  - `<difference>`

## Blockers + Follow-up Tasks
1. `<task-id or description>`
2. `<task-id or description>`

---

## 3) Quest QA Gate (Definition of Done)

A quest is QA-complete only if all are true:

- No stub/TODO logic in live path.
- Stage transitions complete and persistent.
- Item checks/handoffs complete.
- Rewards (XP/items/QP) applied correctly.
- Completion state and journal updates are correct.
- Happy path + at least one fail path validated.

---

## 4) F2P Completion Gate

Track F2P readiness against:

- Quest coverage
- Area definition of done (spawns, interactions, resources, AV)
- Global systems (transport, wilderness behavior, music, core UI)

For detailed historical snapshots, keep archive notes in:
- docs/QUEST_QA_REPORT.md
- docs/F2P_COMPLETION_CHECKLIST.md

---

## 5) Operating Rules

- One sprint = one scoped build + one QA pass.
- Capture defects with concrete repro steps.
- Prefer checklists over prose during active QA.
- If blocked by infra/tooling, record it explicitly and stop silent retries.

