# RSMod NPC Data Tools

This directory contains tools for extracting and converting NPC combat stats and drop tables for RSMod.

## Quick Start

```bash
# 1. Index symbol files (one-time setup)
python tools/symbol_indexer.py

# 2. Lookup a specific NPC
python tools/npc_lookup.py "Hill Giant"

# 3. Generate Kotlin drop table
python tools/npc_lookup.py "Hill Giant" --output kotlin

# 4. Batch process all Tier 1 F2P NPCs
python tools/batch_npc_processor.py --tier 1 --dry-run
python tools/batch_npc_processor.py --tier 1
```

## Tools

### symbol_indexer.py
Indexes RSMod `.sym` files into searchable JSON for fast lookups.

```bash
python tools/symbol_indexer.py
python tools/symbol_indexer.py --search "hill_giant" --type npc
```

### npc_lookup.py
Unified NPC data lookup combining wiki-data, Kronos, and rev 233 symbols.

```bash
python tools/npc_lookup.py "NPC Name"
python tools/npc_lookup.py "NPC Name" --output json
python tools/npc_lookup.py "NPC Name" --output kotlin
```

### batch_npc_processor.py
Batch process multiple NPCs and generate Kotlin files.

```bash
python tools/batch_npc_processor.py --tier 1        # Tier 1 F2P NPCs
python tools/batch_npc_processor.py --tier 2        # Tier 2 F2P NPCs
python tools/batch_npc_processor.py --npcs "Hill Giant" "Moss Giant"
python tools/batch_npc_processor.py --list npcs.txt
```

### drop_rate_converter.py
Convert OSRS drop rates (1/X) to RSMod weights.

```bash
python tools/drop_rate_converter.py "1/128"
python tools/drop_rate_converter.py --file drops.txt
```

## F2P NPC Tiers

| Tier | NPCs | Status |
|------|------|--------|
| 1 | Hill Giant, Moss Giant, Zombie, Skeleton, Giant Spider, Hobgoblin | Ready |
| 2 | Ice Giant, Ice Warrior, King Scorpion, Deadly Red Spider, Mugger, Barbarian, Dwarf, Warrior Woman | Ready |
| 3 | Bear, Unicorn, Black Unicorn, Thief, Outlaw, Jail Guard, Chaos Druid | Ready |

## Data Sources

- **wiki-data/monsters/*.json** - Drop tables from OSRS Wiki
- **Kronos-184-Fixed/** - Combat stats and animations (rev 184)
- **rsmod/.data/symbols/** - Rev 233 internal names and IDs
- **MCP Cache Tools** - Live cache queries

## Output

Generated Kotlin files go to:
```
rsmod/content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcdrops/tables/
```

## Documentation

- `docs/NPC_DATA_PIPELINE_PROPOSAL.md` - Original proposal
- `docs/NPC_DATA_METHODS.md` - Detailed method comparison
- `docs/NPC_DATA_TOOLS.md` - Tool usage guide
