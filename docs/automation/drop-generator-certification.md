# Drop Generator Certification

**Status:** CERTIFIED — Level 5 (Batch Regional Expansion)

**Date:** 2026-06-20
**Upgraded:** 2026-06-20 (Draynor validation passed — Level 5)

**Certified by:** Mai (Hyraxknot Division Operations)

## Proven Commits

| Phase | Commit | Description |
|:-----:|:------:|-------------|
| Tooling | `499eae9` | Shared resolver + G2 corpus drop generator committed |
| 1.5 | `8420e7dc` | Man/Woman drops promoted |
| Draynor | `51e66957` | Wizard, Jail Guard promoted — Draynor zone validated | (standard city variants fix) |
| 2 | `3ff81785` | Skeleton, Zombie, Bat promoted |
| 3 | `fb5d6beb` | Guard, Mugger, Barbarian, Dwarf promoted |
| 4 | `f1e0466a` | Imp, Dark Wizard, Rat promoted. Scorpion skipped |

## Successful Target Families

16+ NPC families processed across 5 phases + 3 regions:

| Family | Phase | Status |
|--------|:-----:|:------:|
| Man, Woman | 1.5 | ✅ Promoted |
| Skeleton | 2 | ✅ Promoted |
| Zombie | 2 | ✅ Promoted |
| Bat | 2 | ✅ Promoted |
| Guard | 3 | ✅ Promoted |
| Mugger | 3 | ✅ Promoted |
| Barbarian | 3 | ✅ Promoted |
| Dwarf | 3 | ✅ Promoted |
| Imp | 4 | ✅ Promoted |
| Dark Wizard | 4 | ✅ Promoted |
| Rat | 4 | ✅ Promoted |
| Wizard | Draynor | ✅ Promoted |
| Jail Guard | Draynor | ✅ Promoted |
| Thief | Varrock | ✅ Promoted |
| Chaos Druid | Varrock | ✅ Promoted |
| Scorpion | 4 | ✅ Skipped (no corpus value) |

## Skip Rules

The following item categories are ALWAYS skipped from automatic promotion:

1. **Not in rev 233 cache** — Item sym_name is None after resolver resolution
2. **Clue scrolls** — All tiers (beginner, easy, medium, hard, elite, master)
3. **Keys** — Medium key, Larran's key, Ecumenical key, loop/tooth halves
4. **Looting bags** — Wilderness-only
5. **Ensouled heads** — Post-2015 Arceuus content
6. **Champion scrolls** — Not usable without Champion's Challenge implementation
7. **Slayer enchantments** — Post-2016 wilderness content
8. **Shield left half** — Post-2013 RDT expansion
9. **Bear fur, Flyer, Potion (Apothecary)** — Not in rev 233 cache
10. **Generic "Staff", "Wizard hat"** — Not in rev 233 cache (named variants only)
11. **Iron bolts, Bronze bolts** — Not in rev 233 cache (`bolts` exists but not `bronze_bolts`)
12. **Grimy marrentill** — Not in rev 233 cache
13. **Fiendish ashes** — Post-rev-233 mechanic (DT2 update, July 2023)

## Raw ID Policy

**ABSOLUTE: No raw cache IDs in Kotlin source files.**

All item references must use `.sym` symbols through:
- `objs.*` from `BaseObjs` (for common items: bones, coins, runes, basic tools)
- `DropTableObjs.*` (for module-specific items)
- Local `XxxObjs` with `ObjReferences()` (for items unique to a table)

Enforcement: Python regex scan for 5-digit numbers in `.kt` files before commit.

## Compile Policy

**Both checks are mandatory before every commit:**
```bash
./gradlew :content:other:npc-drops:compileKotlin    # Module compile
./gradlew :server:app:compileKotlin                  # Full server compile
```

## Batch Size Limits

| Context | Max NPCs/Batch | Notes |
|---------|:--------------:|-------|
| Simple (bones+coins only) | 6 | Rat, Bat, Spider |
| Medium (2-3 sub-tables) | 4 | Skeleton, Zombie |
| Full (5+ sub-tables) | 2 | Guard, Dwarf |
| Unknown/new category | 2 | First pass to establish pattern |

## Promotion Workflow

See `docs/automation/hermes-content-automation-workflow.md` for the full process.

Minimum steps:
1. Inspect existing handler
2. Query corpus data
3. Generate enriched table file (refactor inline → external file)
4. Compare existing vs corpus (item count, symbol resolution, skipped items)
5. Classify each target
6. Apply safety filters
7. Add registration call to NpcDropTablesScript.kt
8. Remove old inline function if refactoring
9. Module compile
10. Full server compile
11. Raw ID scan
12. Commit

## Known Unsafe Categories

These should NOT be automated through the drop generator:

| Category | Risk | Reason |
|----------|:----:|--------|
| Boss drops | High | Unique mechanics, phases, tertiary tables not representable in corpus JSON |
| Slayer monsters | Medium | Slayer-only drops mixed with generic; hand review needed |
| Wilderness NPCs | Medium | Looting bags, keys, and wilderness-only drops need filtering |
| Quest NPCs | High | Quest-only drops, unique dialogue, conditional behavior |
| Named/regional NPC variants | Medium | Corpus may conflate generic and named variants |
| Clue scroll handling | High | Requires clue scroll system implementation |

## Rat Tail Verification

**Status:** ✅ ACCEPTABLE as generic drop.
Rat's tail (obj.sym ID 300) is in the rev 233 cache and is a standard OSRS
always-drop from rats alongside bones. Used in Witch's Potion quest but not
quest-locked — rats always drop it regardless of quest status. No issue found.

## Next Steps — Level 5 Achieved ✅

Drop automation is now **Level 5 certified** for controlled batch regional expansion.

- ✅ **Authorized:** Hermes can batch-expand drops by region
- ✅ **Proven workflow:** Lumbridge → Draynor, no changes needed
- ✅ **Next category:** Shops (unblock stock data), NPC spawns (G1), or dialogue (G3)
