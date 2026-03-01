# OSRS Rev 233 Data Extraction - Deployment Summary

**Date**: 2026-02-22  
**Operation**: Massive parallel data extraction from OSRS Rev 233 cache  
**Total Agents Deployed**: 25 subagents  

---

## Deployment Overview

All agents are running in parallel using the `deep` category with MCP OSRS lookup skills. Each agent is extracting comprehensive data from the cache files and saving to `wiki-data/` directory.

---

## Skill Extraction Agents (13 agents)

| Task ID | Agent | Description | Output File |
|---------|-------|-------------|-------------|
| `bg_55d0261f` | Deep Agent | Mining data | `skills/mining-complete.json` |
| `bg_bff331c7` | Deep Agent | Woodcutting data | `skills/woodcutting-complete.json` |
| `bg_c5f4070b` | Deep Agent | Fishing data | `skills/fishing-complete.json` |
| `bg_e018a328` | Deep Agent | Cooking data | `skills/cooking-complete.json` |
| `bg_8b321b98` | Deep Agent | Firemaking data | `skills/firemaking-complete.json` |
| `bg_a9d61ff5` | Deep Agent | Smithing data | `skills/smithing-complete.json` |
| `bg_a5030a77` | Deep Agent | Crafting data | `skills/crafting-complete.json` |
| `bg_7afddd81` | Deep Agent | Fletching data | `skills/fletching-complete.json` |
| `bg_27d15911` | Deep Agent | Herblore data | `skills/herblore-complete.json` |
| `bg_84d0da3b` | Deep Agent | Thieving data | `skills/thieving-complete.json` |
| `bg_79c2df17` | Deep Agent | Prayer data | `skills/prayer-complete.json` |
| `bg_b9ff75e5` | Deep Agent | Runecrafting data | `skills/runecrafting-complete.json` |
| `bg_d3665f1f` | Deep Agent | Agility data | `skills/agility-complete.json` |

### Skill Data Captured:
- **Tools/Equipment**: Item IDs, animations, requirements
- **Resources**: Ores, logs, fish, herbs, runes, etc.
- **XP Rates**: Per action XP values
- **Level Requirements**: Skill levels needed
- **Locations**: Object loc IDs, spawn coordinates
- **Animations**: All skill-related animations

---

## NPC Combat Agents (2 agents)

| Task ID | Agent | Description | Output File |
|---------|-------|-------------|-------------|
| `bg_208afa45` | Deep Agent | F2P NPC combat | `monsters/f2p-combat.json` |
| `bg_befb9e95` | Deep Agent | P2P NPC combat | `monsters/p2p-combat.json` |

### NPC Data Captured:
- **Combat Stats**: HP, Attack, Strength, Defence
- **Aggression**: Whether NPC is aggressive
- **Drops**: Item IDs, quantities, drop rates
- **Spawn Locations**: Coordinates
- **Slayer Info**: Slayer level req, XP
- **Animations**: Attack, death, block

---

## Quest & Story Agents (1 agent)

| Task ID | Agent | Description | Output File |
|---------|-------|-------------|-------------|
| `bg_a0b15e8b` | Deep Agent | Quest data | `quests/quest-data.json` |

### Quest Data Captured:
- **Quest IDs**: Internal quest identifiers
- **Stages**: All quest stage varps
- **Requirements**: Items, levels, quests needed
- **Rewards**: XP rewards, quest points, items
- **NPCs**: Involved NPC IDs
- **Locations**: Start/end coordinates
- **Items**: Quest-specific item IDs

---

## Shop & Minigame Agents (2 agents)

| Task ID | Agent | Description | Output File |
|---------|-------|-------------|-------------|
| `bg_9e8b7ace` | Deep Agent | Shop data | `shops/shop-data.json` |
| `bg_68259c6c` | Deep Agent | Minigame data | `minigames/minigame-data.json` |

### Shop Data Captured:
- **Shop Types**: General, weapon, armor, food, rune
- **Inventory**: Items sold with prices
- **Restock Rates**: Item restock times
- **NPC Owners**: Shopkeeper IDs
- **Locations**: Shop coordinates

### Minigame Data Captured:
- **Locations**: Minigame entry points
- **Requirements**: Levels, items needed
- **Rewards**: Items, XP, unlocks
- **Mechanics**: Gameplay rules

---

## Location & Map Agents (1 agent)

| Task ID | Agent | Description | Output File |
|---------|-------|-------------|-------------|
| `bg_65658f1b` | Deep Agent | Map data | `locations/map-data.json` |

### Location Data Captured:
- **Cities**: All major city regions
- **Dungeons**: Underground areas
- **Coordinates**: Entry/exit points
- **Multi-combat**: Multi zones
- **Wilderness**: Wilderness levels

---

## UI & Interface Agents (1 agent)

| Task ID | Agent | Description | Output File |
|---------|-------|-------------|-------------|
| `bg_d978ff04` | Deep Agent | UI data | `ui/interface-data.json` |

### UI Data Captured:
- **Interfaces**: Bank, shop, trade, stats
- **Components**: Buttons, sprites, text
- **Overlays**: Minimap, XP drops

---

## Audio & Music Agents (1 agent)

