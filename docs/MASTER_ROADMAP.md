# Master Roadmap — RSMod v2 Rev 233 Parity

**Target:** Full feature parity with OSRS Revision 233 (OpenRS2 `runescape/2293`, build `233`, built `2025-09-10T16:47:47Z`).
**Last updated:** 2026-02-22

**Legend:**
- ✅ Complete — implemented and tested
- 🟡 Partial — scaffolded or mostly done, gaps noted
- ❌ Not started
- ⚠️ Needs engine work (not a content task)
- 🔒 Blocked by dependency

---

## 1. SKILLS (23 total)

### Core Combat Skills
| Skill | Status | Notes |
|-------|--------|-------|
| Attack | ✅ | XP via hit plugin |
| Strength | ✅ | XP via hit plugin |
| Defence | ✅ | XP via hit plugin |
| Hitpoints | ✅ | XP via hit plugin |
| Ranged | 🟡 | Combat works. Ammo consumption needs validation. Ava's devices, bolt specials ❌ |
| Prayer | ✅ | Bury, altar ×2/×3.5. Ectofuntus ❌ |
| Magic | 🟡 | Combat spells ✅. All utility spells ❌ (see §2) |

### Gathering Skills
| Skill | Status | Notes |
|-------|--------|-------|
| Woodcutting | ✅ | All axes, all trees, axe prioritization, depletion/respawn |
| Mining | ✅ | 10 ore types, 16 pickaxes. Gem rocks, guild boost TODO |
| Fishing | ✅ | 20 fish types, 8 spot types, bait, statRandom roll |

### Processing Skills
| Skill | Status | Notes |
|-------|--------|-------|
| Cooking | ✅ | 19 fish types, burn levels, gauntlets, fire + range locs |
| Firemaking | ✅ | All log types, chain-light, fire placement, ashes |
| Smithing | ✅ | Smelting + anvil smithing complete baseline. No make-X interface; furnace interaction breadth still expandable. |
| Herblore | ✅ | 14 herbs + unfinished/finished potion baseline complete. No make-X/decanting polish yet. |
| Fletching | ✅ | Knife-on-log, stringing, and arrow tiers complete baseline. No make-X interface yet. |

### Artisan Skills
| Skill | Status | Notes |
|-------|--------|-------|
| Crafting | ✅ | Baseline module complete (spinning wheel/wool). Leather, gem, and jewellery breadth still expandable. |
| Thieving | ✅ | 14 NPCs, 11 stall types, H.A.M. chests. Ardy diary bonus/rogue outfit/dodgy necklace still pending. |

### Movement / Progression Skills
| Skill | Status | Notes |
|-------|--------|-------|
| Agility | ✅ | Gnome Stronghold course complete with lap flow and mark-of-grace behavior. Other courses/shortcuts pending. |
| Slayer | ❌ | Task assignment, co-op, slayer XP per kill, blocks/preferences, all 70+ slayer monsters, superior slayer, slayer helmet |
| Farming | 🟡 | Herb-patch baseline implemented (weeding/planting/stages/minimal disease-cure-death/harvest for early herbs). Full system pending. |
| Runecrafting | ✅ | F2P altars complete (Air, Mind, Water, Earth, Fire) with Rune Mysteries gate + multipliers. P2P depth pending. |
| Hunter | ❌ | Box traps, bird snares, net traps, deadfall, pitfall, baited kebbit, chinchompas, dark bow method |
| Construction | ❌ | POH system, rooms, furniture, costume room, portal room, altar, dungeon, garden. Hosting. |

---

## 2. MAGIC — UTILITY SPELLS

All non-combat spells. Standard spellbook unless noted.

| Category | Spell | Status |
|----------|-------|--------|
| Teleports | Home Teleport | ❌ |
| Teleports | Varrock Teleport | ❌ |
| Teleports | Lumbridge Teleport | ❌ |
| Teleports | Falador Teleport | ❌ |
| Teleports | Teleport to House | ❌ |
| Teleports | Camelot Teleport | ❌ |
| Teleports | Ardougne Teleport | ❌ |
| Teleports | Watchtower Teleport | ❌ |
| Teleports | Trollheim Teleport | ❌ |
| Teleports | Ape Atoll Teleport | ❌ |
| Teleports | Teleport to Bounty Target | ❌ |
| Teleports | All Kourend/Zeah teleports | ❌ |
| Alchemy | Low Level Alchemy | ❌ |
| Alchemy | High Level Alchemy | ❌ |
| Enchantment | Lvl-1 Enchant (sapphire) | ❌ |
| Enchantment | Lvl-2 Enchant (emerald) | ❌ |
| Enchantment | Lvl-3 Enchant (ruby) | ❌ |
| Enchantment | Lvl-4 Enchant (diamond) | ❌ |
| Enchantment | Lvl-5 Enchant (dragonstone) | ❌ |
| Enchantment | Lvl-6 Enchant (onyx) | ❌ |
| Enchantment | Lvl-7 Enchant (zenyte) | ❌ |
| Enchantment | Enchant Crossbow Bolt (all tiers) | ❌ |
| Utility | Superheat Item | ❌ |
| Utility | String Jewellery | ❌ |
| Utility | Bones to Banana | ❌ |
| Utility | Bones to Peaches | ❌ |
| Utility | Charge | ❌ |
| Utility | Spin Flax | ❌ |
| Utility | Plank Make | ❌ |
| Utility | Tan Leather | ❌ |
| Combat support | Confuse / Weaken / Curse | ❌ |
| Combat support | Bind / Snare / Entangle | ❌ |
| Combat support | Stun | ❌ |
| Spellbook switching | Standard ↔ Ancient (Jaldraocht Altar via DT1), Standard ↔ Lunar (Astral Altar), Standard ↔ Arceuus (Dark Altar) | ❌ |
| Lunar spellbook | All Lunar spells (NPC Contact, Dream, Vengeance, Stat restore, Cure, etc.) | ❌ |
| Arceuus spellbook | All Dark Altar spells, Reanimation, Thralls, Mark of Darkness, Grasp spells | ❌ |
| Ancient spellbook | Barrage / Blitz / Burst / Rush (ice/blood/shadow/smoke) + Senntisten teleport | ❌ |
| Utility | Magic Dart (Slayer staff — scales with Slayer level) | ❌ |

