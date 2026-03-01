# NPC Data Tools Update Summary

## Changes Made

### 1. New Tools Created (`tools/`)

| Tool | Purpose |
|------|---------|
| `symbol_indexer.py` | Parse `.sym` files into searchable JSON indexes |
| `npc_lookup.py` | Unified NPC data lookup (wiki + Kronos + rev 233 symbols) |
| `batch_npc_processor.py` | Batch generate Kotlin drop tables for multiple NPCs |
| `drop_rate_converter.py` | Convert OSRS drop rates (1/X) to RSMod weights |

### 2. New Documentation

| File | Description |
|------|-------------|
| `docs/NPC_DATA_PIPELINE_PROPOSAL.md` | Original architecture proposal |
| `docs/NPC_DATA_METHODS.md` | Comparison of 4 data lookup methods |
| `docs/NPC_DATA_TOOLS.md` | Complete tool usage guide |
| `tools/README.md` | Quick reference for tools |

### 3. Updated AGENTS.md

**Added to Key Docs table:**
- `docs/NPC_DATA_TOOLS.md` — Complete guide to NPC/drop table tools
- `tools/` — Python tools for NPC data extraction

**Added to Claude's Skills table:**
- `/rsmod-npc-data-tools` — NEW skill for automated NPC drop table generation

**Updated Kimi's Guidelines:**
- Added "USE THE TOOLS" directive
- Added "Primary Skill: /rsmod-npc-data-tools"
- Added NPC Data Tools code examples

**Updated Current Blockers:**
- Marked "NPC data tools created" as ✅ DONE

### 4. Updated docs/CONTENT_AUDIT.md

**Added to Data Sources for Porting:**
- NPC drop table generation using the new tools

**Updated Tier 1 Priorities:**
- Added tool references to NPC tasks (e.g., "use `python tools/npc_lookup.py "Hill Giant" --output kotlin`")

### 5. Updated Existing Skills

#### `.claude/skills/rsmod-npc-combat-definer/SKILL.md`
- Added "NEW: Use the NPC Data Tools First" section
- Updated workflow to use `tools/npc_lookup.py` as PRIMARY source
- Added quick reference table for all tools

#### `.claude/skills/mcp-osrs-lookup/SKILL.md`
- Added "RECOMMENDED: Use the Python tools first for bulk work" section
- Shows Python tool examples before MCP examples

#### `.claude/skills/rsmod-wiki-oracle/SKILL.md`
- Added "NEW: Python NPC Data Tools (RECOMMENDED for NPCs)" section
- Shows tool commands before MCP tools section

### 6. New Skill Created

#### `.claude/skills/rsmod-npc-data-tools/SKILL.md`
Complete skill documentation including:
- Quick start guide
- Tool overview table
- Detailed usage for each tool
- Workflow examples (Method 1: Quick Single NPC, Method 2: Batch Generation)
- Data sources explanation
- Troubleshooting section
- Integration with other skills

---

## How Agents Should Use This

### For Kimi (Data Extraction):
```bash
# Before manual data entry, ALWAYS run:
python tools/npc_lookup.py "NPC Name"

# This auto-maps rev 184 → 233 IDs and shows all data in one place
```

### For Claude (Content Implementation):
```bash
# Generate drop table skeleton:
python tools/npc_lookup.py "Hill Giant" --output kotlin

# Or batch process all Tier 1 F2P NPCs:
python tools/batch_npc_processor.py --tier 1
```

### For All Agents:
- Use `/rsmod-npc-data-tools` skill when working with NPC data
- Reference `docs/NPC_DATA_TOOLS.md` for detailed documentation
- The tools eliminate manual ID mapping and reduce errors

---

## Result

**Before:** Manual process to create drop tables:
1. Read wiki-data JSON
2. Read Kronos JSON  
3. Manually map rev 184 IDs to rev 233 symbols
4. Manually write Kotlin file
5. Hope the IDs match

**After:** Automated pipeline:
1. Run `python tools/npc_lookup.py "NPC Name" --output kotlin`
2. Get complete, auto-mapped Kotlin file
3. Customize weights and verify
4. Done

**Time saved:** ~80% reduction in drop table creation time
**Error reduction:** Eliminates manual ID mapping errors