| Task ID | Agent | Description | Output File |
|---------|-------|-------------|-------------|
| `bg_45a84b99` | Deep Agent | Music data | `audio/music-data.json` |

### Audio Data Captured:
- **Music Tracks**: All music IDs
- **Unlock Locations**: Where tracks unlock
- **Sound Effects**: Combat, skill, UI
- **Jingles**: Level up, quest complete

---

## Achievement & Mechanics Agents (2 agents)

| Task ID | Agent | Description | Output File |
|---------|-------|-------------|-------------|
| `bg_2a280324` | Deep Agent | Achievement diaries | `achievements/diary-data.json` |
| `bg_e97fe2e4` | Deep Agent | Game mechanics | `mechanics/game-mechanics.json` |

### Achievement Data Captured:
- **Diaries**: All achievement diaries
- **Tasks**: Easy/Medium/Hard/Elite
- **Requirements**: Skills, quests, items
- **Rewards**: Items, XP lamps

### Mechanics Data Captured:
- **Combat**: Formulas, styles, prayers
- **Random Events**: NPCs, items, triggers
- **Status Effects**: Poison, venom
- **Banking**: Booth/chest IDs
- **Death**: Death mechanics
- **Wilderness**: PvP rules

---

## Item & Object Agents (4 agents)

| Task ID | Agent | Description | Output File |
|---------|-------|-------------|-------------|
| `bg_f27ea6fd` | Deep Agent | Equipment stats | `items/equipment-stats.json` |
| `bg_38bc2e3e` | Deep Agent | Consumables | `items/consumables.json` |
| `bg_ea15fab4` | Deep Agent | Key objects | `objects/key-objects.json` |
| `bg_a5c8532e` | Deep Agent | Magic spells | `skills/magic-spells.json` |

### Item Data Captured:
- **Equipment**: Weapons, armor stats
- **Consumables**: Food, potions, effects
- **Key Objects**: Doors, ladders, banks
- **Spells**: All magic spells

---

## System Reference Agents (2 agents)

| Task ID | Agent | Description | Output File |
|---------|-------|-------------|-------------|
| `bg_14d6c5ac` | Deep Agent | Variable reference | `vars/variable-reference.json` |
| `bg_0e4867dc` | Deep Agent | Master index | `MASTER-INDEX.md` |

### System Data Captured:
- **VARPs**: Player variables
- **VARBITs**: Variable bits
- **Quest Vars**: Quest progress tracking

---

## Output Directory Structure

```
wiki-data/
├── skills/
│   ├── mining-complete.json
│   ├── woodcutting-complete.json
│   ├── fishing-complete.json
│   ├── cooking-complete.json
│   ├── firemaking-complete.json
│   ├── smithing-complete.json
│   ├── crafting-complete.json
│   ├── fletching-complete.json
│   ├── herblore-complete.json
│   ├── thieving-complete.json
│   ├── prayer-complete.json
│   ├── runecrafting-complete.json
│   ├── agility-complete.json
│   └── magic-spells.json
├── monsters/
│   ├── f2p-combat.json
│   └── p2p-combat.json
├── quests/
│   └── quest-data.json
├── shops/
│   └── shop-data.json
├── minigames/
│   └── minigame-data.json
├── locations/
│   └── map-data.json
├── ui/
│   └── interface-data.json
├── audio/
│   └── music-data.json
├── achievements/
│   └── diary-data.json
├── mechanics/
│   └── game-mechanics.json
├── items/
│   ├── equipment-stats.json
│   └── consumables.json
├── objects/
│   └── key-objects.json
├── vars/
│   └── variable-reference.json
└── MASTER-INDEX.md
```

---

## MCP Tools Used

All agents are utilizing these MCP tools:

| Tool | Purpose |
|------|---------|
| `osrs-cache_search_objtypes` | Items, equipment, consumables |
| `osrs-cache_search_npctypes` | NPCs, monsters, shopkeepers |
| `osrs-cache_search_loctypes` | Objects, trees, rocks, obstacles |
| `osrs-cache_search_seqtypes` | Animations, emotes |
| `osrs-cache_search_iftypes` | UI interfaces |
| `osrs-cache_search_varptypes` | Player variables |
| `osrs-cache_search_varbittypes` | Variable bits |
| `osrs-cache_search_soundtypes` | Music, sounds |
| `osrs-wiki-rev233_get_npc` | NPC stats |
| `osrs-wiki-rev233_get_item` | Item stats |

---

## Expected Completion

Each agent typically completes in 5-15 minutes depending on data complexity. All 25 agents are running in parallel.

**Total Expected Data Points**: 10,000+ entries across all files

---

## Monitoring Progress

To check individual agent progress:
```bash
background_output(task_id="bg_XXXXXXXX")
```

To check all active agents:
```bash
background_output(task_id="bg_55d0261f")  # Mining
background_output(task_id="bg_bff331c7")  # Woodcutting
... etc
```

---

## Notes

- All agents are using `category: deep` for thorough extraction
- All agents equipped with `mcp-osrs-lookup` skill
- Some agents also equipped with specialized skills (rsmod-wiki-oracle, rsmod-npc-combat-definer)
- Data is being saved to version-controlled JSON files
- MASTER-INDEX will compile all results when complete
