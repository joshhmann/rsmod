---
name: rsmod-npc-data-tools
description: Use Python tools for NPC drop table and combat data extraction. Automates the conversion of wiki-data, Kronos JSON, and rev 233 cache symbols into ready-to-use Kotlin drop table files.
---

# RSMod NPC Data Tools

**Use these tools to eliminate the NPC drop table bottleneck.**

Instead of manually mapping rev 184 IDs to rev 233 symbols, use the automated pipeline:

```
wiki-data/ + Kronos/ + rsmod/.data/symbols/ → tools/npc_lookup.py → Kotlin drop tables
```

---

## Quick Start

```bash
# 1. Index symbol files (one-time setup)
python tools/symbol_indexer.py

# 2. Lookup any NPC
python tools/npc_lookup.py "Hill Giant"

# 3. Generate Kotlin drop table
python tools/npc_lookup.py "Hill Giant" --output kotlin

# 4. Batch process all Tier 1 F2P NPCs
python tools/batch_npc_processor.py --tier 1 --dry-run
python tools/batch_npc_processor.py --tier 1
```

---

## Tools Overview

| Tool | Purpose | When to Use |
|------|---------|-------------|
| `symbol_indexer.py` | Parse `.sym` files to JSON | One-time setup, or when symbols change |
| `npc_lookup.py` | Unified NPC data lookup | Single NPC investigation |
| `batch_npc_processor.py` | Batch generate drop tables | Multiple NPCs at once |
| `drop_rate_converter.py` | Convert 1/X to weights | Calculating drop table weights |

---

## npc_lookup.py

**Unified lookup combining all data sources:**
- `wiki-data/monsters/*.json` - Drop tables
- `Kronos-184-Fixed/.../npcs/combat/` - Combat stats, animations
- `rsmod/.data/symbols/` - Rev 233 internal names and IDs

### Usage

```bash
# Summary view (default)
python tools/npc_lookup.py "Hill Giant"

# Output as JSON
python tools/npc_lookup.py "Hill Giant" --output json

# Generate Kotlin drop table skeleton
python tools/npc_lookup.py "Hill Giant" --output kotlin
```

### Example Output

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
  Attack Speed: N/A ticks

[Drops] (74 items)
  - Big bones: 1/1 -> big_bones
  - Giant key: 1/128 -> NOT FOUND
  - Iron dagger: 1/32 -> iron_dagger
  - Iron med helm: 1/25 -> iron_med_helm
  ...
```

**Key Features:**
- Auto-maps item names to rev 233 symbols (e.g., "fire rune" → `firerune`)
- Shows all NPC variants
- Flags items not found in cache

---

## batch_npc_processor.py

**Batch process multiple NPCs and generate Kotlin files.**

### Usage

```bash
# Process by tier
python tools/batch_npc_processor.py --tier 1        # 6 NPCs
python tools/batch_npc_processor.py --tier 2        # 8 NPCs
python tools/batch_npc_processor.py --tier 3        # 7 NPCs

# Process specific NPCs
python tools/batch_npc_processor.py --npcs "Hill Giant" "Moss Giant" "Zombie"

# Process from file
python tools/batch_npc_processor.py --list f2p_npcs.txt

# Dry run (don't write files)
python tools/batch_npc_processor.py --tier 1 --dry-run

# Generate report
python tools/batch_npc_processor.py --tier 1 --report report.json
```

### F2P NPC Tiers

| Tier | NPCs | Priority |
|------|------|----------|
| 1 | Hill Giant, Moss Giant, Zombie, Skeleton, Giant Spider, Hobgoblin | Critical |
| 2 | Ice Giant, Ice Warrior, King Scorpion, Deadly Red Spider, Mugger, Barbarian, Dwarf, Warrior Woman | High |
| 3 | Bear, Unicorn, Black Unicorn, Thief, Outlaw, Jail Guard, Chaos Druid | Medium |

---

## drop_rate_converter.py

**Convert OSRS drop rates to RSMod weights.**

OSRS uses "1 in X" rates. RSMod uses weighted random selection.

### Usage

```bash
# Single conversion
python tools/drop_rate_converter.py "1/128"
# Output: Weight (denominator=128): 1

# Show common rates
python tools/drop_rate_converter.py

# Process from file
python tools/drop_rate_converter.py --file drops.txt --denominator 128
```

### Common Rates Reference

| OSRS Rate | Probability | RSMod Weight (denom=128) |
|-----------|-------------|--------------------------|
| 1/1 | 100% | 128 |
| 1/2 | 50% | 64 |
| 1/8 | 12.5% | 16 |
| 1/32 | 3.125% | 4 |
| 1/128 | 0.78% | 1 |
| 1/512 | 0.195% | 1 (use denom=512) |

---

## Workflow: Creating Drop Tables

### Method 1: Quick Single NPC

```bash
# 1. Lookup NPC
python tools/npc_lookup.py "Hill Giant"

