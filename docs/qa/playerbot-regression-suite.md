# PlayerBot Regression Suite

## Overview

- **Document ID:** D3
- **Purpose:** Define the comprehensive regression test suite for PlayerBot capabilities across movement, skills, combat, banking, dialogue, and zone interactions. This suite validates core functionality after every build and serves as the primary quality gate before any release.
- **Owner:** QA Engineering
- **Last Updated:** 2026-06-21
- **Related Documents:** [D1: Night-Run Overview](../night-run-overview.md), [D2: AgentBridge Integration](../agentbridge/agentbridge-integration.md), [D4: Reporting & Alerting](../reporting/reporting-alerting.md)

---

## 1. Purpose

The PlayerBot Regression Suite exists to:

1. **Guarantee baseline stability** — Every bot action exposed through AgentBridge must produce correct, reproducible results across supported game versions.
2. **Catch regressions early** — Detect broken paths, inventory mismanagement, skill leveling failures, combat bugs, and zone-loading errors before they reach end users.
3. **Enable automated night-run execution** — All tests are designed to run headlessly via AgentBridge, producing structured JSON results that feed into the nightly CI/CD pipeline.
4. **Provide mode-appropriate coverage** — Different execution modes (night-run, autonomous, assisted) require different depth of testing; the suite adapts via selection profiles and priority matrices.
5. **Serve as integration contract** — Each test case is both a regression check and a living specification of expected bot behavior.

---

## 2. Suite Structure

```
playerbot-regression-suite/
├── movement/
│   ├── 01-pathfinding-basic.yaml
│   ├── 02-pathfinding-obstacles.yaml
│   ├── 03-pathfinding-multilevel.yaml
│   └── 04-follow-player.yaml
├── skills/
│   ├── cooking/
│   │   ├── cooking-01-level-1-10.yaml
│   │   ├── cooking-02-level-10-20.yaml
│   │   └── cooking-03-level-20-30.yaml
│   ├── woodcutting/
│   │   ├── woodcutting-01-level-1-10.yaml
│   │   ├── woodcutting-02-level-10-20.yaml
│   │   └── woodcutting-03-level-20-30.yaml
│   └── mining/
│       ├── mining-01-level-1-10.yaml
│       ├── mining-02-level-10-20.yaml
│       └── mining-03-level-20-30.yaml
├── combat/
│   ├── combat-01-melee-basic.yaml
│   ├── combat-02-ranged-basic.yaml
│   ├── combat-03-magic-basic.yaml
│   ├── combat-04-multitarget.yaml
│   └── combat-05-safespot-detection.yaml
├── banking/
│   ├── banking-01-deposit.yaml
│   ├── banking-02-withdraw.yaml
│   ├── banking-03-inventory-management.yaml
│   └── banking-04-grand-exchange.yaml
├── dialogue/
│   ├── dialogue-01-npc-interact.yaml
│   ├── dialogue-02-options.yaml
│   ├── dialogue-03-quest-start.yaml
│   └── dialogue-04-shop.yaml
├── zones/
│   ├── lumbridge/
│   │   ├── lumbridge-01-spawn.yaml
│   │   ├── lumbridge-02-kitchen.yaml
│   │   ├── lumbridge-03-courtyard.yaml
│   │   └── lumbridge-04-church.yaml
│   ├── draynor/
│   │   ├── draynor-01-manor.yaml
│   │   ├── draynor-02-market.yaml
│   │   └── draynor-03-seers.yaml
│   └── varrock/
│       ├── varrock-01-east-bank.yaml
│       ├── varrock-02-west-bank.yaml
│       ├── varrock-03-grand-exchange.yaml
│       ├── varrock-04-palace.yaml
│       └── varrock-05-wilderness-ditch.yaml
├── integration/
│   └── agentbridge-smoke.yaml
├── suite-config.yaml
└── README.md
```

### Suite Configuration (`suite-config.yaml`)

Each test file contains a YAML frontmatter with metadata:

```yaml
id: cooking-01-level-1-10
name: "Cooking 1-10"
category: skills/cooking
priority: P0                     # P0 = critical, P1 = high, P2 = medium, P3 = low
estimated_duration: 120s         # expected wall-clock time
required_mode: [NIGHT_RUN_MODE, SAFE_AUTONOMOUS, ASSISTED]
dependencies: []
prerequisites:
  - player_level >= 3
  - skill_cooking >= 1
  - inventory_has: [raw_shrimp, raw_sardine]
tags: [regression, cooking, night-run, skill-grind]
```

