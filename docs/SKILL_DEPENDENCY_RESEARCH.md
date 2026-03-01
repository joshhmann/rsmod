# Skill Dependency & Requirements Research

**Document Purpose**: Comprehensive analysis of skill dependencies, requirements, and implementation priorities for OSRS RSMod v2.

**Last Updated**: 2026-02-26

---

## Executive Summary

This document maps the complete interdependency graph of OSRS skills, equipment requirements, resource tiers, and implementation blockers. Use this to identify what content must be implemented before other content can function.

**Key Insight**: Most artisan skills (Smithing, Crafting, Fletching, Herblore, Cooking) depend on Gathering skills (Mining, Woodcutting, Fishing) for their raw materials.

---

## 1. Skill Dependency Graph

### 1.1 Core Dependency Tree

```
                    COMBAT
                   /   |   \
                  /    |    \
           MELEE  RANGED  MAGIC
            |       |       |
            |       |       |
    +-------+-------+-------+
    |
    v
SMITHING <------ MINING <------+ 
    |                            |
    v                            |
CRAFTING <------+                |
    |           |                |
    +-----------+----------------+
    |
    v
FLETCHING <----- WOODCUTTING
    |
    v
RANGED COMBAT

FISHING ------> COOKING
    |
    +----------> HERBLORE (secondary)

FARMING ------> HERBLORE
    |
    +----------> COOKING (ingredients)

THIEVING -----> COINS/GOLD ----> SMITHING/Crafting

AGILITY ------> RUN ENERGY ----> All gathering skills

CONSTRUCTION --> WOODCUTTING (planks)
            --> MINING (limestone, etc.)

RUNECRAFTING --> MAGIC
```

### 1.2 Dependency Types

| Type | Description | Examples |
|------|-------------|----------|
| **Hard** | Cannot perform without prerequisite | Smelting requires Mining (ore source) |
| **Soft** | Can buy materials, but inefficient | Fletching can buy logs, but WC is cheaper |
| **Equipment** | Need specific tools/gear | Mining requires pickaxe of appropriate tier |
| **Location** | Content locked behind areas | Runecrafting altars in specific locations |
| **Quest** | Content locked behind quests | Rune Mysteries for runecrafting |

---

## 2. Gathering Skills Requirements

### 2.1 Woodcutting

**Tools Required:**
| Axe Type | WC Level | Attack Level (for wielding) | Notes |
|----------|----------|----------------------------|-------|
| Bronze | 1 | 1 | Starter axe |
| Iron | 1 | 1 | Slightly better than bronze |
| Steel | 6 | 5 | |
| Black | 11 | 10 | Rare drop from trees/implings |
| Mithril | 21 | 20 | |
| Adamant | 31 | 30 | |
| Rune | 41 | 40 | Best F2P axe |
| Dragon | 61 | 60 | P2P, 1 tick faster |
| Crystal | 71 | 70 | P2P, requires quest |

**Tree Requirements:**
| Tree | WC Level | Log Type | Notes |
|------|----------|----------|-------|
| Normal | 1 | Logs | F2P, basic tree |
| Oak | 15 | Oak logs | F2P |
| Willow | 30 | Willow logs | F2P, best F2P XP until 60 |
| Teak | 35 | Teak logs | P2P, hardwood |
| Maple | 45 | Maple logs | F2P (members only tree location pre-RS2) |
| Mahogany | 50 | Mahogany logs | P2P, hardwood |
| Yew | 60 | Yew logs | F2P, good money maker |
| Magic | 75 | Magic logs | P2P, best money |
| Redwood | 90 | Redwood logs | P2P, best XP |

**Content Dependencies:**
- **Fletching**: Requires logs from WC
- **Firemaking**: Requires logs from WC  
- **Construction**: Requires planks (processed logs)

---

### 2.2 Mining

**Tools Required:**
| Pickaxe | Mining Level | Attack Level | Notes |
|---------|--------------|--------------|-------|
| Bronze | 1 | 1 | Starter pickaxe |
| Iron | 1 | 1 | Better than bronze |
| Steel | 6 | 5 | |
| Black | 11 | 10 | Rare drop |
| Mithril | 21 | 20 | |
| Adamant | 31 | 30 | |
| Rune | 41 | 40 | Best F2P pickaxe |
| Dragon | 61 | 60 | P2P, 1 tick faster |
| Crystal | 71 | 70 | P2P, requires quest |