# 2. Generate Kotlin
python tools/npc_lookup.py "Hill Giant" --output kotlin > HillGiantDropTables.kt

# 3. Edit the file:
#    - Add proper weights to drops
#    - Fix any "NOT FOUND" items
#    - Verify NPC variants

# 4. Move to correct location
mv HillGiantDropTables.kt rsmod/content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcdrops/tables/

# 5. CRITICAL: Register in NpcDropTablesScript.kt
#
#    Open: rsmod/content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcdrops/NpcDropTablesScript.kt
#
#    Add to startup() function:
#        HillGiantDropTables.registerAll(registry)
#
#    NOTE: The table file is NOT automatically registered! You MUST add the
#    registerAll() call or the drops will never be loaded in-game.
#
#    Example pattern for multiple tables:
#        ZombieDropTables.registerAll(registry)
#        MossGiantDropTables.registerAll(registry)
#        HillGiantDropTables.registerAll(registry)
```

### Method 2: Batch Generation (Recommended)

```bash
# 1. Dry run to check what will be generated
python tools/batch_npc_processor.py --tier 1 --dry-run

# 2. Generate files
python tools/batch_npc_processor.py --tier 1

# 3. Files are written to:
#    rsmod/content/other/npc-drops/.../tables/

# 4. Review and customize each file
#    - Add missing item references
#    - Adjust drop weights
#    - Verify combat stats

# 5. CRITICAL: Register all tables in NpcDropTablesScript.kt
#
#    Open: rsmod/content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcdrops/NpcDropTablesScript.kt
#
#    Add each table's registerAll() call to the startup() function:
#        ZombieDropTables.registerAll(registry)
#        MossGiantDropTables.registerAll(registry)
#        HillGiantDropTables.registerAll(registry)
#
#    NOTE: Generated files are NOT automatically wired up! You MUST manually
#    add each registerAll() call or drops won't work in-game.
#
#    After adding registrations, build to verify:
#        .\gradlew.bat :content:other:npc-drops:build
```

---

## Data Sources

The tools combine data from multiple sources:

| Source | Path | Contains |
|--------|------|----------|
| wiki-data | `wiki-data/monsters/*.json` | Drop rates, quantities |
| Kronos | `Kronos-184-Fixed/.../npcs/combat/` | Combat stats, animations |
| Kronos | `Kronos-184-Fixed/.../npcs/drops/eco/` | Drop tables (rev 184 IDs) |
| RSMod Symbols | `rsmod/.data/symbols/*.sym` | Rev 233 internal names |
| Indexed Symbols | `.data/symbols/indexed/*.json` | Parsed symbol indexes |

---

## Troubleshooting

### "Symbol not found" for an item

The item name from wiki-data doesn't match the rev 233 symbol. Examples:
- "fire rune" → `firerune` (no space)
- "grimy guam leaf" → `grimy_guam` (no "_leaf")

**Fix:** The tool tries common variations automatically. If still not found:
1. Check actual symbol: `grep "firerune" rsmod/.data/symbols/obj.sym`
2. Add local ObjReferences in your drop table file

### "No rev 233 symbol" for an NPC

The NPC name doesn't match any symbol in `npc.sym`.

**Fix:** Try different name variations:
- "Hill Giant" → `kourend_hillgiant`, `wilderness_hill_giant`
- "Goblin" → `goblin`, `goblin_unarmed_melee_1`

Use `python tools/npc_lookup.py` to see all variants.

### Missing combat stats

Kronos JSON file may be missing or have different format.

**Fix:** Check if file exists:
```bash
ls "Kronos-184-Fixed/Kronos-master/kronos-server/data/npcs/combat/Hill_giant.json"
```

If missing, use `get_npc_rev233` MCP tool for stats.

---

## Integration with Other Skills

### With `/rsmod-npc-combat-definer`
Use `npc_lookup.py` to get the data, then follow combat definer patterns for implementation.

### With `/mcp-osrs-lookup`
Use MCP tools for verification after generating with Python tools.

### With `/rsmod-wiki-oracle`
The tools read from wiki-data JSONs that wiki-oracle helps create.

---

## See Also

- `docs/NPC_DATA_TOOLS.md` — Full documentation
- `docs/NPC_DATA_METHODS.md` — Method comparison
- `tools/README.md` — Quick reference
- `.claude/skills/rsmod-npc-combat-definer/SKILL.md` — Combat implementation
