# NPC Drop Table & Combat Data Pipeline Proposal

## Current Bottleneck Analysis

### The Problem
1. **Manual ID mapping**: Converting Kronos rev 184 IDs to RSMod rev 233 symbols is tedious and error-prone
2. **No automated validation**: We can't easily verify if NPC names, item names, or drop rates match OSRS wiki
3. **Fragmented data sources**: Data scattered across Kronos JSON, wiki-data JSON, and RSMod symbol files
4. **No bulk generation**: Each NPC drop table requires manual Kotlin file creation

### Current Data Sources
| Source | Contains | Format | Revision |
|--------|----------|--------|----------|
| `Kronos-184-Fixed/data/npcs/combat/` | Combat stats, animations, IDs | JSON | 184 |
| `Kronos-184-Fixed/data/npcs/drops/eco/` | Drop tables with item IDs | JSON | 184 |
| `rsmod/.data/symbols/npc.sym` | NPC symbol names → IDs | Text | 233 |
| `rsmod/.data/symbols/obj.sym` | Item symbol names → IDs | Text | 233 |
| `wiki-data/monsters/*.json` | Wiki-sourced drops, combat | JSON | N/A |

---

## Proposed Solution: 3-Tier Pipeline

### Tier 1: ID Mapping Database (One-time setup)
Create a mapping file that bridges rev 184 → rev 233 IDs for common items/NPCs.

```json
{
  "_meta": {
    "source_rev": 184,
    "target_rev": 233,
    "generated_at": "2026-02-24"
  },
  "npcs": {
    "hill_giant": {
      "rev184_ids": [2098, 2099, 2100, 2101, 2102, 2103, 7261],
      "rev233_symbol": "hill_giant",
      "rev233_ids": [?, ?, ?]  // From npc.sym lookup
    }
  },
  "items": {
    "532": {  // rev 184 ID
      "name": "Big bones",
      "rev233_symbol": "big_bones",
      "rev233_id": ?
    }
  }
}
```

### Tier 2: Bulk Conversion Tool
Python script that:
1. Reads Kronos combat + drop JSON
2. Looks up rev 233 symbols from `.sym` files
3. Generates wiki-data compatible JSON
4. Outputs RSMod Kotlin drop table files

**Input**: `Hill_giant.json` (Kronos)
**Output**: 
- `wiki-data/monsters/hill_giant.json` (updated)
- `rsmod/content/other/npc-drops/tables/HillGiantDropTables.kt` (generated)

### Tier 3: Validation & Sync
Automated checks:
- Verify all item symbols exist in `BaseObjs` or generate local refs
- Cross-check drop rates against OSRS wiki
- Flag NPCs missing from `npc.sym`

---

## Implementation Plan

### Phase 1: Symbol Indexer (Quick win - 1-2 hours)
Create a Python tool that parses `.sym` files into searchable JSON:

```python
# tools/symbol_indexer.py
# Generates: .data/symbols/indexed/npcs.json, objs.json

# Usage:
# python tools/symbol_indexer.py
# 
# Output: {
#   "hill_giant": {"id": 1234, "internal_name": "hill_giant"},
#   "hill_giant2": {"id": 1235, "internal_name": "hill_giant2"},
#   ...
# }
```

### Phase 2: Kronos-to-RSMod Converter (4-6 hours)
```python
# tools/convert_npc_data.py

# Usage:
# python tools/convert_npc_data.py --npc "Hill_giant" --output-rsmod --output-wiki
#
# Actions:
# 1. Read Kronos combat JSON
# 2. Read Kronos drops JSON  
# 3. Map rev 184 item IDs → rev 233 symbols (using lookup table)
# 4. Generate HillGiantDropTables.kt
# 5. Update wiki-data/monsters/hill_giant.json
```

### Phase 3: Batch Processor (2-3 hours)
```python
# tools/batch_convert_npcs.py

# Usage:
# python tools/batch_convert_npcs.py --list F2P_NPCS.txt
# 
# F2P_NPCS.txt:
#   Hill_giant
#   Moss_giant
#   Goblin
#   ...
```

### Phase 4: Validation Suite (2-3 hours)
```python
# tools/validate_drops.py

# Usage:
# python tools/validate_drops.py --against-wiki
#
# Compares generated drop tables against OSRS wiki data
# Flags discrepancies in rates, quantities, missing items
```

