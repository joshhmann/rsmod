# Zone Readiness Checklist

## Purpose

Track which content categories are automated and verified per zone. Used before calling a zone "complete" for automation workflow validation.

## Zone Status Levels

| Level | Meaning |
|-------|---------|
| 🔴 Not started | No inspection or generation done |
| 🟡 Inspected | Existing implementation documented |
| 🟢 Staged | Generated output exists in staging |
| ✅ Promoted | Content promoted to production |
| 🏆 Certified | Passed full zone verification |

## Zone: Lumbridge (Primary Validation Zone)

| Category | Status | Notes | Commit |
|----------|--------|-------|--------|
| **Cache/symbol truth** | ✅ Complete | `npc.sym`, `obj.sym`, `loc.sym`, `seq.sym` verified | baseline |
| **Placed loc extraction** | ✅ Complete | 46K placed locs exported to CSV | baseline |
| **Doors/stairs/ladders** | ✅ Complete | Generic systems bound: door, double door, ladder, staircase, bank booth, cooking range | baseline |
| **Bank/shop interactions** | ✅ Complete | Generic bank booth + bank chest. Lumbridge General Store + Bob's Brilliant Axes shop handlers | baseline |
| **Starter combat NPCs** | ✅ Complete | Man/Woman, Cow, Chicken, Goblin, Giant rat | Phase 1 |
| **Drop generation pipeline** | 🏆 Certified | G2 corpus drop generator committed | Phase 1 |
| **Drop tables — Starter** | ✅ Complete | Man/Woman, Cow, Chicken, Goblin, Giant rat promoted | Phase 1.5 |
| **Drop tables — Undead** | ✅ Complete | Skeleton, Zombie, Bat promoted | Phase 2 |
| **Drop tables — Humanoid** | ✅ Complete | Guard, Mugger, Barbarian, Dwarf promoted | Phase 3 |
| **Drop tables — Misc** | ✅ Complete | Imp, Dark Wizard, Rat promoted. Scorpion skipped (no corpus value) | Phase 4 |
| **Raw ID validator** | ✅ Complete | Script scans for raw cache IDs | Phase 1 |
| **Compile checks** | ✅ Complete | Module + full server compile | baseline |
| **Reports/staging/promote** | ✅ Complete | Workflow documented and repeatable | Phase 4 |
| **NPC spawns** | 🔴 Not started | Need G1 zone generator pass | — |
| **Dialogue** | 🔴 Not started | Need G3 dialogue porter | — |
| **Skill actions** | 🔴 Not started | Manual implementation | — |

## Zone: Draynor (Second Validation Zone — ✅ PASSED)

| Category | Status | Notes |
|----------|--------|-------|
| **Cache/symbol truth** | ✅ Inherited | Global cache |
| **Placed loc extraction** | ✅ Inherited | Full export available |
| **Doors/stairs/ladders** | 🔴 Not started | — |
| **Bank/shop interactions** | 🔴 Not started | — |
| **Drop generation pipeline** | 🏆 Certified | Same G2 generator, no workflow changes needed |
| **Drop tables — Jail Guard** | ✅ Promoted | New handler created (commit 51e66957) |
| **Drop tables — Wizard (regular)** | ✅ Promoted | New handler created (commit 51e66957) |
| **Drop tables — Skeletons** | ✅ Already handled | Draynor Manor skeletons covered by SkeletonDropTables |
| **Drop tables — Ghosts** | ✅ Skipped (Phase 2) | Wilderness-only drops |
| **Drop tables — Other NPCs** | ✅ Already handled | Men, women, guards, rats, spiders, dark wizards all covered |
| **NPC spawns** | ✅ Present | draynor.toml + draynor-manor.toml exist |
| **Dialogue** | 🔴 Not started | — |
| **Skill actions** | 🔴 Not started | — |

**Result:** ✅ **WORKFLOW GENERALIZES.** All Draynor-area combat NPCs now have drop handlers.
No workflow changes, special-casing, or combat behavior changes needed.

## Zone: Varrock (First Regional Batch — ✅ PASSED)

| Category | Status | Notes |
|----------|--------|-------|
| **Drop generation pipeline** | 🏆 Certified | Same workflow, no changes needed |
| **Drop tables — Thief** | ✅ Promoted | New handler (commit dffde286) |
| **Drop tables — Chaos Druid** | ✅ Promoted | New handler (commit dffde286) |
| **Drop tables — Guards** | ✅ Already handled | Phase 3 |
| **Drop tables — Men/Women** | ✅ Already handled | Phase 1.5 |
| **Drop tables — Sewer NPCs** | ✅ Already handled | Zombies, skeletons, rats, moss giants all covered |
| **Drop tables — Other** | ✅ Already handled | Barbarians, dark wizards |
| **Rogue** | ⏭️ Skipped | Wilderness-only per safety rules |
| **Highwayman** | ⏭️ Skipped | No combat registration |

