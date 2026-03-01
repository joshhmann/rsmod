# F2P Content Completeness Guide

**Document Purpose**: Targeted implementation roadmap for achieving 100% F2P content parity using dependency analysis.

**Based On**: `COMPLETE_CONTENT_DEPENDENCY_MAP.md`

**Last Updated**: 2026-02-26

---

## Executive Summary

### Current F2P Status (Based on CONTENT_AUDIT.md)

| Category | Status | Missing Pieces |
|----------|--------|----------------|
| Gathering Skills | 🟡 Partial | Some higher-tier resources |
| Artisan Skills | 🟡 Partial | Some recipes, interfaces |
| Combat Equipment | 🟡 Partial | Some F2P armor/weapons |
| Quests | 🟡 Partial | 6 F2P quests need implementation |
| NPCs/Drops | 🟡 Partial | Some F2P training monsters |
| Locations | ✅ Mostly Complete | Most F2P areas done |

**Target**: Complete, playable F2P experience matching OSRS revision 233.

---

## Phase 1: Foundation Layer (CRITICAL - Blocks Everything)

### 1.1 Gathering Tools (All Must Work)

| Tool | Status | Missing | Priority |
|------|--------|---------|----------|
| Bronze pickaxe | ✅ | - | - |
| Iron pickaxe | ✅ | - | - |
| Steel pickaxe | ✅ | - | - |
| Mithril pickaxe | ✅ | - | - |
| Adamant pickaxe | ✅ | - | - |
| **Rune pickaxe** | 🟡 | Verify GE/shop availability | HIGH |
| Bronze axe | ✅ | - | - |
| Iron axe | ✅ | - | - |
| Steel axe | ✅ | - | - |
| Mithril axe | ✅ | - | - |
| Adamant axe | ✅ | - | - |
| **Rune axe** | 🟡 | Verify GE/shop availability | HIGH |

**Action Items**:
- [ ] `TOOL-F2P-RUNE-PICKAXE`: Ensure rune pickaxe is available (shop/GE)
- [ ] `TOOL-F2P-RUNE-AXE`: Ensure rune axe is available (shop/GE)

### 1.2 F2P Mining Rocks (Complete Ore Chain)

| Rock | Mining Level | Status | Location | Missing |
|------|--------------|--------|----------|---------|
| Clay | 1 | ✅ | Varrock, Dwarven Mine | - |
| Copper | 1 | ✅ | Lumbridge, Varrock | - |
| Tin | 1 | ✅ | Lumbridge, Varrock | - |
| Iron | 15 | ✅ | Varrock, Dwarven Mine | - |
| Silver | 20 | ✅ | Dwarven Mine, Al Kharid | - |
| Coal | 30 | ✅ | Dwarven Mine, Mining Guild | - |
| Gold | 40 | ✅ | Dwarven Mine, Al Kharid | - |
| **Gem Rock** | 40 | 🟡 | Verify functionality | MEDIUM |
| **Mithril** | 55 | 🟡 | Wilderness F2P? Verify | MEDIUM |

**Action Items**:
- [ ] `MINE-F2P-GEM-ROCKS`: Verify gem rocks work at 40 Mining
- [ ] `MINE-F2P-MITHRIL-WILD`: Check if mithril in Wilderness F2P

### 1.3 F2P Woodcutting Trees

| Tree | WC Level | Status | Location | Missing |
|------|----------|--------|----------|---------|
| Normal | 1 | ✅ | Everywhere | - |
| Oak | 15 | ✅ | Lumbridge, Varrock | - |
| Willow | 30 | ✅ | Draynor, Port Sarim | - |
| **Maple** | 45 | 🟡 | Verify F2P locations | HIGH |
| **Yew** | 60 | 🟡 | Verify F2P locations | HIGH |

**Action Items**:
- [ ] `WC-F2P-MAPLE-TREES`: Add maple trees to F2P locations
- [ ] `WC-F2P-YEW-TREES`: Add yew trees to F2P locations (Edgeville, Falador)

### 1.4 F2P Fishing Spots

