# Orchestrator Routing Table

| Task Keywords | Content Type | Workflow | Mode |
|---------------|-------------|----------|------|
| drops, drop table, npc loot, enrichment | Drops | rsmod-corpus-drops | Generator (L5) |
| cooking, woodcutting, mining, skill test, validate, 1-30 | Skill | rsmod-skill-validation | Validate first |
| shop, stock, store, StoreLine | Shop | rsmod-shop-stock | Generator (L1) |
| zone, readiness, checklist, region | Zone | rsmod-zone-readiness | Checklist |
| minigame, Wintertodt, Motherlode, Tempoross | Minigame | rsmod-minigame-spec | Spec-first |
| quest, spec, dialogue, varbit | Quest | rsmod-quest-spec | Spec-first |
| boss, KBD, KQ, Elvarg | Boss | rsmod-minigame-spec | Spec-first |
| playerbot, qa, test scenario, regression | QA | rsmod-playerbot-qa | Bot execution |
| agent, LLM, playtest, agent bridge | Agent | rsmod-agent-playtest | LLM execution |
| worklog, docs, status, roadmap, update | Docs | rsmod-worklog-updater | Documentation |

## Multi-Type Tasks
Process in order: Inspect -> Generate -> Spec -> QA -> Docs
