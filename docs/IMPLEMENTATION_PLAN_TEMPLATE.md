# Rev 233 Feature Completeness Implementation Plan

**Owner:** Gemini  
**Created:** 2026-02-21  
**Status:** Draft  
**Goal:** Ship complete Rev 233 implementation with systematic approach

---

## 📋 Current State Assessment

### What's Implemented (From Testing)
| Feature | Status | Notes |
|---------|--------|-------|
| Woodcutting | ✅ | Trees, XP, logs - TESTED |
| Mining | ✅ | Rocks, XP, ore - TESTED |
| Fishing | ✅ | Spots, XP, fish - TESTED |
| Cooking | ✅ | Ranges, fire, burn - TESTED |
| Firemaking | ✅ | Logs, tinderbox - TESTED |
| Thieving | ✅ | Pickpocket, stuns, loot - TESTED |
| Combat (Melee) | ✅ | Attack, damage, XP - TESTED |
| Banking | ✅ | Deposit, withdraw - TESTED |
| Prayer | ⚠️ | Basic activation - NEEDS VERIFY |
| Smithing | ⚠️ | Bars, anvils - NEEDS VERIFY |
| Equipment | ⚠️ | Equip/unequip - NEEDS VERIFY |
| Magic | ❓ | Not tested |
| Ranged | ❓ | Not tested |
| Agility | ❓ | Not tested |
| Herblore | ❓ | Not tested |
| Crafting | ❓ | Not tested |
| Fletching | ❓ | Not tested |
| Runecrafting | ❓ | Not tested |
| Slayer | ❓ | Not tested |
| Farming | ❓ | Not tested |
| Hunter | ❓ | Not tested |
| Construction | ❓ | Not tested |

**Last Test Run:** [Date]  
**Test Results:** [Link to test output]  
**Pass Rate:** X%

---

## 🎯 Implementation Philosophy

### Keep It Vanilla
- **Source of Truth:** OSRS Wiki (oldschool.runescape.wiki)
- **Cache Version:** Rev 233 target (already configured)
- **No Custom Content:** Pure OSRS mechanics only
- **XP Rates:** Match OSRS exactly
- **Drop Tables:** Match OSRS exactly
- **Animations:** Use correct cache IDs

### Integration Points
- **AgentBridge:** Already exists (port 43595)
- **MCP Server:** Enhanced version ready
- **Bot Framework:** High-level BotActions ready
- **Test Suite:** Automated tester ready

---

## 📊 Feature Completeness Matrix

### Core Skills (P0 - Must Have)

#### 1. Woodcutting
| Tier | Tree | Level | XP | Status | Assigned |
|------|------|-------|-----|--------|----------|
| 1 | Tree/Dead tree | 1 | 25 | ✅ | - |
| 5 | Oak tree | 15 | 37.5 | ❓ | Gemini |
| 15 | Willow tree | 30 | 67.5 | ❓ | Gemini |
| 30 | Maple tree | 45 | 100 | ❓ | Gemini |
| 45 | Yew tree | 60 | 175 | ❓ | Gemini |
| 60 | Magic tree | 75 | 250 | ❓ | Gemini |

**Mechanics:**
- [ ] Tree depletion (chance based)
- [ ] Axe level requirements
- [ ] Axe wield bonus
- [ ] Bird nests (rare)
- [ ] Multiple logs per tree

**Wiki Reference:** https://oldschool.runescape.wiki/w/Woodcutting

---

#### 2. Mining
| Ore | Level | XP | Status | Assigned |
|-----|-------|-----|--------|----------|
| Clay | 1 | 5 | ❓ | Gemini |
| Copper | 1 | 17.5 | ✅ | - |
| Tin | 1 | 17.5 | ✅ | - |
| Iron | 15 | 35 | ❓ | Gemini |
| Coal | 30 | 50 | ❓ | Gemini |
| Gold | 40 | 65 | ❓ | Gemini |
| Mithril | 55 | 80 | ❓ | Gemini |
| Adamantite | 70 | 95 | ❓ | Gemini |
| Runite | 85 | 125 | ❓ | Gemini |