| Spot | Fishing Level | Method | Status | Missing |
|------|---------------|--------|--------|---------|
| Shrimp/Anchovies | 1 | Small net | ✅ | - |
| Sardine/Herring | 5/10 | Rod + bait | ✅ | - |
| Trout/Salmon | 20/30 | Fly rod | ✅ | - |
| Tuna/Swordfish | 35/50 | Harpoon | ✅ | - |
| Lobster | 40 | Cage | ✅ | - |

**Status**: ✅ **COMPLETE** - All F2P fish implemented

---

## Phase 2: Processing Skills (Week 2-3)

### 2.1 Smithing - Bar Smelting

| Bar | Level | Materials | Status | Missing |
|-----|-------|-----------|--------|---------|
| Bronze | 1 | 1 Copper + 1 Tin | ✅ | - |
| Iron | 15 | 1 Iron ore (50%) | 🟡 | Verify 50% success rate | MEDIUM |
| Silver | 20 | 1 Silver ore | ✅ | - |
| Steel | 30 | 1 Iron + 2 Coal | ✅ | - |
| Gold | 40 | 1 Gold ore | ✅ | - |

**Action Items**:
- [ ] `SMITH-F2P-IRON-FAIL`: Implement 50% failure rate for iron
- [ ] `SMITH-F2P-FURNACE-LOCS`: Verify all F2P furnaces work
  - Lumbridge furnace
  - Falador furnace
  - Al Kharid furnace
  - Edgeville furnace

### 2.2 Smithing - F2P Equipment

| Equipment | Level | Bars | Status | Missing |
|-----------|-------|------|--------|---------|
| Bronze dagger | 1 | 1 | ✅ | - |
| Bronze scimitar | 5 | 2 | ✅ | - |
| Bronze platebody | 18 | 5 | ✅ | - |
| Iron dagger | 15 | 1 | ✅ | - |
| Iron scimitar | 20 | 2 | ✅ | - |
| Iron platebody | 33 | 5 | ✅ | - |
| Steel dagger | 30 | 1 | ✅ | - |
| Steel scimitar | 35 | 2 | ✅ | - |
| Steel platebody | 48 | 5 | ✅ | - |
| Mithril dagger | 50 | 1 | 🟡 | Verify F2P availability | MEDIUM |
| Mithril scimitar | 55 | 2 | 🟡 | Verify F2P availability | MEDIUM |
| Mithril platebody | 68 | 5 | 🟡 | Verify F2P availability | MEDIUM |
| **Rune dagger** | 85 | 1 | 🟡 | Verify implementation | HIGH |
| **Rune scimitar** | 90 | 2 | 🟡 | Verify implementation | HIGH |
| **Rune platebody** | 99 | 5 | 🟡 | Verify implementation | HIGH |

**Action Items**:
- [ ] `SMITH-F2P-RUNE-WEAPONS`: Implement rune weapon smithing (85-90)
- [ ] `SMITH-F2P-RUNE-ARMOR`: Implement rune armor smithing (85-99)
- [ ] `SMITH-F2P-ANVIL-LOCS`: Verify anvil locations
  - Varrock anvils (central plaza + west)
  - Falador anvils

### 2.3 Cooking - F2P Fish

| Fish | Level | Heals | Status | Missing |
|------|-------|-------|--------|---------|
| Shrimp | 1 | 3 | ✅ | - |
| Sardine | 1 | 4 | ✅ | - |
| Herring | 5 | 5 | ✅ | - |
| Mackerel | 10 | 6 | ✅ | - |
| Trout | 15 | 7 | ✅ | - |
| Cod | 18 | 7 | ✅ | - |
| Pike | 20 | 8 | ✅ | - |
| Salmon | 25 | 9 | ✅ | - |
| Tuna | 30 | 10 | ✅ | - |
| Lobster | 40 | 12 | ✅ | - |
| Bass | 43 | 13 | ✅ | - |
| Swordfish | 45 | 14 | ✅ | - |

**Action Items**:
- [ ] `COOK-F2P-BURN-RATES`: Verify burn rates match OSRS
- [ ] `COOK-F2P-RANGES`: Verify F2P ranges
  - Lumbridge castle range (0% burn bonus?)
  - Rogues' Den fire eternal

### 2.4 Crafting - F2P Content

