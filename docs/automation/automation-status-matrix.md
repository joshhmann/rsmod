# Automation Status Matrix

## Confidence Levels

| Level | Definition |
|-------|-----------|
| 0 | Exploratory only |
| 1 | Staged output works |
| 2 | Production promotion works for one controlled slice |
| 3 | Repeated across one full zone |
| 4 | Repeated across second zone without special-case rewrites |
| 5 | Safe for batch expansion within known content category |

## Current Status

| Content Category | Confidence | Proven Commits | Notes |
|-----------------|:----------:|---------------|-------|
| **Drop tables** | **Level 5** | `8420e7dc`, `3ff81785`, `fb5d6beb`, `f1e0466a` | Proven across 5 phases, 14+ NPC families. Draynor validated (no workflow changes). Ready for batch regional expansion |
| **Shops** | Level 1 | — | Generator skeleton works. Blocked by missing corpus stock data |
| **Spells** | Level 1 | — | Corpus too sparse (5 fields). Cache has richer data. Not useful until behavior APIs targeted |
| **NPC combat defs** | Level 1 | — | Core stats cache-defined. Corpus lacks anim/sound IDs. Low value |
| **Loc interactions** | Manual/generic | — | Generic systems (doors, ladders, banks) exist. Manual binding per zone |
| **Full zone automation** | Level 3 | — | Lumbridge nearly proven. Needs one more zone (Draynor) to reach Level 4 |

## Drop Table Automation — Detailed Status

### Proven NPC Families (Promoted)

| Phase | NPCs | Commit | Items Added |
|:-----:|------|:------:|:-----------:|
| 1.5 | Man, Woman | `8420e7dc` | 28 per table (beads removed) |
| 2 | Skeleton, Zombie, Bat | `3ff81785` | 76, 64, 1 |
| 3 | Guard, Mugger, Barbarian, Dwarf | `fb5d6beb` | 35, 25, 30, 25 |
| 4 | Imp, Dark Wizard, Rat | `f1e0466a` | 25, 30, 1 (rat's tail) |

### Skipped NPCs (No Corpus Value)

| NPC | Phase | Reason |
|-----|:-----:|--------|
| Bear | 2 | Corpus has 0 drops. Existing table is final |
| Spider | 2 | Corpus has only conditional/clue/loot bag drops |
| Ghost | 2 | Corpus has only wilderness-only drops |
| Scorpion | 4 | All 6 corpus items: post-2013, wilderness, or unresolved |

### NPCs with Existing Handlers Awaiting Review

These have production handlers but haven't been enriched:

| NPC | Handler Type | Corpus Viability | Priority |
|-----|:-----------:|:----------------:|:--------:|
| Giant Rat | Inline | Low (bones + raw rat meat only) | Low |
| Hill Giant | External file | Medium | Medium |
| Moss Giant | External file | Medium | Medium |
| Lesser Demon | External file | Medium | Medium |
| Black Knight | External file | Medium | Medium |
| Warrior | External file | Medium | Low |
| Unicorn | External file | Low (no drops) | Low |
| Goblin | Inline | Already well-populated | Low |
| Cow | Inline | Already complete | Low |
| Chicken | Inline | Already complete | Low |

## Generator Pipeline Status

| Generator | Status | Confidence | Blockers |
|-----------|--------|:----------:|----------|
| **G1 — Zone Module** | Not started | Level 0 | No autonomous zone module generation tested |
| **G2 — Drop Tables** | **Production** | **Level 4** | None (proven) |
| **G3 — Dialogue** | Not started | Level 0 | Needs POC |
| **G4 — Shops** | Staged POC | Level 1 | No stock data in corpus |
| **G5 — Quest State Machine** | Not started | Level 0 | Needs POC |
| **G6 — NPC Combat** | POC complete | Level 1 | Low value, cache-defined stats |
| **G7 — Pattern Scanner** | **Production** | Level 5 | Complete and live |

## Safety Policy

| Rule | Enforced | Found Violations |
|------|:--------:|:----------------:|
| No raw cache IDs | `git add` + scan | 0 (all phases) |
| Post-2013 filter | Phase-specific | 30+ items filtered |
| Wilderness-only filter | Phase-specific | Looting bags, keys |
| Clue scroll filter | Phase-specific | 15+ items filtered |
| Quest-only filter | Phase-specific | Ensouled heads, champion scrolls |
| Compile before commit | `./gradlew :server:app:compileKotlin` | 0 failures after fix |
| No behavior changes | Manual review | 0 violations |
| No duplicate handlers | Manual review | 0 violations |
