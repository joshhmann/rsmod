# F2P Implementation Work Plan

**Total Tasks:** 255  
**Target:** Complete F2P MVP  
**Estimated Timeline:** 10-12 weeks with parallel execution  

---

## Phase 0: Foundation & Infrastructure (Week 1)

**Goal:** Core infrastructure that everything else depends on

### Critical Path Tasks (Sequential)
| Task ID | Title | Owner | Est. Hours | Blockers |
|---------|-------|-------|------------|----------|
| WORLD-LADDERS-F2P | F2P Ladder Network | TBD | 4 | None |
| WORLD-GATES-F2P | F2P Gate Systems | TBD | 6 | None |
| WORLD-BANKS-F2P | F2P Bank Locations | TBD | 8 | None |
| WORLD-DEPOSIT-BOX | F2P Deposit Boxes | TBD | 4 | WORLD-BANKS-F2P |
| SYSTEM-DEATH-RESPAWN | Death System | TBD | 8 | None |
| SYSTEM-COMBAT-LEVEL | Combat Level Display | TBD | 4 | None |

**Parallel Workstreams:**
- **Stream A:** World infrastructure (ladders, gates, banks) - 3 devs
- **Stream B:** Core systems (death, combat level) - 2 devs

**Definition of Done:**
- [ ] Player can navigate all F2P areas
- [ ] Banking works at all locations
- [ ] Death and respawn functional
- [ ] Combat levels display correctly

---

## Phase 1: Core Gathering Skills (Week 2-3)

**Goal:** All F2P gathering skills functional

### Parallel Workstreams

#### Stream A: Woodcutting & Mining (2 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| WORLD-TREE-F2P | Tree Locations | 6 |
| WORLD-MINE-F2P | Mining Locations | 6 |
| MINING-GEMS | Gem Rocks + Guild | 4 |

#### Stream B: Fishing & Cooking (2 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| WORLD-FISH-F2P | Fishing Spots | 6 |
| BOTS-FISH | Fishing Bot Test | 4 |
| BOTS-COOK | Cooking Bot Test | 4 |

#### Stream C: Agility & Thieving (2 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| AGILITY-2 | Barbarian Outpost | 8 |
| AGILITY-3 | Wilderness Course | 6 |
| AGILITY-4 | Seers Village Course | 6 |

**Definition of Done:**
- [ ] All gathering skills grant XP
- [ ] Resources respawn correctly
- [ ] Bot tests pass for each skill
- [ ] Build passes for all modules

---

## Phase 2: Processing Skills (Week 3-4)

**Goal:** All F2P processing skills functional

### Parallel Workstreams

#### Stream A: Smithing (2 devs)
| Task ID | Title | Est. Hours | Dependencies |
|---------|-------|------------|--------------|
| WORLD-FURNACE | Furnace Locations | 4 | None |
| WORLD-ANVIL | Anvil Locations | 4 | None |
| SMITH-2 | Furnace Interaction | 6 | WORLD-FURNACE |
| MAKEQ-SMITH | Make-X Interface | 8 | SMITH-2 |
| BOTS-SMITH | Smithing Bot Test | 4 | MAKEQ-SMITH |

#### Stream B: Crafting (2 devs)
| Task ID | Title | Est. Hours | Dependencies |
|---------|-------|------------|--------------|
| WORLD-SPINNING | Spinning Wheels | 4 | None |
| CRAFT-GEMS | Gem Cutting | 6 | None |
| CRAFT-JEWEL | Jewellery Making | 8 | None |
| CRAFT-LEATHER | Leather Working | 6 | None |
| MAKEQ-CRAFT | Make-X Interface | 6 | None |
| BOTS-CRAFT | Crafting Bot Test | 4 | All crafting |

#### Stream C: Firemaking & Fletching (2 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| BOTS-FM | Firemaking Test | 4 |
| BOTS-FLETCH | Fletching Test | 6 |
| MAKEQ-FLETCH | Make-X Interface | 6 |

#### Stream D: Herblore & Prayer (2 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| BOTS-HERB | Herblore Test | 6 |
| MAKEQ-HERB | Make-X Interface | 6 |
| WORLD-ALTAR | Altar Locations | 4 |
| BOTS-PRAY | Prayer Test | 4 |

**Definition of Done:**
- [ ] All processing skills grant correct XP
- [ ] Make-X interfaces work
- [ ] All skill bot tests pass
- [ ] No build failures

---

## Phase 3: NPC Ecosystem (Week 4-5)

**Goal:** Combat NPCs with drops populate the world

### Parallel Workstreams

