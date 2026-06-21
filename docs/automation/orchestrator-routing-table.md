# Orchestrator Routing Table

## Classification

| Task Keywords | Content Type | Workflow | Auto Mode |
|---------------|-------------|----------|-----------|
| drops, drop table, npc loot, enrichment | drop_tables | rsmod-corpus-drops | Generator (L5) |
| cooking, woodcutting, mining, skill test, validate, 1-30 | skill_validation | rsmod-skill-validation | Validate first |
| shop, stock, store, StoreLine | shop_stock | rsmod-shop-stock | Generator (L1) |
| zone, readiness, checklist, region | zone_readiness | rsmod-zone-readiness | Checklist |
| minigame, Wintertodt, Motherlode, Tempoross | minigame_spec | rsmod-minigame-spec | Spec-first |
| quest, spec, dialogue, varbit | quest_spec | rsmod-quest-spec | Spec-first |
| boss, KBD, KQ, Elvarg | boss_spec | rsmod-minigame-spec | Spec-first |
| playerbot, qa, test scenario, regression | playerbot_qa | rsmod-playerbot-qa | Bot execution |
| agent, LLM, playtest, agent bridge | agent_playtest | rsmod-agent-playtest | LLM execution |
| worklog, docs, status, roadmap, update | docs_update | rsmod-worklog-updater | Documentation |

## Fresh-Session Recovery

"Start the RS orchestration" triggers: latest worklog -> git log -> system status -> zone status -> report -> route.

## Kanban Worker Routing

Kanban cards should specify `workflow:` and `content_type:` in card body. If absent, classify from title/body using the table above.

## Multi-Type Tasks

Process in order: Inspect -> Generate -> Spec -> QA -> Docs
