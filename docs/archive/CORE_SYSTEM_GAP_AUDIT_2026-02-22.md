# Core System Gap Audit (Rev 233) — 2026-02-22

Purpose: identify the **core systems still missing** for true feature-complete OSRS behavior on this codebase, using current implementation state + task registry.

---

## Ground Truth Used

- `docs/CONTENT_AUDIT.md` (current content status snapshot)
- `docs/MASTER_ROADMAP.md` (broad parity surface area)
- `docs/NEXT_STEPS.md` (active execution order)
- `docs/REV233_COMPLETION_ROADMAP.md` (**contains stale claims**; do not treat as truth)
- Task registry (`agent-tasks` MCP), pending/in-progress lists as of 2026-02-22
- Code scan for explicit TODO/NotImplementedError in `rsmod/api`, `rsmod/engine`, `rsmod/content`

---

## Executive Summary

You are no longer at "missing everything." You are in a **mid-foundation state**:

1. Core F2P skilling baseline is largely present.
2. F2P quests are partially complete (10/13 full, 3 partial).
3. Biggest blockers for "feels like OSRS":
   - combat behavior completeness (aggression/freeze-stun/boss flows),
   - net/protocol parity edges,
   - social/economy/player systems,
   - world population depth.

---

## Tier A — Hard Blockers to Core Playability Feel

### A1. Combat behavior parity
- Missing/partial:
  - NPC aggression radius behavior
  - freeze/stun full mechanics
  - boss scripting path for Count Draynor + Elvarg
  - PvP depth (skulling/singles-plus/smite/item protection nuances)
- Registry mapping:
  - `MECH-1` (in progress)
  - `MECH-2` (in progress)
  - `AGENTBRIDGE-6` (in progress)
  - `QUEST-9-IMPL`, `QUEST-10-IMPL` (pending; blocked by combat event readiness)

### A2. Network/protocol edge parity
- Explicit code-level gaps:
  - token auth TODO
  - reconnect TODO
  - op ceilings leading to `NotImplementedError` on higher ops
  - dynamic region rebuild cache invalidation TODO
  - loc-shape route mapping `NotImplementedError`
- Registry mapping:
  - `NET-1`, `NET-2`, `NET-3`, `NET-4`
  - `ENGINE-3`, `PROTO-1`

### A3. Remaining F2P quest completeness
- Remaining:
  - Romeo & Juliet
  - Vampyre Slayer (boss-dependent)
  - Dragon Slayer I (boss/system dependent)
- Registry mapping:
  - `QUEST-4-IMPL`, `QUEST-9-IMPL`, `QUEST-10-IMPL`

---

## Tier B — Core MMO Systems Missing (Beyond F2P Baseline)

### B1. Social systems
- Missing:
  - Friends list
  - Ignore list
  - full private/social loop parity
- Registry mapping:
  - `SYSTEM-UI-1`

### B2. Economy systems
- Missing/partial:
  - Grand Exchange interface/content wiring
  - broad shop population/restock behavior across cities
- Registry mapping:
  - `SYSTEM-UI-2`, `SHOP-1`

### B3. Player sustain loop systems
- Missing baseline content plugins:
  - food eating system
  - potion drinking + stat boost/restore loop
  - make-X quantity flows across artisan skills
  - prayer active effects polish
- Registry mapping:
  - `FOOD-1`, `FOOD-2`, `MAKEQ-1`, `PRAYER-1`

### B4. World population and interaction depth
- Missing:
  - major city population/behavior modules beyond initial Lumbridge pass
  - richer NPC interactions and world flavor loops
- Registry mapping:
  - `WORLD-1` (in progress), `AREA-2`..`AREA-7`

---

## Tier C — Major Progression Systems Not Started

### C1. Skills not yet in baseline
- Slayer
- Hunter
- Construction
- Registry mapping:
  - `SKILL-22`, `SKILL-23`, `SKILL-24`

### C2. Ranged/PvM quirks and advanced mechanics
- Missing:
  - ammo recovery and cannon behavior
  - additional weapon/gear edge interactions
- Registry mapping:
  - `QUIRK-1`

### C3. Music and polish parity
- Missing/partial:
  - music player content wiring and behavior polish
  - midi type data loading parity
- Registry mapping:
  - `SYSTEM-UI-3`, `ENGINE-4`

---

## Tier D — Long-Horizon Feature Complete Scope (Post-Core)

These are outside immediate "core feel" but required for full parity:

1. Full PvP/Wilderness ecosystem.
2. Full questcape path (all P2P quest chains).
3. Full minigame ecosystem.
4. Full achievement systems (diaries, CAs, collection log, KC).
5. Full transport + utility spellbooks + late-game progression loops.

Refer to `docs/MASTER_ROADMAP.md` for the exhaustive long-horizon list.

---

## Documentation Drift Risks Found

1. `docs/REV233_COMPLETION_ROADMAP.md` has stale/inaccurate assumptions (e.g., 100% networking, outdated era notes, old completion percentages).
2. Multiple roadmap docs can drift unless one file is treated as execution truth.

Recommended authority model:
- Execution truth: `docs/NEXT_STEPS.md` + task registry
- Status truth: `docs/CONTENT_AUDIT.md`
- Long-horizon catalog: `docs/MASTER_ROADMAP.md`

---

## Suggested Completion Sequence (Pragmatic)

1. Finish in-progress combat dependencies (`AGENTBRIDGE-6`, `MECH-1`, `MECH-2`).
2. Close final 3 F2P quest implementations.
3. Finish city/world population pass (`WORLD-1`, `AREA-2`..`AREA-7`).
4. Close social/economy/player-loop core (`SYSTEM-UI-1/2`, `SHOP-1`, `FOOD-1/2`, `MAKEQ-1`, `PRAYER-1`).
5. Harden net/engine parity (`NET-*`, `ENGINE-*`, `PROTO-1`).
6. Move into major missing skills (`SKILL-22/23/24`) and advanced quirks.

---

## Bottom Line

For "feature complete," the missing work is mostly:
- systems depth and parity edge-cases,
- social/economy/player-loop features,
- large content breadth (quests/areas/minigames/P2P progression),
not raw engine existence.

The project is in a strong position to reach **full F2P authenticity + stable core MMO loop** before tackling full P2P parity.