#### Stream A: F2P Combat NPCs (3 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| NPC-SKELETON-F2P | Skeleton Combat | 4 |
| NPC-ZOMBIE-F2P | Zombie Combat | 4 |
| NPC-HILL-GIANT-F2P | Hill Giant Combat | 4 |
| NPC-MAN-WOMAN-COMB | Man/Woman Combat | 3 |
| NPC-BARBARIAN-COMB | Barbarian Combat | 3 |
| NPC-WIZARD-F2P | Wizard Combat | 4 |

#### Stream B: Drop Tables (3 devs)
| Task ID | Title | Est. Hours | Dependencies |
|---------|-------|------------|--------------|
| NPC-DROP-SKELETON | Skeleton Drops | 4 | NPC-SKELETON-F2P |
| NPC-DROP-ZOMBIE | Zombie Drops | 4 | NPC-ZOMBIE-F2P |
| NPC-DROP-HILL-GIANT | Hill Giant Drops | 4 | NPC-HILL-GIANT-F2P |
| NPC-DROP-MAN-WOMAN | Man/Woman Drops | 3 | NPC-MAN-WOMAN-COMB |
| NPC-DROP-BARBARIAN | Barbarian Drops | 3 | NPC-BARBARIAN-COMB |
| NPC-DROP-WIZARD | Wizard Drops | 4 | NPC-WIZARD-F2P |
| NPC-DROP-IMPS | Imp Drops | 4 | None |

#### Stream C: Additional NPCs (2 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| NPC-MUGGER-COMB | Mugger Combat | 3 |
| NPC-DWARF-COMB | Dwarf Combat | 3 |
| NPC-JAIL-GUARD-COMB | Jail Guard Combat | 3 |
| NPC-WARRIOR-WOMAN-COMB | Warrior Combat | 3 |
| NPC-UNICORN-COMB | Unicorn Combat | 3 |

**Definition of Done:**
- [ ] All F2P NPCs have combat stats
- [ ] All NPCs have drop tables
- [ ] Drops match OSRS rates
- [ ] Bot tests verify combat

---

## Phase 4: Dungeons & Areas (Week 5-6)

**Goal:** All F2P dungeons and key areas accessible

### Parallel Workstreams

#### Stream A: Dungeons (3 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| AREA-DRAYNOR-SEWER | Draynor Sewer | 6 |
| AREA-EDGEVILLE-DUNG | Edgeville Dungeon | 8 |
| AREA-VARROCK-SEWER | Varrock Sewer | 8 |
| WORLD-DWARVEN-MINE | Dwarven Mine | 8 |

#### Stream B: Wilderness (2 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| AREA-WILDERNESS-F2P | Wilderness F2P | 8 |
| AREA-WILD-VOLCANO | Wilderness Volcano | 4 |
| WORLD-OUTLAW-CAMP | Outlaw Camp | 4 |
| WILD-1 | Skulling System | 6 |
| WILD-2 | Item Protection | 6 |
| WILD-3 | Combat Ranges | 4 |

#### Stream C: Cities & Locations (3 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| AREA-LUM-SWAMP | Lumbridge Swamp | 4 |
| AREA-WIZARD-TOWER | Wizards Tower | 6 |
| AREA-DARK-WIZARD | Dark Wizards Tower | 4 |
| AREA-COW-PEN | Cow/Chicken Pens | 4 |
| AREA-WHEAT-FIELD | Wheat Fields | 3 |
| AREA-POTATO-ONION | Vegetable Fields | 3 |

**Definition of Done:**
- [ ] All dungeons accessible
- [ ] All monster spawns placed
- [ ] Wilderness systems work
- [ ] All areas explorable

---

## Phase 5: Economy & Shops (Week 6-7)

**Goal:** F2P economy fully functional

### Parallel Workstreams

#### Stream A: General Stores (2 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| SHOP-LUMBRIDGE-GEN | Lumbridge Store | 4 |
| SHOP-VARROCK-GEN | Varrock Store | 4 |
| SHOP-FALADOR-GEN | Falador Store | 4 |
| SHOP-DRAYNOR-GEN | Draynor Store | 4 |
| SHOP-ALKHARID-GEN | Al Kharid Store | 4 |
| SHOP-PORTSARIM-GEN | Port Sarim Store | 4 |
| SHOP-EDGEVILLE-GEN | Edgeville Store | 4 |
| SHOP-GEN-STORE | Standardize Stock | 6 |

#### Stream B: Specialty Shops (3 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| NPC-ELLIS | Ellis Tanner | 4 |
| NPC-KARIM | Kebab Seller | 3 |
| NPC-LOU-E-G | Platelegs Shop | 3 |
| NPC-RANAEL | Plateskirts Shop | 3 |
| NPC-ZEKE | Scimitar Shop | 3 |
| NPC-DOMMIK | Crafting Shop | 3 |
| NPC-GEM-TRADER | Gem Trader | 4 |
| NPC-SILK-TRADER | Silk Trader | 4 |
| SHOP-BETTY | Magic Shop | 4 |