**Ore Requirements:**
| Ore | Mining Level | Smelting Level | Bar Produced | Notes |
|-----|--------------|----------------|--------------|-------|
| Clay | 1 | - | - | Used for Pottery/Crafting |
| Copper | 1 | 1 | Bronze | F2P, pairs with tin |
| Tin | 1 | 1 | Bronze | F2P, pairs with copper |
| Iron | 15 | 15 | Iron | F2P, 50% success rate |
| Silver | 20 | 20 | Silver | F2P, used in Crafting/Herblore |
| Coal | 30 | 30 | Steel+ | F2P, required for steel+ |
| Gold | 40 | 40 | Gold | F2P, used in Crafting |
| Mithril | 55 | 50 | Mithril | P2P (F2P wildy in some revs) |
| Adamantite | 70 | 70 | Adamant | P2P |
| Runite | 85 | 85 | Rune | P2P, very rare |

**Special Mining:**
| Type | Level | Output | Use |
|------|-------|--------|-----|
| Gem rocks | 40 | Uncut gems | Crafting |
| Motherlode Mine | 30 | Pay-dirt | Mining XP + Prospector outfit |
| Volcanic Mine | 50 | Various | P2P, volcanic ash for ultracompost |

**Content Dependencies:**
- **Smithing**: Requires ores/bars from Mining
- **Crafting**: Requires gold/silver from Mining
- **Construction**: Requires limestone from Mining

---

### 2.3 Fishing

**Tools Required:**
| Tool | Level | Fish Type | Notes |
|------|-------|-----------|-------|
| Small net | 1 | Shrimp, Anchovies | F2P, starter |
| Fishing rod + bait | 5 | Sardine, Herring, Pike | F2P |
| Big net | 16 | Mackerel, Cod, Bass | F2P |
| Fly fishing rod + feathers | 20 | Trout, Salmon | F2P, best F2P XP |
| Harpoon | 35 | Tuna, Swordfish | F2P |
| Lobster pot | 40 | Lobster | F2P, good money |
| Barbarian rod | 48 | Various | P2P, bare-handed fishing |

**Fish Requirements:**
| Fish | Level | Cooking Level | XP (Fish) | XP (Cook) | Heals | Notes |
|------|-------|---------------|-----------|-----------|-------|-------|
| Shrimp | 1 | 1 | 10 | 30 | 3 | F2P |
| Sardine | 5 | 1 | 20 | 40 | 4 | F2P |
| Herring | 10 | 5 | 30 | 50 | 5 | F2P |
| Anchovies | 15 | 1 | 40 | 30 | 1 | F2P, pizza topping |
| Trout | 20 | 15 | 50 | 70 | 7 | F2P |
| Salmon | 30 | 25 | 70 | 90 | 9 | F2P |
| Tuna | 35 | 30 | 80 | 100 | 10 | F2P |
| Lobster | 40 | 40 | 90 | 120 | 12 | F2P |
| Bass | 46 | 43 | 100 | 130 | 13 | F2P |
| Swordfish | 50 | 45 | 100 | 140 | 14 | F2P |
| Monkfish | 62 | 62 | 120 | 150 | 16 | P2P, Swan Song quest |
| Shark | 76 | 80 | 110 | 210 | 20 | P2P |
| Manta ray | 81 | 91 | 115 | 216 | 22 | P2P |

**Content Dependencies:**
- **Cooking**: Requires raw fish from Fishing
- **Herblore**: Some ingredients from fishing (e.g., snape grass)

---

## 3. Artisan Skills Requirements

### 3.1 Smithing

**Prerequisites:**
- **Hard Dependency**: Mining (for ores)
- **Alternative**: Buying ores/bars from shops (expensive)

**Smelting Requirements:**
| Bar | Smithing Level | Mining Level (for ore) | Coal Required | Notes |
|-----|----------------|------------------------|---------------|-------|
| Bronze | 1 | 1 (Copper+Tin) | 0 | F2P |
| Blurite | 8 | 10 (Blurite) | 0 | P2P, The Knight's Sword |
| Iron | 15 | 15 | 0 | F2P, 50% success |
| Silver | 20 | 20 | 0 | F2P, used in crafting |
| Steel | 30 | 30 (Iron) | 2 | F2P |
| Gold | 40 | 40 | 0 | F2P, used in crafting |
| Mithril | 50 | 55 | 4 | P2P |
| Adamant | 70 | 70 | 6 | P2P |
| Rune | 85 | 85 | 8 | P2P |

**Equipment Smithing Requirements:**
| Item Type | Bronze | Iron | Steel | Mithril | Adamant | Rune |
|-----------|--------|------|-------|---------|---------|------|
| Dagger | 1 | 15 | 30 | 50 | 70 | 85 |
| Sword | 4 | 19 | 34 | 54 | 74 | 89 |
| Scimitar | 5 | 20 | 35 | 55 | 75 | 90 |
| Longsword | 6 | 21 | 36 | 56 | 76 | 91 |
| Full Helm | 7 | 22 | 37 | 57 | 77 | 92 |
| Sq Shield | 8 | 23 | 38 | 58 | 78 | 93 |
| Platelegs | 16 | 31 | 46 | 66 | 86 | 99 |
| Platebody | 18 | 33 | 48 | 68 | 88 | 99 |

