# F2P Completeness Plan

**Goal:** 1:1 vanilla F2P parity at Rev 233.
**Last updated:** 2026-02-22 (documentation sync: quest status + remaining execution waves)

---

## 🎯 Current Status

```
F2P QUESTS:     10/13 fully complete; 3 partially done (see table)
F2P SKILLS:     17/17 complete (100%) ✅
NPC COMBAT:     F2P baseline done (NPC-1). Aggression system in progress (MECH-1).
AREAS:          Lumbridge in progress (WORLD-1), others pending
```

### ✅ Fully Completed Quests (10)
Cook's Assistant, Sheep Shearer, The Restless Ghost, Imp Catcher, Witch's Potion, Doric's Quest, Rune Mysteries, Pirate's Treasure, Black Knights' Fortress, **Prince Ali Rescue**

### 🟡 Partially Done (3 — skeleton done, full implementation pending)
- **Romeo & Juliet** — stub exists, QUEST-4-IMPL pending (all 8 stages needed)
- **Vampyre Slayer** — skeleton done, QUEST-9-IMPL blocked on AGENTBRIDGE-6 (Count Draynor boss)
- **Dragon Slayer I** — skeleton done, QUEST-10-IMPL blocked on AGENTBRIDGE-6 (Elvarg boss)

---

## Gap Analysis

### Skills Remaining

| Skill | Status | Shard |
|-------|--------|-------|
| Crafting | ✅ | CRAFT-1 |
| Agility | ✅ | AGIL-1 |
| Runecrafting | ✅ | RC-1 |
| All others | ✅ | Done |

**All 17 F2P skills are complete.**

### Quests (F2P — 13 total)

All varps added to `BaseVarps.kt` (PRE-POP complete). All QuestList entries registered.

| Quest | Varp | Status | Task |
|-------|------|--------|------|
| Cook's Assistant | `cookquest` | ✅ | QUEST-1 |
| Sheep Shearer | `sheep` | ✅ | QUEST-2 |
| The Restless Ghost | `haunted` | ✅ | QUEST-3 |
| Romeo & Juliet | `rjquest` | 🟡 stub — full impl needed | QUEST-4-IMPL (pending) |
| Imp Catcher | `imp` | ✅ | QUEST-5 |
| Witch's Potion | `hetty` | ✅ | QUEST-6 |
| Doric's Quest | `doricquest` | ✅ | QUEST-7 |
| Rune Mysteries | `runemysteries` | ✅ | QUEST-8 |
| Vampyre Slayer | `vampire` | 🟡 skeleton — boss needed | QUEST-9-IMPL (blocked on AGENTBRIDGE-6) |
| Dragon Slayer I | `dragonquest` | 🟡 skeleton — boss needed | QUEST-10-IMPL (blocked on AGENTBRIDGE-6) |
| Black Knights' Fortress | `hunt` | ✅ | QUEST-11 |
| Prince Ali Rescue | `desertrescue` | ✅ | QUEST-12 |
| Pirate's Treasure | `100_pirate_quest` | ✅ | QUEST-13 |

### NPC Combat

| Category | Status | Task |
|----------|--------|------|
| F2P baseline (goblin, chicken, cow, rat, guard, wizard, scorpion, imp, lesser demon, black knight) | ✅ Combat stats + drops done | NPC-1 ✅ |
| Remaining F2P combat defs (drop table gaps, missing variants) | 🔄 In progress | NPC-2 |
| NPC aggression radius system | 🔄 In progress | MECH-1 |
| Freeze / stun mechanics | 🔄 In progress | MECH-2 |
| Count Draynor scripted boss | ❌ Blocked on AGENTBRIDGE-6 | QUEST-9-IMPL |
| Elvarg scripted boss | ❌ Blocked on AGENTBRIDGE-6 | QUEST-10-IMPL |

### Areas

| Area | Status | Shard |
|------|--------|-------|
| Lumbridge | 🟡 Partial | AREA-1 |
| Draynor Village | ❌ | AREA-2 |
| Al Kharid | ❌ | AREA-3 |
| Varrock | ❌ | AREA-4 |
| Falador | ❌ | AREA-5 |
| Port Sarim | ❌ | AREA-6 |
| Edgeville / Barbarian Village | ❌ | AREA-7 |

### Systems

| System | Status | Shard |
|--------|--------|-------|
| Spinning wheel (Crafting-linked) | ✅ Complete | CRAFT-1 |
| Shops with restocking stock | 🟡 Framework only | SHOP-1 |
| NPC aggression radius | 🔄 In progress | MECH-1 |
| Slayer XP on kill | ❌ | NPC-1 |