#### Stream C: NPCs & Dialogues (2 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| NPC-APOTHECARY | Apothecary | 4 |
| NPC-BANKER-DIALOGUE | Bank Dialogues | 6 |
| NPC-PRIEST | Priests | 3 |
| SHOP-ROMEO | Romeo | 3 |
| SHOP-JULIET | Juliet | 3 |
| NPC-HANS | Hans | 2 |
| NPC-STARTER | Starter NPCs | 4 |
| SYSTEM-TUTOR-F2P | Skill Tutors | 6 |
| SYSTEM-ADVISOR | Lumbridge Advisor | 4 |

**Definition of Done:**
- [ ] All shops functional
- [ ] Buy/sell prices correct
- [ ] Shop stock replenishes
- [ ] All NPC dialogues work

---

## Phase 6: Quests (Week 7-9)

**Goal:** All 13 F2P quests fully playable

### Parallel Workstreams

#### Stream A: Tutorial Quests (2 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| QUEST-BOT-COOK | Cooks Assistant | 6 |
| QUEST-BOT-SHEEP | Sheep Shearer | 6 |
| QUEST-BOT-IMP | Imp Catcher | 6 |
| QUEST-BOT-WITCH | Witchs Potion | 4 |

#### Stream B: Medium Quests (2 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| QUEST-BOT-RUNE | Rune Mysteries | 6 |
| QUEST-BOT-RESTLESS | Restless Ghost | 6 |
| QUEST-BOT-DORIC | Dorics Quest | 4 |
| QUEST-BOT-GOBLIN | Goblin Diplomacy | 8 |

#### Stream C: Major Quests (3 devs)
| Task ID | Title | Est. Hours | Dependencies |
|---------|-------|------------|--------------|
| QUEST-BOT-ERNEST | Ernest Chicken | 8 | None |
| QUEST-BOT-BLACK-KNIGHT | Black Knights | 10 | None |
| QUEST-BOT-PIRATE | Pirates Treasure | 8 | None |
| QUEST-BOT-PRINCE | Prince Ali Rescue | 12 | All Prince Ali NPCs |
| QUEST-BOT-VAMPYRE | Vampyre Slayer | 10 | NPC-DRAYNOR-VAMP |
| QUEST-BOT-DRAGON | Dragon Slayer I | 15 | WORLD-ELVARG |

**Quest NPCs (Phase 6 Pre-req):**
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| NPC-HASSAN | Hassan | 3 |
| NPC-OSMAN | Osman | 3 |
| NPC-LEELA | Leela | 3 |
| NPC-LADY-KELI | Lady Keli | 3 |
| NPC-NED | Ned | 2 |
| NPC-AGGIE | Aggie | 3 |
| NPC-MORGAN | Morgan | 2 |
| NPC-DR-HARLOW | Dr Harlow | 2 |
| NPC-GOBLIN-CHIEF | Goblin Generals | 4 |
| WORLD-GOBLIN-MAIL | Goblin Mail Spawns | 2 |

**Definition of Done:**
- [ ] All 13 quests completable
- [ ] Quest state transitions work
- [ ] Rewards granted correctly
- [ ] Bot tests pass for all quests

---

## Phase 7: Bosses & Endgame (Week 9)

**Goal:** Boss encounters for major quests

### Tasks
| Task ID | Title | Est. Hours | Dependencies |
|---------|-------|------------|--------------|
| NPC-DRAYNOR-VAMP | Count Draynor | 8 | QUEST-BOT-VAMPYRE |
| WORLD-ELVARG | Elvarg Boss | 12 | QUEST-BOT-DRAGON |
| AREA-CRANDOR | Crandor Island | 6 | WORLD-ELVARG |
| WORLD-CRANDOR-ACCESS | Crandor Access | 4 | AREA-CRANDOR |

**Definition of Done:**
- [ ] Both bosses fightable
- [ ] Special attacks work
- [ ] Drops correct
- [ ] Quests complete with bosses

---

## Phase 8: Polish & Systems (Week 10-11)

**Goal:** F2P MVP polish and remaining systems

### Parallel Workstreams

#### Stream A: Systems (2 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| SYSTEM-LEVELUP | Level Up Messages | 4 |
| SYSTEM-EMOTE-F2P | F2P Emotes | 6 |
| SYSTEM-LORE | NPC Examines | 8 |
| SYSTEM-LORE-LOC | Object Examines | 8 |
| SYSTEM-LORE-ITEM | Item Examines | 8 |
| SYSTEM-MUSIC-F2P | Music Tracks | 6 |
| SYSTEM-RANDOM-F2P | Random Events | 10 |
| SYSTEM-CLUE | Clue Scrolls | 12 |
| SYSTEM-DIARY | Achievement Diary | 10 |