**Skill Dependencies:**
- **Input**: Mining (ores)
- **Output**: Combat equipment, Crafting tools

---

### 3.2 Crafting

**Sub-skills:**
1. **Pottery** (Level 1+)
2. **Leatherworking** (Level 1+)
3. **Gem Cutting** (Level 20+)
4. **Jewelry** (Level 5+)
5. **Silver/Gold Crafting** (Level 16+)
6. **Glassblowing** (Level 1+, P2P)

**Requirements by Type:**

**Pottery:**
| Item | Level | Material | Notes |
|------|-------|----------|-------|
| Pot | 1 | Soft clay | F2P, requires wheel + kiln |
| Pie Dish | 7 | Soft clay | F2P, cooking |
| Bowl | 8 | Soft clay | F2P, cooking |
| Plant Pot | 19 | Soft clay | F2P, farming |
| Pot lid | 25 | Soft clay | P2P |

**Leatherworking:**
| Item | Level | Leather Type | Hides Required |
|------|-------|--------------|----------------|
| Gloves | 1 | Normal | 1 cowhide |
| Boots | 7 | Normal | 1 cowhide |
| Cowl | 9 | Normal | 1 cowhide |
| Vambraces | 11 | Normal | 1 cowhide |
| Body | 14 | Normal | 3 cowhide |
| Chaps | 18 | Normal | 2 cowhide |
| Hardleather body | 28 | Hard leather | 1 cowhide + 10gp tanning |
| Coif | 38 | Normal | 2 cowhide |

**Gem Cutting:**
| Gem | Level | Uncut ID | Cut ID | XP (Cut) | Notes |
|-----|-------|----------|--------|----------|-------|
| Opal | 1 | 1625 | 1609 | 15 | P2P, can crush |
| Jade | 13 | 1627 | 1611 | 20 | P2P, can crush |
| Red Topaz | 16 | 1629 | 1613 | 25 | P2P, can crush |
| Sapphire | 20 | 1623 | 1607 | 50 | F2P |
| Emerald | 27 | 1621 | 1605 | 67.5 | F2P |
| Ruby | 34 | 1619 | 1603 | 85 | F2P |
| Diamond | 43 | 1617 | 1601 | 107.5 | F2P |
| Dragonstone | 55 | 1631 | 1615 | 137.5 | P2P |
| Onyx | 67 | 6571 | 6573 | 167.5 | P2P |

**Jewelry (Gold):**
| Item | Level | Bars | Gem | Result |
|------|-------|------|-----|--------|
| Gold ring | 5 | 1 | None | Ring + 15 XP |
| Gold necklace | 6 | 1 | None | Necklace + 20 XP |
| Gold amulet (u) | 8 | 1 | None | Amulet + 30 XP |
| Sapphire ring | 20 | 1 | 1 | Ring + 40 XP |
| Sapphire necklace | 22 | 1 | 1 | Necklace + 55 XP |
| Emerald ring | 27 | 1 | 1 | Ring + 55 XP |
| Ruby ring | 34 | 1 | 1 | Ring + 70 XP |
| Diamond ring | 43 | 1 | 1 | Ring + 85 XP |

**Silver Crafting:**
| Item | Level | Silver Bars | Notes |
|------|-------|-------------|-------|
| Holy symbol | 16 | 1 | Prayer bonus |
| Unholy symbol | 17 | 1 | Zamorak item |
| Sickle | 18 | 1 | P2P |
| Tiara | 23 | 1 | Runecrafting |
| Demonic sigil | 25 | 1 | Shadow of the Storm |

**Skill Dependencies:**
- **Input**: Mining (gold/silver), Thieving/Cow Killing (hides), Mining (gems)
- **Output**: Magic armor (leather), Prayer symbols, Runecrafting tiaras, Jewelry

---

### 3.3 Fletching

**Prerequisites:**
- **Hard Dependency**: Woodcutting (for logs)
- **Alternative**: Buying logs (expensive)

**Bow Fletching:**
| Bow Type | Fletching Level | Woodcutting Level | Logs Required | XP (Unstrung) | XP (Strung) |
|----------|-----------------|-------------------|---------------|---------------|-------------|
| Shortbow | 5 | 1 | Normal | 5 | 10 |
| Longbow | 10 | 1 | Normal | 10 | 20 |
| Oak shortbow | 20 | 15 | Oak | 16.5 | 33 |
| Oak longbow | 25 | 15 | Oak | 25 | 50 |
| Willow shortbow | 35 | 30 | Willow | 33.3 | 66.6 |
| Willow longbow | 40 | 30 | Willow | 41.5 | 83 |
| Maple shortbow | 50 | 45 | Maple | 50 | 100 |
| Maple longbow | 55 | 45 | Maple | 58.3 | 116.6 |
| Yew shortbow | 65 | 60 | Yew | 67.5 | 135 |
| Yew longbow | 70 | 60 | Yew | 75 | 150 |
| Magic shortbow | 80 | 75 | Magic | 83.3 | 166.6 |
| Magic longbow | 85 | 75 | Magic | 91.5 | 183 |

