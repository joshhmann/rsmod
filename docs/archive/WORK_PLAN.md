# RSMod v2 Porting Work Plan

**Goal:** Port vanilla OSRS rev 233 content into RSMod v2 using Alter/Kronos as donor references.
**Principle:** Accuracy to wiki > speed. Flag engine issues rather than hacking around them.
Last updated: 2026-02-20.

---

## Reading This Document

- **Source** column: where to pull the implementation from (Alter = RSMod v1 Kotlin, Kronos = Java rev 184, Wiki = implement from scratch to wiki spec)
- **Blocker** = cannot start until dependency is done
- **Effort** = rough days of focused work, not calendar time
- Tasks are ordered so each phase can start once the previous is done

---

## Phase 0 — Foundation Infrastructure
*Everything else is blocked until this is done.*

### 0.1 Drop Table Framework
**Effort:** 3–4 days | **Source:** Design from scratch, model on Kronos `LootTable.java`

The RSMod death system currently drops only bones (hardcoded TODO). This blocks all monster content.

Tasks:
- [ ] Design `DropTable` DSL for RSMod v2 (weighted tables, guaranteed items, rarity tiers)
- [ ] Create `BaseDropTriggers` entries — a drop trigger type per NPC content group or NPC type
- [ ] Extend `NpcDeath.spawnDeathDrops()` to look up and roll the NPC's drop table
- [ ] Handle loot ownership (hero points system already exists — wire to loot window)
- [ ] Port Kronos `LootTable.rollItems()` weighted random logic to Kotlin
- [ ] Verify bones still drop automatically as part of the table (not hardcoded)

### 0.2 NPC Stat Loading Pipeline
**Effort:** 2–3 days | **Source:** Kronos `data/npcs/combat/*.json` (900+ files)

RSMod v2 has only ~15 NPCs defined in `BaseNpcs.kt`. Combat stats come from cache params.

Tasks:
- [ ] Survey which Kronos NPC IDs map cleanly to rev 233 symbols (Kronos is rev 184 donor data)
- [ ] Write a migration script or config-builder that reads Kronos combat JSON and emits RSMod v2 param config entries
- [ ] Add missing NPCs to `BaseNpcs.kt` in batches (start with F2P-accessible monsters)
- [ ] Verify each NPC's HP, attack speed, and animations against wiki before committing
- [ ] Flag any NPC that has rev 184 vs 233 stat divergence for manual fix

### 0.3 Skeleton NPC Content Module
**Effort:** 1 day | **Source:** RSMod v2 pattern (see `generic-npcs/cow/`)

Before porting individual monsters, establish the pattern for NPC combat scripts:
- [ ] Create `content/npcs/` module
- [ ] Write `build.gradle.kts` template for NPC content modules
- [ ] Document the 4-part pattern: stat params → combat retaliation → death queue → drop table

---

## Phase 1 — Core Mechanics (Alter → RSMod v2)
*Game feel. Needed before players can meaningfully play.*

### 1.1 Poison & Venom
**Effort:** 1–2 days | **Source:** `alter/mechanics/poison/`
**Blocker:** None

- [ ] Port poison tick damage (6 ticks/damage, diminishing)
- [ ] Port venom escalation (4 damage, +2 every 30s up to 20)
- [ ] Port antidote/antipoison/antivenom item effects
- [ ] Verify tick rates against wiki (Alter may have these correct already)

### 1.2 Prayer System
**Effort:** 2 days | **Source:** `alter/mechanics/prayer/`
**Blocker:** None

Kronos has the full Prayer enum (40+ prayers with drain rates and stat boosts).

- [ ] Port prayer activation/deactivation interface handlers
- [ ] Port drain rate tick system (points per drain tick, configurable per prayer)
- [ ] Port prayer combat bonuses (melee/ranged/magic attack+defence multipliers)
- [ ] Port overhead prayers (Protect from Melee/Ranged/Magic — visual + damage reduction)
- [ ] Port quick-prayer toggle (Alter has this in `mechanics/prayer/`)
- [ ] Add prayers to combat formula — they're referenced by `api/combat-accuracy` but need wiring