---

## 3. COMBAT SYSTEM

### Core Mechanics
| Feature | Status | Notes |
|---------|--------|-------|
| Melee combat | ✅ | Formula, XP, animations |
| Ranged combat | 🟡 | Works. Ammo recovery ❌, bolt specials ❌ |
| Magic combat (standard) | ✅ | Spells, rune consumption |
| Special attacks | ✅ | `api/specials` + `content/other/special-attacks/` |
| Prayer combat bonuses | ✅ | Protect prayers, boost prayers |
| Combat formulas | 🟡 | Need validation against OSRS wiki accuracy formulas |
| NPC combat definitions | 🟡 | F2P baseline combat defs done; broader NPC ecosystem remains. |
| NPC retaliation | ✅ | Framework wired for baseline F2P ecosystem; continue expanding coverage. |
| NPC aggression radius | 🟡 | In progress (`MECH-1`). |
| NPC attack styles (melee/ranged/magic) | ❌ | NPCs don't switch attack style yet |
| Boss special attacks | ❌ | Custom scripted mechanics per boss |

### Status Effects
| Effect | Status | Notes |
|--------|--------|-------|
| Poison | ✅ | 18-tick, decay, immunity windows |
| Venom | ✅ | Starts at 6, +2/tick, cap 20, supersedes poison |
| Freeze (Ice spells) | 🟡 | In progress (`MECH-2`). |
| Stun | 🟡 | In progress (`MECH-2`). |
| Stat drain (combat) | ❌ | Poison damage, curse spells |
| Stat restore (potions) | ❌ | Needs potion drink handlers |
| Recoil damage | ❌ | Ring of Recoil / Necklace of Anguish |
| Vengeance | ❌ | Lunar spell, rebound damage |

### Wilderness / PvP
| Feature | Status | Notes |
|---------|--------|-------|
| Wilderness boundary detection | ❌ | PvP zone flag |
| Skulling | ❌ | Items-kept-on-death, skull icon |
| PvP combat | ❌ | Player vs. player targeting |
| Protect Item prayer (PvP) | ❌ | 4 kept items → 5 |
| Smite prayer | ❌ | Drain prayer on hit |
| Bounty Hunter | ❌ | Targets, emblem system |
| Rev caves | ❌ | Revenant NPCs + drops |
| Wilderness boss access | ❌ | Chaos Elemental, Scorpia, Calvarion, etc. |

---

## 4. QUESTS

### F2P Quests (13 total, 10/13 complete)
| Quest | Varp | Status | Complexity | Agent Shard |
|-------|------|--------|------------|-------------|
| Cook's Assistant | `cookquest` | ✅ | Low | QUEST-1 |
| Sheep Shearer | `sheep` | ✅ | Low | QUEST-2 |
| The Restless Ghost | `haunted` | ✅ | Medium | QUEST-3 |
| Romeo & Juliet | `rjquest` | 🟡 | Low | QUEST-4-IMPL |
| Imp Catcher | `imp` | ✅ | Low | QUEST-5 |
| Witch's Potion | `hetty` | ✅ | Low | QUEST-6 |
| Doric's Quest | `doricquest` | ✅ | Low | QUEST-7 |
| Rune Mysteries | `runemysteries` | ✅ | Low | QUEST-8 |
| Vampyre Slayer | `vampire` | 🟡 | Medium | QUEST-9-IMPL |
| Dragon Slayer I | `dragonquest` | 🟡 | High | QUEST-10-IMPL |
| Black Knights' Fortress | `hunt` | ✅ | Low | QUEST-11 |
| Prince Ali Rescue | `desertrescue` | ✅ | Medium | QUEST-12 |
| Pirate's Treasure | `100_pirate_quest` | ✅ | Low | QUEST-13 |

**Note:** Quest engine in `rsmod/api/quest/`. All 13 F2P quests are registered in `QuestList` and varps are present in `BaseVarps`; remaining work is full implementation completion for the 3 partial quests above.

**Note:** P2P quest list is not exhaustive — 100+ total P2P quests. Lists high-impact quests and quest chain gates. Implement in dependency order.

### P2P Quests — Novice (unlocks skills, areas)
| Quest | Status | Key Unlock |
|-------|--------|------------|
| Ernest the Chicken | ❌ | Feathers mechanic, Draynor Manor |
| Priest in Peril | ❌ | Morytania access |
| Nature Spirit | ❌ | Ghosts Ahoy pre-req |
| Lost City | ❌ | Zanaris, rune/dragon weapons |
| Goblin Diplomacy | ❌ | Goblin mail dye |
| Shield of Arrav | ❌ | Quest points, Phoenix/HAM |
| Merlin's Crystal | ❌ | Camelot/Camelot teleport |
| Holy Grail | ❌ | Grail lore, Fisher King Realm |
| The Knight's Sword | ❌ | 29 Smithing unlock |
| Gertrude's Cat | ❌ | Cats, kittens |
| Demon Slayer | ❌ | Silverlight (demonbane weapon) |
| Witch's House | ❌ | HP experience |
| X Marks the Spot | ❌ | Clue scroll intro, Forlorn Homestead |
| Tree Gnome Village | ❌ | Grand Tree pre-req, gnome area |
| The Grand Tree | ❌ | Gnome gliders, anti-dragon shield (quest chain) |
| Druidic Ritual | ❌ | Herblore skill unlock |
| Waterfall Quest | ❌ | 13,750 Attack + Strength XP on completion — very commonly done early |
| Biohazard | ❌ | East Ardougne, Plague City follow-up |
| Plague City | ❌ | Ardougne, underground passes |
| Underground Pass | ❌ | Regicide chain, Zulrah pre-req |
| In Search of the Myreque | ❌ | Myreque series start |
| Client of Kourend | ❌ | Kourend favour system intro |
| Misthalin Mystery | ❌ | Detective-style, low-level |
| Jungle Potion | ❌ | Karamja, Druidic Ritual follow-up |