**Arrow Fletching:**
| Arrow Type | Level | Head Type | Shaft | Feather | XP per 15 |
|------------|-------|-----------|-------|---------|-----------|
| Headless | 1 | - | 15 | 15 | 15 |
| Bronze | 1 | Bronze | 15 | 15 | 39.5 |
| Iron | 15 | Iron | 15 | 15 | 52.5 |
| Steel | 30 | Steel | 15 | 15 | 95 |
| Mithril | 45 | Mithril | 15 | 15 | 132.5 |
| Adamant | 60 | Adamant | 15 | 15 | 165 |
| Rune | 75 | Rune | 15 | 15 | 187.5 |

**Crossbow Requirements:**
| Component | Level | Materials | Notes |
|-----------|-------|-----------|-------|
| Stock | 9 | 1 log | F2P |
| Bronze limbs | 4 | 1 bronze bar | Smithing or buy |
| Iron limbs | 23 | 1 iron bar | Smithing or buy |
| Crossbow string | 5 | 1 sinew | P2P, from hunting |

**Skill Dependencies:**
- **Input**: Woodcutting (logs), Smithing (arrowheads, bolt tips)
- **Output**: Ranged weapons, ammunition

---

### 3.4 Herblore

**Prerequisites:**
- **Hard Dependency**: Farming (herbs) or Combat (monster drops)
- **Alternative**: Buying grimy herbs (expensive)

**Herb Cleaning:**
| Herb | Level | XP (Clean) | Farming Level | Notes |
|------|-------|------------|---------------|-------|
| Guam | 3 | 2.5 | 9 | F2P, basic herb |
| Marrentill | 5 | 3.8 | 14 | F2P |
| Tarromin | 11 | 5 | 19 | F2P |
| Harralander | 20 | 6.3 | 26 | F2P |
| Ranarr | 25 | 7.5 | 32 | F2P, valuable |
| Toadflax | 30 | 8 | 38 | P2P |
| Irit | 40 | 8.8 | 44 | P2P |
| Avantoe | 48 | 10 | 50 | P2P |
| Kwuarm | 54 | 11.3 | 56 | P2P |
| Snapdragon | 59 | 11.8 | 62 | P2P, valuable |
| Cadantine | 65 | 12.5 | 67 | P2P |
| Lantadyme | 67 | 13.1 | 73 | P2P |
| Dwarf weed | 70 | 13.8 | 79 | P2P |
| Torstol | 75 | 15 | 85 | P2P, best herb |

**Potion Making:**
| Potion | Level | Primary | Secondary | XP | Effect |
|--------|-------|---------|-----------|-----|--------|
| Attack | 3 | Guam | Eye of newt | 25 | +10% Attack |
| Anti-poison | 5 | Marrentill | Unicorn horn dust | 37.5 | Cures poison |
| Strength | 12 | Tarromin | Limpwurt root | 50 | +10% Strength |
| Restore | 22 | Harralander | Red spiders' eggs | 62.5 | Restores stats |
| Energy | 26 | Harralander | Chocolate dust | 67.5 | +10% run energy |
| Defence | 30 | Ranarr | White berries | 75 | +10% Defence |
| Prayer | 38 | Ranarr | Snape grass | 87.5 | +25% Prayer |
| Super attack | 45 | Irit | Eye of newt | 100 | +15% Attack |
| Super strength | 55 | Kwuarm | Limpwurt root | 125 | +15% Strength |
| Super restore | 63 | Snapdragon | Red spiders' eggs | 142.5 | Restores + Prayer |
| Super combat | 90 | Torstol | Super set | 150 | All super stats |

**Skill Dependencies:**
- **Input**: Farming (herbs), Combat (secondaries from monsters)
- **Output**: Combat boosts, prayer restoration, utility potions

---

### 3.5 Cooking

**Prerequisites:**
- **Hard Dependency**: Fishing (for fish) or Farming (for ingredients)
- **Alternative**: Buying raw food (expensive)