### 1.3 Equipment & Stat Requirements
**Effort:** 1–2 days | **Source:** `alter/mechanics/equipment/`
**Blocker:** None

- [ ] Port level requirement check on equip (already has `params.statreq1_skill/level` — wire it)
- [ ] Port equip/unequip sound effects (`params.equipment_sound`)
- [ ] Verify 2H weapon unequips shield slot correctly
- [ ] Port ring/amulet/cape slot effect triggers where needed

### 1.4 NPC Aggression
**Effort:** 1–2 days | **Source:** `alter/mechanics/aggro/`
**Blocker:** Phase 0.3 (NPC module pattern)

RSMod v2 has no aggression radius. Monsters near new players won't attack.

- [ ] Implement aggression radius check (NPC hunt mode trigger)
- [ ] Port "slayer task" aggression override
- [ ] Port time-based aggression reset (after 10 min in area, monsters stop aggro-ing)
- [ ] Verify wilderness aggression (all NPCs aggressive in wildy regardless of level)

### 1.5 Run Energy
**Effort:** 0.5 days | **Source:** `alter/mechanics/run/`
**Blocker:** None

- [ ] Port run energy drain (weight-based, 100 → 0)
- [ ] Port run energy restore (0.4% per tick when not running)
- [ ] Port graceful outfit restore bonus
- [ ] Port run toggle interface button handler

### 1.6 Trading System
**Effort:** 2–3 days | **Source:** `alter/mechanics/trading/`
**Blocker:** None

- [ ] Port trade request/accept flow
- [ ] Port trade interface (offer/accept windows)
- [ ] Port item value display
- [ ] Port trade confirmation step

### 1.7 Bank PIN
**Effort:** 0.5 days | **Source:** `alter/mechanics/bankpin/`
**Blocker:** None

- [ ] Port PIN entry interface
- [ ] Port PIN setup/change flow
- [ ] Port delay-before-pin-required mechanic (Alter has this)

### 1.8 Skull & PKing Mechanics
**Effort:** 1 day | **Source:** `alter/mechanics/skullremoval/`
**Blocker:** None

- [ ] Port skull on player attack
- [ ] Port skull timer (20 min decay)
- [ ] Port item protection on death (keep 3 items, 4 with prayer)
- [ ] Port skull removal (rev 233 behavior: skull removed when timer expires)

### 1.9 Starter Package
**Effort:** 0.5 days | **Source:** `alter/mechanics/starter/`
**Blocker:** None

- [ ] Port starter item grant on first login
- [ ] Verify starter items match server intent (not vanilla OSRS — customize as needed)

---

## Phase 2 — Consumable Items (Alter → RSMod v2)
*Players need food and potions to survive combat.*

### 2.1 Food / Healing Items
**Effort:** 1 day | **Source:** `alter/items/consumables/`
**Blocker:** Phase 0 (drop tables — so food drops from monsters)

- [ ] Port eat food handler (click in inventory → heal → 3-tick eat delay)
- [ ] Port healing amounts per food type (wiki-accurate)
- [ ] Port combo food mechanic (karambwan can be eaten same tick)
- [ ] Port overheat mechanic (cake/pie multiple bites)

### 2.2 Potions
**Effort:** 1–2 days | **Source:** `alter/items/consumables/` + Kronos potion enum
**Blocker:** Phase 1.2 (prayer), Phase 1.3 (equipment)

- [ ] Port stat boost potions (attack, strength, defence, ranging, magic, prayer)
- [ ] Port restore potions (stat restore, super restore, sanfew)
- [ ] Port antipoison/antivenom
- [ ] Port 4-dose → 3-dose → 2-dose → 1-dose → empty vial chain
- [ ] Port overload/divine potion class (if server wants these)

### 2.3 Key Items
**Effort:** 1 day | **Source:** `alter/items/`
**Blocker:** None

