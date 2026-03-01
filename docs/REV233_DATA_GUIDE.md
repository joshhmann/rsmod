# RSMod v2 Revision 233 Data Guide

**Target Revision:** OSRS Revision 233 (late 2023)  
**Data Source:** OSRS Wiki (live data, filtered for rev 233 compatibility)

## Overview

RSMod v2 targets OSRS revision 233 specifically. This guide helps ensure all content matches this revision.

## Revision 233 Context

- **Approximate Date:** Late 2023
- **Major Content:**
  - Wilderness Boss Rework was announced but not yet released (released 2024)
  - Varlamore (new continent) not yet added
  - Skill Cape Perks rework not yet done
  - Amoxliatl, Hueycoatl not yet added
  - Royal Titans not yet added

## Data Sources Comparison

| Source | Revision | Use For RSMod? | Notes |
|--------|----------|----------------|-------|
| Kronos-184-Fixed | 184 | Reference only | Too old, many stats changed |
| OSRS Wiki (live) | Current | Primary source | Filter for rev 233 content |
| Alter | 228 (donor) | API patterns | Good for code structure; IDs must still come from rev 233 symbols |
| RSMod BaseNpcs.kt | 233 | Current base | Symbol references |

## Rev 233 Specific Considerations

### Wilderness Bosses

**CRITICAL:** Wilderness bosses were reworked in 2024. For rev 233, use **pre-rework** versions:

| Boss | Rev 233 Status | Notes |
|------|----------------|-------|
| Vet'ion | Pre-rework | Single form, different mechanics |
| Venenatis | Pre-rework | Original mechanics |
| Callisto | Pre-rework | Original mechanics |
| Scorpia | Pre-rework | Original mechanics |
| Chaos Fanatic | Unchanged | Safe to use current data |
| Crazy Archaeologist | Unchanged | Safe to use current data |

### Items Added After Rev 233

**DO NOT include these items in drop tables:**

- Wilderness Rings (Bellator, Magus, Ultor, Venator)
- Wilderness Emblems tier changes
- Varlamore items
- Scurrius drops
- Post-rev 233 clue scroll rewards

### Monsters Added After Rev 233

**DO NOT implement:**

- Scurrius (Rat boss - added 2024)
- Amoxliatl (added 2024)
- Hueycoatl (added 2024)
- The Royal Titans (added 2025)
- Artio/Calvar'ion/Spindel (wilderness rework)

### Items That Changed

| Item | Rev 233 | Current | Action |
|------|---------|---------|--------|
| Dragon pickaxe (or) | Does not exist | Exists | Omit |
| Dragon harpoon (or) | Does not exist | Exists | Omit |
| Trailblazer relics | Do not exist | Exist | Omit |

## Data Collection Workflow

### Step 1: Identify Rev 233 Content

Use the validator to check compatibility:

```bash
python OSRSWikiScraper/rev233_validator.py --check-monster "Goblin"
```

### Step 2: Scrape from Wiki

```bash
cd OSRSWikiScraper
python scraper_v2.py -n "Goblin" -o ../wiki-data/monsters/goblin.json
```

### Step 3: Validate Against Kronos (Optional)

Compare with rev 184 data to identify changes:

```bash
python OSRSWikiScraper/rev233_validator.py \
  --compare-kronos Goblin \
  --kronos-dir Kronos-184-Fixed/Kronos-master/kronos-server/data
```

### Step 4: Generate RSMod Code

```bash
python scripts/generate_droptables.py \
  --monster goblin \
  --wiki-dir wiki-data/monsters \
  -o goblin_drops.kt
```

### Step 5: Manual Review

Check for:
- [ ] Post-rev 233 drops removed
- [ ] Drop rates match rev 233 (not current OSRS)
- [ ] Item names exist in rev 233 cache

## Validated Rev 233 Data

### F2P Monsters (17) - CONFIRMED SAFE

All F2P monsters are safe for rev 233:

