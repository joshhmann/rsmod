---
name: rsmod-wiki-oracle
description: Extract and manage OSRS wiki data for RSMod v2 development. Use when creating wiki-data JSON files, looking up skill XP rates, item IDs, drop tables, or validating content against OSRS wiki specifications.
---

# RSMod v2 Wiki Oracle

## Task Coordination — Do This First

Multiple agents (Claude, OpenCode, Kimi, Codex) work this codebase simultaneously.
Before writing a single line of code, coordinate via the `agent-tasks` MCP server:

1. `list_tasks({ status: "pending" })` — find available work
2. `get_task("TASK-ID")` — read the full task description
3. `claim_task("TASK-ID", "your-agent-name")` — atomically claim it (fails if taken)
4. `check_conflicts(["path/file"])` — verify no one else is editing your files
5. `lock_file("path/file", "your-agent-name", "TASK-ID")` — lock every file before editing
6. Implement.
7. `complete_task("TASK-ID", "your-agent-name", "what I built")` — releases locks, marks done

If blocked: `block_task("TASK-ID", "your-agent-name", "exact reason")` so others know.
See `START_HERE.md` for the full project orientation.

Manage wiki-accurate data for RSMod v2. Wiki oracle files provide the source of truth for skill XP, level requirements, item IDs, and drop tables.

## Wiki Data Structure

Wiki data lives in `wiki-data/`:

```
wiki-data/
├── skills/
│   ├── woodcutting.json
│   ├── mining.json
│   └── ...
└── monsters/
    ├── goblin.json
    └── cow.json
```

## Skill Oracle Format

```json
{
  "skill": "woodcutting",
  "actions": {
    "normal_tree": {
      "level_req": 1,
      "xp": 25.0,
      "animation": 879,
      "object_ids": [10820, 10808, 10833],
      "log_item_id": 1511,
      "respawn_ticks": 58
    },
    "oak_tree": {
      "level_req": 15,
      "xp": 37.5,
      "animation": 879,
      "object_ids": [10820],
      "log_item_id": 1521,
      "respawn_ticks": 15
    }
  },
  "tools": {
    "bronze_axe": {"item_id": 1351, "level_req": 1},
    "iron_axe": {"item_id": 1349, "level_req": 1},
    "steel_axe": {"item_id": 1353, "level_req": 6}
  },
  "test_locations": [
    {"desc": "Draynor normal/oak trees", "x": 3184, "z": 3436},
    {"desc": "Draynor willows", "x": 3088, "z": 3237}
  ]
}
```

### Required Fields

| Field | Type | Description |
|-------|------|-------------|
| `skill` | string | Skill name (lowercase) |
| `actions` | object | Action definitions keyed by name |
| `actions.*.level_req` | int | Level required |
| `actions.*.xp` | number | XP granted (with 0.1 precision) |
| `actions.*.animation` | int | Animation sequence ID |
| `test_locations` | array | Coordinates for bot testing |

### Optional Fields

| Field | Type | Description |
|-------|------|-------------|
| `tools` | object | Tool definitions with IDs and level reqs |
| `actions.*.object_ids` | array | Loc IDs for the resource |
| `actions.*.npc_ids` | array | NPC IDs (for fishing spots) |
| `actions.*.item_id` | int | Produced/consumed item |

## Monster Oracle Format

```json
{
  "name": "Goblin",
  "id": 1234,
  "combat_level": 2,
  "hitpoints": 8,
  "attack": 1,
  "strength": 1,
  "defence": 1,
  "attack_speed": 6,
  "slayer_level": 1,
  "slayer_xp": 8.0,
  "drops": [
    {
      "id": 526,
      "name": "Bones",
      "rate": 1.0,
      "qty_min": 1,
      "qty_max": 1,
      "guaranteed": true
    },
    {
      "id": 995,
      "name": "Coins",
      "rate": 0.15,
      "qty_min": 1,
      "qty_max": 10
    },
    {
      "id": 1139,
      "name": "Bronze med helm",
      "rate": 0.02,
      "qty_min": 1,
      "qty_max": 1
    }
  ]
}
```

### Drop Rate Format

Rates can be expressed as:
- **Decimal** (0.0-1.0): `0.15` = 15% = ~19/128
- **Fraction string**: `"1/128"`
- **Guaranteed flag**: `guaranteed: true` for 100% drops

## Data Extraction Tools

### NEW: Python NPC Data Tools (RECOMMENDED for NPCs)
```bash
# Unified lookup - combines wiki-data, Kronos, and rev 233 symbols
python tools/npc_lookup.py "Hill Giant"

# Generate Kotlin drop table
python tools/npc_lookup.py "Hill Giant" --output kotlin

# Batch process all F2P NPCs
python tools/batch_npc_processor.py --tier 1

# Convert drop rates to weights
python tools/drop_rate_converter.py "1/128"
```

See `docs/NPC_DATA_TOOLS.md` for complete documentation.

### MCP Tools (PREFERRED for skills/items — use before OSRSWikiScraper)

Three MCP servers provide OSRS data. Use them first; fall back to OSRSWikiScraper only if MCP doesn't cover the need.

### get_npc_rev233 — NPC Stats at Rev 233

```javascript
// MCP: osrs-wiki-rev233
get_npc_rev233({ name: "Green dragon" })
// Returns: hitpoints, attack, strength, defence, combat_level,
//          attack_speed, slayer_xp, drops with rates
```

### get_item_rev233 — Item Data at Rev 233