**Mechanics:**
- [ ] Rock depletion
- [ ] Pickaxe speed bonus
- [ ] Gem drops (while mining)
- [ ] Prospector kit bonus (if implemented)

**Wiki Reference:** https://oldschool.runescape.wiki/w/Mining

---

#### 3. Fishing
| Fish | Level | XP | Tool | Status | Assigned |
|------|-------|-----|------|--------|----------|
| Shrimp/Anchovies | 1 | 10 | Net | ✅ | - |
| Sardine/Herring | 5 | 20 | Bait | ❓ | Gemini |
| Trout/Salmon | 20 | 50/70 | Fly | ❓ | Gemini |
| Pike | 25 | 60 | Bait | ❓ | Gemini |
| Tuna/Swordfish | 35/50 | 80/100 | Harpoon | ❓ | Gemini |
| Lobster | 40 | 90 | Pot | ❓ | Gemini |
| Shark | 76 | 110 | Harpoon | ❓ | Gemini |

**Mechanics:**
- [ ] Fishing spots move
- [ ] Tool requirements
- [ ] Bait consumption
- [ ] Multiple fish types per spot

**Wiki Reference:** https://oldschool.runescape.wiki/w/Fishing

---

#### 4. Cooking
| Food | Level | XP | Heals | Status | Assigned |
|------|-------|-----|-------|--------|----------|
| Meat/Shrimps | 1 | 30 | 3 | ✅ | - |
| Bread | 1 | 40 | 5 | ❓ | Gemini |
| Sardine | 1 | 40 | 4 | ❓ | Gemini |
| Herring | 5 | 50 | 5 | ❓ | Gemini |
| Trout | 15 | 70 | 7 | ❓ | Gemini |
| Pike | 20 | 80 | 8 | ❓ | Gemini |
| Salmon | 25 | 90 | 9 | ❓ | Gemini |
| Tuna | 30 | 100 | 10 | ❓ | Gemini |
| Lobster | 40 | 120 | 12 | ❓ | Gemini |
| Swordfish | 45 | 140 | 14 | ❓ | Gemini |
| Shark | 80 | 210 | 20 | ❓ | Gemini |

**Mechanics:**
- [ ] Burn rates (level-based)
- [ ] Range bonus vs fire
- [ ] Cooking gauntlets (if implemented)
- [ ] Wine making

**Wiki Reference:** https://oldschool.runescape.wiki/w/Cooking

---

### Artisan Skills (P0 - Must Have)

#### 5. Firemaking
| Log | Level | XP | Status | Assigned |
|-----|-------|-----|--------|----------|
| Logs | 1 | 40 | ✅ | - |
| Oak | 15 | 60 | ❓ | Gemini |
| Willow | 30 | 90 | ❓ | Gemini |
| Maple | 45 | 135 | ❓ | Gemini |
| Yew | 60 | 202.5 | ❓ | Gemini |
| Magic | 75 | 303.8 | ❓ | Gemini |

**Mechanics:**
- [ ] Tinderbox required
- [ ] Fire duration
- [ ] Can't light on other fires
- [ ] Clue scrolls (burning)

---

#### 6. Smithing
| Bar | Level | XP (Smelt) | Status | Assigned |
|-----|-------|------------|--------|----------|
| Bronze | 1 | 6.2 | ✅ | - |
| Iron | 15 | 12.5 | ❓ | Gemini |
| Steel | 30 | 17.5 | ❓ | Gemini |
| Gold | 40 | 22.5 (56.2 with gauntlets) | ❓ | Gemini |
| Mithril | 50 | 30 | ❓ | Gemini |
| Adamantite | 70 | 37.5 | ❓ | Gemini |
| Runite | 85 | 50 | ❓ | Gemini |

