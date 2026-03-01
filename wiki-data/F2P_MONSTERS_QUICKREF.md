# F2P Monsters Quick Reference

Combat stats and key drops for F2P monster implementation.

## Low Level (Combat 1-10)

### Chicken
- **Combat:** 1 | **HP:** 3
- **Stats:** Att 1, Str 1, Def 1 | **Speed:** 4
- **Drops:** Bones (100%), Raw chicken (100%), Feather 5 (1/3), Egg (1/3)

### Cow
- **Combat:** 2 | **HP:** 8  
- **Stats:** Att 1, Str 1, Def 1 | **Speed:** 4
- **Drops:** Bones (100%), Cowhide (100%), Raw beef (100%)

### Goblin
- **Combat:** 2 | **HP:** 5
- **Stats:** Att 1, Str 1, Def 1 | **Speed:** 4
- **Key Drops:** Bones (100%), Goblin mail (1/25), Coins 5 (1/4)

### Giant Rat
- **Combat:** 3 | **HP:** 5
- **Stats:** Att 1, Str 1, Def 1 | **Speed:** 4
- **Drops:** Bones (100%), Raw rat meat (100%), Rat's tail (1/4)

### Dark Wizard (Lvl 7)
- **Combat:** 7 | **HP:** 12
- **Stats:** Att 1, Str 1, Def 1 | **Speed:** 4
- **Drops:** Bones (100%), Water rune 10-20, Mind rune 10-20, Body rune 10-20, Wizard hat (1/20)

### Al Kharid Warrior
- **Combat:** 9 | **HP:** 19
- **Stats:** Att 10, Str 10, Def 10 | **Speed:** 4
- **Drops:** Bones (100%), Iron scimitar (1/25), Coins 5-40

## Mid Level (Combat 11-40)

### Guard
- **Combat:** 21 | **HP:** 22
- **Stats:** Att 18, Str 18, Def 18 | **Speed:** 4
- **Drops:** Bones (100%), Iron bolts 1-12, Coins 1-30, Iron dagger (1/25)

### Skeleton
- **Combat:** 21 | **HP:** 24
- **Stats:** Att 20, Str 20, Def 20 | **Speed:** 4
- **Drops:** Bones (100%), Iron dagger (1/25), Coins 1-50, Bronze bar (1/25)

### Zombie
- **Combat:** 13 | **HP:** 22
- **Stats:** Att 12, Str 12, Def 12 | **Speed:** 4
- **Drops:** Bones (100%), Iron axe (1/25), Coins 1-40

### Black Knight
- **Combat:** 33 | **HP:** 42
- **Stats:** Att 30, Str 30, Def 30 | **Speed:** 5
- **Drops:** Bones (100%), Iron sword (1/25), Black knight helm (1/25), Coins 1-80

### Hill Giant
- **Combat:** 28 | **HP:** 35
- **Stats:** Att 18, Str 22, Def 16 | **Speed:** 6
- **Key Drops:** Big bones (100%), Giant key (1/128 - Obor), Iron full helm (1/25), Coins 10-60
- **Location:** Edgeville Dungeon, Giants' Plateau

### Moss Giant
- **Combat:** 42 | **HP:** 60
- **Stats:** Att 30, Str 40, Def 30 | **Speed:** 6
- **Key Drops:** Big bones (100%), Mossy key (1/150 - Bryophyta), Steel kiteshield (1/25), Coins 10-80
- **Location:** Varrock Sewers, Crandor

### Giant Spider
- **Combat:** 27 | **HP:** 32
- **Stats:** Att 20, Str 18, Def 14 | **Speed:** 4
- **Drops:** Nothing notable

## High Level (Combat 41+)

### Lesser Demon
- **Combat:** 82 | **HP:** 79
- **Stats:** Att 68, Str 70, Def 71 | **Speed:** 4
- **Key Drops:** Accursed ashes (100%), Coins 10-300, Fire rune 10-60, Chaos rune 5-20
- **Location:** Wizards' Tower, Karamja Volcano, Wilderness

### Greater Demon
- **Combat:** 92 | **HP:** 87
- **Stats:** Att 76, Str 78, Def 81 | **Speed:** 4
- **Key Drops:** Accursed ashes (100%), Coins 10-500, Fire rune 15-75, Chaos rune 10-30, Rune full helm (1/128)
- **Location:** Wilderness, Brimhaven Dungeon

## Implementation Checklist

### Phase 1: Tutorial Area
- [ ] Chicken - Lumbridge
- [ ] Cow - Lumbridge
- [ ] Goblin - Lumbridge

### Phase 2: Varrock Area
- [ ] Giant rat - Varrock sewers
- [ ] Man/Woman - Varrock
- [ ] Dark wizard - Draynor
- [ ] Guard - Varrock/Falador

### Phase 3: Training Spots
- [ ] Hill giant - Edgeville dungeon
- [ ] Moss giant - Varrock sewers

### Phase 4: Wilderness
- [ ] Lesser demon - Wilderness
- [ ] Greater demon - Wilderness
- [ ] Black knight - Wilderness

### Phase 5: Dungeons
- [ ] Skeleton - Various
- [ ] Zombie - Various

## Combat Formula Testing

These monsters provide good coverage for combat formula testing:

| Stat Range | Example Monster |
|------------|----------------|
| 1-5 stats | Chicken, Goblin |
| 10-20 stats | Guard, Skeleton |
| 25-40 stats | Hill Giant, Moss Giant |
| 65-80 stats | Lesser Demon, Greater Demon |

## Drop Table Priorities

### Must Have (Economy Impact)
1. Bones/Big bones - Prayer training
2. Cowhide - Crafting
3. Coins - All monsters
4. Runes - Magic supply
5. Feathers - Fletching

### Nice to Have
1. Equipment drops - Iron/steel items
2. Clue scrolls - Treasure Trails
3. Key drops - Obor/Bryophyta access

### F2P Rare Drops
- Rune full helm (Greater demon 1/128)
- Mossy key (Moss giant 1/150)
- Giant key (Hill giant 1/128)

## Notes

- All F2P monsters are Aggressive: No (except some in Wilderness)
- All F2P monsters are Poisonous: No
- Attack speeds are 4 (2.4s) or 6 (3.6s) ticks
- Slayer level: None for all F2P monsters

## See Also

- `wiki-data/monsters/` - Full JSON data
- `MONSTER_INDEX.md` - Complete monster list
- `docs/WORK_PLAN.md` - Implementation roadmap
