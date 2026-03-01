---
name: osrs-qa-tester
description: Expert QA workflow for OSRS mechanics/quests/skills in RSMod. Use when validating behavior against OSRS Wiki, detecting missing UI/interactions, verifying quest start signals, and producing structured pass/partial/fail reports with learnings.
---

# OSRS Nerd QA Tester

Use this skill when the task is to validate gameplay behavior, not just compile/build.

## Required Inputs

- Feature under test (quest/skill/mechanic)
- Player name for MCP execution
- Expected behavior source (OSRS Wiki + in-repo spec)
- Clear success criteria

## Hard Rules

1. Always compare observed behavior to OSRS Wiki expectations.
2. Always call out missing UX signals:
   - missing chat lines
   - missing quest start state
   - missing interface/journal updates
   - missing interaction options
3. Never mark pass if core loop is incomplete.
4. For every run, record learnings to `mcp/learnings/` (via `learning_session` metadata when available, or manual note fallback).

## What Must Be Checked

### Quest Start Signals (mandatory for quest QA)

- Dialogue start lines appear
- Quest state changes from not-started to started
- Quest journal/summary updates
- Player-facing confirmation exists (message/UI state)
- If applicable: third-party overlay alignment note (for example RuneLite not recognizing start)

### Interaction Integrity

- Target interaction is discoverable and clickable
- Player can path to target without invalid stalls
- Door/building transitions work (open/enter/interact)
- Failure paths are explicit (blocked, missing item, wrong stage)

### State + Rewards

- Stage/varp progression is correct
- Required items are consumed only when expected
- Rewards are granted correctly (XP/items/quest points)
- Repeat interactions after completion behave correctly

## Execution Workflow

1. Define expected behavior:
   - Pull quest/skill expectations from OSRS Wiki.
   - Pull local implementation expectations from `docs/CONTENT_AUDIT.md`, quest/skill module files, and test plans.
2. Build a minimal reproducible script:
   - setup -> trigger -> verify start/progress/completion -> verify fail path.
3. Run via MCP `execute_script`:
   - prefer deterministic coordinates and explicit waits.
4. Evaluate with strict verdict:
   - `PASS`: all required signals and state transitions present.
   - `PARTIAL`: core action works, but one or more required UX/state signals missing.
   - `FAIL`: core loop broken or progression incorrect.
5. Persist learnings:
   - use `learning_session`, `tested`, `worked`, `stuck`, `live_alignment` metadata where supported.
   - otherwise append manually to a markdown file in `mcp/learnings/`.

## Output Format

Always return:

- Verdict: `PASS` | `PARTIAL` | `FAIL`
- Tested: exact scenario executed
- Worked: confirmed behavior
- Missing/Broken: exact gaps (UI/interactions/state)
- Live Alignment: where behavior matches or diverges from OSRS Wiki/live client expectations
- Next Fixes: concrete implementation tasks

## Example Gaps To Flag

- "Quest accepted dialogue played, but quest did not transition to started stage."
- "Quest started server-side, but no player-facing start confirmation message."
- "Door interaction exists, but pathing stalls at doorway and blocks NPC talk."
- "RuneLite/overlay did not reflect quest start; likely missing quest-state signal exposure."
