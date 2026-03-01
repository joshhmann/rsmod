# Complete OSRS Content Dependency Map

**Document Purpose**: Exhaustive mapping of ALL content dependencies including skills, combat, quests, equipment, tools, and item requirements.

**Last Updated**: 2026-02-26

---

## Table of Contents

1. [Dependency Type Legend](#dependency-type-legend)
2. [Combat Skills Full Requirements](#combat-skills-full-requirements)
3. [Complete Skill-Quest-Equipment Web](#complete-skill-quest-equipment-web)
4. [Tool Requirements Matrix](#tool-requirements-matrix)
5. [Quest Dependency Chains](#quest-dependency-chains)
6. [Equipment Progression Trees](#equipment-progression-trees)
7. [Resource-to-Combat Chains](#resource-to-combat-chains)
8. [F2P vs P2P Content Gates](#f2p-vs-p2p-content-gates)
9. [Implementation Task Priority Matrix](#implementation-task-priority-matrix)

---

## 1. Dependency Type Legend

| Symbol | Type | Description |
|--------|------|-------------|
| **HARD** | Hard Dependency | Cannot use/perform without this |
| **SOFT** | Soft Dependency | Can bypass with gold/GE but inefficient |
| **EQUIP** | Equipment Requirement | Need specific gear equipped |
| **QUEST** | Quest Lock | Cannot access without quest completion |
| **LEVEL** | Level Requirement | Skill or combat level needed |
| **TOOL** | Tool Requirement | Need item in inventory/toolbelt |
| **LOC** | Location Lock | Content in specific area |
| **DIARY** | Achievement Diary | Requires diary completion |

---

## 2. Combat Skills Full Requirements

### 2.1 Attack Skill

**What Attack Enables:**
| Attack Level | Unlocks | Skill Dependency |
|--------------|---------|------------------|
| 1 | Bronze weapons | Mining 1, Smithing 1 |
| 1 | Iron weapons | Mining 15, Smithing 15 |
| 5 | Steel weapons | Mining 30, Smithing 30 |
| 10 | Black weapons | **DROP ONLY** (no smithing) |
| 20 | Mithril weapons | Mining 55, Smithing 50 |
| 30 | Adamant weapons | Mining 70, Smithing 70 |
| 40 | Rune weapons | Mining 85, Smithing 85 |
| 50 | Granite maul | **DROP ONLY** (gargoyles) |
| 60 | Dragon weapons | **QUEST/BOSS** (varies) |
| 70 | Barrows melee | **MINIGAME** (Barrows) |
| 75 | Godswords | **BOSS** (God Wars Dungeon) |
| 80 | Chaotic weapons | **DUNGEONEERING** |

**Attack Equipment Chain Example:**
```
Rune Scimitar (40 Attack)
    ├── HARD: 85 Mining (Runite ore)
    ├── HARD: 85 Smithing (Smelt bar + smith weapon)
    ├── OR SOFT: Buy from GE (expensive)
    ├── OR QUEST: Not available via quest
    └── OR DROP: Can drop from monsters (rare)

Dragon Scimitar (60 Attack)
    ├── HARD: 60 Attack level
    ├── QUEST: Monkey Madness I (partial completion)
    ├── QUEST: The Grand Tree (to access Ape Atoll)
    └── BUY: 100k from Daga on Ape Atoll
```

### 2.2 Strength Skill

**What Strength Enables:**
| Strength Level | Unlocks | Notes |
|----------------|---------|-------|
| 1 | All bronze weapons | No req |
| 5 | Steel halberd | P2P only |
| 20 | Mithril halberd | P2P only |
| 30 | Adamant halberd | P2P only |
| 40 | Rune halberd | P2P only |
| 50 | Granite hammer | P2P, from Grotesque Guardians |
| 60 | Dragon weapons (some) | Dragon halberd, mace, dagger |
| 70 | Barrelchest anchor | Quest: The Great Brain Robbery |
| 75 | Zamorakian hasta | God Wars Dungeon |
| 99 | Cape of Accomplishment | Any skillcape |

**Strength Potion Chain:**
```
Strength Potion (3+10% boost)
    ├── HERBLORE: Level 12
    ├── HERBLORE: Tarromin herb
    │       └── FARMING: Level 19 OR COMBAT: Drop
    ├── HERBLORE: Limpwurt root
    │       └── FARMING: Level 26 OR COMBAT: Hobgoblins
    └── ALTERNATIVE: Buy from GE
```

### 2.3 Defence Skill

**What Defence Enables:**
| Defence Level | Armor Tier | Smithing Requirement | Quest/Other |
|---------------|------------|---------------------|-------------|
| 1 | Bronze | Smithing 1-18 | None |
| 1 | Iron | Smithing 15-33 | None |
| 1 | Leather | Crafting 1-38 | Cowhides from Combat |
| 5 | Steel | Smithing 30-48 | None |
| 10 | Black | **DROP ONLY** | None |
| 20 | Mithril | Smithing 50-68 | None |
| 20 | Studded leather | Crafting 44 | Leather + steel studs |
| 30 | Adamant | Smithing 70-88 | None |
| 30 | Snakeskin | Crafting 51-53 | Snakes from Combat |
| 40 | Rune | Smithing 85-99 | None |
| 40 | Green dragonhide | Crafting 63 | Green dragons |
| 42 | Void armor | **PEST CONTROL** | P2P minigame |
| 45 | Blue dragonhide | Crafting 71 | Blue dragons |
| 50 | Red dragonhide | Crafting 77 | Red dragons |
| 55 | Black dragonhide | Crafting 84 | Black dragons |
| 60 | Dragon armor (some) | **DROP/QUEST** | Fremennik Isles (helm) |
| 70 | Barrows armor | **BARROWS** | P2P minigame |
| 75 | 3rd age | **CLUES** | Master clues |

**Complete Armor Chain (Melee):**
```
Rune Full Helm + Platebody + Platelegs (40 Defence)
    ├── HELM (40 Smithing)
    │   ├── HARD: Mining 85 (Runite ore)
    │   ├── HARD: Smithing 40 (Smelt 1 bar)
    │   └── HARD: Smithing 40 (Smith helm)
    ├── BODY (88 Smithing)
    │   ├── HARD: Mining 85 (5 Runite ore)
    │   ├── HARD: Mining 30 (8 Coal per bar = 40 coal)
    │   ├── HARD: Smithing 85 (Smelt 5 bars)
    │   └── HARD: Smithing 88 (Smith platebody)
    └── LEGS (86 Smithing)
        ├── HARD: Mining 85 (3 Runite ore, 24 coal)
        ├── HARD: Smithing 85 (Smelt 3 bars)
        └── HARD: Smithing 86 (Smith platelegs)

Dragon Chainbody (60 Defence)
    ├── HARD: 60 Defence
    ├── DROP ONLY: Dust devils (rare)
    ├── DROP ONLY: Thermonuclear smoke devil
    └── GIFT: Witch's House quest (not chainbody, med helm)
```

### 2.4 Ranged Skill

**Ranged Equipment Dependencies:**

| Ranged Level | Item | Dependencies |
|--------------|------|--------------|
| 1 | Shortbow | WC 1, Fletching 5 |
| 1 | Bronze arrows | Mining 1, Smithing 1, Fletching 1 |
| 5 | Oak shortbow | WC 15, Fletching 20 |
| 10 | Iron arrows | Mining 15, Smithing 15, Fletching 15 |
| 20 | Willow shortbow | WC 30, Fletching 35 |
| 26 | Iron cbow | Smithing 23, Fletching 39 |
| 30 | Maple shortbow | WC 45, Fletching 50 |
| 31 | Steel arrows | Mining 30, Smithing 30, Fletching 30 |
| 40 | Green d'hide vambs | Crafting 57, Combat (Green dragons) |
| 40 | Maple longbow | WC 45, Fletching 55 |
| 42 | Void armor | **PEST CONTROL** minigame |
| 45 | Steel cbow | Smithing 46, Fletching 62 |
| 50 | Yew shortbow | WC 60, Fletching 65 |
| 55 | Mithril arrows | Mining 55, Smithing 50, Fletching 45 |
| 60 | Yew longbow | WC 60, Fletching 70 |
| 61 | Rune cbow | Smithing 91, Fletching 69 |
| 70 | Adamant arrows | Mining 70, Smithing 70, Fletching 60 |
| 75 | Magic shortbow | WC 75, Fletching 80 |
| 80 | Rune arrows | Mining 85, Smithing 85, Fletching 75 |

**Complete Ranged Setup Chain:**
```
F2P Ranged Setup (Level 40 Ranged)
├── WEAPON: Maple shortbow (Fletching 50, WC 45)
│   ├── WC 45 → Maple logs
│   ├── Fletching 50 → Unstrung maple shortbow
│   └── Fletching 50 + Bow string → Maple shortbow
│       └── Crafting 10 (Bow string from flax)
│           └── PICKING: Flax (requires area access)
│
├── AMMO: Mithril arrows (Fletching 45, Smithing 50)
│   ├── Arrow shafts: WC any logs
│   ├── Feathers: Chickens (Combat) OR Hunter
│   └── Mithril arrowheads: Smithing 50
│       ├── Mining 55 (Mithril ore)
│       ├── Mining 30 (Coal x4 per bar)
│       └── Smithing 50 (Smelt bar, smith heads)
│
└── ARMOR: Green dragonhide (Crafting 63)
    ├── Green dragon leather (Crafting 57 to tan)
    │   └── Green dragonhide from Combat
    │       └── HARD: Can kill Green dragons (CB 79)
    ├── Crafting 63: D'hide body
    ├── Crafting 57: D'hide vambraces
    └── Crafting 60: D'hide chaps
```

### 2.5 Magic Skill

**Magic Equipment & Rune Dependencies:**

| Magic Level | Spell/Item | Dependencies |
|-------------|------------|--------------|
| 1 | Air strike | Runecrafting 1 (air runes) |
| 1 | Wind bolt | Air runes, Chaos runes (RC 35) |
| 5 | Water strike | Runecrafting 5 |
| 9 | Earth strike | Runecrafting 9 |
| 13 | Fire strike | Runecrafting 14 |
| 17 | Wind blast | Death runes (RC 65) |
| 21 | Low Alchemy | Nature runes (RC 44) |
| 25 | Varrock teleport | LAW runes (RC 54), **QUEST: Varrock achievement diary** |
| 27 | Lumbridge teleport | LAW runes, **QUEST: Lumbridge achievement diary** |
| 31 | Falador teleport | LAW runes, **QUEST: Falador achievement diary** |
| 35 | Fire bolt | Nature runes (RC 44) |
| 41 | Wind blast | Death runes |
| 43 | Superheat Item | Nature runes, Smithing level of bar |
| 45 | Camelot teleport | LAW runes, **QUEST: Camelot training room OR King's Ransom** |
| 47 | Water blast | Death runes |
| 50 | Snare | Nature runes |
| 51 | Ardougne teleport | LAW runes, **QUEST: Plague City** |
| 55 | High Alchemy | Nature runes |
| 58 | Watchtower teleport | LAW runes, **QUEST: Watchtower** |
| 59 | Fire blast | Blood runes (RC 77) |
| 60 | Trollheim teleport | LAW runes, **QUEST: Eadgar's Ruse** |
| 61 | God spells | **MAGE ARENA** minigame |
| 66 | Vulnerability | Nature + Soul runes |
| 68 | Enchant onyx | Magic Imbue, **DROP: Onyx from Zulrah/TzHaar** |
| 70 | Teleother spells | Lunar Diplomacy for some |
| 73 | Ice barrage | **ANCIENT MAGICKS** (Desert Treasure) |
| 75 | Fire wave | Blood runes |
| 80 | Stun | Soul runes (RC 90) |
| 94 | Vengeance | **LUNAR SPELLS** (Lunar Diplomacy) |
| 96 | Spellbook swap | **LUNAR SPELLS** |

**Magic Rune Production Chain:**
```
High Alchemy (Level 55)
├── HARD: Nature runes
│   ├── Runecrafting 44
│   │   ├── Quest: Rune Mysteries
│   │   ├── Mining 30+ (Pure essence)
│   │   └── Runecrafting 44 (Nature altar access)
│   │       └── LOCATION: Nature altar (Karamja, requires travel)
│   └── OR: Buy from shops (limited stock)
│   └── OR: Combat drops (varies)
│
├── SOFT: Items to alch
│   ├── Smithing products (Rune items best)
│   ├── Crafting products (Jewelry)
│   ├── Combat drops (staves, armor)
│   └── BUY: Nature rune packs, items
│
└── PROFIT: (0.6 × Item shop price) - Nature rune cost
```

**Combat Spell Chain:**
```
Fire Blast (Level 59)
├── HARD: 5 Fire runes
│   └── Runecrafting 14 (Fire altar) OR Buy
├── HARD: 4 Air runes
│   └── Runecrafting 1 (Air altar) OR Buy
└── HARD: 1 Death rune
    ├── Runecrafting 65 (Death altar)
    │   ├── Quest: Mourning's Ends Part II
    │   └── Location: Temple of Light (complex)
    └── OR: Combat drops (high-level monsters)
```

### 2.6 Prayer Skill

**Prayer Unlock Dependencies:**

| Prayer | Level | Effect | Bone Type | Combat Req |
|--------|-------|--------|-----------|------------|
| Thick Skin | 1 | +5% Defence | Any | None |
| Burst of Strength | 4 | +5% Strength | Any | None |
| Sharp Eye | 8 | +5% Ranged | Any | None |
| Mystic Will | 9 | +5% Magic | Any | None |
| Rock Skin | 10 | +10% Defence | Any | None |
| Superhuman Strength | 13 | +10% Strength | Any | None |
| Improved Reflexes | 16 | +10% Attack | Any | None |
| Rapid Restore | 19 | 2× stat restore | Any | None |
| Rapid Heal | 22 | 2× HP restore | Any | None |
| Protect Item | 25 | Keep 1 item on death | Any | None |
| Protect from Magic | 37 | 100% magic prot | Any | None |
| Protect from Missiles | 40 | 100% ranged prot | Any | None |
| Protect from Melee | 43 | 100% melee prot | Any | None |
| Eagle Eye | 44 | +15% Ranged | Any | None |
| Mystic Might | 45 | +15% Magic | Any | None |
| Retribution | 46 | Recoil damage on death | Any | None |
| Redemption | 49 | Heal when low HP | Any | None |
| Smite | 52 | Drain enemy prayer | Any | None |
| Preserve | 55 | 50% longer stat boosts | Any | **QUEST: DBWest Achievement Diary** |
| Chivalry | 60 | +15% Attack, +18% Strength/Def | Any | **QUEST: Camelot training room** |
| Piety | 70 | +20% Attack, +23% Strength/Def | Any | **QUEST: Camelot training room** |
| Rigour | 74 | +20% Ranged, +25% Defence | Any | **DROP: Dexterous prayer scroll (Raids)** |
| Augury | 77 | +25% Magic, +25% Defence | Any | **DROP: Arcane prayer scroll (Raids)** |

**Prayer Training Chain:**
```
Dragon Bones (72 XP each)
├── HARD: Kill dragon (CB 79+ recommended)
│   ├── Green dragons (Wilderness or Corsair Cove)
│   ├── Blue dragons (Taverley Dungeon)
│   └── All require combat stats
├── SOFT: Buy from GE (expensive)
├── Gilded altar (3.5× XP)
│   ├── Construction 75 (build gilded altar)
│   │   ├── Construction training (expensive)
│   │   └── Marble blocks from Stonemason
│   └── OR: Use someone else's house (W330)
└── Ectofuntus (4× XP)
    ├── Quest: Ghosts Ahoy (for Ectophial teleport)
    └── Buckets of slime (collect from Pools)
        └── Many buckets needed (inventory management)
```

### 2.7 Hitpoints

**Hitpoints Dependencies:**
- **NO direct training** - Gained through combat
- **Food healing required**: Cooking or Fishing
- **Regeneration**: Natural at 1 HP per minute
- **Rapid Heal prayer**: 2× regeneration speed
- **Regen bracelet**: 2× regeneration

---

## 3. Complete Skill-Quest-Equipment Web

### 3.1 Skill Interconnection Matrix

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          SKILL INTERCONNECTION WEB                          │
└─────────────────────────────────────────────────────────────────────────────┘

MINING ───────┬──────> SMITHING ───────┬──────> COMBAT (Melee)
    │         │             │          │
    │         │             ├──────> FLETCHING ────┬──────> RANGED
    │         │             │      (Arrowheads)    │
    │         │             │                      │
    ├──────> CRAFTING ──────┼──────> MAGIC ────────┤
    │      (Gold/Silver)    │     (Jewelry)        │
    │             │         │                      │
    │             ├──────> HERBLORE <──────────────┤
    │             │         │                      │
    │             │         ├──────> FARMING       │
    │             │                            
    └──────> CONSTRUCTION (Limestone, etc.)        │
                                                   │
WOODCUTTING ───┬──────> FLETCHING ────────────────┘
    │          │      (Bows, Stock)
    │          │
    ├──────> FIREMAKING ────┐
    │                       │
    └──────> CONSTRUCTION ──┴──────> COOKING (Ranges)
    (Planks)

FISHING ───────────> COOKING ───────> HITPOINTS (Healing)
    │                    │
    │                    └──────> HERBLORE (Ingredients)
    │
    └──────> FARMING (Seaweed)

THIEVING ────────┬──────> COINS/GOLD
    │            │
    ├──────> HUNTER ───────> CHINCHOMPAS ───────> RANGED
    │                        (Traps)
    │
    └──────> SLAYER ───────> COMBAT (All styles)

AGILITY ────────────> RUN ENERGY ────────────> ALL GATHERING
                           │
                           └──────> HERBLORE (Energy potions)
```

### 3.2 Quest-Skill Dependencies

| Skill | Quest | What It Unlocks | Other Requirements |
|-------|-------|-----------------|-------------------|
| Runecrafting | Rune Mysteries | Mine essence, use altars | None |
| Herblore | Druidic Ritual | Clean herbs, make potions | None |
| Smithing | The Knight's Sword | Blurite smithing | 10 Mining |
| Agility | Grand Tree | Gnome Stronghold course | None (but quest gives XP) |
| Mining | Lost Tribe | Dorgeshuun mine | 17 Mining, 13 Agility, 13 Thieving |
| Crafting | Murder Mystery | Crafting guild access | 40 Crafting |
| Magic | Desert Treasure | Ancient Magicks | 50 Magic, 50 Attack |
| Magic | Lunar Diplomacy | Lunar spellbook | 61 Crafting, 40 Defence, 49 Firemaking, 5 Herblore, 65 Magic, 60 Mining, 55 Woodcutting |
| Magic | Mage Arena | God spells | 60 Magic, Combat gear |
| Prayer | Camelot training | Chivalry, Piety | 60/70 Prayer, 65 Defence |
| Prayer | Ghosts Ahoy | Ectophial (Prayer training) | None |
| Ranged | Animal Magnetism | Ava's attractor | 18 Slayer, 19 Crafting, 30 Ranged, 35 Woodcutting |
| Ranged | Mourning's Ends | Crystal bow (indirectly) | Many requirements |
| Slayer | Smoking Kills | Slayer helmet | 35 Slayer, 85 Combat |
| Slayer | Priest in Peril | Morytania Slayer | None |
| Thieving | Fairytale II | Fairy rings (for Rogue's Castle) | 40 Thieving, 49 Farming, 57 Herblore |
| Construction | - | No specific quest required | 1,000,000 GP for house |
| Hunter | - | No specific quest required | None |
| Farming | - | No specific quest required | None |

### 3.3 Equipment-Quest Locks

| Equipment | Quest Required | Stats Required | Combat Style |
|-----------|----------------|----------------|--------------|
| Dragon longsword | Lost City | 60 Attack | Melee |
| Dragon dagger | Lost City | 60 Attack | Melee |
| Dragon mace | Heroes' Quest | 60 Attack | Melee |
| Dragon battleaxe | Heroes' Quest | 60 Attack | Melee |
| Dragon scimitar | Monkey Madness I (partial) | 60 Attack | Melee |
| Dragon halberd | Regicide | 60 Attack, 30 Strength | Melee |
| Barrelchest anchor | The Great Brain Robbery | 60 Strength | Melee |
| Zamorakian hasta | None (God Wars) | 70 Attack | Melee |
| Saradomin sword | None (God Wars) | 70 Attack | Melee |
| Abyssal whip | None (Combat) | 70 Attack | Melee |
| Abyssal dagger | None (Combat) | 70 Attack | Melee |
| Armadyl crossbow | None (God Wars) | 70 Ranged | Ranged |
| Toxic blowpipe | None (Zulrah) | 75 Ranged | Ranged |
| Crystal bow | Roving Elves | 70 Ranged | Ranged |
| Magic shortbow (i) | Mage Arena II | 50 Ranged | Ranged |
| Trident of the Seas | None (Kraken) | 75 Magic | Magic |
| Slayer staff | None (Slayer) | 50 Magic + 55 Slayer | Magic |
| Helm of Neitiznot | The Fremennik Isles | 55 Defence | Melee |
| Fighter torso | Barbarian Assault | 40 Defence | Melee |
| Void armor | Pest Control | 42 Attack/Strength/Defence/Ranged/Magic/Hitpoints + 22 Prayer | All |
| Graceful outfit | Rooftop Agility | None | Utility |
| Prospector outfit | Motherlode Mine | None | Mining |
| Lumberjack outfit | Temple Trekking | None | Woodcutting |
| Angler outfit | Fishing Trawler | None | Fishing |
| Pyromancer outfit | Wintertodt | None | Firemaking |
| Rogue outfit | Rogues' Den | 50 Thieving, 50 Agility | Thieving |
| Proselyte armor | Slug Menace | 30 Defence, 20 Prayer | Melee/Prayer |
| Initiate armor | Recruitment Drive | 20 Defence, 10 Prayer | Melee/Prayer |
| White armor | Wanted! | Completion of several quests, 10 Defence | Melee |
| Mithril gloves | Recipe for Disaster (partial) | 13 quests completed | All |
| Barrows gloves | Recipe for Disaster (full) | All 10 subquests + main | All |

---

## 4. Tool Requirements Matrix

### 4.1 Gathering Tools

| Skill | Tool | Use | Source | Level Req |
|-------|------|-----|--------|-----------|
| Mining | Bronze pickaxe | Mine ores | Bob's shop, GE | 1 Mining |
| Mining | Iron pickaxe | Better mining | GE | 1 Mining |
| Mining | Steel pickaxe | Better mining | GE | 6 Mining |
| Mining | Mithril pickaxe | Better mining | GE | 21 Mining |
| Mining | Adamant pickaxe | Better mining | GE | 31 Mining |
| Mining | Rune pickaxe | Best F2P | GE | 41 Mining |
| Mining | Dragon pickaxe | Best speed | GE, Chaos Ele | 61 Mining |
| Mining | Prospector kit | +2.5% XP | Motherlode Mine | N/A (tokens) |
| Woodcutting | Bronze axe | Chop trees | Lumbridge spawn | 1 WC |
| Woodcutting | Iron axe | Better WC | GE | 1 WC |
| Woodcutting | Steel axe | Better WC | GE | 6 WC |
| Woodcutting | Mithril axe | Better WC | GE | 21 WC |
| Woodcutting | Adamant axe | Better WC | GE | 31 WC |
| Woodcutting | Rune axe | Best F2P | GE | 41 WC |
| Woodcutting | Dragon axe | Best F2P speed | GE | 61 WC |
| Woodcutting | Lumberjack kit | +2.5% XP | Temple Trekking | N/A (random) |
| Fishing | Small fishing net | Net fishing | GE | 1 Fishing |
| Fishing | Fishing rod | Bait fishing | GE | 5 Fishing |
| Fishing | Fly fishing rod | Feather fishing | GE | 20 Fishing |
| Fishing | Harpoon | Big fish | GE | 35/55/71 Fishing |
| Fishing | Lobster pot | Lobsters | GE | 40 Fishing |
| Fishing | Big fishing net | Net fish | GE | 16 Fishing |
| Fishing | Angler outfit | +2.5% XP | Fishing Trawler | N/A (random) |

### 4.2 Artisan Tools

| Skill | Tool | Use | Source | Level Req |
|-------|------|-----|--------|-----------|
| Smithing | Hammer | Smith items | GE, spawn | 1 Smithing |
| Crafting | Needle | Leather | GE | 1 Crafting |
| Crafting | Thread | Leather | GE | 1 Crafting |
| Crafting | Chisel | Cut gems | GE | 1 Crafting |
| Crafting | Bracelet mould | Jewelry | GE | 1 Crafting |
| Crafting | Necklace mould | Jewelry | GE | 1 Crafting |
| Crafting | Ring mould | Jewelry | GE | 1 Crafting |
| Crafting | Amulet mould | Jewelry | GE | 1 Crafting |
| Fletching | Knife | Fletch logs | GE, Lumbridge spawn | 1 Fletching |
| Fletching | Feather | Arrow making | Chickens, GE | 1 Fletching |
| Fletching | Bow string | String bows | Crafting (flax), GE | 1 Fletching |
| Fletching | Crossbow string | Crossbows | Hunting (sinew), GE | 1 Fletching |
| Herblore | Pestle and mortar | Crush items | GE | 1 Herblore |
| Herblore | Vial | Potions | GE, shop | 1 Herblore |
| Herblore | Vial of water | Potions | GE, fill at sink | 1 Herblore |
| Cooking | None | Just fire/range | N/A | 1 Cooking |
| Firemaking | Tinderbox | Light fires | GE, spawn | 1 Firemaking |

### 4.3 Utility Tools

| Skill | Tool | Use | Source | Level Req |
|-------|------|-----|--------|-----------|
| Farming | Rake | Clear weeds | Farming shop | 1 Farming |
| Farming | Seed dibber | Plant seeds | Farming shop | 1 Farming |
| Farming | Watering can | Water plants | Farming shop | 1 Farming |
| Farming | Spade | Harvest/clear | GE | 1 Farming |
| Farming | Gardening trowel | Pots | Farming shop | 1 Farming |
| Farming | Secateurs | Cure disease | Farming shop | 1 Farming |
| Farming | Magic secateurs | +10% yield | Fairy Tale I | 1 Farming |
| Thieving | None | Just click | N/A | 1 Thieving |
| Thieving | Rogue outfit | 2× loot | Rogues' Den | 50 Thieving/Agility |
| Agility | Graceful outfit | -30% weight, +30% run restore | Rooftop courses | N/A (marks) |
| Hunter | Bird snare | Birds | Hunter shop | 1 Hunter |
| Hunter | Butterfly net | Butterflies | Hunter shop | 15 Hunter |
| Hunter | Box trap | Chinchompas | Hunter shop | 27 Hunter |
| Hunter | Net trap | Salamanders | Hunter shop | 29 Hunter |
| Runecrafting | Pouches | Hold more essence | Abyss creatures | Various RC |
| Runecrafting | Runecrafting outfit | +10% XP | Guardians of the Rift | N/A |
| Construction | Saw | Building | GE, shop | 1 Construction |
| Construction | Hammer | Building | GE | 1 Construction |
| Construction | Plank | Building | Sawmill (WC logs) | 1 Construction |
| Construction | Steel nails | Building | GE | 1 Construction |

---

## 5. Quest Dependency Chains

### 5.1 Recipe for Disaster (Ultimate Combat Quest Chain)

```
RECIPE FOR DISASTER (RFD)
├── START: Cook's Assistant (COMPLETE)
│
├── SUBQUEST: Mountain Daughter
│   └── REQ: None
│
├── SUBQUEST: The Tourist Trap  
│   └── REQ: None (but 10 Fletching, 20 Smithing helpful)
│
├── SUBQUEST: Goblin Diplomacy (COMPLETE)
│
├── SUBQUEST: Gertrude's Cat
│   └── REQ: None
│
├── SUBQUEST: Shadow of the Storm
│   ├── REQ: The Golem (STARTED)
│   ├── REQ: Demon Slayer (STARTED)
│   ├── REQ: 30 Crafting
│   └── REQ: 35 Thieving
│
├── SUBQUEST: Horror from the Deep
│   ├── REQ: Alfred Grimhand's Barcrawl (miniquest)
│   └── REQ: 35 Agility
│
├── SUBQUEST: Fishing Contest
│   └── REQ: None
│
├── SUBQUEST: Family Crest
│   ├── REQ: 40 Mining
│   ├── REQ: 40 Smithing
│   ├── REQ: 40 Crafting
│   ├── REQ: 40 Magic
│   └── REQ: 40 Cooking
│
├── SUBQUEST: Legends' Quest (partial)
│   ├── REQ: 107 Quest Points
│   ├── REQ: 50 Agility
│   ├── REQ: 50 Crafting
│   ├── REQ: 45 Herblore
│   ├── REQ: 56 Magic
│   ├── REQ: 52 Mining
│   ├── REQ: 42 Prayer
│   ├── REQ: 50 Smithing
│   ├── REQ: 50 Strength
│   ├── REQ: 50 Thieving
│   ├── REQ: 50 Woodcutting
│   └── REQ: Started: Family Crest, Heroes' Quest, Underground Pass,
│           Waterfall Quest, Shilo Village
│
└── FINAL BATTLE
    └── REWARD: Barrows gloves (Best in slot for most builds)
```

### 5.2 Ancient Magicks Quest Chain

```
DESERT TREASURE (Ancient Magicks)
├── REQ: The Dig Site
│   ├── REQ: 10 Agility
│   ├── REQ: 10 Herblore
│   └── REQ: 25 Thieving
│
├── REQ: Temple of Ikov
│   ├── REQ: 42 Thieving
│   ├── REQ: 40 Ranged
│   └── REQ: 8 Magic
│
├── REQ: The Tourist Trap
│   └── REQ: 10 Fletching, 20 Smithing (can use assists)
│
├── REQ: Troll Stronghold
│   └── REQ: 15 Agility
│
├── REQ: Priest in Peril
│   └── REQ: None
│
├── REQ: Waterfall Quest
│   └── REQ: None
│
├── REQ: 10 Slayer
├── REQ: 50 Magic
├── REQ: 50 Attack
├── REQ: 50 Ranged
├── REQ: 10 Crafting
├── REQ: 50 Agility
├── REQ: 50 Thieving
├── REQ: 50 Firemaking
├── REQ: 50 Mining
└── REWARD: Ancient Magicks spellbook
    ├── Ice Rush (58 Magic)
    ├── Ice Burst (70 Magic)
    ├── Ice Blitz (82 Magic)
    └── Ice Barrage (94 Magic) - Most used freeze in PvP
```

### 5.3 Lunar Spells Quest Chain

```
LUNAR DIPLOMACY (Lunar Spells)
├── REQ: Lost City
│   ├── REQ: 31 Crafting
│   ├── REQ: 36 Woodcutting
│   └── REQ: Combat gear
│
├── REQ: The Fremennik Trials
│   └── REQ: None
│
├── REQ: Rune Mysteries
│   └── REQ: None
│
├── REQ: Shilo Village
│   └── REQ: 20 Crafting, 32 Agility
│
├── REQ: 61 Crafting
├── REQ: 40 Defence
├── REQ: 49 Firemaking
├── REQ: 5 Herblore
├── REQ: 65 Magic
├── REQ: 60 Mining
├── REQ: 55 Woodcutting
└── REWARD: Lunar spellbook
    ├── Vengeance (94 Magic) - Recoil damage
    ├── Spellbook swap (96 Magic) - Use all 3 books
    └── Trollheim Teleport (61 Magic) - GWD access
```

### 5.4 Barrows Gloves Quest Chain (Detailed)

```
RECIPE FOR DISASTER - FULL REQUIREMENTS

Prerequisite Quests:
├── Cook's Assistant ✓
├── Fishing Contest
├── Goblin Diplomacy ✓
├── Gertrude's Cat
│
└── Shadow of the Storm
    ├── STARTED: The Golem
    │   └── REQ: 25 Thieving
    │
    ├── STARTED: Demon Slayer
    │
    ├── REQ: 30 Crafting
    └── REQ: 35 Thieving

Horror from the Deep
├── Alfred Grimhand's Barcrawl
│   └── REQ: None (just travel)
└── REQ: 35 Agility

Mountain Daughter
└── REQ: None

The Tourist Trap
└── REQ: None

Family Crest
├── REQ: 40 Mining
├── REQ: 40 Smithing
├── REQ: 40 Crafting
├── REQ: 40 Magic
└── REQ: 40 Cooking

Legends' Quest (Partial for RFD)
├── REQ: 107 Quest Points
├── REQ: 50 Agility
├── REQ: 50 Crafting
├── REQ: 45 Herblore
├── REQ: 56 Magic
├── REQ: 52 Mining
├── REQ: 42 Prayer
├── REQ: 50 Smithing
├── REQ: 50 Strength
├── REQ: 50 Thieving
├── REQ: 50 Woodcutting
│
└── Prerequisites:
    ├── Family Crest (see above)
    ├── Heroes' Quest
    │   ├── REQ: 55 Quest Points
    │   ├── REQ: 53 Cooking
    │   ├── REQ: 53 Fishing
    │   ├── REQ: 25 Herblore
    │   ├── REQ: 50 Mining
    │   └── REQ: Another player (opposite gang)
    ├── Shilo Village
    │   └── REQ: 20 Crafting, 32 Agility
    ├── Underground Pass
    │   ├── REQ: Biohazard
    │   │   └── REQ: Plague City
    │   └── REQ: 25 Ranged
    └── Waterfall Quest
        └── REQ: None

TOTAL SKILL REQUIREMENTS FOR BARROWS GLOVES:
├── 175 Quest Points
├── 10 Cooking
├── 10 Fishing
├── 10 Mining
├── 10 Smithing
├── 10 Crafting
├── 10 Magic
├── 10 Herblore
├── 10 Thieving
├── 10 Woodcutting
├── 10 Agility
├── 10 Firemaking
├── 10 Ranged
├── 10 Prayer
├── 10 Attack
├── 10 Strength
├── 10 Defence
├── 10 Hitpoints
├── 35 Agility
├── 40 Cooking
├── 40 Crafting
├── 40 Magic
├── 40 Mining
├── 40 Smithing
├── 45 Herblore
├── 50 Agility
├── 50 Cooking
├── 50 Crafting
├── 50 Firemaking
├── 50 Fishing
├── 50 Magic
├── 50 Mining
├── 50 Prayer
├── 50 Smithing
├── 50 Strength
├── 50 Thieving
├── 50 Woodcutting
├── 52 Mining
└── 56 Magic
```

---

## 6. Equipment Progression Trees

### 6.1 Melee Weapon Progression

```
MELEE WEAPON PROGRESSION TREE

Tier 1: Bronze (Attack 1, Smithing 1-18)
├── Bronze dagger (1 Smithing)
├── Bronze sword (4 Smithing)
├── Bronze scimitar (5 Smithing) ← Best DPS for level
├── Bronze longsword (6 Smithing)
└── Bronze 2h sword (14 Smithing)

Tier 2: Iron (Attack 1, Smithing 15-33)
├── Iron dagger (15 Smithing)
├── Iron sword (19 Smithing)
├── Iron scimitar (20 Smithing) ← Best DPS
└── Iron 2h sword (29 Smithing)

Tier 3: Steel (Attack 5, Smithing 30-48)
├── Steel dagger (30 Smithing)
├── Steel sword (34 Smithing)
├── Steel scimitar (35 Smithing) ← Best DPS
└── Steel 2h sword (44 Smithing)

Tier 4: Black (Attack 10, DROP ONLY)
├── Black sword - Hill Giants
├── Black scimitar - Lv 34 monsters
└── Black 2h sword - Grave scorpions

Tier 5: Mithril (Attack 20, Smithing 50-68)
├── Mithril dagger (50 Smithing)
├── Mithril sword (54 Smithing)
├── Mithril scimitar (55 Smithing) ← Best DPS
└── Mithril 2h sword (64 Smithing)

Tier 6: Adamant (Attack 30, Smithing 70-88)
├── Adamant dagger (70 Smithing)
├── Adamant sword (74 Smithing)
├── Adamant scimitar (75 Smithing) ← Best DPS
└── Adamant 2h sword (84 Smithing)

Tier 7: Rune (Attack 40, Smithing 85-99)
├── Rune dagger (85 Smithing)
├── Rune sword (89 Smithing)
├── Rune scimitar (90 Smithing) ← Best F2P DPS
└── Rune 2h sword (99 Smithing)

Tier 8: Dragon (Attack 60, QUEST/BOSS)
├── Dragon dagger - Lost City quest
├── Dragon longsword - Lost City quest
├── Dragon scimitar - Monkey Madness I (partial)
├── Dragon mace - Heroes' Quest
├── Dragon battleaxe - Heroes' Quest
└── Dragon halberd - Regicide quest

Tier 9: Barrows (Attack 70, MINIGAME)
├── Dharok's greataxe (70 Attack, 70 Strength)
├── Guthan's warspear (70 Attack)
├── Torag's hammers (70 Attack)
├── Verac's flail (70 Attack)
└── Abyssal whip (70 Attack) - Abyssal demons

Tier 10: Godswords (Attack 75, BOSS)
├── Armadyl godsword
├── Bandos godsword
├── Saradomin godsword
└── Zamorak godsword
```

### 6.2 Armor Progression Tree

```
ARMOR PROGRESSION TREE

MELEE ARMOR

Tier 1: Bronze (Defence 1, Smithing 1-18)
├── Bronze med helm (3 Smithing)
├── Bronze chainbody (11 Smithing)
├── Bronze platelegs (16 Smithing)
├── Bronze full helm (18 Smithing)
└── Bronze platebody (18 Smithing)

Tier 2: Iron (Defence 1, Smithing 15-33)
├── Iron med helm (18 Smithing)
├── Iron chainbody (26 Smithing)
├── Iron platelegs (31 Smithing)
└── Iron platebody (33 Smithing)

Tier 3: Steel (Defence 5, Smithing 30-48)
├── Steel med helm (33 Smithing)
├── Steel chainbody (41 Smithing)
├── Steel platelegs (46 Smithing)
└── Steel platebody (48 Smithing)

Tier 4: Black (Defence 10, DROP ONLY)
└── Various monsters

Tier 5: Mithril (Defence 20, Smithing 50-68)
├── Mithril med helm (53 Smithing)
├── Mithril chainbody (61 Smithing)
├── Mithril platelegs (66 Smithing)
└── Mithril platebody (68 Smithing)

Tier 6: Adamant (Defence 30, Smithing 70-88)
├── Adamant med helm (73 Smithing)
├── Adamant chainbody (81 Smithing)
├── Adamant platelegs (86 Smithing)
└── Adamant platebody (88 Smithing)

Tier 7: Rune (Defence 40, Smithing 85-99)
├── Rune med helm (88 Smithing)
├── Rune chainbody (96 Smithing)
├── Rune platelegs (99 Smithing)
└── Rune platebody (99 Smithing)

Tier 8: Dragon (Defence 60, QUEST/DROP)
├── Helm of Neitiznot (55 Def) - Fremennik Isles
└── Dragon chainbody (60 Def) - Dust devils

RANGED ARMOR (Crafting)

Tier 1: Leather (Defence 1, Crafting 1-18)
├── Leather gloves (1 Crafting)
├── Leather boots (7 Crafting)
├── Leather cowl (9 Crafting)
├── Leather vambraces (11 Crafting)
├── Leather chaps (18 Crafting)
└── Leather body (14 Crafting)

Tier 2: Hard Leather (Defence 20, Crafting 28)
├── Hardleather body (28 Crafting)
└── Requires: 1 cowhide + 10gp tanning

Tier 3: Studded (Defence 20, Crafting 44)
├── Studded chaps (44 Crafting)
└── Studded body (41 Crafting)

Tier 4: Green D'hide (Defence 40, Crafting 57-63)
├── Green d'hide vambraces (57 Crafting)
├── Green d'hide chaps (60 Crafting)
└── Green d'hide body (63 Crafting)

Tier 5: Blue D'hide (Defence 50, Crafting 66-71)
├── Blue d'hide vambraces (66 Crafting)
├── Blue d'hide chaps (68 Crafting)
└── Blue d'hide body (71 Crafting)

Tier 6: Red D'hide (Defence 60, Crafting 73-77)
├── Red d'hide vambraces (73 Crafting)
├── Red d'hide chaps (75 Crafting)
└── Red d'hide body (77 Crafting)

Tier 7: Black D'hide (Defence 70, Crafting 79-84)
├── Black d'hide vambraces (79 Crafting)
├── Black d'hide chaps (82 Crafting)
└── Black d'hide body (84 Crafting)

MAGIC ARMOR

Tier 1: Wizard (Defence 1, Crafting)
├── Wizard hat (1 Crafting)
└── Wizard robe (1 Crafting)

Tier 2: Zamorak robes (Defence 1, DROP)
└── Iban's staff quest

Tier 3: Mystic (Defence 20/40, DROP)
└── Slayer monsters
```

---

## 7. Resource-to-Combat Chains

### 7.1 Melee Combat Chain

```
COMPLETE MELEE COMBAT SETUP (Rune tier)

Rune Scimitar (Best F2P DPS)
├── Mining 85
│   ├── Runite ore ×1
│   └── Coal ×8 (2 per bar, 4 bars)
├── Smithing 85 (Smelt rune bar)
└── Smithing 90 (Smith rune scimitar)

Rune Platebody
├── Mining 85
│   ├── Runite ore ×5
│   └── Coal ×40 (8 per bar ×5 bars)
├── Smithing 85 (Smelt 5 rune bars)
└── Smithing 88 (Smith platebody)

Rune Full Helm
├── Mining 85
│   ├── Runite ore ×2
│   └── Coal ×16
├── Smithing 85 (Smelt 2 rune bars)
└── Smithing 87 (Smith full helm)

Rune Platelegs
├── Mining 85
│   ├── Runite ore ×3
│   └── Coal ×24
├── Smithing 85 (Smelt 3 rune bars)
└── Smithing 86 (Smith platelegs)

Rune Kiteshield
├── Mining 85
│   ├── Runite ore ×3
│   └── Coal ×24
├── Smithing 85 (Smelt 3 rune bars)
└── Smithing 87 (Smith kiteshield)

TOTAL RESOURCES:
├── Runite ore: 14
├── Coal: 112
└── Smithing XP: ~7,000 (from smelting and smithing)

ALTERNATIVE: Buy from shop (100k+ per piece)
```

### 7.2 Ranged Combat Chain

```
COMPLETE RANGED COMBAT SETUP (Green D'hide + Maple bow)

Maple Shortbow
├── WC 45 (Maple logs ×1)
└── Fletching 50
    ├── Knife → Maple longbow (u)
    └── Bow string → Maple shortbow
        └── Bow string from flax
            └── Crafting 10 (Spin flax at wheel)
                └── Flax from field picking

Rune Arrows (Best F2P ammo)
├── Arrow shafts (any log, knife)
├── Feathers ×15 (chickens or buy)
└── Rune arrowheads
    ├── Mining 85 (Runite ore)
    ├── Mining 30 (Coal ×2 per bar)
    ├── Smithing 85 (Smelt rune bar)
    └── Smithing 90 (Smith arrowheads ×15)

Green D'hide Armor
├── Green dragonhide ×6 (body 3, chaps 2, vamb 1)
│   └── Combat: Kill green dragons (CB 79)
│       └── 100% drop: Green dragonhide ×1
│       └── 100% drop: Dragon bones ×1
├── Crafting 57: Tan hides (Ellis Al Kharid)
├── Crafting 57: Vambraces
├── Crafting 60: Chaps
└── Crafting 63: Body

TOTAL RESOURCES:
├── Maple logs: 1
├── Bow strings: 1
├── Feathers: 15 per arrow batch
├── Runite ore: 1 per arrow batch
├── Coal: 2 per arrow batch
├── Green dragonhide: 6
└── Crafting XP: ~470
```

### 7.3 Magic Combat Chain

```
COMPLETE MAGIC COMBAT SETUP (F2P)

Staff of Air (Unlimited air runes)
├── BUY: From Zaff in Varrock (1,500 gp)
└── OR: Combat drop from various monsters

Wizard Robes
├── DROP: From wizards, dark wizards
├── Wizard hat (blue) - Dark wizards
└── Wizard robe (blue) - Dark wizards

Fire Strike Casting (Best F2P combat spell)
├── Fire runes ×3
│   └── Runecrafting 14 (Fire altar)
│       └── Rune essence ×3
│           └── Quest: Rune Mysteries
├── Air runes ×2
│   └── Runecrafting 1 (Air altar)
│       └── Rune essence ×2
└── Mind runes ×1
    └── Runecrafting 2 (Mind altar)
        └── Rune essence ×1

OR: BUY runes from shops (more expensive)

FIRE BLAST (Level 59 - Endgame F2P spell)
├── Fire runes ×5
├── Air runes ×4
└── Death runes ×1
    ├── Runecrafting 65 (Death altar)
    │   └── Quest: Mourning's Ends Part II (P2P)
    └── OR: Combat drops (P2P high-level monsters)
```

---

## 8. F2P vs P2P Content Gates

### 8.1 F2P Content Boundaries

**Skills - Full F2P Access:**
| Skill | F2P Limit | P2P Advantage |
|-------|-----------|---------------|
| Attack | 99 | None |
| Strength | 99 | Dragon items, new weapons |
| Defence | 99 | Barrows, higher tier armor |
| Ranged | 99 | Toxic blowpipe, more bows |
| Magic | 99 | Ancient Magicks, Lunar spells |
| Prayer | 99 | More prayers (Chivalry, Piety, etc.) |
| Hitpoints | 99 | None |
| Mining | 99 | Dragon pickaxe, Motherlode Mine, gem rocks access |
| Smithing | 99 | Nothing beyond rune |
| Woodcutting | 99 | Dragon axe, teak, mahogany, redwood |
| Firemaking | 99 | Teak, mahogany, redwood, Wintertodt |
| Fishing | 99 | More fish (monkfish, shark, etc.), special spots |
| Cooking | 99 | More food types |
| Crafting | 99 | Glassblowing, more gems, more leather types |
| Fletching | 99 | More bows, darts, dragon arrows |
| Herblore | 99 | More potions, more herb types |
| Runecrafting | 99 | All runes accessible, but Nature+ higher level |
| Agility | 99 | More courses, shortcuts, Graceful outfit |
| Thieving | 99 | More stalls, chests, Knight/Paladin/Hero |

**Skills - P2P Only:**
- Slayer (entirely P2P)
- Farming (entirely P2P)
- Hunter (entirely P2P)
- Construction (entirely P2P)

### 8.2 F2P Equipment Caps

**Best F2P Equipment by Slot:**
| Slot | F2P Best | P2P Upgrade |
|------|----------|-------------|
| Weapon | Rune scimitar | Dragon scimitar, Abyssal whip |
| Offhand | Rune kiteshield | Dragonfire shield, Defenders |
| Head | Rune full helm | Helm of Neitiznot, Serpentine helm |
| Body | Rune platebody | Fighter torso, Bandos chestplate |
| Legs | Rune platelegs | Bandos tassets |
| Boots | Fighting boots | Dragon boots, Primordial boots |
| Gloves | Combat bracelet | Barrows gloves, Ferocious gloves |
| Cape | Cape of legends | Fire cape, Infernal cape |
| Amulet | Amulet of power | Amulet of fury, Amulet of torture |
| Ring | Explorer's ring 4 | Berserker ring, Ring of suffering |

**Best F2P Ranged:**
| Slot | F2P Best | P2P Upgrade |
|------|----------|-------------|
| Weapon | Maple shortbow / Rune crossbow | Magic shortbow (i), Toxic blowpipe, Armadyl crossbow |
| Ammo | Rune arrows / Adamant bolts | Dragon arrows, Diamond bolts (e) |
| Armor | Green d'hide | Black d'hide, Armadyl, Karil's |

**Best F2P Magic:**
| Slot | F2P Best | P2P Upgrade |
|------|----------|-------------|
| Weapon | Staff of air/water/earth/fire | Trident of the Seas, Sanguinesti staff |
| Armor | Wizard robes | Mystic robes, Ahrim's robes, Ancestral |

---

## 9. Implementation Task Priority Matrix

### 9.1 Tier 0: Absolute Foundation (Week 1)

Must be done before anything else works:

| Task ID | Content | Dependencies | Blocks |
|---------|---------|--------------|--------|
| CORE-0-1 | Bronze/Iron/Steel pickaxes | None | All Mining |
| CORE-0-2 | Bronze/Iron/Steel axes | None | All Woodcutting |
| CORE-0-3 | Fishing net, rod, feathers | None | All Fishing |
| CORE-0-4 | Hammer, tinderbox, knife | None | Smithing, FM, Fletching |
| CORE-0-5 | Hammer, chisel, needle | None | Crafting |

### 9.2 Tier 1: Gathering Foundation (Week 1-2)

```
PARALLEL IMPLEMENTATION:

SKILL-WC-F2P (Claude/kimi)
├── Copper/Tin/Iron/Coal rocks
├── Bronze/Iron/Steel pickaxes
└── Varrock, Lumbridge, Dwarven Mine

SKILL-WC-F2P (Claude/kimi)
├── Normal/Oak/Willow/Maple trees
├── Bronze/Iron/Steel axes
└── Lumbridge, Draynor, Varrock

SKILL-FISH-F2P (Claude/kimi)
├── Net, Bait, Fly, Harpoon, Cage spots
├── Shrimp/Sardine/Herring/Trout/Salmon/Tuna
├── Lumbridge, Draynor, Karamja (F2P)
```

### 9.3 Tier 2: Processing Skills (Week 2-3)

```
DEPENDENT ON TIER 1:

SKILL-SMITH-F2P (kimi)
├── Bronze/Iron/Steel bar smelting
├── Requires: Mining ores from TIER 1
├── Tool: Hammer
└── Outputs: Bars for equipment, arrowheads

SKILL-COOK-F2P (Claude)
├── All fish cooking
├── Requires: Raw fish from TIER 1
├── Tool: Fire (FM) or Range
└── Outputs: Food for HP restore

SKILL-FM-F2P (opencode)
├── Normal/Oak/Willow/Maple log burning
├── Requires: Logs from TIER 1
├── Tool: Tinderbox
└── Outputs: Fires for cooking
```

### 9.4 Tier 3: Equipment Production (Week 3-4)

```
DEPENDENT ON TIER 2:

SKILL-SMITH-EQUIP-F2P (kimi)
├── Bronze/Iron/Steel equipment
├── Requires: Bars from TIER 2
├── Weapons: Daggers, swords, scimitars, longswords
├── Armor: Helms, bodies, legs
└── Blocks: Combat progression

SKILL-CRAFT-LEATHER-F2P (kimi)
├── Leather armor set
├── Requires: Cowhides (Combat/Thieving)
├── Tools: Needle, thread
└── Blocks: Ranged armor

SKILL-CRAFT-JEWELRY-F2P (kimi)
├── Gold rings, necklaces
├── Requires: Gold bars (Mining/Smithing)
├── Tools: Moulds, chisel
└── Blocks: Crafting GP, teleport jewelry
```

### 9.5 Tier 4: Advanced Skills (Week 4-6)

```
DEPENDENT ON TIERS 1-3:

SKILL-FLETCH-F2P (Claude)
├── All bow types (Normal → Maple)
├── Arrow making (Bronze → Steel)
├── Requires: Logs from TIER 1
├── Requires: Arrowheads from Smithing TIER 3
└── Blocks: Ranged combat

SKILL-HERB-F2P (opencode)
├── Guam, Marrentill, Tarromin, Harralander cleaning
├── Basic potions (Attack, Strength, Defence)
├── Requires: Herbs (Farming drops initially, then Farming)
├── Requires: Secondaries (Combat drops)
└── Blocks: Combat efficiency

SKILL-CRAFT-GEMS-F2P (kimi)
├── Sapphire, Emerald, Ruby, Diamond cutting
├── Requires: Uncut gems (Mining gem rocks TIER 1)
├── Tool: Chisel
└── Blocks: Crafting XP, jewelry
```

### 9.6 Tier 5: P2P Expansion (Week 6+)

```
ALL P2P SKILLS (codex/kimi rotation)

SKILL-RC-P2P
├── All altars
├── Quest: Rune Mysteries
└── Blocks: Magic casting

SKILL-AGILITY-P2P
├── Rooftop courses
└── Blocks: Graceful outfit, shortcuts

SKILL-THIEVING-P2P
├── Stalls, pickpocketing
├── Rogues' Den for outfit
└── Blocks: Money making, clue scrolls

SKILL-FARM-P2P
├── Allotments, herbs, trees
└── Blocks: Sustainable Herblore

SKILL-HUNTER-P2P
├── Traps, chinchompas
└── Blocks: Ranged training method

SKILL-SLAYER-P2P
├── Task system
├── Slayer masters
└── Blocks: Slayer monsters, drops

SKILL-CONSTRUCT-P2P
├── House building
└── Blocks: POH features, gilded altar
```

### 9.7 Quest Implementation Priority

```
WEEK 1 (Tutorial):
├── COOK-1: Cook's Assistant
│   └── Unlocks: Lumbridge range
│
└── RUNE-1: Rune Mysteries
    └── Unlocks: Mining essence, Runecrafting

WEEK 2 (Basic):
├── FISH-1: Fishing Contest
├── CRAFT-1: Goblin Diplomacy
├── THIEV-1: Gertrude's Cat
└── MAGIC-1: Imp Catcher

WEEK 3 (Skills):
├── DRUID-1: Druidic Ritual (Herblore)
├── TREE-1: The Grand Tree (Glider)
├── TREE-2: Tree Gnome Village
└── QUEST-DRAYNOR: Vampire Slayer

WEEK 4 (Combat):
├── LOST-1: Lost City (Dragon dagger/longsword)
├── HEROES-1: Heroes' Quest (Dragon mace/battleaxe)
├── MM-1: Monkey Madness I (partial - Dragon scimitar)
└── KNIGHT-1: The Knight's Sword (Blurite)

WEEK 5+ (Advanced):
├── MM-FULL: Monkey Madness I (full)
├── DT-1: Desert Treasure (Ancient Magicks)
├── LUNAR-1: Lunar Diplomacy
├── RFD-1: Recipe for Disaster (partial)
└── RFD-FULL: Recipe for Disaster (Barrows gloves)
```

---

## 10. Summary Tables

### 10.1 Quick Reference: What You Need to Train X

| Want to Train | You Need | Alternative |
|---------------|----------|-------------|
| Mining | Pickaxe | None |
| Woodcutting | Axe | None |
| Fishing | Rod/Net + bait/feathers | None |
| Smithing | Hammer + ores | Buy bars (expensive) |
| Crafting | Needle/thread OR chisel | None |
| Fletching | Knife + logs | None |
| Cooking | Fire/Range + raw food | None |
| Firemaking | Tinderbox + logs | None |
| Herblore | Herbs + secondaries | Buy finished potions |
| Runecrafting | Essence (Mining) | Buy essence |
| Thieving | Nothing | None |
| Agility | Nothing | None |
| Farming | Seeds + tools | Buy produce |
| Hunter | Traps | None |
| Slayer | Combat stats | None |
| Construction | Planks (WC) + nails (Smithing) | Buy planks |

### 10.2 Quick Reference: What X Unlocks

| Skill Level | Unlocks |
|-------------|---------|
| Attack 40 | Rune weapons (best F2P) |
| Attack 60 | Dragon weapons (quest locked) |
| Strength 60 | Dragon weapons (some) |
| Defence 40 | Rune armor (best F2P) |
| Ranged 40 | Green d'hide, Maple shortbow |
| Magic 55 | High Alchemy (money maker) |
| Mining 85 | Runite ore (best F2P bar) |
| Smithing 85 | Rune bars/equipment |
| WC 60 | Yew logs (good money) |
| Fishing 76 | Shark (best P2P food) |
| Cooking 80 | Shark cooking |
| Crafting 75 | Glory amulet (teleport jewelry) |
| Fletching 70 | Yew longbow (F2P best) |
| Herblore 45 | Super attack potions |
| Herblore 55 | Super strength potions |
| RC 44 | Nature runes (High Alch) |
| RC 54 | Law runes (teleports) |
| Agility 70 | Shortcut access |
| Slayer 75 | Gargoyles (good money) |

---

**Document Author**: kimi
**Created**: 2026-02-26
**Status**: Complete dependency map - Ready for task generation
**Next Steps**: Use this to create specific implementation tasks in agent-tasks registry