**Cooking Requirements:**
| Food | Level | Heals | XP | Burn Level (no gauntlets) | Notes |
|------|-------|-------|-----|---------------------------|-------|
| Meat | 1 | 3 | 30 | 34 | Any raw meat |
| Shrimp | 1 | 3 | 30 | 34 | F2P, fishing |
| Sardine | 1 | 4 | 40 | 38 | F2P |
| Herring | 5 | 5 | 50 | 41 | F2P |
| Mackerel | 10 | 6 | 60 | 45 | F2P |
| Trout | 15 | 7 | 70 | 50 | F2P |
| Cod | 18 | 7 | 75 | 52 | F2P |
| Pike | 20 | 8 | 80 | 53 | F2P |
| Salmon | 25 | 9 | 90 | 58 | F2P |
| Tuna | 30 | 10 | 100 | 63 | F2P |
| Lobster | 40 | 12 | 120 | 74 | F2P |
| Bass | 43 | 13 | 130 | 80 | F2P |
| Swordfish | 45 | 14 | 140 | 86 | F2P |
| Monkfish | 62 | 16 | 150 | 92 | P2P |
| Shark | 80 | 20 | 210 | Never* | P2P, 100% at 80 with gauntlets |

**Cooking Locations:**
| Location | Type | Fire Bonus | F2P |
|----------|------|------------|-----|
| Fire | Player-made | None | Yes |
| Range | Static object | Less burns | Yes |
| Hosidius Kitchen | Special | 5% less burns | P2P |

**Skill Dependencies:**
- **Input**: Fishing (fish), Farming (ingredients), Hunter (meat)
- **Output**: Food for healing, Thieving (stamina)

---

## 4. Utility Skills Requirements

### 4.1 Firemaking

**Prerequisites:**
- **Hard Dependency**: Woodcutting (for logs)
- **Alternative**: Buying logs

**Firemaking Requirements:**
| Log Type | Level | XP | Woodcutting Level | Notes |
|----------|-------|-----|-------------------|-------|
| Normal | 1 | 40 | 1 | F2P |
| Oak | 15 | 60 | 15 | F2P |
| Willow | 30 | 90 | 30 | F2P, best F2P XP |
| Teak | 35 | 105 | 35 | P2P |
| Maple | 45 | 135 | 45 | F2P |
| Mahogany | 50 | 157.5 | 50 | P2P |
| Yew | 60 | 202.5 | 60 | F2P |
| Magic | 75 | 303.8 | 75 | P2P |
| Redwood | 90 | 350 | 90 | P2P |

**Skill Dependencies:**
- **Input**: Woodcutting
- **Output**: Cooking fires, Light sources (caves)

---

### 4.2 Runecrafting

**Prerequisites:**
- **Quest**: Rune Mysteries (for essence mining)
- **Soft**: Mining (for pure essence)

**Altar Requirements:**
| Rune | Level | Essence | Multiplier | Notes |
|------|-------|---------|------------|-------|
| Air | 1 | Rune/Pure | 1x | F2P, closest to bank |
| Mind | 2 | Rune/Pure | 1x | F2P |
| Water | 5 | Rune/Pure | 1x | F2P, Lumbridge swamp |
| Earth | 9 | Rune/Pure | 1x | F2P, Varrock nearby |
| Fire | 14 | Rune/Pure | 1x | F2P, Al Kharid |
| Body | 20 | Rune/Pure | 1x | F2P, Edgeville |
| Cosmic | 27 | Pure only | 2x at 59 | P2P, Lost City quest |
| Chaos | 35 | Pure only | 2x at 74 | P2P |
| Astral | 40 | Pure only | 2x at 82 | P2P, Lunar Diplomacy |
| Nature | 44 | Pure only | 2x at 91 | P2P, very profitable |
| Law | 54 | Pure only | 2x at 95 | P2P, no combat gear allowed |
| Death | 65 | Pure only | 2x at 99 | P2P, Mourning's End Pt II |
| Blood | 77 | Pure only | 2x at 99 | P2P, Sins of the Father |
| Soul | 90 | Pure only | 2x at 99 | P2P, Zeah |

**Multiplier Formula:**
- Level X: 1 rune per essence
- Level (X+11): 2 runes per essence
- Level (X+22): 3 runes per essence (some runes)

**Skill Dependencies:**
- **Input**: Mining (essence)
- **Output**: Magic spell casting

---

### 4.3 Agility

**Course Requirements:**
| Course | Level | Region | XP per lap | Marks of Grace | Notes |
|--------|-------|--------|------------|----------------|-------|
| Gnome Stronghold | 1 | Kandarin | 86.5 | Yes | P2P, starter course |
| Al Kharid | 20 | Desert | 180 | No | F2P, no marks |
| Varrock | 30 | Misthalin | 238 | No | F2P, no marks |
| Canifis | 40 | Morytania | 240 | Yes | P2P, best early marks |
| Falador | 50 | Asgarnia | 440 | Yes | P2P |
| Wilderness | 52 | Wilderness | 571.4 | No | P2P, dangerous |
| Seers' | 60 | Kandarin | 570 | Yes | P2P, diary bonus |
| Pollnivneach | 70 | Desert | 890 | Yes | P2P |
| Rellekka | 80 | Fremennik | 780 | Yes | P2P |
| Ardougne | 90 | Kandarin | 793 | Yes | P2P, best XP |