- [ ] Amulet of glory (teleports — `alter/items/amuletofglory/`)
- [ ] Ring of wealth (drop broadcast + gem drop increase)
- [ ] Essence pouch (RC skill prerequisite — `alter/items/essencepouch/`)
- [ ] Looting bag (wildy only, stores dropped items — `alter/items/lootingbag/`)
- [ ] Spade (dig mechanic for clues/farming — `alter/items/spade/`)

---

## Phase 3 — Simple Skills (1 day each)
*Quick wins. Firemaking, Cooking, Prayer training. All have low complexity.*

### 3.1 Firemaking
**Effort:** 1 day | **Source:** Kronos `Burning.java` / wiki
**Blocker:** None (no NPC or drop dependency)

- [ ] Port log + tinderbox → light fire interaction
- [ ] Port fire ground object (spawns, has a lifespan, turns to ashes)
- [ ] Port log enum: Normal(1), Oak(15), Willow(30), Teak(35), Arctic pine(42), Maple(45), Mahogany(50), Yew(60), Magic(75), Redwood(90) — XP per wiki
- [ ] Port pyromancer outfit XP bonus (2.5%)
- [ ] Port barbarian firemaking (blow pipe + bow — separate technique from Agility guild)
- [ ] Add `content.firemaking_log` content group to `BaseContent`
- [ ] Create `content/skills/firemaking/` module

### 3.2 Cooking
**Effort:** 1 day | **Source:** Kronos `Cooking.java` / wiki
**Blocker:** 3.1 (need fire as a loc to cook on) + Phase 2.1 (raw food items)

- [ ] Fill existing placeholder `content/skills/cooking/` module
- [ ] Port use-raw-food-on-range/fire interaction
- [ ] Port burn chance formula (level-based, cookStop threshold per food)
- [ ] Port food list: shrimp(1), anchovies(1), sardine(1), herring(5), mackerel(10), trout(15), cod(18), pike(20), salmon(25), tuna(30), lobster(40), bass(43), swordfish(45), monkfish(62), shark(80), anglerfish(84), dark crab(85), manta ray(91)
- [ ] Port cooking gauntlets (-5% burn chance)
- [ ] Port range vs fire bonus (5% lower burn on range)
- [ ] Port batch cooking interface (Kronos has skill dialogue for this)

### 3.3 Prayer Training (Bones)
**Effort:** 0.5 days | **Source:** Kronos `Prayer.java` bone section / wiki
**Blocker:** Phase 1.2 (prayer system)

- [ ] Port bury bones in inventory (right-click → bury)
- [ ] Port bone XP table: bones(4.5), big bones(15), babydragon(30), wyvern(50), dragon(72), ourg(70), fayrg(84), raurg(96), dagannoth(125), superior dragon(150)
- [ ] Port altar prayer training (x3.5 XP multiplier)
- [ ] Port ectofuntus (x4 XP, bonemeal + slime mechanic)

---

## Phase 4 — Medium Skills (2–4 days each)
*Core skilling loop. All follow the woodcutting pattern.*

### 4.1 Mining
**Effort:** 2–3 days | **Source:** Kronos `Mining.java` / wiki
**Blocker:** None (content group `content.ore` already exists in `BaseContent`)

- [ ] Create `content/skills/mining/` module
- [ ] Port ore enum: clay(1/5xp), copper/tin(1/17.5), iron(15/35), silver(20/40), coal(30/50), gold(40/65), mithril(55/80), adamantite(70/95), runite(85/125), amethyst(92/240)
- [ ] Port pickaxe enum: bronze(1), iron(1), steel(5), black(11), mithril(21), adamant(31), rune(41), dragon(61), infernal(61), crystal(71)
- [ ] Port success rate formula (matches woodcutting pattern — `statRandom`)
- [ ] Port rock respawn system (mirror woodcutting Controller pattern)
- [ ] Port gem rock (random gem on success)
- [ ] Port mining gloves (silver/coal/gold/mithril/adamant/rune ore bonuses — no depletion)
- [ ] Port infernal pickaxe (1 in 3 chance to smelt ore → bar directly, FM XP)
- [ ] Port 3-rock sandstone/granite (random size ore)