**Result:** ✅ **FIRST REGIONAL BATCH PASSES.** Workflow unchanged from single-NPC to regional mode.
2 NPC families promoted, within the 10-family batch cap.

## Zone: Edgeville (Regional Batch #3 — ✅ PASSED)

| Category | Status | Notes |
|----------|--------|-------|
| **Drop generation pipeline** | 🏆 Certified | Same workflow, no changes |
| **Drop tables — Black Knight** | ✅ Enriched | Herbs, runes, aggressive fix (commit 11a026d6) |
| **Drop tables — Hill Giant** | ✅ Enriched | Herbs, post-2013 cleanup (commit 11a026d6) |
| **Drop tables — Chaos Druid** | ✅ Already handled | Varrock batch |
| **Drop tables — Dungeon NPCs** | ✅ Already handled | Zombies, skeletons, rats, spiders all covered |
| **Monk (Edgeville Monastery)** | ⏭️ Skipped | No combat registration |
| **Surface NPCs** | ⏭️ Skipped | Shop/quest NPCs only |

**Result:** ✅ **THIRD REGIONAL BATCH PASSES.** Workflow unchanged.
Edgeville Dungeon NPCs now fully covered.

## Zone: Al Kharid (Regional Batch #2 — ✅ PASSED)

| Category | Status | Notes |
|----------|--------|-------|
| **Drop generation pipeline** | 🏆 Certified | Same workflow, no changes needed |
| **Drop tables — Warrior Woman** | ✅ Enriched | Expanded herb/runes, added gem table (commit 5913a82a) |
| **Drop tables — Al Kharid Man** | ✅ Promoted | Added to city man table (commit 5913a82a) |
| **Drop tables — Guards (city)** | ✅ Already handled | city_guard / guard1 covered by GuardDropTables |
| **Scorpion** | ⏭️ Skipped (Phase 4) | No corpus value — all post-2013/wilderness |
| **Al Kharid Warrior** | ⏭️ Skipped | No combat registration (thieving only) |
| **Border guard** | ⏭️ Skipped | Dialogue NPC, no combat |

**Result:** ✅ **SECOND REGIONAL BATCH PASSES.** Workflow unchanged.
Al Kharid is small but validates desert/city variant handling.

## Zone: Falador (Regional Batch #4 — ✅ PASSED)

| Category | Status | Notes |
|----------|--------|-------|
| **Drop generation pipeline** | 🏆 Certified | Same workflow, no changes needed |
| **Drop tables — Falador Guard** | ✅ Promoted | `fai_falador_guard1` added to GuardDropTables registration (commit cc752479) |
| **Drop tables — Guards (generic)** | ✅ Already handled | `city_guard`/`guard1` already in GuardDropTables |
| **Drop tables — Hill Giant (mine)** | ✅ Already handled | `wilderness_hill_giant` in HillGiantDropTables |
| **Drop tables — Dwarf (mine)** | ✅ Already handled | `dwarf_normal` in DwarfDropTables |
| **Drop tables — Men/Women** | ✅ Already handled | Generic man/woman in ManWomanDropTables |
| **White Knights** | ⏭️ Skipped | No combat registration. Would need onNpcHit first. |
| **Falador dwarf variants** | ⏭️ Skipped | `fai_falador_dwarf_normal*` — non-combat city dwarves |
| **Falador citizens** | ⏭️ Skipped | Gardeners, workmen, bankers — non-combat NPCs |

**Result:** ✅ **FOURTH REGIONAL BATCH PASSES.** Workflow unchanged.
Falador is a guard-variant test: `fai_falador_guard1` uses existing guard table.
Most Falador NPCs are non-combat or already covered by generic tables.

## Zone: Port Sarim / Rimmington (Regional Batch #5 — ✅ PASSED)

| Category | Status | Notes |
|----------|--------|-------|
| **Drop generation pipeline** | 🏆 Certified | Same workflow, no changes needed |
| **Drop tables — Hobgoblin (Rimmington)** | ✅ Created | New HobgoblinDropTables.kt from corpus (135 items). Generic + Rimmington variants (commit 18d609e0) |
| **Drop tables — Jail Guards (Port Sarim)** | ✅ Already handled | Covered by JailGuardDropTables (Draynor batch) |
| **Combat registration — Rimmington hobgoblins** | ✅ Extended | Added `rimmington_hobgoblin_unarmed_1-3`, `rimmington_hobgoblin_armed_1` to AggressiveNpcCombat |
| **Drop tables — Generic man/woman** | ✅ Already handled | Covered by ManWomanDropTables |
| **Pirates (Port Sarim)** | ⏭️ Skipped | No combat registration. Spawns are in Karamja/Brimhaven, not Port Sarim in this build. |
| **Quest NPCs** | ⏭️ Skipped | Redbeard Frank, Thurgo, Chemist, Klarense, Wydin, Brian, Gerrant, Betty — quest/shop NPCs |