---

## 3. Test Categories

### 3.1 Movement / Pathfinding

Validates the bot's ability to navigate the game world correctly.

| Subcategory | Focus | Key Checks |
|---|---|---|
| Basic Pathfinding | Point-to-point navigation | Reaches destination within timeout; avoids walls; updates path on obstruction |
| Obstacle Navigation | Doors, gates, stairs, ladders | Interacts correctly with obstacles; recovers from stuck state |
| Multi-level Navigation | Floors, dungeons, basements | Correct floor transitions; coordinate remapping |
| Follow Player | Follow another entity | Maintains distance; re-paths when target moves |

**Example Test:**
```yaml
id: movement-pathfinding-basic
name: "Basic Pathfinding — Lumbridge Spawn to Varrock GE"
prerequisites:
  - location: lumbridge_spawn
steps:
  - action: navigate
    target: varrock_grand_exchange
    timeout: 120s
  - action: validate_location
    expected: varrock_grand_exchange
    tolerance: 5_tiles
expected_results:
  - bot reaches destination within timeout
  - no "stuck" state triggered during traversal
  - path does not clip through blocked geometry
pass_fail_criteria:
  - PASS: Bot arrives within 5 tiles of target within 120s, no stuck events
  - FAIL: Timeout reached, stuck event fires, or location mismatch > 5 tiles
```

### 3.2 Skill Tests

#### 3.2.1 Cooking 1–30

Progressive skill-leveling tests verifying the cooking loop works end-to-end.

| Test | Range | Action | Items | Expected Time |
|---|---|---|---|---|
| cooking-01 | Level 1→10 | Cook shrimp, sardines | Raw shrimp, raw sardines | ≤ 5 min |
| cooking-02 | Level 10→20 | Cook trout, salmon | Raw trout, raw salmon | ≤ 8 min |
| cooking-03 | Level 20→30 | Cook tuna, lobster | Raw tuna, raw lobster | ≤ 12 min |

**Key Validations:**
- Items are withdrawn from bank correctly
- Cooking animation triggers
- Cooked item appears in inventory (not burnt for successes)
- Experience gains match expected per-item rates
- Burn rate decreases appropriately with level
- Inventory overflow prevention works (bank when full)

**Example Test Detail:**
```yaml
id: cooking-01-level-1-10
name: "Cooking 1-10 — Shrimp and Sardines"
prerequisites:
  - skill_cooking >= 1
  - bank_contains: { raw_shrimp: 28, raw_sardine: 28 }
steps:
  - action: bank.withdraw
    items: [{ name: raw_shrimp, quantity: 28 }]
  - action: skill.cook_all_inventory
    location: "Lumbridge kitchen range"
    timeout: 180s
  - action: bank.deposit_all
  - action: bank.withdraw
    items: [{ name: raw_sardine, quantity: 28 }]
  - action: skill.cook_all_inventory
    location: "Lumbridge kitchen range"
    timeout: 180s
expected_results:
  - cooking_level >= 10 after completing all steps
  - at least 50% of cooked items are edible (not burnt)
  - no inventory mismatches (count in = count out)
  - no bank errors or item loss
pass_fail_criteria:
  - PASS: Cooking level reaches 10+, burn rate ≤ 50%, zero item loss
  - FAIL: Level target not met, item loss detected, or any error thrown
```

#### 3.2.2 Woodcutting 1–30

| Test | Range | Trees | Expected Time |
|---|---|---|---|
| woodcutting-01 | Level 1→10 | Normal trees (oak) | ≤ 6 min |
| woodcutting-02 | Level 10→20 | Willow trees | ≤ 10 min |
| woodcutting-03 | Level 20→30 | Yew trees (with appropriate level) | ≤ 15 min |

**Key Validations:**
- Correct tree type is targeted
- Animation plays and logs appear in inventory
- Level-up notification is detected
- Tree respawn is handled gracefully (wait or move to next)
- Drop/banking of logs works (no full inventory stall)
- Axe handling: correct axe equipped, no degradation errors

#### 3.2.3 Mining 1–30

| Test | Range | Rocks | Expected Time |
|---|---|---|---|
| mining-01 | Level 1→10 | Tin, copper | ≤ 7 min |
| mining-02 | Level 10→20 | Iron | ≤ 12 min |
| mining-03 | Level 20→30 | Coal | ≤ 18 min |

