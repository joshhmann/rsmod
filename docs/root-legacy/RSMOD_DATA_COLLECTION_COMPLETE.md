# RSMod v2 Data Collection - Complete Summary

**Project:** OSRS Private Server (RSMod v2)  
**Target Revision:** OSRS Rev 233 (late 2023)  
**Collection Date:** 2026-02-20

---

## 📊 Collection Overview

| Category | Count | Location | Status |
|----------|-------|----------|--------|
| **Monster Data** | 37 JSON files | `wiki-data/monsters/` | ✅ Complete |
| **Generated Code** | 17 F2P drop tables | `rsmod/content/other/npc-drops/generated/` | ✅ Ready |
| **Skills (manual)** | 5 JSON files | `wiki-data/skills/` | 🟡 Partial |
| **Documentation** | 8 guides | Various | ✅ Complete |
| **Tools** | 5 scripts | `OSRSWikiScraper/`, `scripts/` | ✅ Ready |

---

## 🎯 Monster Collection (37 Total)

### F2P Monsters - READY FOR IMPLEMENTATION (17)

All F2P monsters have complete combat stats and drop tables:

```
✅ goblin.json           Combat 2   | 5 HP   | 27 drops
✅ cow.json              Combat 2   | 8 HP   | 4 drops
✅ chicken.json          Combat 1   | 3 HP   | 5 drops
✅ giant_rat.json        Combat 3   | 5 HP   | 8 drops
✅ guard.json            Combat 21  | 22 HP  | 17 drops
✅ man.json              Combat 2   | 7 HP   | 14 drops
✅ woman.json            Combat 2   | 7 HP   | 13 drops
✅ al_kharid_warrior.json Combat 9  | 19 HP  | 16 drops
✅ hill_giant.json       Combat 28  | 35 HP  | 30 drops
✅ moss_giant.json       Combat 42  | 60 HP  | 30 drops
✅ lesser_demon.json     Combat 82  | 79 HP  | 25 drops
✅ greater_demon.json    Combat 92  | 87 HP  | 22 drops
✅ black_knight.json     Combat 33  | 42 HP  | 18 drops
✅ dark_wizard.json      Combat 7   | 12 HP  | 18 drops
✅ skeleton.json         Combat 21  | 24 HP  | 34 drops
✅ zombie.json           Combat 13  | 22 HP  | 27 drops
✅ giant_spider.json     Combat 27  | 32 HP  | 6 drops
```

### Members Monsters - READY (20)

**Dragons (6):**
- king_black_dragon.json (Combat 276, Boss)
- green_dragon.json (Combat 79)
- blue_dragon.json (Combat 111)
- red_dragon.json (Combat 152)
- black_dragon.json (Combat 227)

**Giants (3):**
- fire_giant.json, ice_giant.json, hobgoblin.json

**Slayer Creatures (10):**
- abyssal_demon.json (85 Slayer)
- dust_devil.json (65 Slayer)
- gargoyle.json (75 Slayer)
- nechryael.json (80 Slayer)
- bloodveld.json (50 Slayer)
- hellhound.json, dagannoth.json
- cave_horror.json (58 Slayer)
- banshee.json (15 Slayer)
- crawling_hand.json (5 Slayer)

**Other (1):**
- jogre.json, earth_warrior.json

---

## 🛠️ Tools Created

### 1. OSRSWikiScraper v2
**Location:** `OSRSWikiScraper/`

```bash
# Scrape single monster
python OSRSWikiScraper/scraper_v2.py -n "Goblin"

# Batch export
python OSRSWikiScraper/export_for_rsmod.py --f2p-monsters -o wiki-data/

# Rev 233 validation
python OSRSWikiScraper/rev233_validator.py --check-monster Goblin
```

**Features:**
- ✅ Caching (24hr)
- ✅ Rate limiting
- ✅ Rev 233 compatibility checking
- ✅ JSON output

### 2. Drop Table Generator
**Location:** `scripts/generate_droptables.py`

```bash
# Generate for single monster
python scripts/generate_droptables.py --monster goblin --wiki-dir wiki-data/monsters

# Generate all F2P
python scripts/generate_droptables.py --all-f2p --wiki-dir wiki-data/monsters \
  --output-dir rsmod/content/other/npc-drops/generated/
```

**Output:** RSMod-compatible Kotlin drop table code

### 3. Cache Symbol Lookup
**Location:** `scripts/cache_lookup.py`

```bash
# Find item ID
python scripts/cache_lookup.py --obj bones
# Output: bones = 526

# List all swords
python scripts/cache_lookup.py --obj sword --list

# Generate Kotlin refs
python scripts/cache_lookup.py --refs items.txt -o refs.kt
```

### 4. Rev 233 Validator
**Location:** `OSRSWikiScraper/rev233_validator.py`

- Validates content for rev 233 compatibility
- Identifies post-rev 233 content
- Compares with Kronos rev 184 data

---

## 📁 File Locations

### Wiki Data (JSON)
```
wiki-data/
├── monsters/                    # 37 monster JSON files
│   ├── goblin.json
│   ├── cow.json
│   ├── chicken.json
│   └── ... (34 more)
├── skills/                      # Manual skill data
│   ├── woodcutting.json
│   ├── mining.json
│   ├── fishing.json
│   └── ... (2 more)
├── MONSTER_INDEX.md            # Monster quick reference
├── F2P_MONSTERS_QUICKREF.md    # F2P implementation guide
└── README.md                   # Wiki data documentation
```

### Generated RSMod Code
```
rsmod/content/other/npc-drops/generated/
└── GeneratedDropTables.kt       # 17 F2P drop tables (55KB)
```