#### Pottery (F2P)
| Item | Level | Material | Status | Missing |
|------|-------|----------|--------|---------|
| Pot | 1 | Soft clay | 🟡 | Verify F2P pottery wheels | MEDIUM |
| Pie dish | 7 | Soft clay | 🟡 | Verify | MEDIUM |
| Bowl | 8 | Soft clay | 🟡 | Verify | MEDIUM |

**Action Items**:
- [ ] `CRAFT-F2P-POTTERY`: Implement pottery wheel + kiln
  - Barbarian Village pottery wheel
  - Crafting Guild pottery wheel (40 Crafting req)

#### Leatherworking (F2P)
| Item | Level | Material | Status | Missing |
|------|-------|----------|--------|---------|
| Leather gloves | 1 | 1 leather | ✅ | - |
| Leather boots | 7 | 1 leather | ✅ | - |
| Leather cowl | 9 | 1 leather | ✅ | - |
| Leather vambraces | 11 | 1 leather | ✅ | - |
| Leather body | 14 | 3 leather | ✅ | - |
| Leather chaps | 18 | 2 leather | ✅ | - |
| **Hardleather body** | 28 | 1 hard leather | 🟡 | Verify tanning | MEDIUM |
| Coif | 38 | 2 leather | 🟡 | Verify F2P | MEDIUM |

**Action Items**:
- [ ] `CRAFT-F2P-TANNING`: Verify Ellis tanning works (Al Kharid)
- [ ] `CRAFT-F2P-HARD-LEATHER`: Implement hard leather tanning

#### Gem Cutting (F2P)
| Gem | Level | XP | Status | Missing |
|-----|-------|-----|--------|---------|
| Sapphire | 20 | 50 | ✅ | - |
| Emerald | 27 | 67.5 | ✅ | - |
| Ruby | 34 | 85 | ✅ | - |
| Diamond | 43 | 107.5 | ✅ | - |

**Action Items**:
- [ ] `CRAFT-F2P-GEM-CRUSH`: Implement gem crushing mechanics
- [ ] `CRAFT-F2P-CHISEL`: Verify chisel tool availability

#### Jewelry (F2P)
| Item | Level | Materials | Status | Missing |
|------|-------|-----------|--------|---------|
| Gold ring | 5 | 1 gold bar | 🟡 | Verify | LOW |
| Gold necklace | 6 | 1 gold bar | 🟡 | Verify | LOW |
| Gold amulet (u) | 8 | 1 gold bar | 🟡 | Verify | LOW |
| Sapphire ring | 20 | 1 gold + 1 sapphire | 🟡 | Verify | LOW |
| Emerald ring | 27 | 1 gold + 1 emerald | 🟡 | Verify | LOW |
| Ruby ring | 34 | 1 gold + 1 ruby | 🟡 | Verify | LOW |
| Diamond ring | 43 | 1 gold + 1 diamond | 🟡 | Verify | LOW |

**Action Items**:
- [ ] `CRAFT-F2P-JEWELRY`: Implement jewelry crafting at furnace
- [ ] `CRAFT-F2P-AMULET-STRING`: Implement amulet stringing

#### Silver Crafting (F2P)
| Item | Level | Materials | Status | Missing |
|------|-------|-----------|--------|---------|
| Holy symbol | 16 | 1 silver bar | 🟡 | Verify | MEDIUM |
| Unholy symbol | 17 | 1 silver bar | 🟡 | Verify | MEDIUM |
| Tiara | 23 | 1 silver bar | 🟡 | Verify | MEDIUM |

**Action Items**:
- [ ] `CRAFT-F2P-SILVER`: Implement silver crafting
- [ ] `CRAFT-F2P-TIARA`: Important for Runecrafting!

### 2.5 Fletching - F2P Content