### 4.2 Smithing
**Effort:** 2 days | **Source:** Kronos `Smithing.java` / wiki
**Blocker:** 4.1 (mining for bars), 3.1 (fire/furnace context)

- [ ] Create `content/skills/smithing/` module
- [ ] Port smelting: furnace + ore → bar (dialogue-based batch)
- [ ] Port bar table: bronze(1/6.2), iron(15/12.5), silver(20/13.7), steel(30/17.5), gold(40/22.5), mithril(50/30), adamant(70/37.5), runite(85/50)
- [ ] Port coal requirements (steel=1, mithril=2, adamant=3, runite=4 per bar)
- [ ] Port goldsmith gauntlets (2.5x gold smelting XP)
- [ ] Port anvil smithing: bar → equipment (interface-based selection)
- [ ] Port cannonball crafting (4 per steel bar, mould required)
- [ ] Port coal bag (stores 27 coal, draw at furnace)

### 4.3 Fishing
**Effort:** 3 days | **Source:** Kronos `Fishing.java` / wiki
**Blocker:** None

- [ ] Fill existing placeholder `content/skills/fishing/` module
- [ ] Port fishing spot NPC → tool check → success rate → fish + XP
- [ ] Port tool enum: small net(1), bait rod(5/requires bait), fly rod(20/requires feather), cage(40), harpoon(35), big net(16), barbarian rod(48/requires bait+feather)
- [ ] Port fish table: shrimp/anchovies(small net), sardine/herring(bait), trout/salmon(fly), lobster/swordfish(cage/harpoon), monkfish(small net, Swan Song req), shark(harpoon/big net), dark crab(wildy, cage), anglerfish(Piscarilius, worm/rod)
- [ ] Port spot movement (spots disappear/reappear randomly)
- [ ] Port angler's outfit bonus (2.5% XP)
- [ ] Port Heron pet drop
- [ ] Port infernal harpoon (1/3 chance cook fish directly, FM XP)

### 4.4 Herblore
**Effort:** 2 days | **Source:** Kronos `Herblore.java` / wiki
**Blocker:** Phase 2 (need clean herbs as items)

- [ ] Create `content/skills/herblore/` module
- [ ] Port grimy herb → clean herb (click, instant, XP)
- [ ] Port clean herb + vial of water → unfinished potion
- [ ] Port unfinished potion + secondary → completed potion
- [ ] Port potion table (65+ potions — start with commonly used ones): attack, strength, defence, ranging, magic, super variants, prayer, antipoison, antidote, antifire, energy, stamina, super restore, overload
- [ ] Port 4-dose bottle chain (potion → 3-dose → 2-dose → 1-dose → empty vial)
- [ ] Port grimy herb drop value (needed for drop table system)

### 4.5 Thieving
**Effort:** 2 days | **Source:** `alter/skills/thieving/` (COMPLETE IN ALTER — direct port)
**Blocker:** Phase 0.3 (need NPC module pattern)

Alter has full pickpocket/stall/chest implementation in RSMod v1 Kotlin. This is the closest to a straight copy.

- [ ] Create `content/skills/thieving/` module
- [ ] Port `PickpocketPlugin.kt` → RSMod v2 event handlers (`onOpNpc1` instead of `onNpcOption`)
- [ ] Port `StallThievingPlugin.kt` → `onOpLoc1` handlers
- [ ] Port `ChestThievingPlugin.kt` → `onOpLoc1` handlers
- [ ] Port stun/damage on fail (player.hit + freeze duration)
- [ ] Port success rate formula (base + level scaling)
- [ ] Port loot tables per NPC/stall/chest (use new drop table framework from Phase 0.1)
- [ ] Validate pickpocket loot vs wiki (Alter may have custom drop rates)