**Item Smithing (Bronze example):**
| Item | Bars | Level | XP | Status |
|------|------|-------|-----|--------|
| Dagger | 1 | 1 | 12.5 | ❓ |
| Axe | 1 | 1 | 12.5 | ❓ |
| Mace | 1 | 2 | 12.5 | ❓ |
| Med helm | 1 | 3 | 12.5 | ❓ |
| Sword | 1 | 4 | 12.5 | ❓ |
| Scimitar | 2 | 5 | 25 | ❓ |
| Longsword | 2 | 6 | 25 | ❓ |
| Full helm | 2 | 7 | 25 | ❓ |
| Square shield | 2 | 8 | 25 | ❓ |
| Warhammer | 3 | 9 | 37.5 | ❓ |
| Battleaxe | 3 | 10 | 37.5 | ❓ |
| Chainbody | 3 | 11 | 37.5 | ❓ |
| Kiteshield | 3 | 12 | 37.5 | ❓ |
| Platelegs | 3 | 16 | 37.5 | ❓ |
| Plateskirt | 3 | 16 | 37.5 | ❓ |
| Platebody | 5 | 18 | 62.5 | ❓ |
| 2h sword | 3 | 14 | 37.5 | ❓ |
| Claws | 2 | 13 | 25 | ❓ |
| Arrowtips | 1 | 5 | 12.5 | ❓ |
| Nails | 1 | 4 | 12.5 | ❓ |
| Dart tips | 1 | 4 | 12.5 | ❓ |
| Bolts | 1 | 3 | 12.5 | ❓ |

**Wiki Reference:** https://oldschool.runescape.wiki/w/Smithing

---

#### 7. Crafting
| Item | Level | XP | Materials | Status |
|------|-------|-----|-----------|--------|
| Leather gloves | 1 | 13.8 | 1 leather | ❓ |
| Leather boots | 7 | 16.25 | 1 leather | ❓ |
| Leather cowl | 9 | 18.5 | 1 leather | ❓ |
| Leather vambraces | 11 | 22 | 1 leather | ❓ |
| Leather body | 14 | 25 | 1 leather | ❓ |
| Leather chaps | 18 | 27 | 1 leather | ❓ |
| Hard leather body | 28 | 35 | 1 hard leather | ❓ |
| Coif | 38 | 37 | 1 leather | ❓ |
| Studded body | 41 | 40 | 1 leather + studs | ❓ |
| Studded chaps | 44 | 42 | 1 leather + studs | ❓ |

**Gems:**
| Gem | Level | XP | Status |
|-----|-------|-----|--------|
| Opal | 1 | 15 | ❓ |
| Jade | 13 | 20 | ❓ |
| Red topaz | 16 | 25 | ❓ |
| Sapphire | 20 | 50 | ❓ |
| Emerald | 27 | 67.5 | ❓ |
| Ruby | 34 | 85 | ❓ |
| Diamond | 43 | 107.5 | ❓ |
| Dragonstone | 55 | 137.5 | ❓ |
| Onyx | 67 | 167.5 | ❓ |

**Wiki Reference:** https://oldschool.runescape.wiki/w/Crafting

---

#### 8. Fletching
| Item | Level | XP | Materials | Status |
|------|-------|-----|-----------|--------|
| Arrow shafts | 1 | 5 | 1 log | ❓ |
| Shortbow (u) | 5 | 5 | 1 log | ❓ |
| Longbow (u) | 10 | 10 | 1 log | ❓ |
| Oak shortbow (u) | 20 | 16.5 | 1 oak | ❓ |
| Oak longbow (u) | 25 | 25 | 1 oak | ❓ |

**Arrows:**
| Type | Level | XP (head) | XP (complete) | Status |
|------|-------|-----------|---------------|--------|
| Headless arrows | 1 | 1 | - | ❓ |
| Bronze arrows | 1 | 1.3 | 2.6 | ❓ |
| Iron arrows | 15 | 2.5 | 5 | ❓ |
| Steel arrows | 30 | 5 | 10 | ❓ |

**Wiki Reference:** https://oldschool.runescape.wiki/w/Fletching

---

### Combat Skills (P0 - Must Have)

