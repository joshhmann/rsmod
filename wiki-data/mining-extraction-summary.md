# Mining Data Extraction Summary

**Date**: 2026-02-22  
**Source**: OSRS Rev 233 Cache (via MCP)  
**Output**: `mining-complete.json`

---

## Extracted Data Overview

### 1. Pickaxes (7 types)
| Type | Item ID | Level Req | Animation | Animation Wall | Broken ID |
|------|---------|-----------|-----------|----------------|-----------|
| Bronze | 1265 | 1 | 625 | 6753 | 468 |
| Iron | 1267 | 1 | 626 | 6754 | 470 |
| Steel | 1269 | 6 | 627 | 6755 | 472 |
| Mithril | 1273 | 21 | 629 | 6757 | 474 |
| Adamant | 1271 | 31 | 628 | 6756 | 476 |
| Rune | 1275 | 41 | 624 | 6752 | 478 |
| Dragon | 11920 | 61 | 7139 | 6758 | 11923 |

**Additional pickaxe data captured:**
- Pickaxeheads for random event repair (IDs 480-490)
- Certificate IDs for all pickaxes
- "No reach forward" animation variants (for closer rocks)
- Dragon pickaxe special variants (pretty version, upgrade kit)

---

### 2. Ores (11 types)
| Ore | Item ID | Level | XP | Respawn Ticks | Rock IDs |
|-----|---------|-------|-----|---------------|----------|
| Clay | 434 | 1 | 5.0 | 2 | 11362, 11363 |
| Copper | 436 | 1 | 17.5 | 4 | 10079, 10943, 11161 |
| Tin | 438 | 1 | 17.5 | 4 | 10080, 11360, 11361 |
| Iron | 440 | 15 | 35.0 | 9 | 11364, 11365 |
| Silver | 442 | 20 | 40.0 | 100 | 11368, 11369 |
| Coal | 453 | 30 | 50.0 | 50 | 11366, 11367 |
| Gold | 444 | 40 | 65.0 | 100 | 11370, 11371 |
| Mithril | 447 | 55 | 80.0 | 200 | 11372, 11373 |
| Adamantite | 449 | 70 | 95.0 | 400 | (none found) |
| Runite | 451 | 85 | 125.0 | 1200 | 11376, 11377 |

---

### 3. Rock Location IDs (35 variants)
Captured loc IDs for all rock types including:
- Tutorial island rocks (newbiecopperrock, newbietinrock)
- Standard rocks (copperrock1, copperrock2, etc.)
- Prifddinas variants (prif_mine_*)
- Group Ironman variants (gim_*)

---

### 4. Animations (24 total)
Each pickaxe has 3-6 animation variants:
- **Standard**: Regular mining animation
- **Wall**: For mining wall-based rocks
- **No-reach**: For rocks that don't require reaching forward
- **Pretty** (Dragon only): Special gilded version animation

---

### 5. Gems (9 types)
| Gem | Uncut ID | Level Req |
|-----|----------|-----------|
| Opal | 1625 | 1 |
| Jade | 1627 | 13 |
| Red Topaz | 1629 | 35 |
| Sapphire | 1623 | 20 |
| Emerald | 1621 | 27 |
| Ruby | 1619 | 34 |
| Diamond | 1617 | 43 |
| Dragonstone | 1631 | 55 |
| Onyx | 6571 | 67 |

**Related:**
- Crushed gemstone: 1633
- Gem bags: 12020, 24481, 19473, 24853

---

### 6. Related Items
- Mining helmets: 5013 (lit), 5014 (unlit)
- Coal bag: 12019
- Juniper charcoal: 13570
- Soft clay: 1761
- Pickaxe handle: 466

---

### 7. Test Locations (8 spots)
- Lumbridge Swamp (copper/tin)
- Dwarven Mine (iron/coal)
- Al Kharid (iron, coal, gold, mithril, adamant)
- Mining Guild entrance (coal)
- Rimmington (copper, tin, iron, gold)
- Varrock SE (copper, tin, iron)
- Falador (copper, tin, iron, coal)
- Crafting Guild (clay, silver, gold)

---

## Files Created

1. **mining-complete.json** - Comprehensive mining reference (327 lines)
2. **mining-extraction-summary.md** - This file

---

## Usage

Reference from Kotlin:
```kotlin
// Access pickaxe data
val bronzePickaxe = miningData.pickaxes["bronze"]
val animId = bronzePickaxe?.animation // 625

// Access ore data
val ironOre = miningData.ores["iron"]
val xp = ironOre?.xp // 35.0

// Access rock loc IDs
val copperRocks = miningData.rockLocations["copper"]?.loc_ids
```

---

## Total Records

| Category | Count |
|----------|-------|
| Pickaxe types | 7 |
| Ore types | 11 |
| Rock variants | 35 |
| Animation IDs | 24 |
| Gem types | 9 |
| Test locations | 8 |
| **Total data points** | **100+** |
