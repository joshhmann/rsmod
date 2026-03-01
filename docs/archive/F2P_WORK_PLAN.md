# F2P MVP Work Plan

**Goal:** Complete all Free-to-Play (F2P) content to achieve full F2P feature parity.

**Total F2P Tasks:** 245+ pending tasks  
**Target:** Complete F2P MVP before moving to P2P content

---

## Execution Waves

### Phase 1: Core Foundation (Week 1-2)
**Priority: CRITICAL** - Block everything else

#### World Infrastructure
1. `WORLD-LADDERS-F2P` - Ladder/staircase network
2. `WORLD-GATES-F2P` - Gate and door systems  
3. `WORLD-BANKS-F2P` - All bank locations working
4. `WORLD-DEPOSIT-BOX` - Deposit boxes
5. `SMITH-2` - Furnace loc interactions

#### Basic Resources
6. `WORLD-TREE-F2P` - Tree locations
7. `WORLD-FISH-F2P` - Fishing spots
8. `WORLD-MINE-F2P` - Mining locations
9. `WORLD-RUNESTONE` - Essence mining
10. `WORLD-MILK` - Dairy cows
11. `WORLD-EGG` - Chicken eggs
12. `WORLD-FLOUR` - Windmill process
13. `WORLD-COW-PEN` - Farm locations
14. `WORLD-WHEAT-FIELD` - Grain fields
15. `WORLD-POTATO-ONION` - Vegetable picking

#### Critical NPCs
16. `NPC-BANKER-DIALOGUE` - Bank interactions
17. `NPC-SHOPKEEPER-F2P` - Shop dialogues
18. `SYSTEM-TUTOR-F2P` - Skill tutors
19. `SYSTEM-ADVISOR` - Lumbridge advisor
20. `NPC-STARTER` - New player guidance

---

### Phase 2: NPC Ecosystem (Week 2-3)
**Priority: HIGH** - Combat and training

#### Combat NPCs (with drop tables)
1. `NPC-SKELETON-F2P` + `NPC-DROP-SKELETON`
2. `NPC-ZOMBIE-F2P` + `NPC-DROP-ZOMBIE`
3. `NPC-HILL-GIANT-F2P` + `NPC-DROP-HILL-GIANT`
4. `NPC-MAN-WOMAN-COMB` + `NPC-DROP-MAN-WOMAN`
5. `NPC-BARBARIAN-COMB` + `NPC-DROP-BARBARIAN`
6. `NPC-WIZARD-F2P` + `DROP-WIZARD`
7. `NPC-MUGGER-COMB` + `NPC-DROP-MUGGER`
8. `NPC-DWARF-COMB` + `NPC-DROP-DWARF`
9. `NPC-JAIL-GUARD-COMB` + `NPC-DROP-JAIL-GUARD`
10. `NPC-WARRIOR-WOMAN-COMB` + `NPC-DROP-WARRIOR`
11. `NPC-UNICORN-COMB` + `NPC-DROP-UNICORN`

#### Basic Drops
12. `NPC-DROP-BAT`, `NPC-DROP-RAT`, `NPC-DROP-SPIDER`
13. `NPC-DROP-SCORPION`, `NPC-DROP-BLACK-KNIGHT`

---

### Phase 3: Cities & Shops (Week 3-4)
**Priority: HIGH** - Economic and social hubs

#### General Stores (All Cities)
1. `SHOP-LUMBRIDGE-GEN`
2. `SHOP-VARROCK-GEN` (if not done)
3. `SHOP-FALADOR-GEN`
4. `SHOP-DRAYNOR-GEN`
5. `SHOP-ALKHARID-GEN`
6. `SHOP-PORTSARIM-GEN`
7. `SHOP-EDGEVILLE-GEN`
8. `SHOP-GEN-STORE` - Standardize stock

#### Al Kharid Shops
9. `NPC-ELLIS` - Tanner
10. `NPC-KARIM` - Kebabs
11. `NPC-LOU-E-G` - Platelegs
12. `NPC-RANAEL` - Plateskirts
13. `NPC-ZEKE` - Scimitars
14. `NPC-DOMMIK` - Crafting
15. `NPC-GEM-TRADER` - Gems
16. `NPC-SILK-TRADER` - Silk
17. `WORLD-BORDER-GUARD` - Toll system