### 4.6 Runecrafting
**Effort:** 3 days | **Source:** Kronos `Runecrafting.java` / wiki
**Blocker:** Phase 2.3 (essence pouch item)

- [ ] Create `content/skills/runecrafting/` module
- [ ] Port altar interaction: talisman/tiara → enter portal → inside altar → craft
- [ ] Port altar table: air(1/5), mind(1/5.5), water(5/6), earth(9/6.5), fire(14/7), body(20/7.5), cosmic(27/8), chaos(35/8.5), nature(44/9), law(54/9.5), death(65/10), blood(77/23.8), soul(90/29.7)
- [ ] Port rune multiplier (more runes per essence at higher levels)
- [ ] Port talisman entry (use talisman on mysterious ruins)
- [ ] Port tiara binding (talisman + blank tiara → tiara type)
- [ ] Port essence pouch drain (large/giant/colossal pouches degrade)
- [ ] Port rune combination altars (mist, dust, mud, smoke, steam, lava — require binding necklace)
- [ ] Port ZMI altar (Ourania — random rune mix, protected)

### 4.7 Fletching
**Effort:** 3 days | **Source:** Kronos `Fletching.java` / wiki
**Blocker:** None (just items and logs)

- [ ] Create `content/skills/fletching/` module
- [ ] Port log + knife → select unstrung bow (interface dialogue)
- [ ] Port log table: normal/shortbow(1/5), longbow(10/10), oak short(20/16.5), oak long(25/25), willow short(35/33.2), willow long(40/41.5), maple short(50/50), maple long(55/58.3), yew short(65/67.5), yew long(70/75), magic short(80/83.3), magic long(85/91.5)
- [ ] Port stringing (use bowstring on unstrung bow)
- [ ] Port arrow shaft cutting (log → 15 shafts)
- [ ] Port arrow assembly: shaft + feather → headless, + tip → arrow
- [ ] Port bolt assembly: bolt tips + crossbow bolts
- [ ] Port bolt tip cutting (gems → bolt tips)
- [ ] Port crossbow stock + crossbow limbs → crossbow unstrung → strung
- [ ] Port dart assembly: dart tip + feather
- [ ] Port javelin shaft + javelin head

---

## Phase 5 — Advanced Skills (4–7 days each)

### 5.1 Agility
**Effort:** 5–7 days | **Source:** Kronos `Agility.java` / wiki
**Blocker:** Phase 1.3 (equipment), engine force-movement must work correctly

Agility is HIGH complexity because each obstacle is a custom teleport+animation sequence.