**Result:** ✅ **FIFTH REGIONAL BATCH PASSES.** Workflow unchanged.
Port Sarim is mostly quest/shop NPCs. Rimmington hobgoblins were the
only combat target — created full drop table and extended combat registration.


## Zone: Varrock West (Alternative Second Validation Zone)

| Category | Status | Notes |
|----------|--------|-------|
| All categories | 🔴 Not started | — |

## Zone Status Template

Use this template for new zones:

```

## Skipped NPC Registry

NPCs that were evaluated and skipped during drop automation batches, with the reasoning and unblock conditions for future revisit.

| NPC | Region | Symbols | Drop Count (Corpus) | Block Reason | Unblock Condition | Batch |
|-----|--------|---------|:-------------------:|-------------|-------------------|:-----:|
| Highwayman | Varrock | `highwayman` | Unknown | No combat registration. | Add onNpcHit registration + create table. | Varrock |
| Rogue | Wilderness | `rogue`, `wilderness_rogue` | Wilderness-only | Wilderness NPC. Looting bags, keys — would need wilderness zone filter. | Wilderness zone handling. | Varrock |
| Scorpion | Al Kharid | `scorpion` | 6 (all post-2013/wilderness) | All corpus items are post-2013, wilderness-only, or not in rev 233 cache. | No corpus value — not worth revisit. | Al Kharid |
| Al Kharid Warrior | Al Kharid | `alkharid_warrior` | Unknown | No combat registration (thieving only NPC). | Add onNpcHit registration. | Al Kharid |
| Bear | Lumbridge/Draynor | `brownbear`, `darkbear`, `brownbear_cub_1`, `brownbear_cub_2` | 0 | Corpus has 0 drops. Existing table is final. | No corpus value — existing table is correct. | Phase 2 |
| Spider | Lumbridge/Draynor | `spider`, `giantspider1-2`, etc. | Conditional only | Only conditional/clue/loot bag drops. | No corpus value for generic drops. | Phase 2 |
| Ghost | Lumbridge/Draynor | `ghost`, `ghost2-8` | Wilderness only | Only wilderness-only drops (looting bag) + clues. | Wilderness zone handling. | Phase 2 |
| Monk | Edgeville | N/A | Unknown | No combat registration. | Add onNpcHit registration. | Edgeville |
| Hobgoblin (Rimmington variants) | Rimmington | `rimmington_hobgoblin_unarmed_1-3`, `rimmington_hobgoblin_armed_1` | 135 (now promoted) | Were region-specific variants with no combat registration in AggressiveNpcCombat. | ✅ Resolved — added to AggressiveNpcCombat + created HobgoblinDropTables.kt. | Port Sarim |
| Pirate (Port Sarim) | Port Sarim | `pirate1`, `pirate2`, `pirate_aggressive` | 39 | No combat registration. Spawns are in Karamja/Brimhaven area, not Port Sarim in this build. | Add onNpcHit registration + create table. Verify spawns are in Port Sarim area. | Port Sarim |
| White Knight | Falador | `white_knight`, `white_knight_yellow_plumes`, `white_knight_green_plumes`, `white_knight_blue_plumes` | 228 | No combat registration (no onNpcHit handler). Drops would never fire. | Add onNpcHit registration in AggressiveNpcCombat or dedicated combat script. Then create WhiteKnightDropTables.kt from corpus (228 items). | Falador |
| Falador dwarf variants | Falador | `fai_falador_dwarf_normal1`, `fai_falador_dwarf_normal2`, `fai_falador_dwarf_normal3` | 0 (part of Dwarf corpus) | Non-combat city NPCs. No spawns in current Falador TOML. | Verify if cache gives them combat levels. If yes, add to DwarfDropTables registration. | Falador |
| Falador citizens | Falador | `falador_gardener`, `falador_gardener2`, `falador_workman_young`, `falador_workman_fat`, `falador_banker` | 0 | Non-combat NPCs (shopkeepers, bankers, gardeners). | None expected — these should never be attackable. | Falador |
| Doric area NPCs | Falador | `falador_doric_area_man1-4`, `falador_doric_area_guard` | 0 | Non-combat zone NPCs. | Verify if they have cache combat levels. Doric area guard could be attackable if cache levels exist. | Falador |

## Zone: [Name]

| Category | Status | Notes |
|----------|--------|-------|
| **Cache/symbol truth** | 🔴 | |
| **Placed loc extraction** | 🔴 | |
| **Doors/stairs/ladders** | 🔴 | |
| **Bank/shop interactions** | 🔴 | |
| **Drop generation pipeline** | 🔴 | |
| **Drop tables (all NPCs)** | 🔴 | |
| **NPC spawns** | 🔴 | |
| **Dialogue** | 🔴 | |
| **Skill actions** | 🔴 | |
| **Compile checks** | 🔴 | |
```