| Item | Level | Materials | Status | Missing |
|------|-------|-----------|--------|---------|
| Arrow shafts | 1 | 1 log | ✅ | - |
| Headless arrows | 1 | 1 shaft + 1 feather | ✅ | - |
| Bronze arrows | 1 | 15 headless + 15 heads | ✅ | - |
| Shortbow | 5 | 1 log + 1 string | ✅ | - |
| Longbow | 10 | 1 log + 1 string | ✅ | - |
| Iron arrows | 15 | 15 headless + 15 heads | ✅ | - |
| Oak shortbow | 20 | 1 oak log + 1 string | ✅ | - |
| Oak longbow | 25 | 1 oak log + 1 string | ✅ | - |
| Steel arrows | 30 | 15 headless + 15 heads | ✅ | - |
| Willow shortbow | 35 | 1 willow log + 1 string | ✅ | - |
| Willow longbow | 40 | 1 willow log + 1 string | ✅ | - |
| **Maple shortbow** | 50 | 1 maple log + 1 string | 🟡 | Verify | HIGH |
| **Maple longbow** | 55 | 1 maple log + 1 string | 🟡 | Verify | HIGH |

**Action Items**:
- [ ] `FLETCH-F2P-MAPLE-BOWS`: Implement maple bow fletching
- [ ] `FLETCH-F2P-BOWSTRING`: Verify bow string spinning (Crafting 10)

### 2.6 Firemaking - F2P

| Log | Level | XP | Status | Missing |
|-----|-------|-----|--------|---------|
| Normal | 1 | 40 | ✅ | - |
| Oak | 15 | 60 | ✅ | - |
| Willow | 30 | 90 | ✅ | - |
| **Teak** | 35 | 105 | ❌ | P2P only |
| **Maple** | 45 | 135 | 🟡 | Verify F2P | HIGH |
| **Yew** | 60 | 202.5 | 🟡 | Verify F2P | HIGH |
| **Magic** | 75 | 303.8 | ❌ | P2P only |

**Action Items**:
- [ ] `FM-F2P-MAPLE-FIRE`: Allow maple log burning in F2P
- [ ] `FM-F2P-YEW-FIRE`: Allow yew log burning in F2P

---

## Phase 3: F2P Quests (Week 3-4)

### F2P Quest Status

| Quest | Difficulty | Length | Status | Dependencies | Reward (Combat) |
|-------|------------|--------|--------|--------------|-----------------|
| Cook's Assistant | Novice | Short | ✅ | None | Lumbridge range access |
| Rune Mysteries | Novice | Short | ✅ | None | Runecrafting, essence mining |
| Sheep Shearer | Novice | Short | ✅ | None | Quest points |
| Goblin Diplomacy | Novice | Short | ✅ | None | Quest points, gold bar |
| **Doric's Quest** | Novice | Short | 🟡 | None (or 15 Mining) | Quest points |
| **The Knight's Sword** | Intermediate | Medium | 🟡 | 10 Mining | Blurite sword, Smithing XP |
| **Dragon Slayer I** | Experienced | Long | 🟡 | 32 Quest Points | Rune platebody equip |
| **Demon Slayer** | Novice | Medium | 🟡 | None | Silverlight sword |
| **Vampyre Slayer** | Novice | Short | 🟡 | None | Attack XP, Stake hammer |
| **Ernest the Chicken** | Novice | Short | 🟡 | None | Quest points |
| **Pirate's Treasure** | Novice | Short | 🟡 | None | Quest points, casket |
| **Prince Ali Rescue** | Novice | Medium | 🟡 | None | Quest points, GP |
| **Romeo & Juliet** | Novice | Short | 🟡 | None | Quest points |
| **Imp Catcher** | Novice | Short | 🟡 | None | Magic XP, amulet |
| **Black Knights' Fortress** | Novice | Medium | 🟡 | 12 Quest Points | Quest points |
| **Witch's Potion** | Novice | Short | 🟡 | None | Magic XP |
| **Shield of Arrav** | Novice | Medium | 🟡 | None (partner) | Quest points |

**Critical Quests for F2P**:
1. **Dragon Slayer I** - Unlocks Rune platebody (BIS F2P body)
2. **The Knight's Sword** - Big Smithing XP boost
3. **Demon Slayer** - Silverlight (good vs demons)
4. **Vampyre Slayer** - Attack XP, good for low levels