### P2P Quests — Intermediate
| Quest | Status | Key Unlock |
|-------|--------|------------|
| Desert Treasure I | ❌ | Ancient Magicks spellbook |
| Animal Magnetism | ❌ | Ava's devices |
| Monkey Madness I | ❌ | Ape Atoll, d'hide armour |
| Watchtower | ❌ | Watchtower teleport, ogre area |
| Lunar Diplomacy | ❌ | Lunar spellbook |
| Dream Mentor | ❌ | Full Lunar spells |
| Fremnik Trials | ❌ | Rellekka, berserker/archer/seers rings |
| Fremnik Isles | ❌ | Neitiznot helm |
| Throne of Miscellania | ❌ | Kingdom management |
| In Aid of the Myreque | ❌ | Myreque series, Barrows gloves pre-req |
| Haunted Mine | ❌ | Salve amulet |
| Family Crest | ❌ | Goldsmith/Cooking gauntlets |
| Heroes' Quest | ❌ | Dragon axe, crystal bow pre-req |
| Legends' Quest | ❌ | Legends' Cape, Karamja diary access |
| One Small Favour | ❌ | Broad arrows pre-req |
| The Dig Site | ❌ | Zamorak spells, Senntisten teleport |
| Elemental Workshop I & II | ❌ | Elemental armour/shield |
| Making History | ❌ | Outpost, Fremnik Trials follow-up |
| Fairytale I - Growing Pains | ❌ | Fairy rings unlock (partial) |
| Fairytale II - Cure a Queen | ❌ | Fairy rings full access |
| Temple of the Eye | ❌ | Guardians of the Rift minigame unlock |
| Perilous Moons | ❌ | Morytania, Blood Moon / Eclipse Moon / Blue Moon |
| A Kingdom Divided | ❌ | Kourend questline, Arceuus spellbook content |
| Sins of the Father | ❌ | Darkmeyer / Blisterwood weapons |
| A Night at the Theatre | ❌ | Theatre of Blood access |
| Recipe for Disaster | ❌ | Barrows gloves (highest-priority reward in game) |

### P2P Quests — Master / Grandmaster
| Quest | Status | Key Unlock |
|-------|--------|------------|
| Regicide | ❌ | Crystal bow/shield pre-req |
| Song of the Elves | ❌ | Prifddinas, Zalcano, crystal equipment |
| Sins of the Father | ❌ | Vyre blisterwood weapons |
| Darkness of Hallowvale | ❌ | Meiyerditch, Myreque series |
| Mourning's End Pt I & II | ❌ | Death altar, Prifddinas pre-req |
| Monkey Madness II | ❌ | Demonic gorilla access |
| Desert Treasure II | ❌ | Awakened bosses, ancient bosses |
| Dragon Slayer II | ❌ | Vorkath access, anti-venom+ use |
| Recipe for Disaster | ❌ | Barrows gloves, GE achievement |
| While Guthix Sleeps | ❌ | Turoth, Tormented demons, Godsword |
| Ritual of the Mahjarrat | ❌ | Glacors, Char, Triskelion |
| Sliske's Endgame | ❌ | — |
| Children of the Sun | ❌ | Varlamore access (Varlamore Part 1) |
| At First Light | ❌ | Hunters' Rumours system unlock (Varlamore Part 2) |
| Shadows of Custodia | ❌ | Auburnvale, Varlamore Part 2 |
| The Heart of Darkness | ❌ | Cam Torum dwarves, Varlamore Part 2 |

### Miniquests (P2P)
| Miniquest | Status |
|-----------|--------|
| Vale Totems (Varlamore) | ❌ |
| Bear Your Soul | ❌ |
| Enchanted Key | ❌ |
| Enter the Abyss | ❌ |
| The General's Shadow | ❌ |
| In Search of Knowledge | ❌ |
| Lair of Tarn Razorlor | ❌ |
| Skippy and the Mogres | ❌ |

### Quest Points Milestones
| QPs | Unlock |
|-----|--------|
| 32 | Champions' Cape (Champions' Guild access) |
| 100 | Quest Cape |
| 125+ | Legends' Cape equivalent |

---

## 5. NPCs & MONSTERS

### F2P Low-Level
| NPC | Status | Notes |
|-----|--------|-------|
| Goblin | 🟡 | Baseline combat+drop definitions done. Aggression/advanced behavior still pending. |
| Cow / Cow calf | 🟡 | Baseline combat+drop definitions done. |
| Chicken | 🟡 | Baseline combat+drop definitions done. |
| Giant rat | 🟡 | Baseline combat+drop definitions done. |
| Man / Woman | 🟡 | Baseline drops done; combat behavior not a current priority. |
| Duck | ✅ | Generic, no combat |
| Sheep | ✅ | Shearing interaction complete |
| Ducks | ✅ | Atmospheric |

