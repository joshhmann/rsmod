# Coordinator SOP

This SOP defines strict assignment, monitoring, and closeout steps for multi-agent execution.

## 1) Pre-Session Checks

1. Confirm source-of-truth docs:
   - `docs/README.md`
   - `docs/AGENTS.md`
   - `docs/NEXT_STEPS.md`
2. Review open blockers and ownership in `docs/AGENTS.md`.
3. Prioritize Tier 0 / Tier 0.5 tasks before new content when stability gates are red.

## 2) Assignment Issuance (Required Format)

Every assignment must include all fields:
- Task ID
- objective (single concrete target)
- allowed paths
- forbidden paths
- exact insertion point
- validation command(s)

If any field is missing, assignee must not edit files.

## 3) Monitoring During Execution

1. Ensure task is claimed and files are locked before edits.
2. Require heartbeat updates every 1-2 minutes for active tasks.
3. Enforce blocker protocol when blocked longer than 10 minutes:
   - exact command
   - first error line
   - impacted file path(s)
   - owner/action requested

## 4) Completion Verification Checklist

Do not accept completion unless all are present:
1. Changed file list.
2. Key edit summary tied to task objective.
3. Validation command output (exact commands).
4. DoD compliance from `docs/AGENTS.md`.
5. Correct status (`complete`, `partial`, `in_progress`) based on evidence.

## 5) Session Closeout

1. Resolve or reassign blockers with explicit owners.
2. Ensure notes/handoff entries are written.
3. Confirm no orphaned locks remain.
4. Confirm Gradle/Java cleanup commands were run if build/test/server executed.

## Bad vs Good Assignment

Bad:
- "Work on quest fixes"

Good:
- Task ID: QUEST-F2P-17
- Objective: Implement Misthalin Mystery stage transition when giving item X to NPC Y.
- Allowed Paths: `content/quests/misthalin-mystery/**`
- Forbidden Paths: `engine/**`, `api/**`, `content/interfaces/**`
- Exact Insertion Point: `MisthalinMysteryScript.kt` inside `startup()` before closing brace.
- Validation Command: `./gradlew :content:quests:misthalin-mystery:build --console=plain`