**Action Items**:
- [ ] `QUEST-F2P-DORIC`: Implement Doric's Quest
- [ ] `QUEST-F2P-KNIGHT-SWORD`: Implement The Knight's Sword
- [ ] `QUEST-F2P-DRAGON-SLAYER`: Implement Dragon Slayer I
- [ ] `QUEST-F2P-DEMON-SLAYER`: Implement Demon Slayer
- [ ] `QUEST-F2P-VAMPYRE-SLAYER`: Implement Vampyre Slayer
- [ ] `QUEST-F2P-ERNEST`: Implement Ernest the Chicken
- [ ] `QUEST-F2P-PIRATE-TREASURE`: Implement Pirate's Treasure
- [ ] `QUEST-F2P-PRINCE-ALI`: Implement Prince Ali Rescue
- [ ] `QUEST-F2P-ROMEO`: Implement Romeo & Juliet
- [ ] `QUEST-F2P-IMP-CATCHER`: Implement Imp Catcher
- [ ] `QUEST-F2P-BKF`: Implement Black Knights' Fortress
- [ ] `QUEST-F2P-WITCH-POTION`: Implement Witch's Potion
- [ ] `QUEST-F2P-SHIELD-ARRAV`: Implement Shield of Arrav (2-player)

---

## Phase 4: F2P Combat Content (Week 4-5)

### 4.1 F2P Weapons (Complete Implementation)

| Weapon | Attack Req | Damage | Speed | Source | Status |
|--------|------------|--------|-------|--------|--------|
| Bronze scimitar | 1 | Low | Fast | Smithing | ✅ |
| Iron scimitar | 1 | Low | Fast | Smithing | ✅ |
| Steel scimitar | 5 | Medium | Fast | Smithing | ✅ |
| Black sword | 10 | Medium | Normal | DROP: Various | 🟡 Verify drops |
| Black scimitar | 10 | Medium | Fast | DROP: Various | 🟡 Verify drops |
| Mithril scimitar | 20 | Good | Fast | Smithing (P2P) | 🟡 Check F2P |
| Adamant scimitar | 30 | Better | Fast | Smithing (P2P) | 🟡 Check F2P |
| **Rune scimitar** | 40 | Best F2P | Fast | Smithing | 🟡 Verify |
| **Rune 2h sword** | 40 | High | Slow | Smithing | 🟡 Verify |
| **Maple shortbow** | Ranged 30 | Medium | Fast | Fletching | 🟡 Verify |
| **Yew shortbow** | Ranged 40 | Good | Fast | Fletching | 🟡 Verify |

**Action Items**:
- [ ] `COMBAT-F2P-BLACK-DROPS`: Add black weapon drops to F2P monsters
- [ ] `COMBAT-F2P-RUNE-SMITH`: Verify rune weapon smithing (99 Smithing)

### 4.2 F2P Armor (Complete Implementation)

| Armor | Defence Req | Slot | Source | Status |
|-------|-------------|------|--------|--------|
| Leather body | 1 | Body | Crafting | ✅ |
| Leather chaps | 1 | Legs | Crafting | ✅ |
| Leather vambraces | 1 | Hands | Crafting | ✅ |
| Leather boots | 1 | Feet | Crafting | ✅ |
| Leather gloves | 1 | Hands | Crafting | ✅ |
| Leather cowl | 1 | Head | Crafting | ✅ |
| Hardleather body | 20 | Body | Crafting | 🟡 Verify |
| Studded body | 20 | Body | Crafting + Smithing | 🟡 Verify |
| Studded chaps | 20 | Legs | Crafting + Smithing | 🟡 Verify |
| **Green d'hide vambs** | 40 | Hands | Crafting | 🟡 Verify |
| **Green d'hide chaps** | 40 | Legs | Crafting | 🟡 Verify |
| **Green d'hide body** | 40 | Body | Crafting | 🟡 Verify |
| **Rune full helm** | 40 | Head | Smithing | 🟡 Verify |
| **Rune platebody** | 40 | Body | Smithing + Quest | 🟡 Verify + Dragon Slayer I |
| **Rune platelegs** | 40 | Legs | Smithing | 🟡 Verify |
| **Rune kiteshield** | 40 | Shield | Smithing | 🟡 Verify |

**Action Items**:
- [ ] `COMBAT-F2P-GREEN-DHIDE`: Implement green d'hide armor crafting
- [ ] `COMBAT-F2P-RUNE-ARMOR`: Implement rune armor smithing
- [ ] `COMBAT-F2P-DRAGON-SLAYER-LOCK`: Lock rune platebody behind Dragon Slayer I

### 4.3 F2P Training Monsters (With Drops)

