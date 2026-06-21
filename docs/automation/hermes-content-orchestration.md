# Hermes Content Orchestration

## Purpose

Master workflow architecture for RSMod content automation. Defines how Hermes classifies, routes, validates, and documents every content task.

## Architecture

```
Task Request
    |
    v
rsmod-content-orchestrator (classifies)
    |
    +-- rsmod-corpus-drops (drops)
    +-- rsmod-skill-validation (skills)
    +-- rsmod-shop-stock (shops)
    +-- rsmod-zone-readiness (zones)
    +-- rsmod-minigame-spec (minigames)
    +-- rsmod-quest-spec (quests)
    +-- rsmod-playerbot-qa (bot testing)
    +-- rsmod-agent-playtest (LLM testing)
    +-- rsmod-worklog-updater (docs/status)
    |
    v
Completion -> docs update -> worklog -> next task -> self-improve
```

## Core Principles

1. Content-type first - Classify before acting
2. Stage before promote - No blind production pushes
3. Compile is not gameplay - Bot/agent testing confirms gameplay
4. Document everything - If it wasn't documented, it wasn't done
5. Self-improve - Every pattern learned becomes a skill update
6. Spec-first for behavior - Complex logic starts as a spec, not code

## Completion States

| State | Check | Required For |
|-------|-------|-------------|
| CODE_DONE | Compiles, raw-ID clean | All code |
| CONTENT_DONE | Symbols/data verified | Drops, configs |
| GAMEPLAY_DONE | Manual/bot test passes | Skills, minigames |
| AGENT_READY | LLM agent can navigate | Interactive content |
| DOCUMENTED | Worklog/status updated | Every task |
| REUSABLE | Skill updated if needed | Every task |

## Automation Confidence Levels

| Level | Meaning | Gate |
|-------|---------|------|
| 0 | Exploratory | None |
| 1 | Staged output/spec works | Manual review |
| 2 | One production promotion | Single slice |
| 3 | Full zone or slice | One zone |
| 4 | Repeated across second zone | Two zones |
| 5 | Approved batch expansion | Level 4 + review |