---

## Sharding Map

Each shard is designed to be **worked by one agent with zero file conflicts**.

### Pre-Population Step — COMPLETE ✅
All shared files were updated in the PRE-POP wave:
- `rsmod/api/quest/QuestList.kt` — all 13 F2P quests registered
- `rsmod/api/config/refs/BaseVarps.kt` — all varps added including `desertrescue` + `pirate_quest`
- `BaseNpcs.kt` + `BaseSeqs.kt` — F2P monster entries populated

---

### QUEST-1: Cook's Assistant
**Module:** `rsmod/content/quests/cooks-assistant/`
**Template:** Follow `Woodcutting.kt` for module scaffold; use `QuestScript`/`QuestExtensions` from `rsmod/api/quest/`
**Key files:** `CooksAssistant.kt` — NPC dialogue for the Cook, item requirement check (milk, flour, egg), stage progression
**Data:** `get_npc_rev233({ name: "Cook" })` for dialogue reference; items already in `objs.*`
**Quest varp:** `QuestList.cooks_assistant` (already defined)
**Touches:** Only its own module directory + nothing in `api/`
**Done when:** Player can talk to Cook, accept quest, collect milk/flour/egg, return, get 300 Cooking XP + 1 QP

---

### QUEST-2: Sheep Shearer
**Module:** `rsmod/content/quests/sheep-shearer/`
**Sheep shearing:** Already works in `content/generic/generic-npcs/sheep/Sheep.kt` — `invAddOrDrop(objRepo, objs.wool)` is live
**Key files:** `FredTheFarmer.kt` — Fred dialogue, wool count check (20), quest stage gate
**Data:** Uses existing `objs.wool`, `objs.ball_of_wool`. No new items needed.
**Quest varp:** `QuestList.sheep_shearer` (already defined)
**Touches:** Only its own module
**Done when:** Fred gives quest, player collects 20 wool, returns, gets 150 Crafting XP + 60 coins + 1 QP

---

### QUEST-3: The Restless Ghost
**Module:** `rsmod/content/quests/restless-ghost/`
**Key files:** `FatherAereck.kt`, `FatherUrhney.kt`, `RestlessGhost.kt`, `GhostCoffinLoc.kt`
**Data:** `get_npc_rev233({ name: "Restless Ghost" })` for ghost dialogue. Skull item needed (check `search_objtypes({ query: "ghost skull" })`).
**Quest varp:** `varps.haunted`
**Done when:** Aereck → Urhney → coffin → ghost → skull → altar sequence completable

---

### QUEST-4: Romeo & Juliet
**Module:** `rsmod/content/quests/romeo-and-juliet/`
**Key files:** `Romeo.kt`, `Juliet.kt`, `PhilippaAndFather.kt`
**Data:** Pure dialogue chain. NPC IDs via `search_npctypes({ query: "romeo" })`.
**Quest varp:** `varps.rjquest`
**Done when:** Full dialogue chain completable, both get letter/rose

---

### QUEST-5: Imp Catcher
**Module:** `rsmod/content/quests/imp-catcher/`
**Key files:** `Wizard Mizgog.kt` — 4 bead check (black/red/yellow/white)
**Data:** Bead items already in `objs.*`. NPC: `search_npctypes({ query: "mizgog" })`
**Quest varp:** `varps.imp`
**Done when:** Collect 4 beads from imps, turn in to Mizgog for 875 Magic XP + 1 QP

---

### QUEST-6: Witch's Potion
**Module:** `rsmod/content/quests/witchs-potion/`
**Key files:** `Hetty.kt` — rat's tail, eye of newt, burnt meat, onion requirement
**Quest varp:** `varps.hetty`
**Done when:** 250 Magic XP + 1 QP on completion

---

### QUEST-7: Doric's Quest
**Module:** `rsmod/content/quests/dorics-quest/`
**Key files:** `Doric.kt` — clay × 6, copper ore × 4, iron ore × 2 check
**Quest varp:** `varps.doricquest`
**Done when:** Items delivered, 1,300 Mining XP + 180 coins + 1 QP

---

### QUEST-8: Rune Mysteries
**Module:** `rsmod/content/quests/rune-mysteries/`
**Key files:** `DukeHoracio.kt`, `AuburyScribe.kt` — item (mysterious talisman) pass-through
**Quest varp:** `varps.runemysteries`
**Done when:** Dialogue chain completable. No XP reward, unlocks Runecrafting.

