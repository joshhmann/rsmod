# Hermes RSMod Content Automation Workflow

## Purpose

Standardized, repeatable process for Hermes agents to automate RSMod content generation from corpus data — without blindly mass-generating content.

## Core Principle

**Review before promote. Staged before production. Blocked before wrong.**

Every automation pass follows this sequence:

```
Select → Inspect → Generate → Compare → Classify → Filter → Promote → Validate → Commit → Track
```

## Step-by-Step Workflow

### 1. Select Target Slice

Choose a small, reversible unit of work:

| Category | Slice Size | Example |
|----------|-----------|---------|
| **Drops** | 1-6 NPCs | Skeleton, Zombie, Bat |
| **Shops** | 1-3 shops | Lumbridge General Store |
| **NPC spawns** | 1 zone | Lumbridge surface |
| **Loc interactions** | 1 system | Doors, ladders |
| **Skills** | 1 action | Cooking range |

**Rules:**
- Keep scope small and reversible
- One commit per slice
- If the slice needs special-case code, it's too big

### 2. Inspect Existing Implementation

Before generating anything, understand what's already in production:

```bash
# Find handler files
find content/other/npc-drops -name "*.kt" | sort

# Read existing handler
cat content/other/npc-drops/src/main/kotlin/.../tables/XxxDropTables.kt

# Check existing registration
grep -n "registerXxx" content/other/npc-drops/src/main/kotlin/.../NpcDropTablesScript.kt

# Check NPC symbols used
grep "val xxx" content/other/npc-drops/src/main/kotlin/.../DropTableNpcs.kt

# Check NPC symbols in cache
grep "xxx" .data/symbols/npc.sym
```

**Checklist:**
- [ ] Existing handler file (or inline function)
- [ ] Production NPC symbols registered
- [ ] Existing always-drops
- [ ] Existing random tables
- [ ] Existing item references (objs.* vs DropTableObjs.* vs local ObjReferences)
- [ ] Module build.gradle.kts dependencies
- [ ] Any existing TODO markers or known issues

### 3. Generate Staged Output Only

Use the shared generator tools — never overwrite production directly:

```bash
# Query corpus data
python3 -c "
import sys; sys.path.insert(0, 'tools/corpus-generators')
from resolver import CorpusResolver
from corpus import CorpusData
db = CorpusData()
resolver = CorpusResolver()
drops = db.drops_by_source.get('TargetName', [])
for d in drops:
    name = d.get('item', '?')
    rarity = d.get('rarity', '?')
    resolved = resolver.resolve_item(name)
    sym = resolved.sym_name if resolved else 'UNRESOLVED'
    print(f'{name:30s} {rarity:15s} -> {sym}')
"

# G2 generator for drops (if creating new tables)
python3 tools/corpus-generators/g2_corpus_drops.py \
    --source corpus --zone <zone> \
    --npcs <npc1,npc2> --staging
```

**Output locations:**
- Drop tables: `content/other/npc-drops/src/main/kotlin/.../tables/` (created by hand, staged in git)
- Reports: `staging/<category>/` or `docs/automation/reports/`

### 4. Compare Staged vs Existing

For every target, produce a comparison table:

| Metric | Existing | Generated | Δ |
|--------|----------|-----------|---|
| Item count | N | N | ±N |
| Always drops | list | list | diff |
| Coin entries | N | N | ±N |
| Weapon/Armour items | N | N | ±N |
| Rune items | N | N | ±N |
| Herb items | N | N | ±N |
| Gem/RDT items | N | N | ±N |
| Skipped items | N/A | N | reasons |

### 5. Classify Every Target

After comparison, assign one classification:

| Classification | Meaning | Action |
|---------------|---------|--------|
| **PROMOTE** | Corpus clearly improves over existing | Enrich production handler |
| **KEEP_EXISTING** | Corpus is equal or worse than current | Leave unchanged |
| **STAGE_ONLY** | Corpus adds value but needs review | Create file in staging/ only |
| **SKIP** | Corpus has no meaningful valid drops | No action |
| **NEEDS_HUMAN_REVIEW** | Uncertain classification | Block and notify |

### 6. Apply Safety Filters

Before promoting, check every item:

