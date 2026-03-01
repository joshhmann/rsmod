# Codebase Audit — 2026-02-26

Comprehensive audit of RSMod content implementations, documentation quality, and agent failure patterns.

---

## Critical Issues (Fix Immediately)

### 1. Runtime Crash: WoodsmanTutor.kt:27
```kotlin
player.baseWoodcuttingLvl >= 99 -> TODO("Mastery dialogue")
```
`TODO()` throws `NotImplementedError` at runtime. Any player with 99 Woodcutting clicking this NPC crashes the server thread.

**Fix:** Replace with `mes("You are already a master woodcutter.")` or a simple dialogue stub.

**File:** `rsmod/content/areas/city/lumbridge/src/main/kotlin/org/rsmod/content/areas/city/lumbridge/npcs/WoodsmanTutor.kt`

---

### 2. Ghost Module: `content/quests/romeo-and-juliet/`
Has `build.gradle.kts` and a compiled JAR but **zero source files**. The real quest is at `content/quests/romeo-juliet/`. Produces an empty JAR that gets loaded at runtime. Wastes a Gradle project slot and confuses agents.

**Fix:** Delete the entire `rsmod/content/quests/romeo-and-juliet/` directory.

---

### 3. Orphaned Files: 5 `.kt` Files Outside `src/`
Location: `rsmod/content/generic/generic-npcs/npcs/`

Files:
- `BarbarianDialogue.kt`
- `DwarfDialogue.kt`
- `MuggerDialogue.kt`
- `RovingMossgiantDialogue.kt`
- `ZombieDialogue.kt`

These files:
- Are **never compiled** (outside `src/main/kotlin`)
- Import a non-existent `GenericNpcs` class
- Contain hardcoded `"TODO: Add NPC backstory here."` strings
- Were clearly LLM-generated and dropped in the wrong directory

**Fix:** Delete all 5 files.

---

### 4. Deprecated Dead Code: F2pDropTables.kt
`rsmod/content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcrops/scripts/F2pDropTables.kt`

Entire class is `@Deprecated` with empty `startup()`. All private methods are dead code.

Additionally, `NpcDropTablesScript.kt` has ~200 lines of dead private `registerXxx()` methods that were superseded by external table files but never removed.

**Fix:** Remove deprecated class. Remove dead private methods from NpcDropTablesScript.kt.

---

## Structural Issues

### Empty Registered Modules (build.gradle.kts but no source)
| Module | Notes |
|--------|-------|
| `content/mechanics/death/` | Awaiting implementation |
| `content/mechanics/stat-restore/` | Awaiting implementation |
| `content/generic/shops/` | Awaiting implementation |

### Unregistered Stubs (no build.gradle.kts)
| Directory | Notes |
|-----------|-------|
| `content/skills/hunter/` | Empty, needs scaffold |
| `content/skills/magic/teleports/` | Empty, needs scaffold |
| `content/areas/misc/champions-guild/` | Only `src/` dir |
| `content/areas/misc/crafting-guild/` | Only `src/` dir |
| `content/areas/misc/ice-mountain/` | Only `src/` dir |

### Quest Stubs (registered in QuestList, empty startup)
| Quest | Status |
|-------|--------|
| `knights-sword` | 22 lines, empty |
| `shield-of-arrav` | 23 lines, empty |
| `misthalin-mystery` | 18 lines, empty |
| `below-ice-mountain` | 18 lines, empty |
| `x-marks-the-spot` | 17 lines, empty |
| `the-corsair-curse` | 18 lines, empty |

### Package Naming Inconsistency
`npc-drops` module has files in both `npcdrops` and `npcrops` packages. Not a compile error but confusing.

---

## Anti-Pattern Scan Results

### Checked and CLEAN (no violations found)
- `bindScript<T>()` — not found
- `private object X : *References()` — not found (all `internal`)
- `player.chatPlayer` / `player.chatNpc` — not found
- `CoordGrid.plane` / `CoordGrid.y` — not found
- `BoundLocInfo.type` — not found
- `addXP` / `addXp` — not found
- `player.message(` — not found
- Hardcoded interface IDs > 924 — not found
- Duplicate NpcEditor entries — not found

### `player.mes()` Usage (valid but confusing)
`player.mes()` is used in `content/other/commands/` (AdminCommands.kt, RealmConfigCommands.kt) and `content/generic/guilds/GuildDefinitions.kt`. In these contexts it's valid because `player` has a `mes()` extension from the engine. However, agents see this pattern and incorrectly copy it into `ProtectedAccess` contexts.

