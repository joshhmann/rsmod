# F2P Sprint Review — Playthrough Readiness Audit

**Date:** 2026-02-24
**Overall F2P Playthrough Readiness: ~65-70%**

Task registry: 378 total | 150 completed (40%) | 220 pending | 5 in progress | 3 blocked

---

## Quests — 13/13 F2P COMPLETE ✅

All 13 F2P quests are fully implemented including boss scripting.

| Quest | Status |
|-------|--------|
| Cook's Assistant | ✅ Done |
| Sheep Shearer | ✅ Done |
| The Restless Ghost | ✅ Done |
| Romeo & Juliet | ✅ Done |
| Imp Catcher | ✅ Done |
| Witch's Potion | ✅ Done |
| Doric's Quest | ✅ Done |
| Rune Mysteries | ✅ Done |
| Vampyre Slayer | ✅ Done (Count Draynor boss scripted) |
| Dragon Slayer I | ✅ Done (Elvarg boss scripted) |
| Black Knights' Fortress | ✅ Done |
| Prince Ali Rescue | ✅ Done |
| Pirate's Treasure | ✅ Done |

Bonus: Ernest the Chicken ✅, Quest journal verified ✅

---

## Skills — 17/17 Have Code

| Skill | Completeness | Pending |
|-------|-------------|---------|
| Woodcutting | ✅ 100% | — |
| Fishing | ✅ 100% | — |
| Firemaking | ✅ 100% | — |
| Cooking | ✅ 100% | — |
| Mining | ✅ 100% | — |
| Prayer | ✅ 100% | — |
| Thieving | ✅ 100% | — |
| Runecrafting | ✅ 100% | — |
| Crafting | ✅ 100% | — |
| Agility | ✅ 100% | — |
| Melee (Atk/Str/Def/HP) | ✅ 100% | Via combat engine |
| Smithing | 🟡 95% | `SMITH-2` furnace loc interaction |
| Fletching | 🟡 90% | `MAKEQ-FLETCH` make-X dialog |
| Herblore | 🟡 90% | `MAKEQ-HERB` make-X dialog |
| Slayer | 🟡 90% | Gear protection (`SLAYER-8` blocked) |
| Magic | 🟡 80% | `MAGIC-TELE`, `MAGIC-ALCH`, `MAGIC-SUPERHEAT` missing |
| Ranged | 🟡 80% | Weapon framework done; ammo recovery done |
| Farming | 🟡 Baseline | Core grow/harvest loop; allotments/trees pending |
| Hunter | 🟡 Baseline | Bird snares, box traps done; core impl pending |
| Construction | 🟡 Baseline | Scaffolding only; `CONSTRUCTION-IMPL` pending |

---

## Areas — 7 Cities Done, Dungeons/Wilderness Bare

| Area | Surface | Dungeons |
|------|---------|----------|
| Lumbridge | ✅ Complete | `WORLD-LUM-BASEMENT` pending |
| Varrock | ✅ Complete | `AREA-VARROCK-SEWER` pending |
| Draynor | ✅ Complete | `AREA-DRAYNOR-SEWER` pending |
| Al Kharid | ✅ Complete | — |
| Falador | ✅ Complete | `WORLD-FAL-DUNGEON` pending |
| Port Sarim | ✅ Complete | — |
| Edgeville / Barbarian Village | ✅ Complete | **`AREA-EDGEVILLE-DUNG` CRITICAL** |
| Rimmington | 🟡 Minimal | `AREA-RIMMINGTON-COMP` pending |
| Karamja (F2P) | ❌ Missing | `AREA-KARAMJA-F2P` pending |
| Wilderness | ❌ Bare | `AREA-WILDERNESS-F2P` + `WILD-1/2/3` pending |
| Crandor | ❌ Missing | `AREA-CRANDOR` pending |

---

## NPC Combat — 10 Types Done, ~15 Missing

**Done:** Chicken, Cow, Goblin, Guard, Giant Rat, Imp, Dark Wizard, Lesser Demon, Black Knight, Scorpion

| Missing Monster | Combat Task | Drop Task | Priority |
|----------------|------------|-----------|----------|
| Hill Giant | `NPC-HILL-GIANT-F2P` | `NPC-DROP-HILL-GIANT` | **CRITICAL** |
| Skeleton | `NPC-SKELETON-F2P` | `NPC-DROP-SKELETON` | HIGH |
| Zombie | `NPC-ZOMBIE-F2P` | `NPC-DROP-ZOMBIE` | HIGH |
| Moss Giant | `NPC-MOSS-GIANT-F2P` | `NPC-DROP-MOSS-GIANT` | HIGH |
| Man / Woman | `NPC-MAN-WOMAN-COMB` | `NPC-DROP-MAN-WOMAN` | MEDIUM |
| Mugger | `NPC-MUGGER-COMB` | `NPC-DROP-MUGGER` | MEDIUM |
| Barbarian | `NPC-BARBARIAN-COMB` | `NPC-DROP-BARBARIAN` | MEDIUM |
| Dwarf | `NPC-DWARF-COMB` | `NPC-DROP-DWARF` | MEDIUM |
| Spider / Giant Spider | — | `NPC-DROP-SPIDER` (in progress) | LOW |
| Unicorn | `NPC-UNICORN-COMB` | `NPC-DROP-UNICORN` | LOW |
| Warrior Woman | `NPC-WARRIOR-WOMAN-COMB` | `NPC-DROP-WARRIOR` | LOW |
| Jail Guard | `NPC-JAIL-GUARD-COMB` | `NPC-DROP-JAIL-GUARD` | LOW |
| Bear | `NPC-BEAR-COMB` | `NPC-DROP-BEAR` | LOW |