### F2P Mid-Level
| NPC | Status | Notes |
|-----|--------|-------|
| Guard | 🟡 | Baseline combat+drop definitions done. |
| Barbarian | ❌ | No params, no drops |
| Dark wizard | 🟡 | Baseline F2P combat/drop coverage in progress across variants. |
| Scorpion | 🟡 | Baseline F2P combat/drop coverage in progress across variants. |
| Spider | ❌ | No params, no drops |
| Imp | 🟡 | Baseline combat/drop coverage in progress; quest relevance confirmed. |
| Ghost | ❌ | Needed for Restless Ghost quest |
| Skeleton | ❌ | Stronghold of Security, Wilderness |
| Zombie | ❌ | Graveyard, Wilderness |
| Hobgoblin | ❌ | Peninsula, Wilderness |
| Highwayman | ❌ | Rimmington road |
| Monk | ❌ | Monastery |
| White knight | ❌ | Falador |
| Black knight | 🟡 | Baseline combat/drop coverage in progress; needed for F2P questline. |
| Wizard | 🟡 | Baseline F2P combat/drop coverage in progress across variants. |
| Seagull | ❌ | Port areas |

### F2P Bosses / Special
| NPC | Status | Notes |
|-----|--------|-------|
| Lesser demon | 🟡 | Baseline F2P combat/drop coverage in progress. |
| Count Draynor | ❌ | Vampyre Slayer boss, stake mechanic |
| Elvarg | ❌ | Dragon Slayer boss, dragonfire |
| Black Knight Titan | ❌ | Dragon Slayer I sub-boss |

### P2P Slayer Monsters (Selected)
| Category | Status |
|----------|--------|
| Aberrant spectres | ❌ |
| Abyssal demons | ❌ |
| Aviansies | ❌ |
| Black demons | ❌ |
| Black dragons | ❌ |
| Blue dragons | ❌ |
| Bloodvelds | ❌ |
| Cave horrors | ❌ |
| Cave kraken | ❌ |
| Dark beasts | ❌ |
| Dust devils | ❌ |
| Fire giants | ❌ |
| Gargoyles | ❌ |
| Greater demons | ❌ |
| Hellhounds | ❌ |
| Iron/steel/mithril/adamant dragons | ❌ |
| Kalphites | ❌ |
| Kurasks | ❌ |
| Lizardmen | ❌ |
| Nechryaels | ❌ |
| Smoke / Shadow / Bloodvelds | ❌ |
| Spiritual creatures | ❌ |
| Turoth | ❌ |
| Trolls (all types) | ❌ |
| Vyres / Vampyres | ❌ |
| Wyrms / Drakes / Hydra | ❌ |

### Bosses (Solo)
| Boss | Status | Notable Mechanic |
|------|--------|-----------------|
| King Black Dragon | ❌ | Dragonfire, multi-head styles |
| Giant Mole | ❌ | Dig mechanic, child moles |
| Chaos Elemental | ❌ | Unequip mechanic |
| Sarachnis | ❌ | Web mechanic, spider spawns |
| Skotizo | ❌ | Demonbane requirement |
| Grotesque Guardians | ❌ | Two-phase, lightning |
| Abyssal Sire | ❌ | Scuttling tentacles |
| Cerberus | ❌ | Ghost prayer requirement |
| Alchemical Hydra | ❌ | 4 phases, elemental weaknesses |
| Thermonuclear Smoke Devil | ❌ | Smoke cloud |
| Zulrah | ❌ | 3 phases rotation, anti-venom |
| Vorkath | ❌ | Dragon Slayer II required, freeze mechanic |
| Callisto / Artio | ❌ | Wilderness boss |
| Vetion / Calvarion | ❌ | Wilderness boss |
| Scorpia | ❌ | Wilderness boss |
| Chaos Fanatic | ❌ | Wilderness boss |
| Crazy Archaeologist | ❌ | Wilderness boss |
| Corporeal Beast | ❌ | Special spear mechanic |
| Nightmare / Phosani | ❌ | Parasite mechanic, Orb phases |
| Phantom Muspah | ❌ | Pray-switching boss |
| Duke Sucellus | ❌ | Desert Treasure II |
| Vardorvis | ❌ | Desert Treasure II, axe dodge |
| The Leviathan | ❌ | Desert Treasure II |
| The Whisperer | ❌ | Desert Treasure II |
| Araxxor | ❌ | Very recent |

### Bosses (GWD — God Wars Dungeon)
| Boss | Status |
|------|--------|
| General Graardor | ❌ |
| K'ril Tsutsaroth | ❌ |
| Commander Zilyana | ❌ |
| Kree'arra | ❌ |
| Nex | ❌ |

### Bosses (Dagannoth Kings)
| Boss | Status |
|------|--------|
| Dagannoth Rex | ❌ |
| Dagannoth Supreme | ❌ |
| Dagannoth Prime | ❌ |

### Barrows
| Boss | Status |
|------|--------|
| Ahrim / Dharok / Guthan / Karil / Torag / Verac | ❌ |

### Raids
| Raid | Status | Notes |
|------|--------|-------|
| Chambers of Xeric (CoX) | ❌ | 15+ rooms, scaling |
| Theatre of Blood (ToB) | ❌ | 6-boss gauntlet |
| Tombs of Amascut (ToA) | ❌ | Invocation scaling |

### Minigame-Specific Monsters
| NPC | Status |
|-----|--------|
| TzTok-Jad (Fight Caves) | ❌ |
| TzKal-Zuk (Inferno) | ❌ |
| Sol Heredit (Colosseum) | ❌ |

---

## 6. AREAS & WORLD

