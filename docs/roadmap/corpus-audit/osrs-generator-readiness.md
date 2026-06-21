# OSRS Corpus → Content Generator Readiness

**Audit date:** 2026-06-20
**Auditor:** Mai (ops)

## Verdict: ✅ SAFE for generator planning

The OSRS Research Corpus is **safe and recommended** for use in the content generator pipeline. No gameplay content has been imported. The corpus is a valuable reference dataset that accelerates roadmap execution.

## What Works Well

| Use Case | Readiness | Reasoning |
|----------|-----------|-----------|
| **Monster stats** (combat formulas) | ✅ High | 1,372 monsters with HP, attack, defence, weakness — directly usable for NPC spawning |
| **Drop tables** | ✅ High | 21 MB drop data, 2,061 drop sources — G2 generator can ingest directly |
| **Quest data** | ✅ High | 217 quests with requirements, rewards, dialogue — scaffolder can generate stubs |
| **Skill guides** | ✅ High | 23 training guides with XP rates, methods — perfect for bot training |
| **Item database** | ⚠️ Good | 16K items but needs name resolution bridge for non-matching items |
| **NPC data** | ⚠️ Good | 7K NPCs but .sym matching needs prefix-stripping logic |
| **Cache diffs** | 🔍 Reference | 593 revision change logs — useful for understanding feature timelines |
| **Ironman progression** | ✅ High | Phased guide directly usable for bot progression paths |

## Name Resolution Bridge Required

The primary integration cost is a **name resolution layer** between corpus names and `.sym` names. This is the same problem the existing G2 generator solves via `OBJ_NAME_OVERRIDES` (42 mappings already exist).

### Recommended Resolution Strategy

```
For Items:
1. Direct: space → underscore (bronze bar → bronze_bar)
2. Rune concat: air rune → airrune
3. Apostrophe: remove ' (ahrim's → ahrims)
4. Parentheses: remove (x) then try underscore
5. Number suffix: try without dose numbers
6. Override table: manually curated exceptions

For NPCs:
1. Direct: space → underscore
2. Prefix strip: remove known location/quest prefixes (wgs_, gt_, mm2_, etc.)
3. Override table: manual mapping for common NPCs
```

## Recommended First Generator POC

Given the corpus strengths, start with:

### Option A: Monster Drop Tables (Recommended)
- **Why:** Drop tables are the largest dataset (21 MB), most complete, and directly translate to Kotlin DSL
- **Corpus data:** `parsed-data/drop_tables.json` + `parsed-data/drops_by_source.json`
- **Output:** NPC handler Kotlin files with drop tables
- **Difficulty:** Low — 90% of work is name resolution (already partially solved in G2)

### Option B: Quest Scaffolding
- **Why:** 217 quests with full HTML guides and structured dependency data
- **Corpus data:** `parsed-data/quest_lookup.json` + `quests/*.html`
- **Output:** Quest stub Kotlin files with stages, dialogues, rewards
- **Difficulty:** Medium — needs HTML parsing + RP experience mapping

### Option C: NPC/Shop Spawning
- **Why:** Natural complement to existing Lumbridge slice work
- **Corpus data:** `parsed-data/npc_directory.json` + `parsed-data/shops.json` + `buckets/infobox_shop.json`
- **Output:** NPC spawn + shop handler Kotlin files
- **Difficulty:** Medium — needs location/category mapping

**Recommendation: Start with Option A (monster drop tables)** — it produces the most immediate value with the least integration friction, and the G2 generator already has the pattern working.

## Integration Cost Estimate

| Step | Effort | Description |
|------|--------|-------------|
| Copy corpus tarball | 5 min | `scp` from .17 to CT 175 (`rsmod/.data/osrs-corpus/`) |
| Name resolution module | 2-3 hrs | Python module to bridge corpus names to `.sym` names |
| Extend G2 generator | 1-2 hrs | Add corpus as a 4th source (Kronos + wiki static + live scrape + corpus) |
| Generate first drop tables | 30 min | Run generator against corpus drop data |
| Validate output | 1-2 hrs | Check generated Kotlin compiles and drops are correct |
| **Total** | **4-8 hrs** | |

## Risks / Gaps

1. **No validation scripts exist in the corpus** — the "7-phase check results" referenced in prior reports are not reproducible from the tarball. Separate tooling produced them.
2. **NPC name matching is the hardest problem** — `.sym` NPC names use inconsistent prefixing conventions. Expect 40-60% of NPC names to need a resolve-via-ID fallback.
3. **Historical vs cache IDs** — Some IDs (especially NPCs) in the corpus are `hist`-prefixed historical IDs, not current cache IDs. Cross-reference against `.sym` raw IDs is essential.
4. **20 MB compressed / 41 MB extracted** — Small enough to store in git-adjacent location. Recommend keeping outside the repo (gitignored). See task constraint #9.

## Files Created/Modified

None. No files were copied to the repo or modified during this audit. All analysis was done on .17's extraction at `/tmp/osrs-corpus-audit/`.