### Documentation
```
docs/
├── WORK_PLAN.md                # Implementation roadmap
├── CONTENT_AUDIT.md            # Current status
├── TRANSLATION_CHEATSHEET.md   # Alter→v2 API mapping
├── LLM_TESTING_GUIDE.md        # Testing methodology
└── REV233_DATA_GUIDE.md        # Rev 233 specific info

REVMOD_DATA_COLLECTION_COMPLETE.md  # This file
REV233_COMPATIBILITY_REPORT.md      # Rev 233 validation
```

### Tools
```
OSRSWikiScraper/
├── scraper_v2.py               # Main wiki scraper
├── export_for_rsmod.py         # Batch export tool
├── rev233_validator.py         # Rev 233 validator
├── requirements.txt            # Python deps
└── README_v2.md               # Scraper docs

scripts/
├── generate_droptables.py      # Drop table generator
└── cache_lookup.py             # Cache ID lookup
```

---

## 🚀 Implementation Readiness

### Phase 1: F2P NPC Combat (READY TO START)

All data ready for the 17 F2P monsters:

```kotlin
// Example: Goblin implementation
// 1. Add to npc params (.toml)
params.hitpoints = 5
params.attack = 1
params.strength = 1
params.defence = 1
params.attack_speed = 4

// 2. Drop table already generated in GeneratedDropTables.kt
register(npcs.goblin) {
    guaranteed(objs.bones)
    drop(objs.bronze_sq_shield, 1, rate = 3)
    // ... etc
}

// 3. Add combat script
onNpcHit(npcs.goblin) {
    npc.queueCombatRetaliate(attacker)
}
```

### Priority Order for Implementation:

1. **Tutorial Area** (Day 1)
   - Chicken, Cow, Goblin

2. **Varrock Area** (Day 1-2)
   - Giant rat, Man, Woman, Dark wizard, Guard

3. **Training Spots** (Day 2-3)
   - Hill Giant, Moss Giant

4. **Wilderness** (Day 3-4)
   - Lesser demon, Greater demon, Black Knight

5. **Dungeons** (Day 4-5)
   - Skeleton, Zombie

---

## ⚠️ Rev 233 Considerations

### Safe Content (No Changes Expected)
- All F2P monsters
- Standard dragons
- Most slayer monsters
- Giants

### Verify Before Implementing
- Wilderness bosses (pre-rework versions)
- Any content from 2024+ updates

### Items to Exclude
- Wilderness rings (Bellator, Magus, Ultor, Venator)
- Varlamore items
- Post-rev 233 clue rewards

See `docs/REV233_DATA_GUIDE.md` for complete details.

---

## 📋 Next Steps

### Immediate (Use This Data)

1. **Implement F2P NPC Combat**
   ```bash
   # Use generated drop tables
   cp rsmod/content/other/npc-drops/generated/GeneratedDropTables.kt \
      rsmod/content/other/npc-drops/F2PDropTables.kt
   
   # Add NPC params from wiki-data/monsters/*.json
   ```

2. **Add Missing Item References**
   ```bash
   # Look up item IDs
   python scripts/cache_lookup.py --obj "goblin_mail"
   
   # Add to BaseObjs.kt if missing
   ```

3. **Add Animations**
   ```bash
   # Find animation IDs
   python scripts/cache_lookup.py --seq goblin
   ```

### Short Term (Expand Data)

1. **More Monsters**
   ```bash
   # Add specific monsters as needed
   cd OSRSWikiScraper
   python scraper_v2.py -n "New_Monster" -o ../wiki-data/monsters/
   ```

2. **Skill Data**
   - Create wiki-data/skills/ files for unimplemented skills
   - Use scraper to extract XP rates, level reqs

3. **Item Stats**
   - Scrape equipment stats for combat formula

### Long Term (Maintain)

1. **Keep Data Updated**
   - Re-scrape when implementing new content
   - Validate against rev 233

2. **Add Test Coverage**
   - Create bot tests for each monster
   - Verify drop rates statistically

---

## 🔍 Data Quality

| Metric | Status |
|--------|--------|
| Combat Stats | ✅ Complete from wiki |
| Drop Tables | ✅ Complete from wiki |
| Drop Rates | ✅ Wiki format (1/128) |
| Item IDs | 🟡 Look up as needed |
| Animation IDs | 🟡 Look up as needed |
| Rev 233 Validation | ✅ All 37 checked |

---

## 📚 Quick Reference

### Get Monster Stats
```bash
cat wiki-data/monsters/goblin.json | jq '.hitpoints, .attack, .strength'
# Output: 5, 1, 1
```

### Get Drop Rate
```bash
cat wiki-data/monsters/goblin.json | jq '.drops[] | select(.name=="Bones")'
# Output: {"name": "Bones", "rate": "1/1", ...}
```

### Generate Code
```bash
python scripts/generate_droptables.py --monster goblin --wiki-dir wiki-data/monsters
```

### Look Up Cache ID
```bash
python scripts/cache_lookup.py --obj bones
# Output: bones = 526
```

---

## 🎉 Summary

**What's Ready:**
- ✅ 37 monster data files (JSON)
- ✅ 17 F2P drop tables (Kotlin)
- ✅ Rev 233 validation tools
- ✅ Cache lookup utilities
- ✅ Complete documentation

**What's Needed Next:**
- 🟡 Item ID mapping (use cache_lookup.py)
- 🟡 Animation references (use cache_lookup.py)
- 🟡 Skill data for unimplemented skills
- 🟡 Test bot scripts

**Ready to Start:**
F2P NPC Combat Implementation (Phase 6 of WORK_PLAN)

---

Last Updated: 2026-02-20

