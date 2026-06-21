# Kanban-to-Orchestrator Routing

## Purpose

Define how a kanban-dispatched worker loads the orchestrator, detects workflow requirements from card metadata, routes to the correct workflow, executes the lifecycle, and completes the card with structured handoff data.

## Worker Startup Sequence

When a new Hermes session starts via kanban dispatch:

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
validation:
  - raw_id_scan
  - compile
  - worklog_update
```

## Workflow Assignment Table

| content_type | workflow | Kanban Assignee |
|--------------|----------|:---------------:|
| drop_tables | rsmod-corpus-drops | mai |
| skill_validation | rsmod-skill-validation | mai/rei |
| shop_stock | rsmod-shop-stock | mai |
| zone_readiness | rsmod-zone-readiness | mai |
| minigame_spec | rsmod-minigame-spec | mai/nei |
| quest_spec | rsmod-quest-spec | mai/nei |
| playerbot_qa | rsmod-playerbot-qa | tai/rei |
| agent_playtest | rsmod-agent-playtest | tai |
| docs_update | rsmod-worklog-updater | any |

## Card Completion Handoff

After executing the lifecycle, call:

```yaml
kanban_complete(
    summary="Edgeville batch: Black Knight + Hill Giant promoted",
    metadata={
        "commit": "11a026d6",
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
