# LLM Agent Testing Guide
## OSRS Private Server — RSMod v2 (Revision 233)

This document describes how LLM agents interact with the testing harness, what they
receive as input, how they should reason, and what they produce as output.

---

## 0. Revision Gate (Mandatory)

Before any test run:
1. Read `docs/REV_LOCK_POLICY.md`.
2. Confirm the scenario and IDs are rev 233 compatible.
3. Reject/flag post-rev-233 mechanics as out-of-scope unless explicitly approved.

---

## 1. Architecture Overview

```
RSMod v2 server (game logic)
        │
        │  per-tick JSON snapshots (every 600 ms)
        │  ◄── action commands (walk, teleport, interact_loc, interact_npc)
        ▼
AgentBridge WebSocket — port 43595
        │
        ├──► MCP Server (mcp/server.ts, bun)       ◄──► Claude Code / Claude API
        │      execute_script, get_state, send_action     (primary LLM interface)
        │
        ├──► Python agent-runner (agent-runner/)    ◄──► Claude API (per-tick loop)
        │      LLMAgent, SkillTesterAgent, DropTesterAgent
        │
RSProx fork
        │  decoded wire packets
        ▼
AgentBroadcaster WebSocket — port 43596   ◄──► (optional, for wire validation)
```

### Two Integration Paths

**Path A — MCP Server (recommended for Claude Code)**
The `mcp/server.ts` exposes MCP tools (`execute_script`, `get_state`, `send_action`).
Claude Code auto-discovers it via `.mcp.json`. The LLM writes TypeScript bot scripts
using `bot.*` and `sdk.*` APIs, runs them, observes results, and iterates. This is the
most flexible approach — scripts can perform entire test workflows in a single call.

**Path B — Python agent-runner (CI/automated)**
`agent-runner/run.py` runs structured per-tick validation. `LLMAgent` calls Claude API
each tick with the state snapshot, Claude returns `{action, evaluation, notes}` JSON,
and the agent executes actions + logs evaluations. Suitable for automated nightly runs.

**AgentBridge (43595)** — bidirectional. Broadcasts per-player state snapshots AND receives
action commands (walk, teleport, interact_loc, interact_npc) that are executed server-side.

**AgentBroadcaster (43596)** — proxy-side. Packet names only (`UPDATE_STAT`, `NPC_INFO`, etc.).
Used to confirm the wire representation matches the server state. Optional for smoke tests.

---

## 2. State Snapshot Schema

Every tick the agent receives one JSON object per logged-in player:

```json
{
  "tick": 12483,
  "player": {
    "name": "TestBot",
    "position": { "x": 3222, "y": 3219, "plane": 0 },
    "skills": {
      "attack":       { "level": 1,  "xp": 0     },
      "woodcutting":  { "level": 35, "xp": 24080 },
      "hitpoints":    { "level": 10, "xp": 1154  },
      "...": "...all 23 skills..."
    },
    "inventory": [
      { "slot": 0, "id": 1519, "qty": 14 }
    ],
    "equipment": [
      { "slot": 3, "id": 1349, "qty": 1 }
    ],
    "animation": 879,
    "nearbyNpcs": [
      { "id": 118, "index": 5, "name": "Goblin", "x": 3224, "z": 3220, "animation": 0 }
    ],
    "nearbyLocs": [
      { "id": 10820, "name": "Willow tree", "x": 3223, "z": 3219 },
      { "id": 35647, "name": "Bank booth",  "x": 3270, "z": 3167 }
    ]
  }
}
```

**Field notes:**
- `tick` — server game cycle counter (monotonically increasing, +1 per 600 ms)
- `skills.*.level` — base level derived from XP (1–99), not boosted/drained
- `skills.*.xp` — total XP (integer, fine XP / 10 — i.e., 10 XP is stored as 10)
- `inventory` / `equipment` — only occupied slots are listed (empty slots omitted)
- `animation` — current animation sequence ID; `0` means idle / no animation
- `position.z` — the Z coordinate (south-north axis); `plane` is the floor level (0 = ground)
- `nearbyNpcs` — all NPCs within 16 Chebyshev tiles. `index` is the server list slot — use it for `interact_npc` commands
- `nearbyLocs` — all game objects within 16 tiles. `id + x + z` are required for `interact_loc` commands

**Equipment slots:**
```
0=head  1=cape  2=amulet  3=weapon  4=body  5=shield  6=legs
7=hands 8=feet  9=ring   10=ammo
```

---

## 3. Action Command Schema

