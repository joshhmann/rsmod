# RSMod Wiki-Data Monster Index

Auto-generated from OSRS Wiki on 2026-02-20.

## F2P Monsters (17)

| Monster | Combat | HP | Slayer Lvl | Drops |
|---------|--------|-----|-----------|-------|
| Goblin | 2 | 5 | 1 | 27 |
| Cow | 2 | 8 | 1 | 4 |
| Chicken | 1 | 3 | 1 | 5 |
| Giant rat | 3 | 5 | 1 | 8 |
| Guard | 21 | 22 | 1 | 17 |
| Man | 2 | 7 | 1 | 14 |
| Woman | 2 | 7 | 1 | 13 |
| Al Kharid warrior | 9 | 19 | 1 | 16 |
| Hill Giant | 28 | 35 | 1 | 30 |
| Moss Giant | 42 | 60 | 1 | 30 |
| Lesser demon | 82 | 79 | 1 | 25 |
| Greater demon | 92 | 87 | 1 | 22 |
| Black Knight | 33 | 42 | 1 | 18 |
| Dark wizard | 7 | 12 | 1 | 18 |
| Skeleton | 21 | 24 | 1 | 34 |
| Zombie | 13 | 22 | 1 | 27 |
| Giant spider | 27 | 32 | 1 | 6 |

## Dragons (6)

| Monster | Combat | HP | Max Hit | Drops |
|---------|--------|-----|---------|-------|
| King Black Dragon | 276 | 240 | 70 | 28 |
| Green dragon | 79 | 75 | 8 | 27 |
| Blue dragon | 111 | 105 | 10 | 30 |
| Red dragon | 152 | 140 | 14 | 28 |
| Black dragon | 227 | 190 | 21 | 33 |

## Giants (3)

| Monster | Combat | HP | Max Hit | Drops |
|---------|--------|-----|---------|-------|
| Fire giant | 86 | 111 | 11 | 27 |
| Ice giant | 53 | 70 | 7 | 47 |
| Hobgoblin | 42 | 49 | 5 | 45 |

## Slayer Monsters (10)

| Monster | Combat | HP | Slayer Lvl | Slayer XP | Drops |
|---------|--------|-----|-----------|-----------|-------|
| Abyssal demon | 124 | 150 | 85 | 150 | 42 |
| Dust devil | 93 | 105 | 65 | 105 | 35 |
| Gargoyle | 111 | 105 | 75 | 105 | 24 |
| Nechryael | 115 | 105 | 80 | 105 | 34 |
| Bloodveld | 76 | 120 | 50 | 120 | 25 |
| Hellhound | 122 | 116 | 1 | 116 | 9 |
| Dagannoth | 74 | 85 | 1 | 85 | 26 |
| Cave horror | 80 | 55 | 58 | 55 | 31 |
| Banshee | 23 | 22 | 15 | 22 | 24 |
| Crawling Hand | 8 | 16 | 5 | 16 | 18 |

## Other Members Monsters (4)

| Monster | Combat | HP | Slayer Lvl | Drops |
|---------|--------|-----|-----------|-------|
| Jogre | 53 | 60 | 1 | 32 |
| Earth warrior | 51 | 54 | 1 | 30 |

## Total: 40 Monster Files

## Usage in RSMod

Reference these files when implementing:

1. **NPC Combat Stats** - Use `hitpoints`, `attack`, `strength`, `defence`, `attack_speed`
2. **Drop Tables** - Use `drops` array with `rate` (format: "1/128"), `qty_min`, `qty_max`
3. **Slayer Integration** - Use `slayer_level` and `slayer_xp`

### Example: Porting to RSMod v2

```kotlin
// In NPC param config (.toml)
params.hitpoints = 5
params.attack = 1
params.strength = 1
params.defence = 1
params.attack_speed = 4

// In drop table plugin
register(npcs.goblin) {
    guaranteed(objs.bones)
    drop(objs.bronze_sq_shield, 1, rate = 3)  // 3/128
    drop(objs.coins, qtyRange(1, 10), rate = 20)
}
```

## Data Source

All data scraped from https://oldschool.runescape.wiki/

See `OSRSWikiScraper/` for the scraping tools.