### F2P Areas
| Area | Status | Key Content |
|------|--------|-------------|
| Tutorial Island | ❌ | New player onboarding. Often skipped in private servers. |
| Lumbridge | 🟡 | Bob ✅, Hans ✅, General Store ✅. Kitchen/Cook interaction ❌, cellar ❌, swamp fishing ✅ |
| Draynor Village | ❌ | Bank, market stalls, Morgan (quest), Wise Old Man, jail |
| Draynor Manor | ❌ | Count Draynor, quest-locked mansion |
| Al Kharid | ❌ | Toll gate, palace, gem shop, duel arena → PvP arena |
| Varrock | ❌ | 2 banks, GE, museum, Champions' Guild, Varrock Palace, 2 anvils, 2 furnaces |
| Edgeville | ❌ | Bank, monastery, Wilderness entry |
| Barbarian Village | ❌ | Stronghold of Security entrance, barbarian NPCs |
| Falador | ❌ | 2 banks, Falador Park, castle, furnace, anvil, White Knights' Castle |
| Port Sarim | ❌ | Dock, ships, jail (Rimmington muggers), Fishing platform |
| Rimmington | ❌ | Mine, general store, crafting shop |
| Dwarven Mines | ❌ | 30+ rocks, combat NPCs, mining guild entrance |
| Stronghold of Security | ❌ | 4-floor dungeon, spin reward, combat boots reward |
| Wilderness (F2P) | ❌ | Chaos Altar, Graveyard, Edgeville dungeon exit, ruins |

### P2P Areas — Misthalin / Karamja
| Area | Status |
|------|--------|
| Taverley | ❌ |
| Burthorpe | ❌ |
| Karamja (Musa Point, jungle, volcano) | ❌ |
| TzHaar City (Fight Caves, tokkul shop) | ❌ |

### P2P Areas — Asgarnia / Kandarin
| Area | Status |
|------|--------|
| Seers' Village | ❌ |
| Camelot Castle | ❌ |
| Catherby | ❌ |
| Ardougne (East + West) | ❌ |
| Yanille | ❌ |
| Fishing Guild | ❌ |
| Gnome Stronghold | ❌ |
| Ranging Guild | ❌ |
| Warriors' Guild | ❌ |
| Legends' Guild | ❌ |
| Champions' Guild | ❌ |
| Crafting Guild | ❌ |
| Cooking Guild | ❌ |
| Mining Guild | ❌ |

### P2P Areas — Desert
| Area | Status |
|------|--------|
| Al Kharid palace / desert | ❌ |
| Shantay Pass | ❌ |
| Pollnivneach | ❌ |
| Sophanem / Menaphos | ❌ |
| Nardah | ❌ |
| Ruins of Uzer | ❌ |

### P2P Areas — Morytania
| Area | Status |
|------|--------|
| Canifis | ❌ |
| Slayer Tower | ❌ |
| Barrows | ❌ |
| Burgh de Rott | ❌ |
| Darkmeyer | ❌ |
| Meiyerditch | ❌ |
| Sisterhood Sanctuary | ❌ |

### P2P Areas — Tirannwn / Elven Lands
| Area | Status |
|------|--------|
| Lletya | ❌ |
| Prifddinas | ❌ |
| Zalcano area | ❌ |
| Gauntlet (Prifddinas dungeon) | ❌ |

### P2P Areas — Fremenik Province
| Area | Status |
|------|--------|
| Rellekka | ❌ |
| Neitiznot / Jatizso | ❌ |
| Waterbirth Island (DKs) | ❌ |
| Lighthouse | ❌ |
| Mountain Camp | ❌ |

### P2P Areas — Keldagrim / Ape Atoll
| Area | Status |
|------|--------|
| Keldagrim | ❌ |
| Ape Atoll | ❌ |
| Gorilla madness caves | ❌ |

### P2P Areas — Kourend / Zeah
| Area | Status |
|------|--------|
| Arceuus | ❌ |
| Hosidius | ❌ |
| Lovakengj | ❌ |
| Piscarilius | ❌ |
| Shayzien | ❌ |
| Wintertodt Camp | ❌ |
| Chambers of Xeric (Mount Quidamortem) | ❌ |
| Forthos Dungeon | ❌ |

### P2P Areas — Varlamore (Rev 233 New Content)
| Area | Status | Notes |
|------|--------|-------|
| Civitas Illa Fortis (capital) | ❌ | Main city: Fortis Colosseum, Temple of Ralos, market |
| Peregrine Quay | ❌ | Docks, entry from ship |
| Hunter's Guild | ❌ | Avium Savannah, level 46 Hunter to access. Hunters' Rumours system. |
| Fortis Colosseum | ❌ | Wave-based combat minigame, Sol Heredit boss, Sunfire fanatic armour |
| Quetzacalli Gorge | ❌ | Hunter creatures, carnivorous chinchompas |
| Ralos' Rise | ❌ | The Teomat religious centre |
| Cam Torum | ❌ | Imcando dwarf city beneath Ralos' Rise |
| Sunset Coast | ❌ | Coastal area of Varlamore |
| Quetzal Transport System | ❌ | Varlamore-wide transport network (like fairy rings); needs 10 Hunters' Rumours for whistle |

### P2P Areas — Misc
| Area | Status |
|------|--------|
| Fossil Island | ❌ |
| Anachronia | ❌ |
| God Wars Dungeon | ❌ |
| Ancient Cavern | ❌ |
| Abyss (Runecrafting) | ❌ |
| Puro-Puro (Hunter) | ❌ |
| Lunar Isle | ❌ |
| Soul Wars Arena | ❌ |
| Pest Control island | ❌ |
| Barbarian Outpost | ❌ |

---

## 7. INTERFACES & UI