Actions are sent to AgentBridge as JSON. Include `"player": "<name>"` to target a specific
player. Actions are executed on the game thread (dequeued in the per-tick soft timer).

```json
{ "player": "TestBot", "type": "walk", "x": 3222, "z": 3219 }
```

```json
{ "player": "TestBot", "type": "teleport", "x": 3222, "z": 3219, "plane": 0 }
```

```json
{ "player": "TestBot", "type": "interact_loc", "id": 10820, "x": 3223, "z": 3219, "option": 1 }
```

```json
{ "player": "TestBot", "type": "interact_npc", "index": 5, "option": 1 }
```

**Supported action types:**

| Type | Fields | Description |
|------|--------|-------------|
| `walk` | `x, z` | Walk using server pathfinding (navigates around obstacles) |
| `teleport` | `x, z, plane` | Teleport instantly (test setup only) |
| `interact_loc` | `id, x, z, option` | Click a game object at (x,z); option 1-5 |
| `interact_npc` | `index, option` | Click an NPC by server list index; option 1-5 |

Actions are executed immediately on the next game tick after receipt.
The server validates loc/NPC existence and handler registration before executing.

Via MCP (`sdk.sendWalk`, `sdk.sendInteractLoc`, etc.) or Python agent-runner (`_send_action`).

---

## 4. OSRS Core Mechanics

### Ticks
One game tick = 600 ms. All timing in OSRS is measured in ticks.
- Burying a bone = 1 tick
- Woodcutting chop interval = 4 ticks (average, varies by level/axe)
- Fishing cast interval = 5 ticks (varies by spot)
- Cooking per-item = 4 ticks on range, 5 on fire

### XP Table (key thresholds)
```
Level  1  =        0 XP       Level 50  =   101,333 XP
Level  2  =       83 XP       Level 60  =   273,742 XP
Level 10  =    1,154 XP       Level 70  =   737,627 XP
Level 20  =    4,470 XP       Level 80  = 2,951,373 XP
Level 30  =   13,363 XP       Level 90  = 5,346,332 XP
Level 40  =   37,224 XP       Level 99  =13,034,431 XP
```

Full table formula: `xp = floor(Σ(i=1..level-1) floor(i + 300 * 2^(i/7))) / 4`

### XP Precision
OSRS grants XP in units of 0.1. The `xp` field in the snapshot is the integer part
(fine XP / 10). An action granting 25 XP appears as a delta of `+25` between ticks,
not `+25.0`. Use a tolerance of ±0.5 when comparing expected vs actual XP gain.

### Animation IDs (common)
```
0    = idle (no animation)
827  = bury bones (human_pickupfloor)
879  = woodcutting (most axes)
867  = woodcutting (rune/dragon axe)
2846 = woodcutting (redwood)
3705 = altar bone offering (human_bone_sacrifice)
881  = pickpocket / thieving stall
```
Animation IDs for other skills are in the wiki oracle JSON under `actions.*.animation`.

### Level Requirements
The server enforces level requirements — a player below the required level cannot
perform the action. Testing access control: attempt the action at level `req - 1`,
confirm it is blocked; attempt at level `req`, confirm it proceeds.

---

## 5. Wiki Oracle Data Format

Oracle files live in `wiki-data/`. Agents load them via `oracle/wiki.py`.

### Skill oracle (`wiki-data/skills/<skill>.json`):
```json
{
  "skill": "woodcutting",
  "actions": {
    "willow_tree": {
      "level_req": 30,
      "xp": 67.5,
      "animation": 879,
      "object_ids": [10819, 10820, 10829],
      "log_item_id": 1519
    }
  },
  "tools": {
    "rune_axe": { "item_id": 1359, "level_req": 41 }
  }
}
```

### Monster oracle (`wiki-data/monsters/<name>.json`):
```json
{
  "name": "Goblin",
  "drops": [
    { "id": 526, "name": "Bones", "rate": 1.0, "qty_min": 1, "qty_max": 1 },
    { "id": 995, "name": "Coins", "rate": 0.166, "qty_min": 1, "qty_max": 4 }
  ]
}
```

---

## 6. Agent Response Format

Every tick the agent returns a JSON object. All fields are required.

```json
{
  "action": {
    "type": "none"
  },
  "evaluation": {
    "check": "xp_grant",
    "pass": true,
    "expected": 67.5,
    "actual": 67.5,
    "delta": 0.0,
    "note": "Willow log XP correct at level 35"
  },
  "notes": "Player cut willow at tick 12483. Animation 879 observed. XP +67.5 — matches wiki."
}
```