#### Combat Stats
| NPC | HP | Attack | Strength | Defence | Status |
|-----|-----|--------|----------|---------|--------|
| Man/Woman | 7 | 1 | 1 | 1 | ✅ |
| Goblin | 5 | 1 | 1 | 1 | ❓ |
| Chicken | 3 | 1 | 1 | 1 | ❓ |
| Cow | 8 | 1 | 1 | 1 | ❓ |
| Giant rat | 5 | 1 | 1 | 1 | ❓ |
| Giant spider | 5 | 1 | 1 | 1 | ❓ |
| Guard | 22 | 19 | 18 | 13 | ❓ |
| Dark wizard | 24 | 1 | 1 | 1 | ❓ |
| Farmer | 12 | 3 | 4 | 8 | ❓ |
| Barbarian | 18 | 6 | 5 | 5 | ❓ |

**Wiki Reference:** https://oldschool.runescape.wiki/w/Bestiary

---

#### 9. Prayer
| Prayer | Level | Effect | Drain | Status |
|--------|-------|--------|-------|--------|
| Thick Skin | 1 | +5% Defence | 1 pt/tick | ❓ |
| Burst of Strength | 4 | +5% Strength | 1 pt/tick | ❓ |
| Clarity of Thought | 7 | +5% Attack | 1 pt/tick | ❓ |
| Sharp Eye | 8 | +5% Ranged | 1 pt/tick | ❓ |
| Mystic Will | 9 | +5% Magic | 1 pt/tick | ❓ |
| Rock Skin | 10 | +10% Defence | 2 pt/tick | ❓ |
| Superhuman Strength | 13 | +10% Strength | 2 pt/tick | ❓ |
| Improved Reflexes | 16 | +10% Attack | 2 pt/tick | ❓ |
| Protect Item | 25 | Keep 1 item on death | 1 pt/tick | ❓ |

**Wiki Reference:** https://oldschool.runescape.wiki/w/Prayer

---

### Advanced Skills (P1 - Important)

#### 10. Thieving (Already Implemented)
| Target | Level | XP | Loot | Status |
|--------|-------|-----|------|--------|
| Man/Woman | 1 | 8 | 3gp, bronze bolts | ✅ |
| Farmer | 10 | 14.5 | Seeds | ❓ |
| Female HAM | 15 | 18.5 | Clues, jewelry | ❓ |
| Male HAM | 20 | 22.2 | Clues, jewelry | ❓ |
| Warrior | 25 | 26 | 18gp | ❓ |
| Rogue | 32 | 36.5 | Lockpick, seeds | ❓ |
| Guard | 40 | 46.8 | 30gp | ❓ |
| Knight | 55 | 84.3 | 50gp | ❓ |
| Watchman | 65 | 137.5 | 60gp, bread | ❓ |
| Paladin | 70 | 151.8 | 80gp, 2 chaos | ❓ |
| Gnome | 75 | 198.3 | 300gp, items | ❓ |
| Hero | 80 | 273.3 | 300gp, items | ❓ |

**Stalls:**
| Stall | Level | XP | Loot | Status |
|-------|-------|-----|------|--------|
| Vegetable | 2 | 10 | Potato, onion, tomato | ❓ |
| Baker's | 5 | 16 | Cake, bread, choc slice | ❓ |
| Crafting | 5 | 20 | Chisel, ring mold | ❓ |
| Monkey food | 5 | 16 | Banana | ❓ |
| Tea | 5 | 16 | Cup of tea | ❓ |
| Silk | 20 | 24 | Silk | ❓ |
| Wine | 22 | 27 | Jug of water/wine | ❓ |
| Seed | 27 | 10 | Seeds | ❓ |
| Fur | 35 | 36 | Grey wolf fur | ❓ |
| Fish | 42 | 42 | Raw salmon/tuna | ❓ |
| Silver | 50 | 54 | Silver ore | ❓ |
| Spice | 65 | 81 | Spice | ❓ |
| Magic | 65 | 100 | Air/Mind runes | ❓ |
| Scimitar | 65 | 100 | Iron scimitar | ❓ |

**Wiki Reference:** https://oldschool.runescape.wiki/w/Thieving

---