#### Stream B: Guilds & Advanced (2 devs)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| WORLD-CHAMPIONS-GUILD | Champions Guild | 4 |
| WORLD-COOKING-GUILD | Cooking Guild | 6 |
| WORLD-CRAFTING-GUILD | Crafting Guild | 6 |
| AREA-MINING-GUILD | Mining Guild | 4 |
| WORLD-GUILD-ENTRY | Guild Entry System | 4 |
| WORLD-MUSEUM | Varrock Museum | 6 |

#### Stream C: Transportation (1 dev)
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| WORLD-CANOE-F2P | Canoe System | 6 |
| AREA-KARAMJA-F2P | Karamja F2P | 6 |
| WORLD-DESERT-F2P | Shantay Pass | 3 |
| MAGIC-TELE | Teleport Spells | 6 |
| MAGIC-ALCH | High Alchemy | 4 |
| MAGIC-SUPERHEAT | Superheat | 4 |

**Definition of Done:**
- [ ] All systems functional
- [ ] All guilds accessible
- [ ] Transportation works
- [ ] Polish features complete

---

## Phase 9: Final Verification (Week 12)

**Goal:** F2P MVP complete and verified

### Final Checks
- [ ] All 255 tasks reviewed
- [ ] All builds passing
- [ ] All bot tests passing
- [ ] All 13 quests playable end-to-end
- [ ] All 7 cities populated
- [ ] All 15 skills functional
- [ ] All dungeons accessible
- [ ] Documentation updated
- [ ] CONTENT_AUDIT.md updated

### Verification Tasks
| Task ID | Title | Est. Hours |
|---------|-------|------------|
| BOTS-COMBAT-F2P | Combat Training Test | 4 |
| BOTS-DEATH-F2P | Death System Test | 4 |
| QUEST-QA-1 | Quest Regression Pass | 8 |
| CONTENT-1 | Content Audit Update | 4 |

---

## Resource Allocation

### Recommended Team Size: 10-12 developers

| Phase | Devs Needed | Duration |
|-------|-------------|----------|
| Phase 0 | 5 | 1 week |
| Phase 1 | 6 | 2 weeks |
| Phase 2 | 8 | 2 weeks |
| Phase 3 | 8 | 2 weeks |
| Phase 4 | 8 | 2 weeks |
| Phase 5 | 7 | 2 weeks |
| Phase 6 | 7 | 3 weeks |
| Phase 7 | 4 | 1 week |
| Phase 8 | 5 | 2 weeks |
| Phase 9 | 4 | 1 week |

**Total:** 18 developer-weeks (with 10-12 devs = ~10-12 calendar weeks)

---

## Critical Path

```
Phase 0 (Infrastructure)
    ↓
Phase 1-2 (Skills)
    ↓
Phase 3 (NPCs)
    ↓
Phase 4 (Dungeons)
    ↓
Phase 5 (Shops)
    ↓
Phase 6 (Quests) ← Longest phase
    ↓
Phase 7 (Bosses)
    ↓
Phase 8 (Polish)
    ↓
Phase 9 (Verification)
```

**Critical Path Duration:** ~10 weeks

---

## Success Criteria

### F2P MVP Complete When:
- [ ] All 13 F2P quests completable
- [ ] All 15 F2P skills functional
- [ ] All 7 F2P cities populated
- [ ] All major dungeons accessible
- [ ] All shops functional
- [ ] All NPCs have proper dialogue
- [ ] Wilderness systems work
- [ ] Death and banking work
- [ ] Transportation (canoes, teleports) work
- [ ] All bot tests pass
- [ ] No game-breaking bugs

---

## Risk Mitigation

### High Risk Tasks
1. **QUEST-BOT-DRAGON** (15 hours) - Complex quest, start early
2. **WORLD-ELVARG** (12 hours) - Boss mechanics, need combat system
3. **SYSTEM-RANDOM-F2P** (10 hours) - Complex system
4. **SYSTEM-CLUE** (12 hours) - Complex clue mechanics

### Mitigation
- Start complex quests in Phase 6 early
- Have combat system experts on boss tasks
- Break complex systems into smaller tasks
- Daily standups to catch blockers

---

## Next Steps

1. **Assign owners** to Phase 0 tasks
2. **Begin Phase 0** immediately
3. **Parallelize** workstreams where possible
4. **Daily check-ins** on critical path
5. **Weekly milestone** reviews

---

**Plan Created:** 2026-02-23  
**Status:** Ready for execution
