# Mining 1-30 Playerbot QA Scenario

> **Cache revision:** 233  
> **Skill:** Mining  
> **Scope:** Free-to-play, levels 1 through 30  
> **Category:** playerbot-qa / scenario-mechanical  
> **Risk Level:** 2 (moderate — mining interactions span multiple rock types, locations, and equipment states)
>
> **⚠️ Implementation note:** The mining skill module ALREADY EXISTS at `content/skills/mining/` (5 .kt files, 790 lines, 10 ore types + gems, pickaxe system). This scenario should **validate** the existing implementation, not build a new one. Focus on whether rocks deplete/respawn, XP rates match, pickaxe progression works, and bots can navigate between rock clusters without crashing.

---

## 1. Objective

Validate that a playerbot can progress from Mining level 1 to level 30 using only F2P rocks and equipment. The bot must handle rock depletion, pickaxe progression, inventory management, level-up interruptions, and location transitions without human intervention.

**Success condition:** Bot reaches Mining level 30 (~13,363 XP) and can mine coal rocks (level 30).

**Failure conditions:**
- Bot gets stuck (waiting indefinitely at empty rock, missing pickaxe, full inventory)
- Bot attempts to mine a rock above its level requirement
- Bot cannot navigate between rock clusters
- Bot crashes or disconnects during mining loop

---

## 2. Starting State

| Parameter | Value |
|-----------|-------|
| Location | Lumbridge Castle courtyard |
| Inventory | 1 × bronze pickaxe, 27 empty slots |
| Equipment | Bronze pickaxe wielded (if Attack ≥ 1 — default fresh account has 1 Attack) |
| Mining level | 1 (0 XP) |
| Attack level | 1 (required for bronze pickaxe wield) |
| Other skills | Irrelevant — no quests, no money, no additional items |
| Game mode | F2P (no access to members areas) |

**Setup procedure for test runner:**
1. Create fresh character at Lumbridge spawn point
2. Verify character has bronze pickaxe in inventory
3. If Attack level < 1, keep pickaxe in inventory (mining works with pickaxe in inventory)
4. Record starting Mining XP (expect 0)
5. Record starting inventory (expect 1 bronze pickaxe, 27 empty)

---

## 3. XP Budget & Ore Requirements

Level 30 requires **13,363 XP** total from level 1.

### Phase 1: Levels 1–15 (Copper/Tin)

| Metric | Value |
|--------|-------|
| XP needed (L1→15) | 2,411 |
| XP per copper/tin ore | 17.5 |
| Ores needed | 2,411 ÷ 17.5 = **138 ores** (split copper+tin) |
| Expected time (bronze pick, 8-tick cycle) | ~138 × 4.8s = ~11 min |
| Expected time (iron pick, 7-tick cycle) | ~138 × 4.2s = ~10 min |

### Phase 2: Levels 15–30 (Iron)

| Metric | Value |
|--------|-------|
| XP needed (L15→30) | 10,952 |
| XP per iron ore | 35 |
| Ores needed | 10,952 ÷ 35 = **313 ores** |
| Expected time (iron pick, 7-tick) | ~313 × 4.2s = ~22 min |
| Expected time (steel pick, 6-tick) | ~313 × 3.6s = ~19 min |
| Expected time (mithril pick, 5-tick) | ~313 × 3.0s = ~16 min |

### Total Scenario Budget

| Total | Value |
|-------|-------|
| Total XP | ~13,363 |
| Total ores mined | ~451 (138 copper/tin + 313 iron) |
| Estimated runtime | ~30–40 minutes (varies with pickaxe tier and rock competition) |

---

## 4. Equipment Progression

Pickaxe upgrades that become available during levels 1–30:

| Level | Pickaxe | Mining Req | Attack Req | Mining Rate | Available? |
|-------|---------|:----------:|:----------:|:-----------:|:----------:|
| Start | Bronze | 1 | 1 | 8 ticks | ✅ Default spawn |
| 1 | Iron | 1 | 1 | 7 ticks | ✅ Buy from NPC or starter |
| 6 | Steel | 6 | 5 | 6 ticks | ✅ F2P (Nurmer's Pickaxe Shop, Varrock) |
| 11 | Black | 11 | 10 | 5 ticks | ⚠️ Quest-locked (Heroes' Quest) — **not reliably F2P** |
| 21 | Mithril | 21 | 20 | 5 ticks | ✅ F2P (Nurmer's Pickaxe Shop) |
| 31 | Adamant | 31 | 30 | 4 ticks | **Out of scope** (requires 31 Mining) |

**Bot behavior:**
- The bot should mine with whatever pickaxe it has. A bronze pickaxe is sufficient for the entire 1–30 journey.
- If the bot can access a pickaxe shop and has coins, it should upgrade to iron (level 1), steel (level 6), and mithril (level 21) for faster rates.
- **Do not** require pickaxe upgrades as a success criterion — treat them as optional optimization.
- **Do test** that switching to a higher-tier pickaxe reduces mining cycle time.

### Pickaxe acquisition paths (F2P)

| Pickaxe | Source | Cost | Location |
|---------|--------|:----:|----------|
| Iron | Pickaxe shop | 140 gp | Varrock, east of square |
| Steel | Pickaxe shop | 500 gp | Varrock |
| Black | Treasure Trails / Heroes' Quest | N/A | **Not reliably obtainable in F2P** |
| Mithril | Pickaxe shop | 1,350 gp | Varrock |

---

## 5. F2P Mining Locations Reference

### Phase 1 locations (copper & tin, levels 1–15)

| Mine | Rocks | Distance from Lumbridge | Notes |
|------|-------|:----------------------:|-------|
| **Lumbridge Swamp** | 2 copper, 2 tin | Immediate (south of castle) | ✅ **Primary location** — nearest spawn, zero travel |
| **Varrock Southeast** | 3 copper, 3 tin | Medium (walk east) | Good fallback |
| **Al Kharid** | 2 copper, 2 tin | Short (walk south, then east past gate) | Also has iron for Phase 2 |
| **Barbarian Village** | 0 copper, 2 tin | Long (north) | Tin + coal only |

### Phase 2 locations (iron, levels 15–30)

| Mine | Iron rocks | Travel from Lumbridge | Notes |
|------|:----------:|:--------------------:|-------|
| **Al Kharid Mine** | 3 adjacent | Short (through Al Kharid gate) | ✅ **Best F2P iron spot** — 3 rocks on one tile |
| **Varrock Southeast** | 3 | Medium | Good alternative |
| **Falador (Dwarven Mine entrance)** | 2 | Long (north-west) | Multi-rock area |
| **Dwarven Mine (F2P section)** | 2 | Long (through Falador) | Deep, multi-ore |

### Priority order for bot pathfinding

1. Lumbridge Swamp Mine — copper/tin (levels 1–15)
2. Al Kharid Mine — iron (levels 15–30)
3. If Al Kharid is overcrowded or depleted → Varrock Southeast Mine (copper/tin/iron)
4. If both are inaccessible → Dwarven Mine (copper/tin/iron/silver/coal)

---

## 6. F2P Rock Type Reference

All rock types accessible in F2P with their cache symbols and level requirements:

| Rock | Level | XP | Cache LOC Symbol(s) | Cache Obj Symbol | Notes |
|:----:|:-----:|:--:|:-------------------|:-----------------|:------|
| Clay | 1 | 5 | `clayrock1` (11362), `clayrock2` (11363) | `clay` | Low XP, skip |
| Copper | 1 | 17.5 | `copperrock1` (10943), `copperrock2` (11161) | `copper_ore` (436) | Phase 1 primary |
| Tin | 1 | 17.5 | `tinrock1` (11360), `tinrock2` (11361) | `tin_ore` (438) | Phase 1 primary |
| Blurite | 10 | 17.5 | `blurite_rock_1` (11378), `blurite_rock_2` (11379) | `blurite_ore` | Quest-locked — skip |
| Iron | 15 | 35 | `ironrock1` (11364), `ironrock2` (11365) | `iron_ore` (440) | Phase 2 primary |
| Silver | 20 | 40 | `silverrock1` (11368), `silverrock2` (11369) | `silver_ore` | Not needed for XP target |
| Coal | 30 | 50 | `coalrock1` (11366), `coalrock2` (11367) | `coal` (453) | **Goal unlock** |
| Gold | 40 | 65 | `goldrock1` (11370), `goldrock2` (11371) | `gold_ore` (444) | Out of scope (req 40) |
| Mithril | 55 | 80 | `mithrilrock1` (11372), `mithrilrock2` (11373) | `mithril_ore` (447) | Out of scope |
| Adamantite | 70 | 95 | `adamantiterock1` (11374), `adamantiterock2` (11375) | `adamantite_ore` (449) | Out of scope |
| Runite | 85 | 125 | `runiterock1` (11376), `runiterock2` (11377) | `runite_ore` (451) | Out of scope |

### Pickaxe animation cache symbols

| Pickaxe | Animation Seq Symbol |
|---------|---------------------|
| Bronze | `human_mining_bronze_pickaxe` (625) |
| Iron | `human_mining_iron_pickaxe` (626) |
| Steel | `human_mining_steel_pickaxe` (627) |
| Mithril | `human_mining_mithril_pickaxe` (629) |
| Adamant | `human_mining_adamant_pickaxe` (628) |
| Rune | `human_mining_rune_pickaxe` (624) |
| Dragon | `human_mining_dragon_pickaxe_pretty` (642) |

---

## 7. Test Phases

### Phase 1: Copper & Tin (Levels 1–15)

**Location:** Lumbridge Swamp Mine (south of Lumbridge Castle)

**Steps:**
1. Spawn at Lumbridge Castle courtyard
2. Navigate south to the swamp mine entrance (south of the castle, near the river)
3. Locate copper and tin rocks
4. Click a copper rock → verify mining animation plays → verify copper ore appears in inventory → verify XP gain (+17.5)
5. Repeat for tin rock → verify tin ore appears
6. Continue mining copper and tin until level 15 Mining (2,411 XP total)

**Validation per rock interaction:**
- [ ] Correct mining animation plays for the equipped pickaxe type
- [ ] Rock shows the mining interaction (player swings pickaxe at rock)
- [ ] Ore is added to inventory after successful cycle
- [ ] XP drop appears (+17.5 for copper/tin)
- [ ] Rock depletes (visual change or interaction disabled) after successful mining
- [ ] Depleted rock respawns within expected time (~5-15 seconds for copper/tin)
- [ ] After reaching level 15, the bot transitions to iron mining

### Phase 2: Iron (Levels 15–30)

**Location:** Al Kharid Mine (recommended) or Varrock Southeast Mine

**Steps:**
1. Navigate from Lumbridge Swamp Mine to Al Kharid Mine
   - Walk east from Lumbridge
   - Enter Al Kharid through the gate (no payment required for entry — the border guard NPC charges 10 gp to pass, but there's a free route via the Lumbridge Swamp cave shortcut or going around)
   - Continue south-east to the mine north of Al Kharid
2. Locate the 3 iron rocks on the single tile (the "triangle" spot)
3. Mine iron ore continuously until level 30 Mining

**Validation per rock interaction:**
- [ ] Mining animation plays for iron rocks
- [ ] +35 XP awarded per iron ore mined
- [ ] Rock depletes after successful mining
- [ ] Rock respawns (~5 seconds for iron in OSRS)
- [ ] When inventory is full, bot either drops ores or banks them
- [ ] Level-up notification fires correctly at level thresholds

### Transition from Phase 1 to Phase 2

The bot should automatically recognize when it reaches Mining level 15 and switch from copper/tin rocks to iron rocks. This requires:

- [ ] Bot can detect current Mining level
- [ ] Bot can identify iron rocks (by LOC proximity or level filter)
- [ ] Bot paths to nearest iron rock location
- [ ] Bot does not attempt to mine iron before level 15

---

## 8. Edge Cases

### 8.1 Rock Depletion & Respawn

| Test | Expected Behavior |
|------|-------------------|
| Mine a copper/tin/iron rock until it depletes | Rock becomes unmineable (visual change or interaction disabled) |
| Wait at the depleted rock | Rock respawns after OSRS-standard time (copper/tin: ~3-5s, iron: ~5s, varies per rock type) |
| Attempt to mine before respawn | No interaction — "There is currently no ore available to mine in this rock." message |
| Switch to another rock while depleted one respawns | Bot correctly targets available rocks |

### 8.2 Pickaxe Unequip / Missing

| Test | Expected Behavior |
|------|-------------------|
| Remove pickaxe from inventory (drop, bank, or destroy) | Bot detects missing pickaxe — message: "You need a pickaxe to mine this rock." |
| Bot has pickaxe in inventory but not wielded | Bot should still be able to mine (pickaxe in inventory works) |
| Bot has wrong-tier pickaxe (e.g., bronze pick on rune rock) | Rock applies level requirement — message: "You need a Mining level of X to mine this rock." |
| Bot wields a non-pickaxe weapon | Bot should be able to mine with pickaxe in inventory even if wielding something else |

### 8.3 Full Inventory Handling

| Test | Expected Behavior |
|------|-------------------|
| 28/28 inventory slots full | "Your inventory is too full to hold any more ore." message |
| Bot with full inventory attempts to mine | Interaction refused — ore is not added to inventory |
| After dropping 1+ ore | Mining resumes on next attempt |
| Bot logic: drop ore vs bank ore | Both are valid — dropping is faster XP, banking preserves ore. Bot should be able to do either. |

### 8.4 Level-Up Interruption

| Test | Expected Behavior |
|------|-------------------|
| Mining XP crosses a level threshold | Level-up animation plays, stats increase, Mining level is now higher |
| Mining cycle completes during level-up | The level-up does not cancel the mining action — ore is awarded normally |
| After level-up, new pickaxe tier is usable | Bot can equip higher-tier pickaxe (if available in inventory) |
| Level-up message displayed correctly | "Congratulations, you've just advanced a Mining level!" + level number |

### 8.5 Rock Hopping (Multi-Mining)

| Test | Expected Behavior |
|------|-------------------|
| Bot has 3 iron rocks in range at Al Kharid | Bot can click any available rock without moving |
| All 3 rocks depleted simultaneously | Bot waits for nearest rock to respawn, or walks to another rock cluster |
| Bot is mid-animation and rock depletes | Animation completes, ore is awarded or failure message appears |
| Bot clicks next rock while previous rock's respawn timer is active | Correctly identifies that rock is not ready and targets another |

### 8.6 Wrong Rock Type

| Test | Expected Behavior |
|------|-------------------|
| Bot clicks an adamantite rock at level 15 | "You need a Mining level of 70 to mine this rock." message |
| Bot clicks a runite rock at level 15 | "You need a Mining level of 85 to mine this rock." message |
| Bot with bronze pickaxe tries to mine runite | Level requirement blocks it, not pickaxe requirement |
| Bot next to both tin and iron at level 15 | Bot correctly chooses iron (higher XP) |

### 8.7 Aggressive NPC Interruption

| Test | Expected Behavior |
|------|-------------------|
| Scorpion at Al Kharid mine attacks bot (lvl 14 scorpions) | Bot continues mining (1-2 damage per hit — not lethal for a fresh character who may have 10 HP) |
| Bot takes damage to 0 HP | Bot dies, respawns at Lumbridge, retains inventory items (including ores and pickaxe) |
| After respawn, bot returns to mine | Bot navigates back to mine location and resumes mining |

---

## 9. Validation Checklist

### Mining Mechanics

- [ ] **Mining animation plays** for each pickaxe type (bronze, iron, steel, mithril)
- [ ] **Rocks deplete and respawn** — each rock type has a depletion state and respawn timer
- [ ] **XP awarded matches OSRS rates**: copper/tin = 17.5, iron = 35 (within ±1 XP tolerance)
- [ ] **Level-up messages fire correctly** at levels 2, 3, ..., 30
- [ ] **Level-up unlocks new pickaxe tiers** — steel at 6, mithril at 21
- [ ] **Pickaxe requirements enforced** — cannot use mithril pickaxe at level 1
- [ ] **Rock level requirements enforced** — level 15 required for iron, level 30 for coal
- [ ] **Empty rock handling** — "There is currently no ore available to mine in this rock."

### Inventory & Equipment

- [ ] **Ore enters inventory** after successful mining attempt
- [ ] **Full inventory blocks mining** — "Your inventory is too full to hold any more ore."
- [ ] **Pickaxe in inventory works** — mining is possible without wielding the pickaxe
- [ ] **No pickaxe blocks mining** — "You need a pickaxe to mine this rock."

### Navigation & Bot Behavior

- [ ] **Bot can path from Lumbridge to Lumbridge Swamp Mine** (Phase 1 location)
- [ ] **Bot can path from Swamp Mine to Al Kharid Mine** (Phase 2 location)
- [ ] **Bot returns to mine after dying** (if killed by scorpion)
- [ ] **Bot does not get stuck on scenery/obstacles** during pathing
- [ ] **Bot handles rock competition** — multiple bots at same rock do not cause deadlock

### Performance

- [ ] No memory leaks during continuous mining (~30 min run)
- [ ] No FPS drops around rock particles/animation effects
- [ ] Bot does not spam interactions faster than game ticks allow
- [ ] Bot maintains consistent mining rate (not stuck in loops)

---

## 10. Telemetry Requirements

The test harness MUST capture the following data points for every run:

### Per-Interaction Events

| Event | Data Captured | Format |
|-------|---------------|--------|
| `mining_start` | rock type, rock loc, bot Mining level, equipped pickaxe | `{ts, rock_sym, loc_coords, mining_lvl, pickaxe_sym}` |
| `mining_success` | ore received, XP gained, Mining level after | `{ts, ore_sym, xp_gained, new_mining_lvl}` |
| `mining_fail` | reason (full inv, no pickaxe, wrong lvl, no ore) | `{ts, fail_reason, fail_code}` |
| `rock_deplete` | rock loc, rock type, time since last deplete | `{ts, rock_sym, loc_coords, respawn_duration}` |
| `rock_respawn` | rock loc, rock type | `{ts, rock_sym, loc_coords}` |
| `level_up` | skill, old level, new level | `{ts, skill, old_lvl, new_lvl}` |
| `pickaxe_equip` | pickaxe type, slot | `{ts, pickaxe_sym, slot}` |
| `inventory_full` | current inventory state | `{ts, item_count, item_types}` |
| `item_drop` | item dropped, count | `{ts, item_sym, count}` |
| `npc_aggro` | NPC type, damage taken | `{ts, npc_sym, damage}` |
| `player_death` | death cause, items lost | `{ts, cause, lost_items}` |
| `location_change` | from, to | `{ts, from_region, to_region}` |
| `bot_stuck` | reason, location, duration | `{ts, reason, loc_coords, duration_s}` |
| `session_metrics` | duration, total_ops, total_xp, errors | end-of-run summary |

### Run-Level Aggregates

| Metric | Source |
|--------|--------|
| Total XP gained | Sum of `mining_success.xp_gained` |
| Total ores mined (by type) | Count of `mining_success` per ore_sym |
| Total time to level 30 | Timestamp delta from first `mining_start` to `mining_success` at level 30 |
| Error count | Count of `mining_fail` events |
| Stuck events | Count of `bot_stuck` events |
| Deaths | Count of `player_death` events |
| Average mining rate (ores/min) | Total ores / total time (minutes) |
| Average XP rate (XP/h) | Total XP / total time (hours) |

### Expected Metrics (Targets)

These are approximate targets for a well-functioning playerbot:

| Metric | Phase 1 (1-15) | Phase 2 (15-30) | Total |
|--------|:--------------:|:---------------:|:-----:|
| Ores mined | ~138 | ~313 | ~451 |
| XP gained | ~2,415 | ~10,955 | ~13,370 |
| Time (bronze pick, no upgrades) | ~11 min | — | — |
| Time (iron pick) | — | ~22 min | ~32 min |
| Errors | 0 | 0 | 0 |
| Stuck events | 0 | 0 | 0 |
| Deaths | 0 | ≤1 (scorpion) | ≤1 |

---

## 11. Stuck-State Recovery

The bot MUST implement automatic recovery for each stuck condition:

| Condition | Detection | Recovery Action |
|-----------|-----------|-----------------|
| No pickaxe in inventory | Inventory scan → count pickaxe items = 0 | Enter stuck-state: report missing pickaxe, wait for human intervention |
| All rocks in area depleted | No interactable rock found after 3 scan cycles | Walk to nearest mine with same rock type (Phase 1: Varrock SE; Phase 2: Varrock SE or Dwarven Mine) |
| Inventory full | Inventory scan → empty slots = 0 | Drop all ores except pickaxe → resume mining. Or path to bank → deposit → return to mine |
| Stuck on geometry | Position unchanged for >30s with no mining activity | Re-calculate path to nearest rock. If stuck persists, teleport (or respawn) |
| Wrong rock type targeted | Mining attempt returns "You need a Mining level of X" | Ignore that rock, scan for correct-level rocks in area |
| Bot dies | HP = 0 → respawn sequence triggered | Re-equip pickaxe from inventory after respawn, navigate back to mine, resume mining |

### Timeout thresholds

| Timeout | Action |
|---------|--------|
| 5s without mining action | Continue attempting |
| 15s without mining action | Scan for nearest available rock, path toward it |
| 30s without mining action | Change location (walk to alternative mine) |
| 60s without mining action | Trigger stuck-state recovery with telemetry event |

---

## 12. Failure Mode Assertions

These assertions MUST be validated during the test scenario:

### Hard Failures (stop run, mark FAIL)

- [ ] Bot reaches level 30 and cannot mine coal rocks
- [ ] Bot attempts iron before level 15 and succeeds (level requirement not enforced)
- [ ] Bot mines ore without having a pickaxe in inventory
- [ ] Bot gets stuck for >5 minutes without recovery
- [ ] Bot disconnects or crashes during mining loop
- [ ] Mining XP awarded incorrectly (copper/tin != 17.5, iron != 35)
- [ ] Rock never respawns after depletion

### Soft Failures (flag as WARN, continue run)

- [ ] Bot takes >50% longer than expected time (performance regression)
- [ ] Bot does not upgrade pickaxe when available (optimization missing, not a bug)
- [ ] Mining animation does not match pickaxe type (visual only)
- [ ] Bot does not return to optimal rock cluster after respawning
- [ ] Multiple bots at same rock cause brief (≤10s) delay

---

## 13. Test Execution Procedure

### Setup

```bash
# 1. Ensure server is running with mining content enabled (check server boot log)
grep -i mining /tmp/rsmod-server.log

# 2. Verify mining cache symbols are loaded
grep -E "copperrock|tinrock|ironrock|coalrock" /tmp/rsmod-server.log

# 3. Create a fresh playerbot
# Using the playerbot management command:
# /bot add <bot-name> --fresh --skill mining --location lumbridge

# 4. Verify bot inventory has bronze pickaxe
# /bot inventory <bot-name>
```

### Execution

```bash
# 1. Start bot with mining task (level 1-30)
# /bot task <bot-name> --skill mining --target-level 30

# 2. Monitor bot progress
# /bot status <bot-name> --interval 60  # Status check every 60s
# /bot telemetry <bot-name> --live      # Real-time interaction feed

# 3. Wait for completion or timeout (60 min max)
```

### Validation

```bash
# 1. Check final Mining level
# /bot skill <bot-name> mining

# 2. Verify total ore counts match expectations
# /bot stat <bot-name> ores_mined

# 3. Export telemetry for analysis
# /bot export <bot-name> --format json --output mining-1-30-run.json

# 4. Run regression check against previous run
# /bot compare <bot-name> --baseline mining-1-30-v1.json
```

---

## 14. Environment Prerequisites

Before running this scenario, verify:

- [ ] Mining skill module is installed and active on the server
- [ ] All cache symbols exist (see Section 6 rock type reference)
- [ ] Mining animations are registered in seq.sym
- [ ] Copper, tin, and iron rocks are placed in the world at Phase 1/Phase 2 locations
- [ ] Rock depletion/respawn system is functional
- [ ] Pickaxe items exist in cache with correct properties
- [ ] PlayerBot framework is running and can accept task commands
- [ ] AgentBridge WebSocket is connected
- [ ] Telemetry pipeline is configured to capture mining events

### Mining content checklist (content/skills/mining/)

If the mining skill module does not exist yet, it must be created before this scenario can run:

- [ ] `build.gradle.kts` with base-conventions + pluginCommons
- [ ] `MiningModule.kt` — PluginModule entry point
- [ ] `MiningPlugin.kt` — PluginScript with mining interaction handlers
- [ ] Rock reference declarations (ore types with `find()`)
- [ ] Pickaxe reference declarations (find by animation or item)
- [ ] Level requirement enforcement on rock types
- [ ] Pickaxe tier enforcement on mining speed
- [ ] Rock depletion system with respawn timers
- [ ] Full inventory detection and messaging
- [ ] XP reward system per rock type
- [ ] Level-up detection
- [ ] `npcs.toml` spawn configs if NPC-based rocks are needed

---

## 15. Regression Tags

```
mining skill f2p lumbridge-swamp-mine al-kharid-mine varrock-east-mine
playerbot-qa scenario-mechanical pickaxe-progression rock-depletion
inventory-management stuck-state-recovery level-up
```

---

## 16. Version History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2026-06-21 | Rei (Hyraxknot QA) | Initial QA scenario for Mining 1-30 playerbot validation |