```python
# Filter rules (applied in order)
def filter_item(name, sym_name, rarity, context):
    if sym_name is None:
        return ('SKIP', 'not in rev 233 cache')
    if is_clue_scroll(name):
        return ('SKIP', 'post-2013 / not explicitly supported')
    if is_key(name):
        return ('SKIP', 'post-2013')
    if is_looting_bag(name):
        return ('SKIP', 'wilderness-only')
    if is_ensouled_head(name):
        return ('SKIP', 'post-2015 Arceuus')
    if is_wilderness_only(name):
        return ('SKIP', 'wilderness-only NPC or item')
    if is_quest_only(name):
        return ('SKIP', 'quest-only')
    if is_slayer_only(name):
        return ('SKIP', 'slayer-only')
    if is_champion_scroll(name):
        return ('SKIP', 'not in rev 233 cache')
    if is_rarity_decimal(name):
        return ('STAGE', 'verify rarity format')
    return ('PROMOTE', 'valid')
```

**Hard rules (never violate):**
- No raw cache IDs in Kotlin — use `.sym` references only
- No post-2013 content unless verified present in rev 233 cache
- No wilderness-only drops outside wilderness zones
- No quest-only drops in generic tables
- No slayer-only drops unless target is a slayer monster
- No clue scrolls unless explicitly supported by the handler
- No named/quest/regional NPC variants unless verified
- No combat/aggression behavior changes unless explicitly in scope
- No duplicate handlers — if one exists, enrich it

### 7. Promote Only High-Confidence Changes

When promoting:

```bash
# Write to correct module path
scp <file> ct175:<rsmod>/content/other/npc-drops/src/main/kotlin/.../tables/

# Update registration (add to NpcDropTablesScript.kt startup)
# Remove inline function if refactoring
```

**Promotion rules:**
- Use correct module path (`content/other/npc-drops/` for drops)
- Avoid duplicate handlers (refactor inline → external file)
- Preserve existing production drops where they are better
- Promote in small commits (one per slice)

### 8. Validate

Run all checks:

```bash
# 1. Resolver smoke test
python3 -c "
import sys; sys.path.insert(0, 'tools/corpus-generators')
from resolver import CorpusResolver
r = CorpusResolver()
for item in ['Bones', 'Coins', 'Iron dagger', 'Chaos rune', 'Steel arrow']:
    res = r.resolve_item(item)
    assert res.sym_name, f'{item} failed'
print('Resolver OK')
"

# 2. Module compile
./gradlew :content:other:npc-drops:compileKotlin

# 3. Full server compile
./gradlew :server:app:compileKotlin

# 4. Raw ID scan
python3 -c "
import os, re
for root, dirs, files in os.walk('content/other/npc-drops/src/main/kotlin/'):
    for f in files:
        if f.endswith('.kt'):
            text = open(os.path.join(root, f)).read()
            ids = re.findall(r'\\b([2-9][0-9]{3,}|1[0-9]{4,})\\b', text)
            ids = [x for x in ids if int(x) > 100 and int(x) < 100000]
            if ids:
                print(f'Numbers in {f}: {ids}')
"

# 5. Git diff check
git diff --stat
git status --short
```

### 9. Commit

```bash
git add <files>
git commit -m "feat(drops): <Category> — <concise summary>"
```

Commit message format:
```
feat(<scope>): <Category> — <summary>

<detailed bullet points of changes>

<skipped items and reasons>

<verification results>
```

### 10. Update Tracking Documents

After each commit:
- Update `docs/automation/automation-status-matrix.md`
- Update `docs/automation/zone-readiness-checklist.md`
- Note skipped items and reasons in generator notes
- Update confidence level in `docs/automation/drop-generator-certification.md`

## Confidence Levels

| Level | Definition | Gate |
|-------|-----------|------|
| 0 | Exploratory only | — |
| 1 | Staged output works | Manual review |
| 2 | Production promotion works for one slice | Manual review |
| 3 | Repeated across one full zone | Single zone pass |
| 4 | Repeated across second zone without special-casing | Two zone passes |
| 5 | Safe for batch expansion within known category | Level 4 + review |

## When to Mass-Generate

**Do NOT mass-generate unless confidence is Level 4+ for that content category.**

Until then: batch-by-batch with staged comparison and human review.
