# Bulk Scraping OSRS Wiki for Rev 233

**YES! We can scrape everything!** 🚀

But we need to be **smart** about it:
- Rate limiting (don't hammer wiki)
- Caching (don't re-scrape)
- Parallel processing (faster but respectful)
- Resume capability (if it crashes)

---

## The Tool

`OSRSWikiScraper/bulk_scraper.py` - Scrapes ALL monsters with:
- ✅ Rev 233 filtering (by release date)
- ✅ Automatic caching
- ✅ Parallel processing (3 workers)
- ✅ Resume capability
- ✅ Progress tracking
- ✅ Export to JSON + Kotlin

---

## How to Use

### 1. Run the Scraper

```bash
cd OSRSWikiScraper
python bulk_scraper.py
```

### 2. Choose What to Scrape

```
What to scrape? (f2p/members/all): f2p
Will scrape 38 monsters
Continue? (yes/no): yes
```

### 3. Watch It Work

```
============================================================
OSRS Wiki Bulk Scraper for Rev 233
============================================================

Scraping 38 monsters with 3 workers...
Rate limit: 1.0s between requests
Cache directory: wiki_cache
------------------------------------------------------------
[1/38] ✓ Goblin: 8 drops
[2/38] ✓ Giant_rat: 5 drops
[3/38] SKIP Bellator_warrior: Released 26 July 2023 (after rev 233)
[4/38] ✓ Cow: 4 drops
[5/38] ✓ Chicken: 6 drops
...
[38/38] ✓ Zombie: 12 drops

Stats: 36 scraped | 2 cached | 1 skipped (post-233) | 0 failed
------------------------------------------------------------

Exporting results...
Exported 36 monsters to wiki_data_rev233/monsters_rev233_f2p.json
Generated Kotlin code for 36 monsters: wiki_data_rev233/GeneratedDropTablesRev233_F2p.kt

DONE!
JSON: wiki_data_rev233/monsters_rev233_f2p.json
Kotlin: wiki_data_rev233/GeneratedDropTablesRev233_F2p.kt
```

---

## What Gets Scraped

### Predefined Lists Included

| List | Count | Examples |
|------|-------|----------|
| `F2P_MONSTERS` | 38 | Goblin, Cow, Chicken, Skeleton |
| `MEMBERS_MONSTERS` | 100+ | Zulrah, Vorkath, Slayer monsters |
| `all` | ~140 | Everything combined |

### You Can Add Your Own

```python
MY_MONSTERS = [
    "Specific_boss_I_want",
    "Rare_monster",
    "Quest_NPC",
]
```

---

## Output Files

### 1. Cache Files (`wiki_cache/`)
```
wiki_cache/
├── monster_Goblin.json
├── monster_Cow.json
├── monster_Zulrah.json
└── ... (one per monster)
```

Each contains raw wiki data:
```json
{
  "name": "Goblin",
  "wiki_url": "https://oldschool.runescape.wiki/w/Goblin",
  "release_date": "4 January 2001",
  "combat_level": 2,
  "hitpoints": 5,
  "drops": [
    {"name": "Bones", "quantity_min": 1, "quantity_max": 1, "rarity_str": "Always"},
    {"name": "Coins", "quantity_min": 5, "quantity_max": 5, "rarity_str": "1/8"}
  ]
}
```

### 2. JSON Export (`wiki_data_rev233/`)
```json
[
  {"name": "Goblin", "combat_level": 2, ...},
  {"name": "Cow", "combat_level": 2, ...},
  ...
]
```

### 3. Kotlin Code (`wiki_data_rev233/`)
```kotlin
// Auto-generated drop tables for Rev 233
object GeneratedDropTablesRev233 {
    fun DropTable.registerAll() {
        // Goblin
        // Released: 4 January 2001
        register(npcs.goblin) {
            drop(objs.bones, 1, rate = 128)
            drop(objs.coins, 5, rate = 16)
            // ...
        }
        
        // Cow
        // Released: 4 January 2001
        register(npcs.cow) {
            drop(objs.bones, 1, rate = 1)
            drop(objs.raw_beef, 1, rate = 1)
            // ...
        }
    }
}
```

---

## Features

### Caching
- First scrape: Downloads from wiki (slower)
- Subsequent: Loads from cache (instant)
- Cache files are JSON, human-readable

### Rate Limiting
```python
REQUEST_DELAY = 1.0  # 1 second between requests
MAX_WORKERS = 3      # 3 parallel threads
```
**Total time:** ~1 second per monster (with cache: instant)

140 monsters = ~2.5 minutes (first time)  
140 monsters = ~5 seconds (with cache)

### Resume Capability
- If it crashes, just re-run
- Already-cached monsters are skipped
- Only failed/uncached get re-scraped

### Rev 233 Filtering
```python
REV_233_DATE = July 26, 2023

if release_date <= July 26, 2023:
    scrape()   # ✓ Include
else:
    skip()     # ✗ Exclude (post-233)
```

---

## Customization

### Change Rate Limit
```python
# In bulk_scraper.py:
REQUEST_DELAY = 2.0  # Slower, more polite
REQUEST_DELAY = 0.5  # Faster (don't abuse!)
```

### Add More Monsters
```python
# Add to F2P_MONSTERS list:
F2P_MONSTERS = [
    # ... existing ...
    "My_custom_monster",
    "Another_one",
]
```

### Scrape Other Categories
```python
# Add methods for items, objects, etc.
def scrape_item(self, name: str) -> Optional[Dict]:
    # Similar to scrape_monster
    pass

def scrape_all_items(self, item_list: List[str]):
    # Bulk item scraping
    pass
```

---

## Tips

### First Time: Start Small
```bash
# Test with F2P first (38 monsters)
python bulk_scraper.py
> f2p

# If good, do members
python bulk_scraper.py
> members

# Then everything
python bulk_scraper.py
> all
```

### Check Cache
```bash
# See what's cached
ls wiki_cache/ | wc -l

# Check specific monster
cat wiki_cache/monster_Goblin.json | jq '.drops'
```

### Clear Cache (if needed)
```bash
# Remove all cached data
rm wiki_cache/*.json

# Or just one
rm wiki_cache/monster_Goblin.json
```

---

## Example Session

```bash
$ cd OSRSWikiScraper
$ python bulk_scraper.py

============================================================
OSRS Wiki Bulk Scraper for Rev 233
============================================================

What to scrape? (f2p/members/all): f2p
Will scrape 38 monsters
Continue? (yes/no): yes

============================================================
Scraping 38 monsters with 3 workers...
Rate limit: 1.0s between requests
Cache directory: wiki_cache
------------------------------------------------------------
[1/38] ✓ Goblin: 8 drops
[2/38] ✓ Giant_rat: 5 drops
[3/38] ✓ Cow: 4 drops
[4/38] ✓ Chicken: 6 drops
[5/38] ✓ Man: 3 drops
...
[38/38] ✓ Barbarian: 7 drops

Stats: 36 scraped | 2 cached | 0 skipped | 0 failed
------------------------------------------------------------

Exporting results...
Exported 36 monsters to wiki_data_rev233/monsters_rev233_f2p.json
Generated Kotlin code for 36 monsters: wiki_data_rev233/GeneratedDropTablesRev233_F2p.kt

DONE!
```

**Total time: ~40 seconds for 38 monsters**

---

## Next Steps

1. **Run the scraper**
2. **Check generated Kotlin code**
3. **Copy to RSMod project**
4. **Hook into combat system**
5. **Test in-game!**

---

## Troubleshooting

### "Failed to scrape X"
- Check internet connection
- Monster might not exist on wiki
- Try again (resume will skip already-cached)

### "0 drops found"
- Monster might have non-standard drop table
- Check wiki page manually
- Some monsters use special drop mechanics

### "Cache not working"
- Ensure `wiki_cache/` directory exists
- Check file permissions
- Cache files are JSON, check they're valid

---

## Summary

✅ **Scrape EVERYTHING** (F2P, members, all)  
✅ **Rev 233 filtered** (no incompatible content)  
✅ **Cached** (fast re-runs)  
✅ **Rate limited** (respect wiki)  
✅ **Parallel** (fast but polite)  
✅ **Export** (JSON + Kotlin)  

**Ready to scrape the entire wiki?** 🚀