---

## Systems Status

| System | Status |
|--------|--------|
| Banks | ✅ |
| Doors / Gates / Ladders | ✅ |
| Shops (all F2P cities) | ✅ |
| Quest Engine | ✅ |
| Combat (melee / ranged / magic) | ✅ |
| NPC Aggression + Tolerance | ✅ |
| Death + Item Drops | ✅ |
| Prayer Active Effects | ✅ |
| Food & Potions | ✅ |
| Player Trading | ✅ |
| Poison / Venom | ✅ |
| Freeze / Stun | ✅ |
| Special Attacks (F2P) | ✅ |
| Stat Restore / HP Regen | ✅ |
| Make-X Framework | ✅ (needs per-skill wiring) |
| Grand Exchange (UI) | 🟡 UI only — `SYSTEM-GE-IMPL` pending |
| Friends / Ignore List (UI) | 🟡 UI only — `SYSTEM-FRIENDS` pending |
| Music Player | ✅ Wired |
| Level-Up Messages | ❌ `SYSTEM-LEVELUP` |
| Combat Level Display | ❌ `SYSTEM-COMBAT-LEVEL` |
| Teleport Spells | ❌ `MAGIC-TELE` |
| High / Low Alchemy | ❌ `MAGIC-ALCH` |
| Superheat Item | ❌ `MAGIC-SUPERHEAT` |
| Emotes | ❌ `SYSTEM-EMOTE-F2P` |
| Wilderness PvP | ❌ `WILD-1/2/3` |
| Tutorial Island | ❌ `WORLD-TUTORIAL-ISLAND` |

---

## Critical Path to Playable F2P

### Tier 1 — "Feels broken without these" (~15 tasks)
These block the playthrough feeling complete:

1. `SYSTEM-LEVELUP` — Level-up messages + fireworks
2. `MAGIC-TELE` — Varrock / Lumbridge / Falador / Camelot teleports
3. `MAGIC-ALCH` — High and Low alchemy
4. `MAGIC-SUPERHEAT` — Superheat Item
5. `NPC-HILL-GIANT-F2P` + `NPC-DROP-HILL-GIANT` — #1 F2P training monster
6. `AREA-EDGEVILLE-DUNG` — Dungeon where Hill Giants live
7. `NPC-SKELETON-F2P` + `NPC-DROP-SKELETON`
8. `NPC-ZOMBIE-F2P` + `NPC-DROP-ZOMBIE`
9. `NPC-MOSS-GIANT-F2P` + `NPC-DROP-MOSS-GIANT`

**Completing Tier 1 → ~85% F2P parity (genuinely playable)**

### Tier 2 — "Noticeable gaps" (~20 tasks)

10. `AREA-VARROCK-SEWER` — Varrock Sewers (Moss Giants, Zombies location)
11. `AREA-KARAMJA-F2P` — Karamja (fishing, banana plantation, volcano)
12. `WORLD-DWARVEN-MINE` — Dwarven Mine
13. `NPC-MAN-WOMAN-COMB` + `NPC-DROP-MAN-WOMAN`
14. `SYSTEM-COMBAT-LEVEL` — Combat level display
15. `MAKEQ-FLETCH` / `MAKEQ-HERB` / `MAKEQ-SMITH` / `MAKEQ-CRAFT` — Make-X wiring
16. `SMITH-2` — Smithing furnace loc interaction
17. `WORLD-LUM-BASEMENT` — Lumbridge castle basement
18. `SYSTEM-EMOTE-F2P` — Basic emotes
19. `NPC-AGGIE` / `NPC-MORGAN` / `NPC-NED` — Remaining Draynor NPCs
20. `NPC-ELLIS` / `NPC-GEM-TRADER` / `NPC-ZEKE` — Remaining Al Kharid NPCs

### Tier 3 — Polish for release (~30 tasks)

- Wilderness PvP (`WILD-1/2/3`)
- Remaining NPC combat types (Barbarian, Dwarf, Mugger, Bear, etc.)
- Remaining drop tables
- Area completions (Rimmington, Goblin Village, Ice Mountain)
- Bot test scripts for all skills
- Examine texts, music tracks, random events

---

## Scoreboard

| Category | Done | Needed | % |
|----------|------|--------|---|
| Quests | 13 | 13 | **100%** |
| Skills (code exists) | 17 | 17 | **100%** |
| Skills (fully polished) | 12 | 17 | **71%** |
| City surface areas | 7 | 10 | **70%** |
| Dungeons | 0 | 5 | **0%** |
| NPC combat types | 10 | ~25 | **40%** |
| Core systems | 15 | 20 | **75%** |
| Registry tasks done | 150 | 378 | **40%** |