| Monster | Combat Level | HP | Attack Style | Location | Drop Value |
|---------|--------------|-----|--------------|----------|------------|
| **Hill Giant** | 28 | 35 | Melee | Edgeville Dungeon, Giants Plateau | Giant key (OBOR), Big bones, Limpwurt roots |
| **Moss Giant** | 42 | 60 | Melee | Varrock Sewers, Crandor (F2P?), Moss Giant Island | Mossy key (BRYOPHYTA), Big bones |
| **Ice Giant** | 53 | 70 | Melee | Ice Mountain, Asgarnian Ice | Big bones, Nature runes |
| **Lesser Demon** | 82 | 79 | Magic + Melee | Wizard's Tower (F2P?), Karamja (F2P?) | Rune med helm, Fire runes |
| **Greater Demon** | 92 | 87 | Magic + Melee | Wilderness (F2P) | Rune full helm, Nature runes |
| **Black Knight** | 33 | 42 | Melee | Black Knights' Fortress | Iron/Steel items |
| **Dark Wizard** | 7/20 | Low | Magic | Dark Wizards' Tower, Varrock | Runes, Wizard robes |

**Bosses**:
| Boss | Combat Level | Location | Key | Drops |
|------|--------------|----------|-----|-------|
| **Obor** | 106 | Edgeville Dungeon | Giant key (Hill Giants) | Hill giant club, Rune kiteshield, 500-1000x Limbs |
| **Bryophyta** | 128 | Varrock Sewers | Mossy key (Moss Giants) | Bryophyta's staff, Nature runes, Law runes |

**Action Items**:
- [ ] `NPC-F2P-HILL-GIANT`: Full implementation with drops
- [ ] `NPC-F2P-MOSS-GIANT`: Full implementation with drops
- [ ] `NPC-F2P-OBOR`: Implement Obor boss
- [ ] `NPC-F2P-BRYOPHYTA`: Implement Bryophyta boss
- [ ] `NPC-F2P-ICE-GIANT`: Verify F2P location
- [ ] `NPC-F2P-LESSER-DEMON`: Verify F2P locations

---

## Phase 5: F2P Magic Content (Week 5)

### 5.1 F2P Runecrafting

| Rune | RC Level | Essence | Altar Location | F2P? | Status |
|------|----------|---------|----------------|------|--------|
| Air | 1 | Rune/Pure | West of Varrock | ✅ | ✅ |
| Mind | 2 | Rune/Pure | East of Goblin Village | ✅ | ✅ |
| Water | 5 | Rune/Pure | Lumbridge Swamp | ✅ | ✅ |
| Earth | 9 | Rune/Pure | South of Varrock | ✅ | ✅ |
| Fire | 14 | Rune/Pure | Al Kharid | ✅ | ✅ |
| Body | 20 | Rune/Pure | Edgeville Monastery | ✅ | ✅ |
| Cosmic | 27 | Pure only | Lost City (P2P) | ❌ | - |
| Chaos | 35 | Pure only | Wilderness (P2P) | ❌ | - |
| Nature | 44 | Pure only | Karamja (P2P) | ❌ | - |
| Law | 54 | Pure only | Entrana (P2P) | ❌ | - |

**Action Items**:
- [ ] `RC-F2P-ALTARS`: Verify all 6 F2P altars work
- [ ] `RC-F2P-RUNE-ESSENCE`: Verify rune essence mining (Rune Mysteries required)
- [ ] `RC-F2P-TIARAS`: Implement tiara crafting for teleport access

### 5.2 F2P Magic Spells

