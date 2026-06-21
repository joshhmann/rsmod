# Playerbot QA Scenario: Mining 1-30

> **Content Type:** docs / playerbot-qa  
> **Risk Level:** 1 (mechanical skill progression, no branching quest logic)  
> **Workflow:** rsmod-playerbot-qa  
> **Created:** 2026-06-21  

---

## 1. Precondition — Starting State

| Field | Value |
|---|---|
| **Account** | Fresh character, Tutorial Island complete |
| **Starting location** | Lumbridge (courtyard, near spawn point) |
| **Mining level** | 1 |
| **Inventory** | 1× Bronze pickaxe (wielded), empty (27 free slots) |
| **Equipment** | Bronze pickaxe equipped in weapon slot |
| **Attack level** | 1 (no other combat training assumed) |
| **Other skills** | All level 1 |
| **Quest status** | None started |
| **Game mode** | Free-to-play (no membership required for 1-30) |

---

## 2. Success Conditions

All of the following must hold:

| # | Condition | Evidence |
|---|---|---|
| SC1 | Mining level reaches 30 | XP tracker / stat query shows level 30 |
| SC2 | At least 400 ores mined across all rock types | Ore count telemetry |
| SC3 | Multiple rock types mined (minimum: copper + tin + iron) | Ore type distribution telemetry |
| SC4 | Bot returns to bank/Lumbridge with inventory intact | No inventory corruption, no infinite loops |
| SC5 | Bot does not exceed 30 Mining before script terminates | Over-level guard catches |

---

## 3. Failure Conditions

Any of the following constitutes a test failure:

| # | Condition | Expected Handling |
|---|---|---|
| FC1 | Bot stuck without pickaxe | Must detect missing pickaxe → return to bank → retrieve bronze pickaxe from bank (or, if bank is empty, halt with clear "missing pickaxe" error) |
| FC2 | Bot stuck in location with no minable rocks | Must detect no rocks in loaded region → path to nearest known mine (Lumbridge Swamp) → if all mines exhausted, halt with "no rocks reachable" |
| FC3 | Inventory full for >30 consecutive ticks without resolution | Must detect full inventory → either drop ore or path to bank → deposit → return to mine |
| FC4 | Bot attempts to mine a rock type above its Mining level | Must not happen. Rock selection must filter by player Mining level. If it does happen, game must reject with "You need a higher Mining level to mine this rock." |
| FC5 | Bot idle for >60 seconds | Must detect stuck state → attempt pathfinding reset → if persists, halt with "STUCK" telemetry |
| FC6 | Bot trapped by aggressive NPCs (e.g., Al Kharid scorpions) | Must handle damage without dying OR retreat to safe area if health < 30% |

---

## 4. Target Rocks & Mining Progression

### 4.1 Rock Table

| Rock | Mining Level | XP/ore | Level Range | Expected Count | Notes |
|------|:-----------:|:------:|:-----------:|:--------------:|-------|
| Copper | 1 | 17.5 | 1–15 | ~80 | Lumbridge Swamp, Varrock East |
| Tin | 1 | 17.5 | 1–15 | ~80 | Lumbridge Swamp, Varrock East |
| Iron | 15 | 35 | 15–30 | ~313 | Lumbridge Swamp, Varrock East, Al Kharid |
| Coal | 30 | 50 | 30 (test only) | ≤5 | Dwarven Mine, Barbarian Village |

### 4.2 Rock Location Reference — F2P

| Mine | Plane | Region | Rocks Available | Travel from Lumbridge |
|------|-------|--------|----------------|-----------------------|
| Lumbridge Swamp Mine | 0 | (51, 48) → (51, 49) | Copper, Tin, Iron, (Coal) | East across bridge, south to swamp |
| Varrock East Mine | 0 | (52, 52) → (53, 52) | Copper, Tin, Iron | North across river, east to Varrock |
| Al Kharid Mine | 0 | (51, 53) | Copper, Tin, Iron, Coal, Silver | South to Al Kharid gate, past palace |
| Dwarven Mine | 0 | (55, 55) | Copper, Tin, Iron, Coal, Mithril, Adamant, Rune | North to Falador east bank, down ladder |
| Barbarian Village | 0 | (52, 54) | Coal | North to River Lum, west to Barbarian Village |