`evaluation.check` values:
- `"xp_grant"` — validating an XP award
- `"animation"` — validating the animation played
- `"access_control"` — validating level gate enforcement
- `"item_produced"` — validating an item was added to inventory
- `"item_consumed"` — validating an item was removed from inventory
- `"level_up"` — validating a level-up occurred at the correct XP threshold
- `"timing"` — validating tick duration of an action
- `"drop_rate"` — statistical rate validation (drop_tester only)
- `"none"` — no evaluable event this tick (idle)

When `pass` is `false`, `note` must explain the discrepancy clearly enough for a
developer to identify and fix the bug.

---

## 7. Reasoning Loop

Each tick:

```
1. OBSERVE   — parse the snapshot. Extract: tick, level, xp, animation, inventory delta.
2. COMPARE   — look up the expected values in the wiki oracle for this action/level.
3. EVALUATE  — determine pass / fail / no-event.
               - XP delta > 0 this tick → run xp_grant check
               - Animation changed from 0 → non-zero → run animation check
               - Level changed → run level_up check
               - Inventory item appeared → run item_produced check
4. ACT       — if an action is needed to advance the test, emit it; otherwise "none".
5. REPORT    — return the evaluation JSON.
```

**Key principle:** Evaluate every tick, not just at test end. This catches bugs that
self-correct (e.g., XP granted twice then clamped) which would be invisible in a
final-state check.

---

## 8. Common Failure Patterns

| Pattern | Symptom | Likely cause |
|---------|---------|--------------|
| Wrong XP | `actual` ≠ `expected` (e.g., 25 instead of 67.5) | Wrong XP value in skill plugin `LOG_DEFS` / `FISH_DEFS` |
| No XP at all | `delta == 0` for many ticks while animation plays | `statAdvance` not called; wrong stat type |
| Wrong animation | `animation` ≠ `expected` | Wrong seq ref in plugin; wrong pickaxe/axe tier selected |
| Action too fast | Ticks between XP grants < expected interval | `delay(n)` not called or called with wrong n |
| Action too slow | Ticks between XP grants > expected interval | Extra `delay` call; wrong timer period |
| Item not produced | Expected item_id missing from inventory after action | `invAdd` not called; wrong item sym name |
| Item not consumed | Tool/bait still in inventory after several actions | `invDel` not called; wrong obj ref |
| Level gate missing | Action succeeds at level below `level_req` | Level check omitted; using wrong stat |
| Double XP | `delta` = 2× expected | `statAdvance` called twice per action |

---

## 9. Skill Coverage Checklist

For each skill, validation must cover these categories before marking ✅:

- [ ] **Access control** — blocked below level req, allowed at level req
- [ ] **XP grant** — matches wiki value for each action tier
- [ ] **Animation** — correct seq ID for tool/action tier
- [ ] **Timing** — action completes in expected ticks
- [ ] **Item produced** — correct item appears in inventory
- [ ] **Item consumed** — tool/bait/fuel consumed correctly
- [ ] **Level-up** — fires at correct XP threshold (not before, not after)
- [ ] **Resource depletion** — rock/tree/stall respawns after correct ticks
- [ ] **Edge cases** — full inventory, insufficient level, wrong tool

---

## 10. Implemented Content Reference

Agents should only test content that is implemented. Current status:

| Skill | Status | Notes |
|-------|--------|-------|
| Woodcutting | ✅ | Template implementation |
| Fishing | ✅ | 20 fish types, 8 spot types |
| Cooking | ✅ | 19 fish types, ranges + fires |
| Firemaking | ✅ | All log types incl. blisterwood |
| Mining | ✅ | 10 ore types, 16 pickaxe anims |
| Thieving | ✅ | 14 NPCs, 11 stalls, H.A.M. chests |
| Prayer | ✅ | 26 bone types, standard + POH altars |
| Magic (combat) | ✅ | Spell system, rune consumption |
| Poison/Venom | ✅ | 18-tick interval, damage decay |
| Drop tables | ✅ | Goblins, cows, chickens, guards, men, giant rats |

See `docs/CONTENT_AUDIT.md` for full status.

---

## 11. Adding New Test Coverage

1. Add the skill oracle JSON to `wiki-data/skills/<skill>.json`
2. Add monster oracles to `wiki-data/monsters/<name>.json` (for drop tests)
3. Add the test to `agent-runner/config/smoke.yaml` or `nightly.yaml`
4. The `SkillTesterAgent` picks up oracle data automatically via `WikiOracle`

No code changes needed in the agent framework for new skills — just add data files
and config entries.