#### 11. Herblore
| Potion | Level | XP | Primary | Secondary | Status |
|--------|-------|-----|---------|-----------|--------|
| Attack potion (3) | 3 | 25 | Guam | Eye of newt | ❓ |
| Antipoison (3) | 5 | 37.5 | Marrentill | Unicorn horn dust | ❓ |
| Strength potion (3) | 12 | 50 | Tarromin | Limpwurt root | ❓ |
| Restore potion (3) | 22 | 62.5 | Harralander | Red spiders' eggs | ❓ |
| Energy potion (3) | 26 | 67.5 | Harralander | Chocolate dust | ❓ |
| Defence potion (3) | 30 | 75 | Ranarr weed | White berries | ❓ |
| Prayer potion (3) | 38 | 87.5 | Ranarr weed | Snape grass | ❓ |

**Wiki Reference:** https://oldschool.runescape.wiki/w/Herblore

---

## 🔧 Implementation Guidelines

### Code Structure
```
rsmod/content/skills/{skill}/
├── build.gradle.kts
└── src/main/kotlin/org/rsmod/content/skills/{skill}/
    ├── {Skill}.kt              # Main plugin
    ├── {Skill}Objs.kt          # Object references
    └── {Skill}Npcs.kt          # NPC references (if needed)
```

### Wiki Data Format
```json
// wiki-data/skills/{skill}.json
{
  "skill": "smithing",
  "recipes": [
    {
      "name": "Bronze bar",
      "level": 1,
      "xp": 6.2,
      "inputs": [{ "id": 436, "qty": 1 }, { "id": 438, "qty": 1 }],
      "output": { "id": 2349, "qty": 1 },
      "loc_pattern": "Furnace"
    }
  ],
  "test_locations": [
    { "desc": "Lumbridge furnace", "x": 3228, "z": 3256 }
  ]
}
```

### Implementation Checklist Per Skill
- [ ] Wiki data JSON created
- [ ] Kotlin plugin implemented
- [ ] Event handlers (onOpLoc, onOpNpc, etc.)
- [ ] XP grants
- [ ] Level requirements checked
- [ ] Tool requirements checked
- [ ] Resource depletion (if applicable)
- [ ] Success/failure mechanics
- [ ] Animations (correct IDs from cache)
- [ ] Sound effects (if applicable)
- [ ] Bot test script created
- [ ] Unit tests (optional)
- [ ] Build passes
- [ ] Integration tested

---

## 🧪 Testing Integration

### Automated Testing
```bash
# Run full test suite
bun bots/rev233_tester.ts {player}

# Run specific skill test
bun bots/test_{skill}.ts {player}
```

### Test Requirements Per Feature
- [ ] Bot can perform action 10 times without error
- [ ] XP matches wiki exactly
- [ ] Animations play
- [ ] No console errors
- [ ] State updates correctly

---

## 📈 Shipping Criteria

### Phase 1: MVP (Core 6 Skills)
- [ ] Woodcutting - All trees
- [ ] Mining - All ores
- [ ] Fishing - All methods
- [ ] Cooking - All foods
- [ ] Firemaking - All logs
- [ ] Combat - Basic melee

### Phase 2: Artisan
- [ ] Smithing - All bars + items
- [ ] Crafting - Leather + gems
- [ ] Fletching - Bows + arrows
- [ ] Herblore - Potions

### Phase 3: Advanced
- [ ] Thieving - All NPCs + stalls
- [ ] Prayer - All prayers
- [ ] Magic - Spells
- [ ] Ranged - Bows + ammo

### Phase 4: Completion
- [ ] All skills 100%
- [ ] All NPCs combat-ready
- [ ] All objects interactive
- [ ] Quest system (if applicable)

---

## 📚 Reference Links

- **OSRS Wiki:** https://oldschool.runescape.wiki
- **Rev 233 Cache Symbols:** `rsmod/.data/symbols/`
- **Test Framework:** `bots/rev233_tester.ts`
- **Current Test Results:** `docs/testing/`
- **Skill Learnings:** `docs/bot-patterns/`

---

## 🎯 Next Steps

1. **Fill in Status Column** - What's actually implemented?
2. **Assign Priority** - What order to implement?
3. **Create Wiki Data** - Scrape/create JSON oracles
4. **Implement Skills** - One at a time
5. **Test & Validate** - Use automated tester
6. **Document Gaps** - What can't be done?

---

**Note:** This is a living document. Update as implementation progresses.