> **Note:** Coordinates are region-grid aligned (plane, region_x, region_y). Convert to absolute tiles via `abs_x = region_x * 64 + local_x`, `abs_y = region_y * 64 + local_y`.

### 4.3 Recommended Path — Level Bracket Progression

| Bracket | Rocks | Location | Rationale |
|---------|-------|----------|-----------|
| 1–15 | Copper + Tin (mixed) | Lumbridge Swamp Mine | Closest to spawn, zero travel, safe (no aggressive NPCs) |
| 15–30 | Iron | Lumbridge Swamp Mine (2 iron rocks) | Same location, minimal path replanning |
| 15–30 (fallback) | Iron | Varrock East Mine | If Lumbridge Swamp iron node is exhausted/contested |
| 30 | Coal (test only) | Barbarian Village / Dwarven Mine | Verify level-gate opens at 30; not needed for SC1 |

---

## 5. Telemetry Requirements

The bot must emit the following telemetry events to validate the scenario:

### 5.1 Required Telemetry Points

| Signal | When | Format |
|--------|------|--------|
| `mining.start` | Bot begins mining loop | `{timestamp, state: "started", level: 1}` |
| `mining.rock_click` | Bot clicks a rock | `{timestamp, rock_type, rock_x, rock_y, player_x, player_y, mining_level}` |
| `mining.ore_gain` | Ore added to inventory | `{timestamp, ore_type, ore_id, quantity, new_total, xp_gained, new_level}` |
| `mining.xp_gain` | XP granted from successful mine | `{timestamp, xp, total_xp, mining_level}` |
| `mining.level_up` | Mining level increases | `{timestamp, old_level, new_level}` |
| `mining.inv_full` | Inventory reaches 28/28 | `{timestamp, action_taken: "drop"|"bank"|"stuck"}` |
| `mining.no_rock` | No minable rocks in loaded region | `{timestamp, region, rocks_in_region, minable_rocks}` |
| `mining.pickaxe_broken` | Pickaxe missing/inventory check | `{timestamp, had_pickaxe: false, action_taken}` |
| `mining.stuck` | Pathfinding fails or idle >60s | `{timestamp, reason, coords}` |
| `mining.complete` | Level 30 achieved | `{timestamp, total_ores, total_xp, runtime_seconds}` |

### 5.2 Telemetry Assertions

| Assertion | Description |
|-----------|-------------|
| `telem_count(ore_gain) >= 400` | At least 400 ore-gain events |
| `telem_has_type("copper")` | Copper was mined at least once |
| `telem_has_type("tin")` | Tin was mined at least once |
| `telem_has_type("iron")` | Iron was mined at level 15+ |
| `telem_count(level_up) == 29` | 29 level-ups (1→2 through 29→30) |
| `telem_count(stuck)` | Should be 0; any non-zero is triaged |
| `telem_last(complete) != null` | Completion event emitted |

---

## 6. Expected XP and Ore Counts

### 6.1 OSRS XP Curve (Mining)

| Target Level | Total XP Required | XP Delta | Phase |
|:-----------:|:-----------------:|:--------:|-------|
| 1 | 0 | 0 | — |
| 5 | 346 | 346 | Copper/Tin only |
| 10 | 1,154 | 808 | Copper/Tin only |
| 15 | 2,411 | 1,257 | Copper/Tin only (switch to Iron at 15) |
| 20 | 4,522 | 2,111 | Iron |
| 25 | 8,773 | 4,251 | Iron |
| 30 | 13,363 | 4,590 | Iron + Coal test at 30 |

### 6.2 Expected Ore Distribution (Optimal Path)