```javascript
// MCP: osrs-wiki-rev233
get_item_rev233({ name: "Dragon scimitar" })
// Returns: id, examine, weight, equipable, attack_bonus, strength_bonus,
//          attack_speed, members
```

### osrs_wiki_parse_page — Full Wiki Page

```javascript
// MCP: osrs-cache
osrs_wiki_parse_page({ page: "Goblin" })
// Returns: full parsed HTML — use for complete drop tables
```

### search_objtypes / search_npctypes / search_seqtypes — Cache IDs

```javascript
// MCP: osrs-cache
search_objtypes({ query: "dragon scimitar", pageSize: 5 })
search_npctypes({ query: "green dragon", pageSize: 5 })
search_seqtypes({ query: "dragon attack", pageSize: 5 })
```

## OSRSWikiScraper Usage (Fallback)

Use only when MCP tools don't cover the specific data needed.

```bash
cd OSRSWikiScraper
python osrswikiscraper.py -e "Dragon Scimitar"   # Item stats
python osrswikiscraper.py -n "Green dragon"       # Monster drops
python osrswikiscraper.py -aw > weapons.json      # All weapons
```

## XP Table Reference

OSRS XP formula:
```
xp(level) = floor(sum(i=1 to level-1) floor(i + 300 * 2^(i/7))) / 4
```

Key thresholds:

| Level | XP | Level | XP |
|-------|-----|-------|-----|
| 1 | 0 | 50 | 101,333 |
| 10 | 1,154 | 60 | 273,742 |
| 20 | 4,470 | 70 | 737,627 |
| 30 | 13,363 | 80 | 2,000,000 |
| 40 | 37,224 | 90 | 5,346,332 |
| 45 | 61,512 | 99 | 13,034,431 |

## Common Item IDs

### Logs (Firemaking/Woodcutting)
| Item | ID |
|------|-----|
| Logs | 1511 |
| Oak logs | 1521 |
| Willow logs | 1519 |
| Maple logs | 1517 |
| Yew logs | 1515 |
| Magic logs | 1513 |

### Ores (Mining/Smithing)
| Item | ID |
|------|-----|
| Copper ore | 436 |
| Tin ore | 438 |
| Iron ore | 440 |
| Coal | 453 |
| Gold ore | 444 |
| Mithril ore | 447 |
| Adamantite ore | 449 |
| Runite ore | 451 |

### Fish (Fishing/Cooking)
| Raw | ID | Cooked | ID | Burnt | ID |
|-----|-----|--------|-----|-------|-----|
| Raw shrimps | 317 | Shrimps | 315 | Burnt fish | 323 |
| Raw trout | 335 | Trout | 333 | Burnt fish | 343 |
| Raw salmon | 331 | Salmon | 329 | Burnt fish | 343 |
| Raw lobster | 377 | Lobster | 379 | Burnt lobster | 381 |
| Raw shark | 383 | Shark | 385 | Burnt shark | 387 |

### Bars (Smithing)
| Bar | ID |
|-----|-----|
| Bronze bar | 2349 |
| Iron bar | 2351 |
| Steel bar | 2353 |
| Gold bar | 2357 |
| Mithril bar | 2359 |
| Adamantite bar | 2361 |
| Runite bar | 2363 |

## Finding Cache IDs

### From obj.sym (items)
```
grep "^logs " .data/symbols/obj.sym          # Find ID for "logs"
grep "^rune_pickaxe " .data/symbols/obj.sym  # Find ID for rune pickaxe
```

### From loc.sym (objects)
```
grep "^tree " .data/symbols/loc.sym          # Tree loc IDs
grep "^willow_tree " .data/symbols/loc.sym   # Willow tree IDs
```

### From seq.sym (animations)
```
grep "^human_woodcutting" .data/symbols/seq.sym
```

### From npc.sym (NPCs)
```
grep "^goblin" .data/symbols/npc.sym
```

## Creating Wiki Data for New Skill

1. **Research wiki page:**
   - URL format: `https://oldschool.runescape.wiki/w/Skill_name`
   - Example: `https://oldschool.runescape.wiki/w/Woodcutting`

2. **Extract action data:**
   - Level requirements
   - XP rates (per action)
   - Resource/object names
   - Tool requirements

3. **Find cache IDs:**
   - Use OSRSWikiScraper for items
   - Check `.data/symbols/` for locs/npcs/seqs

4. **Create JSON file:**
   ```json
   {
     "skill": "skillname",
     "actions": {
       "action_name": {
         "level_req": 1,
         "xp": 25.0,
         "animation": 879
       }
     },
     "test_locations": [
       {"desc": "Description", "x": 3200, "z": 3200}
     ]
   }
   ```

5. **Validate against implementation:**
   - Ensure XP values match plugin constants
   - Ensure animation IDs match seqs.* refs
   - Ensure item IDs match objs.* refs

## Wiki Data Validation

```python
# scripts/validate_wiki_data.py (conceptual)
def validate_skill_oracle(skill_name):
    with open(f"wiki-data/skills/{skill_name}.json") as f:
        data = json.load(f)
    
    # Check required fields
    assert "skill" in data
    assert "actions" in data
    assert "test_locations" in data
    
    # Validate actions
    for name, action in data["actions"].items():
        assert "level_req" in action
        assert "xp" in action
        assert 1 <= action["level_req"] <= 99
        assert action["xp"] > 0
    
    return True
```

## See Also

- `OSRSWikiScraper/osrswikiscraper.py` for extraction tools
- `docs/LLM_TESTING_GUIDE.md` for test integration
- `wiki-data/` for existing examples
