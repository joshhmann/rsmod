# DEPRECATED: Use Agent-Tasks Registry Directly

**Date**: 2026-02-26
**Status**: SUPERSEDED by agent-tasks registry

---

## Why This Changed

The agent-tasks registry is the **source of truth** for all work coordination. Agents use it effectively for:
- Claiming tasks atomically
- File locking to prevent conflicts
- Heartbeat/status updates
- Dependency tracking

**Maintaining a separate plan document is redundant** and creates drift risk.

---

## Current Tasks (Use Agent-Tasks Directly)

### Available to Claim (Pending)

| Task ID | Wave | Description | Module |
|---------|------|-------------|--------|
| **BUILD-HYGIENE-SYMBOL** | W0 | Create Symbol Verification Script | scripts/ |
| **QUEST-1-FIX** | W1 | Fix CooksAssistant Compile Errors | rsmod/content/quests/cooks-assistant/ |
| **MAGIC-F2P-UTILITY** | W1 | F2P Utility Spells (Teleports, Alch, Superheat) | rsmod/content/skills/magic/ |
| **NPC-TRAINING-F2P** | W1 | F2P Training Monsters (Mugger, Dwarf, Bear, Unicorn) | rsmod/content/other/npc-drops/ |

### In Progress (Don't Claim)

| Task ID | Owner | Description |
|---------|-------|-------------|
| **BUILD-CRIT-15** | gemini | Resolve Missing Symbol Name Drift |
| **NPC-DROP-2** | kimi-1 | P2P NPC Drop Tables Batch 1 |
| **SYSTEM-DIARY-FALADOR** | kimi | Falador Easy Achievement Diary |

---

## How to Work

1. **Check registry first**: `agent-tasks_list_tasks(status="pending")`
2. **Claim your task**: `agent-tasks_claim_task(taskId="TASK-NAME", agent="your-name")`
3. **Lock files**: `agent-tasks_lock_file(path="path/to/file", agent="your-name", taskId="TASK-NAME")`
4. **Do the work**
5. **Heartbeat**: `agent-tasks_agent_heartbeat(agent="your-name", status="working", current_task="TASK-NAME", current_file="path/to/file")`
6. **Complete**: `agent-tasks_complete_task(taskId="TASK-NAME", agent="your-name", notes="what you did")`

---

## Reference

See `AGENTS.md` for full coordination protocols.

**Use agent-tasks. It's already working.**
