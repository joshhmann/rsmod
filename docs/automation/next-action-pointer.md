# Next Action Pointer

## Purpose

Single authoritative source for the current recommended next RS orchestration action. Mai reads this on fresh-session recovery and reports to Josh before waiting for a command.

## Location

The pointer lives at:
```
docs/automation/NEXT_ACTION.md
```

## Behavior

### Fresh Session Recovery
When Josh says "Start RS orchestration" (or similar):
1. Mai recovers session context (git log, worklog, status)
2. Mai reads `docs/automation/NEXT_ACTION.md`
3. Mai reports the current recommended command
4. Mai waits for Josh to say "continue" or provide another command

### After Task Completion
After each completed task:
1. Mai reads zone-readiness-checklist and system-status-matrix
2. Mai determines the highest-priority next unstarted or ready task
3. Mai updates `docs/automation/NEXT_ACTION.md` with the recommendation
4. If a new NEXT_ACTION differs from the previous, Mai notes the change

### Command Expansion
When Josh sends a command without a capsule:
1. Mai reads NEXT_ACTION.md for recommended parameters
2. Mai fills in defaults from the command registry
3. Mai reports the expanded form before executing

## Format

```
# Next Recommended RS Orchestration Action
Command: RS_NEXT_SAFE
Mode: SAFE_AUTONOMOUS_MODE
Risk Level: 3
Worker: mai
Target Host: CT123
Promotion Allowed: true
Reason: Lumbridge zones complete; next priority is Varrock guard drops.
Updated: 2026-06-21T04:37:00Z
```

## See Also

- `orchestrator-command-registry.md` -- command definitions
- `task-capsule-format.md` -- task capsule format
- `zone-readiness-checklist.md` -- zone completion tracking
- `system-status-matrix.md` -- system-level status