| Interface | Status | Notes |
|-----------|--------|-------|
| Gameframe / HUD | ✅ | |
| Bank | ✅ | Fully featured |
| Equipment tab | ✅ | |
| Prayer tab | ✅ | |
| Combat tab | ✅ | |
| Skill guide | ✅ | |
| Logout tab | ✅ | |
| Settings | ✅ | |
| Emotes | ✅ | |
| Chat / chat filter | 🟡 | Basic chat. Filter settings ✅ |
| Friends list | ❌ | Add/remove, online status |
| Ignore list | ❌ | |
| Clan chat | ❌ | Owner/rank system, loot split |
| Group Ironman panel | ❌ | |
| Quest journal (functional) | ❌ | Shell exists. No quest tracking UI. |
| Achievement diary interface | ❌ | |
| Collection log | ❌ | Full item tracking per category |
| Grand Exchange interface | 🟡 | `api/market` exists. No GE UI/plugin. |
| Trade screen | ❌ | Player-to-player |
| Duel arena → PvP arena | ❌ | |
| Music player | 🟡 | `api/music` exists. Wiring unclear. |
| Minigame teleport tab | ❌ | |
| Kourend favour panel | ❌ | |
| Slayer task interface | ❌ | |
| Farming interface | ❌ | Patch info overlay |
| Wilderness warning overlay | ❌ | |
| Boss kill count overlay | ❌ | |
| Death recap interface | ❌ | |
| Drop rate toggles | ❌ | |

---

## 8. CORE SYSTEMS

### Economy & Trading
| System | Status | Notes |
|--------|--------|-------|
| Banking | ✅ | Full featured |
| Player-to-player trade | ❌ | Trade accept/decline, second accept screen |
| Grand Exchange | 🟡 | `api/market` framework. No UI, no price matching. |
| Shops (NPC) | 🟡 | `api/shops` framework. Few shops populated. Stock refresh ❌ |
| Item notes | ❌ | Banknote ↔ item conversion |
| Coin pouch | ❌ | Pickpocket coin pouch mechanic |

### Death & Respawn
| System | Status | Notes |
|--------|--------|-------|
| Basic death | ✅ | Drops items at death tile |
| Items kept on death | ✅ | Basic 3-item rule |
| Gravestone | ❌ | Modern OSRS: grave timer, retrieve from death spot |
| Unsafe death (wilderness) | ❌ | Killer gets items |
| Hardcore Ironman death | ❌ | Account status removal |

### Account Modes
| Mode | Status |
|------|--------|
| Normal | ✅ |
| Ironman | ❌ |
| Hardcore Ironman | ❌ |
| Ultimate Ironman | ❌ |
| Group Ironman | ❌ |

### Consumables & Inventory
| System | Status | Notes |
|--------|--------|-------|
| Food eating (HP restore) | ❌ | Tick delay, eat anim |
| Potion drinking (stat boost) | ❌ | 4 doses, timer restore |
| Stat boost / drain mechanics | ❌ | Level above max, timer decay |
| Anti-poison effect | ❌ | Cures + delays re-poisoning |
| Antifire effect | ❌ | Reduces dragonfire damage |
| Divine potions | ❌ | Stat hold without decay |
| Stamina potion | ❌ | Run energy restore + decay prevention |
| Saradomin brew mechanics | ❌ | HP up, stats down |
| Overloads (ToA/CoX) | ❌ | |

### Energy / Weight
| System | Status | Notes |
|--------|--------|-------|
| Run energy | ❌ | Depletes while running, restores while walking |
| Weight system | ❌ | Equipment/inventory weight affects energy drain |
| Stamina potions | ❌ | Depends on above |
| Grace / Agility bonus | ❌ | Agility reduces energy drain |

### Player Interaction
| Feature | Status |
|---------|--------|
| Follow | ❌ |
| Trade | ❌ |
| Challenge / duel | ❌ |
| Report | ❌ |
| Examine | 🟡 |
| NPC examine | 🟡 |

---

## 9. TRANSPORTATION

| Transport | Status | Notes |
|-----------|--------|-------|
| Canoe | ✅ | 5 types, River Lum |
| Magic teleports (standard) | ❌ | See §2 |
| Teleport tablets | ❌ | One-use, item-on-inventory |
| Charged jewelry teleports | ❌ | Ring of Dueling, Amulet of Glory, Duel Arena ring |
| Fairy rings | ❌ | 50+ destinations, completion of Fairytale II |
| Spirit trees | ❌ | Farming-linked |
| Gnome gliders | ❌ | Network of 8 locations |
| Balloon transport | ❌ | Enlightened Journey quest |
| Charter ships | ❌ | Port Sarim, Catherby, etc. |
| Ship to Pest Control island | ❌ | |
| Ship to Lunar Isle | ❌ | |
| Slayer ring | ❌ | 8 charges, Slayer-linked |
| Ectophial | ❌ | Ectofuntus |
| Minecart (Keldagrim) | ❌ | |
| Agility shortcuts | ❌ | Depends on Agility |
| Trapdoor / cave entrances | 🟡 | Ladders ✅. Specific cave content ❌ |
| Teleport to House | ❌ | Construction |
| Lunar Spells teleports | ❌ | Moonclan, Ourania, etc. |
| Quetzal Transport System | ❌ | Varlamore fast travel, unlocked via Hunters' Rumours |

---

## 10. MINIGAMES

### Combat Minigames
| Minigame | Status | Notes |
|----------|--------|-------|
| Fight Caves (TzTok-Jad) | ❌ | 63-wave survival, fire cape reward |
| The Inferno (TzKal-Zuk) | ❌ | 69-wave, infernal cape |
| Fortis Colosseum | ❌ | Varlamore, sol heredit, sunfire fanatic armour |
| Pest Control | ❌ | Void armour, commendation points |
| Barbarian Assault | ❌ | Void, penance, fighter torso |
| Castle Wars | ❌ | Armadyl / Zamorak, castle wars armour |
| Soul Wars | ❌ | Ectoplasmator |
| Last Man Standing | ❌ | PvP drop crate |
| PvP Arena (old Duel Arena) | ❌ | |