| Bracket | Ore Type | XP/unit | Ores Needed | Cumulative Ores | Cumulative XP |
|---------|----------|:-------:|:-----------:|:---------------:|:-------------:|
| 1–15 | Copper + Tin (mixed) | 17.5 | 138 | 138 | 2,415 |
| 15–20 | Iron | 35 | 61 | 199 | 4,532 |
| 20–25 | Iron | 35 | 122 | 321 | 8,802 |
| 25–30 | Iron | 35 | 131 | 452 | 13,387* |

> *Minor variance (±4 XP) from rounding is acceptable at the final tick.

### 6.3 Acceptable Variance Tolerances

| Metric | Tolerance | Rationale |
|--------|:---------:|-----------|
| Total ores mined | ±10% | Rock despawn/respawn timing, competition |
| Total XP | ±100 XP | XP modifier rounding, multi-ore rock ticks |
| Time to 30 | N/A (baseline) | No time target — record first-pass runtime as baseline |
| Level-up events | Exactly 29 | Always deterministic |

---

## 7. Pickaxe Progression

### 7.1 Pickaxe Requirements

| Pickaxe | Attack Level Required | Mining Bonus | When Accessible |
|---------|:--------------------:|:------------:|:---------------:|
| Bronze | 1 | Faster than bare hands | Immediate (start) |
| Iron | 1 | Slightly faster than bronze | Immediate (after banking/gearing) |
| Steel | 5 | Significant speed increase | After gaining 5 Attack |
| Mithril | 20 | Notable speed increase | After gaining 20 Attack |
| Adamant | 30 | Major speed increase | After gaining 30 Attack |
| Rune | 40 | Best F2P speed | After gaining 40 Attack |

### 7.2 Progression for Mining-Only Bot

For a bot that does not train combat skills (Attack remains 1), the **pickaxe progression is flat**:
- Bronze pickaxe at start (Attack 1)
- Iron pickaxe if the bot can obtain one (Attack 1, marginal speed improvement over bronze)

**Recommendation for QA:** The fundamental mining loop (click rock → animation → XP → inventory) is identical regardless of pickaxe type. Pickaxe swap tests are **optional** for risk level 1. If tested, verify:
- Bot can equip a steel pickaxe at 5 Attack
- Unequipping the pickaxe triggers FC1 (missing tool detection)

### 7.3 Pickaxe Interaction Verification

| Check | Expected Behavior |
|-------|------------------|
| Wield pickaxe | Right-click "Wield" → pickaxe moves to weapon slot |
| Use pickaxe on rock | Standard mining interaction (left-click rock) |
| No pickaxe in inventory | Game message: "You need a pickaxe to mine this rock. You do not have a pickaxe." |
| Pickaxe in bank but not inventory | Bot must detect → path to bank → withdraw → return |

---

## 8. Stuck-State Handling Scenarios

### 8.1 No Rocks in Area

```text
Trigger: Bot scans loaded LOCs for mineable rock types and finds 0.
Detection: Periodic rock scan every 10 ticks. If 0 rocks found for 3 consecutive scans:
  → Emit telemetry `mining.no_rock`
  → Attempt path to nearest known mine (Lumbridge Swamp)
  → If all known mine locations exhausted, halt with `mining.stuck`
```

**Valid mine locations (ordered by distance from Lumbridge):**
1. Lumbridge Swamp Mine (copper, tin, iron) — ~20 tiles SE
2. Varrock East Mine (copper, tin, iron) — ~100 tiles NE
3. Al Kharid Mine (copper, tin, iron) — ~40 tiles S (requires passing gate)

### 8.2 Missing Pickaxe

```text
Trigger: Bot enters mining loop, but pickaxe not in inventory or weapon slot.
Detection: Pre-mining equipment check.
  → Emit telemetry `mining.pickaxe_broken`
  → Search bank for any pickaxe (bronze → iron → steel → mithril → adamant → rune)
  → If found in bank: path to nearest bank → withdraw → return to mine
  → If not found: halt with "missing pickaxe — cannot continue"
```

