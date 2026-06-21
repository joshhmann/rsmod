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

## Zone: Draynor (Second Validation Zone — Target)

| Category | Status | Notes |
|----------|--------|-------|
| **Cache/symbol truth** | ✅ Inherited from Lumbridge | Global cache |
| **Placed loc extraction** | ✅ Inherited | Full export available |
| **Doors/stairs/ladders** | 🔴 Not started | — |
| **Bank/shop interactions** | 🔴 Not started | — |
| **Drop generation pipeline** | 🏆 Certified | Uses same G2 generator |
| **Drop tables (all NPCs)** | 🔴 Not started | — |
| **NPC spawns** | 🔴 Not started | — |
| **Dialogue** | 🔴 Not started | — |
| **Skill actions** | 🔴 Not started | — |

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