---

## Quick Reference: Rev 184 → 233 Item ID Mapping

### Common F2P Drops (sample)

| Item | Rev 184 ID | Rev 233 Symbol | Status |
|------|------------|----------------|--------|
| Big bones | 532 | `big_bones` | ✅ Verified |
| Bones | 526 | `bones` | ✅ Verified |
| Coins | 995 | `coins` | ✅ Verified |
| Iron arrow | 884 | `iron_arrow` | ✅ Verified |
| Steel arrow | 886 | `steel_arrow` | ✅ Verified |
| Iron dagger | 1203 | `iron_dagger` | ✅ Verified |
| Iron full helm | 1153 | `iron_full_helm` | ✅ Verified |
| Iron kiteshield | 1191 | `iron_kiteshield` | ✅ Verified |
| Steel longsword | 1295 | `steel_longsword` | ✅ Verified |
| Water rune | 555 | `water_rune` | ✅ Verified |
| Fire rune | 554 | `fire_rune` | ✅ Verified |
| Nature rune | 561 | `nature_rune` | ✅ Verified |
| Law rune | 563 | `law_rune` | ✅ Verified |
| Chaos rune | 562 | `chaos_rune` | ✅ Verified |
| Death rune | 560 | `death_rune` | ✅ Verified |
| Cosmic rune | 564 | `cosmic_rune` | ✅ Verified |
| Mind rune | 558 | `mind_rune` | ✅ Verified |
| Grimy guam | 199 | `grimy_guam` | ✅ Verified |
| Grimy marrentill | 201 | `grimy_marrentill` | ✅ Verified |
| Limpwurt root | 225 | `limpwurt_root` | ✅ Verified |
| Body talisman | 1446 | `body_talisman` | ✅ Verified |

---

## F2P NPC Priority List for Bulk Conversion

### Tier 1 (Critical - F2P Training)
- [ ] Hill Giant (combat + drops exist)
- [ ] Moss Giant (drops exist, need combat)
- [ ] Zombie (need both)
- [ ] Skeleton (combat exists, need drops)
- [ ] Giant Spider (need both)
- [ ] Hobgoblin (need both)

### Tier 2 (F2P Dungeons/Wilderness)
- [ ] Ice Giant (need both)
- [ ] Ice Warrior (need both)
- [ ] King Scorpion (need both)
- [ ] Deadly Red Spider (need both)
- [ ] Mugger (need both)
- [ ] Barbarian (need both)
- [ ] Dwarf (need both)
- [ ] Warrior Woman (need both)

### Tier 3 (F2P Misc)
- [ ] Bear (need both)
- [ ] Unicorn (need both)
- [ ] Black Unicorn (need both)
- [ ] Thief (need both)
- [ ] Outlaw (need both)
- [ ] Jail Guard (need both)

---

## Files to Create

```
tools/
├── symbol_indexer.py          # Parse .sym files to JSON
├── id_mapping_generator.py    # Build rev184→rev233 mapping
├── convert_npc_data.py        # Single NPC converter
├── batch_convert_npcs.py      # Batch processor
├── validate_drops.py          # Wiki validation
├── templates/
│   └── drop_table.kt.template # Kotlin file template
└── data/
    ├── rev184_item_index.json # Cached rev 184 items
    └── id_mapping.json        # 184→233 mappings
```

---

## Immediate Next Steps

1. **Create symbol indexer** - Parse all `.sym` files into searchable JSON
2. **Build F2P item ID mapping** - Focus on common drops first (50-100 items)
3. **Convert 6 Tier 1 NPCs** - Hill Giant, Moss Giant, Zombie, Skeleton, Giant Spider, Hobgoblin
4. **Validate against wiki** - Ensure drop rates match OSRS wiki

---

## Alternative: Wiki-First Approach

Instead of converting Kronos data, we could:
1. Scrape OSRS Wiki directly for drop tables (they have structured data)
2. Map wiki item names → rev 233 symbols
3. Generate Kotlin directly

**Pros**: Always accurate to current OSRS
**Cons**: Wiki doesn't have animation data, attack speeds (need Kronos for that)

**Recommended**: Hybrid approach
- Combat stats (animations, attack speed) → From Kronos
- Drop tables → From OSRS Wiki (more accurate, includes recent changes)