**Benefits:**
- Faster run energy restoration
- Access to shortcuts
- Marks of Grace for Graceful outfit

---

### 4.4 Thieving

**Pickpocket Requirements:**
| Target | Level | XP | Stun Time | Notes |
|--------|-------|-----|-----------|-------|
| Man/Woman | 1 | 8 | 5s | F2P |
| Farmer | 10 | 14.5 | 5s | F2P, seeds |
| Warrior | 25 | 26 | 5s | F2P |
| Rogue | 32 | 36.5 | 5s | P2P |
| Guard | 40 | 46.8 | 5s | F2P |
| Knight | 55 | 84.3 | 5s | P2P, good money |
| Paladin | 70 | 151.8 | 5s | P2P, 2x damage stun |
| Hero | 80 | 273.3 | 6s | P2P, best money |

**Stall Requirements:**
| Stall | Level | XP | Loot | Location |
|-------|-------|-----|------|----------|
| Vegetable | 2 | 10 | Onion, cabbage, potato | Lumbridge |
| Cake | 5 | 16 | Cake, chocolate slice | Ardougne/Keldagrim |
| Silk | 20 | 24 | Silk | Ardougne/Keldagrim |
| Fur | 35 | 36 | Grey wolf fur | Ardougne |
| Silver | 50 | 54 | Silver ore | Ardougne/Keldagrim |
| Spice | 65 | 81 | Spice | Ardougne |
| Gem | 75 | 160 | Uncut gems | Ardougne |

**Chest Requirements:**
| Chest | Level | XP | Loot | Notes |
|-------|-------|-----|------|-------|
| Nature rune | 28 | 25 | Coins, nature runes | Ardougne |
| 10 Coins | 13 | 7.8 | 10 coins | F2P |
| Blood runes | 59 | 250 | Blood runes, gp | HAM store room |

---

### 4.5 Farming

**Patch Types & Requirements:**
| Patch Type | Level | Products | Notes |
|------------|-------|----------|-------|
| Allotment | 1 | Vegetables | 4 patches, needs compost |
| Herb | 9 | Herbs | Most valuable, disease risk |
| Flower | 2 | Flowers | Protects allotments |
| Tree | 15 | Logs, roots | Long grow time |
| Fruit Tree | 27 | Fruit | P2P, payment protection |
| Hardwood | 35 | Teak/Mahogany | P2P, long growth |
| Bush | 48 | Berries | P2P |
| Cactus | 55 | Cactus spine | P2P |
| Mushroom | 53 | Mort myre fungus | P2P |
| Calquat | 72 | Calquat fruit | P2P |
| Spirit Tree | 83 | Spirit tree | P2P, 1 at a time |

**Herb Farming:**
| Herb | Level | XP (Plant) | XP (Harvest) | Payment | Notes |
|------|-------|------------|--------------|---------|-------|
| Guam | 9 | 11 | 12.5 | Marrentill | Basic herb |
| Marrentill | 14 | 13.5 | 15.5 | Potato cactus | |
| Tarromin | 19 | 16 | 18 | | |
| Harralander | 26 | 21.5 | 24 | | |
| Ranarr | 32 | 27 | 30.5 | | Valuable |
| Toadflax | 38 | 34 | 38.5 | | P2P |
| Irit | 44 | 43 | 48.5 | | P2P |
| Avantoe | 50 | 54.5 | 61.5 | | P2P |
| Kwuarm | 56 | 69 | 78 | | P2P |
| Snapdragon | 62 | 87.5 | 98.5 | | P2P, valuable |
| Cadantine | 67 | 106.5 | 120 | | P2P |
| Lantadyme | 73 | 134.5 | 151.5 | | P2P |
| Dwarf weed | 79 | 170.5 | 192 | | P2P |
| Torstol | 85 | 199.5 | 224.5 | | P2P, best |

**Tool Requirements:**
| Tool | Level | Use |
|------|-------|-----|
| Rake | 1 | Clear weeds |
| Seed dibber | 1 | Plant seeds |
| Watering can | 1 | Water patches |
| Spade | 1 | Harvest/remove plants |
| Gardening trowel | 1 | Fill plant pots |
| Secateurs | 1 | Cut diseased leaves |

---

### 4.6 Hunter