**Key Validations:**
- Rock collision and clicking accuracy
- Pickaxe equipped and valid
- Ore appears in inventory after successful mining
- Rock depletion handled (wait or switch rocks)
- Power-mining vs banking paths both work
- Mining gloves / bonuses detected if equipped

### 3.3 Combat Tests

Validates the bot's combat loop against NPCs.

| Test | Type | Target | Key Validations |
|---|---|---|---|
| combat-01 | Melee basic | Cow, Chicken | Attack, loot, eat food, health monitoring |
| combat-02 | Ranged basic | Giant rat, Spider | Weapon/ammo management, distance control |
| combat-03 | Magic basic | Dark wizard, Zombie | Rune management, spell selection, autocast |
| combat-04 | Multitarget | 3+ rats/cows | Targeting priority, aggro management |
| combat-05 | Safespot detection | Lesser demon | Safespot recognition, movement discipline |

**Example Test:**
```yaml
id: combat-01-melee-basic
name: "Melee Combat — Cows in Lumbridge"
prerequisites:
  - combat_level >= 3
  - inventory_contains: { bronze_sword: 1, bronze_shield: 1, trout: 5 }
steps:
  - action: navigate
    target: lumbridge_cow_field
  - action: combat.attack_npc
    npc_type: cow
    count: 10
    loot: true
  - action: combat.eat_when_hp_below
    threshold: 50
  - action: combat.loot_items
    items: [cowhide, raw_beef, bones]
expected_results:
  - bot survives all 10 kills without dying
  - health never drops below 10%
  - looted items appear in inventory
  - correct XP drops detected
pass_fail_criteria:
  - PASS: 10/10 cows killed, bot alive, items looted
  - FAIL: Bot dies, health < 10% at any point, or 0 items looted
```

### 3.4 Banking Tests

Ensures the bot can interact with bank interfaces reliably.

| Test | Focus | Key Validations |
|---|---|---|
| banking-01 | Deposit all | Open bank, deposit all inventory, verify empty |
| banking-02 | Withdraw specific | Withdraw by name, quantity, verify count |
| banking-03 | Inventory mgmt | Withdraw → use skill → deposit → repeat (sustain loop) |
| banking-04 | Grand Exchange | GE buy/sell, offer management, price checking |

**Example Test:**
```yaml
id: banking-01-deposit
name: "Basic Banking — Deposit All Items"
prerequisites:
  - location: "near a bank booth"
  - inventory_not_empty: true
steps:
  - action: bank.open
  - action: bank.deposit_all
  - action: bank.close
  - action: inventory.check
expected_results:
  - inventory is completely empty after deposit_all
  - bank window opened within 5s of interaction
  - no duplicate deposit or item duplication errors
pass_fail_criteria:
  - PASS: Inventory empty, bank opened successfully
  - FAIL: Items remain in inventory, bank fails to open, or error thrown
```

### 3.5 Dialogue Tests

Verifies NPC interaction, dialogue trees, shops, and quest progression.

| Test | Focus | Key Validations |
|---|---|---|
| dialogue-01 | Basic NPC interact | Click NPC, read dialogue, close |
| dialogue-02 | Dialogue options | Select correct option, traverse tree |
| dialogue-03 | Quest start/continue | Progress quest dialogue, accept/reject |
| dialogue-04 | Shop interface | Open shop, browse, buy/sell items |

**Example Test:**
```yaml
id: dialogue-01-npc-interact
name: "Dialogue — NPC Interaction"
prerequisites:
  - location: "Lumbridge courtyard"
steps:
  - action: dialogue.interact
    npc: "Hans"
  - action: dialogue.read
  - action: dialogue.close
expected_results:
  - dialogue window opens within 3s
  - text is readable and contains expected string ("Lumbridge", "Adventurer")
  - dialogue closes without errors
pass_fail_criteria:
  - PASS: Dialogue opens, text verified, dialogue closes cleanly
  - FAIL: Dialogue fails to open, text mismatch, or close error
```

### 3.6 Zone Tests

Validates bot behavior in specific zones — loading, NPC presence, obstacles, and exits.

#### Lumbridge Zone Tests

| Test | Area | Validations |
|---|---|---|
| lumbridge-01 | Spawn | Correct spawn location, tutorial NPCs present |
| lumbridge-02 | Kitchen | Range usable, pots/food present, cooking works |
| lumbridge-03 | Courtyard | Hans present, sheep in field, doors openable |
| lumbridge-04 | Church / Graveyard | Father Aereck present, stairs work, zombie spawn |