---

### QUEST-9: Vampyre Slayer
**Module:** `rsmod/content/quests/vampyre-slayer/`
**Key files:** `Morgan.kt`, `Dr Harlow.kt`, `CountDraynor.kt` (special combat)
**Data:** `get_npc_rev233({ name: "Count Draynor" })` for combat stats
**Quest varp:** `varps.vampire`
**Complexity:** Needs Count Draynor as a scripted boss (stakable with garlic)
**Done when:** 4,825 Attack XP + 1 QP on completion

---

### QUEST-10: Dragon Slayer
**Module:** `rsmod/content/quests/dragon-slayer/`
**Complexity:** HIGH — map pieces, keys, ship to Crandor, Elvarg boss
**Dependency:** Needs NPC combat system + Elvarg scripted boss
**Quest varp:** `varps.dragonquest`
**Defer** until NPC combat system is solid.

---

### QUEST-11: Black Knights' Fortress
**Module:** `rsmod/content/quests/black-knights-fortress/`
**Key files:** `SirAmikVarze.kt` (quest start in Falador), `FortressInfiltration.kt` (disguise check), `BlackKnightsPlot.kt` (sabotage cauldron loc interaction)
**Data:** NPC IDs via `search_npctypes({ query: "sir amik" })`. Disguise requires `objs.monk_robe` + `objs.monk_robe_top`.
**Quest varp:** `varps.hunt` (already in BaseVarps.kt)
**Complexity:** Low — dialogue + disguise item check + loc sabotage
**Done when:** Amik → fortress → cauldron sabotage → return for 2,500 coins + 3 QP

---

### QUEST-12: Prince Ali Rescue
**Module:** `rsmod/content/quests/prince-ali-rescue/`
**Key files:** `LeahnaOfLumbridge.kt`, `JoeTheGuard.kt`, `PrinceAli.kt` (Al Kharid jail)
**Data:** Requires wig, dye, paste for disguise; jail door loc near Al Kharid palace.
**Quest varp:** `varps.desertrescue` — **add to BaseVarps.kt in pre-population pass**
**Complexity:** Medium — multi-item collection + NPC dialogue chain + jail door interaction
**Done when:** Disguise Ali, escape jail, return to Leahna for 700 coins + 1 QP

---

### QUEST-13: Pirate's Treasure
**Module:** `rsmod/content/quests/pirates-treasure/`
**Key files:** `RedbeardFrank.kt` (Port Sarim), `FalmadorPirate.kt` (ship), `TreasureDig.kt` (Falador Park loc)
**Data:** Rum item + flower smuggling mechanic. Loc: `search_loctypes({ query: "flower patch" })` for dig spot.
**Quest varp:** `varps.pirate_quest` — use `find("100_pirate_quest")` — **add to BaseVarps.kt in pre-population pass**
**Complexity:** Low — item fetch + dialogue + loc interaction
**Done when:** Frank → Karamja → Falador Park dig → 450 coins + 1 QP + blue partyhat or cut emerald

---

### CRAFT-1: Crafting Skill
**Module:** `rsmod/content/skills/crafting/`
**Interactions:**
- Spinning wheel loc → wool → ball of wool (lvl 1, 2.5 XP)
- Needle + thread + leather → leather items (gloves, boots, body)
- Furnace + gold bar → gold amulet unstrung → string → amulet (needs Crafting)
- Chisel + gems → gems (sapphire, emerald, ruby, diamond)
**Template:** Follow `Cooking.kt` for processing; `Fletching.kt` for item-on-item
**Data:** `get_item_rev233({ name: "Ball of wool" })` for item IDs; `search_loctypes({ query: "spinning wheel" })`
**Touches:** Own module only

---

### AGIL-1: Agility Skill
**Module:** `rsmod/content/skills/agility/`
**Course:** Gnome Stronghold (first course, levels 1-20, safest)
**Obstacles:** Log balance, nets, pipe squeeze, tree climb
**Template:** See `rsmod-skill-implementer` SKILL.md → Movement Skills section
**Data:** `osrs_wiki_parse_page({ page: "Gnome Stronghold Agility Course" })`
**Touches:** Own module only

---

### RC-1: Runecrafting
**Module:** `rsmod/content/skills/runecrafting/`
**Altars:** Air (lvl 1), Mind (lvl 1), Water (lvl 5), Earth (lvl 9), Fire (lvl 14)
**Mechanic:** Talisman on altar → teleport inside → rune essence on altar → runes
**Dependency:** Rune Mysteries quest must be completable first (unlocks skill)
**Data:** `osrs_wiki_parse_page({ page: "Runecrafting" })` for multiplier table
**Touches:** Own module only