- [ ] Create `content/skills/agility/` module
- [ ] Build obstacle helper: `forceMove(from, to, speed) → anim → delay → XP`
- [ ] Port Gnome Stronghold course (9 obstacles, level 1): pipe squeeze, log balance, net climb, etc.
- [ ] Port Draynor rooftop (10, level 10)
- [ ] Port Al Kharid rooftop (11, level 20)
- [ ] Port Varrock rooftop (12, level 30)
- [ ] Port Canifis rooftop (9, level 40)
- [ ] Port Falador rooftop (11, level 50)
- [ ] Port Seers' Village rooftop (9, level 60)
- [ ] Port Pollnivneach rooftop (10, level 70)
- [ ] Port Rellekka rooftop (9, level 80)
- [ ] Port Ardougne rooftop (8, level 90)
- [ ] Port Barbarian Outpost course (6, level 35)
- [ ] Port marks of grace drops (per-obstacle, per-course rates)
- [ ] Port graceful outfit (mark of grace exchange at Rogues' Den)
- [ ] Port agility shortcuts (level-gated cliffs/pipes/etc. — 15+ shortcuts)

### 5.2 Crafting
**Effort:** 5–7 days | **Source:** Kronos `Crafting.java` / wiki
**Blocker:** None

Crafting is HIGH complexity due to many distinct sub-systems.

Sub-systems to port (one at a time):
- [ ] Create `content/skills/crafting/` module
- [ ] **Gem cutting:** use chisel on uncut gem (10 gems: opal→diamond→dragonstone→onyx→zenyte)
- [ ] **Leather:** cowhide → tanned (NPC tanner) → craft armor (thread + needle + leather)
- [ ] **D'hide armor:** dragon/green/blue/red/black leather (no tanning, direct craft)
- [ ] **Pottery:** clay → soft clay → spin on wheel → fire in kiln (pots, pie dishes, bowls)
- [ ] **Glassblowing:** bucket of sand + soda ash → molten glass (furnace) → blow (pipe → item)
- [ ] **Silver/gold jewelry:** bar + mould → furnace → ring/necklace/amulet
- [ ] **Gem jewelry:** add gem to jewelry (chisel)
- [ ] **Battlestaff:** battlestaff + orb → elemental staff
- [ ] **Amulet stringing:** ball of wool on amulet (u)
- [ ] **Snakeskin/swamp lizard armor:** (members skilling drops)

### 5.3 Hunter
**Effort:** 4–5 days | **Source:** Kronos `Hunter.java` / wiki
**Blocker:** Phase 0 (drop tables for creature loot)

- [ ] Create `content/skills/hunter/` module
- [ ] Port trap placement (click ground → place → animation)
- [ ] Port trap cap (1 + level/20, max 5)
- [ ] Port bird snare (crimson swift, golden warbler, copper longtail, cerulean twitch, tropical wagtail)
- [ ] Port box trap (grey chinchompa(53), red chinchompa(63), black chinchompa(73/wildy))
- [ ] Port net trap (swamp lizard(29), orange salamander(47), red salamander(59), black salamander(67))
- [ ] Port deadfall trap (kebbits)
- [ ] Port impling jar (catching implings by clicking — no trap, requires net at low mage)
- [ ] Port trap timeout system (trap removed if idle too long)
- [ ] Port herbiboar (Fossil Island — track + poke mushrooms — level 80)

### 5.4 Slayer
**Effort:** 2–3 days | **Source:** Kronos `Slayer.java` / wiki
**Blocker:** Phase 0 (NPCs must exist with correct IDs), Phase 4.3 implied

Slayer is logically simple but depends on monster content existing.

- [ ] Create `content/skills/slayer/` module
- [ ] Port slayer master NPC dialogue (Turael/Spria, Mazchna, Vannaka, Chaeldar, Konar, Nieve/Steve, Duradel)
- [ ] Port task assignment (weighted random from master's task pool, respect blocks)
- [ ] Port task counter (kill NPC matching task → decrement)
- [ ] Port task completion reward (slayer XP, points)
- [ ] Port slayer points shop (unlocks: bigger tasks, boss tasks, superiors, slayer helm)
- [ ] Port slayer helm (combines black mask + protective gear)
- [ ] Port block/prefer list (5 blocks at Nieve+, 6 at Duradel)
- [ ] Port superior encounters (1/200 chance on task monster with unlock)

---

## Phase 6 — Monster Content & Drop Tables
*The bulk of the game content. Can run in parallel with Phase 5 once Phase 0 is done.*

### 6.1 F2P Monster Combat Definitions
**Effort:** 3–5 days | **Source:** Kronos JSON combat files + wiki validation
**Blocker:** Phase 0 (all of it)

Target: Every F2P-accessible monster playable with correct stats and drops.

Priority order:
- [ ] Goblins (lvl 2, 5) — tutorial island + lumbridge
- [ ] Cows (lvl 2) — Lumbridge
- [ ] Chickens (lvl 1) — Lumbridge
- [ ] Rats (lvl 1) — sewers
- [ ] Spiders (lvl 1) — Stronghold
- [ ] Skeletons (lvl 21, 25, 45, 68, 77, 132)
- [ ] Zombies (lvl 13, 24, 53, 72, 86)
- [ ] Men/Women (lvl 2) — Lumbridge
- [ ] Al Kharid warriors (lvl 9) — Al Kharid palace
- [ ] Giant spiders (lvl 50) — Stronghold floor 3
- [ ] Moss giants (lvl 42) — Varrock sewers / Crandor
- [ ] Hill giants (lvl 28) — Edgeville dungeon
- [ ] Lesser demons (lvl 82)
- [ ] Greater demons (lvl 92)
- [ ] Black Knights (lvl 33)
- [ ] Guards (lvl 22)
- [ ] Dark wizards (lvl 7, 20)

### 6.2 Members Monster Combat Definitions
**Effort:** 5–7 days (batch work) | **Source:** Kronos JSON + wiki
**Blocker:** Phase 6.1

Work through Kronos `data/npcs/combat/` files alphabetically, validate each against wiki, add to RSMod v2. Goal: 200+ monsters.

### 6.3 Boss Content
**Effort:** 5–7 days per boss | **Source:** Alter (barrows/KBD) + Kronos + wiki
**Blocker:** Phase 1 (mechanics), Phase 6.1 (combat framework solid)

Priority order:
- [ ] King Black Dragon (Alter has `KbdPlugin.kt` — direct port)
- [ ] Barrows (Alter has 6 boss plugins — direct port, need maze)
- [ ] God Wars Dungeon (Kronos has implementations)
- [ ] Corporeal Beast (referenced in RSMod `BaseNpcs.kt` — already added as NPC ID)
- [ ] Cerberus, Zulrah, Vorkath, Alchemical Hydra (later)

---

## Phase 7 — Remaining World Content (Alter → RSMod v2)

### 7.1 Magic Teleports
**Effort:** 1 day | **Source:** `alter/magic/teleports/`
**Blocker:** Phase 1.2 (prayer for magic combat), None for teleports themselves

- [ ] Port standard spellbook teleports (Lumbridge, Falador, Camelot, Ardougne, Watchtower, etc.)
- [ ] Port ancient magicks teleports (if server scope includes these)
- [ ] Port jewelry teleport clicks (Amulet of Glory → Edgeville/Draynor/Al Kharid/Karamja)
- [ ] Port ring teleports

### 7.2 Area Content — Lumbridge Expansion
**Effort:** 2 days | **Source:** Wiki + Alter area module
**Blocker:** Phase 3 (skills need to exist first)

- [ ] Lumbridge Cook NPC (cooking + quest giver)
- [ ] Lumbridge Guide NPC (new player tutorial dialogue)
- [ ] Lumbridge Castle (Duke Horacio, Father Aereck)
- [ ] Al Kharid toll gate (10gp)
- [ ] Lumbridge swamp (fishing spots, fires)

### 7.3 Area Content — Varrock
**Effort:** 3 days | **Source:** Wiki
**Blocker:** Phase 3, Phase 4

- [ ] Varrock general store / Aubury's rune shop / sword shop / shield shop
- [ ] Varrock palace guards/knights
- [ ] Varrock sewers (monsters, ladder access)
- [ ] Grand Exchange NPCs (basic clerk dialogue — full GE system is separate)

### 7.4 Grand Exchange
**Effort:** 5–7 days | **Source:** Design from scratch (RSMod has `api/market`)
**Blocker:** Phase 6 (need items having values), Phase 7.3

The `api/market` framework exists but has no content. This is a complex async system.

- [ ] Design GE offer model (buy/sell offer, price, quantity, progress)
- [ ] Port GE clerk NPC dialogue
- [ ] Port GE interface (create offer, view active, collect)
- [ ] Port GE price engine (use wiki GE prices as seed data)
- [ ] Port offer matching (cross-player, deferred)

---

## Phase 8 — Complex Skills (Defer Until Later)

### 8.1 Farming
**Effort:** 7–10 days | **Source:** Kronos (36 files)
**Blocker:** Player profile persistence must be solid; many skills need harvests

Defer until Phases 1–6 are complete. The patch state must survive server restarts.

- [ ] Design persistent patch state (what stage, compost level, diseased, who planted)
- [ ] Port patch types: allotment, herb, flower, hops, bush, fruit tree, tree, special
- [ ] Port crop growth stages + timers (real-time or tick-based)
- [ ] Port compost system (normal/super/ultra)
- [ ] Port farmer payment (protect from disease)
- [ ] Port harvest (click patch at full growth → receive produce)

### 8.2 Construction
**Effort:** 10–14 days | **Source:** Kronos (53 files)
**Blocker:** Instanced world support must exist in engine

Construction requires player-owned house instances. Verify engine supports dynamic map instances before starting.

- [ ] Verify RSMod v2 engine instance support (`⚠️ Engine flag if missing`)
- [ ] Design room/hotspot data model
- [ ] Port house entry/exit
- [ ] Port basic rooms (parlour, bedroom, kitchen, garden)
- [ ] Port furniture hotspots (chair, bed, fireplace)
- [ ] Port servant system (butler, demon butler)
- [ ] Port advanced rooms (chapel, portal chamber, dungeon)

---

## Tracking Summary

| Phase | Description | Effort | Blocker |
|-------|-------------|--------|---------|
| 0 | Foundation (drop tables, NPC stats) | 6–8 days | — |
| 1 | Core mechanics (poison, prayer, aggro, trading) | 9–12 days | Phase 0 |
| 2 | Consumable items (food, potions, key items) | 3–5 days | Phase 0 |
| 3 | Simple skills (Firemaking, Cooking, Prayer bones) | 2.5 days | Phases 1–2 |
| 4 | Medium skills (Mining, Smithing, Fishing, Herblore, Thieving, RC, Fletching) | 17–22 days | Phase 3 |
| 5 | Advanced skills (Agility, Crafting, Hunter, Slayer) | 16–22 days | Phase 4 |
| 6 | Monster content + drop tables | 15–22 days | Phase 0 |
| 7 | World content (teleports, areas, GE) | 12–16 days | Phases 3–6 |
| 8 | Complex skills (Farming, Construction) | 17–24 days | Phases 1–6 |

**Total estimated effort: 97–131 days of focused work**
(This is implementation hours, not calendar time — parallelizable across multiple devs)

---

## Parallelization Opportunities

These tracks can be worked simultaneously by different people:

**Track A — Skills:** Phases 3 → 4 → 5 → 8 (sequential within track)
**Track B — Monsters:** Phases 0 → 6 (drop tables + NPC content, runs alongside Track A)
**Track C — Mechanics:** Phase 1 → Phase 2 → Phase 7 (core feel + world content)

With 3 people: rough calendar time shrinks to ~6–8 weeks for Phases 0–7.

---

## Engine Issues to Flag Before Starting

These are things that may require RSMod v2 engine changes, not plugin work.
Do not work around these — flag them and wait for engine fix.

| Issue | Affects | Status |
|-------|---------|--------|
| House instances (dynamic map) | Construction | ⚠️ Unverified |
| NPC aggression radius hunt mode | Phase 1.4 | ⚠️ Unverified — check `BaseHuntModes` |
| Force movement (obstacle teleport) | Agility | ⚠️ Check if `mapFindSquare` supports this |
| Real-time farming timers (persist across restarts) | Farming | ⚠️ Needs player profile timer storage |
| Projectile accuracy for ranged combat | All ranged NPCs | ⚠️ Verify `proj_type` param is wired |
| Cannon placement | Slayer / combat | ⚠️ No cannon module exists |
| Degrading items (barrows, crystal) | Equipment | ✅ `api/obj-charges` exists |

---

## First Week Focus

Given everything above, the optimal starting sequence is:

**Day 1–2:** Drop table framework (Phase 0.1) — unblocks everything
**Day 3–4:** NPC stat loading for ~20 F2P monsters (Phase 0.2 partial)
**Day 5:** Poison + venom (Phase 1.1) — immediate game-feel improvement
**Day 6–7:** Firemaking + Cooking (Phase 3.1 + 3.2) — two complete skills in one week

After week 1: Full combat loop works, two skills playable, drop table system live.