**Bank locations for retrieval:**
| Bank | Distance from Lumbridge | Notes |
|------|------------------------|-------|
| Lumbridge Castle bank | Immediate (ground floor[1]) | Fastest |
| Varrock West Bank | Medium | Requires river crossing |
| Al Kharid Bank | Short | Requires 10gp to pass gate (or free with Prince Ali Rescue) |

### 8.3 Inventory Full

```text
Trigger: inv.count reaches 28.
  → Emit telemetry `mining.inv_full`
  → Option A (recommended): Drop lowest-value ore to continue mining
  → Option B: Path to bank → deposit ores → return
  → Option C (fallback): If neither A nor B completes in 30 ticks → halt
```

### 8.4 Wrong Rock Type

```text
Trigger: Bot attempts to mine iron at level 14.
Game response: "You need a Mining level of 15 to mine this rock."
Bot response: Must not retry the same rock. Must either:
  a) Update internal rock filter to only show rocks ≤ current level
  b) Move to a location with appropriate-level rocks
```

---

## 9. Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|:----------:|:------:|------------|
| Bot attempts rock above level | Low | Medium | Rock filter by Mining level; game rejection message fallback |
| Rock despawn timing causes idle ticks | Medium | Low | Periodic rock re-scan every 10 ticks |
| Pathfinding fails in Lumbridge Swamp | Low | High | Include telemetry + halt: no infinite retry |
| Pickaxe dropped/lost mid-run | Low | High | Detect and recover from bank; halt if unresolvable |
| Inventory full stalls progression | Medium | Low | Drop ore or bank; configurable per-bot profile |
| Aggressive NPC interrupts mining | Low | Low | Handle damage; retreat if health < 30% |

---

## 10. Regression Tags

```
mining
skill
f2p
lumbridge
varrock
al-kharid
playerbot-qa
scenario-mechanical
pickaxe-progression
```

---

## 11. Environment Requirements

| Requirement | Value |
|-------------|-------|
| rsmod rev | 233 |
| Mining skill module | Loaded (content/skills/mining/) |
| NPC spawns | Standard F2P world spawns loaded |
| Rock respawn timer | Default (varies by rock type, ~3–12 seconds) |
| Bot pickaxe | Bronze pickaxe (obj symbol: `bronze_pickaxe`) |
| XP rate modifier | Default (1.0x) — no accelerated XP |
| World type | Standard overworld (no instancing) |

---

## 12. Verification Procedure

### Phase 1 — Smoke Test (Risk Level 1)

1. Spawn fresh bot at Lumbridge spawn point
2. Verify bronze pickaxe in inventory/wielded
3. Bot navigates to Lumbridge Swamp Mine (east across bridge, south into swamp)
4. Bot clicks a copper or tin rock
5. Verify: mining animation plays, XP is granted, ore appears in inventory
6. Verify: Mining level increases through 1→5 range

**Expected result:** All 6 steps pass without error.

### Phase 2 — Full Progression Test

1. Run bot from Lumbridge spawn to level 30 Mining
2. Verify telemetry events match Section 5 expectations
3. Verify total XP is within tolerance (Section 6.3)
4. Verify ore counts are within tolerance (Section 6.3)
5. Verify no stuck-state telemetry events fired
6. Verify bot correctly transitions to iron at level 15

### Phase 3 — Failure Injection Test

Inject each failure condition (FC1–FC6) and verify bot responds correctly:

| Test | Injection Method |
|------|-----------------|
| FC1 | Remove pickaxe from inventory mid-run |
| FC2 | Set respawn timer to 0 for all rocks in loaded region |
| FC3 | Fill inventory to 28/28 |
| FC4 | Set bot Mining level to 14 and target iron rock |
| FC5 | Pause bot input for 65 seconds |
| FC6 | Spawn aggressive NPC near mining location |

---

## 13. Document Status

| Field | Value |
|-------|-------|
| **Author** | Rei (QA Lead, Hyraxknot Division) |
| **Reviewer** | Mai (application on CT 123) |
| **Status** | Sandbox staged — awaiting CT 123 application |
| **Risk Level** | 1 (mechanical, low branching) |
| **Estimated Runtime** | ~45–90 min (Phase 2 full progression) |