---

### NPC-1: F2P Monster Combat Batch
**Module:** Add params to existing NPC types; extend `content/other/npc-drops/`
**Monsters (priority order):**
1. Goblin (combat def + params)
2. Cow (combat def + params)
3. Chicken (combat def + params)
4. Guard (combat def + params)
5. Giant rat (combat def + params)
6. Scorpion (combat def + params)
7. Dark wizard (combat def + params)
8. Imp (combat def + params)
9. Lesser demon (combat def + params)
10. Black knight (for Dragon Slayer / Black Knights' Fortress)

**Data source:** `get_npc_rev233({ name: "Goblin" })` for each — more accurate than Kronos
**Fallback:** `Kronos-184-Fixed/.../data/npcs/combat/<Name>.json`
**For each monster:** cache params file + retaliation handler + drop table entry
**Touches:** Param config files (new) + existing npc-drops module (additive)

---

### AREA-1: Lumbridge Completion
**Module:** Extend `rsmod/content/areas/city/lumbridge/`
**Missing:**
- Hans dialogue (playtime, old player meme)
- Church (Father Aereck already needed for Restless Ghost quest)
- Lumbridge Swamp (fishing spots already work, verify)
- Lumbridge Castle kitchen (Cook + range — needed for Cook's Assistant)
- Cellar access (ladder)
**Touches:** Lumbridge module only

---

### AREA-2 through AREA-7: City Modules
Each city is a **new module** in `rsmod/content/areas/city/<cityname>/`.
Each agent should:
1. Create the module scaffold (build.gradle.kts)
2. Add NPC spawns for key NPCs (banker, shopkeepers, quest NPCs)
3. Add NPC dialogue for generic persons
4. Wire any city-specific locs (wheat field, mill, etc.)
5. Add any shops (use `api/shops` framework)

---

## Agent Effectiveness Rules

### What makes an agent succeed:

1. **Single module ownership** — only creates/edits files in its own directory + nothing in `api/`
2. **Named template** — "copy the structure of `Woodcutting.kt`" not "figure it out"
3. **Named data source** — "use `get_npc_rev233({ name: "X" })` for stats" not "find the stats"
4. **Build verification** — always end with `build_server` MCP tool to confirm it compiles
5. **Specific done condition** — "quest is completable start to finish" not "implement the quest"

### What causes agents to fail:

1. **Shared file conflicts** — two agents editing `BaseNpcs.kt` simultaneously
2. **Sym name guessing** — using `objs.wool_ball` when sym is `objs.ball_of_wool` → check `.data/symbols/obj.sym` first
3. **Wrong fallback ID** — `find("name", fallbackId)` breaks if ID exists in cache
4. **Inventing patterns** — not following existing module/plugin structure

### Pre-population checklist (historical — completed)

- [x] Added `desertrescue` and `pirate_quest` (`100_pirate_quest`) to `BaseVarps.kt`
- [x] Added all 13 F2P quests to `QuestList.kt`
- [x] Baseline F2P monster entries populated in shared refs
- [x] Baseline F2P combat/drop framework pass completed

---

## Execution Order (Current Remaining Work)

### Wave A — Finish Core Gameplay Dependencies (in progress)
- AGENTBRIDGE-6 (combat system events)
- MECH-1 (NPC aggression radius)
- MECH-2 (freeze/stun mechanics)

### Wave B — Complete Remaining F2P Quests
- QUEST-4-IMPL (Romeo & Juliet full implementation)
- QUEST-9-IMPL (Vampyre Slayer; depends on AGENTBRIDGE-6 for Count Draynor boss behavior)
- QUEST-10-IMPL (Dragon Slayer I; depends on AGENTBRIDGE-6 for Elvarg/boss flow)

### Wave C — World Population and City Depth
- WORLD-1 (Lumbridge polish)
- AREA-2 through AREA-7 (Draynor, Al Kharid, Varrock, Falador, Port Sarim/Rimmington, Edgeville/Barbarian Village)

### Wave D — Post-F2P Expansion
- NET-1..NET-4 (packet and network parity hardening)
- SKILL-22 (Slayer)
- SYSTEM-UI-1/2/3 (Friends/Ignore, GE baseline, music wiring)
- QUIRK-1, SKILL-23, SKILL-24 (ranged quirks, Hunter, Construction)