**Trap Types & Requirements:**
| Trap | Level | Target | Notes |
|------|-------|--------|-------|
| Bird snare | 1 | Birds | Feathers, raw bird meat |
| Butterfly net | 15 | Butterflies | P2P, released for XP |
| Box trap | 27 | Chinchompas | P2P, ranged weapon |
| Net trap | 29 | Swamp lizards | P2P, requires rope + small fishing net |
| Pitfall | 31 | Large creatures | P2P, larupia, graahk, kyatt |
| Deadfall | 33 | Boulder creatures | P2P, kebbits |

**Skill Dependencies:**
- **Output**: Chinchompas (Ranged), Salamanders (Ranged), Fur (Crafting)

---

## 5. Combat Skills Requirements

### 5.1 Equipment Tiers

**Melee Weapon Progression:**
| Tier | Attack Level | Weapon | Smithing Level | Notes |
|------|--------------|--------|----------------|-------|
| Bronze | 1 | Weapons | 1-18 | Starter |
| Iron | 1 | Weapons | 15-33 | |
| Steel | 5 | Weapons | 30-48 | |
| Black | 10 | Weapons | N/A | Monster drops only |
| Mithril | 20 | Weapons | 50-68 | P2P tiers |
| Adamant | 30 | Weapons | 70-88 | |
| Rune | 40 | Weapons | 85-99 | Best F2P |
| Dragon | 60 | Weapons | N/A | P2P, monster drops/quest |
| Barrows | 70 | Weapons | N/A | P2P, degradable |
| Godsword | 75 | Weapons | N/A | P2P, expensive |

**Armor Progression:**
| Tier | Defence Level | Armor | Smithing Level | Notes |
|------|---------------|-------|----------------|-------|
| Bronze | 1 | All | 1-18 | |
| Iron | 1 | All | 15-33 | |
| Steel | 5 | All | 30-48 | |
| Black | 10 | All | N/A | Monster drops |
| Mithril | 20 | All | 50-68 | |
| Adamant | 30 | All | 70-88 | |
| Rune | 40 | All | 85-99 | Best F2P |
| Dragon | 60 | Chain/Plate | N/A | P2P |
| Barrows | 70 | Sets | N/A | P2P, degradable |

---

## 6. Quest-Locked Content

### 6.1 Skills Requiring Quests

| Skill | Quest | Level Unlocked | Content |
|-------|-------|----------------|---------|
| Runecrafting | Rune Mysteries | 1 | Mine essence, use altars |
| Agility | - | 1 | Gnome Stronghold course |
| Herblore | Druidic Ritual | 3 | Clean herbs, make potions |
| Thieving | - | 1 | All content |
| Slayer | - | 1 | Tasks from Turael |
| Hunter | - | 1 | Bird snares |
| Farming | - | 1 | Allotments |
| Construction | - | 1 | Basic room building |

### 6.2 Quest-Locked Equipment

| Equipment | Quest | Skill Required | Notes |
|-----------|-------|----------------|-------|
| Dragon axe | - | 61 WC | 1 tick faster |
| Dragon pickaxe | - | 61 Mining | 1 tick faster |
| Crystal tools | Roving Elves | 70+ | Best tools |
| Barrelchest anchor | The Great Brain Robbery | 60 Str | 2h weapon |
| Ancient mace | Another Slice of H.A.M. | 15 Prayer | Prayer restore spec |
| Helm of neitiznot | The Fremennik Isles | 55 Def | Best melee helm |

---

## 7. Implementation Priority Matrix

### 7.1 Phase 1: Core Foundation (Week 1-2)
**Must implement first - other skills depend on these:**

1. **Mining** (Ores for Smithing, Gold for Crafting)
   - All ore rocks (Copper, Tin, Iron, Silver, Coal, Gold, Mithril, Adamant, Runite)
   - All pickaxe tiers
   - Gem rocks

2. **Woodcutting** (Logs for Fletching, Firemaking)
   - All tree types (Normal, Oak, Willow, Maple, Yew, Magic)
   - All axe tiers

3. **Fishing** (Fish for Cooking)
   - All fish types
   - All fishing methods (Net, Rod, Fly, Harpoon, Cage)

### 7.2 Phase 2: Artisan Skills (Week 3-4)
**Depend on Phase 1:**

4. **Smithing** (Equipment for Combat)
   - All bar smelting (Bronze, Iron, Steel, Mithril, Adamant, Rune)
   - Essential equipment (Weapons, Armor)
   - Arrowheads for Fletching

5. **Cooking** (Food for survival)
   - All fish cooking
   - Fire making
   - Range cooking

6. **Crafting** (Armor, Jewelry, Magic items)
   - Leatherworking (armor for Ranged)
   - Gem cutting
   - Jewelry (rings, amulets)
   - Silver crafting (Holy symbols for Prayer)

### 7.3 Phase 3: Advanced Artisan (Week 5-6)
**Depend on Phase 1-2:**

