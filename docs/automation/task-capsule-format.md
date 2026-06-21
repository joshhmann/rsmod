# Task Capsule Format

## Purpose

Standardized YAML-like format for Josh to supply additional context alongside command shortcuts. Reduces ambiguity while remaining terse.

## Format

```
command: RS_STEP_2_HANDOFF
mode: ASSISTED_MODE
worker: rei
candidate: Karamja dry-run
promotion_allowed: false
target_host: CT123
notes: optional context
```

## Fields

| Field | Required | Default | Description |
|:------|:--------:|:--------|:------------|
| `command` | Yes | -- | RS_* command shortcut from registry |
| `mode` | No | From command registry | Override operating mode |
| `worker` | No | From command registry | Specific worker/sister assignment |
| `candidate` | No | From NEXT_ACTION.md | Target zone/NPC/content |
| `promotion_allowed` | No | From command registry | Override promotion gate |
| `target_host` | No | CT123 | Deployment target |
| `notes` | No | -- | Free-text context for Mai |

## Examples

### Minimal
```
RS_STATUS
```

### With capsule
```
RS_STEP_2_HANDOFF
worker: rei
candidate: Karamja Pirate
```

### Night run
```
RS_NIGHT_LIMITED
notes: Try Lesser Demon field drops, already Level 5 certified
```

### Decomposition
```
RS_KANBAN_DISPATCH
candidate: All unstarted F2P zones
notes: Create cards grouped by region, 10 NPC families per batch
```

## Expansion Behavior

When Josh sends a command-only message (no capsule):
- Mai uses defaults from the command registry
- Mai checks NEXT_ACTION.md for recommended parameters
- Mai reports the expanded form before executing

When Josh sends a command + capsule:
- Capsule fields override defaults
- Missing fields fall back to defaults
- Mai reports the merged expansion before executing

## Rules

1. Every capsule must contain `command:` as the first line
2. Fields are `key: value` format (YAML-like)
3. Multiline values are NOT supported -- use `notes:` for short context only
4. Capsules are optional -- commands work standalone with defaults

## See Also

- `orchestrator-command-registry.md` -- command registry
- `next-action-pointer.md` -- NEXT_ACTION.md pointer