#### Draynor Zone Tests

| Test | Area | Validations |
|---|---|---|
| draynor-01 | Manor | Entrance, gates, second floor access, Agility shortcut |
| draynor-02 | Market | General store open, banker present, stall thieving |
| draynor-03 | Sewers | Entry grate, rats, exit to Lumbrige swamp |

#### Varrock Zone Tests

| Test | Area | Validations |
|---|---|---|
| varrock-01 | East Bank | Bank booth functional, GE access, guards patrol |
| varrock-02 | West Bank | Bank booth, Champions' Guild door, Aubury's shop |
| varrock-03 | Grand Exchange | GE booth, history board, collection window |
| varrock-04 | Palace | King Roald present, guards, staircase to basement |
| varrock-05 | Wilderness Ditch | Crossing prompt, warning dialogue, return path |

**Example Test:**
```yaml
id: lumbridge-01-spawn
name: "Zone — Lumbridge Spawn Validation"
prerequisites:
  - new_player: true
steps:
  - action: zone.verify
    zone: "Lumbridge"
    expected_features:
      - name: "spawn_point"
        location: { x: 3222, y: 3218, plane: 0 }
      - name: "npc"
        id: "Guide"
        present: true
      - name: "exit"
        direction: "north"
        leads_to: "Draynor"
      - name: "exit"
        direction: "east"
        leads_to: "Al Kharid"
      - name: "interactable"
        id: "ladder"
        location: { x: 3205, y: 3209, plane: 0 }
        leads_to_floor: 1
expected_results:
  - all expected features detected within 10s
  - NPC presence confirmed
  - exits lead to correct zones
pass_fail_criteria:
  - PASS: All 5 expected features verified
  - FAIL: Any feature missing, wrong zone loaded, or detection timeout
```

---

## 4. Test Format Specification

Every test case MUST follow this exact structure for compatibility with the AgentBridge test runner.

### YAML Schema

```yaml
# Metadata
id: string                          # Unique test identifier (e.g., "cooking-01-level-1-10")
name: string                        # Human-readable test name
category: string                    # Dot-notation path (e.g., "skills.cooking")
priority: enum(P0, P1, P2, P3)     # Priority level (see §7)
estimated_duration: duration        # Expected wall-clock time (e.g., "120s", "5m")
required_mode: list[enum]           # Modes this test is valid for
dependencies: list[string]          # Test IDs that must pass before this test runs
tags: list[string]                  # Arbitrary tags for filtering (e.g., "night-run", "smoke", "full")

# Prerequisites
prerequisites:
  player_level?: int                # Minimum player level
  skill_*?: int                     # Minimum skill level for any skill (e.g., skill_cooking: 5)
  location?: string                 # Required starting location
  bank_contains?: list              # Items that must exist in bank
    - name: string
      quantity: int
  inventory_contains?: list         # Items that must be in inventory
    - name: string
      quantity: int
  quest_completed?: list[string]    # Required completed quests
  new_player?: bool                 # Fresh account needed

# Test Steps
steps:
  - action: string                  # Action identifier (e.g., "navigate", "bank.withdraw")
    params:                         # Action-specific parameters (varies by action)
      ...: ...
    timeout?: duration              # Per-step timeout override
    retry?: int                     # Number of retries on failure (default: 0)
    on_failure?: enum(skip, retry, abort)  # Behavior on step failure

# Expected Results (high-level outcomes)
expected_results: list[string]      # Human-readable outcome descriptions

# Pass/Fail Criteria
pass_fail_criteria:
  PASS: string                      # Condition for passing
  FAIL: string                      # Condition for failing
```

### Example Minimal Test

```yaml
id: mining-01-level-1-10
name: "Mining 1-10 — Tin and Copper"
category: skills.mining
priority: P0
estimated_duration: 420s
required_mode: [NIGHT_RUN_MODE, SAFE_AUTONOMOUS]
prerequisites:
  skill_mining: 1
  bank_contains:
    - { name: bronze_pickaxe, quantity: 1 }
steps:
  - action: bank.withdraw
    params: { items: [{ name: bronze_pickaxe, quantity: 1 }] }
  - action: navigate
    params: { target: "lumbridge_swamp_mine" }
  - action: skill.mine
    params: { ore: "tin", quantity: 28 }
  - action: skill.mine
    params: { ore: "copper", quantity: 28 }
  - action: navigate
    params: { target: "lumbridge_bank" }
  - action: bank.deposit_all
expected_results:
  - mining_level reaches 10+
  - 28 tin ore and 28 copper ore deposited
  - pickaxe still in inventory
pass_fail_criteria:
  PASS: "Mining level >= 10, 56+ ores deposited, pickaxe returned"
  FAIL: "Level target not met, ore count < 56, or pickaxe missing"
```