7. **Fletching** (Ranged weapons)
   - All bow types
   - Arrow making
   - Crossbows

8. **Herblore** (Potions for Combat)
   - All herb cleaning
   - Essential potions (Attack, Strength, Defence, Prayer, Restore)

9. **Firemaking** (Utility)
   - All log burning
   - Light sources

### 7.4 Phase 4: Utility Skills (Week 7-8)

10. **Runecrafting** (Magic support)
    - All F2P altars (Air, Mind, Water, Earth, Fire, Body)
    - P2P altars (Cosmic, Chaos, Nature, Law, etc.)

11. **Agility** (Movement)
    - Rooftop courses
    - Shortcuts

12. **Thieving** (Money making, resources)
    - Pickpocketing
    - Stalls
    - Chests

### 7.5 Phase 5: Advanced Gathering (Week 9-10)

13. **Farming** (Herbs, ingredients)
    - Allotments
    - Herbs
    - Trees

14. **Hunter** (Ranged ammo, fur)
    - Traps
    - Chinchompas
    - Kebbits

15. **Slayer** (Combat diversity)
    - Task system
    - Slayer masters
    - Slayer monsters

---

## 8. Cross-Skill Resource Flow

### 8.1 Complete Resource Chain Example: Ranged Combat

```
WOODCUTTING (Yew logs, Level 60)
    |
    v
FLETCHING (Yew longbow, Level 70)
    |
    v
SMITHING (Rune arrowheads, Level 75) <-- MINING (Runite ore, Level 85)
    |                                        |
    |                                        v
    +------> FLETCHING (Rune arrows, Level 75) <--- FEATHERS (Hunter/Chickens)
                  |
                  v
            RANGED COMBAT (Level 40 Ranged to wield)
```

### 8.2 Magic Combat Chain

```
MINING (Rune/Pure essence, Level 1/30)
    |
    v
RUNECRAFTING (Nature runes, Level 44) <-- Rune Mysteries quest
    |
    v
MAGIC (High Alchemy, Level 55)
    |
    v
COINS <-- SMITHING (Rune platebody, Level 99)
```

### 8.3 Melee Combat Chain

```
MINING (Iron ore, Level 15) + WOODCUTTING (For firemaking/house)
    |                                   |
    v                                   v
SMITHING (Steel scimitar, Level 35)  FIREMAKING (Fire for cooking)
    |                                   |
    v                                   v
MELEE COMBAT (Level 5 Attack)        COOKING (Food for healing)
```

---

## 9. Recommended Task Creation

### 9.1 Immediate Tasks (High Priority)

Based on dependencies, create tasks in this order:

1. **SKILL-WC-FOUNDATION**: All axes + all trees (F2P trees first)
2. **SKILL-MINE-FOUNDATION**: All pickaxes + all ore rocks (F2P ores first)
3. **SKILL-FISH-FOUNDATION**: All fishing spots + all fish
4. **SKILL-SMITH-SMELTING**: All bar smelting recipes
5. **SKILL-COOK-FOUNDATION**: All fish cooking + fires
6. **SKILL-CRAFT-LEATHER**: Basic leather armor
7. **SKILL-FLETCH-BOWS**: All bow types
8. **SKILL-HERB-POTIONS**: Essential combat potions

### 9.2 Blocker Analysis

| Task | Blocked By | Blocks |
|------|------------|--------|
| Smithing equipment | Mining (ores) | Combat, Fletching (arrowheads) |
| Crafting jewelry | Mining (gold), Crafting (gems) | Magic (amulets) |
| Fletching arrows | Smithing (arrowheads), WC (logs) | Ranged combat |
| Herblore potions | Farming/Herblore (herbs), Combat (2nds) | Combat efficiency |
| Runecrafting | Mining (essence), Quest (Rune Mysteries) | Magic casting |

---

## 10. Conclusion

**Key Takeaways:**
1. **Gathering skills are the foundation** - Mining, Woodcutting, and Fishing must be implemented before artisan skills
2. **Smithing is critical for equipment** - Without it, players can't get arrowheads for Fletching or armor for Combat
3. **Quest locks are minimal for F2P** - Most F2P content has no quest requirements
4. **P2P adds complexity** - Members content has more interdependencies and quest locks

**Recommended Implementation Order:**
1. Gathering (Mining, Woodcutting, Fishing)
2. Processing (Smithing, Cooking, Crafting basics)
3. Advanced Artisan (Fletching, Herblore, Crafting advanced)
4. Utility (Firemaking, Runecrafting, Agility)
5. Advanced (Farming, Hunter, Slayer, Construction)

---

**Document Author**: kimi
**Created**: 2026-02-26
**Status**: Research Complete - Ready for task generation

