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

## Zone: Varrock West (Alternative Second Validation Zone)

| Category | Status | Notes |
|----------|--------|-------|
| All categories | 🔴 Not started | — |

## Zone Status Template

Use this template for new zones:

```
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
