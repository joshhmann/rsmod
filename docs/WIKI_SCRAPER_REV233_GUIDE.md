# Using OSRS Wiki with Rev 233 Filtering

## The Problem

The OSRS Wiki shows **current/live OSRS data** (around rev 236-240 today).

But RSMod v2 uses **Rev 233** (July 2023).

**Some items/NPCs added after July 2023 won't exist in your cache!**

## The Solution

We've created a scraper that:
1. Gets data from OSRS Wiki
2. Checks the **"Released"** date
3. **Filters out** anything after July 2023
4. Maps names to your **cache IDs**

---

## How It Works

### Wiki Release Dates

Every wiki page has a "Released" field:

```
Goblin:           Released 4 January 2001 ✓ (Way before 233)
Bellator ring:    Released 26 July 2023 ✗ (After 233)
Magus ring:       Released 26 July 2023 ✗ (After 233)
DT2 bosses:       Released 26 July 2023 ✗ (After 233)
```

### The Filter

```python
REV_233_DATE = July 26, 2023

if item_release_date <= REV_233_DATE:
    include_in_scraper()  # ✓ Safe to use
else:
    skip()  # ✗ Not in your cache
```

---

## Using the Scraper

### Step 1: Scrape with Rev 233 Filter

```bash
cd OSRSWikiScraper
python rev233_wiki_scraper.py
```

### Step 2: What It Does

```
✓ Fetches Goblin data
✓ Release date: 4 January 2001
✓ Before July 2023 → INCLUDE
✓ Found cache ID: npcs.goblin = 3078

✗ Fetches Bellator ring data  
✗ Release date: 26 July 2023
✗ After July 2023 → EXCLUDE
✗ Skipping (not in rev 233)
```

### Step 3: Output

```kotlin
// Auto-generated for Goblin (Rev 233 compatible)
// Released: 4 January 2001
register(npcs.goblin) {
    drop(objs.bones, 1, rate = 1)
    drop(objs.bronze_sq_shield, 1, rate = 3)
    drop(objs.coins, 5, rate = 35)
    // ... all verified to exist in rev 233
}
```

---

## Important Dates

| Date | What Happened | Rev 233? |
|------|---------------|----------|
| Jan 2001 | OSRS launch | ✓ Yes |
| 2013-2023 | Regular updates | ✓ Yes |
| **July 26, 2023** | **DT2 release** | **✗ No** |
| Late 2023 | Varlamore teasers | ✗ No |
| 2024 | Forestry, etc | ✗ No |

**Rev 233 = Just BEFORE Desert Treasure II**

---

## What to Expect

### WILL Be Included (Pre-July 2023)
```
✓ All F2P content
✓ All skills and their training methods
✓ God Wars Dungeon
✓ Chambers of Xeric
✓ Theatre of Blood
✓ Tombs of Amascut (early version)
✓ Most bosses
✓ All classic quests
```

### Will NOT Be Included (Post-July 2023)
```
✗ Desert Treasure II (most of it)
✗ Bellator/Magus/Ultor/Venator rings
✗ New DT2 bosses
✗ Varlamore content
✗ Post-2023 items
```

---

## Verifying Results

### Manual Check

```python
# After scraping, verify with cache:
python scripts/cache_lookup.py obj "item_name"

# If found → ✓ Valid for rev 233
# If not found → ✗ Added after rev 233
```

### Cross-Reference with Alter (Rev 228)

```python
# Alter is a rev 228 donor reference (5 revs earlier)
# Use it for behavior patterns only
# Always resolve final IDs from rev 233 symbols/cache
```

---

## Example Workflow

### 1. Scrape Monster

```python
scraper = Rev233WikiScraper()
data = scraper.get_monster_data("Zulrah")

# Output:
# Zulrah: Released 8 January 2015
# ✓ Before July 2023 → Include
# ✓ Cache ID: npcs.zulrah = 2042
```

### 2. Generate Drop Table

```python
code = scraper.generate_rev233_drop_table("Zulrah")
# Only includes drops released before July 2023
# (Most Zulrah drops are old, so all included)
```

### 3. Verify Items

```python
# Check each drop:
for drop in data['drops']:
    cache_id = scraper.get_cache_id('obj', drop['name'])
    if cache_id:
        print(f"✓ {drop['name']} = {cache_id}")
    else:
        print(f"? {drop['name']} - VERIFY MANUALLY")
```

---

## Limitations

### 1. Wiki Data Quality
```
- Some pages missing release dates
- Some dates may be inaccurate
- Assumes "Released" = "Available in cache"
```

### 2. Cache Mapping
```
- Wiki names vs cache names may differ
- "Rune scimitar" vs "rune_scimitar"
- Need fuzzy matching
```

### 3. Edge Cases
```
- Items reworked (old version removed)
- Items renamed
- Holiday events (re-released items)
```

---

## Best Practices

### Always Verify

```python
# 1. Scrape from wiki
# 2. Check release date
# 3. Verify in cache
# 4. Test in-game
```

### Use Multiple Sources

```
1. Wiki scraper (release dates)
2. cache_lookup.py (ID verification)
3. Alter repo (rev 228 donor patterns only; verify against rev 233 symbols)
4. OSRS Wiki "Released" field (manual check)
```

---

## Quick Reference

### Safe to Use (Definitely Pre-233)
```
All F2P monsters
All F2P items
All classic skills
All God Wars content
All classic bosses (KBD, KQ, etc.)
CoX, ToB, early ToA
```

### Check First (Around 233 Release)
```
DT2 items (July 2023)
New rings
New bosses
New areas
```

### Skip (Post-233)
```
Anything released after July 26, 2023
Varlamore
Post-DT2 additions
2024 content
```

---

## Running the Tool

```bash
cd OSRSWikiScraper

# Scrape single monster with rev 233 filter
python -c "
from rev233_wiki_scraper import Rev233WikiScraper
scraper = Rev233WikiScraper()
data = scraper.get_monster_data('Goblin')
print(data)
"

# Generate drop table
python -c "
from rev233_wiki_scraper import Rev233WikiScraper
scraper = Rev233WikiScraper()
code = scraper.generate_rev233_drop_table('Cow')
print(code)
"
```

---

## Summary

The wiki scraper now **filters by release date** to ensure compatibility with Rev 233.

**Before:** Scrape everything → Some items don't work  
**After:** Scrape + filter → Only rev 233 compatible items

**Result:** Generated code that actually works in your server! 🎉