---

## 5. AgentBridge Integration

All tests are executed via AgentBridge, which provides the runtime environment for bot control and observation.

### Execution Model

```
Test Runner (CI/CD)
    │
    ▼
AgentBridge Orchestrator
    │
    ├──► BotInstance 1 (rs-bot-01) ──► Game Client
    ├──► BotInstance 2 (rs-bot-02) ──► Game Client
    └──► ...
    │
    ▼
Test Result Aggregator ──► Report Generator
```

### AgentBridge Actions Used

| Action | Test Usage | Parameters |
|---|---|---|
| `navigate(to, opts)` | Movement tests | `to`: coordinate/zone name, `opts`: timeout, precision |
| `bank.open()` | All banking tests | — |
| `bank.depositAll()` | Banking tests | — |
| `bank.withdraw(name, qty)` | Banking, skill tests | `name`: item name, `qty`: integer |
| `skill.cookAll(loc)` | Cooking tests | `loc`: range/cooking location |
| `skill.chop(treeType, count)` | Woodcutting tests | `treeType`: tree name, `count`: integer |
| `skill.mine(oreType, count)` | Mining tests | `oreType`: ore name, `count`: integer |
| `combat.attack(npc, count)` | Combat tests | `npc`: NPC name, `count`: kill target |
| `combat.eat(threshold)` | Combat tests | `threshold`: HP percentage |
| `combat.loot(target)` | Combat tests | `target`: loot filter |
| `dialogue.interact(npc)` | Dialogue tests | `npc`: NPC name |
| `dialogue.selectOption(idx)` | Dialogue tests | `idx`: option index |
| `zone.verify(name)` | Zone tests | `name`: zone identifier |
| `player.getStats()` | All tests | Returns current stat snapshot |
| `inventory.contents()` | All tests | Returns current inventory |
| `bank.contents()` | Banking tests | Returns bank contents |

### Test Runner Configuration

The AgentBridge runner processes all `.yaml` files in the suite directory:

```bash
# Run full suite
agentbridge run-suite \
  --suite /tmp/night-run-docs/qa/playerbot-regression-suite/ \
  --mode NIGHT_RUN_MODE \
  --output /tmp/night-run-results/

# Run specific category
agentbridge run-suite \
  --suite /tmp/night-run-docs/qa/playerbot-regression-suite/ \
  --filter "skills.cooking" \
  --mode SAFE_AUTONOMOUS

# Run priority P0 only
agentbridge run-suite \
  --suite /tmp/night-run-docs/qa/playerbot-regression-suite/ \
  --filter "priority=P0" \
  --mode NIGHT_RUN_MODE
```

### Result Aggregation

Each test produces a structured result that feeds into the reporting pipeline:

```json
{
  "test_id": "cooking-01-level-1-10",
  "status": "PASS",
  "duration_ms": 342000,
  "steps_completed": 5,
  "steps_failed": 0,
  "errors": [],
  "snapshots": [
    {
      "step": 3,
      "timestamp": "2026-06-21T02:15:30Z",
      "inventory": { "cooked_shrimp": 28 },
      "player_stats": { "cooking": 7 }
    }
  ],
  "final_state": {
    "cooking_level": 11,
    "inventory_empty": true,
    "bank_count": 56
  }
}
```

---

## 6. Reporting Format

After suite execution, the runner generates a comprehensive report.

### JSON Report Structure