| Spell | Magic Level | Runes | Use | Status |
|-------|-------------|-------|-----|--------|
| Wind strike | 1 | 1 Air, 1 Mind | Combat | ✅ |
| Water strike | 5 | 1 Water, 1 Air, 1 Mind | Combat | ✅ |
| Earth strike | 9 | 2 Earth, 1 Air, 1 Mind | Combat | ✅ |
| Fire strike | 13 | 3 Fire, 2 Air, 1 Mind | Combat | ✅ |
| Wind bolt | 17 | 2 Air, 1 Chaos | Combat | ✅ |
| Water bolt | 23 | 2 Water, 2 Air, 1 Chaos | Combat | ✅ |
| Earth bolt | 29 | 3 Earth, 2 Air, 1 Chaos | Combat | ✅ |
| Fire bolt | 35 | 4 Fire, 3 Air, 1 Chaos | Combat | ✅ |
| **Low Alchemy** | 21 | 3 Fire, 1 Nature | Money | 🟡 Verify |
| **Varrock teleport** | 25 | 3 Air, 1 Fire, 1 Law | Teleport | 🟡 Verify F2P |
| **Lumbridge teleport** | 31 | 3 Air, 1 Earth, 1 Law | Teleport | 🟡 Verify F2P |
| **Falador teleport** | 37 | 3 Air, 1 Water, 1 Law | Teleport | 🟡 Verify F2P |
| **High Alchemy** | 55 | 5 Fire, 1 Nature | Money | 🟡 Verify |

**Action Items**:
- [ ] `MAGIC-F2P-TELEPORTS`: Implement F2P city teleports
- [ ] `MAGIC-F2P-ALCH`: Verify High/Low Alchemy
- [ ] `MAGIC-F2P-STAFFS`: Verify elemental staffs (unlimited runes)

---

## Phase 6: F2P Money Making (Week 5-6)

### 6.1 F2P Money Makers

| Method | Req | GP/Hour | Status | Missing |
|--------|-----|---------|--------|---------|
| **Yew logs** | WC 60 | 50-100k | 🟡 | Need yew trees in F2P |
| **Lobsters** | Fish 40 | 30-60k | ✅ | - |
| **Swordfish** | Fish 50 | 40-80k | ✅ | - |
| **Cowhides** | Combat | 20-40k | ✅ | - |
| **Iron ore** | Mine 15 | 30-50k | ✅ | - |
| **Coal** | Mine 30 | 20-40k | ✅ | - |
| **Gold ore** | Mine 40 | 50k | ✅ | - |
| **High Alchemy** | Magic 55 | Varies | 🟡 | Need runes |
| **Wine of Zamorak** | Telegrab | 100k+ | 🟡 | Implement chaos temple |
| **Air orbs** | Magic 66 | 200k+ | 🟡 | P2P only? |
| **Big bones** | Combat | 30-50k | 🟡 | Hill/Moss giants |
| **Limpwurt roots** | Combat | 40k | 🟡 | Hill giants |

**Action Items**:
- [ ] `MONEY-F2P-YEWS`: Add F2P yew locations for money making
- [ ] `MONEY-F2P-WINE-ZAMORAK`: Implement Wine of Zamorak at Chaos Temple
- [ ] `MONEY-F2P-OBOR`: Implement Obor boss for consistent money

---

## Implementation Priority Summary

### Critical Path (Do First)

**Week 1-2: Foundation**
1. `WC-F2P-MAPLE-YEW-TREES` - Blocks F2P money making, Fletching
2. `SMITH-F2P-RUNE-EQUIPMENT` - Blocks BIS F2P gear
3. `CRAFT-F2P-GREEN-DHIDE` - Blocks BIS F2P ranged
4. `FLETCH-F2P-MAPLE-YEW-BOWS` - Blocks F2P ranged
5. `NPC-F2P-HILL-MOSS-GIANTS` - Blocks F2P training, money

**Week 3-4: Quests**
6. `QUEST-F2P-DRAGON-SLAYER` - Unlocks rune platebody
7. `QUEST-F2P-KNIGHT-SWORD` - Big Smithing XP
8. `QUEST-F2P-OTHER` - Other F2P quests

**Week 5-6: Polish**
9. `MAGIC-F2P-TELEPORTS` - Quality of life
10. `NPC-F2P-OBOR-BRYOPHYTA` - Boss content

### F2P Definition of Done

- [ ] All F2P skills trainable to 99
- [ ] All F2P quests completable
- [ ] BIS F2P gear obtainable (Rune scim/plate, Green d'hide, Maple bow)
- [ ] F2P money makers functional (Yews, Lobsters, Giants, Alching)
- [ ] F2P training spots viable (Giants, Mossies)
- [ ] F2P bosses accessible (Obor, Bryophyta)

---

**Document Status**: Ready for task generation
**Next Step**: Create agent-tasks entries for critical path items

