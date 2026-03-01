# NPC & Drop Data Lookup Methods

## Summary of Available Tools

We have **4 different methods** to get NPC/item/drop data, each with pros/cons:

---

## Method 1: MCP Cache Tools (Recommended for quick lookups)

**Tools available:**
- `search_npctypes` - Search NPC definitions
- `search_objtypes` - Search item/object definitions  
- `search_seqtypes` - Search animation sequences
- `search_spottypes` - Search spot animations
- `search_loctypes` - Search location/object definitions
- `search_soundtypes` - Search sound effects
- `osrs_wiki_search` / `osrs_wiki_parse_page` - Get wiki page context from cache-backed MCP

**Best for:**
- Quick symbol name → ID lookups
- Verifying if an NPC/item exists in rev 233
- Finding animation IDs

**Example:**
```python
# Find Hill Giant NPC IDs
search_npctypes(query="hill giant", pageSize=10)
# Returns: wilderness_hill_giant (13502), wilderness_hill_giant2 (13503), etc.

# Find Big Bones item ID
search_objtypes(query="big bones", pageSize=5)
# Returns: big_bones (532)
```

**Limitations:**
- Not all wiki-derived fields are normalized; verify final IDs/symbols in `rsmod/.data/symbols/*.sym`

---

## Method 2: Kronos JSON Files (Best for combat stats)

**Location:** `Kronos-184-Fixed/Kronos-master/kronos-server/data/npcs/`

**Files:**
- `combat/*.json` - Combat stats, HP, animations, attack speed
- `drops/eco/*.json` - Drop tables with item IDs
- `spawns/**/*.json` - NPC spawn locations

**Best for:**
- Combat stats (attack, strength, defence levels)
- Animation IDs (attack, death, defend)
- Attack speed/ticks
- Aggressive level

**Example:**
```json
// Kronos-184-Fixed/Kronos-master/kronos-server/data/npcs/combat/Hill_giant.json
{
  "ids": [2098, 2099, 2100, 2101, 2102, 2103, 7261],
  "hitpoints": 35,
  "attack": 18,
  "strength": 22,
  "defence": 26,
  "attack_ticks": 4,
  "death_ticks": 5,
  "attack_animation": 4652,
  "defend_animation": 4651,
  "death_animation": 4653
}
```

**Limitations:**
- Uses rev 184 IDs (need mapping to rev 233)
- Drop tables use old item IDs

---

## Method 3: wiki-data JSON Files (Best for drops)

**Location:** `wiki-data/monsters/*.json`

**Best for:**
- Drop rates (OSRS Wiki accurate)
- Drop quantities
- Tertiary drops

**Example:**
```json
// wiki-data/monsters/hill_giant.json
{
  "name": "Hill Giant",
  "combat_level": 28,
  "drops": [
    {"name": "Big bones", "rate": "1/1", "qty_min": 1, "qty_max": 1},
    {"name": "Giant key", "rate": "1/128", "qty_min": 1, "qty_max": 1}
  ]
}
```

**Limitations:**
- Uses item names (need mapping to rev 233 symbols)
- No animation data
- No attack speed

---

## Method 4: RSMod Symbol Files (Ground truth for IDs)

**Location:** `rsmod/.data/symbols/*.sym`

**Key files:**
- `npc.sym` - NPC internal names → IDs
- `obj.sym` - Item internal names → IDs
- `loc.sym` - Object internal names → IDs
- `seq.sym` - Animation internal names → IDs

**Best for:**
- Final verification of rev 233 IDs
- Finding exact internal names for Kotlin code

**Example:**
```
// npc.sym
7261    kourend_hillgiant
13502   wilderness_hill_giant

// obj.sym
532     big_bones
526     bones
```

**Limitations:**
- Text files (need parsing/indexing)
- No drop/combat data

---

## Recommended Workflow

### For New NPC Drop Tables:

1. **Get drop data from wiki-data** (most accurate rates)
   ```
   Read wiki-data/monsters/<npc_name>.json
   ```

2. **Get combat stats from Kronos** (animations, attack speed)
   ```
   Read Kronos-184-Fixed/.../npcs/combat/<NpcName>.json
   ```

3. **Map item names to rev 233 symbols** using MCP:
   ```python
   search_objtypes(query="item name from wiki")
   ```

4. **Verify NPC symbols** using MCP:
   ```python
   search_npctypes(query="npc name")
   ```

5. **Generate Kotlin** drop table file

---

## Quick Reference: Common F2P NPCs

| NPC | Wiki Data | Kronos Combat | Rev 233 Symbol |
|-----|-----------|---------------|----------------|
| Hill Giant | ✅ | ✅ | `kourend_hillgiant` (7261) |
| Moss Giant | ✅ | ✅ | `mossgiant` (?) |
| Goblin | ✅ | ✅ | `goblin` (3029) |
| Cow | ✅ | ✅ | `cow` (2791) |
| Chicken | ✅ | ✅ | `chicken` (?) |
| Giant Rat | ✅ | ✅ | `giant_rat` (?) |
| Guard | ✅ | ✅ | `guard` (?) |
| Skeleton | ✅ | ✅ | `skeleton_unarmed` (70) |
| Zombie | ✅ | ✅ | `zombie_unarmed` (26) |

---

## Proposed Unified Tool

A Python tool that combines all 4 methods:

```python
# tools/npc_lookup.py

# 1. Query all sources
npc_data = lookup_npc("Hill Giant")

# Returns merged data:
{
  "name": "Hill Giant",
  "rev233": {
    "symbol": "kourend_hillgiant",
    "id": 7261,
    "variants": ["wilderness_hill_giant", "hillgiant_boss"]
  },
  "combat": {
    "hp": 35,
    "attack": 18,
    "strength": 22,
    "defence": 26,
    "attack_speed": 4,
    "animations": {
      "attack": 4652,
      "defend": 4651,
      "death": 4653
    }
  },
  "drops": [
    {"item": "big_bones", "rate": "1/1", "qty": 1},
    {"item": "giant_key", "rate": "1/128", "qty": 1}
  ]
}
```

This would be the **single source of truth** for NPC implementation.