```json
{
  "suite": {
    "id": "night-run-2026-06-21",
    "mode": "NIGHT_RUN_MODE",
    "started_at": "2026-06-21T02:00:00Z",
    "completed_at": "2026-06-21T03:45:00Z",
    "duration_ms": 6300000,
    "total_tests": 42,
    "passed": 38,
    "failed": 3,
    "skipped": 1
  },
  "summary": {
    "pass_rate": 90.48,
    "critical_failures": 0,
    "blocking_failures": 1,
    "category_breakdown": {
      "movement": { "passed": 3, "failed": 0, "total": 3 },
      "skills.cooking": { "passed": 2, "failed": 1, "total": 3 },
      "skills.woodcutting": { "passed": 3, "failed": 0, "total": 3 },
      "skills.mining": { "passed": 3, "failed": 0, "total": 3 },
      "combat": { "passed": 4, "failed": 1, "total": 5 },
      "banking": { "passed": 4, "failed": 0, "total": 4 },
      "dialogue": { "passed": 3, "failed": 1, "total": 4 },
      "zones": { "passed": 16, "failed": 0, "total": 16 },
      "integration": { "passed": 0, "failed": 0, "total": 1 }
    }
  },
  "failures": [
    {
      "test_id": "cooking-02-level-10-20",
      "status": "FAIL",
      "error": "Burn rate exceeded 40% threshold (actual: 62%)",
      "duration_ms": 480000,
      "steps_failed": 3,
      "screenshot_ref": "/tmp/night-run-results/screenshots/cooking-02-2026-06-21T024530.png"
    }
  ],
  "flaky_tests": [
    {
      "test_id": "combat-05-safespot-detection",
      "status": "FAIL",
      "notes": "Safespot not recognized on first attempt; retry succeeded. Potential timing issue."
    }
  ],
  "performance_warnings": [
    {
      "test_id": "woodcutting-03-level-20-30",
      "expected_duration": 900000,
      "actual_duration": 1120000,
      "notes": "Yew tree competition caused slower XP rates"
    }
  ]
}
```

### HTML Executive Summary

A companion HTML report is generated for human review, containing:
- **Dashboard:** Pass/fail pie chart, duration bar chart, mode breakdown
- **Failure Table:** Sortable list of all failures with links to logs/screenshots
- **Flaky Test Log:** Tests that passed after retry
- **Performance Trends:** Duration comparisons against last 7 runs
- **Category Health:** Color-coded per-category pass rates

### Artifacts Generated

| Artifact | Path | Description |
|---|---|---|
| Full JSON Report | `{output_dir}/report.json` | Machine-readable results |
| HTML Dashboard | `{output_dir}/report.html` | Human-readable summary |
| Test Logs | `{output_dir}/logs/{test_id}.log` | Per-test verbose logs |
| Screenshots | `{output_dir}/screenshots/{test_id}-{timestamp}.png` | Failure screenshots |
| JUnit XML | `{output_dir}/junit.xml` | CI tool integration |

---

## 7. Priority Matrix

Tests are prioritized differently depending on the execution mode. This matrix defines which tests run in each mode.

### Mode Definitions

| Mode | Purpose | Frequency | Max Duration | Coverage Target |
|---|---|---|---|---|
| **NIGHT_RUN_MODE** | Full regression after every build | Nightly (or per-commit) | 60 min | All P0 + P1 |
| **SAFE_AUTONOMOUS** | Self-initiated bot operation over extended period | Continuous | Unlimited | All P0–P3 |
| **ASSISTED** | User-guided bot with human supervision | On-demand | User-defined | All P0–P3 |

### Priority Matrix

| Priority | Label | NIGHT_RUN_MODE | SAFE_AUTONOMOUS | ASSISTED | Description |
|---|---|---|---|---|---|
| **P0** | Critical | ✅ Required | ✅ Required | ✅ Required | Core functionality — failure blocks release |
| **P1** | High | ✅ Required | ✅ Required | ✅ Required | Important — must pass before next night-run |
| **P2** | Medium | ⚠️ Optional | ✅ Required | ✅ Required | Standard coverage — required for autonomous ops |
| **P3** | Low | ❌ Skipped | ⚠️ Optional | ✅ Required | Edge cases, rare zones, niche skills |

### Priority Assignment Per Category