### Skilling Minigames
| Minigame | Status | Skill |
|----------|--------|-------|
| Wintertodt | ❌ | Firemaking (50+) |
| Tempoross | ❌ | Fishing (35+) |
| Guardians of the Rift | ❌ | Runecrafting (27+) |
| Mahogany Homes | ❌ | Construction |
| Tithe Farm | ❌ | Farming |
| Blast Furnace | ❌ | Smithing (at-level or pay) |
| Volcanic Mine | ❌ | Mining |
| Blast Mine | ❌ | Mining |
| Shooting Stars | ❌ | Mining |
| Puro-Puro | ❌ | Hunter |
| Pyramid Plunder | ❌ | Thieving |

### Raids
| Raid | Status |
|------|--------|
| Chambers of Xeric | ❌ |
| Theatre of Blood | ❌ |
| Tombs of Amascut | ❌ |

### Other
| Minigame | Status |
|----------|--------|
| Gnome Restaurant | ❌ |
| The Gauntlet / Corrupted Gauntlet | ❌ |
| Hallowed Sepulchre | ❌ |
| Trouble Brewing | ❌ |
| Mahogany Homes | ❌ |

---

## 11. ACHIEVEMENT SYSTEMS

| System | Status | Notes |
|--------|--------|-------|
| Achievement Diary (all 12 regions) | ❌ | Easy/Medium/Hard/Elite per region. Regions: Ardougne, Desert, Falador, Fremennik, Kandarin, Karamja, Kourend & Kebos, Lumbridge & Draynor, Morytania, Varrock, Western Provinces, Wilderness. No Varlamore diary at Rev 233. |
| Diary rewards (Ava's, Kandarin headgear, etc.) | ❌ | |
| Collection log | ❌ | ~1,500+ tracked items |
| Combat achievements | ❌ | Easy→Grandmaster tiers |
| Boss kill count (KCs) | ❌ | NPC kill tracking varbit |
| Quest points counter | ❌ | Per-quest QP sum |
| Quest cape | ❌ | All quests complete |
| Skill capes (99) | ❌ | 23 capes + trimmed |
| Max cape | ❌ | All 99s |
| Completionist mechanics | ❌ | |

---

## 12. ITEMS & EQUIPMENT

### Food (All need eating handler + HP values)
| Category | Status |
|----------|--------|
| Basic food (bread, cheese, meat, shrimp, etc.) | ❌ |
| Cooked fish (all 20 types) | ❌ |
| High-tier food (karambwan, manta ray, anglerfish) | ❌ |
| Pies (apple, meat, garden, fish, summer, wild, dragonfruit) | ❌ |
| Pizzas | ❌ |
| Cakes | ❌ |

### Potions (All need drink handler + stat effects)
| Category | Status |
|----------|--------|
| Attack/Strength/Defence/Ranging/Magic potions | ❌ |
| Super Attack/Strength/Defence | ❌ |
| Super combat | ❌ |
| Prayer potion / Restore potion | ❌ |
| Antipoison / Super antipoison | ❌ |
| Antifire / Extended antifire | ❌ |
| Stamina potion | ❌ |
| Saradomin brew | ❌ |
| Zamorak brew | ❌ |
| Overloads (CoX) | ❌ |

### Charged Items
| Item | Status | Notes |
|------|--------|-------|
| Trident of the Seas/Swamp | ❌ | Charge tracking, auto-cast |
| Blowpipe | ❌ | Scales + dart loading |
| Toxic Staff | ❌ | Charge + auto-cast |
| Ring of Recoil | ❌ | Damage reflect |
| Amulet of Glory (charged) | ❌ | Teleport charges |
| Ring of Dueling | ❌ | 8 charges |
| Combat bracelet | ❌ | 6 charges |
| Arclight | ❌ | Emberdust charges |
| Blade of Saeldor | ❌ | Crystal shard charges |
| Bow of Faerdhinen | ❌ | Crystal shard charges |
| Abyssal tentacle | ❌ | Kraken tentacle wrapped |
| Slayer helm (i) | ❌ | Imbued version |
| Crystal equipment | ❌ | Shard charging system |

### Degrading Items
| Item | Status | Notes |
|------|--------|-------|
| Barrows armour/weapons | ❌ | Degrade per hit, repair at Bob |
| Crystal armour/weapons | ❌ | Crystal shards |
| Scythe of Vitur | ❌ | Vial of blood + blood rune |
| Sanguinesti Staff | ❌ | Blood rune charges |
| Elysian spirit shield | ❌ | Cosmetically degrades? No. |

---

## 13. SHOPS

| Shop Type | Status | Key Locations |
|-----------|--------|---------------|
| General stores | ❌ | Lumbridge, Varrock, Edgeville, etc. |
| Axe shops | 🟡 | Bob's in Lumbridge ✅ |
| Armour/weapon shops | ❌ | Varrock, Falador |
| Food shops | ❌ | Port Sarim, Ardougne |
| Magic shops | ❌ | Varrock, Port Sarim, Arceuss |
| Fishing shops | ❌ | Port Sarim, Catherby |
| Herblore shops | ❌ | Taverley, Gielinor |
| Slayer shops | ❌ | Per slayer master |
| Slayer point shop | ❌ | Unlock system |
| Tokkul shop (TzHaar) | ❌ | Obsidian equipment |
| Void knight shop | ❌ | Pest Control rewards |
| Clan Wars reward shop | ❌ | |
| Construction shop | ❌ | Plank, furniture material |

---

## 14. MISCELLANEOUS / NICKKNACKS

### Pets
| Category | Status |
|----------|--------|
| Skilling pets (Rocky, Baby chinchompa, etc.) | ❌ |
| Boss pets (Jal-Nib-Rek, Olmlet, etc.) | ❌ |
| Pet interface | ❌ |
| Pet following | ❌ |

### Clue Scrolls
| Tier | Status |
|------|--------|
| Beginner | ❌ |
| Easy | ❌ |
| Medium | ❌ |
| Hard | ❌ |
| Elite | ❌ |
| Master | ❌ |
| STASH unit system | ❌ |

### Random Events (Legacy)
| Event | Status | Notes |
|-------|--------|-------|
| Mysterious Old Man / Genie / Evil Bob | ❌ | Mostly cosmetic/skill XP |

### Other Mechanics
| Feature | Status | Notes |
|---------|--------|-------|
| Crystal chest | ❌ | Taverley, crystal key |
| Bird houses (Hunter) | ❌ | Fossil Island |
| Lamps / books of XP | ❌ | Quest rewards, Genie |
| Skill-specific XP lamps | ❌ | |
| Slayer helm crafting | ❌ | Head parts + black mask |
| Imbue system (NMZ) | ❌ | Ring imbues |
| NMZ (Nightmare Zone) | ❌ | Practice boss fights, imbue points |
| Dagannoth Kings rings | ❌ | (berserker/archer/seers/explorer) |
| Godsword special restore | ❌ | |
| Cannon (Dwarf Multicannon) | ❌ | Set up, load, fires ranged |
| Ensouled heads | ❌ | Arceuus prayer XP |
| Bone Voyage / Fossil Island | ❌ | |
| Tan leather (Magic spell) | ❌ | |
| Reanimation prayers (Arceuus) | ❌ | |
| Mushroom garden (Hosidius) | ❌ | |
| Molten glass blowing | ❌ | Crafting |
| Charter ship network | ❌ | |
| Kingdom of Miscellania | ❌ | Throne of Miscellania |
| Shades of Mort'ton | ❌ | Shade keys, Sacred oil |
| Brimhaven Agility Arena | ❌ | Agility tickets |
| Gnome Agility Course | ❌ | Marks of grace (P2P) |
| Barbarian Fishing | ❌ | Strength/Agility XP |
| 3-tick Fishing / Woodcutting | ❌ | Tick manipulation mechanic |
| Farming Contract (Kourend) | ❌ | |

---

## 15. WORLD INTERACTIONS (Generic Locs)

| Interaction | Status |
|-------------|--------|
| Doors (single + double) | ✅ |
| Picket / Garden gates | ✅ |
| Ladders (up/down/dungeon) | ✅ |
| Spiral staircases | ✅ |
| Bank booths / chests / deposit boxes | ✅ |
| Bookcases (examine) | ✅ |
| Crates / Sacks / Boxes (search) | ✅ |
| Signposts (examine) | ✅ |
| Chicken coops | ✅ |
| Mining rocks | ✅ |
| Fishing spots | ✅ |
| Altars (prayer) | ✅ |
| Trees (woodcutting) | ✅ |
| Fire (cooking) | ✅ |
| Range | ✅ |
| Windmill (flour making) | ✅ |
| Furnace | ❌ |
| Anvil (walk-up interaction) | ❌ |
| Spinning wheel | ✅ |
| Loom | ❌ |
| Pottery wheel + kiln | ❌ |
| Tanner (NPC) | ❌ |
| Watchtower (climb) | ❌ |
| Chaos altar (prayer) | ❌ |
| Agility obstacles | 🟡 |
| Farming patches | 🟡 |
| Beehives | ❌ |
| Hay bales | ❌ |
| Trapdoors | 🟡 |
| Coffin (quest interaction) | ❌ |
| Boat / Ship (click to travel) | ❌ |
| Cart network (Keldagrim) | ❌ |
| Canoe stations | ✅ |

---

## SUMMARY — Effort Estimate by Phase

| Phase | Scope | Relative Effort |
|-------|-------|----------------|
| **F2P Complete** | 13 quests, 3 skills, 15 monster defs, 7 areas | Medium |
| **P2P Skills** | Agility, Slayer, Farming, Hunter, Construction, RC | Very High |
| **P2P Quests (first 50)** | Novice + Intermediate quest chains | Very High |
| **NPC/Boss Ecosystem** | 200+ NPC combat defs, 30+ boss mechanics | Very High |
| **P2P Areas** | 40+ city/dungeon modules | High |
| **Minigames** | Fight Caves, Raids, Skilling minigames | Extreme |
| **Systems** | Food/potions, energy, trade, GE, diaries | High |
| **Raids + Endgame** | CoX, ToB, ToA, Colosseum | Extreme |

**Every item in this document is an independently-agentable work unit.**
See `docs/NEXT_STEPS.md` for the immediate sharding map and execution plan.
See `docs/CONTENT_AUDIT.md` for current pass/fail status.

**Verified against Rev 233 (Varlamore Part 2, Sept 25 2024):**
- F2P quests: 13 total tracked; current completion state 10/13 complete, 3 partial
- Achievement diaries: 12 regions (no Varlamore diary at Rev 233) ✓
- Varlamore areas: 8 zones + Quetzal Transport System ✓
- Varlamore quests: Children of the Sun, At First Light, Shadows of Custodia, The Heart of Darkness ✓
- P2P quest list is non-exhaustive (~100+ total quests in OSRS at Rev 233) — lists key chain gates and major unlock quests. Implement in dependency order.