### High TODO Counts
| File | TODO Count | Category |
|------|-----------|----------|
| `NpcDropTablesScript.kt` | 35 | Drop rate validation |
| `Cooking.kt` | 17 | Burn levels, gauntlet |
| `Firemaking.kt` | 14 | Fire lifespan ticks |
| `PoisonScript.kt` | 8 | Missing spotanim/synth refs |
| `PrinceAliRescue.kt` | 7 | Quest logic not implemented |
| `Herblore.kt` | 6 | Missing ingredients |
| `DwarfMulticannonScript.kt` | 13 | All logic unimplemented |

---

## Documentation Gap Analysis

### Why Agents Keep Failing (Root Causes)

#### 1. `spotlessApply` missing from README and CLAUDE.md
AGENTS.md step 8a requires it. README and CLAUDE.md — the two docs agents read first — don't mention it. Result: agents skip formatting, builds fail.

**Fixed in this session:** Added to both CLAUDE.md and README.md.

#### 2. Boot success signal inconsistency
- README: `Bound to ports: 43594`
- START_HERE: `[MainGameProcess] World is live`
- AGENTS.md DoD: `World is live`

`Bound to ports` fires before `World is live`. Agents following README declare success prematurely.

**Fixed in this session:** Unified to `[MainGameProcess] World is live` in README.

#### 3. No `onButton` pattern documented
Prayer tab, bank, shop interfaces all use `onButton(interfaces.x, component = y)`. Not in TRANSLATION_CHEATSHEET. Agents reverse-engineer it from code.

**Fixed in this session:** Added Section 21 to TRANSLATION_CHEATSHEET.

#### 4. `invAddOrDrop` injection path never explained
Docs show `invAddOrDrop(objRepo, objs.logs)` but never explain that `objRepo` requires `ObjTypeList` constructor injection.

**Fixed in this session:** Added injection example to TRANSLATION_CHEATSHEET Section 7.

#### 5. Duplicate event registration under-documented
The most dangerous bug (silently disables entire scripts) is buried in Agent Learning #6. Not in TRANSLATION_CHEATSHEET where agents look when writing handlers.

**Fixed in this session:** Added warning box to TRANSLATION_CHEATSHEET Section 2.

#### 6. `player.mes()` pattern confusion
Engine commands use `player.mes()` (valid in Player extension context). Agents copy this into ProtectedAccess handlers where only `mes()` works.

**Documented:** Added clarification to TRANSLATION_CHEATSHEET Section 6.

#### 7. CLAUDE.md too thin
Missing: agent_heartbeat requirement, api/ exception rule, WARN grep check.

**Fixed in this session:** Expanded CLAUDE.md significantly.

#### 8. MEMORY.md stale references
References `docs/F2P_PLAN.md` which was archived. States BaseVarps need adding when they're already present.

**Fixed in this session:** Updated MEMORY.md.

---

## Documentation Quality Scores

| Document | Score | Notes |
|----------|-------|-------|
| START_HERE.md | 9/10 | BUILD-CRIT learnings are outstanding |
| AGENTS.md | 9/10 | Best multi-agent coordination doc |
| OSRS_MECHANICS_REFERENCE.md | 9/10 | Missing ranged/magic formulas |
| CORE_SYSTEMS_GUIDE.md | 8.5/10 | FoodScript has stub TODO in template |
| SYM_NAMING_GUIDE.md | 8.5/10 | Missing npc.sym/loc.sym quirks |
| TRANSLATION_CHEATSHEET.md | 8/10 → 9/10 | **Improved this session** |
| README.md | 8/10 → 9/10 | **Improved this session** |
| CLAUDE.md | 6/10 → 8/10 | **Improved this session** |
| F2P_PLAN.md (archived) | 6/10 | Materially stale, area status wrong |

---

## Remaining Improvement Opportunities (P3)

1. Expand SYM_NAMING_GUIDE with `npc.sym` and `loc.sym` quirk tables
2. Add ranged/magic combat formulas to OSRS_MECHANICS_REFERENCE
3. Replace FoodScript stub TODO in CORE_SYSTEMS_GUIDE with actual timer check
4. List the exact 5 disabled scripts from BUILD-CRIT-15 in CONTENT_AUDIT
5. Add `docs/DOC_AUTHORITY.md` link to README's Role Routing section
6. Update AGENTS.md blocker table (several resolved blockers still listed)