#### Other Key NPCs
18. `NPC-APOTHECARY` - Varrock
19. `SHOP-BETTY` - Port Sarim magic
20. `SHOP-ROMEO`, `SHOP-JULIET` - Varrock
21. `NPC-PRIEST` - Churches
22. `NPC-HANS` - Lumbridge

---

### Phase 4: Dungeons & Wilderness (Week 4-5)
**Priority: MEDIUM-HIGH** - Training spots

#### Dungeons
1. `AREA-DRAYNOR-SEWER`
2. `AREA-EDGEVILLE-DUNG` (Hill Giants!)
3. `AREA-VARROCK-SEWER` (Moss Giants!)
4. `WORLD-DWARVEN-MINE`
5. `AREA-ASGARNIA-ICE` (Ice dungeon)
6. `AREA-DARK-WIZARD` (Dark Wizards Tower)
7. `WORLD-LUM-BASEMENT` (Spiders)

#### Wilderness
8. `AREA-WILDERNESS-F2P` (Basic wildy)
9. `AREA-WILD-VOLCANO` (Lesser demons)
10. `WORLD-OUTLAW-CAMP`

#### Areas
11. `AREA-WIZARD-TOWER`
12. `AREA-LUM-SWAMP` (Father Urhney)
13. `WORLD-ICE-MOUNTAIN`
14. `WORLD-RUNESTONE-MINE`
15. `WORLD-UNICORN-PEN`

---

### Phase 5: Quest Framework (Week 5-6)
**Priority: MEDIUM-HIGH** - F2P quest completion

#### Quest NPCs (Existing Quests)
1. `NPC-GOBLIN-CHIEF` - Goblin Diplomacy
2. `NPC-HASSAN`, `NPC-OSMAN` - Prince Ali
3. `NPC-LEELA`, `NPC-LADY-KELI` - Prince Ali
4. `NPC-NED`, `NPC-AGGIE` - Prince Ali
5. `NPC-MORGAN`, `NPC-DR-HARLOW` - Vampyre Slayer
6. `NPC-THUTRHOFF` - Knight's Sword
7. `NPC-WYSON` - Dragon Slayer
8. `NPC-RELDO`, `NPC-HAIGHELEN` - Museum
9. `NPC-SIR-VYVIN`, `NPC-SQUIRE` - White Knights

#### Quest Bot Tests
10. `QUEST-BOT-COOK` - Cooks Assistant
11. `QUEST-BOT-SHEEP` - Sheep Shearer
12. `QUEST-BOT-RESTLESS` - Restless Ghost
13. `QUEST-BOT-RUNE` - Rune Mysteries
14. `QUEST-BOT-DORIC` - Dorics Quest
15. `QUEST-BOT-WITCH` - Witchs Potion
16. `QUEST-BOT-IMP` - Imp Catcher
17. `QUEST-BOT-GOBLIN` - Goblin Diplomacy
18. `QUEST-BOT-ERNEST` - Ernest the Chicken
19. `QUEST-BOT-BLACK-KNIGHT` - Black Knights Fortress
20. `QUEST-BOT-PIRATE` - Pirates Treasure
21. `QUEST-BOT-PRINCE` - Prince Ali Rescue
22. `QUEST-BOT-VAMPYRE` - Vampyre Slayer
23. `QUEST-BOT-DRAGON` - Dragon Slayer I

---

### Phase 6: Skills & Make-X (Week 6-7)
**Priority: MEDIUM** - Polish skills

#### Make-X Interfaces
1. `MAKEQ-SMITH` - Smithing quantity dialog
2. `MAKEQ-CRAFT` - Crafting quantity dialog
3. `MAKEQ-FLETCH` - Fletching quantity dialog
4. `MAKEQ-HERB` - Herblore quantity dialog

#### Skill Extensions
5. `CRAFT-GEMS` - Gem cutting
6. `CRAFT-JEWEL` - Jewellery making
7. `CRAFT-LEATHER` - Leather working
8. `MINING-GEMS` - Gem rocks + Guild boost
9. `MAGIC-ALCH` - High alchemy
10. `MAGIC-TELE` - Teleport spells
11. `MAGIC-SUPERHEAT` - Superheat item

