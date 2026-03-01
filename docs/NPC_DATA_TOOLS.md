# NPC Data Tools - Quick Reference

## Overview

We've created a complete pipeline for NPC drop table and combat data extraction:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   wiki-data/    │    │  Kronos JSON    │    │  MCP Cache API  │
│  (drop tables)  │    │ (combat stats)  │    │  (rev 233 IDs)  │
└────────┬────────┘    └────────┬────────┘    └────────┬────────┘
         │                      │                      │
         └──────────────────────┼──────────────────────┘
                                │
                    ┌───────────▼───────────┐
                    │   tools/npc_lookup.py │
                    │   (unified lookup)    │
                    └───────────┬───────────┘
                                │
                    ┌───────────▼───────────┐
                    │ tools/batch_npc_      │
                    │  processor.py         │
                    │  (batch generation)   │
                    └───────────┬───────────┘
                                │
                    ┌───────────▼───────────┐
                    │  Generated Kotlin     │
                    │  Drop Table Files     │
                    └───────────────────────┘
```

---

## Tools

### 1. Symbol Indexer
**File:** `tools/symbol_indexer.py`

Indexes RSMod `.sym` files into searchable JSON.

```bash
# Index all symbols
python tools/symbol_indexer.py

# Search for specific symbol
python tools/symbol_indexer.py --search "hill_giant" --type npc
python tools/symbol_indexer.py --search "big_bones" --type obj
```

**Output:** `.data/symbols/indexed/*.json`

---

### 2. NPC Lookup
**File:** `tools/npc_lookup.py`

Unified lookup combining wiki-data, Kronos, and rev 233 symbols.

```bash
# Get NPC summary
python tools/npc_lookup.py "Hill Giant"

# Output as JSON
python tools/npc_lookup.py "Hill Giant" --output json

# Generate Kotlin drop table
python tools/npc_lookup.py "Hill Giant" --output kotlin
```

**Example Output:**
```
==================================================
NPC: Hill Giant
==================================================

[Rev 233 Cache]
  Symbol: wilderness_hill_giant
  ID: 13502
  Variants (2):
    - wilderness_hill_giant2 (ID: 13503)
    - wilderness_hill_giant3 (ID: 13504)

[Combat Stats]
  Combat Level: 28
  HP: 35
  Attack: 18
  Strength: 22
  Defence: 26

[Drops] (74 items)
  - Big bones: 1/1 -> big_bones
  - Giant key: 1/128 -> NOT FOUND
  - Iron dagger: 1/32 -> iron_dagger
  ...
```

---

### 3. Batch Processor
**File:** `tools/batch_npc_processor.py`

Process multiple NPCs and generate Kotlin files.

```bash
# Process by tier
python tools/batch_npc_processor.py --tier 1 --dry-run
python tools/batch_npc_processor.py --tier 1

# Process specific NPCs
python tools/batch_npc_processor.py --npcs "Hill Giant" "Moss Giant" "Zombie"

# Process from file
python tools/batch_npc_processor.py --list f2p_npcs.txt

# Generate report
python tools/batch_npc_processor.py --tier 1 --report report.json
```

**F2P NPC Tiers:**
- **Tier 1:** Hill Giant, Moss Giant, Zombie, Skeleton, Giant Spider, Hobgoblin
- **Tier 2:** Ice Giant, Ice Warrior, King Scorpion, Deadly Red Spider, Mugger, Barbarian, Dwarf, Warrior Woman
- **Tier 3:** Bear, Unicorn, Black Unicorn, Thief, Outlaw, Jail Guard, Chaos Druid

---

## Data Sources

### wiki-data/monsters/*.json
- **Source:** OSRS Wiki
- **Contains:** Drop rates, quantities, item names
- **Best for:** Accurate drop tables

### Kronos-184-Fixed/.../npcs/
- **Source:** Kronos RSPS (rev 184)
- **Contains:** Combat stats, animations, attack speed
- **Best for:** Combat behavior

### rsmod/.data/symbols/*.sym
- **Source:** RSMod cache (rev 233)
- **Contains:** Internal names → IDs
- **Best for:** Final ID verification

### MCP Cache Tools
- **Tools:** `search_npctypes`, `search_objtypes`, etc.
- **Contains:** Live cache queries
- **Best for:** Quick lookups, verification

---

## Quick Workflow

### Adding a New NPC Drop Table:

1. **Check if NPC exists in wiki-data:**
   ```bash
   ls wiki-data/monsters/hill_giant.json
   ```

2. **Lookup unified data:**
   ```bash
   python tools/npc_lookup.py "Hill Giant"
   ```

3. **Generate Kotlin file:**
   ```bash
   python tools/npc_lookup.py "Hill Giant" --output kotlin > HillGiantDropTables.kt
   ```

4. **Or batch process:**
   ```bash
   python tools/batch_npc_processor.py --tier 1
   ```

5. **Verify and customize:**
   - Edit generated file
   - Add proper weights to drop tables
   - Verify all item symbols exist

6. **Build module:**
   ```bash
   cd rsmod && gradlew.bat :content:other:npc-drops:build
   ```

---

## Common Issues & Solutions

### Issue: Item symbol not found
**Example:** "Giant key" → NOT FOUND

**Solution:** 
- Check if item exists in rev 233: `search_objtypes(query="giant key")`
- May need to add local ObjReferences in the drop table file

### Issue: Wrong NPC variant
**Example:** "Hill Giant" matches `wilderness_hill_giant` instead of `hill_giant`

**Solution:**
- Check all variants: `python tools/npc_lookup.py "Hill Giant"`
- Manually select correct symbol in generated Kotlin

### Issue: Missing combat stats
**Example:** Attack speed shows "N/A"

**Solution:**
- Kronos data may be missing
- Check Kronos file exists: `ls Kronos-184-Fixed/.../npcs/combat/Hill_giant.json`
- May need to manually add from OSRS Wiki

---

## Files Generated

After running batch processor, you'll have:

```
rsmod/content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcdrops/tables/
├── HillGiantDropTables.kt
├── MossGiantDropTables.kt
├── ZombieDropTables.kt
├── SkeletonDropTables.kt
├── GiantSpiderDropTables.kt
└── HobgoblinDropTables.kt
```

Each file contains:
- Drop table registration
- NPC variant references
- Item references (local ObjReferences for missing BaseObjs)

---

## Next Steps

1. ✅ **Symbol Indexer** - Parse .sym files
2. ✅ **NPC Lookup** - Unified data retrieval
3. ✅ **Batch Processor** - Mass generation
4. 🔄 **Weight Calculator** - Convert drop rates to weights
5. 🔄 **Validation Tool** - Compare against OSRS Wiki
6. 🔄 **Auto-registration** - Wire into NpcDropTablesScript.kt