| Monster | Status | Notes |
|---------|--------|-------|
| Goblin | ✅ Safe | No changes |
| Cow | ✅ Safe | No changes |
| Chicken | ✅ Safe | No changes |
| Giant rat | ✅ Safe | No changes |
| Guard | ✅ Safe | No changes |
| Man/Woman | ✅ Safe | No changes |
| Al Kharid warrior | ✅ Safe | No changes |
| Hill Giant | ✅ Safe | No changes |
| Moss Giant | ✅ Safe | No changes |
| Lesser demon | ✅ Safe | No changes |
| Greater demon | ✅ Safe | No changes |
| Black Knight | ✅ Safe | No changes |
| Dark wizard | ✅ Safe | No changes |
| Skeleton | ✅ Safe | No changes |
| Zombie | ✅ Safe | No changes |
| Giant spider | ✅ Safe | No changes |

### Dragons - SAFE

| Dragon | Status | Notes |
|--------|--------|-------|
| Green dragon | ✅ Safe |
| Blue dragon | ✅ Safe |
| Red dragon | ✅ Safe |
| Black dragon | ✅ Safe |
| King Black Dragon | ✅ Safe |

### Slayer Monsters - MOSTLY SAFE

| Monster | Status | Notes |
|---------|--------|-------|
| Abyssal demon | ✅ Safe |
| Dust devil | ✅ Safe |
| Gargoyle | ✅ Safe |
| Nechryael | ✅ Safe |
| Bloodveld | ✅ Safe |
| Hellhound | ✅ Safe |
| Dagannoth | ✅ Safe |
| Cave horror | ✅ Safe |
| Banshee | ✅ Safe |
| Crawling Hand | ✅ Safe |

### Giants - SAFE

| Giant | Status |
|-------|--------|
| Fire giant | ✅ Safe |
| Ice giant | ✅ Safe |
| Hobgoblin | ✅ Safe |

## Items to Filter from Drop Tables

When implementing drop tables, remove or verify these items:

### Ornament Kits (Post-rev 233)
- Dragon pickaxe (or)
- Dragon harpoon (or)  
- Dragon axe (or)

### Wilderness Rework Items (2024)
- Bellator ring
- Magus ring
- Ultor ring
- Venator ring
- Feral warrior helmet
- Voidwaker variants
- Webweaver bow
- Ursine chainmace
- Accursed sceptre

### Varlamore Items (2024+)
- Any "Sunlight" weapons
- Any "Moonlight" weapons
- Glacial tempest items

## Implementation Checklist

When porting content to RSMod v2:

- [ ] Verify monster exists in rev 233
- [ ] Check for post-rev 233 drops
- [ ] Verify drop rates match rev 233
- [ ] Check cache symbols exist (BaseNpcs.kt, BaseObjs.kt)
- [ ] Test in-game with rev 233 client
- [ ] Document any rev 233-specific behaviors

## Tools

### 1. Rev 233 Validator

```bash
# Check single monster
python OSRSWikiScraper/rev233_validator.py --check-monster Goblin

# Generate full report
python OSRSWikiScraper/rev233_validator.py --generate-report -o REPORT.md

# Compare with Kronos rev 184
python OSRSWikiScraper/rev233_validator.py --compare-kronos Goblin --kronos-dir ...
```

### 2. Wiki Scraper

```bash
# Scrape monster (includes rev 233 filtering)
cd OSRSWikiScraper
python scraper_v2.py -n Goblin
```

### 3. Drop Table Generator

```bash
# Generate RSMod code
python scripts/generate_droptables.py --monster goblin
```

## Common Issues

### Issue: "Item not found in cache"
**Cause:** Item added after rev 233  
**Fix:** Remove item from drop table

### Issue: "Drop rate seems wrong"
**Cause:** Wiki shows current OSRS rates, may differ from rev 233  
**Fix:** Cross-reference with Kronos rev 184 data

### Issue: "Monster mechanics different"
**Cause:** Monster reworked after rev 233  
**Fix:** Use Kronos/Alter as reference for mechanics

## Resources

- **OSRS Wiki:** https://oldschool.runescape.wiki/
- **Rev 233 Cache:** Check `.data/` folder in rsmod
- **Symbol Files:** `seq.sym`, `obj.sym`, `npc.sym` in `.data/symbols/`
- **Kronos Reference:** `Kronos-184-Fixed/` (rev 184, use with caution)
- **Alter Reference:** `Alter/` (rev 228 donor, good for patterns only)

## Updates

This guide should be updated when:
- RSMod updates target revision
- New content is verified safe for rev 233
- Issues are discovered with current data

Last updated: 2026-02-20