#### Skill Guilds
12. `WORLD-COOKING-GUILD` + entry req
13. `WORLD-CRAFTING-GUILD` + entry req
14. `WORLD-MINING-GUILD` (in AREA-EDGEVILLE-DUNG)
15. `WORLD-GUILD-ENTRY` - Entry system

---

### Phase 7: Transportation & Content (Week 7-8)
**Priority: MEDIUM**

#### Transportation
1. `WORLD-CANOE-F2P` - Canoe network
2. `AREA-KARAMJA-F2P` - Musa Point
3. `WORLD-DESERT-F2P` - Shantay Pass
4. `WORLD-CRANDOR-ACCESS` - Dragon Slayer island

#### Additional Areas
5. `AREA-DRAYNOR-MANOR` - Ernest the Chicken
6. `AREA-GOBLIN-VILLAGE` - Goblin Diplomacy
7. `AREA-RIMMINGTON-COMP` - Witchs Potion
8. `AREA-BARBARIAN-VIL` - Complete content
9. `WORLD-CHAMPIONS-GUILD` - 32 QP req
10. `WORLD-MUSEUM` - Varrock Museum
11. `WORLD-FAL-DUNGEON` - Mole dungeon entrance
12. `WORLD-LUM-ROOF` - Castle roof
13. `WORLD-LOST-CITY-BORDER` - Members gate
14. `WORLD-PLATEAU-TELEPORT` - Clan Wars
15. `WORLD-CLAN-WARS` - F2P PvP

---

### Phase 8: Systems & Polish (Week 8+)
**Priority: LOW-MEDIUM**

#### System Features
1. `SYSTEM-LEVELUP` - Level up messages
2. `SYSTEM-EMOTE-F2P` - F2P emotes
3. `SYSTEM-LORE` - NPC examine texts
4. `SYSTEM-LORE-LOC` - Object examines
5. `SYSTEM-LORE-ITEM` - Item examines
6. `SYSTEM-RANDOM-F2P` - Random events
7. `SYSTEM-MUSIC-F2P` - F2P music tracks
8. `SYSTEM-DIARY-F2P` - F2P diary tasks
9. `SYSTEM-NEW-PLAYER` - Tutorial/New player flow

#### Remaining NPCs
10. All remaining quest NPCs
11. All remaining shopkeepers
12. `NPC-CHAOS-DRUID-COMB` + drops
13. `NPC-OUTLAW-COMB` + drops
14. Various wilderness NPCs

---

## Daily Execution Checklist

Each work session:

```
□ Check agent-tasks for pending F2P tasks in current phase
□ Claim 1-3 tasks from current phase
□ Lock relevant files
□ Implement with tests
□ Build and verify
□ Complete tasks
□ Update CONTENT_AUDIT.md if needed
□ Move to next task
```

## Completion Criteria per Phase

- [ ] Phase 1: Player can move, bank, gather basic resources
- [ ] Phase 2: Player can fight monsters and get drops
- [ ] Phase 3: Player can trade, shop, access all cities
- [ ] Phase 4: Player can train at dungeons and wilderness
- [ ] Phase 5: All F2P quests completable
- [ ] Phase 6: All skills have full F2P functionality
- [ ] Phase 7: All areas accessible and explorable
- [ ] Phase 8: Polish and F2P-complete experience

## Blocking Rules

1. **NO P2P work** until Phase 8 of F2P is complete
2. **Fix bugs immediately** - don't defer
3. **Test every feature** with bot scripts
4. **Document in CONTENT_AUDIT.md** as tasks complete

## Success Metrics

- [ ] 245+ F2P tasks completed
- [ ] All 13 F2P quests fully playable
- [ ] All F2P cities populated
- [ ] All F2P skills functional
- [ ] All F2P dungeons accessible
- [ ] Bot tests passing for all skills
- [ ] No game-breaking bugs

---

**Last Updated:** 2026-02-23  
**Next Review:** Weekly or when phase completes