| Category | P0 Tests | P1 Tests | P2 Tests | P3 Tests |
|---|---|---|---|---|
| **Movement** | pathfinding-basic | pathfinding-obstacles, multilevel | follow-player | — |
| **Cooking** | level-1-10 | level-10-20 | level-20-30 | burn-rate-edge |
| **Woodcutting** | level-1-10 | level-10-20 | level-20-30 | special-tree-interaction |
| **Mining** | level-1-10 | level-10-20 | level-20-30 | gem-rock-mining |
| **Combat** | melee-basic, ranged-basic | magic-basic, safespot-detection | multitarget | prayer-flicking, gear-switching |
| **Banking** | deposit, withdraw | inventory-management | grand-exchange | collection-box |
| **Dialogue** | npc-interact | dialogue-options, shop | quest-start | quest-chain-progression |
| **Zones (Lumbridge)** | spawn, kitchen | courtyard, church | — | — |
| **Zones (Draynor)** | manor, market | sewers | — | — |
| **Zones (Varrock)** | east-bank, west-bank | grand-exchange, palace | wilderness-ditch | — |
| **Integration** | agentbridge-smoke | — | — | — |

### Mode-Specific Execution Profiles

```yaml
# NIGHT_RUN_MODE profile
night_run_profile:
  priority_filter: [P0, P1]
  retry_failed: true
  max_retries: 2
  timeout_multiplier: 1.5    # generous timeout for CI variance
  screenshot_on_fail: true
  abort_on_critical_fail: true
  parallel_execution: false   # sequential to ensure reproducibility

# SAFE_AUTONOMOUS profile
safe_autonomous_profile:
  priority_filter: [P0, P1, P2]
  retry_failed: true
  max_retries: 3
  timeout_multiplier: 2.0    # account for server lag
  screenshot_on_fail: true
  abort_on_critical_fail: false  # continue running other tasks
  parallel_execution: false

# ASSISTED profile
assisted_profile:
  priority_filter: [P0, P1, P2, P3]
  retry_failed: true
  max_retries: 5
  timeout_multiplier: 3.0    # user may interrupt
  screenshot_on_fail: true
  abort_on_critical_fail: false
  parallel_execution: false
  interactive_failures: true  # prompt user on failure
```

### Regression Exit Criteria

Before any release or deployment:

| Condition | NIGHT_RUN_MODE | SAFE_AUTONOMOUS | ASSISTED |
|---|---|---|---|
| P0 pass rate | 100% | 100% | 100% |
| P1 pass rate | ≥ 95% | 100% | 100% |
| P2 pass rate | N/A | ≥ 90% | ≥ 95% |
| P3 pass rate | N/A | N/A | ≥ 80% |
| No critical failures | ✅ | ✅ | ✅ |
| No blocking failures | ✅ | ✅ | ✅ |
| All screenshots reviewed | ✅ | ❌ | ❌ |
| Flaky test count ≤ 3 | ✅ | ✅ | ❌ |

---

## Appendix A: Common Failure Modes & Remediation

| Symptom | Likely Cause | Remediation |
|---|---|---|
| Bot gets stuck at same tile | Pathfinding obstacle | Verify obstacle interaction; add door/gate opening step |
| Items not found in bank | Bank tab changed or item name mismatch | Validate item name against game data; check tab ID |
| Skill XP gain too slow | Wrong tool/level requirement | Verify tool equip; confirm level prerequisites are met |
| Dialogue not progressing | Option index changed | Use text matching instead of index; add retry logic |
| Combat bot dies repeatedly | Food not eating or gear insufficient | Check eat threshold configuration; verify gear stats |
| Zone not loading | Wrong coordinates or map region | Re-verify coordinate data; add region load wait |
| Inventory full | No banking step in loop | Add deposit-all step after each batch |
| AgentBridge timeout | Action taking longer than expected | Increase timeout multiplier for laggy environments |

---

## Appendix B: Environment Requirements

| Requirement | Specification |
|---|---|
| Game Client Version | Latest stable (verified per run) |
| Account Type | Fresh F2P accounts for skill tests; member accounts for zone tests |
| AgentBridge Version | ≥ 2.1.0 |
| Bot Client Version | ≥ 4.3.0 |
| Resource Requirements | 2 CPU cores, 4 GB RAM per bot instance |
| Network Requirements | < 200ms latency to game server; stable connection |
| Storage Requirements | 10 GB for logs and screenshots (30-day retention) |

---

## Appendix C: Adding New Tests

1. Create `.yaml` file in the appropriate category subdirectory
2. Follow the YAML schema defined in §4
3. Assign priority based on the matrix in §7
4. Add `required_mode` to specify which modes support the test
5. Run validation: `agentbridge validate-test <file.yaml>`
6. Run the test manually: `agentbridge run-test <file.yaml> --mode SAFE_AUTONOMOUS`
7. Once passing consistently, commit to the suite repository

---

*End of Document 3 — PlayerBot Regression Suite*
