# RSMod Wiki-Data Collection Summary

Generated: 2026-02-20

## Overview

This collection contains OSRS wiki data scraped for RSMod v2 development.

| Category | Count | Location |
|----------|-------|----------|
| Monsters | 40 | `wiki-data/monsters/*.json` |
| Skills | 0 | `wiki-data/skills/*.json` (manual) |
| Generated Code | 17 | `rsmod/content/other/npc-drops/generated/` |

## Monster Data (40 files)

### F2P Monsters (17)
Essential for F2P gameplay and early-game content.

```
goblin.json          - Combat 2, 27 drops
cow.json             - Combat 2, 4 drops  
chicken.json         - Combat 1, 5 drops
giant_rat.json       - Combat 3, 8 drops
guard.json           - Combat 21, 17 drops
man.json             - Combat 2, 14 drops
woman.json           - Combat 2, 13 drops
al_kharid_warrior.json - Combat 9, 16 drops
hill_giant.json      - Combat 28, 30 drops
moss_giant.json      - Combat 42, 30 drops
lesser_demon.json    - Combat 82, 25 drops
greater_demon.json   - Combat 92, 22 drops
black_knight.json    - Combat 33, 18 drops
dark_wizard.json     - Combat 7, 18 drops
skeleton.json        - Combat 21, 34 drops
zombie.json          - Combat 13, 27 drops
giant_spider.json    - Combat 27, 6 drops
```

### Dragon Line (6)
For mid-to-high level PvM content.

```
king_black_dragon.json - Combat 276, 28 drops (Boss)
green_dragon.json      - Combat 79, 27 drops
blue_dragon.json       - Combat 111, 30 drops
red_dragon.json        - Combat 152, 28 drops
black_dragon.json      - Combat 227, 33 drops
```

### Giants (3)
Mid-level training monsters.

```
fire_giant.json  - Combat 86, 27 drops
ice_giant.json   - Combat 53, 47 drops
hobgoblin.json   - Combat 42, 45 drops
```

### Slayer Monsters (10)
For Slayer skill implementation.

```
abyssal_demon.json   - Combat 124, 42 drops (85 Slayer)
dust_devil.json      - Combat 93, 35 drops (65 Slayer)
gargoyle.json        - Combat 111, 24 drops (75 Slayer)
nechryael.json       - Combat 115, 34 drops (80 Slayer)
bloodveld.json       - Combat 76, 25 drops (50 Slayer)
hellhound.json       - Combat 122, 9 drops (1 Slayer)
dagannoth.json       - Combat 74, 26 drops
cave_horror.json     - Combat 80, 31 drops (58 Slayer)
banshee.json         - Combat 23, 24 drops (15 Slayer)
crawling_hand.json   - Combat 8, 18 drops (5 Slayer)
```

### Other Members (4)
Miscellaneous members content.

```
jogre.json         - Combat 53, 32 drops
earth_warrior.json - Combat 51, 30 drops
```

## Data Format

### Monster JSON Schema

```json
{
  "name": "Goblin",
  "combat_level": 2,
  "hitpoints": 5,
  "attack": 1,
  "strength": 1,
  "defence": 1,
  "attack_speed": 4,
  "slayer_level": null,
  "slayer_xp": 5.0,
  "drops": [
    {
      "name": "Bones",
      "rate": "1/1",
      "qty_min": 1,
      "qty_max": 1,
      "guaranteed": true
    },
    {
      "name": "Bronze sq shield",
      "rate": "1/42",
      "qty_min": 1,
      "qty_max": 1
    }
  ]
}
```

### Field Descriptions

| Field | Type | Description |
|-------|------|-------------|
| `name` | string | Monster name |
| `combat_level` | int | Combat level |
| `hitpoints` | int | HP stat |
| `attack` | int | Attack level |
| `strength` | int | Strength level |
| `defence` | int | Defence level |
| `attack_speed` | int | Attack speed in ticks |
| `slayer_level` | int/null | Slayer requirement |
| `slayer_xp` | float/null | Slayer XP reward |
| `drops` | array | Drop table entries |
| `drops[].name` | string | Item name |
| `drops[].rate` | string | Drop rate as "num/den" |
| `drops[].qty_min` | int | Minimum quantity |
| `drops[].qty_max` | int | Maximum quantity |
| `drops[].guaranteed` | bool | Always drops |

## Generated Code

The `scripts/generate_droptables.py` tool can generate RSMod-compatible Kotlin code:

### Usage

```bash
# Single monster
python scripts/generate_droptables.py --monster goblin --wiki-dir wiki-data/monsters

# All F2P monsters
python scripts/generate_droptables.py --all-f2p --wiki-dir wiki-data/monsters --output-dir rsmod/content/other/npc-drops/generated/
```

### Output Example

```kotlin
// Auto-generated from wiki-data for Goblin
register(npcs.goblin) {
    guaranteed(objs.bones)
    drop(objs.bronze_sq_shield, 1, rate = 3)
    drop(objs.coins, qtyRange(1, 10), rate = 20)
}
```

## Tools

### OSRSWikiScraper

Location: `OSRSWikiScraper/`

Files:
- `scraper_v2.py` - Main scraper
- `export_for_rsmod.py` - Batch export tool
- `requirements.txt` - Python dependencies

Features:
- Caching (24hr)
- Rate limiting
- JSON output
- Batch export

### Drop Table Generator

Location: `scripts/generate_droptables.py`

Generates RSMod v2 Kotlin code from wiki-data JSON files.

## Implementation Priorities

### Tier 1 - Essential F2P (17 monsters)
All F2P monsters are exported and ready for implementation.

Priority order:
1. Goblin, Cow, Chicken - Tutorial/Lumbridge area
2. Giant rat, Man, Woman - Varrock area
3. Guard - Cities
4. Hill Giant, Moss Giant - Training spots
5. Lesser Demon - Wilderness
6. Skeleton, Zombie - Dungeons

### Tier 2 - Early Members (10 monsters)
Dragons, slayer monsters for members content.

### Tier 3 - Bosses (1 monster)
King Black Dragon for end-game PvM.

## Missing Data

Skills data needs to be created manually or scraped separately:

- `wiki-data/skills/woodcutting.json`
- `wiki-data/skills/mining.json`
- `wiki-data/skills/fishing.json`
- `wiki-data/skills/cooking.json`
- `wiki-data/skills/smithing.json`
- etc.

See `docs/WORK_PLAN.md` for skill implementation priorities.

## Data Source

All data scraped from: https://oldschool.runescape.wiki/

Last updated: 2026-02-20

## Next Steps

1. Implement F2P monster combat definitions (Phase 6 of WORK_PLAN)
2. Port drop tables using generated code
3. Add animation references to NPC configs
4. Test drop rates in-game
5. Add more monsters as needed (bosses, slayer creatures)

## See Also

- `docs/WORK_PLAN.md` - Implementation roadmap
- `docs/CONTENT_AUDIT.md` - Current implementation status
- `OSRSWikiScraper/README_v2.md` - Scraper documentation

