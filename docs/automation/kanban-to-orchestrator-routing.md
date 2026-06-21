# Kanban-to-Orchestrator Routing

## Purpose

Define how a kanban-dispatched worker loads the orchestrator, detects workflow requirements from card metadata, routes to the correct workflow, executes the lifecycle, and completes the card with structured handoff data.

## Target Host & Deployment

**All RSMod content work targets CT 175** (container 175, `192.168.0.175`).

The rsmod project is at `/root/osrs-ps-dev/OSRS-PS-DEV/rsmod/` on **CT 175**.

### Fleet Reference

| Host | IP | Purpose |
|:-----|:---:|:--------|
| **CT 175** | `192.168.0.175` | Primary build target — rsmod project, game server, gradle, cache symbols |
| **CT 17** | `192.168.0.17` | Model server — 2x RTX 5060 Ti + RTX A4000, llama.cpp, corpus origin |
| **CT 111** | `192.168.0.162` | ComfyUI image generation server
Kanban workers interact with CT 175 via SSH from their sandbox workspace.

### Deployment Pattern: Sandbox → SCP → CT 175


```
+-----------------------------+
| Kanban Workspace (sandbox)  |  Local temp dir on worker host
|  - Write/modify files       |
|  - Query corpus data        |
|  - Generate staged output   |
+--------------+--------------+
               | SCP to CT 175
               v
+-----------------------------+
| CT 175: rsmod/ target dir   |  Remote build host (192.168.0.175)
|  - Compile (gradlew)        |
|  - Run raw-ID scan          |
|  - Verify via grep/git      |
|  - Commit to git            |
+--------------+--------------+
               | Live on CT 175
               v
+-----------------------------+
| Production (CT 175)         |  Committed and deployed
+-----------------------------+
```

### SSH Access

Workers in the **mai** profile have SSH access to CT 175 (`ssh root@192.168.0.175`).
Cross-profile workers (tai/rei/nei) should either:
1. Be dispatched with `target_host: local` for docs-only tasks, OR
2. Route execution back to the **mai** profile via kanban child task

### Why Sandbox

- Isolated from production until SCP'd
- Files are reviewed before transfer
- Compile/git tools exist on CT 175, not in sandbox
- CT 175 has the full cache and toolchain (JDK 21, Gradle 8.13, .sym files)
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
target_host: ct175
sync_pattern: scp
target_host: ct175
sync_pattern: scp
validation:
  - raw_id_scan
  - compile
  - worklog_update
```

## Workflow Assignment Table

| content_type | workflow | Kanban Assignee | Target Host | Deployment |
|--------------|----------|:---------------:|:-----------:|:----------:|
| drop_tables | rsmod-corpus-drops | mai | ct175 | scp sandbox |
| skill_validation | rsmod-skill-validation | mai/rei | ct175 | scp sandbox |
| shop_stock | rsmod-shop-stock | mai | ct175 | scp sandbox |
| zone_readiness | rsmod-zone-readiness | mai | any | local docs |
| minigame_spec | rsmod-minigame-spec | mai/nei | any | local docs |
| quest_spec | rsmod-quest-spec | mai/nei | any | local docs |
| playerbot_qa | rsmod-playerbot-qa | tai/rei | ct175 | ssh direct |
| agent_playtest | rsmod-agent-playtest | tai | ct175 | ssh direct |
| docs_update | rsmod-worklog-updater | any | any | local |

## Card Completion Handoff

After executing the lifecycle, call:

```yaml
kanban_complete(
    summary="Edgeville batch: Black Knight + Hill Giant promoted",
    metadata={
        "commit": "11a026d6",
        "target_host": "ct175",
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
